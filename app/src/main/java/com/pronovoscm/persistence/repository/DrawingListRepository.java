package com.pronovoscm.persistence.repository;

import android.content.Context;

import com.google.gson.Gson;
import com.pronovoscm.chipslayoutmanager.util.log.Log;
import com.pronovoscm.model.PDFSynEnum;
import com.pronovoscm.model.response.drawingpunchlist.DrwPunchlists;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.DrawingFolders;
import com.pronovoscm.persistence.domain.DrawingFoldersDao;
import com.pronovoscm.persistence.domain.DrawingList;
import com.pronovoscm.persistence.domain.DrawingListDao;
import com.pronovoscm.persistence.domain.DrwPunchlist;
import com.pronovoscm.persistence.domain.DrwPunchlistDao;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.SharedPref;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Defines all methods of Drawing for querying offline event table
 *
 * @author GWL
 */

public class DrawingListRepository extends AbstractRepository {
    private Context context;
    private LoginResponse loginResponse;

    public DrawingListRepository(DaoSession daoSession, Context context) {
        super(daoSession);
        this.context = context;
    }


    /**
     * Save drawing item
     *
     * @param drawing
     */
    public void saveDrawingList(DrawingList drawing) {
        getDaoSession().getDrawingListDao().save(drawing);
    }

    /**
     * Save drawing item
     *
     * @param drawing
     */
    public void updateDrawingList(DrawingList drawing) {
        getDaoSession().getDrawingListDao().update(drawing);
    }


