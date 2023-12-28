package com.pronovoscm.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pronovoscm.R;
import com.pronovoscm.data.FieldPaperWorkProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.model.request.assignee.AssigneeRequest;
import com.pronovoscm.model.response.companylist.CompanyListRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.login.UserPermissions;
import com.pronovoscm.persistence.domain.PunchlistAssignee;
import com.pronovoscm.utils.IntentExtra;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.dialogs.MessageDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class FieldPaperWorkActivity extends BaseActivity {
    @Inject
    FieldPaperWorkProvider mFieldPaperWorkProvider;

    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.dailyReportView)
    CardView dailyReportView;
    @BindView(R.id.punchListView)
    CardView punchListView;
    private int projectId;
    private MessageDialog messageDialog;
    private LoginResponse loginResponse;

    @Override
    protected int doGetContentView() {
        return R.layout.field_paper_work_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        messageDialog = new MessageDialog();
        projectId = getIntent().getIntExtra("project_id", 0);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.INVISIBLE);
        titleTextView.setText(getString(R.string.field_paper_work));

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        UserPermissions userPermissions = loginResponse.getUserDetails().getPermissions().get(0);
//        view1.setVisibility(View.VISIBLE);
        if (userPermissions.getViewProjectDailyReport() == 1) {
            dailyReportView.setVisibility(View.VISIBLE);
        } else {
            dailyReportView.setVisibility(View.GONE);
//            view1.setVisibility(View.GONE);
        }
        if (userPermissions.getViewPunchList() == 1) {
            punchListView.setVisibility(View.VISIBLE);
        } else {
            punchListView.setVisibility(View.GONE);
//            view1.setVisibility(View.GONE);
        }


        callTradesAPI();
        callAssigneeAPI();
        callCompanyListAPI();

    }

    private void callTradesAPI() {
        mFieldPaperWorkProvider.getTrades(new ProviderResult<String>() {
            @Override
            public void success(String result) {

            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(FieldPaperWorkActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(FieldPaperWorkActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(FieldPaperWorkActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(FieldPaperWorkActivity.this, message, getString(R.string.ok));
//                messageDialog.showMessageAlert(FieldPaperWorkActivity.this, getString(R.string.failureMessage), getString(R.string.ok));

            }
        });
    }

    private void callAssigneeAPI() {
        AssigneeRequest assigneeRequest = new AssigneeRequest();
        assigneeRequest.setProjectId(projectId);
        mFieldPaperWorkProvider.getAssignee(assigneeRequest, new ProviderResult<List<PunchlistAssignee>>() {
            @Override
            public void success(List<PunchlistAssignee> result) {

            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(FieldPaperWorkActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(FieldPaperWorkActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(FieldPaperWorkActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(FieldPaperWorkActivity.this, message, getString(R.string.ok));
//                messageDialog.showMessageAlert(FieldPaperWorkActivity.this, getString(R.string.failureMessage), getString(R.string.ok));

            }
        });
    }

    private void callCompanyListAPI() {
        CompanyListRequest companyListRequest = new CompanyListRequest();
        companyListRequest.setProjectId(projectId);
        mFieldPaperWorkProvider.getCompanyList(companyListRequest, new ProviderResult<String>() {
            @Override
            public void success(String result) {

            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(FieldPaperWorkActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(FieldPaperWorkActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(FieldPaperWorkActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(FieldPaperWorkActivity.this, message, getString(R.string.ok));
//                messageDialog.showMessageAlert(FieldPaperWorkActivity.this, getString(R.string.failureMessage), getString(R.string.ok));

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
        } else {
            offlineTextView.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.leftImageView)
    public void onBackClick() {
        super.onBackPressed();
    }

    @OnClick(R.id.dailyReportView)
    public void onDailyReportClick() {
        startActivity(new Intent(this, DailyReportActivity.class).putExtra("project_id", projectId));
    }

    @OnClick(R.id.punchListView)
    public void onPunchListClick() {
        Log.e("TAG", " FindPaperWorkActivity onPunchListClick: " );
        startActivity(new Intent(this, PunchListActivity.class).putExtra(IntentExtra.PROJECT_ID.name(), projectId).putExtra(IntentExtra.PROJECT_NAME.name(), ""));
    }
}
