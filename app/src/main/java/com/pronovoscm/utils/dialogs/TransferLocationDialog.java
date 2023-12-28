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
import com.pronovoscm.adapter.TransferLocationAdapter;
import com.pronovoscm.model.response.transferlocation.TransferLocationResponse;
import com.pronovoscm.model.response.transferlocation.TransferLocationVendorResponse;
import com.pronovoscm.persistence.repository.WeatherReportRepository;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransferLocationDialog extends DialogFragment implements View.OnClickListener, TransferLocationAdapter.updateWeatherCondition {
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
    private TransferLocationAdapter transferLocationAdapter;
    private ArrayList<TransferLocationResponse.Locations> locationsArrayList;
    private ArrayList<TransferLocationVendorResponse.Locations> vendorLocationsArrayList;
    private ArrayList<TransferLocationResponse.Locations> searchLocationsArrayList;
    private ArrayList<TransferLocationVendorResponse.Locations> searchVendorLocationsArrayList;
    private TransferLocationResponse.Locations selectedLocation;
    private TransferLocationVendorResponse.Locations selectedVendorLocation;
    private boolean isVendor;

    public static ArrayList<TransferLocationResponse.Locations> cloneListLocation(ArrayList<TransferLocationResponse.Locations> imageTags) {
        ArrayList<TransferLocationResponse.Locations> clonedList = new ArrayList<>(imageTags.size());
        for (TransferLocationResponse.Locations imageTag : imageTags) {
            clonedList.add(imageTag);
        }
        return clonedList;
    }

    public static ArrayList<TransferLocationVendorResponse.Locations> cloneList(ArrayList<TransferLocationVendorResponse.Locations> imageTags) {
        ArrayList<TransferLocationVendorResponse.Locations> clonedList = new ArrayList<>(imageTags.size());
        for (TransferLocationVendorResponse.Locations imageTag : imageTags) {
            clonedList.add(imageTag);
        }
        return clonedList;
    }

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
        isVendor = getArguments().getBoolean("is_vendor");
        locationsArrayList = new ArrayList<>();
        vendorLocationsArrayList = new ArrayList<>();
        searchLocationsArrayList = new ArrayList<>();
        searchVendorLocationsArrayList = new ArrayList<>();
        if (isVendor) {
            vendorLocationsArrayList = cloneList((ArrayList<TransferLocationVendorResponse.Locations>) getArguments().getSerializable("transfer_location"));
            searchVendorLocationsArrayList = cloneList((ArrayList<TransferLocationVendorResponse.Locations>) getArguments().getSerializable("transfer_location"));
            selectedVendorLocation = (TransferLocationVendorResponse.Locations) getArguments().getSerializable("selected_location");
            transferLocationAdapter = new TransferLocationAdapter(this, searchVendorLocationsArrayList, selectedVendorLocation);
        } else {
            locationsArrayList = cloneListLocation((ArrayList<TransferLocationResponse.Locations>) getArguments().getSerializable("transfer_location"));
            searchLocationsArrayList = cloneListLocation((ArrayList<TransferLocationResponse.Locations>) getArguments().getSerializable("transfer_location"));
            selectedLocation = (TransferLocationResponse.Locations) getArguments().getSerializable("selected_location");
            transferLocationAdapter = new TransferLocationAdapter(this, searchLocationsArrayList, selectedLocation);
        }


        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tagsRecyclerView.setAdapter(transferLocationAdapter);
        saveTextView.setText("Select");

        /*cardView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();

                cardView.getWindowVisibleDisplayFrame(r);

                int heightDiff = view.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > 100) {
                    //enter your code here
                    buttonView.setVisibility(View.GONE);

                }else{
                    //enter code for hid
                    buttonView.setVisibility(View.VISIBLE);

                }
            }
        });*/
        searchAlbumEditText.setHint(getString(R.string.search_here));
        searchAlbumEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String string = charSequence.toString();
                searchVendorLocationsArrayList.clear();
                searchLocationsArrayList.clear();
                if (isVendor) {
                    if (string.length() > 0) {
                        for (int j = 0; j < vendorLocationsArrayList.size(); j++) {
                            if (vendorLocationsArrayList.get(j).getVendorName().toLowerCase().contains(string.toLowerCase())) {
                                searchVendorLocationsArrayList.add(vendorLocationsArrayList.get(j));
                            }
                        }
                    } else {
                        searchVendorLocationsArrayList.addAll(vendorLocationsArrayList);
                    }
                } else {

                    if (string.length() > 0) {
                        for (int j = 0; j < locationsArrayList.size(); j++) {
                            if (locationsArrayList.get(j).getProjectName().toLowerCase().contains(string.toLowerCase())) {
                                searchLocationsArrayList.add(locationsArrayList.get(j));
                            }
                        }
                    } else {
                        searchLocationsArrayList.addAll(locationsArrayList);
                    }

                }
                if (transferLocationAdapter != null) {
                    transferLocationAdapter.notifyDataSetChanged();
                }
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
                    EventBus.getDefault().post(selectedLocation);
                    dismiss();
                } else if (selectedVendorLocation != null) {
                    EventBus.getDefault().post(selectedVendorLocation);
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
    public void onUpdateSelectLocation(TransferLocationResponse.Locations selectedTag) {
        selectedLocation = selectedTag;

    }

    @Override
    public void onUpdateSelectVendorLocation(TransferLocationVendorResponse.Locations selectedTag) {
        selectedVendorLocation = selectedTag;

    }
}

