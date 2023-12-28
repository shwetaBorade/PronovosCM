package com.pronovoscm.api;

import com.pronovoscm.model.request.assignee.AssigneeRequest;
import com.pronovoscm.model.request.validateemail.ValidateEmailRequest;
import com.pronovoscm.model.response.assignee.AssigneeResponse;
import com.pronovoscm.model.response.companylist.CompanyListRequest;
import com.pronovoscm.model.response.companylist.CompanyListResponse;
import com.pronovoscm.model.response.trades.TradesResponse;
import com.pronovoscm.model.response.validateemail.ValidateEmailResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface FieldPaperWorkApi {

    /**
     * @param header
     * @return
     */

    @GET("dailyreport/trades")
    Call<TradesResponse> getTradesAPI(@HeaderMap HashMap<String, String> header);

    /**
     * @param headers
     * @param assigneeRequest
     * @return
     */
    @POST("assignee")
    Call<AssigneeResponse> getAssigneeAPI(@HeaderMap HashMap<String, String> headers, @Body AssigneeRequest assigneeRequest);

    /**
     * @param headers
     * @param companyListRequest
     * @return
     */
    @POST("dailyreport/companylist")
    Call<CompanyListResponse> getCompanyListAPI(@HeaderMap HashMap<String, String> headers, @Body CompanyListRequest companyListRequest);

    /**
     * @param headers
     * @param validateEmailRequest
     * @return
     */
    @POST("dailyreport/validateemail")
    Call<ValidateEmailResponse> getValidateEmail(@HeaderMap HashMap<String, String> headers, @Body ValidateEmailRequest validateEmailRequest);
}
