package com.pronovoscm.data;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.api.CrewReportApi;
import com.pronovoscm.model.request.crewreport.CrewReportRequest;
import com.pronovoscm.model.response.AbstractCallback;
import com.pronovoscm.model.response.ErrorResponse;
import com.pronovoscm.model.response.crewreport.CrewReportResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.CrewList;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.repository.CrewReportRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.SharedPref;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;

public class CrewReportProvider {


    private final String TAG = CrewReportProvider.class.getName();
    private final CrewReportApi mCrewReportApi;
    private PronovosApplication context;
    NetworkStateProvider networkStateProvider;
    private DaoSession daoSession;
    private LoginResponse loginResponse;
    private CrewReportRepository mCrewReportRepository;


    public CrewReportProvider(NetworkStateProvider networkStateProvider, CrewReportApi crewReportApi, DaoSession daoSession, CrewReportRepository crewReportRepository) {
        this.context = PronovosApplication.getContext();
        context.setUrl(Constants.BASE_API_URL);
        this.mCrewReportApi = crewReportApi;
        this.networkStateProvider = networkStateProvider;
        this.daoSession = daoSession;
        mCrewReportRepository = crewReportRepository;
    }


    public void getCrewReports(CrewReportRequest crewReportRequest, Date date, final ProviderResult<List<CrewList>> callback) {


        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<CrewReportResponse> assigneeAPI = mCrewReportApi.getCrewReport(headers, crewReportRequest);

            assigneeAPI.enqueue(new AbstractCallback<CrewReportResponse>() {
                @Override
                protected void handleFailure(Call<CrewReportResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                    callback.success(mCrewReportRepository.getCrewList(crewReportRequest.getProjectId(),date));
                }

                @Override
                protected void handleError(Call<CrewReportResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<CrewReportResponse> response) {
                    if (response.body() != null) {
                        CrewReportResponse crewReportResponse = null;
                        try {
                            crewReportResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (crewReportResponse != null && crewReportResponse.getStatus() == 200 && (crewReportResponse.getCrewReportData().getResponsecode() == 101 || crewReportResponse.getCrewReportData().getResponsecode() == 102)) {
                            List<CrewList> crewList = mCrewReportRepository.doUpdateCrewListTable(crewReportResponse.getCrewReportData().getCrewReport(), date, crewReportRequest.getProjectId());

                            mCrewReportRepository.doUpdateSyncCrewListTable(crewReportResponse.getCrewReportData().getSyncData(),crewReportRequest.getProjectId());
                            callback.success(crewList);
                        } else if (crewReportResponse != null) {
                            callback.failure(crewReportResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.success(mCrewReportRepository.getCrewList(crewReportRequest.getProjectId(),date));
        }

    }


}
