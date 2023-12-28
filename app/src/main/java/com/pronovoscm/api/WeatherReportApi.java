package com.pronovoscm.api;

import com.pronovoscm.model.request.assignee.AssigneeRequest;
import com.pronovoscm.model.request.forecast.ForecastRequest;
import com.pronovoscm.model.request.weatherreport.WeatherReportRequest;
import com.pronovoscm.model.response.assignee.AssigneeResponse;
import com.pronovoscm.model.response.companylist.CompanyListRequest;
import com.pronovoscm.model.response.companylist.CompanyListResponse;
import com.pronovoscm.model.response.forecastresponse.ForecastResponse;
import com.pronovoscm.model.response.trades.TradesResponse;
import com.pronovoscm.model.response.weatherconditions.WeatherConditionsResponse;
import com.pronovoscm.model.response.weatherreport.WeatherReportResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface WeatherReportApi {

    /**
     * @param header
     * @return
     */
    @POST("dailyreport/addweatherreport")
    Call<WeatherReportResponse> getWeatherReport(@HeaderMap HashMap<String, String> header, @Body WeatherReportRequest weatherReportRequest);

    /**
     * @param header
     * @return
     */
    @GET("weatherconditions")
    Call<WeatherConditionsResponse> getWeatherConditions(@HeaderMap HashMap<String, String> header);

    /**
     * @param header
     * @return
     */
    @POST("dailyreport/forecast")
    Call<ForecastResponse> getDailyreportForecast(@HeaderMap HashMap<String, String> header, @Body ForecastRequest forecastRequest);


}
