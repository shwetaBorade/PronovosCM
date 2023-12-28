package com.pronovoscm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pronovoscm.R;
import com.pronovoscm.adapter.FormUserAdapter;
import com.pronovoscm.data.ProjectFormProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.model.TransactionLogUpdate;
import com.pronovoscm.model.TransactionModuleEnum;
import com.pronovoscm.model.request.formcomponent.ProjectFormComponentRequest;
import com.pronovoscm.model.request.formuser.ProjectFormUserRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.FormAssets;
import com.pronovoscm.persistence.domain.Forms;
import com.pronovoscm.persistence.domain.FormsComponent;
import com.pronovoscm.persistence.domain.FormsName;
import com.pronovoscm.persistence.domain.UserForms;
import com.pronovoscm.persistence.repository.ProjectFormRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.ui.CustomProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class ProjectFormUserActivity extends BaseActivity implements FormUserAdapter.clickUserForm {
    @Inject
    ProjectFormProvider mprojectFormProvider;
    @Inject
    ProjectFormRepository mprojectFormRepository;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.addImageView)
    ImageView addImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
    @BindView(R.id.userFormRV)
    RecyclerView userFormRV;
    String formNameTitle;
    private String formSections;
    private FormUserAdapter formUserAdapter;
    private int projectId;
    private int originalFormId;
    private int activeRevisionNumber;
    private LoginResponse loginResponse;
    private ArrayList<UserForms> formsArrayList;
    private int formId;
    private ProjectFormUserRequest projectFormUserRequest;
    private long mLastClickTime = 0;

    @Override
    protected int doGetContentView() {
        return R.layout.project_form_user_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        doGetApplication().getDaggerComponent().inject(this);
        projectId = getIntent().getIntExtra("project_id", 0);
        formId = getIntent().getIntExtra("form_id", 0);
        originalFormId = getIntent().getIntExtra(Constants.INTENT_KEY_ORIGINAL_FORM_ID, 0);
        activeRevisionNumber = getIntent().getIntExtra(Constants.INTENT_KEY_FORM_ACTIVE_REVISION_NUMBER, 0);
        formSections = getIntent().getStringExtra(Constants.INTENT_KEY_FORM_SECTIONS);
        formsArrayList = new ArrayList<>();
        userFormRV.setLayoutManager(new LinearLayoutManager(this));
//        formsArrayList.addAll(mprojectFormRepository.getUserForms(projectId, originalFormId, loginResponse.getUserDetails().getUsers_id()));
        formUserAdapter = new FormUserAdapter(formsArrayList, projectId, this);
        userFormRV.setAdapter(formUserAdapter);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.GONE);
        if (loginResponse.getUserDetails().getPermissions().get(0).getEditForm() == 1) {
            addImageView.setVisibility(View.VISIBLE);
            addImageView.setClickable(true);
        } else {
            addImageView.setClickable(false);
            addImageView.setVisibility(View.INVISIBLE);

        }
        FormsName formsName = mprojectFormRepository.getUserFormsName(originalFormId, activeRevisionNumber, projectId);

        if (formsName != null) {
            formNameTitle = formsName.formName;
        } else {
            formNameTitle = mprojectFormRepository.getFormName(formId);
        }
        titleTextView.setText(formNameTitle);
        projectFormUserRequest = new ProjectFormUserRequest(projectId, formId);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("userActivity", "  onResume: originalFormId " + originalFormId + "  activeRevisionNumber   " + activeRevisionNumber + "  formID " + formId);
        loadData();
    }

    private void refreshAdapter() {
        formsArrayList.clear();
        formsArrayList.addAll(mprojectFormRepository.getUserForms(projectId, originalFormId, loginResponse.getUserDetails().getUsers_id()));
        formUserAdapter.notifyDataSetChanged();
    }

    private void callFormUserAPI() {
        mprojectFormProvider.getFormUsers(projectFormUserRequest, loginResponse,new ProviderResult<List<UserForms>>() {
            @Override
            public void success(List<UserForms> result) {
                mprojectFormRepository.getUserForms(projectId, originalFormId, loginResponse.getUserDetails().getUsers_id());
                refreshAdapter();
                if (formsArrayList.size() <= 0) {
                    noRecordTextView.setText("This form has not yet been filled out for this project.");
                    noRecordTextView.setVisibility(View.VISIBLE);
                } else {
                    noRecordTextView.setVisibility(View.GONE);

                }
            }

            @Override
            public void AccessTokenFailure(String message) {

                refreshAdapter();
                CustomProgressBar.dissMissDialog(ProjectFormUserActivity.this);
            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(ProjectFormUserActivity.this);
                refreshAdapter();

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
            if (formsArrayList.size() <= 0 && !NetworkService.isNetworkAvailable(this)) {
                noRecordTextView.setText("This form has not yet been filled out for this project.");
                noRecordTextView.setVisibility(View.VISIBLE);
            } else {
                noRecordTextView.setVisibility(View.GONE);
            }
        } else {
            offlineTextView.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.addImageView)
    public void addNewForm() {
        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        List<FormAssets> formAssets = mprojectFormRepository.getFormAssets();
//        boolean allAssetExist = true;
        File myDir = new File(getFilesDir().getAbsolutePath() + "/Pronovos/");//"/PronovosPronovos"
        /*for (int i = 0; i < formAssets.size(); i++) {
            String fname = formAssets.get(i).getFileName();
            File file = new File(myDir, fname);
            if (!file.exists() || file.length() == 0) {
                allAssetExist = false;
            }
        }

        */

        // Forms actualForm = mprojectFormRepository.getActualForm(formId, userForms.getRevisionNumber());
        boolean formComponent = mprojectFormRepository.isFormComponentDataExist(formId, originalFormId, activeRevisionNumber);
        Log.i("ProjectUser", "  onClickUserForm: isFormComponentDataExist  " + formComponent + "  formId " + formId + "  originalFormId " + originalFormId + " activeRevisionNumber " + activeRevisionNumber);

        if (formComponent) {
            startActivity(new Intent(this, ProjectFormDetailActivity.class).putExtra("project_id", projectId)
                    /* .putExtra(Constants.INTENT_KEY_FORM_SECTIONS, formSections)*/
                    .putExtra("form_id", formId)
                    .putExtra(Constants.INTENT_KEY_ORIGINAL_FORM_ID, originalFormId)
                    .putExtra(Constants.INTENT_KEY_FORM_ACTIVE_REVISION_NUMBER, activeRevisionNumber)
                    .putExtra("form_type", "Sync")
                    .putExtra("isAdded", true));
        } else {
            // call project form using revision number and original form iD here
            Log.d("ProjectUser", " call project form using revision number and original form iD  ");
            downLoadFormComponent(activeRevisionNumber, true);


        }
    }

    private void downLoadFormComponent(int revisionNumber, boolean isAdded) {
        CustomProgressBar.showDialog(ProjectFormUserActivity.this);
        ProjectFormComponentRequest request = new ProjectFormComponentRequest(originalFormId, projectId, revisionNumber);
        mprojectFormProvider.getProjectFormComponents(request, loginResponse, new ProviderResult<List<FormsComponent>>() {
            @Override
            public void success(List<FormsComponent> result) {
                CustomProgressBar.dissMissDialog(ProjectFormUserActivity.this);
                startActivity(new Intent(ProjectFormUserActivity.this, ProjectFormDetailActivity.class).putExtra("project_id", projectId)
                        /* .putExtra(Constants.INTENT_KEY_FORM_SECTIONS, formSections)*/
                        .putExtra("form_id", formId)
                        .putExtra(Constants.INTENT_KEY_ORIGINAL_FORM_ID, originalFormId)
                        .putExtra(Constants.INTENT_KEY_FORM_ACTIVE_REVISION_NUMBER, activeRevisionNumber)
                        .putExtra("form_type", "Sync")
                        .putExtra("isAdded", isAdded));
            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(ProjectFormUserActivity.this);
                refreshAdapter();
            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(ProjectFormUserActivity.this);
                (ProjectFormUserActivity.this).showMessageAlert(ProjectFormUserActivity.this, message, getString(R.string.ok));
                refreshAdapter();
            }
        });
    }

    @OnClick(R.id.leftImageView)
    public void onBackClick() {
        super.onBackPressed();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TransactionLogUpdate transactionLogUpdate) {
        Log.i("TEMP", "onEvent  bind: 1 ");
        if (transactionLogUpdate.getTransactionModuleEnum() != null && (transactionLogUpdate.getTransactionModuleEnum().equals(TransactionModuleEnum.PROJECT_FORM_SUBMIT))) {
            if (formsArrayList == null) {
                formsArrayList = new ArrayList<>();
            }
            formsArrayList.clear();
            formsArrayList.addAll(mprojectFormRepository.getUserForms(projectId, originalFormId, loginResponse.getUserDetails().getUsers_id()));
            if (userFormRV != null && formUserAdapter != null && formsArrayList != null) {
//            refreshAdapter();
                if (formsArrayList.size() <= 0 && !NetworkService.isNetworkAvailable(this)) {
                    noRecordTextView.setText("This form has not yet been filled out for this project.");
                    noRecordTextView.setVisibility(View.VISIBLE);
                } else {
                    noRecordTextView.setVisibility(View.GONE);
                }
                formUserAdapter.notifyDataSetChanged();

            }
        }
    }

    @Override
    public void onClickUserForm(UserForms userForms) {
        List<FormAssets> formAssets = mprojectFormRepository.getFormAssets();
//        boolean allAssetExist = true;
//        File myDir = new File(getFilesDir().getAbsolutePath() + "/Pronovos/");//"/PronovosPronovos"
        /*for (int i = 0; i < formAssets.size(); i++) {
            String fname = formAssets.get(i).getFileName();
            File file = new File(myDir, fname);
            if (!file.exists() || file.length() == 0) {
                allAssetExist = false;
            }
        }*/
        Forms actualForm = mprojectFormRepository.getActualForm(userForms.getFormId(), userForms.getRevisionNumber());
        if (actualForm != null) {
            boolean formComponent = mprojectFormRepository.isFormComponentDataExist(actualForm.formsId, originalFormId, activeRevisionNumber);
            Log.i("Project userActivity", "formsId  " + actualForm.formsId + " original formsId = " + actualForm.originalFormsId + " 0onClickUserForm: " + formComponent + "  userForms.getCreatedAt() " + userForms.getCreatedAt());
            if (formComponent) {
                launchFormDetailActivity(userForms, actualForm);
            } else {
                downLoadFormComponent(actualForm.revisionNumber, false);
            }
        } else {
            downloadProjectForm(userForms.getFormId(), projectId, userForms.getRevisionNumber());
        }

    }

    private void downloadProjectForm(int originalFormId, int projectId, int revisionNum) {
        CustomProgressBar.showDialog(ProjectFormUserActivity.this);
        ProjectFormComponentRequest projectOverviewRequest = new ProjectFormComponentRequest(originalFormId, projectId, revisionNum);
        mprojectFormProvider.getProjectFromUsingID(projectOverviewRequest, loginResponse, new ProviderResult<Forms>() {
            @Override
            public void success(Forms result) {
                // formsArrayList.clear();
                //  formsArrayList.addAll(mprojectFormRepository.getProjectForm(projectId, ""));
                // CustomProgressBar.dissMissDialog(ProjectFormUserActivity.this);
                downLoadFormComponent(revisionNum, false);
            }

            @Override
            public void AccessTokenFailure(String message) {

                CustomProgressBar.dissMissDialog(ProjectFormUserActivity.this);
            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(ProjectFormUserActivity.this);
                (ProjectFormUserActivity.this).showMessageAlert(ProjectFormUserActivity.this, message, getString(R.string.ok));

            }
        });
    }

    private void launchFormDetailActivity(UserForms userForms, Forms form) {
        Log.d("OPEN_FORM", "PROJECT FORM USER ACTIVITY launchFormDetailActivity  userForms.getId()  " + userForms.getId());
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        startActivity(new Intent(this, ProjectFormDetailActivity.class)
                .putExtra("project_id", projectId).putExtra("form_id", form.formsId)
                .putExtra("user_form_id", userForms.getId()).putExtra("form_type", "Sync")
                .putExtra(Constants.INTENT_KEY_ORIGINAL_FORM_ID, userForms.getFormId())
                .putExtra(Constants.INTENT_KEY_FORM_ACTIVE_REVISION_NUMBER, userForms.getRevisionNumber())
                .putExtra(Constants.INTENT_KEY_FORM_CREATED_DATE, sdf.format(userForms.getCreatedAt()))
                .putExtra(Constants.INTENT_KEY_FORM_CREATED_BY, userForms.getCreatedByUserName()));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String s) {
        if (s.equals("DeletedForm")) {
            Log.e("ProjectUnSyncForm", "DeleteForm");
            loadData();
        }
    }

    private void loadData() {
        Log.e("FormUserActivity", "loadData: originalFormId " + originalFormId + "  ** formId = " + formId + "   ** activeRevisionNumber  =" + activeRevisionNumber);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        callFormUserAPI();
        if (userFormRV != null && formUserAdapter != null && formsArrayList != null) {
//            refreshAdapter();
            if (formsArrayList.size() <= 0 && !NetworkService.isNetworkAvailable(this)) {
                noRecordTextView.setText(getString(R.string.no_form_record_message));
                noRecordTextView.setVisibility(View.VISIBLE);
            } else {
                noRecordTextView.setVisibility(View.GONE);
            }
            if (formsArrayList.size() <= 0 && NetworkService.isNetworkAvailable(this)) {
                noRecordTextView.setText("Loading Forms");
                noRecordTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
