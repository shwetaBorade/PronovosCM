package com.pronovoscm.data;

import android.util.Log;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.api.FileUploadAPI;
import com.pronovoscm.model.response.AbstractCallback;
import com.pronovoscm.model.response.ErrorResponse;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.PhotosMobile;
import com.pronovoscm.persistence.domain.PhotosMobileDao;
import com.pronovoscm.persistence.domain.PunchListAttachments;
import com.pronovoscm.persistence.domain.Taggables;
import com.pronovoscm.persistence.domain.TaggablesDao;
import com.pronovoscm.persistence.domain.WorkDetailsAttachments;
import com.pronovoscm.persistence.domain.WorkImpactAttachments;
import com.pronovoscm.persistence.domain.punchlist.PunchListRejectReasonAttachments;
import com.pronovoscm.persistence.repository.PunchListRepository;
import com.pronovoscm.persistence.repository.WorkDetailsRepository;
import com.pronovoscm.persistence.repository.WorkImpactRepository;
import com.pronovoscm.services.NetworkService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;


public class FileUploadProvider {
    private NetworkStateProvider networkStateProvider;
    private FileUploadAPI fileUploadAPI;
    private PronovosApplication pronovosApplication;
    private DaoSession daoSession;

    public FileUploadProvider(NetworkStateProvider networkStateProvider,
                              FileUploadAPI fileUploadAPI,
                              PronovosApplication pronovosApplication, DaoSession daoSession) {
        this.networkStateProvider = networkStateProvider;
        this.fileUploadAPI = fileUploadAPI;
        this.pronovosApplication = pronovosApplication;
        this.daoSession = daoSession;

    }

    public void setUrl(String url) {
        pronovosApplication.setUrl(url);
    }

