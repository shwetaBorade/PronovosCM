package com.pronovoscm.api;

import com.pronovoscm.model.request.drawinglist.DrawingListRequest;
import com.pronovoscm.model.response.drawinglist.DrawingListResponse;
import com.pronovoscm.model.response.logresponse.LogRequest;
import com.pronovoscm.model.response.logresponse.LogResponse;
import com.pronovoscm.model.response.syncupdate.SyncUpdateResponse;

import java.util.HashMap;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ProjectDrawingListApi {
    /**
     * @param drawingListRequest
     * @return
     */
    @POST("project/drawing/list")
    Call<DrawingListResponse> getProjectDrawingList(@HeaderMap HashMap<String, String> header, @Body DrawingListRequest drawingListRequest);

    /**
     * @param header
     * @param folderId
     * @return
     */

    @GET("drawing/syncupdate")
    Call<SyncUpdateResponse> getDrawingSyncUpdate(@HeaderMap HashMap<String, String> header, @Query("folder_id") int folderId);

    /**
     * @param header
     * @param finalRequestBody
     * @return
     */
    @POST("drawing/logs")
    Call<LogResponse> postDrawingLogs(@HeaderMap HashMap<String, String> header, @Body RequestBody finalRequestBody);

}
