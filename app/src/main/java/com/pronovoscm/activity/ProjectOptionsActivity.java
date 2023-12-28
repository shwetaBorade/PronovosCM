package com.pronovoscm.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pronovoscm.R;
import com.pronovoscm.adapter.ProjectOptionAdapter;
import com.pronovoscm.data.FieldPaperWorkProvider;
import com.pronovoscm.data.ProjectFormProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.data.issuetracking.ProjectIssueTrackingProvider;
import com.pronovoscm.model.OptionEnum;
import com.pronovoscm.model.request.assignee.AssigneeRequest;
import com.pronovoscm.model.response.companylist.CompanyListRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.login.UserPermissions;
import com.pronovoscm.persistence.domain.PunchlistAssignee;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.dialogs.MessageDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProjectOptionsActivity extends BaseActivity implements View.OnClickListener {
    @Inject
    FieldPaperWorkProvider mFieldPaperWorkProvider;
    @Inject
    ProjectFormProvider mprojectFormProvider;
    @Inject
    ProjectIssueTrackingProvider projectIssueTrackingProvider;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    Intent recievedIntent;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.optionsRecyclerView)
    RecyclerView optionsRecyclerView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;

    private String projectName;
    private int projectId;
    private ProjectOptionAdapter mProjectOptionAdapter;
    private ArrayList<OptionEnum> listOfOption;
    private LoginResponse loginResponse;
    private MessageDialog messageDialog;

    @Override
    protected int doGetContentView() {
        return R.layout.project_folder_list;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.project_folder_list);
        doGetApplication().getDaggerComponent().inject(this);
        doGetApplication().getDaggerComponent().inject(this);
        messageDialog = new MessageDialog();
        updateView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        setContentView(R.layout.project_folder_list);
//        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            optionsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//        } else {
//            optionsRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
//
//        }
        updateView();
        if (networkStateProvider.isOffline()) {
            offlineTextView.setVisibility(View.VISIBLE);
        } else {
            offlineTextView.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
        } else {
            offlineTextView.setVisibility(View.GONE);
        }
    }

    private void updateView() {
        ButterKnife.bind(this);
        recievedIntent = getIntent();

        projectName = getIntent().getStringExtra("project_name");
        projectId = getIntent().getIntExtra("project_id", 0);
        titleTextView.setText(projectName);
        backImageView.setOnClickListener(this);
//        backImageView.setImageResource(R.drawable.ic_arrow_back);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.INVISIBLE);
        Configuration newConfig = getResources().getConfiguration();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            optionsRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            // set background for landscape
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            optionsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            // set background for portrait
        }
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        UserPermissions userPermissions = loginResponse.getUserDetails().getPermissions().get(0);
        listOfOption = new ArrayList<>();
        listOfOption.add(OptionEnum.OVERVIEW);
        if (loginResponse.getUserDetails().getPermissions().get(0).getViewAlbums() == 1) {
            listOfOption.add(OptionEnum.PHOTO);
        }
        if (loginResponse.getUserDetails().getPermissions().get(0).getViewDrawings() == 1) {
            listOfOption.add(OptionEnum.DRAWING);

        }
        if (userPermissions.getViewProjectDailyReport() == 1) {
            listOfOption.add(OptionEnum.DAILY_REPORTS);
        }
        if (userPermissions.getViewForm() == 1) {
            listOfOption.add(OptionEnum.FORMS);
        }
        if (userPermissions.getViewDocument() == 1) {
            listOfOption.add(OptionEnum.DOCUMENTS);
        }
        if (loginResponse.getUserDetails().getPermissions().get(0).getViewFieldDocs() == 1) {
//            listOfOption.add(OptionEnum.FIELDPAPERWORK);
        }
        if (userPermissions.getViewPunchList() == 1) {
            listOfOption.add(OptionEnum.PUNCH_LIST);
        }
        if (loginResponse.getUserDetails().getPermissions().get(0).getViewProjectEquipment() == 1) {
            listOfOption.add(OptionEnum.EQUIPMENT);
        }
        if (userPermissions.getViewRfi() == 1) {
            listOfOption.add(OptionEnum.RFIS);
        }
        if (userPermissions.getViewSubmittal() == 1) {
            listOfOption.add(OptionEnum.SUBMITTALS);
        }
        //uncomment once permission is given from backend
        if (userPermissions.getViewIssueTracking() == 1) {
            listOfOption.add(OptionEnum.ISSUE_TRACKING);
        }
        // remove after permission
        //listOfOption.add(OptionEnum.ISSUE_TRACKING);

        mProjectOptionAdapter = new ProjectOptionAdapter(this, listOfOption, projectId, projectName, mprojectFormProvider);
        optionsRecyclerView.setAdapter(mProjectOptionAdapter);

        callTradesAPI();
        callAssigneeAPI();
        callCompanyListAPI();
        projectIssueTrackingProvider.getIssueSections();
        projectIssueTrackingProvider.getCustomItems();
        projectIssueTrackingProvider.getCustomItemTypes();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftImageView:
                onBackPressed();
                break;
        }
    }

    private void callTradesAPI() {
        mFieldPaperWorkProvider.getTrades(new ProviderResult<String>() {
            @Override
            public void success(String result) {

            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(ProjectOptionsActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(ProjectOptionsActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(ProjectOptionsActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(ProjectOptionsActivity.this, message, getString(R.string.ok));
//                messageDialog.showMessageAlert(ProjectOptionsActivity.this, getString(R.string.failureMessage), getString(R.string.ok));

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
                startActivity(new Intent(ProjectOptionsActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(ProjectOptionsActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(ProjectOptionsActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(ProjectOptionsActivity.this, message, getString(R.string.ok));
//                messageDialog.showMessageAlert(ProjectOptionsActivity.this, getString(R.string.failureMessage), getString(R.string.ok));

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
                startActivity(new Intent(ProjectOptionsActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(ProjectOptionsActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(ProjectOptionsActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(ProjectOptionsActivity.this, message, getString(R.string.ok));
//                messageDialog.showMessageAlert(ProjectOptionsActivity.this, getString(R.string.failureMessage), getString(R.string.ok));

            }
        });
    }

}
