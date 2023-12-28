package com.pronovoscm.fragments;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.model.response.transferlogdetails.Details;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class TransferLogInfoFragment extends Fragment {

    @BindView(R.id.truckSizeTextView)
    TextView truckSizeTextView;
    @BindView(R.id.freightLineTextView)
    TextView freightLineTextView;
    @BindView(R.id.commentsTextView)
    TextView commentsTextView;
    @BindView(R.id.loadTimeTextView)
    TextView loadTimeTextView;
    @BindView(R.id.departureTimeTextView)
    TextView departureTimeTextView;
    @BindView(R.id.arrivalTimeTextView)
    TextView arrivalTimeTextView;
    @BindView(R.id.dropOffLoadTimeTextView)
    TextView dropOffLoadTimeTextView;
    @BindView(R.id.dropOffDepartureTimeTextView)
    TextView dropOffDepartureTimeTextView;
    @BindView(R.id.dropOffArrivalTimeTextView)
    TextView dropOffArrivalTimeTextView;
    @BindView(R.id.createdByTextView)
    TextView createdByTextView;
    @BindView(R.id.infoView)
    LinearLayout infoView;
    private Details transferLogDetails;

    @SuppressLint("ValidFragment")
    public TransferLogInfoFragment(Details transferLogResponse) {
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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.transfer_log_info_fragment, container, false);
        ButterKnife.bind(this, rootView);
        if (transferLogDetails != null) {
            truckSizeTextView.setText(TextUtils.isEmpty(transferLogDetails.getTruckSize()) ? "-" : transferLogDetails.getTruckSize());
            freightLineTextView.setText(TextUtils.isEmpty(transferLogDetails.getFreightLine()) ? "-" : transferLogDetails.getFreightLine());
            commentsTextView.setText(TextUtils.isEmpty(transferLogDetails.getComments()) ? "-" : transferLogDetails.getComments());
            loadTimeTextView.setText(TextUtils.isEmpty(transferLogDetails.getActualPickupLoadTime()) ? "-" : transferLogDetails.getActualPickupLoadTime());
            departureTimeTextView.setText(TextUtils.isEmpty(transferLogDetails.getActualPickupDepartureTime()) ? "-" : transferLogDetails.getActualPickupDepartureTime());
            arrivalTimeTextView.setText(TextUtils.isEmpty(transferLogDetails.getActualPickupTime()) ? "-" : transferLogDetails.getActualPickupTime());
            dropOffLoadTimeTextView.setText(TextUtils.isEmpty(transferLogDetails.getActualDropoffLoadTime()) ? "-" : transferLogDetails.getActualDropoffLoadTime());
            dropOffDepartureTimeTextView.setText(TextUtils.isEmpty(transferLogDetails.getActualDropoffDepartureTime()) ? "-" : transferLogDetails.getActualDropoffDepartureTime());
            dropOffArrivalTimeTextView.setText(TextUtils.isEmpty(transferLogDetails.getActualDropoffTime()) ? "-" : transferLogDetails.getActualDropoffTime());
            createdByTextView.setText(TextUtils.isEmpty(transferLogDetails.getCreatedBy()) ? "-" : transferLogDetails.getCreatedBy());
            infoView.setVisibility(View.VISIBLE);
        } else {
            infoView.setVisibility(View.GONE);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
