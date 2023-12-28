package com.pronovoscm.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.pronovoscm.R;
import com.pronovoscm.activity.CreateTransfersActivity;
import com.pronovoscm.adapter.AutocompleteSelectContactAdapter;
import com.pronovoscm.adapter.TradeAdapter;
import com.pronovoscm.model.UnloadingEnum;
import com.pronovoscm.model.request.transfercontact.TransferContactRequest;
import com.pronovoscm.model.request.transferrequest.TransferRequest;
import com.pronovoscm.model.response.transfercontacts.Contacts;
import com.pronovoscm.model.response.transfercontacts.TransferContactsResponse;
import com.pronovoscm.model.response.transferlocation.TransferLocationResponse;
import com.pronovoscm.model.response.transferlocation.TransferLocationVendorResponse;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.dialogs.TransferLocationDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class CreateTransferDropoffFragment extends Fragment {
    @BindView(R.id.dropoffLocationSpinnewView)
    RelativeLayout dropoffLocationSpinnewView;
    @BindView(R.id.requestView)
    RelativeLayout requestView;
    @BindView(R.id.nextTextView)
    TextView nextTextView;
    @BindView(R.id.selectTextView)
    TextView selectTextView;
    @BindView(R.id.selectMethodTextView)
    TextView selectMethodTextView;
    @BindView(R.id.switchLocationTextViewView)
    TextView switchLocationTextViewView;
    @BindView(R.id.dropoffNameAutoCompleteTextView)
    AutoCompleteTextView dropoffNameAutoCompleteTextView;
    @BindView(R.id.dropoffNumberNewET)
    EditText dropoffNumberNewET;
    @BindView(R.id.switchLocation)
    Switch switchLocation;
    @BindView(R.id.switchRounTrip)
    Switch switchRounTrip;
    @BindView(R.id.dropoffDateViewET)
    EditText dropoffDateViewET;
    @BindView(R.id.dropoffTimeViewET)
    EditText dropoffTimeViewET;
    @BindView(R.id.dropoffLocationTextView)
    TextView dropoffLocationTextView;
    @BindView(R.id.unloadingMethodSpinner)
    AppCompatSpinner unloadingMethodSpinner;
    @BindView(R.id.dropOffNumberErrorTextView)
    TextView dropOffNumberErrorTextView;
    @BindView(R.id.dropOffNameErrorTextView)
    TextView dropOffNameErrorTextView;
    @BindView(R.id.dropOffDateErrorTextView)
    TextView dropOffDateErrorTextView;
    @BindView(R.id.dropOffTimeErrorTextView)
    TextView dropOffTimeErrorTextView;
    @BindView(R.id.dropoffLocatioErrorTextView)
    TextView dropoffLocatioErrorTextView;
    @BindView(R.id.dropOffUnloadingMethodErrorTextView)
    TextView dropOffUnloadingMethodErrorTextView;
    @BindView(R.id.clickViewTime)
    View clickViewTime;
    @BindView(R.id.clickViewDate)
    View clickViewDate;
    @BindView(R.id.dropoffDateView)
    ConstraintLayout dropoffDateView;
    @BindView(R.id.timeView)
    ConstraintLayout timeView;
    @BindView(R.id.dropoffLocatioView)
    RelativeLayout dropoffLocatioView;
    @BindView(R.id.dropoffNumberView)
    RelativeLayout dropoffNumberView;
    @BindView(R.id.unloadingMethodSpinnewView)
    RelativeLayout unloadingMethodSpinnewView;
    @BindView(R.id.cancelTextView)
    TextView cancelTextView;

    private boolean mFormatting; // a flag that prevents stack overflows.
    private int mAfter;
    private String transferOption;
    private TransferLocationResponse.Locations locations;
    private TransferLocationVendorResponse.Locations vendorLocation;
    private ArrayList<TransferLocationResponse.Locations> locationsArrayList = new ArrayList<>();
    private ArrayList<TransferLocationVendorResponse.Locations> vendorLocationsArrayList = new ArrayList<>();
    private TransferLocationResponse.Locations selectedLocations;
    private TransferLocationVendorResponse.Locations selectedVendorLocations;
    private Date pickUpDateTime = null;
    private Calendar dropOfCalendar = Calendar.getInstance();
    private Calendar pickUpCalendar = Calendar.getInstance();
    private AutocompleteSelectContactAdapter adapter;
    private Contacts jobSitecontact;
    private ArrayList<String> unloadingEnumArrayList;
    private TransferRequest createTransfer;
    private AlertDialog alertDialog;
    private boolean isLocationChanged = false;
    private int transferType = -1;

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ((CreateTransfersActivity) getActivity()).hideKeyboard(getActivity());
            jobSitecontact = (Contacts) adapterView.getItemAtPosition(i);
            Log.i("dropoff", "onLocationCheckedChange:  l " + jobSitecontact.getName());
            dropoffNameAutoCompleteTextView.setText(jobSitecontact.getName());
            if (switchLocation.isChecked()) {
                dropoffNumberNewET.setText(((CreateTransfersActivity) getActivity()).addMasking(jobSitecontact.getPhone1() != null ? jobSitecontact.getPhone1().replaceAll("-", "") : ""));
            } else {
                dropoffNumberNewET.setText(((CreateTransfersActivity) getActivity()).addMasking(jobSitecontact.getPhone_no() != null ? jobSitecontact.getPhone_no().replaceAll("-", "") : ""));
            }
            dropOffNameErrorTextView.setText("");
            dropOffNumberErrorTextView.setText("");

        }
    };
    private List<Contacts> contactList = new ArrayList<>();

    //    private List<Contacts> jobSitecontactList = new ArrayList<>();
    private int projectId;
    private TradeAdapter tradeAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create_transfer_dropoff_fragment, container, false);
        ButterKnife.bind(this, rootView);
