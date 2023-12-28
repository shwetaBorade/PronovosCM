package com.pronovoscm.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface PDFFileDownloadAPI {
    /**
     * @return
     */
    @GET("")
    Call<ResponseBody> getDrawingPDF(@Url String url);
}
