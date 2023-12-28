package com.pronovoscm.data;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.pronovoscm.LogData;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.activity.LoginActivity;
import com.pronovoscm.api.DrawingAnnotationApi;
import com.pronovoscm.model.SyncDataEnum;
import com.pronovoscm.model.TransactionModuleEnum;
import com.pronovoscm.model.request.drawingpunchlist.DrawingPunchlist;
import com.pronovoscm.model.request.drawingstore.DrawingStoreRequest;
import com.pronovoscm.model.response.AbstractCallback;
import com.pronovoscm.model.response.ErrorResponse;
import com.pronovoscm.model.response.drawingannotation.AnnotationData;
import com.pronovoscm.model.response.drawingannotation.DrawingAnnotationResponse;
import com.pronovoscm.model.response.drawingpunchlist.DrawingPunchlistResponse;
import com.pronovoscm.model.response.drawingstore.DrawingStoreAnnotationResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.DrawingList;
import com.pronovoscm.persistence.domain.DrawingXmls;
import com.pronovoscm.persistence.domain.DrawingXmlsDao;
import com.pronovoscm.persistence.domain.DrwPunchlist;
import com.pronovoscm.persistence.domain.DrwPunchlistDao;
import com.pronovoscm.persistence.domain.TransactionLogMobile;
import com.pronovoscm.persistence.domain.TransactionLogMobileDao;
import com.pronovoscm.persistence.repository.DrawingListRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.FileUtils;
import com.pronovoscm.utils.SharedPref;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Provider to Store and Get Annotation of Drawing
 *
 * @author GWL
 */
public class DrawingAnnotationProvider {


    private final String TAG = DrawingAnnotationProvider.class.getName();
    private final DrawingAnnotationApi mDrawingAnnotationApi;
    NetworkStateProvider networkStateProvider;
    private PronovosApplication context;
    private DaoSession daoSession;
    private LoginResponse loginResponse;
    private DrawingListRepository drawingListRepository;

    /**
     * Provider for Drawing Annotation
     *
     * @param networkStateProvider
     * @param drawingAnnotationApi
     * @param daoSession
     */
    public DrawingAnnotationProvider(NetworkStateProvider networkStateProvider, DrawingAnnotationApi drawingAnnotationApi, DaoSession daoSession, DrawingListRepository drawingListRepository) {
        this.context = PronovosApplication.getContext();
        context.setUrl(Constants.BASE_API_URL);
        this.mDrawingAnnotationApi = drawingAnnotationApi;
        this.networkStateProvider = networkStateProvider;
        this.daoSession = daoSession;
        this.drawingListRepository = drawingListRepository;
    }

    /**
     * Get Annotations of Drawing according to drawing id.
     *
     * @param drawingId
     * @param listProviderResult
     */
    public void getDrawingAnnotations(int folderID, final int drawingId, final ProviderResult<String> listProviderResult) {

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
//            headers.put("lastupdate", getMAXDrawingUpdateDate(drawingFolderRequest.getProject_id()));
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            Call<DrawingAnnotationResponse> drawingAnnotations = mDrawingAnnotationApi.getDrawingAnnotations(headers, drawingId);

            drawingAnnotations.enqueue(new AbstractCallback<DrawingAnnotationResponse>() {
                @Override
                protected void handleFailure(Call<DrawingAnnotationResponse> call, Throwable throwable) {
                    listProviderResult.failure(throwable.getMessage());
                    listProviderResult.success(null);
                }

                @Override
                protected void handleError(Call<DrawingAnnotationResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        listProviderResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        listProviderResult.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<DrawingAnnotationResponse> response) {
                    if (response != null && response.body() != null) {
                        DrawingAnnotationResponse drawingAnnotationResponse = null;
                        try {
                            drawingAnnotationResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (drawingAnnotationResponse != null && drawingAnnotationResponse.getStatus() == 200 && (drawingAnnotationResponse.getAnnotationData().getResponseCode() == 101 || drawingAnnotationResponse.getAnnotationData().getResponseCode() == 102)) {

                            String annotations = doUpdateDrawingAnnotationTable(response.body().getAnnotationData().getAnnotxml(), drawingId, true, true, false, "");

                            doUpdateDrawingListItem(folderID, response.body().getAnnotationData());
                            listProviderResult.success(annotations);

                        } else if (drawingAnnotationResponse != null) {
                            listProviderResult.failure(drawingAnnotationResponse.getMessage());
                        } else {
                            listProviderResult.failure("response null");
                        }
                    } else {
                        listProviderResult.failure("response null");
                    }
                }
            });

        } else {
            listProviderResult.success(getDrawingAnnotation(drawingId).getAnnotxml());
        }
    }

