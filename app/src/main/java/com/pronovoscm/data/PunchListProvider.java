package com.pronovoscm.data;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.api.PunchListApi;
import com.pronovoscm.model.request.punchlisemail.PunchListEmailRequest;
import com.pronovoscm.model.request.punchlist.PunchListHistoryRequest;
import com.pronovoscm.model.request.punchlist.PunchListRequest;
import com.pronovoscm.model.response.AbstractCallback;
import com.pronovoscm.model.response.ErrorResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.punchlist.PunchListHistoryResponse;
import com.pronovoscm.model.response.punchlist.PunchListResponse;
import com.pronovoscm.model.response.punchlistemail.PunchListEmailResponse;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.PunchlistDb;
import com.pronovoscm.persistence.domain.punchlist.PunchListHistoryDb;
import com.pronovoscm.persistence.repository.PunchListRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.SharedPref;

import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;

public class PunchListProvider {

    private final String TAG = PunchListProvider.class.getName();
    private final PunchListApi mPunchListApi;
    private PronovosApplication context;
    private NetworkStateProvider networkStateProvider;
    private DaoSession daoSession;
    private LoginResponse loginResponse;
    public PunchListRepository mPunchListRepository;
    private boolean isLoading;
    private FileUploadProvider mFileUploadProvider;

    public PunchListProvider(NetworkStateProvider networkStateProvider, PunchListApi punchListApi, DaoSession daoSession, PunchListRepository punchListRepository, FileUploadProvider fileUploadProvider) {
        this.context = PronovosApplication.getContext();
        context.setUrl(Constants.BASE_API_URL);
        this.mPunchListApi = punchListApi;
        this.networkStateProvider = networkStateProvider;
        this.daoSession = daoSession;
        mPunchListRepository = punchListRepository;
        mFileUploadProvider = fileUploadProvider;
    }

    public void getPunchList(PunchListRequest request, final ProviderResult<List<PunchlistDb>> callback) {

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", "");
//            headers.put("timezone", TimeZone.getDefault().getID());
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            Call<PunchListResponse> mPunchListApiPunchList = mPunchListApi.getPunchList(headers, request);

            mPunchListApiPunchList.enqueue(new AbstractCallback<PunchListResponse>() {
                @Override
                protected void handleFailure(Call<PunchListResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<PunchListResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<PunchListResponse> response) {
                    if (response.body() != null) {
                        PunchListResponse punchListResponse = null;
                        try {
                            punchListResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (punchListResponse != null && punchListResponse.getStatus() == 200 && (punchListResponse.getData().getResponseCode() == 101 || punchListResponse.getData().getResponseCode() == 102)) {

                            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

                            List<PunchlistDb> punchlistDbs = mPunchListRepository.doUpdatePunchListDb(punchListResponse.getData().getPunchlists(), request.getProjectId());

                            callback.success(punchlistDbs);


                        } else if (punchListResponse != null) {
                            callback.failure(punchListResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.success(mPunchListRepository.getPunchList(request.getProjectId()));
        }
    }

    public void getPunchListHistories(PunchListHistoryRequest request, ProviderResult<List<PunchListHistoryDb>> callback) {
        if (NetworkService.isNetworkAvailable(context)) {

            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", mPunchListRepository.getMAXPunchListHistoryUpdateDate(request.getProjectId(),loginResponse.getUserDetails().getUsers_id()));
//            headers.put("timezone", TimeZone.getDefault().getID());

            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            Call<PunchListHistoryResponse> punchListHistoryAPI = mPunchListApi.getPunchListHistories(headers,request);
            punchListHistoryAPI.enqueue(new AbstractCallback<PunchListHistoryResponse> (){
                @Override
                protected void handleFailure(Call<PunchListHistoryResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<PunchListHistoryResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<PunchListHistoryResponse> response) {
                    if( response.body() != null) {
                        PunchListHistoryResponse punchListHistoryResponse = null;
                        try {
                            punchListHistoryResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (punchListHistoryResponse != null && punchListHistoryResponse.getStatus() == 200 && (punchListHistoryResponse.getData().getResponseCode() == 101 || punchListHistoryResponse.getData().getResponseCode() == 102)) {

                            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
                            Log.d("MAna***", "handleSuccess: "+ punchListHistoryResponse.getData().getPunchListHistories().size());
                            List<PunchListHistoryDb> punchListHistoryDbs = mPunchListRepository.doUpdatePunchListHistoryInDb(punchListHistoryResponse.getData().getPunchListHistories(), Long.valueOf(request.getProjectId()));

                            Log.d("king", "handleSuccess: "+ punchListHistoryDbs);
                            callback.success(punchListHistoryDbs);

                        }else if (punchListHistoryResponse != null) {
                            callback.failure(punchListHistoryResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            } );
        }
    }

    public void sendPunchListEmail(PunchListEmailRequest punchListEmailRequest, ProviderResult<PunchListEmailResponse> providerResult) {
        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());

            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            Call<PunchListEmailResponse> projectResponseCall = mPunchListApi.sendPunchListEmail(headers, punchListEmailRequest);

            projectResponseCall.enqueue(new AbstractCallback<PunchListEmailResponse>() {
                @Override
                protected void handleFailure(Call<PunchListEmailResponse> call, Throwable throwable) {
                    providerResult.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<PunchListEmailResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        providerResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        providerResult.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<PunchListEmailResponse> response) {
                    if (response.body() != null) {
                        PunchListEmailResponse punchListEmailResponse = null;
                        try {
                            punchListEmailResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (punchListEmailResponse != null && punchListEmailResponse.getStatus() == 200) {
                            providerResult.success(punchListEmailResponse);
                            Toast.makeText(context, punchListEmailResponse.getMessage(), Toast.LENGTH_LONG).show();
                        } else if (punchListEmailResponse != null) {
                            providerResult.failure(punchListEmailResponse.getMessage());
                            Toast.makeText(context, punchListEmailResponse.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            providerResult.failure("response null");
                        }
                    } else {
                        providerResult.failure("response null");

                    }
                }
            });

        } else {

        }
    }


}
