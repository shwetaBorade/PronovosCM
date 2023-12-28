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
import com.pronovoscm.model.UnloadingEnum;
import com.pronovoscm.model.response.transferlogdetails.Details;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class TransferLogDropoffFragment extends Fragment {

    private static Details transferLogDetails;
    @BindView(R.id.dropoffDateTextView)
    TextView dropoffDateTextView;
    @BindView(R.id.dropoffTimeTextView)
    TextView dropoffTimeTextView;
    @BindView(R.id.dropoffLocationTextView)
    TextView dropoffLocationTextView;
    @BindView(R.id.dropoffContactTextView)
    TextView dropoffContactTextView;
    @BindView(R.id.dropoffContactNumberTextView)
    TextView dropoffContactNumberTextView;
    @BindView(R.id.dropoffUnloadingTextView)
    TextView dropoffUnloadingTextView;
    @BindView(R.id.mainView)
    LinearLayout mainView;

    @SuppressLint("ValidFragment")
    public TransferLogDropoffFragment(Details transferLogResponse) {
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
        View rootView = inflater.inflate(R.layout.transfer_log_dropoff_fragment, container, false);
        ButterKnife.bind(this, rootView);


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
        if (transferLogDetails != null) {
            dropoffContactNumberTextView.setText(TextUtils.isEmpty(transferLogDetails.getDropoffContactNumber()) ? "-" : String.valueOf(transferLogDetails.getDropoffContactNumber()));
            dropoffContactTextView.setText(TextUtils.isEmpty(transferLogDetails.getDropoffContact()) ? "-" : String.valueOf(transferLogDetails.getDropoffContact()));
            dropoffLocationTextView.setText(TextUtils.isEmpty(transferLogDetails.getDropoffLocationName()) ? "-" : String.valueOf(transferLogDetails.getDropoffLocationName()));
            dropoffDateTextView.setText(TextUtils.isEmpty(transferLogDetails.getDropoffDate()) ? "-" : String.valueOf(transferLogDetails.getDropoffDate()));
            dropoffTimeTextView.setText(TextUtils.isEmpty(transferLogDetails.getDropoffTime()) ? "-" : String.valueOf(transferLogDetails.getDropoffTime()));
            dropoffUnloadingTextView.setText(transferLogDetails.getUnloadingMethod() == 0 ? "None" : transferLogDetails.getUnloadingMethod() == 1 ? UnloadingEnum.FORKLIFT.toString() : transferLogDetails.getUnloadingMethod() == 2 ? UnloadingEnum.CRANE.toString() : UnloadingEnum.OTHER.toString());
        } else {
            mainView.setVisibility(View.GONE);
        }
    }
}
