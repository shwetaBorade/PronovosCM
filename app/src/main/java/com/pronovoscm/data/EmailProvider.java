package com.pronovoscm.data;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.api.EmailReportApi;
import com.pronovoscm.model.request.cclist.CCListRequest;
import com.pronovoscm.model.request.emailassignee.EmailAssigneeRequest;
import com.pronovoscm.model.request.emaildefaultsettings.DefaultSettingsRequest;
import com.pronovoscm.model.request.sendemail.SendEmailRequest;
import com.pronovoscm.model.response.AbstractCallback;
import com.pronovoscm.model.response.ErrorResponse;
import com.pronovoscm.model.response.cclist.CCListResponse;
import com.pronovoscm.model.response.emailassignee.EmailAssigneeResponse;
import com.pronovoscm.model.response.emaildefaultsettings.DefaultSettingsResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.sendemail.SendEmailResponse;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.SharedPref;

import java.util.HashMap;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;

public class EmailProvider {


    private final String TAG = EmailProvider.class.getName();
    private final EmailReportApi mEmailReportApi;
    private PronovosApplication context;
    NetworkStateProvider networkStateProvider;
    private DaoSession daoSession;
    private LoginResponse loginResponse;



    public EmailProvider(NetworkStateProvider networkStateProvider, EmailReportApi emailReportApi, DaoSession daoSession) {
        this.context = PronovosApplication.getContext();
        context.setUrl(Constants.BASE_API_URL);
        this.mEmailReportApi = emailReportApi;
        this.networkStateProvider = networkStateProvider;
        this.daoSession = daoSession;

    }

    public void getEmailAssignee(EmailAssigneeRequest assigneeRequest, final ProviderResult<EmailAssigneeResponse> callback) {


        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<EmailAssigneeResponse> assigneeAPI = mEmailReportApi.getEmailAssigneeList(headers, assigneeRequest);

            assigneeAPI.enqueue(new AbstractCallback<EmailAssigneeResponse>() {
                @Override
                protected void handleFailure(Call<EmailAssigneeResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<EmailAssigneeResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<EmailAssigneeResponse> response) {
                    if (response.body() != null) {
                        EmailAssigneeResponse emailAssigneeResponse = null;
                        try {
                            emailAssigneeResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (emailAssigneeResponse != null && emailAssigneeResponse.getStatus() == 200 && (emailAssigneeResponse.getEmailAssigneeData().getResponsecode() == 101 || emailAssigneeResponse.getEmailAssigneeData().getResponsecode() == 102)) {
                            callback.success(emailAssigneeResponse);
                        } else if (emailAssigneeResponse != null) {
                            callback.failure(emailAssigneeResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {

        }

    }



    public void getEmailCC(CCListRequest ccListRequest, final ProviderResult<CCListResponse> callback) {


        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<CCListResponse> assigneeAPI = mEmailReportApi.getCclist(headers, ccListRequest);

            assigneeAPI.enqueue(new AbstractCallback<CCListResponse>() {
                @Override
                protected void handleFailure(Call<CCListResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<CCListResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<CCListResponse> response) {
                    if (response.body() != null) {
                        CCListResponse ccListResponse = null;
                        try {
                            ccListResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (ccListResponse != null && ccListResponse.getStatus() == 200 && (ccListResponse.getCCListData().getResponsecode() == 101 || ccListResponse.getCCListData().getResponsecode() == 102)) {
                            callback.success(ccListResponse);
                        } else if (ccListResponse != null) {
                            callback.failure(ccListResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {

        }

    }


    public void getEmailDefaultSetting(DefaultSettingsRequest ccListRequest, final ProviderResult<DefaultSettingsResponse> callback) {


        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<DefaultSettingsResponse> assigneeAPI = mEmailReportApi.getDefaultSetting(headers, ccListRequest);

            assigneeAPI.enqueue(new AbstractCallback<DefaultSettingsResponse>() {
                @Override
                protected void handleFailure(Call<DefaultSettingsResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<DefaultSettingsResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<DefaultSettingsResponse> response) {
                    if (response.body() != null) {
                        DefaultSettingsResponse defaultSettingsResponse = null;
                        try {
                            defaultSettingsResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (defaultSettingsResponse != null && defaultSettingsResponse.getStatus() == 200 && (defaultSettingsResponse.getData().getResponsecode() == 101 || defaultSettingsResponse.getData().getResponsecode() == 102)) {
                            callback.success(defaultSettingsResponse);
                        } else if (defaultSettingsResponse != null) {
                            callback.failure(defaultSettingsResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {

        }

    }
    public void sendEmail(SendEmailRequest sendEmailRequest, final ProviderResult<SendEmailResponse> callback) {


        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<SendEmailResponse> assigneeAPI = mEmailReportApi.sendEmail(headers, sendEmailRequest);

            assigneeAPI.enqueue(new AbstractCallback<SendEmailResponse>() {
                @Override
                protected void handleFailure(Call<SendEmailResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<SendEmailResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<SendEmailResponse> response) {
                    if (response.body() != null) {
                        SendEmailResponse sendEmailResponse = null;
                        try {
                            sendEmailResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (sendEmailResponse != null && sendEmailResponse.getStatus() == 200 && (sendEmailResponse.getSendEmailResData().getResponsecode() == 101 || sendEmailResponse.getSendEmailResData().getResponsecode() == 102)) {
                            callback.success(sendEmailResponse);
                        } else if (sendEmailResponse != null) {
                            callback.failure(sendEmailResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {

        }

    }




}
