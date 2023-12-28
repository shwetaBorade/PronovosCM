package com.pronovoscm.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.activity.CreateTransferEquipmentActivity;
import com.pronovoscm.model.request.transferrequest.TransferRequest;
import com.pronovoscm.persistence.domain.EquipmentCategoriesMaster;
import com.pronovoscm.persistence.domain.EquipmentInventory;
import com.pronovoscm.persistence.domain.EquipmentRegion;
import com.pronovoscm.persistence.domain.EquipmentSubCategoriesMaster;
import com.pronovoscm.persistence.repository.EquipementInventoryRepository;
import com.pronovoscm.utils.dialogs.EquipmentCategoryDialog;
import com.pronovoscm.utils.dialogs.EquipmentRegionDialog;
import com.pronovoscm.utils.dialogs.EquipmentSubCategoryDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LookUpEquipmentFragment extends Fragment {

    @Inject
    EquipementInventoryRepository mEquipementInventoryRepository;
    @BindView(R.id.categorySpinnewView)
    RelativeLayout categorySpinnewView;
    @BindView(R.id.subCategorySpinnewView)
    RelativeLayout subCategorySpinnewView;
    @BindView(R.id.equipmentNameSpinnewView)
    RelativeLayout equipmentNameSpinnewView;
    @BindView(R.id.categoryTextView)
    TextView categoryTextView;
    @BindView(R.id.subCategoryTextView)
    TextView subCategoryTextView;
    @BindView(R.id.equipmentNameTextView)
    TextView equipmentNameTextView;
    @BindView(R.id.categoryErrorTextView)
    TextView categoryErrorTextView;
    @BindView(R.id.subcategoryErrorTextView)
    TextView subcategoryErrorTextView;
    @BindView(R.id.equipmentNameErrorTextView)
    TextView equipmentNameErrorTextView;
    private EquipmentCategoriesMaster eqCategoriesMaster;
    private EquipmentSubCategoriesMaster eqSubCategoriesMaster;
    private EquipmentRegion eqRegion;
    private int projectId;
    private AlertDialog alertDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lookup_equiment_fragment, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
        projectId = getActivity().getIntent().getIntExtra("project_id", 0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectEquipmentCategoriesMaster(EquipmentCategoriesMaster equipmentCategoriesMaster) {
        eqRegion = null;
        eqCategoriesMaster = equipmentCategoriesMaster;
        categoryTextView.setText(eqCategoriesMaster.getName());
        subCategoryTextView.setText("");
        equipmentNameTextView.setText("");
        categoryErrorTextView.setText("");
        subcategoryErrorTextView.setText("");
        equipmentNameErrorTextView.setText("");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectEquipmentSubCategoriesMaster(EquipmentSubCategoriesMaster equipmentSubCategoriesMaster) {
        eqSubCategoriesMaster = equipmentSubCategoriesMaster;
        subCategoryTextView.setText(eqSubCategoriesMaster.getName());
        eqRegion = null;
        equipmentNameTextView.setText("");
        subcategoryErrorTextView.setText("");
        equipmentNameErrorTextView.setText("");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectEquipmentCategoriesMaster(EquipmentRegion equipmentRegion) {
        eqRegion = equipmentRegion;
        equipmentNameTextView.setText(eqRegion.getName());
        equipmentNameErrorTextView.setText("");
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }


    @OnClick(R.id.okTextView)
    public void onClickOk() {
        if (checkValidation()) {
            ((CreateTransferEquipmentActivity) getActivity()).setEquipmentRegion(eqRegion);
            getActivity().onBackPressed();
        }
    }

    private boolean checkValidation() {
        boolean isValid = true;
        if (TextUtils.isEmpty(equipmentNameTextView.getText().toString())) {
            equipmentNameErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(categoryTextView.getText().toString())) {
            categoryErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(subCategoryTextView.getText().toString())) {
            subcategoryErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        if (isValid && eqRegion != null && eqRegion.getType().equals("Unique")) {
            String message = "This equipment is not currently in the pick-up location's inventory.";
            TransferRequest transferRequest = ((CreateTransferEquipmentActivity) getActivity()).getTransferRequest();
            if (transferRequest.getPickupVendorStatus() == 1) {
                isValid = false;
                showMessageAlert(message, getString(R.string.ok));
            }
            List<EquipmentInventory> equipmentCategories = mEquipementInventoryRepository.getEquipmentInventory(eqRegion.getEqRegionEquipentId(), transferRequest.getPickupLocation());
            if (equipmentCategories == null || equipmentCategories.size() == 0) {
                isValid = false;
                showMessageAlert(message, getString(R.string.ok));
            }
        }
        return isValid;
    }

    @OnClick(R.id.categorySpinnewView)
    public void categorySpinnewView() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        EquipmentCategoryDialog equipmentCategoryDialog = new EquipmentCategoryDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("project_id", projectId);
        bundle.putSerializable("eqCategoriesMaster", eqCategoriesMaster);
        equipmentCategoryDialog.setCancelable(false);
        equipmentCategoryDialog.setArguments(bundle);
        equipmentCategoryDialog.show(ft, "");
    }

    @OnClick(R.id.subCategorySpinnewView)
    public void subCategorySpinnewView() {
        if (eqCategoriesMaster != null) {

            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            EquipmentSubCategoryDialog equipmentCategoryDialog = new EquipmentSubCategoryDialog();
            Bundle bundle = new Bundle();
            bundle.putInt("project_id", projectId);
            bundle.putSerializable("eqSubCategoriesMaster", eqSubCategoriesMaster);
            bundle.putInt("eq_category_id", eqCategoriesMaster.getEq_categories_id());
            equipmentCategoryDialog.setCancelable(false);
            equipmentCategoryDialog.setArguments(bundle);
            equipmentCategoryDialog.show(ft, "");
        } else {
            showMessageAlert(getString(R.string.select_subcategory_validation), getString(R.string.ok));
        }
    }

    /**
     * Alert to show message
     *
     * @param message
     * @param positiveButtonText
     */
    public void showMessageAlert(String message, String positiveButtonText) {


        try {
            if (alertDialog == null || !alertDialog.isShowing()) {
                alertDialog = new AlertDialog.Builder(getContext()).create();
            }
            alertDialog.setMessage(message);


            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, positiveButtonText, (dialog, which) -> {
                alertDialog.dismiss();

            });
            if (alertDialog != null && !alertDialog.isShowing()) {
                alertDialog.setCancelable(false);
                alertDialog.show();
            }


        } catch (Exception e) {
        }

    }

    @OnClick(R.id.cancelTextView)
    public void cancelViewClick() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.equipmentNameSpinnewView)
    public void equipmentNameSpinnewView() {
        if (eqSubCategoriesMaster != null) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            EquipmentRegionDialog equipmentCategoryDialog = new EquipmentRegionDialog();
            Bundle bundle = new Bundle();
            bundle.putInt("project_id", projectId);
            bundle.putInt("eq_sub_category_id", eqSubCategoriesMaster.getEqSubCategoryId());
            bundle.putSerializable("eqRegion", eqRegion);
            equipmentCategoryDialog.setCancelable(false);
            equipmentCategoryDialog.setArguments(bundle);
            equipmentCategoryDialog.show(ft, "");
        } else {
            showMessageAlert(getString(R.string.select_category_subcategory_validation), getString(R.string.ok));

        }
    }
}


