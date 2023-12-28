package com.pronovoscm.adapter;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.persistence.domain.EquipmentSubCategoriesMaster;
import com.pronovoscm.utils.dialogs.EquipmentSubCategoryDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<EquipmentSubCategoriesMaster> locationsList;
    private EquipmentSubCategoriesMaster equipmentSubCategoriesMaster;
    private EquipmentSubCategoryDialog equipmentSubCategoryDialog;

    public SubCategoryAdapter(EquipmentSubCategoryDialog equipmentSubCategoryDialog, List<EquipmentSubCategoriesMaster> locationsList, EquipmentSubCategoriesMaster equipmentSubCategoriesMaster) {
        this.locationsList = locationsList;
        this.equipmentSubCategoriesMaster = equipmentSubCategoriesMaster;
        this.equipmentSubCategoryDialog = equipmentSubCategoryDialog;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.select_album_item_list, parent, false);

        return new WeatherConditionViewHolder(view);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((WeatherConditionViewHolder) holder).bind();
    }

    @Override
    public int getItemCount() {
        if (locationsList != null) {
            return locationsList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface updateSubCategory {
        /*    void onUpdateSelectLocation(TransferLocationResponse.Locations selectedTag);
         */
        void onUpdateSelectSubCategory(EquipmentSubCategoriesMaster selectedTag);
    }

    public class WeatherConditionViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tagsView)
        ConstraintLayout tagsView;
        @BindView(R.id.albumTextView)
        TextView albumTextView;
        @BindView(R.id.albumRadioButton)
        RadioButton albumRadioButton;


        public WeatherConditionViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind() {
            EquipmentSubCategoriesMaster locations = locationsList.get(getAdapterPosition());
            albumTextView.setText(locations.getName());
            albumRadioButton.setClickable(false);
            if (equipmentSubCategoriesMaster != null && equipmentSubCategoriesMaster.getEqSubCategoryId() == (locations.getEqSubCategoryId())) {
                albumRadioButton.setChecked(true);
            } else {
                albumRadioButton.setChecked(false);
            }
            tagsView.setOnClickListener(v -> {
                albumRadioButton.setChecked(true);
                equipmentSubCategoriesMaster = locations;
                equipmentSubCategoryDialog.onUpdateSelectSubCategory(equipmentSubCategoriesMaster);
                notifyDataSetChanged();
            });

        }

    }

}