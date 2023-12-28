package com.pronovoscm.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pronovoscm.R;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.login.UserPermissions;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.SharedPref;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

public class EquipmentActivity extends BaseActivity {

    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.transferLogArrow)
    ImageView transferLogArrow;
    @BindView(R.id.transferOverviewArrow)
    ImageView transferOverviewArrow;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.transferOverviewTextView)
    TextView transferOverviewTextView;
    @BindView(R.id.transferLogTextView)
    TextView transferLogTextView;
    @BindView(R.id.offlineTwoTextView)
    TextView offlineTwoTextView;
    @BindView(R.id.inventoryView)
    CardView inventoryView;
    @BindView(R.id.transferLogCardView)
    CardView transferLogCardView;
    @BindView(R.id.transferOverviewView)
    CardView transferOverviewView;
    private int projectId;
    private LoginResponse loginResponse;

    @Override
    protected int doGetContentView() {
        return R.layout.equipment_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        projectId = getIntent().getIntExtra("project_id", 0);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.INVISIBLE);
        titleTextView.setText(getString(R.string.equipment));
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        UserPermissions userPermissions = loginResponse.getUserDetails().getPermissions().get(0);
        if (userPermissions.getViewProjectInventory() == 1) {
            inventoryView.setVisibility(View.VISIBLE);
        } else {
            inventoryView.setVisibility(View.GONE);
        }
        if (userPermissions.getViewProjectTransfers() == 1) {
            transferOverviewView.setVisibility(View.VISIBLE);
            transferLogCardView.setVisibility(View.VISIBLE);
        } else {
            transferLogCardView.setVisibility(View.GONE);
            transferOverviewView.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
            /*UserPermissions userPermissions = loginResponse.getUserDetails().getPermissions().get(0);
            if (userPermissions.getViewProjectTransfers() == 1) {
            */
            offlineTwoTextView.setVisibility(View.VISIBLE);
//            }
            transferLogCardView.setClickable(false);
            transferOverviewView.setClickable(false);
            transferLogTextView.setTextColor(ContextCompat.getColor(this, R.color.disable_gray));
            transferOverviewTextView.setTextColor(ContextCompat.getColor(this, R.color.disable_gray));
            transferLogArrow.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_right_arrow_disable));
            transferOverviewArrow.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_right_arrow_disable));
        } else {
            offlineTwoTextView.setVisibility(View.GONE);
            offlineTextView.setVisibility(View.GONE);
            transferLogCardView.setClickable(true);
            transferOverviewView.setClickable(true);
            transferLogTextView.setTextColor(ContextCompat.getColor(this, R.color.gray_4a4a4a));
            transferOverviewTextView.setTextColor(ContextCompat.getColor(this, R.color.gray_4a4a4a));
            transferLogArrow.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_right_arrow));
            transferOverviewArrow.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_right_arrow));
        }
    }

    @OnClick(R.id.leftImageView)
    public void onBackClick() {
        super.onBackPressed();
    }

    @OnClick(R.id.inventoryView)
    public void onInventoryViewClick() {
        inventoryView.setClickable(false);
        startActivity(new Intent(this, InventoryActivity.class).putExtra("project_id", projectId));
    }

    @OnClick(R.id.transferOverviewView)
    public void onTransferOverviewViewClick() {
        transferOverviewView.setClickable(false);
        startActivity(new Intent(this, TransferOverviewActivity.class).putExtra("project_id", projectId));
    }

    @OnClick(R.id.transferLogCardView)
    public void onTransferLogViewClick() {
        transferLogCardView.setClickable(false);
        startActivity(new Intent(this, TransferLogActivity.class).putExtra("project_id", projectId));
    }

    @Override
    public void onResume() {
        super.onResume();
        inventoryView.setClickable(true);
        if (NetworkService.isNetworkAvailable(this)) {
            transferLogCardView.setClickable(true);
            transferOverviewView.setClickable(true);
        }
    }
}
