package com.pronovoscm.data;

import android.util.Log;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.api.UpdatePhotoDetailApi;
import com.pronovoscm.model.SyncDataEnum;
import com.pronovoscm.model.TransactionModuleEnum;
import com.pronovoscm.model.request.updatephoto.UpdatePhotoDetail;
import com.pronovoscm.model.request.updatephoto.UpdatePhotoDetail2;
import com.pronovoscm.model.response.AbstractCallback;
import com.pronovoscm.model.response.ErrorResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.updatephoto.UpdatePhotoDetailResponse;
import com.pronovoscm.model.response.updatephoto.UpdatedTag;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.ImageTag;
import com.pronovoscm.persistence.domain.PhotosMobile;
import com.pronovoscm.persistence.domain.PhotosMobileDao;
import com.pronovoscm.persistence.domain.Taggables;
import com.pronovoscm.persistence.domain.TaggablesDao;
import com.pronovoscm.persistence.domain.TransactionLogMobile;
import com.pronovoscm.persistence.domain.TransactionLogMobileDao;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.SharedPref;

import org.greenrobot.greendao.query.DeleteQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;

public class UpdatePhotoDetailsProvider {


    private final String TAG = UpdatePhotoDetailsProvider.class.getName();
    private final UpdatePhotoDetailApi loginApi;
    NetworkStateProvider networkStateProvider;
    private PronovosApplication context;
    private DaoSession daoSession;
    private LoginResponse loginResponse;

    public UpdatePhotoDetailsProvider(NetworkStateProvider networkStateProvider, UpdatePhotoDetailApi loginApi, DaoSession daoSession) {
        this.context = PronovosApplication.getContext();
        context.setUrl(Constants.BASE_API_URL);
        this.loginApi = loginApi;
        this.networkStateProvider = networkStateProvider;
        this.daoSession = daoSession;
    }


    public void updatePhotoDetails(final UpdatePhotoDetail updatePhotoDetail, PhotosMobile photosMobile, final ProviderResult<UpdatePhotoDetailResponse> photoDetailResponseProviderResult) {

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            headers.put("lastupdate", "");
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            headers.put("timezone", TimeZone.getDefault().getID());
            Call<UpdatePhotoDetailResponse> photoDetailResponseCall = loginApi.updatePhotoDetails(headers, updatePhotoDetail);

            photoDetailResponseCall.enqueue(new AbstractCallback<UpdatePhotoDetailResponse>() {
                @Override
                protected void handleFailure(Call<UpdatePhotoDetailResponse> call, Throwable throwable) {
                    photoDetailResponseProviderResult.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<UpdatePhotoDetailResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        photoDetailResponseProviderResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        photoDetailResponseProviderResult.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<UpdatePhotoDetailResponse> response) {
                    if (response.body() != null) {
                        UpdatePhotoDetailResponse albumResponse = null;
                        try {
                            albumResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (albumResponse != null && albumResponse.getStatus() == 200 && albumResponse.getData() != null
                                && albumResponse.getData().getPhoto() != null &&
                                (albumResponse.getData().getResponseCode() == 101 || albumResponse.getData().getResponseCode() == 102)) {

                            photosMobile.setDescriptions(albumResponse.getData().getPhoto().get(0).getDescription());
                            photosMobile.setIsawsSync(true);
                            photosMobile.setIsSync(true);
                            photosMobile.setUserId(loginResponse.getUserDetails().getUsers_id());
                            photosMobile.setUploadedBy(albumResponse.getData().getPhoto().get(0).getUploaded_by());
                            photosMobile.setPhotoName(albumResponse.getData().getPhoto().get(0).getPhoto_name());
                            photosMobile.setPhotoLocation(albumResponse.getData().getPhoto().get(0).getPhoto_location());
                            photosMobile.setPhotoThumb(albumResponse.getData().getPhoto().get(0).getPhoto_thumb());
                            photosMobile.setPjPhotosFolderId(albumResponse.getData().getAlbum_id());
                            photosMobile.setCreatedAt(albumResponse.getData().getPhoto().get(0).getCreated_at() != null && !albumResponse.getData().getPhoto().get(0).getCreated_at().equals("") ? DateFormatter.getDateFromDateTimeString(albumResponse.getData().getPhoto().get(0).getCreated_at()) : null);
                            photosMobile.setCreatedAt(albumResponse.getData().getPhoto().get(0).getUpdated_at() != null && !albumResponse.getData().getPhoto().get(0).getUpdated_at().equals("") ? DateFormatter.getDateFromDateTimeString(albumResponse.getData().getPhoto().get(0).getUpdated_at()) : null);


                            ArrayList<ImageTag> tags = new ArrayList<>();
                            for (UpdatedTag updatedTag : albumResponse.getData().getPhoto().get(0).getTags()) {
                                ImageTag imageTag = new ImageTag();
                                imageTag.setName(updatedTag.getTag_name());
                                imageTag.setId(updatedTag.getTag_id());
                                tags.add(imageTag);
                            }

                            updatePhotosData(photosMobile, tags);
                        } else if (albumResponse != null) {
                            photoDetailResponseProviderResult.failure(albumResponse.getMessage());
                        } else {
                            photoDetailResponseProviderResult.failure("response null");
                        }
                    } else {
                        photoDetailResponseProviderResult.failure("response null");
                    }
                }
            });

        }else {
            photoDetailResponseProviderResult.failure(context.getString(R.string.internet_connection_check));

        }
    }

