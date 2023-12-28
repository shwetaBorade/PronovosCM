package com.pronovoscm.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pronovoscm.R;
import com.pronovoscm.data.InventoryProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.fragments.CategoryFragment;
import com.pronovoscm.fragments.EquipmentDetailFragment;
import com.pronovoscm.model.request.inventory.InventoryRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.login.UserPermissions;
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

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class InventoryActivity extends BaseActivity {
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
    @BindView(R.id.searchTextView)
    TextView searchTextView;
    @BindView(R.id.searchDrawingEditText)
    EditText searchDrawingEditText;
    @BindView(R.id.searchViewConstraintLayout)
    ConstraintLayout searchViewConstraintLayout;

    private int projectId;
    private LoginResponse loginResponse;
    private CategoryFragment categoryFragment;
    private EquipmentDetailFragment equipmentDetailFragment;
    private boolean isLoading;

    @Override
    protected int doGetContentView() {
        return R.layout.inventory_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        projectId = getIntent().getIntExtra("project_id", 0);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.INVISIBLE);
        titleTextView.setText(getString(R.string.inventory));
//        mCategoriesArrayList = new ArrayList<>();
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        UserPermissions userPermissions = loginResponse.getUserDetails().getPermissions().get(0);
        loadCategories();
        searchDrawingEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    searchTextView.setBackground(ContextCompat.getDrawable(InventoryActivity.this, R.drawable.rounded_blue_button));
                } else {
                    searchTextView.setBackground(ContextCompat.getDrawable(InventoryActivity.this, R.drawable.rounded_light_gray_button));
                    Fragment f = getSupportFragmentManager().findFragmentById(R.id.listContainer);
                    if (f != null && f instanceof EquipmentDetailFragment) {
                        onBackClick();
//                        categoryFragment.updateList();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        callCategoriesAPI(false);
    }

    /**
     * Get inventory List
     *
     * @param isRefreshing
     */
    public void callCategoriesAPI(boolean isRefreshing) {
//        isLoading = true;
        setLoading(true);
        if (!isRefreshing) {
            CustomProgressBar.showDialog(this);
        }

        mInventoryProvider.getCategories(projectId,new ProviderResult<List<EquipmentCategoriesMaster>>() {
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
                                        CustomProgressBar.dissMissDialog(InventoryActivity.this);

                                        setLoading(false);
                                        if (categoryFragment != null) {

                                            if (mEquipementInventoryRepository.getCategories(projectId, loginResponse.getUserDetails().getUsers_id()).size() > 0 && NetworkService.isNetworkAvailable(InventoryActivity.this)) {
                                                searchViewConstraintLayout.setVisibility(View.VISIBLE);
                                            } else {
                                                searchViewConstraintLayout.setVisibility(View.GONE);

                                            }
                                            categoryFragment.updateList();
                                        }
                                    }

                                    @Override
                                    public void AccessTokenFailure(String message) {
//                                        isLoading = false;
                                        CustomProgressBar.dissMissDialog(InventoryActivity.this);
                                        setLoading(false);

                                    }

                                    @Override
                                    public void failure(String message) {
//                                        isLoading = false;
                                        CustomProgressBar.dissMissDialog(InventoryActivity.this);
                                        setLoading(false);

                                    }
                                }, loginResponse);
                            }

                            @Override
                            public void AccessTokenFailure(String message) {
//                                isLoading = false;
                                CustomProgressBar.dissMissDialog(InventoryActivity.this);
                                setLoading(false);

                            }

                            @Override
                            public void failure(String message) {
//                                isLoading = false;
                                CustomProgressBar.dissMissDialog(InventoryActivity.this);
                                setLoading(false);

                            }
                        }, loginResponse);
                    }

                    @Override
                    public void AccessTokenFailure(String message) {
//                        isLoading = false;
                        CustomProgressBar.dissMissDialog(InventoryActivity.this);
                        setLoading(false);

                    }

                    @Override
                    public void failure(String message) {
//                        isLoading = false;
                        CustomProgressBar.dissMissDialog(InventoryActivity.this);
                        setLoading(false);

                    }
                }, loginResponse);
            }

            @Override
            public void AccessTokenFailure(String message) {
//                isLoading = false;
                CustomProgressBar.dissMissDialog(InventoryActivity.this);
                setLoading(false);
                startActivity(new Intent(InventoryActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(InventoryActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(InventoryActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
//                isLoading = false;
                CustomProgressBar.dissMissDialog(InventoryActivity.this);
                setLoading(false);

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

    public void loadCategories() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        categoryFragment = new CategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("projectId", projectId);
        categoryFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.listContainer, categoryFragment, categoryFragment.getClass().getSimpleName());
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.listContainer);
        if (NetworkService.isNetworkAvailable(this) && f instanceof CategoryFragment) {
            categoryFragment.updateList();
        }

    }

    @OnClick({R.id.searchTextView})
    public void loadSearchInventory() {
        if (!TextUtils.isEmpty(searchDrawingEditText.getText().toString())) {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.listContainer);
            if (f instanceof EquipmentDetailFragment) {
                equipmentDetailFragment.refreshSearchResult(searchDrawingEditText.getText().toString());
            } else {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                equipmentDetailFragment = new EquipmentDetailFragment(searchDrawingEditText.getText().toString(), projectId);
                Bundle bundle = new Bundle();
                bundle.putInt("project_id", projectId);
                bundle.putString("search_string", searchDrawingEditText.getText().toString());
                equipmentDetailFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.listContainer, equipmentDetailFragment, equipmentDetailFragment.getClass().getSimpleName()).addToBackStack(CategoryFragment.class.getName());
                fragmentTransaction.commit();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
//                if (!emailIdEt.hasWindowFocus() && !passwordEt.hasWindowFocus()){
                hideKeyboard(this);
//                }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @OnClick(R.id.leftImageView)
    public void onBackClick() {
        onBackPressed();
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        if (!loading && categoryFragment != null) {
            categoryFragment.updateList();
        }
    }
}
