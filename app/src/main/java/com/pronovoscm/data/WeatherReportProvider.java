package com.pronovoscm.data;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.api.WeatherReportApi;
import com.pronovoscm.model.request.forecast.ForecastRequest;
import com.pronovoscm.model.request.weatherreport.WeatherReportRequest;
import com.pronovoscm.model.response.AbstractCallback;
import com.pronovoscm.model.response.ErrorResponse;
import com.pronovoscm.model.response.forecastresponse.Forecast;
import com.pronovoscm.model.response.forecastresponse.ForecastResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.weatherconditions.WeatherConditionsResponse;
import com.pronovoscm.model.response.weatherreport.WeatherReportResponse;
import com.pronovoscm.model.response.weatherreport.WeatherReports;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.ImageTag;
import com.pronovoscm.persistence.domain.WeatherReport;
import com.pronovoscm.persistence.domain.WeatherWidget;
import com.pronovoscm.persistence.repository.WeatherReportRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.SharedPref;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;

public class WeatherReportProvider {


    private final String TAG = WeatherReportProvider.class.getName();
    private final WeatherReportApi mWeatherReportApi;
    private PronovosApplication context;
    NetworkStateProvider networkStateProvider;
    private DaoSession daoSession;
    private LoginResponse loginResponse;
    private WeatherReportRepository mWeatherReportRepository;


    public WeatherReportProvider(NetworkStateProvider networkStateProvider, WeatherReportApi weatherReportApi, DaoSession daoSession, WeatherReportRepository weatherReportRepository) {
        this.context = PronovosApplication.getContext();
        context.setUrl(Constants.BASE_API_URL);
        this.mWeatherReportApi = weatherReportApi;
        this.networkStateProvider = networkStateProvider;
        this.daoSession = daoSession;
        mWeatherReportRepository = weatherReportRepository;
    }


    public void getWeatherCondition(final ProviderResult<String> callback) {


        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<WeatherConditionsResponse> assigneeAPI = mWeatherReportApi.getWeatherConditions(headers);

            assigneeAPI.enqueue(new AbstractCallback<WeatherConditionsResponse>() {
                @Override
                protected void handleFailure(Call<WeatherConditionsResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<WeatherConditionsResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<WeatherConditionsResponse> response) {
                    if (response.body() != null) {
                        WeatherConditionsResponse weatherConditionsResponse = null;
                        try {
                            weatherConditionsResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (weatherConditionsResponse != null && weatherConditionsResponse.getStatus() == 200 && (weatherConditionsResponse.getData().getResponsecode() == 101 || weatherConditionsResponse.getData().getResponsecode() == 102)) {
                            mWeatherReportRepository.doUpdateWeatherConditionTable(weatherConditionsResponse.getData().getConditions());
                            callback.success("");
                        } else if (weatherConditionsResponse != null) {
                            callback.failure(weatherConditionsResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {

        }

    }

    public void getWeatherForecast(ForecastRequest forecastRequest, Date date, final ProviderResult<List<WeatherWidget>> callback) {


        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<ForecastResponse> assigneeAPI = mWeatherReportApi.getDailyreportForecast(headers, forecastRequest);

            assigneeAPI.enqueue(new AbstractCallback<ForecastResponse>() {
                @Override
                protected void handleFailure(Call<ForecastResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<ForecastResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<ForecastResponse> response) {
                    if (response.body() != null) {
                        ForecastResponse forecastResponse = null;
                        try {
                            forecastResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (forecastResponse != null && forecastResponse.getStatus() == 200 && (forecastResponse.getForecastData().getResponsecode() == 101 || forecastResponse.getForecastData().getResponsecode() == 102)) {
                            List<WeatherWidget> weatherWidgets = mWeatherReportRepository.doUpdateWeatherWidgetTable(forecastResponse.getForecastData().getForecast(), forecastRequest.getProjectId(), date);
                            callback.success(weatherWidgets);
                        } else if (forecastResponse != null) {
                            callback.failure(forecastResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.success(mWeatherReportRepository.getWeatherWidget(forecastRequest.getProjectId(), date));

        }

    }

    public void getWeatherReport(WeatherReportRequest weatherReportRequest, Date date, final ProviderResult<WeatherReport> callback) {


        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<WeatherReportResponse> assigneeAPI = mWeatherReportApi.getWeatherReport(headers, weatherReportRequest);

            assigneeAPI.enqueue(new AbstractCallback<WeatherReportResponse>() {
                @Override
                protected void handleFailure(Call<WeatherReportResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                    callback.success(mWeatherReportRepository.getWeatherReports(Integer.parseInt(weatherReportRequest.getProjectId()), date));
                }

                @Override
                protected void handleError(Call<WeatherReportResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<WeatherReportResponse> response) {
                    if (response.body() != null) {
                        WeatherReportResponse weatherReportResponse = null;
                        try {
                            weatherReportResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (weatherReportResponse != null && weatherReportResponse.getStatus() == 200 && (weatherReportResponse.getWeatherReportData().getResponsecode() == 101 || weatherReportResponse.getWeatherReportData().getResponsecode() == 102)) {
                            if (weatherReportResponse.getWeatherReportData().getWeatherReports() != null) {
                                WeatherReport weatherReport = mWeatherReportRepository.doUpdateWeatherReportTable(weatherReportResponse.getWeatherReportData().getWeatherReports(), Integer.parseInt(weatherReportRequest.getProjectId()), date, true);

                                mWeatherReportRepository.doUpdateSyncWeatherReportTable(weatherReportResponse.getWeatherReportData().getSyncData(), Integer.parseInt(weatherReportRequest.getProjectId()));
                                callback.success(weatherReport);
                            } else {
                                callback.success(null);
                            }
                        } else if (weatherReportResponse != null) {
                            callback.failure(weatherReportResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.success(mWeatherReportRepository.getWeatherReports(Integer.parseInt(weatherReportRequest.getProjectId()), date));
        }

    }

    public List<ImageTag> getWeatherCondition(long photoMobileId) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));


        Query query = daoSession.getWeatherConditionsDao().queryBuilder().where(
                new WhereCondition.StringCondition("id IN " +
                        "(SELECT tag_id FROM Taggables where taggable_id_mobile = " + photoMobileId + ")")).build();
        List<ImageTag> photoTags = query.list();

        return photoTags;
    }
}
