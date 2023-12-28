package com.pronovoscm.api;

import com.pronovoscm.model.request.emailassignee.EmailAssigneeRequest;
import com.pronovoscm.model.request.cclist.CCListRequest;
import com.pronovoscm.model.request.emaildefaultsettings.DefaultSettingsRequest;
import com.pronovoscm.model.request.sendemail.SendEmailRequest;
import com.pronovoscm.model.response.cclist.CCListResponse;
import com.pronovoscm.model.response.emailassignee.EmailAssigneeResponse;
import com.pronovoscm.model.response.emaildefaultsettings.DefaultSettingsResponse;
import com.pronovoscm.model.response.sendemail.SendEmailResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface EmailReportApi {

    /**
     * @param header
     * @return
     */

    @POST("dailyreport/assigneelist")
    Call<EmailAssigneeResponse> getEmailAssigneeList(@HeaderMap HashMap<String, String> header, @Body EmailAssigneeRequest weatherReportRequest);

    /**
     * @param header
     * @return
     */

    @POST("dailyreport/defaultsettings")
    Call<DefaultSettingsResponse> getDefaultSetting(@HeaderMap HashMap<String, String> header, @Body DefaultSettingsRequest weatherReportRequest);

    /**
     * @param header
     * @return
     */

    @POST("dailyreport/cclist")
    Call<CCListResponse> getCclist(@HeaderMap HashMap<String, String> header, @Body CCListRequest weatherReportRequest); /**
     * @param header
     * @return
     */

    @POST("dailyreport/sendemail")
        Call<SendEmailResponse> sendEmail(@HeaderMap HashMap<String, String> header, @Body SendEmailRequest weatherReportRequest);

}
