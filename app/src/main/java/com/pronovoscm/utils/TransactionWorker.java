package com.pronovoscm.utils;

import static com.pronovoscm.model.TransactionModuleEnum.PUNCHLIST;
import static com.pronovoscm.model.TransactionModuleEnum.PUNCHLIST_HISTORY;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.activity.LoginActivity;
import com.pronovoscm.data.CrewReportProvider;
import com.pronovoscm.data.FileUploadProvider;
import com.pronovoscm.data.ProjectFormProvider;
import com.pronovoscm.data.issuetracking.ProjectIssueTrackingProvider;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.data.PunchListProvider;
import com.pronovoscm.data.UpdatePhotoDetailsProvider;
import com.pronovoscm.data.WeatherReportProvider;
import com.pronovoscm.data.WorkDetailsProvider;
import com.pronovoscm.data.WorkImpactProvider;
import com.pronovoscm.model.SyncDataEnum;
import com.pronovoscm.model.TransactionLogUpdate;
import com.pronovoscm.model.TransactionModuleEnum;
import com.pronovoscm.model.UrlTypeEnum;
import com.pronovoscm.model.request.albums.Album;
import com.pronovoscm.model.request.albums.AlbumRequest;
import com.pronovoscm.model.request.crewreport.CrewReport;
import com.pronovoscm.model.request.crewreport.CrewReportRequest;
import com.pronovoscm.model.request.photo.Photo;
import com.pronovoscm.model.request.photo.PhotoRequest;
import com.pronovoscm.model.request.punchlist.Attachments;
import com.pronovoscm.model.request.punchlist.PunchList;
import com.pronovoscm.model.request.punchlist.PunchListHistory;
import com.pronovoscm.model.request.punchlist.PunchListHistoryRequest;
import com.pronovoscm.model.request.punchlist.PunchListRequest;
import com.pronovoscm.model.request.signurl.SignedUrlRequest;
import com.pronovoscm.model.request.submitform.Submission;
import com.pronovoscm.model.request.submitform.SubmitFormRequest;
import com.pronovoscm.model.request.updatephoto.Photo_tags;
import com.pronovoscm.model.request.updatephoto.UpdatePhotoDetail;
import com.pronovoscm.model.request.weatherreport.WeatherReportRequest;
import com.pronovoscm.model.request.weatherreport.WeatherReports;
import com.pronovoscm.model.request.workdetails.WorkDetailsReportRequest;
import com.pronovoscm.model.request.workdetails.WorkDetailsRequest;
import com.pronovoscm.model.request.workimpact.WorkImpactRequest;
import com.pronovoscm.model.request.workimpact.WorkImpactsReportRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.signedurl.SignedUrlResponse;
import com.pronovoscm.model.response.updatephoto.UpdatePhotoDetailResponse;
import com.pronovoscm.model.response.uploadformfile.UploadFile;
import com.pronovoscm.persistence.domain.BackupSyncImageFiles;
import com.pronovoscm.persistence.domain.CrewList;
import com.pronovoscm.persistence.domain.FormImage;
import com.pronovoscm.persistence.domain.PhotoFolder;
import com.pronovoscm.persistence.domain.PhotosMobile;
import com.pronovoscm.persistence.domain.PunchListAttachments;
import com.pronovoscm.persistence.domain.PunchlistDb;
import com.pronovoscm.persistence.domain.Taggables;
import com.pronovoscm.persistence.domain.TaggablesDao;
import com.pronovoscm.persistence.domain.TransactionLogMobile;
import com.pronovoscm.persistence.domain.TransactionLogMobileDao;
import com.pronovoscm.persistence.domain.UserForms;
import com.pronovoscm.persistence.domain.WeatherReport;
import com.pronovoscm.persistence.domain.WorkDetails;
import com.pronovoscm.persistence.domain.WorkDetailsAttachments;
import com.pronovoscm.persistence.domain.WorkImpact;
import com.pronovoscm.persistence.domain.WorkImpactAttachments;
import com.pronovoscm.persistence.domain.punchlist.PunchListHistoryDb;
import com.pronovoscm.persistence.domain.punchlist.PunchListRejectReasonAttachments;
import com.pronovoscm.persistence.repository.BackupSyncRepository;
import com.pronovoscm.persistence.repository.CrewReportRepository;
import com.pronovoscm.persistence.repository.ProjectFormRepository;
import com.pronovoscm.persistence.repository.PunchListRepository;
import com.pronovoscm.persistence.repository.WeatherReportRepository;
import com.pronovoscm.persistence.repository.WorkDetailsRepository;
import com.pronovoscm.persistence.repository.WorkImpactRepository;
import com.pronovoscm.utils.backupsync.BackupSyncProgressUpdate;
import com.pronovoscm.utils.backupsync.SyncOldFileUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.DeleteQuery;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * It is responsible for the central controller process.
 */
public class TransactionWorker extends Worker {

    private static final String TAG = TransactionWorker.class.getSimpleName();
    /**
     * Provider for album and photo.
     */
    @Inject
    ProjectsProvider projectsProvider;

    /**
     * It is used for CRUD operations of crew.
     */
    @Inject
    CrewReportRepository crewReportRepository;

    /**
     * Provider for crew api call.
     */
    @Inject
    CrewReportProvider mCrewReportProvider;

    /**
     * Provider for update photo.
     */
    @Inject
    UpdatePhotoDetailsProvider mUpdatePhotoDetailsProvider;

    /**
     * Provider for upload image file to AWS server.
     */
    @Inject
    FileUploadProvider mFileUploadProvider;

    /**
     * It is used for CRUD operations of weather.
     */
    @Inject
    WeatherReportRepository mWeatherReportRepository;

    /**
     * Provider for weather api call.
     */
    @Inject
    WeatherReportProvider mWeatherReportProvider;

    /**
     * Provider for punch list api call.
     */
    @Inject
    PunchListProvider mPunchListProvider;

    /**
     * It is used for CRUD operations of punch list.
     */
    @Inject
    PunchListRepository mPunchListRepository;

    /**
     * It is used for CRUD operations of work details.
     */
    @Inject
    WorkDetailsRepository mWorkDetailsRepository;

    /**
     * Provider for work details api call.
     */
    @Inject
    WorkDetailsProvider mWorkDetailsProvider;
    /**
     * It is used for CRUD operations of work impacts.
     */
    @Inject
    WorkImpactRepository mWorkImpactRepository;
    /**
     * Provider for work impacts api call.
     */
    @Inject
    WorkImpactProvider mWorkImpactProvider;
    /**
     * It is used for CRUD operations of Project forms.
     */
    @Inject
    ProjectFormRepository mprojectFormRepository;

    @Inject
    BackupSyncRepository mBackupSyncRepository;

    @Inject
    ProjectFormProvider mprojectFormProvider;

    @Inject
    ProjectIssueTrackingProvider projectIssueTrackingProvider;
    /**
     * It is an enum for different modules.
     */
    private TransactionModuleEnum[] enumVals;

    private final int isHistoryInprogressCount = 0;

    private final boolean isPunchListSyn = false;