    public void updatePhotoDetails2(final UpdatePhotoDetail2 updatePhotoDetail, PhotosMobile photosMobile, final ProviderResult<PhotosMobile> photoDetailResponseProviderResult) {

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            Call<UpdatePhotoDetailResponse> photoDetailResponseCall = loginApi.updatePhotoDetails2(headers, updatePhotoDetail);

            photoDetailResponseCall.enqueue(new AbstractCallback<UpdatePhotoDetailResponse>() {
                @Override
                protected void handleFailure(Call<UpdatePhotoDetailResponse> call, Throwable throwable) {
                    photoDetailResponseProviderResult.failure(throwable.getMessage());
                    photoDetailResponseProviderResult.success(photosMobile);
                }

                @Override
                protected void handleError(Call<UpdatePhotoDetailResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        photoDetailResponseProviderResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        photoDetailResponseProviderResult.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<UpdatePhotoDetailResponse> response) {
                    if (response.body() != null) {
                        UpdatePhotoDetailResponse albumResponse = null;
                        try {
                            albumResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (albumResponse != null && albumResponse.getStatus() == 200 && (albumResponse.getData().getResponseCode() == 101 || albumResponse.getData().getResponseCode() == 102)) {

                            photosMobile.setDescriptions(albumResponse.getData().getPhoto().get(0).getDescription());
                            photosMobile.setIsawsSync(true);
                            photosMobile.setIsSync(true);
                            photosMobile.setUserId(loginResponse.getUserDetails().getUsers_id());
                            photosMobile.setUploadedBy(albumResponse.getData().getPhoto().get(0).getUploaded_by());
                            photosMobile.setPhotoName(albumResponse.getData().getPhoto().get(0).getPhoto_name());
                            photosMobile.setPhotoLocation(albumResponse.getData().getPhoto().get(0).getPhoto_location());
                            photosMobile.setPhotoThumb(albumResponse.getData().getPhoto().get(0).getPhoto_thumb());
                            photosMobile.setPjPhotosFolderId(albumResponse.getData().getAlbum_id());
                            photosMobile.setCreatedAt(albumResponse.getData().getPhoto().get(0).getCreated_at() != null && !albumResponse.getData().getPhoto().get(0).getCreated_at().equals("") ? DateFormatter.getDateFromDateTimeString(albumResponse.getData().getPhoto().get(0).getCreated_at()) : null);
                            photosMobile.setCreatedAt(albumResponse.getData().getPhoto().get(0).getUpdated_at() != null && !albumResponse.getData().getPhoto().get(0).getUpdated_at().equals("") ? DateFormatter.getDateFromDateTimeString(albumResponse.getData().getPhoto().get(0).getUpdated_at()) : null);
                           // photosMobile.setDeletedAt(albumResponse.getData().getPhoto().get(0).getD);
                            ArrayList<ImageTag> tags = new ArrayList<>();
                            for (UpdatedTag updatedTag : albumResponse.getData().getPhoto().get(0).getTags()) {
                                ImageTag imageTag = new ImageTag();
                                imageTag.setName(updatedTag.getTag_name());
                                imageTag.setId(updatedTag.getTag_id());

                                tags.add(imageTag);
                            }

                            PhotosMobile photosMobile1 = updatePhotosData(photosMobile, tags);
                            photoDetailResponseProviderResult.success(photosMobile1);
                        } else if (albumResponse != null) {
                            photoDetailResponseProviderResult.failure(albumResponse.getMessage());
                        } else {
                            photoDetailResponseProviderResult.failure("response null");
                        }
                    } else {
                        photoDetailResponseProviderResult.failure("response null");
                    }
                }
            });

        } else {
            photoDetailResponseProviderResult.success(photosMobile);

        }
    }

    public PhotosMobile updatePhotosData(PhotosMobile photosMobile, ArrayList<ImageTag> selectedImageTags) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        int userId = loginResponse.getUserDetails().getUsers_id();
        PhotosMobileDao mPhotosMobileDao = daoSession.getPhotosMobileDao();

        List<PhotosMobile> photoFolderList = daoSession.getPhotosMobileDao().queryBuilder().where(PhotosMobileDao.Properties.PjPhotosId.eq(photosMobile.getPjPhotosId()), PhotosMobileDao.Properties.PjPhotosFolderId.eq(photosMobile.getPjPhotosFolderId()), PhotosMobileDao.Properties.PjProjectsId.eq(photosMobile.getPjProjectsId()), PhotosMobileDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id())).limit(1).list();
        if (photoFolderList.size() > 0) {
            photosMobile.setPjPhotosIdMobile(photoFolderList.get(0).getPjPhotosIdMobile());
//            Log.d(TAG, "Aki updatePhotosData: "+ photoFolderList.get(0).getPjPhotosIdMobile());
        }

        TaggablesDao mTaggablesDao = daoSession.getTaggablesDao();

