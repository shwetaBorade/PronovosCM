package com.pronovoscm.api;

import com.pronovoscm.model.request.albums.AlbumRequest;
import com.pronovoscm.model.request.documents.DocumentsFolderFileRequest;
import com.pronovoscm.model.request.issueTracking.IssuesRequest;
import com.pronovoscm.model.request.issueTracking.NewIssueRequest;
import com.pronovoscm.model.request.photo.PhotoRequest;
import com.pronovoscm.model.request.projects.ProjectVersionsCheck;
import com.pronovoscm.model.request.rfi.RfiListRequest;
import com.pronovoscm.model.request.rfi.RfiRepliesRequest;
import com.pronovoscm.model.request.signurl.SignedUrlRequest;
import com.pronovoscm.model.request.submittals.SubmittalsRequest;
import com.pronovoscm.model.response.album.AlbumResponse;
import com.pronovoscm.model.response.documents.ProjectDocumentFilesResponse;
import com.pronovoscm.model.response.documents.ProjectDocumentFoldersResponse;
import com.pronovoscm.model.response.issueTracking.impactAndRootCause.ImpactAndRootCauseResponse;
import com.pronovoscm.model.response.issueTracking.issues.CustomItemTypesResponse;
import com.pronovoscm.model.response.issueTracking.issues.CustomItemsResponse;
import com.pronovoscm.model.response.issueTracking.issues.IssueSectionResponse;
import com.pronovoscm.model.response.issueTracking.issues.IssuesResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.photo.PhotoResponse;
import com.pronovoscm.model.response.projectdata.ProjectDataResponse;
import com.pronovoscm.model.response.projects.ProjectResponse;
import com.pronovoscm.model.response.regions.RegionsResponse;
import com.pronovoscm.model.response.rfi.RfiResponse;
import com.pronovoscm.model.response.rfi.attachment.RfiAttachmentResponse;
import com.pronovoscm.model.response.rfi.contact.RfiContactListResponse;
import com.pronovoscm.model.response.rfi.replies.RfiRepliesResponse;
import com.pronovoscm.model.response.signedurl.SignedUrlResponse;
import com.pronovoscm.model.response.submittals.SubmittalsResponse;
import com.pronovoscm.model.response.tag.TagsResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ProjectsApi {

    /**
     * @param header
     * @param regionId
     * @return
     */

    @POST("user/projects")
    Call<ProjectResponse> getUsersProjects(@HeaderMap HashMap<String, String> header, @Query("region_id") String regionId, @Body
            ProjectVersionsCheck projectVersionsCheck);

    /**
     * @param header
     * @return
     */

    @GET("get/project/data")
    Call<ProjectDataResponse> getProjectsData(@HeaderMap HashMap<String, String> header);

    /**
     * @param header
     * @return
     */
    @GET("user/regions")
    Call<RegionsResponse> getUsersRegions(@HeaderMap HashMap<String, String> header);

    /**
     * @param header
     * @return
     */
    @POST("project/photo/tags")
    Call<TagsResponse> getPhotoTags(@HeaderMap HashMap<String, String> header);

    /**
     * @param header
     * @return
     */
    @POST("project/albums")
    Call<AlbumResponse> getProjectAlbums(@HeaderMap HashMap<String, String> header, @Body AlbumRequest albumRequest);

    /**
     * @param header
     * @return
     */
    @POST("project/album/photos")
    Call<PhotoResponse> getAlbumsPhoto(@HeaderMap HashMap<String, String> header, @Body PhotoRequest photoRequest);

//    http://dev.smartsubz.com/api/v1/project/photo/signedurl

    /**
     * @param header
     * @return
     */
    @POST("project/photo/signedurl")
    Call<SignedUrlResponse> getSignedUrl(@HeaderMap HashMap<String, String> header, @Body SignedUrlRequest signedUrlRequest);

    @POST("user/permissions")
    Call<LoginResponse> userPermission(@HeaderMap HashMap<String, String> headers);

    @POST("project/get/document/folders")
    Call<ProjectDocumentFoldersResponse> getProjectDocumentFolders(@HeaderMap HashMap<String, String> headers, @Body DocumentsFolderFileRequest documentsFolderFileRequest);

    @POST("project/get/document/files")
    Call<ProjectDocumentFilesResponse> getProjectDocumentFiles(@HeaderMap HashMap<String, String> headers, @Body DocumentsFolderFileRequest documentsFolderFileRequest);

    @POST("project/get/rfi")
    Call<RfiResponse> getProjectRfi(@HeaderMap HashMap<String, String> headers, @Body RfiListRequest rfiListRequest);

    @POST("project/get/rfi/replies")
    Call<RfiRepliesResponse> getProjectRfiReplies(@HeaderMap HashMap<String, String> headers, @Body RfiRepliesRequest rfiRequest);

    @POST("project/get/rfi/attachments")
    Call<RfiAttachmentResponse> getProjectRfiAttachments(@HeaderMap HashMap<String, String> headers, @Body RfiRepliesRequest rfiRequest);

    @POST("project/get/rfi/contacts/list")
    Call<RfiContactListResponse> getProjectRfiContactList(@HeaderMap HashMap<String, String> headers, @Body RfiListRequest rfiRequest);

    @POST("project/get/submittals")
    Call<SubmittalsResponse> getProjectSubmittals(@HeaderMap HashMap<String, String> headers, @Body SubmittalsRequest submittalsListRequest);

    @POST("project/get/submittal/detail")
    Call<SubmittalsResponse> getSubmittalDetail(@HeaderMap HashMap<String, String> headers, @Body SubmittalsRequest submittalsDetailRequest);

    @POST("project/issue-tracking/get-impacts-and-root-causes")
    Call<ImpactAndRootCauseResponse> getImpactAndRootCauseResponse(@HeaderMap HashMap<String, String> header);

    @POST("project/issue-tracking")
    Call<IssuesResponse> getIssues(@HeaderMap HashMap<String, String> headers, @Body IssuesRequest issuesRequest);

    @POST("project/issue-tracking")
    Call<IssuesResponse> postIssues(@HeaderMap HashMap<String, String> headers, @Body NewIssueRequest issuesRequest);

    @GET("project/issue-tracking/custom/sections")
    Call<IssueSectionResponse> getIssueSection(@HeaderMap HashMap<String, String> headers);

    @GET("project/issue-tracking/custom/items")
    Call<CustomItemsResponse> getCustomItems(@HeaderMap HashMap<String, String> headers);
    @GET("project/issue-tracking/item/types")
    Call<CustomItemTypesResponse> getCustomItemsTypes(@HeaderMap HashMap<String, String> headers);
}
