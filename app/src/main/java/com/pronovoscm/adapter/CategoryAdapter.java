package com.pronovoscm.adapter;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.persistence.domain.EquipmentCategoriesMaster;
import com.pronovoscm.utils.dialogs.EquipmentCategoryDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<EquipmentCategoriesMaster> locationsList;
    private EquipmentCategoriesMaster equipmentCategoriesMaster;
    private EquipmentCategoryDialog equipmentCategoryDialog;

    public CategoryAdapter(EquipmentCategoryDialog equipmentCategoryDialog, List<EquipmentCategoriesMaster> locationsList, EquipmentCategoriesMaster equipmentCategoriesMaster) {
        this.locationsList = locationsList;
        this.equipmentCategoriesMaster = equipmentCategoriesMaster;
        this.equipmentCategoryDialog = equipmentCategoryDialog;
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

    public interface updateCategory {
        /*    void onUpdateSelectLocation(TransferLocationResponse.Locations selectedTag);
         */
        void onUpdateSelectCategory(EquipmentCategoriesMaster selectedTag);
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
            EquipmentCategoriesMaster locations = locationsList.get(getAdapterPosition());
            albumTextView.setText(locations.getName());
            albumRadioButton.setClickable(false);
            if (equipmentCategoriesMaster != null && equipmentCategoriesMaster.getEq_categories_id() == (locations.getEq_categories_id())) {
                albumRadioButton.setChecked(true);
            } else {
                albumRadioButton.setChecked(false);
            }
            tagsView.setOnClickListener(v -> {
                albumRadioButton.setChecked(true);
                equipmentCategoriesMaster = locations;
                equipmentCategoryDialog.onUpdateSelectCategory(equipmentCategoriesMaster);
                notifyDataSetChanged();
            });

        }

    }

}