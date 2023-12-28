package com.pronovoscm.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pronovoscm.R;
import com.pronovoscm.activity.CreateTransfersActivity;
import com.pronovoscm.adapter.TransferEquipmentsAdapter;
import com.pronovoscm.model.request.transferrequest.Equipment;
import com.pronovoscm.model.request.transferrequest.TransferRequest;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;

import java.math.BigDecimal;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateTransferEquipmentFragment extends Fragment {
    @BindView(R.id.addEquipmentView)
    ConstraintLayout addEquipmentView;
    @BindView(R.id.equipmentContainsView)
    ConstraintLayout equipmentContainsView;
    @BindView(R.id.equipmentRV)
    RecyclerView equipmentRV;
    @BindView(R.id.totalWeight)
    TextView totalWeightTV;
    @BindView(R.id.nextTextView)
    TextView nextTextView;
    @BindView(R.id.cancelTextView)
    TextView cancelTextView;

    private TransferRequest createTransfer;
    private int projectId;
    private TransferEquipmentsAdapter transferEquipmentsAdapter;
    private ArrayList<Equipment> equipmentArrayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create_transfer_equiment_fragment, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    private int transferType = -1;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createTransfer = ((CreateTransfersActivity) getActivity()).getCreateTransfer();
        transferType = getActivity().getIntent().getIntExtra(Constants.INTENT_KEY_TRANSFER_TYPE, -1);

        if (equipmentArrayList != null) {
            equipmentArrayList.clear();
        } else {
            equipmentArrayList = new ArrayList<>();
        }
        transferEquipmentsAdapter = new TransferEquipmentsAdapter(getActivity(), equipmentArrayList, equipmentRV, createTransfer);
        equipmentRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        equipmentRV.setAdapter(transferEquipmentsAdapter);
        projectId = getActivity().getIntent().getIntExtra("project_id", 0);
        updateData();
    }

    @OnClick(R.id.addEquipmentView)
    public void addEquipmentView() {
        ((CreateTransfersActivity) getActivity()).addEquipment();
    }

    private boolean checkDropOffValidations() {
        if (TextUtils.isEmpty(createTransfer.getDeliveryDate()) || TextUtils.isEmpty(createTransfer.getDropoffTime()) || TextUtils.isEmpty(createTransfer.getDropOffName()) || TextUtils.isEmpty(createTransfer.getDropOffPhone())) {
            return true;

        } else {
            return false;
        }
    }

    @OnClick(R.id.nextTextView)
    public void addEquipment() {

        if (!NetworkService.isNetworkAvailable(getActivity())) {
            ((CreateTransfersActivity) getActivity()).showMessageAlert(getActivity(), getString(R.string.internet_connection_check_transfer_overview), getString(R.string.ok), false);
            return;
        }
        ((CreateTransfersActivity) getActivity()).canAheadEquipment(true);

        if (!getActivity().getIntent().getStringExtra("transfer_option").equals("New Request") || createTransfer.getStatus() >= 1) {
            ((CreateTransfersActivity) getActivity()).openInfo();
        } else if (checkDropOffValidations()) {
            ((CreateTransfersActivity) getActivity()).showMessageAlert(getContext(), "The drop-off info is required.", getString(R.string.ok), false);
        } else {
            if ((createTransfer.getPickupVendorStatus() == 0 && createTransfer.getPickupLocation() == projectId) && (createTransfer.getDropoffVendorStatus() == 0 && createTransfer.getDropOffLocation() == projectId)) {
                ((CreateTransfersActivity) getActivity()).showMessageAlert(getContext(), "The pick-up location and drop-off location must be different.", getString(R.string.ok), false);
                return;
            }
            if (transferType != 1) {
                Log.d("nitin", "addEquipment:transferType " + transferType);
                ((CreateTransfersActivity) getActivity()).callTransferRequestAPI(((CreateTransfersActivity) getActivity()).getCreateTransfer(), false);
            } else {
                ((CreateTransfersActivity) getActivity()).openInfo();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        ((CreateTransfersActivity) getActivity()).selectEquipment();
    }

    public void updateData() {
        createTransfer = ((CreateTransfersActivity) getActivity()).getCreateTransfer();
        if (createTransfer.getStatus() >= 1 && (createTransfer.getStatus() != 2 || (createTransfer.getDropoffVendorStatus() != 1 && createTransfer.getPickupLocation() != getActivity().getIntent().getIntExtra("project_id", 0)))) {
            cancelTextView.setVisibility(View.GONE);
        }
        if (createTransfer.getStatus() == 3 && createTransfer.getDropoffVendorStatus() == 0 && createTransfer.getDropOffLocation() == getActivity().getIntent().getIntExtra("project_id", 0)) {
            cancelTextView.setVisibility(View.VISIBLE);
        }

        if (createTransfer.getStatus() == 5 || (createTransfer.getEquipment() != null && createTransfer.getEquipment().size() > 0)) {
            addEquipmentView.setVisibility(View.GONE);
            equipmentContainsView.setVisibility(View.VISIBLE);
            if (equipmentArrayList != null) {
                equipmentArrayList.clear();
            } else {
                equipmentArrayList = new ArrayList<>();
            }
            equipmentArrayList.addAll(createTransfer.getEquipment());
            BigDecimal totalWeight = BigDecimal.ZERO;
            for (Equipment equipment :
                    equipmentArrayList) {
                totalWeight = totalWeight.add(BigDecimal.valueOf(Float.parseFloat(equipment.getTotalWeight())));
            }
            ((CreateTransfersActivity) getActivity()).updateTotalWeight(String.valueOf(totalWeight));
            if (totalWeight != BigDecimal.ZERO) {
                Float f = totalWeight.floatValue();
                String s = String.valueOf(Math.round(totalWeight.doubleValue()));
                totalWeightTV.setText(s + " Total Weight");
            }
            transferEquipmentsAdapter.notifyDataSetChanged();
            nextTextView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rounded_blue_button));
            nextTextView.setClickable(true);
        } else {
            nextTextView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rounded_opacity_blue_view));
            nextTextView.setClickable(false);
            if (createTransfer.getStatus() >= 1 && (createTransfer.getStatus() != 2 || (createTransfer.getDropoffVendorStatus() != 1 && createTransfer.getPickupLocation() != getActivity().getIntent().getIntExtra("project_id", 0)))) {
                addEquipmentView.setVisibility(View.GONE);
            } else {
                addEquipmentView.setVisibility(View.VISIBLE);
            }
            equipmentContainsView.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.cancelTextView)
    public void onClickCancel() {
        getActivity().onBackPressed();
    }

    public void setOffline(Boolean event) {

        transferEquipmentsAdapter.hidePopup();
    }
}


