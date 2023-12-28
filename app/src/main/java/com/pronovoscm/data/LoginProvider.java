package com.pronovoscm.data;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.api.LoginApi;
import com.pronovoscm.model.request.forgetpassword.ForgetPasswordRequest;
import com.pronovoscm.model.request.login.LoginRequest;
import com.pronovoscm.model.response.AbstractCallback;
import com.pronovoscm.model.response.ErrorResponse;
import com.pronovoscm.model.response.forgetpassword.ForgetPasswordResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;

import java.util.HashMap;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;

public class LoginProvider {


    private final String TAG = LoginProvider.class.getName();
    private final LoginApi loginApi;
    NetworkStateProvider networkStateProvider;
    private PronovosApplication context;

    public LoginProvider(NetworkStateProvider networkStateProvider, LoginApi loginApi) {
        this.context = PronovosApplication.getContext();
        context.setUrl(Constants.BASE_API_URL);
        this.loginApi = loginApi;
        this.networkStateProvider = networkStateProvider;
    }


    public void loginUser(final LoginRequest loginRequest, final ProviderResult<LoginResponse> loginResponseProviderResult) {

        if (NetworkService.isNetworkAvailable(context)) {
           /* PronovosApiRequest pronovosApiRequest = new PronovosApiRequest();
            pronovosApiRequest.setReqParam(loginRequest);*/
            HashMap<String, String> headers = new HashMap<>();

            headers.put("timezone", TimeZone.getDefault().getID());
            Call<LoginResponse> loginRequestCall = loginApi.userLogin(headers, loginRequest);

            loginRequestCall.enqueue(new AbstractCallback<LoginResponse>() {
                @Override
                protected void handleFailure(Call<LoginResponse> call, Throwable throwable) {
                    loginResponseProviderResult.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<LoginResponse> call, ErrorResponse errorResponse) {
                    loginResponseProviderResult.failure(errorResponse.getMessage());
                }

                @Override
                protected void handleSuccess(Response<LoginResponse> response) {
                    if (response.body() != null) {
                        /*LoginResponse loginResponse = gson.fromJson(response.body().getResStr(), LoginResponse.class);*/
                        LoginResponse loginResponse = null;
                        try {
                            loginResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (loginResponse != null && loginResponse.getStatus() == 200 && loginResponse.getUserDetails().getResponsecode() == 101) {
                            loginResponseProviderResult.success(loginResponse);
                        } else if (loginResponse != null && loginResponse.getStatus() == 200 && loginResponse.getUserDetails().getResponsecode() == 102) {
                            loginResponseProviderResult.failure(loginResponse.getMessage());
                        } else if (loginResponse != null && loginResponse.getMessage() != null) {
                            loginResponseProviderResult.failure(loginResponse.getMessage());
                        } else {
                            loginResponseProviderResult.failure("Login response null");
                        }
                    } else {
                        loginResponseProviderResult.failure("response null");
                    }
                }
            });

        } else {
            loginResponseProviderResult.failure(context.getString(R.string.internet_connection_check));
        }
    }


    public void forgotPassword(ForgetPasswordRequest request, final ProviderResult<ForgetPasswordResponse> responseProviderResult) {

        if (NetworkService.isNetworkAvailable(context)) {
            HashMap<String, String> headers = new HashMap<>();

            headers.put("timezone", TimeZone.getDefault().getID());
            Call<ForgetPasswordResponse> forgetPasswordResponseCall = loginApi.forgetPassword(headers, request);
            forgetPasswordResponseCall.enqueue(new AbstractCallback<ForgetPasswordResponse>() {
                @Override
                protected void handleFailure(Call<ForgetPasswordResponse> call, Throwable throwable) {
                    responseProviderResult.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<ForgetPasswordResponse> call, ErrorResponse errorResponse) {
                    responseProviderResult.failure(errorResponse.getMessage());
                }

                @Override
                protected void handleSuccess(Response<ForgetPasswordResponse> response) {
                    if (response.body() != null) {
                        ForgetPasswordResponse forgetPasswordResponse = null;
                        try {
                            forgetPasswordResponse = response.body();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (forgetPasswordResponse != null && forgetPasswordResponse.getStatus() == 200 && forgetPasswordResponse.getData().getResponsecode() == 101) {
                            responseProviderResult.success(forgetPasswordResponse);
                        } else if (forgetPasswordResponse != null && forgetPasswordResponse.getStatus() == 200 && forgetPasswordResponse.getData().getResponsecode() == 102) {
                            responseProviderResult.failure(forgetPasswordResponse.getData().getResponsemsg());
                        } else if (forgetPasswordResponse != null && forgetPasswordResponse.getStatus() != 200) {
                            responseProviderResult.failure(forgetPasswordResponse.getData().getResponsemsg());
                        }
                    } else {
                        responseProviderResult.failure("response null");
                    }
                }
            });

        } else {
            responseProviderResult.failure(context.getString(R.string.internet_connection_check));
        }
    }

}
