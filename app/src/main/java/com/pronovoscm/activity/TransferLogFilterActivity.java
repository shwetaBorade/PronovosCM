package com.pronovoscm.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pronovoscm.R;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.data.TransferLogProvider;
import com.pronovoscm.model.request.transferlog.TransferLogFilterRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.transferloglocation.Locations;
import com.pronovoscm.model.response.transferloglocation.TransferLogLocationResponse;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.dialogs.TransferLogLocationDialog;
import com.pronovoscm.utils.ui.CustomProgressBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class TransferLogFilterActivity extends BaseActivity {
    @Inject
    TransferLogProvider transferLogProvider;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.pickupFromDateET)
    TextView pickupFromDateET;
    @BindView(R.id.pickupToDateET)
    TextView pickupToDateET;
    @BindView(R.id.dropoffFromDateET)
    TextView dropoffFromDateET;
    @BindView(R.id.dropoffToDateET)
    TextView dropoffToDateET;
    @BindView(R.id.enterPickupViewET)
    TextView enterPickupViewET;
    @BindView(R.id.enterDropoffViewET)
    TextView enterDropoffViewET;
    @BindView(R.id.filterErrorTextView)
    TextView filterErrorTextView;
    private int projectId;
    private LoginResponse loginResponse;
    private String pickUpFromDate;
    private String pickUpToDate;
    private String dropOffFromDate;
    private String dropOffToDate;
    private int pickupLocation = -1;
    private int dropOffLocation = -1;
    private ArrayList<Locations> logLocations;
    private Locations selectedPickupLocations;
    private Locations selectedDropOffLocations;
    private Locations currentProject;

    @Override
    protected int doGetContentView() {
        return R.layout.transfer_log_filter_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        projectId = getIntent().getIntExtra("project_id", 0);
        pickupLocation = getIntent().getIntExtra("pickup_loc_id", 0);
        dropOffLocation = getIntent().getIntExtra("dropoff_loc_id", 0);
        pickUpFromDate = getIntent().getStringExtra("pickup_from_date");
        pickUpToDate = getIntent().getStringExtra("pickup_to_date");
        dropOffToDate = getIntent().getStringExtra("dropoff_to_date");
        dropOffFromDate = getIntent().getStringExtra("dropoff_from_date");

        pickupFromDateET.setText(TextUtils.isEmpty(pickUpFromDate) ? "" : pickUpFromDate);
        pickupToDateET.setText(TextUtils.isEmpty(pickUpToDate) ? "" : pickUpToDate);
        dropoffFromDateET.setText(TextUtils.isEmpty(dropOffFromDate) ? "" : dropOffFromDate);
        dropoffToDateET.setText(TextUtils.isEmpty(dropOffToDate) ? "" : dropOffToDate);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.INVISIBLE);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        titleTextView.setText(getString(R.string.transfer_log));
        CustomProgressBar.showDialog(this);
        logLocations = new ArrayList<>();
        callTransferLocationAPI();

    }

    @Override
    public void onResume() {
        super.onResume();


    }

    /**
     * Get List
     */
    public void callTransferLocationAPI() {
        TransferLogFilterRequest transferLogFilterRequest = new TransferLogFilterRequest();
        transferLogFilterRequest.setProjectId(projectId);
        transferLogProvider.getTransferLocation(transferLogFilterRequest, new ProviderResult<TransferLogLocationResponse>() {
            @Override
            public void success(TransferLogLocationResponse transferLogResponse) {
                CustomProgressBar.dissMissDialog(TransferLogFilterActivity.this);
                logLocations.addAll(transferLogResponse.getData().getLocations());
//                if (dropOffLocation != -1 || pickupLocation != -1)
                for (Locations loc : logLocations) {
                    if (currentProject == null && (loc.getPickupId() == projectId || loc.getDropoffId() == projectId)) {
                        currentProject = new Locations();

                        currentProject.setDropoffId(projectId);
                        currentProject.setPickupId(projectId);
                        currentProject.setDropOffLocation(loc.getPickupId() == projectId ? loc.getPickUpLocation() : loc.getDropOffLocation());
                        currentProject.setPickUpLocation(loc.getPickupId() == projectId ? loc.getPickUpLocation() : loc.getDropOffLocation());
                    }
                    if (loc.getDropoffId() == dropOffLocation) {
                        selectLocation(loc, true);
                    }
                    if (loc.getPickupId() == pickupLocation) {
                        selectLocation(loc, false);
                    }
                }

            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(TransferLogFilterActivity.this);
                startActivity(new Intent(TransferLogFilterActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(TransferLogFilterActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(TransferLogFilterActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();

            }

            @Override
            public void failure(String message) {

                CustomProgressBar.dissMissDialog(TransferLogFilterActivity.this);
                showMessageAlert(TransferLogFilterActivity.this, message, getString(R.string.ok));

            }
        }, loginResponse);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
        } else {
            offlineTextView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @OnClick(R.id.leftImageView)
    public void onBackClick() {
        super.onBackPressed();
    }

    public void hideKeyboard() {
        if (this != null && this.getWindow() != null && this.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @OnClick(R.id.pickupFromDateView)
    public void onPickupFromDateView() {
        Calendar calendar = Calendar.getInstance();
        if (!TextUtils.isEmpty(pickupFromDateET.getText().toString())) {
            Date date = DateFormatter.getDateFromString(pickupFromDateET.getText().toString());
            calendar.setTime(date);
        }
        DatePickerDialog.OnDateSetListener myDateListener = (arg0, arg1, arg2, arg3) -> {
            arg2 = arg2 + 1;
            pickupFromDateET.setText((arg2 < 10 ? "0" : "") + arg2 + "/" + (arg3 < 10 ? "0" : "") + arg3 + "/" + arg1);
            filterErrorTextView.setText("");
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, myDateListener, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH),
                calendar.get(calendar.DAY_OF_MONTH));
//        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
     /*   if (!TextUtils.isEmpty(pickupToDateET.getText().toString())) {
            Date date = DateFormatter.getDateFromString(pickupToDateET.getText().toString());
            datePickerDialog.getDatePicker().setMaxDate(date.getTime());
        }
     */
        datePickerDialog.show();

    }

    @OnClick(R.id.pickupToDateView)
    public void onPickupToDateView() {
        Calendar calendar = Calendar.getInstance();
        if (!TextUtils.isEmpty(pickupToDateET.getText().toString())) {
            Date date = DateFormatter.getDateFromString(pickupToDateET.getText().toString());
            calendar.setTime(date);
        }
        DatePickerDialog.OnDateSetListener myDateListener = (arg0, arg1, arg2, arg3) -> {
            arg2 = arg2 + 1;
            pickupToDateET.setText((arg2 < 10 ? "0" : "") + arg2 + "/" + (arg3 < 10 ? "0" : "") + arg3 + "/" + arg1);
            filterErrorTextView.setText("");
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, myDateListener, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH),
                calendar.get(calendar.DAY_OF_MONTH));
//        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
        /*if (!TextUtils.isEmpty(pickupFromDateET.getText().toString())) {
            Date date = DateFormatter.getDateFromString(pickupFromDateET.getText().toString());
            datePickerDialog.getDatePicker().setMinDate(date.getTime());
        }*/
        datePickerDialog.show();

    }

    @OnClick(R.id.dropoffFromDateView)
    public void onDropOffFromDateView() {
        Calendar calendar = Calendar.getInstance();
        if (!TextUtils.isEmpty(dropoffFromDateET.getText().toString())) {
            Date date = DateFormatter.getDateFromString(dropoffFromDateET.getText().toString());
            calendar.setTime(date);
        }
        DatePickerDialog.OnDateSetListener myDateListener = (arg0, arg1, arg2, arg3) -> {
            arg2 = arg2 + 1;
            dropoffFromDateET.setText((arg2 < 10 ? "0" : "") + arg2 + "/" + (arg3 < 10 ? "0" : "") + arg3 + "/" + arg1);
            filterErrorTextView.setText("");
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, myDateListener, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH),
                calendar.get(calendar.DAY_OF_MONTH));
//        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
      /*  if (!TextUtils.isEmpty(dropoffToDateET.getText().toString())) {
            Date date = DateFormatter.getDateFromString(dropoffToDateET.getText().toString());
            datePickerDialog.getDatePicker().setMaxDate(date.getTime());
        }
      */
        datePickerDialog.show();

    }

    @OnClick(R.id.dropoffToDateView)
    public void onDropoffToDateView() {
        Calendar calendar = Calendar.getInstance();
        if (!TextUtils.isEmpty(dropoffToDateET.getText().toString())) {
            Date date = DateFormatter.getDateFromString(dropoffToDateET.getText().toString());
            calendar.setTime(date);
        }
        DatePickerDialog.OnDateSetListener myDateListener = (arg0, arg1, arg2, arg3) -> {
            arg2 = arg2 + 1;
            dropoffToDateET.setText((arg2 < 10 ? "0" : "") + arg2 + "/" + (arg3 < 10 ? "0" : "") + arg3 + "/" + arg1);
            filterErrorTextView.setText("");
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, myDateListener, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH),
                calendar.get(calendar.DAY_OF_MONTH));

       /* if (!TextUtils.isEmpty(dropoffFromDateET.getText().toString())) {
            Date date = DateFormatter.getDateFromString(dropoffFromDateET.getText().toString());
            datePickerDialog.getDatePicker().setMinDate(date.getTime());
        }*/
//        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();

    }


    @OnClick(R.id.applyTextView)
    public void onApplyView() {
        if (!NetworkService.isNetworkAvailable(this)) {
            showMessageAlert(TransferLogFilterActivity.this, getString(R.string.internet_connection_check), getString(R.string.ok));
            return;
        }


        if (pickupLocation == -1 && dropOffLocation == -1 && TextUtils.isEmpty(pickupFromDateET.getText().toString()) && TextUtils.isEmpty(pickupToDateET.getText().toString()) && TextUtils.isEmpty(dropoffToDateET.getText().toString()) && TextUtils.isEmpty(dropoffFromDateET.getText().toString())) {
            filterErrorTextView.setText("Please select at least one option to apply filter.");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("pickup_loc_id", pickupLocation)
                .putExtra("dropoff_loc_id", dropOffLocation)
                .putExtra("pickup_from_date", pickupFromDateET.getText().toString())
                .putExtra("pickup_to_date", pickupToDateET.getText().toString())
                .putExtra("dropoff_to_date", dropoffToDateET.getText().toString())
                .putExtra("dropoff_from_date", dropoffFromDateET.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick(R.id.enterPickupView)
    public void onPickupLocationView() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        TransferLogLocationDialog tagsDialog = new TransferLogLocationDialog();
        Bundle bundle = new Bundle();
        TreeSet<Locations> locations = new TreeSet<>((l1, l2) -> l1.getPickUpLocation().compareTo(l2.getPickUpLocation()));
        if (selectedDropOffLocations != null && selectedDropOffLocations.getDropoffId() != projectId) {
            for (Locations loc : logLocations) {
                if (loc.getPickupId() != selectedDropOffLocations.getDropoffId()) {
                    locations.add(loc);
                }
            }
        } else {
            locations.addAll(logLocations);
        }
        bundle.putSerializable("transfer_location", locations);
        bundle.putSerializable("selected_location", selectedPickupLocations);
        tagsDialog.setCancelable(false);
        tagsDialog.setArguments(bundle);
        tagsDialog.show(ft, "");
    }

    @OnClick(R.id.enterDropoffView)
    public void onDropOffLocationView() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        TransferLogLocationDialog tagsDialog = new TransferLogLocationDialog();
        Bundle bundle = new Bundle();
        TreeSet<Locations> locations = new TreeSet<>((l1, l2) -> l1.getDropOffLocation().compareTo(l2.getDropOffLocation()));
        if (selectedPickupLocations != null && selectedPickupLocations.getPickupId() != projectId) {
            for (Locations loc : logLocations) {
                if (loc.getDropoffId() != selectedPickupLocations.getPickupId()) {
                    locations.add(loc);
                }
            }
        } else {
            locations.addAll(logLocations);
        }

        bundle.putSerializable("transfer_location", locations);
        bundle.putBoolean("is_dropOff", true);
        bundle.putSerializable("selected_location", selectedDropOffLocations);
        tagsDialog.setCancelable(false);
        tagsDialog.setArguments(bundle);
        tagsDialog.show(ft, "");
    }


    @OnClick(R.id.clearFiltersTextView)
    public void onClearFiltersView() {
        if (!NetworkService.isNetworkAvailable(this)) {
            showMessageAlert(TransferLogFilterActivity.this, getString(R.string.internet_connection_check), getString(R.string.ok));
            return;
        }

        pickupLocation = -1;
        dropOffLocation = -1;
        dropoffFromDateET.setText("");
        dropoffToDateET.setText("");
        pickupFromDateET.setText("");
        pickupToDateET.setText("");
        enterPickupViewET.setText("");
        enterDropoffViewET.setText("");
        selectedDropOffLocations = null;
        selectedPickupLocations = null;
        Intent intent = new Intent();
        intent.putExtra("pickup_loc_id", pickupLocation)
                .putExtra("dropoff_loc_id", dropOffLocation)
                .putExtra("pickup_from_date", pickupFromDateET.getText().toString())
                .putExtra("pickup_to_date", pickupToDateET.getText().toString())
                .putExtra("dropoff_to_date", dropoffToDateET.getText().toString())
                .putExtra("dropoff_from_date", dropoffFromDateET.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    public void selectLocation(Locations locations, boolean isDropOff) {
        if (isDropOff) {
            dropOffLocation = locations.getDropoffId();
            selectedDropOffLocations = locations;
            enterDropoffViewET.setText(locations.getDropOffLocation());
            if (locations.getDropoffId() != projectId) {
                enterPickupViewET.setText(currentProject.getPickUpLocation());
                selectedPickupLocations = currentProject;
                pickupLocation = currentProject.getPickupId();
            }
            if (selectedPickupLocations != null && selectedPickupLocations.getPickupId() == projectId && locations.getDropoffId() == projectId) {
                enterPickupViewET.setText("");
                selectedPickupLocations = null;
                pickupLocation = -1;
            }
        } else {
            enterPickupViewET.setText(locations.getPickUpLocation());
            selectedPickupLocations = locations;
            pickupLocation = locations.getPickupId();
            if (locations.getPickupId() != projectId) {

                dropOffLocation = currentProject.getDropoffId();
                selectedDropOffLocations = currentProject;
                enterDropoffViewET.setText(currentProject.getDropOffLocation());
            }

            if (selectedDropOffLocations != null && selectedDropOffLocations.getDropoffId() == projectId && locations.getPickupId() == projectId) {
                enterDropoffViewET.setText("");
                selectedDropOffLocations = null;
                dropOffLocation = -1;
            }
        }
        filterErrorTextView.setText("");

    }

}

