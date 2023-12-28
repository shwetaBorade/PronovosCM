package com.pronovoscm.data;

import android.util.Log;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.api.FileUploadAPI;
import com.pronovoscm.api.ProjectFormApi;
import com.pronovoscm.model.SyncDataEnum;
import com.pronovoscm.model.request.emaildefaultsettings.DefaultSettingsRequest;
import com.pronovoscm.model.request.formDelete.UserFormDeleteRequest;
import com.pronovoscm.model.request.formcomponent.ProjectFormComponentRequest;
import com.pronovoscm.model.request.formpermission.FormsPermissionRequest;
import com.pronovoscm.model.request.formuser.ProjectFormUserRequest;
import com.pronovoscm.model.request.projectoverview.ProjectOverviewRequest;
import com.pronovoscm.model.request.submitform.SubmitFormRequest;
import com.pronovoscm.model.response.AbstractCallback;
import com.pronovoscm.model.response.ErrorResponse;
import com.pronovoscm.model.response.cssjs.CSSJSResponse;
import com.pronovoscm.model.response.emaildefaultsettings.DefaultSettingsResponse;
import com.pronovoscm.model.response.formDelete.DeleteUserFormResponse;
import com.pronovoscm.model.response.formarea.FormAreaResponse;
import com.pronovoscm.model.response.formcategory.FormCategoryResponse;
import com.pronovoscm.model.response.formpermission.FormPermissionResponse;
import com.pronovoscm.model.response.forms.ProjectFormResponse;
import com.pronovoscm.model.response.formscheduleresponse.FormScheduleResponse;
import com.pronovoscm.model.response.formuser.FormUserResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.projectformcomponent.ProjectFormComponentResponse;
import com.pronovoscm.model.response.uploadformfile.UploadFile;
import com.pronovoscm.persistence.domain.FormAssets;
import com.pronovoscm.persistence.domain.FormCategory;
import com.pronovoscm.persistence.domain.Forms;
import com.pronovoscm.persistence.domain.FormsComponent;
import com.pronovoscm.persistence.domain.FormsSchedule;
import com.pronovoscm.persistence.domain.ProjectFormArea;
import com.pronovoscm.persistence.domain.TransactionLogMobile;
import com.pronovoscm.persistence.domain.TransactionLogMobileDao;
import com.pronovoscm.persistence.domain.UserForms;
import com.pronovoscm.persistence.repository.ProjectFormRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.SharedPref;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProjectFormProvider {


    private final String TAG = ProjectFormProvider.class.getName();
    private final ProjectFormApi projectFormApi;
    NetworkStateProvider networkStateProvider;
    private PronovosApplication context;
    private LoginResponse loginResponse;
    private ProjectFormRepository projectFormRepository;


    public ProjectFormProvider(NetworkStateProvider networkStateProvider, ProjectFormApi workImpactApi, ProjectFormRepository projectOverviewRepository) {
        this.context = PronovosApplication.getContext();
        context.setUrl(Constants.BASE_API_URL);
        this.projectFormApi = workImpactApi;
        this.networkStateProvider = networkStateProvider;
        projectFormRepository = projectOverviewRepository;
    }

    public void getCategories(LoginResponse loginResponse, final ProviderResult<List<FormCategory>> callback) {


        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", projectFormRepository.getMAXCategoryUpdateDate());
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<FormCategoryResponse> assigneeAPI = projectFormApi.getCategory(headers);

            assigneeAPI.enqueue(new AbstractCallback<FormCategoryResponse>() {
                @Override
                protected void handleFailure(Call<FormCategoryResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<FormCategoryResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<FormCategoryResponse> response) {
                    if (response.body() != null) {
                        FormCategoryResponse formCategoryResponse = null;
                        try {
                            formCategoryResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (formCategoryResponse != null && formCategoryResponse.getStatus() == 200 && (formCategoryResponse.getData().getResponsecode() == 101 || formCategoryResponse.getData().getResponsecode() == 102)) {

                            if (formCategoryResponse != null && formCategoryResponse.getData() != null && formCategoryResponse.getData().getCategories() != null) {
                                List<FormCategory> projectForms = projectFormRepository.doUpdateCategories(formCategoryResponse.getData().getCategories());
                                callback.success(projectForms);
                            } else {
//                                callback.success(projectForms);


                            }
                        } else if (formCategoryResponse != null) {
                            callback.failure(formCategoryResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.failure(context.getString(R.string.internet_connection_check));
        }
    }

    public void getCSSJS(LoginResponse loginResponse, final ProviderResult<CSSJSResponse> callback) {


        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", projectFormRepository.getMAXCSSJSUpdateDate());
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<CSSJSResponse> assigneeAPI = projectFormApi.getCSSJS(headers);

            assigneeAPI.enqueue(new AbstractCallback<CSSJSResponse>() {
                @Override
                protected void handleFailure(Call<CSSJSResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<CSSJSResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<CSSJSResponse> response) {
                    if (response.body() != null) {
                        CSSJSResponse formCategoryResponse = null;
                        try {
                            formCategoryResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (formCategoryResponse != null && formCategoryResponse.getStatus() == 200 && (formCategoryResponse.getData().getResponsecode() == 101 || formCategoryResponse.getData().getResponsecode() == 102)) {

                            if (formCategoryResponse != null && formCategoryResponse.getData() != null && formCategoryResponse.getData().getFormAssets() != null) {
                                List<FormAssets> projectForms = projectFormRepository.doUpdateCSSJS(formCategoryResponse.getData().getFormAssets());
                                callback.success(formCategoryResponse);
                            } else {
                            }
                        } else if (formCategoryResponse != null) {
                            callback.failure(formCategoryResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.failure(context.getString(R.string.internet_connection_check));
        }
    }

    public void getFormUsers(ProjectFormUserRequest projectFormUserRequest, LoginResponse loginResponse, final ProviderResult<List<UserForms>> callback) {


        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", projectFormRepository.getMAXUserFormUpdateDate(projectFormUserRequest.getProjectId(), projectFormUserRequest.getFormId(), loginResponse.getUserDetails().getUsers_id()));
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<FormUserResponse> assigneeAPI = projectFormApi.getProjectFormsUser(headers, projectFormUserRequest);

            assigneeAPI.enqueue(new AbstractCallback<FormUserResponse>() {
                @Override
                protected void handleFailure(Call<FormUserResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<FormUserResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<FormUserResponse> response) {
                    if (response.body() != null) {

                        FormUserResponse formCategoryResponse = null;
                        try {
                            formCategoryResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (formCategoryResponse != null && formCategoryResponse.getStatus() == 200 && (formCategoryResponse.getData().getResponsecode() == 101 || formCategoryResponse.getData().getResponsecode() == 102)) {

                            if (formCategoryResponse != null && formCategoryResponse.getData() != null && formCategoryResponse.getData().getUserForms() != null) {
                                List<UserForms> userForms = projectFormRepository.doUpdateUserForms(formCategoryResponse.getData().getUserForms(),
                                        projectFormUserRequest.getFormId(), projectFormUserRequest.getProjectId(), true, loginResponse.getUserDetails().getUsers_id());
                                callback.success(userForms);
                            } else {
//                                List<UserForms> userForms=projectFormRepository.doUpdateCategories( formCategoryResponse.getData().getUserForms(),projectFormUserRequest.getFormId(),projectFormUserRequest.getProjectId());
                                callback.success(new ArrayList<>());
                            }
                        } else if (formCategoryResponse != null) {
                            callback.failure(formCategoryResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.failure(context.getString(R.string.internet_connection_check));
        }
    }

    public void getProjectFormUsers(ProjectFormUserRequest projectFormUserRequest, LoginResponse loginResponse, final ProviderResult<List<UserForms>> callback) {


        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", projectFormRepository.getMAXUserFormProjectUpdateDate(projectFormUserRequest.getProjectId(), loginResponse.getUserDetails().getUsers_id()));
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<FormUserResponse> assigneeAPI = projectFormApi.getProjectFormsUser(headers, projectFormUserRequest);

            assigneeAPI.enqueue(new AbstractCallback<FormUserResponse>() {
                @Override
                protected void handleFailure(Call<FormUserResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<FormUserResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<FormUserResponse> response) {
                    if (response.body() != null) {
                        FormUserResponse formCategoryResponse = null;
                        try {
                            formCategoryResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (formCategoryResponse != null && formCategoryResponse.getStatus() == 200 && (formCategoryResponse.getData().getResponsecode() == 101 || formCategoryResponse.getData().getResponsecode() == 102)) {

                            if (formCategoryResponse != null && formCategoryResponse.getData() != null && formCategoryResponse.getData().getUserForms() != null) {
                                List<UserForms> userForms = projectFormRepository.doUpdateUserForms(formCategoryResponse.getData().getUserForms(), -1, projectFormUserRequest.getProjectId(), true, loginResponse.getUserDetails().getUsers_id());
                                callback.success(userForms);
                            } else {
//                                List<UserForms> userForms=projectFormRepository.doUpdateCategories( formCategoryResponse.getData().getUserForms(),projectFormUserRequest.getFormId(),projectFormUserRequest.getProjectId());
                                callback.success(new ArrayList<>());
                            }
                        } else if (formCategoryResponse != null) {
                            callback.failure(formCategoryResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.failure(context.getString(R.string.internet_connection_check));
        }
    }

    public void getProjectFromUsingID(ProjectFormComponentRequest projectOverviewRequest, LoginResponse loginResponse, final ProviderResult<Forms> callback) {

        if (NetworkService.isNetworkAvailable(context)) {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", projectFormRepository.getMAXFormUpdateDate(projectOverviewRequest.getProjectId()));
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<ProjectFormResponse> assigneeAPI = projectFormApi.getProjectForms(headers, projectOverviewRequest);
            assigneeAPI.enqueue(new AbstractCallback<ProjectFormResponse>() {
                @Override
                protected void handleFailure(Call<ProjectFormResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<ProjectFormResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<ProjectFormResponse> response) {
                    if (response.body() != null) {
                        ProjectFormResponse projectFormResponse = null;
                        try {
                            projectFormResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (projectFormResponse != null && projectFormResponse.getStatus() == 200 && (projectFormResponse.getData().getResponsecode() == 101 || projectFormResponse.getData().getResponsecode() == 102)) {

                            if (projectFormResponse != null && projectFormResponse.getData() != null && projectFormResponse.getData().getProjectForms() != null) {
                                Forms projectForms = projectFormRepository.insertUpdateProjectForm(loginResponse.getUserDetails().getTenantId(),
                                        loginResponse.getUserDetails().getUsers_id(),
                                        projectFormResponse.getData().getProjectForms().get(0), projectOverviewRequest.getProjectId());
                                callback.success(projectForms);
                            } else if (projectFormResponse.getData().getResponsecode() == 102) {
                                callback.failure("response null");
                                //callback.success(null);
                            }
                        } else if (projectFormResponse != null) {
                            callback.failure(projectFormResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.failure(context.getString(R.string.internet_connection_check));
        }
    }

    public void getProjectAreas(ProjectOverviewRequest projectOverviewRequest, LoginResponse loginResponse, final ProviderResult<ProjectFormArea> callback) {
        Long projectID = Long.valueOf(projectOverviewRequest.getProjectId());
        if (NetworkService.isNetworkAvailable(context)) {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", projectFormRepository.getMAXFormAreaUpdateDate(projectOverviewRequest.getProjectId()));
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<FormAreaResponse> assigneeAPI = projectFormApi.getProjectFormsArea(headers, projectOverviewRequest);

            assigneeAPI.enqueue(new AbstractCallback<FormAreaResponse>() {
                @Override
                protected void handleFailure(Call<FormAreaResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<FormAreaResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<FormAreaResponse> response) {
                    if (response.body() != null) {
                        FormAreaResponse projectFormAreaResponse = null;
                        try {
                            projectFormAreaResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (projectFormAreaResponse != null && projectFormAreaResponse.getStatus() == 200 &&
                                (projectFormAreaResponse.getData().getResponseCode() == 101 || projectFormAreaResponse.getData().getResponseCode() == 102)) {

                            if (projectFormAreaResponse != null && projectFormAreaResponse.getData() != null && projectFormAreaResponse.getData().getProjectAreas() != null) {
                                ProjectFormArea projectFormAreas = projectFormRepository.doUpdateProjectArea(projectID, projectFormAreaResponse.getData().getProjectAreas());
                                callback.success(projectFormAreas);
                            } else if (projectFormAreaResponse.getData().getResponseCode() == 102) {
                                ProjectFormArea projectFormAreas = projectFormRepository.getProjectFormArea(projectID);
                                callback.success(projectFormAreas);
                            }
                        } else if (projectFormAreaResponse != null) {
                            callback.failure(projectFormAreaResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.failure(context.getString(R.string.internet_connection_check));
        }
    }

    public void getProjectForms(ProjectOverviewRequest projectOverviewRequest, LoginResponse loginResponse, final ProviderResult<List<Forms>> callback) {
        if (NetworkService.isNetworkAvailable(context)) {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", projectFormRepository.getMAXFormUpdateDate(projectOverviewRequest.getProjectId()));
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<ProjectFormResponse> assigneeAPI = projectFormApi.getProjectForms(headers, projectOverviewRequest);

            assigneeAPI.enqueue(new AbstractCallback<ProjectFormResponse>() {
                @Override
                protected void handleFailure(Call<ProjectFormResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<ProjectFormResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<ProjectFormResponse> response) {
                    if (response.body() != null) {
                        ProjectFormResponse projectFormResponse = null;
                        try {
                            projectFormResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (projectFormResponse != null && projectFormResponse.getStatus() == 200 && (projectFormResponse.getData().getResponsecode() == 101 || projectFormResponse.getData().getResponsecode() == 102)) {

                            if (projectFormResponse != null && projectFormResponse.getData() != null && projectFormResponse.getData().getProjectForms() != null) {
                                List<Forms> projectForms = projectFormRepository.doUpdateProjectForm(loginResponse.getUserDetails().getTenantId(), loginResponse.getUserDetails().getUsers_id(), projectFormResponse.getData().getProjectForms(), projectOverviewRequest.getProjectId());
                                callback.success(projectForms);
                            } else if (projectFormResponse.getData().getResponsecode() == 102) {
                                List<Forms> projectForms = new ArrayList<>();
                                callback.success(projectForms);
                            }
                        } else if (projectFormResponse != null) {
                            callback.failure(projectFormResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.failure(context.getString(R.string.internet_connection_check));
        }
    }

    public void getProjectFormsSchedules(ProjectOverviewRequest projectOverviewRequest, LoginResponse loginResponse, final ProviderResult<List<FormsSchedule>> callback) {


        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", projectFormRepository.getMAXFormScheduleUpdateDate(projectOverviewRequest.getProjectId()));
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<FormScheduleResponse> assigneeAPI = projectFormApi.getFormSchedule(headers, projectOverviewRequest);

            assigneeAPI.enqueue(new AbstractCallback<FormScheduleResponse>() {
                @Override
                protected void handleFailure(Call<FormScheduleResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<FormScheduleResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<FormScheduleResponse> response) {
                    if (response.body() != null) {
                        FormScheduleResponse projectInfoResponse = null;
                        try {
                            projectInfoResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (projectInfoResponse != null && projectInfoResponse.getStatus() == 200 && (projectInfoResponse.getData().getResponsecode() == 101 || projectInfoResponse.getData().getResponsecode() == 102)) {
                            Log.d(TAG, " schedule handleSuccess: " + projectInfoResponse);
                            if (projectInfoResponse != null && projectInfoResponse.getData() != null && projectInfoResponse.getData().getScheduledForms() != null) {
                                List<FormsSchedule> projectForms = projectFormRepository.doUpdateProjectFormSchedule(loginResponse.getUserDetails().getTenantId(), loginResponse.getUserDetails().getUsers_id(), projectInfoResponse.getData().getScheduledForms(), projectOverviewRequest.getProjectId());
                                callback.success(projectForms);
                            } else if (projectInfoResponse.getData().getResponsecode() == 102) {
//                                List<Forms> projectForms=projectFormRepository.doUpdateProjectForm(loginResponse.getUserDetails().getTenantId(), loginResponse.getUserDetails().getUsers_id(), projectInfoResponse.getData().getProjectForms(), projectOverviewRequest.getProjectId());
                                callback.success(new ArrayList<>());
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
            callback.failure(context.getString(R.string.internet_connection_check));
        }
    }


    public void getProjectFormComponents(ProjectFormComponentRequest projectOverviewRequest, LoginResponse loginResponse, final ProviderResult<List<FormsComponent>> callback) {


        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", projectFormRepository.getMAXFormcomponentUpdateDate(projectOverviewRequest.getProjectId()));
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<ProjectFormComponentResponse> assigneeAPI = projectFormApi.getProjectFormComponent(headers, projectOverviewRequest);

            assigneeAPI.enqueue(new AbstractCallback<ProjectFormComponentResponse>() {
                @Override
                protected void handleFailure(Call<ProjectFormComponentResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<ProjectFormComponentResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<ProjectFormComponentResponse> response) {
                    if (response.body() != null) {
                        ProjectFormComponentResponse projectInfoResponse = null;
                        try {
                            projectInfoResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (projectInfoResponse != null && projectInfoResponse.getStatus() == 200 && (projectInfoResponse.getData().getResponsecode() == 101 || projectInfoResponse.getData().getResponsecode() == 102)) {

                            if (projectInfoResponse != null && projectInfoResponse.getData() != null && projectInfoResponse.getData().getFormComponent() != null) {
                                List<FormsComponent> projectForms = projectFormRepository.doUpdateFormComponent(projectInfoResponse.getData().getFormComponent(), projectOverviewRequest.getProjectId());
                                callback.success(projectForms);
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
            callback.failure(context.getString(R.string.internet_connection_check));
        }
    }

    public void getProjectFormComponents(ProjectOverviewRequest projectOverviewRequest, LoginResponse loginResponse, final ProviderResult<List<FormsComponent>> callback) {


        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", projectFormRepository.getMAXFormcomponentUpdateDate(projectOverviewRequest.getProjectId()));
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<ProjectFormComponentResponse> assigneeAPI = projectFormApi.getProjectFormComponent(headers, projectOverviewRequest);

            assigneeAPI.enqueue(new AbstractCallback<ProjectFormComponentResponse>() {
                @Override
                protected void handleFailure(Call<ProjectFormComponentResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<ProjectFormComponentResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<ProjectFormComponentResponse> response) {
                    if (response.body() != null) {
                        ProjectFormComponentResponse projectInfoResponse = null;
                        try {
                            projectInfoResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (projectInfoResponse != null && projectInfoResponse.getStatus() == 200 && (projectInfoResponse.getData().getResponsecode() == 101 || projectInfoResponse.getData().getResponsecode() == 102)) {

                            if (projectInfoResponse != null && projectInfoResponse.getData() != null && projectInfoResponse.getData().getFormComponent() != null) {
                                List<FormsComponent> projectForms = projectFormRepository.doUpdateFormComponent(projectInfoResponse.getData().getFormComponent(), projectOverviewRequest.getProjectId());
                                callback.success(projectForms);
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
            callback.failure(context.getString(R.string.internet_connection_check));
        }
    }

    public void submitProjectFormComponents(int formId, int originalFormId, int activeRevisionNumber,
                                            int projectId, SubmitFormRequest submitFormRequest,
                                            LoginResponse loginResponse, final ProviderResult<List<UserForms>> callback) {

        Log.d(TAG, "OPEN_FORM submitProjectFormComponents: formId " + formId + "  originalFormId   " + originalFormId);
        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", projectFormRepository.getMAXUserFormUpdateDate(projectId, formId, loginResponse.getUserDetails().getUsers_id()));
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<FormUserResponse> assigneeAPI = projectFormApi.submitForm(headers, submitFormRequest);

            assigneeAPI.enqueue(new AbstractCallback<FormUserResponse>() {
                @Override
                protected void handleFailure(Call<FormUserResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<FormUserResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<FormUserResponse> response) {
                    if (response.body() != null) {
                        FormUserResponse formCategoryResponse = null;
                        try {
                            formCategoryResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (formCategoryResponse != null && formCategoryResponse.getStatus() == 200 && (formCategoryResponse.getData().getResponsecode() == 101 || formCategoryResponse.getData().getResponsecode() == 102)) {

                            if (formCategoryResponse != null && formCategoryResponse.getData() != null && formCategoryResponse.getData().getUserForms() != null) {
                                List<UserForms> userForms = projectFormRepository.doUpdateUserForms(formCategoryResponse.getData().getUserForms(),
                                        formId, projectId, false, loginResponse.getUserDetails().getUsers_id());
                                callback.success(userForms);
                            }
                        } else if (formCategoryResponse != null) {
                            callback.failure(formCategoryResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.failure(context.getString(R.string.internet_connection_check));
        }
    }

    public void getFormEmailDefaultSetting(DefaultSettingsRequest ccListRequest, final ProviderResult<DefaultSettingsResponse> callback) {


        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<DefaultSettingsResponse> assigneeAPI = projectFormApi.getDefaultSetting(headers, ccListRequest);

            assigneeAPI.enqueue(new AbstractCallback<DefaultSettingsResponse>() {
                @Override
                protected void handleFailure(Call<DefaultSettingsResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<DefaultSettingsResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<DefaultSettingsResponse> response) {
                    if (response.body() != null) {
                        DefaultSettingsResponse defaultSettingsResponse = null;
                        try {
                            defaultSettingsResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (defaultSettingsResponse != null && defaultSettingsResponse.getStatus() == 200 && (defaultSettingsResponse.getData().getResponsecode() == 101 || defaultSettingsResponse.getData().getResponsecode() == 102)) {
                            callback.success(defaultSettingsResponse);
                        } else if (defaultSettingsResponse != null) {
                            callback.failure(defaultSettingsResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {

        }

    }


    public void uploadFormImage(String fileName, Long id, TransactionLogMobile transactionLogMobile, TransactionLogMobileDao mPronovosSyncData, final ProviderResult<UploadFile> callback) {
        if (NetworkService.isNetworkAvailable(context)) {
            File myDir = new File(context.getFilesDir().getAbsolutePath() + "/Pronovos/Form/");

            File newCreatedFile = new File(myDir.getAbsolutePath() + File.separator + fileName);

            //   HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            // interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().readTimeout(200, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)/*.addInterceptor(interceptor)*/.build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_API_URL + "form/upload/file/").client(client)
                    .build();
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), newCreatedFile);

            builder.addFormDataPart("name", newCreatedFile.getName());
            builder.addFormDataPart("file", newCreatedFile.getName(), reqFile);
            RequestBody finalRequestBody = builder.build();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

            FileUploadAPI service = retrofit.create(FileUploadAPI.class);
            HashMap<String, String> headers = new HashMap<>();
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<ResponseBody> callCreateFile = service.uploadFormImage("", headers, finalRequestBody);

            callCreateFile.enqueue(new AbstractCallback<ResponseBody>() {
                @Override
                protected void handleFailure(Call<ResponseBody> call, Throwable throwable) {
                    if (transactionLogMobile != null && mPronovosSyncData != null) {
                        transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                        mPronovosSyncData.update(transactionLogMobile);
                    }
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<ResponseBody> call, ErrorResponse errorResponse) {
                    if (transactionLogMobile != null && mPronovosSyncData != null) {
                        transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                        mPronovosSyncData.update(transactionLogMobile);
                    }
                    callback.failure(errorResponse.getMessage());
                }

                @Override
                protected void handleSuccess(Response<ResponseBody> response) {
                    try {
                        Log.d(TAG, "handleSuccess: uploadFormImage  ");
                        String string = response.body().string();
                        UploadFile uploadFile = new Gson().fromJson(string + "", UploadFile.class);
                        if (uploadFile != null && uploadFile.getStatus() == 200 && uploadFile.getData() != null && uploadFile.getData().getUrl() != null) {
                            projectFormRepository.deleteFormImage(id);
                            ((PronovosApplication) context.getApplicationContext()).setupAndStartWorkManager();
                            callback.success(uploadFile);
                        } else {
                            if (transactionLogMobile != null && mPronovosSyncData != null) {
                                transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                                mPronovosSyncData.update(transactionLogMobile);
                            }
                            callback.failure(response.message());

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (transactionLogMobile != null && mPronovosSyncData != null) {
                            transactionLogMobile.setStatus(SyncDataEnum.NOTSYNC.ordinal());
                            mPronovosSyncData.update(transactionLogMobile);
                        }
                        callback.failure(e.getMessage());

                    }
                }
            });
        } else {
            callback.failure(context.getString(R.string.internet_connection_check));

        }
    }

    /**
     * Call the Delete Form API.
     *
     * @param userFormId The user form id.
     * @param callback   The Callback.
     */
    public void deleteForm(long userFormId, final ProviderResult<DeleteUserFormResponse> callback) {

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> apiHeader = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            apiHeader.put("lastupdate", "");
            apiHeader.put("timezone", TimeZone.getDefault().getID());
            apiHeader.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            UserFormDeleteRequest userFormDeleteRequest = new UserFormDeleteRequest();
            userFormDeleteRequest.setUserFormId((int) userFormId);

            Call<DeleteUserFormResponse> deleteUserFormResponseCall = projectFormApi.deleteUserForm(apiHeader, userFormDeleteRequest);

            deleteUserFormResponseCall.enqueue(new AbstractCallback<DeleteUserFormResponse>() {
                @Override
                protected void handleFailure(Call<DeleteUserFormResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<DeleteUserFormResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<DeleteUserFormResponse> response) {
                    if (response.body() != null) {
                        DeleteUserFormResponse deleteUserFormResponse = null;
                        try {
                            deleteUserFormResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (deleteUserFormResponse != null && deleteUserFormResponse.getStatus() == 200) {
                            callback.success(deleteUserFormResponse);
                        } else if (deleteUserFormResponse != null) {
                            callback.failure(deleteUserFormResponse.getMessage());
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

    public void getProjectFormPermission(int projectId, ProviderResult<FormPermissionResponse> callback) {
        String lastUpdatedDate = projectFormRepository.getMAXFormPermissionUpdateDate(projectId);
        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> apiHeader = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            apiHeader.put("lastupdate", lastUpdatedDate);
            apiHeader.put("timezone", TimeZone.getDefault().getID());
            apiHeader.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            FormsPermissionRequest permissionRequest = new FormsPermissionRequest();
            permissionRequest.setProject_id(projectId);
            Call<FormPermissionResponse> formPermissionResponseCall = projectFormApi.getProjectFormPermission(apiHeader, permissionRequest);

            formPermissionResponseCall.enqueue(new AbstractCallback<FormPermissionResponse>() {
                @Override
                protected void handleFailure(Call<FormPermissionResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<FormPermissionResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<FormPermissionResponse> response) {
                    if (response.body() != null) {
                        FormPermissionResponse formPermissionResponse = null;
                        try {
                            formPermissionResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (formPermissionResponse != null && formPermissionResponse.getStatus() == 200) {
                            projectFormRepository.saveFromPermissionResponse(formPermissionResponse.getData());

                            callback.success(formPermissionResponse);
                        } else if (formPermissionResponse != null) {
                            callback.failure(formPermissionResponse.getMessage());
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
}
