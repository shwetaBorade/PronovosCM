package com.pronovoscm.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pronovoscm.R;
import com.pronovoscm.adapter.TransferLogsAdapter;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.data.TransferLogProvider;
import com.pronovoscm.model.request.transferlog.TransferLogRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.transferlog.Logs;
import com.pronovoscm.model.response.transferlog.TransferLogResponse;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.ui.CustomProgressBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class TransferLogActivity extends BaseActivity {
    private static final int GET_FILTERS = 943;
    @Inject
    TransferLogProvider transferLogProvider;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.filterTextView)
    TextView filterTextView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.logRecyclerView)
    RecyclerView logRecyclerView;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
    boolean isLoading = false;
    int filterCount = 0;
    private int projectId;
    private LoginResponse loginResponse;
    private PopupWindow mPopupWindow;
    private int pageNo = 1;
    private TransferLogRequest transferLogRequest;
    private ArrayList<Logs> transferLogs;
    private TransferLogsAdapter transferLogsAdapter;
    private boolean hasMoreTransfers = false;
    private String pickUpFromDate;
    private String pickUpToDate;
    private String dropOffFromDate;
    private String dropOffToDate;
    private int pickupLocation = -1;
    private int dropOffLocation = -1;

    @Override
    protected int doGetContentView() {
        return R.layout.transfer_log_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        projectId = getIntent().getIntExtra("project_id", 0);

        transferLogs = new ArrayList<>();
        logRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transferLogsAdapter = new TransferLogsAdapter(this, transferLogs);
        logRecyclerView.setAdapter(transferLogsAdapter);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.VISIBLE);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        titleTextView.setText(getString(R.string.transfer_log));
        CustomProgressBar.showDialog(this);
        noRecordTextView.setText("Loading Transfer Log");
        callTransferLogAPI();
        logRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading && hasMoreTransfers) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == transferLogs.size() - 1) {
                        isLoading = true;
                        pageNo = pageNo + 1;
                        noRecordTextView.setText("");
                        callTransferLogAPI();

                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    /**
     * Get List
     */
    public void callTransferLogAPI() {
        transferLogRequest = new TransferLogRequest();
        transferLogRequest.setProjectId(projectId);
        transferLogRequest.setPage(pageNo);
        transferLogRequest.setDropoffDateFrom(dropOffFromDate);
        transferLogRequest.setPickupDateFrom(pickUpFromDate);
        transferLogRequest.setPickupDateTo(pickUpToDate);
        transferLogRequest.setDropoffDateTo(dropOffToDate);
        transferLogRequest.setPickupLocation(pickupLocation);
        transferLogRequest.setDropoffLocation(dropOffLocation);

        filterCount = 0;
        if (!TextUtils.isEmpty(dropOffFromDate)) {
            filterCount = filterCount + 1;
        }
        if (!TextUtils.isEmpty(pickUpFromDate)) {
            filterCount = filterCount + 1;
        }
        if (!TextUtils.isEmpty(pickUpToDate)) {
            filterCount = filterCount + 1;
        }
        if (!TextUtils.isEmpty(dropOffToDate)) {
            filterCount = filterCount + 1;
        }
        if (pickupLocation != -1) {
            filterCount = filterCount + 1;
        }
        if (dropOffLocation != -1) {
            filterCount = filterCount + 1;
        }

        if (filterCount > 0) {
            filterTextView.setText(String.valueOf(filterCount));
            filterTextView.setVisibility(View.VISIBLE);
        } else {
            filterTextView.setVisibility(View.GONE);
        }

        transferLogProvider.getTransferLogs(transferLogRequest, new ProviderResult<TransferLogResponse>() {
            @Override
            public void success(TransferLogResponse transferLogResponse) {
                hasMoreTransfers = transferLogResponse.getData().getHastransfers();
                noRecordTextView.setText("");
                CustomProgressBar.dissMissDialog(TransferLogActivity.this);
                if (transferLogs.size() > 1) {
                    transferLogs.remove(transferLogs.size() - 1);
                }
                transferLogs.addAll(transferLogResponse.getData().getLogs());
                if (hasMoreTransfers && transferLogs.get(transferLogs.size() - 1) != null) {
                    transferLogs.add(null);
                }
                transferLogsAdapter.notifyDataSetChanged();
                isLoading = false;
                if (transferLogs == null || transferLogs.size() == 0) {
                if (filterCount==0){
                    noRecordTextView.setText("Transfers have not yet been completed for this project.");
                }else{
                    noRecordTextView.setText("There are no completed transfers for this filter criteria.");

                }
                }
                if (filterCount == 0 && (transferLogs == null || transferLogs.size() == 0)) {
                    rightImageView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(TransferLogActivity.this);
                startActivity(new Intent(TransferLogActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(TransferLogActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(TransferLogActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();

                if (pageNo > 1) {
                    pageNo = pageNo - 1;
                }
                isLoading = false;
            }

            @Override
            public void failure(String message) {
                isLoading = false;
                if (pageNo > 1) {
                    pageNo = pageNo - 1;
                }
                CustomProgressBar.dissMissDialog(TransferLogActivity.this);
                showMessageAlert(TransferLogActivity.this, message, getString(R.string.ok));

            }
        }, loginResponse);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            if (transferLogs.size() > 1) {
                transferLogs.remove(transferLogs.size() - 1);
            }
            transferLogsAdapter.notifyDataSetChanged();
            offlineTextView.setVisibility(View.VISIBLE);
        } else {
            if (hasMoreTransfers && transferLogs.size() > 1 && transferLogs.get(transferLogs.size() - 1) != null) {
                transferLogs.add(null);
            }
            transferLogsAdapter.notifyDataSetChanged();

            offlineTextView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @OnClick(R.id.leftImageView)
    public void onBackClick() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case GET_FILTERS:
                    noRecordTextView.setText("");
                    String pUFDate = pickUpFromDate;
                    String pUTDate = pickUpToDate;
                    String dOTDate = dropOffToDate;
                    String dOFDate = dropOffToDate;
                    int pickupL = pickupLocation;
                    int dropoffL = dropOffLocation;
                    pickUpFromDate = data.getStringExtra("pickup_from_date");
                    pickUpToDate = data.getStringExtra("pickup_to_date");
                    dropOffToDate = data.getStringExtra("dropoff_to_date");
                    dropOffFromDate = data.getStringExtra("dropoff_from_date");
                    pickupLocation = data.getIntExtra("pickup_loc_id", -1);
                    dropOffLocation = data.getIntExtra("dropoff_loc_id", -1);
                    pickUpFromDate = TextUtils.isEmpty(pickUpFromDate) ? null : pickUpFromDate;
                    pickUpToDate = TextUtils.isEmpty(pickUpToDate) ? null : pickUpToDate;
                    dropOffFromDate = TextUtils.isEmpty(dropOffFromDate) ? null : dropOffFromDate;
                    dropOffToDate = TextUtils.isEmpty(dropOffToDate) ? null : dropOffToDate;
                    if (pickupL != pickupLocation || dropoffL != dropOffLocation || !compareTwoStrings(pickUpFromDate, pUFDate) || !compareTwoStrings(pickUpToDate, pUTDate) || !compareTwoStrings(dropOffFromDate, dOFDate) || !compareTwoStrings(dropOffToDate, dOTDate)) {
                        transferLogs.clear();
                        pageNo = 1;
                        transferLogsAdapter.notifyDataSetChanged();
                        CustomProgressBar.showDialog(this);
                        callTransferLogAPI();
                    }
                    break;
            }
        }
    }

    private boolean compareTwoStrings(String str1, String str2) {
        if ((str1 != null ? str1 : "").equals(str2 != null ? str2 : "")) {
            return true;
        }
        return false;
    }

    @OnClick(R.id.rightImageView)
    public void onFilterClick() {
        if(NetworkService.isNetworkAvailable(this)){
            startActivityForResult(new Intent(this, TransferLogFilterActivity.class)
                    .putExtra("project_id", projectId)
                    .putExtra("pickup_loc_id", pickupLocation)
                    .putExtra("dropoff_loc_id", dropOffLocation)
                    .putExtra("pickup_from_date", pickUpFromDate)
                    .putExtra("pickup_to_date", pickUpToDate)
                    .putExtra("dropoff_to_date", dropOffToDate)
                    .putExtra("dropoff_from_date", dropOffFromDate), GET_FILTERS);
        }else{
            showMessageAlert(this,getString(R.string.internet_connection_check),getString(R.string.ok));
        }


    }
}
