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
import com.pronovoscm.model.response.transferloglocation.Locations;
import com.pronovoscm.utils.dialogs.TransferLogLocationDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransferLogLocationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Locations selectedLocation;
    private List<Locations> locationsList;
    private TransferLogLocationDialog transferLocationDialog;
    private boolean isDropOff;

    public TransferLogLocationAdapter(TransferLogLocationDialog transferLocationDialog, List<Locations> locationsList, Locations selectedLocation, boolean isdropOff) {
        this.locationsList = locationsList;
        this.selectedLocation = selectedLocation;
        this.transferLocationDialog = transferLocationDialog;
        this.isDropOff = isdropOff;
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

    public interface updateSelectedLocation {
        void onUpdateSelectLocation(Locations selectedTag);

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

            Locations locations = locationsList.get(getAdapterPosition());
            albumTextView.setText(isDropOff ? /*locations.getDropoffId() + " - " + */locations.getDropOffLocation() : /*locations.getPickupId() + " - " + */locations.getPickUpLocation());
            if (selectedLocation != null && !isDropOff && selectedLocation.getPickupId() == (locations.getPickupId())) {
                albumRadioButton.setChecked(true);
            } else if (selectedLocation != null && isDropOff && selectedLocation.getDropoffId() == (locations.getDropoffId())) {
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