package com.pronovoscm.adapter;

import android.app.Activity;
import android.content.Intent;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.activity.TransferOverviewDetailsActivity;
import com.pronovoscm.model.response.transferoverviewcount.TransferCount;
import com.pronovoscm.model.response.transferoverviewcount.TransferOverviewCountResponse;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransferOverviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TransferCount> transfersList;
    private Activity mActivity;
    private TransferOverviewCountResponse transferOverviewResponse;
    private int projectId;

    public TransferOverviewAdapter(Activity mActivity, List<TransferCount> transfersList, TransferOverviewCountResponse transferOverviewResponse, int projectId) {
        this.transfersList = transfersList;
        this.mActivity = mActivity;
        this.projectId = projectId;
        this.transferOverviewResponse = transferOverviewResponse;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.inventory_category_list_item, parent, false);
        return new InventoryCategoryHolder(view);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((InventoryCategoryHolder) holder).bind();
    }

    @Override
    public int getItemCount() {
        if (transfersList != null) {
            return transfersList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {

        return position;

    }

    public class InventoryCategoryHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.categoryNameTextView)
        TextView transferOverviewNameTextView;
        @BindView(R.id.categoryCountTextView)
        TextView transferOverviewCountTextView;
        @BindView(R.id.categoryCardView)
        CardView transferOverviewCardView;

        public InventoryCategoryHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind() {

            transferOverviewNameTextView.setText(transfersList.get(getAdapterPosition()).getTitle());
            transferOverviewCountTextView.setText(String.valueOf(transfersList.get(getAdapterPosition()).getCount()));
            transferOverviewCardView.setOnClickListener(v -> mActivity.startActivity(new Intent(mActivity, TransferOverviewDetailsActivity.class).putExtra("project_id", projectId).putExtra("selected_tab", getAdapterPosition())));


        }
    }
}
