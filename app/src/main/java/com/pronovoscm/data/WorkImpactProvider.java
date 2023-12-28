package com.pronovoscm.data;

import android.content.Intent;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.activity.LoginActivity;
import com.pronovoscm.api.WorkImpactApi;
import com.pronovoscm.model.UrlTypeEnum;
import com.pronovoscm.model.request.signurl.SignedUrlRequest;
import com.pronovoscm.model.request.workimpact.WorkImpactRequest;
import com.pronovoscm.model.response.AbstractCallback;
import com.pronovoscm.model.response.ErrorResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.signedurl.SignedUrlResponse;
import com.pronovoscm.model.response.workimpact.WorkImpactResponse;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.WorkImpact;
import com.pronovoscm.persistence.domain.WorkImpactAttachments;
import com.pronovoscm.persistence.repository.WorkImpactRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.SharedPref;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class WorkImpactProvider {


    private final String TAG = WorkImpactProvider.class.getName();
    private final WorkImpactApi mWorkImpactApi;
    private PronovosApplication context;
    NetworkStateProvider networkStateProvider;
    private DaoSession daoSession;
    private LoginResponse loginResponse;
    private WorkImpactRepository mWorkImpactRepository;
    private FileUploadProvider mFileUploadProvider;
    private boolean isLoading;


    public WorkImpactProvider(NetworkStateProvider networkStateProvider, WorkImpactApi workImpactApi, DaoSession daoSession, WorkImpactRepository workImpactRepository,FileUploadProvider uploadProvider) {
        this.context = PronovosApplication.getContext();
        context.setUrl(Constants.BASE_API_URL);
        this.mWorkImpactApi = workImpactApi;
        this.networkStateProvider = networkStateProvider;
        this.daoSession = daoSession;
        mWorkImpactRepository = workImpactRepository;
        this.mFileUploadProvider = uploadProvider;
    }


    public void getWorkImpacts(WorkImpactRequest workImpactRequest, Date date, final ProviderResult<List<WorkImpact>> callback) {

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<WorkImpactResponse> assigneeAPI = mWorkImpactApi.getWorkImpacts(headers, workImpactRequest);

            assigneeAPI.enqueue(new AbstractCallback<WorkImpactResponse>() {
                @Override
                protected void handleFailure(Call<WorkImpactResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                    callback.success(mWorkImpactRepository.getWorkImpact(workImpactRequest.getProjectId(),date));
                }

                @Override
                protected void handleError(Call<WorkImpactResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<WorkImpactResponse> response) {
                    if (response.body() != null) {
                        WorkImpactResponse workImpactResponse = null;
                        try {
                            workImpactResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (workImpactResponse != null && workImpactResponse.getStatus() == 200 && (workImpactResponse.getWorkImpactData().getResponsecode() == 101 || workImpactResponse.getWorkImpactData().getResponsecode() == 102)) {
                            List<WorkImpact> workImpacts = mWorkImpactRepository.doUpdateWorkImpactTable(workImpactResponse.getWorkImpactData().getWorkImpactsReport(), date, workImpactRequest.getProjectId());
                            callback.success(workImpacts);
                        } else if (workImpactResponse != null) {
                            callback.failure(workImpactResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.success(mWorkImpactRepository.getWorkImpact(workImpactRequest.getProjectId(),date));
        }

    }

    /**
     * Syc offline workimpact to server
     *
     * @param projectId
     * @param workImpactDate at which work impact is created
     */
    public void syncWorkImpactsToServer(int projectId, Date workImpactDate) {
        if (!NetworkService.isNetworkAvailable(context)) {
            return;
        }
        isLoading=false;
//        List<WorkImpact> nonSyncWorkImpactList = mWorkImpactRepository.getNonSyncWorkImpactList(projectId, workImpactDate);
//
//        // Upload attachments if there is any in work impact
//        if (nonSyncWorkImpactList.size() > 0) {
//                        // Sync if all the attachments are uploaded i.e. nonSyncWorkImpacts whose attachments are synced to AWS
//                        // and ready to upload to server
//                        else if (nonSyncedAttachments.size() == 0) {
//                            // TODO: 20/11/18 sync work impact
//
//                }
//            });
//        }

        List<WorkImpactAttachments> nonSyncedAttachments = mWorkImpactRepository.getNonSyncedAttachments();

        // Upload attachments if there is any in work detail
        if (nonSyncedAttachments.size() > 0) {
            SignedUrlRequest signedUrlRequest = new SignedUrlRequest(UrlTypeEnum.REPORT_FILES.toString());
            getSignedUrl(signedUrlRequest, new ProviderResult<SignedUrlResponse>() {
                @Override
                public void success(SignedUrlResponse result) {
                    String key = result.getData().getFormattributes().getKey();
                    result.getData().getFormattributes().setKey("report_files/" + key);
                    mFileUploadProvider.setUrl(result.getData().getFormaction().getAction());

                    // iterate all non synced work detail
                    for (WorkImpactAttachments workImpactAttachments : nonSyncedAttachments) {
                        WorkImpact workDetail1 = mWorkImpactRepository.getWorkImpactItem(workImpactAttachments.getWorkImpactReportIdMobile());

                        List<WorkImpactAttachments> nonSyncedAttachments = mWorkImpactRepository.getNonSyncedWorkImpactAttachments(workImpactAttachments.getWorkImpactReportIdMobile());

                        // Non synced work details list with non synced attachment
                        List<WorkImpact> nonSyncWorkDetails = mWorkImpactRepository.getNonSyncWorkImpactWithNonSyncedAttachments(projectId, workImpactDate);

                        if (nonSyncedAttachments.size() > 0) {

                            uploadWorkImpactAttachment(result, workDetail1, workImpactAttachments);

                        }
                        // Sync if all the attachments are uploaded i.e. nonSyncWorkDetails whose attachments are synced to AWS
                        // and ready to upload to server
                        else if (nonSyncedAttachments.size() == 0) {
                            // TODO: 20/11/18 sync work detail
                            // update attachment status to
                            workDetail1.setIsAttachmentSync(true);
                            mWorkImpactRepository.updateWorkImpact(workDetail1);
//                            workDetailsRequest.setReportDate(DateFormatter.formatDateTimeHHForService(workDetailsDate));
//                            getWorkDetails()
                        }
                    }
                }

                @Override
                public void AccessTokenFailure(String message) {
                }

                @Override
                public void failure(String message) {
                }
            });
        } else {
            List<WorkImpact> workDetails = mWorkImpactRepository.getNonSyncWorkImpactWithNonSyncedAttachments(projectId, workImpactDate);
            for (WorkImpact w : workDetails) {
                w.setIsAttachmentSync(true);
                mWorkImpactRepository.updateWorkImpact(w);
            }

            WorkImpactRequest workDetailsRequest = new WorkImpactRequest();
            workDetailsRequest.setProjectId(projectId);
            workDetailsRequest.setReportDate(DateFormatter.formatDateTimeHHForService(workImpactDate));
            workDetailsRequest.setWorkImpactsReport(mWorkImpactRepository.getNonSyncWorkImpactyncAttachmentList(projectId, workImpactDate));
          //  Log.e("syncWorkImpactsToServer", "callWorkDetailsReport: getWorkImpacts ");
            getWorkImpacts(workDetailsRequest, workImpactDate, new ProviderResult<List<WorkImpact>>() {
                @Override
                public void success(List<WorkImpact> result) {

                }

                @Override
                public void AccessTokenFailure(String message) {

                }

                @Override
                public void failure(String message) {

                }
            });
        }
    }



    /**
     * Upload work impact attachment to AWS server to get its url
     *
     * @param result
     * @param workImpact
     * @param attachment
     */
    private void uploadWorkImpactAttachment(SignedUrlResponse result, WorkImpact workImpact, WorkImpactAttachments attachment) {
        // iterate all non synced attachment of work impact
//        for (WorkImpactAttachments attachment : nonSyncedAttachments) {
            File file = new File(attachment.getAttachmentPath());
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
            mFileUploadProvider.uploadWorkImpactAttachmentFile(result.getData().getFormaction().getAction(), finalRequestBody, attachment, mWorkImpactRepository, new ProviderResult<Boolean>() {
                @Override
                public void success(Boolean result) {
                    if (result) {

                        List<WorkImpactAttachments> nonSyncedAttachments = mWorkImpactRepository.getNonSyncedWorkImpactAttachments(workImpact.getWorkImpactReportIdMobile());

                        // If all the non synced attachments have been synced then call the photo list service to update the photo list
                        if (nonSyncedAttachments.size() == 0) {
                            workImpact.setIsAttachmentSync(true);
                            mWorkImpactRepository.updateWorkImpact(workImpact);

//                            WorkImpactRequest workImpactRequest = new WorkImpactRequest();
//                            workImpactRequest.setEqCategoriesId(workImpact.getEqCategoriesId());
//                            workImpactRequest.setReportDate(DateFormatter.formatDateTimeHHForService(workImpact.getCreatedAt()));
//                            workImpactRequest.setWorkImpactsReport(mWorkImpactRepository.getNonSyncWorkImpactyncAttachmentList(workImpact.getEqCategoriesId(), workImpact.getCreatedAt()));
//                            Log.i("uploadWorkImpact", "callWorkImpactReport: ");
//                            getWorkImpacts(workImpactRequest, workImpact.getCreatedAt(), new ProviderResult<List<WorkImpact>>() {
//                                @Override
//                                public void success(List<WorkImpact> result) {
//
//                                }
//
//                                @Override
//                                public void AccessTokenFailure(String message) {
//
//                                }
//
//                                @Override
//                                public void failure(String message) {
//
//                                }
//                            });
//                            callPhotoRequest();
                        }


                        List<WorkImpactAttachments> nonSyncedAttachment = mWorkImpactRepository.getNonSyncedAttachments();
                        if (nonSyncedAttachment.size() == 0 && !isLoading) {
                            WorkImpactRequest workDetailsRequest = new WorkImpactRequest();
                            workDetailsRequest.setProjectId(workImpact.getProjectId());
                            workDetailsRequest.setReportDate(DateFormatter.formatDateTimeHHForService(workImpact.getCreatedAt()));
                            workDetailsRequest.setWorkImpactsReport(mWorkImpactRepository.getNonSyncWorkImpactyncAttachmentList(workImpact.getProjectId(), workImpact.getCreatedAt()));
                            //     Log.i("syncWorkImpactsToServer", "****** callWorkDetailsReport: getWorkImpacts ");
                            isLoading = true;
                            getWorkImpacts(workDetailsRequest, workImpact.getCreatedAt(), new ProviderResult<List<WorkImpact>>() {
                                @Override
                                public void success(List<WorkImpact> result) {
                                    isLoading = false;

                                }

                                @Override
                                public void AccessTokenFailure(String message) {
                                    isLoading = false;

                                }

                                @Override
                                public void failure(String message) {
                                    isLoading = false;

                                }
                            });
                        }
                    }
                }

                @Override
                public void AccessTokenFailure(String message) {
                    context.startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    SharedPref.getInstance(context).writePrefs(SharedPref.SESSION_DETAILS, null);
                    SharedPref.getInstance(context).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
//                                    context.finish();
                }

                @Override
                public void failure(String message) {
//                                    messageDialog.showMessageAlert(AlbumsPhotoActivity.this, message, getString(R.string.ok));
//                                messageDialog.showMessageAlert(AlbumsPhotoActivity.this, getString(R.string.failureMessage), getString(R.string.ok));

                }
            });
        }
//    }
    /**
     *
     * Service call to get albums according to album request with project id and list of album
     *
     * @param signedUrlRequest
     * @param projectResponseProviderResult
     */
    public void getSignedUrl(SignedUrlRequest signedUrlRequest, final ProviderResult<SignedUrlResponse> projectResponseProviderResult) {

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());

            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            Call<SignedUrlResponse> projectResponseCall = mWorkImpactApi.getSignedUrl(headers, signedUrlRequest);

            projectResponseCall.enqueue(new AbstractCallback<SignedUrlResponse>() {
                @Override
                protected void handleFailure(Call<SignedUrlResponse> call, Throwable throwable) {
                    projectResponseProviderResult.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<SignedUrlResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        projectResponseProviderResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        projectResponseProviderResult.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<SignedUrlResponse> response) {
                    if (response.body() != null) {
                        SignedUrlResponse signedUrlResponse = null;
                        try {
                            signedUrlResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (signedUrlResponse != null && signedUrlResponse.getStatus() == 200) {
                            projectResponseProviderResult.success(signedUrlResponse);
                        } else if (signedUrlResponse != null) {
                            projectResponseProviderResult.failure(signedUrlResponse.getMessage());
                        } else {
                            projectResponseProviderResult.failure("response null");
                        }
                    } else {
                        projectResponseProviderResult.failure("response null");
                    }
                }
            });

        }
    }

}
