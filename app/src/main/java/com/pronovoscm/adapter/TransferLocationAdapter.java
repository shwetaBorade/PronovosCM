package com.pronovoscm.adapter;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.model.response.transferlocation.TransferLocationResponse;
import com.pronovoscm.model.response.transferlocation.TransferLocationVendorResponse;
import com.pronovoscm.utils.dialogs.TransferLocationDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransferLocationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TransferLocationResponse.Locations> locationsList;
    private TransferLocationResponse.Locations selectedLocation;
    private List<TransferLocationVendorResponse.Locations> vendorLocationsList;
    private TransferLocationVendorResponse.Locations selectedVendorLocations;
    private TransferLocationDialog transferLocationDialog;
    private boolean isVendor;

    public TransferLocationAdapter(TransferLocationDialog transferLocationDialog, List<TransferLocationResponse.Locations> locationsList, TransferLocationResponse.Locations selectedLocation) {
        this.locationsList = locationsList;
        this.selectedLocation = selectedLocation;
        this.transferLocationDialog = transferLocationDialog;
        isVendor = false;
    }

    public TransferLocationAdapter(TransferLocationDialog transferLocationDialog, ArrayList<TransferLocationVendorResponse.Locations> vendorLocationsArrayList, TransferLocationVendorResponse.Locations selectedVendorLocation) {
        this.transferLocationDialog = transferLocationDialog;
        this.selectedVendorLocations = selectedVendorLocation;
        this.vendorLocationsList = vendorLocationsArrayList;
        isVendor = true;

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
        } else if (vendorLocationsList != null) {
            return vendorLocationsList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface updateWeatherCondition {
        void onUpdateSelectLocation(TransferLocationResponse.Locations selectedTag);

        void onUpdateSelectVendorLocation(TransferLocationVendorResponse.Locations selectedTag);
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
            albumRadioButton.setClickable(false);
            if (isVendor) {
                TransferLocationVendorResponse.Locations locations = vendorLocationsList.get(getAdapterPosition());
                albumTextView.setText(locations.getVendorName());
                if (selectedVendorLocations != null && selectedVendorLocations.getLocationId() == (locations.getLocationId())) {
                    albumRadioButton.setChecked(true);
                } else {
                    albumRadioButton.setChecked(false);
                }
                tagsView.setOnClickListener(v -> {

                    InputMethodManager imm = (InputMethodManager) tagsView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    albumRadioButton.setChecked(true);
                    selectedVendorLocations = locations;
                    transferLocationDialog.onUpdateSelectVendorLocation(selectedVendorLocations);
                    notifyDataSetChanged();
                });

            } else {
                TransferLocationResponse.Locations locations = locationsList.get(getAdapterPosition());
                albumTextView.setText(locations.getProjectName());
                if (selectedLocation != null && selectedLocation.getPjProjectsId() == (locations.getPjProjectsId())) {
                    albumRadioButton.setChecked(true);
                } else {
                    albumRadioButton.setChecked(false);
                }
                tagsView.setOnClickListener(v -> {
                    InputMethodManager imm = (InputMethodManager) tagsView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    albumRadioButton.setChecked(true);
                    selectedLocation = locations;
                    transferLocationDialog.onUpdateSelectLocation(selectedLocation);
                    notifyDataSetChanged();
                });

            }

        }

    }
}