// photosMobile.setPjPhotosIdMobile(photosMobile.getPjPhotosIdMobile());
        mPhotosMobileDao.insertOrReplace(photosMobile);

        DeleteQuery<Taggables> tableDeleteQuery = daoSession.queryBuilder(Taggables.class)
                .where(TaggablesDao.Properties.TaggableIdMobile.eq(photosMobile.getPjPhotosIdMobile()))
                .buildDelete();
        tableDeleteQuery.executeDeleteWithoutDetachingEntities();
        for (ImageTag imageTag :
                selectedImageTags) {
            Taggables taggables = new Taggables();
            taggables.setTagName(imageTag.getName());
            taggables.setTaggableId(photosMobile.getPjPhotosId());
            taggables.setTaggableIdMobile(photosMobile.getPjPhotosIdMobile());
            taggables.setTagId(imageTag.getId());
// taggables.setUserId(photosMobile.getUserId());
// taggables.setUserId(photosMobile.getUserId());
            taggables.setUserId(loginResponse.getUserDetails().getUsers_id());

            taggables.setCreatedAt(photosMobile.getCreatedAt());
            taggables.setUpdatedAt(photosMobile.getUpdatedAt());
            mTaggablesDao.save(taggables);
        }

        PhotosMobile photosMobile1 = daoSession.getPhotosMobileDao().queryBuilder().where(PhotosMobileDao.Properties.PjPhotosIdMobile.eq(photosMobile.getPjPhotosIdMobile()), PhotosMobileDao.Properties.PjPhotosFolderMobileId.eq(photosMobile.getPjPhotosFolderMobileId()), PhotosMobileDao.Properties.PjProjectsId.eq(photosMobile.getPjProjectsId())).limit(1).list().get(0);

        if (photosMobile.getPjPhotosId() != 0) {
            TransactionLogMobileDao mPronovosSyncDataDao = daoSession.getTransactionLogMobileDao();
            TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
            transactionLogMobile.setUsersId(userId);
            transactionLogMobile.setModule(TransactionModuleEnum.PHOTO.ordinal());
            transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
            transactionLogMobile.setMobileId(photosMobile.getPjPhotosIdMobile());
            transactionLogMobile.setServerId(Long.valueOf(photosMobile.getPjPhotosId()));
            transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
            mPronovosSyncDataDao.save(transactionLogMobile);
            Log.i(TAG, "updatePhotosData: setupAndStartWorkManager");
            context.setupAndStartWorkManager();
        }
        return photosMobile1;
    }
    /*public PhotosMobile updatePhotosData(PhotosMobile photosMobile, ArrayList<ImageTag> selectedImageTags) {
        PhotosMobileDao mPhotosMobileDao = daoSession.getPhotosMobileDao();
        TaggablesDao mTaggablesDao = daoSession.getTaggablesDao();

        photosMobile.setPjPhotosIdMobile(photosMobile.getPjPhotosIdMobile());
        mPhotosMobileDao.insertOrReplace(photosMobile);

        DeleteQuery<Taggables> tableDeleteQuery = daoSession.queryBuilder(Taggables.class)
                .where(TaggablesDao.Properties.TaggableIdMobile.eq(photosMobile.getPjPhotosIdMobile()))
                .buildDelete();
        tableDeleteQuery.executeDeleteWithoutDetachingEntities();
        for (ImageTag imageTag :
                selectedImageTags) {
            Taggables taggables = new Taggables();
            taggables.setTagName(imageTag.getName());
            taggables.setTaggableId(photosMobile.getPjPhotosId());
            taggables.setTaggableIdMobile(photosMobile.getPjPhotosIdMobile());
            taggables.setTagId(imageTag.getId());
//            taggables.setUserId(photosMobile.getUserId());
//            taggables.setUserId(photosMobile.getUserId());
            taggables.setUserId(loginResponse.getUserDetails().getUsers_id());

            taggables.setCreatedAt(photosMobile.getCreatedAt());
            taggables.setUpdatedAt(photosMobile.getUpdatedAt());
            mTaggablesDao.save(taggables);
        }

        PhotosMobile photosMobile1 = daoSession.getPhotosMobileDao().queryBuilder().where(PhotosMobileDao.Properties.PjPhotosIdMobile.eq(photosMobile.getPjPhotosIdMobile()), PhotosMobileDao.Properties.PjPhotosFolderMobileId.eq(photosMobile.getPjPhotosFolderMobileId()), PhotosMobileDao.Properties.PjProjectsId.eq(photosMobile.getPjProjectsId())).limit(1).list().get(0);
        return photosMobile1;
    }*/
}
