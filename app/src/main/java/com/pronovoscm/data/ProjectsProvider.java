package com.pronovoscm.data;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.activity.LoginActivity;
import com.pronovoscm.api.ProjectsApi;
import com.pronovoscm.model.PDFSynEnum;
import com.pronovoscm.model.SyncDataEnum;
import com.pronovoscm.model.TransactionModuleEnum;
import com.pronovoscm.model.request.albums.Album;
import com.pronovoscm.model.request.albums.AlbumRequest;
import com.pronovoscm.model.request.documents.DocumentsFolderFileRequest;
import com.pronovoscm.model.request.photo.Photo;
import com.pronovoscm.model.request.photo.PhotoRequest;
import com.pronovoscm.model.request.projects.ProjectsRequest;
import com.pronovoscm.model.request.rfi.RfiListRequest;
import com.pronovoscm.model.request.rfi.RfiRepliesRequest;
import com.pronovoscm.model.request.signurl.SignedUrlRequest;
import com.pronovoscm.model.request.submittals.SubmittalsRequest;
import com.pronovoscm.model.request.updatephoto.Photo_tags;
import com.pronovoscm.model.response.AbstractCallback;
import com.pronovoscm.model.response.ErrorResponse;
import com.pronovoscm.model.response.album.AlbumResponse;
import com.pronovoscm.model.response.album.Albums;
import com.pronovoscm.model.response.documents.Documentfolder;
import com.pronovoscm.model.response.documents.Documentsfile;
import com.pronovoscm.model.response.documents.ProjectDocumentFilesResponse;
import com.pronovoscm.model.response.documents.ProjectDocumentFoldersResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.photo.PhotoResponse;
import com.pronovoscm.model.response.photo.PhotoTag;
import com.pronovoscm.model.response.projectdata.ProjectDataResponse;
import com.pronovoscm.model.response.projects.ProjectResponse;
import com.pronovoscm.model.response.projects.Projects;
import com.pronovoscm.model.response.projects.UsersProject;
import com.pronovoscm.model.response.regions.Regions;
import com.pronovoscm.model.response.regions.RegionsResponse;
import com.pronovoscm.model.response.rfi.Rfi;
import com.pronovoscm.model.response.rfi.RfiResponse;
import com.pronovoscm.model.response.rfi.RfiResponseListData;
import com.pronovoscm.model.response.rfi.attachment.RfiAttachment;
import com.pronovoscm.model.response.rfi.attachment.RfiAttachmentResponse;
import com.pronovoscm.model.response.rfi.attachment.RfiAttachmentResponseData;
import com.pronovoscm.model.response.rfi.contact.RfiContact;
import com.pronovoscm.model.response.rfi.contact.RfiContactListResponse;
import com.pronovoscm.model.response.rfi.contact.RfiContactListResponseData;
import com.pronovoscm.model.response.rfi.replies.RfiRepliesData;
import com.pronovoscm.model.response.rfi.replies.RfiRepliesResponse;
import com.pronovoscm.model.response.rfi.replies.RfiReply;
import com.pronovoscm.model.response.signedurl.SignedUrlResponse;
import com.pronovoscm.model.response.submittals.AssigneeSubmittals;
import com.pronovoscm.model.response.submittals.AttachmentsSubmittals;
import com.pronovoscm.model.response.submittals.CcListSubmittals;
import com.pronovoscm.model.response.submittals.Submittals;
import com.pronovoscm.model.response.submittals.SubmittalsResponse;
import com.pronovoscm.model.response.submittals.SubmittalsResponseListData;
import com.pronovoscm.model.response.tag.Tags;
import com.pronovoscm.model.response.tag.TagsResponse;
import com.pronovoscm.persistence.domain.AlbumCoverPhoto;
import com.pronovoscm.persistence.domain.AlbumCoverPhotoDao;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.ImageTag;
import com.pronovoscm.persistence.domain.ImageTagDao;
import com.pronovoscm.persistence.domain.PhotoFolder;
import com.pronovoscm.persistence.domain.PhotoFolderDao;
import com.pronovoscm.persistence.domain.PhotosMobile;
import com.pronovoscm.persistence.domain.PhotosMobileDao;
import com.pronovoscm.persistence.domain.PjAssigneeAttachments;
import com.pronovoscm.persistence.domain.PjAssigneeAttachmentsDao;
import com.pronovoscm.persistence.domain.PjDocumentsFiles;
import com.pronovoscm.persistence.domain.PjDocumentsFilesDao;
import com.pronovoscm.persistence.domain.PjDocumentsFolders;
import com.pronovoscm.persistence.domain.PjDocumentsFoldersDao;
import com.pronovoscm.persistence.domain.PjProjects;
import com.pronovoscm.persistence.domain.PjProjectsDao;
import com.pronovoscm.persistence.domain.PjRfi;
import com.pronovoscm.persistence.domain.PjRfiAttachments;
import com.pronovoscm.persistence.domain.PjRfiAttachmentsDao;
import com.pronovoscm.persistence.domain.PjRfiContactList;
import com.pronovoscm.persistence.domain.PjRfiContactListDao;
import com.pronovoscm.persistence.domain.PjRfiDao;
import com.pronovoscm.persistence.domain.PjRfiReplies;
import com.pronovoscm.persistence.domain.PjRfiRepliesDao;
import com.pronovoscm.persistence.domain.PjSubmittalAttachments;
import com.pronovoscm.persistence.domain.PjSubmittalAttachmentsDao;
import com.pronovoscm.persistence.domain.PjSubmittalContactList;
import com.pronovoscm.persistence.domain.PjSubmittalContactListDao;
import com.pronovoscm.persistence.domain.PjSubmittals;
import com.pronovoscm.persistence.domain.PjSubmittalsDao;
import com.pronovoscm.persistence.domain.RegionsTable;
import com.pronovoscm.persistence.domain.RegionsTableDao;
import com.pronovoscm.persistence.domain.Taggables;
import com.pronovoscm.persistence.domain.TaggablesDao;
import com.pronovoscm.persistence.domain.TransactionLogMobile;
import com.pronovoscm.persistence.domain.TransactionLogMobileDao;
import com.pronovoscm.persistence.repository.FieldPaperWorkRepository;
import com.pronovoscm.persistence.repository.ProjectDocumentsRepository;
import com.pronovoscm.persistence.repository.ProjectRfiRepository;
import com.pronovoscm.persistence.repository.ProjectSubmittalsRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.LogUtils;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.customcamera.Facing;
import com.pronovoscm.utils.customcamera.Flash;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Response;


public class ProjectsProvider {


    private static AlertDialog alertDialog;
    private final String TAG = ProjectsProvider.class.getName();
    private final ProjectsApi projectsApi;
    private final PronovosApplication context;
    private final DaoSession daoSession;
    private LoginResponse loginResponse;
    private final String CLASS_NAME = "ProjectsProvider";
    private ArrayList<String> captureImageList = new ArrayList<>();
    private int CAMERA_CURRNT_FACE = Facing.BACK.value();
    private int cameraFlash = Flash.OFF.value();
    private FieldPaperWorkRepository fieldPaperWorkRepository;
    private ProjectDocumentsRepository projectDocumentsRepository;
    private ProjectRfiRepository projectRfiRepository;
    private ProjectSubmittalsRepository projectSubmittalsRepository;

    public ProjectsProvider(ProjectsApi mProjectsApi, DaoSession daoSession, FieldPaperWorkRepository fieldPaperWorkRepository,
                            ProjectDocumentsRepository projectDocumentsRepo, ProjectRfiRepository rfiRepository, ProjectSubmittalsRepository projectSubmittalsRepository) {
        this.context = PronovosApplication.getContext();
        context.setUrl(Constants.BASE_API_URL);
        this.projectsApi = mProjectsApi;
        this.daoSession = daoSession;
        this.fieldPaperWorkRepository = fieldPaperWorkRepository;
        this.projectDocumentsRepository = projectDocumentsRepo;
        this.projectRfiRepository = rfiRepository;
        this.projectSubmittalsRepository = projectSubmittalsRepository;
    }

