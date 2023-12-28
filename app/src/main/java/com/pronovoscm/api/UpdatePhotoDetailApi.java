package com.pronovoscm.api;

import com.pronovoscm.model.request.updatephoto.UpdatePhotoDetail;
import com.pronovoscm.model.request.updatephoto.UpdatePhotoDetail2;
import com.pronovoscm.model.response.updatephoto.UpdatePhotoDetailResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface UpdatePhotoDetailApi {
    /**
     * @param photoRequest
     * @return
     */
    @POST("project/updatephotodetail")
    Call<UpdatePhotoDetailResponse> updatePhotoDetails(@HeaderMap HashMap<String, String> header, @Body UpdatePhotoDetail photoRequest);

    /**
     * @param photoRequest
     * @return
     */
    @POST("project/updatephotodetail")
    Call<UpdatePhotoDetailResponse> updatePhotoDetails2(@HeaderMap HashMap<String, String> header, @Body UpdatePhotoDetail2 photoRequest);


}
