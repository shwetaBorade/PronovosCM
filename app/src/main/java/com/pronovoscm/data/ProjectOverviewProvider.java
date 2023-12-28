package com.pronovoscm.data;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.api.ProjectOverviewApi;
import com.pronovoscm.model.request.projectoverview.ProjectOverviewRequest;
import com.pronovoscm.model.response.AbstractCallback;
import com.pronovoscm.model.response.ErrorResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.projectinfo.ProjectOverviewInfoData;
import com.pronovoscm.model.response.projectinfo.ProjectOverviewInfoResponse;
import com.pronovoscm.model.response.projectsubcontractors.ProjectSubcontractorsResponse;
import com.pronovoscm.model.response.projectsubcontractors.SubcontractorData;
import com.pronovoscm.model.response.projectteam.ProjectTeamResponse;
import com.pronovoscm.model.response.projectteam.TeamData;
import com.pronovoscm.model.response.resources.ProjectResourcesResponse;
import com.pronovoscm.model.response.resources.ResourceData;
import com.pronovoscm.persistence.repository.ProjectOverviewRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.SharedPref;

import java.util.HashMap;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;

public class ProjectOverviewProvider {


    private final String TAG = ProjectOverviewProvider.class.getName();
    private final ProjectOverviewApi mWorkImpactApi;
    NetworkStateProvider networkStateProvider;
    private PronovosApplication context;
    private LoginResponse loginResponse;
    private ProjectOverviewRepository mProjectOverviewRepository;


    public ProjectOverviewProvider(NetworkStateProvider networkStateProvider, ProjectOverviewApi workImpactApi, ProjectOverviewRepository projectOverviewRepository) {
        this.context = PronovosApplication.getContext();
        context.setUrl(Constants.BASE_API_URL);
        this.mWorkImpactApi = workImpactApi;
        this.networkStateProvider = networkStateProvider;
        mProjectOverviewRepository = projectOverviewRepository;
    }

