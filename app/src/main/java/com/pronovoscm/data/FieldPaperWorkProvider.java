package com.pronovoscm.data;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.api.FieldPaperWorkApi;
import com.pronovoscm.model.request.assignee.AssigneeRequest;
import com.pronovoscm.model.request.validateemail.ValidateEmailRequest;
import com.pronovoscm.model.response.AbstractCallback;
import com.pronovoscm.model.response.ErrorResponse;
import com.pronovoscm.model.response.assignee.AssigneeResponse;
import com.pronovoscm.model.response.companylist.CompanyListRequest;
import com.pronovoscm.model.response.companylist.CompanyListResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.trades.TradesResponse;
import com.pronovoscm.model.response.validateemail.ValidateEmailResponse;
import com.pronovoscm.persistence.domain.DaoSession;
import com.pronovoscm.persistence.domain.DrawingList;
import com.pronovoscm.persistence.domain.ImageTag;
import com.pronovoscm.persistence.domain.PunchlistAssignee;
import com.pronovoscm.persistence.domain.Taggables;
import com.pronovoscm.persistence.domain.TaggablesDao;
import com.pronovoscm.persistence.repository.FieldPaperWorkRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.SharedPref;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;

public class FieldPaperWorkProvider {


    private final String TAG = FieldPaperWorkProvider.class.getName();
    private final FieldPaperWorkApi mFieldPaperWorkApi;
    private PronovosApplication context;
    NetworkStateProvider networkStateProvider;
    private DaoSession daoSession;
    private LoginResponse loginResponse;
    private FieldPaperWorkRepository mFieldPaperWorkRepository;


    public FieldPaperWorkProvider(NetworkStateProvider networkStateProvider, FieldPaperWorkApi fieldPaperWorkApi, DaoSession daoSession, FieldPaperWorkRepository fieldPaperWorkRepository) {
        this.context = PronovosApplication.getContext();
        context.setUrl(Constants.BASE_API_URL);
        this.mFieldPaperWorkApi = fieldPaperWorkApi;
        this.networkStateProvider = networkStateProvider;
        this.daoSession = daoSession;
        mFieldPaperWorkRepository = fieldPaperWorkRepository;
    }

    public void getTrades(final ProviderResult<String> callback) {
        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            int userId=loginResponse.getUserDetails().getUsers_id();
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());
            Call<TradesResponse> photoDetailResponseCall = mFieldPaperWorkApi.getTradesAPI(headers);

