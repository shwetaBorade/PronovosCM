package com.pronovoscm.fragments;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.adapter.EquipmentDetailsAdapter;
import com.pronovoscm.data.InventoryProvider;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.EquipmentRegion;
import com.pronovoscm.persistence.domain.EquipmentSubCategoriesMaster;
import com.pronovoscm.persistence.repository.EquipementInventoryRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.SharedPref;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class EquipmentDetailFragment extends Fragment {
    @Inject
    InventoryProvider mInventoryProvider;
    @Inject
    EquipementInventoryRepository mEquipementInventoryRepository;
    @BindView(R.id.equipmentsRecyclerView)
    RecyclerView equipmentsRecyclerView;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
    @BindView(R.id.searchTextView)
    TextView searchTextView;
    @BindView(R.id.notificationView)
    ConstraintLayout notificationView;

    private EquipmentSubCategoriesMaster equipmentSubCategories;
    private int eqSubCategoryId;
    private List<EquipmentRegion> equipmentCategoriesDetails = new ArrayList<>();
    private EquipmentDetailsAdapter equipmentDetailsAdapter;
    private LoginResponse loginResponse;
    private int projetctId;
    private String searchString;

    public EquipmentDetailFragment(String string, int projectId) {
    }


    @SuppressLint("ValidFragment")
    public EquipmentDetailFragment(EquipmentSubCategoriesMaster equipmentSubCategories/*, Integer eqSubCategoryId, int projectId*/) {
        this.equipmentSubCategories = equipmentSubCategories;
    }

    @SuppressLint("ValidFragment")
    public EquipmentDetailFragment(String searchString) {
        this.searchString = searchString;
    }
/*
    public static EquipmentDetailFragment newInstance(EquipmentSubCategoriesMaster equipmentSubCategoriesMaster) {
        equipmentDetailFragment = new EquipmentDetailFragment();
        return equipmentDetailFragment;
    }*/

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.equipment_detail_view_fragment, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        equipmentCategoriesDetails.clear();

        if (equipmentSubCategories != null) {
            searchTextView.setVisibility(View.GONE);
            equipmentCategoriesDetails.addAll(mEquipementInventoryRepository.getEquipmentRegion(equipmentSubCategories.getEqSubCategoryId(), loginResponse.getUserDetails().getUsers_id(), projetctId));
        } else {
            searchTextView.setVisibility(View.VISIBLE);
            searchString = getArguments().getString("search_string");
            projetctId = getArguments().getInt("project_id");
            equipmentCategoriesDetails.addAll(mEquipementInventoryRepository.getSearchEquipmentRegion(searchString, loginResponse.getUserDetails().getUsers_id(), projetctId));

        }
        if (!NetworkService.isNetworkAvailable(getActivity())) {
            equipmentCategoriesDetails.clear();
        }
        equipmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        equipmentDetailsAdapter = new EquipmentDetailsAdapter(getActivity(), equipmentCategoriesDetails, projetctId, loginResponse.getUserDetails().getUsers_id());
        equipmentsRecyclerView.setAdapter(equipmentDetailsAdapter);
        equipmentDetailsAdapter.notifyDataSetChanged();
        if (equipmentCategoriesDetails.size() <= 0) {
            noRecordTextView.setText(R.string.no_inventory_found);
            noRecordTextView.setVisibility(View.VISIBLE);
//            if (equipmentSubCategories == null) {
                searchTextView.setVisibility(View.GONE);
                notificationView.setVisibility(View.GONE);
//            }
        } else {
            if (equipmentSubCategories != null) {
                searchTextView.setVisibility(View.GONE);
            } else {
                searchTextView.setVisibility(View.VISIBLE);
            }
            notificationView.setVisibility(View.VISIBLE);
            noRecordTextView.setVisibility(View.GONE);

        }
//        callSubCategoriesAPI();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }

    public void setProjectId(int projectId) {
        this.projetctId = projectId;
    }

    public String getSearchString() {
        return searchString;
    }

    public void refreshSearchResult(String searchString) {
        this.searchString = searchString;
        equipmentCategoriesDetails.clear();
        equipmentCategoriesDetails.addAll(mEquipementInventoryRepository.getSearchEquipmentRegion(searchString, loginResponse.getUserDetails().getUsers_id(), projetctId));

        if (equipmentCategoriesDetails.size() <= 0 || !NetworkService.isNetworkAvailable(getActivity())) {
            equipmentCategoriesDetails.clear();
            noRecordTextView.setText(R.string.no_inventory_found);
            searchTextView.setVisibility(View.GONE);
            notificationView.setVisibility(View.GONE);
            noRecordTextView.setVisibility(View.VISIBLE);
        } else {
            searchTextView.setVisibility(View.VISIBLE);
            notificationView.setVisibility(View.VISIBLE);
            noRecordTextView.setVisibility(View.GONE);
        }
        equipmentDetailsAdapter.notifyDataSetChanged();
    }
}
