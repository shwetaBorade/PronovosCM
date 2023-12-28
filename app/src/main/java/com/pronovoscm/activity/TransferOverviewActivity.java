package com.pronovoscm.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pronovoscm.R;
import com.pronovoscm.adapter.AddTransferOptionAdapter;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.data.TransferOverviewProvider;
import com.pronovoscm.fragments.TransferOverviewFragment;
import com.pronovoscm.model.request.transferoverview.TransferOverviewRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.transferoverviewcount.TransferOverviewCountResponse;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.ui.CustomProgressBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class TransferOverviewActivity extends BaseActivity implements AddTransferOptionAdapter.selectOption {
    @Inject
    TransferOverviewProvider transferOverviewProvider;

    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.addImageView)
    ImageView addImageView;
    @BindView(R.id.addView)
    ImageView addView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    private int projectId;
    private LoginResponse loginResponse;
    private TransferOverviewFragment transferOverviewFragment;
    private PopupWindow mPopupWindow;
    private AddTransferOptionAdapter mAddTransferAdapter;
    private TransferOverviewCountResponse mTransferOverviewResponse;

    @Override
    protected int doGetContentView() {
        return R.layout.transfer_overview_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        projectId = getIntent().getIntExtra("project_id", 0);
        backImageView.setImageResource(R.drawable.ic_arrow_back);        rightImageView.setVisibility(View.GONE);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        addView.setVisibility(View.VISIBLE);
        if (loginResponse.getUserDetails().getPermissions().get(0).getCreateTransfer() == 1 || loginResponse.getUserDetails().getPermissions().get(0).getCreateRequest() == 1) {
            addImageView.setVisibility(View.VISIBLE);
        } else {
            addImageView.setVisibility(View.INVISIBLE);
        }
        titleTextView.setText(getString(R.string.transfer_overview));
        loadTransferOverview();
    }

    @Override
    public void onResume() {
        super.onResume();
        CustomProgressBar.showDialog(this);
        if (transferOverviewFragment != null) {
            transferOverviewFragment.removeAll();
        }
        callTransferOverviewAPI();

    }

    /**
     * Get List
     */
    public void callTransferOverviewAPI() {
        TransferOverviewRequest transferOverviewRequest = new TransferOverviewRequest();
        transferOverviewRequest.setProjectId(projectId);
        transferOverviewProvider.getTransferOverview(transferOverviewRequest, new ProviderResult<TransferOverviewCountResponse>() {
            @Override
            public void success(TransferOverviewCountResponse transferOverviewResponse) {
                mTransferOverviewResponse = transferOverviewResponse;
                transferOverviewFragment.updateResponse(mTransferOverviewResponse);
                CustomProgressBar.dissMissDialog(TransferOverviewActivity.this);

            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(TransferOverviewActivity.this);
                startActivity(new Intent(TransferOverviewActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(TransferOverviewActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(TransferOverviewActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                if (transferOverviewFragment != null) {
                    transferOverviewFragment.removeLoadingText();
                }
                CustomProgressBar.dissMissDialog(TransferOverviewActivity.this);
                showMessageAlert(TransferOverviewActivity.this, message, getString(R.string.ok));

            }
        }, loginResponse);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            addImageView.setVisibility(View.INVISIBLE);
            offlineTextView.setVisibility(View.VISIBLE);
        } else {
            if (loginResponse.getUserDetails().getPermissions().get(0).getCreateTransfer() == 1 || loginResponse.getUserDetails().getPermissions().get(0).getCreateRequest() == 1) {
                addImageView.setVisibility(View.VISIBLE);
                addImageView.setClickable(true);
            }
            offlineTextView.setVisibility(View.GONE);
        }

        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    public void loadTransferOverview() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        transferOverviewFragment = new TransferOverviewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("projectId", projectId);
        transferOverviewFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.listContainer, transferOverviewFragment, transferOverviewFragment.getClass().getSimpleName());
        try {
            fragmentTransaction.commit();
        } catch (IllegalStateException e) {

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

    @OnClick(R.id.addImageView)
    public void onAddClick() {
        LayoutInflater inflater = (LayoutInflater) TransferOverviewActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.regions_popup_view, null);
        mPopupWindow = new PopupWindow(
                customView,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        RecyclerView recyclerView = customView.findViewById(R.id.regionsRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        ArrayList<String> optionList = new ArrayList<>();
        if (loginResponse.getUserDetails().getPermissions().get(0).getCreateTransfer() == 1) {
            optionList.add("New Transfer");
        }
        if (loginResponse.getUserDetails().getPermissions().get(0).getCreateRequest() == 1) {
            optionList.add("New Request");
        }
        mAddTransferAdapter = new AddTransferOptionAdapter(TransferOverviewActivity.this, optionList);
        recyclerView.setAdapter(mAddTransferAdapter);
        int[] loc_int = new int[2];

        try {
            addView.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.

        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + addImageView.getWidth();
        location.bottom = location.top + addImageView.getHeight();
        if (mPopupWindow != null) {
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setFocusable(true);
            mPopupWindow.showAtLocation(addView, Gravity.TOP | Gravity.RIGHT, 0, location.top + addView.getHeight());
        }

    }

    @Override
    public void onSelectOption(String option) {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        if (!NetworkService.isNetworkAvailable(this)) {
            showMessageAlert(TransferOverviewActivity.this, getString(R.string.internet_connection_check), getString(R.string.ok));
            return;
        }
        startActivity(new Intent(this, CreateTransfersActivity.class).putExtra("project_id", projectId)
                .putExtra("transfer_option", option));
    }
}
