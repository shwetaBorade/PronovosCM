package com.pronovoscm.utils.dialogs;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.activity.TransferLogFilterActivity;
import com.pronovoscm.adapter.TransferLogLocationAdapter;
import com.pronovoscm.model.response.transferloglocation.Locations;
import com.pronovoscm.persistence.repository.WeatherReportRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransferLogLocationDialog extends DialogFragment implements View.OnClickListener, TransferLogLocationAdapter.updateSelectedLocation {
    @Inject
    WeatherReportRepository mWeatherReportRepository;

    @BindView(R.id.saveTextView)
    TextView saveTextView;
    @BindView(R.id.cardView)
    CardView cardView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.cancelTextView)
    TextView cancelTextView;
    @BindView(R.id.tagsRecyclerView)
    RecyclerView tagsRecyclerView;
    @BindView(R.id.searchView)
    RelativeLayout searchView;
    @BindView(R.id.searchAlbumEditText)
    EditText searchAlbumEditText;
    @BindView(R.id.buttonView)
    LinearLayout buttonView;
    private TransferLogLocationAdapter transferLocationAdapter;
    private Locations selectedLocation;
    private boolean isdropOff;
    private TreeSet<Locations> locationsBackup;
    private List<Locations> locationList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Translucent_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.tags_dialog_view, container, false);
        ButterKnife.bind(this, rootview);
        return rootview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cancelTextView.setOnClickListener(this);
        saveTextView.setOnClickListener(this);
        titleTextView.setText("Select Location");
        searchView.setVisibility(View.VISIBLE);
        isdropOff = getArguments().getBoolean("is_dropOff");
        locationsBackup = (TreeSet<Locations>) getArguments().getSerializable("transfer_location");
        selectedLocation = (Locations) getArguments().getSerializable("selected_location");
        locationList = new ArrayList<>(locationsBackup);
        transferLocationAdapter = new TransferLogLocationAdapter(this, locationList, selectedLocation, isdropOff);
        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tagsRecyclerView.setAdapter(transferLocationAdapter);
        saveTextView.setText("Select");

        searchAlbumEditText.setHint(getString(R.string.search_here));

        searchAlbumEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String string = charSequence.toString();

                locationList.clear();
                for (Locations loc : locationsBackup) {
                    if (!isdropOff && loc.getPickUpLocation().toLowerCase().contains(string.toLowerCase()))
                        locationList.add(loc);
                    if (isdropOff && loc.getDropOffLocation().toLowerCase().contains(string.toLowerCase()))
                        locationList.add(loc);
                }
                transferLocationAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        switch (v.getId()) {
            case R.id.saveTextView:
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if (selectedLocation != null) {
                    ((TransferLogFilterActivity) getActivity()).selectLocation(selectedLocation, isdropOff);
                    dismiss();
                }
                break;
            case R.id.cancelTextView:

                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onUpdateSelectLocation(Locations selectedTag) {
        selectedLocation = selectedTag;

    }


}

