package com.pronovoscm.persistence.repository;

import android.content.Context;
import android.util.Log;

import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.BackupSyncImageFiles;
import com.pronovoscm.persistence.domain.BackupSyncImageFilesDao;
import com.pronovoscm.persistence.domain.DaoSession;

import org.greenrobot.greendao.query.DeleteQuery;

import java.util.List;

public class BackupSyncRepository extends AbstractRepository {
    private Context context;
    private LoginResponse loginResponse;

    public BackupSyncRepository(DaoSession daoSession, Context context) {
        super(daoSession);
        this.context = context;
    }

    public void deleteBackupSyncRecord(BackupSyncImageFiles backupSyncImageFiles) {
        DeleteQuery<BackupSyncImageFiles> backupSyncImageFilesDeleteQuery = getDaoSession()
                .queryBuilder(BackupSyncImageFiles.class)
                .where(
                        BackupSyncImageFilesDao.Properties.Name.eq(backupSyncImageFiles.getName()),
                        BackupSyncImageFilesDao.Properties.Type.eq(backupSyncImageFiles.getType()),

                        BackupSyncImageFilesDao.Properties.Location.eq(backupSyncImageFiles.getLocation()))
                .buildDelete();

     /*   QueryBuilder<BackupSyncImageFiles> backupSyncImageFilesDeleteQuery = getDaoSession()
                .queryBuilder(BackupSyncImageFiles.class)
                .where(   BackupSyncImageFilesDao.Properties.Id.eq(backupSyncImageFiles.getId()),
                        BackupSyncImageFilesDao.Properties.Name.eq(backupSyncImageFiles.getName()),
                        BackupSyncImageFilesDao.Properties.Type.eq(backupSyncImageFiles.getType()),

                        BackupSyncImageFilesDao.Properties.Location.eq(backupSyncImageFiles.getLocation()))
                .buildDelete();*/
        backupSyncImageFilesDeleteQuery.executeDeleteWithoutDetachingEntities();
        Log.e("BackupSyncRepository", "deleteBackupSyncRecord: ");
    }

    public boolean isBackupSyncTableEmpty() {
        List<BackupSyncImageFiles> backupSyncImageFilesList = getDaoSession().getBackupSyncImageFilesDao().queryBuilder().list();
        if (backupSyncImageFilesList == null || backupSyncImageFilesList.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public List<BackupSyncImageFiles> getBackupSyncFilesList() {
        List<BackupSyncImageFiles> backupSyncImageFilesList = getDaoSession().getBackupSyncImageFilesDao().queryBuilder().list();
        Log.d("Repository ", "getBackupSyncFilesList: backupSyncImageFilesList  " + backupSyncImageFilesList);
        if (backupSyncImageFilesList == null || backupSyncImageFilesList.size() == 0) {
            return null;
        } else {
            return backupSyncImageFilesList;
        }
    }
}
