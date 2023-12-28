package com.pronovoscm.data;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.api.TransferLogApi;
import com.pronovoscm.model.request.transferlog.TransferLogDetailRequest;
import com.pronovoscm.model.request.transferlog.TransferLogFilterRequest;
import com.pronovoscm.model.request.transferlog.TransferLogRequest;
import com.pronovoscm.model.response.AbstractCallback;
import com.pronovoscm.model.response.ErrorResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.transferlog.TransferLogResponse;
import com.pronovoscm.model.response.transferlogdetails.TransferLogDetailResponse;
import com.pronovoscm.model.response.transferloglocation.TransferLogLocationResponse;
import com.pronovoscm.persistence.repository.EquipementInventoryRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;

import java.util.HashMap;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;

public class TransferLogProvider {


    private final String TAG = TransferLogProvider.class.getName();
    private final TransferLogApi transferOverviewApi;
    NetworkStateProvider networkStateProvider;
    private PronovosApplication context;
    private EquipementInventoryRepository equipementInventoryRepository;


    public TransferLogProvider(NetworkStateProvider networkStateProvider, TransferLogApi transferOverviewApi, EquipementInventoryRepository equipementInventoryRepository) {
        this.context = PronovosApplication.getContext();
        context.setUrl(Constants.BASE_API_URL);
        this.transferOverviewApi = transferOverviewApi;
        this.networkStateProvider = networkStateProvider;
        this.equipementInventoryRepository = equipementInventoryRepository;
    }


    public void getTransferLogs(TransferLogRequest transferOverviewRequest, final ProviderResult<TransferLogResponse> callback, LoginResponse loginResponse) {

//        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<TransferLogResponse> inventoryCategories1 = transferOverviewApi.getTransferLog(headers, transferOverviewRequest);

            inventoryCategories1.enqueue(new AbstractCallback<TransferLogResponse>() {
                @Override
                protected void handleFailure(Call<TransferLogResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<TransferLogResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<TransferLogResponse> response) {
                    if (response.body() != null) {
                        TransferLogResponse transferOverviewResponse = null;
                        try {
                            transferOverviewResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (transferOverviewResponse != null && transferOverviewResponse.getData() != null && transferOverviewResponse.getData().getLogs() != null && transferOverviewResponse.getStatus() == 200 && (transferOverviewResponse.getData().getResponsecode() == 101 || transferOverviewResponse.getData().getResponsecode() == 102)) {
                            callback.success(transferOverviewResponse);
                        } else if (transferOverviewResponse != null) {
                            callback.failure(transferOverviewResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.failure(context.getString(R.string.internet_connection_check));
        }

    }

    public void getTransferLocation(TransferLogFilterRequest transferOverviewRequest, final ProviderResult<TransferLogLocationResponse> callback, LoginResponse loginResponse) {

//        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<TransferLogLocationResponse> inventoryCategories1 = transferOverviewApi.getTransferLocations(headers, transferOverviewRequest);

            inventoryCategories1.enqueue(new AbstractCallback<TransferLogLocationResponse>() {
                @Override
                protected void handleFailure(Call<TransferLogLocationResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<TransferLogLocationResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<TransferLogLocationResponse> response) {
                    if (response.body() != null) {
                        TransferLogLocationResponse transferOverviewResponse = null;
                        try {
                            transferOverviewResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (transferOverviewResponse != null && transferOverviewResponse.getData() != null && transferOverviewResponse.getData().getLocations() != null && transferOverviewResponse.getStatus() == 200 && (transferOverviewResponse.getData().getResponsecode() == 101 || transferOverviewResponse.getData().getResponsecode() == 102)) {
                            callback.success(transferOverviewResponse);
                        } else if (transferOverviewResponse != null) {
                            callback.failure(transferOverviewResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.failure(context.getString(R.string.internet_connection_check));
        }

    }

    public void getTransferLogDetailRequest(TransferLogDetailRequest transferOverviewRequest, final ProviderResult<TransferLogDetailResponse> callback, LoginResponse loginResponse) {

//        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<TransferLogDetailResponse> inventoryCategories1 = transferOverviewApi.getTransferLogDetails(headers, transferOverviewRequest);

            inventoryCategories1.enqueue(new AbstractCallback<TransferLogDetailResponse>() {
                @Override
                protected void handleFailure(Call<TransferLogDetailResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<TransferLogDetailResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<TransferLogDetailResponse> response) {
                    if (response.body() != null) {
                        TransferLogDetailResponse transferOverviewResponse = null;
                        try {
                            transferOverviewResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (transferOverviewResponse != null && transferOverviewResponse.getData() != null && transferOverviewResponse.getData().getDetails() != null && transferOverviewResponse.getStatus() == 200 && (transferOverviewResponse.getData().getResponsecode() == 101 || transferOverviewResponse.getData().getResponsecode() == 102)) {
                            callback.success(transferOverviewResponse);
                        } else if (transferOverviewResponse != null) {
                            callback.failure(transferOverviewResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.failure(context.getString(R.string.internet_connection_check));
        }

    }
}
