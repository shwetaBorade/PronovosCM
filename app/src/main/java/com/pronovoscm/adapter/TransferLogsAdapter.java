package com.pronovoscm.adapter;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.activity.TransferLogDetailsActivity;
import com.pronovoscm.model.response.transferlog.Logs;
import com.pronovoscm.persistence.repository.EquipementInventoryRepository;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransferLogsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    @Inject
    EquipementInventoryRepository mEquipementInventoryRepository;
    private List<Logs> logsList;
    private Activity mActivity;

    public TransferLogsAdapter(Activity mActivity, List<Logs> logsList) {
        this.logsList = logsList;
        this.mActivity = mActivity;
        ((PronovosApplication) mActivity.getApplication()).getDaggerComponent().inject(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transfer_logs_list_item, parent, false);
            return new InventoryCategoryHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
      /*  LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.transfer_logs_list_item, parent, false);
        return new InventoryCategoryHolder(view);*/

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        ((InventoryCategoryHolder) holder).bind();
        if (holder instanceof InventoryCategoryHolder) {

            ((InventoryCategoryHolder) holder).bind();
        } else if (holder instanceof LoadingViewHolder) {
//            showLoadingView((LoadingViewHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        if (logsList != null) {
            return logsList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return logsList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;


    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    public class InventoryCategoryHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.transferIdTV)
        TextView transferIdTV;
        @BindView(R.id.pickupLocationTV)
        TextView pickupLocationTV;
        @BindView(R.id.dropoffLocationTV)
        TextView dropoffLocationTV;
        @BindView(R.id.pickUpDateTV)
        TextView pickUpDateTV;
        @BindView(R.id.dropOffDateTV)
        TextView dropOffDateTV;

        public InventoryCategoryHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind() {
            Logs logs = logsList.get(getAdapterPosition());
            transferIdTV.setText("Transfer # " + logs.getTransferId());
            pickupLocationTV.setText(TextUtils.isEmpty(logs.getPickUpLocation()) ? "" : logs.getPickUpLocation());
            dropoffLocationTV.setText(TextUtils.isEmpty(logs.getDropOffLocation()) ? "" : logs.getDropOffLocation());
            pickUpDateTV.setText(TextUtils.isEmpty(logs.getPickUpDate()) ? "" : logs.getPickUpDate());
            dropOffDateTV.setText(TextUtils.isEmpty(logs.getDropOffDate()) ? "" : logs.getDropOffDate());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mActivity.startActivity(new Intent(mActivity, TransferLogDetailsActivity.class).putExtra("transfer_id",logs.getTransferId()));
                }
            });

        }
    }
}
