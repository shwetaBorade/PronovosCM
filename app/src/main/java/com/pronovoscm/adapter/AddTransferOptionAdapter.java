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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddTransferOptionAdapter extends RecyclerView.Adapter<AddTransferOptionAdapter.ProjectHolder> {
    private List<String> mRegionsList;
    private Activity mActivity;

    public AddTransferOptionAdapter(Activity mActivity, List<String> stringList) {
        this.mRegionsList = stringList;
        this.mActivity = mActivity;
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


    public interface selectOption {
        void onSelectOption(String option);
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

        private void bind(final String regions) {
                regionNameTextView.setTextColor(ContextCompat.getColor(regionsView.getContext(), R.color.colorPrimary));
                regionsView.setBackgroundColor(ContextCompat.getColor(regionsView.getContext(), R.color.gray_fafafa));
                bottom_view.setVisibility(View.VISIBLE);
            regionNameTextView.setText(regions);
            regionNameTextView.setOnClickListener(v -> {
                ((selectOption) mActivity).onSelectOption(regions);
            });
            if (getAdapterPosition() == (mRegionsList.size() - 1)) {
                bottom_view.setVisibility(View.GONE);
            }
        }
    }

}
