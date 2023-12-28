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
import com.pronovoscm.persistence.domain.EquipmentCategoriesMaster;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EquipmentCategoryPopupAdapter extends RecyclerView.Adapter<EquipmentCategoryPopupAdapter.ProjectHolder> {
    private List<EquipmentCategoriesMaster> equipmentCategoriesList;
    private Activity mActivity;
    private EquipmentCategoriesMaster equipmentCategories;

    public EquipmentCategoryPopupAdapter(Activity mActivity, List<EquipmentCategoriesMaster> regionsList, EquipmentCategoriesMaster equipmentCategories) {
        this.equipmentCategoriesList = regionsList;
        this.mActivity = mActivity;
        this.equipmentCategories = equipmentCategories;
    }


    @Override
    public ProjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.drawing_popup_item_list, parent, false);

        return new ProjectHolder(view);
    }


    @Override
    public void onBindViewHolder(ProjectHolder holder, int position) {
        holder.bind(equipmentCategoriesList.get(position));
    }

    @Override
    public int getItemCount() {
        if (equipmentCategoriesList != null) {
            return equipmentCategoriesList.size();
        } else {
            return 0;
        }
    }


    public interface selectEquipment{
        void onSelectEquipment(EquipmentCategoriesMaster equipmentCategories);
    }

    public class ProjectHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.drawingNameTextView)
        TextView drawingNameTextView;
        @BindView(R.id.drawingView)
        ConstraintLayout drawingView;
        @BindView(R.id.bottom_view)
        View bottom_view;

        public ProjectHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind(EquipmentCategoriesMaster equipmentCategories) {
            if (equipmentCategories.getEq_categories_id() == EquipmentCategoryPopupAdapter.this.equipmentCategories.getEq_categories_id()) {
                drawingNameTextView.setTextColor(ContextCompat.getColor(drawingView.getContext(), R.color.white));
                drawingView.setBackgroundColor(ContextCompat.getColor(drawingView.getContext(), R.color.colorPrimary));
                bottom_view.setVisibility(View.GONE);
            } else {
                drawingNameTextView.setTextColor(ContextCompat.getColor(drawingView.getContext(), R.color.colorPrimary));
                drawingView.setBackgroundColor(ContextCompat.getColor(drawingView.getContext(), R.color.gray_fafafa));
                bottom_view.setVisibility(View.VISIBLE);
            }
            drawingNameTextView.setText(equipmentCategories.getName());
            drawingNameTextView.setOnClickListener(v -> {
                EquipmentCategoryPopupAdapter.this.equipmentCategories = equipmentCategories;
                notifyDataSetChanged();
              if (mActivity!=null){
                  ((selectEquipment) mActivity).onSelectEquipment(equipmentCategories);
              }
            });
            if (getAdapterPosition() == (equipmentCategoriesList.size() - 1)) {
                bottom_view.setVisibility(View.GONE);
            }
        }
    }

}
