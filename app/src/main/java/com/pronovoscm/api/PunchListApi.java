package com.pronovoscm.api;

import com.pronovoscm.model.request.punchlisemail.PunchListEmailRequest;
import com.pronovoscm.model.request.punchlist.PunchListHistoryRequest;
import com.pronovoscm.model.request.punchlist.PunchListRequest;
import com.pronovoscm.model.request.signurl.SignedUrlRequest;
import com.pronovoscm.model.response.punchlist.PunchListHistoryResponse;
import com.pronovoscm.model.response.punchlist.PunchListResponse;
import com.pronovoscm.model.response.punchlistemail.PunchListEmailResponse;
import com.pronovoscm.model.response.signedurl.SignedUrlResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface PunchListApi {

    @POST("punchlist")
    Call<PunchListResponse> getPunchList(@HeaderMap HashMap<String, String> header, @Body PunchListRequest request);

    /**
     * @param header
     * @return
     */
    @POST("project/photo/signedurl")
    Call<SignedUrlResponse> getSignedUrl(@HeaderMap HashMap<String, String> header, @Body SignedUrlRequest signedUrlRequest);

    /**
     *
     * @param headers
     * @param punchListEmailRequest
     * @return
     */
    @POST("punchlist/sendemail")
    Call<PunchListEmailResponse> sendPunchListEmail(@HeaderMap HashMap<String, String> headers,@Body PunchListEmailRequest punchListEmailRequest);

    @POST("punchlist/history")
    Call<PunchListHistoryResponse> getPunchListHistories(@HeaderMap HashMap<String, String> headers,
                                                         @Body PunchListHistoryRequest punchListHistoryRequest);
}
