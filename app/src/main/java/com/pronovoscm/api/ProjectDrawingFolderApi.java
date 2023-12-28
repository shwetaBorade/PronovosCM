package com.pronovoscm.api;

import com.pronovoscm.model.request.drawingfolder.DrawingFolderRequest;
import com.pronovoscm.model.response.drawingfolder.DrawingFolderResponse;
import com.pronovoscm.model.response.logresponse.LogRequest;
import com.pronovoscm.model.response.logresponse.LogResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface ProjectDrawingFolderApi {
    /**
     * @param photoRequest
     * @return
     */
    @POST("project/drawings")
    Call<DrawingFolderResponse> getProjectDrawingFolder(@HeaderMap HashMap<String, String> header, @Body DrawingFolderRequest photoRequest);



}
