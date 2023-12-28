package com.pronovoscm.api;

import com.pronovoscm.model.request.projectoverview.ProjectOverviewRequest;
import com.pronovoscm.model.response.projectinfo.ProjectInfoResponse;
import com.pronovoscm.model.response.projectinfo.ProjectOverviewInfoResponse;
import com.pronovoscm.model.response.projectsubcontractors.ProjectSubcontractorsResponse;
import com.pronovoscm.model.response.projectteam.ProjectTeamResponse;
import com.pronovoscm.model.response.resources.ProjectResourcesResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface ProjectOverviewApi {
    /**
     * @param header
     * @return
     */

    @POST("project/info")
    Call<ProjectInfoResponse> getProjectInfo(@HeaderMap HashMap<String, String> header, @Body ProjectOverviewRequest projectOverviewRequest);

    @POST("project/info")
    Call<ProjectOverviewInfoResponse> getDynamicProjectInfo(@HeaderMap HashMap<String, String> header, @Body ProjectOverviewRequest projectOverviewRequest);

    /**
     * @param header
     * @return
     */

    @POST("project/resources")
    Call<ProjectResourcesResponse> getProjectResources(@HeaderMap HashMap<String, String> header, @Body ProjectOverviewRequest projectOverviewRequest);

    /**
     * @param header
     * @return
     */

    @POST("project/team")
    Call<ProjectTeamResponse> getProjectTeam(@HeaderMap HashMap<String, String> header, @Body ProjectOverviewRequest projectOverviewRequest);

    /**
     * @param header
     * @return
     */

    @POST("project/team/modified")
    Call<ProjectTeamResponse> getProjectTeamModified(@HeaderMap HashMap<String, String> header, @Body ProjectOverviewRequest projectOverviewRequest);

    /**
     * @param header
     * @return
     */

    @POST("project/subcontractors")
    Call<ProjectSubcontractorsResponse> getProjectSubcontractors(@HeaderMap HashMap<String, String> header, @Body ProjectOverviewRequest projectOverviewRequest);


}
