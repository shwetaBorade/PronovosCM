package com.pronovoscm.adapter;

import android.app.Activity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.persistence.domain.RegionsTable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegionAdapter extends RecyclerView.Adapter<RegionAdapter.ProjectHolder> {
    private List<RegionsTable> mRegionsList;
    private Activity mActivity;
    private int regionId;

    public RegionAdapter(Activity mActivity, List<RegionsTable> regionsList, int regionId) {
        this.mRegionsList = regionsList;
        this.mActivity = mActivity;
        this.regionId = regionId;
    }


    @Override
    public ProjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.region_item_list, parent, false);

        return new ProjectHolder(view);
    }


    @Override
    public void onBindViewHolder(ProjectHolder holder, int position) {
        holder.bind(mRegionsList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mRegionsList != null) {
            return mRegionsList.size();
        } else {
            return 0;
        }
    }


    public interface selectRegion {
        void onSelectRegion(RegionsTable regions);
    }

    public class ProjectHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.regionNameTextView)
        TextView regionNameTextView;
        @BindView(R.id.regionsView)
        ConstraintLayout regionsView;
        @BindView(R.id.bottom_view)
        View bottom_view;

        public ProjectHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind(final RegionsTable regions) {
            if (regions.getRegions_id() == regionId) {
                regionNameTextView.setTextColor(ContextCompat.getColor(regionsView.getContext(), R.color.white));
                regionsView.setBackgroundColor(ContextCompat.getColor(regionsView.getContext(), R.color.colorPrimary));
                bottom_view.setVisibility(View.GONE);
            } else {
                regionNameTextView.setTextColor(ContextCompat.getColor(regionsView.getContext(), R.color.colorPrimary));
                regionsView.setBackgroundColor(ContextCompat.getColor(regionsView.getContext(), R.color.gray_fafafa));
                bottom_view.setVisibility(View.VISIBLE);
            }
            regionNameTextView.setText(regions.getName());
            regionNameTextView.setOnClickListener(v -> {
                regionId = regions.getRegions_id();
                notifyDataSetChanged();
                ((selectRegion) mActivity).onSelectRegion(regions);
            });
            if (getAdapterPosition() == (mRegionsList.size() - 1)) {
                bottom_view.setVisibility(View.GONE);
            }
        }
    }

}
