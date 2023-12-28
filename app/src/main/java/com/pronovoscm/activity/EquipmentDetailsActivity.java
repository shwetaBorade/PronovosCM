package com.pronovoscm.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pronovoscm.R;
import com.pronovoscm.adapter.EquipmentCategoryPopupAdapter;
import com.pronovoscm.adapter.EquipmentSectionsPagerAdapter;
import com.pronovoscm.adapter.InventorySubCategoryAdapter;
import com.pronovoscm.data.InventoryProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.fragments.EquipmentDetailFragment;
import com.pronovoscm.model.request.inventory.InventoryRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.EquipmentCategoriesMaster;
import com.pronovoscm.persistence.domain.EquipmentInventory;
import com.pronovoscm.persistence.domain.EquipmentRegion;
import com.pronovoscm.persistence.domain.EquipmentSubCategoriesMaster;
import com.pronovoscm.persistence.repository.EquipementInventoryRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.SharedPref;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

//import com.pronovoscm.adapter.EquipmentCategoryPopupAdapter;

public class EquipmentDetailsActivity extends BaseActivity implements EquipmentCategoryPopupAdapter.selectEquipment {
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
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
    int selectTab = 0;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    int selectedTab = 0;
    private PopupWindow mPopupWindow;
    private InventorySubCategoryAdapter inventoryCategoryAdapter;
    private List<EquipmentSubCategoriesMaster> equipmentSubCategoriesMasterList;
    private int categoryId;
    private LoginResponse loginResponse;
    private EquipmentCategoriesMaster equipmentCategories;
    private int projectId;
    private EquipmentSectionsPagerAdapter mSectionsPagerAdapter;
    private int subCategoryId;
    private EquipmentCategoryPopupAdapter drawingListPopupAdapter;
    private boolean isLoading;
    private List<EquipmentCategoriesMaster> equipmentCategorie;

