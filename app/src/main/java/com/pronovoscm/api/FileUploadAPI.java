package com.pronovoscm.api;

import com.pronovoscm.model.response.uploadformfile.UploadFile;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Url;

public interface FileUploadAPI {
    @POST("")
    Call<ResponseBody> callCreateFile(@Url String url, @Body RequestBody file);
    @POST("")
    Call<ResponseBody> uploadFormImage(@Url String url, @HeaderMap HashMap<String, String> headers, @Body RequestBody file);

}