//        ((CreateTransfersActivity) getActivity()).callTransferLocationAPI("projects");
//        ((CreateTransfersActivity) getActivity()).callTransferJobsiteContactAPI("projects");
        //        refreshContactData(((CreateTransfersActivity) getActivity()).getJobSiteTransferContactsResponse());
        dropoffNameAutoCompleteTextView.setText("");
        dropoffNumberNewET.setText("");
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createTransfer = ((CreateTransfersActivity) getActivity()).getCreateTransfer();
        transferType = getActivity().getIntent().getIntExtra(Constants.INTENT_KEY_TRANSFER_TYPE, -1);

        projectId = getActivity().getIntent().getIntExtra("project_id", 0);
        transferOption = getActivity().getIntent().getStringExtra("transfer_option");
        if (transferOption.equals("New Request")) {
        }
        dropoffDateViewET.setKeyListener(null);
        dropoffTimeViewET.setKeyListener(null);
//        jobSitecontactList = new ArrayList<>();

        contactList.clear();
        contactList.addAll(((CreateTransfersActivity) getActivity()).getDropOffjobSitecontactList());
        adapter = new AutocompleteSelectContactAdapter(getActivity(), R.layout.searchable_adapter_item, contactList, createTransfer.getPickupName());
        dropoffNameAutoCompleteTextView.setOnItemClickListener(onItemClickListener);
        dropoffNameAutoCompleteTextView.setAdapter(adapter);
        unloadingEnumArrayList = new ArrayList<>();
        unloadingEnumArrayList.add(null);
        unloadingEnumArrayList.add(UnloadingEnum.FORKLIFT.toString());
        unloadingEnumArrayList.add(UnloadingEnum.CRANE.toString());
        unloadingEnumArrayList.add(UnloadingEnum.OTHER.toString());
        tradeAdapter = new TradeAdapter(getActivity(), R.layout.simple_spinner_item, unloadingEnumArrayList);
        tradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unloadingMethodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos > 0) {
                    createTransfer.setUnloading(pos);
                    updateCreateTransfer();
                    selectMethodTextView.setVisibility(View.GONE);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        unloadingMethodSpinner.setAdapter(tradeAdapter);

        if (createTransfer.getStatus() > 1 || !getActivity().getIntent().getStringExtra("transfer_option").equals("New Request")) {
            requestView.setVisibility(View.GONE);
        }
        dropoffNumberNewET.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mAfter = i2; // flag to detect backspace.

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i("dropoffnumber", "onTextChanged: "+((CreateTransfersActivity) getActivity()).isAllDone());
                if (((CreateTransfersActivity) getActivity()).isAllDone()) {
                    dropOffNumberErrorTextView.setText("");
                    createTransfer.setDropOffPhone(dropoffNumberNewET.getText().toString());
                    updateCreateTransfer();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
               /* if (!mFormatting) {
                    mFormatting = true;
                    // using US formatting.
                    if(mAfter != 0) // in case back space ain't clicked.
                        PhoneNumberUtils.formatNumber(
                                editable,PhoneNumberUtils.getFormatTypeForLocale(Locale.US));
                    mFormatting = false;
                }*/
                int i = dropoffNumberNewET.getText().toString().length();
                if (i > 0) {
                    String lastChar = String.valueOf(dropoffNumberNewET.getText().toString().charAt(dropoffNumberNewET.getText().length() - 1));
                    if (lastChar.equals("-")) {
                        return;
                    }
                }
                int len = 0;
                if (i < 4)
                    len = 0;
                if (i == 4 && len < 5) {
                    len = 5;
                    String ss = editable.toString();
                    String first = ss.substring(0, ss.length() - 1);
                    String last = ss.substring(ss.length() - 1);
                    dropoffNumberNewET.setText(first + "-" + last);
                    dropoffNumberNewET.setSelection(dropoffNumberNewET.getText().length());
                }
                int len2 = 0;
                if (i < 8)
                    len2 = 0;
                if (i == 8 && len2 < 9) {
                    len2 = 9;
                    String ss = editable.toString();
                    String first = ss.substring(0, ss.length() - 1);
                    String last = ss.substring(ss.length() - 1);
                    dropoffNumberNewET.setText(first + "-" + last);
                    dropoffNumberNewET.setSelection(dropoffNumberNewET.getText().length());
                }
            }
        });
        dropoffNameAutoCompleteTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i("dropoffname", "onTextChanged: "+((CreateTransfersActivity) getActivity()).isAllDone()+"  "+dropoffNameAutoCompleteTextView.getText().toString());
                if (((CreateTransfersActivity) getActivity()).isAllDone()) {
                    dropOffNameErrorTextView.setText("");
                    createTransfer.setDropOffName(dropoffNameAutoCompleteTextView.getText().toString());
                    updateCreateTransfer();
                }
            }
        });
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
       /* createTransfer = ((CreateTransfersActivity) getActivity()).getCreateTransfer();
        dropoffNameAutoCompleteTextView.setText(createTransfer.getDropOffName());
        dropoffNumberNewET.setText(createTransfer.getDropOffPhone());
       callData();
       */
        callData();
        setData();

    }

    private void callData() {
        createTransfer = ((CreateTransfersActivity) getActivity()).getCreateTransfer();

        if (createTransfer.getDropoffVendorStatus() == 0 && createTransfer.getPickupVendorStatus() == 0 && createTransfer.getPickupLocation() == createTransfer.getDropOffLocation()) {
            selectedLocations = null;
            createTransfer.setDropOffLocation(0);
            Log.i("dropoff", "onLocationCheckedChange:  empty a");
            dropoffNameAutoCompleteTextView.setText("");
            createTransfer.setDropOffName("");
            createTransfer.setDropOffPhone("");
            updateCreateTransfer();
            dropoffNumberNewET.setText("");
            selectedLocations = null;
            contactList.clear();
            ((CreateTransfersActivity) getActivity()).clearDropOffjobSitecontactList();
            selectedVendorLocations = null;
        } else if ((createTransfer.getPickupLocation() != projectId || createTransfer.getPickupVendorStatus() == 1)) {
            if (createTransfer.getDropoffVendorStatus() == 1 || createTransfer.getDropOffLocation() != projectId) {
                Log.i("dropoff", "onLocationCheckedChange:  c empty");
                createTransfer.setDropOffName("");
                createTransfer.setDropOffPhone("");
                updateCreateTransfer();
                dropoffNameAutoCompleteTextView.setText("");
                createTransfer.setDropOffLocation(0);
                dropoffNumberNewET.setText("");
                selectedVendorLocations = null;
                contactList.clear();
                ((CreateTransfersActivity) getActivity()).clearDropOffjobSitecontactList();
                selectedLocations = null;
            } else {
                dropoffNameAutoCompleteTextView.setText(createTransfer.getDropOffName());
                dropoffNumberNewET.setText(((CreateTransfersActivity) getActivity()).addMasking(createTransfer.getDropOffPhone() != null ? createTransfer.getDropOffPhone().replaceAll("-", "") : ""));
            }
        } else {
            dropoffNameAutoCompleteTextView.setText(createTransfer.getDropOffName());
            dropoffNumberNewET.setText(((CreateTransfersActivity) getActivity()).addMasking(createTransfer.getDropOffPhone() != null ? createTransfer.getDropOffPhone().replaceAll("-", "") : ""));
        }
        Log.i("dropoff", "onLocationCheckedChange: 22222 " + createTransfer.getDropOffName());

        refreshData(((CreateTransfersActivity) getActivity()).getTransferOverviewResponse());

    }

    private void setData() {
        if (selectedLocations == null && createTransfer.getDropOffLocation() != 0 && createTransfer.getDropoffVendorStatus() != 1) {
            ((CreateTransfersActivity) getActivity()).callTransferJobsiteContactAPI("projects", createTransfer.getDropOffLocation());
        }

        if (createTransfer.getDropoffVendorStatus() == 1 && !(createTransfer.getPickupVendorStatus() == 1 || createTransfer.getPickupLocation() != projectId)) {
//            ((CreateTransfersActivity) getActivity()).callTransferLocationVendorAPI("vendors");
            switchLocation.setChecked(false);
            refreshData(((CreateTransfersActivity) getActivity()).getTransferOverviewVendorResponse());
        } else {
            switchLocation.setChecked(true);
            refreshData(((CreateTransfersActivity) getActivity()).getTransferOverviewResponse());

  /*     ((CreateTransfersActivity) getActivity()).callTransferLocationAPI("projects");
            ((CreateTransfersActivity) getActivity()).callTransferJobsiteContactAPI("projects");
       */
//            refreshContactData(((CreateTransfersActivity) getActivity()).getJobSiteTransferContactsResponse());
        }

        contactList.clear();
        contactList = ((CreateTransfersActivity) getActivity()).getDropOffjobSitecontactList();

        if (selectedLocations != null) {
            dropoffLocationTextView.setText(selectedLocations.getProjectName());
            ((CreateTransfersActivity) getActivity()).callTransferJobsiteContactAPI("projects", selectedLocations.getPjProjectsId());
            selectTextView.setVisibility(View.GONE);
        } else if (selectedVendorLocations != null && !(createTransfer.getPickupVendorStatus() == 1 || createTransfer.getPickupLocation() != projectId)) {
            dropoffLocationTextView.setText(selectedVendorLocations.getVendorName());
            selectTextView.setVisibility(View.GONE);
            TransferContactRequest transferOverviewRequest = new TransferContactRequest();
            transferOverviewRequest.setVendorId(selectedVendorLocations.getVendorId());
            transferOverviewRequest.setType("vendors");
            transferOverviewRequest.setLocationId(selectedVendorLocations.getLocationId());
            ((CreateTransfersActivity) getActivity()).callTransferContactAPI(transferOverviewRequest);
        }

        if (createTransfer.getPickupVendorStatus() == 1 || createTransfer.getPickupLocation() != projectId) {
            switchLocation.setClickable(false);
            switchLocation.setEnabled(false);
//            switchLocation.setChecked(true);
            if (selectedLocations == null) {
                dropoffLocationTextView.setText("");
            }
        }
        if (TextUtils.isEmpty(createTransfer.getPickupDate()) || TextUtils.isEmpty(createTransfer.getPickupTime())) {

        } else {
            Date date = DateFormatter.getDateFromTransferDate(createTransfer.getPickupDate() + " 00:00 AM");
            pickUpCalendar.setTime(date);
        }
        dropoffDateViewET.setText(createTransfer.getDeliveryDate());
        dropoffTimeViewET.setText(createTransfer.getDropoffTime());
//        dropoffNameAutoCompleteTextView.setText(createTransfer.getDropOffName());
//        dropoffNumberNewET.setText(((CreateTransfersActivity) getActivity()).addMasking(createTransfer.getDropOffPhone() != null ? createTransfer.getDropOffPhone().replaceAll("-", "") : ""));
        unloadingMethodSpinner.setSelection(createTransfer.getUnloading());
        switchRounTrip.setChecked(createTransfer.getRoundTrip() == 1);

        if ((createTransfer.getStatus() >= 1 && (createTransfer.getStatus() != 2 || (createTransfer.getDropoffVendorStatus() != 1 && createTransfer.getPickupLocation() != getActivity().getIntent().getIntExtra("project_id", 0)))) ||/*createTransfer.getTransferId() != 0 || */(createTransfer.getEquipment() != null && createTransfer.getEquipment().size() > 0)) {
            if (!TextUtils.isEmpty(createTransfer.getDeliveryDate()) && !TextUtils.isEmpty(createTransfer.getDropoffTime())) {
                Date date1 = DateFormatter.getDateFromTransferDate(createTransfer.getDeliveryDate() + " " + createTransfer.getDropoffTime());
                dropOfCalendar.setTime(date1);
            } else if (!TextUtils.isEmpty(createTransfer.getDropoffTime())) {
                Date date1 = DateFormatter.getTimeFromTransferDate(createTransfer.getDropoffTime());
                dropOfCalendar.setTime(date1);
            }
            if (createTransfer.getStatus() >= 1 && (createTransfer.getStatus() != 2 ||
                    (createTransfer.getDropoffVendorStatus() != 1 &&
                            createTransfer.getPickupLocation() != getActivity().getIntent().getIntExtra("project_id", 0)))) {
                dropoffDateView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                timeView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                dropoffLocatioView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                dropoffNumberView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                unloadingMethodSpinnewView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                cancelTextView.setVisibility(View.GONE);
                clickViewTime.setClickable(false);
                clickViewDate.setClickable(false);
                dropoffNameAutoCompleteTextView.setClickable(false);
                dropoffNameAutoCompleteTextView.setFocusableInTouchMode(false);
                dropoffNumberNewET.setFocusableInTouchMode(false);
                switchRounTrip.setClickable(false);
                switchRounTrip.setEnabled(false);
                unloadingMethodSpinner.setFocusableInTouchMode(false);
                unloadingMethodSpinner.setClickable(false);
                unloadingMethodSpinner.setEnabled(false);
            }

            if (createTransfer.getStatus() == 3 && createTransfer.getDropoffVendorStatus() == 0 && createTransfer.getDropOffLocation() == getActivity().getIntent().getIntExtra("project_id", 0)) {
                cancelTextView.setVisibility(View.VISIBLE);
            }

            switchLocation.setEnabled(false);
            switchLocation.setClickable(false);
            switchRounTrip.setChecked(createTransfer.getRoundTrip() == 1);
//            switchLocation.setChecked(createTransfer.getDropOffLocation() == 0);
            unloadingMethodSpinner.setSelection(createTransfer.getUnloading());
            dropoffLocationSpinnewView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
            dropoffLocationSpinnewView.setClickable(false);

        }
        if (createTransfer.isInterofficeTransfer()) {
            dropoffLocationSpinnewView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
            dropoffLocationSpinnewView.setClickable(false);
            dropoffLocationTextView.setText(createTransfer.getDropoffLocationName());
            selectTextView.setVisibility(View.GONE);
        }
        ((CreateTransfersActivity) getActivity()).setAllDone(true);

    }

    @OnCheckedChanged(R.id.switchRounTrip)
    public void onRoundTripCheckedChange() {
        createTransfer.setRoundTrip(switchRounTrip.isChecked() ? 1 : 0);
        updateCreateTransfer();
    }

    @OnCheckedChanged(R.id.switchLocation)
    public void onLocationCheckedChange() {
        if (createTransfer.getPickupVendorStatus() != 1) {
            if (((CreateTransfersActivity) getActivity()).isAllDone()) {
                Log.i("dropoff", "onLocationCheckedChange: d empty");
                dropoffNameAutoCompleteTextView.setText("");
                selectedVendorLocations = null;
                dropoffNumberNewET.setText("");
                contactList.clear();
                createTransfer.setDropOffLocation(0);
                selectedLocations = null;
                ((CreateTransfersActivity) getActivity()).setCreateTransfer(createTransfer);
                ((CreateTransfersActivity) getActivity()).clearDropOffjobSitecontactList();
            }
            createTransfer.setDropoffVendorStatus(switchLocation.isChecked() ? 0 : 1);
            if (((CreateTransfersActivity) getActivity()).getTransferDetails() != null && ((CreateTransfersActivity) getActivity()).getTransferDetails().getDropoffIsVendor() != createTransfer.getDropoffVendorStatus()) {
                isLocationChanged = true;
            }
            updateCreateTransfer();
            selectTextView.setVisibility(View.VISIBLE);
            dropoffLocationTextView.setText("");
           /* if (createTransfer.getTransferId() == 0) {
                dropoffNameAutoCompleteTextView.setText("");
                dropoffNumberNewET.setText("");
            }
           */
            dropOffNameErrorTextView.setText("");
            dropOffNumberErrorTextView.setText("");

            adapter.notifyDataSetChanged();
            adapter.clear();
            adapter = new AutocompleteSelectContactAdapter(getActivity(), R.layout.searchable_adapter_item, contactList, createTransfer.getPickupName());
            dropoffNameAutoCompleteTextView.setOnItemClickListener(onItemClickListener);
            dropoffNameAutoCompleteTextView.setAdapter(adapter);
            if (switchLocation.isChecked()) {
                switchLocationTextViewView.setText(getString(R.string.jobsite));
               /* ((CreateTransfersActivity) getActivity()).callTransferLocationAPI("projects");
                ((CreateTransfersActivity) getActivity()).callTransferJobsiteContactAPI("projects");*/
                refreshData(((CreateTransfersActivity) getActivity()).getTransferOverviewResponse());
//                refreshContactData(((CreateTransfersActivity) getActivity()).getJobSiteTransferContactsResponse());
            } else {
                switchLocationTextViewView.setText(getString(R.string.vendor));
                refreshData(((CreateTransfersActivity) getActivity()).getTransferOverviewVendorResponse());

//                ((CreateTransfersActivity) getActivity()).callTransferLocationVendorAPI("vendors");
            }
        } else {
            switchLocation.setChecked(true);
        }
    }

    public boolean checkValidation() {
        if (createTransfer.getStatus() == 1 || createTransfer.getStatus() == 4 || (((createTransfer.getVendorLocation()) == 1 || createTransfer.getPickupLocation() != projectId) && createTransfer.getStatus() == 2) || createTransfer.getStatus() == 3 || createTransfer.getStatus() == 5)
            return true;

        boolean isValid = true;
        if (TextUtils.isEmpty(dropoffDateViewET.getText().toString())) {
            dropOffDateErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(dropoffTimeViewET.getText().toString())) {
            dropOffTimeErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(dropoffLocationTextView.getText().toString()) && (((CreateTransfersActivity) getActivity()).getTransferDetails() == null || isLocationChanged)) {
            dropoffLocatioErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(dropoffNameAutoCompleteTextView.getText().toString())) {
            dropOffNameErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(dropoffNumberNewET.getText().toString())) {
            dropOffNumberErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        Calendar cal=Calendar.getInstance();
        if (isValid) {
            Date date1 = DateFormatter.getDateFromTransferDate(dropoffDateViewET.getText().toString() + " 00:00 AM");
            cal.setTime(date1);
        }
        if (isValid && pickUpCalendar.getTime().compareTo(cal.getTime()) > 0) {

            String dateDropoff = dropoffDateViewET.getText().toString();
            String timeDropoff = dropoffTimeViewET.getText().toString();
            dropoffDateViewET.setText(((CreateTransfersActivity) getActivity()).getTransferDetails() != null ? ((CreateTransfersActivity) getActivity()).getTransferDetails().getDropoffDate() : dateDropoff);
            dropoffTimeViewET.setText(((CreateTransfersActivity) getActivity()).getTransferDetails() != null ? ((CreateTransfersActivity) getActivity()).getTransferDetails().getDropoffTime() : timeDropoff);
            showMessageAlert(getActivity(), "Drop-off date should be greater than pick-up date.", getString(R.string.ok));
            isValid = false;
        }

        if (isValid && createTransfer.getPickupVendorStatus() == 0 && (createTransfer.getPickupLocation() != projectId && (selectedLocations == null || selectedLocations.getPjProjectsId() != projectId))) {
            isValid = false;

            showMessageAlert(getActivity(), getString(R.string.locaion_validation_message), getString(R.string.ok));
        } else if (isValid && createTransfer.getPickupVendorStatus() == 1 && (selectedLocations == null || selectedLocations.getPjProjectsId() != projectId)) {
            isValid = false;
            showMessageAlert(getActivity(), getString(R.string.locaion_validation_message), getString(R.string.ok));

        }
        if (isValid && (createTransfer.getPickupVendorStatus() == 0 && createTransfer.getPickupLocation() == projectId) && (createTransfer.getDropoffVendorStatus() == 0 && selectedLocations != null && selectedLocations.getPjProjectsId() == projectId)) {
            isValid = false;
            ((CreateTransfersActivity) getActivity()).showMessageAlert(getContext(), "The pick-up location and drop-off location must be different.", getString(R.string.ok), false);
        }

        return isValid;
    }

    /**
     * Alert to show message
     *
     * @param context
     * @param message
     * @param positiveButtonText
     */
    public void showMessageAlert(final Context context, String message, String positiveButtonText) {


        try {
            if (alertDialog == null || !alertDialog.isShowing()) {
                alertDialog = new AlertDialog.Builder(context).create();
            }
            alertDialog.setMessage(message);


            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, positiveButtonText, (dialog, which) -> {
                alertDialog.dismiss();
            });
            if (alertDialog != null && !alertDialog.isShowing()) {

                alertDialog.setCancelable(false);
                alertDialog.show();
            }


        } catch (Exception e) {
        }

    }


    @OnClick(R.id.nextTextView)
    public void onClickNext() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(nextTextView.getWindowToken(), 0);
