package com.pronovoscm.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pronovoscm.R;
import com.pronovoscm.adapter.SubmittalsListAdapter;
import com.pronovoscm.chipslayoutmanager.util.log.Log;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.model.SubmittalListItem;
import com.pronovoscm.model.SubmittalStatusEnum;
import com.pronovoscm.model.response.submittals.SubmittalsResponse;
import com.pronovoscm.persistence.domain.PjSubmittalContactList;
import com.pronovoscm.persistence.domain.PjSubmittals;
import com.pronovoscm.persistence.repository.ProjectSubmittalsRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.SubmittalListFilterEvent;
import com.pronovoscm.utils.dialogs.SubmittalListFilterDialog;
import com.pronovoscm.utils.ui.CustomProgressBar;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SubmittalsListActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.searchEditText)
    EditText searchEditText;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.submittalsRecyclerView)
    RecyclerView submittalsRecyclerView;
    @BindView(R.id.seachClearImageView)
    ImageView searchClearImageView;
    @BindView(R.id.filterTextView)
    TextView filterTextView;
    @Inject
    ProjectsProvider projectsProvider;
    @Inject
    ProjectSubmittalsRepository projectSubmittalsRepository;
    List<SubmittalListItem> submittalList = new ArrayList<>();

    boolean isOffline = false;
    private int projectId;
    private SubmittalsListAdapter submittalsListAdapter;
    private SubmittalStatusEnum submittalStatusEnum;

    private void initUi() {
        backImageView.setOnClickListener(this);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        titleTextView.setText(getString(R.string.submittals));
        submittalsRecyclerView.setLayoutManager(new LinearLayoutManager(SubmittalsListActivity.this));
        setSubmittalAdapter();
        initializeFilterValue();
    }

    private void initializeFilterValue() {
        submittalStatusEnum = SubmittalStatusEnum.All;
        setFilterCount();
    }

    @OnClick(R.id.rightImageView)
    public void onFilterClick() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        SubmittalListFilterDialog filterDialog = new SubmittalListFilterDialog(submittalStatusEnum);
        Bundle bundle = new Bundle();
        bundle.putInt("projectId", projectId);
        filterDialog.setCancelable(false);
        filterDialog.setArguments(bundle);
        filterDialog.show(ft, "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        doGetApplication().getDaggerComponent().inject(this);
        projectId = getIntent().getIntExtra(Constants.INTENT_KEY_PROJECT_ID, 0);
        initUi();
        setSearchTextWatcher();
        submittalStatusEnum = SubmittalStatusEnum.All;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{getExternalPermission()},
                    Constants.FILESTORAGE_REQUEST_CODE);
        } else {
            // API call for submittal list and submittal detail
            callSubmittalsListApi(true);
            callSubmittalsDetailApi();
        }
    }

    private void setSubmittalAdapter() {
        submittalsListAdapter = new SubmittalsListAdapter(SubmittalsListActivity.this, submittalList, projectId);
        submittalsRecyclerView.setLayoutManager(new LinearLayoutManager(SubmittalsListActivity.this));
        submittalsRecyclerView.setAdapter(submittalsListAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults.length > 0 && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED && requestCode == Constants.FILESTORAGE_REQUEST_CODE) {
            callSubmittalsListApi(true);
            callSubmittalsDetailApi();
        }
    }

    private void handleAccessTokenFails() {
        startActivity(new Intent(SubmittalsListActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK));
        SharedPref.getInstance(SubmittalsListActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
        SharedPref.getInstance(SubmittalsListActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
        finish();
    }

    private void callSubmittalsListApi(boolean showProgress) {
        if (NetworkService.isNetworkAvailable(SubmittalsListActivity.this)) {
            if (showProgress)
                CustomProgressBar.showDialog(SubmittalsListActivity.this);
            projectsProvider.getProjectSubmittalList(projectId, new ProviderResult<SubmittalsResponse>() {
                @Override
                public void success(SubmittalsResponse result) {
                    CustomProgressBar.dissMissDialog(SubmittalsListActivity.this);
                    loadSubmittalList();
                }

                @Override
                public void AccessTokenFailure(String message) {
                    CustomProgressBar.dissMissDialog(SubmittalsListActivity.this);
                    noRecordTextView.setText("");
                    // isLoading = false;
                    handleAccessTokenFails();
                }

                @Override
                public void failure(String message) {
                    CustomProgressBar.dissMissDialog(SubmittalsListActivity.this);
                    noRecordTextView.setText("");
                    loadSubmittalList();
                }
            });
        } else {
            CustomProgressBar.dissMissDialog(SubmittalsListActivity.this);
            loadSubmittalList();
        }
    }

    private void loadSubmittalList() {
        CustomProgressBar.dissMissDialog(SubmittalsListActivity.this);
        List<PjSubmittals> submittalsList = projectSubmittalsRepository.getSearchSubmittalFilterList(projectId, searchEditText.getText().toString(), submittalStatusEnum);
        noRecordTextView.setVisibility(View.GONE);
        submittalList.clear();
        if (submittalsList != null && submittalsList.size() > 0) {
            for (PjSubmittals pjSubmittals : submittalsList) {
                PjSubmittalContactList contactList = projectSubmittalsRepository.getSearchPjSubmittalAssignToContact(pjSubmittals);
                SubmittalListItem item = new SubmittalListItem();
                item.setPjSubmittals(pjSubmittals);
                item.setPjSubmittalContactList(contactList);
                item.setContactCount(projectSubmittalsRepository.getPjContactListSize(pjSubmittals));
                submittalList.add(item);
            }
            submittalsListAdapter.setSubmittalList(submittalList);
            submittalsListAdapter.notifyDataSetChanged();
        } else {
            if (NetworkService.isNetworkAvailable(SubmittalsListActivity.this))
                noRecordTextView.setText(getString(R.string.no_results_submittals));
            else {
                if (TextUtils.isEmpty(searchEditText.getText().toString()))
                    noRecordTextView.setText(getString(R.string.no_submittal_offline_message));
                else {
                    noRecordTextView.setText(getString(R.string.no_results_submittals));
                }
            }
            noRecordTextView.setVisibility(View.VISIBLE);
        }
        CustomProgressBar.dissMissDialog(SubmittalsListActivity.this);
    }

    private void callSubmittalsDetailApi() {
        if (NetworkService.isNetworkAvailable(SubmittalsListActivity.this)) {
            projectsProvider.getSubmittalDetail(projectId, new ProviderResult<SubmittalsResponse>() {
                @Override
                public void success(SubmittalsResponse result) {
                    Log.v("SubmittalListActivity", "submittalsDetailApi success" + new Gson().toJson(result));
                }

                @Override
                public void AccessTokenFailure(String message) {
                    handleAccessTokenFails();
                }

                @Override
                public void failure(String message) {
                    Log.v("SubmittalListActivity", "submittalsDetailApi failure");
                }
            });
        }
    }
    private void setSearchTextWatcher() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())) {
                    searchClearImageView.setVisibility(View.INVISIBLE);
                } else {
                    searchClearImageView.setVisibility(View.VISIBLE);
                }
                loadSubmittalList();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick({R.id.seachClearImageView})
    public void clickSearchClear() {
        searchEditText.setText("");

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.leftImageView) {
            onBackPressed();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SubmittalListFilterEvent submittalListFilterEvent) {
        Log.d("SubmittalsListActivity", "onEvent: " + submittalListFilterEvent);

        submittalStatusEnum = submittalListFilterEvent.getSubmittalStatusEnum();
        setFilterCount();
        loadSubmittalList();
    }

    private void setFilterCount() {
        int filterCount = 0;
        if (submittalStatusEnum != SubmittalStatusEnum.All) {
            filterCount = filterCount + 1;
        }
        if (filterCount > 0) {
            filterTextView.setText(String.valueOf(filterCount));
            filterTextView.setVisibility(View.VISIBLE);
            rightImageView.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_filter_regions));
        } else {
            filterTextView.setVisibility(View.GONE);
            rightImageView.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_filter_regions));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            isOffline = true;
            offlineTextView.setVisibility(View.VISIBLE);
        } else {
            isOffline = false;
            offlineTextView.setVisibility(View.GONE);
            callSubmittalsListApi(true);
            callSubmittalsDetailApi();
        }
    }

    @Override
    protected int doGetContentView() {
        return R.layout.activity_submittals_list;
    }
}
