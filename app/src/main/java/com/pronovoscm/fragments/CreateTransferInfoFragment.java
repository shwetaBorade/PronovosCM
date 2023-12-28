package com.pronovoscm.fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.activity.CreateTransfersActivity;
import com.pronovoscm.model.request.createtransfer.CreateTransferEquipment;
import com.pronovoscm.model.request.createtransfer.CreateTransferRequest;
import com.pronovoscm.model.request.transferrequest.Equipment;
import com.pronovoscm.model.request.transferrequest.TransferRequest;
import com.pronovoscm.persistence.domain.EquipmentInventory;
import com.pronovoscm.persistence.domain.EquipmentRegion;
import com.pronovoscm.persistence.repository.EquipementInventoryRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.DateFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateTransferInfoFragment extends Fragment {

    @Inject
    EquipementInventoryRepository mEquipementInventoryRepository;
    @BindView(R.id.truckSizeViewET)
    EditText truckSizeViewET;
    @BindView(R.id.truckSizeView)
    ConstraintLayout truckSizeView;
    @BindView(R.id.commentEditTextLayout)
    TextInputLayout commentEditTextLayout;
    @BindView(R.id.commentEditText)
    TextInputEditText commentEditText;
    @BindView(R.id.pickupTransferView)
    RelativeLayout pickupTransferView;
    @BindView(R.id.dropoffTransferView)
    RelativeLayout dropoffTransferView;
    @BindView(R.id.saveSendTextView)
    TextView saveSendTextView;
    @BindView(R.id.saveAsDraftTextView)
    TextView saveAsDraftTextView;
    @BindView(R.id.truckSizeTextView)
    TextView truckSizeTextView;
    @BindView(R.id.pickarriveTimeET)
    TextView pickarriveTimeET;
    @BindView(R.id.pickloadTimeET)
    TextView pickloadTimeET;
    @BindView(R.id.pickdepartureTimeET)
    TextView pickdepartureTimeET;
    @BindView(R.id.arriveTimeET)
    TextView droparriveTimeET;
    @BindView(R.id.loadTimeET)
    TextView droploadTimeET;
    @BindView(R.id.departureTimeET)
    TextView dropdepartureTimeET;
    @BindView(R.id.pickarriveTimeView)
    RelativeLayout pickarriveTimeView;
    @BindView(R.id.pickloadTimeView)
    RelativeLayout pickloadTimeView;
    @BindView(R.id.pickdepartureTimeView)
    RelativeLayout pickdepartureTimeView;
    @BindView(R.id.arriveTimeView)
    RelativeLayout arriveTimeView;
    @BindView(R.id.loadTimeView)
    RelativeLayout loadTimeView;
    @BindView(R.id.departureTimeView)
    RelativeLayout departureTimeView;
    @BindView(R.id.errorTextView)
    TextView errorView;
    @BindView(R.id.cancelTextView)
    TextView cancelTextView;
    @BindView(R.id.saveTransferAsDraftTextView)
    TextView saveTransferAsDraftTextView;

    private TransferRequest createTransfer;
    private int projectId;
    private CreateTransferRequest mCreateTransfer;
    private Calendar calendar = Calendar.getInstance();
    private int transferType = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create_transfer_info_fragment, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
        createTransfer = ((CreateTransfersActivity) getActivity()).getCreateTransfer();
        projectId = getActivity().getIntent().getIntExtra("project_id", 0);
        transferType = getActivity().getIntent().getIntExtra(Constants.INTENT_KEY_TRANSFER_TYPE, -1);
        if (createTransfer.getStatus() >= 1 && (createTransfer.getStatus() != 2
                || (createTransfer.getDropoffVendorStatus() != 1
                && createTransfer.getPickupLocation() != getActivity().getIntent().getIntExtra("project_id", 0)))) {
            truckSizeView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
            commentEditTextLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
            commentEditText.setFocusableInTouchMode(false);
            truckSizeViewET.setFocusableInTouchMode(false);
            saveSendTextView.setVisibility(View.GONE);
            saveAsDraftTextView.setVisibility(View.GONE);
            cancelTextView.setVisibility(View.GONE);
        }
        if (createTransfer.getStatus() >= 1 && (createTransfer.getStatus() != 2 || (createTransfer.getDropoffVendorStatus() != 1 &&
                createTransfer.getPickupLocation() != getActivity().getIntent().getIntExtra("project_id", 0)))) {
            departureTimeView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
            loadTimeView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
            arriveTimeView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
            pickdepartureTimeView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
            pickloadTimeView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
            pickarriveTimeView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
            departureTimeView.setClickable(false);
            loadTimeView.setClickable(false);
            arriveTimeView.setClickable(false);
            pickdepartureTimeView.setClickable(false);
            pickloadTimeView.setClickable(false);
            pickarriveTimeView.setClickable(false);
        }
        pickdepartureTimeET.setKeyListener(null);
        pickarriveTimeET.setKeyListener(null);
        pickloadTimeET.setKeyListener(null);
        dropdepartureTimeET.setKeyListener(null);
        commentEditText.setText(createTransfer.getComments() != null ? createTransfer.getComments() : "");
        truckSizeViewET.setText(createTransfer.getTruckSize() != null ? createTransfer.getTruckSize() : "");
        droparriveTimeET.setKeyListener(null);
        droploadTimeET.setKeyListener(null);

        if (createTransfer.getStatus() > 1 || !getActivity().getIntent().getStringExtra("transfer_option").equals("New Request") || transferType == 1) {
            setTransferRequest();
            dropoffTransferView.setVisibility(View.VISIBLE);
            pickupTransferView.setVisibility(View.VISIBLE);
            saveSendTextView.setText(getString(R.string.save_as_scheduled));
            if (createTransfer.getTransferId() != 0) {
                saveSendTextView.setText(getString(R.string.save));
            }
            if (transferType == 1) {
                saveSendTextView.setText(getString(R.string.save_as_scheduled));
                saveTransferAsDraftTextView.setText(getString(R.string.save));
                saveTransferAsDraftTextView.setVisibility(View.GONE);
            }
            saveAsDraftTextView.setText("Execute");
            truckSizeTextView.setText("Freight Line");
            pickarriveTimeET.setText(createTransfer.getPickupArriveTime());
            pickdepartureTimeET.setText(createTransfer.getPickupDepartureTime());
            pickloadTimeET.setText(createTransfer.getPickupLoadTime());
            droparriveTimeET.setText(createTransfer.getDropoffArriveTime());
            dropdepartureTimeET.setText(createTransfer.getDropoffDepartureTime());
            droploadTimeET.setText(createTransfer.getDropoffLoadTime());
            truckSizeViewET.setText(createTransfer.getFreightLine() != null ? createTransfer.getFreightLine() : "");
        }

        if (createTransfer.getStatus() == 3 && createTransfer.getDropoffVendorStatus() == 0 && createTransfer.getDropOffLocation() == projectId) {
            saveSendTextView.setText("Accept");
            saveAsDraftTextView.setText("Dispute");
            saveSendTextView.setVisibility(View.VISIBLE);
            cancelTextView.setVisibility(View.VISIBLE);
            saveAsDraftTextView.setVisibility(View.VISIBLE);
        }
        if (getActivity().getIntent().getStringExtra("transfer_option").equals("New Request")
                || transferType == 2) {
            saveTransferAsDraftTextView.setVisibility(View.GONE);
        }
        /*if (transferType == 2) {
            saveTransferAsDraftTextView.setVisibility(View.GONE);
        }*/
        if (transferType == 1) {
            if (createTransfer.getStatus() == 0) { // Draft & transfer state
                saveTransferAsDraftTextView.setVisibility(View.VISIBLE);
            } else if (createTransfer.getStatus() == 2) {// Schedule & transfer state
                saveSendTextView.setVisibility(View.GONE);
            }
        }


    }

    private void setTransferRequest() {
        mCreateTransfer = new CreateTransferRequest();
        mCreateTransfer.setTransferId(createTransfer.getTransferId());
        mCreateTransfer.setPickupDate(createTransfer.getPickupDate());
        mCreateTransfer.setPickupTime(createTransfer.getPickupTime());
        mCreateTransfer.setPickupLocation(String.valueOf(createTransfer.getPickupLocation()));
        mCreateTransfer.setPickupVendorStatus(createTransfer.getPickupVendorStatus());
        mCreateTransfer.setPickupPhone(createTransfer.getPickupPhone());
        mCreateTransfer.setPickupName(createTransfer.getPickupName());
        mCreateTransfer.setVendorLocation(createTransfer.getVendorLocation());
        mCreateTransfer.setUnloading(String.valueOf(createTransfer.getUnloading()));
        mCreateTransfer.setTotalWeight(createTransfer.getTotalWeight());
        mCreateTransfer.setDeliveryDate(createTransfer.getDeliveryDate());
        mCreateTransfer.setDropoffTime(createTransfer.getDropoffTime());
        mCreateTransfer.setDropOffLocation(String.valueOf(createTransfer.getDropOffLocation()));
        mCreateTransfer.setDropOffVendorStatus(createTransfer.getDropoffVendorStatus());
        mCreateTransfer.setDropOffPhone(createTransfer.getDropOffPhone());
        mCreateTransfer.setDropOffName(createTransfer.getDropOffName());
        mCreateTransfer.setActualPickupTime(createTransfer.getPickupArriveTime());
        mCreateTransfer.setActualDropoffTime(createTransfer.getDropoffArriveTime());
        mCreateTransfer.setActualPickupDepartureTime(createTransfer.getPickupDepartureTime());
        mCreateTransfer.setActualDropoffDepartureTime(createTransfer.getDropoffDepartureTime());
        mCreateTransfer.setActualPickupLoadTime(createTransfer.getPickupLoadTime());
        mCreateTransfer.setActualDropoffLoadTime(createTransfer.getDropoffLoadTime());
        mCreateTransfer.setFreightLine(createTransfer.getFreightLine());
        mCreateTransfer.setComments(createTransfer.getComments());
        mCreateTransfer.setCreateTransferEquipments(new ArrayList<>());
        if (createTransfer.getEquipment() != null) {
            for (Equipment equipment : createTransfer.getEquipment()) {
                CreateTransferEquipment createTransferEquipment = new CreateTransferEquipment();
                createTransferEquipment.setEquipmentId(equipment.getEquipmentId());
                createTransferEquipment.setTransferEquipmentId(equipment.getTransferEquipmentId());
                createTransferEquipment.setEquipmentStatus(String.valueOf(equipment.getEquipmentStatus()));
                createTransferEquipment.setName(String.valueOf(equipment.getName()));
                createTransferEquipment.setQuantity(String.valueOf(equipment.getQuantity()));
                createTransferEquipment.setStatus(String.valueOf(equipment.getStatus()));
                createTransferEquipment.setTotalWeight(String.valueOf(equipment.getTotalWeight()));
                createTransferEquipment.setTrackingNumber(String.valueOf(equipment.getTrackingNumber()));
                createTransferEquipment.setUnit(String.valueOf(equipment.getUnit()));
                createTransferEquipment.setWeight(String.valueOf(equipment.getWeight()));
                mCreateTransfer.getCreateTransferEquipments().add(createTransferEquipment);
                if ((createTransfer.getStatus() == 2 && (createTransfer.getPickupVendorStatus() != 1 && createTransfer.getPickupLocation() == projectId))) {
                    EquipmentRegion equipmentCategoriesDetail = null;
                    if (equipment.getEquipmentId() != 0) {
                        equipmentCategoriesDetail = mEquipementInventoryRepository.getEquipmentRegion(equipment.getEquipmentId());
                    }
                    List<EquipmentInventory> equipmentCategories = mEquipementInventoryRepository.checkgetEquipmentInventory(equipment.getEquipmentId(), 0, createTransfer.getPickupLocation());
                    if (equipmentCategoriesDetail != null && equipmentCategoriesDetail.getType().equals("Unique") && ((createTransfer.getPickupVendorStatus() == 1 || (equipmentCategories == null || equipmentCategories.size() == 0)) || TextUtils.isEmpty(equipment.getTrackingNumber()))) {
                        errorView.setVisibility(View.VISIBLE);
                        errorView.setText("Tracking Numbers for Unique Equipment are required in the Equipment step before Executing this Transfer.");
                        saveAsDraftTextView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rounded_gray_button));
                        saveAsDraftTextView.setClickable(false);
                    }
                }
            }
        }

    }

    @OnClick(R.id.saveSendTextView)
    public void onClickSaveSend() {
        if (NetworkService.isNetworkAvailable(getActivity())) {
            ((CreateTransfersActivity) getActivity()).hideKeyboard(getActivity());
            if (createTransfer.getStatus() == 3 && createTransfer.getDropoffVendorStatus() == 0 && createTransfer.getDropOffLocation() == projectId) {
                updateCallCreateTransfer(5);
            } else if (checkPickupValidations()) {
                ((CreateTransfersActivity) getActivity()).showMessageAlert(getContext(), "The pick-up info is required.", getString(R.string.ok), false);
            } else if (checkDropOffValidations()) {
                ((CreateTransfersActivity) getActivity()).showMessageAlert(getContext(), "The drop-off info is required.", getString(R.string.ok), false);
            } else if (checkDateTimeValidation()) {
                ((CreateTransfersActivity) getActivity()).showMessageAlert(getContext(), "Drop-off date should be greater than pick-up date.", getString(R.string.ok), false);

            } else if (checkSameLocationValidation()) {
                ((CreateTransfersActivity) getActivity()).showMessageAlert(getContext(), "The pick-up location and drop-off location must be different.", getString(R.string.ok), false);
            } else if (checkLocationValidation()) {
                ((CreateTransfersActivity) getActivity()).showMessageAlert(getContext(), getActivity().getString(R.string.locaion_validation_message), getString(R.string.ok), false);

            } else if (createTransfer.getEquipment() == null || createTransfer.getEquipment().size() == 0) {
                ((CreateTransfersActivity) getActivity()).showMessageAlert(getContext(), "The equipment detail is required.", getString(R.string.ok), false);
            } else if (transferType == 1) {
                updateCallCreateTransfer(2);
            } else if (createTransfer.getStatus() > 1 || !getActivity().getIntent().getStringExtra("transfer_option").equals("New Request")) {
                updateCallCreateTransfer(2);
            } else {
                Log.d("nitin", "onClickSaveSend: status 1");
                callAPI(1);
            }
        } else {
            ((CreateTransfersActivity) getActivity()).showMessageAlert(getActivity(), getString(R.string.internet_connection_check_transfer_overview), getString(R.string.ok), false);
        }
    }

    private boolean checkPickupValidations() {
        if (TextUtils.isEmpty(createTransfer.getPickupDate()) || TextUtils.isEmpty(createTransfer.getPickupTime()) || TextUtils.isEmpty(createTransfer.getPickupName()) || TextUtils.isEmpty(createTransfer.getPickupPhone())) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkLocationValidation() {
        if ((createTransfer.getPickupVendorStatus() == 0 && createTransfer.getPickupLocation() == projectId) || (createTransfer.getDropoffVendorStatus() == 0 && createTransfer.getDropOffLocation() == projectId)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean checkSameLocationValidation() {
        if ((createTransfer.getPickupVendorStatus() == 0 && createTransfer.getPickupLocation() == projectId) && (createTransfer.getDropoffVendorStatus() == 0 && createTransfer.getDropOffLocation() == projectId)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkDateTimeValidation() {
        if (!TextUtils.isEmpty(createTransfer.getDeliveryDate()) && !TextUtils.isEmpty(createTransfer.getDropoffTime())) {
            Date date = DateFormatter.getDateFromTransferDate(createTransfer.getDeliveryDate() + " 00:00 AM");
            Calendar dropOfCalendar = Calendar.getInstance();
            Calendar pickUpCalendar = Calendar.getInstance();
            dropOfCalendar.setTime(date);
            Date date1 = DateFormatter.getDateFromTransferDate(createTransfer.getPickupDate() + " 00:00 AM");
            pickUpCalendar.setTime(date1);
            if (pickUpCalendar.getTime().compareTo(dropOfCalendar.getTime()) > 0) {
                return true;
            }
        }
        return false;
    }

    private void updateCallCreateTransfer(int status) {
        mCreateTransfer.setActualPickupLoadTime(pickloadTimeET.getText().toString());
        mCreateTransfer.setActualPickupTime(pickarriveTimeET.getText().toString());
        mCreateTransfer.setActualPickupDepartureTime(pickdepartureTimeET.getText().toString());
        mCreateTransfer.setActualDropoffDepartureTime(dropdepartureTimeET.getText().toString());
        mCreateTransfer.setActualDropoffLoadTime(droploadTimeET.getText().toString());
        mCreateTransfer.setActualDropoffTime(droparriveTimeET.getText().toString());
        mCreateTransfer.setStatus(status);
        mCreateTransfer.setComments(commentEditText.getText().toString());
        mCreateTransfer.setFreightLine(truckSizeViewET.getText().toString());
        Log.d("nitin", "updateCallCreateTransfer: status " + status);
        ((CreateTransfersActivity) getActivity()).callCreateTransferAPI(mCreateTransfer);

    }


    private boolean checkDropOffValidations() {
        if (TextUtils.isEmpty(createTransfer.getDeliveryDate()) || TextUtils.isEmpty(createTransfer.getDropoffTime()) || TextUtils.isEmpty(createTransfer.getDropOffName()) || TextUtils.isEmpty(createTransfer.getDropOffPhone())) {
            return true;

        } else {
            return false;
        }
    }

    @OnClick(R.id.saveTransferAsDraftTextView)
    public void onClickSaveTransferAsDraftTextView() {
        if (NetworkService.isNetworkAvailable(getActivity())) {
            ((CreateTransfersActivity) getActivity()).hideKeyboard(getActivity());
            if (createTransfer.getStatus() == 3 && createTransfer.getDropoffVendorStatus() == 0 && createTransfer.getDropOffLocation() == projectId) {
                ((CreateTransfersActivity) getActivity()).addDisputeMessage();
            } else if (checkPickupValidations()) {
                ((CreateTransfersActivity) getActivity()).showMessageAlert(getContext(), "The pick-up info is required.", getString(R.string.ok), false);
            } else if (checkDropOffValidations()) {
                ((CreateTransfersActivity) getActivity()).showMessageAlert(getContext(), "The drop-off info is required.", getString(R.string.ok), false);
            } else if (checkDateTimeValidation()) {
                ((CreateTransfersActivity) getActivity()).showMessageAlert(getContext(), "Drop-off date should be greater than pick-up date.", getString(R.string.ok), false);

            } else if (checkSameLocationValidation()) {
                ((CreateTransfersActivity) getActivity()).showMessageAlert(getContext(), "The pick-up location and drop-off location must be different.", getString(R.string.ok), false);
            } else if (checkLocationValidation()) {
                ((CreateTransfersActivity) getActivity()).showMessageAlert(getContext(), "Either pick-up or drop-off location should be same as project location.", getString(R.string.ok), false);
            } else if (createTransfer.getStatus() > 1 || !getActivity().getIntent().getStringExtra("transfer_option")
                    .equals(getString(R.string.new_transfer))) {
                if (createTransfer.getEquipment() == null || createTransfer.getEquipment().size() == 0) {
                    ((CreateTransfersActivity) getActivity()).showMessageAlert(getContext(), "The equipment detail is required.", getString(R.string.ok), false);
                } else {
                    updateCallCreateTransfer(0);
                }
            } else {
                updateCallCreateTransfer(0);
            }

        } else {
            ((CreateTransfersActivity) getActivity()).showMessageAlert(getActivity(), getString(R.string.internet_connection_check_transfer_overview), getString(R.string.ok), false);
        }
    }

    @OnClick(R.id.saveAsDraftTextView)
    public void onClickSaveAsDraft() {
        if (NetworkService.isNetworkAvailable(getActivity())) {
            Log.d("nitin", "onClickSaveAsDraft: Status " + createTransfer.getStatus() + "transfer_option " + getActivity().getIntent().getStringExtra("transfer_option"));
            ((CreateTransfersActivity) getActivity()).hideKeyboard(getActivity());
            if (createTransfer.getStatus() == 3 && createTransfer.getDropoffVendorStatus() == 0 && createTransfer.getDropOffLocation() == projectId) {
                ((CreateTransfersActivity) getActivity()).addDisputeMessage();
            } else if (checkPickupValidations()) {
                ((CreateTransfersActivity) getActivity()).showMessageAlert(getContext(), "The pick-up info is required.", getString(R.string.ok), false);
            } else if (checkDropOffValidations()) {
                ((CreateTransfersActivity) getActivity()).showMessageAlert(getContext(), "The drop-off info is required.", getString(R.string.ok), false);
            } else if (checkDateTimeValidation()) {
                ((CreateTransfersActivity) getActivity()).showMessageAlert(getContext(), "Drop-off date should be greater than pick-up date.", getString(R.string.ok), false);

            } else if (checkSameLocationValidation()) {
                ((CreateTransfersActivity) getActivity()).showMessageAlert(getContext(), "The pick-up location and drop-off location must be different.", getString(R.string.ok), false);
            } else if (checkLocationValidation()) {
                ((CreateTransfersActivity) getActivity()).showMessageAlert(getContext(), "Either pick-up or drop-off location should be same as project location.", getString(R.string.ok), false);
            } else if (createTransfer.getStatus() > 1 || !getActivity().getIntent().getStringExtra("transfer_option").equals("New Request")) {
                if (createTransfer.getEquipment() == null || createTransfer.getEquipment().size() == 0) {
                    ((CreateTransfersActivity) getActivity()).showMessageAlert(getContext(), "The equipment detail is required.", getString(R.string.ok), false);
                }
                Log.d("nitin", "onClickSaveAsDraft: else if (createTransfer.getStatus() > 1  transferType " + transferType);
                if (transferType == 1) {
                    updateCallCreateTransfer(3);
                } else {
                    updateCallCreateTransfer(3);
                }
            }
            /*if (transferType == 1) {
                Log.d("nitin", "onClickSaveAsDraft: if transferType "+ transferType);
                updateCallCreateTransfer(3);
            } else {
                Log.d("nitin", "onClickSaveAsDraft: else transferType "+ transferType);
                callAPI(0);
            }*/
        } else {
            ((CreateTransfersActivity) getActivity()).showMessageAlert(getActivity(), getString(R.string.internet_connection_check_transfer_overview), getString(R.string.ok), false);
        }
    }

    private void callAPI(int status) {
        createTransfer.setStatus(status);
        createTransfer.setTruckSize(truckSizeViewET.getText().toString());
        createTransfer.setComments(commentEditText.getText().toString());
        ((CreateTransfersActivity) getActivity()).callTransferRequestAPI(createTransfer, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateOffline(((CreateTransfersActivity) getActivity()).isOffline());
        ((CreateTransfersActivity) getActivity()).selectInfo();
    }

    @OnClick(R.id.cancelTextView)
    public void cancelViewClick() {
        ((CreateTransfersActivity) getActivity()).hideKeyboard(getActivity());

        getActivity().onBackPressed();
    }

    @OnClick(R.id.pickdepartureTimeView)
    public void onPickDeparture() {
        ((CreateTransfersActivity) getActivity()).hideKeyboard(getActivity());
        TimePickerDialog.OnTimeSetListener myDateListener = (timePicker, i, i1) -> {
            i = i == 0 ? 12 : i;
            String amPM = "AM";
            if (i >= 12) {
                amPM = "PM";
            }
            i = i > 12 ? i - 12 : i;
            pickdepartureTimeET.setText(i + ":" + (i1 < 10 ? "0" : "") + i1 + " " + amPM);
        };
        new TimePickerDialog(getActivity(), myDateListener, calendar.get(calendar.HOUR_OF_DAY), calendar.get(calendar.MINUTE), false).show();

    }

    @OnClick(R.id.departureTimeView)
    public void onDropDeparture() {
        ((CreateTransfersActivity) getActivity()).hideKeyboard(getActivity());

        TimePickerDialog.OnTimeSetListener myDateListener = (timePicker, i, i1) -> {
            i = i == 0 ? 12 : i;
            String amPM = "AM";
            if (i >= 12) {
                amPM = "PM";
            }
            i = i > 12 ? i - 12 : i;
            dropdepartureTimeET.setText(i + ":" + (i1 < 10 ? "0" : "") + i1 + " " + amPM);
        };
        new TimePickerDialog(getActivity(), myDateListener, calendar.get(calendar.HOUR_OF_DAY), calendar.get(calendar.MINUTE), false).show();
    }

    @OnClick(R.id.pickarriveTimeView)
    public void onPickArrive() {
        ((CreateTransfersActivity) getActivity()).hideKeyboard(getActivity());
        TimePickerDialog.OnTimeSetListener myDateListener = (timePicker, i, i1) -> {
            i = i == 0 ? 12 : i;
            String amPM = "AM";
            if (i >= 12) {
                amPM = "PM";
            }
            i = i > 12 ? i - 12 : i;
            pickarriveTimeET.setText(i + ":" + (i1 < 10 ? "0" : "") + i1 + " " + amPM);
        };
        new TimePickerDialog(getActivity(), myDateListener, calendar.get(calendar.HOUR_OF_DAY), calendar.get(calendar.MINUTE), false).show();
    }

    @OnClick(R.id.arriveTimeView)
    public void onDropArrive() {
        ((CreateTransfersActivity) getActivity()).hideKeyboard(getActivity());

        TimePickerDialog.OnTimeSetListener myDateListener = (timePicker, i, i1) -> {
            i = i == 0 ? 12 : i;
            String amPM = "AM";
            if (i >= 12) {
                amPM = "PM";
            }
            i = i > 12 ? i - 12 : i;
            droparriveTimeET.setText(i + ":" + (i1 < 10 ? "0" : "") + i1 + " " + amPM);
        };
        new TimePickerDialog(getActivity(), myDateListener, calendar.get(calendar.HOUR_OF_DAY), calendar.get(calendar.MINUTE), false).show();
    }

    @OnClick(R.id.pickloadTimeView)
    public void onPickLoad() {
        ((CreateTransfersActivity) getActivity()).hideKeyboard(getActivity());

        TimePickerDialog.OnTimeSetListener myDateListener = (timePicker, i, i1) -> {
            i = i == 0 ? 12 : i;
            String amPM = "AM";
            if (i >= 12) {
                amPM = "PM";
            }
            i = i > 12 ? i - 12 : i;
            pickloadTimeET.setText(i + ":" + (i1 < 10 ? "0" : "") + i1 + " " + amPM);
        };
        new TimePickerDialog(getActivity(), myDateListener, calendar.get(calendar.HOUR_OF_DAY), calendar.get(calendar.MINUTE), false).show();
    }

    @OnClick(R.id.loadTimeView)
    public void onDropLoad() {
        ((CreateTransfersActivity) getActivity()).hideKeyboard(getActivity());
        TimePickerDialog.OnTimeSetListener myDateListener = (timePicker, i, i1) -> {
            i = i == 0 ? 12 : i;
            String amPM = "AM";
            if (i >= 12) {
                amPM = "PM";
            }
            i = i > 12 ? i - 12 : i;
            droploadTimeET.setText(i + ":" + (i1 < 10 ? "0" : "") + i1 + " " + amPM);
        };
        new TimePickerDialog(getActivity(), myDateListener, calendar.get(calendar.HOUR_OF_DAY), calendar.get(calendar.MINUTE), false).show();
    }

    public CreateTransferRequest getUpdatedCreateTransfer() {
        return mCreateTransfer;
    }

    public void updateOffline(Boolean isOffline) {
        if (getContext() != null && saveAsDraftTextView != null && saveSendTextView != null) {
            if (isOffline) {
                saveAsDraftTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                saveAsDraftTextView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rounded_opacity_blue_view));
                saveSendTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                saveSendTextView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rounded_opacity_blue_view));
                saveAsDraftTextView.setClickable(false);
                saveSendTextView.setClickable(false);
            } else {
                saveAsDraftTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                saveAsDraftTextView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_blue_button));
                saveSendTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                saveSendTextView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_blue_button));
                saveAsDraftTextView.setClickable(true);
                saveSendTextView.setClickable(true);
            }
        }
    }
}


