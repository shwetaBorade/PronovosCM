package com.pronovoscm.data;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.pronovoscm.LogData;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.api.ProjectDrawingListApi;
import com.pronovoscm.model.DrawingAction;
import com.pronovoscm.model.PDFSynEnum;
import com.pronovoscm.model.request.drawinglist.DrawingListRequest;
import com.pronovoscm.model.response.AbstractCallback;
import com.pronovoscm.model.response.ErrorResponse;
import com.pronovoscm.model.response.drawinglist.DrawingListResponse;
import com.pronovoscm.model.response.drawinglist.Drwings;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.logresponse.LogResponse;
import com.pronovoscm.model.response.syncupdate.SyncUpdateResponse;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.DrawingFolders;
import com.pronovoscm.persistence.domain.DrawingFoldersDao;
import com.pronovoscm.persistence.domain.DrawingList;
import com.pronovoscm.persistence.domain.DrawingListDao;
import com.pronovoscm.persistence.repository.DrawingListRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.FileUtils;
import com.pronovoscm.utils.SharedPref;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class ProjectDrawingListProvider {


    private final String TAG = ProjectDrawingListProvider.class.getName();
    private final ProjectDrawingListApi mDrawingListApi;
    private PronovosApplication context;
    private NetworkStateProvider networkStateProvider;
    private DrawingListRepository mDrawingListRepository;
    private DaoSession daoSession;
    private LoginResponse loginResponse;

    public ProjectDrawingListProvider(NetworkStateProvider networkStateProvider, DrawingListRepository drawingListRepository, ProjectDrawingListApi drawingListApi, DaoSession daoSession) {
        this.context = PronovosApplication.getContext();
        context.setUrl(Constants.BASE_API_URL);
        this.mDrawingListApi = drawingListApi;
        this.networkStateProvider = networkStateProvider;
        this.mDrawingListRepository = drawingListRepository;
        this.daoSession = daoSession;
    }

    public void getDrawingList(final DrawingListRequest drawingListRequest, String lastupdate, int projectId, DrawingAction action, final ProviderResult<List<DrawingList>> listProviderResult) {

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            //  headers.put("lastupdate", getMAXDrawingUpdateDate(drawingListRequest.getFolder_id()));
            headers.put("lastupdate", lastupdate);
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            Call<DrawingListResponse> photoDetailResponseCall = mDrawingListApi.getProjectDrawingList(headers, drawingListRequest);

            photoDetailResponseCall.enqueue(new AbstractCallback<DrawingListResponse>() {
                @Override
                protected void handleFailure(Call<DrawingListResponse> call, Throwable throwable) {
                    listProviderResult.failure(throwable.getMessage());
                    listProviderResult.success(mDrawingListRepository.getDrawingList(drawingListRequest.getFolder_id()));
                    //write logs
                    FileUtils.createFolderAndFile(new LogData("\n\n URL : " + call.request().url(),
                            "\n\nRequest: "+ photoDetailResponseCall.request(),
                            "\n Message : " + throwable.getMessage()));
                }

                @Override
                protected void handleError(Call<DrawingListResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        listProviderResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        listProviderResult.failure(errorResponse.getMessage());
                    }
                    //write logs
                    FileUtils.createFolderAndFile(new LogData("\n\n URL : " + call.request().url(),
                            "\n\nRequest: "+ photoDetailResponseCall.request(),
                            "\n Message : " + errorResponse.getMessage()));
                }

                @Override
                protected void handleSuccess(Response<DrawingListResponse> response) {
                    if (response.body() != null) {
                        DrawingListResponse drawingFolderResponse = null;
                        try {
                            drawingFolderResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (drawingFolderResponse != null && drawingFolderResponse.getStatus() == 200 && (drawingFolderResponse.getData().getResponseCode() == 101
                                || drawingFolderResponse.getData().getResponseCode() == 102)) {
                            updateLastUpdateFolder(projectId, drawingFolderResponse.getData().getDrw_folders_id());
                            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
                            SyncDrawingList syncDrawingList = new SyncDrawingList(mDrawingListRepository, daoSession, loginResponse, projectId, action,
                                    response.body().getData().getDrw_folders_id(), new ProviderResult<List<DrawingList>>() {

                                @Override
                                public void success(List<DrawingList> result) {
                                    listProviderResult.success(result);
                                }

                                @Override
                                public void AccessTokenFailure(String message) {
                                    listProviderResult.failure(message);
                                }

                                @Override
                                public void failure(String message) {
                                    listProviderResult.failure(message);
                                }
                            });

                            syncDrawingList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, response.body().getData().getDrwings());

                        } else if (drawingFolderResponse != null) {
                            listProviderResult.failure(drawingFolderResponse.getMessage());
                        } else {
                            listProviderResult.failure("response null");
                        }

                        //write logs
                        FileUtils.createFolderAndFile(new LogData("\n\n URL : " + response.raw().request().url(),
                                "\n\nRequest: "+photoDetailResponseCall.request(),
                                "\n\nResponse: \nDrawingFolderData : " + new Gson().toJson(response.body().getData().getDrwings()) + "\n Message : "
                                        + response.body().getMessage()));

                    } else {
                        listProviderResult.failure("response null");
                    }
                }
            });

        } else {
            listProviderResult.success(mDrawingListRepository.getDrawingList(drawingListRequest.getFolder_id()));
        }
    }


    public void getDrawingSyncUpdate(int projectId, int folderId, Date lastUpdatedDate, final ProviderResult<SyncUpdateResponse> drawingFoldersResult) {

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            headers.put("lastupdate", DateFormatter.formatDateTimeForService(lastUpdatedDate));
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            Call<SyncUpdateResponse> drawingSyncUpdate = mDrawingListApi.getDrawingSyncUpdate(headers, folderId);

            drawingSyncUpdate.enqueue(new AbstractCallback<SyncUpdateResponse>() {
                @Override
                protected void handleFailure(Call<SyncUpdateResponse> call, Throwable throwable) {
                    drawingFoldersResult.failure(throwable.getMessage());
                    //write logs
                    FileUtils.createFolderAndFile(new LogData("\n\n URL : " + call.request().url(),
                            "\n\nRequest: "+ drawingSyncUpdate.request(),
                            "\n Message : " + throwable.getMessage()));
                }

                @Override
                protected void handleError(Call<SyncUpdateResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        drawingFoldersResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        drawingFoldersResult.failure(errorResponse.getMessage());
                    }
                    //write logs
                    FileUtils.createFolderAndFile(new LogData("\n \n URL : " + call.request().url(),
                            "\n\nRequest: "+drawingSyncUpdate.request(),
                            "\n Message : " + errorResponse.getMessage()));
                }

                @Override
                protected void handleSuccess(Response<SyncUpdateResponse> response) {
                    if (response.body() != null) {
                        SyncUpdateResponse drawingFolderResponse = null;
                        try {
                            drawingFolderResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (drawingFolderResponse != null && drawingFolderResponse.getStatus() == 200 &&
                                (drawingFolderResponse.getData().getResponseCode() == 101 || drawingFolderResponse.getData().getResponseCode() == 102)) {

                            updateLastUpdateFolder(projectId, folderId);

                            //                            List<DrawingList> drawingFolders = doUpdateDrawingListTable(response.body().getAnnotationData().getDrwings(), response.body().getAnnotationData().getDrw_folders_id());
//                            updateLastUpdateFolder(projectId, drawingFolderResponse.getAnnotationData().getDrw_folders_id());

                            drawingFoldersResult.success(drawingFolderResponse);
                        } else if (drawingFolderResponse != null) {
                            drawingFoldersResult.failure(drawingFolderResponse.getMessage());
                        } else {
                            drawingFoldersResult.failure("response null");
                        }
                        //write logs
                        FileUtils.createFolderAndFile(new LogData("\n\n URL : " + response.raw().request().url(),
                                "\n\nRequest: "+drawingSyncUpdate.request(),
                                "\n\nResponse: \nSyncUpdate : " + new Gson().toJson(response.body().getData().getDrawings()) + "\n Message : "
                                        + response.body().getMessage()));
                    } else {
                        drawingFoldersResult.failure("response null");
                    }
                }
            });

        } else {
            drawingFoldersResult.failure("Offline mode");
        }
    }


    private void updateLastUpdateFolder(int pj_projects_id, int folderId) {
        DrawingFoldersDao mDrawingFoldersDao = daoSession.getDrawingFoldersDao();
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<DrawingFolders> drawingFolders = daoSession.getDrawingFoldersDao().queryBuilder().where(DrawingFoldersDao.Properties.PjProjectsId.eq(pj_projects_id), DrawingFoldersDao.Properties.DrwFoldersId.eq(folderId), DrawingFoldersDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).limit(1).list();

            for (int i = 0; i < drawingFolders.size(); i++) {
                drawingFolders.get(i).setLastupdatedate(new Date());
                mDrawingFoldersDao.update(drawingFolders.get(i));
            }
        }
    }

    /**
     * Get max updated at of drawing folders according to project id.
     *
     * @param drwFolderId
     * @return (Default value 1970 - 01 - 01 01 : 01 : 01)
     */

    public String getMAXDrawingUpdateDate(int drwFolderId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        List<DrawingList> maxPostIdRow = daoSession.getDrawingListDao().queryBuilder().where(DrawingListDao.Properties.UpdatedAt.isNotNull(), DrawingListDao.Properties.DrwFoldersId.eq(drwFolderId), DrawingListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).orderDesc(DrawingListDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1970-01-01 01:01:01";
    }

//    /**
//     * Insert or Update Drawing List
//     *
//     * @param drwFoldersId
//     * @return
//     */
//    // TODO: 29/10/18 moved to DrawingListRepository
//    public List<DrawingList> doUpdateDrawingListTable(List<Drwings> drwings, int projectId, int drwFoldersId) {
//        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
//        try {
//            daoSession.callInTx(new Callable<List<Drwings>>() {
//                DrawingListDao mDrawingListDao = daoSession.getDrawingListDao();
//
//                @Override
//                public List<Drwings> call() throws Exception {
////                    return null;
//                    for (Drwings drw : drwings) {
//
//                        if (drw.getDeletedAt() != null && !TextUtils.isEmpty(drw.getDeletedAt())) {
//                            DeleteQuery<DrawingList> tableDeleteQuery = daoSession.queryBuilder(DrawingList.class).where(
//                                    DrawingListDao.Properties.DrwFoldersId.eq(drwFoldersId),
//                                    DrawingListDao.Properties.DrawingName.like(drw.getDrawing_name()),
//                                    DrawingListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
//                                    .buildDelete();
//                            tableDeleteQuery.executeDeleteWithoutDetachingEntities();
//                        } else {
//                            DrawingList drawingList1 = new DrawingList();
//                            drawingList1.setCreatedAt(drw.getCreated_at() != null && !drw.getCreated_at().equals("") ?
//                                    DateFormatter.getDateFromDateTimeString(drw.getCreated_at()) : null);
//                            drawingList1.setDescriptions(drw.getDescription());
//                            drawingList1.setDrawingDate(drw.getDrawing_date() != null && !drw.getDrawing_date().equals("") ?
//                                    DateFormatter.getDateFromDrawingDateString(drw.getDrawing_date()) : null);
//                            drawingList1.setDrawingName(drw.getDrawing_name());
//                            drawingList1.setDrawingStatus(drw.getDrawing_status());
//                            drawingList1.setDrawingDiscipline(drw.getDrw_discipline());
//                            drawingList1.setDrawingDisciplineId(String.valueOf(drw.getDrw_discipline_id()));
//                            drawingList1.setDrawingsId(drw.getDrw_drawings_id());
//                            drawingList1.setDrwFoldersId(drwFoldersId);
//                            drawingList1.setImageOrg(drw.getImage_org());
//                            drawingList1.setImageThumb(drw.getImage_thumb());
//                            drawingList1.setPdfOrg(drw.getPdf_org());
//                            drawingList1.setRevisitedNum(drw.getRevisited_num());
//                            drawingList1.setStatus(drw.getStatus());
//                            drawingList1.setUpdatedAt(drw.getUpdated_at() != null && !drw.getUpdated_at().equals("") ?
//                                    DateFormatter.getDateFromDateTimeString(drw.getUpdated_at()) : null);
//                            drawingList1.setUsersId(loginResponse.getUserDetails().getUsers_id());
//                            drawingList1.setIsSync(true);
//                            String fileName = "";
//                            if (drw.getPdf_org() != null && !TextUtils.isEmpty(drw.getPdf_org())) {
//                                URL url = new URL(drw.getPdf_org());
//                                String[] segments = url.getPath().split("/");
//                                fileName = segments[segments.length - 1];
//                            } else {
//                                URL url = new URL(drw.getImage_org());
//                                String[] segments = url.getPath().split("/");
//                                fileName = segments[segments.length - 1];
//                            }
//                            if (isPDFFileExist(loginResponse.getUserDetails().getUsers_id() + fileName)) {
//                                drawingList1.setPdfStatus(PDFSynEnum.SYNC.ordinal());
//                            } else {
//                                drawingList1.setPdfStatus(PDFSynEnum.NOTSYNC.ordinal());
//
//                                if (getDrawingFolderDetail(projectId, drwFoldersId).getSyncFolder() &&
//                                        getDrawingDetail(drwFoldersId, drw.getDrawing_name()) != null) {
//                                    drawingList1.setPdfStatus(PDFSynEnum.PROCESSING.ordinal());
//                                    mDrawingListDao.save(drawingList1);
//                                    EventBus.getDefault().post(drawingList1);
//                                    continue;
//                                }
//
//                            }
//                            mDrawingListDao.save(drawingList1);
//                        }
//                    }
//                    return drwings;
//                }
//
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return getDrawingList(drwFoldersId);
//    }

//    /**
//     * Check that File is exist in storage or not.
//     *
//     * @param fileName
//     * @return
//     */
//    // TODO: 29/10/18 moved to DrawingListRepository
//    private boolean isPDFFileExist(String fileName) {
//        String root = context.getFilesDir().getAbsolutePath();
//        File myDir = new File(root + "/Pronovos/PDF");
//        if (!myDir.exists()) {
//            myDir.mkdirs();
//        }
//        String fname = fileName;
//        File file = new File(myDir, fname);
//        if (file.exists()) {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * Get Drawing folders according to project.
//     *
//     * @param drwFolderId
//     * @return list of Photo Folder.
//     */
//    // TODO: 29/10/18 moved to DrawingListRepository
//    public List<DrawingList> getDrawingList(int drwFolderId) {
//        // get drawings according to folder id.
//        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
//
//
//        List<DrawingList> list1 = daoSession.getDrawingListDao().queryBuilder().
//                where(new WhereCondition.StringCondition("drw_folders_id = " + drwFolderId + " AND users_id = " +
//                        loginResponse.getUserDetails().getUsers_id() +
//                        " GROUP BY UPPER(drawing_name)"))
//                .orderAsc(DrawingListDao.Properties.DrawingDiscipline
//                        , DrawingListDao.Properties.DrawingName).orderDesc(DrawingListDao.Properties.RevisitedNum).list();
//        return list1;
//    }


    public Date getLastUpdatedDateOfFolder(int drwFolderId) {
        // get drawings according to folder id.
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<DrawingList> list1 = daoSession.getDrawingListDao().queryBuilder().
                where(new WhereCondition.StringCondition("drw_folders_id = " + drwFolderId + " AND users_id = " +
                        loginResponse.getUserDetails().getUsers_id() /*+
                        " GROUP BY UPPER(drawing_name)"*/))
                .orderDesc(DrawingListDao.Properties.UpdatedAt).limit(1).list();
//        return DateFormatter.formatDateTimeForService(list1.get(0).getUpdatedAt());
        if (list1.size() > 0) {
            return list1.get(0).getUpdatedAt();
        } else {
            return DateFormatter.getDateFromDateHHTimeString("1970-01-01 01:01:01");
        }
    }


    /**
     * Get Drawing folder details according to project id and folder id.
     *
     * @param pjProjectId
     * @param drwFolderId
     * @return
     */
    public DrawingFolders getDrawingFolderDetail(int pjProjectId, int drwFolderId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        QueryBuilder<DrawingFolders> queryBuilder = daoSession.getDrawingFoldersDao().queryBuilder();
        queryBuilder.where(DrawingFoldersDao.Properties.PjProjectsId.eq(pjProjectId), DrawingFoldersDao.Properties.DrwFoldersId.eq(drwFolderId), DrawingFoldersDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()));
        List<DrawingFolders> result = queryBuilder.build().list();
        return result.get(0);
    }

    /**
     * Update Drawing folder details .
     *
     * @return
     */
    public void updateDrawingFolder(DrawingFolders drawingFolder) {
        DrawingFoldersDao dao = daoSession.getDrawingFoldersDao();
        dao.update(drawingFolder);
        Log.d(TAG, "updateDrawingFolder: drawingFolder " + drawingFolder);
    }

    private List<DrawingList> sortDrawingList(List<DrawingList> drawingLists) {

        Collections.sort(drawingLists, new Comparator<DrawingList>() {
            int compareRight(String a, String b) {
                int bias = 0;
                int ia = 0;
                int ib = 0;
                for (; ; ia++, ib++) {
                    char ca = charAt(a, ia);
                    char cb = charAt(b, ib);

                    if (!Character.isDigit(ca) && !Character.isDigit(cb)) {
                        return bias;
                    } else if (!Character.isDigit(ca)) {
                        return -1;
                    } else if (!Character.isDigit(cb)) {
                        return +1;
                    } else if (ca < cb) {
                        if (bias == 0) {
                            bias = -1;
                        }
                    } else if (ca > cb) {
                        if (bias == 0)
                            bias = +1;
                    } else if (ca == 0 && cb == 0) {
                        return bias;
                    }
                }
            }

            char charAt(String s, int i) {
                if (i >= s.length()) {
                    return 0;
                } else {
                    return s.charAt(i);
                }
            }

            @Override
            public int compare(DrawingList o1, DrawingList o2) {
                String a = o1.getDrawingName();
                String b = o2.getDrawingName();

                int ia = 0, ib = 0;
                int nza = 0, nzb = 0;
                char ca, cb;
                int result;

                while (true) {
                    // only count the number of zeroes leading the last number compared
                    nza = nzb = 0;

                    ca = charAt(a, ia);
                    cb = charAt(b, ib);

                    // skip over leading spaces or zeros
                    while (Character.isSpaceChar(ca) || ca == '0') {
                        if (ca == '0') {
                            nza++;
                        } else {
                            // only count consecutive zeroes
                            nza = 0;
                        }

                        ca = charAt(a, ++ia);
                    }

                    while (Character.isSpaceChar(cb) || cb == '0') {
                        if (cb == '0') {
                            nzb++;
                        } else {
                            // only count consecutive zeroes
                            nzb = 0;
                        }

                        cb = charAt(b, ++ib);
                    }

                    // process run of digits
                    if (Character.isDigit(ca) && Character.isDigit(cb)) {
                        if ((result = compareRight(a.substring(ia), b.substring(ib))) != 0) {
                            return result;
                        }
                    }

                    if (ca == 0 && cb == 0) {
                        // The strings compare the same. Perhaps the caller
                        // will want to call strcmp to break the tie.
                        return nza - nzb;
                    }

                    if (ca < cb) {
                        return -1;
                    } else if (ca > cb) {
                        return +1;
                    }

                    ++ia;
                    ++ib;
                }
            }
        });
        return drawingLists;
    }

    /**
     * search drawings according to folder id and search name
     *
     * @param drwFolderId
     * @param name
     * @return
     */
    public List<DrawingList> getSearchDrawing(int drwFolderId, String name, DrawingList drawingList) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse == null) {
            return new ArrayList<>();
        }
        if (drawingList.getDrawingsId() == -1) {
            List<DrawingList> list1 = daoSession.getDrawingListDao().queryBuilder().
                    where(DrawingListDao.Properties.DrwFoldersId.eq(drwFolderId), DrawingListDao.Properties.UsersId.eq(
                                    loginResponse.getUserDetails().getUsers_id())
                            , DrawingListDao.Properties.CurrentRevision.eq(1)
                    ).
                    whereOr(DrawingListDao.Properties.DrawingName.like("%" + name + "%"),
                            DrawingListDao.Properties.Descriptions.like("%" + name + "%"))
                    .orderAsc(
                            DrawingListDao.Properties.DrawingDiscipline
                            , DrawingListDao.Properties.DrawingName)
                    .list();

            if (list1 != null && list1.size() > 0) {
                return sortDrawingList(list1);
            }
            return list1;
        } else {
            List<DrawingList> list1 = daoSession.getDrawingListDao().queryBuilder().
                    where(DrawingListDao.Properties.DrwFoldersId.eq(drwFolderId), DrawingListDao.Properties.UsersId.eq(
                                    loginResponse.getUserDetails().getUsers_id()),
                            DrawingListDao.Properties.DrawingDiscipline.eq(drawingList.getDrawingDiscipline())
                            , DrawingListDao.Properties.CurrentRevision.eq(1)
                    ).
                    whereOr(DrawingListDao.Properties.DrawingName.like("%" + name + "%"),
                            DrawingListDao.Properties.Descriptions.like("%" + name + "%"))
                    .orderAsc(
                            DrawingListDao.Properties.DrawingDiscipline
                            , DrawingListDao.Properties.DrawingName).list();
            if (list1 != null && list1.size() > 0) {
                return sortDrawingList(list1);
            }
            return list1;

        }
    }

    /**
     * search drawings according to folder id and search name
     *
     * @param drwFolderId
     * @return
     */
    public List<DrawingList> getNonSyncDrawings(int drwFolderId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
//        List<DrawingList> list = daoSession.getDrawingListDao().queryBuilder().where(new WhereCondition.StringCondition("drw_folders_id = " + drwFolderId + " AND users_id = " + loginResponse.getUserDetails().getUsers_id() + " AND drawing_name LIKE '%" + name + "%' GROUP BY drawing_name")).orderAsc(DrawingListDao.Properties.DrawingDiscipline, DrawingListDao.Properties.DrawingName).orderDesc(DrawingListDao.Properties.RevisitedNum).list();
        if (loginResponse == null) {
            return new ArrayList<>();
        }
        List<DrawingList> list1 = daoSession.getDrawingListDao().queryBuilder().
                where(DrawingListDao.Properties.DrwFoldersId.eq(drwFolderId), DrawingListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()), DrawingListDao.Properties.PdfStatus.notEq(PDFSynEnum.SYNC.ordinal()))
                .orderAsc(DrawingListDao.Properties.DrawingDiscipline
                        , DrawingListDao.Properties.DrawingName).list();

        return list1;
    }

    /**
     * search drawings according to folder id and search name
     *
     * @param drwFolderId
     * @return
     */
    public List<DrawingList> getProcessingDrawings(int drwFolderId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
//        List<DrawingList> list = daoSession.getDrawingListDao().queryBuilder().where(new WhereCondition.StringCondition("drw_folders_id = " + drwFolderId + " AND users_id = " + loginResponse.getUserDetails().getUsers_id() + " AND drawing_name LIKE '%" + name + "%' GROUP BY drawing_name")).orderAsc(DrawingListDao.Properties.DrawingDiscipline, DrawingListDao.Properties.DrawingName).orderDesc(DrawingListDao.Properties.RevisitedNum).list();
        if (loginResponse == null) {
            return new ArrayList<>();
        }
        List<DrawingList> list1 = daoSession.getDrawingListDao().queryBuilder().
                where(DrawingListDao.Properties.DrwFoldersId.eq(drwFolderId), DrawingListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                        DrawingListDao.Properties.PdfStatus.eq(PDFSynEnum.PROCESSING.ordinal())).list();

        return list1;
    }

    /**
     * search drawings according to folder id and search name
     *
     * @param drwFolderId
     * @param name
     * @return
     */
    public List<DrawingList> getSearchDrawing(int drwFolderId, String name) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