    @Override
    protected int doGetContentView() {
        return R.layout.equipment_detail_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        categoryId = getIntent().getIntExtra("category_id", 0);
        subCategoryId = getIntent().getIntExtra("sub_category_id", 0);
        projectId = getIntent().getIntExtra("project_id", 0);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        equipmentSubCategoriesMasterList = mEquipementInventoryRepository.getSubCategories(categoryId, projectId, loginResponse.getUserDetails().getUsers_id());
        equipmentCategories = mEquipementInventoryRepository.getEquipmentCategoryByCategoryId(categoryId, loginResponse.getUserDetails().getUsers_id());
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.INVISIBLE);
        if (equipmentCategories != null) {
            titleTextView.setText(equipmentCategories.getName());
        }
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.equipment_popup_view, null);
        mPopupWindow = new PopupWindow(
                customView,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        RecyclerView recyclerView = customView.findViewById(R.id.equipmentRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        equipmentCategorie = mEquipementInventoryRepository.getCategories(projectId, loginResponse.getUserDetails().getUsers_id());
        drawingListPopupAdapter = new EquipmentCategoryPopupAdapter(this, equipmentCategorie, equipmentCategories);
        recyclerView.setAdapter(drawingListPopupAdapter);
        if (NetworkService.isNetworkAvailable(this) && equipmentSubCategoriesMasterList.size() > 0) {
            setupViewPager();
            noRecordTextView.setVisibility(View.GONE);
        } else {
            lineView.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            noRecordTextView.setText(R.string.no_inventory_found);
            noRecordTextView.setVisibility(View.VISIBLE);
        }

        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.i("EquipmentDetail", "updateViewPager: call");
            if (!isLoading() ) {
                if (NetworkService.isNetworkAvailable(this) ){
                    callCategoriesAPI();
                }else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    public void setupViewPager() {
        selectedTab = 0;
        viewPager.removeAllViews();
        tabLayout.removeAllTabs();
        lineView.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        mSectionsPagerAdapter = new EquipmentSectionsPagerAdapter(getSupportFragmentManager(), equipmentSubCategoriesMasterList);
        for (int i = 0; i < equipmentSubCategoriesMasterList.size(); i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setText(equipmentSubCategoriesMasterList.get(i).getName());
            if (subCategoryId != -1 && equipmentSubCategoriesMasterList.get(i).getEqSubCategoryId() == subCategoryId) {
                selectTab = i;
            }
            tabLayout.addTab(tab);
            EquipmentDetailFragment equipmentDetailFragment = new EquipmentDetailFragment(equipmentSubCategoriesMasterList.get(i)/*,equipmentSubCategoriesMasterList.get(i).getEqSubCategoryId(), projectId*/);
            equipmentDetailFragment.setProjectId(projectId);
            mSectionsPagerAdapter.addFragment(equipmentDetailFragment, equipmentSubCategoriesMasterList.get(i).getName());
        }
        //mSectionsPagerAdapter.notifyDataSetChanged();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                selectedTab = i;
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
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        tabLayout.setScrollPosition(selectTab, 0f, true);
                    }
                }, 50);
        viewPager.setCurrentItem(selectTab);
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
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("category_id", equipmentCategories.getId());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @OnClick({R.id.titleTextView})
    public void changeCategory() {
        int[] loc_int = new int[2];

        try {
            titleTextView.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.

        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + titleTextView.getWidth();
        location.bottom = location.top + titleTextView.getHeight();
        if (mPopupWindow != null) {
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setFocusable(true);
            mPopupWindow.showAtLocation(titleTextView, Gravity.TOP | Gravity.CENTER, 0, location.top + titleTextView.getHeight());
        }
    }

    @Override
    public void onSelectEquipment(EquipmentCategoriesMaster equipmentCategories) {
        tabLayout.removeAllTabs();
        equipmentSubCategoriesMasterList.clear();
        lineView.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
//        setupViewPager();
        this.equipmentCategories = equipmentCategories;
        titleTextView.setText(equipmentCategories.getName());
        categoryId = equipmentCategories.getEq_categories_id();
        equipmentSubCategoriesMasterList.addAll(mEquipementInventoryRepository.getSubCategories(categoryId, projectId, loginResponse.getUserDetails().getUsers_id()));
        mPopupWindow.dismiss();
        if (NetworkService.isNetworkAvailable(this) && equipmentSubCategoriesMasterList.size() > 0) {
            updateViewPager();
            noRecordTextView.setVisibility(View.GONE);
        } else {
            noRecordTextView.setText(R.string.no_inventory_found);
            noRecordTextView.setVisibility(View.VISIBLE);
        }
//        updateViewPager();
    }


    public void updateViewPager() {
        selectedTab = 0;
        tabLayout.removeAllTabs();
        mSectionsPagerAdapter = new EquipmentSectionsPagerAdapter(getSupportFragmentManager(), equipmentSubCategoriesMasterList);
        int selectTab = 0;
        for (int i = 0; i < equipmentSubCategoriesMasterList.size(); i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setText(equipmentSubCategoriesMasterList.get(i).getName());
            tabLayout.addTab(tab);
            EquipmentDetailFragment equipmentDetailFragment = new EquipmentDetailFragment(equipmentSubCategoriesMasterList.get(i));
            equipmentDetailFragment.setProjectId(projectId);
            mSectionsPagerAdapter.addFragment(equipmentDetailFragment, equipmentSubCategoriesMasterList.get(i).getName(), i);

        }
        lineView.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                selectedTab = i;
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

        viewPager.setOffscreenPageLimit(equipmentSubCategoriesMasterList.size());
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                tabLayout.getTabAt(i).select();
             /*   if (!addingTab) {
                    lastSelectedTab = mDrawingDisciplineList.get(i).getDrawingDiscipline();
                }
*/
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
                    }
                }, 50);
        viewPager.setCurrentItem(selectTab);

    }


    /**
     * Get inventory List
     */
    public void callCategoriesAPI() {
//        isLoading = true;
        setLoading(true);

        mInventoryProvider.getCategories(projectId, new ProviderResult<List<EquipmentCategoriesMaster>>() {
            @Override
            public void success(List<EquipmentCategoriesMaster> result) {
                mInventoryProvider.getSubCategories(new ProviderResult<List<EquipmentSubCategoriesMaster>>() {
                    @Override
                    public void success(List<EquipmentSubCategoriesMaster> result) {
                        mInventoryProvider.getEquipmentDetails(new ProviderResult<List<EquipmentRegion>>() {
                            @Override
                            public void success(List<EquipmentRegion> result) {
                                InventoryRequest inventoryRequest = new InventoryRequest();
                                inventoryRequest.setProjectId(projectId);
                                mInventoryProvider.getInventory(inventoryRequest, new ProviderResult<List<EquipmentInventory>>() {
                                    @Override
                                    public void success(List<EquipmentInventory> result) {

                                        equipmentSubCategoriesMasterList.clear();
                                        equipmentCategorie.clear();
                                        equipmentSubCategoriesMasterList.addAll(mEquipementInventoryRepository.getSubCategories(categoryId, projectId, loginResponse.getUserDetails().getUsers_id()));
                                        equipmentCategorie.addAll(mEquipementInventoryRepository.getCategories(projectId, loginResponse.getUserDetails().getUsers_id()));
                                        drawingListPopupAdapter.notifyDataSetChanged();
                                        subCategoryId = equipmentSubCategoriesMasterList.get(selectedTab).getEqSubCategoryId();
                                        if (equipmentSubCategoriesMasterList.size()>0){
                                            noRecordTextView.setText(R.string.no_inventory_found);
                                            noRecordTextView.setVisibility(View.GONE);
                                        }
                                        setupViewPager();
                                        setLoading(false);
                                        swipeRefreshLayout.setRefreshing(false);

                                    }

                                    @Override
                                    public void AccessTokenFailure(String message) {
//                                        isLoading = false;
                                        setLoading(false);

                                    }

                                    @Override
                                    public void failure(String message) {
//                                        isLoading = false;
                                        setLoading(false);

                                    }
                                }, loginResponse);
                            }

                            @Override
                            public void AccessTokenFailure(String message) {
//                                isLoading = false;
                                setLoading(false);

                            }

                            @Override
                            public void failure(String message) {
//                                isLoading = false;
                                setLoading(false);

                            }
                        }, loginResponse);
                    }

                    @Override
                    public void AccessTokenFailure(String message) {
//                        isLoading = false;
                        setLoading(false);

                    }

                    @Override
                    public void failure(String message) {
//                        isLoading = false;
                        setLoading(false);

                    }
                }, loginResponse);
            }

            @Override
            public void AccessTokenFailure(String message) {
//                isLoading = false;
                setLoading(false);
                startActivity(new Intent(EquipmentDetailsActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(EquipmentDetailsActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(EquipmentDetailsActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
//                isLoading = false;
                setLoading(false);

            }
        }, loginResponse);
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        if (!isLoading) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }


}