    /**
     * Service call to get projects according to project request with region id
     */
    public void uploadFile(String url, RequestBody options, final PhotosMobile photosMobile, final ProviderResult<Boolean> updatePhotoAWSResult) {

        if (NetworkService.isNetworkAvailable(pronovosApplication)) {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            //interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().readTimeout(200, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS).addInterceptor(interceptor).build();


            // Set header for API call
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url + "/")
                    .client(client)
                    .build();

            FileUploadAPI service = retrofit.create(FileUploadAPI.class);
            Call<ResponseBody> callCreateFile = service.callCreateFile("", options);

            callCreateFile.enqueue(new AbstractCallback<ResponseBody>() {
                @Override
                protected void handleFailure(Call<ResponseBody> call, Throwable throwable) {
                    Log.i("TAG", "handleFailure: " + "fail");
                    updatePhotoAWSResult.failure("");
                }

                @Override
                protected void handleError(Call<ResponseBody> call, ErrorResponse errorResponse) {
                    updatePhotoAWSResult.failure("");
                    Log.i("TAG", "handleFailure: " + " error ");
                }

                @Override
                protected void handleSuccess(Response<ResponseBody> response) {
                    if (response.headers() != null) {
                        Headers projectResponse = null;
                        try {
                            projectResponse = response.headers();

                            Log.i("Image S3 ", "handleSuccess: " + response.headers().get("Location"));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (projectResponse != null && projectResponse.get("Location") != null) {
                            projectResponse.get("Location");
                            PhotosMobile photosMobile1 = daoSession.getPhotosMobileDao().queryBuilder()
                                    .where(PhotosMobileDao.Properties.PjPhotosIdMobile.eq(photosMobile.getPjPhotosIdMobile()),
                                            PhotosMobileDao.Properties.PjPhotosFolderId.eq(photosMobile.getPjPhotosFolderId()),
                                            PhotosMobileDao.Properties.PjProjectsId.eq(photosMobile.getPjProjectsId())).limit(1).list().get(0);
                            updatePhotoMobile(photosMobile1);
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

    private void updatePhotoMobile(PhotosMobile photosMobile) {
        List<PhotosMobile> list = daoSession.getPhotosMobileDao().queryBuilder().where(PhotosMobileDao.Properties.PjPhotosIdMobile.eq(photosMobile.getPjPhotosIdMobile()), PhotosMobileDao.Properties.PjPhotosFolderId.eq(photosMobile.getPjPhotosFolderId()), PhotosMobileDao.Properties.PjProjectsId.eq(photosMobile.getPjProjectsId())).limit(1).list();
        PhotosMobileDao mPhotosMobileDao = daoSession.getPhotosMobileDao();

        if (list.size() > 0) {
//            PhotosMobile photoMobile = new PhotosMobile();
            photosMobile.setIsawsSync(true);

            mPhotosMobileDao.insertOrReplace(photosMobile);
        }
    }

    public List<Taggables> getTaggable(PhotosMobile photosMobile) {
        return daoSession.getTaggablesDao().queryBuilder().where(TaggablesDao.Properties.TaggableIdMobile.eq(photosMobile.getPjPhotosIdMobile())).list();
    }


    /**
     * Service call to get projects according to project request with region id
     */
    public void uploadAttachmentFile(String url, RequestBody options, final WorkDetailsAttachments attachment, WorkDetailsRepository workDetailsRepository, final ProviderResult<Boolean> updatePhotoAWSResult) {

        if (NetworkService.isNetworkAvailable(pronovosApplication)) {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            // interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().readTimeout(200, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS).addInterceptor(interceptor).build();


            // Set header for API call
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url+ "/").client(client)
                    .build();

            FileUploadAPI service = retrofit.create(FileUploadAPI.class);
            Call<ResponseBody> callCreateFile = service.callCreateFile("", options);

            callCreateFile.enqueue(new AbstractCallback<ResponseBody>() {
                @Override
                protected void handleFailure(Call<ResponseBody> call, Throwable throwable) {
                    Log.i("TAG", "handleFailure: " + "fail");
                    updatePhotoAWSResult.failure("");

                }

                @Override
                protected void handleError(Call<ResponseBody> call, ErrorResponse errorResponse) {
                    Log.i("TAG", "handleFailure: " + " error ");
                    updatePhotoAWSResult.failure("");
                }

                @Override
                protected void handleSuccess(Response<ResponseBody> response) {
                    if (response.headers() != null) {
                        Headers projectResponse = null;
                        try {
                            projectResponse = response.headers();

                            Log.i("Image S3 ", "handleSuccess: " + response.headers().get("Location"));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (projectResponse != null && projectResponse.get("Location") != null) {
                            projectResponse.get("Location");
                            attachment.setIsAwsSync(true);
//                            attachment.setAttachmentId(0);
                            Log.i("Attachment", " uploadAttachmentFile WorkDetail: " + attachment.getAttachmentIdMobile() +"  "+attachment.getAttachmentId());

//                            attachment.setAttachmentPath(projectResponse.get("Location"));
                            workDetailsRepository.updateAttachment(attachment);
                            /*WorkDetailsAttachments attachments = daoSession.getWorkDetailsAttachmentsDao().queryBuilder().where(
                                    WorkDetailsAttachmentsDao.Properties.WorkDetailsReportIdMobile.eq(attachment.getWorkDetailsReportIdMobile()),
                                    WorkDetailsAttachmentsDao.Properties.AttachmentIdMobile.eq(attachment.getAttachmentIdMobile()),
                                    WorkDetailsAttachmentsDao.Properties.UsersId.eq(attachment.getUsersId())).limit(1).list().get(0);
                            updatePhotoMobile(photosMobile1);*/
                            updatePhotoAWSResult.success(true);
                        } else {
                            updatePhotoAWSResult.failure("");
                        }

                    } else {
                    }
                }
            });
        } else {
            updatePhotoAWSResult.failure("");

        }

    }


    /**
     * Service call to get projects according to project request with region id
     */
    public void uploadWorkImpactAttachmentFile(String url, RequestBody options, final WorkImpactAttachments attachment, WorkImpactRepository workImpactRepository, final ProviderResult<Boolean> updatePhotoAWSResult) {

        if (NetworkService.isNetworkAvailable(pronovosApplication)) {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            // interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().readTimeout(200, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS).addInterceptor(interceptor).build();


            // Set header for API call
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url+ "/").client(client)
                    .build();

            FileUploadAPI service = retrofit.create(FileUploadAPI.class);
            Call<ResponseBody> callCreateFile = service.callCreateFile("", options);

            callCreateFile.enqueue(new AbstractCallback<ResponseBody>() {
                @Override
                protected void handleFailure(Call<ResponseBody> call, Throwable throwable) {
                    Log.i("TAG", "handleFailure: " + "fail");
                    updatePhotoAWSResult.failure("");

                }

                @Override
                protected void handleError(Call<ResponseBody> call, ErrorResponse errorResponse) {
                    Log.i("TAG", "handleFailure: " + " error ");
                    updatePhotoAWSResult.failure("");

                }

                @Override
                protected void handleSuccess(Response<ResponseBody> response) {
                    if (response.headers() != null) {
                        Headers projectResponse = null;
                        try {
                            projectResponse = response.headers();

                            Log.i("Image S3 ", "handleSuccess: " + response.headers().get("Location"));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (projectResponse != null && projectResponse.get("Location") != null) {
                            projectResponse.get("Location");
                            attachment.setIsAwsSync(true);
//                            attachment.setAttachmentId(0);
                            Log.i("Attachment", " uploadAttachmentFile WorkDetail: " + attachment.getAttachmentIdMobile() +"  "+attachment.getAttachmentId());

//                            attachment.setAttachmentPath(projectResponse.get("Location"));
                            workImpactRepository.updateAttachment(attachment);
                            /*WorkDetailsAttachments attachments = daoSession.getWorkDetailsAttachmentsDao().queryBuilder().where(
                                    WorkDetailsAttachmentsDao.Properties.WorkDetailsReportIdMobile.eq(attachment.getWorkDetailsReportIdMobile()),
                                    WorkDetailsAttachmentsDao.Properties.AttachmentIdMobile.eq(attachment.getAttachmentIdMobile()),
                                    WorkDetailsAttachmentsDao.Properties.UsersId.eq(attachment.getUsersId())).limit(1).list().get(0);
                            updatePhotoMobile(photosMobile1);*/
                            updatePhotoAWSResult.success(true);
                        } else {
                            updatePhotoAWSResult.failure("");
                        }

                    } else {
                    }
                }
            });
        } else {
            updatePhotoAWSResult.failure("");

        }

    }





    /**
     * Service call to get projects according to project request with region id
     */
    public void uploadPunchListAttachmentFile(String url, RequestBody options, final PunchListAttachments attachment,
                                              PunchListRepository workDetailsRepository,
                                              PunchListRejectReasonAttachments rejectReasonAttachment,
                                              final ProviderResult<Boolean> updatePhotoAWSResult) {

        if (NetworkService.isNetworkAvailable(pronovosApplication)) {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            //   interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().readTimeout(200, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS).addInterceptor(interceptor).build();


            // Set header for API call
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url+ "/").client(client)
                    .build();

            FileUploadAPI service = retrofit.create(FileUploadAPI.class);
            Call<ResponseBody> callCreateFile = service.callCreateFile("", options);

            callCreateFile.enqueue(new AbstractCallback<ResponseBody>() {
                @Override
                protected void handleFailure(Call<ResponseBody> call, Throwable throwable) {
                    Log.i("TAG", "handleFailure: " + "fail");
                    updatePhotoAWSResult.failure("Fail");
//                    CustomProgressBar.dissMissDialog();
                }

                @Override
                protected void handleError(Call<ResponseBody> call, ErrorResponse errorResponse) {
                    Log.i("TAG", "handleFailure: " + " error ");
                    updatePhotoAWSResult.failure(errorResponse.getMessage());
                }

                @Override
                protected void handleSuccess(Response<ResponseBody> response) {
                    if (response.headers() != null) {
                        Headers projectResponse = null;
                        try {
                            projectResponse = response.headers();

                            Log.i("Image S3 ", "handleSuccess: " + response.headers().get("Location"));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (projectResponse != null && projectResponse.get("Location") != null) {
                            projectResponse.get("Location");
                            if(attachment != null){
                                attachment.setIsAwsSync(true);
                                Log.i("Attachment", " uploadAttachmentFile WorkDetail: " + attachment.getAttachmentIdMobile() +"  "+attachment.getAttachmentId());
                                workDetailsRepository.updateAttachment(attachment);
                            }
                            else {
                                rejectReasonAttachment.setIsAwsSync(true);
                                Log.i("Attachment", " Reject reason : " + rejectReasonAttachment.getRejectAttachmentIdMobile() +"  "+rejectReasonAttachment.getRejectAttachmentId());
                                workDetailsRepository.updateRejectReasonAttachment(rejectReasonAttachment);
                            }
//                            attachment.setAttachmentId(0);


//                            attachment.setAttachmentPath(projectResponse.get("Location"));

                            /*WorkDetailsAttachments attachments = daoSession.getWorkDetailsAttachmentsDao().queryBuilder().where(
                                    WorkDetailsAttachmentsDao.Properties.WorkDetailsReportIdMobile.eq(attachment.getWorkDetailsReportIdMobile()),
                                    WorkDetailsAttachmentsDao.Properties.AttachmentIdMobile.eq(attachment.getAttachmentIdMobile()),
                                    WorkDetailsAttachmentsDao.Properties.UsersId.eq(attachment.getUsersId())).limit(1).list().get(0);
                            updatePhotoMobile(photosMobile1);*/
                            updatePhotoAWSResult.success(true);
                        } else {
                            updatePhotoAWSResult.failure("");

                        }

                    }
                }
            });
        } else {
            updatePhotoAWSResult.failure("");

        }

    }

}
