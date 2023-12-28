package com.pronovoscm.persistence.repository;

import android.content.Context;

import com.google.gson.Gson;
import com.pronovoscm.chipslayoutmanager.util.log.Log;
import com.pronovoscm.model.PDFSynEnum;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.PjDocumentsFiles;
import com.pronovoscm.persistence.domain.PjDocumentsFilesDao;
import com.pronovoscm.persistence.domain.PjDocumentsFolders;
import com.pronovoscm.persistence.domain.PjDocumentsFoldersDao;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.SharedPref;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProjectDocumentsRepository extends AbstractRepository {
    private Context mContext;
    private LoginResponse loginResponse;

    public ProjectDocumentsRepository(DaoSession daoSession, Context context) {
        super(daoSession);
        mContext = context;
    }

    /**
     * Save PjDocumentsFolders items
     *
     * @param documentsFolders
     */
    public void saveDocumentFolderList(List<PjDocumentsFolders> documentsFolders) {
        getDaoSession().getPjDocumentsFoldersDao().saveInTx(documentsFolders);
    }

    /**
     * update PjDocumentsFolders items
     *
     * @param documentsFolders
     */
    public void updateDocumentFolderList(List<PjDocumentsFolders> documentsFolders) {
        getDaoSession().getPjDocumentsFoldersDao().insertOrReplaceInTx(documentsFolders);
    }

    /**
     * Save PjDocumentsFolders items
     *
     * @param documentsFolder
     */
    public void saveDocumentFolder(PjDocumentsFolders documentsFolder) {
        getDaoSession().getPjDocumentsFoldersDao().saveInTx(documentsFolder);
    }

    /**
     * Save PjDocumentsFiles items
     *
     * @param documentsFiles
     */
    public void saveDocumentFilesList(List<PjDocumentsFiles> documentsFiles) {
        getDaoSession().getPjDocumentsFilesDao().saveInTx(documentsFiles);
    }

    /**
     * Save PjDocumentsFiles item
     *
     * @param documentsFiles
     */
    public void saveDocumentFile(PjDocumentsFiles documentsFiles) {
        getDaoSession().getPjDocumentsFilesDao().saveInTx(documentsFiles);
    }

    /**
     * Save PjDocumentsFolders item
     *
     * @param drawing
     */
    public void updateDocumentFolder(PjDocumentsFolders drawing) {
        getDaoSession().getPjDocumentsFoldersDao().update(drawing);
    }

    /**
     * Save DocumentFile item
     *
     * @param drawing
     */
    public void updateDocumentFile(PjDocumentsFiles drawing) {
        getDaoSession().getPjDocumentsFilesDao().update(drawing);
    }

    /**
     * Check that File is exist in storage or not.
     *
     * @param fileName
     * @return
     */
    public boolean isDocumentFileExist(String fileName) {
        String root = mContext.getFilesDir().getAbsolutePath();
        File myDir = new File(root + Constants.DOCUMENT_FILES_PATH);
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

    public void deleteExistingDocumentFile(String fileName) {
        String root = mContext.getFilesDir().getAbsolutePath();
        File myDir = new File(root + Constants.DOCUMENT_FILES_PATH + fileName);
        if (myDir.exists()) {
            boolean isDeleted = myDir.delete();
            Log.e("Delete", "isDeleted: " + isDeleted);
        }
    }


    /**
     * Get PjDocumentsFolders folders according to project. and parent folder
     *
     * @param projectId
     * @return list of   Folders.
     */
    public List<PjDocumentsFolders> getPjDocumentsFoldersList(int projectId, long parentId, String name) {
        // get drawings according to folder id.
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(mContext).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PjDocumentsFolders> list1 = getDaoSession().getPjDocumentsFoldersDao().queryBuilder()
                    .where(PjDocumentsFoldersDao.Properties.PjProjectsId.eq(projectId))
                    .where(PjDocumentsFoldersDao.Properties.ParentId.eq(parentId))
                    .where(PjDocumentsFoldersDao.Properties.Name.like("%" + name + "%"))
                    /*.where(  PjDocumentsFoldersDao.Properties.users_id = " +
                            loginResponse.getUserDetails().getUsers_id()))*/

                    .where(PjDocumentsFoldersDao.Properties.DeletedAt.isNull())
                    .where(PjDocumentsFoldersDao.Properties.IsVisible.eq(1))
                    .orderAsc(PjDocumentsFoldersDao.Properties.Name)
                    .list();
            return list1;
        } else {
            return new ArrayList<>();
        }
    }

    public void updateLastUpdatedTime(int projectId) {
        List<PjDocumentsFolders> list1 = getDaoSession().getPjDocumentsFoldersDao().queryBuilder()
                .where(PjDocumentsFoldersDao.Properties.PjProjectsId.eq(projectId)).list();
        if (list1 != null && list1.size() > 0) {
            android.util.Log.d("DocumentRepository", "updateLastUpdatedTime: liist size " + list1.size());
            List<PjDocumentsFolders> list2 = new ArrayList<>();
            for (PjDocumentsFolders folders : list1) {
                folders.setLastupdatedate(new Date());
                //  list2.add(folders);
                getDaoSession().getPjDocumentsFoldersDao().insertOrReplaceInTx(folders);
            }
            //   updateDocumentFolderList(list2);
        }
    }

    /**
     * Get PjDocumentsFiles files according to project. and parent folder
     *
     * @param projectId
     * @return list of PjDocumentsFiles.
     */
    public List<PjDocumentsFiles> getPjDocumentsFilesList(int projectId, long pjDocumentsFoldersId, String name) {
        // get drawings according to folder id.
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(mContext).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PjDocumentsFiles> list1 = getDaoSession().getPjDocumentsFilesDao().queryBuilder()
                    .where(PjDocumentsFilesDao.Properties.PjProjectsId.eq(projectId))
                    .where(PjDocumentsFilesDao.Properties.PjDocumentsFoldersId.eq(pjDocumentsFoldersId))
                    .where(PjDocumentsFilesDao.Properties.DeletedAt.isNull())
                    .where(PjDocumentsFilesDao.Properties.ActiveRevision.eq(1))
                    .where(PjDocumentsFilesDao.Properties.IsVisible.eq(1))
                  /*  .where(PjDocumentsFilesDao.Properties.IsPrivate.eq(0))*/
                    .where(PjDocumentsFilesDao.Properties.OriginalName.like("%" + name + "%"))
                    .orderAsc(PjDocumentsFilesDao.Properties.OriginalName)
                    .list();
            return list1;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Get PjDocumentsFiles files according to project. and parent folder
     *
     * @param projectId
     * @return list of PjDocumentsFiles.
     */
    public PjDocumentsFiles getPjDocumentsFilesSyncRevision(int projectId, long originalFilesID) {
        // get drawings according to folder id.
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(mContext).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            List<PjDocumentsFiles> list1 = getDaoSession().getPjDocumentsFilesDao().queryBuilder()
                    .where(PjDocumentsFilesDao.Properties.PjProjectsId.eq(projectId))
                    .where(PjDocumentsFilesDao.Properties.OriginalPjDocumentsFilesId.eq(originalFilesID))
                    .where(PjDocumentsFilesDao.Properties.FileStatus.eq(PDFSynEnum.SYNC.ordinal()))
                    .where(PjDocumentsFilesDao.Properties.DeletedAt.isNull())
                    .where(PjDocumentsFilesDao.Properties.IsVisible.eq(1))

                    .list();
            if (list1 != null && list1.size() > 0)
                return list1.get(0);
            else {
                return null;
            }
        } else {
            return null;
        }
    }


    public String getMAXProjectDocumentFolderUpdateDate(int projectID) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(mContext).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<PjDocumentsFolders> maxPostIdRow = getDaoSession().getPjDocumentsFoldersDao().queryBuilder()
                .where(PjDocumentsFoldersDao.Properties.UpdatedAt.isNotNull(),
                        PjDocumentsFoldersDao.Properties.PjProjectsId.eq(projectID)
                        /* , PjDocumentsFoldersDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))*/
                ).orderDesc(PjDocumentsFoldersDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1990-01-01 01:01:01";
    }

    public String getMAXProjectDocumentFilesUpdateDate(int projectID) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(mContext).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<PjDocumentsFiles> maxPostIdRow = getDaoSession().getPjDocumentsFilesDao().queryBuilder()
                .where(PjDocumentsFilesDao.Properties.UpdatedAt.isNotNull(),
                        PjDocumentsFoldersDao.Properties.PjProjectsId.eq(projectID)
                        /* , PjDocumentsFoldersDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))*/
                ).orderDesc(PjDocumentsFoldersDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1990-01-01 01:01:01";
    }
}