    /**
     * Check that File is exist in storage or not.
     *
     * @param fileName
     * @return
     */
    public boolean isPDFFileExist(String fileName) {
        String root = context.getFilesDir().getAbsolutePath();
        File myDir = new File(root + "/Pronovos/PDF");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String fname = fileName;
        File file = new File(myDir, fname);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public void  deleteExistingPdf(String fileName) {
        String root = context.getFilesDir().getAbsolutePath();
        File myDir = new File(root + "/Pronovos/PDF/"+fileName);
        if (myDir.exists()) {
            boolean isDeleted = myDir.delete();
            Log.e("Delete", "isDeleted: " + isDeleted);
        }
    }

    /**
     * Get Drawing folders according to project.
     *
     * @param drwFolderId
     * @return list of Photo Folder.
     */
    public List<DrawingList> getDrawingList(int drwFolderId) {
        // get drawings according to folder id.
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<DrawingList> list1 = getDaoSession().getDrawingListDao().queryBuilder().
                    where(new WhereCondition.StringCondition("drw_folders_id = " + drwFolderId + " AND users_id = " +
                            loginResponse.getUserDetails().getUsers_id()))
                    .orderAsc(DrawingListDao.Properties.DrawingDiscipline
                            , DrawingListDao.Properties.DrawingName)
                    .list();
            return list1;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Get Drawing folders according to project.
     *
     * @param drwFolderId
     * @return list of Photo Folder.
     */
    public List<DrawingList> getDrawingListGroupName(int drwFolderId) {
        // get drawings according to folder id.
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        List<DrawingList> list1 = getDaoSession().getDrawingListDao().queryBuilder().
                where(new WhereCondition.StringCondition("drw_folders_id = " + drwFolderId + " AND users_id = " +
                        loginResponse.getUserDetails().getUsers_id() +
                        " GROUP BY UPPER(drawing_name)"))
                .orderAsc(DrawingListDao.Properties.DrawingDiscipline
                        , DrawingListDao.Properties.DrawingName).orderDesc(DrawingListDao.Properties.RevisitedNum).list();
        return list1;
    }

    /**
     * Get Drawing folders according to project.
     *
     * @param drwFolderId
     * @return list of Photo Folder.
     */
    public List<DrawingList> getDrawingListdrwDiscipline(int drwFolderId) {
        // get drawings according to folder id.
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<DrawingList> list1 = new ArrayList<>();
            list1.add(new DrawingList(-1, "All"));

            list1.addAll(getDaoSession().getDrawingListDao().queryBuilder().
                    where(new WhereCondition.StringCondition("drw_folders_id = " + drwFolderId + " AND users_id = " +
                            loginResponse.getUserDetails().getUsers_id() + " AND current_revision = " + 1 +
                            " GROUP BY UPPER(drw_discipline)"))
                    .orderAsc(DrawingListDao.Properties.DrawingDiscipline
                            , DrawingListDao.Properties.DrawingName).orderDesc(DrawingListDao.Properties.RevisitedNum).list());
            return list1;
        } else return new ArrayList<>();
    }

    /**
     * Get drawing details
     *
     * @param folderId    id of the folder that contains the drawing
     * @param drawingName name of the drawing
     * @return an object of {@DrawingList} which contains the details of the drawing
     */
    public DrawingList getDrawingDetail(int folderId, String drawingName) {
        try {
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            List<DrawingList> list = getDaoSession().getDrawingListDao().queryBuilder().where(DrawingListDao.Properties.DrawingName.like(drawingName),
                    DrawingListDao.Properties.DrwFoldersId.eq(folderId),
                    DrawingListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                    .orderDesc(DrawingListDao.Properties.RevisitedNum)
                    .limit(1).build().list();
            return list.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get any sync drawing details
     *
     * @param folderId    id of the folder that contains the drawing
     * @param drawingName name of the drawing
     * @return an object of {@DrawingList} which contains the details of the drawing
     */
    public DrawingList getSyncDrawingDetail(int folderId, String drawingName) {
        try {
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            List<DrawingList> list = getDaoSession().getDrawingListDao().queryBuilder().where(DrawingListDao.Properties.DrawingName.like(drawingName),
                    DrawingListDao.Properties.DrwFoldersId.eq(folderId),
                    DrawingListDao.Properties.PdfStatus.eq(PDFSynEnum.SYNC.ordinal()),
                    DrawingListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                    .build().list();
            if (list.size() > 0) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get any sync drawing details
     *
     * @param folderId id of the folder that contains the drawing
     * @return an object of {@DrawingList} which contains the list of sync drawing
     */
    public ArrayList<DrawingList> getSyncDrawingList(int folderId) {
        try {
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            ArrayList<DrawingList> list = (ArrayList<DrawingList>) getDaoSession().getDrawingListDao().queryBuilder().where(
                    DrawingListDao.Properties.DrwFoldersId.eq(folderId),
                    DrawingListDao.Properties.PdfStatus.eq(PDFSynEnum.SYNC.ordinal()),
                    DrawingListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                    .orderAsc(DrawingListDao.Properties.DrawingDiscipline
                            , DrawingListDao.Properties.DrawingName)
                    .build().list();
            if (list.size() > 0) {
                return list;
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Get any sync drawing details
     *
     * @param folderId id of the folder that contains the drawing
     * @return an object of {@DrawingList} which contains the list of sync drawing
     */
    public ArrayList<DrawingList> getAllDrawingList(int folderId) {
        try {
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            ArrayList<DrawingList> list = (ArrayList<DrawingList>) getDaoSession().getDrawingListDao().queryBuilder().where(
                    DrawingListDao.Properties.DrwFoldersId.eq(folderId),
                    DrawingListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                    .orderAsc(DrawingListDao.Properties.DrawingDiscipline
                            , DrawingListDao.Properties.DrawingName)
                    .build().list();
            if (list.size() > 0) {
                return list;
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Get any sync drawing details
     *
     * @param folderId id of the folder that contains the drawing
     * @return an object of {@DrawingList} which contains the list of sync drawing
     */
    public ArrayList<DrawingList> getAllCurrentRevisionDrawings(int folderId) {
        try {
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            ArrayList<DrawingList> list = (ArrayList<DrawingList>) getDaoSession().getDrawingListDao().queryBuilder().where(
                    DrawingListDao.Properties.DrwFoldersId.eq(folderId),
                    DrawingListDao.Properties.CurrentRevision.eq(1),
                    DrawingListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                    .orderAsc(DrawingListDao.Properties.DrawingDiscipline
                            , DrawingListDao.Properties.DrawingName)
                    .build().list();
            if (list.size() > 0) {
                return list;
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Get any sync drawing details
     *
     * @param folderId          id of the folder that contains the drawing
     * @param originalDrawingId name of the drawing
     * @param revisited_num
     * @return an object of {@DrawingList} which contains the details of the drawing
     */
    public DrawingList getSyncDrawingDetail(int folderId, int originalDrawingId, int revisited_num) {
        try {
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            List<DrawingList> list = getDaoSession().getDrawingListDao().queryBuilder()
                    .where(DrawingListDao.Properties.OriginalDrwId.eq(originalDrawingId),
                            DrawingListDao.Properties.DrwFoldersId.eq(folderId),
                            DrawingListDao.Properties.RevisitedNum.eq(revisited_num),
                            DrawingListDao.Properties.PdfStatus.eq(PDFSynEnum.SYNC.ordinal()),
                            DrawingListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                    .build().list();
            if (list.size() > 0) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get any sync drawing details
     *
     * @param folderId          id of the folder that contains the drawing
     * @param originalDrawingId name of the drawing
     * @return an object of {@DrawingList} which contains the details of the drawing
     */
    public DrawingList getSyncDrawingDetail(int folderId, int originalDrawingId) {
        try {
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            List<DrawingList> list = getDaoSession().getDrawingListDao().queryBuilder()
                    .where(DrawingListDao.Properties.OriginalDrwId.eq(originalDrawingId),
                            DrawingListDao.Properties.DrwFoldersId.eq(folderId),
                            DrawingListDao.Properties.PdfStatus.eq(PDFSynEnum.SYNC.ordinal()),
                            DrawingListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                    .build().list();
            if (list.size() > 0) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public DrawingList getCurrentDrawingRevision(int folderId, int originalDrawingId) {
        try {
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            List<DrawingList> list = getDaoSession().getDrawingListDao().queryBuilder()
                    .where(DrawingListDao.Properties.OriginalDrwId.eq(originalDrawingId),
                            DrawingListDao.Properties.DrwFoldersId.eq(folderId),
                            DrawingListDao.Properties.CurrentRevision.eq(1),
                            DrawingListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                    .build().list();
            if (list.size() > 0) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get any sync drawing details
     *
     * @param folderId          id of the folder that contains the drawing
     * @param originalDrawingId id of the drawing
     * @return an object of {@DrawingList} which contains the details of the drawing
     */
    public DrawingList getDrawing(int folderId, int originalDrawingId) {
        try {
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            List<DrawingList> list = getDaoSession().getDrawingListDao().queryBuilder()
                    .where(DrawingListDao.Properties.OriginalDrwId.eq(originalDrawingId),
                            DrawingListDao.Properties.DrwFoldersId.eq(folderId),
                            DrawingListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                    .build().list();
            if (list.size() > 0) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Get any sync drawing details
     *
     * @param folderId     id of the folder that contains the drawing
     * @param drwDrawingId id of the drawing
     * @return an object of {@DrawingList} which contains the details of the drawing
     */
    public DrawingList getDrawingBYDrawingId(int folderId, int drwDrawingId) {
        try {
            Log.d("DrawingListRepository", "getDrawingBYDrawingId: folderId " + folderId + " originalDrawingId " + drwDrawingId);
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            List<DrawingList> list;

            list = getDaoSession().getDrawingListDao().queryBuilder().where(
                    DrawingListDao.Properties.DrawingsId.eq(drwDrawingId),
                    DrawingListDao.Properties.DrwFoldersId.eq(folderId),
                    DrawingListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                    .build().list();

            if (list != null && list.size() > 0) {
                return list.get(0);
            } else {
                Log.e("DrawingListRepository", "getDrawing: return null  folderId " + folderId + "  originalDrawingId  " + drwDrawingId);
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Get any sync drawing details
     *
     * @param folderId          id of the folder that contains the drawing
     * @param originalDrawingId id of the drawing
     * @return an object of {@DrawingList} which contains the details of the drawing
     */
    public DrawingList getDrawing(int folderId, int originalDrawingId, Integer revisionNo) {
        try {
            Log.d("DrawingListRepository", "getDrawing: folderId " + folderId + " originalDrawingId " + originalDrawingId + "  revisionNo  " + revisionNo);
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            List<DrawingList> list;
            if (revisionNo != null)
                list = getDaoSession().getDrawingListDao().queryBuilder().where(
                        DrawingListDao.Properties.OriginalDrwId.eq(originalDrawingId),
                        DrawingListDao.Properties.DrwFoldersId.eq(folderId),
                        DrawingListDao.Properties.RevisitedNum.eq(revisionNo),
                        DrawingListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                        .build().list();
            else {
                list = getDaoSession().getDrawingListDao().queryBuilder().where(
                        DrawingListDao.Properties.OriginalDrwId.eq(originalDrawingId),
                        DrawingListDao.Properties.DrwFoldersId.eq(folderId),
                        DrawingListDao.Properties.CurrentRevision.eq(1),
                        DrawingListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                        .build().list();

            }
            if (list.size() > 0) {
                return list.get(0);
            } else {
                Log.e("DrawingListRepository", "getDrawing: return null  folderId " + folderId + "  originalDrawingId  " + originalDrawingId + " revisionNo " + revisionNo);
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }/**
     * Get any sync drawing details
     *
     * @param folderId          id of the folder that contains the drawing
     * @param originalDrawingId id of the drawing
     * @return an object of {@DrawingList} which contains the details of the drawing
     */
    public DrawingList getAnySyncDrawing(int folderId, int originalDrawingId,int revisionNo) {
        try {
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            List<DrawingList> list = getDaoSession().getDrawingListDao().queryBuilder().where(
                    DrawingListDao.Properties.OriginalDrwId.eq(originalDrawingId),
                    DrawingListDao.Properties.DrwFoldersId.eq(folderId),
                    DrawingListDao.Properties.RevisitedNum.eq(revisionNo),
                    DrawingListDao.Properties.PdfStatus.eq((PDFSynEnum.SYNC.ordinal())),
                    DrawingListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                    .build().list();
            if (list.size() > 0) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public DrawingList getAnySyncDrawing(int folderId, int originalDrawingId) {
        try {
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            List<DrawingList> list = getDaoSession().getDrawingListDao().queryBuilder().where(
                    DrawingListDao.Properties.OriginalDrwId.eq(originalDrawingId),
                    DrawingListDao.Properties.DrwFoldersId.eq(folderId),
                    DrawingListDao.Properties.PdfStatus.eq((PDFSynEnum.SYNC.ordinal())),
                    DrawingListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                    .build().list();
            if (list.size() > 0) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get Drawing list according to projects whose pdf is synced locally.
     *
     * @param drwFolderId
     * @return list of Photo Folder.
     */
    public List<DrawingList> getDrawingListWithPDFSynced(int drwFolderId) {
        // get drawings according to folder id.
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        List<DrawingList> list1 = getDaoSession().getDrawingListDao().queryBuilder().
                where(new WhereCondition.StringCondition("drw_folders_id = " + drwFolderId + " AND users_id = " +
                        loginResponse.getUserDetails().getUsers_id() + " AND pdf_status = " + PDFSynEnum.SYNC.ordinal() +
//                        " GROUP BY drw_drawings_id"))
                        " GROUP BY UPPER(drawing_name)"))
                .orderAsc(DrawingListDao.Properties.DrawingDiscipline
                        , DrawingListDao.Properties.DrawingName).orderDesc(DrawingListDao.Properties.RevisitedNum).list();
        return list1;
    }


    /**
     * Get Drawing folders according to project.
     *
     * @param folderId
     * @return list of Photo Folder.
     */
    /*public List<DrawingList> getAutoDownloadDrawingList(int folderId) {
        // get drawings according to folder id.
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        List<DrawingList> list1 = getDaoSession().getDrawingListDao().queryBuilder().
                where(new WhereCondition.StringCondition("drw_folders_id = " + folderId + " AND users_id = " +
                        loginResponse.getUserDetails().getUsers_id() +
                        " AND is_sync_latest_revision = " + true +
                        " GROUP BY UPPER(drawing_name)"))
                .orderAsc(DrawingListDao.Properties.DrawingDiscipline
                        , DrawingListDao.Properties.DrawingName).orderDesc(DrawingListDao.Properties.RevisitedNum).list();
        return list1;
    }*/


    /**
     * Get Drawing folder details according to project id and folder id.
     *
     * @param pjProjectId
     * @param drwFolderId
     * @return
     */
    public DrawingFolders getDrawingFolderDetail(int pjProjectId, int drwFolderId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        QueryBuilder<DrawingFolders> queryBuilder = getDaoSession().getDrawingFoldersDao().queryBuilder();
        queryBuilder.where(DrawingFoldersDao.Properties.PjProjectsId.eq(pjProjectId), DrawingFoldersDao.Properties.DrwFoldersId.eq(drwFolderId), DrawingFoldersDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()));
        List<DrawingFolders> result = queryBuilder.build().list();
        if (result.size() > 0) {
            return result.get(0);
        } else return null;
    }

    /**
     * Get Drawing folder details according to project id and folder id.
     *
     * @param userId
     * @param drwFolderId
     * @return
     */
    public DrawingFolders getDrawingFolder(int drwFolderId, int userId) {
        QueryBuilder<DrawingFolders> queryBuilder = getDaoSession().getDrawingFoldersDao().queryBuilder();
        queryBuilder.where(DrawingFoldersDao.Properties.DrwFoldersId.eq(drwFolderId), DrawingFoldersDao.Properties.UsersId.eq(userId));
        List<DrawingFolders> result = queryBuilder.build().list();
        return result.get(0);
    }

    /**
     * update PDF status
     *
     * @param drawingList
     * @param pdfSyncStatus
     */

    public void updatePDFSync(DrawingList drawingList, int pdfSyncStatus) {
        DrawingListDao drawingListDao = getDaoSession().getDrawingListDao();
        drawingList.setPdfStatus(pdfSyncStatus);
        drawingListDao.update(drawingList);
    }

    public String doUpdateDrawingPunchlist(List<DrwPunchlists> drwPunchlists) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            int userId = loginResponse.getUserDetails().getUsers_id();
            try {
                getDaoSession().callInTx((Callable<DrawingList>) () -> {
                    for (DrwPunchlists drwPunch : drwPunchlists) {
                        if (drwPunch.getDeletedAt() == null && drwPunch.getPunchListsId() != 0) {
                            List<DrwPunchlist> drwPunchlist = getDaoSession().getDrwPunchlistDao().queryBuilder()
                                    .where(DrwPunchlistDao.Properties.PunchlistId.eq(drwPunch.getPunchListsId()),
                                            DrwPunchlistDao.Properties.DrawingId.eq(drwPunch.getDrwDrawingsId()),
                                            DrwPunchlistDao.Properties.UserId.eq(userId),
                                            DrwPunchlistDao.Properties.PjProjectsId.eq(drwPunch.getPjProjectsId())
                                    ).limit(1).list();

                            DrwPunchlist drwPnchList = new DrwPunchlist();
                            if (drwPunchlist.size() > 0) {
                                drwPnchList = drwPunchlist.get(0);
                            }
                            drwPnchList.setDrawingId(Long.valueOf(drwPunch.getDrwDrawingsId() + ""));
                            drwPnchList.setPjProjectsId(drwPunch.getPjProjectsId());
                            drwPnchList.setPunchlistId(drwPunch.getPunchListsId());
                            drwPnchList.setUserId(userId);
                            drwPnchList.setCreatedAt(drwPunch.getCreatedAt() != null && !drwPunch.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(drwPunch.getCreatedAt()) : null);
                            drwPnchList.setDeletedAt(drwPunch.getDeletedAt() != null && !drwPunch.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(drwPunch.getDeletedAt()) : null);
                            drwPnchList.setUpdatedAt(drwPunch.getUpdatedAt() != null && !drwPunch.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(drwPunch.getUpdatedAt()) : null);
                            getDaoSession().insertOrReplace(drwPnchList);
                        } else {
                            DeleteQuery<DrwPunchlist> drwPunchlistDeleteQuery = getDaoSession().queryBuilder(DrwPunchlist.class)
                                    .where(DrwPunchlistDao.Properties.PjProjectsId.eq(drwPunch.getPjProjectsId()),
                                            DrwPunchlistDao.Properties.PunchlistId.eq(drwPunch.getPunchListsId()),
                                            DrwPunchlistDao.Properties.UserId.eq(userId))
                                    .buildDelete();
                            drwPunchlistDeleteQuery.executeDeleteWithoutDetachingEntities();
                        }

                    }
                    return null;

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * Save drawing item
     *
     * @param drawing
     */
    public void updateDrawingListForCurrentRevision(DrawingList drawing) {
        try {
            getDaoSession().getDrawingListDao().insertOrReplace(drawing);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
