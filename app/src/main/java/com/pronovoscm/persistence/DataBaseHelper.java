package com.pronovoscm.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.pronovoscm.BuildConfig;
import com.pronovoscm.data.DrawingAnnotationProvider;
import com.pronovoscm.model.SyncDataEnum;
import com.pronovoscm.model.TransactionModuleEnum;
import com.pronovoscm.model.response.cssjs.CSSJSResponse;
import com.pronovoscm.model.response.cssjs.FormAsset;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.login.UserPermissions;
import com.pronovoscm.persistence.domain.AlbumCoverPhoto;
import com.pronovoscm.persistence.domain.AlbumCoverPhotoDao;
import com.pronovoscm.persistence.domain.BackupSyncImageFiles;
import com.pronovoscm.persistence.domain.BackupSyncImageFilesDao;
import com.pronovoscm.persistence.domain.CompanyListDao;
import com.pronovoscm.persistence.domain.CrewList;
import com.pronovoscm.persistence.domain.CrewListDao;
import com.pronovoscm.persistence.domain.DaoMaster;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.DrawingFolders;
import com.pronovoscm.persistence.domain.DrawingFoldersDao;
import com.pronovoscm.persistence.domain.DrawingList;
import com.pronovoscm.persistence.domain.DrawingListDao;
import com.pronovoscm.persistence.domain.DrawingXmls;
import com.pronovoscm.persistence.domain.DrawingXmlsDao;
import com.pronovoscm.persistence.domain.DrwPunchlistDao;
import com.pronovoscm.persistence.domain.EquipmentCategoriesMasterDao;
import com.pronovoscm.persistence.domain.EquipmentInventoryDao;
import com.pronovoscm.persistence.domain.EquipmentRegionDao;
import com.pronovoscm.persistence.domain.EquipmentSubCategoriesMasterDao;
import com.pronovoscm.persistence.domain.FormAssets;
import com.pronovoscm.persistence.domain.FormAssetsDao;
import com.pronovoscm.persistence.domain.FormCategoryDao;
import com.pronovoscm.persistence.domain.FormImageDao;
import com.pronovoscm.persistence.domain.Forms;
import com.pronovoscm.persistence.domain.FormsComponentDao;
import com.pronovoscm.persistence.domain.FormsDao;
import com.pronovoscm.persistence.domain.FormsNameDao;
import com.pronovoscm.persistence.domain.FormsPermissionDao;
import com.pronovoscm.persistence.domain.FormsScheduleDao;
import com.pronovoscm.persistence.domain.ImageTagDao;
import com.pronovoscm.persistence.domain.PhotoFolder;
import com.pronovoscm.persistence.domain.PhotoFolderDao;
import com.pronovoscm.persistence.domain.PhotosMobile;
import com.pronovoscm.persistence.domain.PhotosMobileDao;
import com.pronovoscm.persistence.domain.PjAssigneeAttachmentsDao;
import com.pronovoscm.persistence.domain.PjDocumentsFilesDao;
import com.pronovoscm.persistence.domain.PjDocumentsFoldersDao;
import com.pronovoscm.persistence.domain.PjProjects;
import com.pronovoscm.persistence.domain.PjProjectsDao;
import com.pronovoscm.persistence.domain.PjProjectsInfoDao;
import com.pronovoscm.persistence.domain.PjProjectsResourcesDao;
import com.pronovoscm.persistence.domain.PjProjectsSubcontractorsDao;
import com.pronovoscm.persistence.domain.PjProjectsTeamDao;
import com.pronovoscm.persistence.domain.PjRfiAttachmentsDao;
import com.pronovoscm.persistence.domain.PjRfiContactListDao;
import com.pronovoscm.persistence.domain.PjRfiDao;
import com.pronovoscm.persistence.domain.PjRfiRepliesDao;
import com.pronovoscm.persistence.domain.PjRfiSettingsDao;
import com.pronovoscm.persistence.domain.PjSubmittalAttachmentsDao;
import com.pronovoscm.persistence.domain.PjSubmittalContactListDao;
import com.pronovoscm.persistence.domain.PjSubmittalsDao;
import com.pronovoscm.persistence.domain.ProjectForm;
import com.pronovoscm.persistence.domain.ProjectFormAreaDao;
import com.pronovoscm.persistence.domain.ProjectFormDao;
import com.pronovoscm.persistence.domain.PunchListAttachments;
import com.pronovoscm.persistence.domain.PunchListAttachmentsDao;
import com.pronovoscm.persistence.domain.PunchlistAssigneeDao;
import com.pronovoscm.persistence.domain.PunchlistDb;
import com.pronovoscm.persistence.domain.PunchlistDbDao;
import com.pronovoscm.persistence.domain.PunchlistDrawingDao;
import com.pronovoscm.persistence.domain.RegionsTableDao;
import com.pronovoscm.persistence.domain.TransactionLogMobile;
import com.pronovoscm.persistence.domain.TransactionLogMobileDao;
import com.pronovoscm.persistence.domain.UserForms;
import com.pronovoscm.persistence.domain.UserFormsDao;
import com.pronovoscm.persistence.domain.WeatherReport;
import com.pronovoscm.persistence.domain.WeatherReportDao;
import com.pronovoscm.persistence.domain.WorkDetails;
import com.pronovoscm.persistence.domain.WorkDetailsAttachments;
import com.pronovoscm.persistence.domain.WorkDetailsAttachmentsDao;
import com.pronovoscm.persistence.domain.WorkDetailsDao;
import com.pronovoscm.persistence.domain.WorkImpact;
import com.pronovoscm.persistence.domain.WorkImpactAttachments;
import com.pronovoscm.persistence.domain.WorkImpactAttachmentsDao;
import com.pronovoscm.persistence.domain.WorkImpactDao;
import com.pronovoscm.persistence.domain.projectissuetracking.ImpactsAndRootCauseCacheDao;
import com.pronovoscm.persistence.domain.projectissuetracking.IssueTrackingCustomFieldsCacheDao;
import com.pronovoscm.persistence.domain.projectissuetracking.IssueTrackingItemTypesCacheDao;
import com.pronovoscm.persistence.domain.projectissuetracking.IssueTrackingItemsCacheDao;
import com.pronovoscm.persistence.domain.projectissuetracking.IssueTrackingSectionCacheDao;
import com.pronovoscm.persistence.domain.projectissuetracking.ProjectIssueImpactsAndCausesCacheDao;
import com.pronovoscm.persistence.domain.projectissuetracking.ProjectIssuesCacheDao;
import com.pronovoscm.persistence.domain.projectissuetracking.ProjectIssuesItemBreakdownCacheDao;
import com.pronovoscm.persistence.domain.punchlist.PunchListHistoryDbDao;
import com.pronovoscm.persistence.domain.punchlist.PunchListRejectReasonAttachmentsDao;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.LogUtils;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.backupsync.BackupFileTypeEnum;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.Join;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Database helper.
 *
 * @author Nitin Bhawsar
 */
public class DataBaseHelper extends DaoMaster.OpenHelper {
    /*@Inject
    ProjectsProvider projectsProvider;
    @Inject
    CrewReportRepository crewReportRepository;
    @Inject
    WorkDetailsRepository workDetailsRepository;
    @Inject
    WorkImpactRepository workImpactRepository;
    @Inject
    PunchListRepository punchListRepository;
    @Inject
    DrawingAnnotationProvider drawingAnnotationProvider;*/

    private static final String TAG = LogUtils.makeLogTag(DataBaseHelper.class);

    private final Context context;
    private DaoSession daoSession;

    public DataBaseHelper(Context context, String name) {
        super(context, name);
        this.context = context;
        //((PronovosApplication)context).getDaggerComponent().inject(this);

    }