    /**
     * Get Annotations of Drawing according to drawing id.
     *
     * @param drawingId
     * @param listProviderResult
     */
    public void getDrawingAnnotations(int revisitedNum, int folderID, final int drawingId, final ProviderResult<String> listProviderResult) {

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
//            headers.put("lastupdate", getMAXDrawingUpdateDate(drawingFolderRequest.getProject_id()));
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            Call<DrawingAnnotationResponse> drawingAnnotations = mDrawingAnnotationApi.getDrawingAnnotations(headers, drawingId);

            drawingAnnotations.enqueue(new AbstractCallback<DrawingAnnotationResponse>() {
                @Override
                protected void handleFailure(Call<DrawingAnnotationResponse> call, Throwable throwable) {
                    listProviderResult.failure(throwable.getMessage());
                    listProviderResult.success(null);
                    //write logs
                    FileUtils.createFolderAndFile(new LogData("\n\n URL : " + call.request().url(),
                            "\n\nRequest: "+ drawingAnnotations.request(),
                            "\n Message : " + throwable.getMessage()));
                }

                @Override
                protected void handleError(Call<DrawingAnnotationResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        listProviderResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        listProviderResult.failure(errorResponse.getMessage());
                    }
                    //write logs
                    FileUtils.createFolderAndFile(new LogData("\n\n URL : " + call.request().url(),
                            "\n\nRequest: "+ drawingAnnotations.request(),
                            "\n Message : " + errorResponse.getMessage()));
                }

                @Override
                protected void handleSuccess(Response<DrawingAnnotationResponse> response) {
                    if (response != null && response.body() != null) {
                        DrawingAnnotationResponse drawingAnnotationResponse = null;
                        try {
                            drawingAnnotationResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (drawingAnnotationResponse != null && drawingAnnotationResponse.getStatus() == 200 && (drawingAnnotationResponse.getAnnotationData().getResponseCode() == 101 || drawingAnnotationResponse.getAnnotationData().getResponseCode() == 102)) {

                            String annotations = doUpdateDrawingAnnotationTable(response.body().getAnnotationData().getAnnotxml(), drawingId, true, true, false, "");

                            doUpdateDrawingListItem(revisitedNum, folderID, response.body().getAnnotationData());
                            listProviderResult.success(annotations);

                        } else if (drawingAnnotationResponse != null) {
                            listProviderResult.failure(drawingAnnotationResponse.getMessage());
                        } else {
                            listProviderResult.failure("response null");
                        }
                    } else {
                        listProviderResult.failure("response null");
                    }

                    //write logs
                    FileUtils.createFolderAndFile(new LogData("\n\n URL : " + response.raw().request().url(),
                            "\n\nRequest: "+   drawingAnnotations.request(),
                            "\n\nResponse: \nAnnotationData() : " + new Gson().toJson(response.body().getAnnotationData()) + "\n Message : "
                                    + response.body().getMessage()));
                }
            });

        } else {
            listProviderResult.success(getDrawingAnnotation(drawingId).getAnnotxml());
        }
    }

    /**
     * Get Annotations of Drawing according to drawing id.
     *
     * @param drawingPunchlist
     * @param listProviderResult
     */
    public void getDrawingPunchlist(DrawingPunchlist drawingPunchlist, LoginResponse loginResponse, final ProviderResult<DrawingPunchlistResponse> listProviderResult) {

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            headers.put("lastupdate", getMAXDrawingPunchlistUpdate(drawingPunchlist.getProjectId(), loginResponse));
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            Call<DrawingPunchlistResponse> drawingAnnotations = mDrawingAnnotationApi.getDrawingPunchlists(headers, drawingPunchlist);

            drawingAnnotations.enqueue(new AbstractCallback<DrawingPunchlistResponse>() {
                @Override
                protected void handleFailure(Call<DrawingPunchlistResponse> call, Throwable throwable) {
                    listProviderResult.failure(throwable.getMessage());
                    listProviderResult.success(null);
                    //write logs
                    FileUtils.createFolderAndFile(new LogData("\n\n URL : " + call.request().url(),
                            "\n\nRequest: "+   drawingAnnotations.request(),
                            "\n Message : " + throwable.getMessage()));
                }

                @Override
                protected void handleError(Call<DrawingPunchlistResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        listProviderResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        listProviderResult.failure(errorResponse.getMessage());
                    }
                    //write logs
                    FileUtils.createFolderAndFile(new LogData("\n\n URL : " + call.request().url(),
                            "\n\nRequest: "+  drawingAnnotations.request(),
                            "\n Message : " + errorResponse.getMessage()));
                }

                @Override
                protected void handleSuccess(Response<DrawingPunchlistResponse> response) {
                    if (response != null && response.body() != null) {
                        DrawingPunchlistResponse drawingPunchlistResponse = null;
                        try {
                            drawingPunchlistResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (drawingPunchlistResponse != null && drawingPunchlistResponse.getStatus() == 200 && (drawingPunchlistResponse.getData().getResponsecode() == 101 || drawingPunchlistResponse.getData().getResponsecode() == 102)) {

                            drawingListRepository.doUpdateDrawingPunchlist(drawingPunchlistResponse.getData().getDrwPunchlists());

//                            doUpdateDrawingListItem(folderID, response.body().getAnnotationData());
//                            listProviderResult.success(annotations);

                        } else if (drawingPunchlistResponse != null) {
                            listProviderResult.failure(drawingPunchlistResponse.getMessage());
                        } else {
                            listProviderResult.failure("response null");
                        }

                        //write logs
                        FileUtils.createFolderAndFile(new LogData("\n\n URL : " + response.raw().request().url(),
                                "\n\nRequest: "+ drawingAnnotations.request().toString(),
                                "\n\nResponse: \nDrwPunchlists : " + new Gson().toJson(response.body().getData().getDrwPunchlists()) + "\n Message : "
                                        + response.body().getMessage()));//

                    } else {
                        listProviderResult.failure("response null");
                    }
                }
            });

        } else {
//            listProviderResult.success(getDrawingAnnotation(drawingId).getAnnotxml());
        }
    }

    private String getMAXDrawingPunchlistUpdate(int projectId, LoginResponse loginResponse) {

        List<DrwPunchlist> maxPostIdRow = daoSession.getDrwPunchlistDao().queryBuilder().where(
                DrwPunchlistDao.Properties.UpdatedAt.isNotNull(),
                DrwPunchlistDao.Properties.PjProjectsId.eq(projectId),
                DrwPunchlistDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id())
        ).orderDesc(DrwPunchlistDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1970-01-01 01:01:01";
    }

    private void doUpdateDrawingListItem(int folderID, AnnotationData annotationData) {

        DrawingList originalDrawing = drawingListRepository.getDrawing(folderID, annotationData.getOriginalDrwId());
        if (originalDrawing != null) {
            originalDrawing.setUpdatedAt(annotationData.getUpdatedAt() != null && !annotationData.getUpdatedAt().equals("") ?
                    DateFormatter.getDateFromDateTimeString(annotationData.getUpdatedAt()) : null);
            originalDrawing.setCreatedAt(annotationData.getCreatedAt() != null && !annotationData.getCreatedAt().equals("") ?
                    DateFormatter.getDateFromDateTimeString(annotationData.getCreatedAt()) : null);
            drawingListRepository.updateDrawingList(originalDrawing);
        }
    }

    private void doUpdateDrawingListItem(int revisitedNum, int folderID, AnnotationData annotationData) {

        DrawingList originalDrawing = drawingListRepository.getDrawing(revisitedNum, folderID, annotationData.getOriginalDrwId());
        if (originalDrawing != null) {
            originalDrawing.setUpdatedAt(annotationData.getUpdatedAt() != null && !annotationData.getUpdatedAt().equals("") ?
                    DateFormatter.getDateFromDateTimeString(annotationData.getUpdatedAt()) : null);
            originalDrawing.setCreatedAt(annotationData.getCreatedAt() != null && !annotationData.getCreatedAt().equals("") ?
                    DateFormatter.getDateFromDateTimeString(annotationData.getCreatedAt()) : null);
            drawingListRepository.updateDrawingList(originalDrawing);
        }
    }

    /**
     * Store updated annotation to the server
     *
     * @param drawingStoreRequest
     * @param providerResult
     */
    public Call<DrawingStoreAnnotationResponse> getDrawingStoreAnnotations(final DrawingStoreRequest drawingStoreRequest, final ProviderResult<String> providerResult) {
        //    Log.d(TAG, "getDrawingStoreAnnotations: ");
        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            Call<DrawingStoreAnnotationResponse> drawingAnnotations = mDrawingAnnotationApi.setDrawingStroreAnnotations(headers, drawingStoreRequest);

            drawingAnnotations.enqueue(new AbstractCallback<DrawingStoreAnnotationResponse>() {
                @Override
                protected void handleFailure(Call<DrawingStoreAnnotationResponse> call, Throwable throwable) {
                    providerResult.failure(throwable.getMessage());
                    //write logs
                    FileUtils.createFolderAndFile(new LogData("\n\n URL : " + call.request().url(),
                            drawingAnnotations.request().toString(),
                            "\n Message : " + throwable.getMessage()));
                }

                @Override
                protected void handleError(Call<DrawingStoreAnnotationResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        providerResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        providerResult.failure(errorResponse.getMessage());
                    }
                    //write logs
                    FileUtils.createFolderAndFile(new LogData("\n\n URL : " + call.request().url(),
                            "\n\nRequest: "+  drawingAnnotations.request().toString(),
                            "\n Message : " + errorResponse.getMessage()));
                }

                @Override
                protected void handleSuccess(Response<DrawingStoreAnnotationResponse> response) {
                    //                 Log.d(TAG, " getDrawingStoreAnnotations     ****************************************************    handleSuccess: ");
                    if (response != null && response.body() != null) {
                        DrawingStoreAnnotationResponse drawingStoreAnnotationResponse = null;
                        try {
                            drawingStoreAnnotationResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (drawingStoreAnnotationResponse != null && drawingStoreAnnotationResponse.getStatus() == 200 && (drawingStoreAnnotationResponse.getData().getResponseCode() == 101 || drawingStoreAnnotationResponse.getData().getResponseCode() == 102)) {
                            doUpdateDrawingAnnotationTable(drawingStoreRequest.getAnnot_xml(), drawingStoreRequest.getDrawing_id(), true, true, false, "");
                            providerResult.success("");
                        } else if (drawingStoreAnnotationResponse != null) {
                            providerResult.failure(drawingStoreAnnotationResponse.getMessage());
                        } else {
                            providerResult.failure("response null");
                        }
                    } else {
                        providerResult.failure("response null");
                    }

                    //write logs
                    FileUtils.createFolderAndFile(new LogData("\n\n URL : " + response.raw().request().url(),"\n\nRequest: "+
                            drawingAnnotations.request(),
                            "\n\nResponse: \n Annotations : " + new Gson().toJson(response.body().getData()) + "\n Message : "
                                    + response.body().getMessage()));
                }
            });
            return drawingAnnotations;
        } else {
        }
        return null;
    }


    /**
     * Save annotation in the database with respect to drawing id
     *
     * @param annotxml        drawing annotation
     * @param drawingId       drawingId
     * @param sync            true if the synced to server
     * @param addToWorker
     * @param updateSyncAPI
     * @param deletedAnnotXml
     * @return
     */
    public String doUpdateDrawingAnnotationTable(String annotxml, int drawingId, boolean sync, boolean addToWorker, boolean updateSyncAPI, String deletedAnnotXml) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            int userId = loginResponse.getUserDetails().getUsers_id();
            DrawingXmlsDao drawingXmlsDao = daoSession.getDrawingXmlsDao();
            try {

                DrawingXmls drawingXml = getDrawingAnnotation(drawingId);
                if (drawingXml != null && !drawingXml.getIsSync() && updateSyncAPI) {
                    String xml = drawingXml.getAnnotxml();
                    Log.i(TAG, "doUpdateDrawingAnnotationTable: not update  xml  " + xml);
                    return xml;
                } else {
                    DeleteQuery<DrawingXmls> tableDeleteQuery = daoSession.queryBuilder(DrawingXmls.class).where(
                                    DrawingXmlsDao.Properties.DrwDrawingsId.eq(drawingId))
                            .buildDelete();
                    tableDeleteQuery.executeDeleteWithoutDetachingEntities();
                    DrawingXmls drawingXmls = new DrawingXmls();
                    drawingXmls.setAnnotxml(annotxml);
                    drawingXmls.setDrwDrawingsId(drawingId);
                    drawingXmls.setIsSync(sync);
                    drawingXmls.setAnnotdeletexml(deletedAnnotXml);
                    drawingXmls.setUsersId(loginResponse.getUserDetails().getUsers_id());
                    drawingXmlsDao.save(drawingXmls);
                    List<TransactionLogMobile> transactionLogMobiles = daoSession.getTransactionLogMobileDao().queryBuilder()
                            .where(TransactionLogMobileDao.Properties.MobileId.eq(0L),
                                    TransactionLogMobileDao.Properties.ServerId.eq(Long.valueOf(drawingId)),
                                    TransactionLogMobileDao.Properties.Module.eq(TransactionModuleEnum.DRAWING_ANNOTATION.ordinal()),
                                    TransactionLogMobileDao.Properties.Status.eq(SyncDataEnum.PROCESSING),
                                    TransactionLogMobileDao.Properties.UsersId.eq(userId)).list();

                    if (transactionLogMobiles.size() <= 0 && !sync && addToWorker) {
                        TransactionLogMobileDao mPronovosSyncDataDao = daoSession.getTransactionLogMobileDao();

                        TransactionLogMobile transactionLogMobile = new TransactionLogMobile();

                        transactionLogMobile.setUsersId(userId);
                        transactionLogMobile.setModule(TransactionModuleEnum.DRAWING_ANNOTATION.ordinal());

                        transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                        transactionLogMobile.setMobileId(0L);
                        transactionLogMobile.setServerId(Long.valueOf(drawingId));
                        transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                        mPronovosSyncDataDao.save(transactionLogMobile);
                        Log.i(TAG, "doUpdateDrawingAnnotationTable:  setupAndStartWorkManager   " + annotxml);


                        ((PronovosApplication) context.getApplicationContext()).setupAndStartWorkManager();
                    }
                    return annotxml;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return annotxml;
    }


    /**
     * Get annotation details of the drawing according to drawing id.
     *
     * @param drawingId id of the drawing
     * @return an object of {@DrawingXmls} which contains the details of the annotation
     */
    public DrawingXmls getDrawingAnnotation(int drawingId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        Cursor rawLengthDeleteXmlCursor = null;
        Cursor rawLengthAnnotxmlCursor = null;

        try {
            int drawingXmlLength = 0;
            int annotDeleteXmlLength = 0;
            Database greenDaoDB = PronovosApplication.getContext().getGreenDaoDB();
            SQLiteDatabase sqliteDB = (SQLiteDatabase) greenDaoDB.getRawDatabase();
            //SQLiteDatabase sqliteDB = greenDaoStandardDB.getSQLiteDatabase();
            rawLengthAnnotxmlCursor = sqliteDB.rawQuery("SELECT length(" + DrawingXmlsDao.Properties.Annotxml.columnName + " ) FROM   " + DrawingXmlsDao.TABLENAME + " WHERE "
                    + DrawingXmlsDao.Properties.DrwDrawingsId.columnName + " =?", new String[]{String.valueOf(drawingId)});
            if (rawLengthAnnotxmlCursor != null && rawLengthAnnotxmlCursor.moveToFirst()) {
                drawingXmlLength = rawLengthAnnotxmlCursor.getInt(0);
            }
            rawLengthDeleteXmlCursor = sqliteDB.rawQuery("SELECT length(" + DrawingXmlsDao.Properties.Annotdeletexml.columnName + " ) FROM   " + DrawingXmlsDao.TABLENAME + " WHERE "
                    + DrawingXmlsDao.Properties.DrwDrawingsId.columnName + " =?", new String[]{String.valueOf(drawingId)});
            if (rawLengthDeleteXmlCursor != null && rawLengthDeleteXmlCursor.moveToFirst())

                annotDeleteXmlLength = rawLengthDeleteXmlCursor.getInt(0);

            Log.e(TAG, "******** getDrawingAnnotation:drawingXmlLength  " + drawingXmlLength + "  annotDeleteXmlLength   " + annotDeleteXmlLength);
            if (drawingXmlLength < 2000000 && annotDeleteXmlLength < 2000000 && (drawingXmlLength + annotDeleteXmlLength) < 2000000) {
                QueryBuilder<DrawingXmls> queryBuilder = daoSession.getDrawingXmlsDao().queryBuilder();
                queryBuilder.where(DrawingXmlsDao.Properties.DrwDrawingsId.eq(drawingId));
                List<DrawingXmls> result = queryBuilder.limit(1).build().list();
                if (result.size() > 0) {
                    return result.get(0);
                } else {
                    return null;
                }
            } else {
                //
                Log.e(TAG, "$$$$$$$$$$$$$$$$$$$$ getDrawingAnnotation: read xml using divided query ");
                return getReadDrawingXmlsInCunck(drawingXmlLength, annotDeleteXmlLength, sqliteDB, drawingId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rawLengthDeleteXmlCursor != null) {
                rawLengthDeleteXmlCursor.close();
            }

            if (rawLengthAnnotxmlCursor != null) {
                rawLengthAnnotxmlCursor.close();
            }
        }
        return null;
    }

    private DrawingXmls getReadDrawingXmlsInCunck(int drawingXmlLength, int annotDeleteXmlLength, SQLiteDatabase sqliteDB, int drawingId) {
        DrawingXmls drawingXmls = new DrawingXmls();
        Cursor columnCursor = null;
        Cursor annotDeleteXmlColumnCursor = null;
        Cursor annotXmlColumnCursor = null;
        String[] queryArgs = new String[]{String.valueOf(drawingId)};
        int chunk_size = 1000000;
        try {
            columnCursor = sqliteDB.rawQuery("SELECT  " + DrawingXmlsDao.Properties.Id.columnName + " , "
                    + DrawingXmlsDao.Properties.IsSync.columnName + " , " + DrawingXmlsDao.Properties.UsersId.columnName + "  FROM   " + DrawingXmlsDao.TABLENAME
                    + " WHERE " + DrawingXmlsDao.Properties.DrwDrawingsId.columnName + " =?", queryArgs);
            if (columnCursor != null && columnCursor.moveToFirst()) {
                drawingXmls.setDrwDrawingsId(drawingId);
                drawingXmls.setId(columnCursor.getLong(columnCursor.getColumnIndex(DrawingXmlsDao.Properties.Id.columnName)));
                drawingXmls.setIsSync(columnCursor.getInt(columnCursor.getColumnIndex(DrawingXmlsDao.Properties.IsSync.columnName)) == 1);
                drawingXmls.setUsersId(columnCursor.getInt(columnCursor.getColumnIndex(DrawingXmlsDao.Properties.UsersId.columnName)));
            }

            if (drawingXmlLength < 2000000) {
                annotXmlColumnCursor = sqliteDB.rawQuery("SELECT  " + DrawingXmlsDao.Properties.Annotxml.columnName + "  FROM   " + DrawingXmlsDao.TABLENAME
                        + " WHERE " + DrawingXmlsDao.Properties.DrwDrawingsId.columnName + " =?", queryArgs);
                if (annotXmlColumnCursor != null && annotXmlColumnCursor.moveToFirst()) {
                    drawingXmls.setAnnotdeletexml(annotXmlColumnCursor.getString(annotDeleteXmlColumnCursor.getColumnIndex(DrawingXmlsDao.Properties.Annotxml.columnName)));
                }
            } else {

                int numSteps = drawingXmlLength / chunk_size + 1;
                Log.d(TAG, " getReadDrawingXmlsInCunck: Annotxml size is too big *************************** ************  numSteps  = " + numSteps);
                int stingIndex = 0;
                StringBuilder sb = new StringBuilder();
                StringBuilder wholeAnnotXmlSB = new StringBuilder();
                for (stingIndex = 1; stingIndex < drawingXmlLength + 1; stingIndex += chunk_size) {
                    if (sb.length() > 1)
                        sb.append(" UNION ALL ");
                    sb.append("SELECT substr( " + DrawingXmlsDao.Properties.Annotxml.columnName + " , ")
                            .append(String.valueOf(stingIndex)).append(",").append(String.valueOf(chunk_size))
                            .append(") FROM ").append(DrawingXmlsDao.TABLENAME)
                            .append(" WHERE " + DrawingXmlsDao.Properties.DrwDrawingsId.columnName + " =").append(String.valueOf(drawingId));

                }
                sb.append(";");
                Log.d(TAG, "getReadDrawingXmlsInCunck: query ======== " + sb.toString());
                annotXmlColumnCursor = sqliteDB.rawQuery(sb.toString(), null);
                wholeAnnotXmlSB = new StringBuilder();
                while (annotXmlColumnCursor.moveToNext()) {
                    wholeAnnotXmlSB.append(annotXmlColumnCursor.getString(0));
                    Log.d("AnnotXML", "!!!$$$$@@*** Obtained String who's length is " + annotXmlColumnCursor.getString(0).length() + "\n\tTotal Extracted = " + wholeAnnotXmlSB.length());
                }
                drawingXmls.setAnnotxml(wholeAnnotXmlSB.toString());


            }
            if (annotDeleteXmlLength < 2000000) {
                annotDeleteXmlColumnCursor = sqliteDB.rawQuery("SELECT  " + DrawingXmlsDao.Properties.Annotdeletexml.columnName + "  FROM   " + DrawingXmlsDao.TABLENAME
                        + " WHERE " + DrawingXmlsDao.Properties.DrwDrawingsId.columnName + " =?", queryArgs);
                if (annotDeleteXmlColumnCursor != null && annotDeleteXmlColumnCursor.moveToFirst()) {
                    drawingXmls.setAnnotdeletexml(annotDeleteXmlColumnCursor.getString(annotDeleteXmlColumnCursor.getColumnIndex(DrawingXmlsDao.Properties.Annotdeletexml.columnName)));
                }
            } else {
                int numSteps = annotDeleteXmlLength / chunk_size + 1;
                Log.d(TAG, " getRead Delete XmlsInCunck: delete Xml size is too big *************************** ************  numSteps  = " + numSteps);
                int stingIndex = 0;
                StringBuilder querySB = new StringBuilder();
                StringBuilder wholeDeleteAnnotXmlSB = new StringBuilder();
                for (stingIndex = 1; stingIndex < drawingXmlLength + 1; stingIndex += chunk_size) {
                    if (querySB.length() > 1)
                        querySB.append(" UNION ALL ");
                    querySB.append("SELECT substr( " + DrawingXmlsDao.Properties.Annotdeletexml.columnName + " , ")
                            .append(String.valueOf(stingIndex)).append(",").append(String.valueOf(chunk_size))
                            .append(") FROM ").append(DrawingXmlsDao.TABLENAME)
                            .append(" WHERE " + DrawingXmlsDao.Properties.DrwDrawingsId.columnName + " =").append(String.valueOf(drawingId));

                }
                querySB.append(";");
                Log.d(TAG, "getReadDrawingXmlsInCunck: query ======== " + querySB.toString());
                annotDeleteXmlColumnCursor = sqliteDB.rawQuery(querySB.toString(), null);
                wholeDeleteAnnotXmlSB = new StringBuilder();
                while (annotDeleteXmlColumnCursor.moveToNext()) {
                    wholeDeleteAnnotXmlSB.append(annotDeleteXmlColumnCursor.getString(0));
                    Log.d("DELETEXML", "!!!$$$$@@*** Obtained String who's length is " + annotDeleteXmlColumnCursor.getString(0).length() + "\n\tTotal Extracted = " + wholeDeleteAnnotXmlSB.length());
                }
                drawingXmls.setAnnotdeletexml(wholeDeleteAnnotXmlSB.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (columnCursor != null)
                columnCursor.close();
            if (annotDeleteXmlColumnCursor != null)
                annotDeleteXmlColumnCursor.close();
            if (annotXmlColumnCursor != null)
                annotXmlColumnCursor.close();
        }
        return drawingXmls;
    }

    public void updateDrawing(DrawingXmls drawingXmls) {
        DrawingXmlsDao drawingXmlsDao = daoSession.getDrawingXmlsDao();
        drawingXmlsDao.update(drawingXmls);
    }


    /**
     * Check Annotation is Sync or not.
     *
     * @param drawingsId
     */
    public DrawingXmls isAnnotationSync(Integer drawingsId) {
        // get Drawing xml sync according to drawing id.
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        QueryBuilder<DrawingXmls> queryBuilder = daoSession.getDrawingXmlsDao().queryBuilder();
        queryBuilder.where(DrawingXmlsDao.Properties.DrwDrawingsId.eq(drawingsId));
        List<DrawingXmls> result = queryBuilder.limit(1).build().list();
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }

    /**
     * List of all not sync annotation xml of drawings
     *
     * @return
     */
    public List<DrawingXmls> getNotSyncAnnotations() {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        QueryBuilder<DrawingXmls> queryBuilder = daoSession.getDrawingXmlsDao().queryBuilder();
        queryBuilder.where(DrawingXmlsDao.Properties.IsSync.eq(false));
        List<DrawingXmls> result = queryBuilder.build().list();
        return result;
    }

    /**
     * List of all not sync annotation xml of drawings
     *
     * @return
     */
    public List<DrawingXmls> getNotSyncDrawingAnnotation() {
        QueryBuilder<DrawingXmls> queryBuilder = daoSession.getDrawingXmlsDao().queryBuilder();
        queryBuilder.where(DrawingXmlsDao.Properties.IsSync.eq(false));
        List<DrawingXmls> result = queryBuilder.build().list();
        return result;
    }

    public void accessTokenFailure() {
        context.startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        SharedPref.getInstance(context).writePrefs(SharedPref.SESSION_DETAILS, null);
        SharedPref.getInstance(context).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
     /*   if ((Activity)context instanceof Activity) {
            ((Activity) getApplicationContext()).finish();
        }*/
    }
}
