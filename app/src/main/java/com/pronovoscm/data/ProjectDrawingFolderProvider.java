package com.pronovoscm.data;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.pronovoscm.LogData;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.api.ProjectDrawingFolderApi;
import com.pronovoscm.model.request.drawingfolder.DrawingFolderRequest;
import com.pronovoscm.model.response.AbstractCallback;
import com.pronovoscm.model.response.ErrorResponse;
import com.pronovoscm.model.response.drawingfolder.DrawingFolderResponse;
import com.pronovoscm.model.response.drawingfolder.Drawingfolders;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.logresponse.LogRequest;
import com.pronovoscm.model.response.logresponse.LogResponse;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.DrawingFolders;
import com.pronovoscm.persistence.domain.DrawingFoldersDao;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.FileUtils;
import com.pronovoscm.utils.SharedPref;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Response;

public class ProjectDrawingFolderProvider {


    private final String TAG = ProjectDrawingFolderProvider.class.getName();
    private final ProjectDrawingFolderApi mProjectDrawingApi;
    NetworkStateProvider networkStateProvider;
    private PronovosApplication context;
    private DaoSession daoSession;
    private LoginResponse loginResponse;

    public ProjectDrawingFolderProvider(NetworkStateProvider networkStateProvider, ProjectDrawingFolderApi mProjectDrawingApi, DaoSession daoSession) {
        this.context = PronovosApplication.getContext();
        context.setUrl(Constants.BASE_API_URL);
        this.mProjectDrawingApi = mProjectDrawingApi;
        this.networkStateProvider = networkStateProvider;
        this.daoSession = daoSession;
    }

    public void getDrawingFolderList(final DrawingFolderRequest drawingFolderRequest, final ProviderResult<List<DrawingFolders>> listProviderResult) {

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            headers.put("lastupdate", getMAXDrawingUpdateDate(drawingFolderRequest.getProject_id()));
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            Call<DrawingFolderResponse> photoDetailResponseCall = mProjectDrawingApi.getProjectDrawingFolder(headers, drawingFolderRequest);

            photoDetailResponseCall.enqueue(new AbstractCallback<DrawingFolderResponse>() {
                @Override
                protected void handleFailure(Call<DrawingFolderResponse> call, Throwable throwable) {
                    listProviderResult.failure(throwable.getMessage());
                    listProviderResult.success(getDrawingFolders(drawingFolderRequest.getProject_id(), ""));
                }

                @Override
                protected void handleError(Call<DrawingFolderResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        listProviderResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        listProviderResult.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<DrawingFolderResponse> response) {
                    if (response.body() != null) {
                        DrawingFolderResponse drawingFolderResponse = null;
                        try {
                            drawingFolderResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (drawingFolderResponse != null && drawingFolderResponse.getStatus() == 200 &&
                                (drawingFolderResponse.getDrawingFolderData().getResponseCode() == 101 || drawingFolderResponse.getDrawingFolderData().getResponseCode() == 102)) {
                            List<DrawingFolders> drawingFolders = doUpdateDrawingFolderTable(response.body().getDrawingFolderData().getDrawingfolders(), response.body().getDrawingFolderData().getPj_projects_id());
//                            updateLastUpdateAll(response.body().getDrawingFolderData().getPj_projects_id());
                            listProviderResult.success(drawingFolders);

                        } else if (drawingFolderResponse != null) {
                            listProviderResult.failure(drawingFolderResponse.getMessage());
                        } else {
                            listProviderResult.failure("response null");
                        }
                    } else {
                        listProviderResult.failure("response null");
                    }
                }
            });

        } else {
            listProviderResult.success(getDrawingFolders(drawingFolderRequest.getProject_id(), ""));
        }
    }

    /**
     * Get max updated at of drawing folders according to project id.
     *
     * @param projectId
     * @return (Default value 1970 - 01 - 01 01 : 01 : 01)
     */