    public TransactionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        enumVals = TransactionModuleEnum.values();
        ((PronovosApplication) (getApplicationContext())).getDaggerComponent().inject(this);
        LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(getApplicationContext()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            TransactionLogMobileDao mPronovosSyncData = ((PronovosApplication) getApplicationContext()).getDaoSession().getTransactionLogMobileDao();
            int userId = loginResponse.getUserDetails().getUsers_id();
            List<TransactionLogMobile> transactionLogMobileList = ((PronovosApplication) getApplicationContext()).getDaoSession().
                    getTransactionLogMobileDao().queryBuilder().where(
                            TransactionLogMobileDao.Properties.Status.eq(SyncDataEnum.NOTSYNC.ordinal()), TransactionLogMobileDao.Properties.UsersId.eq(userId)).list();
            if (transactionLogMobileList.size() == 0) {
                transactionLogMobileList = ((PronovosApplication) getApplicationContext()).getDaoSession().getTransactionLogMobileDao().queryBuilder().where(
                        TransactionLogMobileDao.Properties.Status.eq(SyncDataEnum.SYNC_FAILED.ordinal()), TransactionLogMobileDao.Properties.UsersId.eq(userId)).list();
                Log.i(TAG, "Transaction SYNC_FAILED   Worker worker Start" + transactionLogMobileList.size());
            }

            for (int i = 0; i < transactionLogMobileList.size(); i++) {
                callAPI(transactionLogMobileList.get(i), mPronovosSyncData, loginResponse);
            }
            boolean isSyncRunning = SharedPref.getInstance(getApplicationContext()).readBooleanPrefs(SharedPref.SYNC_OLD_FILES_RUNNING);
            if (transactionLogMobileList.size() == 0 && isSyncRunning == true) {
                Log.e(TAG, "Transaction SYNC_ last status    Worker worker Start" + transactionLogMobileList.size());
                TransactionLogMobile transactionLogMobile = projectsProvider.getSyncTransactionLogMobile(userId);
                if (transactionLogMobile != null)
                    callAPI(transactionLogMobile, mPronovosSyncData, loginResponse);
            }
        }
        return Result.success();
    }

    /**
     * To call API's according to different modules.
     *
     * @param transactionLogMobile It determines API call's.
     * @param mPronovosSyncData    used to update data after getting api response.
     * @param loginResponse
     */
    private void callAPI(TransactionLogMobile transactionLogMobile, TransactionLogMobileDao mPronovosSyncData, LoginResponse loginResponse) {
        Log.i(TAG, "callAPI: " + transactionLogMobile.getModule() + "  sync status " + transactionLogMobile.getStatus());
//        if (PUNCHLIST.ordinal() == transactionLogMobile.getModule()) {
        transactionLogMobile.setStatus(SyncDataEnum.PROCESSING.ordinal());
        mPronovosSyncData.update(transactionLogMobile);
//        }
        switch (enumVals[transactionLogMobile.getModule()]) {
            case ALBUM:
                //  Log.i(TAG, "Transaction Worker album: start");
                albumAPICall(transactionLogMobile, mPronovosSyncData);
                break;
            case PHOTO_DELETE: {
                //  Log.i(TAG, "Transaction Worker photoApiCallForDelete: start");
                photoApiCallForDelete(transactionLogMobile, mPronovosSyncData);
            }
            case PHOTO:
                //  Log.i(TAG, "Transaction Worker photoAPICall: start");
                photoAPICall(transactionLogMobile, mPronovosSyncData);
                break;

            case CREW:
                CrewList crewList = crewReportRepository.getCrewDetail(transactionLogMobile.getUsersId(), transactionLogMobile.getMobileId(), transactionLogMobile.getServerId());
                if (crewList != null) {
                    crewAPICall(transactionLogMobile, mPronovosSyncData, crewList);
                } else {
                    deleteSyncData(transactionLogMobile);
                }
                break;

            case WEATHER:
                weatherAPICall(transactionLogMobile, mPronovosSyncData);
                break;

            case PUNCHLIST_ATTACHMENT:
                punchlistAttachmentAPICall(transactionLogMobile, mPronovosSyncData);
                break;

            case PUNCHLIST:
                PunchlistDb punchlistdb = mPunchListRepository.getPunchlistDetail(transactionLogMobile.getUsersId(), transactionLogMobile.getMobileId(), transactionLogMobile.getServerId());
                Log.d(TAG, "Punchlist status call : " + ((punchlistdb != null) ? punchlistdb.getStatus() : "null"));
                if (punchlistdb != null && mPunchListRepository.getNonSyncedPunchListAttachments(punchlistdb.getPunchlistIdMobile()).size() <= 0) {
                    Log.d(TAG, "Punchlist call : ");
                    punchlistAPICall(transactionLogMobile, mPronovosSyncData, punchlistdb);
                } else if (punchlistdb == null) {
                    deleteSyncData(transactionLogMobile);
                } else if (punchlistdb != null && mPunchListRepository.getNonSyncedPunchListAttachments(punchlistdb.getPunchlistIdMobile()).size() > 0) {
                    updateTransactionLogMobile(transactionLogMobile, mPronovosSyncData);
                }
                break;

            case WORK_DETAIL_ATTACHMENT:
                workDetailAttachmentAPICall(transactionLogMobile, mPronovosSyncData);
                break;

            case WORK_DETAIL:
                WorkDetails workDetails = mWorkDetailsRepository.getWorkDetails(transactionLogMobile.getUsersId(), transactionLogMobile.getMobileId(), transactionLogMobile.getServerId());
                if (workDetails != null && mWorkDetailsRepository.getNonSyncedWorkDetailAttachments(workDetails.getWorkDetailsReportIdMobile()).size() <= 0) {
                    workDetailAPICall(transactionLogMobile, mPronovosSyncData, workDetails);
                } else if (workDetails == null) {
                    deleteSyncData(transactionLogMobile);
                } else if (workDetails != null && mWorkDetailsRepository.getNonSyncedWorkDetailAttachments(workDetails.getWorkDetailsReportIdMobile()).size() > 0) {
                    updateTransactionLogMobile(transactionLogMobile, mPronovosSyncData);
                }
                break;

            case WORK_IMPACT:
                WorkImpact workImpact = mWorkImpactRepository.getWorkImpact(transactionLogMobile.getUsersId(), transactionLogMobile.getMobileId(), transactionLogMobile.getServerId());
                if (workImpact != null && mWorkImpactRepository.getNonSyncedWorkImpactAttachments(workImpact.getWorkImpactReportIdMobile()).size() <= 0) {
                    workImpactAPICall(transactionLogMobile, mPronovosSyncData, workImpact);
                } else if (workImpact == null) {
                    deleteSyncData(transactionLogMobile);
                } else if (workImpact != null && mWorkImpactRepository.getNonSyncedWorkImpactAttachments(workImpact.getWorkImpactReportIdMobile()).size() > 0) {
                    updateTransactionLogMobile(transactionLogMobile, mPronovosSyncData);
                }
                break;

            case WORK_IMPACT_ATTACHMENT:
                workImpactAttachmentAPICall(transactionLogMobile, mPronovosSyncData);
                break;

            case DRAWING_ANNOTATION:
                Log.d(TAG, "callAPI DRAWING_ANNOTATION : ");
                Log.d(TAG, "callAPI isNonSyncPunchListAvailable : " + mPunchListRepository.isNonSyncPunchListAvailable(transactionLogMobile.getUsersId()));
                Log.d(TAG, "callAPI getNonSyncedAttachments : " + mPunchListRepository.getNonSyncedAttachments(transactionLogMobile.getUsersId()).size());
                if (!mPunchListRepository.isNonSyncPunchListAvailable(transactionLogMobile.getUsersId()) && mPunchListRepository.getNonSyncedAttachments(transactionLogMobile.getUsersId()).size() <= 0) {
                    Log.d(TAG, "callAPI DRAWING_ANNOTATION if : ");
                    new DrawingWorker(transactionLogMobile, mPronovosSyncData, getApplicationContext()).doTransaction();
                } else {
                    updateTransactionLogMobile(transactionLogMobile, mPronovosSyncData);
                }
                break;

            case PROJECT_FORM_SUBMIT:

                List<TransactionLogMobile> transactionLogMobileList = ((PronovosApplication) getApplicationContext())
                        .getDaoSession().getTransactionLogMobileDao().queryBuilder().where(
                                TransactionLogMobileDao.Properties.Module.eq(TransactionModuleEnum.FORM_IMAGE.ordinal()),
                                TransactionLogMobileDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).list();

                if (transactionLogMobileList.size() <= 0) {
                    submitProjectFormCall(transactionLogMobile, mPronovosSyncData, loginResponse);
                } else {
                    updateTransactionLogMobile(transactionLogMobile, mPronovosSyncData);
                }
                break;
            case FORM_IMAGE:


                callFormImage(transactionLogMobile, mPronovosSyncData, loginResponse);

                break;
            case SYNC_OLD_FILES: {
                callSyncOldFiles(transactionLogMobile);
                break;
            }
            case PUNCHLIST_HISTORY: {
//                if(isPunchListSyn){
                   /* PunchListHistoryDb punchListHistoryDb = mPunchListRepository.getPunchListHistoryDetailSingle(transactionLogMobile.getUsersId(), transactionLogMobile.getMobileId(), transactionLogMobile.getServerId());
                    List<PunchListHistoryDb> punchListHistoryDbs = mPunchListRepository.getPunchListHistoryDetail(transactionLogMobile.getUsersId(), transactionLogMobile.getMobileId(), transactionLogMobile.getServerId());
                    Log.d("Manya", "callAPI PUNCHLIST_HISTORY getNonSyncedPunchListRejectReasonAttachments : "+ (mPunchListRepository.getNonSyncedPunchListRejectReasonAttachments(Long.valueOf(punchListHistoryDb.getPunchListAuditsMobileId())).size() <= 0 ));
                    Log.d("Manya", "callAPI PUNCHLIST_HISTORY getNonSyncedPunchList : "+ (mPunchListRepository.getNonSyncedPunchList(Long.valueOf(punchListHistoryDb.getPunchListAuditsMobileId())).size() <= 0));
                    if (punchListHistoryDbs != null
                            && mPunchListRepository.getNonSyncedPunchListRejectReasonAttachments(Long.valueOf(punchListHistoryDb.getPunchListAuditsMobileId())).size() <= 0
                            && mPunchListRepository.getNonSyncedPunchList(Long.valueOf(punchListHistoryDb.getPunchListAuditsMobileId())).size() > 0) {

                            punchListHistoryAPICall(transactionLogMobile, mPronovosSyncData, punchListHistoryDbs);

                    } else if (punchListHistoryDbs == null) {
                        Log.d(TAG, "History call delete ********* : ");
                        deleteSyncData(transactionLogMobile);
                    } else if (punchListHistoryDbs != null
                            && mPunchListRepository.getNonSyncedPunchListRejectReasonAttachments(Long.valueOf(punchListHistoryDb.getPunchListAuditsMobileId())).size() > 0
                            && mPunchListRepository.getNonSyncedPunchList(Long.valueOf(punchListHistoryDb.getPunchListAuditsMobileId())).size() > 0) {
                        Log.d(TAG, "History call update ********* : ");
                        updateTransactionLogMobile(transactionLogMobile, mPronovosSyncData);
                        ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();
                    } else {
//                        isHistoryInprogressCount = 0;
                        updateTransactionLogMobile(transactionLogMobile, mPronovosSyncData);
                    }
//                }*/

                PunchListHistoryDb punchListHistoryDb = mPunchListRepository.getPunchListHistoryDetailSingle(transactionLogMobile.getUsersId(), transactionLogMobile.getMobileId(), transactionLogMobile.getServerId());
                List<PunchListHistoryDb> punchListHistoryDbs = mPunchListRepository.getPunchListHistoryDetail(transactionLogMobile.getUsersId(), transactionLogMobile.getMobileId(), transactionLogMobile.getServerId());
                Log.d("Manya", "callAPI PUNCHLIST_HISTORY getNonSyncedPunchListRejectReasonAttachments : " + (mPunchListRepository.getNonSyncedPunchListRejectReasonAttachments(Long.valueOf(punchListHistoryDb.getPunchListAuditsMobileId())).size() <= 0));
                Log.d("Manya", "callAPI PUNCHLIST_HISTORY getNonSyncedPunchList : " + (mPunchListRepository.getNonSyncedPunchList(Long.valueOf(punchListHistoryDb.getPunchListAuditsMobileId())).size() <= 0));
                if (punchListHistoryDbs != null
                        && mPunchListRepository.getNonSyncedPunchListRejectReasonAttachments(Long.valueOf(punchListHistoryDb.getPunchListAuditsMobileId())).size() <= 0
                        && mPunchListRepository.getNonSyncedPunchList(Long.valueOf(punchListHistoryDb.getPunchListAuditsMobileId())).size() <= 0) {

                    punchListHistoryAPICall(transactionLogMobile, mPronovosSyncData, punchListHistoryDbs);

                } else if (punchListHistoryDbs == null) {
                    Log.d(TAG, "History call delete ********* : ");
                    deleteSyncData(transactionLogMobile);
                } else if (punchListHistoryDbs != null
                        && mPunchListRepository.getNonSyncedPunchListRejectReasonAttachments(Long.valueOf(punchListHistoryDb.getPunchListAuditsMobileId())).size() > 0
                        && mPunchListRepository.getNonSyncedPunchList(Long.valueOf(punchListHistoryDb.getPunchListAuditsMobileId())).size() > 0) {
                    Log.d(TAG, "History call update ********* : ");
                    updateTransactionLogMobile(transactionLogMobile, mPronovosSyncData);
                    ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();
                } else {
                    Log.d(TAG, "History call in else update ********* : ");
//                        isHistoryInprogressCount = 0;
                    updateTransactionLogMobile(transactionLogMobile, mPronovosSyncData);
                }
//                }
                break;
            }
            case PUNCHLIST_REJECT_REASON_ATTACHMENT: {
                PunchListRejectReasonAttachments rejectReasonAttachments = mPunchListRepository.getPunchListRejectReasonDetail(transactionLogMobile.getUsersId(), transactionLogMobile.getMobileId(), transactionLogMobile.getServerId());
                Log.d(TAG, "PUNCHLIST_REJECT_REASON_ATTACHMENT: " + rejectReasonAttachments.getAttachmentPath());
                if (rejectReasonAttachments != null)
                    punchListRejectReasonAttachmentAPICall(transactionLogMobile, mPronovosSyncData);
                break;
            }
            case ISSUE_TRACKING: {
                projectIssueTrackingProvider.syncIssuesToServer(transactionLogMobile);
                break;
            }
            default:
                break;
        }
    }

    private void callSyncOldFiles(TransactionLogMobile transactionLogMobile) {
        Log.d(TAG, "callSyncOldFiles: ");

        LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(getApplicationContext()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        List<BackupSyncImageFiles> filesList = mBackupSyncRepository.getBackupSyncFilesList();
        if (filesList != null && filesList.size() > 0) {
            SyncOldFileUtil.uploadUnSyncFiles(filesList, getApplicationContext(), projectsProvider, mBackupSyncRepository, loginResponse, transactionLogMobile);
            BackupSyncProgressUpdate backupSyncProgressUpdate = new BackupSyncProgressUpdate();
            backupSyncProgressUpdate.isSyncStartedRunning = true;
            EventBus.getDefault().post(backupSyncProgressUpdate);

        }
    }

    private void callFormImage(TransactionLogMobile transactionLogMobile, TransactionLogMobileDao mPronovosSyncData, LoginResponse loginResponse) {
        FormImage formImage = mprojectFormRepository.getFormImage(transactionLogMobile.getMobileId());
        if (formImage != null) {
            mprojectFormProvider.uploadFormImage(formImage.getImageName(), formImage.getId(), transactionLogMobile, mPronovosSyncData, new ProviderResult<UploadFile>() {
                @Override
                public void success(UploadFile result) {
                    mprojectFormRepository.deleteFormImage(formImage.getId());
                    ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();
                }

                @Override
                public void AccessTokenFailure(String message) {
                    transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                    mPronovosSyncData.update(transactionLogMobile);
                }

                @Override
                public void failure(String message) {
                    transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                    mPronovosSyncData.update(transactionLogMobile);
                }
            });
        } else {

            DeleteQuery<TransactionLogMobile> pronovosSyncDataDeleteQuery = ((PronovosApplication) getApplicationContext()).getDaoSession().queryBuilder(TransactionLogMobile.class)
                    .where(TransactionLogMobileDao.Properties.MobileId.eq(transactionLogMobile.getMobileId()),
                            TransactionLogMobileDao.Properties.Module.eq(TransactionModuleEnum.FORM_IMAGE.ordinal()))
                    .buildDelete();
            pronovosSyncDataDeleteQuery.executeDeleteWithoutDetachingEntities();
        }
    }

    @Override
    public void onStopped() {
        super.onStopped();
        Log.i(TAG, "onStopped worker: ");

    }

    private void workImpactAPICall(TransactionLogMobile transactionLogMobile, TransactionLogMobileDao mPronovosSyncData, WorkImpact workImpact) {
        WorkImpactRequest workImpactRequest = new WorkImpactRequest();
        workImpactRequest.setProjectId(workImpact.getProjectId());
        workImpactRequest.setReportDate(DateFormatter.formatDateTimeHHForService(workImpact.getCreatedAt()));

        List<WorkImpactsReportRequest> workImpactsReportRequests = new ArrayList<>();
        workImpactsReportRequests.add(createWorkImpact(workImpact));
        workImpactRequest.setWorkImpactsReport(workImpactsReportRequests);

        transactionLogMobile.setStatus(SyncDataEnum.PROCESSING.ordinal());
        mPronovosSyncData.update(transactionLogMobile);
        Log.d(TAG, "TRANSACTION_WORKER call workImpactAPICall: getWorkImpacts ");
        mWorkImpactProvider.getWorkImpacts(workImpactRequest, workImpact.getCreatedAt(), new ProviderResult<List<WorkImpact>>() {
            @Override
            public void success(List<WorkImpact> workImpacts) {
                transactionLogMobile.setStatus(SyncDataEnum.SYNC.ordinal());
                deleteSyncData(transactionLogMobile);
                TransactionLogUpdate transactionLogUpdate = new TransactionLogUpdate();
                transactionLogUpdate.setTransactionModuleEnum(TransactionModuleEnum.WORK_IMPACT);
                EventBus.getDefault().post(transactionLogUpdate);
                doWork();
            }

            @Override
            public void AccessTokenFailure(String message) {
                onAccessTokenFail(transactionLogMobile, mPronovosSyncData);
            }

            @Override
            public void failure(String message) {
                transactionLogMobile.setStatus(SyncDataEnum.SYNC_FAILED.ordinal());
                mPronovosSyncData.update(transactionLogMobile);
//                ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();
            }
        });
    }

    private void submitProjectFormCall(TransactionLogMobile transactionLogMobile, TransactionLogMobileDao mPronovosSyncData, LoginResponse loginResponse) {
        UserForms userForms = mprojectFormRepository.getSubmittedUserForm(transactionLogMobile.getServerId(), transactionLogMobile.getMobileId(), loginResponse.getUserDetails().getUsers_id());
        Log.e(TAG, "OPEN_FORM **************  submitProjectFormCall: userForms userForms.getFormId() " + userForms.getFormId());
        if (userForms != null) {
            SubmitFormRequest submitFormRequest = new SubmitFormRequest();
            List<Submission> submissions = new ArrayList<>();
            Submission submission = new Submission();
            submission.setForm(userForms.getFormId());
            submission.setProject(userForms.getPjProjectsId());
            submission.setPublish(userForms.getPublish());
            submission.setSendEmail(0);
            if (userForms.getPjAreasId() != null && userForms.getPjAreasId() != 0)
                submission.setPjAreasId(userForms.getPjAreasId());
            submission.setScheduleFormId(userForms.getScheduleFormId());
            SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            submission.setDueDate(userForms.getDueDate() == null ? "" : sdformat.format(userForms.getDueDate()));
            submission.setSubmittedData(userForms.getSubmittedData());
            submission.setUserFormsId(userForms.getFormSubmitId());
            submission.setUserFormMobileId(userForms.getFormSubmitMobileId());
            // added for new date change feature
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            submission.setCreatedDate(sdf.format(userForms.getCreatedAt()));
            submission.setRevisionNumber(userForms.getRevisionNumber());
            submission.setSaveDate(DateFormatter.formatDateTimeHHForService(userForms.getFormSaveDate()));
            // chges end
            submissions.add(submission);
            String str = userForms.getDeletedImages();
            if (str != null) {
                str = str.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "");
                if (!TextUtils.isEmpty(str)) {
                    ArrayList<String> deletedImages = new ArrayList<String>(Arrays.asList(str.split(",")));
                    submission.setDeletedImages(deletedImages);
                }
            }
            submitFormRequest.setSubmission(submissions);
            mprojectFormProvider.submitProjectFormComponents(userForms.getFormId(), userForms.getFormId(), userForms.getRevisionNumber(),
                    userForms.getPjProjectsId(), submitFormRequest, loginResponse, new ProviderResult<List<UserForms>>() {
                        @Override
                        public void success(List<UserForms> result) {
                            transactionLogMobile.setStatus(SyncDataEnum.SYNC.ordinal());
                            deleteSyncData(transactionLogMobile);
                            TransactionLogUpdate transactionLogUpdate = new TransactionLogUpdate();
                            transactionLogUpdate.setTransactionModuleEnum(TransactionModuleEnum.PROJECT_FORM_SUBMIT);

                            EventBus.getDefault().post(transactionLogUpdate);
                            doWork();
                        }

                        @Override
                        public void AccessTokenFailure(String message) {
                            onAccessTokenFail(transactionLogMobile, mPronovosSyncData);

                        }

                        @Override
                        public void failure(String message) {
                            Log.d(TAG, " OPEN_FORM  submitProjectFormComponents failure: " + message);
                            transactionLogMobile.setStatus(SyncDataEnum.SYNC_FAILED.ordinal());
                            mPronovosSyncData.update(transactionLogMobile);
                        }
                    });
        }
    }

    private WorkImpactsReportRequest createWorkImpact(WorkImpact workImpact) {

        WorkImpactsReportRequest workImpactsReport = new WorkImpactsReportRequest();
        workImpactsReport.setDeletedAt(workImpact.getDeletedAt() != null ? DateFormatter.formatDateTimeHHForService(workImpact.getDeletedAt()) : "");
        workImpactsReport.setType(workImpact.getType());
        workImpactsReport.setCompanyId(String.valueOf(workImpact.getCompanyId()));
        workImpactsReport.setProjectId(String.valueOf(workImpact.getProjectId()));
        workImpactsReport.setCompanyname(workImpact.getCompanyName());
        workImpactsReport.setWorkImpactReportId(String.valueOf(workImpact.getWorkImpactReportId()));
        workImpactsReport.setWorkImpactReportIdMobile(String.valueOf(workImpact.getWorkImpactReportIdMobile()));
        workImpactsReport.setWorkImpLocation(workImpact.getWorkImpLocation());
        workImpactsReport.setWorkSummary(workImpact.getWorkSummary());
        workImpactsReport.setCreatedAt(workImpact.getCreatedAt() != null ? DateFormatter.formatDateTimeHHForService(workImpact.getCreatedAt()) : "");
        List<WorkImpactAttachments> attachments = mWorkImpactRepository.getSyncedAttachments(workImpact.getWorkImpactReportIdMobile());
        List<com.pronovoscm.model.request.workimpact.Attachments> attachmentsList = new ArrayList<>();
        for (WorkImpactAttachments attachment :
                attachments) {
            com.pronovoscm.model.request.workimpact.Attachments attachmnt = new com.pronovoscm.model.request.workimpact.Attachments();
            attachmnt.setAttachmentsId(attachment.getAttachmentId() != null ? attachment.getAttachmentId() : 0);
            attachmnt.setAttachmentsIdMobile((int) (long) attachment.getAttachmentIdMobile());
            attachmnt.setAttachPath(attachment.getAttachmentPath());
            attachmnt.setDeletedAt(attachment.getDeletedAt() != null ? DateFormatter.formatDateTimeHHForService(attachment.getDeletedAt()) : "");
            attachmentsList.add(attachmnt);

        }

        workImpactsReport.setAttachments(attachmentsList);

        return workImpactsReport;
    }

    private void workImpactAttachmentAPICall(TransactionLogMobile transactionLogMobile, TransactionLogMobileDao mPronovosSyncData) {
        WorkImpactAttachments workImpactAttachments = mWorkImpactRepository.getWorkImpactAttachments(transactionLogMobile.getUsersId(),
                transactionLogMobile.getMobileId(), transactionLogMobile.getServerId());
        SignedUrlRequest signedUrlRequest = new SignedUrlRequest(UrlTypeEnum.REPORT_FILES.toString());

        transactionLogMobile.setStatus(SyncDataEnum.PROCESSING.ordinal());
        mPronovosSyncData.update(transactionLogMobile);

        projectsProvider.getSignedUrl(signedUrlRequest, new ProviderResult<SignedUrlResponse>() {
            @Override
            public void success(SignedUrlResponse result) {
                String key = result.getData().getFormattributes().getKey();
                result.getData().getFormattributes().setKey(UrlTypeEnum.REPORT_FILES + "/" + key);
                mFileUploadProvider.setUrl(result.getData().getFormaction().getAction());

                File file = new File(workImpactAttachments.getAttachmentPath());
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

                mFileUploadProvider.uploadWorkImpactAttachmentFile(result.getData().getFormaction().getAction(), finalRequestBody, workImpactAttachments, mWorkImpactRepository, new ProviderResult<Boolean>() {
                    @Override
                    public void success(Boolean result) {
                        if (result) {
                            transactionLogMobile.setStatus(SyncDataEnum.SYNC.ordinal());
                            deleteSyncData(transactionLogMobile);
                            TransactionLogUpdate transactionLogUpdate = new TransactionLogUpdate();
                            transactionLogUpdate.setTransactionModuleEnum(TransactionModuleEnum.WORK_IMPACT_ATTACHMENT);
                            EventBus.getDefault().post(transactionLogUpdate);
                            doWork();
                        }
                    }

                    @Override
                    public void AccessTokenFailure(String message) {
                        onAccessTokenFail(transactionLogMobile, mPronovosSyncData);
                    }

                    @Override
                    public void failure(String message) {
                        transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                        mPronovosSyncData.update(transactionLogMobile);
//                        ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();
                    }
                });
            }

            @Override
            public void AccessTokenFailure(String message) {
                onAccessTokenFail(transactionLogMobile, mPronovosSyncData);
            }

            @Override
            public void failure(String message) {
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                mPronovosSyncData.update(transactionLogMobile);
//                ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();
            }
        });
    }

    private void workDetailAPICall(TransactionLogMobile transactionLogMobile, TransactionLogMobileDao mPronovosSyncData, WorkDetails workDetails) {
        WorkDetailsRequest workDetailsRequest = new WorkDetailsRequest();
        workDetailsRequest.setProjectId(workDetails.getProjectId());
        workDetailsRequest.setReportDate(DateFormatter.formatDateTimeHHForService(workDetails.getCreatedAt()));

        List<WorkDetailsReportRequest> workDetailsReport = new ArrayList<>();

        workDetailsReport.add(createWorkDetail(workDetails));

        workDetailsRequest.setWorkDetailsReport(workDetailsReport);

        transactionLogMobile.setStatus(SyncDataEnum.PROCESSING.ordinal());
        mPronovosSyncData.update(transactionLogMobile);

        mWorkDetailsProvider.getWorkDetails(workDetailsRequest, workDetails.getCreatedAt(), new ProviderResult<List<WorkDetails>>() {
            @Override
            public void success(List<WorkDetails> result) {
                transactionLogMobile.setStatus(SyncDataEnum.SYNC.ordinal());
                deleteSyncData(transactionLogMobile);
                TransactionLogUpdate transactionLogUpdate = new TransactionLogUpdate();
                transactionLogUpdate.setTransactionModuleEnum(TransactionModuleEnum.WORK_DETAIL);
                EventBus.getDefault().post(transactionLogUpdate);
                doWork();
            }

            @Override
            public void AccessTokenFailure(String message) {
                onAccessTokenFail(transactionLogMobile, mPronovosSyncData);
            }

            @Override
            public void failure(String message) {
                transactionLogMobile.setStatus(SyncDataEnum.SYNC_FAILED.ordinal());
                mPronovosSyncData.update(transactionLogMobile);
//                ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();
            }
        });


    }

    private WorkDetailsReportRequest createWorkDetail(WorkDetails workDetails) {

        WorkDetailsReportRequest workDetailsReport = new WorkDetailsReportRequest();
        workDetailsReport.setDeletedAt(workDetails.getDeletedAt() != null ? DateFormatter.formatDateTimeHHForService(workDetails.getDeletedAt()) : "");
        workDetailsReport.setType(workDetails.getType());
        workDetailsReport.setCompanyId(String.valueOf(workDetails.getCompanyId()));
        workDetailsReport.setProjectId(String.valueOf(workDetails.getProjectId()));
        workDetailsReport.setCompanyname(workDetails.getCompanyName());
        workDetailsReport.setWorkDetailsReportId(String.valueOf(workDetails.getWorkDetailsReportId()));
        workDetailsReport.setWorkDetailsReportIdMobile(String.valueOf(workDetails.getWorkDetailsReportIdMobile()));
        workDetailsReport.setWorkDetLocation(workDetails.getWorkDetLocation());
        workDetailsReport.setWorkSummary(workDetails.getWorkSummary());
        workDetailsReport.setCreatedAt(workDetails.getCreatedAt() != null ? DateFormatter.formatDateTimeHHForService(workDetails.getCreatedAt()) : "");
        List<WorkDetailsAttachments> attachments = mWorkDetailsRepository.getSyncedAttachments(workDetails.getWorkDetailsReportIdMobile());
        List<com.pronovoscm.model.request.workdetails.Attachments> attachmentsList = new ArrayList<>();
        for (WorkDetailsAttachments attachment :
                attachments) {
            com.pronovoscm.model.request.workdetails.Attachments attachmnt = new com.pronovoscm.model.request.workdetails.Attachments();
            attachmnt.setAttachmentsId(attachment.getAttachmentId() != null ? attachment.getAttachmentId() : 0);
            attachmnt.setAttachmentsIdMobile((int) (long) attachment.getAttachmentIdMobile());
            attachmnt.setAttachPath(attachment.getAttachmentPath());
            attachmnt.setDeletedAt(attachment.getDeletedAt() != null ? DateFormatter.formatDateTimeHHForService(attachment.getDeletedAt()) : "");
            attachmentsList.add(attachmnt);

        }

        workDetailsReport.setAttachments(attachmentsList);

        return workDetailsReport;
    }

    private void workDetailAttachmentAPICall(TransactionLogMobile transactionLogMobile, TransactionLogMobileDao mPronovosSyncData) {
        WorkDetailsAttachments workDetailsAttachments =
                mWorkDetailsRepository.getWorkDetailsAttachments(transactionLogMobile.getUsersId(),
                        transactionLogMobile.getMobileId(), transactionLogMobile.getServerId());
        SignedUrlRequest signedUrlRequest = new SignedUrlRequest(UrlTypeEnum.REPORT_FILES.toString());

        transactionLogMobile.setStatus(SyncDataEnum.PROCESSING.ordinal());
        mPronovosSyncData.update(transactionLogMobile);
        if (workDetailsAttachments != null) {
            projectsProvider.getSignedUrl(signedUrlRequest, new ProviderResult<SignedUrlResponse>() {
                @Override
                public void success(SignedUrlResponse result) {
                    String key = result.getData().getFormattributes().getKey();
                    result.getData().getFormattributes().setKey(UrlTypeEnum.REPORT_FILES + "/" + key);
                    mFileUploadProvider.setUrl(result.getData().getFormaction().getAction());

                    File file = new File(workDetailsAttachments.getAttachmentPath());
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

                    mFileUploadProvider.uploadAttachmentFile(result.getData().getFormaction().getAction(), finalRequestBody, workDetailsAttachments, mWorkDetailsRepository, new ProviderResult<Boolean>() {
                        @Override
                        public void success(Boolean result) {
                            if (result) {
                                transactionLogMobile.setStatus(SyncDataEnum.SYNC.ordinal());
                                deleteSyncData(transactionLogMobile);
                                TransactionLogUpdate transactionLogUpdate = new TransactionLogUpdate();
                                transactionLogUpdate.setTransactionModuleEnum(TransactionModuleEnum.WORK_DETAIL_ATTACHMENT);
                                EventBus.getDefault().post(transactionLogUpdate);
                                doWork();
                            }
                        }

                        @Override
                        public void AccessTokenFailure(String message) {
                            onAccessTokenFail(transactionLogMobile, mPronovosSyncData);
                        }

                        @Override
                        public void failure(String message) {
                            transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                            mPronovosSyncData.update(transactionLogMobile);
//                        ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();
                        }
                    });

                }

                @Override
                public void AccessTokenFailure(String message) {
                    onAccessTokenFail(transactionLogMobile, mPronovosSyncData);
                }

                @Override
                public void failure(String message) {
                    transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                    mPronovosSyncData.update(transactionLogMobile);
//                ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();

                }
            });
        } else {
            transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
            mPronovosSyncData.update(transactionLogMobile);
        }
    }

    /**
     * To call weather report api.
     *
     * @param transactionLogMobile used to update the status in Transaction table.
     * @param mPronovosSyncData    used to update Transaction table.
     */
    private void weatherAPICall(TransactionLogMobile transactionLogMobile, TransactionLogMobileDao mPronovosSyncData) {

        WeatherReport weatherReport = mWeatherReportRepository.getWeatherReport(transactionLogMobile.getUsersId(), transactionLogMobile.getMobileId(), transactionLogMobile.getCreateDate());
        List<WeatherReports> weatherReportList = new ArrayList<>();

        weatherReportList.add(createWeatherReport(weatherReport));

        WeatherReportRequest weatherReportRequest = new WeatherReportRequest();
        weatherReportRequest.setProjectId(String.valueOf(weatherReport.getProjectId()));
        weatherReportRequest.setWeatherReports(weatherReportList);
        weatherReportRequest.setReportDate(DateFormatter.formatDateTimeHHForService(weatherReport.getReportDate()));

        transactionLogMobile.setStatus(SyncDataEnum.PROCESSING.ordinal());

        mPronovosSyncData.update(transactionLogMobile);

        mWeatherReportProvider.getWeatherReport(weatherReportRequest, weatherReport.getReportDate(), new ProviderResult<WeatherReport>() {
            @Override
            public void success(WeatherReport result) {
                transactionLogMobile.setStatus(SyncDataEnum.SYNC.ordinal());
                deleteSyncData(transactionLogMobile);
                TransactionLogUpdate transactionLogUpdate = new TransactionLogUpdate();
                transactionLogUpdate.setTransactionModuleEnum(TransactionModuleEnum.WEATHER);
                transactionLogUpdate.setWeatherReport(result);
                EventBus.getDefault().post(transactionLogUpdate);
                doWork();
            }

            @Override
            public void AccessTokenFailure(String message) {
                onAccessTokenFail(transactionLogMobile, mPronovosSyncData);
            }

            @Override
            public void failure(String message) {
                transactionLogMobile.setStatus(SyncDataEnum.SYNC_FAILED.ordinal());
                mPronovosSyncData.update(transactionLogMobile);
//                ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();

            }
        });

    }

    /**
     * Sync punch list attachments on AWS server.
     *
     * @param transactionLogMobile used to update the status in Transaction table.
     * @param mPronovosSyncData    used to update Transaction table.
     */
    private void punchlistAttachmentAPICall(TransactionLogMobile transactionLogMobile, TransactionLogMobileDao mPronovosSyncData) {
        PunchListAttachments punchlistAttachmentDetail = mPunchListRepository.getPunchlistAttachmentDetail(transactionLogMobile.getUsersId(), transactionLogMobile.getMobileId(), transactionLogMobile.getServerId());
        SignedUrlRequest signedUrlRequest = new SignedUrlRequest(UrlTypeEnum.PUNCHLIST_FILES.toString());
        transactionLogMobile.setStatus(SyncDataEnum.PROCESSING.ordinal());
        mPronovosSyncData.update(transactionLogMobile);
        projectsProvider.getSignedUrl(signedUrlRequest, new ProviderResult<SignedUrlResponse>() {
            @Override
            public void success(SignedUrlResponse result) {
                String key = result.getData().getFormattributes().getKey();
                result.getData().getFormattributes().setKey(UrlTypeEnum.PUNCHLIST_FILES + "/" + key);
                mFileUploadProvider.setUrl(result.getData().getFormaction().getAction());

                File file = new File(punchlistAttachmentDetail.getAttachmentPath());
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


                // Upload punchlist attachment to the server
                mFileUploadProvider.uploadPunchListAttachmentFile(result.getData().getFormaction().getAction(),
                        finalRequestBody, punchlistAttachmentDetail, mPunchListRepository, null,
                        new ProviderResult<Boolean>() {
                            @Override
                            public void success(Boolean result) {
                                if (result) {
                                    Log.e("Manya", " punchlistAttachmentAPICall  success: ");
                                    transactionLogMobile.setStatus(SyncDataEnum.SYNC.ordinal());
                                    deleteSyncData(transactionLogMobile);
                                    TransactionLogUpdate transactionLogUpdate = new TransactionLogUpdate();
                                    transactionLogUpdate.setTransactionModuleEnum(TransactionModuleEnum.PUNCHLIST_ATTACHMENT);
                                    EventBus.getDefault().post(transactionLogUpdate);
                                    ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();

                                } else {
                                    transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                                    mPronovosSyncData.update(transactionLogMobile);
                                    ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();
                                }
                            }

                            @Override
                            public void AccessTokenFailure(String message) {
                                onAccessTokenFail(transactionLogMobile, mPronovosSyncData);
                            }

                            @Override
                            public void failure(String message) {
                                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                                mPronovosSyncData.update(transactionLogMobile);
//                        ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();

                            }
                        });
            }

            @Override
            public void AccessTokenFailure(String message) {
                onAccessTokenFail(transactionLogMobile, mPronovosSyncData);

            }

            @Override
            public void failure(String message) {
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                mPronovosSyncData.update(transactionLogMobile);
//                ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();
            }
        });
    }

    /**
     * To sync punch list with server.
     *
     * @param punchlistdb          an object of not sync punch list on server.
     * @param transactionLogMobile used to update the status in Transaction table.
     * @param mPronovosSyncData    used to update Transaction table.
     */
    private void punchlistAPICall(TransactionLogMobile transactionLogMobile, TransactionLogMobileDao mPronovosSyncData, PunchlistDb punchlistdb) {


        PunchListRequest punchListRequest = new PunchListRequest();
        punchListRequest.setProjectId(punchlistdb.getPjProjectsId());
        List<PunchList> punchlistreq = new ArrayList<>();

        punchlistreq.add(createPunchList(punchlistdb));

        punchListRequest.setPunchlists(punchlistreq);
        Log.d(TAG, "Punchlist  call 2: ");
        mPunchListProvider.getPunchList(punchListRequest, new ProviderResult<List<PunchlistDb>>() {
            @Override
            public void success(List<PunchlistDb> result) {
                //  Log.e(TAG, "Punchlist called success: " +  result);
                transactionLogMobile.setStatus(SyncDataEnum.SYNC.ordinal());
                deleteSyncData(transactionLogMobile);
                TransactionLogUpdate transactionLogUpdate = new TransactionLogUpdate();
                transactionLogUpdate.setTransactionModuleEnum(PUNCHLIST);
                Log.d(TAG, "Punchlist call 3 : ");
                EventBus.getDefault().post(transactionLogUpdate);

                if (!mPunchListRepository.isNonSyncPunchListAvailable(transactionLogMobile.getUsersId()) && mPunchListRepository.getNonSyncedAttachments(transactionLogMobile.getUsersId()).size() <= 0) {
                    Log.i(TAG, "Punchlist called success: drawing " + "");
                    List<TransactionLogMobile> transactionLogMobileList = ((PronovosApplication) getApplicationContext()).getDaoSession().getTransactionLogMobileDao().queryBuilder().where(
                            TransactionLogMobileDao.Properties.Status.eq(SyncDataEnum.NOTSYNC.ordinal()), TransactionLogMobileDao.Properties.UsersId.eq(transactionLogMobile.getUsersId())).list();
                    if (transactionLogMobileList.size() == 0) {
                        transactionLogMobileList = ((PronovosApplication) getApplicationContext()).getDaoSession().getTransactionLogMobileDao().queryBuilder().where(
                                TransactionLogMobileDao.Properties.Status.eq(SyncDataEnum.SYNC_FAILED.ordinal()), TransactionLogMobileDao.Properties.UsersId.eq(transactionLogMobile.getUsersId())).list();
                    }
                    for (int i = 0; i < transactionLogMobileList.size(); i++) {
                        LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(getApplicationContext()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
                        if (loginResponse != null) {
                            callAPI(transactionLogMobileList.get(i), mPronovosSyncData, loginResponse);
                        }
                    }
//                    new DrawingWorker(transactionLogMobile, mPronovosSyncData, getApplicationContext()).doTransaction();
                }/*else if(mPunchListRepository.isNonSyncPunchListHistoryAvailable(transactionLogMobile.getUsersId())) {
                    Log.d(TAG, "success: in callTransinon ");
                    callTransaction();
                }*/ else {
                    Log.i(TAG, "Punchlist called success: not drawing " + "");
                    ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                onAccessTokenFail(transactionLogMobile, mPronovosSyncData);
            }

            @Override
            public void failure(String message) {
                transactionLogMobile.setStatus(SyncDataEnum.SYNC_FAILED.ordinal());
                mPronovosSyncData.update(transactionLogMobile);
//                ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();
            }
        });
    }

    /**
     * To create PunchList object.
     *
     * @param punchlistdb an object of not sync punch list on server.
     * @return an object of PunchList.
     */
    private PunchList createPunchList(PunchlistDb punchlistdb) {
        PunchList punchlist = new PunchList();
        //TODO:Nitin
//        punchlist.setAssignedTo(punchlistdb.getAssignedTo());
        punchlist.setAssignedTo(punchlistdb.getAssignedTo());
        punchlist.setAssignedCc(punchlistdb.getAssignedCcList());
        punchlist.setAssigneeName(punchlistdb.getAssigneeName());
        punchlist.setDeletedAt(punchlistdb.getDeletedAt() != null ? DateFormatter.formatDateTimeHHForService(punchlistdb.getDeletedAt()) : "");
        punchlist.setCreatedAt(punchlistdb.getCreatedAt() != null ? DateFormatter.formatDateTimeHHForService(punchlistdb.getCreatedAt()) : "");
        punchlist.setDateDue(punchlistdb.getDateDue() != null ? DateFormatter.formatDateTimeHHForService(punchlistdb.getDateDue()) : "");
        punchlist.setDateCreated(punchlistdb.getDateCreated() != null ? DateFormatter.formatDateTimeHHForService(punchlistdb.getDateCreated()) : "");
        punchlist.setCreatedBy(punchlistdb.getCreatedByUserId());
        punchlist.setDescription(punchlistdb.getDescriptions());
        punchlist.setItemNumber(String.valueOf(punchlistdb.getItemNumber()));
        punchlist.setLocation(punchlistdb.getLocation());
        punchlist.setComments(punchlistdb.getComments());
        punchlist.setPjProjectsId(String.valueOf(punchlistdb.getPjProjectsId()));
        punchlist.setPunchListsId(String.valueOf(punchlistdb.getPunchlistId()));
        punchlist.setPunchListsIdMobile(String.valueOf(punchlistdb.getPunchlistIdMobile()));
        punchlist.setStatus(String.valueOf(punchlistdb.getStatus()));
        punchlist.setSendEmail(punchlistdb.getSendEmail());

        List<PunchListAttachments> attachments = mPunchListRepository.getSyncedAttachments(punchlistdb.getPunchlistIdMobile());
        List<Attachments> attachmentsList = new ArrayList<>();
        for (PunchListAttachments punchListAttachment :
                attachments) {
            if (punchListAttachment != null) {
                Attachments attachment = new Attachments();
                if (punchListAttachment.getAttachmentId() != null) {
                    attachment.setAttachmentsId(punchListAttachment.getAttachmentId());
                } else {
                    attachment.setAttachmentsId(0);

                }
                attachment.setAttachmentsIdMobile((int) (long) punchListAttachment.getAttachmentIdMobile());
                attachment.setAttachPath(punchListAttachment.getAttachmentPath());
                attachment.setDeletedAt(punchListAttachment.getDeletedAt() != null ? DateFormatter.formatDateTimeHHForService(punchListAttachment.getDeletedAt()) : "");
                attachmentsList.add(attachment);
            }
        }
        punchlist.setAttachments(attachmentsList);

        return punchlist;
    }

    /**
     * Sync punch list reject reason attachments on AWS server.
     *
     * @param transactionLogMobile used to update the status in Transaction table.
     * @param mPronovosSyncData    used to update Transaction table.
     *                             /project/220/punchlist/2211/rejection/
     *                             <p>
     *                             /project/{{$project_id}}/punchlist/{{$punch_list_id}}/rejection/
     */
    private void punchListRejectReasonAttachmentAPICall(TransactionLogMobile transactionLogMobile, TransactionLogMobileDao mPronovosSyncData) {
        PunchListRejectReasonAttachments punchListRejectReasonAttachments = mPunchListRepository.getPunchListRejectReasonAttachmentDetail(transactionLogMobile.getUsersId(), transactionLogMobile.getMobileId(), transactionLogMobile.getServerId());
        /*SignedUrlRequest signedUrlRequest = new SignedUrlRequest("/project/" + punchListRejectReasonAttachments.getPjProjectsId()
                + "/punchlist/" + punchListRejectReasonAttachments.getPunchListId() + "/rejection/");*/
        SignedUrlRequest signedUrlRequest = new SignedUrlRequest(UrlTypeEnum.PUNCHLIST_FILES.toString());
        transactionLogMobile.setStatus(SyncDataEnum.PROCESSING.ordinal());
        mPronovosSyncData.update(transactionLogMobile);
        Log.d(TAG, "punchListRejectReasonAttachmentAPICall: ");
        projectsProvider.getSignedUrl(signedUrlRequest, new ProviderResult<SignedUrlResponse>() {
            @Override
            public void success(SignedUrlResponse result) {
                String key = result.getData().getFormattributes().getKey();
                Log.d(TAG, "punchListRejectReasonAttachmentAPICall: " + key);
                /*result.getData().getFormattributes().setKey("/project/" + punchListRejectReasonAttachments.getPjProjectsId()
                        + "/punchlist/" + punchListRejectReasonAttachments.getPunchListId() + "/rejection" + "/" + key);*/
                result.getData().getFormattributes().setKey(UrlTypeEnum.PUNCHLIST_FILES + "/" + key);
                mFileUploadProvider.setUrl(result.getData().getFormaction().getAction());

                File file = new File(punchListRejectReasonAttachments.getAttachmentPath());
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


                // Upload punchlist attachment to the server
                mFileUploadProvider.uploadPunchListAttachmentFile(result.getData().getFormaction().getAction(),
                        finalRequestBody, null, mPunchListRepository, punchListRejectReasonAttachments,
                        new ProviderResult<Boolean>() {
                            @Override
                            public void success(Boolean result) {
                                if (result) {
                                    Log.e(TAG, " punchlistreject reason AttachmentAPICall  success: ");
                                    transactionLogMobile.setStatus(SyncDataEnum.SYNC.ordinal());
                                    deleteSyncData(transactionLogMobile);
                                    TransactionLogUpdate transactionLogUpdate = new TransactionLogUpdate();
                                    transactionLogUpdate.setTransactionModuleEnum(TransactionModuleEnum.PUNCHLIST_REJECT_REASON_ATTACHMENT);
                                    EventBus.getDefault().post(transactionLogUpdate);
                                    ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();

                                } else {
                                    transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                                    mPronovosSyncData.update(transactionLogMobile);
                                    ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();
                                }
                            }

                            @Override
                            public void AccessTokenFailure(String message) {
                                onAccessTokenFail(transactionLogMobile, mPronovosSyncData);
                            }

                            @Override
                            public void failure(String message) {
                                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                                mPronovosSyncData.update(transactionLogMobile);
//                        ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();

                            }
                        });
            }

            @Override
            public void AccessTokenFailure(String message) {
                onAccessTokenFail(transactionLogMobile, mPronovosSyncData);

            }

            @Override
            public void failure(String message) {
                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                mPronovosSyncData.update(transactionLogMobile);
//                ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();
            }
        });
    }


    /**
     * punch history API calls
     */
    private void punchListHistoryAPICall(TransactionLogMobile transactionLogMobile, TransactionLogMobileDao mPronovosSyncData, List<PunchListHistoryDb> punchListHistoryDb) {

        transactionLogMobile.setStatus(SyncDataEnum.PROCESSING.ordinal());
        mPronovosSyncData.update(transactionLogMobile);

        PunchListHistoryRequest punchListHistoryRequest = new PunchListHistoryRequest();
        List<PunchListHistory> punchListHistoryReq = new ArrayList<>();
        for (PunchListHistoryDb history : punchListHistoryDb) {
            punchListHistoryRequest.setProjectId(history.getPjProjectsId());
            punchListHistoryReq.add(createPunchListHistory(history));
            Log.d("MAna****", "punchListHistoryAPICall: " + history.getPunchListAuditsMobileId() + " PUnhc id " + history.getPunchListId());
        }
        Log.d(TAG, "History call 2: ");
        punchListHistoryRequest.setPunchListHistories(punchListHistoryReq);
        mPunchListProvider.getPunchListHistories(punchListHistoryRequest, new ProviderResult<List<PunchListHistoryDb>>() {
            @Override
            public void success(List<PunchListHistoryDb> result) {
                Log.e(TAG, "Punchlist called success: " + result);

                if (result != null) {
                    for (PunchListHistoryDb historyDb : result) {
                        TransactionLogMobile transactionLogMobileList = ((PronovosApplication) getApplicationContext()).getDaoSession().
                                getTransactionLogMobileDao().queryBuilder().where(
                                        TransactionLogMobileDao.Properties.Status.eq(SyncDataEnum.NOTSYNC.ordinal()),
                                        TransactionLogMobileDao.Properties.UsersId.eq(historyDb.getUserId()),
                                        TransactionLogMobileDao.Properties.MobileId.eq(historyDb.getPunchListAuditsMobileId())
                                ).orderAsc(TransactionLogMobileDao.Properties.CreateDate).limit(1).list().get(0);
                        transactionLogMobileList.setStatus(SyncDataEnum.SYNC.ordinal());
                        deleteSyncData(transactionLogMobileList);
                    }
                }

//                isHistoryInprogressCount = 0;

              /*if(mPunchListRepository.getPunchListForStatusCheck(transactionLogMobile.getUsersId()).size() == 0){

              }*/

//                transactionLogMobile.setStatus(SyncDataEnum.SYNC.ordinal());
//                deleteSyncData(transactionLogMobile);
                TransactionLogUpdate transactionLogUpdate = new TransactionLogUpdate();
                transactionLogUpdate.setTransactionModuleEnum(PUNCHLIST_HISTORY);
                EventBus.getDefault().post(transactionLogUpdate); //TODO: Need to check

                if (!mPunchListRepository.isNonSyncPunchListHistoryAvailable(transactionLogMobile.getUsersId())
                        && mPunchListRepository.getNonSyncedRejectReasonAttachments(transactionLogMobile.getUsersId()).size() <= 0
                        && mPunchListRepository.isNonSyncPunchListAvailable(transactionLogMobile.getUsersId())) {
                    //  Log.i(TAG, "Punchlist called success: drawing " + "");
                    List<TransactionLogMobile> transactionLogMobileList = ((PronovosApplication) getApplicationContext()).getDaoSession().getTransactionLogMobileDao().queryBuilder().where(
                                    TransactionLogMobileDao.Properties.Status.eq(SyncDataEnum.NOTSYNC.ordinal()), TransactionLogMobileDao.Properties.UsersId.eq(transactionLogMobile.getUsersId()))
                            .orderAsc(TransactionLogMobileDao.Properties.CreateDate).list();
                    if (transactionLogMobileList.size() == 0) {
                        transactionLogMobileList = ((PronovosApplication) getApplicationContext()).getDaoSession().getTransactionLogMobileDao().queryBuilder().where(
                                        TransactionLogMobileDao.Properties.Status.eq(SyncDataEnum.SYNC_FAILED.ordinal()), TransactionLogMobileDao.Properties.UsersId.eq(transactionLogMobile.getUsersId()))
                                .orderAsc(TransactionLogMobileDao.Properties.CreateDate).list();
                    }
                    for (int i = 0; i < transactionLogMobileList.size(); i++) {
                        LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(getApplicationContext()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
                        if (loginResponse != null) {
                            callAPI(transactionLogMobileList.get(i), mPronovosSyncData, loginResponse);
                        }
                    }
//                    new DrawingWorker(transactionLogMobile, mPronovosSyncData, getApplicationContext()).doTransaction();
                } else {
                    //     Log.i(TAG, "Punchlist called success: not drawing " + "");
                    ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                onAccessTokenFail(transactionLogMobile, mPronovosSyncData);
            }

            @Override
            public void failure(String message) {
                transactionLogMobile.setStatus(SyncDataEnum.SYNC_FAILED.ordinal());
                mPronovosSyncData.update(transactionLogMobile);
//                ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();
            }
        });
    }

    /**
     * To create PunchList history object.
     *
     * @param punchListHistoryDb an object of not sync punch list on server.
     * @return an object of PunchList history.
     */
    private PunchListHistory createPunchListHistory(PunchListHistoryDb punchListHistoryDb) {
        PunchListHistory punchListHistory = new PunchListHistory();
        punchListHistory.setPunchListAuditsId(punchListHistoryDb.getPunchListAuditsId());
        punchListHistory.setPunchListAuditsMobileId(String.valueOf(punchListHistoryDb.getPunchListAuditsMobileId()));
        punchListHistory.setPunchListsId(String.valueOf(punchListHistoryDb.getPunchListId()));
//        punchListHistory.se(punchlistdb.getDeletedAt() != null ? DateFormatter.formatDateTimeHHForService(punchlistdb.getDeletedAt()) : "");
        punchListHistory.setCreatedAt(DateFormatter.currentDateIntoMMDDYYY(punchListHistoryDb.getCreatedAt()));
        punchListHistory.setCreatedTimestamp(punchListHistoryDb.getCreatedTimestamp());
        punchListHistory.setCreatedBy(punchListHistoryDb.getCreatedBy());
        punchListHistory.setComments(punchListHistoryDb.getComments());
        punchListHistory.setStatus(punchListHistoryDb.getStatus());
        Log.d(TAG, "History call 3: " + punchListHistoryDb.getPunchListId());
        List<PunchListRejectReasonAttachments> attachments = mPunchListRepository.getSyncedRejectReasonAttachments(Long.valueOf(punchListHistoryDb.getPunchListAuditsMobileId()));
        List<Attachments> attachmentsList = new ArrayList<>();
        for (PunchListRejectReasonAttachments punchListAttachment :
                attachments) {
            if (punchListAttachment != null) {
                Attachments attachment = new Attachments();
                if (punchListAttachment.getRejectAttachmentId() != null) {
                    attachment.setAttachmentsId(punchListAttachment.getRejectAttachmentId());
                } else {
                    attachment.setAttachmentsId(0);

                }
                attachment.setAttachmentsIdMobile((int) (long) punchListAttachment.getRejectAttachmentIdMobile());
                Log.d(TAG, "createPunchListHistory: " + punchListAttachment.getAttachmentPath());
                attachment.setAttachPath(punchListAttachment.getAttachmentPath());
                attachment.setDeletedAt(punchListAttachment.getDeletedAt() != null ? DateFormatter.formatDateTimeHHForService(punchListAttachment.getDeletedAt()) : "");
                attachmentsList.add(attachment);
            }
        }
        punchListHistory.setAttachments(attachmentsList);

        return punchListHistory;
    }

    /**
     * To create WeatherReports object.
     *
     * @param weatherReport an object of WeatherReport.
     * @return an object of WeatherReports.
     */
    private WeatherReports createWeatherReport(WeatherReport weatherReport) {

        WeatherReports weatherReports = new WeatherReports();
        weatherReports.setConditions(weatherReport.getConditions());
        weatherReports.setImpact(weatherReport.getImpact());
        weatherReports.setNotes(weatherReport.getNotes());
        weatherReports.setProjectId(String.valueOf(weatherReport.getProjectId()));
        weatherReports.setReportDate(DateFormatter.formatDateTimeHHForService(weatherReport.getReportDate()));

        return weatherReports;
    }

    private void photoApiCallForDelete(TransactionLogMobile transactionLogMobile, TransactionLogMobileDao mPronovosSyncData) {
        Log.e(TAG, "photoApiCallForDelete: getMobileId " + transactionLogMobile.getMobileId());
        PhotosMobile photosMobile = projectsProvider.getPhotoDetail(transactionLogMobile.getUsersId(), transactionLogMobile.getMobileId(), transactionLogMobile.getServerId());
        if (photosMobile != null && photosMobile.getDeletedAt() != null && !photosMobile.getIsSync()) {
            callPhotoRequest(photosMobile, transactionLogMobile, mPronovosSyncData);
            Log.i(TAG, "photoApiCallForDelete: " + photosMobile.getPjPhotosFolderId());
        } else {
            Log.i(TAG, "Transaction Worker photoAPICall: fail");
            updateTransactionLogMobile(transactionLogMobile, mPronovosSyncData);

        }
    }

    /**
     * To determine the calling for image upload to AWS server or image link to pronovos server.
     *
     * @param transactionLogMobile used to update the status in Transaction table.
     * @param mPronovosSyncData    used to update Transaction table.
     */
    private void photoAPICall(TransactionLogMobile transactionLogMobile, TransactionLogMobileDao mPronovosSyncData) {
        PhotosMobile photosMobile = projectsProvider.getPhotoDetail(transactionLogMobile.getUsersId(), transactionLogMobile.getMobileId(), transactionLogMobile.getServerId());

        Log.i(TAG, "photoAPICall: " + photosMobile);
        if (photosMobile != null && photosMobile.getPjPhotosFolderId() != 0 && photosMobile.getPjPhotosId() == 0) {
            Log.i(TAG, "Transaction Worker photoAPICall: " + photosMobile.getPjPhotosFolderId());
            if (photosMobile.getIsawsSync()) {
                callPhotoRequest(photosMobile, transactionLogMobile, mPronovosSyncData);
            } else {
                callAwsSync(photosMobile, transactionLogMobile, mPronovosSyncData);
            }
        } else if (photosMobile != null && photosMobile.getPjPhotosFolderId() != 0 && !photosMobile.getIsSync() && photosMobile.getPjPhotosId() != 0) {
            callUpdatePhotoAPI(photosMobile, transactionLogMobile, mPronovosSyncData);
        } else {
            //    Log.i(TAG, "Transaction Worker photoAPICall: fail");
            updateTransactionLogMobile(transactionLogMobile, mPronovosSyncData);

        }
    }

    private void callUpdatePhotoAPI(PhotosMobile photosMobile, TransactionLogMobile transactionLogMobile,
                                    TransactionLogMobileDao mPronovosSyncData) {
        UpdatePhotoDetail updatePhotoDetail = new UpdatePhotoDetail();
        updatePhotoDetail.setPhoto_description(photosMobile.getDescriptions());
        List<Photo_tags> photo_tags = new ArrayList<>();
        List<Taggables> selectedImageTags = projectsProvider.getAllTaggables(photosMobile.getPjPhotosId(), photosMobile.getPjPhotosIdMobile());
        for (Taggables imagetag :
                selectedImageTags) {
            Photo_tags photoTag = new Photo_tags();
            photoTag.setKeyword(imagetag.getTagName());
            photo_tags.add(photoTag);
        }
        updatePhotoDetail.setPhoto_tags(photo_tags);
        updatePhotoDetail.setAlbum_id(photosMobile.getPjPhotosFolderId());
        updatePhotoDetail.setPhoto_id(photosMobile.getPjPhotosId());
        transactionLogMobile.setStatus(SyncDataEnum.PROCESSING.ordinal());
        mPronovosSyncData.update(transactionLogMobile);
        mUpdatePhotoDetailsProvider.updatePhotoDetails(updatePhotoDetail, photosMobile, new ProviderResult<UpdatePhotoDetailResponse>() {
            @Override
            public void success(UpdatePhotoDetailResponse result) {
                transactionLogMobile.setStatus(SyncDataEnum.SYNC.ordinal());
                deleteSyncData(transactionLogMobile);
                TransactionLogUpdate transactionLogUpdate = new TransactionLogUpdate();
                transactionLogUpdate.setTransactionModuleEnum(TransactionModuleEnum.PHOTO);
                EventBus.getDefault().post(transactionLogUpdate);
                doWork();

            }

            @Override
            public void AccessTokenFailure(String message) {
                onAccessTokenFail(transactionLogMobile, mPronovosSyncData);
            }

            @Override
            public void failure(String message) {
                transactionLogMobile.setStatus(SyncDataEnum.SYNC_FAILED.ordinal());
                mPronovosSyncData.update(transactionLogMobile);
//                ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();
            }
        });


    }

    /**
     * To upload image on AWS server.
     *
     * @param photosMobile         used to get photo name.
     * @param transactionLogMobile used to update the status in Transaction table.
     * @param mPronovosSyncData    used to update Transaction table.
     */
    private void callAwsSync(PhotosMobile photosMobile, TransactionLogMobile transactionLogMobile, TransactionLogMobileDao mPronovosSyncData) {
        SignedUrlRequest signedUrlRequest = new SignedUrlRequest(UrlTypeEnum.PHOTOS.toString());
        transactionLogMobile.setStatus(SyncDataEnum.PROCESSING.ordinal());
        mPronovosSyncData.update(transactionLogMobile);
        projectsProvider.getSignedUrl(signedUrlRequest, new ProviderResult<SignedUrlResponse>() {
            @Override
            public void success(SignedUrlResponse result) {
                String key = result.getData().getFormattributes().getKey();
                result.getData().getFormattributes().setKey(UrlTypeEnum.PHOTOS + "/" + key);
                mFileUploadProvider.setUrl(result.getData().getFormaction().getAction());
                String completePath = getApplicationContext().getFilesDir().getAbsolutePath() + "/Pronovos/" + photosMobile.getPhotoName();

                File file = new File(completePath);
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


                // Upload photo to the server
                mFileUploadProvider.uploadFile(result.getData().getFormaction().getAction(), finalRequestBody, photosMobile, new ProviderResult<Boolean>() {
                    @Override
                    public void success(Boolean result) {
                        //   Log.i("dowork:", "uploadFile: uploadFile success");
                        if (result) {
                            callPhotoRequest(photosMobile, transactionLogMobile, mPronovosSyncData);
                        } else {
                            transactionLogMobile.setStatus(SyncDataEnum.SYNC_FAILED.ordinal());
                            mPronovosSyncData.update(transactionLogMobile);
                            ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();

                        }
                    }

                    @Override
                    public void AccessTokenFailure(String message) {
                        //   Log.i("dowork:", "uploadFile: getSignedUrl  uploadFile AccessTokenFailure");
                        onAccessTokenFail(transactionLogMobile, mPronovosSyncData);
                    }

                    @Override
                    public void failure(String message) {
                        //  Log.i("dowork:", "uploadFile: getSignedUrl  uploadFile failure");
                        transactionLogMobile.setStatus(SyncDataEnum.SYNC_FAILED.ordinal());
                        mPronovosSyncData.update(transactionLogMobile);
//                        ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();

                    }
                });
            }

            @Override
            public void AccessTokenFailure(String message) {
                //   Log.i("dowork:", "callAwsSync: AccessTokenFailure");
                onAccessTokenFail(transactionLogMobile, mPronovosSyncData);

            }

            @Override
            public void failure(String message) {
                Log.i("dowork:", "callAwsSync: failure");
                transactionLogMobile.setStatus(SyncDataEnum.SYNC_FAILED.ordinal());
                mPronovosSyncData.update(transactionLogMobile);
                //  Log.i("dowork:", "callAwsSync: failure updated" + SyncDataEnum.SYNC_FAILED.ordinal());
//                ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();

            }
        });
    }

    /**
     * To upload image link on pronovos server.
     *
     * @param photosMobile         an object of PhotosMobile.
     * @param transactionLogMobile used to update the status in Transaction table.
     * @param mPronovosSyncData    used to update Transaction table.
     */
    private void callPhotoRequest(PhotosMobile photosMobile, TransactionLogMobile transactionLogMobile, TransactionLogMobileDao mPronovosSyncData) {
        PhotoRequest albumRequest = new PhotoRequest(photosMobile.getPjPhotosFolderId());
        List<Photo> photoList = new ArrayList<>();
        Log.i("dowork:", "callPhotoRequest: ");
        albumRequest.setMinPhotoId(projectsProvider.getMINPhotoID(photosMobile.getPjPhotosFolderId(), photosMobile.getPjProjectsId()));

        photoList.add(createPhotoRequestObject(photosMobile));

        projectsProvider.getAlbumPhoto(photoList, albumRequest, photosMobile.getPjProjectsId(), photosMobile.getPjPhotosFolderMobileId(), new ProviderResult<List<PhotosMobile>>() {
            @Override
            public void success(List<PhotosMobile> photosMobileListResult) {
                transactionLogMobile.setStatus(SyncDataEnum.SYNC.ordinal());
                DeleteQuery<TransactionLogMobile> pronovosSyncDataDeleteQuery = ((PronovosApplication) getApplicationContext()).getDaoSession()
                        .queryBuilder(TransactionLogMobile.class)
                        .where(TransactionLogMobileDao.Properties.SyncId.eq(transactionLogMobile.getSyncId()))
                        .buildDelete();
                pronovosSyncDataDeleteQuery.executeDeleteWithoutDetachingEntities();
                TransactionLogUpdate transactionLogUpdate = new TransactionLogUpdate();
                transactionLogUpdate.setTransactionModuleEnum(TransactionModuleEnum.PHOTO);
                EventBus.getDefault().post(transactionLogUpdate);
                doWork();
                Log.i("dowork:", "success: callPhotoRequest ");

            }

            @Override
            public void AccessTokenFailure(String message) {
                Log.i("dowork:", "AccessTokenFailure: callPhotoRequest");
                onAccessTokenFail(transactionLogMobile, mPronovosSyncData);
            }

            @Override
            public void failure(String message) {
                Log.i("dowork:", "failure: callPhotoRequest");
                transactionLogMobile.setStatus(SyncDataEnum.SYNC_FAILED.ordinal());
                mPronovosSyncData.update(transactionLogMobile);
//                ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();
            }
        });
    }

    /**
     * Create Photo object for photo list api.
     *
     * @param photosMobile an object of PhotosMobile from database.
     * @return an object of Photo.
     */
    private Photo createPhotoRequestObject(PhotosMobile photosMobile) {

        Photo photo = new Photo();
        photo.setAlbum_id(photosMobile.getPjPhotosFolderId());

        photo.setDate_taken(photosMobile.getDateTaken() != null ? DateFormatter.formatDateTimeForService(photosMobile.getDateTaken()) : "");
        photo.setPhoto_description(photosMobile.getDescriptions());
        photo.setPhoto_location(photosMobile.getPhotoLocation());
        photo.setPhoto_name(photosMobile.getPhotoName());
        photo.setDeletedAt(photosMobile.getDeletedAt() != null ? DateFormatter.formatDateTimeForService(photosMobile.getDeletedAt()) : "");
        photo.setPhoto_size(photosMobile.getSize());
        photo.setPhoto_tags(new ArrayList<>());
        photo.setPj_photos_id(photosMobile.getPjPhotosId());
        photo.setPj_photos_id_mobile(photosMobile.getPjPhotosIdMobile());
        List<Taggables> taggables = ((PronovosApplication) getApplicationContext()).getDaoSession().getTaggablesDao().queryBuilder().where(TaggablesDao.Properties.TaggableIdMobile.eq(photosMobile.getPjPhotosIdMobile())).list();
        List<Photo_tags> tagsList = new ArrayList<>();
        for (Taggables tag : taggables) {
            Photo_tags photo_tags = new Photo_tags();
            photo_tags.setKeyword(tag.getTagName());
            tagsList.add(photo_tags);
        }
        photo.setPhoto_tags(tagsList);

        return photo;
    }

    /**
     * To call the crew reports api.
     *
     * @param transactionLogMobile used to update the status in Transaction table.
     * @param mPronovosSyncData    used to update Transaction table.
     * @param crewList             an object of CrewList.
     */
    private void crewAPICall(TransactionLogMobile transactionLogMobile, TransactionLogMobileDao mPronovosSyncData, CrewList crewList) {
        List<CrewReport> crewReportList = new ArrayList<>();

        crewReportList.add(createCrewReport(crewList));

        CrewReportRequest crewReportRequest = new CrewReportRequest();
        crewReportRequest.setCrewReport(crewReportList);
        crewReportRequest.setProjectId(crewList.getProjectId());
        crewReportRequest.setReportDate(DateFormatter.formatDateTimeHHForService(crewList.getCreatedAt()));

        transactionLogMobile.setStatus(SyncDataEnum.PROCESSING.ordinal());

        mPronovosSyncData.update(transactionLogMobile);

        mCrewReportProvider.getCrewReports(crewReportRequest, crewList.getCreatedAt(), new ProviderResult<List<CrewList>>() {
            @Override
            public void success(List<CrewList> result) {
                transactionLogMobile.setStatus(SyncDataEnum.SYNC.ordinal());
                deleteSyncData(transactionLogMobile);
                TransactionLogUpdate transactionLogUpdate = new TransactionLogUpdate();
                transactionLogUpdate.setTransactionModuleEnum(TransactionModuleEnum.CREW);
                EventBus.getDefault().post(transactionLogUpdate);
                doWork();
            }

            @Override
            public void AccessTokenFailure(String message) {
                onAccessTokenFail(transactionLogMobile, mPronovosSyncData);
            }

            @Override
            public void failure(String message) {
                transactionLogMobile.setStatus(SyncDataEnum.SYNC_FAILED.ordinal());
                mPronovosSyncData.update(transactionLogMobile);
//                ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();
            }
        });
    }

    /**
     * Create CrewReport object for crew api call.
     *
     * @param crewList an object of CrewList.
     * @return an object of CrewReport.
     */
    private CrewReport createCrewReport(CrewList crewList) {

        CrewReport crewReport = new CrewReport();
        crewReport.setProjectId(String.valueOf(crewList.getProjectId()));
        crewReport.setCreatedAt(DateFormatter.formatDateTimeHHForService(crewList.getCreatedAt()));
        crewReport.setApprentice(String.valueOf(crewList.getApprentice()));
        crewReport.setCompanyId(String.valueOf(crewList.getCompanyId()));
        crewReport.setCompanyname(crewList.getCompanyName());
        crewReport.setCrewReportId(String.valueOf(crewList.getCrewReportId()));
        crewReport.setCrewReportIdMobile(String.valueOf(crewList.getCrewReportIdMobile()));
        crewReport.setDeletedAt(crewList.getDeletedAt() != null ? DateFormatter.formatDateTimeHHForService(crewList.getDeletedAt()) : "");
        crewReport.setForeman(String.valueOf(crewList.getForeman()));
        crewReport.setJourneyman(String.valueOf(crewList.getJourneyman()));
        crewReport.setSupt(String.valueOf(crewList.getSupt()));
        crewReport.setTrade(String.valueOf(crewList.getTrade()));
        crewReport.setTradesId(String.valueOf(crewList.getTradesId()));
        crewReport.setType(String.valueOf(crewList.getType()));

        return crewReport;
    }

    /**
     * To call the project's album api.
     *
     * @param transactionLogMobile used to update the status in Transaction table.
     * @param mPronovosSyncData    used to update Transaction table.
     */
    private void albumAPICall(TransactionLogMobile transactionLogMobile, TransactionLogMobileDao mPronovosSyncData) {
        PhotoFolder photoFolder = projectsProvider.getAlbumDetail(transactionLogMobile.getUsersId(), transactionLogMobile.getMobileId(), transactionLogMobile.getServerId());
        List<Album> list = new ArrayList<>();
        list.add(new Album(DateFormatter.formatDateTimeForService(photoFolder.getCreatedAt()), DateFormatter.formatDateTimeForService(photoFolder.getDeletedAt()), DateFormatter.formatDateTimeForService(photoFolder.getUpdatedAt()), String.valueOf(photoFolder.getPjPhotosFolderMobileId()), String.valueOf(photoFolder.getPjPhotosFolderId()), photoFolder.getName()));
        AlbumRequest albumRequest = new AlbumRequest(list, photoFolder.getPjProjectsId());
        transactionLogMobile.setStatus(SyncDataEnum.PROCESSING.ordinal());
        mPronovosSyncData.update(transactionLogMobile);

        projectsProvider.getProjectAlbum(albumRequest, new ProviderResult<List<PhotoFolder>>() {
            @Override
            public void success(List<PhotoFolder> result) {
                transactionLogMobile.setStatus(SyncDataEnum.SYNC.ordinal());
                deleteSyncData(transactionLogMobile);
                TransactionLogUpdate transactionLogUpdate = new TransactionLogUpdate();
                transactionLogUpdate.setTransactionModuleEnum(TransactionModuleEnum.ALBUM);
                EventBus.getDefault().post(transactionLogUpdate);
                doWork();

            }

            @Override
            public void AccessTokenFailure(String message) {
                onAccessTokenFail(transactionLogMobile, mPronovosSyncData);
            }

            @Override
            public void failure(String message) {
                transactionLogMobile.setStatus(SyncDataEnum.SYNC_FAILED.ordinal());
                mPronovosSyncData.update(transactionLogMobile);
//                ((PronovosApplication) getApplicationContext()).setupAndStartWorkManager();
            }
        });
    }

    /**
     * Redirect user to Login screen in case of "AccessTokenFailure".
     *
     * @param transactionLogMobile used to update the status in Transaction table.
     * @param mPronovosSyncData    used to update Transaction table.
     */
    private void onAccessTokenFail(TransactionLogMobile transactionLogMobile, TransactionLogMobileDao mPronovosSyncData) {
        transactionLogMobile.setStatus(SyncDataEnum.SYNC_FAILED.ordinal());
        mPronovosSyncData.update(transactionLogMobile);
        getApplicationContext().startActivity(new Intent(getApplicationContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        SharedPref.getInstance(getApplicationContext()).writePrefs(SharedPref.SESSION_DETAILS, null);
        SharedPref.getInstance(getApplicationContext()).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
        if (getApplicationContext() instanceof Activity) {
            ((Activity) getApplicationContext()).finish();
        }

    }

    /**
     * Delete the sync data from Transaction table.
     *
     * @param transactionLogMobile used to apply conditions for database operations.
     */
    private void deleteSyncData(TransactionLogMobile transactionLogMobile) {
        DeleteQuery<TransactionLogMobile> pronovosSyncDataDeleteQuery = ((PronovosApplication) getApplicationContext()).getDaoSession().queryBuilder(TransactionLogMobile.class)
                .where(TransactionLogMobileDao.Properties.SyncId.eq(transactionLogMobile.getSyncId()))
                .buildDelete();
        pronovosSyncDataDeleteQuery.executeDeleteWithoutDetachingEntities();
    }

    /**
     * Delete the sync data from Transaction table.
     *
     * @param transactionLogMobile used to apply conditions for database operations.
     */
    private void updateTransactionLogMobile(TransactionLogMobile transactionLogMobile, TransactionLogMobileDao mPronovosSyncData) {
        transactionLogMobile.setStatus(SyncDataEnum.SYNC_FAILED.ordinal());
        mPronovosSyncData.update(transactionLogMobile);
    }

    private void callTransaction() {
        LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(getApplicationContext()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        TransactionLogMobileDao mPronovosSyncData = ((PronovosApplication) getApplicationContext()).getDaoSession().getTransactionLogMobileDao();
        int userId = loginResponse.getUserDetails().getUsers_id();

        List<TransactionLogMobile> transactionLogMobileList = ((PronovosApplication) getApplicationContext()).getDaoSession().
                getTransactionLogMobileDao().queryBuilder().where(
                        TransactionLogMobileDao.Properties.Status.eq(SyncDataEnum.NOTSYNC.ordinal()), TransactionLogMobileDao.Properties.UsersId.eq(userId))
                .orderAsc(TransactionLogMobileDao.Properties.CreateDate).list();

        if (transactionLogMobileList.size() == 0) {

            transactionLogMobileList = ((PronovosApplication) getApplicationContext()).getDaoSession().getTransactionLogMobileDao().queryBuilder().where(
                            TransactionLogMobileDao.Properties.Status.eq(SyncDataEnum.SYNC_FAILED.ordinal()), TransactionLogMobileDao.Properties.UsersId.eq(userId))
                    .orderAsc(TransactionLogMobileDao.Properties.CreateDate).list();

            Log.i(TAG, "Transaction SYNC_FAILED   Worker worker Start" + transactionLogMobileList.size());
        }
        for (int i = 0; i < transactionLogMobileList.size(); i++) {

            callAPI(transactionLogMobileList.get(i), mPronovosSyncData, loginResponse);
        }
    }

}
