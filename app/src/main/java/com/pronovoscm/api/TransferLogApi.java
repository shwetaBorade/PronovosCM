package com.pronovoscm.api;

import com.pronovoscm.model.request.transferlog.TransferLogDetailRequest;
import com.pronovoscm.model.request.transferlog.TransferLogFilterRequest;
import com.pronovoscm.model.request.transferlog.TransferLogRequest;
import com.pronovoscm.model.response.transferlog.TransferLogResponse;
import com.pronovoscm.model.response.transferlogdetails.TransferLogDetailResponse;
import com.pronovoscm.model.response.transferloglocation.TransferLogLocationResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface TransferLogApi {


    @POST("transfer/logs")
    Call<TransferLogResponse> getTransferLog(@HeaderMap HashMap<String, String> header, @Body TransferLogRequest transferOverviewRequest);

    @POST("transfer/details")
    Call<TransferLogDetailResponse> getTransferLogDetails(@HeaderMap HashMap<String, String> header, @Body TransferLogDetailRequest transferOverviewRequest);

    @POST("transfer/log/locations")
    Call<TransferLogLocationResponse> getTransferLocations(@HeaderMap HashMap<String, String> headers, @Body TransferLogFilterRequest transferOverviewRequest);
}
