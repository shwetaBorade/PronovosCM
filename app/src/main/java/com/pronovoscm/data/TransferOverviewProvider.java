package com.pronovoscm.data;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.api.TransferOverviewApi;
import com.pronovoscm.model.request.createtransfer.CreateTransferRequest;
import com.pronovoscm.model.request.deleteequipment.DeleteEquipmentRequest;
import com.pronovoscm.model.request.transfercontact.TransferContactRequest;
import com.pronovoscm.model.request.transferdetails.TransferDetailRequest;
import com.pronovoscm.model.request.transferlocation.TransferLocationRequest;
import com.pronovoscm.model.request.transferoverview.TransferOverviewRequest;
import com.pronovoscm.model.request.transferrequest.TransferRequest;
import com.pronovoscm.model.response.AbstractCallback;
import com.pronovoscm.model.response.ErrorResponse;
import com.pronovoscm.model.response.createtransfer.CreateTransferResponse;
import com.pronovoscm.model.response.deleteequipment.DeleteEquipmentResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.transfercontacts.TransferContactsResponse;
import com.pronovoscm.model.response.transferdelete.TransferDeleteResponse;
import com.pronovoscm.model.response.transferdetail.TransferDetailResponse;
import com.pronovoscm.model.response.transferlocation.TransferLocationResponse;
import com.pronovoscm.model.response.transferlocation.TransferLocationVendorResponse;
import com.pronovoscm.model.response.transferoverview.TransferOverviewResponse;
import com.pronovoscm.model.response.transferoverviewcount.TransferOverviewCountResponse;
import com.pronovoscm.model.response.transferrequest.TransferRequestResponse;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.repository.EquipementInventoryRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;

import java.net.SocketException;
import java.util.HashMap;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;

public class TransferOverviewProvider {


    private final String TAG = TransferOverviewProvider.class.getName();
    private final TransferOverviewApi transferOverviewApi;
    NetworkStateProvider networkStateProvider;
    private PronovosApplication context;
    private EquipementInventoryRepository equipementInventoryRepository;


    public TransferOverviewProvider(NetworkStateProvider networkStateProvider, TransferOverviewApi transferOverviewApi, DaoSession daoSession, EquipementInventoryRepository equipementInventoryRepository) {
        this.context = PronovosApplication.getContext();
        context.setUrl(Constants.BASE_API_URL);
        this.transferOverviewApi = transferOverviewApi;
        this.networkStateProvider = networkStateProvider;
        this.equipementInventoryRepository = equipementInventoryRepository;
    }


