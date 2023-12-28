package com.pronovoscm.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.pronovoscm.R;
import com.pronovoscm.adapter.AddTransferOptionAdapter;
import com.pronovoscm.adapter.TransferOverviewPagerAdapter;
import com.pronovoscm.data.InventoryProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.data.TransferOverviewProvider;
import com.pronovoscm.fragments.TransferOverviewDetailFragment;
import com.pronovoscm.model.request.inventory.InventoryRequest;
import com.pronovoscm.model.request.transferoverview.TransferOverviewRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.transferoverview.Data;
import com.pronovoscm.model.response.transferoverview.TransferOverviewResponse;
import com.pronovoscm.model.response.transferoverview.Transfers;
import com.pronovoscm.persistence.domain.EquipmentCategoriesMaster;
import com.pronovoscm.persistence.domain.EquipmentInventory;
import com.pronovoscm.persistence.domain.EquipmentRegion;
import com.pronovoscm.persistence.domain.EquipmentSubCategoriesMaster;
import com.pronovoscm.persistence.repository.EquipementInventoryRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.ui.CustomProgressBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

//import com.pronovoscm.adapter.EquipmentCategoryPopupAdapter;

public class TransferOverviewDetailsActivity extends BaseActivity implements AddTransferOptionAdapter.selectOption {
    @Inject
    TransferOverviewProvider transferOverviewProvider;
    @Inject
    EquipementInventoryRepository mEquipementInventoryRepository;
    @Inject
    InventoryProvider mInventoryProvider;

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
    @BindView(R.id.addImageView)
    ImageView addImageView;
    @BindView(R.id.addView)
    ImageView addView;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;

    int selectTab = -1;
    private List<Transfers> equipmentSubCategoriesMasterList;
    private String transferTitle;
    private LoginResponse loginResponse;
    private TransferOverviewPagerAdapter mSectionsPagerAdapter;
    private Data transferOverview;
    private PopupWindow mPopupWindow;
    private AddTransferOptionAdapter mAddTransferAdapter;
    private boolean isLoading;

    @Override
    protected int doGetContentView() {
        return R.layout.transfer_detail_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        transferTitle = getIntent().getStringExtra("transfer_title");
        rightImageView.setVisibility(View.GONE);
        addView.setVisibility(View.VISIBLE);
        if (loginResponse.getUserDetails().getPermissions().get(0).getCreateTransfer() == 1 || loginResponse.getUserDetails().getPermissions().get(0).getCreateRequest() == 1) {
            addImageView.setVisibility(View.VISIBLE);
        } else {
            addImageView.setVisibility(View.INVISIBLE);
        }
        titleTextView.setText(getString(R.string.transfer_overview));

        backImageView.setImageResource(R.drawable.ic_arrow_back);
        noRecordTextView.setText("Loading Transfer Overview");
        noRecordTextView.setVisibility(View.GONE);
//        callTransferOverviewAPI();
        if (!mEquipementInventoryRepository.hasEquipmentInventoryList(getIntent().getIntExtra("project_id", 0))) {
            callCategoriesAPI();
        }
        setupViewPager();
    }


