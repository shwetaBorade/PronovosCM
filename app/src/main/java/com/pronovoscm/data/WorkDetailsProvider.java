package com.pronovoscm.data;

import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.activity.LoginActivity;
import com.pronovoscm.api.WorkDetailsApi;
import com.pronovoscm.model.request.signurl.SignedUrlRequest;
import com.pronovoscm.model.request.workdetails.WorkDetailsRequest;
import com.pronovoscm.model.response.AbstractCallback;
import com.pronovoscm.model.response.ErrorResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.signedurl.SignedUrlResponse;
import com.pronovoscm.model.response.workdetails.WorkDetailsResponse;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.WorkDetails;
import com.pronovoscm.persistence.domain.WorkDetailsAttachments;
import com.pronovoscm.persistence.repository.WorkDetailsRepository;
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

public class WorkDetailsProvider {


    private final String TAG = WorkDetailsProvider.class.getName();
    private final WorkDetailsApi mWorkDetailsApi;
    private PronovosApplication context;
    private NetworkStateProvider networkStateProvider;
    private DaoSession daoSession;
    private LoginResponse loginResponse;
    private WorkDetailsRepository mWorkDetailsRepository;
    private FileUploadProvider mFileUploadProvider;
    private boolean isLoading = false;


    public WorkDetailsProvider(NetworkStateProvider networkStateProvider, WorkDetailsApi workDetailsApi, DaoSession daoSession, WorkDetailsRepository workDetailsRepository, FileUploadProvider uploadProvider) {
        this.context = PronovosApplication.getContext();
        context.setUrl(Constants.BASE_API_URL);
        this.mWorkDetailsApi = workDetailsApi;
        this.networkStateProvider = networkStateProvider;
        this.daoSession = daoSession;
        mWorkDetailsRepository = workDetailsRepository;
        this.mFileUploadProvider = uploadProvider;
    }