    public void getDynamicProjectInfo(ProjectOverviewRequest projectOverviewRequest, LoginResponse loginResponse,
                                      final ProviderResult<ProjectOverviewInfoData> callback) {

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<ProjectOverviewInfoResponse> assigneeAPI = mWorkImpactApi.getDynamicProjectInfo(headers, projectOverviewRequest);

            assigneeAPI.enqueue(new AbstractCallback<ProjectOverviewInfoResponse>() {
                @Override
                protected void handleFailure(Call<ProjectOverviewInfoResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<ProjectOverviewInfoResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<ProjectOverviewInfoResponse> response) {
                    if (response.body() != null) {
                        ProjectOverviewInfoResponse projectInfoResponse = null;
                        try {
                            projectInfoResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (projectInfoResponse != null && projectInfoResponse.getStatus() == 200 &&
                                (projectInfoResponse.getProjectOverviewInfoData().getResponseCode() == 101 ||
                                        projectInfoResponse.getProjectOverviewInfoData().getResponseCode() == 102)) {

                            if (projectInfoResponse != null && projectInfoResponse.getProjectOverviewInfoData() != null &&
                                    projectInfoResponse.getProjectOverviewInfoData().getSections() != null) {
                                Gson gson = new Gson();
                                String infoJson = gson.toJson(projectInfoResponse.getProjectOverviewInfoData());
                                ProjectOverviewInfoData info = mProjectOverviewRepository.doUpdateDynamicProjectOverviewInfo(loginResponse.getUserDetails().getUsers_id(),
                                        infoJson, projectOverviewRequest.getProjectId());
                                callback.success(info);
                            }
                        } else if (projectInfoResponse != null) {
                            callback.failure(projectInfoResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        }
    }
/*

    public void getProjectInfo(ProjectOverviewRequest projectOverviewRequest, LoginResponse loginResponse, final ProviderResult<Info> callback) {


        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<ProjectInfoResponse> assigneeAPI = mWorkImpactApi.getProjectInfo(headers, projectOverviewRequest);

            assigneeAPI.enqueue(new AbstractCallback<ProjectInfoResponse>() {
                @Override
                protected void handleFailure(Call<ProjectInfoResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<ProjectInfoResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<ProjectInfoResponse> response) {
                    if (response.body() != null) {
                        ProjectInfoResponse projectInfoResponse = null;
                        try {
                            projectInfoResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (projectInfoResponse != null && projectInfoResponse.getStatus() == 200 && (projectInfoResponse.getData().getResponsecode() == 101 || projectInfoResponse.getData().getResponsecode() == 102)) {

                            if (projectInfoResponse != null && projectInfoResponse.getData() != null && projectInfoResponse.getData().getInfo() != null) {
                                Gson gson = new Gson();
                                String infoJson = gson.toJson(projectInfoResponse.getData().getInfo());
                                //Info info = mProjectOverviewRepository.doUpdateProjectOverviewInfo(loginResponse.getUserDetails().getUsers_id(), infoJson, projectOverviewRequest.getProjectId());
                              //  callback.success(info);
                                //TODO SIDDESH
                            }
                        } else if (projectInfoResponse != null) {
                            callback.failure(projectInfoResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
//            callback.failure(context.getString(R.string.internet_connection_check));
        }
    }
*/

    public void getProjectResources(ProjectOverviewRequest projectOverviewRequest, LoginResponse loginResponse, final ProviderResult<ResourceData> callback) {


        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            this.loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + this.loginResponse.getUserDetails().getAuthtoken());

            Call<ProjectResourcesResponse> assigneeAPI = mWorkImpactApi.getProjectResources(headers, projectOverviewRequest);

            assigneeAPI.enqueue(new AbstractCallback<ProjectResourcesResponse>() {
                @Override
                protected void handleFailure(Call<ProjectResourcesResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<ProjectResourcesResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<ProjectResourcesResponse> response) {
                    if (response.body() != null) {
                        ProjectResourcesResponse projectResourcesResponse = null;
                        try {
                            projectResourcesResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (projectResourcesResponse != null && projectResourcesResponse.getStatus() == 200 && (projectResourcesResponse.getData().getResponsecode() == 101 || projectResourcesResponse.getData().getResponsecode() == 102)) {
                            Gson gson = new Gson();
                            String resourceJson = gson.toJson(projectResourcesResponse.getData());
                            ResourceData resourceData = mProjectOverviewRepository.doUpdateProjectOverviewResources(loginResponse.getUserDetails().getUsers_id(), resourceJson, projectOverviewRequest.getProjectId());
                            callback.success(resourceData);
                        } else if (projectResourcesResponse != null) {
                            callback.failure(projectResourcesResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
//            callback.failure(context.getString(R.string.internet_connection_check));

        }
    }

    public void getProjectTeamModifiedApi(ProjectOverviewRequest projectOverviewRequest, LoginResponse loginResponse, final ProviderResult<TeamData> callback) {

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            this.loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + this.loginResponse.getUserDetails().getAuthtoken());

            Call<ProjectTeamResponse> assigneeAPI = mWorkImpactApi.getProjectTeamModified(headers, projectOverviewRequest);

            assigneeAPI.enqueue(new AbstractCallback<ProjectTeamResponse>() {
                @Override
                protected void handleFailure(Call<ProjectTeamResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<ProjectTeamResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<ProjectTeamResponse> response) {
                    if (response.body() != null) {
                        ProjectTeamResponse projectTeamResponse = null;
                        try {
                            projectTeamResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (projectTeamResponse != null && projectTeamResponse.getStatus() == 200 && (projectTeamResponse.getData().getResponseCode() == 101 ||
                                projectTeamResponse.getData().getResponseCode() == 102)) {
                            Gson gson = new Gson();
                            String resourceJson = gson.toJson(projectTeamResponse.getData());
                            TeamData info = mProjectOverviewRepository.doUpdateProjectOverviewTeam(loginResponse.getUserDetails().getUsers_id(), resourceJson, projectOverviewRequest.getProjectId());
                            callback.success(info);

                        } else if (projectTeamResponse != null) {
                            callback.failure(projectTeamResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
//            callback.failure(context.getString(R.string.internet_connection_check));
        }
    }

    public void getProjectTeam(ProjectOverviewRequest projectOverviewRequest, LoginResponse loginResponse, final ProviderResult<TeamData> callback) {


        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            this.loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + this.loginResponse.getUserDetails().getAuthtoken());

            Call<ProjectTeamResponse> assigneeAPI = mWorkImpactApi.getProjectTeam(headers, projectOverviewRequest);

            assigneeAPI.enqueue(new AbstractCallback<ProjectTeamResponse>() {
                @Override
                protected void handleFailure(Call<ProjectTeamResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<ProjectTeamResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<ProjectTeamResponse> response) {
                    if (response.body() != null) {
                        ProjectTeamResponse projectTeamResponse = null;
                        try {
                            projectTeamResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (projectTeamResponse != null && projectTeamResponse.getStatus() == 200 && (projectTeamResponse.getData().getResponseCode() == 101 ||
                                projectTeamResponse.getData().getResponseCode() == 102)) {
                            Gson gson = new Gson();
                            String resourceJson = gson.toJson(projectTeamResponse.getData());
                            TeamData info = mProjectOverviewRepository.doUpdateProjectOverviewTeam(loginResponse.getUserDetails().getUsers_id(), resourceJson, projectOverviewRequest.getProjectId());
                            callback.success(info);

                        } else if (projectTeamResponse != null) {
                            callback.failure(projectTeamResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
//            callback.failure(context.getString(R.string.internet_connection_check));
        }
    }

    public void getProjectSubcontractors(ProjectOverviewRequest projectOverviewRequest, LoginResponse loginResponse, final ProviderResult<SubcontractorData> callback) {


        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            this.loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + this.loginResponse.getUserDetails().getAuthtoken());

            Call<ProjectSubcontractorsResponse> assigneeAPI = mWorkImpactApi.getProjectSubcontractors(headers, projectOverviewRequest);

            assigneeAPI.enqueue(new AbstractCallback<ProjectSubcontractorsResponse>() {
                @Override
                protected void handleFailure(Call<ProjectSubcontractorsResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<ProjectSubcontractorsResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<ProjectSubcontractorsResponse> response) {
                    if (response.body() != null) {
                        ProjectSubcontractorsResponse projectSubcontractorsResponse = null;
                        try {
                            projectSubcontractorsResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (projectSubcontractorsResponse != null && projectSubcontractorsResponse.getStatus() == 200 && (projectSubcontractorsResponse.getData().getResponsecode() == 101 || projectSubcontractorsResponse.getData().getResponsecode() == 102)) {
                            Gson gson = new Gson();
                            String resourceJson = gson.toJson(projectSubcontractorsResponse.getData());
                            SubcontractorData info = mProjectOverviewRepository.doUpdateProjectOverviewSubcontractors(loginResponse.getUserDetails().getUsers_id(), resourceJson, projectOverviewRequest.getProjectId());
                            callback.success(info);
                        } else if (projectSubcontractorsResponse != null) {
                            callback.failure(projectSubcontractorsResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
//            callback.failure(context.getString(R.string.internet_connection_check));
        }

    }


}
