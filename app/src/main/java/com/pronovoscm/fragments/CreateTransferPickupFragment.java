package com.pronovoscm.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.pronovoscm.R;
import com.pronovoscm.activity.CreateTransfersActivity;
import com.pronovoscm.adapter.AutocompleteSelectContactAdapter;
import com.pronovoscm.model.request.transfercontact.TransferContactRequest;
import com.pronovoscm.model.request.transferrequest.TransferRequest;
import com.pronovoscm.model.response.transfercontacts.Contacts;
import com.pronovoscm.model.response.transfercontacts.TransferContactsResponse;
import com.pronovoscm.model.response.transferlocation.TransferLocationResponse;
import com.pronovoscm.model.response.transferlocation.TransferLocationVendorResponse;
import com.pronovoscm.services.NetworkService;
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

public class CreateTransferPickupFragment extends Fragment {
    //    @BindView(R.id.transferView)
//    RelativeLayout transferView;
    @BindView(R.id.PickUpLocatioView)
    RelativeLayout PickUpLocatioView;
    @BindView(R.id.timeView)
    ConstraintLayout timeView;
    @BindView(R.id.PickUpNumberView)
    RelativeLayout PickUpNumberView;
    @BindView(R.id.pickUpDateView)
    ConstraintLayout pickUpDateView;
    @BindView(R.id.pickUpLocationSpinnewView)
    RelativeLayout pickUpLocationSpinnewView;
    @BindView(R.id.nextTextView)
    TextView nextTextView;
    @BindView(R.id.selectTextView)
    TextView selectTextView;
    @BindView(R.id.switchLocationTextViewView)
    TextView switchLocationTextViewView;
    @BindView(R.id.pickupNameAutoCompleteTextView)
    AutoCompleteTextView pickupNameAutoCompleteTextView;
    @BindView(R.id.pickupNumberTextView)
    EditText pickupNumberTextView;
    @BindView(R.id.switchLocation)
    Switch switchLocation;
    @BindView(R.id.pickUpDateViewET)
    EditText pickUpDateViewET;
    @BindView(R.id.clickViewDate)
    View clickViewDate;
    @BindView(R.id.clickViewTime)
    View clickViewTime;
    @BindView(R.id.pickUpTimeViewET)
    EditText pickUpTimeViewET;
    @BindView(R.id.pickupLocationTextView)
    TextView pickupLocationTextView;
    @BindView(R.id.pickUpNumberErrorTextView)
    TextView pickUpNumberErrorTextView;
    @BindView(R.id.pickUpNameErrorTextView)
    TextView pickUpNameErrorTextView;
    @BindView(R.id.pickupDateErrorTextView)
    TextView pickupDateErrorTextView;
    @BindView(R.id.pickupTimeErrorTextView)
    TextView pickupTimeErrorTextView;
    @BindView(R.id.pickUpLocatioErrorTextView)
    TextView pickUpLocatioErrorTextView;
    @BindView(R.id.cancelTextView)
    TextView cancelTextView;
    private AlertDialog alertDialog;
    private String transferOption;
    private CreateTransferDropoffFragment createTransferPickupFragment;
    private TransferLocationResponse.Locations locations;
    private TransferLocationVendorResponse.Locations vendorLocation;
    private ArrayList<TransferLocationResponse.Locations> locationsArrayList = new ArrayList<>();
    private ArrayList<TransferLocationVendorResponse.Locations> vendorLocationsArrayList = new ArrayList<>();
    private TransferLocationResponse.Locations selectedLocations;
    private TransferLocationVendorResponse.Locations selectedVendorLocations;
    private Date pickUpDateTime = null;
    private Calendar pickUpCalendar = Calendar.getInstance();
    private AutocompleteSelectContactAdapter adapter;
    private Contacts jobSitecontact;
    private List<Contacts> contactList = new ArrayList<>();
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ((CreateTransfersActivity) getActivity()).hideKeyboard(getActivity());
            jobSitecontact = (Contacts) adapterView.getItemAtPosition(i);
            pickupNameAutoCompleteTextView.setText(jobSitecontact.getName());
            if (switchLocation.isChecked()) {
                pickupNumberTextView.setText(((CreateTransfersActivity) getActivity()).addMasking(jobSitecontact.getPhone1() != null ? jobSitecontact.getPhone1().replaceAll("-", "") : ""));
            } else {
                pickupNumberTextView.setText(((CreateTransfersActivity) getActivity()).addMasking(jobSitecontact.getPhone_no() != null ? jobSitecontact.getPhone_no().replaceAll("-", "") : ""));
            }
        }
    };
    private TransferRequest createTransfer;
    private int len;
    private int len2;
    private int projectId;
    private boolean isLocationChanged = false;
    private boolean allDone = false;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create_transfer_pickup_fragment, container, false);
        ButterKnife.bind(this, rootView);
        allDone = false;
        createTransfer = ((CreateTransfersActivity) getActivity()).getCreateTransfer();
