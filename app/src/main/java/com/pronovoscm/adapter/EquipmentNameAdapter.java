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
import com.pronovoscm.utils.dialogs.EquipmentCategoryDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EquipmentNameAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<EquipmentSubCategoriesMaster> locationsList;
    private EquipmentSubCategoriesMaster selectedLocation;
    private EquipmentCategoryDialog transferLocationDialog;

    public EquipmentNameAdapter(EquipmentCategoryDialog transferLocationDialog, List<EquipmentSubCategoriesMaster> locationsList, EquipmentSubCategoriesMaster selectedLocation) {
        this.locationsList = locationsList;
        this.selectedLocation = selectedLocation;
        this.transferLocationDialog = transferLocationDialog;
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

    public interface updateWeatherCondition {
    /*    void onUpdateSelectLocation(TransferLocationResponse.Locations selectedTag);
        void onUpdateSelectVendorLocation(TransferLocationVendorResponse.Locations selectedTag);
    */
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
            if (selectedLocation != null && selectedLocation.getEqSubCategoryId() == (locations.getEqSubCategoryId())) {
                albumRadioButton.setChecked(true);
            } else {
                albumRadioButton.setChecked(false);
            }
            tagsView.setOnClickListener(v -> {
                albumRadioButton.setChecked(true);
                selectedLocation = locations;
                EventBus.getDefault().post(selectedLocation);
                transferLocationDialog.dismiss();
            });

        }

    }

}