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

import com.pronovoscm.R;
import com.pronovoscm.adapter.TransferLogEquipmentsAdapter;
import com.pronovoscm.model.response.transferlogdetails.Details;
import com.pronovoscm.model.response.transferlogdetails.Equipments;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class TransferLogEquipmentFragment extends Fragment {

    Details transferLogDetails;
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
    private ArrayList<Equipments> equipmentArrayList;
    private TransferLogEquipmentsAdapter transferLogEquipmentsAdapter;

    @SuppressLint("ValidFragment")
    public TransferLogEquipmentFragment(Details transferLogResponse) {
        this.transferLogDetails = transferLogResponse;
    }


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
        View rootView = inflater.inflate(R.layout.create_transfer_equiment_fragment, container, false);
        ButterKnife.bind(this, rootView);


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (transferLogDetails!=null){
            totalWeightTV.setText(transferLogDetails.getTotalWeight()+" Total Weight");
        }
        nextTextView.setVisibility(View.GONE);
        cancelTextView.setVisibility(View.GONE);
        addEquipmentView.setVisibility(View.GONE);
        equipmentContainsView.setVisibility(View.VISIBLE);
        equipmentArrayList = new ArrayList<>();
        if (transferLogDetails!=null){
            equipmentArrayList.addAll(transferLogDetails.getEquipments());
        }
        transferLogEquipmentsAdapter = new TransferLogEquipmentsAdapter(equipmentArrayList);
        equipmentRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        equipmentRV.setAdapter(transferLogEquipmentsAdapter);
    }
}
