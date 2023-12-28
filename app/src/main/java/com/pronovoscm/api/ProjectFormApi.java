package com.pronovoscm.api;

import com.pronovoscm.model.request.emaildefaultsettings.DefaultSettingsRequest;
import com.pronovoscm.model.request.formDelete.UserFormDeleteRequest;
import com.pronovoscm.model.request.formcomponent.ProjectFormComponentRequest;
import com.pronovoscm.model.request.formpermission.FormsPermissionRequest;
import com.pronovoscm.model.request.formuser.ProjectFormUserRequest;
import com.pronovoscm.model.request.projectoverview.ProjectOverviewRequest;
import com.pronovoscm.model.request.submitform.SubmitFormRequest;
import com.pronovoscm.model.response.cssjs.CSSJSResponse;
import com.pronovoscm.model.response.emaildefaultsettings.DefaultSettingsResponse;
import com.pronovoscm.model.response.formDelete.DeleteUserFormResponse;
import com.pronovoscm.model.response.formarea.FormAreaResponse;
import com.pronovoscm.model.response.formcategory.FormCategoryResponse;
import com.pronovoscm.model.response.formpermission.FormPermissionResponse;
import com.pronovoscm.model.response.forms.ProjectFormResponse;
import com.pronovoscm.model.response.formscheduleresponse.FormScheduleResponse;
import com.pronovoscm.model.response.formuser.FormUserResponse;
import com.pronovoscm.model.response.projectformcomponent.ProjectFormComponentResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface ProjectFormApi {
//    Method Post : http://dev.smartsubz.com/api/v2/project/forms (project_id)
//Method Post : http://dev.smartsubz.com/api/v2/project/user/forms (project_id,form_id)
//Method GET : http://dev.smartsubz.com/api/v2/form/categories
//Method Post : http://dev.smartsubz.com/api/v2/project/form/components (project_id)

    @POST("project/forms")
    Call<ProjectFormResponse> getProjectForms(@HeaderMap HashMap<String, String> headers, @Body ProjectOverviewRequest projectOverviewRequest);

    @POST("project/forms")
    Call<ProjectFormResponse> getProjectForms(@HeaderMap HashMap<String, String> headers, @Body ProjectFormComponentRequest projectOverviewRequest);

    @POST("project/areas")
    Call<FormAreaResponse> getProjectFormsArea(@HeaderMap HashMap<String, String> headers, @Body ProjectOverviewRequest projectAreaRequest);

    @POST("project/form/components")
    Call<ProjectFormComponentResponse> getProjectFormComponent(@HeaderMap HashMap<String, String> headers, @Body ProjectOverviewRequest projectOverviewRequest);

    @POST("project/form/components")
    Call<ProjectFormComponentResponse> getProjectFormComponent(@HeaderMap HashMap<String, String> headers, @Body ProjectFormComponentRequest projectFormComponentRequest);

    @POST("submit/form")
    Call<FormUserResponse> submitForm(@HeaderMap HashMap<String, String> headers, @Body SubmitFormRequest projectOverviewRequest);

    @GET("form/categories")
    Call<FormCategoryResponse> getCategory(@HeaderMap HashMap<String, String> headers);

    @GET("form/asset/update")
    Call<CSSJSResponse> getCSSJS(@HeaderMap HashMap<String, String> headers);

    @POST("project/user/forms")
    Call<FormUserResponse> getProjectFormsUser(@HeaderMap HashMap<String, String> headers, @Body ProjectFormUserRequest projectFormUserRequest);

    /**
     * @param header
     * @return
     */
    @POST("form/default/settings")
    Call<DefaultSettingsResponse> getDefaultSetting(@HeaderMap HashMap<String, String> header, @Body DefaultSettingsRequest defaultSettingsRequest);

    /**
     * @param header
     * @return
     */
    @POST("project/form/schedules")
    Call<FormScheduleResponse> getFormSchedule(@HeaderMap HashMap<String, String> header, @Body ProjectOverviewRequest defaultSettingsRequest);


    /**
     * To delete user form
     *
     * @param headers
     * @param userFormDeleteRequest
     * @return
     */
    @POST("project/user/form/delete")
    Call<DeleteUserFormResponse> deleteUserForm(@HeaderMap HashMap<String, String> headers, @Body UserFormDeleteRequest userFormDeleteRequest);

    /**
     * To  get form permisson
     *
     * @param headers
     * @return
     */
    @POST("form/permissions")
    Call<FormPermissionResponse> getProjectFormPermission(@HeaderMap HashMap<String, String> headers, @Body FormsPermissionRequest permissionRequest);

}
