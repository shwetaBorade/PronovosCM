package com.pronovoscm.utils.backupsync;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.activity.LoginActivity;
import com.pronovoscm.api.FileUploadAPI;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.model.UrlTypeEnum;
import com.pronovoscm.model.request.signurl.SignedUrlRequest;
import com.pronovoscm.model.response.AbstractCallback;
import com.pronovoscm.model.response.ErrorResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.signedurl.SignedUrlResponse;
import com.pronovoscm.persistence.domain.BackupSyncImageFiles;
import com.pronovoscm.persistence.domain.TransactionLogMobile;
import com.pronovoscm.persistence.repository.BackupSyncRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.SharedPref;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class SyncOldFileUtil {

    public static final String PHOTO_THUMB_IMAGE_LOCAL_PATH = "/Pronovos/ThumbImage/";
    public static final String PRONOVOS_BASE_LOCAL_PATH = "/Pronovos/";
    public static final String DRAWING_PDF_LOCAL_PATH = "/Pronovos/PDF/";
    public static final String FORM_ATTACHMENTS_PATH = "/Pronovos/Form/";
    private static final String TAG = "SyncOldFileUtil";
    private static ArrayList<String> uploadUrlList = new ArrayList();
    private static File backUpSyncFile;
    private static File backUpSyncStartFile;

    public static void uploadUnSyncFiles(List<BackupSyncImageFiles> backupSyncImageFilesList,
                                         Context context, ProjectsProvider projectsProvider,
                                         BackupSyncRepository repository,
                                         LoginResponse loginResponse, TransactionLogMobile transactionLogMobile) {

        File myDir = new File(PronovosApplication.getContext().getFilesDir().getAbsolutePath() + "/Pronovos/syncStatus");//"/Pronovos"
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String fname = loginResponse.getUserDetails().getUsers_id() + "_android_backupsync_end.txt";
        String startFilename = loginResponse.getUserDetails().getUsers_id() + "_android_backupsync_start.txt";

        if (backupSyncImageFilesList != null && backupSyncImageFilesList.size() > 0) {
            backUpSyncStartFile = new File(myDir, startFilename);
            backUpSyncFile = new File(myDir, fname);
            // checkUpdateSyncFiles(backupSyncImageFilesList, context, projectsProvider, repository, loginResponse);

            writeStartUploadFile(" User  " + loginResponse.getUserDetails().getFirstname()
                    + " " + loginResponse.getUserDetails().getLastname() + "   " + loginResponse.getUserDetails().getUsers_id(), backupSyncImageFilesList);

            sendSyncStartEvent(context);
            uploadStartSyncFile(backupSyncImageFilesList, context, projectsProvider, repository, loginResponse, transactionLogMobile);
        }

    }

    private static void sendSyncStartEvent(Context context) {
        SharedPref.getInstance(context).writeBooleanPrefs(SharedPref.SYNC_OLD_FILES_RUNNING, true);
        BackupSyncProgressStartUpdate backupSyncProgressStartUpdate = new BackupSyncProgressStartUpdate();
        backupSyncProgressStartUpdate.isSyncStartedRunning = true;
        EventBus.getDefault().post(backupSyncProgressStartUpdate);
    }

    private static void uploadStartSyncFile(List<BackupSyncImageFiles> backupSyncImageFilesList,
                                            Context context, ProjectsProvider projectsProvider,
                                            BackupSyncRepository repository,
                                            LoginResponse loginResponse, TransactionLogMobile transactionLogMobile) {
        BackupSyncUpdateListner backupTextFilelistner = new BackupSyncUpdateListner() {
            @Override
            public void onSyncSuccess(BackupSyncImageFiles backupSyncImageFiles) {
                Log.d(TAG, "backupTextFilelistner onSyncSuccess: ");
                checkUpdateSyncFiles(backupSyncImageFilesList, context, projectsProvider, repository, loginResponse, transactionLogMobile);
            }

            @Override
            public void onSyncFail(BackupSyncImageFiles backupSyncImageFiles) {
                Log.d(TAG, "backupTextFilelistner onSyncFail: ");
                SharedPref.getInstance(context).writeBooleanPrefs(SharedPref.SYNC_OLD_FILES_RUNNING, false);
                sendSyncFail(context, transactionLogMobile, projectsProvider);

            }
        };

        uploadStartBackupFile(projectsProvider, null, backupTextFilelistner, context);
    }

    private static void sendSyncFail(Context context, TransactionLogMobile transactionLogMobile, ProjectsProvider projectsProvider) {
        BackupSyncProgressUpdate backupSyncProgressUpdate = new BackupSyncProgressUpdate();
        backupSyncProgressUpdate.isFailure = true;
        EventBus.getDefault().post(backupSyncProgressUpdate);
        SharedPref.getInstance(context).writeBooleanPrefs(SharedPref.SYNC_OLD_FILES_RUNNING, false);
        projectsProvider.updateSyncFailTransaction(transactionLogMobile);
    }

    private static void checkUpdateSyncFiles(List<BackupSyncImageFiles> backupSyncImageFilesList,
                                             Context context, ProjectsProvider projectsProvider,
                                             BackupSyncRepository repository,
                                             LoginResponse loginResponse, TransactionLogMobile transactionLogMobile
    ) {
        Log.d(TAG, "checkUpdateSyncFiles: " + backupSyncImageFilesList.size());
        BackupSyncUpdateListner backupTextFilelistner = new BackupSyncUpdateListner() {
            @Override
            public void onSyncSuccess(BackupSyncImageFiles backupSyncImageFiles) {
                Log.d(TAG, "backupTextFilelistner onSyncSuccess: ");
            }

            @Override
            public void onSyncFail(BackupSyncImageFiles backupSyncImageFiles) {
                Log.d(TAG, "backupTextFilelistner onSyncFail: ");
                SharedPref.getInstance(context).writeBooleanPrefs(SharedPref.SYNC_OLD_FILES_RUNNING, false);
                sendSyncFail(context, transactionLogMobile, projectsProvider);
            }
        };
        if (backupSyncImageFilesList.size() > 0) {
            BackupSyncUpdateListner listner = new BackupSyncUpdateListner() {

                @Override
                public void onSyncSuccess(BackupSyncImageFiles backupSyncImageFiles) {
                    Log.e(TAG, "onSyncSuccess: removing " + backupSyncImageFiles.getName());
                    backupSyncImageFilesList.remove(backupSyncImageFiles);
                    repository.deleteBackupSyncRecord(backupSyncImageFiles);
                    if (backupSyncImageFilesList.size() > 0) {
                        checkUpdateSyncFiles(backupSyncImageFilesList, context, projectsProvider, repository, loginResponse, transactionLogMobile);
                    } else {
                        BackupSyncImageFiles backUpRecordFile = new BackupSyncImageFiles();
                        backUpRecordFile.setType("backUpSync");
                        backUpRecordFile.setName(backUpSyncFile.getName());
                        uploadBackupFile(projectsProvider, backUpRecordFile, backupTextFilelistner, context);
                        SharedPref.getInstance(context).writeBooleanPrefs(SharedPref.SYNC_OLD_FILES_REQUIRED, false);
                        SharedPref.getInstance(context).writeBooleanPrefs(SharedPref.SYNC_OLD_FILES_RUNNING, false);
                        BackupSyncProgressUpdate backupSyncProgressUpdate = new BackupSyncProgressUpdate();
                        backupSyncProgressUpdate.isFailure = false;
                        backupSyncProgressUpdate.isSYncOldFileDone = true;
                        EventBus.getDefault().post(backupSyncProgressUpdate);

                    }
                }

                @Override
                public void onSyncFail(BackupSyncImageFiles backupSyncImageFiles) {
                    // TODO need to check here for fail case use event bus
                    BackupSyncProgressUpdate backupSyncProgressUpdate = new BackupSyncProgressUpdate();
                    backupSyncProgressUpdate.isFailure = true;
                    SharedPref.getInstance(context).writeBooleanPrefs(SharedPref.SYNC_OLD_FILES_RUNNING, false);
                    projectsProvider.updateSyncFailTransaction(transactionLogMobile);
                    EventBus.getDefault().post(backupSyncProgressUpdate);
                }
            };


            BackupSyncImageFiles backupfile = backupSyncImageFilesList.get(0);

           /* if (!backupfile.getType().equals(BackupFileTypeEnum.PHOTOS.toString())) {
                // repository.deleteBackupSyncRecord(backupfile);
                listner.onSyncSuccess(backupfile);
            }*/
            Log.d(TAG, "checkUpdateSyncFiles: backupfile " + backupfile.toString() + "   \n  " + backupfile.getType());

            if (!(backupfile.getLocation().startsWith("http") || backupfile.getLocation().startsWith("https"))) {
                listner.onSyncSuccess(backupfile);
            } else
                switch (backupfile.getType()) {
                    case "photos": {
                        //BackupFileTypeEnum.PHOTOS.toString(): {
                        if (isFileExist(backupfile.getName(), context, PRONOVOS_BASE_LOCAL_PATH)) {
                            checkUpdatePhotoFileOnAws(backupfile, repository, context, projectsProvider, listner);
                        } else {
                            listner.onSyncSuccess(backupfile);
                        }

                        break;
                    }
                    case "AlbumCoverPhoto": {
                        //BackupFileTypeEnum.ALBUM_COVER_PHOTO: {
                        //   checkUGenericImageFileOnAws(backupfile, repository, context,projectsProvider, listner);
                        checkPhotoThumbExistOnAWS(backupfile, repository, context, projectsProvider, listner, loginResponse);
                        break;
                    }
                    case "/Pronovos/ThumbImage/": {
                        // photo mobile thumb
                        checkPhotoThumbExistOnAWS(backupfile, repository, context, projectsProvider, listner, loginResponse);

                        break;
                    }
                    case "WorkImpactAttachments": {
                        //BackupFileTypeEnum. WORK_IMPACT_ATTACHMENTS: {
                        checkPhotoThumbExistOnAWS(backupfile, repository, context, projectsProvider, listner, loginResponse);
                        break;
                    }
                    case "WorkDetailAttachments": {
                        checkPhotoThumbExistOnAWS(backupfile, repository, context, projectsProvider, listner, loginResponse);
                        break;
                    }
                    case "punchlist_files": {
                        checkPhotoThumbExistOnAWS(backupfile, repository, context, projectsProvider, listner, loginResponse);
                        break;
                    }
                    case "PROJECT_SHOWCASE_IMAGE": {
                        //BackupFileTypeEnum.PROJECT_SHOWCASE_IMAGE: {
                        checkPhotoThumbExistOnAWS(backupfile, repository, context, projectsProvider, listner, loginResponse);
                        break;
                    }
                    case "DRAWING_PDF_ORG_FILE": {
                        //BackupFileTypeEnum.DRAWING_PDF_ORG_FILE: {
                        checkDrawingPdfExistOnAws(backupfile, repository, context, projectsProvider, listner, loginResponse);
                        break;
                    }
                    case "DrawingListIMAGE": {
                        //BackupFileTypeEnum.DRAWING_LIST_THUMB_IMAGE: {
                        // photo mobile thumb
                        checkPhotoThumbExistOnAWS(backupfile, repository, context, projectsProvider, listner, loginResponse);
                        break;
                    }
                    case "DrawingOrgIMAGE": {
                        // BackupFileTypeEnum.DrawingOrgIMAGE: {
                        checkPhotoThumbExistOnAWS(backupfile, repository, context, projectsProvider, listner, loginResponse);
                        break;
                    }
                    case "FORM_ATTACHMENTS": {
                        // BackupFileTypeEnum.FORM_ATTACHMENTS: {
                        //  checkPhotoThumbExistOnAWS(backupfile, repository, context, projectsProvider, listner);
                        checkFormFileExistOnAWS(backupfile, repository, context, projectsProvider, listner, loginResponse);
                        break;
                    }
                }


        }


    }

    private static void checkFormFileExistOnAWS(BackupSyncImageFiles backupSyncImageFiles,
                                                BackupSyncRepository backupSyncRepository,
                                                Context context, ProjectsProvider projectsProvider,
                                                BackupSyncUpdateListner backupSyncUpdateListner, LoginResponse loginResponse) {

        Log.d(TAG, "checkUpdatePhotoFileOnAws: " + backupSyncImageFiles);


        AWSFileListener awsFileListener1 = new AWSFileListener() {
            @Override
            public void onFileExist(BackupSyncImageFiles backupSyncImageFiles) {
                Log.d(TAG, "checkUpdatePhotoFileOnAws  onFileExist: ");
                backupSyncRepository.deleteBackupSyncRecord(backupSyncImageFiles);
                backupSyncUpdateListner.onSyncSuccess(backupSyncImageFiles);
            }

            @Override
            public void onFileDownloadError(BackupSyncImageFiles backupSyncImageFiles) {
                // call upload from here
                if ((
                        backupSyncImageFiles.getType().equals(BackupFileTypeEnum.FORM_ATTACHMENTS.toString())
                ) &&
                        isFileExist(backupSyncImageFiles.getName(), context, FORM_ATTACHMENTS_PATH.toString())) {
                    callGenericAwsSync(projectsProvider, backupSyncImageFiles, backupSyncUpdateListner, context, loginResponse);
                } else {
                    Log.e(TAG, "onFileDownloadError: THUMB File not exist locally");
                    backupSyncRepository.deleteBackupSyncRecord(backupSyncImageFiles);
                    backupSyncUpdateListner.onSyncSuccess(backupSyncImageFiles);
                }
            }


        };
        CheckAWSFileInbackgraound checkAWSFileInbackgraound = new CheckAWSFileInbackgraound(awsFileListener1, backupSyncImageFiles);
        String filePath = context.getFilesDir().getAbsolutePath() + FORM_ATTACHMENTS_PATH;
        String[] params = new String[]{backupSyncImageFiles.getLocation(), filePath};

        checkAWSFileInbackgraound.executeOnExecutor(THREAD_POOL_EXECUTOR, params);

    }

    private static synchronized void checkDrawingPdfExistOnAws(BackupSyncImageFiles backupSyncImageFiles,
                                                               BackupSyncRepository backupSyncRepository,
                                                               Context context, ProjectsProvider projectsProvider,
                                                               BackupSyncUpdateListner backupSyncUpdateListner,
                                                               LoginResponse loginResponse) {


        Log.d(TAG, "checkDrawingPdfExistOnAws: " + backupSyncImageFiles);
        AWSFileListener awsFileListener1 = new AWSFileListener() {
            @Override
            public void onFileExist(BackupSyncImageFiles backupSyncImageFiles) {
                Log.d(TAG, "checkDrawingPdfExistOnAws  onFileExist: ");
                backupSyncRepository.deleteBackupSyncRecord(backupSyncImageFiles);
                backupSyncUpdateListner.onSyncSuccess(backupSyncImageFiles);
            }

            @Override
            public void onFileDownloadError(BackupSyncImageFiles backupSyncImageFiles) {
                // call upload from here
                if (backupSyncImageFiles.getType().equals(BackupFileTypeEnum.DRAWING_PDF_ORG_FILE.toString()) &&
                        isPDFFileExist(backupSyncImageFiles.getName(), context, DRAWING_PDF_LOCAL_PATH, loginResponse)) {
                    Log.e(TAG, " checkDrawingPdfExistOnAws onFileDownloadError: PDF file not exist ");
                    callGenericAwsSync(projectsProvider, backupSyncImageFiles, backupSyncUpdateListner, context, loginResponse);
                } else {
                    Log.e(TAG, "checkDrawingPdfExistOnAws onFileDownloadError: PDF File not exist locally");
                    backupSyncRepository.deleteBackupSyncRecord(backupSyncImageFiles);
                    backupSyncUpdateListner.onSyncSuccess(backupSyncImageFiles);
                }
            }


        };
        CheckAWSFileInbackgraound checkAWSFileInbackgraound = new CheckAWSFileInbackgraound(awsFileListener1, backupSyncImageFiles);
        String filePath = context.getFilesDir().getAbsolutePath() + DRAWING_PDF_LOCAL_PATH;
        String[] params = new String[]{backupSyncImageFiles.getLocation(), filePath};

        checkAWSFileInbackgraound.executeOnExecutor(THREAD_POOL_EXECUTOR, params);


    }
/*    public static synchronized void checkUGenericImageFileOnAws(BackupSyncImageFiles backupSyncImageFiles,
                                                                BackupSyncRepository backupSyncRepository,
                                                                Context context, ProjectsProvider projectsProvider,
                                                                BackupSyncUpdateListner backupSyncUpdateListner) {

        Log.d(TAG, "checkUGenericImageFileOnAws: " + backupSyncImageFiles);
        AWSFileListener awsFileListener1 = new AWSFileListener() {
            @Override
            public void onFileExist(BackupSyncImageFiles backupSyncImageFiles) {
                Log.d(TAG, "checkUGenericImageFileOnAws  onFileExist: ");
                backupSyncRepository.deleteBackupSyncRecord(backupSyncImageFiles);
                backupSyncUpdateListner.onSyncSuccess(backupSyncImageFiles);
            }

            @Override
            public void onFileDownloadError(BackupSyncImageFiles backupSyncImageFiles) {
                // call upload from here
             *//*   if(backupSyncImageFiles.getType().equals(BackupFileTypeEnum.WORK_IMPACT_ATTACHMENTS)){
                   // if( isFileExist(backupSyncImageFiles.getName(),context,PHOTO_IMAGE_LOCAL_PATH))
                 //   callPhotoMobileAwsSync(projectsProvider,backupSyncImageFiles,backupSyncUpdateListner);
                }
                else if(isFileExist(backupSyncImageFiles.getName(),context,PHOTO_THUMB_IMAGE_LOCAL_PATH))
                    /// callGenericAwsSync(projectsProvider,backupSyncImageFiles,backupSyncUpdateListner);
                    //callPhotoMobileAwsSync(projectsProvider,backupSyncImageFiles,backupSyncUpdateListner);
                else{
                    Log.e(TAG, "onFileDownloadError: generic File not exist locally");
                    backupSyncRepository.deleteBackupSyncRecord(backupSyncImageFiles);
                    backupSyncUpdateListner.onSyncSuccess(backupSyncImageFiles);
                }*//*
            }


        };
        CheckAWSFileInbackgraound checkAWSFileInbackgraound = new CheckAWSFileInbackgraound(awsFileListener1, backupSyncImageFiles);
        String filePath = context.getFilesDir().getAbsolutePath() + PHOTO_THUMB_IMAGE_LOCAL_PATH;
        String[] params = new String[]{backupSyncImageFiles.getLocation(), filePath};

        checkAWSFileInbackgraound.executeOnExecutor(THREAD_POOL_EXECUTOR, params);

    }*/

    public static void checkPhotoThumbExistOnAWS(BackupSyncImageFiles backupSyncImageFiles,
                                                 BackupSyncRepository backupSyncRepository,
                                                 Context context, ProjectsProvider projectsProvider,
                                                 BackupSyncUpdateListner backupSyncUpdateListner
            , LoginResponse loginResponse) {
        Log.d(TAG, "checkUpdatePhotoFileOnAws: " + backupSyncImageFiles);
        AWSFileListener awsFileListener1 = new AWSFileListener() {
            @Override
            public void onFileExist(BackupSyncImageFiles backupSyncImageFiles) {
                Log.d(TAG, "checkUpdatePhotoFileOnAws  onFileExist: ");
                backupSyncRepository.deleteBackupSyncRecord(backupSyncImageFiles);
                backupSyncUpdateListner.onSyncSuccess(backupSyncImageFiles);
            }

            @Override
            public void onFileDownloadError(BackupSyncImageFiles backupSyncImageFiles) {
                // call upload from here
                if ((backupSyncImageFiles.getType().equals(BackupFileTypeEnum.PHOTOS_THUMB_IMAGE.toString()) ||
                        backupSyncImageFiles.getType().equals(BackupFileTypeEnum.PROJECT_SHOWCASE_IMAGE.toString()) ||
                        backupSyncImageFiles.getType().equals(BackupFileTypeEnum.ALBUM_COVER_PHOTO.toString())) &&
                        isFileExist(backupSyncImageFiles.getName(), context, PHOTO_THUMB_IMAGE_LOCAL_PATH.toString())) {
                    callGenericAwsSync(projectsProvider, backupSyncImageFiles, backupSyncUpdateListner, context, loginResponse);
                } else if ((
                        backupSyncImageFiles.getType().equals(BackupFileTypeEnum.WORK_IMPACT_ATTACHMENTS.toString())
                                || backupSyncImageFiles.getType().equals(BackupFileTypeEnum.WORK_DETAIL_ATTACHMENTS.toString())
                                || backupSyncImageFiles.getType().equals(BackupFileTypeEnum.DRAWING_LIST_THUMB_IMAGE.toString())
                                || backupSyncImageFiles.getType().equals(BackupFileTypeEnum.PUNCHLIST_FILES.toString())
                ) && isFileExist(backupSyncImageFiles.getName(), context, PRONOVOS_BASE_LOCAL_PATH.toString())) {
                    callGenericAwsSync(projectsProvider, backupSyncImageFiles, backupSyncUpdateListner, context, loginResponse);
                } else if (backupSyncImageFiles.getType().equals(BackupFileTypeEnum.FORM_ATTACHMENTS.toString())
                        && isFileExist(backupSyncImageFiles.getName(), context, FORM_ATTACHMENTS_PATH.toString())) {
                    callGenericAwsSync(projectsProvider, backupSyncImageFiles, backupSyncUpdateListner, context, loginResponse);
                } else if (backupSyncImageFiles.getType().equals(BackupFileTypeEnum.DrawingOrgIMAGE.toString())
                        && isFileExist(backupSyncImageFiles.getName(), context, DRAWING_PDF_LOCAL_PATH.toString())) {
                    callGenericAwsSync(projectsProvider, backupSyncImageFiles, backupSyncUpdateListner, context, loginResponse);
                } else {
                    Log.e(TAG, "onFileDownloadError: THUMB File not exist locally");
                    backupSyncRepository.deleteBackupSyncRecord(backupSyncImageFiles);
                    backupSyncUpdateListner.onSyncSuccess(backupSyncImageFiles);
                }
            }


        };
        CheckAWSFileInbackgraound checkAWSFileInbackgraound = new CheckAWSFileInbackgraound(awsFileListener1, backupSyncImageFiles);
        String filePath = context.getFilesDir().getAbsolutePath() + PHOTO_THUMB_IMAGE_LOCAL_PATH;
        String[] params = new String[]{backupSyncImageFiles.getLocation(), filePath};

        checkAWSFileInbackgraound.executeOnExecutor(THREAD_POOL_EXECUTOR, params);
    }

    private static void onAccessTokenFail(Context context) {

        context.startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        SharedPref.getInstance(context).writePrefs(SharedPref.SESSION_DETAILS, null);
        SharedPref.getInstance(context).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");


    }

    private static void callGenericAwsSync(ProjectsProvider projectsProvider,
                                           BackupSyncImageFiles backupFileRecord,
                                           BackupSyncUpdateListner backupSyncUpdateListner, Context context, LoginResponse loginResponse) {

        SignedUrlRequest signedUrlRequest = null;
        if (backupFileRecord.getType().equals(BackupFileTypeEnum.PHOTOS_THUMB_IMAGE.toString())
                || backupFileRecord.getType().equals(BackupFileTypeEnum.PROJECT_SHOWCASE_IMAGE.toString())

                || backupFileRecord.getType().equals(BackupFileTypeEnum.ALBUM_COVER_PHOTO.toString())) {
            signedUrlRequest = new SignedUrlRequest(UrlTypeEnum.PHOTOS.toString());
        } else if (backupFileRecord.getType().equals(BackupFileTypeEnum.DrawingOrgIMAGE.toString())) {
            signedUrlRequest = new SignedUrlRequest("drawings/drawing_imgs");
        } else if (backupFileRecord.getType().equals(BackupFileTypeEnum.DRAWING_LIST_THUMB_IMAGE.toString())) {
            signedUrlRequest = new SignedUrlRequest("drawings/drawing_thumbs");
        } else if (backupFileRecord.getType().equals(BackupFileTypeEnum.WORK_IMPACT_ATTACHMENTS.toString())
                || backupFileRecord.getType().equals(BackupFileTypeEnum.WORK_DETAIL_ATTACHMENTS.toString())) {
            signedUrlRequest = new SignedUrlRequest(UrlTypeEnum.REPORT_FILES.toString());
        } else if (backupFileRecord.getType().equals(BackupFileTypeEnum.PUNCHLIST_FILES.toString())) {
            signedUrlRequest = new SignedUrlRequest(UrlTypeEnum.PUNCHLIST_FILES.toString());
        } else if (backupFileRecord.getType().equals(BackupFileTypeEnum.DRAWING_PDF_ORG_FILE.toString())) {
            Log.d(TAG, "callGenericAwsSync:DRAWING_PDF_ORG_FILE location =  " + backupFileRecord.getLocation());
            if (backupFileRecord.getLocation().contains("drawings/split_pdfs")) {
                String folderPath = backupFileRecord.getLocation().replace(backupFileRecord.getName(), "");
                folderPath = folderPath.substring(folderPath.indexOf("drawings/split_pdfs"));
                if (folderPath.endsWith("/")) {
                    folderPath = folderPath.substring(0, folderPath.length() - 1);
                }

                signedUrlRequest = new SignedUrlRequest(folderPath);
                Log.e(TAG, "callGenericAwsSync:***************** DRAWING_PDF_ORG_FILE folderPath = " + folderPath);
                Log.e(TAG, "callGenericAwsSync:***************** DRAWING_PDF_ORG_FILE backupFileRecord.getLocation() = " + backupFileRecord.getLocation());
            } else {
                signedUrlRequest = new SignedUrlRequest("drawings");
            }

        } else if (backupFileRecord.getType().equals(BackupFileTypeEnum.FORM_ATTACHMENTS.toString())) {
            signedUrlRequest = new SignedUrlRequest("form_uploads");
        }

        projectsProvider.getSignedUrl(signedUrlRequest, new ProviderResult<SignedUrlResponse>() {
            @Override
            public void success(SignedUrlResponse result) {
                String completeLocalPath = "";
                String awsUrlWiwhoutFilename = backupFileRecord.getLocation().substring(0, backupFileRecord.getLocation().lastIndexOf("/"));
                String awsFolderPath = "";
                if (awsUrlWiwhoutFilename.contains(result.getData().getFormaction().getAction())) {

                    awsFolderPath = awsUrlWiwhoutFilename.replace(result.getData().getFormaction().getAction(), "");
                } else if (awsUrlWiwhoutFilename.contains(".net/")) {
                    awsFolderPath = awsUrlWiwhoutFilename.substring(awsUrlWiwhoutFilename.lastIndexOf(".net/") + 5);
                } else if (awsUrlWiwhoutFilename.contains(".com/")) {
                    awsFolderPath = awsUrlWiwhoutFilename.substring(awsUrlWiwhoutFilename.lastIndexOf(".com/") + 5);
                } else
                    awsFolderPath = awsUrlWiwhoutFilename.substring(awsUrlWiwhoutFilename.lastIndexOf("/"), awsUrlWiwhoutFilename.length());
                Log.e(TAG, "callGenericAwsSync getSignedUrl success: awsFolderPath " + awsFolderPath + "  \n type = " + backupFileRecord.getType());

                String key = result.getData().getFormattributes().getKey();
                // result.getData().getFormattributes().setKey(UrlTypeEnum.PHOTOS.toString() + "/" + key);
                if
                (awsFolderPath.startsWith("/")) {
                    awsFolderPath = awsFolderPath.substring(1);
                }
                result.getData().getFormattributes().setKey(awsFolderPath + "/" + key);

                if (backupFileRecord.getType().equals(BackupFileTypeEnum.PHOTOS.toString())
                        || backupFileRecord.getType().equals(BackupFileTypeEnum.PUNCHLIST_FILES.toString())
                        || backupFileRecord.getType().equals(BackupFileTypeEnum.DRAWING_LIST_THUMB_IMAGE.toString())
                        || backupFileRecord.getType().equals(BackupFileTypeEnum.WORK_IMPACT_ATTACHMENTS.toString())
                        || backupFileRecord.getType().equals(BackupFileTypeEnum.WORK_DETAIL_ATTACHMENTS.toString())) {
                    completeLocalPath = PronovosApplication.getContext().getFilesDir().getAbsolutePath()
                            + PRONOVOS_BASE_LOCAL_PATH + backupFileRecord.getName();
                } else if (backupFileRecord.getType().equals(BackupFileTypeEnum.FORM_ATTACHMENTS.toString())) {
                    completeLocalPath = PronovosApplication.getContext().getFilesDir().getAbsolutePath() + FORM_ATTACHMENTS_PATH + backupFileRecord.getName();
                } else if (backupFileRecord.getType().equals(BackupFileTypeEnum.PHOTOS_THUMB_IMAGE.toString())
                        || backupFileRecord.getType().equals(BackupFileTypeEnum.PROJECT_SHOWCASE_IMAGE.toString())
                        || backupFileRecord.getType().equals(BackupFileTypeEnum.ALBUM_COVER_PHOTO.toString())) {
                    completeLocalPath = PronovosApplication.getContext().getFilesDir().getAbsolutePath()
                            + PHOTO_THUMB_IMAGE_LOCAL_PATH + backupFileRecord.getName();
                } else if (backupFileRecord.getType().equals(BackupFileTypeEnum.DrawingOrgIMAGE.toString())) {
                    completeLocalPath = PronovosApplication.getContext().getFilesDir().getAbsolutePath()
                            + DRAWING_PDF_LOCAL_PATH + backupFileRecord.getName();
                    Log.e("PDF_IMG", " completeLocalPath for pdf file  : " + completeLocalPath);
                } else if (backupFileRecord.getType().equals(BackupFileTypeEnum.DRAWING_PDF_ORG_FILE.toString())) {
                    String userPdfFIleName = loginResponse.getUserDetails().getUsers_id() + backupFileRecord.getName();
                    Log.e("PDF", " completeLocalPath for pdf file  : " + backupFileRecord.getName());
                    if (isFileExist(userPdfFIleName, context, DRAWING_PDF_LOCAL_PATH)) {
                        completeLocalPath = PronovosApplication.getContext().getFilesDir().getAbsolutePath()
                                + DRAWING_PDF_LOCAL_PATH + userPdfFIleName;
                    } else {
                        completeLocalPath = PronovosApplication.getContext().getFilesDir().getAbsolutePath()
                                + DRAWING_PDF_LOCAL_PATH + backupFileRecord.getName();
                    }

                    Log.e("PDF", " completeLocalPath for pdf file  : " + completeLocalPath);
                }


                //  Log.e("UTIL:", "callAwsSync: success " + result.getData().getFormaction().getAction() + "  key =  " + result.getData().getFormattributes().getKey());

                //Log.e("UTIL:", backupFileRecord.getName()+"   ****** callAwsSync: success " + result.getData().toString() + "  completeLocalPath  = " +  completeLocalPath);
                File file = new File(completeLocalPath);

                RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), file);

                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);

                builder.addFormDataPart("key", result.getData().getFormattributes().getKey());
                if (!backupFileRecord.getType().equals(BackupFileTypeEnum.DRAWING_PDF_ORG_FILE.toString())) {
                    builder.addFormDataPart("content-type", "image/jpeg");
                    builder.addFormDataPart("file", file.getName(), reqFile);
                } else {
                    String pdfFileName = file.getName();
                    if (pdfFileName.startsWith(String.valueOf(loginResponse.getUserDetails().getUsers_id()))) {
                        pdfFileName = pdfFileName.replaceFirst(String.valueOf(loginResponse.getUserDetails().getUsers_id()), "");
                    }
                    Log.d(TAG, "UPloding pdf file request pdf file name  = : " + pdfFileName);
                    builder.addFormDataPart("file", pdfFileName, reqFile);
                    builder.addFormDataPart("content-type", "application/pdf");
                }
                builder.addFormDataPart("X-Amz-Credential", result.getData().getFormattributes().getXAmzCredential());
                builder.addFormDataPart("X-Amz-Algorithm", result.getData().getFormattributes().getXAmzAlgorithm());
                builder.addFormDataPart("X-Amz-Date", result.getData().getFormattributes().getXAmzDate());
                builder.addFormDataPart("Policy", result.getData().getFormattributes().getPolicy());
                builder.addFormDataPart("X-Amz-Signature", result.getData().getFormattributes().getXAmzSignature());


                RequestBody finalRequestBody = builder.build();

                // Log.d(TAG, " getSignedUrl success: " + result.getData().getFormaction().getAction());
                // Upload photo to the server
                callUploadFile(result.getData().getFormaction().getAction(), finalRequestBody, backupFileRecord, backupSyncUpdateListner, context);
            }

            @Override
            public void AccessTokenFailure(String message) {
                //   Log.i("dowork:", "callAwsSync: AccessTokenFailure  " + backupFileRecord.getName());
                // onAccessTokenFail(transactionLogMobile, mPronovosSyncData);
                backupSyncUpdateListner.onSyncFail(backupFileRecord);
                SharedPref.getInstance(context).writeBooleanPrefs(SharedPref.SYNC_OLD_FILES_RUNNING, false);
                onAccessTokenFail(context);

            }

            @Override
            public void failure(String message) {
                //   Log.i("dowork:", "callAwsSync: failure" + backupFileRecord.getName());
               /* transactionLogMobile.setStatus(SyncDataEnum.SYNC_FAILED.ordinal());
                mPronovosSyncData.update(transactionLogMobile);*/
                // TODO handle fail
                SharedPref.getInstance(context).writeBooleanPrefs(SharedPref.SYNC_OLD_FILES_RUNNING, false);
                backupSyncUpdateListner.onSyncFail(backupFileRecord);

            }
        });


    }

    public static synchronized void checkUpdatePhotoFileOnAws(BackupSyncImageFiles backupSyncImageFiles,
                                                              BackupSyncRepository backupSyncRepository,
                                                              Context context, ProjectsProvider projectsProvider,
                                                              BackupSyncUpdateListner backupSyncUpdateListner) {
        Log.d(TAG, "checkUpdatePhotoFileOnAws: " + backupSyncImageFiles);
        AWSFileListener awsFileListener = new AWSFileListener() {
            @Override
            public void onFileExist(BackupSyncImageFiles backupSyncImageFiles) {
                //   Log.d(TAG, "checkUpdatePhotoFileOnAws  onFileExist: ");
                backupSyncRepository.deleteBackupSyncRecord(backupSyncImageFiles);
                backupSyncUpdateListner.onSyncSuccess(backupSyncImageFiles);
            }

            @Override
            public void onFileDownloadError(BackupSyncImageFiles backupSyncImageFiles) {
                // call upload from here
                if (isFileExist(backupSyncImageFiles.getName(), context, PRONOVOS_BASE_LOCAL_PATH))
                    callPhotoMobileAwsSync(projectsProvider, backupSyncImageFiles, backupSyncUpdateListner, context);
                else {
                    //  Log.e(TAG, "onFileDownloadError: File not exist locally");
                    backupSyncUpdateListner.onSyncSuccess(backupSyncImageFiles);
                    backupSyncRepository.deleteBackupSyncRecord(backupSyncImageFiles);
                }
            }

        };
        CheckAWSFileInbackgraound checkAWSFileInbackgraound = new CheckAWSFileInbackgraound(awsFileListener, backupSyncImageFiles);
        String filePath = context.getFilesDir().getAbsolutePath() + PRONOVOS_BASE_LOCAL_PATH;
        String[] params = new String[]{backupSyncImageFiles.getLocation(), filePath};

        checkAWSFileInbackgraound.executeOnExecutor(THREAD_POOL_EXECUTOR, params);

    }

    private static void callPhotoMobileAwsSync(ProjectsProvider projectsProvider,
                                               BackupSyncImageFiles backupFileRecord,
                                               BackupSyncUpdateListner backupSyncUpdateListner, Context context) {
        LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse == null) {
            backupSyncUpdateListner.onSyncFail(backupFileRecord);
            SharedPref.getInstance(context).writeBooleanPrefs(SharedPref.SYNC_OLD_FILES_RUNNING, false);
            onAccessTokenFail(context);
        }
        SignedUrlRequest signedUrlRequest;

        if (backupFileRecord.getLocation().contains("report_files/")) {
            signedUrlRequest = new SignedUrlRequest(UrlTypeEnum.REPORT_FILES.toString());
        } else if (backupFileRecord.getLocation().contains("photos/"))
            signedUrlRequest = new SignedUrlRequest(UrlTypeEnum.PHOTOS.toString());
        else {
            signedUrlRequest = new SignedUrlRequest("report_files");
        }
        projectsProvider.getSignedUrl(signedUrlRequest, new ProviderResult<SignedUrlResponse>() {
            @Override
            public void success(SignedUrlResponse result) {
                String completeLocalPath = "";
                String awsUrlWiwhoutFilename = backupFileRecord.getLocation().substring(0, backupFileRecord.getLocation().lastIndexOf("/"));
                String awsFolderPath = "";
                if (awsUrlWiwhoutFilename.contains(result.getData().getFormaction().getAction())) {

                    awsFolderPath = awsUrlWiwhoutFilename.replace(result.getData().getFormaction().getAction(), "");
                } else if (awsUrlWiwhoutFilename.contains(".net/")) {
                    awsFolderPath = awsUrlWiwhoutFilename.substring(awsUrlWiwhoutFilename.lastIndexOf(".net/") + 5);
                } else if (awsUrlWiwhoutFilename.contains(".com/")) {
                    awsFolderPath = awsUrlWiwhoutFilename.substring(awsUrlWiwhoutFilename.lastIndexOf(".com/") + 5);
                } else
                    awsFolderPath = awsUrlWiwhoutFilename.substring(awsUrlWiwhoutFilename.lastIndexOf("/"), awsUrlWiwhoutFilename.length());

                //   Log.d(TAG, "12345678 callPhotoMobileAwsSync getSignedUrl success: awsFolderPath " + awsFolderPath+"  Local PATH "+PronovosApplication.getContext().getFilesDir().getAbsolutePath());
                String key = result.getData().getFormattributes().getKey();
                // result.getData().getFormattributes().setKey(UrlTypeEnum.PHOTOS.toString() + "/" + key);
                result.getData().getFormattributes().setKey(awsFolderPath + "/" + key);

                if (backupFileRecord.getType().equals(BackupFileTypeEnum.PHOTOS.toString()))
                    //   || backupFileRecord.getType().equals(BackupFileTypeEnum.WORK_IMPACT_ATTACHMENTS.toString()) )
                    completeLocalPath = PronovosApplication.getContext().getFilesDir().getAbsolutePath() + PRONOVOS_BASE_LOCAL_PATH + backupFileRecord.getName();
                else if (backupFileRecord.getType().equals(BackupFileTypeEnum.PHOTOS_THUMB_IMAGE.toString())
                        || backupFileRecord.getType().equals(BackupFileTypeEnum.ALBUM_COVER_PHOTO.toString()))
                    completeLocalPath = PronovosApplication.getContext().getFilesDir().getAbsolutePath() + PHOTO_THUMB_IMAGE_LOCAL_PATH
                            + backupFileRecord.getName();


                //   Log.e("UTIL:", "callPhotoMobileAwsSync: success " + result.getData().getFormaction().getAction() + "  key =  " + result.getData().getFormattributes().getKey());
                Log.e("UTIL:", backupFileRecord.getName() + "c ######## callPhotoMobileAwsSync: success completeLocalPath " + completeLocalPath);

                File file = new File(completeLocalPath);
                RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), file);

                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);

                builder.addFormDataPart("key", result.getData().getFormattributes().getKey());
                builder.addFormDataPart("content-type", "image/jpeg");
                builder.addFormDataPart("X-Amz-Credential", result.getData().getFormattributes().getXAmzCredential());
                builder.addFormDataPart("X-Amz-Algorithm", result.getData().getFormattributes().getXAmzAlgorithm());
                builder.addFormDataPart("X-Amz-Date", result.getData().getFormattributes().getXAmzDate());
                builder.addFormDataPart("Policy", result.getData().getFormattributes().getPolicy());
                builder.addFormDataPart("X-Amz-Signature", result.getData().getFormattributes().getXAmzSignature());
                builder.addFormDataPart("file", file.getName(), reqFile);
                RequestBody finalRequestBody = builder.build();

                //  Log.d(TAG, " getSignedUrl success: " + result.getData().getFormaction().getAction());
                // Upload photo to the server
                callUploadFile(result.getData().getFormaction().getAction(), finalRequestBody, backupFileRecord, backupSyncUpdateListner, context);
            }

            @Override
            public void AccessTokenFailure(String message) {
                //   Log.i("dowork:", "callPhotoMobileAwsSync: AccessTokenFailure");
                // onAccessTokenFail(transactionLogMobile, mPronovosSyncData);
                backupSyncUpdateListner.onSyncFail(backupFileRecord);
                SharedPref.getInstance(context).writeBooleanPrefs(SharedPref.SYNC_OLD_FILES_RUNNING, false);
                onAccessTokenFail(context);
            }

            @Override
            public void failure(String message) {
                Log.i("dowork:", "callPhotoMobileAwsSync: failure " + message);
               /* transactionLogMobile.setStatus(SyncDataEnum.SYNC_FAILED.ordinal());
                mPronovosSyncData.update(transactionLogMobile);*/
                // TODO handle fail
                SharedPref.getInstance(context).writeBooleanPrefs(SharedPref.SYNC_OLD_FILES_RUNNING, false);
                backupSyncUpdateListner.onSyncFail(backupFileRecord);

            }
        });
    }

    private static void callUploadFile(String url, RequestBody finalRequestBody, BackupSyncImageFiles photoFile,
                                       BackupSyncUpdateListner backupSyncUpdateListner, Context context) {
        uploadFile(url, finalRequestBody, new ProviderResult<Boolean>() {
            @Override
            public void success(Boolean result) {
                // Log.i("Util:", "uploadFile: uploadFile success");
                if (result) {
                    backupSyncUpdateListner.onSyncSuccess(photoFile);
                } else {
                    Log.d(TAG, " file upload fail: ");
                    backupSyncUpdateListner.onSyncFail(photoFile);

                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                Log.i("dowork:", "uploadFile:    uploadFile AccessTokenFailure");
                //   onAccessTokenFail(transactionLogMobile, mPronovosSyncData);
                // TODO handle fail
                SharedPref.getInstance(context).writeBooleanPrefs(SharedPref.SYNC_OLD_FILES_RUNNING, false);
                backupSyncUpdateListner.onSyncFail(photoFile);
                onAccessTokenFail(context);
            }

            @Override
            public void failure(String message) {
                Log.i("dowork:", "uploadFile:    uploadFile failure");
                backupSyncUpdateListner.onSyncFail(photoFile);
                SharedPref.getInstance(context).writeBooleanPrefs(SharedPref.SYNC_OLD_FILES_RUNNING, false);
            }
        });
    }

    //folder path = "/Pronovos"
    public static boolean isPDFFileExist(String fileName, Context context, String folderPath, LoginResponse loginResponse) {

        File myDir = new File(context.getFilesDir().getAbsolutePath() + folderPath);
        //   Log.d("isFileExist", "  isFileExist: fileName = "+fileName+" \n myDir = "+myDir);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String fname = fileName;
        String userPdfFileName = loginResponse.getUserDetails().getUsers_id() + fileName;
        File file = new File(myDir, fname);
        File userPdfFile = new File(myDir, userPdfFileName);
        if (file.exists() && file.length() > 1000) {
            return true;
        } else if (userPdfFile.exists() && userPdfFile.length() > 1000) {
            return true;
        }
        return false;
    }


    //folder path = "/Pronovos"
    public static boolean isFileExist(String fileName, Context context, String folderPath) {

        File myDir = new File(context.getFilesDir().getAbsolutePath() + folderPath);
        Log.d("isFileExist", "  isFileExist: fileName = " + fileName + " \n myDir = " + myDir);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String fname = fileName;
        File file = new File(myDir, fname);
        if (file.exists() && file.length() > 1000) {
            return true;
        }
        return false;
    }

    /**
     * Service call to get projects according to project request with region id
     */
    public static synchronized void uploadFile(String url, RequestBody options, final ProviderResult<Boolean> updatePhotoAWSResult) {

        if (NetworkService.isNetworkAvailable(PronovosApplication.getContext())) {
            Log.d(TAG, "uploadFile: URL to upload = " + url);
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            //interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().readTimeout(200, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS).addInterceptor(interceptor).build();
            // Set header for API call
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url + "/")
                    .client(client).build();

            FileUploadAPI service = retrofit.create(FileUploadAPI.class);
            Call<ResponseBody> callCreateFile = service.callCreateFile("", options);

            callCreateFile.enqueue(new AbstractCallback<ResponseBody>() {
                @Override
                protected void handleFailure(Call<ResponseBody> call, Throwable throwable) {
                    Log.i("uploadFile", "handleFailure: " + "fail");
                    updatePhotoAWSResult.failure("");
                }

                @Override
                protected void handleError(Call<ResponseBody> call, ErrorResponse errorResponse) {
                    updatePhotoAWSResult.failure(errorResponse.getMessage());
                    Log.e("uploadFile", " #### ##### #### handleFailure: " + " error " + errorResponse.getMessage() + " status code    =" + errorResponse.getStatus());
                }

                @Override
                protected void handleSuccess(Response<ResponseBody> response) {
                    if (response.headers() != null) {
                        Headers uploadResponseHeaders = null;
                        try {
                            uploadResponseHeaders = response.headers();

                            Log.i("File S3", "Uploaded handleSuccess: " + response.headers().get("Location"));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (uploadResponseHeaders != null && uploadResponseHeaders.get("Location") != null) {
                            uploadResponseHeaders.get("Location");
                            writeUploadUrl(uploadResponseHeaders.get("Location"));
                            updatePhotoAWSResult.success(true);
                        } else {
                            updatePhotoAWSResult.failure("");
                        }

                    } else {
                        updatePhotoAWSResult.failure("");
                    }
                }
            });
        } else {
            updatePhotoAWSResult.failure("");

        }

    }


    private static void uploadBackupFile(ProjectsProvider projectsProvider, BackupSyncImageFiles
            backupFileRecord, BackupSyncUpdateListner backupSyncUpdateListner, Context context) {
        SignedUrlRequest signedUrlRequest = new SignedUrlRequest("form_uploads");
        Log.d(TAG, "uploadBackupFile: ");
        projectsProvider.getSignedUrl(signedUrlRequest, new ProviderResult<SignedUrlResponse>() {
            @Override
            public void success(SignedUrlResponse result) {
                //  File file = new File(backUpSyncFile.getAbsolutePath());
                String key = result.getData().getFormattributes().getKey();
                RequestBody reqFile = RequestBody.create(MediaType.parse("text/plain"), backUpSyncFile);
                result.getData().getFormattributes().setKey("form_uploads/backup" + "/" + key);
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);

                builder.addFormDataPart("key", result.getData().getFormattributes().getKey());
                builder.addFormDataPart("content-type", "text/plain");
                builder.addFormDataPart("X-Amz-Credential", result.getData().getFormattributes().getXAmzCredential());
                builder.addFormDataPart("X-Amz-Algorithm", result.getData().getFormattributes().getXAmzAlgorithm());
                builder.addFormDataPart("X-Amz-Date", result.getData().getFormattributes().getXAmzDate());
                builder.addFormDataPart("Policy", result.getData().getFormattributes().getPolicy());
                builder.addFormDataPart("X-Amz-Signature", result.getData().getFormattributes().getXAmzSignature());
                builder.addFormDataPart("file", backUpSyncFile.getName(), reqFile);
                RequestBody finalRequestBody = builder.build();

                Log.d(TAG, " getSignedUrl success: " + result.getData().getFormaction().getAction());
                // Upload photo to the server
                Log.d(TAG, "uploadBackupFile  getSignedUrl success: url =" + result.getData().getFormaction().getAction());
                callUploadFile(result.getData().getFormaction().getAction(), finalRequestBody, backupFileRecord, backupSyncUpdateListner, context);
            }

            @Override
            public void AccessTokenFailure(String message) {
                backupSyncUpdateListner.onSyncFail(backupFileRecord);
                onAccessTokenFail(context);
            }

            @Override
            public void failure(String message) {
                backupSyncUpdateListner.onSyncFail(backupFileRecord);
                SharedPref.getInstance(context).writeBooleanPrefs(SharedPref.SYNC_OLD_FILES_RUNNING, false);
            }
        });
    }

    private static void uploadStartBackupFile(ProjectsProvider projectsProvider, BackupSyncImageFiles backupFileRecord,
                                              BackupSyncUpdateListner backupSyncUpdateListner, Context context) {
        SignedUrlRequest signedUrlRequest = new SignedUrlRequest("form_uploads");
        Log.d(TAG, "uploadStartBackupFile: ");
        projectsProvider.getSignedUrl(signedUrlRequest, new ProviderResult<SignedUrlResponse>() {
            @Override
            public void success(SignedUrlResponse result) {
                // File file = new File(backUpSyncStartFile.getAbsolutePath());
                Log.d(TAG, "uploadStartBackupFile getSignedUrl  : " + backUpSyncStartFile.getName() + " absolutePath =   " + backUpSyncStartFile.getAbsolutePath());
                String key = result.getData().getFormattributes().getKey();
                RequestBody reqFile = RequestBody.create(MediaType.parse("text/plain"), backUpSyncStartFile);
                result.getData().getFormattributes().setKey("form_uploads/backup" + "/" + key);
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);

                builder.addFormDataPart("key", result.getData().getFormattributes().getKey());
                builder.addFormDataPart("content-type", "text/plain");
                builder.addFormDataPart("X-Amz-Credential", result.getData().getFormattributes().getXAmzCredential());
                builder.addFormDataPart("X-Amz-Algorithm", result.getData().getFormattributes().getXAmzAlgorithm());
                builder.addFormDataPart("X-Amz-Date", result.getData().getFormattributes().getXAmzDate());
                builder.addFormDataPart("Policy", result.getData().getFormattributes().getPolicy());
                builder.addFormDataPart("X-Amz-Signature", result.getData().getFormattributes().getXAmzSignature());
                builder.addFormDataPart("file", backUpSyncStartFile.getName(), reqFile);
                RequestBody finalRequestBody = builder.build();

                Log.d(TAG, " getSignedUrl success: " + result.getData().getFormaction().getAction());
                // Upload photo to the server
                Log.d(TAG, "uploadStartBackupFile  getSignedUrl success: url =" + result.getData().getFormaction().getAction());
                callUploadFile(result.getData().getFormaction().getAction(), finalRequestBody, backupFileRecord, backupSyncUpdateListner, context);
            }

            @Override
            public void AccessTokenFailure(String message) {
                backupSyncUpdateListner.onSyncFail(backupFileRecord);
                onAccessTokenFail(context);
            }

            @Override
            public void failure(String message) {
                backupSyncUpdateListner.onSyncFail(backupFileRecord);
                SharedPref.getInstance(context).writeBooleanPrefs(SharedPref.SYNC_OLD_FILES_RUNNING, false);
            }
        });
    }

    private static void writeUploadUrl(String urlLocation) {
        try {

            FileOutputStream fOut = new FileOutputStream(backUpSyncFile, true);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fOut));
            {
                bw.write(urlLocation);
                bw.newLine();
            }
            bw.close();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeStartUploadFile(String urlLocation, List<BackupSyncImageFiles> filesList) {
        try {
            FileOutputStream fOut = new FileOutputStream(backUpSyncStartFile);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fOut));
            {
                bw.write(urlLocation);
                bw.newLine();
            }
            for (BackupSyncImageFiles file : filesList) {
                bw.write(file.getLocation());
                bw.newLine();
            }
            bw.close();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface BackupSyncUpdateListner {
        void onSyncSuccess(BackupSyncImageFiles backupSyncImageFiles);

        void onSyncFail(BackupSyncImageFiles backupSyncImageFiles);
    }

    public interface AWSFileListener {
        void onFileExist(final BackupSyncImageFiles backupSyncImageFiles);

        void onFileDownloadError(BackupSyncImageFiles backupSyncImageFiles);
    }
}