    private static void copyDBFile(Context context) {
        try {
            Log.d(TAG, "copyDBFile: start ");
            InputStream in = null;
            OutputStream out = null;
            String dbCopyFilename = "pronovos_cm_market_copy";
            File myDir = new File(context.getFilesDir().getAbsolutePath() + "/Pronovos/dbBackup/");//"/PronovosPronovos"
            if (!myDir.exists()) {
                myDir.mkdirs();
            }
            String path = context.getDatabasePath("pronovos_cm").getPath();
            Log.d(TAG, "copyDBFile: start path " + path);
            File dbFile = new File(path);


            if (dbFile != null && dbFile.exists()) {
                File outFile = new File(myDir, dbCopyFilename);
                if (outFile.exists()) {
                    outFile.delete();
                }
                out = new FileOutputStream(outFile);
                in = new FileInputStream(dbFile);
                Log.e(TAG, "************* copyDBFile: file exist");
                copyFile(in, out);
            } else {
                Log.e(TAG, "************* copyDBFile: db file not exist");
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    public static void copyAssets(Context context) {
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File myDir = new File(context.getFilesDir().getAbsolutePath() + "/Pronovos/");//"/PronovosPronovos"
                if (!myDir.exists()) {
                    myDir.mkdirs();
                }
                File outFile = new File(myDir, filename);
                if (outFile.exists()) {
                    outFile.delete();
                }
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch (IOException e) {
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
// NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
// NOOP
                    }
                }
            }
        }
    }

    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        Log.d(TAG, "copyFile: done ");
    }

    private static boolean existsColumnInTable(SQLiteDatabase inDatabase, String inTable, String columnToCheck) {
        Cursor mCursor = null;
        try {
            // Query 1 row
            mCursor = inDatabase.rawQuery("SELECT * FROM " + inTable + " LIMIT 0", null);
            // getColumnIndex() gives us the index (0 to ...) of the column - otherwise we get a -1
            if (mCursor.getColumnIndex(columnToCheck) != -1) {
                Log.d(TAG, "existsColumnInTable: $$$$$$$$$$$$$$$$$$$$$  true " + columnToCheck);
                return true;
            } else {
                Log.d(TAG, "existsColumnInTable: $$$$$$$$$$$$$$$$$$$$$  false  " + columnToCheck);
                return false;
            }

        } catch (Exception Exp) {
            // Something went wrong. Missing the database? The table?
            Log.d(TAG, " ... - existsColumnInTable When checking whether a column exists in the table, an error occurred: " + Exp.getMessage());
            return false;
        } finally {
            if (mCursor != null) mCursor.close();
        }
    }

    static void clearSession(Context context) {
        SharedPref.getInstance(context).writePrefs(SharedPref.SESSION_DETAILS, null);
        SharedPref.getInstance(context).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
    }

    private void createTables(Database db) {
        createInfoTable(db);
        createDrwPunchlist(db);
        createPjProjectsResources(db);
        createPjProjectsSubcontractors(db);
        createPjProjectsTeam(db);
        createEquipmentTables(db);
        createPjProjects(db);
        createTransactionLogMobile(db);
        createFormsPermission(db);
        createFormsName(db);
        createProjectFormArea(db);
        createPunchlistDrawing(db);
        updatePunchlistTable(db);
        createForms(db);
        createFormsComponent(db);
        createFormsSchedule(db);

        createFormCategory(db);
        createUserForms(db);
        createProjectForm(db);
        createFormAssets(db);

        createFormImage(db);
        createRegionsTable(db);
        addCurrentRevisionColumanDrawingList(db);
        alterTableForMissingColumn(db);
        alterTableForMissingColumn(db);
        alterTableForMissingColumn(db);
        //setDrawingFolderDateto1970(db);

        createSubmittals(db);
        createSubmittalsAttachment(db);
        createSubmittalsContact(db);
        createSubmittalsContactAttachment(db);
    }

    private void createSubmittals(Database db) {

        SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
        try {
            if (!doesTableExist(sqliteDB, PjSubmittalsDao.TABLENAME))
                PjSubmittalsDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void createSubmittalsAttachment(Database db) {

        SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
        try {
            if (!doesTableExist(sqliteDB, PjSubmittalAttachmentsDao.TABLENAME))
                PjSubmittalAttachmentsDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void createSubmittalsContact(Database db) {

        SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
        try {
            if (!doesTableExist(sqliteDB, PjSubmittalContactListDao.TABLENAME))
                PjSubmittalContactListDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void createSubmittalsContactAttachment(Database db) {

        SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
        try {
            if (!doesTableExist(sqliteDB, PjAssigneeAttachmentsDao.TABLENAME))
                PjAssigneeAttachmentsDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void alterTableForMissingColumn(Database db) {
        SQLiteDatabase sqLiteDatabase = (SQLiteDatabase) db.getRawDatabase();
        try {
            if (!existsColumnInTable(sqLiteDatabase, UserFormsDao.TABLENAME, UserFormsDao.Properties.Publish.columnName))
                db.execSQL("ALTER TABLE " + UserFormsDao.TABLENAME + " ADD COLUMN " + UserFormsDao.Properties.Publish.columnName + " INTEGER");
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        try {
            if (!existsColumnInTable(sqLiteDatabase, FormsDao.TABLENAME, FormsDao.Properties.FormSections.columnName))
                db.execSQL("ALTER TABLE " + FormsDao.TABLENAME + " ADD COLUMN " + FormsDao.Properties.FormSections.columnName + " TEXT");
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void createInfoTable(Database db) {
        try {
            SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
            if (!doesTableExist(sqliteDB, PjProjectsInfoDao.TABLENAME))
                PjProjectsInfoDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void createDrwPunchlist(Database db) {
        try {
            SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
            if (!doesTableExist(sqliteDB, DrwPunchlistDao.TABLENAME))
                DrwPunchlistDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void updatePunchlistTable(Database db) {
        SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
        try {
            if (!existsColumnInTable(sqliteDB, PunchlistDbDao.TABLENAME, PunchlistDbDao.Properties.SendEmail.columnName))
                db.execSQL("ALTER TABLE " + PunchlistDbDao.TABLENAME + " ADD COLUMN " + PunchlistDbDao.Properties.SendEmail.columnName + " INTEGER");
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        try {
            if (!existsColumnInTable(sqliteDB, PunchlistDbDao.TABLENAME, PunchlistDbDao.Properties.IsInProgress.columnName))
                db.execSQL("ALTER TABLE " + PunchlistDbDao.TABLENAME + " ADD COLUMN " + PunchlistDbDao.Properties.IsInProgress.columnName + " BOOLEAN");
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        try {
            if (!existsColumnInTable(sqliteDB, PjProjectsDao.TABLENAME, PjProjectsDao.Properties.ShowcasePhoto.columnName))
                db.execSQL("ALTER TABLE " + PjProjectsDao.TABLENAME + " ADD COLUMN " + PjProjectsDao.Properties.ShowcasePhoto.columnName + " TEXT");
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void createPunchlistDrawing(Database db) {
        SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();

        try {
            if (!doesTableExist(sqliteDB, PunchlistDrawingDao.TABLENAME))
                PunchlistDrawingDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }

        try {
            if (!existsColumnInTable(sqliteDB, DrawingFoldersDao.TABLENAME, DrawingFoldersDao.Properties.SyncDrawingFolder.columnName))
                db.execSQL("ALTER TABLE " + DrawingFoldersDao.TABLENAME + " ADD COLUMN " + DrawingFoldersDao.Properties.SyncDrawingFolder.columnName + " BOOLEAN");
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        try {
            if (!existsColumnInTable(sqliteDB, PhotoFolderDao.TABLENAME, PhotoFolderDao.Properties.IsStatic.columnName))
                db.execSQL("ALTER TABLE " + PhotoFolderDao.TABLENAME + " ADD COLUMN " + PhotoFolderDao.Properties.IsStatic.columnName + " INTEGER");
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        try {
            if (!existsColumnInTable(sqliteDB, CompanyListDao.TABLENAME, CompanyListDao.Properties.IsDeleted.columnName))
                db.execSQL("ALTER TABLE " + CompanyListDao.TABLENAME + " ADD COLUMN " + CompanyListDao.Properties.IsDeleted.columnName + " INTEGER");

        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        try {
            if (!existsColumnInTable(sqliteDB, DrawingXmlsDao.TABLENAME, DrawingXmlsDao.Properties.Annotdeletexml.columnName))
                db.execSQL("ALTER TABLE " + DrawingXmlsDao.TABLENAME + " ADD COLUMN " + DrawingXmlsDao.Properties.Annotdeletexml.columnName + " TEXT");
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        try {
            if (!existsColumnInTable(sqliteDB, PunchlistAssigneeDao.TABLENAME, PunchlistAssigneeDao.Properties.UserId.columnName))
                db.execSQL("ALTER TABLE " + PunchlistAssigneeDao.TABLENAME + " ADD COLUMN " + PunchlistAssigneeDao.Properties.UserId.columnName + " INTEGER");
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }


    }

    private void createFormsPermission(Database db) {
        try {
            SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
            if (!doesTableExist(sqliteDB, FormsPermissionDao.TABLENAME))
                FormsPermissionDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void createFormsName(Database db) {
        try {
            SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
            if (!doesTableExist(sqliteDB, FormsNameDao.TABLENAME))
                FormsNameDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void createPjProjectsResources(Database db) {
        try {
            SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
            if (!doesTableExist(sqliteDB, PjProjectsResourcesDao.TABLENAME))
                PjProjectsResourcesDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void createPjProjectsSubcontractors(Database db) {
        try {
            SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
            if (!doesTableExist(sqliteDB, PjProjectsSubcontractorsDao.TABLENAME))
                PjProjectsSubcontractorsDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void createPjProjectsTeam(Database db) {
        try {
            SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
            if (!doesTableExist(sqliteDB, PjProjectsTeamDao.TABLENAME))
                PjProjectsTeamDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    public boolean doesTableExist(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                Log.d(TAG, "doesTableExist: $$$$$$$ true   " + tableName);
                return true;
            }
            cursor.close();
        }
        Log.d(TAG, "doesTableExist: $$$$$$$ false   " + tableName);
        return false;
    }

    private void createPjProjects(Database db) {
        try {
            SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
            if (!doesTableExist(sqliteDB, PjProjectsDao.TABLENAME))
                PjProjectsDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void addCurrentRevisionColumanDrawingList(Database db) {
        try {
            SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
            if (!existsColumnInTable(sqliteDB, DrawingListDao.TABLENAME, DrawingListDao.Properties.CurrentRevision.columnName)) {
                db.execSQL("ALTER TABLE " + DrawingListDao.TABLENAME + " ADD COLUMN " +
                        DrawingListDao.Properties.CurrentRevision.columnName + " INTEGER NOT NULL DEFAULT(1)");
                Log.e(TAG, "addCurrentRevisionColumanDrawingList:  column added  $$$$$$$$ " + DrawingListDao.Properties.CurrentRevision.columnName);
                //  updateSyncDrawingFolder(daoSession);
                setDrawingFolderDateto1970(db);
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }


    }

    private void updateSyncDrawingFolder(DaoSession daoSession) {
        List<DrawingFolders> drawingFoldersList = daoSession.getDrawingFoldersDao().queryBuilder().list();
        for (DrawingFolders drawingFolders : drawingFoldersList) {
            SimpleDateFormat serviceDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = serviceDateFormat.parse("1970-01-01 01:01:01");
                drawingFolders.setLastUpdateXml(date);
                daoSession.getDrawingFoldersDao().update(drawingFolders);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void setDrawingFolderDateto1970(Database db) {
        try {
            SQLiteDatabase sqLiteDatabase = (SQLiteDatabase) db.getRawDatabase();
            //String lastUpdateStr = "1970-01-01 01:01:01";
            Date defaultDate = DateFormatter.getDateFromDateTimeString(DateFormatter.DEFAULT_DATE);
            String updateQuery = "UPDATE " + DrawingFoldersDao.TABLENAME + " SET COLUMN " + DrawingFoldersDao.Properties.LastUpdateXml.columnName + " = " + defaultDate.getTime();
            Log.d(TAG, "setDrawingFolderDateto1970: start $$$$$$$ time  =  " + defaultDate.getTime());
            Log.d(TAG, "setDrawingFolderDateto1970: start $$$$$$$** updateQuery  =  " + updateQuery);
            //db.execSQL("UPDATE " + DrawingFoldersDao.TABLENAME + " SET COLUMN " + DrawingFoldersDao.Properties.LastUpdateXml.columnName + " = " + defaultDate.getTime());
            ContentValues cvUpdate = new ContentValues();
            cvUpdate.put(DrawingFoldersDao.Properties.SyncDrawingFolder.columnName, 0);
            cvUpdate.put(DrawingFoldersDao.Properties.LastUpdateXml.columnName, defaultDate.getTime());
            long i = sqLiteDatabase.update(DrawingFoldersDao.TABLENAME, cvUpdate, null, null);

            // sqLiteDatabase.execSQL(updateQuery);

            Log.d(TAG, "setDrawingFolderDateto1970: end $$$$$$$ records updated  " + i);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void createRegionsTable(Database db) {
        SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
        try {
            if (!doesTableExist(sqliteDB, RegionsTableDao.TABLENAME))
                RegionsTableDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        try {
            if (!existsColumnInTable(sqliteDB, PhotosMobileDao.TABLENAME, PhotosMobileDao.Properties.DeletedAt.columnName))
                db.execSQL("ALTER TABLE " + PhotosMobileDao.TABLENAME + " ADD COLUMN " + PhotosMobileDao.Properties.DeletedAt.columnName + "  INTEGER   ");
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void createProjectFormArea(Database db) {
        SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
        try {
            if (!doesTableExist(sqliteDB, ProjectFormAreaDao.TABLENAME))
                ProjectFormAreaDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void createForms(Database db) {
        // Add new Forms table
        SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
        try {
            if (!doesTableExist(sqliteDB, FormsDao.TABLENAME))
                FormsDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        try {
            if (!existsColumnInTable(sqliteDB, FormsDao.TABLENAME, FormsDao.Properties.DefaultValues.columnName))
                db.execSQL("ALTER TABLE " + FormsDao.TABLENAME + " ADD COLUMN " + FormsDao.Properties.DefaultValues.columnName + " TEXT");
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void createFormsComponent(Database db) {
        // Add new Forms table
        SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
        try {
            if (!doesTableExist(sqliteDB, FormsComponentDao.TABLENAME))
                FormsComponentDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void createFormCategory(Database db) {
        // Add new Forms table
        SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
        try {
            if (!doesTableExist(sqliteDB, FormCategoryDao.TABLENAME))
                FormCategoryDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void createFormAssets(Database db) {
        SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
        try {
            if (!doesTableExist(sqliteDB, FormAssetsDao.TABLENAME))
                FormAssetsDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void createFormsSchedule(Database db) {
        SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();

        try {
            if (!doesTableExist(sqliteDB, FormsScheduleDao.TABLENAME))
                FormsScheduleDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }


    }

    private void createFormImage(Database db) {

        SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
        try {
            if (!doesTableExist(sqliteDB, FormImageDao.TABLENAME))
                FormImageDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        try {
            if (!existsColumnInTable(sqliteDB, ImageTagDao.TABLENAME, ImageTagDao.Properties.TenantId.columnName))
                db.execSQL("ALTER TABLE " + ImageTagDao.TABLENAME + " ADD COLUMN " + ImageTagDao.Properties.TenantId.columnName + " INTEGER");
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void createProjectForm(Database db) {
        // Add new Forms table
        try {
            SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
            if (!doesTableExist(sqliteDB, ProjectFormDao.TABLENAME))
                ProjectFormDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void createTransactionLogMobile(Database db) {
        try {
            SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
            if (!doesTableExist(sqliteDB, TransactionLogMobileDao.TABLENAME))
                TransactionLogMobileDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void createEquipmentTables(Database db) {
        SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
        try {
            if (!doesTableExist(sqliteDB, EquipmentCategoriesMasterDao.TABLENAME))
                EquipmentCategoriesMasterDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }

        try {
            if (!doesTableExist(sqliteDB, EquipmentSubCategoriesMasterDao.TABLENAME))
                EquipmentSubCategoriesMasterDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        try {
            if (!doesTableExist(sqliteDB, EquipmentRegionDao.TABLENAME))
                EquipmentRegionDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        try {
            if (!doesTableExist(sqliteDB, EquipmentInventoryDao.TABLENAME))
                EquipmentInventoryDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void createUserForms(Database db) {
        SQLiteDatabase sqliteDB = (SQLiteDatabase) db.getRawDatabase();
        try {
            if (!doesTableExist(sqliteDB, UserFormsDao.TABLENAME))
                UserFormsDao.createTable(db, false);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        //((PronovosApplication)context).getDaggerComponent().inject(this);
        List<Migration> migrations = getMigrations();
        // Log.e(TAG, "onUpgrade Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables  list size  " + migrations.size());
        // Only run migrations past the old version
        try {
            // copyDBFile(context);
            createTables(db);
            for (Migration migration : migrations) {

                if (oldVersion < migration.getVersion()) {
                    Log.e(TAG, "  migration " + migration.getVersion() + "  migration " + migration.getClass().getSimpleName());
                    migration.runMigration(db, context);

                }
                Log.d(TAG, oldVersion + "  = oldVersion onUpgrade: migration " + migration.getVersion() + "  migration " + migration.getClass().getSimpleName());
            }

        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            Log.e(TAG, "onUpgrade: exception occure " + e.getMessage());
        }

        // clear all refresh dates
       /* SharedPreferences preferences = PronovosApplication.getSharedPreferencesRefreshDates(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();*/

//        onCreate(db);
    }

    private List<Migration> getMigrations() {
        List<Migration> migrations = new ArrayList<>();
//        migrations.add(new MigrationV2());
//        Toast.makeText(context, "up 2", Toast.LENGTH_SHORT).show();
        migrations.add(new MigrationV1());
        migrations.add(new MigrationV2());
        migrations.add(new MigrationV3());
        // Sorting just to be safe, in case other people add migrations in the wrong order.
        migrations.add(new MigrationV4());
        migrations.add(new MigrationV5());
        migrations.add(new MigrationV6());
        migrations.add(new MigrationV7());
        migrations.add(new MigrationV8());
        migrations.add(new MigrationV9());
        migrations.add(new MigrationV10());
        migrations.add(new MigrationV11());
        migrations.add(new MigrationV12());
        migrations.add(new MigrationV13());
        migrations.add(new MigrationV14());
        migrations.add(new MigrationV15());
        migrations.add(new MigrationV16());
        migrations.add(new MigrationV17());
        migrations.add(new MigrationV18());
        migrations.add(new MigrationV19()); //TODO: Add punch list assigned_cc field. 23 Aug 2022
        migrations.add(new MigrationV20());  //TODO: Add punch list description field Oct 1, 2022
        migrations.add(new MigrationV21());  //TODO: Add default Assignee and CC in Assignee table and email_status in users Form table. Oct 11, 2022
        migrations.add(new MigrationV22());  //TODO: Add submittal , submittal attachment , submittal contact , submittal contact attachment. Dec 05, 2022
        migrations.add(new MigrationV23());  //TODO: Add Root Cause and Impact Master, Project issues, Issues Impact and root cause, Issues Breakdown. Jan 04, 2023
        migrations.add(new MigrationV24());
        // Toast.makeText(context, "  getMigrations ", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "getMigrations: migrations list  " + migrations.size());
        Comparator<Migration> migrationComparator = new Comparator<Migration>() {
            @Override
            public int compare(Migration m1, Migration m2) {
                return m1.getVersion().compareTo(m2.getVersion());
            }
        };

        Collections.sort(migrations, migrationComparator);
        return migrations;
    }

    private interface Migration {
        Integer getVersion();

        void runMigration(Database db, Context context);
    }

    private static class MigrationV3 implements Migration {


        @Override
        public Integer getVersion() {
            return 10;
        }

        @Override
        public void runMigration(Database db, Context context) {
            try {
                db.execSQL("ALTER TABLE " + PunchlistDbDao.TABLENAME + " ADD COLUMN " + PunchlistDbDao.Properties.SendEmail.columnName + " INTEGER");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                PjProjectsInfoDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
        }
    }

    private static class MigrationV4 implements Migration {


        @Override
        public Integer getVersion() {
            return 11;
        }

        @Override
        public void runMigration(Database db, Context context) {
            try {
                DrwPunchlistDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
//            PjProjectsInfoDao.createTable(db, false);
            try {
                PjProjectsResourcesDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                PjProjectsSubcontractorsDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                PjProjectsTeamDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
        }
    }

    private static class MigrationV2 implements Migration {

        DaoSession daoSession;

        @Override
        public Integer getVersion() {
            return 8;
        }

        @Override
        public void runMigration(Database db, Context context) {
            try {
                EquipmentCategoriesMasterDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                EquipmentCategoriesMasterDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {

                EquipmentSubCategoriesMasterDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                EquipmentRegionDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                EquipmentInventoryDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
        }
    }

    private static class MigrationV5 implements Migration {

        DaoSession daoSession;

        @Override
        public Integer getVersion() {
            return 12;
        }

        @Override
        public void runMigration(Database db, Context context) {
            // Add new column to user table
            PjProjectsDao.dropTable(db, true);
            try {
                PjProjectsDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
        }
    }

    private static class MigrationV13 implements Migration {
        DaoSession daoSession;

        @Override
        public Integer getVersion() {
            return 21;
        }

        @Override
        public void runMigration(Database db, Context context) {
            daoSession = new DaoMaster(db).newSession();
            try {
                FormsPermissionDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                FormsNameDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                db.execSQL("ALTER TABLE " + FormsDao.TABLENAME + " ADD COLUMN " + FormsDao.Properties.DefaultValues.columnName + " TEXT");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                db.execSQL("ALTER TABLE " + FormsDao.TABLENAME + " ADD COLUMN " + FormsDao.Properties.OriginalFormsId.columnName + " INTEGER NOT NULL DEFAULT(0)");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                db.execSQL("ALTER TABLE " + FormsDao.TABLENAME + " ADD COLUMN " + FormsDao.Properties.RevisionNumber.columnName + " INTEGER NOT NULL DEFAULT(0)");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {

                db.execSQL("ALTER TABLE " + UserFormsDao.TABLENAME + " ADD COLUMN " + UserFormsDao.Properties.RevisionNumber.columnName + " INTEGER NOT NULL DEFAULT(0)");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);

            }
            try {
                db.execSQL("ALTER TABLE " + FormsDao.TABLENAME + " ADD COLUMN " + FormsDao.Properties.ActiveRevision.columnName + " INTEGER NOT NULL DEFAULT(0)");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);

            }
            try {
                db.execSQL("ALTER TABLE " + FormCategoryDao.TABLENAME + " ADD COLUMN " + FormCategoryDao.Properties.TenantId.columnName + " INTEGER NOT NULL DEFAULT(0)");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);

            }
            try {


                db.execSQL("ALTER TABLE " + FormCategoryDao.TABLENAME + " ADD COLUMN " + FormCategoryDao.Properties.IsDefault.columnName + " INTEGER NOT NULL DEFAULT(0)");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            //  db.execSQL("UPDATE " + FormsDao.TABLENAME + " SET COLUMN " + FormsDao.Properties.OriginalFormsId.columnName + " = " + FormsDao.Properties.FormsId.columnName);
            updateProjectFormsTable();
            updateFormsTable();
            //  db.endTransaction();

        }

        private void updateProjectFormsTable() {
            Date date = new Date(0);
            ProjectFormDao projectFormDao = daoSession.getProjectFormDao();
            List<ProjectForm> projectFormList = projectFormDao.queryBuilder().list();
            for (ProjectForm p : projectFormList) {
                p.setFormLastUpdatedDate(date);
                projectFormDao.insertOrReplace(p);
            }
        }

        private void updateFormsTable() {
            FormsDao formsDao = daoSession.getFormsDao();
            List<Forms> formsList = formsDao.queryBuilder().list();
            for (Forms f : formsList) {
                f.setOriginalFormsId(f.formsId);
                //  f.setActiveRevision(1);
                formsDao.insertOrReplace(f);
            }
        }
    }

    private static class MigrationV12 implements Migration {
        DaoSession daoSession;

        @Override
        public Integer getVersion() {
            return 19;
        }

        @Override
        public void runMigration(Database db, Context context) {
            daoSession = new DaoMaster(db).newSession();
            try {
                db.execSQL("ALTER TABLE " + UserFormsDao.TABLENAME + " ADD COLUMN " + UserFormsDao.Properties.FormSaveDate.columnName + " INTEGER ");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
        }
    }

    private static class MigrationV16 implements Migration {
        DaoSession daoSession;
        private Context context;

        @Override
        public Integer getVersion() {
            return 27;
        }

        @Override
        public void runMigration(Database db, Context context1) {
            PjDocumentsFoldersDao.createTable(db, false);
            PjDocumentsFilesDao.createTable(db, false);
            daoSession = new DaoMaster(db).newSession();
            makeAllNonSynEntries();
        }

        private void makeAllNonSynEntries() {

            List<PhotosMobile> nonSyncPhotosMobiles = getAllNonSyncPhoto(daoSession);
            TransactionLogMobileDao mPronovosSyncDataDao = daoSession.getTransactionLogMobileDao();
            for (PhotosMobile photosMobile : nonSyncPhotosMobiles) {

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                transactionLogMobile.setUsersId(photosMobile.getUserId());
                transactionLogMobile.setModule(TransactionModuleEnum.PHOTO.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(photosMobile.getPjPhotosIdMobile());
                transactionLogMobile.setServerId(Long.valueOf(photosMobile.getPjPhotosId()));
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }
            List<PhotoFolder> nonSyncPhotosFolder = getAllNonSyncFolder(daoSession);
            for (PhotoFolder photoFolder : nonSyncPhotosFolder) {

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                transactionLogMobile.setUsersId(photoFolder.getUsersId());
                transactionLogMobile.setModule(TransactionModuleEnum.ALBUM.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(photoFolder.getPjPhotosFolderMobileId());
                transactionLogMobile.setServerId(0L);
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }
            List<CrewList> nonSyncCrew = getNotSyncCrewList(daoSession);
            for (CrewList crewList : nonSyncCrew) {

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                transactionLogMobile.setUsersId(crewList.getUsersId());
                transactionLogMobile.setModule(TransactionModuleEnum.CREW.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(crewList.getCrewReportIdMobile());
                transactionLogMobile.setServerId(Long.valueOf(crewList.getCrewReportId()));
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }
            List<WeatherReport> nonSyncWeather = getNotSyncWeatherList(daoSession);
            for (WeatherReport weatherReport : nonSyncWeather) {
                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                transactionLogMobile.setUsersId(weatherReport.getUsersId());
                transactionLogMobile.setModule(TransactionModuleEnum.WEATHER.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(weatherReport.getId());
                transactionLogMobile.setServerId(0L);
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }

//        WorkDetailsRepository workDetailsRepository = new WorkDetailsRepository(daoSession, context);
            List<WorkDetails> notSyncWorkDetails = getNotSyncWorkDetails(daoSession);
            for (WorkDetails workDetails : notSyncWorkDetails) {

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                transactionLogMobile.setUsersId(workDetails.getUsersId());
                transactionLogMobile.setModule(TransactionModuleEnum.WORK_DETAIL.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(workDetails.getWorkDetailsReportIdMobile());
                transactionLogMobile.setServerId(Long.valueOf(workDetails.getWorkDetailsReportId()));
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }
            List<WorkDetailsAttachments> notSyncWorkDetailsAttachments = getNotSyncWorkDetailAttachment(daoSession);
            for (WorkDetailsAttachments workDetailsAttachments : notSyncWorkDetailsAttachments) {

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                transactionLogMobile.setUsersId(workDetailsAttachments.getUsersId());
                transactionLogMobile.setModule(TransactionModuleEnum.WORK_DETAIL_ATTACHMENT.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(workDetailsAttachments.getAttachmentIdMobile());
                transactionLogMobile.setServerId(0L);
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }

//        WorkImpactRepository workImpactRepository = new WorkImpactRepository(daoSession, context);
            List<WorkImpact> notSyncWorkImpact = getNotSyncWorkImpact(daoSession);
            for (WorkImpact workImpact : notSyncWorkImpact) {

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                transactionLogMobile.setUsersId(workImpact.getUsersId());
                transactionLogMobile.setModule(TransactionModuleEnum.WORK_IMPACT.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(workImpact.getWorkImpactReportIdMobile());
                transactionLogMobile.setServerId(Long.valueOf(workImpact.getWorkImpactReportIdMobile()));
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }
            List<WorkImpactAttachments> notSyncWorkImpactAttachments = getNotSyncWorkImpactAttachments(daoSession);
            for (WorkImpactAttachments workImpactAttachments : notSyncWorkImpactAttachments) {

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                transactionLogMobile.setUsersId(workImpactAttachments.getUsersId());
                transactionLogMobile.setModule(TransactionModuleEnum.WORK_IMPACT_ATTACHMENT.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(workImpactAttachments.getAttachmentIdMobile());
                transactionLogMobile.setServerId(0L);
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }

//        PunchListRepository punchListRepository = new PunchListRepository(daoSession, context);
            List<PunchlistDb> notSyncPunchlist = getNotSyncPunchList();
            for (PunchlistDb punchlistDb : notSyncPunchlist) {

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                transactionLogMobile.setUsersId(punchlistDb.getUserId());
                transactionLogMobile.setModule(TransactionModuleEnum.PUNCHLIST.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(punchlistDb.getPunchlistIdMobile());
                transactionLogMobile.setServerId(Long.valueOf(punchlistDb.getPunchlistId()));
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }
            List<PunchListAttachments> notSyncPunchListAttachments = getNotSyncPunchListAttachment();
            for (PunchListAttachments workDetailsAttachments : notSyncPunchListAttachments) {

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                transactionLogMobile.setUsersId(workDetailsAttachments.getUsersId());
                transactionLogMobile.setModule(TransactionModuleEnum.PUNCHLIST_ATTACHMENT.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(workDetailsAttachments.getAttachmentIdMobile());
                transactionLogMobile.setServerId(Long.valueOf(workDetailsAttachments.getAttachmentId()));
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }

            DrawingAnnotationProvider drawingAnnotationProvider = new DrawingAnnotationProvider(null, null, daoSession, null);
            List<DrawingXmls> notSyncXML = drawingAnnotationProvider.getNotSyncDrawingAnnotation();
            for (DrawingXmls drawingXml : notSyncXML) {

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                transactionLogMobile.setUsersId(drawingXml.getUsersId());
                transactionLogMobile.setModule(TransactionModuleEnum.DRAWING_ANNOTATION.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(0L);
                transactionLogMobile.setServerId(Long.valueOf(drawingXml.getDrwDrawingsId()));
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }
        }

        private List<WeatherReport> getNotSyncWeatherList(DaoSession daoSession) {
            List<WeatherReport> weatherReports = daoSession.getWeatherReportDao().queryBuilder().where(
                    WeatherReportDao.Properties.IsSync.eq(false)).list();
            return weatherReports;
        }

        public List<PunchlistDb> getNotSyncPunchList() {
            List<PunchlistDb> punchlistDbs = daoSession.getPunchlistDbDao().queryBuilder().where(
                    PunchlistDbDao.Properties.IsSync.eq(false)).list();
            return punchlistDbs;
        }

        public List<PunchListAttachments> getNotSyncPunchListAttachment() {
            List<PunchListAttachments> punchListAttachments = daoSession.getPunchListAttachmentsDao().queryBuilder().where(
                    PunchListAttachmentsDao.Properties.IsAwsSync.eq(false)).list();
            return punchListAttachments;
        }

        public List<WorkImpact> getNotSyncWorkImpact(DaoSession session) {
            List<WorkImpact> workDetails = session.getWorkImpactDao().queryBuilder()
                    .where(WorkImpactDao.Properties.IsSync.eq(false)).list();
            return workDetails;
        }

        public List<WorkImpactAttachments> getNotSyncWorkImpactAttachments(DaoSession daoSession) {
            List<WorkImpactAttachments> workDetails = daoSession.getWorkImpactAttachmentsDao().queryBuilder()
                    .where(WorkImpactAttachmentsDao.Properties.IsAwsSync.eq(false)).list();
            return workDetails;
        }

        public List<WorkDetailsAttachments> getNotSyncWorkDetailAttachment(DaoSession
                                                                                   daoSession) {

            List<WorkDetailsAttachments> workDetails = daoSession.getWorkDetailsAttachmentsDao().queryBuilder()
                    .where(WorkDetailsAttachmentsDao.Properties.IsAwsSync.eq(false)).list();
            return workDetails;
        }

        public List<WorkDetails> getNotSyncWorkDetails(DaoSession daoSession) {

            List<WorkDetails> workDetails = daoSession.getWorkDetailsDao().queryBuilder()
                    .where(WorkDetailsDao.Properties.IsSync.eq(false)).list();
            return workDetails;
        }

        public List<CrewList> getNotSyncCrewList(DaoSession daoSession) {
            List<CrewList> crewLists = daoSession.getCrewListDao().queryBuilder().where(CrewListDao.Properties.IsSync.eq(false)).limit(1).list();
        /*if (crewLists.size() > 0) {
            return crewLists;
        } else {
            return null;
        }*/
            return crewLists;
        }


        /**
         * Get Folders Added in local database
         *
         * @return
         */
        public List<PhotosMobile> getAllNonSyncPhoto(DaoSession daoSession) {
            Log.e(TAG, "getAllNonSyncPhoto: daoSession =  " + daoSession);
            return daoSession.getPhotosMobileDao().queryBuilder().whereOr(PhotosMobileDao.Properties.IsawsSync.eq(false),
                    PhotosMobileDao.Properties.IsSync.eq(false)).list();
        }

        /**
         * Get Folders Added in local database
         *
         * @return
         */
        public List<PhotoFolder> getAllNonSyncFolder(DaoSession daoSession) {
            List<PhotoFolder> photoFolderList = daoSession.getPhotoFolderDao().queryBuilder()
                    .where(PhotoFolderDao.Properties.IsSync.eq(false)).list();

            return photoFolderList;
        }


    }

    private static class MigrationV15 implements Migration {
        DaoSession daoSession;
        private Context context;

        @Override
        public Integer getVersion() {
            return 26;
        }

        @Override
        public void runMigration(Database db, Context context1) {
            Log.d(TAG, "runMigration: 15 for images sync");
            daoSession = new DaoMaster(db).newSession();
            context = context1;
            try {
                BackupSyncImageFilesDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
                Log.e(TAG, "runMigration: 15 " + e.getMessage());
            }
            LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            //TODO need to change to 22 april
            Date date = new GregorianCalendar(2021, Calendar.JULY, 22).getTime();
            if (loginResponse != null) {
                try {
                    saveAlbumPhotosData(date, loginResponse.getUserDetails().getUsers_id());
                } catch (Exception r) {
                    FirebaseCrashlytics.getInstance().recordException(r);
                    r.printStackTrace();
                }
                try {
                    saveWorkDetailAttachments(date, loginResponse.getUserDetails().getUsers_id());
                } catch (Exception r) {
                    FirebaseCrashlytics.getInstance().recordException(r);
                    r.printStackTrace();
                }
                try {
                    saveWorkImpactsAttachments(date, loginResponse.getUserDetails().getUsers_id());
                } catch (Exception r) {
                    FirebaseCrashlytics.getInstance().recordException(r);
                    r.printStackTrace();
                }
                try {
                    savePunchListImages(date, loginResponse.getUserDetails().getUsers_id());
                } catch (Exception r) {
                    FirebaseCrashlytics.getInstance().recordException(r);
                    r.printStackTrace();
                }
                try {
                    saveProjectShowCaseImage(date, loginResponse.getUserDetails().getUsers_id());

                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    e.printStackTrace();
                }
                try {
                    saveAlbumCoverPhoto(date, loginResponse.getUserDetails().getUsers_id());
                } catch (Exception r) {
                    FirebaseCrashlytics.getInstance().recordException(r);
                    r.printStackTrace();
                }

                try {
                    saveFormsAttachments(date, loginResponse.getUserDetails().getUsers_id());
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    e.printStackTrace();
                }

                try {

                    saveDrawingListImages(date, loginResponse.getUserDetails().getUsers_id());
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    e.printStackTrace();
                }
                try {
                    SharedPref.getInstance(context).writeBooleanPrefs(SharedPref.SYNC_OLD_FILES_REQUIRED, true);
                    SharedPref.getInstance(context).writeBooleanPrefs(SharedPref.SYNC_OLD_FILES_RUNNING, false);

                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    e.printStackTrace();
                }
            }

            clearSession(context);
        }

        private void saveFormsAttachments(Date date, int userId) {

            File myDir = new File(context.getFilesDir().getAbsolutePath() + "/Pronovos/Form/");
            if (myDir.exists()) {
                File[] formAttachmentsFileList = myDir.listFiles();
                if (formAttachmentsFileList != null && formAttachmentsFileList.length > 0) {
                    List<BackupSyncImageFiles> backupSyncImageFilesList = new ArrayList<>();
                    for (File file : formAttachmentsFileList) {
                        // Check if the file is a directory
                        if (file.isDirectory()) {
                            // We will use the directory
                        } else {
                            // We can use .length() to get the file size
                            // and in android file creation date is not an available but you can get the last-modified date
                            Log.d("DBHelper", file.getName() + " (size in bytes: " + file.length() + ")");
                            if (!(file.getName().endsWith(".css") || file.getName().endsWith(".CSS")
                                    || file.getName().endsWith(".JS") || file.getName().endsWith(".js")
                                    || file.getName().endsWith(".HTML") || file.getName().endsWith(".html")
                            ) && file.length() > 1024 && (new Date(file.lastModified())).before(date)) {

                                BackupSyncImageFiles imagefile = new BackupSyncImageFiles();
                                imagefile.setIsSync(false);
                                imagefile.setType(BackupFileTypeEnum.FORM_ATTACHMENTS.toString());
                                imagefile.setLocation("http://d15ydgc8l746ey.cloudfront.net/form_uploads/" + file.getName());
                                imagefile.setName(file.getName());
                                backupSyncImageFilesList.add(imagefile);

                                Log.d(TAG, "saveFormsAttachments: " + file.getName());
                            }
                        }
                    }
                    insertBackupSyncImagesList(backupSyncImageFilesList);
                }
            }



           /* FormAssetsDao formAssetsDao = daoSession.getFormAssetsDao();
            List<FormAssets> formAssetsList = formAssetsDao.queryBuilder()
                    .where(FormAssetsDao.Properties.UpdatedAt.le(date)).list();
            if (formAssetsList != null && formAssetsList.size() > 0) {
                List<BackupSyncImageFiles> backupSyncImageFilesList = new ArrayList<>();
                for (FormAssets formAssets : formAssetsList) {
                    BackupSyncImageFiles imagefile = new BackupSyncImageFiles();
                    imagefile.setIsSync(false);
                    imagefile.setType(BackupFileTypeEnum.FORM_ATTACHMENTS.toString());
                    imagefile.setLocation(formAssets.getFilePath());
                    imagefile.setName(formAssets.getFileName());
                    backupSyncImageFilesList.add(imagefile);
                }
                insertBackupSyncImagesList(backupSyncImageFilesList);
            }*/
        }

        private void savePunchListImages(Date date, int userId) {
            PunchListAttachmentsDao drawingListDao = daoSession.getPunchListAttachmentsDao();
            List<PunchListAttachments> punchListAttachmentsList = null;
            /*drawingListDao.queryBuilder()
                    .where(PunchListAttachmentsDao.Properties.DeletedAt.isNull())
                    .list();*/

            QueryBuilder<PunchListAttachments> qb = drawingListDao.queryBuilder();
            Join<PunchListAttachments, PunchlistDb> join = qb.join(PunchListAttachmentsDao.Properties.PunchListId, PunchlistDb.class,
                    PunchlistDbDao.Properties.PunchlistId);
            qb.where(PunchListAttachmentsDao.Properties.DeletedAt.isNull(),
                    PunchListAttachmentsDao.Properties.UsersId.eq(userId),
                    new WhereCondition.StringCondition(" T.punch_list_id IN "
                            + "(SELECT punch_list_id FROM punchlist where deleted_at IS NULL AND created_at <=  " + date.getTime() + ")"));

            punchListAttachmentsList = qb.list();
            if (punchListAttachmentsList != null && punchListAttachmentsList.size() > 0) {
                List<BackupSyncImageFiles> backupSyncImageFilesList = new ArrayList<>();
                for (PunchListAttachments attachments : punchListAttachmentsList) {
                    if (attachments.getDeletedAt() == null) {
                        BackupSyncImageFiles imagefile = new BackupSyncImageFiles();
                        imagefile.setIsSync(false);
                        imagefile.setType(BackupFileTypeEnum.PUNCHLIST_FILES.toString());
                        imagefile.setLocation(attachments.getAttachmentPath());
                        try {
                            // imagefile.setName(attachments.getAttachmentPath().substring(attachments.getAttachmentPath().lastIndexOf("/" + 1)));
                            String attachmentPath = attachments.getAttachmentPath();
                            URI uri = new URI(attachmentPath);
                            String[] segments = uri.getPath().split("/");
                            String imageName = segments[segments.length - 1];
                            Log.d(TAG, "savePunchListImages: " + attachmentPath + " \n " + imageName);
                            //imagefile.setName(attachments.getAttachmentPath().substring(attachments.getAttachmentPath().lastIndexOf("/" + 1)));
                            imagefile.setName(imageName);

                        } catch (Exception e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                            e.printStackTrace();
                        }
                        backupSyncImageFilesList.add(imagefile);
                    }
                }
                Log.d(TAG, "savePunchListImages: " + backupSyncImageFilesList + " \n size  = " + backupSyncImageFilesList.size());
                insertBackupSyncImagesList(backupSyncImageFilesList);
            }
        }

        private void saveDrawingListImages(Date date, int userId) {
            try {
                Log.d(TAG, "saveDrawingListImages: start");
                DrawingListDao drawingListDao = daoSession.getDrawingListDao();
                List<DrawingList> drawingLists = drawingListDao.queryBuilder()
                        .where(DrawingListDao.Properties.UpdatedAt.le(date),
                                DrawingListDao.Properties.UsersId.eq(userId)).list();

                if (drawingLists != null && drawingLists.size() > 0) {
                    List<BackupSyncImageFiles> backupSyncImageFilesList = new ArrayList<>();
                    for (DrawingList drawingItem : drawingLists) {
                        Log.d(TAG, "saveDrawingListImages: " + drawingItem);
                        try {
                            BackupSyncImageFiles imagefile = new BackupSyncImageFiles();
                            imagefile.setIsSync(false);
                            imagefile.setLocation(drawingItem.getImageThumb());
                            imagefile.setType(BackupFileTypeEnum.DRAWING_LIST_THUMB_IMAGE.toString());

                            String attachmentPath = drawingItem.getImageThumb();
                            URI uri = new URI(attachmentPath);
                            String[] segments = uri.getPath().split("/");
                            String imageName = segments[segments.length - 1];
                            Log.e(TAG, "saveDrawingListImages: " + attachmentPath + " \n " + imageName);
                            //imagefile.setName(attachments.getAttachmentPath().substring(attachments.getAttachmentPath().lastIndexOf("/" + 1)));
                            imagefile.setName(imageName);
                            // imagefile.setName(drawingItem.getImageThumb().substring(drawingItem.getImageThumb().lastIndexOf("/" + 1)));
                            backupSyncImageFilesList.add(imagefile);

                        } catch (Exception e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                            e.printStackTrace();
                        }
                        if (!TextUtils.isEmpty(drawingItem.getImageOrg())) {

                            backupSyncImageFilesList.add(getDrawingOrgBackupSyncImageFiles(drawingItem));
                        }
                        if (!TextUtils.isEmpty(drawingItem.getPdfOrg())) {

                            backupSyncImageFilesList.add(getPdfBackupSyncImageFiles(drawingItem));
                        }
                    }
                    Log.e(TAG, " $$$$$$$$$ saveDrawingListImages: before insert  drawings  " + backupSyncImageFilesList.size() + " \n " + backupSyncImageFilesList);
                    insertBackupSyncImagesList(backupSyncImageFilesList);
                }
            } catch (Exception e) {
                //   FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }

        }

        private BackupSyncImageFiles getPdfBackupSyncImageFiles(DrawingList attachments) {
            BackupSyncImageFiles imagefile = new BackupSyncImageFiles();

            try {
                imagefile.setIsSync(false);
                imagefile.setLocation(attachments.getPdfOrg());
                imagefile.setType(BackupFileTypeEnum.DRAWING_PDF_ORG_FILE.toString());
                try {

                    String attachmentPath = attachments.getPdfOrg();
                    URI uri = new URI(attachmentPath);
                    String[] segments = uri.getPath().split("/");
                    String imageName = segments[segments.length - 1];
                    Log.d(TAG, "getPdfBackupSyncImageFiles: " + attachmentPath + " \n " + imageName);
                    //imagefile.setName(attachments.getAttachmentPath().substring(attachments.getAttachmentPath().lastIndexOf("/" + 1)));
                    imagefile.setName(imageName);
                    //   imagefile.setName(attachments.getPdfOrg().substring(attachments.getPdfOrg().lastIndexOf("/" + 1)));
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    e.printStackTrace();
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            return imagefile;
        }

        private BackupSyncImageFiles getDrawingOrgBackupSyncImageFiles(DrawingList attachments) {
            BackupSyncImageFiles imagefile = new BackupSyncImageFiles();
            imagefile.setIsSync(false);
            imagefile.setLocation(attachments.getImageOrg());
            imagefile.setType(BackupFileTypeEnum.DrawingOrgIMAGE.toString());
            try {
                String attachmentPath = attachments.getImageOrg();
                URI uri = new URI(attachmentPath);
                String[] segments = uri.getPath().split("/");
                String imageName = segments[segments.length - 1];
                Log.d(TAG, "getPdfBackupSyncImageFiles: getImageOrg " + attachmentPath + " \n " + imageName);
                //imagefile.setName(attachments.getAttachmentPath().substring(attachments.getAttachmentPath().lastIndexOf("/" + 1)));
                imagefile.setName(imageName);
                //  imagefile.setName(attachments.getImageOrg().substring(attachments.getImageOrg().lastIndexOf("/" + 1)));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            return imagefile;
        }

        private void saveProjectShowCaseImage(Date date, int userId) {
            PjProjectsDao pjProjectsDao = daoSession.getPjProjectsDao();
            List<PjProjects> pjProjectsList = pjProjectsDao.queryBuilder()
                    .where(PjProjectsDao.Properties.UpdatedAt.le(date)
                            , PjProjectsDao.Properties.UsersId.eq(userId)).list();

            if (pjProjectsList != null && pjProjectsList.size() > 0) {
                List<BackupSyncImageFiles> backupSyncImageFilesList = new ArrayList<>();
                for (PjProjects pjProjects : pjProjectsList) {
                    try {

                        BackupSyncImageFiles imagefile = new BackupSyncImageFiles();
                        imagefile.setIsSync(false);
                        imagefile.setLocation(pjProjects.getShowcasePhoto());
                        imagefile.setType(BackupFileTypeEnum.PROJECT_SHOWCASE_IMAGE.toString());
                        String attachmentPath = pjProjects.getShowcasePhoto();
                        URI uri = new URI(attachmentPath);
                        String[] segments = uri.getPath().split("/");
                        String imageName = segments[segments.length - 1];
                        imagefile.setName(imageName);
                        imagefile.setName(imageName);
                        //  imagefile.setName(pjProjects.getShowcasePhoto().substring(pjProjects.getShowcasePhoto().lastIndexOf("/" + 1)));
                        if (!imageName.equals("pj_showcase_default.png")) {
                            backupSyncImageFilesList.add(imagefile);
                            Log.d(TAG, "saveProjectShowCaseImage:imageName  " + imageName);
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        e.printStackTrace();
                    }

                }
                insertBackupSyncImagesList(backupSyncImageFilesList);
            }
        }


        private void saveAlbumCoverPhoto(Date date, int userId) {
            try {
                AlbumCoverPhotoDao albumCoverPhotoDao = daoSession.getAlbumCoverPhotoDao();

                QueryBuilder<AlbumCoverPhoto> qb = albumCoverPhotoDao.queryBuilder();

                Join<AlbumCoverPhoto, PhotoFolder> join = qb.join(AlbumCoverPhotoDao.Properties.PjPhotosFolderId, PhotoFolder.class,
                        PhotoFolderDao.Properties.PjPhotosFolderId);
                qb.where(
                        AlbumCoverPhotoDao.Properties.UsersId.eq(userId),
                        new WhereCondition.StringCondition(" T.pj_photos_folder_id IN "
                                + "(SELECT pj_photos_folder_id FROM Pj_photos_folders_mobile where deleted_at IS NULL AND updated_at <= " + date.getTime() + ")"));
                List<AlbumCoverPhoto> albumCoverPhotoList = qb.list();
                /*           List<AlbumCoverPhoto> albumCoverPhotoList = albumCoverPhotoDao.queryBuilder().list();
                 * */
                if (albumCoverPhotoList != null && albumCoverPhotoList.size() > 0) {
                    List<BackupSyncImageFiles> backupSyncImageFilesList = new ArrayList<>();
                    for (AlbumCoverPhoto albumCoverPhoto : albumCoverPhotoList) {
                        if (!albumCoverPhoto.getPhotoName().equals("default")) {
                            BackupSyncImageFiles imagefile = new BackupSyncImageFiles();
                            imagefile.setIsSync(false);
                            imagefile.setType(BackupFileTypeEnum.ALBUM_COVER_PHOTO.toString());
                            imagefile.setLocation(albumCoverPhoto.getPhotoLocation());
                            imagefile.setName(albumCoverPhoto.getPhotoName());
                            //if ((albumCoverPhoto.getPhotoLocation().startsWith("http") || albumCoverPhoto.getPhotoLocation().startsWith("https")))
                            backupSyncImageFilesList.add(imagefile);
                        }
                    }
                    insertBackupSyncImagesList(backupSyncImageFilesList);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
        }

        private void saveWorkImpactsAttachments(Date date, int userId) {
            try {
                WorkImpactAttachmentsDao attachmentsDao = daoSession.getWorkImpactAttachmentsDao();
                QueryBuilder<WorkImpactAttachments> qb = attachmentsDao.queryBuilder();

                Join<WorkImpactAttachments, WorkImpact> join = qb.join(WorkImpactAttachmentsDao.Properties.WorkImpactReportId, WorkImpact.class,
                        WorkImpactDao.Properties.WorkImpactReportId);
                qb.where(WorkImpactAttachmentsDao.Properties.DeletedAt.isNull(),
                        WorkImpactAttachmentsDao.Properties.UsersId.eq(userId),
                        new WhereCondition.StringCondition(" T.work_impact_report_id IN "
                                + "(SELECT work_impact_report_id FROM WorkImpact where deleted_at IS NULL AND created_at <= " + date.getTime() + ")"));
                List<WorkImpactAttachments> workImpactAttachmentsList = qb.list();
                if (workImpactAttachmentsList != null && workImpactAttachmentsList.size() > 0) {
                    List<BackupSyncImageFiles> backupSyncImageFilesList = new ArrayList<>();
                    for (WorkImpactAttachments attachments : workImpactAttachmentsList) {
                        try {
                            BackupSyncImageFiles imagefile = new BackupSyncImageFiles();
                            imagefile.setIsSync(false);
                            imagefile.setLocation(attachments.getAttachmentPath());
                            imagefile.setType(BackupFileTypeEnum.WORK_IMPACT_ATTACHMENTS.toString());
                            String attachmentPath = attachments.getAttachmentPath();
                            URI uri = new URI(attachmentPath);
                            String[] segments = uri.getPath().split("/");
                            String imageName = segments[segments.length - 1];
                            Log.d(TAG, "saveWorkImpactsAttachments: " + attachmentPath + " \n " + imageName);
                            imagefile.setName(imageName);
                            backupSyncImageFilesList.add(imagefile);
                        } catch (Exception e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                            e.printStackTrace();
                        }
                    }
                    insertBackupSyncImagesList(backupSyncImageFilesList);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
        }

        private void saveWorkDetailAttachments(Date date, int userId) {
            try {
                WorkDetailsAttachmentsDao attachmentsDao = daoSession.getWorkDetailsAttachmentsDao();
                QueryBuilder<WorkDetailsAttachments> qb = attachmentsDao.queryBuilder();

                Join<WorkDetailsAttachments, WorkDetails> join = qb.join(WorkDetailsAttachmentsDao.Properties.WorkDetailsReportId, WorkDetails.class,
                        WorkDetailsDao.Properties.WorkDetailsReportId);
                qb.where(WorkDetailsAttachmentsDao.Properties.DeletedAt.isNull(),
                        WorkDetailsAttachmentsDao.Properties.UsersId.eq(userId),
                        new WhereCondition.StringCondition(" T.work_details_report_id IN "
                                + "(SELECT work_details_report_id FROM WorkDetails where deleted_at IS NULL AND created_at <= " + date.getTime() + ")"));
                List<WorkDetailsAttachments> workDetailsAttachmentsList = qb.list();

                if (workDetailsAttachmentsList != null && workDetailsAttachmentsList.size() > 0) {
                    List<BackupSyncImageFiles> backupSyncImageFilesList = new ArrayList<>();
                    for (WorkDetailsAttachments attachments : workDetailsAttachmentsList) {
                        try {
                            BackupSyncImageFiles imagefile = new BackupSyncImageFiles();
                            imagefile.setIsSync(false);
                            imagefile.setLocation(attachments.getAttachmentPath());
                            imagefile.setType(BackupFileTypeEnum.WORK_DETAIL_ATTACHMENTS.toString());
                            String attachmentPath = attachments.getAttachmentPath();
                            URI uri = new URI(attachmentPath);
                            String[] segments = uri.getPath().split("/");
                            String imageName = segments[segments.length - 1];
                            Log.d(TAG, "saveWorkDetailAttachments: attachmentPath " + attachmentPath + " \n imageName " + imageName);
                            // imagefile.setName(attachments.getAttachmentPath().substring(attachments.getAttachmentPath().lastIndexOf("/" + 1)));
                            imagefile.setName(imageName);
                            // if ((attachments.getAttachmentPath().startsWith("http") || attachments.getAttachmentPath().startsWith("https")))
                            backupSyncImageFilesList.add(imagefile);
                        } catch (Exception e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                            e.printStackTrace();
                        }
                    }
                    insertBackupSyncImagesList(backupSyncImageFilesList);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
        }

        private void saveAlbumPhotosData(Date date, int userId) {
            PhotosMobileDao photosMobileDao = daoSession.getPhotosMobileDao();
            List<PhotosMobile> albumPhotosList = photosMobileDao.queryBuilder()
                    .where(PhotosMobileDao.Properties.UpdatedAt.le(date),
                            PhotosMobileDao.Properties.UserId.eq(userId),
                            PhotosMobileDao.Properties.DeletedAt.isNull()).list();


            if (albumPhotosList != null && albumPhotosList.size() > 0) {
                List<BackupSyncImageFiles> backupSyncImageFilesList = new ArrayList<>();
                for (PhotosMobile photosMobile : albumPhotosList) {
                    try {

                        BackupSyncImageFiles imagefile = new BackupSyncImageFiles();
                        imagefile.setIsSync(false);
                        imagefile.setLocation(photosMobile.getPhotoLocation());
                        String photoPath = photosMobile.getPhotoLocation();
                        URI uri = new URI(photoPath);
                        String[] segments = uri.getPath().split("/");
                        String imageName = segments[segments.length - 1];
                        imagefile.setName(imageName);

                        // imagefile.setName(photosMobile.getPhotoName());
                        imagefile.setType(BackupFileTypeEnum.PHOTOS.toString());
                        backupSyncImageFilesList.add(imagefile);
                        if (!TextUtils.isEmpty(photosMobile.getPhotoThumb())) {
                            backupSyncImageFilesList.add(getPhotoThumbSyncImage(photosMobile));
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        e.printStackTrace();
                    }

                    Log.d(TAG, "saveAlbumPhotossData " + backupSyncImageFilesList.size());
                }
                insertBackupSyncImagesList(backupSyncImageFilesList);
            }
        }

        private BackupSyncImageFiles getPhotoThumbSyncImage(PhotosMobile photosMobile) {
            BackupSyncImageFiles imagefile = new BackupSyncImageFiles();
            imagefile.setIsSync(false);
            imagefile.setLocation(photosMobile.getPhotoThumb());
            try {
                String attachmentPath = photosMobile.getPhotoThumb();
                URI uri = new URI(attachmentPath);
                String[] segments = uri.getPath().split("/");
                String imageName = segments[segments.length - 1];
                imagefile.setName(imageName);
                Log.d(TAG, "getPhotoThumbSyncImage:imageName  " + imageName);
                //imagefile.setName(photosMobile.getPhotoName());
                //  imagefile.setType("/Pronovos/ThumbImage/");
                imagefile.setType(BackupFileTypeEnum.PHOTOS_THUMB_IMAGE.toString());
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();

            }
            return imagefile;
        }

        private void insertBackupSyncImagesList(List<BackupSyncImageFiles> backupSyncImageFilesList) {
            try {
                if (backupSyncImageFilesList != null && backupSyncImageFilesList.size() > 0) {
                    BackupSyncImageFilesDao backupSyncImageFilesDao = daoSession.getBackupSyncImageFilesDao();
                    backupSyncImageFilesDao.insertOrReplaceInTx(backupSyncImageFilesList);
                    Log.d(TAG, " insertBackupSyncImagesList: backupSyncImageFilesList " + backupSyncImageFilesList.size());
                }
            } catch (Exception e) {
                e.printStackTrace();
                FirebaseCrashlytics.getInstance().recordException(e);
            }
        }
    }

    private static class MigrationV17 implements Migration {

        DaoSession daoSession;

        @Override
        public Integer getVersion() {
            return 28;
        }

        @Override
        public void runMigration(Database db, Context context) {

            PjRfiDao.createTable(db, false);
            PjRfiRepliesDao.createTable(db, false);
            PjRfiSettingsDao.createTable(db, false);
            PjRfiContactListDao.createTable(db, false);
            PjRfiAttachmentsDao.createTable(db, false);
        }

    }

    private static class MigrationV18 implements Migration {

        DaoSession daoSession;

        @Override
        public Integer getVersion() {
            return 29;
        }

        @Override
        public void runMigration(Database db, Context context) {
            db.execSQL("ALTER TABLE " + WorkImpactAttachmentsDao.TABLENAME + " ADD COLUMN " + WorkImpactAttachmentsDao.Properties.Type.columnName + " TEXT DEFAULT 'jpeg' ");
            db.execSQL("ALTER TABLE " + WorkImpactAttachmentsDao.TABLENAME + " ADD COLUMN " + WorkImpactAttachmentsDao.Properties.FileStatus.columnName + " INTEGER ");
            db.execSQL("ALTER TABLE " + WorkDetailsAttachmentsDao.TABLENAME + " ADD COLUMN " + WorkDetailsAttachmentsDao.Properties.Type.columnName + " TEXT DEFAULT 'jpeg' ");
            db.execSQL("ALTER TABLE " + WorkDetailsAttachmentsDao.TABLENAME + " ADD COLUMN " + WorkDetailsAttachmentsDao.Properties.FileStatus.columnName + " INTEGER ");
            db.execSQL("ALTER TABLE " + PunchListAttachmentsDao.TABLENAME + " ADD COLUMN " + PunchListAttachmentsDao.Properties.Type.columnName + " TEXT  DEFAULT 'jpeg'  ");
            db.execSQL("ALTER TABLE " + PunchListAttachmentsDao.TABLENAME + " ADD COLUMN " + PunchListAttachmentsDao.Properties.FileStatus.columnName + " INTEGER ");
        }
    }

    /**
     * Add migration for punch list assigned_cc field.
     * 23 Aug 202
     */
    private static class MigrationV19 implements Migration {

        DaoSession daoSession;

        @Override
        public Integer getVersion() {
            return 30;
        }

        @Override
        public void runMigration(Database db, Context context) {

            db.execSQL("ALTER TABLE " + PunchlistDbDao.TABLENAME + " ADD COLUMN " + PunchlistDbDao.Properties.AssignedCcList.columnName);
            DaoSession daoSession = new DaoMaster(db).newSession();
            List<PunchlistDb> punchlistDbs = daoSession.getPunchlistDbDao().queryBuilder().limit(1).list();
//            db.execSQL("ALTER TABLE " + PunchlistDbDao.TABLENAME + " ADD COLUMN " + PunchlistDbDao.Properties.AssignedCcList.columnName );
            for (PunchlistDb punchlistDb : punchlistDbs) {
                daoSession.getPunchlistDbDao().insertOrReplace(punchlistDb);
            }
        }
    }

    /**
     * Add migration for punch list dicription(comments) field.
     * 12 Sept 2022
     */
    private static class MigrationV20 implements Migration {

        DaoSession daoSession;

        @Override
        public Integer getVersion() {
            return 31;
        }

        @Override
        public void runMigration(Database db, Context context) {
            try {
                db.execSQL("ALTER TABLE " + PunchlistDbDao.TABLENAME + " ADD COLUMN " + PunchlistDbDao.Properties.Comments.columnName);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("Manya", "runMigration: " + Arrays.toString(e.getStackTrace()));
            }
            try {
                PunchListHistoryDbDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                PunchListRejectReasonAttachmentsDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            DaoSession daoSession = new DaoMaster(db).newSession();
            List<PunchlistDb> punchlistDbs = daoSession.getPunchlistDbDao().queryBuilder().limit(1).list();
//            db.execSQL("ALTER TABLE " + PunchlistDbDao.TABLENAME + " ADD COLUMN " + PunchlistDbDao.Properties.AssignedCcList.columnName );
            for (PunchlistDb punchlistDb : punchlistDbs) {
                daoSession.getPunchlistDbDao().insertOrReplace(punchlistDb);
            }
        }
    }


    /**
     * Add migration for assignee and cc defalut selected value.
     * 11 Oct 2022
     */
    private static class MigrationV21 implements Migration {

        DaoSession daoSession;

        @Override
        public Integer getVersion() {
            return 32;
        }

        @Override
        public void runMigration(Database db, Context context) {
            daoSession = new DaoMaster(db).newSession();
            try {
                db.execSQL("ALTER TABLE " + PunchlistAssigneeDao.TABLENAME + " ADD COLUMN " + PunchlistAssigneeDao.Properties.DefaultAssignee.columnName + " BOOLEAN ");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("Manya", "runMigration 21: " + Arrays.toString(e.getStackTrace()));
            }
            try {
                db.execSQL("ALTER TABLE " + PunchlistAssigneeDao.TABLENAME + " ADD COLUMN " + PunchlistAssigneeDao.Properties.DefaultCC.columnName + " BOOLEAN ");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("Manya", "runMigration 21: " + Arrays.toString(e.getStackTrace()));
            }
            try {
                db.execSQL("ALTER TABLE " + UserFormsDao.TABLENAME + " ADD COLUMN " + UserFormsDao.Properties.EmailStatus.columnName + " INTEGER ");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("Manya", "runMigration 21 email status: " + Arrays.toString(e.getStackTrace()));
            }
            updateFormAsset();
            updateForm();

        }

        private void updateForm() {
            List<UserForms> userForms = daoSession.getUserFormsDao().queryBuilder().list();
            for (UserForms userForm : userForms) {
                try {
                    if (userForm.getDateSent() != null) {
                        userForm.setEmailStatus(2);
                    } else {
                        userForm.setEmailStatus(0);
                    }
                    daoSession.getUserFormsDao().update(userForm);
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    e.printStackTrace();
                }

            }
        }

        private void updateFormAsset() {
            List<FormAssets> formAssets = daoSession.getFormAssetsDao().queryBuilder().list();
            for (FormAssets formAsset : formAssets) {
                if (formAsset.getId() == 5 && formAsset.getFileName().equals("jquery-ui.min.js")) {
                    formAsset.setFilePath("https://poc.pronovos.com/assets/newtheme/js/libs/jquery-ui-1.10.3.min.js");
                    daoSession.getFormAssetsDao().update(formAsset);
                }
            }
        }
    }

    private static class MigrationV14 implements Migration {
        DaoSession daoSession;

        @Override
        public Integer getVersion() {
            return 22;
        }

        @Override
        public void runMigration(Database db, Context context) {
            daoSession = new DaoMaster(db).newSession();
            try {
                db.execSQL("ALTER TABLE " + UserFormsDao.TABLENAME + " ADD COLUMN " + UserFormsDao.Properties.PjAreasId.columnName + " INTEGER  ");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                ProjectFormAreaDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {

                db.execSQL("ALTER TABLE " + PhotosMobileDao.TABLENAME + " ADD COLUMN " + PhotosMobileDao.Properties.DeletedAt.columnName + "  INTEGER   ");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }

            CSSJSResponse cssjsResponse = (new Gson().fromJson(getAssetsResponse(), CSSJSResponse.class));
            doUpdateCSSJS(cssjsResponse.getData().getFormAssets());
            DataBaseHelper.copyAssets(context);
        }

        private void doUpdateCSSJS(List<FormAsset> formAssetsList) {
            FormAssetsDao formCategoryDao = daoSession.getFormAssetsDao();
            for (int i = 0; i < formAssetsList.size(); i++) {
                FormAsset formAsset = formAssetsList.get(i);
                FormAssets formAssets = new FormAssets();
                List<FormAssets> formCategories = daoSession.getFormAssetsDao().queryBuilder().where(FormAssetsDao.Properties.FileName.eq(formAsset.getFileName())).limit(1).list();
                if (formCategories.size() > 0) {
                    formAssets = formCategories.get(0);
                }
                formAssets.setFileName(formAsset.getFileName());
                formAssets.setFileType(formAsset.getFileType());
                formAssets.setFilePath(formAsset.getFilePath());
                formAssets.setUpdatedAt(formAsset.getUpdatedAt() != null && !formAsset.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(formAsset.getUpdatedAt()) : null);
                formCategoryDao.insertOrReplace(formAssets);
            }
        }

        public String getAssetsResponse() {
            String str = "";
            if (BuildConfig.BASE_URL.equals(Constants.PRODUCTION)) {
                str = "{\n" +
                        "  \"status\": 200,\n" +
                        "  \"message\": \"Form asset last update.\",\n" +
                        "  \"data\": {\n" +
                        "    \"form_assets\": [\n" +
                        "      {\n" +
                        "        \"file_name\": \"bootstrap.min.css\",\n" +
                        "        \"file_type\": \"css\",\n" +
                        "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/css\\/bootstrap.min.css\",\n" +
                        "        \"updated_at\": \"2020-12-10 11:54:45\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"file_name\": \"formio.full.min.css\",\n" +
                        "        \"file_type\": \"css\",\n" +
                        "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/js\\/form-builder\\/formio.full.min.css\",\n" +
                        "        \"updated_at\": \"2019-11-21 11:54:45\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"file_name\": \"formio.custom.css\",\n" +
                        "        \"file_type\": \"css\",\n" +
                        "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/css\\/formio.custom.css\",\n" +
                        "        \"updated_at\": \"2020-12-10 06:45:02\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"file_name\": \"jquery-2.1.1.min.js\",\n" +
                        "        \"file_type\": \"js\",\n" +
                        "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/js\\/libs\\/jquery-2.1.1.min.js\",\n" +
                        "        \"updated_at\": \"2019-11-21 11:54:45\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"file_name\": \"formio.full.min.js\",\n" +
                        "        \"file_type\": \"js\",\n" +
                        "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/js\\/form-builder\\/formio.full.min.js\",\n" +
                        "        \"updated_at\": \"2020-12-10 10:16:04\"\n" +
                        "      },\n" +

                        " {\n" +
                        " \"file_name\": \"nestedSelect2.css\",\n" +
                        " \"file_type\": \"css\",\n" +
                        "  \"file_path\":\"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/css\\/nestedSelect2.css?ver=1607929196\",\n" +
                        " \"updated_at\": \"2020-12-14 00:27:27\"\n" +
                        " },\n" +
                        " {\n" +
                        " \"file_name\": \"nestedselect2.full.js\",\n" +
                        " \"file_type\": \"js\",\n" +
                        " \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/js\\/plugin\\/nestedSelect2\\/nestedselect2.full.js?ver=1607929196\",\n" +
                        " \"updated_at\": \"2020-12-14 00:45:02\"\n" +
                        " },\n" +
                        "      {\n" +
                        "        \"file_name\": \"custom.project.form.js\",\n" +
                        "        \"file_type\": \"js\",\n" +
                        "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/js\\/form-builder\\/custom.project.form.js\",\n" +
                        "        \"updated_at\": \"2020-12-14 10:16:04\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"file_name\": \"font-awesome.min.css\",\n" +
                        "        \"file_type\": \"css\",\n" +
                        "        \"file_path\": \"\",\n" +
                        "        \"updated_at\": \"2020-03-16 10:16:04\"\n" +
                        "      }\n" +
                        "    ],\n" +
                        "    \"responseCode\": 101,\n" +
                        "    \"responseMsg\": \"Form asset last update.\"\n" +
                        "  }\n" +
                        "}";
            } else {
                str = "{\n" +
                        " \"status\": 200,\n" +
                        " \"message\": \"Form asset last update.\",\n" +
                        " \"data\": {\n" +
                        " \"form_assets\": [\n" +
                        " {\n" +
                        " \"file_name\": \"bootstrap.min.css\",\n" +
                        " \"file_type\": \"css\",\n" +
                        " \"file_path\": \"http:\\/\\/poc.pronovos.com\\/assets\\/newtheme\\/css\\/bootstrap.min.css\",\n" +
                        " \"updated_at\": \"2020-12-12 11:54:45\"\n" +
                        " },\n" +
                        " {\n" +
                        " \"file_name\": \"formio.full.min.css\",\n" +
                        " \"file_type\": \"css\",\n" +
                        " \"file_path\": \"http:\\/\\/poc.pronovos.com\\/assets\\/newtheme\\/js\\/form-builder\\/formio.full.min.css\",\n" +
                        " \"updated_at\": \"2019-11-21 11:54:45\"\n" +
                        " },\n" +
                        " {\n" +
                        " \"file_name\": \"formio.custom.css\",\n" +
                        " \"file_type\": \"css\",\n" +
                        " \"file_path\": \"http:\\/\\/poc.pronovos.com\\/assets\\/newtheme\\/css\\/formio.custom.css\",\n" +
                        " \"updated_at\": \"2020-02-24 06:45:02\"\n" +
                        " },\n" +
                        " {\n" +
                        " \"file_name\": \"jquery-2.1.1.min.js\",\n" +
                        " \"file_type\": \"js\",\n" +
                        " \"file_path\": \"http:\\/\\/poc.pronovos.com\\/assets\\/newtheme\\/js\\/libs\\/jquery-2.1.1.min.js\",\n" +
                        " \"updated_at\": \"2019-11-21 11:54:45\"\n" +
                        " },\n" +
                        " {\n" +
                        " \"file_name\": \"jquery-ui.min.js\",\n" +
                        " \"file_type\": \"js\",\n" +
                        " \"file_path\": \"https:\\/\\/poc.pronovos.com\\/assets\\/newtheme\\/js\\/libs\\/jquery-ui-1.10.3.min.js\",\n" +
                        " \"updated_at\": \"2020-12-11 07:46:57\"\n" +
                        " },\n" +
                        " {\n" +
                        " \"file_name\": \"formio.full.min.js\",\n" +
                        " \"file_type\": \"js\",\n" +
                        " \"file_path\": \"http:\\/\\/poc.pronovos.com\\/assets\\/newtheme\\/js\\/form-builder\\/formio.full.min.js\",\n" +
                        " \"updated_at\": \"2020-12-10 10:57:02\"\n" +
                        " },\n"
                        + " {\n" +
                        " \"file_name\": \"font-awesome.min.css\",\n" +
                        " \"file_type\": \"css\",\n" +
                        "  \"file_path\": \"\",\n" +
                        " \"updated_at\": \"2019-09-05 15:27:27\"\n" +
                        " },\n" +
                        " {\n" +
                        " \"file_name\": \"nestedSelect2.css\",\n" +
                        " \"file_type\": \"css\",\n" +
                        "  \"file_path\": \"\",\n" +
                        " \"updated_at\": \"2020-12-14 00:27:27\"\n" +
                        " },\n" +
                        " {\n" +
                        " \"file_name\": \"nestedselect2.full.js\",\n" +
                        " \"file_type\": \"js\",\n" +
                        " \"file_path\": \"http:\\/\\/poc.pronovos.com\\/assets\\/newtheme\\/js\\/plugin\\/nestedSelect2\\/nestedselect2.full.js\",\n" +
                        " \"updated_at\": \"2020-12-14 00:45:02\"\n" +
                        " },\n" +
                        " {\n" +
                        " \"file_name\": \"custom.project.form.js\",\n" +
                        " \"file_type\": \"js\",\n" +
                        " \"file_path\": \"http:\\/\\/poc.pronovos.com\\/assets\\/newtheme\\/js\\/form-builder\\/custom.project.form.js\",\n" +
                        " \"updated_at\": \"2020-12-14 06:45:02\"\n" +
                        " }\n" +
                        " ],\n" +
                        " \"responseCode\": 101,\n" +
                        " \"responseMsg\": \"Form asset last update.\"\n" +
                        " }\n" +
                        "}";
            }
            return str;
        }


    }

    private static class MigrationV11 implements Migration {

        DaoSession daoSession;

        @Override
        public Integer getVersion() {
            return 18;
        }

        @Override
        public void runMigration(Database db, Context context) {
            daoSession = new DaoMaster(db).newSession();
            String str = "{\n" +
                    "  \"status\": 200,\n" +
                    "  \"message\": \"Form asset last update.\",\n" +
                    "  \"data\": {\n" +
                    "    \"form_assets\": [\n" +
                    "      {\n" +
                    "        \"file_name\": \"bootstrap.min.css\",\n" +
                    "        \"file_type\": \"css\",\n" +
                    "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/css\\/bootstrap.min.css\",\n" +
                    "        \"updated_at\": \"2020-12-10 11:54:45\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"file_name\": \"formio.full.min.css\",\n" +
                    "        \"file_type\": \"css\",\n" +
                    "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/js\\/form-builder\\/formio.full.min.css\",\n" +
                    "        \"updated_at\": \"2019-11-21 11:54:45\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"file_name\": \"formio.custom.css\",\n" +
                    "        \"file_type\": \"css\",\n" +
                    "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/css\\/formio.custom.css\",\n" +
                    "        \"updated_at\": \"2020-12-10 06:45:02\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"file_name\": \"jquery-2.1.1.min.js\",\n" +
                    "        \"file_type\": \"js\",\n" +
                    "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/js\\/libs\\/jquery-2.1.1.min.js\",\n" +
                    "        \"updated_at\": \"2019-11-21 11:54:45\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"file_name\": \"formio.full.min.js\",\n" +
                    "        \"file_type\": \"js\",\n" +
                    "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/js\\/form-builder\\/formio.full.min.js\",\n" +
                    "        \"updated_at\": \"2020-12-10 10:16:04\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"file_name\": \"custom.project.form.js\",\n" +
                    "        \"file_type\": \"js\",\n" +
                    "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/js\\/form-builder\\/custom.project.form.js\",\n" +
                    "        \"updated_at\": \"2020-12-14 10:16:04\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"file_name\": \"jquery-ui.min.js\",\n" +
                    "        \"file_type\": \"js\",\n" +
                    "        \"file_path\": \"\",\n" +
                    "        \"updated_at\": \"2020-01-11 07:46:57\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"file_name\": \"font-awesome.min.css\",\n" +
                    "        \"file_type\": \"css\",\n" +
                    "        \"file_path\": \"\",\n" +
                    "        \"updated_at\": \"2019-09-05 15:27:27\"\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"responseCode\": 101,\n" +
                    "    \"responseMsg\": \"Form asset last update.\"\n" +
                    "  }\n" +
                    "}";
            CSSJSResponse cssjsResponse = (new Gson().fromJson(str, CSSJSResponse.class));
            doUpdateCSSJS(cssjsResponse.getData().getFormAssets());
            DataBaseHelper.copyAssets(context);


        }


        private void doUpdateCSSJS(List<FormAsset> formAssetsList) {
            FormAssetsDao formCategoryDao = daoSession.getFormAssetsDao();
            for (int i = 0; i < formAssetsList.size(); i++) {
                FormAsset formAsset = formAssetsList.get(i);
                FormAssets formAssets = new FormAssets();
                List<FormAssets> formCategories = daoSession.getFormAssetsDao().queryBuilder().where(FormAssetsDao.Properties.FileName.eq(formAsset.getFileName())).limit(1).list();
                if (formCategories.size() > 0) {
                    formAssets = formCategories.get(0);
                }
                formAssets.setFileName(formAsset.getFileName());
                formAssets.setFileType(formAsset.getFileType());
                formAssets.setFilePath(formAsset.getFilePath());
                formAssets.setUpdatedAt(formAsset.getUpdatedAt() != null && !formAsset.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(formAsset.getUpdatedAt()) : null);
                formCategoryDao.insertOrReplace(formAssets);
            }
        }
    }

    private static class MigrationV8 implements Migration {

        DaoSession daoSession;

        @Override
        public Integer getVersion() {
            return 15;
        }

        @Override
        public void runMigration(Database db, Context context) {
            // Add new Punchlist Drawing table
            try {
                PunchlistDrawingDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                db.execSQL("ALTER TABLE " + DrawingListDao.TABLENAME + " ADD COLUMN " + DrawingListDao.Properties.CurrentRevision.columnName + " INTEGER NOT NULL DEFAULT(0)");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                db.execSQL("ALTER TABLE " + UserFormsDao.TABLENAME + " ADD COLUMN " + UserFormsDao.Properties.Publish.columnName + " INTEGER");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                db.execSQL("ALTER TABLE " + FormsDao.TABLENAME + " ADD COLUMN " + FormsDao.Properties.FormSections.columnName + " TEXT");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            daoSession = new DaoMaster(db).newSession();
            updateSyncDrawingFolder(daoSession);
            updateSyncDrawingList(daoSession);
        }

        private void updateSyncDrawingList(DaoSession daoSession) {
            List<DrawingList> drawingFoldersList = daoSession.getDrawingListDao().queryBuilder().list();
            for (DrawingList drawingFolders : drawingFoldersList) {
                drawingFolders.setCurrentRevision(1);
                daoSession.getDrawingListDao().update(drawingFolders);
            }
        }

        private void updateSyncDrawingFolder(DaoSession daoSession) {
            List<DrawingFolders> drawingFoldersList = daoSession.getDrawingFoldersDao().queryBuilder().list();
            for (DrawingFolders drawingFolders : drawingFoldersList) {
                SimpleDateFormat serviceDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date date = serviceDateFormat.parse("1970-01-01 01:01:01");
                    drawingFolders.setLastUpdateXml(date);
                    daoSession.getDrawingFoldersDao().update(drawingFolders);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class MigrationV6 implements Migration {

        DaoSession daoSession;

        @Override
        public Integer getVersion() {
            return 13;
        }

        @Override
        public void runMigration(Database db, Context context) {
            // Add new Forms table
            try {
                FormsDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {

            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                FormsComponentDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                FormCategoryDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                UserFormsDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                ProjectFormDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                FormAssetsDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                FormsScheduleDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                FormImageDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                db.execSQL("ALTER TABLE " + ImageTagDao.TABLENAME + " ADD COLUMN " + ImageTagDao.Properties.TenantId.columnName + " INTEGER");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            if (loginResponse != null && loginResponse.getUserDetails() != null && loginResponse.getUserDetails().getPermissions() != null) {
                List<UserPermissions> userPermissions = loginResponse.getUserDetails().getPermissions();
                userPermissions.get(0).setViewForm(0);
                userPermissions.get(0).setEditForm(0);
                loginResponse.getUserDetails().setPermissions(userPermissions);
            }
            SharedPref.getInstance(context).writePrefs(SharedPref.SESSION_DETAILS, new Gson().toJson(loginResponse));
        }

    }

    private static class MigrationV7 implements Migration {

        DaoSession daoSession;

        @Override
        public Integer getVersion() {
            return 14;
        }

        @Override
        public void runMigration(Database db, Context context) {
            LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

            if (loginResponse != null && loginResponse.getUserDetails() != null && loginResponse.getUserDetails().getPermissions() != null) {

                List<UserPermissions> userPermissions = loginResponse.getUserDetails().getPermissions();
                try {
                    userPermissions.get(0).getViewForm();
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    userPermissions.get(0).setViewForm(0);
                    userPermissions.get(0).setEditForm(0);
                    loginResponse.getUserDetails().setPermissions(userPermissions);
                    SharedPref.getInstance(context).writePrefs(SharedPref.SESSION_DETAILS, new Gson().toJson(loginResponse));
                }
            }
        }
    }

    private static class MigrationV22 implements Migration {

        @Override
        public Integer getVersion() {
            return 33;
        }

        @Override
        public void runMigration(Database db, Context context) {
            // Add new Forms table
            try {
                PjSubmittalsDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                PjSubmittalContactListDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                PjSubmittalAttachmentsDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                PjAssigneeAttachmentsDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
        }

    }

    private static class MigrationV23 implements Migration {

        @Override
        public Integer getVersion() {
            return 34;
        }

        @Override
        public void runMigration(Database db, Context context) {
            // Add new Forms table
            try {
                ImpactsAndRootCauseCacheDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                ProjectIssueImpactsAndCausesCacheDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                ProjectIssuesItemBreakdownCacheDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                ProjectIssuesCacheDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
        }

    }
    private static class MigrationV24 implements Migration {

        @Override
        public Integer getVersion() {
            return 35;
        }

        @Override
        public void runMigration(Database db, Context context) {

            try {
                IssueTrackingSectionCacheDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                IssueTrackingItemsCacheDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                IssueTrackingItemTypesCacheDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                IssueTrackingCustomFieldsCacheDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                db.execSQL("ALTER TABLE " + ProjectIssuesCacheDao.TABLENAME + " ADD COLUMN " + ProjectIssuesCacheDao.Properties.NeedeBy.columnName + " TEXT");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }try {
                db.execSQL("ALTER TABLE " + ProjectIssuesCacheDao.TABLENAME + " ADD COLUMN " + ProjectIssuesCacheDao.Properties.AssigneId.columnName + " INTEGER");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }try {
                db.execSQL("ALTER TABLE " + ProjectIssuesCacheDao.TABLENAME + " ADD COLUMN " + ProjectIssuesCacheDao.Properties.AssigneeName.columnName + " TEXT");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }try {
                db.execSQL("ALTER TABLE " + ProjectIssuesCacheDao.TABLENAME + " ADD COLUMN " + ProjectIssuesCacheDao.Properties.NeededByTimezone.columnName + " TEXT");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
        }

    }

    private static class MigrationV1 implements Migration {

        DaoSession daoSession;

        @Override
        public Integer getVersion() {
            return 7;
        }

        @Override
        public void runMigration(Database db, Context context) {
            // Add new column to user table
            try {
                TransactionLogMobileDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            RegionsTableDao.dropTable(db, true);
            PjProjectsDao.dropTable(db, true);
            try {
                RegionsTableDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
                PjProjectsDao.createTable(db, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            try {
//            db.execSQL("ALTER TABLE " + PjProjectsDao.TABLENAME + " ADD COLUMN " + PjProjectsDao.Properties.ShowcasePhoto.columnName + " TEXT");
                try {
                    db.execSQL("ALTER TABLE " + DrawingFoldersDao.TABLENAME + " ADD COLUMN " + DrawingFoldersDao.Properties.SyncDrawingFolder.columnName + " BOOLEAN");
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    e.printStackTrace();
                }
                try {
                    db.execSQL("ALTER TABLE " + PhotoFolderDao.TABLENAME + " ADD COLUMN " + PhotoFolderDao.Properties.IsStatic.columnName + " INTEGER");
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    e.printStackTrace();
                }
                try {
                    //if(!existsColumnInTable(sqliteDB,CompanyListDao.TABLENAME, CompanyListDao.Properties.IsDeleted.columnName))
                    db.execSQL("ALTER TABLE " + CompanyListDao.TABLENAME + " ADD COLUMN " + CompanyListDao.Properties.IsDeleted.columnName + " INTEGER");
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    e.printStackTrace();
                }
                try {
                    db.execSQL("ALTER TABLE " + PunchlistAssigneeDao.TABLENAME + " ADD COLUMN " + PunchlistAssigneeDao.Properties.UserId.columnName + " INTEGER");
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    e.printStackTrace();
                }
                try {
                    db.execSQL("ALTER TABLE " + PunchlistDbDao.TABLENAME + " ADD COLUMN " + PunchlistDbDao.Properties.IsInProgress.columnName + " BOOLEAN");
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    e.printStackTrace();
                }
                try {
                    db.execSQL("ALTER TABLE " + DrawingXmlsDao.TABLENAME + " ADD COLUMN " + DrawingXmlsDao.Properties.Annotdeletexml.columnName + " TEXT");
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    e.printStackTrace();
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            daoSession = new DaoMaster(db).newSession();
            List<PhotosMobile> nonSyncPhotosMobiles = getAllNonSyncPhoto(daoSession);
            TransactionLogMobileDao mPronovosSyncDataDao = getDaoSession().getTransactionLogMobileDao();
            for (PhotosMobile photosMobile : nonSyncPhotosMobiles) {

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                transactionLogMobile.setUsersId(photosMobile.getUserId());
                transactionLogMobile.setModule(TransactionModuleEnum.PHOTO.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(photosMobile.getPjPhotosIdMobile());
                transactionLogMobile.setServerId(Long.valueOf(photosMobile.getPjPhotosId()));
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }
            List<PhotoFolder> nonSyncPhotosFolder = getAllNonSyncFolder(daoSession);
            for (PhotoFolder photoFolder : nonSyncPhotosFolder) {

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                transactionLogMobile.setUsersId(photoFolder.getUsersId());
                transactionLogMobile.setModule(TransactionModuleEnum.ALBUM.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(photoFolder.getPjPhotosFolderMobileId());
                transactionLogMobile.setServerId(0L);
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }
            List<CrewList> nonSyncCrew = getNotSyncCrewList(daoSession);
            for (CrewList crewList : nonSyncCrew) {

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                transactionLogMobile.setUsersId(crewList.getUsersId());
                transactionLogMobile.setModule(TransactionModuleEnum.CREW.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(crewList.getCrewReportIdMobile());
                transactionLogMobile.setServerId(Long.valueOf(crewList.getCrewReportId()));
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }
            List<WeatherReport> nonSyncWeather = getNotSyncWeatherList(daoSession);
            for (WeatherReport weatherReport : nonSyncWeather) {
                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                transactionLogMobile.setUsersId(weatherReport.getUsersId());
                transactionLogMobile.setModule(TransactionModuleEnum.WEATHER.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(weatherReport.getId());
                transactionLogMobile.setServerId(0L);
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }

//        WorkDetailsRepository workDetailsRepository = new WorkDetailsRepository(daoSession, context);
            List<WorkDetails> notSyncWorkDetails = getNotSyncWorkDetails(daoSession);
            for (WorkDetails workDetails : notSyncWorkDetails) {

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                transactionLogMobile.setUsersId(workDetails.getUsersId());
                transactionLogMobile.setModule(TransactionModuleEnum.WORK_DETAIL.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(workDetails.getWorkDetailsReportIdMobile());
                transactionLogMobile.setServerId(Long.valueOf(workDetails.getWorkDetailsReportId()));
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }
            List<WorkDetailsAttachments> notSyncWorkDetailsAttachments = getNotSyncWorkDetailAttachment(daoSession);
            for (WorkDetailsAttachments workDetailsAttachments : notSyncWorkDetailsAttachments) {

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                transactionLogMobile.setUsersId(workDetailsAttachments.getUsersId());
                transactionLogMobile.setModule(TransactionModuleEnum.WORK_DETAIL_ATTACHMENT.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(workDetailsAttachments.getAttachmentIdMobile());
                transactionLogMobile.setServerId(0L);
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }

//        WorkImpactRepository workImpactRepository = new WorkImpactRepository(daoSession, context);
            List<WorkImpact> notSyncWorkImpact = getNotSyncWorkImpact(daoSession);
            for (WorkImpact workImpact : notSyncWorkImpact) {

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                transactionLogMobile.setUsersId(workImpact.getUsersId());
                transactionLogMobile.setModule(TransactionModuleEnum.WORK_IMPACT.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(workImpact.getWorkImpactReportIdMobile());
                transactionLogMobile.setServerId(Long.valueOf(workImpact.getWorkImpactReportIdMobile()));
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }
            List<WorkImpactAttachments> notSyncWorkImpactAttachments = getNotSyncWorkImpactAttachments(daoSession);
            for (WorkImpactAttachments workImpactAttachments : notSyncWorkImpactAttachments) {

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                transactionLogMobile.setUsersId(workImpactAttachments.getUsersId());
                transactionLogMobile.setModule(TransactionModuleEnum.WORK_IMPACT_ATTACHMENT.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(workImpactAttachments.getAttachmentIdMobile());
                transactionLogMobile.setServerId(0L);
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }

//        PunchListRepository punchListRepository = new PunchListRepository(daoSession, context);
            List<PunchlistDb> notSyncPunchlist = getNotSyncPunchList();
            for (PunchlistDb punchlistDb : notSyncPunchlist) {

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                transactionLogMobile.setUsersId(punchlistDb.getUserId());
                transactionLogMobile.setModule(TransactionModuleEnum.PUNCHLIST.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(punchlistDb.getPunchlistIdMobile());
                transactionLogMobile.setServerId(Long.valueOf(punchlistDb.getPunchlistId()));
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }
            List<PunchListAttachments> notSyncPunchListAttachments = getNotSyncPunchListAttachment();
            for (PunchListAttachments workDetailsAttachments : notSyncPunchListAttachments) {

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                transactionLogMobile.setUsersId(workDetailsAttachments.getUsersId());
                transactionLogMobile.setModule(TransactionModuleEnum.PUNCHLIST_ATTACHMENT.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(workDetailsAttachments.getAttachmentIdMobile());
                transactionLogMobile.setServerId(Long.valueOf(workDetailsAttachments.getAttachmentId()));
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }

            DrawingAnnotationProvider drawingAnnotationProvider = new DrawingAnnotationProvider(null, null, daoSession, null);
            List<DrawingXmls> notSyncXML = drawingAnnotationProvider.getNotSyncDrawingAnnotation();
            for (DrawingXmls drawingXml : notSyncXML) {

                TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
                transactionLogMobile.setUsersId(drawingXml.getUsersId());
                transactionLogMobile.setModule(TransactionModuleEnum.DRAWING_ANNOTATION.ordinal());
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                transactionLogMobile.setMobileId(0L);
                transactionLogMobile.setServerId(Long.valueOf(drawingXml.getDrwDrawingsId()));
                transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
                mPronovosSyncDataDao.save(transactionLogMobile);
            }
        }


        private List<WeatherReport> getNotSyncWeatherList(DaoSession daoSession) {
            List<WeatherReport> weatherReports = getDaoSession().getWeatherReportDao().queryBuilder().where(
                    WeatherReportDao.Properties.IsSync.eq(false)).list();
            return weatherReports;
        }

        private DaoSession getDaoSession() {
            return this.daoSession;
        }

        public List<PunchlistDb> getNotSyncPunchList() {
            List<PunchlistDb> punchlistDbs = getDaoSession().getPunchlistDbDao().queryBuilder().where(
                    PunchlistDbDao.Properties.IsSync.eq(false)).list();
            return punchlistDbs;
        }

        public List<PunchListAttachments> getNotSyncPunchListAttachment() {
            List<PunchListAttachments> punchListAttachments = getDaoSession().getPunchListAttachmentsDao().queryBuilder().where(
                    PunchListAttachmentsDao.Properties.IsAwsSync.eq(false)).list();
            return punchListAttachments;
        }

        public List<WorkImpact> getNotSyncWorkImpact(DaoSession session) {
            List<WorkImpact> workDetails = session.getWorkImpactDao().queryBuilder()
                    .where(WorkImpactDao.Properties.IsSync.eq(false)).list();
            return workDetails;
        }

        public List<WorkImpactAttachments> getNotSyncWorkImpactAttachments(DaoSession daoSession) {
            List<WorkImpactAttachments> workDetails = daoSession.getWorkImpactAttachmentsDao().queryBuilder()
                    .where(WorkImpactAttachmentsDao.Properties.IsAwsSync.eq(false)).list();
            return workDetails;
        }

        public List<WorkDetailsAttachments> getNotSyncWorkDetailAttachment(DaoSession
                                                                                   daoSession) {

            List<WorkDetailsAttachments> workDetails = daoSession.getWorkDetailsAttachmentsDao().queryBuilder()
                    .where(WorkDetailsAttachmentsDao.Properties.IsAwsSync.eq(false)).list();
            return workDetails;
        }

        public List<WorkDetails> getNotSyncWorkDetails(DaoSession daoSession) {

            List<WorkDetails> workDetails = daoSession.getWorkDetailsDao().queryBuilder()
                    .where(WorkDetailsDao.Properties.IsSync.eq(false)).list();
            return workDetails;
        }

        public List<CrewList> getNotSyncCrewList(DaoSession daoSession) {
            List<CrewList> crewLists = daoSession.getCrewListDao().queryBuilder().where(CrewListDao.Properties.IsSync.eq(false)).limit(1).list();
        /*if (crewLists.size() > 0) {
            return crewLists;
        } else {
            return null;
        }*/
            return crewLists;
        }


        /**
         * Get Folders Added in local database
         *
         * @return
         */
        public List<PhotosMobile> getAllNonSyncPhoto(DaoSession daoSession) {

            return daoSession.getPhotosMobileDao().queryBuilder().whereOr(PhotosMobileDao.Properties.IsawsSync.eq(false),
                    PhotosMobileDao.Properties.IsSync.eq(false)).list();
        }

        /**
         * Get Folders Added in local database
         *
         * @return
         */
        public List<PhotoFolder> getAllNonSyncFolder(DaoSession daoSession) {
            List<PhotoFolder> photoFolderList = daoSession.getPhotoFolderDao().queryBuilder()
                    .where(PhotoFolderDao.Properties.IsSync.eq(false)).list();

            return photoFolderList;
        }
    }

    private static class MigrationV10 implements Migration {

        DaoSession daoSession;

        @Override
        public Integer getVersion() {
            return 17;
        }

        @Override
        public void runMigration(Database db, Context context) {
            daoSession = new DaoMaster(db).newSession();
            List<FormAssets> formAssetsList = daoSession.getFormAssetsDao().queryBuilder().where(FormAssetsDao.Properties.FileName.eq("font-awesome.min.css"),
                    FormAssetsDao.Properties.FileType.eq("css")).list();
            if (formAssetsList.size() > 0) {
                FormAssets formAsset = formAssetsList.get(0);
                formAsset.setFilePath("");
                daoSession.getFormAssetsDao().update(formAsset);
            }
            copyAssets(context);
        }

        private void copyAssets(Context context) {
            AssetManager assetManager = context.getAssets();
            String[] files = null;
            try {
                files = assetManager.list("");
            } catch (IOException e) {
                Log.e("tag", "Failed to get asset file list.", e);
            }
            if (files != null) for (String filename : files) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open(filename);
                    File myDir = new File(context.getFilesDir().getAbsolutePath() + "/Pronovos/");//"/PronovosPronovos"
                    if (!myDir.exists()) {
                        myDir.mkdirs();
                    }
                    File outFile = new File(myDir, filename);
                    if (outFile.exists()) {
                        outFile.delete();
                    }
                    out = new FileOutputStream(outFile);
                    DataBaseHelper.copyFile(in, out);
                } catch (IOException e) {
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
// NOOP
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
// NOOP
                        }
                    }
                }
            }
        }
    }

    private static class MigrationV9 implements Migration {

        DaoSession daoSession;

        @Override
        public Integer getVersion() {
            return 16;
        }

        @Override
        public void runMigration(Database db, Context context) {

            String str = "";
            if (BuildConfig.BASE_URL.equals("https://app.pronovos.com/api/v5/")) {
                str = "{\n" +
                        "  \"status\": 200,\n" +
                        "  \"message\": \"Form asset last update.\",\n" +
                        "  \"data\": {\n" +
                        "    \"form_assets\": [\n" +
                        "      {\n" +
                        "        \"file_name\": \"bootstrap.min.css\",\n" +
                        "        \"file_type\": \"css\",\n" +
                        "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/css\\/bootstrap.min.css\",\n" +
                        "        \"updated_at\": \"2020-12-10 11:54:45\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"file_name\": \"formio.full.min.css\",\n" +
                        "        \"file_type\": \"css\",\n" +
                        "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/js\\/form-builder\\/formio.full.min.css\",\n" +
                        "        \"updated_at\": \"2019-11-21 11:54:45\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"file_name\": \"formio.custom.css\",\n" +
                        "        \"file_type\": \"css\",\n" +
                        "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/css\\/formio.custom.css\",\n" +
                        "        \"updated_at\": \"2020-12-10 06:45:02\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"file_name\": \"jquery-2.1.1.min.js\",\n" +
                        "        \"file_type\": \"js\",\n" +
                        "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/js\\/libs\\/jquery-2.1.1.min.js\",\n" +
                        "        \"updated_at\": \"2019-11-21 11:54:45\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"file_name\": \"formio.full.min.js\",\n" +
                        "        \"file_type\": \"js\",\n" +
                        "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/js\\/form-builder\\/formio.full.min.js\",\n" +
                        "        \"updated_at\": \"2020-12-10 10:16:04\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"file_name\": \"custom.project.form.js\",\n" +
                        "        \"file_type\": \"js\",\n" +
                        "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/js\\/form-builder\\/custom.project.form.js\",\n" +
                        "        \"updated_at\": \"2020-12-14 10:16:04\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"file_name\": \"font-awesome.min.css\",\n" +
                        "        \"file_type\": \"css\",\n" +
                        "        \"file_path\": \"\",\n" +
                        "        \"updated_at\": \"2020-03-16 10:16:04\"\n" +
                        "      }\n" +
                        "    ],\n" +
                        "    \"responseCode\": 101,\n" +
                        "    \"responseMsg\": \"Form asset last update.\"\n" +
                        "  }\n" +
                        "}";
            } else {
                str = "{\n" +
                        " \"status\": 200,\n" +
                        " \"message\": \"Form asset last update.\",\n" +
                        " \"data\": {\n" +
                        " \"form_assets\": [\n" +
                        " {\n" +
                        " \"file_name\": \"bootstrap.min.css\",\n" +
                        " \"file_type\": \"css\",\n" +
                        " \"file_path\": \"http:\\/\\/poc.pronovos.com\\/assets\\/newtheme\\/css\\/bootstrap.min.css\",\n" +
                        " \"updated_at\": \"2020-12-12 11:54:45\"\n" +
                        " },\n" +
                        " {\n" +
                        " \"file_name\": \"formio.full.min.css\",\n" +
                        " \"file_type\": \"css\",\n" +
                        " \"file_path\": \"http:\\/\\/poc.pronovos.com\\/assets\\/newtheme\\/js\\/form-builder\\/formio.full.min.css\",\n" +
                        " \"updated_at\": \"2019-11-21 11:54:45\"\n" +
                        " },\n" +
                        " {\n" +
                        " \"file_name\": \"formio.custom.css\",\n" +
                        " \"file_type\": \"css\",\n" +
                        " \"file_path\": \"http:\\/\\/poc.pronovos.com\\/assets\\/newtheme\\/css\\/formio.custom.css\",\n" +
                        " \"updated_at\": \"2020-02-24 06:45:02\"\n" +
                        " },\n" +
                        " {\n" +
                        " \"file_name\": \"jquery-2.1.1.min.js\",\n" +
                        " \"file_type\": \"js\",\n" +
                        " \"file_path\": \"http:\\/\\/poc.pronovos.com\\/assets\\/newtheme\\/js\\/libs\\/jquery-2.1.1.min.js\",\n" +
                        " \"updated_at\": \"2019-11-21 11:54:45\"\n" +
                        " },\n" +
                        " {\n" +
                        " \"file_name\": \"jquery-ui.min.js\",\n" +
                        " \"file_type\": \"js\",\n" +
                        " \"file_path\": \"https:\\/\\/poc.pronovos.com\\/assets\\/newtheme\\/js\\/libs\\/jquery-ui-1.10.3.min.js\",\n" +
                        " \"updated_at\": \"2020-01-11 07:46:57\"\n" +
                        " },\n" +
                        " {\n" +
                        " \"file_name\": \"formio.full.min.js\",\n" +
                        " \"file_type\": \"js\",\n" +
                        " \"file_path\": \"http:\\/\\/poc.pronovos.com\\/assets\\/newtheme\\/js\\/form-builder\\/formio.full.min.js\",\n" +
                        " \"updated_at\": \"2020-12-10 10:57:02\"\n" +
                        " },\n"
                        + " {\n" +
                        " \"file_name\": \"font-awesome.min.css\",\n" +
                        " \"file_type\": \"css\",\n" +
                        "  \"file_path\": \"\",\n" +
                        " \"updated_at\": \"2019-09-05 15:27:27\"\n" +
                        " },\n" +
                        " {\n" +
                        " \"file_name\": \"custom.project.form.js\",\n" +
                        " \"file_type\": \"js\",\n" +
                        " \"file_path\": \"http:\\/\\/poc.pronovos.com\\/assets\\/newtheme\\/js\\/form-builder\\/custom.project.form.js\",\n" +
                        " \"updated_at\": \"2020-12-14 06:45:02\"\n" +
                        " }\n" +
                        " ],\n" +
                        " \"responseCode\": 101,\n" +
                        " \"responseMsg\": \"Form asset last update.\"\n" +
                        " }\n" +
                        "}";
            }

            CSSJSResponse cssjsResponse = (new Gson().fromJson(str, CSSJSResponse.class));
            daoSession = new DaoMaster(db).newSession();
            doUpdateCSSJS(cssjsResponse.getData().getFormAssets());

            copyAssets(context);
        }

        private void doUpdateCSSJS(List<FormAsset> formAssetsList) {
            FormAssetsDao formCategoryDao = daoSession.getFormAssetsDao();
            for (int i = 0; i < formAssetsList.size(); i++) {
                FormAsset formAsset = formAssetsList.get(i);
                FormAssets formAssets = new FormAssets();
                List<FormAssets> formCategories = daoSession.getFormAssetsDao().queryBuilder().where(FormAssetsDao.Properties.FileName.eq(formAsset.getFileName())).limit(1).list();
                if (formCategories.size() > 0) {
                    formAssets = formCategories.get(0);
                }
                formAssets.setFileName(formAsset.getFileName());
                formAssets.setFileType(formAsset.getFileType());
                formAssets.setFilePath(formAsset.getFilePath());
                formAssets.setUpdatedAt(formAsset.getUpdatedAt() != null && !formAsset.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(formAsset.getUpdatedAt()) : null);
                formCategoryDao.insertOrReplace(formAssets);
            }
        }

        private void copyAssets(Context context) {
            AssetManager assetManager = context.getAssets();
            String[] files = null;
            try {
                files = assetManager.list("");
            } catch (IOException e) {
                Log.e("tag", "Failed to get asset file list.", e);
            }
            if (files != null) for (String filename : files) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open(filename);
                    File myDir = new File(context.getFilesDir().getAbsolutePath() + "/Pronovos/");//"/PronovosPronovos"
                    if (!myDir.exists()) {
                        myDir.mkdirs();
                    }
                    File outFile = new File(myDir, filename);
                    if (outFile.exists()) {
                        outFile.delete();
                    }
                    out = new FileOutputStream(outFile);
                    DataBaseHelper.copyFile(in, out);
                } catch (IOException e) {
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
// NOOP
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
// NOOP
                        }
                    }
                }
            }
        }


    }


}