    public void setupViewPager() {
        viewPager.removeAllViews();
        tabLayout.removeAllTabs();
        mSectionsPagerAdapter = new TransferOverviewPagerAdapter(getSupportFragmentManager());
      /*  for (int i = 0; i < equipmentSubCategoriesMasterList.size(); i++) {
            lineView.setVisibility(View.VISIBLE);
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setText("   " + equipmentSubCategoriesMasterList.get(i).getTitle() + "   ");
            if (transferTitle != null && equipmentSubCategoriesMasterList.get(i).getTitle().equals(transferTitle) && selectTab == -1) {
                selectTab = i;
            }
            tabLayout.addTab(tab);
            TransferOverviewDetailFragment equipmentDetailFragment = new TransferOverviewDetailFragment(equipmentSubCategoriesMasterList.get(i), equipmentSubCategoriesMasterList.get(i).getTitle());
            mSectionsPagerAdapter.addFragment(equipmentDetailFragment, equipmentSubCategoriesMasterList.get(i).getTitle());
        }
*/

        lineView.setVisibility(View.VISIBLE);
        TabLayout.Tab tab = tabLayout.newTab();
        tab.setText("   Drafts   ");
        tabLayout.addTab(tab);
        TransferOverviewDetailFragment equipmentDetailFragment = new TransferOverviewDetailFragment(0);
        mSectionsPagerAdapter.addFragment(equipmentDetailFragment, "Drafts");

        TabLayout.Tab tab1 = tabLayout.newTab();
        tab1.setText("   Awaiting Approval   ");
        tabLayout.addTab(tab1);
        TransferOverviewDetailFragment equipmentDetailFragment1 = new TransferOverviewDetailFragment(1);
        mSectionsPagerAdapter.addFragment(equipmentDetailFragment1, "Awaiting Approval");

        TabLayout.Tab tab2 = tabLayout.newTab();
        tab2.setText("   Scheduled Transfers   ");
        tabLayout.addTab(tab2);
        TransferOverviewDetailFragment equipmentDetailFragment2 = new TransferOverviewDetailFragment(2);
        mSectionsPagerAdapter.addFragment(equipmentDetailFragment2, "Drafts");

        TabLayout.Tab tab3 = tabLayout.newTab();
        tab3.setText("   Transfers Pending Approval   ");
        tabLayout.addTab(tab3);
        TransferOverviewDetailFragment equipmentDetailFragment3 = new TransferOverviewDetailFragment(3);
        mSectionsPagerAdapter.addFragment(equipmentDetailFragment3, "Drafts");

        TabLayout.Tab tab4 = tabLayout.newTab();
        tab4.setText("   Transfers Pending Reconciliation   ");
        tabLayout.addTab(tab4);
        TransferOverviewDetailFragment equipmentDetailFragment4 = new TransferOverviewDetailFragment(4);
        mSectionsPagerAdapter.addFragment(equipmentDetailFragment4, "Drafts");


        if (selectTab == -1) {
            selectTab = getIntent().getIntExtra("selected_tab", 0);
        }
        if (selectTab == -1) {
            selectTab = 0;
        }
        //mSectionsPagerAdapter.notifyDataSetChanged();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (mSectionsPagerAdapter != null && mSectionsPagerAdapter.getCurrentFragment() != null) {
                    mSectionsPagerAdapter.setCurrentFragment(selectTab);
                    selectTab = i;
                    callTransferOverviewAPI(i);
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
                mSectionsPagerAdapter.setCurrentFragment(tab.getPosition());
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
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        tabLayout.setScrollPosition(selectTab, 0f, true);
                        mSectionsPagerAdapter.setCurrentFragment(selectTab);
                        callTransferOverviewAPI(selectTab);
                    }
                }, 50);
        viewPager.setCurrentItem(selectTab);
        mSectionsPagerAdapter.setCurrentFragment(selectTab);

    }


    /**
     * Get List
     */
    private void callTransferOverviewAPI(int status) {
        isLoading = true;
        ((TransferOverviewDetailFragment) mSectionsPagerAdapter.getCurrentFragment()).loadingText();
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        CustomProgressBar.showDialog(this);
        TransferOverviewRequest transferOverviewRequest = new TransferOverviewRequest();
        transferOverviewRequest.setProjectId(getIntent().getIntExtra("project_id", 0));
        transferOverviewRequest.setStatus(status);
        transferOverviewProvider.getTransferOverviews(transferOverviewRequest, new ProviderResult<TransferOverviewResponse>() {
            @Override
            public void success(TransferOverviewResponse transferOverviewResponse) {
                noRecordTextView.setText("");
                noRecordTextView.setVisibility(View.GONE);
                if (viewPager.getCurrentItem() == status) {
                    isLoading = false;
                }
                ((TransferOverviewDetailFragment) mSectionsPagerAdapter.getCurrentFragment()).updateData(transferOverviewResponse);
                CustomProgressBar.dissMissDialog(TransferOverviewDetailsActivity.this);
            }

            @Override
            public void AccessTokenFailure(String message) {
                noRecordTextView.setText("");
                isLoading = false;
                noRecordTextView.setVisibility(View.GONE);
                ((TransferOverviewDetailFragment) mSectionsPagerAdapter.getCurrentFragment()).loadingFail();
                CustomProgressBar.dissMissDialog(TransferOverviewDetailsActivity.this);
                startActivity(new Intent(TransferOverviewDetailsActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(TransferOverviewDetailsActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(TransferOverviewDetailsActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                TransferOverviewDetailsActivity.this.finish();
            }

            @Override
            public void failure(String message) {
                noRecordTextView.setText("");
                noRecordTextView.setVisibility(View.GONE);
                isLoading = false;
                CustomProgressBar.dissMissDialog(TransferOverviewDetailsActivity.this);
                showMessageAlert(TransferOverviewDetailsActivity.this, message, getString(R.string.ok));

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

        if (mSectionsPagerAdapter != null && mSectionsPagerAdapter.getCurrentFragment() != null && mSectionsPagerAdapter.getCurrentFragment() instanceof TransferOverviewDetailFragment) {
            ((TransferOverviewDetailFragment) mSectionsPagerAdapter.getCurrentFragment()).setOffline(event);
        }
    }

    @OnClick(R.id.leftImageView)
    public void onBackClick() {
        super.onBackPressed();
    }

    @OnClick(R.id.addImageView)
    public void onAddClick() {

        LayoutInflater inflater = (LayoutInflater) TransferOverviewDetailsActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
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
        mAddTransferAdapter = new AddTransferOptionAdapter(TransferOverviewDetailsActivity.this, optionList);
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
        location.right = location.left + addView.getWidth();
        location.bottom = location.top + addView.getHeight();
        if (mPopupWindow != null) {
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setFocusable(true);
            mPopupWindow.showAtLocation(addView, Gravity.TOP | Gravity.RIGHT, 0, location.top + addView.getHeight());
        }

    }

    @Override
    public void onSelectOption(String option) {
//        if (option.equalsIgnoreCase("New Request")) {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        if (!NetworkService.isNetworkAvailable(this)) {
            showMessageAlert(TransferOverviewDetailsActivity.this, getString(R.string.internet_connection_check), getString(R.string.ok));
            return;
        }
        startActivity(new Intent(this, CreateTransfersActivity.class)
                .putExtra("selected_tab", selectTab).putExtra("project_id", getIntent().getIntExtra("project_id", 0))
                .putExtra("transfer_option", option).putExtra("open_details", true));
//        }
        this.finish();

    }

    public void callCategoriesAPI() {

        mInventoryProvider.getCategories(getIntent().getIntExtra("project_id", 0), new ProviderResult<List<EquipmentCategoriesMaster>>() {
            @Override
            public void success(List<EquipmentCategoriesMaster> result) {
                mInventoryProvider.getSubCategories(new ProviderResult<List<EquipmentSubCategoriesMaster>>() {
                    @Override
                    public void success(List<EquipmentSubCategoriesMaster> result) {
                        mInventoryProvider.getEquipmentDetails(new ProviderResult<List<EquipmentRegion>>() {
                            @Override
                            public void success(List<EquipmentRegion> result) {
                                InventoryRequest inventoryRequest = new InventoryRequest();
                                inventoryRequest.setProjectId(getIntent().getIntExtra("project_id", 0));
                                mInventoryProvider.getInventory(inventoryRequest, new ProviderResult<List<EquipmentInventory>>() {
                                    @Override
                                    public void success(List<EquipmentInventory> result) {

                                    }

                                    @Override
                                    public void AccessTokenFailure(String message) {
                                    }

                                    @Override
                                    public void failure(String message) {

                                    }
                                }, loginResponse);
                            }

                            @Override
                            public void AccessTokenFailure(String message) {
                            }

                            @Override
                            public void failure(String message) {

                            }
                        }, loginResponse);
                    }

                    @Override
                    public void AccessTokenFailure(String message) {

                    }

                    @Override
                    public void failure(String message) {

                    }
                }, loginResponse);
            }

            @Override
            public void AccessTokenFailure(String message) {
            }

            @Override
            public void failure(String message) {

            }
        }, loginResponse);
    }

    public void clearAndRefresh() {
        viewPager.removeAllViews();
        tabLayout.removeAllTabs();
        setupViewPager();
//        callTransferOverviewAPI();
    }

    public int getSelectTab() {
        return selectTab;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isLoading() {
        return isLoading;
    }
}
