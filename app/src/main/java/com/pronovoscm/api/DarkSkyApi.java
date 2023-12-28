package com.pronovoscm.api;

import com.pronovoscm.model.response.darksky.DarkSkyResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DarkSkyApi {


    @GET("forecast/{key}/{lat},{lng},{time}?exclude=currently,flags,daily")
    Call<DarkSkyResponse> getForecast(@Path(value = "key", encoded = true) String key,
                                      @Path(value = "lat", encoded = true) double lat,
                                      @Path(value = "lng", encoded = true) double lng,
                                      @Path(value = "time", encoded = true) long time);

}
