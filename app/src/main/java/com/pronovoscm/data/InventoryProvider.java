package com.pronovoscm.data;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.api.InventoryApi;
import com.pronovoscm.model.request.inventory.InventoryRequest;
import com.pronovoscm.model.response.AbstractCallback;
import com.pronovoscm.model.response.ErrorResponse;
import com.pronovoscm.model.response.inventorycategories.InventoryCategoriesResponse;
import com.pronovoscm.model.response.inventoryequipment.InventoryEquipmentResponse;
import com.pronovoscm.model.response.inventoryresponse.InvRes;
import com.pronovoscm.model.response.inventorysubcategories.InventorySubCategoriesResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.EquipmentCategoriesMaster;
import com.pronovoscm.persistence.domain.EquipmentCategoriesMasterDao;
import com.pronovoscm.persistence.domain.EquipmentInventory;
import com.pronovoscm.persistence.domain.EquipmentInventoryDao;
import com.pronovoscm.persistence.domain.EquipmentRegion;
import com.pronovoscm.persistence.domain.EquipmentRegionDao;
import com.pronovoscm.persistence.domain.EquipmentSubCategoriesMaster;
import com.pronovoscm.persistence.domain.EquipmentSubCategoriesMasterDao;
import com.pronovoscm.persistence.repository.EquipementInventoryRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.DateFormatter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;

public class InventoryProvider {


    private final String TAG = InventoryProvider.class.getName();
    private final InventoryApi inventoryApi;
    NetworkStateProvider networkStateProvider;
    private PronovosApplication context;
    private DaoSession daoSession;
//    private LoginResponse loginResponse;
    private EquipementInventoryRepository equipementInventoryRepository;


    public InventoryProvider(NetworkStateProvider networkStateProvider, InventoryApi inventoryApi, DaoSession daoSession, EquipementInventoryRepository equipementInventoryRepository) {
        this.context = PronovosApplication.getContext();
        context.setUrl(Constants.BASE_API_URL);
        this.inventoryApi = inventoryApi;
        this.networkStateProvider = networkStateProvider;
        this.daoSession = daoSession;
        this.equipementInventoryRepository = equipementInventoryRepository;
    }

