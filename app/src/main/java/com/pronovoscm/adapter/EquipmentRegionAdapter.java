package com.pronovoscm.adapter;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.persistence.domain.EquipmentRegion;
import com.pronovoscm.utils.dialogs.EquipmentRegionDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EquipmentRegionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<EquipmentRegion> locationsList;
    private EquipmentRegion equipmentRegion;
    private EquipmentRegionDialog transferLocationDialog;

    public EquipmentRegionAdapter(EquipmentRegionDialog transferLocationDialog, List<EquipmentRegion> locationsList, EquipmentRegion equipmentRegion) {
        this.locationsList = locationsList;
        this.equipmentRegion = equipmentRegion;
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

    public interface updateEquipment {
        void onUpdateSelectEquipment(EquipmentRegion equipmentRegion);
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
            EquipmentRegion locations = locationsList.get(getAdapterPosition());
            albumTextView.setText(locations.getName());
            albumRadioButton.setClickable(false);
            if (equipmentRegion != null && equipmentRegion.getEqRegionEquipentId() == (locations.getEqRegionEquipentId())) {
                albumRadioButton.setChecked(true);
            } else {
                albumRadioButton.setChecked(false);
            }
            tagsView.setOnClickListener(v -> {
                albumRadioButton.setChecked(true);
                equipmentRegion = locations;
                transferLocationDialog.onUpdateSelectEquipment(equipmentRegion);
                notifyDataSetChanged();
            });

        }

    }

}