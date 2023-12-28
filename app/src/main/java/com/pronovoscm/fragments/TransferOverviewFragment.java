package com.pronovoscm.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.adapter.TransferOverviewAdapter;
import com.pronovoscm.model.response.transferoverviewcount.TransferCount;
import com.pronovoscm.model.response.transferoverviewcount.TransferOverviewCountResponse;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransferOverviewFragment extends Fragment {

    TransferOverviewAdapter transferOverviewAdapter;

    @BindView(R.id.transferOverviewRecyclerView)
    RecyclerView transferOverviewRecyclerView;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;

    private ArrayList<TransferCount> transfers;
    private int projectId;
    private TransferOverviewCountResponse transferOverviewResponse;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.transfer_overview_view_fragment, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if (transfers != null && transfers.size() > 0) {
            transfers.clear();
            transferOverviewAdapter.notifyDataSetChanged();
        }*/
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        projectId = getArguments().getInt("projectId");
//        transferOverviewResponse = (TransferOverviewResponse) getArguments().getSerializable("transfer_overview");
        noRecordTextView.setText("Loading Transfer Overview");
        noRecordTextView.setVisibility(View.VISIBLE);
        transfers = new ArrayList<>();
    }


    public void updateResponse(TransferOverviewCountResponse mTransferOverviewResponse) {
        noRecordTextView.setText("");
        noRecordTextView.setVisibility(View.GONE);
        transferOverviewResponse = mTransferOverviewResponse;
        if (transfers != null) {
            transfers.clear();
        } else {
            transfers = new ArrayList<>();
        }
        if (transferOverviewResponse != null && transferOverviewResponse.getData() != null) {
            transfers.addAll(transferOverviewResponse.getData().getTransferCount());
            transferOverviewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            transferOverviewAdapter = new TransferOverviewAdapter(getActivity(), transfers, transferOverviewResponse, projectId);
            transferOverviewRecyclerView.setAdapter(transferOverviewAdapter);
        }
        if (transfers == null || transfers.size() == 0) {
            noRecordTextView.setText(R.string.no_record_found);
            noRecordTextView.setVisibility(View.VISIBLE);
        }
    }

    public void removeLoadingText() {
        noRecordTextView.setText("");

    }

    public void removeAll() {
        noRecordTextView.setText("Loading Transfer Overview");
        noRecordTextView.setVisibility(View.VISIBLE);
        if (transfers != null && transfers.size() > 0) {
            transfers.clear();
            transferOverviewAdapter.notifyDataSetChanged();
        }
    }
}
