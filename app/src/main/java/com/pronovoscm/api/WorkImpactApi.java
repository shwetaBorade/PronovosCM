package com.pronovoscm.api;

import com.pronovoscm.model.request.signurl.SignedUrlRequest;
import com.pronovoscm.model.request.workdetails.WorkDetailsRequest;
import com.pronovoscm.model.request.workimpact.WorkImpactRequest;
import com.pronovoscm.model.response.signedurl.SignedUrlResponse;
import com.pronovoscm.model.response.workdetails.WorkDetailsResponse;
import com.pronovoscm.model.response.workimpact.WorkImpactResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface WorkImpactApi {

    /**
     * @param header
     * @return
     */

    @POST("dailyreport/workimpactsreport")
    Call<WorkImpactResponse> getWorkImpacts(@HeaderMap HashMap<String, String> header, @Body WorkImpactRequest workImpactRequest);
    /**
     * @param header
     * @return
     */
    @POST("project/photo/signedurl")
    Call<SignedUrlResponse> getSignedUrl(@HeaderMap HashMap<String, String> header, @Body SignedUrlRequest signedUrlRequest);
}