//        List<DrawingList> list = daoSession.getDrawingListDao().queryBuilder().where(new WhereCondition.StringCondition("drw_folders_id = " + drwFolderId + " AND users_id = " + loginResponse.getUserDetails().getUsers_id() + " AND drawing_name LIKE '%" + name + "%' GROUP BY drawing_name")).orderAsc(DrawingListDao.Properties.DrawingDiscipline, DrawingListDao.Properties.DrawingName).orderDesc(DrawingListDao.Properties.RevisitedNum).list();
        if (loginResponse == null) {
            return new ArrayList<>();
        }
        List<DrawingList> list1 = daoSession.getDrawingListDao().queryBuilder().
                where(new WhereCondition.StringCondition("drw_folders_id = " + drwFolderId + " AND users_id = " +
                        loginResponse.getUserDetails().getUsers_id() +
                        " AND drawing_name LIKE '%" + name + "%'"))
                .orderAsc(DrawingListDao.Properties.DrawingDiscipline
                        , DrawingListDao.Properties.DrawingName).list();

        return list1;

    }


    /**
     * Get drawing details
     *
     * @param folderId    id of the folder that contains the drawing
     * @param drawingName name of the drawing
     * @param revNo       revision number of the drawing
     * @return an object of {@DrawingList} which contains the details of the drawing
     */
    public DrawingList getDrawingDetail(int folderId, String drawingName, int revNo, long drawingId) {
        // get projects according to region id.
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        QueryBuilder<DrawingList> queryBuilder = daoSession.getDrawingListDao().queryBuilder();
        queryBuilder.where(DrawingListDao.Properties.DrawingName.like(drawingName), DrawingListDao.Properties.RevisitedNum.eq(revNo), DrawingListDao.Properties.DrwFoldersId.eq(folderId), DrawingListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()), DrawingListDao.Properties.Id.eq(drawingId));
        List<DrawingList> result = queryBuilder.limit(1).build().list();
        return result.get(0);
    }

    public DrawingList getDrawingDetail(Integer drwDrawingsId, Integer usersId) {
        QueryBuilder<DrawingList> queryBuilder = daoSession.getDrawingListDao().queryBuilder();
        queryBuilder.where(DrawingListDao.Properties.UsersId.eq(usersId), DrawingListDao.Properties.DrawingsId.eq(drwDrawingsId));
        List<DrawingList> result = queryBuilder.limit(1).build().list();
        return result.get(0);
    }


    private static class SyncDrawingList extends AsyncTask<List<Drwings>, Void, List<DrawingList>> {
        private final LoginResponse loginResponse;
        private final DaoSession daoSession;
        private int projectId, folderId;
        private DrawingAction action;
        private ProviderResult<List<DrawingList>> callback;
        private DrawingListRepository drawingListRepository;

        SyncDrawingList(DrawingListRepository drawingListRepository, DaoSession daoSession, LoginResponse loginResponse, int projectId, DrawingAction action, int folderId, ProviderResult<List<DrawingList>> callback) {
            this.drawingListRepository = drawingListRepository;
            this.loginResponse = loginResponse;
            this.daoSession = daoSession;
            this.projectId = projectId;
            this.action = action;
            this.folderId = folderId;
            this.callback = callback;
        }


        @SafeVarargs
        @Override
        protected final List<DrawingList> doInBackground(List<Drwings>... lists) {
            try {
                daoSession.callInTx(new Callable<List<Drwings>>() {

                    @Override
                    public List<Drwings> call() throws Exception {
                        Log.d("PDrawingListProvider", "call: ");
                        for (Drwings drw : lists[0]) {
                            Log.d("PDrawingListProvider", "call: " + drw);
                            // To Delete record from database.
                            if (drw.getDeletedAt() != null && !TextUtils.isEmpty(drw.getDeletedAt())) {
                                DrawingList originalDrawing = drawingListRepository.getDrawing(folderId, drw.getOriginal_drw_id()/*, drw.getRevisited_num()*/);
                                if (originalDrawing != null /*&& originalDrawing.getPdfStatus() == PDFSynEnum.NOTSYNC.ordinal()*/) {
                                    DeleteQuery<DrawingList> tableDeleteQuery = daoSession.queryBuilder(DrawingList.class).where(
                                                    DrawingListDao.Properties.DrwFoldersId.eq(folderId),
                                                    DrawingListDao.Properties.DrawingName.like(drw.getDrawing_name()),
                                                    DrawingListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                                            .buildDelete();
                                    tableDeleteQuery.executeDeleteWithoutDetachingEntities();
                                }
                            } else {
                                DrawingList currentDrawing = drawingListRepository.getCurrentDrawingRevision(folderId, drw.getOriginal_drw_id()/*, drw.getRevisited_num()*/);
                                DrawingList previousSyncedDrawing = drawingListRepository.getSyncDrawingDetail(folderId, drw.getOriginal_drw_id()/*, drw.getRevisited_num()*/);
                                if (action.compareTo(DrawingAction.NEW_DRAWING) == 0
                                        && previousSyncedDrawing != null) {
                                    DrawingList originalDrawing = drawingListRepository.getDrawing(folderId, drw.getOriginal_drw_id(), drw.getRevisited_num());


                                    DrawingList drawingList1 = new DrawingList();
                                    if (drw.getCurrentRevision() != 1 && originalDrawing != null && currentDrawing.getRevisitedNum() != drw.getRevisited_num()) {
                                        drawingList1 = originalDrawing;
                                        drawingList1.setDrawingDiscipline(drw.getDrw_discipline());
                                        drawingList1.setDrawingDisciplineId(String.valueOf(drw.getDrw_discipline_id()));
                                        drawingList1.setDrawingDate(drw.getDrawing_date() != null && !drw.getDrawing_date().equals("") ?
                                                DateFormatter.getDateFromDrawingDateString(drw.getDrawing_date()) : null);
                                        drawingList1.setCurrentRevision(drw.getCurrentRevision());
                                        drawingListRepository.updateDrawingList(drawingList1);
                                    }
                                    continue;

                                } else {
                                    DrawingList originalDrawing = drawingListRepository.getDrawing(folderId, drw.getOriginal_drw_id(), drw.getRevisited_num());
                                    DrawingList syncedOriginalDrawing = drawingListRepository.getAnySyncDrawing(folderId, drw.getOriginal_drw_id());

                                    DrawingList drawingList1 = new DrawingList();
                                    if (originalDrawing != null) {
                                        drawingList1 = originalDrawing;
                                    } else {
                                        drawingList1.setOriginalDrwId(drw.getOriginal_drw_id());
                                        drawingList1.setPdfStatus(PDFSynEnum.NOTSYNC.ordinal());

                                    }
                                    drawingList1.setCreatedAt(drw.getCreated_at() != null && !drw.getCreated_at().equals("") ?
                                            DateFormatter.getDateFromDateTimeString(drw.getCreated_at()) : null);
                                    drawingList1.setDescriptions(drw.getDescription());
                                    drawingList1.setDrawingDate(drw.getDrawing_date() != null && !drw.getDrawing_date().equals("") ?
                                            DateFormatter.getDateFromDrawingDateString(drw.getDrawing_date()) : null);
                                    drawingList1.setDrawingName(drw.getDrawing_name());
                                    drawingList1.setDrawingStatus(drw.getDrawing_status());
                                    drawingList1.setDrawingDiscipline(drw.getDrw_discipline());
                                    drawingList1.setDrawingDisciplineId(String.valueOf(drw.getDrw_discipline_id()));
                                    drawingList1.setDrawingsId(drw.getDrw_drawings_id());
                                    drawingList1.setDrwFoldersId(folderId);
                                    drawingList1.setImageOrg(drw.getImage_org());
                                    drawingList1.setImageThumb(drw.getImage_thumb());
                                    drawingList1.setPdfOrg(drw.getPdf_org());
                                    drawingList1.setRevisitedNum(drw.getRevisited_num());
                                    drawingList1.setStatus(drw.getStatus());
                                    drawingList1.setUpdatedAt(drw.getUpdated_at() != null && !drw.getUpdated_at().equals("") ?
                                            DateFormatter.getDateFromDateTimeString(drw.getUpdated_at()) : null);
                                    drawingList1.setUsersId(loginResponse.getUserDetails().getUsers_id());
                                    drawingList1.setIsSync(true);
                                    String fileName = "";
                                    if (drw.getPdf_org() != null && !TextUtils.isEmpty(drw.getPdf_org())) {
                                        URL url = new URL(drw.getPdf_org());
                                        String[] segments = url.getPath().split("/");
                                        fileName = segments[segments.length - 1];
                                    } else {
                                        URL url = new URL(drw.getImage_org());
                                        String[] segments = url.getPath().split("/");
                                        fileName = segments[segments.length - 1];
                                    }
                                    DrawingFolders drawingFolders = (drawingListRepository.getDrawingFolderDetail(projectId, folderId));
                                    if (drawingFolders != null) {
                                        if (originalDrawing == null) {
                                            drawingList1.setCurrentRevision(drw.getCurrentRevision());

                                            drawingListRepository.saveDrawingList(drawingList1);
                                            if (drawingFolders.getSyncDrawingFolder()) {
                                                drawingList1.setPdfStatus(PDFSynEnum.PROCESSING.ordinal());
                                                drawingListRepository.updateDrawingList(drawingList1);
                                                EventBus.getDefault().post(drawingList1);
                                            }
                                            if (currentDrawing != null && drw.getCurrentRevision() == 1) {
                                                currentDrawing.setCurrentRevision(0);
                                                drawingListRepository.updateDrawingList(currentDrawing);
                                            }
                                        } else if (syncedOriginalDrawing == null) {//TODO : manage check of anydrawing is synced or not

                                            drawingList1.setCurrentRevision(drw.getCurrentRevision());
                                            drawingListRepository.updateDrawingList(drawingList1);
                                        } else if (drw.getCurrentRevision() == 1) {

                                            if (drawingListRepository.isPDFFileExist(loginResponse.getUserDetails().getUsers_id() + fileName)) {
                                                if (/*(currentDrawing == null && drw.getCurrentRevision() == 1)  || */ (currentDrawing != null && drw.getRevisited_num() != currentDrawing.getRevisitedNum())) {
                                                    drawingList1.setPdfStatus(PDFSynEnum.SYNC.ordinal());
                                                    drawingList1.setCurrentRevision(drw.getCurrentRevision());
                                                    if (currentDrawing != null) {
                                                        currentDrawing.setCurrentRevision(0);
                                                        drawingListRepository.updateDrawingList(currentDrawing);
                                                    }
                                                    drawingListRepository.updateDrawingList(drawingList1);
                                                }
                                            } else {
                                                drawingList1.setPdfStatus(PDFSynEnum.NOTSYNC.ordinal());
                                                if ((drawingFolders.getSyncFolder() || action.compareTo(DrawingAction.MANUAL) == 0) &&
                                                        previousSyncedDrawing != null) {
                                                    updateDrawingDBForRevision(drawingList1, drw, currentDrawing, PDFSynEnum.PROCESSING.ordinal());

                                                } else if (previousSyncedDrawing == null) {
                                                    updateDrawingDBForRevision(drawingList1, drw, currentDrawing, PDFSynEnum.PROCESSING.ordinal());
                                                }

                                            }
                                        }
                                    } else {
                                        if (originalDrawing == null) {

                                            drawingListRepository.saveDrawingList(drawingList1);

                                        }
                                    }
                                }
                            }
                        }
                        return lists[0];
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return drawingListRepository.getDrawingList(folderId);
        }

        @Override
        protected void onPostExecute(List<DrawingList> drawingLists) {
            super.onPostExecute(drawingLists);
            callback.success(drawingLists);
        }

        synchronized private void updateDrawingDBForRevision(DrawingList drawingList1, Drwings drw, DrawingList syncedOriginalDrawing, int pdfStatus) {
            if (drw.getRevisited_num() != syncedOriginalDrawing.getRevisitedNum()) {
                drawingList1.setPdfStatus(pdfStatus);
                drawingList1.setCurrentRevision(drw.getCurrentRevision());
                syncedOriginalDrawing.setCurrentRevision(0);
                drawingListRepository.updateDrawingList(syncedOriginalDrawing);
                drawingListRepository.updateDrawingList(drawingList1);
                EventBus.getDefault().post(drawingList1);
            }
        }
    }

    /*
     *@method reportBugs
     *@param lastUpdateStr ,finalRequestBody,listProviderResult
     *@return void
     * */
    public void reportBugs(String lastUpdateStr, RequestBody finalRequestBody, final ProviderResult<String> listProviderResult) {

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).
                    readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            headers.put("lastupdate", lastUpdateStr);
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            Call<LogResponse> photoDetailResponseCall = mDrawingListApi.postDrawingLogs(headers, finalRequestBody);

            photoDetailResponseCall.enqueue(new AbstractCallback<LogResponse>() {
                @Override
                protected void handleFailure(Call<LogResponse> call, Throwable throwable) {
                    listProviderResult.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<LogResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        listProviderResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        listProviderResult.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<LogResponse> response) {
                    if (response.body() != null) {
                        LogResponse logResponse = null;
                        try {
                            logResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (logResponse != null && logResponse.getStatus() == 200 &&
                                (logResponse.getmDrawingLogs().getResponseCode() == 101 ||
                                        logResponse.getmDrawingLogs().getResponseCode() == 102)) {
                            listProviderResult.success(logResponse.getmDrawingLogs().getResponseMsg());
                        } else if (logResponse != null) {
                            listProviderResult.failure(logResponse.getMessage());
                        } else {
                            listProviderResult.failure("response null");
                        }
                    } else {
                        listProviderResult.failure("response null");
                    }
                }
            });

        } else {
            listProviderResult.success(context.getString(R.string.internet_connection_check_transfer_overview));
        }
    }
}
