package com.pronovoscm.api;

import com.pronovoscm.model.request.crewreport.CrewReportRequest;
import com.pronovoscm.model.request.weatherreport.WeatherReportRequest;
import com.pronovoscm.model.response.crewreport.CrewReportResponse;
import com.pronovoscm.model.response.weatherconditions.WeatherConditionsResponse;
import com.pronovoscm.model.response.weatherreport.WeatherReportResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface CrewReportApi {

    /**
     * @param header
     * @return
     */

    @POST("dailyreport/crewreport")
    Call<CrewReportResponse> getCrewReport(@HeaderMap HashMap<String, String> header, @Body CrewReportRequest weatherReportRequest);

}
