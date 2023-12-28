package com.pronovoscm.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.pronovoscm.adapter.InventorySubCategoryAdapter;
import com.pronovoscm.data.InventoryProvider;
import com.pronovoscm.data.ProviderResult;
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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

//import com.pronovoscm.adapter.EquipmentCategoryPopupAdapter;

public class InventorySubcategoryActivity extends BaseActivity implements EquipmentCategoryPopupAdapter.selectEquipment {
    public static final int INVENTORY_UPDATE = 502;
    @Inject
    InventoryProvider mInventoryProvider;
    @Inject
    EquipementInventoryRepository mEquipementInventoryRepository;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.subcategoryRecyclerView)
    RecyclerView categoryRecyclerView;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    List<EquipmentCategoriesMaster> equipmentCategorie;
    private InventorySubCategoryAdapter inventoryCategoryAdapter;
    private ArrayList<EquipmentSubCategoriesMaster> mCategoriesArrayList;
    private long categoryId;
    private int projectId;
    private LoginResponse loginResponse;
    private EquipmentCategoriesMaster equipmentCategories;
    private EquipmentCategoryPopupAdapter drawingListPopupAdapter;
    private PopupWindow mPopupWindow;
    private boolean isLoading;

    @Override
    protected int doGetContentView() {
        return R.layout.inventory_subcategory_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        projectId = getIntent().getIntExtra("project_id", 0);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        categoryId = getIntent().getLongExtra("category_id", 0);
        equipmentCategories = mEquipementInventoryRepository.getEquipmentCategory(categoryId, loginResponse.getUserDetails().getUsers_id());
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.INVISIBLE);
        if (equipmentCategories != null) {
            titleTextView.setText(equipmentCategories.getName());
        }
        swipeRefreshLayout.setOnRefreshListener(() -> {

            if (NetworkService.isNetworkAvailable(this) && !isLoading()) {
                callCategoriesAPI();
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        mCategoriesArrayList = new ArrayList<>();
        mCategoriesArrayList.addAll(mEquipementInventoryRepository.getSubCategories(equipmentCategories.getEq_categories_id(), projectId, loginResponse.getUserDetails().getUsers_id()));
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (!NetworkService.isNetworkAvailable(this)) {
            mCategoriesArrayList.clear();
        }
        inventoryCategoryAdapter = new InventorySubCategoryAdapter(this, mCategoriesArrayList, projectId, loginResponse.getUserDetails().getUsers_id());
        categoryRecyclerView.setAdapter(inventoryCategoryAdapter);
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
        if (mCategoriesArrayList.size() <= 0) {
            noRecordTextView.setText(R.string.no_inventory_found);
            noRecordTextView.setVisibility(View.VISIBLE);
        } else {
            noRecordTextView.setVisibility(View.GONE);

        }
//        callSubCategoriesAPI();

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
        super.onBackPressed();
    }

    @Override
    public void onSelectEquipment(EquipmentCategoriesMaster equipmentCategories) {
        this.equipmentCategories = equipmentCategories;
        rightImageView.setVisibility(View.INVISIBLE);
        titleTextView.setText(equipmentCategories.getName());
        mCategoriesArrayList.clear();
        drawingListPopupAdapter.notifyDataSetChanged();
        mPopupWindow.dismiss();
        mCategoriesArrayList.addAll(mEquipementInventoryRepository.getSubCategories(equipmentCategories.getEq_categories_id(), projectId, loginResponse.getUserDetails().getUsers_id()));
        if (!NetworkService.isNetworkAvailable(this)) {
            mCategoriesArrayList.clear();
        }
        if (mCategoriesArrayList.size() <= 0) {
            noRecordTextView.setText(R.string.no_inventory_found);
            noRecordTextView.setVisibility(View.VISIBLE);
        } else {
            noRecordTextView.setVisibility(View.GONE);
        }
        inventoryCategoryAdapter.notifyDataSetChanged();
//        callSubCategoriesAPI();
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
                                        mCategoriesArrayList.clear();
                                        equipmentCategorie.clear();
                                        mCategoriesArrayList.addAll(mEquipementInventoryRepository.getSubCategories(equipmentCategories.getEq_categories_id(), projectId, loginResponse.getUserDetails().getUsers_id()));
                                        equipmentCategorie.addAll(mEquipementInventoryRepository.getCategories(projectId, loginResponse.getUserDetails().getUsers_id()));
                                        drawingListPopupAdapter.notifyDataSetChanged();
                                        inventoryCategoryAdapter.notifyDataSetChanged();
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
                startActivity(new Intent(InventorySubcategoryActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(InventorySubcategoryActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(InventorySubcategoryActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
//                isLoading = false;
                setLoading(false);

            }
        }, loginResponse);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case INVENTORY_UPDATE:
                    categoryId = data.getLongExtra("category_id", 0);
                    equipmentCategories = mEquipementInventoryRepository.getEquipmentCategory(categoryId, loginResponse.getUserDetails().getUsers_id());
                    mCategoriesArrayList.clear();
                    if (NetworkService.isNetworkAvailable(this)) {
                        mCategoriesArrayList.addAll(mEquipementInventoryRepository.getSubCategories(equipmentCategories.getEq_categories_id(), projectId, loginResponse.getUserDetails().getUsers_id()));
                    }
                    if (equipmentCategories != null) {
                        titleTextView.setText(equipmentCategories.getName());
                    }
                    inventoryCategoryAdapter.notifyDataSetChanged();
                    if (mCategoriesArrayList.size() <= 0) {
                        noRecordTextView.setText(R.string.no_inventory_found);
                        noRecordTextView.setVisibility(View.VISIBLE);
                    } else {
                        noRecordTextView.setVisibility(View.GONE);

                    }
                    break;
            }
        }
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