    /**
     * Service call to get projects according to project request with region id
     *
     * @param projectsRequest
     * @param callback
     */
    public void getUserProjects(ProjectsRequest projectsRequest, final ProviderResult<UsersProject> callback) {

        if (NetworkService.isNetworkAvailable(context)) {
            // Set header for API call
            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            int userId = loginResponse.getUserDetails().getUsers_id();

            headers.put("lastupdate", getMAXProjectUpdateDate(Integer.parseInt(projectsRequest.getRegionId())));
            // headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            if (loginResponse != null && loginResponse.getUserDetails() != null)
                headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            else {
                callback.AccessTokenFailure("");
                return;
            }

            headers.put("timezone", TimeZone.getDefault().getID());
            Call<ProjectResponse> projectResponseCall = projectsApi.getUsersProjects(headers, projectsRequest.getRegionId(), projectsRequest.getProjectVersionsCheck());
            projectResponseCall.enqueue(new AbstractCallback<ProjectResponse>() {
                @Override
                protected void handleFailure(Call<ProjectResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                    callback.success(new UsersProject());
                }

                @Override
                protected void handleError(Call<ProjectResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(Objects.requireNonNull(errorResponse).getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<ProjectResponse> response) {
                    if (response.body() != null) {
                        ProjectResponse projectResponse = null;
                        try {
                            projectResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (projectResponse != null && projectResponse.getStatus() == 200 && projectResponse.getUsersProject().getResponsecode() == 101) {


                            try {
                                List<PjProjects> projectList = doProjectUpdate(projectResponse.getUsersProject().getProjects(), projectsRequest.getRegionId(), userId);
                                callback.success(projectResponse.getUsersProject());
                            } catch (Exception e) {
                                LogUtils.LOGE(TAG, e.getMessage(), e);
                                callback.failure(e.getMessage());
                            }


                        } else if (projectResponse != null) {
                            callback.failure(projectResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });
        } else {
            //       List<PjProjects> projectList = getRegionProject(Integer.parseInt(projectsRequest.getRegionId()));
            callback.success(new UsersProject());
        }
    }

    private void deleteRFiAttachments(int rfiID, Context context, RfiAttachmentResponseData rfiAttachmentResponseData) {
        final PjRfiAttachmentsDao pjRfiDao = daoSession.getPjRfiAttachmentsDao();
        HashMap<Integer, RfiAttachment> serverAttachmentRecordMap = new HashMap<>();
        List<PjRfiAttachments> localPjRfiList = pjRfiDao.queryBuilder()
                .where(
                        PjRfiAttachmentsDao.Properties.PjRfiId.eq(rfiID)
                        /* ,PjRfiDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())*/
                ).list();
        try {

            if (rfiAttachmentResponseData.getRfiAttachments() != null && rfiAttachmentResponseData.getRfiAttachments().size() > 0)
                for (RfiAttachment serverRecord : rfiAttachmentResponseData.getRfiAttachments()) {
                    serverAttachmentRecordMap.put(serverRecord.getPjRfiAttachmentsId(), serverRecord);
                    Log.d(TAG, "deleteRFiAttachments: 1111  ====== " + serverRecord.getPjRfiAttachmentsId());
                }
            if (localPjRfiList != null && localPjRfiList.size() > 0) {
                for (PjRfiAttachments attachments : localPjRfiList) {

                    if (serverAttachmentRecordMap.get(attachments.getPjRfiAttachmentsId()) != null) {
                        // do nothning in this case
                        Log.d("Adapter", " 222 deleteRFiAttachments: " + attachments.getPjRfiAttachmentsId());
                    } else {
                        Log.d("Adapter", " 3333 deleteRFiAttachments: " + attachments.getPjRfiAttachmentsId());
                        URI uri = null;
                        uri = new URI(attachments.getAttachPath());
                        String[] segments = uri.getPath().split("/");
                        String imageName = segments[segments.length - 1];
                        String filePath = context.getFilesDir().getAbsolutePath() + Constants.RFI_ATTACHMENTS_PATH;
                        File imgFile = new File(filePath + "/" + imageName);
                        if (imgFile.exists()) {
                            imgFile.delete();
                        }
                        projectRfiRepository.deleteRFIAttachment(rfiID, attachments.getPjRfiAttachmentsId());
                    }

                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getProjectRfiAttachments(int rfiId, int projectId, ProviderResult<RfiAttachmentResponse> rfiAttachmentProviderResult) {

        if (NetworkService.isNetworkAvailable(context)) {
            RfiRepliesRequest rfiListRequest = new RfiRepliesRequest();
            rfiListRequest.rfiId = (rfiId);
            HashMap<String, String> headers = new HashMap<>();
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("lastupdate", getMAXRfiAttachmentUpdateDate(rfiId, projectId));
            if (loginResponse != null && loginResponse.getUserDetails() != null) {
                headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            } else {
                rfiAttachmentProviderResult.AccessTokenFailure("");
            }

            Call<RfiAttachmentResponse> documentFoldersResponseCall = projectsApi.getProjectRfiAttachments(headers, rfiListRequest);
            documentFoldersResponseCall.enqueue(new AbstractCallback<RfiAttachmentResponse>() {
                @Override
                protected void handleFailure(Call<RfiAttachmentResponse> call, Throwable throwable) {
                    rfiAttachmentProviderResult.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<RfiAttachmentResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        rfiAttachmentProviderResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        rfiAttachmentProviderResult.failure(Objects.requireNonNull(errorResponse).getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<RfiAttachmentResponse> response) {
                    if (response.body() != null) {
                        /*LoginResponse loginResponse = gson.fromJson(response.body().getResStr(), LoginResponse.class);*/
                        RfiAttachmentResponse rfiResponse = null;
                        try {
                            rfiResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (rfiResponse != null && rfiResponse.getStatus() == 200 &&
                                (rfiResponse.getData().getResponseCode() == 101 || rfiResponse.getData().getResponseCode() == 102)
                                && rfiResponse.getData() != null) {
                            deleteRFiAttachments(rfiId, context, rfiResponse.getData());
                            doUpdateProjectRFIAttachments(rfiResponse.getData());
                            rfiAttachmentProviderResult.success(rfiResponse);
                        } else if (rfiResponse != null) {
                            rfiAttachmentProviderResult.failure(rfiResponse.getMessage());
                        } else {
                            rfiAttachmentProviderResult.failure("response null");
                        }

                    }
                }
            });
        } else {

            rfiAttachmentProviderResult.failure("response null");
        }
    }

    private void doUpdateProjectRFIAttachments(RfiAttachmentResponseData rfiAttachmentResponseData) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        try {
            daoSession.callInTx(new Callable<List<PjRfiAttachments>>() {
                final PjRfiAttachmentsDao pjRfiDao = daoSession.getPjRfiAttachmentsDao();

                @Override
                public List<PjRfiAttachments> call() {
                    List<PjRfiAttachments> foldersList = new ArrayList<>();


                    if (rfiAttachmentResponseData.getRfiAttachments() != null && rfiAttachmentResponseData.getRfiAttachments().size() > 0)
                        for (RfiAttachment rfi : rfiAttachmentResponseData.getRfiAttachments()) {
                            List<PjRfiAttachments> localPjRfi = pjRfiDao.queryBuilder()
                                    .where(PjRfiAttachmentsDao.Properties.PjRfiAttachmentsId.eq(rfi.getPjRfiAttachmentsId()),
                                            PjRfiAttachmentsDao.Properties.PjRfiId.eq(rfi.getPjRfiId())
                                            /* ,PjRfiDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())*/
                                    ).limit(1).list();
                            if (localPjRfi != null && localPjRfi.size() > 0) {
                                //  pjDocFile.   getIsSync();
                                PjRfiAttachments localRecord = localPjRfi.get(0);
                                localRecord = updatePjRfiAttachment(localRecord, rfi);
                                projectRfiRepository.updatePjRfiAttachments(localRecord);
                                foldersList.add(localRecord);
                            } else {
                                PjRfiAttachments pjDocFile = createPjRfiAttachment(rfi);
                                projectRfiRepository.savePjRfiAttachments(pjDocFile);
                                foldersList.add(pjDocFile);
                            }


                        }
                    return foldersList;
                }


            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;

    }

    private PjRfiAttachments createPjRfiAttachment(RfiAttachment rfi) {
        PjRfiAttachments localRecord = new PjRfiAttachments();
        localRecord.createdAt = rfi.getCreatedAt() != null && !rfi.getCreatedAt().equals("") ?
                DateFormatter.getDateFromDateTimeString(rfi.getCreatedAt()) : null;
        localRecord.updatedAt = rfi.getUpdatedAt() != null && !rfi.getUpdatedAt().equals("") ?
                DateFormatter.getDateFromDateTimeString(rfi.getUpdatedAt()) : null;

        localRecord.pjRfiAttachmentsId = rfi.getPjRfiAttachmentsId();
        localRecord.pjRfiId = rfi.getPjRfiId();
        localRecord.pjProjectsId = rfi.getPjProjectsId();
        localRecord.attachPath = rfi.getAttachPath();
        localRecord.pjRfiRepliesId = rfi.getPjRfiRepliesId();
        localRecord.pjRfiOrigName = rfi.getPjRfiOrigname();
        localRecord.type = rfi.getType();
        return localRecord;
    }

    private PjRfiAttachments updatePjRfiAttachment(PjRfiAttachments localRecord, RfiAttachment rfi) {

        localRecord.createdAt = rfi.getCreatedAt() != null && !rfi.getCreatedAt().equals("") ?
                DateFormatter.getDateFromDateTimeString(rfi.getCreatedAt()) : null;
        localRecord.updatedAt = rfi.getUpdatedAt() != null && !rfi.getUpdatedAt().equals("") ?
                DateFormatter.getDateFromDateTimeString(rfi.getUpdatedAt()) : null;

        localRecord.pjRfiAttachmentsId = rfi.getPjRfiAttachmentsId();
        localRecord.pjRfiId = rfi.getPjRfiId();
        localRecord.pjProjectsId = rfi.getPjProjectsId();
        localRecord.attachPath = rfi.getAttachPath();
        localRecord.pjRfiRepliesId = rfi.getPjRfiRepliesId();
        localRecord.pjRfiOrigName = rfi.getPjRfiOrigname();
        localRecord.type = rfi.getType();
        return localRecord;
    }

    public void getProjectRfiReplies(int rfiId, ProviderResult<RfiRepliesResponse> rfiRepliesResponseProviderResult) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        if (NetworkService.isNetworkAvailable(context)) {
            RfiRepliesRequest rfiRepliesRequest = new RfiRepliesRequest();
            rfiRepliesRequest.rfiId = rfiId;
            HashMap<String, String> headers = new HashMap<>();
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("lastupdate", getMAXProjectRfiRepliesUpdateDate(rfiId));
            if (loginResponse != null && loginResponse.getUserDetails() != null) {
                headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            } else {
                rfiRepliesResponseProviderResult.AccessTokenFailure("");
            }
            Call<RfiRepliesResponse> documentFoldersResponseCall = projectsApi.getProjectRfiReplies(headers, rfiRepliesRequest);
            documentFoldersResponseCall.enqueue(new AbstractCallback<RfiRepliesResponse>() {
                @Override
                protected void handleFailure(Call<RfiRepliesResponse> call, Throwable throwable) {
                    rfiRepliesResponseProviderResult.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<RfiRepliesResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        rfiRepliesResponseProviderResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        rfiRepliesResponseProviderResult.failure(Objects.requireNonNull(errorResponse).getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<RfiRepliesResponse> response) {
                    if (response.body() != null) {
                        /*LoginResponse loginResponse = gson.fromJson(response.body().getResStr(), LoginResponse.class);*/
                        RfiRepliesResponse rfiResponse = null;
                        try {
                            rfiResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (rfiResponse != null && rfiResponse.getStatus() == 200 &&
                                (rfiResponse.getData().getResponseCode() == 101 || rfiResponse.getData().getResponseCode() == 102)
                                && rfiResponse.getData() != null) {
                            projectRfiRepository.deleteRfiReplies(rfiId);
                            doUpdateRfiReplies(rfiResponse.getData());
                            rfiRepliesResponseProviderResult.success(rfiResponse);
                        } else if (rfiResponse != null) {
                            rfiRepliesResponseProviderResult.failure(rfiResponse.getMessage());
                        } else {
                            rfiRepliesResponseProviderResult.failure("response null");
                        }
                    }
                }
            });

        } else {

            rfiRepliesResponseProviderResult.failure("response null");
        }
    }

    public void doUpdateRfiReplies(RfiRepliesData rfiRepliesData) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        try {
            daoSession.callInTx(new Callable<List<PjRfiReplies>>() {
                final PjRfiRepliesDao pjRfiDao = daoSession.getPjRfiRepliesDao();

                @Override
                public List<PjRfiReplies> call() {
                    List<PjRfiReplies> foldersList = new ArrayList<>();
                    if (rfiRepliesData.getRfiReplies() != null && rfiRepliesData.getRfiReplies().size() > 0)
                        for (RfiReply rfi : rfiRepliesData.getRfiReplies()) {
                            List<PjRfiReplies> localPjRfi = pjRfiDao.queryBuilder()
                                    .where(PjRfiRepliesDao.Properties.PjRfiRepliesId.eq(rfi.getPjRfiRepliesId()),
                                            PjRfiRepliesDao.Properties.PjRfiId.eq(rfi.getPjRfiId())
                                            /* ,PjRfiRepliesDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())*/
                                    ).limit(1).list();
                            if (localPjRfi != null && localPjRfi.size() > 0) {
                                //  pjDocFile.   getIsSync();
                                PjRfiReplies localRecord = localPjRfi.get(0);
                                localRecord = updatePjRfiReplies(localRecord, rfi);
                                projectRfiRepository.updatePjRfiReplies(localRecord);
                                foldersList.add(localRecord);
                            } else {
                                PjRfiReplies pjDocFile = createPjRfiReplies(rfi);
                                projectRfiRepository.savePjRfiReplies(pjDocFile);
                                foldersList.add(pjDocFile);
                            }


                        }
                    return foldersList;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;

    }

    public PjRfiReplies updatePjRfiReplies(PjRfiReplies localRecord, RfiReply rfi) {
        localRecord.pjRfiId = rfi.getPjRfiId();
        localRecord.pjRfiRepliesId = rfi.getPjRfiRepliesId();
        localRecord.userId = rfi.getUsersId();
        localRecord.rfiReplies = rfi.getRfiReplies();
        localRecord.username = rfi.getUsername();
        localRecord.createdAt = rfi.getCreatedAt() != null && !rfi.getCreatedAt().equals("") ?
                DateFormatter.getDateFromDateTimeString(rfi.getCreatedAt()) : null;
        localRecord.updatedAt = rfi.getUpdatedAt() != null && !rfi.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(rfi.getUpdatedAt()) : null;
        localRecord.isOfficialResponse = rfi.getIsOfficialResponse();
        return localRecord;
    }

    public PjRfiReplies createPjRfiReplies(RfiReply rfi) {
        PjRfiReplies pjRfiReplies = new PjRfiReplies();
        pjRfiReplies.pjRfiId = rfi.getPjRfiId();
        pjRfiReplies.pjRfiRepliesId = rfi.getPjRfiRepliesId();
        pjRfiReplies.userId = rfi.getUsersId();
        pjRfiReplies.rfiReplies = rfi.getRfiReplies();
        pjRfiReplies.username = rfi.getUsername();
        pjRfiReplies.createdAt = rfi.getCreatedAt() != null && !rfi.getCreatedAt().equals("") ?
                DateFormatter.getDateFromDateTimeString(rfi.getCreatedAt()) : null;

        pjRfiReplies.updatedAt = rfi.getUpdatedAt() != null && !rfi.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(rfi.getUpdatedAt()) : null;

        pjRfiReplies.isOfficialResponse = rfi.getIsOfficialResponse();
        return pjRfiReplies;
    }

    public String getMAXProjectRfiRepliesUpdateDate(int rfiId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<PjRfiReplies> maxPostIdRow = PronovosApplication.getContext().getDaoSession().getPjRfiRepliesDao().queryBuilder()
                .where(PjRfiRepliesDao.Properties.UpdatedAt.isNotNull(),
                        PjRfiRepliesDao.Properties.PjRfiId.eq(rfiId)
                        /* , PjDocumentsFilesDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))*/
                ).orderDesc(PjRfiRepliesDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1990-01-01 01:01:01";
    }

    public void getProjectRfiList(int projectId, ProviderResult<RfiResponse> rfiResponseResult) {

        if (NetworkService.isNetworkAvailable(context)) {
            RfiListRequest rfiListRequest = new RfiListRequest();
            rfiListRequest.setProjectId(projectId);
            HashMap<String, String> headers = new HashMap<>();
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("lastupdate", getMAXProjectRfiUpdateDate(projectId));
            if (loginResponse != null && loginResponse.getUserDetails() != null) {
                headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            } else {
                rfiResponseResult.AccessTokenFailure("");
            }

            Call<RfiResponse> documentFoldersResponseCall = projectsApi.getProjectRfi(headers, rfiListRequest);
            documentFoldersResponseCall.enqueue(new AbstractCallback<RfiResponse>() {

                @Override
                protected void handleFailure(Call<RfiResponse> call, Throwable throwable) {
                    rfiResponseResult.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<RfiResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        rfiResponseResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        rfiResponseResult.failure(Objects.requireNonNull(errorResponse).getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<RfiResponse> response) {
                    if (response.body() != null) {
                        /*LoginResponse loginResponse = gson.fromJson(response.body().getResStr(), LoginResponse.class);*/
                        RfiResponse rfiResponse = null;
                        try {
                            rfiResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (rfiResponse != null && rfiResponse.getStatus() == 200 &&
                                (rfiResponse.getData().getResponseCode() == 101 || rfiResponse.getData().getResponseCode() == 102)
                                && rfiResponse.getData() != null) {
                            doUpdateProjectRFI(rfiResponse.getData());
                            rfiResponseResult.success(rfiResponse);
                        } else if (rfiResponse != null) {
                            rfiResponseResult.failure(rfiResponse.getMessage());
                        } else {
                            rfiResponseResult.failure("response null");
                        }

                    }
                }
            });


        } else {

            rfiResponseResult.failure("response null");
        }
    }

    public void getProjectRfiContactList(int projectId, ProviderResult<RfiContactListResponse> rfiResponseResult) {

        if (NetworkService.isNetworkAvailable(context)) {
            RfiListRequest rfiListRequest = new RfiListRequest();
            rfiListRequest.setProjectId(projectId);
            HashMap<String, String> headers = new HashMap<>();
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("lastupdate", getMAXProjectRfiUpdateDate(projectId));
            if (loginResponse != null && loginResponse.getUserDetails() != null) {
                headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            } else {
                rfiResponseResult.AccessTokenFailure("");
            }

            Call<RfiContactListResponse> documentFoldersResponseCall = projectsApi.getProjectRfiContactList(headers, rfiListRequest);
            documentFoldersResponseCall.enqueue(new AbstractCallback<RfiContactListResponse>() {

                @Override
                protected void handleFailure(Call<RfiContactListResponse> call, Throwable throwable) {
                    rfiResponseResult.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<RfiContactListResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        rfiResponseResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        rfiResponseResult.failure(Objects.requireNonNull(errorResponse).getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<RfiContactListResponse> response) {
                    if (response.body() != null) {
                        /*LoginResponse loginResponse = gson.fromJson(response.body().getResStr(), LoginResponse.class);*/
                        RfiContactListResponse rfiResponse = null;
                        try {
                            rfiResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (rfiResponse != null && rfiResponse.getStatus() == 200 &&
                                (rfiResponse.getData().getResponseCode() == 101 || rfiResponse.getData().getResponseCode() == 102)
                                && rfiResponse.getData() != null) {
                            doUpdateProjectRfiContactList(rfiResponse.getData());
                            rfiResponseResult.success(rfiResponse);
                        } else if (rfiResponse != null) {
                            rfiResponseResult.failure(rfiResponse.getMessage());
                        } else {
                            rfiResponseResult.failure("response null");
                        }

                    }
                }
            });


        } else {

            rfiResponseResult.failure("response null");
        }
    }

    private void doUpdateProjectRfiContactList(RfiContactListResponseData rfiContactListResponseData) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        try {
            daoSession.callInTx(new Callable<List<PjRfiContactList>>() {
                final PjRfiContactListDao pjRfiContactDao = daoSession.getPjRfiContactListDao();

                @Override
                public List<PjRfiContactList> call() {

                    List<PjRfiContactList> foldersList = new ArrayList<>();
                    if (rfiContactListResponseData.getRfiContacts() != null && rfiContactListResponseData.getRfiContacts().size() > 0)

                        for (RfiContact rfiContact : rfiContactListResponseData.getRfiContacts()) {
                            List<PjRfiContactList> localPjRfi = pjRfiContactDao.queryBuilder()
                                    .where(PjRfiContactListDao.Properties.PjRfiContactListId.eq(rfiContact.getPjRfiContactListId()),
                                            PjRfiContactListDao.Properties.PjRfiId.eq(rfiContact.getPjRfiId())
                                            /* ,PjRfiContactListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())*/
                                    ).limit(1).list();
                            if (localPjRfi != null && localPjRfi.size() > 0) {
                                //  pjDocFile.   getIsSync();
                                PjRfiContactList localRecord = localPjRfi.get(0);
                                localRecord = updatePjRfiContact(localRecord, rfiContact);
                                projectRfiRepository.updatePjRfiContactListItem(localRecord);
                                foldersList.add(localRecord);
                            } else {
                                PjRfiContactList pjDocFile = createPjRfiContactList(rfiContact);
                                projectRfiRepository.savePjRfiContactList(pjDocFile);
                                foldersList.add(pjDocFile);
                            }


                        }


                    return foldersList;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    private void doUpdateProjectRFI(RfiResponseListData rfiResponseListData) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        try {
            daoSession.callInTx(new Callable<List<PjRfi>>() {
                final PjRfiDao pjRfiDao = daoSession.getPjRfiDao();

                @Override
                public List<PjRfi> call() {
                    List<PjRfi> foldersList = new ArrayList<>();
                    if (rfiResponseListData.getRfis() != null && rfiResponseListData.getRfis().size() > 0)
                        for (Rfi rfi : rfiResponseListData.getRfis()) {
                            List<PjRfi> localPjRfi = pjRfiDao.queryBuilder()
                                    .where(PjRfiDao.Properties.PjProjectsId.eq(rfi.getPjProjectsId()),
                                            PjRfiDao.Properties.PjRfiId.eq(rfi.getPjRfiId())
                                            /* ,PjRfiDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())*/
                                    ).limit(1).list();
                            if (localPjRfi != null && localPjRfi.size() > 0) {
                                //  pjDocFile.   getIsSync();
                                PjRfi localRecord = localPjRfi.get(0);
                                localRecord = updatePjRfi(localRecord, rfi);
                                projectRfiRepository.updatePjRfi(localRecord);
                                foldersList.add(localRecord);
                            } else {
                                PjRfi pjDocFile = createPjRfi(rfi);
                                projectRfiRepository.savePjRfi(pjDocFile);
                                foldersList.add(pjDocFile);
                            }


                        }
                    return foldersList;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;

    }


    private PjRfiContactList updatePjRfiContact(PjRfiContactList localRecord, RfiContact rfi) {
        localRecord.pjRfiContactListId = rfi.getPjRfiContactListId();
        localRecord.pjRfiId = rfi.getPjRfiId();
        localRecord.contactList = rfi.getContactList();
        localRecord.name = rfi.getName();
        localRecord.email = rfi.getEmail();
        localRecord.defaultType = rfi.getDefaultType();
        localRecord.createdAt = rfi.getCreatedAt() != null && !rfi.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(rfi.getCreatedAt()) : null;
        localRecord.updatedAt = rfi.getUpdatedAt() != null && !rfi.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(rfi.getUpdatedAt()) : null;
        return localRecord;
    }

    private PjRfi updatePjRfi(PjRfi localRecord, Rfi rfi) {
        localRecord.dateSubmitted = rfi.getDateSubmitted() != null && !rfi.getDateSubmitted().equals("") ? DateFormatter.getDateFromDateTimeString(rfi.getDateSubmitted()) : null;
        localRecord.receivedDate = rfi.getReceivedDate() != null && !rfi.getReceivedDate().equals("") ? DateFormatter.getDateFromDateTimeString(rfi.getReceivedDate()) : null;
        localRecord.dueDate = rfi.getDueDate() != null && !rfi.getDueDate().equals("") ? DateFormatter.getDateFromDateTimeString(rfi.getDueDate()) : null;
        localRecord.dateSent = rfi.getDateSent() != null && !rfi.getDateSent().equals("") ? DateFormatter.getDateFromDateTimeString(rfi.getDateSent()) : null;
        localRecord.createdAt = rfi.getCreatedAt() != null && !rfi.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(rfi.getCreatedAt()) : null;
        localRecord.createdBy = rfi.getCreatedBy();
        localRecord.updatedAt = rfi.getUpdatedAt() != null && !rfi.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(rfi.getUpdatedAt()) : null;
        localRecord.deletedAt = rfi.getDeletedAt() != null && !rfi.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(rfi.getDeletedAt()) : null;
        localRecord.updatedBy = rfi.getUpdatedBy();
        localRecord.tenantId = rfi.getTenantId();
        localRecord.pjRfiId = rfi.getPjRfiId();
        localRecord.pjProjectsId = rfi.getPjProjectsId();
        localRecord.rfiNumber = rfi.getRfiNo();
        localRecord.origRfiNumber = rfi.getOrigRfiNo();
        localRecord.rfiTitle = rfi.getRfiTitle();
        localRecord.status = rfi.getStatus();
        localRecord.internalAuthor = rfi.getInternalAuthor();
        localRecord.receivedFrom = rfi.getReceivedFrom();
        localRecord.responseDays = rfi.getResponseDays();
        localRecord.assignedTo = rfi.getAssignedTo();
        localRecord.refDrawingNumber = rfi.getRefDrawingNumber();
        localRecord.location = rfi.getLocation();
        localRecord.scheduleImpactDays = rfi.getScheduleImpactDays();
        localRecord.question = rfi.getQuestion();
        localRecord.cc = rfi.getCc();
        localRecord.refSpecification = rfi.getRefSpecification();
        localRecord.costImpact = rfi.getCostImpact();
        localRecord.isRfiSent = rfi.getIsRfiSent();
        localRecord.attachment = rfi.getAttachment();
        localRecord.setAuthorName(rfi.getAuthorName());
        localRecord.receiverName = (rfi.getReceiverName());
        return localRecord;
    }

    private PjRfiContactList createPjRfiContactList(RfiContact contact) {
        PjRfiContactList pjRfiContactList = new PjRfiContactList();
        pjRfiContactList.pjRfiContactListId = contact.getPjRfiContactListId();
        pjRfiContactList.pjRfiId = contact.getPjRfiId();
        pjRfiContactList.contactList = contact.getContactList();
        pjRfiContactList.name = contact.getName();
        pjRfiContactList.email = contact.getEmail();
        pjRfiContactList.defaultType = contact.getDefaultType();
        pjRfiContactList.createdAt = contact.getCreatedAt() != null && !contact.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(contact.getCreatedAt()) : null;
        pjRfiContactList.updatedAt = contact.getUpdatedAt() != null && !contact.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(contact.getUpdatedAt()) : null;
        return pjRfiContactList;
    }

    private PjRfi createPjRfi(Rfi rfi) {
        PjRfi pjRfi = new PjRfi();

        pjRfi.dateSubmitted = rfi.getDateSubmitted() != null && !rfi.getDateSubmitted().equals("") ?
                DateFormatter.getDateFromDateTimeString(rfi.getDateSubmitted()) : null;
        pjRfi.receivedDate = rfi.getReceivedDate() != null && !rfi.getReceivedDate().equals("") ?
                DateFormatter.getDateFromDateTimeString(rfi.getReceivedDate()) : null;
        pjRfi.dueDate = rfi.getDueDate() != null && !rfi.getDueDate().equals("") ? DateFormatter.getDateFromDateTimeString(rfi.getDueDate()) : null;
        pjRfi.dateSent = rfi.getDateSent() != null && !rfi.getDateSent().equals("") ? DateFormatter.getDateFromDateTimeString(rfi.getDateSent()) : null;
        pjRfi.createdAt = rfi.getCreatedAt() != null && !rfi.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(rfi.getCreatedAt()) : null;
        pjRfi.createdBy = rfi.getCreatedBy();
        pjRfi.updatedAt = rfi.getUpdatedAt() != null && !rfi.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(rfi.getUpdatedAt()) : null;
        pjRfi.updatedBy = rfi.getUpdatedBy();
        pjRfi.tenantId = rfi.getTenantId();
        pjRfi.pjRfiId = rfi.getPjRfiId();
        pjRfi.pjProjectsId = rfi.getPjProjectsId();
        pjRfi.rfiNumber = rfi.getRfiNo();
        pjRfi.origRfiNumber = rfi.getOrigRfiNo();
        pjRfi.rfiTitle = rfi.getRfiTitle();
        pjRfi.status = rfi.getStatus();
        pjRfi.internalAuthor = rfi.getInternalAuthor();
        pjRfi.receivedFrom = rfi.getReceivedFrom();
        pjRfi.responseDays = rfi.getResponseDays();
        pjRfi.assignedTo = rfi.getAssignedTo();
        pjRfi.refDrawingNumber = rfi.getRefDrawingNumber();
        pjRfi.location = rfi.getLocation();
        pjRfi.scheduleImpactDays = rfi.getScheduleImpactDays();
        pjRfi.question = rfi.getQuestion();
        pjRfi.cc = rfi.getCc();
        pjRfi.refSpecification = rfi.getRefSpecification();
        pjRfi.costImpact = rfi.getCostImpact();
        pjRfi.isRfiSent = rfi.getIsRfiSent();
        pjRfi.deletedAt = rfi.getDeletedAt() != null && !rfi.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(rfi.getDeletedAt()) : null;
        pjRfi.setAuthorName(rfi.getAuthorName());
        pjRfi.receiverName = (rfi.getReceiverName());
        pjRfi.attachment = rfi.getAttachment();

        return pjRfi;

    }

    private String getMAXProjectRfiUpdateDate(int projectId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<PjRfi> maxPostIdRow = daoSession.getPjRfiDao().queryBuilder()
                .where(PjRfiDao.Properties.UpdatedAt.isNotNull(),
                        PjRfiDao.Properties.PjProjectsId.eq(projectId)
                        /* , PjDocumentsFilesDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))*/
                ).orderDesc(PjRfiDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1990-01-01 01:01:01";
    }

    private String getMAXRfiAttachmentUpdateDate(int rfiId, int projectId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<PjRfiAttachments> maxPostIdRow = daoSession.getPjRfiAttachmentsDao().queryBuilder()
                .where(PjRfiAttachmentsDao.Properties.UpdatedAt.isNotNull(),
                        PjRfiAttachmentsDao.Properties.PjProjectsId.eq(projectId),
                        PjRfiAttachmentsDao.Properties.PjRfiId.eq(rfiId)
                        /* , PjDocumentsFilesDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))*/
                ).orderDesc(PjRfiAttachmentsDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1990-01-01 01:01:01";
    }

    public void getProjectDocumentFiles(int projectId, ProviderResult<ProjectDocumentFilesResponse> documentFilesResponseResult) {

        if (NetworkService.isNetworkAvailable(context)) {
            DocumentsFolderFileRequest documentsFolderFileRequest = new DocumentsFolderFileRequest();
            documentsFolderFileRequest.setProject_id(projectId);

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", getMAXProjectDocumentFilesUpdateDate(projectId));
            headers.put("timezone", TimeZone.getDefault().getID());
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            if (loginResponse != null && loginResponse.getUserDetails() != null) {

                headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            } else {
                documentFilesResponseResult.AccessTokenFailure("");
            }
            Call<ProjectDocumentFilesResponse> documentFoldersResponseCall = projectsApi.getProjectDocumentFiles(headers, documentsFolderFileRequest);
            documentFoldersResponseCall.enqueue(new AbstractCallback<ProjectDocumentFilesResponse>() {

                @Override
                protected void handleFailure(Call<ProjectDocumentFilesResponse> call, Throwable throwable) {
                    documentFilesResponseResult.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<ProjectDocumentFilesResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        documentFilesResponseResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        documentFilesResponseResult.failure(Objects.requireNonNull(errorResponse).getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<ProjectDocumentFilesResponse> response) {
                    if (response.body() != null) {
                        /*LoginResponse loginResponse = gson.fromJson(response.body().getResStr(), LoginResponse.class);*/
                        ProjectDocumentFilesResponse projectDocumentFilesResponse = null;
                        try {
                            projectDocumentFilesResponse = response.body();


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (projectDocumentFilesResponse != null && projectDocumentFilesResponse.getStatus() == 200 &&
                                (projectDocumentFilesResponse.getData().getResponseCode() == 101
                                        || projectDocumentFilesResponse.getData().getResponseCode() == 102)
                                && projectDocumentFilesResponse.getData() != null) {
                            doUpdateProjectDocumentFiles(projectDocumentFilesResponse.getData().getDocumentfiles());
                            documentFilesResponseResult.success(projectDocumentFilesResponse);
                        } else if (projectDocumentFilesResponse != null) {
                            documentFilesResponseResult.failure(projectDocumentFilesResponse.getMessage());
                        } else {
                            documentFilesResponseResult.failure("response null");
                        }


                    }
                }
            });


        } else {

            documentFilesResponseResult.failure("response null");
        }
    }

    public void getProjectDocumentFolders(int projectId, ProviderResult<ProjectDocumentFoldersResponse> documentFoldersResponseResult) {

        if (NetworkService.isNetworkAvailable(context)) {
            DocumentsFolderFileRequest documentsFolderFileRequest = new DocumentsFolderFileRequest();
            documentsFolderFileRequest.setProject_id(projectId);

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", getMAXProjectDocumentFolderUpdateDate(projectId));
            headers.put("timezone", TimeZone.getDefault().getID());
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            if (loginResponse != null && loginResponse.getUserDetails() != null) {

                headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            } else {
                documentFoldersResponseResult.AccessTokenFailure("");
            }
            Call<ProjectDocumentFoldersResponse> documentFoldersResponseCall = projectsApi.getProjectDocumentFolders(headers, documentsFolderFileRequest);
            documentFoldersResponseCall.enqueue(new AbstractCallback<ProjectDocumentFoldersResponse>() {

                @Override
                protected void handleFailure(Call<ProjectDocumentFoldersResponse> call, Throwable throwable) {
                    documentFoldersResponseResult.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<ProjectDocumentFoldersResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        documentFoldersResponseResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        documentFoldersResponseResult.failure(Objects.requireNonNull(errorResponse).getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<ProjectDocumentFoldersResponse> response) {
                    if (response.body() != null) {
                        /*LoginResponse loginResponse = gson.fromJson(response.body().getResStr(), LoginResponse.class);*/
                        ProjectDocumentFoldersResponse projectDocumentFoldersResponse = null;
                        try {
                            projectDocumentFoldersResponse = response.body();


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (projectDocumentFoldersResponse != null && projectDocumentFoldersResponse.getStatus() == 200 &&
                                (projectDocumentFoldersResponse.getData().getResponseCode() == 101
                                        || projectDocumentFoldersResponse.getData().getResponseCode() == 102) &&
                                projectDocumentFoldersResponse.getData() != null) {
                            doUpdateProjectDocumentFolders(projectDocumentFoldersResponse.getData().getDocumentfolders());
                            documentFoldersResponseResult.success(projectDocumentFoldersResponse);
                        } else if (projectDocumentFoldersResponse != null) {
                            documentFoldersResponseResult.failure(projectDocumentFoldersResponse.getMessage());
                        } else {
                            documentFoldersResponseResult.failure("response null");
                        }


                    }
                }
            });


        } else {

            documentFoldersResponseResult.failure("response null");
        }
    }

    private PjDocumentsFiles createPjDocumentsFiles(Documentsfile documentsfile) {
        PjDocumentsFiles pjDocumentsFiles = new PjDocumentsFiles();
        pjDocumentsFiles.setPjDocumentsFilesId(documentsfile.getPjDocumentsFilesId());
        pjDocumentsFiles.setOriginalPjDocumentsFilesId(documentsfile.getOriginalPjDocumentsFilesId());

        pjDocumentsFiles.setPjProjectsId(documentsfile.getPjProjectsId());
        pjDocumentsFiles.setPjDocumentsFoldersId(documentsfile.getPjDocumentsFoldersId());

        pjDocumentsFiles.setName(documentsfile.getName());
        pjDocumentsFiles.setOriginalName(documentsfile.getOriginalName());

        pjDocumentsFiles.setRevisionNumber(documentsfile.getRevisionNumber());
        pjDocumentsFiles.setActiveRevision(documentsfile.getActiveRevision());
        pjDocumentsFiles.setLocation(documentsfile.getLocation());
        pjDocumentsFiles.setCreatedBy(documentsfile.getCreatedBy());
        pjDocumentsFiles.setType(documentsfile.getType());
        pjDocumentsFiles.setUpdatedBy(documentsfile.getUpdatedBy());
        pjDocumentsFiles.setTenantId(documentsfile.getTenantId());
        pjDocumentsFiles.setCreatedAt(documentsfile.getCreatedAt() != null && !documentsfile.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(documentsfile.getCreatedAt()) : null);
        pjDocumentsFiles.setDeletedAt(documentsfile.getDeletedAt() != null && !documentsfile.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(documentsfile.getDeletedAt()) : null);
        pjDocumentsFiles.setUpdatedAt(documentsfile.getUpdatedAt() != null && !documentsfile.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(documentsfile.getUpdatedAt()) : null);
        pjDocumentsFiles.setLastupdatedate(new Date());
        pjDocumentsFiles.setIsPrivate(documentsfile.getIsPrivate());
        pjDocumentsFiles.setIsVisible(documentsfile.getIsVisible());
        return pjDocumentsFiles;
    }

    private PjDocumentsFolders createPjDocumentsFolders(Documentfolder folder) {
        PjDocumentsFolders documentsFolder = new PjDocumentsFolders();

        documentsFolder.setIsDefault(folder.getIsDefault());
        documentsFolder.setName(folder.getName());
        documentsFolder.setParentId(folder.getParentId());
        documentsFolder.setPjProjectsId(folder.getPjProjectsId());
        documentsFolder.setPjDocumentsFoldersId(folder.getPjDocumentsFoldersId());
        documentsFolder.setCreatedAt(folder.getCreatedAt() != null && !folder.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(folder.getCreatedAt()) : null);
        documentsFolder.setDeletedAt(folder.getDeletedAt() != null && !folder.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(folder.getDeletedAt()) : null);
        documentsFolder.setUpdatedAt(folder.getUpdatedAt() != null && !folder.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(folder.getUpdatedAt()) : null);
        documentsFolder.setUsersId(folder.getUsersId());
        documentsFolder.setCreatedBy(folder.getCreatedBy());
        documentsFolder.setUpdatedBy(folder.getUpdatedBy());
        documentsFolder.setTenantId(folder.getTenantId());
        documentsFolder.setIsPrivate(folder.getIsPrivate());
        documentsFolder.setIsVisible(folder.getIsVisible());
        documentsFolder.setLastupdatedate(new Date());

        return documentsFolder;
    }

    private void doUpdateProjectDocumentFolders(List<Documentfolder> documentfolders) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        try {
            daoSession.callInTx(new Callable<List<PjDocumentsFolders>>() {
                final PjDocumentsFoldersDao pjDocumentsFoldersDao = daoSession.getPjDocumentsFoldersDao();


                @Override
                public List<PjDocumentsFolders> call() {
                    List<PjDocumentsFolders> foldersList = new ArrayList<>();
                    if (documentfolders != null)
                        for (Documentfolder folder : documentfolders) {

                            List<PjDocumentsFolders> pjDocumentsFoldersList = pjDocumentsFoldersDao.queryBuilder()
                                    .where(PjDocumentsFoldersDao.Properties.PjDocumentsFoldersId.eq(folder.getPjDocumentsFoldersId()),
                                            PjDocumentsFoldersDao.Properties.PjProjectsId.eq(folder.getPjProjectsId())
                                            /* ,PjDocumentsFoldersDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())*/
                                    ).limit(1).list();
                            if (pjDocumentsFoldersList != null && pjDocumentsFoldersList.size() > 0) {

                                if (folder.getDeletedAt() != null && pjDocumentsFoldersList.get(0) != null) {
                                    // TODO upldate logic to delete files and folder
                                }
                                PjDocumentsFolders localFolderRecord = pjDocumentsFoldersList.get(0);


                                localFolderRecord.setIsDefault(folder.getIsDefault());
                                localFolderRecord.setName(folder.getName());
                                localFolderRecord.setParentId(folder.getParentId());
                                localFolderRecord.setPjProjectsId(folder.getPjProjectsId());
                                localFolderRecord.setPjDocumentsFoldersId(folder.getPjDocumentsFoldersId());
                                localFolderRecord.setCreatedAt(folder.getCreatedAt() != null && !folder.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(folder.getCreatedAt()) : null);
                                localFolderRecord.setDeletedAt(folder.getDeletedAt() != null && !folder.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(folder.getDeletedAt()) : null);
                                localFolderRecord.setUpdatedAt(folder.getUpdatedAt() != null && !folder.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(folder.getUpdatedAt()) : null);
                                localFolderRecord.setUsersId(folder.getUsersId());
                                localFolderRecord.setCreatedBy(folder.getCreatedBy());
                                localFolderRecord.setUpdatedBy(folder.getUpdatedBy());
                                localFolderRecord.setTenantId(folder.getTenantId());
                                localFolderRecord.setLastupdatedate(new Date());
                                localFolderRecord.setIsPrivate(folder.getIsPrivate());
                                localFolderRecord.setIsVisible(folder.getIsVisible());

                                projectDocumentsRepository.updateDocumentFolder(localFolderRecord);

                                foldersList.add(localFolderRecord);

                            } else {
                                PjDocumentsFolders pjDocumentsFolders = createPjDocumentsFolders(folder);
                                projectDocumentsRepository.saveDocumentFolder(pjDocumentsFolders);

                                foldersList.add(pjDocumentsFolders);
                            }

                        }
                    return foldersList;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    private void doUpdateProjectDocumentFiles(List<Documentsfile> documentsfiles) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        try {
            daoSession.callInTx(new Callable<List<PjDocumentsFiles>>() {
                final PjDocumentsFilesDao pjDocumenttsFilessDao = daoSession.getPjDocumentsFilesDao();


                @Override
                public List<PjDocumentsFiles> call() {
                    List<PjDocumentsFiles> foldersList = new ArrayList<>();
                    if (documentsfiles != null)
                        for (Documentsfile documentsfile : documentsfiles) {


                            List<PjDocumentsFiles> pjDocumentsFiles = pjDocumenttsFilessDao.queryBuilder()
                                    .where(
                                            /*PjDocumentsFilesDao.Properties.PjDocumentsFoldersId.eq(documentsfile.getPjDocumentsFoldersId()),*/
                                            PjDocumentsFilesDao.Properties.PjProjectsId.eq(documentsfile.getPjProjectsId()),
                                            PjDocumentsFilesDao.Properties.PjDocumentsFilesId.eq(documentsfile.getPjDocumentsFilesId())
                                            /* ,PjDocumentsFoldersDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())*/
                                    ).limit(1).list();
                            if (pjDocumentsFiles != null && pjDocumentsFiles.size() > 0) {
                                //  pjDocFile.   getIsSync();
                                PjDocumentsFiles localRecord = pjDocumentsFiles.get(0);

                                localRecord.setPjDocumentsFilesId(documentsfile.getPjDocumentsFilesId());
                                localRecord.setOriginalPjDocumentsFilesId(documentsfile.getOriginalPjDocumentsFilesId());

                                localRecord.setPjProjectsId(documentsfile.getPjProjectsId());
                                localRecord.setPjDocumentsFoldersId(documentsfile.getPjDocumentsFoldersId());

                                localRecord.setName(documentsfile.getName());
                                localRecord.setOriginalName(documentsfile.getOriginalName());

                                localRecord.setRevisionNumber(documentsfile.getRevisionNumber());
                                localRecord.setActiveRevision(documentsfile.getActiveRevision());

                                localRecord.setLocation(documentsfile.getLocation());


                                localRecord.setCreatedBy(documentsfile.getCreatedBy());
                                localRecord.setType(documentsfile.getType());
                                localRecord.setUpdatedBy(documentsfile.getUpdatedBy());
                                localRecord.setTenantId(documentsfile.getTenantId());
                                localRecord.setCreatedAt(documentsfile.getCreatedAt() != null && !documentsfile.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(documentsfile.getCreatedAt()) : null);
                                localRecord.setDeletedAt(documentsfile.getDeletedAt() != null && !documentsfile.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(documentsfile.getDeletedAt()) : null);
                                localRecord.setUpdatedAt(documentsfile.getUpdatedAt() != null && !documentsfile.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(documentsfile.getUpdatedAt()) : null);
                                localRecord.setLastupdatedate(new Date());
                                //projectDocumentsRepository.updateDocumentFile(pjDocFile);
                                localRecord.setIsPrivate(documentsfile.getIsPrivate());
                                localRecord.setIsVisible(documentsfile.getIsVisible());

                                projectDocumentsRepository.updateDocumentFile(localRecord);
                                if (documentsfile.getDeletedAt() != null && pjDocumentsFiles.get(0) != null) {
                                    // TODO upldate logic to delete files and folder
                                }

                                if ((!localRecord.getRevisionNumber().equals(documentsfile.getRevisionNumber())) ||
                                        (localRecord.getLocation() != null
                                                && !localRecord.getLocation().equals(documentsfile.getLocation())))
                                    localRecord.setSync(false);

                                foldersList.add(localRecord);
                            } else {
                                PjDocumentsFiles pjDocFile = createPjDocumentsFiles(documentsfile);

                                pjDocFile.setFileStatus(PDFSynEnum.NOTSYNC.ordinal());
                                pjDocFile.setIsSync(false);
                                projectDocumentsRepository.saveDocumentFile(pjDocFile);
                                foldersList.add(pjDocFile);
                            }


                        }
                    return foldersList;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;

    }

    public String getMAXProjectDocumentFilesUpdateDate(int projectID) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<PjDocumentsFiles> maxPostIdRow = daoSession.getPjDocumentsFilesDao().queryBuilder()
                .where(PjDocumentsFilesDao.Properties.UpdatedAt.isNotNull(),
                        PjDocumentsFilesDao.Properties.PjProjectsId.eq(projectID)
                        /* , PjDocumentsFilesDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))*/
                ).orderDesc(PjDocumentsFilesDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1990-01-01 01:01:01";
    }


    private String getMAXProjectDocumentFolderUpdateDate(int projectID) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<PjDocumentsFolders> maxPostIdRow = daoSession.getPjDocumentsFoldersDao().queryBuilder()
                .where(PjDocumentsFoldersDao.Properties.UpdatedAt.isNotNull(),
                        PjDocumentsFoldersDao.Properties.PjProjectsId.eq(projectID)
                        /*, PjDocumentsFoldersDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())*/)
                .orderDesc(PjDocumentsFoldersDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1990-01-01 01:01:01";
    }

    public void getUserPermission(ProviderResult<LoginResponse> loginResponseProviderResult) {

        if (NetworkService.isNetworkAvailable(context)) {
           /* PronovosApiRequest pronovosApiRequest = new PronovosApiRequest();
            pronovosApiRequest.setReqParam(loginRequest);*/
            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", getMAXRegionUpdateDate());
            headers.put("timezone", TimeZone.getDefault().getID());

            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            int userId = loginResponse.getUserDetails().getUsers_id();

            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            Call<LoginResponse> loginRequestCall = projectsApi.userPermission(headers);

            loginRequestCall.enqueue(new AbstractCallback<LoginResponse>() {
                @Override
                protected void handleFailure(Call<LoginResponse> call, Throwable throwable) {
                    loginResponseProviderResult.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<LoginResponse> call, ErrorResponse errorResponse) {
                    loginResponseProviderResult.failure(errorResponse.getMessage());
                }

                @Override
                protected void handleSuccess(Response<LoginResponse> response) {
                    if (response.body() != null) {
                        /*LoginResponse loginResponse = gson.fromJson(response.body().getResStr(), LoginResponse.class);*/
                        LoginResponse loginResponse = null;
                        try {
                            loginResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (loginResponse != null && loginResponse.getStatus() == 200 && loginResponse.getUserDetails().getResponsecode() == 101) {
                            loginResponseProviderResult.success(loginResponse);
                        } else if (loginResponse != null && loginResponse.getStatus() == 200 && loginResponse.getUserDetails().getResponsecode() == 102) {
                            loginResponseProviderResult.failure(loginResponse.getMessage());
                        } else if (loginResponse != null && loginResponse.getMessage() != null) {
                            loginResponseProviderResult.failure(loginResponse.getMessage());
                        } else {
                            loginResponseProviderResult.failure("Login response null");
                        }
                    } else {
                        loginResponseProviderResult.failure("response null");
                    }
                }
            });

        } else {
            loginResponseProviderResult.failure(context.getString(R.string.internet_connection_check));
        }
    }

    /**
     * Service call to get regions
     *
     * @param regionListResponse
     */
    public void getUserRegions(final ProviderResult<List<RegionsTable>> regionListResponse) {

        if (NetworkService.isNetworkAvailable(context)) {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", getMAXRegionUpdateDate());
            headers.put("timezone", TimeZone.getDefault().getID());

            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            int userId = loginResponse.getUserDetails().getUsers_id();

            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            Call<RegionsResponse> projectResponseCall = projectsApi.getUsersRegions(headers);

            projectResponseCall.enqueue(new AbstractCallback<RegionsResponse>() {
                @Override
                protected void handleFailure(Call<RegionsResponse> call, Throwable throwable) {
                    regionListResponse.failure(throwable.getMessage());
                    regionListResponse.success(getAllActiveRegions());
                }

                @Override
                protected void handleError(Call<RegionsResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        regionListResponse.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        regionListResponse.failure(Objects.requireNonNull(errorResponse).getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<RegionsResponse> response) {
                    if (response.body() != null) {

                        RegionsResponse regionsResponse = null;
                        try {
                            regionsResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (regionsResponse != null && regionsResponse.getStatus() == 200 && regionsResponse.getRegionData().getResponsecode() == 101) {
                            List<RegionsTable> regionsTables = doUpdateRegionTable(regionsResponse.getRegionData().getRegions(), userId);
                            regionListResponse.success(regionsTables);

                        } else if (regionsResponse != null) {
                            regionListResponse.failure(regionsResponse.getMessage());
                        } else {
                            regionListResponse.failure("response null");
                        }
                    } else {
                        regionListResponse.failure("response null");
                    }
                }
            });

        } else {
            List<RegionsTable> regionsTables = getAllActiveRegions();
            regionListResponse.success(regionsTables);
        }
    }


    /**
     * Service call to get project data
     *
     * @param regionListResponse
     */
    public void getUserProjectData(final ProviderResult<List<RegionsTable>> regionListResponse) {

        if (NetworkService.isNetworkAvailable(context)) {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("lastupdate", "1990-01-01 01:01:01");

            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            int userid = loginResponse.getUserDetails().getUsers_id();
            //headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            if (loginResponse != null && loginResponse.getUserDetails() != null)
                headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            else {
                regionListResponse.AccessTokenFailure("");
                return;
            }


            Call<ProjectDataResponse> projectResponseCall = projectsApi.getProjectsData(headers);

            projectResponseCall.enqueue(new AbstractCallback<ProjectDataResponse>() {
                @Override
                protected void handleFailure(Call<ProjectDataResponse> call, Throwable throwable) {
                    regionListResponse.failure(throwable.getMessage());
                    regionListResponse.success(getAllActiveRegions());
                }

                @Override
                protected void handleError(Call<ProjectDataResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        regionListResponse.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        regionListResponse.failure(Objects.requireNonNull(errorResponse).getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<ProjectDataResponse> response) {
                    if (response.body() != null) {

                        ProjectDataResponse projectDataResponse = null;
                        try {
                            projectDataResponse = response.body();
                            Log.i(TAG, "handleSuccess: " + projectDataResponse);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (projectDataResponse != null
                                && projectDataResponse.getStatus() == 200 && projectDataResponse.getData().getResponseCode() == 101) {
                            doUpdateRegionTable(projectDataResponse.getData().getRegions(), userid);
                            List<com.pronovoscm.model.response.projectdata.Projects> projects = projectDataResponse.getData().getProjects();
                            List<com.pronovoscm.model.response.projectdata.Company_list> company_lists = projectDataResponse.getData().getCompany_list();
                            List<com.pronovoscm.model.response.projectdata.Assigned_to> assigneeLists = projectDataResponse.getData().getAssigned_to();
                            for (int i = 0; i < projects.size(); i++) {
                                doProjectUpdate(projects.get(i).getProjectsList(), String.valueOf(projects.get(i).getRegion_id()), userid);
                            }
                            for (int i = 0; i < company_lists.size(); i++) {
                                fieldPaperWorkRepository.doUpdateCompanyList(company_lists.get(i).getCompanies(), company_lists.get(i).getProject_id(), userid);
                            }
                            for (int i = 0; i < assigneeLists.size(); i++) {
                                fieldPaperWorkRepository.doUpdateAssigneeTable(assigneeLists.get(i).getAssignee_list(), assigneeLists.get(i).getProject_id(), userid);
                            }
                        } else if (projectDataResponse != null) {
                            regionListResponse.failure(projectDataResponse.getMessage());
                        } else {
                            regionListResponse.failure("response null");
                        }
                    } else {
                        regionListResponse.failure("response null");
                    }
                }
            });

        } else {
            List<RegionsTable> regionsTables = getAllActiveRegions();
            regionListResponse.success(regionsTables);
        }
    }

    /**
     * Service call to get photos photo tag
     *
     * @param tagResponse
     * @param loginResponse
     */
    public void getPhotoTags(final ProviderResult<List<ImageTag>> tagResponse, LoginResponse loginResponse) {

        if (NetworkService.isNetworkAvailable(context)) {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());


            // headers.put("Authorization", "Bearer " + this.loginResponse.getUserDetails().getAuthtoken());
            if (loginResponse != null && loginResponse.getUserDetails() != null)
                headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            else {
                tagResponse.AccessTokenFailure("");
                return;
            }


            Call<TagsResponse> projectResponseCall = projectsApi.getPhotoTags(headers);

            projectResponseCall.enqueue(new AbstractCallback<TagsResponse>() {
                @Override
                protected void handleFailure(Call<TagsResponse> call, Throwable throwable) {
                    tagResponse.failure(throwable.getMessage());
                    tagResponse.success(getImageTags("", loginResponse));
                }

                @Override
                protected void handleError(Call<TagsResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        tagResponse.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        tagResponse.failure(Objects.requireNonNull(errorResponse).getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<TagsResponse> response) {
                    if (response.body() != null) {

                        TagsResponse tagsResponse = null;
                        try {
                            tagsResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (tagsResponse != null && tagsResponse.getStatus() == 200 && tagsResponse.getData().getResponsecode() == 101) {
                            List<ImageTag> imageTags = doUpdateTagTable(tagsResponse.getData().getTags(), loginResponse);
                            tagResponse.success(imageTags);
                        } else if (tagsResponse != null) {
                            tagResponse.failure(tagsResponse.getMessage());
                        } else {
                            tagResponse.failure("response null");
                        }
                    } else {
                        tagResponse.failure("response null");
                    }
                }
            });

        } else {
            tagResponse.success(getImageTags("", loginResponse));
        }
    }

    /**
     * Service call to get albums according to album request with project id and list of album
     *
     * @param albumRequest
     * @param projectResponseProviderResult
     */
    public void getProjectAlbum(AlbumRequest albumRequest, final ProviderResult<List<PhotoFolder>> projectResponseProviderResult) {

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", getMAXAlbumUpdateDate(albumRequest.getProjectId()));
            headers.put("timezone", TimeZone.getDefault().getID());

            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

            // headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            if (loginResponse != null && loginResponse.getUserDetails() != null)
                headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            else {
                projectResponseProviderResult.AccessTokenFailure("");
                return;
            }


            Call<AlbumResponse> projectResponseCall = projectsApi.getProjectAlbums(headers, albumRequest);

            projectResponseCall.enqueue(new AbstractCallback<AlbumResponse>() {
                @Override
                protected void handleFailure(Call<AlbumResponse> call, Throwable throwable) {
                    projectResponseProviderResult.failure(throwable.getMessage());
                    projectResponseProviderResult.success(getPhotoFolders(albumRequest.getProjectId(), ""));
                }

                @Override
                protected void handleError(Call<AlbumResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        projectResponseProviderResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        projectResponseProviderResult.failure(Objects.requireNonNull(errorResponse).getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<AlbumResponse> response) {
                    if (response.body() != null) {
                        AlbumResponse albumResponse = null;
                        try {
                            albumResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (albumResponse != null && albumResponse.getStatus() == 200 && (albumResponse.getAlbumData().getResponsecode() == 101 || albumResponse.getAlbumData().getResponsecode() == 102)) {
                            List<PhotoFolder> photoFolders = doUpdateAlbumTable(response.body().getAlbumData().getAlbums(), albumRequest.getProjectId());
                            projectResponseProviderResult.success(photoFolders);
                        } else if (albumResponse != null) {
                            projectResponseProviderResult.failure(albumResponse.getMessage());
                        } else {
                            projectResponseProviderResult.failure("response null");
                        }
                    } else {
                        projectResponseProviderResult.failure("response null");
                    }
                }
            });

        } else {
            projectResponseProviderResult.failure(context.getString(R.string.internet_connection_check));
    /* List<PhotoFolder> photoFolders = getPhotoFolders(albumRequest.getProjectId(), "");
            projectResponseProviderResult.success(photoFolders);
        */
        }
    }

    /**
     * Service call to get photos according to photo request with folder id and list of photo
     *
     * @param photoRequest
     * @param projectId
     * @param photoFolderMobileId
     * @param projectResponseProviderResult
     */
    public Call<PhotoResponse> getAlbumPhoto(PhotoRequest photoRequest, int projectId, long photoFolderMobileId, final ProviderResult<List<PhotosMobile>> projectResponseProviderResult) {

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", getMAXPhotoUpdatedDate(photoRequest.getAlbumId(), projectId));
            headers.put("timezone", TimeZone.getDefault().getID());

            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

            //  headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            if (loginResponse != null && loginResponse.getUserDetails() != null)
                headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            else {
                projectResponseProviderResult.AccessTokenFailure("");
                return null;
            }
//            List<Photo> photoList = getAllNonSyncPhoto(projectId, photoRequest.getAlbumId());
            List<Photo> photoList = new ArrayList<>();
            photoRequest.setPhotos(photoList);
            Call<PhotoResponse> projectResponseCall = projectsApi.getAlbumsPhoto(headers, photoRequest);

            projectResponseCall.enqueue(new AbstractCallback<PhotoResponse>() {
                @Override
                protected void handleFailure(Call<PhotoResponse> call, Throwable throwable) {
                    updatePhotoMobile(photoList, projectId);
                    projectResponseProviderResult.failure(throwable.getMessage());
                    projectResponseProviderResult.success(getAlbumPhotos(photoRequest.getAlbumId(), projectId, photoFolderMobileId));
                }

                @Override
                protected void handleError(Call<PhotoResponse> call, ErrorResponse errorResponse) {
                    updatePhotoMobile(photoList, projectId);
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        projectResponseProviderResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        projectResponseProviderResult.failure(Objects.requireNonNull(errorResponse).getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<PhotoResponse> response) {
                    if (response.body() != null) {

                        PhotoResponse photoResponse = null;
                        try {
                            photoResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (photoResponse != null && photoResponse.getStatus() == 200 && (photoResponse.getPhotoData().getResponseCode() == 101 || photoResponse.getPhotoData().getResponseCode() == 102)) {

                            LoadPhotoInBackground loadPhotoInBackground = new LoadPhotoInBackground(response.body().getPhotoData().getPhotos(), photoRequest.getAlbumId(), photoFolderMobileId, projectId, projectResponseProviderResult);
                            loadPhotoInBackground.execute();
//                            List<PhotosMobile> photosMobiles = doUpdatePhotos(response.body().getPhotoData().getPhotos(), photoRequest.getAlbumId(), photoFolderMobileId, projectId);

//                            projectResponseProviderResult.success(photosMobiles);
                        } else if (photoResponse != null) {
                            updatePhotoMobile(photoList, projectId);
                            projectResponseProviderResult.failure(photoResponse.getMessage());
                        } else {
                            updatePhotoMobile(photoList, projectId);
                            projectResponseProviderResult.failure("response null");
                        }
                    } else {
                        updatePhotoMobile(photoList, projectId);
                        projectResponseProviderResult.failure("response null");
                    }
                }
            });
            return projectResponseCall;
        } else {
            projectResponseProviderResult.success(getAlbumPhotos(photoRequest.getAlbumId(), projectId, photoFolderMobileId));
            return null;
        }

    }

    /**
     * Service call to get photos according to photo request with folder id and list of photo
     *
     * @param photoRequest
     * @param projectId
     * @param photoFolderMobileId
     * @param projectResponseProviderResult
     */
    public Call<PhotoResponse> getAlbumPhoto(List<Photo> photoList, PhotoRequest photoRequest, int projectId, long photoFolderMobileId,
                                             final ProviderResult<List<PhotosMobile>> projectResponseProviderResult) {

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", getMAXPhotoUpdatedDate(photoRequest.getAlbumId(), projectId));
            headers.put("timezone", TimeZone.getDefault().getID());

            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

            //headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            if (loginResponse != null && loginResponse.getUserDetails() != null)
                headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            else {
                projectResponseProviderResult.AccessTokenFailure("");
                return null;
            }
//            List<Photo> photoList = getAllNonSyncPhoto(projectId, photoRequest.getAlbumId());
//            List<Photo> photoList = new ArrayList<>();
            photoRequest.setPhotos(photoList);
            Call<PhotoResponse> projectResponseCall = projectsApi.getAlbumsPhoto(headers, photoRequest);

            projectResponseCall.enqueue(new AbstractCallback<PhotoResponse>() {
                @Override
                protected void handleFailure(Call<PhotoResponse> call, Throwable throwable) {
                    updatePhotoMobile(photoList, projectId);
                    projectResponseProviderResult.failure(throwable.getMessage());
                    projectResponseProviderResult.success(getAlbumPhotos(photoRequest.getAlbumId(), projectId, photoFolderMobileId));
                }

                @Override
                protected void handleError(Call<PhotoResponse> call, ErrorResponse errorResponse) {
                    updatePhotoMobile(photoList, projectId);
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        projectResponseProviderResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        projectResponseProviderResult.failure(Objects.requireNonNull(errorResponse).getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<PhotoResponse> response) {
                    if (response.body() != null) {

                        PhotoResponse photoResponse = null;
                        try {
                            photoResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (photoResponse != null && photoResponse.getStatus() == 200 && (photoResponse.getPhotoData().getResponseCode() == 101 || photoResponse.getPhotoData().getResponseCode() == 102)) {
                            List<PhotosMobile> photosMobiles = doUpdatePhotos(response.body().getPhotoData().getPhotos(), photoRequest.getAlbumId(), photoFolderMobileId, projectId);
                            projectResponseProviderResult.success(photosMobiles);
                        } else if (photoResponse != null) {
                            updatePhotoMobile(photoList, projectId);
                            projectResponseProviderResult.failure(photoResponse.getMessage());
                        } else {
                            updatePhotoMobile(photoList, projectId);
                            projectResponseProviderResult.failure("response null");
                        }
                    } else {
                        updatePhotoMobile(photoList, projectId);
                        projectResponseProviderResult.failure("response null");
                    }
                }
            });
            return projectResponseCall;
        } else {
//            projectResponseProviderResult.success(getAlbumPhotos(photoRequest.getAlbumId(), projectId, photoFolderMobileId));
            projectResponseProviderResult.failure("response null");
            return null;
        }

    }

    /**
     * show Logout Alert
     *
     * @param context
     * @param message
     * @param positiveButtonText
     * @param negativeButtonText
     */

    public void showLogoutAlert(final Context context, String message, String positiveButtonText, String negativeButtonText) {
        try {
            if (alertDialog == null || !alertDialog.isShowing()) {
                alertDialog = new AlertDialog.Builder(context).create();
            }
            alertDialog.setMessage(message);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, positiveButtonText, (dialog, which) -> {
                alertDialog.dismiss();
                context.startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(context).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(context).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                ((Activity) context).finish();
            });
            if (negativeButtonText != null) {
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, negativeButtonText, (dialog, which) -> alertDialog.dismiss());
            }
            alertDialog.setCancelable(false);
            if (alertDialog != null && !alertDialog.isShowing()) {
                alertDialog.show();
            }
            Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(ContextCompat.getColor(context, R.color.gray_948d8d));
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * ALert to show message
     *
     * @param context
     * @param message
     * @param positiveButtonText
     * @param isShowMessage
     */
    public void showMessageAlert(final Context context, String message, String positiveButtonText, boolean isShowMessage) {
        try {
            if (alertDialog == null || !alertDialog.isShowing()) {
                alertDialog = new AlertDialog.Builder(context).create();
            }
            if (isShowMessage) {
//                alertDialog.setTitle(context.getString(R.string.message));
            }
            alertDialog.setMessage(message);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, positiveButtonText, (dialog, which) -> alertDialog.dismiss());
            if (alertDialog != null && !alertDialog.isShowing()) {
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Insert or update projects
     *
     * @param items
     * @param regionId
     * @param users_id
     * @return
     */
    private List<PjProjects> doProjectUpdate(final List<Projects> items, String regionId, int users_id) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        try {

            daoSession.callInTx(new Callable<List<com.pronovoscm.model.response.projects.Projects>>() {
                final PjProjectsDao projectsDao = daoSession.getPjProjectsDao();

                @Override
                public List<Projects> call() {
                    for (Projects response : items) {
                        List<PjProjects> pjProjects = daoSession.getPjProjectsDao().queryBuilder().where(PjProjectsDao.Properties.PjProjectsId.eq(response.getPjProjectsId()),
                                PjProjectsDao.Properties.UsersId.eq(users_id)).limit(1).list();


                        if (pjProjects.size() > 0) {
                            pjProjects.get(0).setAddress(response.getAddress());
                            pjProjects.get(0).setCity(response.getCity());
                            pjProjects.get(0).setCreatedAt(response.getCreatedAt() != null && !response.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(response.getCreatedAt()) : null);
                            pjProjects.get(0).setIsArchived(response.getIsArchived());
                            pjProjects.get(0).setName(response.getName());
                            pjProjects.get(0).setPjProjectsId(response.getPjProjectsId());
                            pjProjects.get(0).setProjectNumber(response.getProjectNumber());
                            pjProjects.get(0).setState(response.getState());
                            pjProjects.get(0).setUpdatedAt(response.getUpdatedAt() != null && !response.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(response.getUpdatedAt()) : null);
                            pjProjects.get(0).setZip(response.getZip());
                            pjProjects.get(0).setRegion_id(Integer.parseInt(regionId));
                            pjProjects.get(0).setUsersId(users_id);
                            pjProjects.get(0).setShowcasePhoto(response.getShowcasePhoto());
                            projectsDao.update(pjProjects.get(0));
                        } else {
                            PjProjects project = new PjProjects();
                            project.setAddress(response.getAddress());
                            project.setCity(response.getCity());
                            project.setCreatedAt(response.getCreatedAt() != null && !response.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(response.getCreatedAt()) : null);
                            project.setIsArchived(response.getIsArchived());
                            project.setName(response.getName());
                            project.setPjProjectsId(response.getPjProjectsId());
                            project.setProjectNumber(response.getProjectNumber());
                            project.setState(response.getState());
                            project.setUpdatedAt(response.getUpdatedAt() != null && !response.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(response.getUpdatedAt()) : null);
                            project.setZip(response.getZip());
                            project.setRegion_id(Integer.parseInt(regionId));
                            project.setShowcasePhoto(response.getShowcasePhoto());
                            project.setUsersId(users_id);
                            projectsDao.save(project);
                        }
                    }
                    return items;
                }
            });

        } catch (Exception e) {
//            throwable = e;
        }
        return getRegionProject(Integer.parseInt(regionId));
    }

    /**
     * Insert or update regions
     *
     * @param regions
     * @param users_id
     * @return
     */
    private List<RegionsTable> doUpdateRegionTable(List<Regions> regions, int users_id) {

        try {
            daoSession.callInTx(new Callable<List<Regions>>() {
                final RegionsTableDao regionsTableDao = daoSession.getRegionsTableDao();

                @Override
                public List<Regions> call() {
                    for (Regions region :
                            regions) {
//                        List<RegionsTable> regionsTables = daoSession.getRegionsTableDao().queryBuilder().where(RegionsTableDao.Properties.Regions_id.eq(region.getRegionsId())).limit(1).list();
                        RegionsTable regionsTable = new RegionsTable();
                        List<RegionsTable> regionsTableList = daoSession.getRegionsTableDao().queryBuilder().where(
                                RegionsTableDao.Properties.Regions_id.eq(region.getRegionsId()),
                                RegionsTableDao.Properties.UsersId.eq(users_id)).limit(1).list();
                        if (regionsTableList.size() > 0) {
                            regionsTable = regionsTableList.get(0);
                        }

                        regionsTable.setActive(region.getActive() == 1);
                        regionsTable.setCreated_at(region.getCreatedAt() != null && !region.getCreatedAt().equals("") ? DateFormatter.getDateFromDateString(region.getCreatedAt()) : null);
                        regionsTable.setUpdated_at(region.getUpdatedAt() != null && !region.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateString(region.getUpdatedAt()) : null);

                        regionsTable.setName(region.getName());
                        regionsTable.setRegions_id(region.getRegionsId());

                        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
                        regionsTable.setUsersId(users_id);
                        if (regionsTableList.size() > 0) {
                            regionsTableDao.insertOrReplace(regionsTable);
                        } else {

                            regionsTableDao.save(regionsTable);
                        }

                    }
                    return regions;
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getAllActiveRegions();
    }

    /**
     * Insert or update tags
     *
     * @param tags
     * @param loginResponse
     * @return
     */
    private List<ImageTag> doUpdateTagTable(List<Tags> tags, LoginResponse loginResponse) {

        try {
            daoSession.callInTx(new Callable<List<Tags>>() {
                final ImageTagDao mImageTagDao = daoSession.getImageTagDao();

                @Override
                public List<Tags> call() {
                    for (Tags tag : tags) {
                        List<ImageTag> imageTags = daoSession.getImageTagDao().queryBuilder().where(ImageTagDao.Properties.Id.eq(tag.getId())).limit(1).list();
                        ImageTag imageTag = new ImageTag();
                        imageTag.setId(tag.getId());
                        imageTag.setName(tag.getName());
                        imageTag.setTenantId(loginResponse.getUserDetails().getTenantId());
                        if (imageTags.size() > 0) {
                            mImageTagDao.insertOrReplace(imageTag);
                        } else {

                            mImageTagDao.save(imageTag);
                        }
                    }
                    return tags;
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getImageTags("", loginResponse);
    }

    /**
     * Insert or update photos
     *
     * @param photos
     * @return
     */
    private List<PhotosMobile> doUpdatePhotos(List<PhotoResponse.Photos> photos, int pjPhotosFolderId, long pjPhotosFolderMobileId, int pjProjectId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        int userId = loginResponse.getUserDetails().getUsers_id();
        Log.e(TAG, "doUpdatePhotos: ***************************************   photos     ************************************* " + photos);
        try {
            daoSession.callInTx(new Callable<List<PhotoResponse.Photos>>() {
                final PhotosMobileDao mPhotosMobileDao = daoSession.getPhotosMobileDao();
                final TaggablesDao mTaggablesDao = daoSession.getTaggablesDao();

                @Override
                public List<PhotoResponse.Photos> call() {
                    PhotosMobile lastPhotosMobile = null;
                    for (PhotoResponse.Photos photo : photos) {
                        List<PhotosMobile> photoFolderList = daoSession.getPhotosMobileDao().queryBuilder().where(
                                PhotosMobileDao.Properties.PjPhotosId.eq(photo.getPjPhotosId()),
                                PhotosMobileDao.Properties.PjPhotosFolderId.eq(pjPhotosFolderId),
                                PhotosMobileDao.Properties.PjProjectsId.eq(pjProjectId),
                                PhotosMobileDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id())).limit(1).list();
                        List<PhotosMobile> photoFolderList1 = daoSession.getPhotosMobileDao().queryBuilder().where(
                                PhotosMobileDao.Properties.PjPhotosId.eq(0),
                                PhotosMobileDao.Properties.PjPhotosIdMobile.eq(photo.getPjPhotosIdMobile()),
                                PhotosMobileDao.Properties.PjPhotosFolderMobileId.eq(pjPhotosFolderMobileId),
                                PhotosMobileDao.Properties.PjPhotosFolderId.eq(pjPhotosFolderId),
                                PhotosMobileDao.Properties.PjProjectsId.eq(pjProjectId),
                                PhotosMobileDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id())).limit(1).list();


                        if (photo.getDeletedAt() == null || TextUtils.isEmpty(photo.getDeletedAt())) {

                            long photoMobileId;
                            if (photoFolderList.size() > 0 || photoFolderList1.size() > 0) {
                                PhotosMobile photosMobile = new PhotosMobile();
                                photosMobile.setPjPhotosId(photo.getPjPhotosId());
                                photosMobile.setPjProjectsId(pjProjectId);
                                photosMobile.setCreatedAt(photo.getCreatedAt() != null && !photo.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(photo.getCreatedAt()) : null);
                                photosMobile.setDateTaken(photo.getDateTaken() != null && !photo.getDateTaken().equals("") && !photo.getDateTaken().equals("0000-00-00 00:00:00") ? DateFormatter.getDateFromDateTimeString(photo.getDateTaken()) : null);
                                photosMobile.setIsSync(true);
                                photosMobile.setIsawsSync(true);
                                photosMobile.setPhotoName(photo.getPhotoName());
                                photosMobile.setDescriptions(photo.getDescription());
                                photosMobile.setPhotoLocation(photo.getPhotoLocation());
                                photosMobile.setPhotoThumb(photo.getPhotoThumb());
                                photosMobile.setUploadedBy(photo.getUploadedBy());
                                photosMobile.setPjPhotosFolderId(pjPhotosFolderId);
                                photosMobile.setPjPhotosFolderMobileId(pjPhotosFolderMobileId);
                                photosMobile.setUserId(loginResponse.getUserDetails().getUsers_id());
                                photosMobile.setIsInProcess(false);
                                photosMobile.setSize("0.0 bytes");
                                photosMobile.setUpdatedAt(photo.getUpdatedAt() != null && !photo.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(photo.getUpdatedAt()) : null);
                                if (photoFolderList.size() > 0) {
                                    photosMobile.setPjPhotosIdMobile(photoFolderList.get(0).getPjPhotosIdMobile());
                                    photoMobileId = photoFolderList.get(0).getPjPhotosIdMobile();
                                } else {
                                    photosMobile.setPjPhotosIdMobile(photoFolderList1.get(0).getPjPhotosIdMobile());
                                    photoMobileId = photoFolderList1.get(0).getPjPhotosIdMobile();
                                    Log.i(TAG, "getAlbumPhoto: aaa " + photosMobile.getPjPhotosId() + "  " + photosMobile.getPjPhotosIdMobile() + "  " + photosMobile.getPjPhotosFolderId() + "  " + pjProjectId);

                                }
                                lastPhotosMobile = photosMobile;
                                photosMobile.setDeletedAt(photo.getDeletedAt() != null && !photo.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(photo.getDeletedAt()) : null);
                                mPhotosMobileDao.insertOrReplace(photosMobile);
                            } else {
                                PhotosMobile photosMobile = new PhotosMobile();
                                photoMobileId = generateUniqueMobilePhotoId();
                                photosMobile.setPjPhotosId(photo.getPjPhotosId());
                                photosMobile.setPjProjectsId(pjProjectId);
                                photosMobile.setPjPhotosIdMobile(photoMobileId);
                                photosMobile.setCreatedAt(photo.getCreatedAt() != null && !photo.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(photo.getCreatedAt()) : null);
//                                photosMobile.setDateTaken(photo.getDateTaken() != null && !photo.getDateTaken().equals("") ? DateFormatter.getDateFromDateTimeString(photo.getDateTaken()) : null);
                                photosMobile.setDateTaken(photo.getDateTaken() != null && !photo.getDateTaken().equals("") && !photo.getDateTaken().equals("0000-00-00 00:00:00") ? DateFormatter.getDateFromDateTimeString(photo.getDateTaken()) : null);
                                photosMobile.setIsSync(true);
                                photosMobile.setIsawsSync(true);
                                photosMobile.setPhotoName(photo.getPhotoName());
                                photosMobile.setDescriptions(photo.getDescription());
                                photosMobile.setPhotoLocation(photo.getPhotoLocation());
                                photosMobile.setPhotoThumb(photo.getPhotoThumb());
                                photosMobile.setUploadedBy(photo.getUploadedBy());
                                photosMobile.setPjPhotosFolderId(pjPhotosFolderId);
                                photosMobile.setPjPhotosFolderMobileId(pjPhotosFolderMobileId);
                                photosMobile.setUserId(loginResponse.getUserDetails().getUsers_id());
                                photosMobile.setIsInProcess(false);
                                photosMobile.setUpdatedAt(photo.getUpdatedAt() != null && !photo.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(photo.getUpdatedAt()) : null);
                                photosMobile.setDeletedAt(photo.getDeletedAt() != null && !photo.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(photo.getDeletedAt()) : null);
                                mPhotosMobileDao.save(photosMobile);

//                                List<PhotosMobile> photosMobiles = daoSession.getPhotosMobileDao().queryBuilder().where(PhotosMobileDao.Properties.PjPhotosIdMobile.isNotNull()).orderDesc(PhotosMobileDao.Properties.PjPhotosIdMobile).limit(1).list();
//                                photoMobileId = photosMobiles.get(0).getPjPhotosIdMobile();
                                lastPhotosMobile = photosMobile;
                            }
                            DeleteQuery<Taggables> tableDeleteQuery = daoSession.queryBuilder(Taggables.class)
                                    .where(TaggablesDao.Properties.TaggableIdMobile.eq(photoMobileId), TaggablesDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()))
                                    .buildDelete();
                            tableDeleteQuery.executeDeleteWithoutDetachingEntities();
                            for (PhotoTag photoTag : photo.getTags()) {
                                Taggables taggables = new Taggables();
                                taggables.setTagName(photoTag.getTag_name());
                                taggables.setTaggableId(photo.getPjPhotosId());
                                taggables.setTaggableIdMobile(photoMobileId);
                                taggables.setTagId(photoTag.getTag_id());
                                taggables.setUserId(loginResponse.getUserDetails().getUsers_id());
                                taggables.setCreatedAt(photo.getCreatedAt() != null && !photo.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(photo.getCreatedAt()) : null);
                                taggables.setUpdatedAt(photo.getUpdatedAt() != null && !photo.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(photo.getUpdatedAt()) : null);
                                mTaggablesDao.save(taggables);
                            }

                        } else if (photoFolderList.size() > 0) {

                            deleteLocalPhotos(photo.getPhotoName());

                            DeleteQuery<Taggables> tableDeleteQuery = daoSession.queryBuilder(Taggables.class)
                                    .where(TaggablesDao.Properties.TaggableId.eq(photo.getPjPhotosId()), TaggablesDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()))
                                    .buildDelete();
                            tableDeleteQuery.executeDeleteWithoutDetachingEntities();
                            DeleteQuery<PhotosMobile> photoDeleteQuery = daoSession.queryBuilder(PhotosMobile.class)
                                    .where(PhotosMobileDao.Properties.PjPhotosId.eq(photo.getPjPhotosId()),
                                            PhotosMobileDao.Properties.PjPhotosFolderId.eq(pjPhotosFolderId),
                                            PhotosMobileDao.Properties.PjProjectsId.eq(pjProjectId),
                                            PhotosMobileDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()))
                                    .buildDelete();
                            photoDeleteQuery.executeDeleteWithoutDetachingEntities();
                            DeleteQuery<AlbumCoverPhoto> coverPhotoDeleteQuery = daoSession.queryBuilder(AlbumCoverPhoto.class)
                                    .where(AlbumCoverPhotoDao.Properties.PjPhotoId.eq(photo.getPjPhotosId()),
                                            AlbumCoverPhotoDao.Properties.PjPhotosFolderId.eq(pjPhotosFolderId),
                                            AlbumCoverPhotoDao.Properties.PjProjectsId.eq(pjProjectId)
                                    ).buildDelete();
                            coverPhotoDeleteQuery.executeDeleteWithoutDetachingEntities();
                        }

                    }
                    if (lastPhotosMobile == null) {
                        QueryBuilder<PhotosMobile> queryBuilder = daoSession.getPhotosMobileDao().queryBuilder();
                        queryBuilder.where(PhotosMobileDao.Properties.PjProjectsId.eq(pjProjectId),
                                PhotosMobileDao.Properties.PjPhotosFolderMobileId.eq(pjPhotosFolderMobileId),
                                PhotosMobileDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()),
                                PhotosMobileDao.Properties.DeletedAt.isNull(),
                                PhotosMobileDao.Properties.DateTaken.isNotNull());
                        queryBuilder.orderDesc(PhotosMobileDao.Properties.DateTaken).limit(1);
                        List<PhotosMobile> dateTakenPhotos = queryBuilder.build().list();
                        if (dateTakenPhotos != null && dateTakenPhotos.size() > 0)
                            lastPhotosMobile = dateTakenPhotos.get(0);
                    }
                    if (lastPhotosMobile != null) {

                        AlbumCoverPhotoDao mAlbumCoverPhotoDao = daoSession.getAlbumCoverPhotoDao();
                        AlbumCoverPhoto albumCoverPhoto = new AlbumCoverPhoto();
                        albumCoverPhoto.setPhotoLocation(lastPhotosMobile.getPhotoThumb());
//                        albumCoverPhoto.setPhotoLocation("/photos/" + lastPhotosMobile.getPhotoName());
                        albumCoverPhoto.setPjPhotoId(lastPhotosMobile.getPjPhotosId());
                        albumCoverPhoto.setPjPhotosFolderId(lastPhotosMobile.getPjPhotosFolderId());
                        albumCoverPhoto.setPhotoName(lastPhotosMobile.getPhotoName());
//            albumCoverPhoto.setPjPhotoMobileId(photoMobileID);
                        albumCoverPhoto.setPjProjectsId(pjProjectId);
                        albumCoverPhoto.setUsersId(userId);
//            albumCoverPhoto.setPhotoLocation(lastPhotosMobile.getPhotoLocation());
                        albumCoverPhoto.setPjPhotosFolderMobileId((int) (long) lastPhotosMobile.getPjPhotosFolderMobileId());
                        mAlbumCoverPhotoDao.insertOrReplace(albumCoverPhoto);
                        Log.i("TEMP", "  bind: 123  " + albumCoverPhoto.getPhotoLocation());
                    }
                    return photos;
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getAlbumPhotos(pjPhotosFolderId, pjProjectId, pjPhotosFolderMobileId);
    }

    /**
     * List of photos
     *
     * @param pjPhotosFolderId
     * @param pjProjectId
     * @param pjPhotosFolderMobileId
     * @return
     */
    public List<PhotosMobile> getAlbumPhotos(int pjPhotosFolderId, int pjProjectId, long pjPhotosFolderMobileId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));


        QueryBuilder<PhotosMobile> queryBuilder = daoSession.getPhotosMobileDao().queryBuilder();
        queryBuilder.where(PhotosMobileDao.Properties.PjProjectsId.eq(pjProjectId),
                PhotosMobileDao.Properties.PjPhotosFolderMobileId.eq(pjPhotosFolderMobileId),
                PhotosMobileDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()),
                PhotosMobileDao.Properties.DeletedAt.isNull(),
                PhotosMobileDao.Properties.DateTaken.isNotNull());
        queryBuilder.orderDesc(PhotosMobileDao.Properties.DateTaken);
        List<PhotosMobile> dateTakenPhotos = queryBuilder.build().list();

        QueryBuilder<PhotosMobile> photosMobileQueryBuilder = daoSession.getPhotosMobileDao().queryBuilder();
        photosMobileQueryBuilder.where(PhotosMobileDao.Properties.PjProjectsId.eq(pjProjectId), PhotosMobileDao.Properties.PjPhotosFolderMobileId.eq(pjPhotosFolderMobileId), PhotosMobileDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id())
                , PhotosMobileDao.Properties.DateTaken.isNull());
        photosMobileQueryBuilder.orderDesc(PhotosMobileDao.Properties.CreatedAt);
        List<PhotosMobile> createdAtPhotos = photosMobileQueryBuilder.build().list();
        List<PhotosMobile> photosMobileList = new ArrayList<>();

        Iterator<PhotosMobile> dateTakenIterator = dateTakenPhotos.iterator();
        Iterator<PhotosMobile> createdAtIterator = createdAtPhotos.iterator();
        PhotosMobile dateTaken = null;
        PhotosMobile createdAt = null;
        while (dateTakenIterator.hasNext() && createdAtIterator.hasNext()) {
            if (dateTaken == null) {
                dateTaken = dateTakenIterator.next();
            }
            if (createdAt == null) {
                createdAt = createdAtIterator.next();
            }
            if (dateTaken.getDateTaken().compareTo(createdAt.getCreatedAt()) > 0) {
                photosMobileList.add(dateTaken);
                dateTaken = null;
            } else {
                photosMobileList.add(createdAt);
                createdAt = null;
            }
        }
        if (dateTaken != null) {
            photosMobileList.add(dateTaken);
        } else if (createdAt != null) {

            photosMobileList.add(createdAt);
        }
        while (dateTakenIterator.hasNext()) {
            photosMobileList.add(dateTakenIterator.next());
        }
        while (createdAtIterator.hasNext()) {
            photosMobileList.add(createdAtIterator.next());
        }
        return photosMobileList;
    }

    /**
     * single photo details
     *
     * @param pjProjectId
     * @param pjPhotosFolderMobileId
     * @param photoMobileId
     * @return
     */
    public PhotosMobile getAlbumPhoto(int pjProjectId, long pjPhotosFolderMobileId, long photoMobileId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        QueryBuilder<PhotosMobile> queryBuilder = daoSession.getPhotosMobileDao().queryBuilder();
        queryBuilder.where(PhotosMobileDao.Properties.PjProjectsId.eq(pjProjectId), PhotosMobileDao.Properties.PjPhotosFolderMobileId.eq(pjPhotosFolderMobileId), PhotosMobileDao.Properties.PjPhotosIdMobile.eq(photoMobileId), PhotosMobileDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id()));
        List<PhotosMobile> result = queryBuilder.build().list();
        return result.get(0);
    }

    /**
     * Insert or Update Album And AlbumCover Table
     *
     * @param albums
     * @param projectId
     * @return
     */
    private List<PhotoFolder> doUpdateAlbumTable(List<Albums> albums, int projectId) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        try {
            daoSession.callInTx(new Callable<List<Albums>>() {
                final PhotoFolderDao mPhotoFolderDao = daoSession.getPhotoFolderDao();
                final AlbumCoverPhotoDao mAlbumCoverPhotoDao = daoSession.getAlbumCoverPhotoDao();

                @Override
                public List<Albums> call() {
                    for (Albums album : albums) {

                        List<PhotoFolder> photoFolderList = daoSession.getPhotoFolderDao().queryBuilder()
                                .where(PhotoFolderDao.Properties.PjPhotosFolderMobileId.eq(album.getPjPhotosFoldersIdMobile()),
                                        PhotoFolderDao.Properties.PjPhotosFolderId.eq(0), PhotoFolderDao.Properties.PjProjectsId.eq(projectId),
                                        PhotoFolderDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).limit(1).list();
                        List<PhotoFolder> photoFolderListAccFolderID = daoSession.getPhotoFolderDao().queryBuilder().
                                where(PhotoFolderDao.Properties.PjPhotosFolderId.eq(album.getPjPhotosFoldersId()),
                                        PhotoFolderDao.Properties.PjProjectsId.eq(projectId),
                                        PhotoFolderDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                                .limit(1).list();
                        if (album.getDeletedAt() == null || TextUtils.isEmpty(album.getDeletedAt())) {
                            AlbumCoverPhoto albumCoverPhoto = new AlbumCoverPhoto();
                            albumCoverPhoto.setPhotoLocation(album.getCoverphoto().getPhotoLocation());
                            albumCoverPhoto.setPhotoName(album.getCoverphoto().getPhotoName());
                            albumCoverPhoto.setPjPhotoId(album.getCoverphoto().getPjPhotosId());

                            //Save Album and its cover data
                            albumCoverPhoto.setPjPhotosFolderId(album.getPjPhotosFoldersId());
                            albumCoverPhoto.setPjProjectsId(projectId);
                            albumCoverPhoto.setUsersId(loginResponse.getUserDetails().getUsers_id());
                            if (photoFolderList.size() > 0 || photoFolderListAccFolderID.size() > 0) {
                                PhotoFolder photoFolder = new PhotoFolder();
                                photoFolder.setCreatedAt(album.getCreatedAt() != null && !album.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(album.getCreatedAt()) : null);
                                photoFolder.setUpdatedAt(album.getUpdatedAt() != null && !album.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(album.getUpdatedAt()) : null);
                                photoFolder.setName(album.getName());
                                photoFolder.setPjProjectsId(projectId);
                                photoFolder.setIsSync(true);
                                photoFolder.setIsStatic(album.getIsStatic());
                                photoFolder.setPjPhotosFolderId(album.getPjPhotosFoldersId());
                                photoFolder.setDeletedAt(null);
                                if (photoFolderListAccFolderID.size() > 0) {
                                    photoFolder.setPjPhotosFolderMobileId(photoFolderListAccFolderID.get(0).getPjPhotosFolderMobileId());
                                    albumCoverPhoto.setPjPhotosFolderMobileId((int) (long) photoFolderListAccFolderID.get(0).getPjPhotosFolderMobileId());
                                } else {
                                    photoFolder.setPjPhotosFolderMobileId(photoFolderList.get(0).getPjPhotosFolderMobileId());

                                    updatePhotosFolderId(album.getPjPhotosFoldersIdMobile(), album.getPjPhotosFoldersId());
                                    albumCoverPhoto.setPjPhotosFolderMobileId(album.getPjPhotosFoldersIdMobile());
                                }
                                photoFolder.setUsersId(loginResponse.getUserDetails().getUsers_id());

                                mPhotoFolderDao.insertOrReplace(photoFolder);
//                                if (!album.getCoverphoto().getPhotoName().equals("default") && album.getCoverphoto().getPjPhotosId() != 0) {
                                mAlbumCoverPhotoDao.insertOrReplace(albumCoverPhoto);
//                                }
                            } else {
                                PhotoFolder photoFolder = new PhotoFolder();
                                photoFolder.setCreatedAt(album.getCreatedAt() != null && !album.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(album.getCreatedAt()) : null);
                                photoFolder.setUpdatedAt(album.getUpdatedAt() != null && !album.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(album.getUpdatedAt()) : null);
                                photoFolder.setName(album.getName());
                                photoFolder.setIsStatic(album.getIsStatic());
                                photoFolder.setPjProjectsId(projectId);
                                photoFolder.setIsSync(true);
                                photoFolder.setPjPhotosFolderId(album.getPjPhotosFoldersId());
                                photoFolder.setDeletedAt(null);
                                photoFolder.setUsersId(loginResponse.getUserDetails().getUsers_id());
                                mPhotoFolderDao.save(photoFolder);

                                List<PhotoFolder> photoFolders = daoSession.getPhotoFolderDao().queryBuilder().where(PhotoFolderDao.Properties.PjPhotosFolderMobileId.isNotNull()).orderDesc(PhotoFolderDao.Properties.PjPhotosFolderMobileId).limit(1).list();
                                albumCoverPhoto.setPjPhotosFolderMobileId((int) (long) photoFolders.get(0).getPjPhotosFolderMobileId());
                                albumCoverPhoto.setPhotoLocation(album.getCoverphoto().getPhotoLocation());
                                mAlbumCoverPhotoDao.save(albumCoverPhoto);
                            }
                        } else if (photoFolderListAccFolderID.size() > 0) {
                            DeleteQuery<AlbumCoverPhoto> tableDeleteQuery = daoSession.queryBuilder(AlbumCoverPhoto.class)
                                    .where(AlbumCoverPhotoDao.Properties.PjPhotosFolderId.eq(album.getPjPhotosFoldersId()), AlbumCoverPhotoDao.Properties.PjProjectsId.eq(projectId), AlbumCoverPhotoDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                                    .buildDelete();
                            tableDeleteQuery.executeDeleteWithoutDetachingEntities();
                            DeleteQuery<PhotoFolder> photoFolderDeleteQuery = daoSession.queryBuilder(PhotoFolder.class)
                                    .where(PhotoFolderDao.Properties.PjPhotosFolderId.eq(album.getPjPhotosFoldersId()), PhotoFolderDao.Properties.PjProjectsId.eq(projectId), PhotoFolderDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                                    .buildDelete();
                            photoFolderDeleteQuery.executeDeleteWithoutDetachingEntities();
                        }

                    }
                    return albums;
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getPhotoFolders(projectId, "");
    }

    private void updatePhotoMobile(List<Photo> photosMobile, int projectsId) {
        for (int i = 0; i < photosMobile.size(); i++) {
            List<PhotosMobile> list = daoSession.getPhotosMobileDao().queryBuilder()
                    .where(PhotosMobileDao.Properties.PjPhotosIdMobile.eq(photosMobile.get(i).getPj_photos_id_mobile()), PhotosMobileDao.Properties.PjPhotosFolderId.eq(photosMobile.get(i).getAlbum_id()), PhotosMobileDao.Properties.PjProjectsId.eq(projectsId)).limit(1).list();
            PhotosMobileDao mPhotosMobileDao = daoSession.getPhotosMobileDao();

            if (list.size() > 0) {
                PhotosMobile photoMobile = list.get(0);
                photoMobile.setIsInProcess(false);
                photoMobile.setDeletedAt(photosMobile.get(i).getDeletedAt() != null && !photosMobile.get(i).getDeletedAt().equals("") ?
                        DateFormatter.getDateFromDateTimeString(photosMobile.get(i).getDeletedAt()) : null);
                mPhotosMobileDao.insertOrReplace(photoMobile);
            }
        }
    }

    /**
     * Get max updated date from projects according to region id
     *
     * @param regionID
     * @return
     */
    private String getMAXProjectUpdateDate(int regionID) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        List<PjProjects> maxPostIdRow = daoSession.getPjProjectsDao().queryBuilder().where(PjProjectsDao.Properties.UpdatedAt.isNotNull(), PjProjectsDao.Properties.Region_id.eq(regionID), PjProjectsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).orderDesc(PjProjectsDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1990-01-01 01:01:01";
    }

    /**
     * Get max updated date from Region table.
     *
     * @return (Default value 1990 - 01 - 01)
     */
    private String getMAXRegionUpdateDate() {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        List<RegionsTable> maxPostIdRow = daoSession.getRegionsTableDao().queryBuilder().where(RegionsTableDao.Properties.Updated_at.isNotNull(), RegionsTableDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).orderDesc(RegionsTableDao.Properties.Updated_at).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdated_at();
            return DateFormatter.formatDateForService(maxUpdatedAt);
        }
        return "1990-01-01";
    }

    /**
     * Gets project for the given region id.
     *
     * @return list of project.
     */
    public List<PjProjects> getRegionProject(int regionId) {
        // get projects according to region id.
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            QueryBuilder<PjProjects> queryBuilder = daoSession.getPjProjectsDao().queryBuilder();
            queryBuilder.where(PjProjectsDao.Properties.Region_id.eq(regionId),
                    PjProjectsDao.Properties.IsArchived.eq(0),
                    PjProjectsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).
                    orderAsc(PjProjectsDao.Properties.ProjectNumber);
            return queryBuilder.build().list();
        } else {
            return new ArrayList<>();
        }

    }

    /**
     * Gets project for the given region id.
     *
     * @return list of project.
     */
    public PjProjects getProjectDetail(int projectID, int userId) {
        // get projects according to region id.
        QueryBuilder<PjProjects> queryBuilder = daoSession.getPjProjectsDao().queryBuilder();
        queryBuilder.where(PjProjectsDao.Properties.PjProjectsId.eq(projectID),
                PjProjectsDao.Properties.UsersId.eq(userId));
        if (queryBuilder.build().list().size() > 0) {
            return queryBuilder.build().list().get(0);
        } else {
            return null;
        }

    }

    /**
     * Gets project for the given region id and search string.
     *
     * @return list of project.
     */
    public List<PjProjects> getSearchProject(int regionId, String search, LoginResponse loginResponse) {
        // get projects according to region id.
//        this.loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        QueryBuilder<PjProjects> queryBuilder = daoSession.getPjProjectsDao().queryBuilder();
        queryBuilder.where(PjProjectsDao.Properties.Region_id.eq(regionId),
                PjProjectsDao.Properties.IsArchived.eq(0),
                PjProjectsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()))
                .whereOr(PjProjectsDao.Properties.Name.like("%" + search + "%"),
                        PjProjectsDao.Properties.ProjectNumber.like("%" + search + "%"))
//                .orderAsc(PjProjectsDao.Properties.ProjectNumber);
                .orderRaw(" CAST(project_number AS UNSIGNED), project_number ");

        List<PjProjects> pjProjects = queryBuilder.build().list();
//        Collections.sort(pjProjects,new NaturalOrderComparator());
        return pjProjects;

    }

    /**
     * Get Active Regions.
     *
     * @return list of Active Regions.
     */
    private List<RegionsTable> getAllActiveRegions() {
        // get projects according to region id.
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (loginResponse != null) {
            QueryBuilder<RegionsTable> queryBuilder = daoSession.getRegionsTableDao().queryBuilder();
            queryBuilder.where(RegionsTableDao.Properties.Active.eq(true), RegionsTableDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).orderAsc(RegionsTableDao.Properties.Name);
            return queryBuilder.build().list();
        } else {
            return new ArrayList<>();
        }

    }

    /**
     * Get Photo folders.
     *
     * @param projectId
     * @param searchString
     * @return list of Photo Folder.
     */
    public List<PhotoFolder> getPhotoFolders(int projectId, String searchString) {
        // get projects according to region id.
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        QueryBuilder<PhotoFolder> queryBuilder = daoSession.getPhotoFolderDao().queryBuilder();
        queryBuilder.where(PhotoFolderDao.Properties.PjProjectsId.eq(projectId), PhotoFolderDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()), PhotoFolderDao.Properties.Name.like("%" + searchString + "%")).orderAsc(PhotoFolderDao.Properties.Name);
        return queryBuilder.build().list();

    }

    /**
     * Get Photo folders.
     *
     * @param projectId
     * @param searchString
     * @return list of Photo Folder.
     */
    public List<PhotoFolder> getNonStaticPhotoFolders(int projectId, String searchString) {
        // get projects according to region id.
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        QueryBuilder<PhotoFolder> queryBuilder = daoSession.getPhotoFolderDao().queryBuilder();
        queryBuilder.where(PhotoFolderDao.Properties.PjProjectsId.eq(projectId),
                PhotoFolderDao.Properties.IsStatic.notEq(1),
                PhotoFolderDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id()),
                PhotoFolderDao.Properties.Name.like("%" + searchString + "%"))
                .orderAsc(PhotoFolderDao.Properties.Name);
        return queryBuilder.build().list();

    }

    /**
     * Get Photo folders.
     *
     * @param photoFolderMobileId
     * @return Photo Folder.
     */
    public PhotoFolder getPhotoFolder(long photoFolderMobileId) {
        // get projects according to region id.
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        QueryBuilder<PhotoFolder> queryBuilder = daoSession.getPhotoFolderDao().queryBuilder();
        queryBuilder.where(PhotoFolderDao.Properties.PjPhotosFolderMobileId.eq(photoFolderMobileId), PhotoFolderDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).orderAsc(PhotoFolderDao.Properties.Name).limit(1);
        List<PhotoFolder> result = queryBuilder.build().list();
        return result.get(0);

    }

    public List<ImageTag> getImageTags(String searchString, LoginResponse loginResponse) {
        // get Tag list.
        QueryBuilder<ImageTag> queryBuilder = daoSession.getImageTagDao().queryBuilder();
        queryBuilder.where(ImageTagDao.Properties.Name.like("%" + searchString + "%"), ImageTagDao.Properties.TenantId.eq(loginResponse.getUserDetails().getTenantId()));
        return queryBuilder.build().list();

    }

    /**
     * Get max updated at according to project id.
     *
     * @param projectId
     * @return (Default value 1970 - 01 - 01 01 : 01 : 01)
     */

    private String getMAXAlbumUpdateDate(int projectId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        List<PhotoFolder> maxPostIdRow = daoSession.getPhotoFolderDao().queryBuilder().where(PhotoFolderDao.Properties.UpdatedAt.isNotNull(), PhotoFolderDao.Properties.PjProjectsId.eq(projectId), PhotoFolderDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).orderDesc(PhotoFolderDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1970-01-01 01:01:01";
    }

    /**
     * Get max updated at of photos according to project id and album id.
     *
     * @param albumId
     * @param projectId
     * @return (Default value 1970 - 01 - 01 01 : 01 : 01)
     */

    private String getMAXPhotoUpdatedDate(int albumId, int projectId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        List<PhotosMobile> maxPostIdRow = daoSession.getPhotosMobileDao().queryBuilder().where(PhotosMobileDao.Properties.UpdatedAt.isNotNull(), PhotosMobileDao.Properties.PjProjectsId.eq(projectId), PhotosMobileDao.Properties.PjPhotosFolderId.eq(albumId), PhotosMobileDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id())).orderDesc(PhotosMobileDao.Properties.UpdatedAt).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
            return DateFormatter.formatDateTimeForService(maxUpdatedAt);
        }
        return "1970-01-01 01:01:01";
    }

    /**
     * Get max updated at of photos according to project id and album id.
     *
     * @param albumId
     * @param projectId
     * @return (Default value 1970 - 01 - 01 01 : 01 : 01)
     */

    public int getMINPhotoID(int albumId, int projectId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        List<PhotosMobile> maxPostIdRow = daoSession.getPhotosMobileDao().queryBuilder().where(PhotosMobileDao.Properties.PjProjectsId.eq(projectId), PhotosMobileDao.Properties.PjPhotosFolderId.eq(albumId), PhotosMobileDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id())).orderAsc(PhotosMobileDao.Properties.PjPhotosId).limit(1).list();
        if (maxPostIdRow.size() > 0) {
            int maxUpdatedAt = maxPostIdRow.get(0).getPjPhotosId();
            return maxUpdatedAt;
        }
        return -1;
    }

    /**
     * Get photo location string from Cover photo according pj project id and pj photo folder id
     *
     * @param pjProjectsId
     * @param pjPhotosFolderId
     * @return
     */
    public AlbumCoverPhoto getCoverPhoto(int pjProjectsId, int pjPhotosFolderId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        List<AlbumCoverPhoto> albumCoverPhotos = daoSession.getAlbumCoverPhotoDao().queryBuilder()
                .where(AlbumCoverPhotoDao.Properties.PjPhotosFolderMobileId.eq(pjPhotosFolderId),
                        AlbumCoverPhotoDao.Properties.PjProjectsId.eq(pjProjectsId),
                        AlbumCoverPhotoDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).limit(1).list();
        if (albumCoverPhotos.size() > 0) {

            return albumCoverPhotos.get(0);
        }
        return null;
    }

    public int getAlbumPhotosCount(int projectId, long albumMobileId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));


        List<PhotosMobile> photosMobiles = daoSession.getPhotosMobileDao().queryBuilder().where(PhotosMobileDao.Properties.PjProjectsId.eq(projectId),
                PhotosMobileDao.Properties.PjPhotosFolderMobileId.eq(albumMobileId),
                PhotosMobileDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id())).list();

        return photosMobiles.size();
    }

    /**
     * Add a new Folder offline
     *
     * @param folderName
     * @param projectId
     */
    public void addNewFolder(String folderName, int projectId) {


        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        int userId = loginResponse.getUserDetails().getUsers_id();
        PhotoFolderDao mPhotoFolderDao = daoSession.getPhotoFolderDao();
        PhotoFolder photoFolder = new PhotoFolder();
        Date defaultDate = DateFormatter.getDateFromDateTimeString(DateFormatter.DEFAULT_DATE);
        photoFolder.setCreatedAt(defaultDate);
        photoFolder.setUpdatedAt(defaultDate);
        photoFolder.setDeletedAt(defaultDate);
        photoFolder.setName(folderName);
        photoFolder.setPjProjectsId(projectId);
        photoFolder.setIsSync(false);
        photoFolder.setPjPhotosFolderId(0);
        photoFolder.setIsStatic(0);
        photoFolder.setUsersId(userId);
        mPhotoFolderDao.save(photoFolder);

        AlbumCoverPhotoDao mAlbumCoverPhotoDao = daoSession.getAlbumCoverPhotoDao();
        AlbumCoverPhoto albumCoverPhoto = new AlbumCoverPhoto();
        albumCoverPhoto.setPhotoLocation("http://d17mrl0bfuhyge.cloudfront.net/photos/default-image-2x.png");
        albumCoverPhoto.setUsersId(loginResponse.getUserDetails().getUsers_id());
        albumCoverPhoto.setPjPhotoId(0);
        albumCoverPhoto.setPjPhotosFolderId(0);
        albumCoverPhoto.setPhotoName("default");
        albumCoverPhoto.setPjProjectsId(projectId);
        List<PhotoFolder> maxPostIdRow = daoSession.getPhotoFolderDao().queryBuilder().where(PhotoFolderDao.Properties.PjPhotosFolderMobileId.isNotNull()).orderDesc(PhotoFolderDao.Properties.PjPhotosFolderMobileId).limit(1).list();

        albumCoverPhoto.setPjPhotosFolderMobileId((int) (long) (maxPostIdRow.get(0).getPjPhotosFolderMobileId()));
        mAlbumCoverPhotoDao.save(albumCoverPhoto);

        TransactionLogMobileDao mPronovosSyncDataDao = daoSession.getTransactionLogMobileDao();

        TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
        transactionLogMobile.setUsersId(userId);
        transactionLogMobile.setModule(TransactionModuleEnum.ALBUM.ordinal());
        transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
        transactionLogMobile.setMobileId(maxPostIdRow.get(0).getPjPhotosFolderMobileId());
        transactionLogMobile.setServerId(0L);
        transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
        mPronovosSyncDataDao.save(transactionLogMobile);
        Log.i(TAG, "add new folder: setupAndStartWorkManager");

        context.setupAndStartWorkManager();

    }

    /**
     * Get Folders Added in local database
     *
     * @param projectId
     * @return
     */
    public List<Album> getAllNonSyncFolder(int projectId) {
        List<Album> albums = new ArrayList<>();
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        List<PhotoFolder> photoFolderList = daoSession.getPhotoFolderDao().queryBuilder().where(PhotoFolderDao.Properties.PjProjectsId.eq(projectId), PhotoFolderDao.Properties.IsSync.eq(false), PhotoFolderDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())).list();
        for (PhotoFolder photoFolder :
                photoFolderList) {
            albums.add(new Album(DateFormatter.formatDateTimeForService(photoFolder.getCreatedAt()), DateFormatter.formatDateTimeForService(photoFolder.getDeletedAt()), DateFormatter.formatDateTimeForService(photoFolder.getUpdatedAt()), String.valueOf(photoFolder.getPjPhotosFolderMobileId()), String.valueOf(photoFolder.getPjPhotosFolderId()), photoFolder.getName()));
        }
        return albums;
    }

    /**
     * Get Folders Added in local database
     *
     * @return
     */
    public List<PhotoFolder> getAllNonSyncFolder() {
        List<PhotoFolder> photoFolderList = daoSession.getPhotoFolderDao().queryBuilder()
                .where(PhotoFolderDao.Properties.IsSync.eq(false)).list();

        return photoFolderList;
    }

    /**
     * Delete Pronovos images saved with folder.
     */
    private void deleteLocalPhotos(String name) {
        /*File photo = new File(Environment.getExternalStorageDirectory() + "/Pronovos/" + name);
        File thumbPhoto = new File(Environment.getExternalStorageDirectory() + "/Pronovos/ThumbImage/thumb" + name);*/
        File photo = new File(context.getFilesDir().getAbsolutePath() + "/Pronovos/" + name);
        File thumbPhoto = new File(context.getFilesDir().getAbsolutePath() + "/Pronovos/ThumbImage/thumb" + name);

        if (photo.exists()) {
            photo.delete();
        }
        if (thumbPhoto.exists()) {
            thumbPhoto.delete();
        }

    }

    public List<ImageTag> getPhotosTag(long photoMobileId, LoginResponse loginResponse) {

        List<Taggables> photoFolderList = daoSession.getTaggablesDao().queryBuilder().where(TaggablesDao.Properties.TaggableIdMobile.eq(photoMobileId), TaggablesDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id())).list();
        Query query = daoSession.getImageTagDao().queryBuilder().where(ImageTagDao.Properties.TenantId.eq(loginResponse.getUserDetails().getTenantId()),
                new WhereCondition.StringCondition("id IN " +
                        "(SELECT tag_id FROM Taggables where taggable_id_mobile = " + photoMobileId + ")")).build();

        return (List<ImageTag>) query.list();
    }

    public void addImages(ArrayList<String> photoList, int projectId, int albumId, long albumMobileId, Date dateTaken, String descriptions, ArrayList<ImageTag> imageTags) {
        PhotosMobileDao photosMobileDao = daoSession.getPhotosMobileDao();
        TaggablesDao mTaggablesDao = daoSession.getTaggablesDao();
        PhotosMobile lastPhotosMobile = null;
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        int userId = loginResponse.getUserDetails().getUsers_id();
        for (String photoName :
                photoList) {
            PhotosMobile photosMobile = new PhotosMobile();
            Long photoMobileID = generateUniqueMobilePhotoId();
            Date defaultDate = DateFormatter.getDateFromDateTimeString(DateFormatter.DEFAULT_DATE);
            photosMobile.setCreatedAt(defaultDate);
            photosMobile.setUpdatedAt(defaultDate);
            photosMobile.setDateTaken(dateTaken);
            photosMobile.setPhotoLocation("/photos/" + photoName);
            photosMobile.setPjPhotosIdMobile(photoMobileID);
            photosMobile.setPhotoName(photoName);
            photosMobile.setPhotoThumb(null);
            photosMobile.setPjProjectsId(projectId);
            photosMobile.setIsSync(false);
            photosMobile.setIsawsSync(false);
            photosMobile.setPjPhotosFolderId(albumId);
            photosMobile.setPjPhotosId(0);
            photosMobile.setDescriptions(descriptions);
            photosMobile.setPjPhotosFolderMobileId(albumMobileId);
            photosMobile.setIsInProcess(false);
            photosMobile.setSize(convertToFileSizeToString(photoName));
            photosMobile.setUserId(userId);

            photosMobile.setUploadedBy(loginResponse.getUserDetails().getFirstname() + " " + loginResponse.getUserDetails().getLastname());
            lastPhotosMobile = photosMobile;

            photosMobileDao.save(photosMobile);
         /*   List<PhotosMobile> photosMobiles = daoSession.getPhotosMobileDao().queryBuilder().where(PhotosMobileDao.Properties.PjPhotosIdMobile.isNotNull()).orderDesc(PhotosMobileDao.Properties.PjPhotosIdMobile).limit(1).list();
            long photoMobileID = photosMobiles.get(0).getPjPhotosIdMobile();
*/
            for (ImageTag imageTag : imageTags) {
                Taggables taggables = new Taggables();
                taggables.setTagName(imageTag.getName());
                taggables.setTaggableId(photosMobile.getPjPhotosId());
                taggables.setTaggableIdMobile(photoMobileID);
                taggables.setTagId(imageTag.getId());
                taggables.setUserId(userId);
                taggables.setCreatedAt(photosMobile.getCreatedAt() != null && !photosMobile.getCreatedAt().equals("") ? photosMobile.getCreatedAt() : null);
                taggables.setUpdatedAt(photosMobile.getUpdatedAt() != null && !photosMobile.getUpdatedAt().equals("") ? photosMobile.getUpdatedAt() : null);
                mTaggablesDao.save(taggables);
            }

            TransactionLogMobileDao mPronovosSyncDataDao = daoSession.getTransactionLogMobileDao();

            TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
            transactionLogMobile.setUsersId(userId);
            transactionLogMobile.setModule(TransactionModuleEnum.PHOTO.ordinal());
            transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
            transactionLogMobile.setMobileId(photoMobileID);
            transactionLogMobile.setServerId(0L);
            transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
            mPronovosSyncDataDao.save(transactionLogMobile);
        }
        if (lastPhotosMobile != null) {

            AlbumCoverPhotoDao mAlbumCoverPhotoDao = daoSession.getAlbumCoverPhotoDao();
            AlbumCoverPhoto albumCoverPhoto = new AlbumCoverPhoto();
            albumCoverPhoto.setPhotoLocation("/photos/" + lastPhotosMobile.getPhotoName());
            albumCoverPhoto.setPjPhotoId(lastPhotosMobile.getPjPhotosId());
            albumCoverPhoto.setPjPhotosFolderId(lastPhotosMobile.getPjPhotosFolderId());
            albumCoverPhoto.setPhotoName(lastPhotosMobile.getPhotoName());
            albumCoverPhoto.setPjProjectsId(projectId);
            albumCoverPhoto.setUsersId(userId);
//            albumCoverPhoto.setPhotoLocation(lastPhotosMobile.getPhotoLocation());
            albumCoverPhoto.setPjPhotosFolderMobileId((int) (long) lastPhotosMobile.getPjPhotosFolderMobileId());
            mAlbumCoverPhotoDao.insertOrReplace(albumCoverPhoto);
            Log.i("TEMP", "  bind: 123  " + albumCoverPhoto.getPhotoLocation());
        }
        Log.i(TAG, "add photo: setupAndStartWorkManager");

        context.setupAndStartWorkManager();


    }

    public Long generateUniqueMobilePhotoId() {

        long timeSeed = System.nanoTime(); // to get the current date time value

        double randSeed = Math.random() * 1000; // random number generation

        long mobileId = (long) (timeSeed * randSeed);

        String s = mobileId + "";
        String subStr = s.substring(0, 9);
        mobileId = Long.parseLong(subStr);

        List<PhotosMobile> photoFolderList = daoSession.getPhotosMobileDao().queryBuilder().where(
                PhotosMobileDao.Properties.PjPhotosIdMobile.eq(mobileId)).limit(1).list();
        if (photoFolderList.size() > 0) {
            return generateUniqueMobilePhotoId();
        }
        return mobileId;
    }

    private String convertToFileSizeToString(String photoName) {

//        String completePath = Environment.getExternalStorageDirectory() + "/Pronovos/" + photoName;
        String completePath = context.getFilesDir().getAbsolutePath() + "/Pronovos/" + photoName;

        File file = new File(completePath);
        long length = file.length();
        //        var convertedValue:Double = Double(size)
        int multiplyFactor = 0;
        String[] tokens = {"bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        while (length > 1024) {
            length /= 1024;
            multiplyFactor += 1;
        }
        return length + " " + tokens[multiplyFactor];
    }

    /**
     * Get Folders Added in local database
     *
     * @param projectId
     * @return
     */
    public List<PhotosMobile> getAllNonAWSSyncPhoto(int projectId, long albumId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));


        return daoSession.getPhotosMobileDao().queryBuilder().where(PhotosMobileDao.Properties.PjProjectsId.eq(projectId),
                PhotosMobileDao.Properties.PjPhotosFolderId.eq(albumId), PhotosMobileDao.Properties.IsawsSync.eq(false),
                PhotosMobileDao.Properties.IsInProcess.eq(false), PhotosMobileDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id())).list();
    }

    /**
     * Get Folders Added in local database
     *
     * @return
     */
    public List<PhotosMobile> getAllNonSyncPhoto() {

        return daoSession.getPhotosMobileDao().queryBuilder().whereOr(PhotosMobileDao.Properties.IsawsSync.eq(false),
                PhotosMobileDao.Properties.IsSync.eq(false)).list();
    }

    /**
     * Get Folders Added in local database
     *
     * @param projectId
     * @return
     */
    public List<PhotosMobile> getAllAWSSyncPhotos(int projectId, long albumId) {
        return daoSession.getPhotosMobileDao().queryBuilder().where(PhotosMobileDao.Properties.PjProjectsId.eq(projectId),
                PhotosMobileDao.Properties.PjPhotosFolderId.eq(albumId), PhotosMobileDao.Properties.IsawsSync.eq(true),
                PhotosMobileDao.Properties.IsInProcess.eq(true)).list();
    }

    /**
     * Get Folders Added in local database
     *
     * @param projectId
     * @return
     */
    public List<Photo> getAllNonSyncPhoto(int projectId, long albumId) {
        if (getAllNonAWSSyncPhoto(projectId, albumId).size() > 0) {
            return new ArrayList<>();

        } else {
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

            List<PhotosMobile> photosMobiles = daoSession.getPhotosMobileDao().queryBuilder().where(PhotosMobileDao.Properties.PjProjectsId.eq(projectId), PhotosMobileDao.Properties.PjPhotosFolderId.eq(albumId), PhotosMobileDao.Properties.IsawsSync.eq(true), PhotosMobileDao.Properties.IsSync.eq(false), PhotosMobileDao.Properties.PjPhotosFolderId.notEq(0), PhotosMobileDao.Properties.PjPhotosId.eq(0), PhotosMobileDao.Properties.IsInProcess.eq(false), PhotosMobileDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id())).list();
            PhotosMobileDao mPhotosMobileDao = daoSession.getPhotosMobileDao();
            List<Photo> photoList = new ArrayList<>();
            for (PhotosMobile photoMobile :
                    photosMobiles) {
                Photo photo = new Photo();
                photo.setAlbum_id(albumId);
                photo.setDate_taken(DateFormatter.formatDateTimeForService(photoMobile.getDateTaken()));
                photo.setPhoto_description(photoMobile.getDescriptions());
                photo.setPhoto_location(photoMobile.getPhotoLocation());
                photo.setPhoto_name(photoMobile.getPhotoName());
                photo.setPhoto_size(photoMobile.getSize());
                //  photo.setDeletedAt(photoMobile.getDeletedAt()!=null);
                photo.setPhoto_tags(new ArrayList<>());
                photo.setPj_photos_id(photoMobile.getPjPhotosId());
                photo.setPj_photos_id_mobile(photoMobile.getPjPhotosFolderMobileId());
                List<Taggables> taggables = daoSession.getTaggablesDao().queryBuilder().where(TaggablesDao.Properties.TaggableIdMobile.eq(photoMobile.getPjPhotosIdMobile())).list();
                List<Photo_tags> tagsList = new ArrayList<>();
                for (Taggables tag :
                        taggables
                ) {
                    Photo_tags photo_tags = new Photo_tags();
                    photo_tags.setKeyword(tag.getTagName());
                    tagsList.add(photo_tags);
                }
                photo.setPhoto_tags(tagsList);

                photoList.add(photo);
                photoMobile.setIsInProcess(true);
                mPhotosMobileDao.insertOrReplace(photoMobile);
            }
            return photoList;
        }
    }

    /**
     * Get Folders Added in local database
     *
     * @param projectId
     * @return
     */
    public List<PhotosMobile> getAllNonSyncUpdatePhoto(int projectId, long albumId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        return daoSession.getPhotosMobileDao().queryBuilder().where(PhotosMobileDao.Properties.PjProjectsId.eq(projectId), PhotosMobileDao.Properties.PjPhotosFolderMobileId.eq(albumId), PhotosMobileDao.Properties.PjPhotosId.notEq(0), PhotosMobileDao.Properties.PjPhotosFolderId.notEq(0), PhotosMobileDao.Properties.IsawsSync.eq(true), PhotosMobileDao.Properties.IsSync.eq(false), PhotosMobileDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id())).list();
    }

    /**
     * @param tagId
     * @param mobileTagId
     * @return
     */
    public List<Taggables> getAllTaggables(int tagId, long mobileTagId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));


        return daoSession.getTaggablesDao().queryBuilder().where(TaggablesDao.Properties.TaggableId.eq(tagId), TaggablesDao.Properties.TaggableIdMobile.eq(mobileTagId), TaggablesDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id())).list();
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
            if (loginResponse != null && loginResponse.getUserDetails() != null)
                headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            else {
                projectResponseProviderResult.AccessTokenFailure("");
                return;
            }


            Call<SignedUrlResponse> projectResponseCall = projectsApi.getSignedUrl(headers, signedUrlRequest);

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
                        projectResponseProviderResult.failure(Objects.requireNonNull(errorResponse).getMessage());
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
            projectResponseProviderResult.failure(context.getString(R.string.internet_connection_check));

        }
    }

    private void updatePhotosFolderId(long pjPhotosFolderMobileId, int folderMobileId) {

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        List<PhotosMobile> photosMobiles = daoSession.getPhotosMobileDao().queryBuilder().where(PhotosMobileDao.Properties.PjPhotosFolderMobileId.eq(pjPhotosFolderMobileId), PhotosMobileDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id())).list();
        PhotosMobileDao mPhotosMobileDao = daoSession.getPhotosMobileDao();

        for (PhotosMobile photoMobile : photosMobiles) {
            photoMobile.setPjPhotosFolderId(folderMobileId);
            photoMobile.setPjPhotosFolderMobileId(pjPhotosFolderMobileId);
            photoMobile.setUserId(loginResponse.getUserDetails().getUsers_id());
            mPhotosMobileDao.insertOrReplace(photoMobile);
        }
    }

    /*    */

    /**
     * Update in process status of a photo
     *
     * @param //photosMobiles
     * @param //isInProcess
     *//*
    public void updatePhotosInProcessStatus(List<PhotosMobile> photosMobiles, boolean isInProcess) {
        PhotosMobileDao mPhotosMobileDao = daoSession.getPhotosMobileDao();

        for (PhotosMobile photoMobile : photosMobiles) {
            PhotosMobile photo = new PhotosMobile();
            photo.setPjPhotosId(photoMobile.getPjPhotosId());
            photo.setPjProjectsId(photoMobile.getPjProjectsId());
            photo.setCreatedAt(photoMobile.getCreatedAt());
            photo.setDateTaken(photoMobile.getDateTaken());
            photo.setPhotoName(photoMobile.getPhotoName());
            photo.setDescriptions(photoMobile.getDescriptions());
            photo.setPhotoLocation(photoMobile.getPhotoLocation());
            photo.setPhotoThumb(photoMobile.getPhotoThumb());
            photo.setUploadedBy(photoMobile.getUploadedBy());
            photo.setIsSync(photoMobile.getIsSync());
            photo.setIsawsSync(photoMobile.getIsawsSync());
            photo.setPjPhotosFolderId(photoMobile.getPjPhotosFolderId());
            photo.setPjPhotosFolderMobileId(photoMobile.getPjPhotosFolderMobileId());
            photo.setUserId(photoMobile.getUserId());
            photo.setUpdatedAt(photoMobile.getUpdatedAt());
            photo.setDeletedAt(photoMobile.getDeletedAt());
            photo.setPjPhotosIdMobile(photoMobile.getPjPhotosIdMobile());
            photo.setIsInProcess(isInProcess);
            photo.setSize(photoMobile.getSize());
            mPhotosMobileDao.insertOrReplace(photo);
        }
    }*/
    public ArrayList<String> getCaptureImageList() {
        return captureImageList;
    }

    public void setCaptureImageList(ArrayList<String> captureImageList) {
        this.captureImageList = captureImageList;
    }

    public int getCAMERA_CURRNT_FACE() {
        return CAMERA_CURRNT_FACE;
    }

    public void setCAMERA_CURRNT_FACE(int CAMERA_CURRNT_FACE) {
        this.CAMERA_CURRNT_FACE = CAMERA_CURRNT_FACE;
    }

    public int getCameraFlash() {
        return cameraFlash;
    }

    public void setCameraFlash(int cameraFlash) {
        this.cameraFlash = cameraFlash;
    }

    /**
     * To retrieve photo detail from the database.
     *
     * @param userId        an id of the user.
     * @param photoMobileId mobile id of the photo.
     * @param serverId      server id of the photo.
     * @return an object of PhotosMobile.
     */
    public PhotosMobile getPhotoDetail(int userId, long photoMobileId, long serverId) {
        Log.d(TAG, "getPhotoDetail: userId " + userId + " photoMobileId " + photoMobileId + " serverId  " + serverId);
        List<PhotosMobile> photosMobiles = daoSession.getPhotosMobileDao().queryBuilder().where(
                PhotosMobileDao.Properties.PjPhotosIdMobile.eq(photoMobileId),
                PhotosMobileDao.Properties.PjPhotosId.eq(serverId),
                PhotosMobileDao.Properties.UserId.eq(userId)).list();
        if (photosMobiles != null && photosMobiles.size() > 0)
            return photosMobiles.get(0);
        return null;
    }

    /**
     * To retrieve the album detail from the database.
     *
     * @param userId        an id of the user.
     * @param albumMobileId mobile id of the album.
     * @param serverId      server id of the album.
     * @return an object of PhotoFolder.
     */
    public PhotoFolder getAlbumDetail(int userId, long albumMobileId, long serverId) {
        List<PhotoFolder> photoFolders = daoSession.getPhotoFolderDao().queryBuilder()
                .where(PhotoFolderDao.Properties.PjPhotosFolderMobileId.eq(albumMobileId),
                        PhotoFolderDao.Properties.PjPhotosFolderId.eq(serverId),
                        PhotoFolderDao.Properties.UsersId.eq(userId)).list();

        return photoFolders.get(0);
    }

    public int getProject(int projectId, int userId) {
        List<PjProjects> pjProjects = daoSession.getPjProjectsDao().queryBuilder()
                .where(PjProjectsDao.Properties.PjProjectsId.eq(projectId),
                        PjProjectsDao.Properties.UsersId.eq(userId)).list();

        return pjProjects.get(0).getRegion_id();

    }
   /* public void callDeleteAlbumPhoto(PhotoDeleteRequest request, ProviderResult<DeleteAlbumPhotoResponse>callback){
        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();

            headers.put("timezone", TimeZone.getDefault().getID());

            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
//            List<Photo> photoList = getAllNonSyncPhoto(projectId, photoRequest.getAlbumId());
//            List<Photo> photoList = new ArrayList<>();

            Call<DeleteAlbumPhotoResponse> projectResponseCall = projectsApi.callDeleteAlbumPhoto(headers, request);

            projectResponseCall.enqueue(new AbstractCallback<DeleteAlbumPhotoResponse>() {
                @Override
                protected void handleFailure(Call<DeleteAlbumPhotoResponse> call, Throwable throwable) {
                   *//* updatePhotoMobile(photoList, projectId);
                    projectResponseProviderResult.failure(throwable.getMessage());
                    projectResponseProviderResult.success(getAlbumPhotos(photoRequest.getAlbumId(), projectId, photoFolderMobileId));*//*
                }

                @Override
                protected void handleError(Call<DeleteAlbumPhotoResponse> call, ErrorResponse errorResponse) {
                   *//* updatePhotoMobile(photoList, projectId);
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        projectResponseProviderResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        projectResponseProviderResult.failure(Objects.requireNonNull(errorResponse).getMessage());
                    }*//*
                }

                @Override
                protected void handleSuccess(Response<DeleteAlbumPhotoResponse> response) {
                    if (response.body() != null) {

                        DeleteAlbumPhotoResponse photoResponse = null;
                        try {
                            photoResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                      *//*  if (photoResponse != null && photoResponse.getStatus() == 200 && (photoResponse.getPhotoData().getResponseCode() == 101 || photoResponse.getPhotoData().getResponseCode() == 102)) {
                            List<PhotosMobile> photosMobiles = doUpdatePhotos(response.body().getPhotoData().getPhotos(), photoRequest.getAlbumId(), photoFolderMobileId, projectId);
                            projectResponseProviderResult.success(photosMobiles);
                        } else if (photoResponse != null) {
                            updatePhotoMobile(photoList, projectId);
                            projectResponseProviderResult.failure(photoResponse.getMessage());
                        } else {
                            updatePhotoMobile(photoList, projectId);
                            projectResponseProviderResult.failure("response null");
                        }
                    } else {
                        updatePhotoMobile(photoList, projectId);
                        projectResponseProviderResult.failure("response null");
                    }*//*
                    }
                }
            });
            return  ;
        } else {
//            projectResponseProviderResult.success(getAlbumPhotos(photoRequest.getAlbumId(), projectId, photoFolderMobileId));
            callback.failure(context.getString(R.string.internet_connection_check));
            return  ;
        }
    }*/

    public PhotosMobile updatePhotoForDelete(PhotosMobile photosMobile) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        int userId = loginResponse.getUserDetails().getUsers_id();
        PhotosMobileDao mPhotosMobileDao = daoSession.getPhotosMobileDao();
        List<PhotosMobile> photoFolderList = daoSession.getPhotosMobileDao().queryBuilder().where(PhotosMobileDao.Properties.PjPhotosId.eq(photosMobile.getPjPhotosId()), PhotosMobileDao.Properties.PjPhotosFolderId.eq(photosMobile.getPjPhotosFolderId()), PhotosMobileDao.Properties.PjProjectsId.eq(photosMobile.getPjProjectsId()), PhotosMobileDao.Properties.UserId.eq(loginResponse.getUserDetails().getUsers_id())).limit(1).list();
        if (photoFolderList.size() > 0) {
            photosMobile.setPjPhotosIdMobile(photoFolderList.get(0).getPjPhotosIdMobile());
            photosMobile.setIsSync(false);
//            Log.d(TAG, "Aki updatePhotosData: "+ photoFolderList.get(0).getPjPhotosIdMobile());
        }
        mPhotosMobileDao.insertOrReplace(photosMobile);
        PhotosMobile photosMobile1 = daoSession.getPhotosMobileDao().queryBuilder()
                .where(PhotosMobileDao.Properties.PjPhotosIdMobile.eq(photosMobile.getPjPhotosIdMobile()),
                        PhotosMobileDao.Properties.PjPhotosFolderMobileId.eq(photosMobile.getPjPhotosFolderMobileId()),
                        PhotosMobileDao.Properties.PjProjectsId.eq(photosMobile.getPjProjectsId())).limit(1).list().get(0);

        if (photosMobile.getPjPhotosId() != 0) {
            TransactionLogMobileDao mPronovosSyncDataDao = daoSession.getTransactionLogMobileDao();
            TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
            transactionLogMobile.setUsersId(userId);
            transactionLogMobile.setModule(TransactionModuleEnum.PHOTO_DELETE.ordinal());
            transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
            transactionLogMobile.setMobileId(photosMobile.getPjPhotosIdMobile());
            transactionLogMobile.setServerId(Long.valueOf(photosMobile.getPjPhotosId()));
            transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
            mPronovosSyncDataDao.save(transactionLogMobile);
            Log.i(TAG, "updatePhotoForDelete: setupAndStartWorkManager");
            context.setupAndStartWorkManager();
        }
        return photosMobile1;
    }

    public void createBackUpSyncTransactionLog(int userID) {
        TransactionLogMobileDao mPronovosSyncDataDao = daoSession.getTransactionLogMobileDao();

        TransactionLogMobile transactionLogMobile = new TransactionLogMobile();
        transactionLogMobile.setUsersId(userID);
        transactionLogMobile.setModule(TransactionModuleEnum.SYNC_OLD_FILES.ordinal());
        transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());

        transactionLogMobile.setCreateDate(Calendar.getInstance().getTime());
        mPronovosSyncDataDao.save(transactionLogMobile);
    }

    public void updateSyncFailTransaction(TransactionLogMobile transactionLogMobile) {
        transactionLogMobile.setStatus(SyncDataEnum.SYNC_FAILED.ordinal());
        TransactionLogMobileDao mPronovosSyncDataDao = daoSession.getTransactionLogMobileDao();
        mPronovosSyncDataDao.save(transactionLogMobile);
    }

    public TransactionLogMobile getSyncTransactionLogMobile(int userId) {
        TransactionLogMobileDao mPronovosSyncDataDao = daoSession.getTransactionLogMobileDao();
        List<TransactionLogMobile> transactionLogMobileList = mPronovosSyncDataDao.queryBuilder().where(
                TransactionLogMobileDao.Properties.Module.eq(TransactionModuleEnum.SYNC_OLD_FILES.ordinal()),
                TransactionLogMobileDao.Properties.UsersId.eq(userId)).list();
        if (transactionLogMobileList != null && transactionLogMobileList.size() > 0) {
            return transactionLogMobileList.get(0);
        }
        return null;
    }

    public void deleteBackupSyncTransactionLog(int userID) {
        DeleteQuery<TransactionLogMobile> transactionLogMobileDeleteQuery = daoSession
                .queryBuilder(TransactionLogMobile.class)
                .where(TransactionLogMobileDao.Properties.UsersId.eq(userID),
                        TransactionLogMobileDao.Properties.Module.eq(TransactionModuleEnum.SYNC_OLD_FILES.ordinal()))
                .buildDelete();
        transactionLogMobileDeleteQuery.executeDeleteWithoutDetachingEntities();
    }

    private class LoadPhotoInBackground extends AsyncTask<String, Void, List<PhotosMobile>> {

        List<PhotoResponse.Photos> photos;
        int albumId;
        long photoFolderMobileId;
        int projectId;
        ProviderResult<List<PhotosMobile>> projectResponseProviderResult;

        public LoadPhotoInBackground(List<PhotoResponse.Photos> photos, int albumId, long photoFolderMobileId, int projectId, ProviderResult<List<PhotosMobile>> projectResponseProviderResult) {
            this.photoFolderMobileId = photoFolderMobileId;
            this.photos = photos;
            this.projectId = projectId;
            this.albumId = albumId;
            this.projectResponseProviderResult = projectResponseProviderResult;
        }

        @Override
        protected List<PhotosMobile> doInBackground(String... urls) {
            List<PhotosMobile> photosMobiles = doUpdatePhotos(photos, albumId, photoFolderMobileId, projectId);
            return photosMobiles;
        }
        @Override
        protected void onPostExecute(List<PhotosMobile> photosMobiles) {
            projectResponseProviderResult.success(photosMobiles);
        }
    }

    /**
     * To call api for the submittal list.
     *
     * @param projectId  an id of the project.
     * @return submittalList.
     */

    public void getProjectSubmittalList(int projectId, ProviderResult<SubmittalsResponse> submittalsResponseResult) {
        if (NetworkService.isNetworkAvailable(context)) {
            SubmittalsRequest submittalsListRequest = new SubmittalsRequest();
            submittalsListRequest.setProjectId(projectId);
            HashMap<String, String> headers = new HashMap<>();
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("lastupdate", getMAXProjectSubmittalUpdateDate(projectId));
            if (loginResponse != null && loginResponse.getUserDetails() != null) {
                headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            } else {
                submittalsResponseResult.AccessTokenFailure("");
            }

            Call<SubmittalsResponse> submittalResponseCall = projectsApi.getProjectSubmittals(headers, submittalsListRequest);
            submittalResponseCall.enqueue(new AbstractCallback<SubmittalsResponse>() {

                @Override
                protected void handleFailure(Call<SubmittalsResponse> call, Throwable throwable) {
                    submittalsResponseResult.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<SubmittalsResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        submittalsResponseResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        submittalsResponseResult.failure(Objects.requireNonNull(errorResponse).getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<SubmittalsResponse> response) {
                    if (response.body() != null) {
                        SubmittalsResponse submittalsResponse = null;
                        try {
                            submittalsResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (submittalsResponse != null && submittalsResponse.getStatus() == 200 &&
                                (submittalsResponse.getData().getResponseCode() == 101 || submittalsResponse.getData().getResponseCode() == 102)
                                && submittalsResponse.getData() != null) {
                            deleteProjectSubmittals(submittalsResponse.getData());
                            doUpdateProjectSubmittals(submittalsResponse.getData(), false);
                            submittalsResponseResult.success(submittalsResponse);
                        } else if (submittalsResponse != null) {
                            submittalsResponseResult.failure(submittalsResponse.getMessage());
                        } else {
                            submittalsResponseResult.failure("response null");
                        }

                    }
                }
            });
        } else {
            submittalsResponseResult.failure("response null");
        }
    }

    /**
     * To call api for the submittal detail.
     *
     * @param projectId  an id of the project.
     * @return all submittal detail.
     */


    public void getSubmittalDetail(int projectId, ProviderResult<SubmittalsResponse> submittalsResponseResult) {
        if (NetworkService.isNetworkAvailable(context)) {
            SubmittalsRequest submittalsDetailRequest = new SubmittalsRequest();
            submittalsDetailRequest.setProjectId(projectId);
            HashMap<String, String> headers = new HashMap<>();
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("lastupdate", getMAXProjectSubmittalDetailUpdateDate(projectId));
            if (loginResponse != null && loginResponse.getUserDetails() != null) {
                headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            } else {
                submittalsResponseResult.AccessTokenFailure("");
            }

            Call<SubmittalsResponse> submittalResponseCall = projectsApi.getSubmittalDetail(headers, submittalsDetailRequest);
            submittalResponseCall.enqueue(new AbstractCallback<SubmittalsResponse>() {

                @Override
                protected void handleFailure(Call<SubmittalsResponse> call, Throwable throwable) {
                    submittalsResponseResult.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<SubmittalsResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        submittalsResponseResult.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        submittalsResponseResult.failure(Objects.requireNonNull(errorResponse).getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<SubmittalsResponse> response) {
                    if (response.body() != null) {
                        SubmittalsResponse submittalsResponse = null;
                        try {
                            submittalsResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (submittalsResponse != null && submittalsResponse.getStatus() == 200 &&
                                (submittalsResponse.getData().getResponseCode() == 101 || submittalsResponse.getData().getResponseCode() == 102)
                                && submittalsResponse.getData() != null) {

                            // delete submittals if the deleted at param is having some value
                            deleteProjectSubmittals(submittalsResponse.getData());
                            // update submittal data
                            doUpdateProjectSubmittals(submittalsResponse.getData(), true);
                            submittalsResponseResult.success(submittalsResponse);
                        } else if (submittalsResponse != null) {
                            submittalsResponseResult.failure(submittalsResponse.getMessage());
                        } else {
                            submittalsResponseResult.failure("response null");
                        }

                    }
                }
            });
        } else {
            submittalsResponseResult.failure("response null");
        }
    }

    /**
     * To update submittals data in db.
     */
    private void doUpdateProjectSubmittals(SubmittalsResponseListData submittalsResponseListData, boolean isDetail) {
        try {
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            try {
                daoSession.callInTx(new Callable<List<PjSubmittals>>() {
                    final PjSubmittalsDao pjSubmittalsDao = daoSession.getPjSubmittalsDao();

                    @Override
                    public List<PjSubmittals> call() {
                        List<PjSubmittals> foldersList = new ArrayList<>();
                        if (submittalsResponseListData.getSubmittals() != null && submittalsResponseListData.getSubmittals().size() > 0)
                            for (Submittals submittals : submittalsResponseListData.getSubmittals()) {
                                List<PjSubmittals> localPjSubmittal = pjSubmittalsDao.queryBuilder()
                                        .where(PjSubmittalsDao.Properties.PjProjectsId.eq(submittals.getPjProjectsId()),
                                                PjSubmittalsDao.Properties.PjSubmittalsId.eq(submittals.getPjSubmittalsId()),
                                                PjSubmittalsDao.Properties.DeletedAt.isNull()
                                                , PjSubmittalsDao.Properties.Users_id.eq(loginResponse.getUserDetails().getUsers_id())
                                        ).limit(1).list();
                                if (localPjSubmittal != null && localPjSubmittal.size() > 0) {
                                    PjSubmittals localRecord = localPjSubmittal.get(0);
                                    localRecord = updatePjSubmittal(localRecord, submittals, isDetail);
                                    projectSubmittalsRepository.updatePjSubmittal(localRecord);
                                    foldersList.add(localRecord);
                                }// This check is for the records which are not added into db but deleted
                                else if (submittals.getDeletedAt() == null) {
                                    if (submittals.getPreviousRevisionsList() != null && !submittals.getPreviousRevisionsList().isEmpty()) {
                                        for (int i = 0; i < submittals.getPreviousRevisionsList().size(); i++) {
                                            projectSubmittalsRepository.deletePjSubmittals(submittals.getPreviousRevisionsList().get(i));
                                        }
                                    }
                                    PjSubmittals pjDocFile = createPjSubmittal(submittals, isDetail);
                                    projectSubmittalsRepository.savePjSubmittal(pjDocFile);
                                    foldersList.add(pjDocFile);
                                }
                            }
                        return foldersList;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * To update submittals if already exist in db.
     */
    private PjSubmittals updatePjSubmittal(PjSubmittals localRecord, Submittals submittals, boolean isDetail) {
        localRecord.submittedDate = submittals.getSubmittedDate() != null && !submittals.getSubmittedDate().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getSubmittedDate()) : null;
        localRecord.dueDate = submittals.getDueDate() != null && !submittals.getDueDate().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getDueDate()) : null;
        localRecord.createdAt = submittals.getCreatedAt() != null && !submittals.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getCreatedAt()) : null;
        localRecord.updatedAt = submittals.getUpdatedAt() != null && !submittals.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getUpdatedAt()) : null;
        localRecord.deletedAt = submittals.getDeletedAt() != null && !submittals.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getDeletedAt()) : null;
        localRecord.pjSubmittalsId = submittals.getPjSubmittalsId();
        localRecord.pjProjectsId = submittals.getPjProjectsId();
        localRecord.submittalNumber = submittals.getSubmittalNumber();
        localRecord.submittalTitle = submittals.getSubmittalTitle();
        localRecord.status = submittals.getStatus();
        localRecord.submittalStatus = submittals.getSubmittal_status();
        localRecord.revision = submittals.getRevision();
        localRecord.currentResponseStatus = submittals.getCurrentResponseStatus();
        localRecord.ballInCourt = submittals.getBallInCourt();
        localRecord.users_id = loginResponse.getUserDetails().getUsers_id();
        if (isDetail) {
            localRecord.author = submittals.getAuthor();
            localRecord.isDetailedSync = true;
            localRecord.submittalAuthorName = submittals.getSubmittalAuthorName();
            localRecord.isSubmittalSent = submittals.getIsSubmittalSent();
            localRecord.leadTime = submittals.getLeadTime();
            localRecord.location = submittals.getLocation();
            localRecord.receivedFrom = submittals.getReceivedFrom();
            localRecord.specSection = submittals.getSpecSection();
            localRecord.submittalType = submittals.getSubmittalType();
            localRecord.description = submittals.getDescription();
            localRecord.tenantId = submittals.getTenant_id();
            localRecord.onsiteDate = submittals.getOnsiteDate() != null && !submittals.getOnsiteDate().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getOnsiteDate()) : null;
            localRecord.closedDate = submittals.getClosedDate() != null && !submittals.getClosedDate().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getClosedDate()) : null;
            localRecord.dateSent = submittals.getDateSent() != null && !submittals.getDateSent().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getDateSent()) : null;
            localRecord.receivedDate = submittals.getReceivedDate() != null && !submittals.getReceivedDate().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getReceivedDate()) : null;
            localRecord.detailUpdatedAt = submittals.getUpdatedAt() != null && !submittals.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getUpdatedAt()) : null;
        }
        else {
            localRecord.updatedAt = submittals.getUpdatedAt() != null && !submittals.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getUpdatedAt()) : null;
        }
        addSubmittalDetailToDb(submittals, isDetail);
        return localRecord;
    }

    /**
     * To create submittals if not exist in db.
     */
    private PjSubmittals createPjSubmittal(Submittals submittals, boolean isDetail) {
        PjSubmittals pjSubmittals = new PjSubmittals();

        pjSubmittals.submittedDate = submittals.getSubmittedDate() != null && !submittals.getSubmittedDate().equals("") ?
                DateFormatter.getDateFromDateTimeString(submittals.getSubmittedDate()) : null;
        pjSubmittals.dueDate = submittals.getDueDate() != null && !submittals.getDueDate().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getDueDate()) : null;
        pjSubmittals.createdAt = submittals.getCreatedAt() != null && !submittals.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getCreatedAt()) : null;
        pjSubmittals.deletedAt = submittals.getDeletedAt() != null && !submittals.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getDeletedAt()) : null;
        pjSubmittals.pjSubmittalsId = submittals.getPjSubmittalsId();
        pjSubmittals.pjProjectsId = submittals.getPjProjectsId();
        pjSubmittals.submittalNumber = submittals.getSubmittalNumber();
        pjSubmittals.submittalTitle = submittals.getSubmittalTitle();
        pjSubmittals.submittalStatus = submittals.getSubmittal_status();
        pjSubmittals.revision = submittals.getRevision();
        pjSubmittals.currentResponseStatus = submittals.getCurrentResponseStatus();
        pjSubmittals.ballInCourt = submittals.getBallInCourt();
        pjSubmittals.status = submittals.getStatus();
        pjSubmittals.users_id = loginResponse.getUserDetails().getUsers_id();
        if (isDetail) {
            pjSubmittals.isDetailedSync = true;
            pjSubmittals.author = submittals.getAuthor();
            pjSubmittals.submittalAuthorName = submittals.getSubmittalAuthorName();
            pjSubmittals.isSubmittalSent = submittals.getIsSubmittalSent();
            pjSubmittals.leadTime = submittals.getLeadTime();
            pjSubmittals.location = submittals.getLocation();
            pjSubmittals.receivedFrom = submittals.getReceivedFrom();
            pjSubmittals.specSection = submittals.getSpecSection();
            pjSubmittals.submittalType = submittals.getSubmittalType();
            pjSubmittals.description = submittals.getDescription();
            pjSubmittals.tenantId = submittals.getTenant_id();
            pjSubmittals.onsiteDate = submittals.getOnsiteDate() != null && !submittals.getOnsiteDate().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getOnsiteDate()) : null;
            pjSubmittals.closedDate = submittals.getClosedDate() != null && !submittals.getClosedDate().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getClosedDate()) : null;
            pjSubmittals.dateSent = submittals.getDateSent() != null && !submittals.getDateSent().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getDateSent()) : null;
            pjSubmittals.receivedDate = submittals.getReceivedDate() != null && !submittals.getReceivedDate().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getReceivedDate()) : null;
            pjSubmittals.detailUpdatedAt = submittals.getUpdatedAt() != null && !submittals.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getUpdatedAt()) : null;
        }
        else {
            pjSubmittals.updatedAt = submittals.getUpdatedAt() != null && !submittals.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getUpdatedAt()) : null;
        }
        addSubmittalDetailToDb(submittals, isDetail);

        return pjSubmittals;

    }

    /**
     * To add submittals details in db.
     */
    private void addSubmittalDetailToDb(Submittals submittals, boolean isDetail) {
        if (submittals.getAssigneeSubmittals() != null && !submittals.getAssigneeSubmittals().isEmpty()) {
            // delete the contact records having deleted at param with some value
            deleteSubmittalContact(submittals);
            doUpdateProjectSubmittalsContact(submittals, isDetail);
        }
        if (submittals.getCcListSubmittals() != null && !submittals.getCcListSubmittals().isEmpty()) {
            // delete the cc records having deleted at param with some value
            deleteCcSubmittal(submittals);
            doUpdateProjectSubmittalsCCContact(submittals);
        }
        if (submittals.getAttachmentsSubmittals() != null && !submittals.getAttachmentsSubmittals().isEmpty()) {
            // delete the attachment records having deleted at param with some value
            deleteSubmittalAttachments(submittals);
            doUpdateProjectSubmittalsAttachment(submittals);
        }
    }


    /**
     * To update submittals assignee in db.
     */
    private void doUpdateProjectSubmittalsContact(Submittals submittals, boolean isDetail) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        try {
            daoSession.callInTx(new Callable<List<PjSubmittalContactList>>() {
                final PjSubmittalContactListDao pjSubmittalContactListDao = daoSession.getPjSubmittalContactListDao();

                @Override
                public List<PjSubmittalContactList> call() {
                    List<PjSubmittalContactList> foldersList = new ArrayList<>();
                    if (submittals.getAssigneeSubmittals() != null && submittals.getAssigneeSubmittals().size() > 0)
                        for (AssigneeSubmittals assigneeSubmittals : submittals.getAssigneeSubmittals()) {
                            List<PjSubmittalContactList> localPjSubmittal = pjSubmittalContactListDao.queryBuilder()
                                    .where(PjSubmittalContactListDao.Properties.PjProjectsId.eq(submittals.getPjProjectsId()),
                                            PjSubmittalContactListDao.Properties.PjSubmittalsId.eq(assigneeSubmittals.getPjSubmittalsId()),
                                            PjSubmittalContactListDao.Properties.IsCc.eq(false),
                                            PjSubmittalContactListDao.Properties.DeletedAt.isNull(),
                                            PjSubmittalContactListDao.Properties.PjSubmittalContactListId.eq(assigneeSubmittals.getPjSubmittalContactListId())
                                            , PjSubmittalContactListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())
                                    ).limit(1).list();
                            if (localPjSubmittal != null && localPjSubmittal.size() > 0) {
                                PjSubmittalContactList localRecord = localPjSubmittal.get(0);
                                localRecord = updatePjSubmittalContact(localRecord, assigneeSubmittals, isDetail, submittals.getPjProjectsId());
                                projectSubmittalsRepository.updatePjSubmittalContact(localRecord);
                                foldersList.add(localRecord);
                            } else if (assigneeSubmittals.getDeletedAt() == null) {
                                PjSubmittalContactList pjDocFile = createPjSubmittalContact(assigneeSubmittals, isDetail, submittals.getPjProjectsId());
                                projectSubmittalsRepository.savePjSubmittalContact(pjDocFile);
                                foldersList.add(pjDocFile);
                            }
                        }
                    return foldersList;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;

    }

    private PjSubmittalContactList updatePjSubmittalContact(PjSubmittalContactList localRecord, AssigneeSubmittals submittals, boolean isDetail, int projectId) {
        localRecord.createdAt = submittals.getCreatedAt() != null && !submittals.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getCreatedAt()) : null;
        localRecord.updatedAt = submittals.getUpdatedAt() != null && !submittals.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getUpdatedAt()) : null;
        localRecord.deletedAt = submittals.getDeletedAt() != null && !submittals.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getDeletedAt()) : null;
        localRecord.pjSubmittalsId = submittals.getPjSubmittalsId();
        localRecord.pjProjectsId = projectId;
        localRecord.contactName = submittals.getContactName();
        localRecord.pjSubmittalContactListId = submittals.getPjSubmittalContactListId();
        localRecord.usersId = loginResponse.getUserDetails().getUsers_id();
        localRecord.isCc = false;
        if (isDetail) {
            localRecord.comments = submittals.getComments();
            localRecord.response = submittals.getResponse();
            localRecord.sortOrder = submittals.getSortOrder();
            localRecord.companyName = submittals.getCompanyName();
            localRecord.pjSubmittalApproverResponsesId = submittals.getPj_submittal_approver_responses_id();
            localRecord.responseDate = submittals.getResponseDate() != null && !submittals.getResponseDate().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getResponseDate()) : null;
        }
        if (submittals.getAttachmentsSubmittals() != null && !submittals.getAttachmentsSubmittals().isEmpty()) {
            deleteSubmittalsAssigneeAttachment(submittals);
            doUpdateProjectSubmittalsAssigneeAttachment(submittals, projectId, submittals.getPjSubmittalsId(), submittals.getPjSubmittalContactListId());
        }
        return localRecord;
    }

    private PjSubmittalContactList createPjSubmittalContact(AssigneeSubmittals submittals, boolean isDetail, int projectId) {
        PjSubmittalContactList pjSubmittals = new PjSubmittalContactList();
        pjSubmittals.createdAt = submittals.getCreatedAt() != null && !submittals.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getCreatedAt()) : null;
        pjSubmittals.updatedAt = submittals.getUpdatedAt() != null && !submittals.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getUpdatedAt()) : null;
        pjSubmittals.deletedAt = submittals.getDeletedAt() != null && !submittals.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getDeletedAt()) : null;
        pjSubmittals.pjSubmittalsId = submittals.getPjSubmittalsId();
        pjSubmittals.pjProjectsId = projectId;
        pjSubmittals.contactName = submittals.getContactName();
        pjSubmittals.pjSubmittalContactListId = submittals.getPjSubmittalContactListId();
        pjSubmittals.usersId = loginResponse.getUserDetails().getUsers_id();
        pjSubmittals.isCc = false;
        if (isDetail) {
            pjSubmittals.response = submittals.getResponse();
            pjSubmittals.comments = submittals.getComments();
            pjSubmittals.sortOrder = submittals.getSortOrder();
            pjSubmittals.companyName = submittals.getCompanyName();
            pjSubmittals.pjSubmittalApproverResponsesId = submittals.getPj_submittal_approver_responses_id();
            pjSubmittals.responseDate = submittals.getResponseDate() != null && !submittals.getResponseDate().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getResponseDate()) : null;
        }
        if (submittals.getAttachmentsSubmittals() != null && !submittals.getAttachmentsSubmittals().isEmpty()) {
            deleteSubmittalsAssigneeAttachment(submittals);
            doUpdateProjectSubmittalsAssigneeAttachment(submittals, projectId, submittals.getPjSubmittalsId(), submittals.getPjSubmittalContactListId());
        }
        return pjSubmittals;
    }

    /**
     * To update submittals cc contacts in db.
     */
    private void doUpdateProjectSubmittalsCCContact(Submittals submittals) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        try {
            daoSession.callInTx(new Callable<List<PjSubmittalContactList>>() {
                final PjSubmittalContactListDao pjSubmittalContactListDao = daoSession.getPjSubmittalContactListDao();

                @Override
                public List<PjSubmittalContactList> call() {
                    List<PjSubmittalContactList> foldersList = new ArrayList<>();
                    if (submittals.getCcListSubmittals() != null && submittals.getCcListSubmittals().size() > 0)
                        for (CcListSubmittals assigneeSubmittals : submittals.getCcListSubmittals()) {
                            List<PjSubmittalContactList> localPjSubmittal = pjSubmittalContactListDao.queryBuilder()
                                    .where(PjSubmittalContactListDao.Properties.PjProjectsId.eq(submittals.getPjProjectsId()),
                                            PjSubmittalContactListDao.Properties.PjSubmittalsId.eq(assigneeSubmittals.getPjSubmittalsId()),
                                            PjSubmittalContactListDao.Properties.IsCc.eq(true),
                                            PjSubmittalContactListDao.Properties.DeletedAt.isNull(),
                                            PjSubmittalContactListDao.Properties.PjSubmittalContactListId.eq(assigneeSubmittals.getPjSubmittalContactListId())
                                            , PjSubmittalContactListDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())
                                    ).limit(1).list();
                            if (localPjSubmittal != null && localPjSubmittal.size() > 0) {
                                PjSubmittalContactList localRecord = localPjSubmittal.get(0);
                                localRecord = updatePjSubmittalCcContact(localRecord, assigneeSubmittals, submittals.getPjProjectsId());
                                projectSubmittalsRepository.updatePjSubmittalContact(localRecord);
                                foldersList.add(localRecord);
                            } else if (assigneeSubmittals.getDeletedAt() == null) {
                                PjSubmittalContactList pjDocFile = createPjSubmittalCcContact(assigneeSubmittals, submittals.getPjProjectsId());
                                projectSubmittalsRepository.savePjSubmittalContact(pjDocFile);
                                foldersList.add(pjDocFile);
                            }
                        }
                    return foldersList;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;

    }

    private PjSubmittalContactList updatePjSubmittalCcContact(PjSubmittalContactList localRecord, CcListSubmittals submittals, int projectId) {
        localRecord.pjSubmittalsId = submittals.getPjSubmittalsId();
        localRecord.pjProjectsId = projectId;
        localRecord.contactName = submittals.getContactName();
        localRecord.pjSubmittalContactListId = submittals.getPjSubmittalContactListId();
        localRecord.usersId = loginResponse.getUserDetails().getUsers_id();
        localRecord.isCc = true;
        localRecord.deletedAt = submittals.getDeletedAt() != null && !submittals.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getDeletedAt()) : null;

        return localRecord;
    }

    private PjSubmittalContactList createPjSubmittalCcContact(CcListSubmittals submittals, int projectId) {
        PjSubmittalContactList pjSubmittals = new PjSubmittalContactList();
        pjSubmittals.pjSubmittalsId = submittals.getPjSubmittalsId();
        pjSubmittals.pjProjectsId = projectId;
        pjSubmittals.contactName = submittals.getContactName();
        pjSubmittals.pjSubmittalContactListId = submittals.getPjSubmittalContactListId();
        pjSubmittals.usersId = loginResponse.getUserDetails().getUsers_id();
        pjSubmittals.isCc = true;
        pjSubmittals.deletedAt = submittals.getDeletedAt() != null && !submittals.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getDeletedAt()) : null;

        return pjSubmittals;
    }

    /**
     * To update submittals attchments contacts in db.
     */
    private void doUpdateProjectSubmittalsAttachment(Submittals submittals) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        try {
            daoSession.callInTx(new Callable<List<PjSubmittalAttachments>>() {
                final PjSubmittalAttachmentsDao pjSubmittalAttachmentsDao = daoSession.getPjSubmittalAttachmentsDao();

                @Override
                public List<PjSubmittalAttachments> call() {
                    List<PjSubmittalAttachments> foldersList = new ArrayList<>();
                    if (submittals.getAttachmentsSubmittals() != null && submittals.getAttachmentsSubmittals().size() > 0)
                        for (AttachmentsSubmittals attachmentsSubmittals : submittals.getAttachmentsSubmittals()) {
                            List<PjSubmittalAttachments> localPjSubmittal = pjSubmittalAttachmentsDao.queryBuilder()
                                    .where(PjSubmittalAttachmentsDao.Properties.PjProjectsId.eq(submittals.getPjProjectsId()),
                                            PjSubmittalAttachmentsDao.Properties.PjSubmittalsId.eq(submittals.getPjSubmittalsId()),
                                            PjSubmittalAttachmentsDao.Properties.DeletedAt.isNull(),
                                            PjSubmittalAttachmentsDao.Properties.AttachmentsId.eq(attachmentsSubmittals.getAttachments_id())
                                            , PjSubmittalAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())
                                    ).limit(1).list();
                            if (localPjSubmittal != null && localPjSubmittal.size() > 0) {
                                PjSubmittalAttachments localRecord = localPjSubmittal.get(0);
                                localRecord = updatePjSubmittalAttachment(localRecord, attachmentsSubmittals, submittals.getPjSubmittalsId(), submittals.getPjProjectsId());
                                projectSubmittalsRepository.updatePjSubmittalAttachments(localRecord);
                                foldersList.add(localRecord);
                            } else if (attachmentsSubmittals.getDeletedAt() == null) {
                                PjSubmittalAttachments pjDocFile = createPjSubmittalAttachment(attachmentsSubmittals, submittals.getPjSubmittalsId(), submittals.getPjProjectsId());
                                projectSubmittalsRepository.savePjSubmittalAttachments(pjDocFile);
                                foldersList.add(pjDocFile);
                            }
                        }
                    return foldersList;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;

    }

    private PjSubmittalAttachments updatePjSubmittalAttachment(PjSubmittalAttachments localRecord, AttachmentsSubmittals submittals, int submittalId, int projectId) {
        localRecord.createdAt = submittals.getCreatedAt() != null && !submittals.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getCreatedAt()) : null;
        localRecord.updatedAt = submittals.getUpdatedAt() != null && !submittals.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getUpdatedAt()) : null;
        localRecord.deletedAt = submittals.getDeletedAt() != null && !submittals.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getDeletedAt()) : null;
        localRecord.pjSubmittalsId = submittalId;
        localRecord.pjProjectsId = projectId;
        localRecord.attachmentsId = submittals.getAttachments_id();
        localRecord.attachPath = submittals.getAttach_path();
        localRecord.originalName = submittals.getOriginalName();
        localRecord.usersId = loginResponse.getUserDetails().getUsers_id();
        if (submittals.getAttach_path() != null) {
            String type = submittals.getAttach_path().substring(submittals.getAttach_path().lastIndexOf("."));
            localRecord.type = type.replace(".", "");
        }
        return localRecord;
    }

    private PjSubmittalAttachments createPjSubmittalAttachment(AttachmentsSubmittals submittals, int submittalId, int projectId) {
        PjSubmittalAttachments pjSubmittals = new PjSubmittalAttachments();
        pjSubmittals.createdAt = submittals.getCreatedAt() != null && !submittals.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getCreatedAt()) : null;
        pjSubmittals.updatedAt = submittals.getUpdatedAt() != null && !submittals.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getUpdatedAt()) : null;
        pjSubmittals.deletedAt = submittals.getDeletedAt() != null && !submittals.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getDeletedAt()) : null;
        pjSubmittals.pjSubmittalsId = submittalId;
        pjSubmittals.pjProjectsId = projectId;
        pjSubmittals.attachmentsId = submittals.getAttachments_id();
        pjSubmittals.attachPath = submittals.getAttach_path();
        pjSubmittals.originalName = submittals.getOriginalName();
        pjSubmittals.usersId = loginResponse.getUserDetails().getUsers_id();
        if (submittals.getAttach_path() != null) {
            String type = submittals.getAttach_path().substring(submittals.getAttach_path().lastIndexOf("."));
            pjSubmittals.type = type.replace(".", "");
        }
        return pjSubmittals;
    }

    /**
     * To update submittals assignee attchments contacts in db.
     */
    private void doUpdateProjectSubmittalsAssigneeAttachment(AssigneeSubmittals assigneeSubmittals, int projectId, int submittalId, int contactListId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        try {
            daoSession.callInTx(new Callable<List<PjAssigneeAttachments>>() {
                final PjAssigneeAttachmentsDao pjAssigneeAttachmentsDao = daoSession.getPjAssigneeAttachmentsDao();

                @Override
                public List<PjAssigneeAttachments> call() {
                    List<PjAssigneeAttachments> foldersList = new ArrayList<>();
                    if (assigneeSubmittals.getAttachmentsSubmittals() != null && assigneeSubmittals.getAttachmentsSubmittals().size() > 0)
                        for (AttachmentsSubmittals attachmentsSubmittals : assigneeSubmittals.getAttachmentsSubmittals()) {
                            List<PjAssigneeAttachments> localPjSubmittal = pjAssigneeAttachmentsDao.queryBuilder()
                                    .where(PjAssigneeAttachmentsDao.Properties.PjProjectsId.eq(projectId),
                                            PjAssigneeAttachmentsDao.Properties.PjSubmittalsId.eq(submittalId),
                                            PjAssigneeAttachmentsDao.Properties.DeletedAt.isNull(),
                                            PjAssigneeAttachmentsDao.Properties.AttachmentsId.eq(attachmentsSubmittals.getAttachments_id()),
                                            PjAssigneeAttachmentsDao.Properties.PjSubmittalContactListId.eq(contactListId)
                                            , PjAssigneeAttachmentsDao.Properties.UsersId.eq(loginResponse.getUserDetails().getUsers_id())
                                    ).limit(1).list();
                            if (localPjSubmittal != null && localPjSubmittal.size() > 0) {
                                PjAssigneeAttachments localRecord = localPjSubmittal.get(0);
                                localRecord = updatePjAssigneeAttachments(localRecord, attachmentsSubmittals, submittalId, projectId, contactListId);
                                projectSubmittalsRepository.updatePjSubmittalAssigneeAtt(localRecord);
                                foldersList.add(localRecord);
                            } else if (attachmentsSubmittals.getDeletedAt() == null) {
                                PjAssigneeAttachments pjDocFile = createPjAssigneeAttachments(attachmentsSubmittals, submittalId, projectId, contactListId);
                                projectSubmittalsRepository.savePjSubmittalContact(pjDocFile);
                                foldersList.add(pjDocFile);
                            }
                        }
                    return foldersList;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;

    }

    private PjAssigneeAttachments updatePjAssigneeAttachments(PjAssigneeAttachments localRecord, AttachmentsSubmittals submittals, int submittalId, int projectId, int contactListId) {
        localRecord.createdAt = submittals.getCreatedAt() != null && !submittals.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getCreatedAt()) : null;
        localRecord.updatedAt = submittals.getUpdatedAt() != null && !submittals.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getUpdatedAt()) : null;
        localRecord.deletedAt = submittals.getDeletedAt() != null && !submittals.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getDeletedAt()) : null;
        localRecord.pjSubmittalsId = submittalId;
        localRecord.pjProjectsId = projectId;
        localRecord.attachmentsId = submittals.getAttachments_id();
        localRecord.attachPath = submittals.getAttach_path();
        localRecord.originalName = submittals.getOriginalName();
        localRecord.pjSubmittalContactListId = contactListId;
        localRecord.usersId = loginResponse.getUserDetails().getUsers_id();
        if (submittals.getAttach_path() != null) {
            String type = submittals.getAttach_path().substring(submittals.getAttach_path().lastIndexOf("."));
            localRecord.type = type.replace(".", "");
        }
        return localRecord;
    }

    private PjAssigneeAttachments createPjAssigneeAttachments(AttachmentsSubmittals submittals, int submittalId, int projectId, int contactListId) {
        PjAssigneeAttachments pjSubmittals = new PjAssigneeAttachments();
        pjSubmittals.createdAt = submittals.getCreatedAt() != null && !submittals.getCreatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getCreatedAt()) : null;
        pjSubmittals.updatedAt = submittals.getUpdatedAt() != null && !submittals.getUpdatedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getUpdatedAt()) : null;
        pjSubmittals.deletedAt = submittals.getDeletedAt() != null && !submittals.getDeletedAt().equals("") ? DateFormatter.getDateFromDateTimeString(submittals.getDeletedAt()) : null;
        pjSubmittals.pjSubmittalsId = submittalId;
        pjSubmittals.pjProjectsId = projectId;
        pjSubmittals.attachmentsId = submittals.getAttachments_id();
        pjSubmittals.attachPath = submittals.getAttach_path();
        pjSubmittals.originalName = submittals.getOriginalName();
        pjSubmittals.pjSubmittalContactListId = contactListId;
        pjSubmittals.usersId = loginResponse.getUserDetails().getUsers_id();
        if (submittals.getAttach_path() != null) {
            String type = submittals.getAttach_path().substring(submittals.getAttach_path().lastIndexOf("."));
            pjSubmittals.type = type.replace(".", "");
        }
        return pjSubmittals;
    }

    /**
     * To delete submittal from db.
     */
    private synchronized void deleteProjectSubmittals(SubmittalsResponseListData submittalsResponseListData) {
        for (Submittals submittals : submittalsResponseListData.getSubmittals()) {
            if (submittals != null && submittals.getDeletedAt() != null) {
                try {
                    //delete all the submittal related data i.e assignee, cc, attachment, assignee attachment
                    projectSubmittalsRepository.deletePjSubmittals(submittals.getPjSubmittalsId());
                    projectSubmittalsRepository.deleteAllContactPjSubmittals(submittals.getPjSubmittalsId());
                    projectSubmittalsRepository.deleteAllCcPjSubmittals(submittals.getPjSubmittalsId());
                    projectSubmittalsRepository.deleteAllAttachmentPjSubmittals(submittals.getPjSubmittalsId());
                } catch (Exception e) {
                    Log.e(CLASS_NAME, "deleteProjectSubmittals::" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * To delete submittal cc from db.
     */
    private void deleteCcSubmittal(Submittals submittals) {
        for (CcListSubmittals assigneeSubmittals : submittals.getCcListSubmittals()) {
            if (assigneeSubmittals.getDeletedAt() != null) {
                projectSubmittalsRepository.deletePjCcContactSubmittal(assigneeSubmittals.getPjSubmittalContactListId());
            }
        }
    }

    /**
     * To delete submittal assignee from db.
     */
    private void deleteSubmittalContact(Submittals submittals) {
        for (AssigneeSubmittals assigneeSubmittals : submittals.getAssigneeSubmittals()) {
            if (assigneeSubmittals.getDeletedAt() != null) {
                projectSubmittalsRepository.deletePjContactSubmittal(assigneeSubmittals.getPjSubmittalContactListId());
            }
        }
    }

    /**
     * To delete submittal assignee attachment from db.
     */
    private void deleteSubmittalsAssigneeAttachment(AssigneeSubmittals assigneeSubmittals) {
        for (AttachmentsSubmittals attachmentsSubmittals : assigneeSubmittals.getAttachmentsSubmittals()) {
            if (attachmentsSubmittals.getDeletedAt() != null) {
                URI uri = null;
                try {
                    uri = new URI(attachmentsSubmittals.getAttach_path());
                    String[] segments = uri.getPath().split("/");
                    String imageName = segments[segments.length - 1];
                    String filePath = context.getFilesDir().getAbsolutePath() + Constants.SUBMITTALS_ATTACHMENTS_PATH;
                    File imgFile = new File(filePath + "/" + imageName);
                    if (imgFile.exists()) {
                        imgFile.delete();
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                projectSubmittalsRepository.deletePjSubmittalAssigneeAtt(attachmentsSubmittals.getAttachments_id());
            }
        }
    }

    /**
     * To delete submittal attachment from db.
     */
    private void deleteSubmittalAttachments(Submittals submittals) {
        for (AttachmentsSubmittals attachmentsSubmittals : submittals.getAttachmentsSubmittals()) {
            if (attachmentsSubmittals.getDeletedAt() != null) {
                URI uri = null;
                try {
                    uri = new URI(attachmentsSubmittals.getAttach_path());
                    String[] segments = uri.getPath().split("/");
                    String imageName = segments[segments.length - 1];
                    String filePath = context.getFilesDir().getAbsolutePath() + Constants.SUBMITTALS_ATTACHMENTS_PATH;
                    File imgFile = new File(filePath + "/" + imageName);
                    if (imgFile.exists()) {
                        imgFile.delete();
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                projectSubmittalsRepository.deletePjSubmittalAttachments(attachmentsSubmittals.getAttachments_id());
            }
        }
    }

    /**
     * To get submittal updated date from db.
     */
    private String getMAXProjectSubmittalUpdateDate(int projectId) {
        try {
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            List<PjSubmittals> maxPostIdRow = daoSession.getPjSubmittalsDao().queryBuilder()
                    .where(PjSubmittalsDao.Properties.UpdatedAt.isNotNull(),
                            PjSubmittalsDao.Properties.PjProjectsId.eq(projectId)
                    ).orderDesc(PjSubmittalsDao.Properties.UpdatedAt).limit(1).list();
            if (maxPostIdRow.size() > 0) {
                Date maxUpdatedAt = maxPostIdRow.get(0).getUpdatedAt();
                return DateFormatter.formatDateTimeForService(maxUpdatedAt);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "1990-01-01 01:01:01";
    }

    /**
     * To get submittal detail updated date from db.
     */
    private String getMAXProjectSubmittalDetailUpdateDate(int projectId) {
        try {
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            List<PjSubmittals> maxPostIdRow = daoSession.getPjSubmittalsDao().queryBuilder()
                    .where(PjSubmittalsDao.Properties.DetailUpdatedAt.isNotNull(),
                            PjSubmittalsDao.Properties.PjProjectsId.eq(projectId)
                    ).orderDesc(PjSubmittalsDao.Properties.DetailUpdatedAt).limit(1).list();
            if (maxPostIdRow.size() > 0) {
                Date maxUpdatedAt = maxPostIdRow.get(0).getDetailUpdatedAt();
                return DateFormatter.formatDateTimeForService(maxUpdatedAt);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "1990-01-01 01:01:01";
    }

  /*  public static class NaturalOrderComparator implements Comparator {

        int compareRight(String a, String b) {
            int bias = 0;
            int ia = 0;
            int ib = 0;

            // The longest run of digits wins. That aside, the greatest
            // value wins, but we can't know that it will until we've scanned
            // both numbers to know that they have the same magnitude, so we
            // remember it in BIAS.
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
                    if (bias == 0) bias = +1;
                } else if (ca == 0 && cb == 0) {
                    return bias;
                }
            }
        }

        public int compare(Object o1, Object o2) {
            if (o1 instanceof PjProjects){
                o1= ((PjProjects)o1).getProjectNumber();
            }
            if (o2 instanceof PjProjects){
                o2= ((PjProjects)o2).getProjectNumber();
            }
            String a = o1.toString();
            String b = o2.toString();

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

        static char charAt(String s, int i) {
            if (i >= s.length()) {
                return 0;
            } else {
                return s.charAt(i);
            }
        }
    }*/

}
