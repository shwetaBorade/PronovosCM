package com.pronovoscm.api;

import com.pronovoscm.model.request.signurl.SignedUrlRequest;
import com.pronovoscm.model.request.workdetails.WorkDetailsRequest;
import com.pronovoscm.model.response.signedurl.SignedUrlResponse;
import com.pronovoscm.model.response.workdetails.WorkDetailsResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface WorkDetailsApi {

    /**
     * @param header
     * @return
     */

    @POST("dailyreport/workdetailsreport")
    Call<WorkDetailsResponse> getWorkDetails(@HeaderMap HashMap<String, String> header, @Body WorkDetailsRequest workDetailsRequest);

    /**
     * @param header
     * @return
     */
    @POST("project/photo/signedurl")
    Call<SignedUrlResponse> getSignedUrl(@HeaderMap HashMap<String, String> header, @Body SignedUrlRequest signedUrlRequest);
}