//        if (createTransfer.getStatus() == 5 || checkValidation()) {

        if (!NetworkService.isNetworkAvailable(getActivity())) {
            showMessageAlert(getActivity(), getString(R.string.internet_connection_check_transfer_overview), getString(R.string.ok));
      return;  }
        if (createTransfer.getStatus() == 1 || createTransfer.getStatus() == 4 || (((createTransfer.getVendorLocation()) == 1 || createTransfer.getPickupLocation() != projectId) && createTransfer.getStatus() == 2) || createTransfer.getStatus() == 3 || createTransfer.getStatus() == 5 || checkValidation()) {
            createTransfer.setDeliveryDate(dropoffDateViewET.getText().toString());
            createTransfer.setDropoffTime(dropoffTimeViewET.getText().toString());
            createTransfer.setDropoffVendorStatus(switchLocation.isChecked() ? 0 : 1);
            createTransfer.setRoundTrip(switchRounTrip.isChecked() ? 1 : 0);
            createTransfer.setUnloading(unloadingMethodSpinner.getSelectedItemPosition());
            createTransfer.setDropOffName(dropoffNameAutoCompleteTextView.getText().toString());
            if (createTransfer.getDropoffVendorStatus() == 0) {
                createTransfer.setDropOffLocation(selectedLocations != null ? selectedLocations.getPjProjectsId() : (((CreateTransfersActivity) getActivity()).getTransferDetails()) != null ? ((CreateTransfersActivity) getActivity()).getTransferDetails().getDropoffLocation() : 0);
                createTransfer.setDropoffVendorStatus(0);
                createTransfer.setDropOffPhone(dropoffNumberNewET.getText().toString());
            } else {
                createTransfer.setDropoffVendorStatus(1);
                createTransfer.setDropOffPhone(dropoffNumberNewET.getText().toString());
                createTransfer.setDropOffLocation(selectedVendorLocations != null ? selectedVendorLocations.getVendorId() : (((CreateTransfersActivity) getActivity()).getTransferDetails()) != null ? ((CreateTransfersActivity) getActivity()).getTransferDetails().getDropoffLocation() : 0);
                createTransfer.setVendorLocation(selectedVendorLocations != null ? selectedVendorLocations.getLocationId() : (((CreateTransfersActivity) getActivity()).getTransferDetails()) != null ? ((CreateTransfersActivity) getActivity()).getTransferDetails().getVendorLocation() : 0);
            }
            ((CreateTransfersActivity) getActivity()).setDropOffDone();
            if (createTransfer.getStatus() > 0 || !getActivity().getIntent().getStringExtra("transfer_option").equals("New Request")) {
                ((CreateTransfersActivity) getActivity()).openEquipment();
            } else {
                if (((CreateTransfersActivity) getActivity()).getTransferDetails() == null || !((CreateTransfersActivity) getActivity()).isUpdateCreateTransferData()) {
                    ((CreateTransfersActivity) getActivity()).callTransferRequestAPI(createTransfer, true);
                } else {
                    ((CreateTransfersActivity) getActivity()).openEquipment();
                }
            }
        }
    }

    public void refreshData(TransferLocationResponse transferOverviewResponse) {
        TransferLocationResponse.Locations loc = null;
        if (transferOverviewResponse != null) {
            locationsArrayList.clear();
            locationsArrayList.addAll(cloneLocationList(transferOverviewResponse.getData().getLocations()));
            if (createTransfer != null && createTransfer.getDropOffLocation() != 0 && createTransfer.getDropoffVendorStatus() != 1 && !(createTransfer.getPickupLocation() != projectId || createTransfer.getPickupVendorStatus() == 1)) {
                for (TransferLocationResponse.Locations location : locationsArrayList) {
                    if (location.getPjProjectsId() == createTransfer.getDropOffLocation()) {
                        selectedLocations = location;
                        dropoffLocationTextView.setText(selectedLocations.getProjectName());
                        selectTextView.setVisibility(View.GONE);
                    }
                    if (createTransfer.getPickupLocation() == location.getPjProjectsId()) {
                        loc = location;
                    }
                }
            } else /*if (createTransfer.getPickupVendorStatus() == 1)*/ {
                for (int i = 0; i < locationsArrayList.size(); i++) {
                    if (projectId == locationsArrayList.get(i).getPjProjectsId() && (createTransfer.getPickupLocation() != projectId || createTransfer.getPickupVendorStatus() == 1)) {
                        if (createTransfer.getDropoffVendorStatus() == 1 || createTransfer.getDropOffLocation() != projectId) {
                            createTransfer.setDropOffPhone("");
                            createTransfer.setDropOffName("");
                            dropoffNumberNewET.setText("");
                            Log.i("dropoff", "onLocationCheckedChange: e empty");
                            dropoffNameAutoCompleteTextView.setText("");
                            createTransfer.setDropoffVendorStatus(0);
                        }

                        dropoffLocationTextView.setText(locationsArrayList.get(i).getProjectName());
                        selectedLocations = locationsArrayList.get(i);
                        createTransfer.setDropOffLocation(selectedLocations.getPjProjectsId());
                        selectTextView.setVisibility(View.GONE);
//                        refreshContactData(((CreateTransfersActivity) getActivity()).getJobSiteTransferContactsResponse());

                    }
                    if (createTransfer.getPickupLocation() == locationsArrayList.get(i).getPjProjectsId()) {
                        locationsArrayList.remove(i);
                    }
                }
            }
            if (loc != null) {
                locationsArrayList.remove(loc);
            }
        }
        if (selectedLocations != null) {
            ((CreateTransfersActivity) getActivity()).callTransferJobsiteContactAPI("projects", selectedLocations.getPjProjectsId());
        }


    }

    public void refreshData(TransferLocationVendorResponse transferOverviewResponse) {
        if (transferOverviewResponse != null) {
            vendorLocationsArrayList.clear();
            vendorLocationsArrayList.addAll(cloneVendorLocationList(transferOverviewResponse.getData().getLocations()));
            if (createTransfer.getDropOffLocation() != 0 && createTransfer.getDropoffVendorStatus() == 1 && createTransfer.getVendorLocation() != 0) {
                for (TransferLocationVendorResponse.Locations location : vendorLocationsArrayList) {
                    if (location.getLocationId() == createTransfer.getVendorLocation() && location.getVendorId() == createTransfer.getDropOffLocation()) {
                        selectedVendorLocations = location;
                        dropoffLocatioErrorTextView.setText("");
                        dropoffLocationTextView.setText(location.getVendorName());
                        Log.i("dropoff", "onLocationCheckedChange: f " + createTransfer.getDropOffName());
                        dropoffNameAutoCompleteTextView.setText(TextUtils.isEmpty(createTransfer.getDropOffName()) ? "" : createTransfer.getDropOffName());
                        dropoffNumberNewET.setText(TextUtils.isEmpty(createTransfer.getDropOffPhone()) ? "" : ((CreateTransfersActivity) getActivity()).addMasking(createTransfer.getDropOffPhone().replace("-", "")));
                        selectTextView.setVisibility(View.GONE);
                        TransferContactRequest transferOverviewRequest = new TransferContactRequest();
                        transferOverviewRequest.setVendorId(selectedVendorLocations.getVendorId());
                        transferOverviewRequest.setType("vendors");
                        transferOverviewRequest.setLocationId(selectedVendorLocations.getLocationId());
                        ((CreateTransfersActivity) getActivity()).callTransferContactAPI(transferOverviewRequest);
                    }

                }
            }
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
//        dropoffNameAutoCompleteTextView.addTextChangedListener(null);
//        dropoffNumberNewET.addTextChangedListener(null);
    }

    @OnClick(R.id.dropoffLocationSpinnewView)
    public void onPickupLocationClick() {
        if (switchLocation.isChecked()) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            TransferLocationDialog transferLocationDialog = new TransferLocationDialog();
            Bundle bundle = new Bundle();
            bundle.putSerializable("transfer_location", locationsArrayList);
            bundle.putSerializable("selected_location", selectedLocations);
            transferLocationDialog.setCancelable(false);
            transferLocationDialog.setArguments(bundle);
            transferLocationDialog.show(ft, "");
        } else {

            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            TransferLocationDialog transferLocationDialog = new TransferLocationDialog();
            Bundle bundle = new Bundle();
            bundle.putSerializable("transfer_location", vendorLocationsArrayList);
            bundle.putSerializable("selected_location", selectedVendorLocations);
            bundle.putSerializable("is_vendor", true);
            transferLocationDialog.setCancelable(false);
            transferLocationDialog.setArguments(bundle);
            transferLocationDialog.show(ft, "");
        }

    }

    @OnClick(R.id.clickViewDate)
    public void onPickupDateClick() {

        DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {


                dropOfCalendar.set(Calendar.MONTH, arg2);
                dropOfCalendar.set(Calendar.DAY_OF_MONTH, arg3);
                dropOfCalendar.set(Calendar.YEAR, arg1);
                dropOfCalendar.set(Calendar.SECOND, 0);
                dropOfCalendar.set(Calendar.MILLISECOND, 0);
                arg2 = arg2 + 1;
                dropoffDateViewET.setText((arg2 < 10 ? "0" : "") + arg2 + "/" + (arg3 < 10 ? "0" : "") + arg3 + "/" + arg1);
                dropOffDateErrorTextView.setText("");
                createTransfer.setDeliveryDate(dropoffDateViewET.getText().toString());
                updateCreateTransfer();
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), myDateListener, dropOfCalendar.get(dropOfCalendar.YEAR), dropOfCalendar.get(dropOfCalendar.MONTH),
                dropOfCalendar.get(dropOfCalendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(pickUpCalendar.getTimeInMillis());

        datePickerDialog.show();
    }

    @OnClick(R.id.clickViewTime)
    public void onPickupTimeClick() {

        TimePickerDialog.OnTimeSetListener myDateListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                dropOfCalendar.set(Calendar.HOUR_OF_DAY, i);
                dropOfCalendar.set(Calendar.MINUTE, i1);
                dropOfCalendar.set(Calendar.SECOND, 0);
                dropOfCalendar.set(Calendar.MILLISECOND, 0);
                String amPM = "AM";
                if (i >= 12) {
                    amPM = "PM";
                }
                dropOfCalendar.set(Calendar.AM_PM, i < 12 ? Calendar.AM : Calendar.PM);
                i = i == 0 ? 12 : i;
                i = i > 12 ? i - 12 : i;
                dropoffTimeViewET.setText(i + ":" + (i1 < 10 ? "0" : "") + i1 + " " + amPM);
                dropOffTimeErrorTextView.setText("");
                createTransfer.setDropoffTime(dropoffTimeViewET.getText().toString());
                updateCreateTransfer();
            }
        };
        new TimePickerDialog(getActivity(), myDateListener, dropOfCalendar.get(dropOfCalendar.HOUR_OF_DAY), dropOfCalendar.get(dropOfCalendar.MINUTE), false).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectLocation(TransferLocationResponse.Locations locations) {

        Fragment f = getActivity().getSupportFragmentManager().findFragmentById(R.id.listContainer);
        if (f instanceof CreateTransferDropoffFragment) {
            if (selectedLocations == null || selectedLocations.getPjProjectsId() != locations.getPjProjectsId()) {
                selectedLocations = locations;

                if (contactList == null) {
                    contactList = new ArrayList<>();
                }

                contactList.clear();
                ((CreateTransfersActivity) getActivity()).clearDropOffjobSitecontactList();
                Log.i("dropoff", "onLocationCheckedChange: g empty");
                dropoffNameAutoCompleteTextView.setText("");
                dropoffNumberNewET.setText("");
                if (selectedLocations.getPjProjectsId() == createTransfer.getDropOffLocation()) {
                    Log.i("dropoff", "onLocationCheckedChange: h " + createTransfer.getDropOffName());
                    dropoffNameAutoCompleteTextView.setText(!TextUtils.isEmpty(createTransfer.getDropOffName()) ? createTransfer.getDropOffName() : "");
                    dropoffNumberNewET.setText(!TextUtils.isEmpty(createTransfer.getDropOffPhone()) ? ((CreateTransfersActivity) getActivity()).addMasking(createTransfer.getDropOffPhone().replace("-", "")) : "");
                }
                ((CreateTransfersActivity) getActivity()).callTransferJobsiteContactAPI("projects", selectedLocations.getPjProjectsId());
                if (createTransfer.getDropOffLocation() != 0) {
                    createTransfer.setDropOffLocation(selectedLocations.getPjProjectsId());
                    updateCreateTransfer();
                }
                dropoffLocationTextView.setText(locations.getProjectName());
                dropoffLocatioErrorTextView.setText("");
                selectTextView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        ((CreateTransfersActivity) getActivity()).selectDropOff();
        if (createTransfer != null && dropoffDateViewET != null &&
                dropoffTimeViewET != null &&
                dropoffNameAutoCompleteTextView != null &&
                dropOffNameErrorTextView != null) {
            setData();
        }
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectVendorLocation(TransferLocationVendorResponse.Locations locations) {
        Fragment f = getActivity().getSupportFragmentManager().findFragmentById(R.id.listContainer);
        if (f instanceof CreateTransferDropoffFragment) {
            if (selectedVendorLocations == null || selectedVendorLocations.getVendorId() != locations.getVendorId() || selectedVendorLocations.getLocationId() != locations.getLocationId()) {
                selectedVendorLocations = locations;
             dropoffLocatioErrorTextView.setText("");
                dropoffLocationTextView.setText(selectedVendorLocations.getVendorName());
                selectTextView.setVisibility(View.GONE);
                createTransfer.setVendorLocation(selectedVendorLocations.getLocationId());
                createTransfer.setDropOffLocation(selectedVendorLocations.getVendorId());
                createTransfer.setDropoffVendorStatus(1);
                if (createTransfer.getDropOffLocation() != 0) {
                    createTransfer.setDropoffVendorStatus(1);
                    updateCreateTransfer();
                }
                TransferContactRequest transferOverviewRequest = new TransferContactRequest();

                contactList.clear();
                ((CreateTransfersActivity) getActivity()).clearDropOffjobSitecontactList();
                adapter.notifyDataSetChanged();
                adapter.clear();
                adapter = new AutocompleteSelectContactAdapter(getActivity(), R.layout.searchable_adapter_item, contactList, createTransfer.getPickupName());

                dropoffNameAutoCompleteTextView.setOnItemClickListener(onItemClickListener);
                dropoffNameAutoCompleteTextView.setAdapter(adapter);
                dropoffNameAutoCompleteTextView.setText("");
                Log.i("dropoff", "onLocationCheckedChange: i empty");
                dropoffNumberNewET.setText("");
                dropOffNameErrorTextView.setText("");
                dropOffNumberErrorTextView.setText("");
                transferOverviewRequest.setVendorId(locations.getVendorId());
                transferOverviewRequest.setType("vendors");
                transferOverviewRequest.setLocationId(locations.getLocationId());
                ((CreateTransfersActivity) getActivity()).callTransferContactAPI(transferOverviewRequest);
            }

        }
    }

    public ArrayList<Contacts> cloneList(List<Contacts> imageTags) {
        if (imageTags == null) {
            return new ArrayList<>();
        }
        ArrayList<Contacts> clonedList = new ArrayList<>(imageTags.size());
        for (Contacts imageTag : imageTags) {
            clonedList.add(imageTag);
        }
        return clonedList;
    }

    public void refreshContactData(TransferContactsResponse transferContactsResponse) {
        if (transferContactsResponse != null) {
            dropOffNameErrorTextView.setText("");
            dropOffNumberErrorTextView.setText("");
            contactList = cloneList(transferContactsResponse.getData().getContacts());

            ((CreateTransfersActivity) getActivity()).setDropOffjobSitecontactList(transferContactsResponse.getData().getContacts());
            adapter = new AutocompleteSelectContactAdapter(getActivity(), R.layout.searchable_adapter_item, contactList, createTransfer.getPickupName());
            dropoffNameAutoCompleteTextView.setOnItemClickListener(onItemClickListener);
            dropoffNameAutoCompleteTextView.setAdapter(adapter);
            if (!switchLocation.isChecked() && contactList != null && contactList.size() == 1) {
//                if ((((CreateTransfersActivity) getActivity()).getTransferDetails() == null || (selectedVendorLocations == null || (createTransfer.getDropoffVendorStatus() == 1 && createTransfer.getDropOffLocation() != selectedVendorLocations.getLocationId() && createTransfer.getVendorLocation() != selectedVendorLocations.getVendorId())))) {
                if (createTransfer.getDropoffVendorStatus() == 1 && createTransfer.getDropOffLocation() != selectedVendorLocations.getLocationId() && createTransfer.getVendorLocation() != selectedVendorLocations.getVendorId() && TextUtils.isEmpty(dropoffNameAutoCompleteTextView.getText())) {
                    Contacts jobSitecontact = contactList.get(0);
                    Log.i("dropoff", "onLocationCheckedChange: j " + jobSitecontact.getName());
                    dropoffNameAutoCompleteTextView.setText(jobSitecontact.getName());
                    if (switchLocation.isChecked()) {
                        dropoffNumberNewET.setText(((CreateTransfersActivity) getActivity()).addMasking(jobSitecontact.getPhone1() != null ? jobSitecontact.getPhone1().replaceAll("-", "") : ""));
                    } else {
                        dropoffNumberNewET.setText(((CreateTransfersActivity) getActivity()).addMasking(jobSitecontact.getPhone_no() != null ? jobSitecontact.getPhone_no().replaceAll("-", "") : ""));
                    }
                }
            } else if (selectedLocations != null && ((CreateTransfersActivity) getActivity()).getTransferDetails() != null
                    && ((CreateTransfersActivity) getActivity()).getTransferDetails().getDropoffIsVendor() == 0
                    && !TextUtils.isEmpty(((CreateTransfersActivity) getActivity()).getTransferDetails().getDropoffContact())
                    && ((CreateTransfersActivity) getActivity()).getTransferDetails().getDropoffLocation() == selectedLocations.getPjProjectsId()) {
                Log.i("dropoff", "onLocationCheckedChange: k " + createTransfer.getDropOffName());
                dropoffNameAutoCompleteTextView.setText(createTransfer.getDropOffName());
                dropoffNumberNewET.setText(TextUtils.isEmpty(createTransfer.getDropOffPhone()) ? "" : ((CreateTransfersActivity) getActivity()).addMasking(createTransfer.getDropOffPhone().replace("-", "")));
            }
        }
    }

    public ArrayList<TransferLocationResponse.Locations> cloneLocationList(List<TransferLocationResponse.Locations> imageTags) {
        ArrayList<TransferLocationResponse.Locations> clonedList = new ArrayList<>(imageTags.size());
        for (TransferLocationResponse.Locations imageTag : imageTags) {
            clonedList.add(imageTag);
        }
        return clonedList;
    }

    public ArrayList<TransferLocationVendorResponse.Locations> cloneVendorLocationList(List<TransferLocationVendorResponse.Locations> imageTags) {
        ArrayList<TransferLocationVendorResponse.Locations> clonedList = new ArrayList<>(imageTags.size());
        for (TransferLocationVendorResponse.Locations imageTag : imageTags) {
            clonedList.add(imageTag);
        }
        return clonedList;
    }

    private void updateCreateTransfer() {
        ((CreateTransfersActivity) getActivity()).setCreateTransfer(createTransfer);
    }

    @OnClick(R.id.cancelTextView)
    public void onClickCancel() {
        getActivity().onBackPressed();
    }

    public boolean checkEmptyValidation() {
        if (createTransfer.getStatus() == 1 || createTransfer.getStatus() == 4 || (((createTransfer.getVendorLocation()) == 1 || createTransfer.getPickupLocation() != projectId) && createTransfer.getStatus() == 2) || createTransfer.getStatus() == 3 || createTransfer.getStatus() == 5)
            return true;

        boolean isValid = true;
        if (TextUtils.isEmpty(dropoffDateViewET.getText().toString())) {
            dropOffDateErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(dropoffTimeViewET.getText().toString())) {
            dropOffTimeErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(dropoffLocationTextView.getText().toString()) && (((CreateTransfersActivity) getActivity()).getTransferDetails() == null || isLocationChanged)) {
            dropoffLocatioErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }

        if (TextUtils.isEmpty(dropoffNameAutoCompleteTextView.getText().toString())) {
            dropOffNameErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(dropoffNumberNewET.getText().toString())) {
            dropOffNumberErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        return isValid;
    }
}


