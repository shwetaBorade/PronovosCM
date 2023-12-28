package com.pronovoscm.fragments;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.model.response.transferlogdetails.Details;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class TransferLogPickupFragment extends Fragment {

    private static Details transferLogDetails;
    @BindView(R.id.pickupDateTextView)
    TextView pickupDateTextView;
    @BindView(R.id.pickupTimeTextView)
    TextView pickupTimeTextView;
    @BindView(R.id.pickupLocationTextView)
    TextView pickupLocationTextView;
    @BindView(R.id.pickupContactTextView)
    TextView pickupContactTextView;
    @BindView(R.id.pickupContactNumberTextView)
    TextView pickupContactNumberTextView;
    @BindView(R.id.mainView)
    LinearLayout mainView;

    @SuppressLint("ValidFragment")
    public TransferLogPickupFragment(Details transferLogResponse) {
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
        View rootView = inflater.inflate(R.layout.transfer_log_pickup_fragment, container, false);
        ButterKnife.bind(this, rootView);


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (transferLogDetails != null) {
            pickupContactNumberTextView.setText(TextUtils.isEmpty(transferLogDetails.getPickupContactNumber()) ? "-" : String.valueOf(transferLogDetails.getPickupContactNumber()));
            pickupContactTextView.setText(TextUtils.isEmpty(transferLogDetails.getPickupContact()) ? "-" : String.valueOf(transferLogDetails.getPickupContact()));
            pickupLocationTextView.setText(TextUtils.isEmpty(transferLogDetails.getPickupLocationName()) ? "-" : String.valueOf(transferLogDetails.getPickupLocationName()));
            pickupDateTextView.setText(TextUtils.isEmpty(transferLogDetails.getPickupDate()) ? "-" : String.valueOf(transferLogDetails.getPickupDate()));
            pickupTimeTextView.setText(TextUtils.isEmpty(transferLogDetails.getPickupTime()) ? "-" : String.valueOf(transferLogDetails.getPickupTime()));
        } else {
            mainView.setVisibility(View.GONE);
        }
    }
}