    public void getTransferOverviews(TransferOverviewRequest transferOverviewRequest, final ProviderResult<TransferOverviewResponse> callback, LoginResponse loginResponse) {

//        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<TransferOverviewResponse> inventoryCategories1 = transferOverviewApi.getTransferOverview(headers, transferOverviewRequest);

            inventoryCategories1.enqueue(new AbstractCallback<TransferOverviewResponse>() {
                @Override
                protected void handleFailure(Call<TransferOverviewResponse> call, Throwable throwable) {
                    if (throwable instanceof SocketException) {
                        callback.failure(context.getString(R.string.internet_connection_check_transfer_overview));
                    } else {
                        callback.failure(throwable.getMessage());
                    }
                }

                @Override
                protected void handleError(Call<TransferOverviewResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<TransferOverviewResponse> response) {
                    if (response.body() != null) {
                        TransferOverviewResponse transferOverviewResponse = null;
                        try {
                            transferOverviewResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (transferOverviewResponse != null && transferOverviewResponse.getData() != null && transferOverviewResponse.getData().getTransfers() != null && transferOverviewResponse.getStatus() == 200 && (transferOverviewResponse.getData().getResponsecode() == 101 || transferOverviewResponse.getData().getResponsecode() == 102)) {
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
            callback.failure(context.getString(R.string.internet_connection_check_transfer_overview));
        }

    }
    public void getTransferOverview(TransferOverviewRequest transferOverviewRequest, final ProviderResult<TransferOverviewCountResponse> callback, LoginResponse loginResponse) {

//        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<TransferOverviewCountResponse> inventoryCategories1 = transferOverviewApi.getTransferOverviewNew(headers, transferOverviewRequest);

            inventoryCategories1.enqueue(new AbstractCallback<TransferOverviewCountResponse>() {
                @Override
                protected void handleFailure(Call<TransferOverviewCountResponse> call, Throwable throwable) {
                    if (throwable instanceof SocketException) {
                        callback.failure(context.getString(R.string.internet_connection_check_transfer_overview));
                    } else {
                        callback.failure(throwable.getMessage());
                    }
                }

                @Override
                protected void handleError(Call<TransferOverviewCountResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<TransferOverviewCountResponse> response) {
                    if (response.body() != null) {
                        TransferOverviewCountResponse transferOverviewResponse = null;
                        try {
                            transferOverviewResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (transferOverviewResponse != null && transferOverviewResponse.getData() != null && transferOverviewResponse.getData().getTransferCount() != null && transferOverviewResponse.getStatus() == 200 && (transferOverviewResponse.getData().getResponsecode() == 101 || transferOverviewResponse.getData().getResponsecode() == 102)) {
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
            callback.failure(context.getString(R.string.internet_connection_check_transfer_overview));
        }

    }

    public void getTransferLocations(TransferLocationRequest transferLocationRequest, final ProviderResult<TransferLocationResponse> callback, LoginResponse loginResponse) {

//        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<TransferLocationResponse> inventoryCategories1 = transferOverviewApi.getTransferLocation(headers, transferLocationRequest);

            inventoryCategories1.enqueue(new AbstractCallback<TransferLocationResponse>() {
                @Override
                protected void handleFailure(Call<TransferLocationResponse> call, Throwable throwable) {
                    if (throwable instanceof SocketException) {
                        callback.failure(context.getString(R.string.internet_connection_check_transfer_overview));
                    } else {
                        callback.failure(throwable.getMessage());
                    }
                }

                @Override
                protected void handleError(Call<TransferLocationResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<TransferLocationResponse> response) {
                    if (response.body() != null) {
                        TransferLocationResponse transferLocationResponse = null;
                        try {
                            transferLocationResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (transferLocationResponse != null && transferLocationResponse.getStatus() == 200 && (transferLocationResponse.getData().getResponsecode() == 101 || transferLocationResponse.getData().getResponsecode() == 102)) {
                            callback.success(transferLocationResponse);
                        } else if (transferLocationResponse != null) {
                            callback.failure(transferLocationResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        }
    }

    public void getTransferVendorLocations(TransferLocationRequest transferLocationRequest, final ProviderResult<TransferLocationVendorResponse> callback, LoginResponse loginResponse) {

//        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<TransferLocationVendorResponse> inventoryCategories1 = transferOverviewApi.getTransferLocationVendor(headers, transferLocationRequest);

            inventoryCategories1.enqueue(new AbstractCallback<TransferLocationVendorResponse>() {
                @Override
                protected void handleFailure(Call<TransferLocationVendorResponse> call, Throwable throwable) {
                    if (throwable instanceof SocketException) {
                        callback.failure(context.getString(R.string.internet_connection_check_transfer_overview));
                    } else {
                        callback.failure(throwable.getMessage());
                    }
                }

                @Override
                protected void handleError(Call<TransferLocationVendorResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<TransferLocationVendorResponse> response) {
                    if (response.body() != null) {
                        TransferLocationVendorResponse transferLocationResponse = null;
                        try {
                            transferLocationResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (transferLocationResponse != null && transferLocationResponse.getStatus() == 200 && (transferLocationResponse.getData().getResponsecode() == 101 || transferLocationResponse.getData().getResponsecode() == 102)) {
                            callback.success(transferLocationResponse);
                        } else if (transferLocationResponse != null) {
                            callback.failure(transferLocationResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        }
    }

    public void getTransferContacts(TransferContactRequest transfertransferContactRequest, final ProviderResult<TransferContactsResponse> callback, LoginResponse loginResponse) {

//        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<TransferContactsResponse> transferContacts = transferOverviewApi.getTransferContacts(headers, transfertransferContactRequest);

            transferContacts.enqueue(new AbstractCallback<TransferContactsResponse>() {
                @Override
                protected void handleFailure(Call<TransferContactsResponse> call, Throwable throwable) {
                    if (throwable instanceof SocketException) {
                        callback.failure(context.getString(R.string.internet_connection_check_transfer_overview));
                    } else {
                        callback.failure(throwable.getMessage());
                    }
                }

                @Override
                protected void handleError(Call<TransferContactsResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<TransferContactsResponse> response) {
                    if (response.body() != null) {
                        TransferContactsResponse transferLocationResponse = null;
                        try {
                            transferLocationResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (transferLocationResponse != null && transferLocationResponse.getStatus() == 200 && (transferLocationResponse.getData().getResponsecode() == 101 || transferLocationResponse.getData().getResponsecode() == 102)) {
                            callback.success(transferLocationResponse);
                        } else if (transferLocationResponse != null) {
                            callback.failure(transferLocationResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
//            List<EquipmentCategoriesDetails> repositoryEquipmentDetails = equipementInventoryRepository.getEquipmentDetails(projectId,eqCategoryId, loginResponse.getUserDetails().getUsers_id(),inventoryEquipmentsRequest.getEqSubCategoriesId());
//            callback.success(repositoryEquipmentDetails);
        }

    }

    public void callTransferRequest(TransferRequest transferRequest, final ProviderResult<TransferRequestResponse> callback, LoginResponse loginResponse) {
        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<TransferRequestResponse> transferContacts = transferOverviewApi.callTransferRequest(headers, transferRequest);

            transferContacts.enqueue(new AbstractCallback<TransferRequestResponse>() {
                @Override
                protected void handleFailure(Call<TransferRequestResponse> call, Throwable throwable) {
                    if (throwable instanceof SocketException) {
                        callback.failure(context.getString(R.string.internet_connection_check_transfer_overview));
                    } else {
                        callback.failure(throwable.getMessage());
                    }
                }

                @Override
                protected void handleError(Call<TransferRequestResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<TransferRequestResponse> response) {
                    if (response.body() != null) {
                        TransferRequestResponse transferLocationResponse = null;
                        try {
                            transferLocationResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (transferLocationResponse != null && transferLocationResponse.getData() != null && transferLocationResponse.getStatus() == 200 && (transferLocationResponse.getData().getResponsecode() == 101)) {
                            callback.success(transferLocationResponse);
                        } else if (transferLocationResponse != null && transferLocationResponse.getData() != null && transferLocationResponse.getData().getResponsemsg() != null) {
                            callback.failure(transferLocationResponse.getData().getResponsemsg());

                        } else if (transferLocationResponse != null && transferLocationResponse.getMessage() != null) {
                            callback.failure(transferLocationResponse.getMessage());

                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.failure(context.getString(R.string.internet_connection_check_transfer_overview));
        }

    }

    public void callCreateTransfer(CreateTransferRequest transferRequest, final ProviderResult<CreateTransferResponse> callback, LoginResponse loginResponse) {
        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<CreateTransferResponse> transferContacts = transferOverviewApi.callCreateTransfer(headers, transferRequest);

            transferContacts.enqueue(new AbstractCallback<CreateTransferResponse>() {
                @Override
                protected void handleFailure(Call<CreateTransferResponse> call, Throwable throwable) {
                    if (throwable instanceof SocketException) {
                        callback.failure(context.getString(R.string.internet_connection_check_transfer_overview));
                    } else {
                        callback.failure(throwable.getMessage());
                    }
                }

                @Override
                protected void handleError(Call<CreateTransferResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<CreateTransferResponse> response) {
                    if (response.body() != null) {
                        CreateTransferResponse transferLocationResponse = null;
                        try {
                            transferLocationResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (transferLocationResponse != null && transferLocationResponse.getData() != null && transferLocationResponse.getStatus() == 200 && (transferLocationResponse.getData().getResponsecode() == 101)) {
                            callback.success(transferLocationResponse);
                        } else if (transferLocationResponse != null && transferLocationResponse.getData() != null && transferLocationResponse.getData().getResponsemsg() != null) {
                            callback.failure(transferLocationResponse.getData().getResponsemsg());

                        } else if (transferLocationResponse != null && transferLocationResponse.getMessage() != null) {
                            callback.failure(transferLocationResponse.getMessage());

                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.failure(context.getString(R.string.internet_connection_check_transfer_overview));
        }

    }

    public void callTransferDetail(TransferDetailRequest transferRequest, final ProviderResult<TransferDetailResponse> callback, LoginResponse loginResponse) {
        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<TransferDetailResponse> transferContacts = transferOverviewApi.callTransferDetailRequest(headers, transferRequest);

            transferContacts.enqueue(new AbstractCallback<TransferDetailResponse>() {
                @Override
                protected void handleFailure(Call<TransferDetailResponse> call, Throwable throwable) {
                    if (throwable instanceof SocketException) {
                        callback.failure(context.getString(R.string.internet_connection_check_transfer_overview));
                    } else {
                        callback.failure(throwable.getMessage());
                    }
                }

                @Override
                protected void handleError(Call<TransferDetailResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<TransferDetailResponse> response) {
                    if (response.body() != null) {
                        TransferDetailResponse transferLocationResponse = null;
                        try {
                            transferLocationResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (transferLocationResponse != null && transferLocationResponse.getData() != null && transferLocationResponse.getStatus() == 200 && (transferLocationResponse.getData().getResponsecode() == 101 || transferLocationResponse.getData().getResponsecode() == 102)) {
                            callback.success(transferLocationResponse);
                        } else if (transferLocationResponse != null && transferLocationResponse.getData() != null && transferLocationResponse.getData().getResponsemsg() != null) {
                            callback.failure(transferLocationResponse.getData().getResponsemsg());

                        } else if (transferLocationResponse != null && transferLocationResponse.getMessage() != null) {
                            callback.failure(transferLocationResponse.getMessage());

                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.failure(context.getString(R.string.internet_connection_check_transfer_overview));
        }

    }

    public void callTransferDelete(TransferDetailRequest transferRequest, final ProviderResult<TransferDeleteResponse> callback, LoginResponse loginResponse) {
        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<TransferDeleteResponse> transferContacts = transferOverviewApi.callTransferDelete(headers, transferRequest);

            transferContacts.enqueue(new AbstractCallback<TransferDeleteResponse>() {
                @Override
                protected void handleFailure(Call<TransferDeleteResponse> call, Throwable throwable) {
                    if (throwable instanceof SocketException) {
                        callback.failure(context.getString(R.string.internet_connection_check_transfer_overview));
                    } else {
                        callback.failure(throwable.getMessage());
                    }
                }

                @Override
                protected void handleError(Call<TransferDeleteResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<TransferDeleteResponse> response) {
                    if (response.body() != null) {
                        TransferDeleteResponse transferLocationResponse = null;
                        try {
                            transferLocationResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (transferLocationResponse != null && transferLocationResponse.getData() != null && transferLocationResponse.getStatus() == 200 && (transferLocationResponse.getData().getResponsecode() == 101 || transferLocationResponse.getData().getResponsecode() == 102)) {
                            callback.success(transferLocationResponse);
                        } else if (transferLocationResponse != null && transferLocationResponse.getData() != null && transferLocationResponse.getData().getResponsemsg() != null) {
                            callback.failure(transferLocationResponse.getData().getResponsemsg());

                        } else if (transferLocationResponse != null && transferLocationResponse.getMessage() != null) {
                            callback.failure(transferLocationResponse.getMessage());

                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.failure(context.getString(R.string.internet_connection_check_transfer_overview));
        }

    }

    public void callDeleteEquipment(DeleteEquipmentRequest deleteEquipmentRequest, ProviderResult<DeleteEquipmentResponse> deleteEquipmentResponseProviderResult, LoginResponse loginResponse) {
        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<DeleteEquipmentResponse> transferContacts = transferOverviewApi.callDeleteEquipment(headers, deleteEquipmentRequest);

            transferContacts.enqueue(new AbstractCallback<DeleteEquipmentResponse>() {
                @Override
                protected void handleFailure(Call<DeleteEquipmentResponse> call, Throwable throwable) {
                    if (throwable instanceof SocketException) {
                        deleteEquipmentResponseProviderResult.failure(context.getString(R.string.internet_connection_check_transfer_overview));
                    } else {
                        deleteEquipmentResponseProviderResult.failure(throwable.getMessage());
                    }
                }

                @Override
                protected void handleError(Call<DeleteEquipmentResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        deleteEquipmentResponseProviderResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        deleteEquipmentResponseProviderResult.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<DeleteEquipmentResponse> response) {
                    if (response.body() != null) {
                        DeleteEquipmentResponse transferLocationResponse = null;
                        try {
                            transferLocationResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (transferLocationResponse != null && transferLocationResponse.getData() != null && transferLocationResponse.getStatus() == 200 && (transferLocationResponse.getData().getResponsecode() == 101 || transferLocationResponse.getData().getResponsecode() == 102)) {
                            deleteEquipmentResponseProviderResult.success(transferLocationResponse);
                        } else if (transferLocationResponse != null && transferLocationResponse.getData() != null && transferLocationResponse.getData().getResponsemsg() != null) {
                            deleteEquipmentResponseProviderResult.failure(transferLocationResponse.getData().getResponsemsg());

                        } else if (transferLocationResponse != null && transferLocationResponse.getMessage() != null) {
                            deleteEquipmentResponseProviderResult.failure(transferLocationResponse.getMessage());

                        } else {
                            deleteEquipmentResponseProviderResult.failure("response null");
                        }
                    } else {
                        deleteEquipmentResponseProviderResult.failure("response null");
                    }
                }
            });

        } else {
            deleteEquipmentResponseProviderResult.failure(context.getString(R.string.internet_connection_check_transfer_overview));
        }
    }
}