    public String getMAXDrawingUpdateDate(int projectId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        List<DrawingFolders> maxPostIdRow = daoSession.getDrawingFoldersDao().queryBuilder().where(DrawingFoldersDao.Properties.UpdatedAt.isNotNull(), DrawingFoldersDao.Properties.PjProjectsId.eq(projectId), DrawingFoldersDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).orderDesc(DrawingFoldersDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1970-01-01 01:01:01";
    }

    /**
     * Insert or Drawing Folder Table
     *
     * @param drawingfolders
     * @param projectId
     * @return
     */
    public List<DrawingFolders> doUpdateDrawingFolderTable(List<Drawingfolders> drawingfolders, int projectId) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        try {
            daoSession.callInTx(new Callable<List<Drawingfolders>>() {
                DrawingFoldersDao mDrawingFoldersDao = daoSession.getDrawingFoldersDao();

                @Override
                public List<Drawingfolders> call() throws Exception {
                    for (Drawingfolders drawingfolders : drawingfolders) {

                        List<DrawingFolders> drawingFolders1 = daoSession.getDrawingFoldersDao().queryBuilder().where(DrawingFoldersDao.Properties.DrwFoldersId.eq(drawingfolders.getDrwFoldersId()), DrawingFoldersDao.Properties.PjProjectsId.eq(projectId), DrawingFoldersDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).limit(1).list();

                        if (drawingfolders.getDeletedAt() == null || TextUtils.isEmpty(drawingfolders.getDeletedAt())) {

                            if (drawingFolders1.size() > 0) {
                                drawingFolders1.get(0).setCreatedAt(drawingfolders.getCreatedAt() != null && !drawingfolders.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(drawingfolders.getCreatedAt()) : null);
                                drawingFolders1.get(0).setUpdatedAt(drawingfolders.getUpdatedAt() != null && !drawingfolders.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(drawingfolders.getUpdatedAt()) : null);
                                drawingFolders1.get(0).setFolderName(drawingfolders.getFolderName());
                                drawingFolders1.get(0).setPjProjectsId(projectId);
                                drawingFolders1.get(0).setDeletedAt(null);
                                drawingFolders1.get(0).setUsersId(loginResponse.getUserDetails().getUsers_id());
                                drawingFolders1.get(0).setFolderDescription(drawingfolders.getFolderDescription());
                                drawingFolders1.get(0).setLastupdatedate(null);
                                mDrawingFoldersDao.update(drawingFolders1.get(0));
                            } else {
                                DrawingFolders drawingFolders = new DrawingFolders();
                                drawingFolders.setCreatedAt(drawingfolders.getCreatedAt() != null && !drawingfolders.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(drawingfolders.getCreatedAt()) : null);
                                drawingFolders.setUpdatedAt(drawingfolders.getUpdatedAt() != null && !drawingfolders.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(drawingfolders.getUpdatedAt()) : null);
                                drawingFolders.setSyncFolder(false);
                                drawingFolders.setSyncDrawingFolder(false);
                                drawingFolders.setFolderName(drawingfolders.getFolderName());
                                drawingFolders.setPjProjectsId(projectId);
                                drawingFolders.setDrwFoldersId(drawingfolders.getDrwFoldersId());
                                drawingFolders.setDeletedAt(null);
                                drawingFolders.setUsersId(loginResponse.getUserDetails().getUsers_id());
                                drawingFolders.setFolderDescription(drawingfolders.getFolderDescription());
                                drawingFolders.setLastupdatedate(null);
                                drawingFolders.setLastUpdateXml(null);
                                mDrawingFoldersDao.save(drawingFolders);
                            }
                        } else if (drawingFolders1.size() > 0) {

                            DeleteQuery<DrawingFolders> photoFolderDeleteQuery = daoSession.queryBuilder(DrawingFolders.class)
                                    .where(DrawingFoldersDao.Properties.DrwFoldersId.eq(drawingfolders.getDrwFoldersId()), DrawingFoldersDao.Properties.PjProjectsId.eq(projectId), DrawingFoldersDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                                    .buildDelete();
                            photoFolderDeleteQuery.executeDeleteWithoutDetachingEntities();
                        }

                    }
                    return drawingfolders;
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getDrawingFolders(projectId, "");
    }

    /**
     * Get Drawing folders according to project.
     *
     * @param projectId
     * @return list of Photo Folder.
     */
    public List<DrawingFolders> getDrawingFolders(int projectId, String searchString) {
        // get projects according to region id.
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            QueryBuilder<DrawingFolders> queryBuilder = daoSession.getDrawingFoldersDao().queryBuilder();
            queryBuilder.where(DrawingFoldersDao.Properties.PjProjectsId.eq(projectId), DrawingFoldersDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()), DrawingFoldersDao.Properties.FolderName.like("%" + searchString + "%")).orderAsc(DrawingFoldersDao.Properties.CreatedAt);
            List<DrawingFolders> result = queryBuilder.build().list();
            return result;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Get Drawing folder according to project.
     */
    public DrawingFolders getDrawingFolder(int projectId, int folderId) {
        // get projects according to region id.
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        QueryBuilder<DrawingFolders> queryBuilder = daoSession.getDrawingFoldersDao().queryBuilder();
        queryBuilder.where(DrawingFoldersDao.Properties.PjProjectsId.eq(projectId), DrawingFoldersDao.Properties.DrwFoldersId.eq(folderId), DrawingFoldersDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).orderAsc(DrawingFoldersDao.Properties.FolderName);
        List<DrawingFolders> drawingFolders = queryBuilder.build().list();
        if (drawingFolders.size() > 0) {
            DrawingFolders result = queryBuilder.build().list().get(0);
            return result;
        } else {
            return null;
        }
    }


    /**
     * Update Drawing folder according to project.
     */
    public void updateDrawingFolder(int projectId, int folderId, boolean isAutoSync) {
        // get projects according to region id.
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        QueryBuilder<DrawingFolders> queryBuilder = daoSession.getDrawingFoldersDao().queryBuilder();
        queryBuilder.where(DrawingFoldersDao.Properties.PjProjectsId.eq(projectId),
                DrawingFoldersDao.Properties.DrwFoldersId.eq(folderId),
                DrawingFoldersDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).orderAsc(DrawingFoldersDao.Properties.FolderName);
        DrawingFolders result = queryBuilder.build().list().get(0);
        result.setSyncFolder(isAutoSync);
        daoSession.getDrawingFoldersDao().update(result);
    }

    public void updateDrawingFolderSync(int projectId, int folderId, boolean isChecked) {
        // get projects according to region id.
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        QueryBuilder<DrawingFolders> queryBuilder = daoSession.getDrawingFoldersDao().queryBuilder();
        queryBuilder.where(DrawingFoldersDao.Properties.PjProjectsId.eq(projectId),
                DrawingFoldersDao.Properties.DrwFoldersId.eq(folderId),
                DrawingFoldersDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).orderAsc(DrawingFoldersDao.Properties.FolderName);
        DrawingFolders result = queryBuilder.build().list().get(0);
        result.setSyncDrawingFolder(isChecked);
        daoSession.getDrawingFoldersDao().update(result);
    }

}
