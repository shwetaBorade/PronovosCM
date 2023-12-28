package com.pronovoscm.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pronovoscm.R;
import com.pronovoscm.adapter.TransferLogPagerAdapter;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.data.TransferLogProvider;
import com.pronovoscm.fragments.TransferLogDropoffFragment;
import com.pronovoscm.fragments.TransferLogEquipmentFragment;
import com.pronovoscm.fragments.TransferLogInfoFragment;
import com.pronovoscm.fragments.TransferLogPickupFragment;
import com.pronovoscm.model.request.transferlog.TransferLogDetailRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.transferlogdetails.Details;
import com.pronovoscm.model.response.transferlogdetails.TransferLogDetailResponse;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.ui.CustomProgressBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class TransferLogDetailsActivity extends BaseActivity {
    @Inject
    TransferLogProvider transferLogProvider;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.lineView)
    View lineView;
    private int transferId;
    private LoginResponse loginResponse;
    private TransferLogDetailRequest transferLogDetailRequest;
    private TransferLogPagerAdapter mSectionsPagerAdapter;
    private TransferLogDetailResponse mTransferLogResponse;

    @Override
    protected int doGetContentView() {
        return R.layout.transfer_log_detail_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        lineView.setVisibility(View.GONE);
        transferId = getIntent().getIntExtra("transfer_id", 0);

        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.INVISIBLE);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        titleTextView.setText("Transfer #" + transferId);
        transferLogDetailRequest = new TransferLogDetailRequest();
        transferLogDetailRequest.setTransferId(transferId);
        setupViewPager(null);
        callTransferLogDetailsAPI();

    }

    @Override
    public void onResume() {
        super.onResume();


    }

    /**
     * Get Detail of Transfer
     */
    public void callTransferLogDetailsAPI() {
        CustomProgressBar.showDialog(this);
        transferLogProvider.getTransferLogDetailRequest(transferLogDetailRequest, new ProviderResult<TransferLogDetailResponse>() {
            @Override
            public void success(TransferLogDetailResponse transferLogResponse) {
                CustomProgressBar.dissMissDialog(TransferLogDetailsActivity.this);
                mTransferLogResponse = transferLogResponse;
                setupViewPager(mTransferLogResponse.getData().getDetails());
            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(TransferLogDetailsActivity.this);
                startActivity(new Intent(TransferLogDetailsActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(TransferLogDetailsActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(TransferLogDetailsActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();

            }

            @Override
            public void failure(String message) {

                CustomProgressBar.dissMissDialog(TransferLogDetailsActivity.this);
                showMessageAlert(TransferLogDetailsActivity.this, message, getString(R.string.ok));

            }
        }, loginResponse);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
        } else {

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

    public void setupViewPager(Details details) {
        viewPager.removeAllViews();
        tabLayout.removeAllTabs();
        mSectionsPagerAdapter = new TransferLogPagerAdapter(getSupportFragmentManager());
        lineView.setVisibility(View.VISIBLE);
        TabLayout.Tab pickUpTab = tabLayout.newTab();
        pickUpTab.setText("   Pick-up info   ");
        tabLayout.addTab(pickUpTab);
        TransferLogPickupFragment transferLogPickupFragment = new TransferLogPickupFragment(details);
        mSectionsPagerAdapter.addFragment(transferLogPickupFragment, "Pick-up info");
        TabLayout.Tab dropOffTab = tabLayout.newTab();
        dropOffTab.setText("   Drop-off info   ");
        tabLayout.addTab(dropOffTab);
        TransferLogDropoffFragment transferLogDropoffFragment = new TransferLogDropoffFragment(details);
        mSectionsPagerAdapter.addFragment(transferLogDropoffFragment, "Drop-off info");
        TabLayout.Tab equipmentTab = tabLayout.newTab();
        equipmentTab.setText("   Equipment   ");
        tabLayout.addTab(equipmentTab);
        TransferLogEquipmentFragment transferLogEquipmentFragment = new TransferLogEquipmentFragment(details);
        mSectionsPagerAdapter.addFragment(transferLogEquipmentFragment, "Equipment");
        TabLayout.Tab infoTab = tabLayout.newTab();
        infoTab.setText("   Info   ");
        tabLayout.addTab(infoTab);
        TransferLogInfoFragment transferLogInfoFragment = new TransferLogInfoFragment(details);
        mSectionsPagerAdapter.addFragment(transferLogInfoFragment, "Info");

        //mSectionsPagerAdapter.notifyDataSetChanged();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (mSectionsPagerAdapter != null && mSectionsPagerAdapter.getCurrentFragment() != null) {
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setAdapter(mSectionsPagerAdapter);
        //viewPager.setOffscreenPageLimit(equipmentSubCategoriesMasterList.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                tabLayout.getTabAt(i).select();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mSectionsPagerAdapter.notifyDataSetChanged();

    }


}