            photoDetailResponseCall.enqueue(new AbstractCallback<TradesResponse>() {
                @Override
                protected void handleFailure(Call<TradesResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<TradesResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<TradesResponse> response) {
                    if (response.body() != null) {
                        TradesResponse tradesResponse = null;
                        try {
                            tradesResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (tradesResponse != null && tradesResponse.getStatus() == 200 && (tradesResponse.getData().getResponsecode() == 101 || tradesResponse.getData().getResponsecode() == 102)) {
                            mFieldPaperWorkRepository.doUpdateTrades(tradesResponse.getData().getTrades(),userId,loginResponse);
                            callback.success("");
                        } else if (tradesResponse != null) {
                            callback.failure(tradesResponse.getMessage());
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

    public void getAssignee(AssigneeRequest assigneeRequest, final ProviderResult<List<PunchlistAssignee>> callback) {
        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            int userId=loginResponse.getUserDetails().getUsers_id();
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<AssigneeResponse> assigneeAPI = mFieldPaperWorkApi.getAssigneeAPI(headers, assigneeRequest);

            assigneeAPI.enqueue(new AbstractCallback<AssigneeResponse>() {
                @Override
                protected void handleFailure(Call<AssigneeResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                    callback.success(mFieldPaperWorkRepository.getAssignee(assigneeRequest.getProjectId()));
                }

                @Override
                protected void handleError(Call<AssigneeResponse> call, ErrorResponse errorResponse) {
                    callback.success(mFieldPaperWorkRepository.getAssignee(assigneeRequest.getProjectId()));
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());

                    } else {
                        callback.failure(errorResponse.getMessage());
                    }

                }

                @Override
                protected void handleSuccess(Response<AssigneeResponse> response) {
                    if (response.body() != null) {
                        AssigneeResponse assigneeResponse = null;
                        try {
                            assigneeResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (assigneeResponse != null && assigneeResponse.getStatus() == 200 && (assigneeResponse.getData().getResponsecode() == 101 || assigneeResponse.getData().getResponsecode() == 102)) {
                            List<PunchlistAssignee> punchlistAssignees = mFieldPaperWorkRepository.doUpdateAssigneeTable(assigneeResponse.getData().getAssigneeList(), assigneeRequest.getProjectId(),userId);

                            callback.success(punchlistAssignees);
                        } else if (assigneeResponse != null) {
                            callback.failure(assigneeResponse.getMessage());
                        } else {
                            callback.failure("response null");
                        }
                    } else {
                        callback.failure("response null");
                    }
                }
            });

        } else {
            callback.success(mFieldPaperWorkRepository.getAssignee(assigneeRequest.getProjectId()));
        }

    }

public List<PunchlistAssignee> getDeActiveAssignees(Integer projectId){
        return mFieldPaperWorkRepository.getDeActiveAssignee(projectId);
}

public PunchlistAssignee getAssignee(Integer projectId, Integer usersId){
        return mFieldPaperWorkRepository.getDeAcAssignee(projectId,usersId);
}

    public void getCompanyList(CompanyListRequest companyListRequest, final ProviderResult<String> callback) {

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            int userId=loginResponse.getUserDetails().getUsers_id();
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<CompanyListResponse> companyListAPI = mFieldPaperWorkApi.getCompanyListAPI(headers, companyListRequest);

            companyListAPI.enqueue(new AbstractCallback<CompanyListResponse>() {
                @Override
                protected void handleFailure(Call<CompanyListResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<CompanyListResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<CompanyListResponse> response) {
                    if (response.body() != null) {
                        CompanyListResponse companyListResponse = null;
                        try {
                            companyListResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (companyListResponse != null && companyListResponse.getStatus() == 200 && (companyListResponse.getData().getResponsecode() == 101 || companyListResponse.getData().getResponsecode() == 102)) {
                            mFieldPaperWorkRepository.doUpdateCompanyList(companyListResponse.getData().getCompanies(), companyListRequest.getProjectId(),userId);
                            callback.success("");
                        } else if (companyListResponse != null) {
                            callback.failure(companyListResponse.getMessage());
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

    public void getValidateEmail(ValidateEmailRequest validateEmailRequest, final ProviderResult<ValidateEmailResponse> callback) {

        if (NetworkService.isNetworkAvailable(context)) {

            HashMap<String, String> headers = new HashMap<>();
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            headers.put("lastupdate", "");
            headers.put("timezone", TimeZone.getDefault().getID());
            headers.put("Authorization", "Bearer " + loginResponse.getUserDetails().getAuthtoken());

            Call<ValidateEmailResponse> validateEmail = mFieldPaperWorkApi.getValidateEmail(headers, validateEmailRequest);

            validateEmail.enqueue(new AbstractCallback<ValidateEmailResponse>() {
                @Override
                protected void handleFailure(Call<ValidateEmailResponse> call, Throwable throwable) {
                    callback.failure(throwable.getMessage());
                }

                @Override
                protected void handleError(Call<ValidateEmailResponse> call, ErrorResponse errorResponse) {
                    if (errorResponse != null && errorResponse.getData() != null && errorResponse.getData().getResponsecode() == 103) {
                        callback.AccessTokenFailure(errorResponse.getMessage());
                    } else {
                        callback.failure(errorResponse.getMessage());
                    }
                }

                @Override
                protected void handleSuccess(Response<ValidateEmailResponse> response) {
                    if (response.body() != null) {
                        ValidateEmailResponse validateEmailResponse = null;
                        try {
                            validateEmailResponse = response.body();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (validateEmailResponse != null && validateEmailResponse.getStatus() == 200 && (validateEmailResponse.getValidateEmailData().getResponsecode() == 101 || validateEmailResponse.getValidateEmailData().getResponsecode() == 102)) {
                            callback.success(validateEmailResponse);
                        } else if (validateEmailResponse != null) {
                            callback.failure(validateEmailResponse.getMessage());
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


}
