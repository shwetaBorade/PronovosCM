package com.pronovoscm.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.activity.InventoryActivity;
import com.pronovoscm.adapter.InventoryCategoryAdapter;
import com.pronovoscm.data.InventoryProvider;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.EquipmentCategoriesMaster;
import com.pronovoscm.persistence.repository.EquipementInventoryRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.SharedPref;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryFragment extends Fragment {

    @Inject
    InventoryProvider mInventoryProvider;
    @Inject
    EquipementInventoryRepository mEquipementInventoryRepository;
    InventoryCategoryAdapter inventoryCategoryAdapter;

    @BindView(R.id.categoryRecyclerView)
    RecyclerView categoryRecyclerView;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
    @BindView(R.id.categoryTextView)
    TextView categoryTextView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<EquipmentCategoriesMaster> mCategoriesArrayList = new ArrayList<>();
    private int projectId;
    private boolean showNoRecord;
    private LoginResponse loginResponse;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.inventory_view_fragment, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
        projectId = getArguments().getInt("projectId");
        showNoRecord = getArguments().getBoolean("show_no_record");
        swipeRefreshLayout.setOnRefreshListener(() -> {

            if (NetworkService.isNetworkAvailable(getActivity()) && !((InventoryActivity) getActivity()).isLoading()) {
                ((InventoryActivity) getActivity()).callCategoriesAPI(true);
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        mCategoriesArrayList = new ArrayList<>();
//        mCategoriesArrayList.addAll(mEquipementInventoryRepository.getCategories(projectId, 0));
        if (!NetworkService.isNetworkAvailable(getActivity())) {
            mCategoriesArrayList.clear();
            noRecordTextView.setText(getString(R.string.inventory_no_record_message));
            noRecordTextView.setVisibility(View.VISIBLE);
            categoryTextView.setVisibility(View.GONE);
        } else if (mCategoriesArrayList.size() == 0) {
            noRecordTextView.setVisibility(View.VISIBLE);
            categoryTextView.setVisibility(View.GONE);
            noRecordTextView.setText(getString(R.string.searching_for_inventory));
        }
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        inventoryCategoryAdapter = new InventoryCategoryAdapter(getActivity(), mCategoriesArrayList, loginResponse.getUserDetails().getUsers_id(), projectId);
        categoryRecyclerView.setAdapter(inventoryCategoryAdapter);
        inventoryCategoryAdapter.notifyDataSetChanged();
    }


    @SuppressLint("SetTextI18n")
    public void updateList() {
        mCategoriesArrayList.clear();
        if (loginResponse != null) {
            mCategoriesArrayList.addAll(mEquipementInventoryRepository.getCategories(projectId, 0));
            inventoryCategoryAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            if (mCategoriesArrayList.size() <= 0) {
                noRecordTextView.setText(getString(R.string.inventory_no_record_message));
                noRecordTextView.setVisibility(View.VISIBLE);
                categoryTextView.setVisibility(View.GONE);
            } else {
                categoryTextView.setVisibility(View.VISIBLE);
                noRecordTextView.setVisibility(View.GONE);

            }
        }
    }
}