//        pickUpjobSitecontactList = new ArrayList<>();
        contactList.clear();
        contactList.addAll(((CreateTransfersActivity) getActivity()).getPickUpjobSitecontactList());

        adapter = new AutocompleteSelectContactAdapter(getActivity(), R.layout.searchable_adapter_item, contactList, createTransfer != null && !TextUtils.isEmpty(createTransfer.getDropOffName()) ? createTransfer.getDropOffName() : "");
        pickupNameAutoCompleteTextView.setOnItemClickListener(onItemClickListener);
        pickupNameAutoCompleteTextView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        projectId = getActivity().getIntent().getIntExtra("project_id", 0);
        transferOption = getActivity().getIntent().getStringExtra("transfer_option");
        createTransfer = ((CreateTransfersActivity) getActivity()).getCreateTransfer();
        if (transferOption.equals("New Request") || createTransfer.getStatus() == 0) {
//            transferView.setVisibility(View.GONE);
        }
        pickUpDateViewET.setKeyListener(null);
        pickUpTimeViewET.setKeyListener(null);

        pickupNumberTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pickUpNumberErrorTextView.setText("");
                createTransfer.setPickupPhone(pickupNumberTextView.getText().toString());
                updateCreateTransfer();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int i = pickupNumberTextView.getText().toString().length();
                if (i > 0) {
                    String lastChar = String.valueOf(pickupNumberTextView.getText().toString().charAt(pickupNumberTextView.getText().length() - 1));
                    if (lastChar.equals("-")) {
                        return;
                    }
                }
                if (i < 4)
                    len = 0;
                if (i == 4 && len < 5) {
                    len = 5;
                    String ss = editable.toString();
                    String first = ss.substring(0, ss.length() - 1);
                    String last = ss.substring(ss.length() - 1);
                    pickupNumberTextView.setText(first + "-" + last);
                    pickupNumberTextView.setSelection(pickupNumberTextView.getText().length());
                }
                if (i < 8)
                    len2 = 0;
                if (i == 8 && len2 < 9) {
                    len2 = 9;
                    String ss = editable.toString();
                    String first = ss.substring(0, ss.length() - 1);
                    String last = ss.substring(ss.length() - 1);
                    pickupNumberTextView.setText(first + "-" + last);
                    pickupNumberTextView.setSelection(pickupNumberTextView.getText().length());
                }
            }
        });
        pickupNameAutoCompleteTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pickUpNameErrorTextView.setText("");
                createTransfer.setPickupName(pickupNameAutoCompleteTextView.getText().toString());

                updateCreateTransfer();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        setData();

    }

    private void setData() {

        if (selectedLocations == null && createTransfer.getPickupLocation() != 0 && createTransfer.getPickupVendorStatus() != 1) {
            ((CreateTransfersActivity) getActivity()).callTransferJobsiteContactAPI("projects", createTransfer.getPickupLocation());
        }
        if (createTransfer.getPickupVendorStatus() == 0) {
            switchLocation.setChecked(true);
//            ((CreateTransfersActivity) getActivity()).callTransferLocationAPI("projects");
//            ((CreateTransfersActivity) getActivity()).callTransferJobsiteContactAPI("projects");

            refreshData(((CreateTransfersActivity) getActivity()).getTransferOverviewResponse());

//            refreshContactData(((CreateTransfersActivity) getActivity()).getJobSiteTransferContactsResponse());
        } else {
            switchLocation.setChecked(false);
//            ((CreateTransfersActivity) getActivity()).callTransferLocationVendorAPI("vendors");
            refreshData(((CreateTransfersActivity) getActivity()).getTransferOverviewVendorResponse());

        }

        contactList.clear();
        contactList.addAll(((CreateTransfersActivity) getActivity()).getPickUpjobSitecontactList());
        pickUpDateViewET.setText(createTransfer.getPickupDate());
        pickUpTimeViewET.setText(createTransfer.getPickupTime());
        pickupNameAutoCompleteTextView.setText(createTransfer.getPickupName());
        pickupNumberTextView.setText(((CreateTransfersActivity) getActivity()).addMasking(createTransfer.getPickupPhone() != null ? createTransfer.getPickupPhone().replaceAll("-", "") : ""));
        if (selectedLocations != null) {
            pickupLocationTextView.setText(selectedLocations.getProjectName());
            selectTextView.setVisibility(View.GONE);
            ((CreateTransfersActivity) getActivity()).callTransferJobsiteContactAPI("projects", selectedLocations.getPjProjectsId());

        } else if (selectedVendorLocations != null) {
            pickupLocationTextView.setText(selectedVendorLocations.getVendorName());
            selectTextView.setVisibility(View.GONE);
            TransferContactRequest transferOverviewRequest = new TransferContactRequest();
            transferOverviewRequest.setVendorId(selectedVendorLocations.getVendorId());
            transferOverviewRequest.setType("vendors");
            transferOverviewRequest.setLocationId(selectedVendorLocations.getLocationId());
            ((CreateTransfersActivity) getActivity()).callTransferContactAPI(transferOverviewRequest);
        }

        if ((createTransfer.getStatus() >= 1 && (createTransfer.getStatus() != 2 ||
                (createTransfer.getDropoffVendorStatus() != 1 && createTransfer.getPickupLocation() != projectId)))
                || (createTransfer.getEquipment() != null && createTransfer.getEquipment().size() > 0)) {
            if (!TextUtils.isEmpty(createTransfer.getPickupDate()) && !TextUtils.isEmpty(createTransfer.getPickupTime())) {
                Date date1 = DateFormatter.getDateFromTransferDate(createTransfer.getPickupDate() + " " + createTransfer.getPickupTime());
                pickUpCalendar.setTime(date1);
            } else if (!TextUtils.isEmpty(createTransfer.getPickupTime())) {
                Date date1 = DateFormatter.getTimeFromTransferDate(createTransfer.getPickupTime());
                pickUpCalendar.setTime(date1);
            }
            switchLocation.setEnabled(false);
            switchLocation.setClickable(false);
            if (createTransfer.getStatus() >= 1 && (createTransfer.getStatus() != 2 || (createTransfer.getDropoffVendorStatus() != 1 && createTransfer.getPickupLocation() != projectId))) {
                pickUpDateView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                timeView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                PickUpNumberView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                PickUpLocatioView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                cancelTextView.setVisibility(View.GONE);
                clickViewTime.setClickable(false);
                clickViewDate.setClickable(false);
                pickupNameAutoCompleteTextView.setClickable(false);
                pickupNameAutoCompleteTextView.setFocusableInTouchMode(false);
                pickupNumberTextView.setFocusableInTouchMode(false);
//                switchLocation.setChecked(createTransfer.getPickupVendorStatus() == 0 ? true : false);
            }
            if (createTransfer.getStatus() == 3 && createTransfer.getDropoffVendorStatus() == 0 && createTransfer.getDropOffLocation() == projectId) {
                cancelTextView.setVisibility(View.VISIBLE);
            }
            pickUpLocationSpinnewView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
            pickUpLocationSpinnewView.setClickable(false);
        }
        if (createTransfer.getStatus() == 2 && (createTransfer.getPickupVendorStatus() != 1 && createTransfer.getPickupLocation() == projectId)) {

            pickUpLocationSpinnewView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
            pickUpLocationSpinnewView.setClickable(false);
            switchLocation.setEnabled(false);
            switchLocation.setClickable(false);
        }
        if (createTransfer.isInterofficeTransfer()) {
            pickUpLocationSpinnewView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
            pickUpLocationSpinnewView.setClickable(false);
            pickupLocationTextView.setText(createTransfer.getPickupLocationName());
            selectTextView.setVisibility(View.GONE);

        }
        allDone = true;
    }

    @OnCheckedChanged(R.id.switchLocation)
    public void onLocationCheckedChange() {
        ((CreateTransfersActivity) getActivity()).hideKeyboard(getActivity());
        if (allDone) {
            selectedVendorLocations = null;
            pickupNameAutoCompleteTextView.setText("");
            pickupNumberTextView.setText("");

            contactList.clear();
            ((CreateTransfersActivity) getActivity()).clearPickUpjobSitecontactList();
            createTransfer.setPickupLocation(0);
            ((CreateTransfersActivity) getActivity()).setCreateTransfer(createTransfer);
//            pickUpjobSitecontactList.clear();
            selectedLocations = null;
        }
        createTransfer.setPickupVendorStatus(switchLocation.isChecked() ? 0 : 1);
        if (((CreateTransfersActivity) getActivity()).getTransferDetails() != null && ((CreateTransfersActivity) getActivity()).getTransferDetails().getPickupIsVendor() != createTransfer.getPickupVendorStatus()) {
            isLocationChanged = true;
        }
        updateCreateTransfer();
        selectTextView.setVisibility(View.VISIBLE);
        pickupLocationTextView.setText("");
        pickUpLocatioErrorTextView.setText("");
        /*
         */
        adapter.notifyDataSetChanged();
        adapter.clear();
        adapter = new AutocompleteSelectContactAdapter(getActivity(), R.layout.searchable_adapter_item, contactList, createTransfer != null && !TextUtils.isEmpty(createTransfer.getDropOffName()) ? createTransfer.getDropOffName() : "");
        pickupNameAutoCompleteTextView.setOnItemClickListener(onItemClickListener);
        pickupNameAutoCompleteTextView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if (switchLocation.isChecked()) {
            switchLocationTextViewView.setText(getString(R.string.jobsite));
            refreshData(((CreateTransfersActivity) getActivity()).getTransferOverviewResponse());
//            refreshContactData(((CreateTransfersActivity) getActivity()).getJobSiteTransferContactsResponse());
        } else {
            switchLocationTextViewView.setText(getString(R.string.vendor));
            refreshData(((CreateTransfersActivity) getActivity()).getTransferOverviewVendorResponse());
        }
    }

    public boolean checkValidation() {
        if (createTransfer.getStatus() == 1 || createTransfer.getStatus() == 4 || (((createTransfer.getVendorLocation()) == 1 || createTransfer.getPickupLocation() != projectId) && createTransfer.getStatus() == 2) || createTransfer.getStatus() == 3 || createTransfer.getStatus() == 5)
            return true;

        boolean isValid = true;
        if (TextUtils.isEmpty(pickUpDateViewET.getText().toString())) {
            pickupDateErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(pickUpTimeViewET.getText().toString())) {
            pickupTimeErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(pickupLocationTextView.getText().toString()) && (((CreateTransfersActivity) getActivity()).getTransferDetails() == null || isLocationChanged)) {
            pickUpLocatioErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(pickupNameAutoCompleteTextView.getText().toString())) {
            pickUpNameErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(pickupNumberTextView.getText().toString())) {
            pickUpNumberErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
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

        if (!NetworkService.isNetworkAvailable(getActivity())) {
            showMessageAlert(getActivity(), getString(R.string.internet_connection_check_transfer_overview), getString(R.string.ok));
            return;
        }

        ((CreateTransfersActivity) getActivity()).hideKeyboard(getActivity());
        if (createTransfer.getStatus() == 1 || createTransfer.getStatus() == 4 || (((createTransfer.getVendorLocation()) == 1 ||
                createTransfer.getPickupLocation() != projectId) && createTransfer.getStatus() == 2) || createTransfer.getStatus() == 3 || createTransfer.getStatus() == 5 ||
                checkValidation()) {
            createTransfer.setPickupDate(pickUpDateViewET.getText().toString());
            createTransfer.setPickupTime(pickUpTimeViewET.getText().toString());
            createTransfer.setPickupVendorStatus(switchLocation.isChecked() ? 0 : 1);
            createTransfer.setPickupName(pickupNameAutoCompleteTextView.getText().toString());
            if (switchLocation.isChecked()) {
                createTransfer.setPickupLocation(selectedLocations != null ? selectedLocations.getPjProjectsId() : (((CreateTransfersActivity) getActivity()).getTransferDetails()) != null ? ((CreateTransfersActivity) getActivity()).getTransferDetails().getPickupLocation() : 0);
                createTransfer.setPickupVendorStatus(0);
                createTransfer.setPickupPhone(pickupNumberTextView.getText().toString());
            } else {
                createTransfer.setPickupVendorStatus(1);
                createTransfer.setPickupPhone(pickupNumberTextView.getText().toString());
                createTransfer.setPickupLocation(selectedVendorLocations != null ? selectedVendorLocations.getVendorId() : (((CreateTransfersActivity) getActivity()).getTransferDetails()) != null ? ((CreateTransfersActivity) getActivity()).getTransferDetails().getPickupLocation() : 0);
                createTransfer.setVendorLocation(selectedVendorLocations != null ? selectedVendorLocations.getLocationId() : (((CreateTransfersActivity) getActivity()).getTransferDetails()) != null ? ((CreateTransfersActivity) getActivity()).getTransferDetails().getVendorLocation() : 0);
            }
            ((CreateTransfersActivity) getActivity()).setPickUpDone();

            ((CreateTransfersActivity) getActivity()).setCreateTransfer(createTransfer);
            ((CreateTransfersActivity) getActivity()).openTransferDropOff();

        }
    }

    public void refreshData(TransferLocationResponse transferLocationResponse) {
        if (transferLocationResponse != null) {
            locationsArrayList.clear();
            locationsArrayList.addAll(cloneLocationList(transferLocationResponse.getData().getLocations()));
            if (createTransfer.getPickupLocation() != 0 && createTransfer.getPickupVendorStatus() != 1) {
                for (TransferLocationResponse.Locations location :
                        locationsArrayList) {
                    if (location.getPjProjectsId() == createTransfer.getPickupLocation()) {
                        selectTextView.setVisibility(View.GONE);
                        selectedLocations = location;
                        pickupLocationTextView.setText(selectedLocations.getProjectName());
                    }
                }
            }
        }

    }

    public void refreshData(TransferLocationVendorResponse transferOverviewResponse) {
        if (transferOverviewResponse != null) {
            vendorLocationsArrayList.clear();
            vendorLocationsArrayList.addAll(cloneVendorLocationList(transferOverviewResponse.getData().getLocations()));

            if ((createTransfer.getPickupLocation() != 0 && createTransfer.getPickupVendorStatus() == 1 && createTransfer.getVendorLocation() != 0) || !switchLocation.isChecked()) {
                for (TransferLocationVendorResponse.Locations location : vendorLocationsArrayList) {
                    if (location.getLocationId() == createTransfer.getVendorLocation() && location.getVendorId() == createTransfer.getPickupLocation()) {
                        selectedVendorLocations = location;
                        createTransfer.setPickupLocation(selectedVendorLocations != null ? selectedVendorLocations.getVendorId() : (((CreateTransfersActivity) getActivity()).getTransferDetails()) != null ? ((CreateTransfersActivity) getActivity()).getTransferDetails().getPickupLocation() : 0);
                        createTransfer.setVendorLocation(selectedVendorLocations != null ? selectedVendorLocations.getLocationId() : (((CreateTransfersActivity) getActivity()).getTransferDetails()) != null ? ((CreateTransfersActivity) getActivity()).getTransferDetails().getVendorLocation() : 0);
                        selectTextView.setVisibility(View.GONE);
                        pickupLocationTextView.setText(location.getVendorName());
                        pickupNameAutoCompleteTextView.setText(TextUtils.isEmpty(createTransfer.getPickupName()) ? "" : createTransfer.getPickupName());
                        pickupNumberTextView.setText(TextUtils.isEmpty(createTransfer.getPickupPhone()) ? "" : ((CreateTransfersActivity) getActivity()).addMasking(createTransfer.getPickupPhone().replace("-", "")));
                        TransferContactRequest transferOverviewRequest = new TransferContactRequest();
                        transferOverviewRequest.setVendorId(selectedVendorLocations.getVendorId());
                        transferOverviewRequest.setType("vendors");
                        transferOverviewRequest.setLocationId(selectedVendorLocations.getLocationId());
                        ((CreateTransfersActivity) getActivity()).setCreateTransfer(createTransfer);
                        ((CreateTransfersActivity) getActivity()).callTransferContactAPI(transferOverviewRequest);
                    }

                }
            }
        }
    }

    @OnClick(R.id.pickUpLocationSpinnewView)
    public void onPickupLocationClick() {
        if (switchLocation.isChecked()) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            TransferLocationDialog tagsDialog = new TransferLocationDialog();
            Bundle bundle = new Bundle();
            bundle.putSerializable("transfer_location", locationsArrayList);
            bundle.putSerializable("selected_location", selectedLocations);
            tagsDialog.setCancelable(false);
            tagsDialog.setArguments(bundle);
            tagsDialog.show(ft, "");
        } else {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            TransferLocationDialog tagsDialog = new TransferLocationDialog();
            Bundle bundle = new Bundle();
            bundle.putSerializable("transfer_location", vendorLocationsArrayList);
            bundle.putSerializable("selected_location", selectedVendorLocations);
            bundle.putSerializable("is_vendor", true);
            tagsDialog.setCancelable(false);
            tagsDialog.setArguments(bundle);
            tagsDialog.show(ft, "");
        }
    }

    @OnClick(R.id.clickViewDate)
    public void onPickupDateClick() {
        ((CreateTransfersActivity) getActivity()).hideKeyboard(getActivity());
        DatePickerDialog.OnDateSetListener myDateListener = (arg0, arg1, arg2, arg3) -> {
            pickUpCalendar.set(Calendar.MONTH, arg2);
            pickUpCalendar.set(Calendar.DAY_OF_MONTH, arg3);
            pickUpCalendar.set(Calendar.YEAR, arg1);
            arg2 = arg2 + 1;
            pickUpDateViewET.setText((arg2 < 10 ? "0" : "") + arg2 + "/" + (arg3 < 10 ? "0" : "") + arg3 + "/" + arg1);
            pickupDateErrorTextView.setText("");
            createTransfer.setPickupDate(pickUpDateViewET.getText().toString());

            updateCreateTransfer();
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), myDateListener, pickUpCalendar.get(pickUpCalendar.YEAR), pickUpCalendar.get(pickUpCalendar.MONTH),
                pickUpCalendar.get(pickUpCalendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();
    }

    @OnClick(R.id.clickViewTime)
    public void onPickupTimeClick() {
        ((CreateTransfersActivity) getActivity()).hideKeyboard(getActivity());
        TimePickerDialog.OnTimeSetListener myDateListener = (timePicker, i, i1) -> {
            pickUpCalendar.set(Calendar.HOUR_OF_DAY, i);
            pickUpCalendar.set(Calendar.MINUTE, i1);
            String amPM = "AM";
            if (i >= 12) {
                amPM = "PM";
            }

            pickUpCalendar.set(Calendar.AM_PM, i < 12 ? Calendar.AM : Calendar.PM);
            i = i == 0 ? 12 : i;
            i = i > 12 ? i - 12 : i;
//            String hr = (i < 10 ? "" + i : "" + i);
            pickUpTimeViewET.setText(i + ":" + (i1 < 10 ? "0" : "") + i1 + " " + amPM);
            pickupTimeErrorTextView.setText("");
            createTransfer.setPickupTime(pickUpTimeViewET.getText().toString());
            updateCreateTransfer();
        };
        new TimePickerDialog(getActivity(), myDateListener, pickUpCalendar.get(pickUpCalendar.HOUR_OF_DAY), pickUpCalendar.get(pickUpCalendar.MINUTE), false).show();
    }

    private void updateCreateTransfer() {
        ((CreateTransfersActivity) getActivity()).setCreateTransfer(createTransfer);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectLocation(TransferLocationResponse.Locations locations) {
        Fragment f = getActivity().getSupportFragmentManager().findFragmentById(R.id.listContainer);
        if (f instanceof CreateTransferPickupFragment) {
            ((CreateTransfersActivity) getActivity()).hideKeyboard(getActivity());
            if (selectedLocations == null || selectedLocations.getPjProjectsId() != locations.getPjProjectsId()) {
                selectedLocations = locations;
                createTransfer.setPickupLocation(selectedLocations != null ? selectedLocations.getPjProjectsId() : (((CreateTransfersActivity) getActivity()).getTransferDetails()) != null ? ((CreateTransfersActivity) getActivity()).getTransferDetails().getPickupLocation() : 0);
                createTransfer.setPickupVendorStatus(0);

                if (contactList == null) {
                    contactList = new ArrayList<>();
                }
                ((CreateTransfersActivity) getActivity()).clearPickUpjobSitecontactList();
                ((CreateTransfersActivity) getActivity()).setCreateTransfer(createTransfer);
                contactList.clear();
                pickupNumberTextView.setText("");
                pickupNameAutoCompleteTextView.setText("");
                ((CreateTransfersActivity) getActivity()).callTransferJobsiteContactAPI("projects", selectedLocations.getPjProjectsId());
                if (createTransfer.getPickupLocation() != 0) {
                    createTransfer.setPickupVendorStatus(0);
                    createTransfer.setPickupLocation(selectedLocations.getPjProjectsId());
                    updateCreateTransfer();
                }

                pickupLocationTextView.setText(locations.getProjectName());
                selectTextView.setVisibility(View.GONE);
                pickUpLocatioErrorTextView.setText("");

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        ((CreateTransfersActivity) getActivity()).select1();
        if (createTransfer != null && pickUpDateViewET != null &&
                pickUpTimeViewET != null &&
                pickupNameAutoCompleteTextView != null &&
                pickUpNameErrorTextView != null) {
            setData();
        }
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
//        ((CreateTransfersActivity) getActivity()).deSelect1();
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
//        ((CreateTransfersActivity) getActivity()).deSelect1();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectVendorLocation(TransferLocationVendorResponse.Locations locations) {

        Fragment f = getActivity().getSupportFragmentManager().findFragmentById(R.id.listContainer);
        if (f instanceof CreateTransferPickupFragment) {
            if (selectedVendorLocations == null || selectedVendorLocations.getVendorId() != locations.getVendorId() || selectedVendorLocations.getLocationId() != locations.getLocationId()) {
                selectedVendorLocations = locations;
                pickupLocationTextView.setText(selectedVendorLocations.getVendorName());
                createTransfer.setVendorLocation(selectedVendorLocations.getLocationId());
                createTransfer.setPickupLocation(selectedVendorLocations.getVendorId());
                createTransfer.setPickupVendorStatus(1);
                /*if (createTransfer.getPickupLocation() != 0) {
                    createTransfer.setPickupVendorStatus(1);
                    createTransfer.setPickupLocation(selectedVendorLocations.getVendorId());
                  */
                updateCreateTransfer();
//                }
                pickUpLocatioErrorTextView.setText("");
                selectTextView.setVisibility(View.GONE);

                ((CreateTransfersActivity) getActivity()).clearPickUpjobSitecontactList();
                contactList.clear();
                adapter.notifyDataSetChanged();
                adapter.clear();
                adapter = new AutocompleteSelectContactAdapter(getActivity(), R.layout.searchable_adapter_item, contactList, createTransfer != null && !TextUtils.isEmpty(createTransfer.getDropOffName()) ? createTransfer.getDropOffName() : "");
                pickupNameAutoCompleteTextView.setOnItemClickListener(onItemClickListener);
                pickupNameAutoCompleteTextView.setAdapter(adapter);
                pickupNameAutoCompleteTextView.setText("");
                pickupNumberTextView.setText("");
                TransferContactRequest transferOverviewRequest = new TransferContactRequest();
                transferOverviewRequest.setVendorId(locations.getVendorId());
                transferOverviewRequest.setType("vendors");
                transferOverviewRequest.setLocationId(locations.getLocationId());
                ((CreateTransfersActivity) getActivity()).callTransferContactAPI(transferOverviewRequest);
            }
        }

    }

    public void refreshContactData(TransferContactsResponse transferContactsResponse) {
        if (transferContactsResponse != null && transferContactsResponse.getData() != null && transferContactsResponse.getData().getContacts() != null) {

            contactList = cloneList(transferContactsResponse.getData().getContacts());
            ((CreateTransfersActivity) getActivity()).setPickUpjobSitecontactList(transferContactsResponse.getData().getContacts());
            adapter = new AutocompleteSelectContactAdapter(getActivity(), R.layout.searchable_adapter_item, contactList, createTransfer != null && !TextUtils.isEmpty(createTransfer.getDropOffName()) ? createTransfer.getDropOffName() : "");
            pickupNameAutoCompleteTextView.setOnItemClickListener(onItemClickListener);
            pickupNameAutoCompleteTextView.setAdapter(adapter);
            if (!switchLocation.isChecked() && contactList != null && contactList.size() == 1) {

                if (createTransfer.getPickupVendorStatus() == 1 && createTransfer.getPickupLocation() != selectedVendorLocations.getLocationId() && createTransfer.getVendorLocation() != selectedVendorLocations.getVendorId() && TextUtils.isEmpty(pickupNameAutoCompleteTextView.getText())) {
                    Contacts jobSitecontact = contactList.get(0);
                    pickupNameAutoCompleteTextView.setText(jobSitecontact.getName());
                    if (switchLocation.isChecked()) {
                        pickupNumberTextView.setText(((CreateTransfersActivity) getActivity()).addMasking(jobSitecontact.getPhone1() != null ? jobSitecontact.getPhone1().replaceAll("-", "") : ""));
                    } else {
                        pickupNumberTextView.setText(((CreateTransfersActivity) getActivity()).addMasking(jobSitecontact.getPhone_no() != null ? jobSitecontact.getPhone_no().replaceAll("-", "") : ""));
                    }
                }
            } else if (selectedLocations != null && ((CreateTransfersActivity) getActivity()).getTransferDetails() != null
                    && ((CreateTransfersActivity) getActivity()).getTransferDetails().getPickupIsVendor() == 0
                    && !TextUtils.isEmpty(((CreateTransfersActivity) getActivity()).getTransferDetails().getPickupContact())
                    && ((CreateTransfersActivity) getActivity()).getTransferDetails().getPickupLocation() == selectedLocations.getPjProjectsId()) {
                pickupNameAutoCompleteTextView.setText(createTransfer.getPickupName());
                pickupNumberTextView.setText(((CreateTransfersActivity) getActivity()).addMasking(createTransfer.getPickupPhone().replace("-", "")));
            }


        }
    }

    @OnClick(R.id.cancelTextView)
    public void onClickCancel() {
        getActivity().onBackPressed();
    }

    public boolean checkEmptyValidation() {
        if (createTransfer.getStatus() == 1 || createTransfer.getStatus() == 4 || (((createTransfer.getVendorLocation()) == 1 || createTransfer.getPickupLocation() != projectId) && createTransfer.getStatus() == 2) || createTransfer.getStatus() == 3 || createTransfer.getStatus() == 5)
            return true;

        boolean isValid = true;
        if (TextUtils.isEmpty(pickUpDateViewET.getText().toString())) {
            pickupDateErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(pickUpTimeViewET.getText().toString())) {
            pickupTimeErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(pickupLocationTextView.getText().toString()) && (((CreateTransfersActivity) getActivity()).getTransferDetails() == null || isLocationChanged)) {
            pickUpLocatioErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(pickupNameAutoCompleteTextView.getText().toString())) {
            pickUpNameErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(pickupNumberTextView.getText().toString())) {
            pickUpNumberErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        return isValid;
    }


    public boolean isSomethingFilled() {
        if (!TextUtils.isEmpty(pickUpDateViewET.getText().toString()) || !TextUtils.isEmpty(pickUpTimeViewET.getText().toString()) || !TextUtils.isEmpty(pickupLocationTextView.getText().toString()) || !TextUtils.isEmpty(pickupNameAutoCompleteTextView.getText().toString()) || !TextUtils.isEmpty(pickupNumberTextView.getText().toString())) {
            return true;
        }
        return false;
    }
}
