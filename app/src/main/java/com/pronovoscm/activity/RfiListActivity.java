package com.pronovoscm.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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

import com.pronovoscm.R;
import com.pronovoscm.adapter.RfiListAdapter;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.model.RFIStatusEnum;
import com.pronovoscm.model.RfiListItem;
import com.pronovoscm.model.response.rfi.RfiResponse;
import com.pronovoscm.model.response.rfi.contact.RfiContactListResponse;
import com.pronovoscm.persistence.domain.PjRfi;
import com.pronovoscm.persistence.domain.PjRfiContactList;
import com.pronovoscm.persistence.repository.ProjectRfiRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.RfiListFilterEvent;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.dialogs.RfiListFilterDialog;
import com.pronovoscm.utils.ui.CustomProgressBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RfiListActivity extends BaseActivity implements View.OnClickListener {
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
    @BindView(R.id.rfiRecyclerView)
    RecyclerView rfiRecyclerView;
    @BindView(R.id.seachClearImageView)
    ImageView searchClearImageView;
    @Inject
    ProjectsProvider projectsProvider;
    @Inject
    ProjectRfiRepository projectRfiRepository;
    List<RfiListItem> rfiList = new ArrayList<>();
    @BindView(R.id.filterTextView)
    TextView filterTextView;
    boolean isOffline = false;
    private String projectName;
    private int projectId;
    private RfiListAdapter rfiListAdapter;
    private RFIStatusEnum mRFIStatusEnum;
    private boolean isRfiApiDone = false;
    private boolean isRfiContactListApiDone = false;

    private Date mDueDate;
    private Date mSubmittedDate;
    private PjRfiContactList filterAssignToContact;

    private void initUi() {
        backImageView.setOnClickListener(this);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        titleTextView.setText(getString(R.string.rfis));
        rfiRecyclerView.setLayoutManager(new LinearLayoutManager(RfiListActivity.this));
        setRfiAdapter();
        initializeFilterValue();
    }

    private void initializeFilterValue() {
        mRFIStatusEnum = RFIStatusEnum.Open;
        setFilterCount();
    }

    @OnClick(R.id.rightImageView)
    public void onFilterClick() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        RfiListFilterDialog filterDialog = new RfiListFilterDialog(mRFIStatusEnum, filterAssignToContact, mSubmittedDate, null, mDueDate);
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
        projectName = getIntent().getStringExtra(Constants.INTENT_KEY_PROJECT_NAME);
        projectId = getIntent().getIntExtra(Constants.INTENT_KEY_PROJECT_ID, 0);
        initUi();
        setSearchTextWatcher();
        mRFIStatusEnum = RFIStatusEnum.Open;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{getExternalPermission()},
                    Constants.FILESTORAGE_REQUEST_CODE);
        } else {
            callRfiListApi(true);
            callContactList(true);
        }
    }

    private void setRfiAdapter() {
        rfiListAdapter = new RfiListAdapter(RfiListActivity.this, rfiList, projectId);
        rfiRecyclerView.setLayoutManager(new LinearLayoutManager(RfiListActivity.this));
        rfiRecyclerView.setAdapter(rfiListAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults.length > 0 && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED && requestCode == Constants.FILESTORAGE_REQUEST_CODE) {
            callRfiListApi(true);
            callContactList(true);
        }
    }

    private void handleAccessTokenFails() {
        startActivity(new Intent(RfiListActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK));
        SharedPref.getInstance(RfiListActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
        SharedPref.getInstance(RfiListActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
        finish();
    }

    private void callRfiListApi(boolean showProgress) {
        if (NetworkService.isNetworkAvailable(RfiListActivity.this)) {
            if (showProgress)
                CustomProgressBar.showDialog(RfiListActivity.this);
            projectsProvider.getProjectRfiList(projectId, new ProviderResult<RfiResponse>() {
                @Override
                public void success(RfiResponse result) {
                    Log.d("callRfiListApi", "callRfiListApi success: ");
                    if (isRfiContactListApiDone)
                        CustomProgressBar.dissMissDialog(RfiListActivity.this);
                    isRfiApiDone = true;
                    loadRfiList(isRfiContactListApiDone && isRfiApiDone);
                }

                @Override
                public void AccessTokenFailure(String message) {
                    CustomProgressBar.dissMissDialog(RfiListActivity.this);
                    noRecordTextView.setText("");
                    // isLoading = false;
                    handleAccessTokenFails();
                }

                @Override
                public void failure(String message) {
                    CustomProgressBar.dissMissDialog(RfiListActivity.this);
                    noRecordTextView.setText("");
                    loadRfiList(true);
                }
            });
        } else {
            CustomProgressBar.dissMissDialog(RfiListActivity.this);
            loadRfiList(true);
            //TODO show records form DB here
        }

    }

    private void loadRfiList(boolean loadList) {
        if (loadList) {
            CustomProgressBar.dissMissDialog(RfiListActivity.this);
            noRecordTextView.setVisibility(View.GONE);
            List<PjRfi> rfiList1 = projectRfiRepository.getSearchRfiFilterList(projectId, searchEditText.getText().toString(),
                    mDueDate, mSubmittedDate, filterAssignToContact, mRFIStatusEnum);
            rfiList.clear();
            Log.d("RFIListActivity", "loadRfiList: " + rfiList1);
            if (rfiList1 != null && rfiList1.size() > 0) {
                for (PjRfi pjRfi : rfiList1) {
                    PjRfiContactList contactList = projectRfiRepository.getSearchPjRfiAssignToContact(pjRfi);
                    RfiListItem item = new RfiListItem();
                    item.setPjRfi(pjRfi);
                    item.setPjRfiContactList(contactList);
                    rfiList.add(item);
                }
                rfiListAdapter.setRfiList(rfiList);
                rfiListAdapter.notifyDataSetChanged();
            } else {

                if (NetworkService.isNetworkAvailable(RfiListActivity.this))
                    noRecordTextView.setText(getString(R.string.no_results));
                else {
                    if (TextUtils.isEmpty(searchEditText.getText().toString()))
                        noRecordTextView.setText(getString(R.string.no_rfi_offline_message));
                    else {
                        noRecordTextView.setText(getString(R.string.no_results));
                    }

                }
                noRecordTextView.setVisibility(View.VISIBLE);
            }
            CustomProgressBar.dissMissDialog(RfiListActivity.this);
        }

    }

    private void callContactList(boolean showProgress) {

        if (NetworkService.isNetworkAvailable(RfiListActivity.this)) {
            if (showProgress)
                CustomProgressBar.showDialog(RfiListActivity.this);
            projectsProvider.getProjectRfiContactList(projectId, new ProviderResult<RfiContactListResponse>() {
                @Override
                public void success(RfiContactListResponse result) {
                    Log.d("callRfiListApi", "callRfiListApi success: ");
                    if (isRfiApiDone)
                        CustomProgressBar.dissMissDialog(RfiListActivity.this);
                    isRfiContactListApiDone = true;
                    loadRfiList(isRfiContactListApiDone && isRfiApiDone);
                }

                @Override
                public void AccessTokenFailure(String message) {
                    CustomProgressBar.dissMissDialog(RfiListActivity.this);
                    noRecordTextView.setText("");
                    // isLoading = false;
                    handleAccessTokenFails();
                }

                @Override
                public void failure(String message) {
                    CustomProgressBar.dissMissDialog(RfiListActivity.this);
                    noRecordTextView.setText("");
                    loadRfiList(true);
                }
            });
        } else {
            loadRfiList(true);
        }

    }

    private void setSearchTextWatcher() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // mAlbumList.addAll(photoFolders);
                // mAlbumAdapter.notifyDataSetChanged();
                //loadFolderFilesList();
                if (TextUtils.isEmpty(s.toString())) {
                    searchClearImageView.setVisibility(View.INVISIBLE);
                } else {
                    searchClearImageView.setVisibility(View.VISIBLE);
                }

                loadRfiList(true);
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
        switch (v.getId()) {
            case R.id.leftImageView:
                onBackPressed();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RfiListFilterEvent rfiListFilterEvent) {
        Log.d("RfiListActivity", "onEvent: " + rfiListFilterEvent);

        mRFIStatusEnum = rfiListFilterEvent.getRfiStatusEnum();
        mDueDate = rfiListFilterEvent.getDueDate();
        mSubmittedDate = rfiListFilterEvent.getSubmittedDate();
        filterAssignToContact = rfiListFilterEvent.getPjRfiContactList();
        setFilterCount();
       /* List<PjRfi> filteredRfiList = projectRfiRepository.getSearchRfiFilterList(projectId, searchEditText.getText().toString(),
                mDueDate, mSubmittedDate ,filterAssignToContact, mRFIStatusEnum);*/
        loadRfiList(true);


    }

    private void setFilterCount() {
        int filterCount = 0;
        if (mRFIStatusEnum != RFIStatusEnum.All) {
            filterCount = filterCount + 1;
        }

        if (filterAssignToContact != null && filterAssignToContact.getPjRfiContactListId() != -1) {
            filterCount = filterCount + 1;
        } else {
            filterAssignToContact = null;
        }
        if (mDueDate != null) {
            filterCount = filterCount + 1;
        }
        if (mSubmittedDate != null) {
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
            callRfiListApi(true);
            callContactList(true);
        }

    }

    @Override
    protected int doGetContentView() {
        return R.layout.activity_rfi_list;
    }
}
