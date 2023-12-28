package com.pronovoscm.api;

import com.pronovoscm.model.request.drawinglist.DrawingListRequest;
import com.pronovoscm.model.request.drawingpunchlist.DrawingPunchlist;
import com.pronovoscm.model.request.drawingstore.DrawingStoreRequest;
import com.pronovoscm.model.response.drawingannotation.DrawingAnnotationResponse;
import com.pronovoscm.model.response.drawinglist.DrawingListResponse;
import com.pronovoscm.model.response.drawingpunchlist.DrawingPunchlistResponse;
import com.pronovoscm.model.response.drawingstore.DrawingStoreAnnotationResponse;
import com.pronovoscm.model.response.syncupdate.SyncUpdateResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DrawingAnnotationApi {
    /**
     *
     * @param header
     * @param drawingId
     * @return
     */

    @GET("drawing/annotation")
    Call<DrawingAnnotationResponse> getDrawingAnnotations(@HeaderMap HashMap<String, String> header, @Query("drawing_id") int drawingId);

    /**
     *
     * @param headers
     * @param drawingStoreRequest
     * @return
     */
    @POST("drawing/store/annotation")
    Call<DrawingStoreAnnotationResponse> setDrawingStroreAnnotations(@HeaderMap HashMap<String, String> headers, @Body DrawingStoreRequest drawingStoreRequest);
  /**
     *
     * @param headers
     * @param drawingStoreRequest
     * @return
     */
    @POST("project/drawing/punchlists")
    Call<DrawingPunchlistResponse> getDrawingPunchlists(@HeaderMap HashMap<String, String> headers, @Body DrawingPunchlist drawingStoreRequest);
}
