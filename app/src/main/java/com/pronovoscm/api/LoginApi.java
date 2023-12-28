package com.pronovoscm.api;

import com.pronovoscm.model.request.forgetpassword.ForgetPasswordRequest;
import com.pronovoscm.model.request.login.LoginRequest;
import com.pronovoscm.model.response.forgetpassword.ForgetPasswordResponse;
import com.pronovoscm.model.response.login.LoginResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface LoginApi {

    /**
     * Api to register Family member
     *
     * @param request
     * @return
     */
    /*

     */
    @POST("login")
    Call<LoginResponse> userLogin(@HeaderMap HashMap<String, String> header, @Body LoginRequest request);

    @POST("forgot")
    Call<ForgetPasswordResponse> forgetPassword(@HeaderMap HashMap<String, String> header, @Body ForgetPasswordRequest request);

}