    public void getCategories(int projectId, final ProviderResult<List<EquipmentCategoriesMaster>> callback, LoginResponse loginResponse) {

//        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", getMAXEquipmentCategoriesUpdateDate(projectId));
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<InventoryCategoriesResponse> inventoryCategories1 = inventoryApi.getInventoryCategories(headers);

            inventoryCategories1.enqueue(new AbstractCallback<InventoryCategoriesResponse>() {
                @Override
                protected void handleFailure(Call<InventoryCategoriesResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<InventoryCategoriesResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<InventoryCategoriesResponse> response) {
                    if (response.body() != null) {
                        InventoryCategoriesResponse inventoryCategoriesResponse = null;
                        try {
                            inventoryCategoriesResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (inventoryCategoriesResponse != null && inventoryCategoriesResponse.getStatus() == 200 && (inventoryCategoriesResponse.getData().getResponseCode() == 101 || inventoryCategoriesResponse.getData().getResponseCode() == 102)) {
                            List<EquipmentCategoriesMaster> equipmentCategories =
                                    equipementInventoryRepository.doUpdateEquipmentCategoriesMaster(
                                            inventoryCategoriesResponse.getData().getCategories(), 0);

//                            callback.success(equipmentCategories);


                            callback.success(equipmentCategories);

                        } else if (inventoryCategoriesResponse != null) {
                            callback.failure(inventoryCategoriesResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.failure("No Internet");

           /* List<EquipmentCategories> equipmentCategories = equipementInventoryRepository.getEquipmentCategories(inventoryCategories.getProjectId(), loginResponse.getUserDetails().getUsers_id());
            callback.success(equipmentCategories);*/
        }

    }

    public void getSubCategories( final ProviderResult<List<EquipmentSubCategoriesMaster>> callback,LoginResponse loginResponse) {

//        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", getMAXEquipmentSubCategoriesUpdateDate(loginResponse));
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<InventorySubCategoriesResponse> inventoryCategories1 = inventoryApi.getInventorySubCategories(headers);

            inventoryCategories1.enqueue(new AbstractCallback<InventorySubCategoriesResponse>() {
                @Override
                protected void handleFailure(Call<InventorySubCategoriesResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<InventorySubCategoriesResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<InventorySubCategoriesResponse> response) {
                    if (response.body() != null) {
                        InventorySubCategoriesResponse inventoryCategoriesResponse = null;
                        try {
                            inventoryCategoriesResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (inventoryCategoriesResponse != null && inventoryCategoriesResponse.getStatus() == 200 && (inventoryCategoriesResponse.getData().getResponseCode() == 101 || inventoryCategoriesResponse.getData().getResponseCode() == 102)) {
                            List<EquipmentSubCategoriesMaster> equipmentCategories = equipementInventoryRepository.doUpdateEquipmentSubCategoriesMaster(inventoryCategoriesResponse.getData().getSubcategories(), 0);

                            callback.success(equipmentCategories);
                        } else if (inventoryCategoriesResponse != null) {
                            callback.failure(inventoryCategoriesResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.failure("No Internet");

//            List<EquipmentSubCategories> equipmentCategories = equipementInventoryRepository.getEquipmentSubCategories(projectId, inventoryCategories.getEqCategoriesId(), loginResponse.getUserDetails().getUsers_id());
//            callback.success(equipmentCategories);
        }

    }

    public void getEquipmentDetails(final ProviderResult<List<EquipmentRegion>> callback,LoginResponse loginResponse) {

//        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", getMAXEquipmentUpdateDate(loginResponse));
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<InventoryEquipmentResponse> inventoryCategories1 = inventoryApi.getEquipmentDetails(headers);

            inventoryCategories1.enqueue(new AbstractCallback<InventoryEquipmentResponse>() {
                @Override
                protected void handleFailure(Call<InventoryEquipmentResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<InventoryEquipmentResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<InventoryEquipmentResponse> response) {
                    if (response.body() != null) {
                        InventoryEquipmentResponse inventoryCategoriesResponse = null;
                        try {
                            inventoryCategoriesResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (inventoryCategoriesResponse != null && inventoryCategoriesResponse.getStatus() == 200 && (inventoryCategoriesResponse.getData().getResponsecode() == 101 || inventoryCategoriesResponse.getData().getResponsecode() == 102)) {
                            List<EquipmentRegion> equipmentCategories = equipementInventoryRepository.doUpdateEquipmentDetails(inventoryCategoriesResponse.getData().getEquipments(),0);
                            callback.success(equipmentCategories);
                        } else if (inventoryCategoriesResponse != null) {
                            callback.failure(inventoryCategoriesResponse.getMessage());
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
            callback.failure("No Internet");
        }

    }

    public void getInventory(InventoryRequest inventoryRequest,final ProviderResult<List<EquipmentInventory>> callback,LoginResponse loginResponse) {

//        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", getMAXEquipmentInventoryUpdateDate(loginResponse,inventoryRequest.getProjectId()));
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<InvRes> inventoryCategories1 = inventoryApi.getInventory(headers,inventoryRequest);

            inventoryCategories1.enqueue(new AbstractCallback<InvRes>() {
                @Override
                protected void handleFailure(Call<InvRes> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<InvRes> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<InvRes> response) {
                    if (response.body() != null) {
                        InvRes inventoryCategoriesResponse = null;
                        try {
                            inventoryCategoriesResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (inventoryCategoriesResponse != null && inventoryCategoriesResponse.getStatus() == 200 && (inventoryCategoriesResponse.getData().getResponsecode() == 101 || inventoryCategoriesResponse.getData().getResponsecode() == 102)) {
                            List<EquipmentInventory> equipmentCategories = equipementInventoryRepository.doUpdateEquipmentInventory(inventoryCategoriesResponse.getData().getInventory(), 0, inventoryRequest.getProjectId());
                            callback.success(equipmentCategories);
                        } else if (inventoryCategoriesResponse != null) {
                            callback.failure(inventoryCategoriesResponse.getMessage());
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
            callback.failure("No Internet");
        }

    }
    /**
     * Get max updated date from projects according to region id
     *
     * @return
     * @param projectId
     */
    private String getMAXEquipmentCategoriesUpdateDate(int projectId) {
        List<EquipmentCategoriesMaster> maxPostIdRow = daoSession.getEquipmentCategoriesMasterDao().queryBuilder().where(
                EquipmentCategoriesMasterDao.Properties.UpdatedAt.isNotNull(),
                EquipmentCategoriesMasterDao.Properties.UsersId.eq(0))
                .orderDesc(EquipmentCategoriesMasterDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1990-01-01 01:01:01";
    }
    /**
     * Get max updated date from projects according to region id
     *
     * @return
     */
    private String getMAXEquipmentSubCategoriesUpdateDate(LoginResponse loginResponse) {
        List<EquipmentSubCategoriesMaster> maxPostIdRow = daoSession.getEquipmentSubCategoriesMasterDao().queryBuilder()
                .where(EquipmentSubCategoriesMasterDao.Properties.UpdatedAt.isNotNull(),
                EquipmentSubCategoriesMasterDao.Properties.UsersId.eq(0))
                .orderDesc(EquipmentSubCategoriesMasterDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1990-01-01 01:01:01";
    }
    /**
     * Get max updated date from projects according to region id
     *
     * @return
     */
    private String getMAXEquipmentUpdateDate(LoginResponse loginResponse) {
        List<EquipmentRegion> maxPostIdRow = daoSession.getEquipmentRegionDao().queryBuilder()
                .where(EquipmentRegionDao.Properties.UpdatedAt.isNotNull(),
                        EquipmentRegionDao.Properties.UsersId.eq(0))
                .orderDesc(EquipmentRegionDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1990-01-01 01:01:01";
    }
    /**
     * Get max updated date from projects according to region id
     *
     * @return
     */
    private String getMAXEquipmentInventoryUpdateDate(LoginResponse loginResponse,int projectId) {
        List<EquipmentInventory> maxPostIdRow = daoSession.getEquipmentInventoryDao().queryBuilder()
                .where(EquipmentInventoryDao.Properties.UpdatedAt.isNotNull(),
                EquipmentInventoryDao.Properties.PjProjectsId.eq(projectId),
                        EquipmentInventoryDao.Properties.UsersId.eq(0))
                .orderDesc(EquipmentInventoryDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1990-01-01 01:01:01";
    }
}