    public void getWorkDetails(WorkDetailsRequest workDetailsRequest, Date date, final ProviderResult<List<WorkDetails>> callback) {


        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<WorkDetailsResponse> assigneeAPI = mWorkDetailsApi.getWorkDetails(headers, workDetailsRequest);

            assigneeAPI.enqueue(new AbstractCallback<WorkDetailsResponse>() {
                @Override
                protected void handleFailure(Call<WorkDetailsResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                    callback.success(mWorkDetailsRepository.getWorkDetails(workDetailsRequest.getProjectId(), date));
                }

                @Override
                protected void handleError(Call<WorkDetailsResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<WorkDetailsResponse> response) {
                    if (response.body() != null) {
                        WorkDetailsResponse workDetailsData = null;
                        try {
                            workDetailsData = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (workDetailsData != null && workDetailsData.getStatus() == 200 && (workDetailsData.getWorkDetailsData().getResponsecode() == 101 || workDetailsData.getWorkDetailsData().getResponsecode() == 102)) {
                            List<WorkDetails> workDetails = mWorkDetailsRepository.doUpdateWorkDetailsTable(workDetailsData.getWorkDetailsData().getWorkDetailsReport(), date, workDetailsRequest.getProjectId());
                            callback.success(workDetails);
                        } else if (workDetailsData != null) {
                            callback.failure(workDetailsData.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.success(mWorkDetailsRepository.getWorkDetails(workDetailsRequest.getProjectId(), date));
        }

    }


    /**
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
            Call<SignedUrlResponse> projectResponseCall = mWorkDetailsApi.getSignedUrl(headers, signedUrlRequest);

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

        } else {

        }
    }


    /**
     * Syc offline workdetail to server
     *
     * @param projectId
     * @param workDetailsDate at which work detail is created
     */
   /* public void syncWorkDetailsToServer(int projectId, Date workDetailsDate) {
        isLoading = false;
        if (!NetworkService.isNetworkAvailable(context)) {
            return;
        }
        List<WorkDetailsAttachments> nonSyncedAttachments = mWorkDetailsRepository.getNonSyncedAttachments();

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
                    for (WorkDetailsAttachments workDetailAttachments : nonSyncedAttachments) {
                        WorkDetails workDetail1 = mWorkDetailsRepository.getWorkDetailsItem(workDetailAttachments.getWorkDetailsReportIdMobile());

                        List<WorkDetailsAttachments> nonSyncedAttachments = mWorkDetailsRepository.getNonSyncedWorkDetailAttachments(workDetailAttachments.getWorkDetailsReportIdMobile());

                        // Non synced work details list with non synced attachment
                        List<WorkDetails> nonSyncWorkDetails = mWorkDetailsRepository.getNonSyncWorkDetailsWithNonSyncedAttachments(projectId, workDetailsDate);

                        if (nonSyncedAttachments.size() > 0) {

                            uploadWorkDetailAttachment(result, workDetail1, workDetailAttachments);

                        }
                        // Sync if all the attachments are uploaded i.e. nonSyncWorkDetails whose attachments are synced to AWS
                        // and ready to upload to server
                        else if (nonSyncedAttachments.size() == 0 ) {
                            // TODO: 20/11/18 sync work detail

                            // update attachment status to
                            workDetail1.setIsAttachmentSync(true);
                            mWorkDetailsRepository.updateWorkDetail(workDetail1);
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
            List<WorkDetails> workDetails = mWorkDetailsRepository.getNonSyncWorkDetailsWithNonSyncedAttachments(projectId, workDetailsDate);
            for (WorkDetails w : workDetails) {
                w.setIsAttachmentSync(true);
                mWorkDetailsRepository.updateWorkDetail(w);
            }

            WorkDetailsRequest workDetailsRequest = new WorkDetailsRequest();
            workDetailsRequest.setProjectId(projectId);
            workDetailsRequest.setReportDate(DateFormatter.formatDateTimeHHForService(workDetailsDate));
            workDetailsRequest.setWorkDetailsReport(mWorkDetailsRepository.getNonSyncWorkDetailSyncAttachmentList(projectId, workDetailsDate));
            Log.i("syncWorkImpactsToServer", "callWorkDetailsReport: ");
            getWorkDetails(workDetailsRequest, workDetailsDate, new ProviderResult<List<WorkDetails>>() {
                @Override
                public void success(List<WorkDetails> result) {

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
*/

    /**
     * Upload work detail attachment to AWS server to get its url
     *
     * @param result
     * @param workDetail
     * @param attachment
     */
    private void uploadWorkDetailAttachment(SignedUrlResponse result, WorkDetails workDetail, WorkDetailsAttachments attachment) {
        // iterate all non synced attachment of work detail
//        for (WorkDetailsAttachments attachment : nonSyncedAttachments) {

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
            mFileUploadProvider.uploadAttachmentFile(result.getData().getFormaction().getAction(), finalRequestBody, attachment, mWorkDetailsRepository, new ProviderResult<Boolean>() {
                @Override
                public void success(Boolean result) {
                    if (result) {

                        List<WorkDetailsAttachments> nonSyncedAttachments = mWorkDetailsRepository.getNonSyncedWorkDetailAttachments(workDetail.getWorkDetailsReportIdMobile());

                        // If all the non synced attachments have been synced then call the photo list service to update the photo list
                        if (nonSyncedAttachments.size() == 0) {
                            workDetail.setIsAttachmentSync(true);
                            mWorkDetailsRepository.updateWorkDetail(workDetail);
                        }

                        List<WorkDetailsAttachments> nonSyncedAttachment = mWorkDetailsRepository.getNonSyncedAttachments();
                        Log.i("uploadWorkDetail", "callWorkDetailsReport: nonSyncedAttachment "+nonSyncedAttachment.size());
                        if (nonSyncedAttachment.size() == 0 && !isLoading) {
                            WorkDetailsRequest workDetailsRequest = new WorkDetailsRequest();
                            workDetailsRequest.setProjectId(workDetail.getProjectId());
                            workDetailsRequest.setReportDate(DateFormatter.formatDateTimeHHForService(workDetail.getCreatedAt()));
                            workDetailsRequest.setWorkDetailsReport(mWorkDetailsRepository.getNonSyncWorkDetailSyncAttachmentList(workDetail.getProjectId(), workDetail.getCreatedAt()));
                            Log.i("uploadWorkDetail", "callWorkDetailsReport: ");
                            isLoading = true;

                            getWorkDetails(workDetailsRequest, workDetail.getCreatedAt(), new ProviderResult<List<WorkDetails>>() {
                                @Override
                                public void success(List<WorkDetails> result) {
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
}
