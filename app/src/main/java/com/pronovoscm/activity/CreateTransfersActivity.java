package com.pronovoscm.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.data.InventoryProvider;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.data.TransferOverviewProvider;
import com.pronovoscm.fragments.CreateTransferDropoffFragment;
import com.pronovoscm.fragments.CreateTransferEquipmentFragment;
import com.pronovoscm.fragments.CreateTransferInfoFragment;
import com.pronovoscm.fragments.CreateTransferPickupFragment;
import com.pronovoscm.model.request.createtransfer.CreateTransferRequest;
import com.pronovoscm.model.request.deleteequipment.DeleteEquipmentRequest;
import com.pronovoscm.model.request.transfercontact.TransferContactRequest;
import com.pronovoscm.model.request.transferdetails.TransferDetailRequest;
import com.pronovoscm.model.request.transferlocation.TransferLocationRequest;
import com.pronovoscm.model.request.transferrequest.Equipment;
import com.pronovoscm.model.request.transferrequest.TransferRequest;
import com.pronovoscm.model.response.createtransfer.CreateTransferResponse;
import com.pronovoscm.model.response.deleteequipment.DeleteEquipmentResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.transfercontacts.Contacts;
import com.pronovoscm.model.response.transfercontacts.TransferContactsResponse;
import com.pronovoscm.model.response.transferdelete.TransferDeleteResponse;
import com.pronovoscm.model.response.transferdetail.Details;
import com.pronovoscm.model.response.transferdetail.Equipments;
import com.pronovoscm.model.response.transferlocation.TransferLocationResponse;
import com.pronovoscm.model.response.transferlocation.TransferLocationVendorResponse;
import com.pronovoscm.model.response.transferrequest.TransferRequestResponse;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.ui.CustomProgressBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateTransfersActivity extends BaseActivity {
    private static final int GET_EQUIPMENT = 652;
    private static final int GET_DISPUTE_MESSAGE = 951;
    @Inject
    TransferOverviewProvider transferOverviewProvider;
    @Inject
    ProjectsProvider mProjectsProvider;
    @Inject
    InventoryProvider mInventoryProvider;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.deleteImageView)
    ImageView deleteImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.no1TextView)
    TextView no1TextView;
    @BindView(R.id.no2TextView)
    TextView no2TextView;
    @BindView(R.id.no3TextView)
    TextView no3TextView;
    @BindView(R.id.no4TextView)
    TextView no4TextView;
    @BindView(R.id.pickupTextView)
    TextView pickupTextView;
    @BindView(R.id.dropoffTextView)
    TextView dropoffTextView;
    @BindView(R.id.equipmentTextView)
    TextView equipmentTextView;
    @BindView(R.id.infoTextView)
    TextView infoTextView;
    @BindView(R.id.pickDropView)
    View pickDropView;
    @BindView(R.id.dropEqView)
    View dropEqView;
    @BindView(R.id.eqInfoView)
    View eqInfoView;
    @BindView(R.id.addImageView)
    ImageView addImageView;//    @BindView(R.id.nestedScrollView)
    ArrayList<Equipment> equipmentArrayList = null;
    private List<Contacts> pickUpjobSitecontactList = new ArrayList<>();
    private List<Contacts> dropOffjobSitecontactList = new ArrayList<>();

    //    NestedScrollView nestedScrollView;
    private Drawable selectedDrawable;
    private Drawable completedDrawable;
    private Drawable disableDrawable;
    private Drawable inCompletedDrawable;
    private int coloeWhite;
    private int colorPrimary;
    private int colorGray;
    private int colorDisableGray;
    private TransferRequest createTransfer;
    private int projectId;
    private int regionId;
    private LoginResponse loginResponse;
    private CreateTransferPickupFragment createTransferPickupFragment;
    private CreateTransferDropoffFragment createTransferDropoffFragment;
    private CreateTransferEquipmentFragment createTransferEquipmentFragment;
    private CreateTransferInfoFragment createTransferInfoFragment;
    private String transferOption;
    private AlertDialog alertDialog;
    private Details transferDetails;
    //    private Drawable enableDrawable;
//    private Drawable disableDrawable;
    private boolean updateTransferData = false;
    private TransferLocationResponse transferOverviewResponse;
    private TransferLocationVendorResponse transferOverviewVendorResponse;
    private TransferContactsResponse jobSiteTransferContactsResponse;
    private boolean canAheadEquipment = false;
    private boolean isPickUpDone = false;
    private boolean isDropOffDone = false;
    private int canEditTransfer;
    private boolean allDone = false;
    private boolean isOffline = false;
    private boolean newDraftCreated = false;
    private int transferType = -1;

    public boolean isAllDone() {
        Log.i("dropoff", "openTransferDropOff: get dropoff " + allDone);
        return allDone;
    }

    public void setAllDone(boolean allDone) {
        this.allDone = allDone;
        Log.i("dropoff", "openTransferDropOff: dropoff set " + allDone);
    }

    public List<Contacts> getPickUpjobSitecontactList() {
        return pickUpjobSitecontactList;
    }

    public void setPickUpjobSitecontactList(List<Contacts> pickUpjobSitecontactList) {
        this.pickUpjobSitecontactList = pickUpjobSitecontactList;
    }

    public void clearPickUpjobSitecontactList() {
        pickUpjobSitecontactList.clear();
    }

    public List<Contacts> getDropOffjobSitecontactList() {
        return dropOffjobSitecontactList;
    }

    public void setDropOffjobSitecontactList(List<Contacts> dropOffjobSitecontactList) {
        this.dropOffjobSitecontactList = dropOffjobSitecontactList;
    }

    public void clearDropOffjobSitecontactList() {
        dropOffjobSitecontactList.clear();
    }

    @Override
    protected int doGetContentView() {
        return R.layout.create_transfer_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        createTransfer = new TransferRequest();
        selectedDrawable = getResources().getDrawable(R.drawable.ic_circle_filled_blue);
        completedDrawable = getResources().getDrawable(R.drawable.ic_circle_border_blue);
        inCompletedDrawable = getResources().getDrawable(R.drawable.ic_circle_border_gray);
        disableDrawable = getResources().getDrawable(R.drawable.ic_disable_circle);
//        enableDrawable = ContextCompat.getDrawable(this, R.drawable.rounded_gray_border);
//        disableDrawable = ContextCompat.getDrawable(this, R.drawable.disable_rounded_gray_border);

        coloeWhite = ContextCompat.getColor(this, R.color.white);
        colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
        colorGray = ContextCompat.getColor(this, R.color.gray_6c7b8a);
        colorDisableGray = ContextCompat.getColor(this, R.color.disable_gray);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        canEditTransfer = loginResponse.getUserDetails().getPermissions().get(0).getEditProjectTransfers();
        projectId = getIntent().getIntExtra("project_id", 0);
        transferOption = getIntent().getStringExtra("transfer_option");
        transferDetails = getIntent().getParcelableExtra("details");
        titleTextView.setText(transferOption);
        if (transferDetails != null) {
            if (transferDetails.getStatus() == 1 || transferDetails.getStatus() == 4 || (transferDetails.getStatus() == 2
                    && (transferDetails.getPickupIsVendor() == 1 || transferDetails.getPickupLocation() != projectId)) || (transferDetails.getStatus() == 3 && (transferDetails.getDropoffIsVendor() == 1 || transferDetails.getDropoffLocation() != projectId))) {
            } else if ((transferDetails.getStatus() == 3 && !(transferDetails.getDropoffIsVendor() == 1 || transferDetails.getDropoffLocation() != projectId))) {
            } else if (canEditTransfer == 1) {
                deleteImageView.setVisibility(View.VISIBLE);
            }
            createTransfer = new TransferRequest();
            if (transferDetails.getPickupIsVendor() == 0) {
                callTransferJobsiteContactAPI("projects", transferDetails.getPickupLocation());
            }
            createTransfer.setInterofficeTransfer(transferDetails.isInterofficeTransfer());
            createTransfer.setPickupLocationName(transferDetails.getPickupLocationName());
            createTransfer.setDropoffLocationName(transferDetails.getDropoffLocationName());

            createTransfer.setPickupDate(transferDetails.getPickupDate());
            createTransfer.setPickupTime(transferDetails.getPickupTime());
            createTransfer.setPickupName(transferDetails.getPickupContact());
            createTransfer.setPickupPhone(addMasking(transferDetails.getPickupContactNumber() != null ? transferDetails.getPickupContactNumber().replace("-", "") : ""));
            createTransfer.setPickupVendorStatus(transferDetails.getPickupIsVendor());
            createTransfer.setPickupLocation(transferDetails.getPickupLocation());
            createTransfer.setVendorLocation(transferDetails.getVendorLocation());
            createTransfer.setDeliveryDate(transferDetails.getDropoffDate());
            createTransfer.setDropoffTime(transferDetails.getDropoffTime());
            createTransfer.setDropOffName(transferDetails.getDropoffContact());
            createTransfer.setDropOffPhone(addMasking(transferDetails.getDropoffContactNumber() != null ? transferDetails.getDropoffContactNumber().replace("-", "") : ""));
            createTransfer.setDropoffVendorStatus(transferDetails.getDropoffIsVendor());
            createTransfer.setDropOffLocation(transferDetails.getDropoffLocation());
            createTransfer.setRoundTrip(transferDetails.getRoundTrip());
            createTransfer.setStatus(transferDetails.getStatus());
            createTransfer.setUnloading(transferDetails.getUnloadingMethod());
            createTransfer.setComments(transferDetails.getComments());
            createTransfer.setFreightLine(transferDetails.getFreightLine());
            createTransfer.setTruckSize(transferDetails.getTruckSize());
            createTransfer.setPickupLoadTime(transferDetails.getActualPickupLoadTime());
            createTransfer.setPickupDepartureTime(transferDetails.getActualPickupDepartureTime());
            createTransfer.setPickupArriveTime(transferDetails.getActualPickupTime());
            createTransfer.setDropoffLoadTime(transferDetails.getActualDropoffLoadTime());
            createTransfer.setDropoffDepartureTime(transferDetails.getActualDropoffDepartureTime());
            createTransfer.setDropoffArriveTime(transferDetails.getActualDropoffTime());
            createTransfer.setTransferId(transferDetails.getTransferId());
            if (createTransfer.getEquipment() == null) {
                createTransfer.setEquipment(new ArrayList<>());
            }
            for (Equipments equipment :
                    transferDetails.getEquipments()) {
                Equipment equipment1 = new Equipment();
                equipment1.setName(equipment.getName());
                equipment1.setUnit(TextUtils.isEmpty(equipment.getUnits()) || equipment.getUnits().equals("-") ? 0 : Float.parseFloat(equipment.getUnits()));
                equipment1.setEquipmentStatus(equipment.getStatus());
                equipment1.setTransferEquipmentId(equipment.getTransferEquipmentId());
                equipment1.setWeight(equipment.getWeight());
                equipment1.setTotalWeight(String.valueOf(equipment.getTotalWeight()));
                equipment1.setStatus(equipment.getStatus());
                equipment1.setQuantity(String.valueOf(equipment.getQuantity()));
                equipment1.setEquipmentId(equipment.getEquipmentId());
                equipment1.setTrackingNumber(equipment.getTracking());
                createTransfer.getEquipment().add(equipment1);
            }

            titleTextView.setText((createTransfer.getStatus() > 1 ? "Transfer #" : "Request #") + createTransfer.getTransferId());
            if (canEditTransfer != 1) {
                createTransfer.setStatus(5);
            }
            transferType = getIntent().getIntExtra(Constants.INTENT_KEY_TRANSFER_TYPE, -1);
            if (transferType == 1) {
                titleTextView.setText("Transfer #" + createTransfer.getTransferId());
            }
            no2TextView.setBackground(completedDrawable);
            no2TextView.setTextColor(colorPrimary);
            if (createTransfer.getStatus() == 5 || createTransfer.getEquipment().size() > 0) {
                no3TextView.setBackground(completedDrawable);
                no3TextView.setTextColor(colorPrimary);
                no4TextView.setBackground(completedDrawable);
                no4TextView.setTextColor(colorPrimary);
            }
            no1TextView.setClickable(true);
            no2TextView.setClickable(true);
            no3TextView.setClickable(true);
            no4TextView.setClickable(true);
            pickupTextView.setClickable(true);
            dropoffTextView.setClickable(true);
            equipmentTextView.setClickable(true);
            infoTextView.setClickable(true);


        } else if (transferOption.equals("New Request")) {
            disableAllView();
            no1TextView.setClickable(false);
            no2TextView.setClickable(false);
            no3TextView.setClickable(false);
            no4TextView.setClickable(false);
            pickupTextView.setClickable(false);
            dropoffTextView.setClickable(false);
            equipmentTextView.setClickable(false);
            infoTextView.setClickable(false);
        } else {
            no1TextView.setClickable(false);
            no2TextView.setClickable(false);
            no3TextView.setClickable(false);
            no4TextView.setClickable(false);
            pickupTextView.setClickable(false);
            dropoffTextView.setClickable(false);
            equipmentTextView.setClickable(false);
            infoTextView.setClickable(false);
            disableAllView();
        }

        regionId = mProjectsProvider.getProject(projectId, loginResponse.getUserDetails().getUsers_id());
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.GONE);
        addImageView.setVisibility(View.INVISIBLE);

        loadPickupView();
        callTransferLocationAPI("projects");
        callTransferLocationVendorAPI("vendors");

    }

    private void disableAllView() {
        eqInfoView.setBackgroundColor(colorDisableGray);
        dropEqView.setBackgroundColor(colorDisableGray);
        pickDropView.setBackgroundColor(colorDisableGray);
    }

    /**
     * Get List
     */
    public void callTransferLocationAPI(String type) {
        TransferLocationRequest transferOverviewRequest = new TransferLocationRequest();
        transferOverviewRequest.setRegionId(regionId);
        transferOverviewRequest.setType(type);
        transferOverviewProvider.getTransferLocations(transferOverviewRequest, new ProviderResult<TransferLocationResponse>() {
            @Override
            public void success(TransferLocationResponse transferOverviewResponse) {
                CreateTransfersActivity.this.transferOverviewResponse = transferOverviewResponse;
                updateData(transferOverviewResponse);
            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(CreateTransfersActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(CreateTransfersActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(CreateTransfersActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {

            }
        }, loginResponse);
    }

    @OnClick(R.id.deleteImageView)
    public void deleteTransfer() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage("Are you sure you want to delete this request?");
        if (transferDetails.getStatus() > 1) {
            alertDialog.setMessage("Are you sure you want to delete this Transfer?");
        }
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> {
            dialog.dismiss();
        });
        alertDialog.setCancelable(false);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
            alertDialog.dismiss();
            CustomProgressBar.showDialog(CreateTransfersActivity.this);
            callTransferDelete(transferDetails.getTransferId());
        });
        alertDialog.show();
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(ContextCompat.getColor(this, R.color.gray_948d8d));
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

    }

    /**
     * Get List
     */
    public void callTransferDelete(int transferID) {
        TransferDetailRequest transferOverviewRequest = new TransferDetailRequest();
        transferOverviewRequest.setTransferId(transferID);

        transferOverviewProvider.callTransferDelete(transferOverviewRequest, new ProviderResult<TransferDeleteResponse>() {
            @Override
            public void success(TransferDeleteResponse transferOverviewResponse) {
                CustomProgressBar.dissMissDialog(CreateTransfersActivity.this);
                if (CreateTransfersActivity.this != null) {
                    CreateTransfersActivity.this.finish();
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(CreateTransfersActivity.this);
                startActivity(new Intent(CreateTransfersActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(CreateTransfersActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(CreateTransfersActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                CreateTransfersActivity.this.finish();
            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(CreateTransfersActivity.this);
                showMessageAlert(CreateTransfersActivity.this, message, getString(R.string.ok));
            }
        }, loginResponse);
    }


    /**
     * Get List
     */
    public void callTransferLocationVendorAPI(String type) {
        TransferLocationRequest transferOverviewRequest = new TransferLocationRequest();
        transferOverviewRequest.setRegionId(regionId);
        transferOverviewRequest.setType(type);
        transferOverviewProvider.getTransferVendorLocations(transferOverviewRequest, new ProviderResult<TransferLocationVendorResponse>() {
            @Override
            public void success(TransferLocationVendorResponse transferOverviewResponse) {
                CreateTransfersActivity.this.transferOverviewVendorResponse = transferOverviewResponse;
                updateData(transferOverviewResponse);
            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(CreateTransfersActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(CreateTransfersActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(CreateTransfersActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {

            }
        }, loginResponse);
    }

    /**
     * Get List
     */
    public void callTransferJobsiteContactAPI(String type, int pickupLocation) {
        TransferContactRequest transferOverviewRequest = new TransferContactRequest();
        transferOverviewRequest.setRegionId(regionId);
        transferOverviewRequest.setType(type);
        transferOverviewRequest.setProjectId(pickupLocation);
        transferOverviewProvider.getTransferContacts(transferOverviewRequest, new ProviderResult<TransferContactsResponse>() {
            @Override
            public void success(TransferContactsResponse transferContactsResponse) {
                jobSiteTransferContactsResponse = transferContactsResponse;
                updateContactData(transferContactsResponse);
            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(CreateTransfersActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(CreateTransfersActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(CreateTransfersActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {

            }
        }, loginResponse);
    }

    /**
     * Store Request
     */
    public void callTransferRequestAPI(TransferRequest transferRequest, boolean removeEquipment) {
        CustomProgressBar.showDialog(this);
        transferRequest.setProjectId(projectId);
        if (transferRequest.getEquipment() != null) {
            equipmentArrayList = new ArrayList<>(transferRequest.getEquipment());
        }
        if (removeEquipment) {
            transferRequest.setEquipment(new ArrayList<>());
        }
        transferOverviewProvider.callTransferRequest(transferRequest, new ProviderResult<TransferRequestResponse>() {
            @Override
            public void success(TransferRequestResponse transferRequestResponse) {
                newDraftCreated = true;
                CustomProgressBar.dissMissDialog(CreateTransfersActivity.this);
                createTransfer.setEquipment(equipmentArrayList);
                if (transferRequestResponse.getData().getTransfer() != null) {
                    createTransfer.setTransferId(transferRequestResponse.getData().getTransfer().getTransferId());
                    titleTextView.setText("Request #" + createTransfer.getTransferId());
                    Fragment f = getSupportFragmentManager().findFragmentById(R.id.listContainer);
                    if (f instanceof CreateTransferInfoFragment && createTransferInfoFragment != null) {
                        startActivity(new Intent(CreateTransfersActivity.this, TransferOverviewDetailsActivity.class).putExtra("project_id", projectId)
                                .putExtra("selected_tab", createTransfer.getStatus()));
                        CreateTransfersActivity.this.finish();
                    } else if (f instanceof CreateTransferEquipmentFragment && createTransferEquipmentFragment != null) {
                        createTransfer.getEquipment().clear();
                        createTransfer.setEquipment(new ArrayList<>());
                        for (com.pronovoscm.model.response.transferrequest.Equipment equipment : transferRequestResponse.getData().getTransfer().getEquipment()) {
                            Equipment equipment1 = new Equipment();
                            equipment1.setName(equipment.getName());
                            equipment1.setUnit(TextUtils.isEmpty(equipment.getUnit()) || equipment.getUnit().equals("-") ? 0 : Float.parseFloat(equipment.getUnit()));
                            equipment1.setEquipmentStatus(equipment.getStatus());
                            equipment1.setTransferEquipmentId(equipment.getTransferEquipmentId());
                            equipment1.setWeight(equipment.getWeight());
                            equipment1.setTotalWeight(String.valueOf(equipment.getTotalWeight()));
                            equipment1.setStatus(equipment.getStatus());
                            equipment1.setQuantity(String.valueOf(equipment.getQuantity()));
                            equipment1.setEquipmentId(equipment.getEquipmentId());
//                            equipment1.setTrackingNumber(equipment.getTr());
                            createTransfer.getEquipment().add(equipment1);
                        }
                        openInfo();
                    } else {
                        if (f instanceof CreateTransferDropoffFragment && createTransferDropoffFragment != null) {
                            openEquipment();
                        } else {
                            startActivity(new Intent(CreateTransfersActivity.this, TransferOverviewDetailsActivity.class).putExtra("project_id", projectId)
                                    .putExtra("selected_tab", getIntent().getIntExtra("selected_tab", 0)));
                            CreateTransfersActivity.this.finish();
                        }
                    }
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(CreateTransfersActivity.this);
                startActivity(new Intent(CreateTransfersActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(CreateTransfersActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(CreateTransfersActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                showMessageAlert(CreateTransfersActivity.this, message, getString(R.string.ok), false);
                CustomProgressBar.dissMissDialog(CreateTransfersActivity.this);

            }
        }, loginResponse);/*
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.listContainer);
        if (f instanceof CreateTransferInfoFragment && createTransferEquipmentFragment != null) {
            CreateTransfersActivity.this.finish();
        } else if (f instanceof CreateTransferEquipmentFragment && createTransferEquipmentFragment != null) {
            openInfo();
        } else {
            openEquipment();
        }*/
    }

    public void openInfo() {
        allDone = false;
        hideKeyboard(this);
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.listContainer);
        if (createTransfer.getStatus() != 5 && f instanceof CreateTransferPickupFragment && createTransferPickupFragment != null && !createTransferPickupFragment.checkEmptyValidation()) {
            return;
        } else if (createTransfer.getStatus() != 5 && f instanceof CreateTransferDropoffFragment && createTransferDropoffFragment != null && !createTransferDropoffFragment.checkEmptyValidation()) {
            return;
        } else if (!(f instanceof CreateTransferInfoFragment)) {
            no4TextView.setClickable(true);
            infoTextView.setClickable(true);
            addImageView.setVisibility(View.INVISIBLE);
            eqInfoView.setBackgroundColor(colorPrimary);
            infoTextView.setTextColor(colorGray);

//            nestedScrollView.scrollTo(0, 0);
            no3TextView.setBackground(completedDrawable);
            no3TextView.setTextColor(colorPrimary);
            no4TextView.setBackground(selectedDrawable);
            no4TextView.setTextColor(coloeWhite);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            createTransferInfoFragment = new CreateTransferInfoFragment();
            fragmentTransaction.replace(R.id.listContainer, createTransferInfoFragment, createTransferInfoFragment.getClass().getSimpleName()).addToBackStack(CreateTransferInfoFragment.class.getName());
            try {
                fragmentTransaction.commit();
            } catch (IllegalStateException e) {
            }
        }

    }

    /**
     * Alert to show message
     *
     * @param context
     * @param message
     * @param positiveButtonText
     * @param closeActivity
     */
    public void showMessageAlert(final Context context, String message, String positiveButtonText, boolean closeActivity) {
        try {
            if (alertDialog == null || !alertDialog.isShowing()) {
                alertDialog = new AlertDialog.Builder(context).create();
            }
            alertDialog.setMessage(message);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, positiveButtonText, (dialog, which) -> {
                alertDialog.dismiss();
                if (closeActivity && !newDraftCreated) {
                    startActivity(new Intent(this, TransferOverviewDetailsActivity.class).putExtra("project_id", projectId)
                            .putExtra("selected_tab", getIntent().getIntExtra("selected_tab", 0)));
                    CreateTransfersActivity.this.finish();
                } else if (closeActivity) {
                    startActivity(new Intent(this, TransferOverviewDetailsActivity.class).putExtra("project_id", projectId)
                            .putExtra("selected_tab", 0));
                    CreateTransfersActivity.this.finish();
                }

            });
            if (closeActivity) {
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> {
                    alertDialog.dismiss();
                });
            }
            if (alertDialog != null && !alertDialog.isShowing()) {
                alertDialog.setCancelable(false);
                alertDialog.show();
            }


        } catch (Exception e) {
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case GET_EQUIPMENT:
                    updateTransferData = true;
                    boolean deleteEquipment = data.getBooleanExtra("delete_equipment", false);
                    int position = data.getIntExtra("position", -1);
                    if (deleteEquipment) {
                        position = data.getIntExtra("remove_position", -1);
                        createTransfer.getEquipment().remove(position);
                        createTransferEquipmentFragment.updateData();
                        return;
                    }
                    Equipment equipment = (Equipment) data.getParcelableExtra("equipment_new");
                    if (createTransfer.getEquipment() != null) {
                        if (position != -1) {
                            createTransfer.getEquipment().set(position, equipment);
                        } else {
                            createTransfer.getEquipment().add(equipment);
                        }
                    } else {
                        ArrayList<Equipment> eqList = new ArrayList<>();
                        eqList.add(equipment);
                        createTransfer.setEquipment(eqList);
                    }
                    Fragment f = getSupportFragmentManager().findFragmentById(R.id.listContainer);
                    if (f instanceof CreateTransferEquipmentFragment && createTransferEquipmentFragment != null) {
                        createTransferEquipmentFragment.updateData();
                    }
                    if (canAheadEquipment) {
                        no4TextView.setBackground(inCompletedDrawable);
                        no4TextView.setTextColor(colorGray);
                        infoTextView.setTextColor(colorGray);
                        eqInfoView.setBackgroundColor(colorPrimary);
                    }
                    break;
                case GET_DISPUTE_MESSAGE:
                    String disputeMessage = data.getStringExtra("dispute_message");
                    if (createTransferInfoFragment != null) {
                        CreateTransferRequest createTransferRequest = createTransferInfoFragment.getUpdatedCreateTransfer();
                        createTransferRequest.setDisputeNotes(disputeMessage);
                        createTransferRequest.setStatus(4);
                        callCreateTransferAPI(createTransferRequest);
                    }
                    break;
            }
        }
    }

    /**
     * Get List
     */
    public void callTransferContactAPI(TransferContactRequest transferOverviewRequest) {
        transferOverviewRequest.setProjectId(projectId);
        transferOverviewProvider.getTransferContacts(transferOverviewRequest, new ProviderResult<TransferContactsResponse>() {
            @Override
            public void success(TransferContactsResponse transferContactsResponse) {
                updateContactData(transferContactsResponse);
            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(CreateTransfersActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(CreateTransfersActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(CreateTransfersActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {

            }
        }, loginResponse);
    }

    private void updateContactData(TransferContactsResponse transferContactsResponse) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.listContainer);
        if (f instanceof CreateTransferPickupFragment) {
            createTransferPickupFragment.refreshContactData(transferContactsResponse);
        } else if (f instanceof CreateTransferDropoffFragment) {
            ((CreateTransferDropoffFragment) f).refreshContactData(transferContactsResponse);
        }
    }

    private void updateData(TransferLocationResponse transferOverviewResponse) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.listContainer);
        if (f instanceof CreateTransferPickupFragment) {
            createTransferPickupFragment.refreshData(transferOverviewResponse);
        } else if (f instanceof CreateTransferDropoffFragment) {
            ((CreateTransferDropoffFragment) f).refreshData(transferOverviewResponse);

        }
    }

    private void updateData(TransferLocationVendorResponse transferOverviewResponse) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.listContainer);
        if (f instanceof CreateTransferPickupFragment) {
            createTransferPickupFragment.refreshData(transferOverviewResponse);
        } else if (f instanceof CreateTransferDropoffFragment) {
            ((CreateTransferDropoffFragment) f).refreshData(transferOverviewResponse);
        }
    }

    public boolean isOffline() {
        return isOffline;
    }

    public void setOffline(boolean offline) {
        isOffline = offline;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.listContainer);
        if (f instanceof CreateTransferInfoFragment && createTransferInfoFragment != null) {
            createTransferInfoFragment.updateOffline(event);
        }
        isOffline = event;
        if (event) {
            if (f instanceof CreateTransferEquipmentFragment && createTransferEquipmentFragment != null) {
                createTransferEquipmentFragment.setOffline(event);
            }
            offlineTextView.setVisibility(View.VISIBLE);
        } else {
            offlineTextView.setVisibility(View.GONE);
        }
    }

    public void loadPickupView() {
        hideKeyboard(this);
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.listContainer);
   /*     if (f instanceof CreateTransferDropoffFragment && createTransferDropoffFragment != null && !createTransferDropoffFragment.checkValidation()) {
            return;
        }
   */
        if (!(f instanceof CreateTransferPickupFragment)) {
            addImageView.setVisibility(View.INVISIBLE);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            createTransferPickupFragment = new CreateTransferPickupFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("projectId", projectId);
            createTransferPickupFragment.setArguments(bundle);
            fragmentTransaction.add(R.id.listContainer, createTransferPickupFragment, createTransferPickupFragment.getClass().getSimpleName());
            try {
                fragmentTransaction.commit();
            } catch (IllegalStateException e) {

            }
        }
    }

    public void openPickupView() {
        hideKeyboard(this);
        allDone = false;
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.listContainer);
//        if (f instanceof CreateTransferDropoffFragment && createTransferDropoffFragment != null && !createTransferDropoffFragment.checkValidation()) {
//            return;
//        }
        if (!(f instanceof CreateTransferPickupFragment)) {
            addImageView.setVisibility(View.INVISIBLE);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            createTransferPickupFragment = new CreateTransferPickupFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("projectId", projectId);
            createTransferPickupFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.listContainer, createTransferPickupFragment, createTransferPickupFragment.getClass().getSimpleName());
            try {
                fragmentTransaction.commit();
            } catch (IllegalStateException e) {

            }
        }
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.listContainer);
        if (createTransfer.getStatus() == 5 || createTransfer.getStatus() == 1 || createTransfer.getStatus() == 4 || (createTransfer.getStatus() == 2 && (createTransfer.getPickupVendorStatus() == 1 || createTransfer.getPickupLocation() != projectId)) || (createTransfer.getStatus() == 3 && (createTransfer.getDropoffVendorStatus() == 1 || createTransfer.getDropOffLocation() != projectId))) {
            startActivity(new Intent(CreateTransfersActivity.this, TransferOverviewDetailsActivity.class).putExtra("project_id", projectId).putExtra("selected_tab", getIntent().getIntExtra("selected_tab", 0)));
            CreateTransfersActivity.this.finish();
        } else if (f instanceof CreateTransferPickupFragment && createTransfer.getTransferId() == 0 && (createTransferPickupFragment == null || !createTransferPickupFragment.isSomethingFilled())) {
            if (getIntent().getBooleanExtra("open_details", false)) {
                startActivity(new Intent(CreateTransfersActivity.this, TransferOverviewDetailsActivity.class).putExtra("project_id", projectId).putExtra("selected_tab", getIntent().getIntExtra("selected_tab", 0)));
            }
            CreateTransfersActivity.this.finish();
        } else {
            if (createTransfer.getTransferId() == 0 || (transferDetails != null && !isUpdateCreateTransferData()
                    && transferDetails.getStatus() != 1) || f instanceof CreateTransferEquipmentFragment || f instanceof CreateTransferInfoFragment) {
                showMessageAlert(this, getString(R.string.are_you_sure_you_want_to_exit), getString(R.string.ok), true);
            } else {
                startActivity(new Intent(CreateTransfersActivity.this, TransferOverviewDetailsActivity.class)
                        .putExtra("project_id", projectId).putExtra("selected_tab", getIntent().getIntExtra("selected_tab", 0)));
                CreateTransfersActivity.this.finish();
            }
        }
    }

    public boolean isUpdateCreateTransferData() {
        if (createTransfer.getPickupVendorStatus() != transferDetails.getPickupIsVendor()) {
            return false;
        }
        if (createTransfer.getDropoffVendorStatus() != transferDetails.getDropoffIsVendor()) {
            return false;
        }

        if (!createTransfer.getPickupTime().equals(transferDetails.getPickupTime())) {
            return false;
        } else if (!createTransfer.getPickupDate().equals(transferDetails.getPickupDate())) {
            return false;
        } else if (!createTransfer.getPickupName().equals(transferDetails.getPickupContact())) {
            return false;
        } else if (!createTransfer.getPickupPhone().equals(addMasking(transferDetails.getPickupContactNumber() != null ? transferDetails.getPickupContactNumber().replace("-", "") : ""))) {
            return false;
        } else if (!createTransfer.getDeliveryDate().equals(transferDetails.getDropoffDate())) {
            return false;
        } else if (!createTransfer.getDropoffTime().equals(transferDetails.getDropoffTime())) {
            return false;
        } else if (!createTransfer.getDropOffName().equals(transferDetails.getDropoffContact())) {
            return false;
        } else if (!createTransfer.getDropOffPhone().equals(addMasking(transferDetails.getDropoffContactNumber() != null ? transferDetails.getDropoffContactNumber().replace("-", "") : ""))) {
            return false;
        } else if (createTransfer.getRoundTrip() != transferDetails.getRoundTrip()) {
            return false;
        } else if (createTransfer.getUnloading() != transferDetails.getUnloadingMethod()) {
            return false;
        }
        return !updateTransferData;
    }


    @OnClick(R.id.leftImageView)
    public void onBackClick() {
        hideKeyboard(this);
        onBackPressed();
    }


    public TransferRequest getCreateTransfer() {
        return createTransfer;
    }

    public void setCreateTransfer(TransferRequest createTransfer) {
        if (createTransfer != null) {
            this.createTransfer = createTransfer;
        }
    }

    public int getProjectId() {
        return projectId;
    }

    public void openTransferDropOff() {
        hideKeyboard(this);
        allDone = false;
        Log.i("dropoff", "openTransferDropOff: dropoff " + allDone);
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.listContainer);
        if (createTransfer.getStatus() != 5 && f instanceof CreateTransferPickupFragment &&
                createTransferPickupFragment != null && !createTransferPickupFragment.checkEmptyValidation()) {
            return;
        } else if (!(f instanceof CreateTransferDropoffFragment)) {
//            nestedScrollView.scrollTo(0, 0);

            no1TextView.setClickable(true);
            pickupTextView.setClickable(true);
            no2TextView.setClickable(true);
            dropoffTextView.setClickable(true);
            pickDropView.setBackgroundColor(colorPrimary);
            dropoffTextView.setTextColor(colorGray);
            addImageView.setVisibility(View.INVISIBLE);
            no1TextView.setBackground(completedDrawable);
            no1TextView.setTextColor(colorPrimary);
            no2TextView.setBackground(selectedDrawable);
            no2TextView.setTextColor(coloeWhite);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (createTransferDropoffFragment == null) {
                createTransferDropoffFragment = new CreateTransferDropoffFragment();
            }
            fragmentTransaction.replace(R.id.listContainer, createTransferDropoffFragment, createTransferDropoffFragment.getClass().getSimpleName()).addToBackStack(CreateTransferPickupFragment.class.getName());
            try {
                fragmentTransaction.commit();
            } catch (IllegalStateException e) {

            }
        }
    }


    public void openEquipment() {
        hideKeyboard(this);
        allDone = false;
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.listContainer);

        if (createTransfer.getStatus() != 5 && f instanceof CreateTransferPickupFragment && createTransferPickupFragment != null && !createTransferPickupFragment.checkEmptyValidation()) {
            return;
        } else if (createTransfer.getStatus() != 5 && f instanceof CreateTransferDropoffFragment && createTransferDropoffFragment != null && !createTransferDropoffFragment.checkEmptyValidation()) {
            return;
        } else if (!(f instanceof CreateTransferEquipmentFragment)) {
            no3TextView.setClickable(true);
            equipmentTextView.setClickable(true);
            hideKeyboard(this);
            equipmentTextView.setTextColor(colorGray);
            if (createTransfer.getStatus() >= 1 && (createTransfer.getStatus() != 2 || (createTransfer.getDropoffVendorStatus() != 1 && createTransfer.getPickupLocation() != getIntent().getIntExtra("project_id", 0)))) {
                addImageView.setVisibility(View.INVISIBLE);
            } else {
                addImageView.setVisibility(View.VISIBLE);
            }
            dropEqView.setBackgroundColor(colorPrimary);

            //nestedScrollView.scrollTo(0, 0);
            no2TextView.setBackground(completedDrawable);
            no2TextView.setTextColor(colorPrimary);
            no3TextView.setBackground(selectedDrawable);
            no3TextView.setTextColor(coloeWhite);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            createTransferEquipmentFragment = new CreateTransferEquipmentFragment();
            fragmentTransaction.replace(R.id.listContainer, createTransferEquipmentFragment, createTransferEquipmentFragment.getClass().getSimpleName()).addToBackStack(CreateTransferEquipmentFragment.class.getName());
            try {
                fragmentTransaction.commit();
            } catch (IllegalStateException e) {
            }
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

    public void addEquipment() {
        if (!NetworkService.isNetworkAvailable(this)) {
            showMessageAlert(this, getString(R.string.internet_connection_check_transfer_overview), getString(R.string.ok), false);
            return;
        }
        if (checkDateTimeValidation()) {
            showMessageAlert(this, "Drop-off date should be greater than pick-up date.", getString(R.string.ok), false);
            return;
        }
        if (checkSameLocationValidation()) {
            showMessageAlert(this, "The pick-up location and drop-off location must be different.", getString(R.string.ok), false);
            return;
        }
        if (checkDropOffValidations()) {
            showMessageAlert(this, "The drop-off info is required.", getString(R.string.ok), false);
            return;
        }
        if (checkLocationValidation()) {
            showMessageAlert(this, getString(R.string.locaion_validation_message), getString(R.string.ok), false);
            return;
        }
        ((PronovosApplication) getApplication()).setCreateTransferInBaseActivity(createTransfer);
        boolean showTrackId = false;
        if (!transferOption.equals("New Request") || (transferDetails != null && transferDetails.getStatus() > 1)) {
            showTrackId = true;
        }
        startActivityForResult(new Intent(this, CreateTransferEquipmentActivity.class)
                .putExtra("project_id", projectId)
                .putExtra("show_track_id", showTrackId)
//                .putExtra("transfer_request", (Parcelable) createTransfer)
                .putExtra("transfer_option1", titleTextView.getText().toString()), GET_EQUIPMENT);

    }

    private boolean checkDropOffValidations() {
        if (TextUtils.isEmpty(createTransfer.getDeliveryDate()) || TextUtils.isEmpty(createTransfer.getDropoffTime())
                || TextUtils.isEmpty(createTransfer.getDropOffName()) || TextUtils.isEmpty(createTransfer.getDropOffPhone())
                || createTransfer.getDropOffLocation() == 0) {
            return true;

        } else {
            return false;
        }
    }

    private boolean checkSameLocationValidation() {
        if ((createTransfer.getPickupVendorStatus() == 0 && createTransfer.getPickupLocation() == projectId) && (createTransfer.getDropoffVendorStatus() == 0 && createTransfer.getDropOffLocation() == projectId)) {
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

    public void addDisputeMessage() {
        startActivityForResult(new Intent(this, CreateTransferDisputeMessage.class)
                .putExtra("project_id", projectId)
                .putExtra("transfer_option", titleTextView.getText()), GET_DISPUTE_MESSAGE);

    }

    public void select1() {
        no1TextView.setBackground(selectedDrawable);
        no1TextView.setTextColor(coloeWhite);

        if (transferDetails != null) {
            no2TextView.setBackground(completedDrawable);
            no2TextView.setTextColor(colorPrimary);
            if (createTransfer.getStatus() == 5 || createTransfer.getEquipment().size() > 0) {
                no4TextView.setBackground(completedDrawable);
                no4TextView.setTextColor(colorPrimary);
                no3TextView.setBackground(completedDrawable);
                no3TextView.setTextColor(colorPrimary);
            } else {
                no3TextView.setBackground(inCompletedDrawable);
                no3TextView.setTextColor(colorGray);
                no4TextView.setBackground(inCompletedDrawable);
                no4TextView.setTextColor(colorGray);
            }
         /*
            no4TextView.setTextColor(colorGray);
*/
        } else {
            if (canMoveAheadDropOff()) {
                no2TextView.setBackground(completedDrawable);
                no2TextView.setTextColor(colorPrimary);
            } else if (canMoveAheadPickup()) {
                no2TextView.setBackground(inCompletedDrawable);
                no2TextView.setTextColor(colorGray);
            } else {
                no2TextView.setBackground(disableDrawable);
                no2TextView.setTextColor(colorDisableGray);
                dropoffTextView.setTextColor(colorDisableGray);
            }
            if (createTransfer.getEquipment() != null && createTransfer.getEquipment().size() > 0 && canAheadEquipment) {
                no3TextView.setBackground(completedDrawable);
                no3TextView.setTextColor(colorPrimary);
            } else if (canMoveAheadDropOff()) {
                no3TextView.setBackground(inCompletedDrawable);
                no3TextView.setTextColor(colorGray);
            } else {
                no3TextView.setBackground(disableDrawable);
                no3TextView.setTextColor(colorDisableGray);
                equipmentTextView.setTextColor(colorDisableGray);
            }
            if ((canAheadEquipment && createTransfer.getEquipment() != null && createTransfer.getEquipment().size() > 0)) {
                no4TextView.setBackground(completedDrawable);
                no4TextView.setTextColor(colorPrimary);
            } else {
                no4TextView.setBackground(disableDrawable);
                no4TextView.setTextColor(colorDisableGray);
                infoTextView.setTextColor(colorDisableGray);
            }
        }

    }

    public void selectDropOff() {
        no1TextView.setBackground(completedDrawable);
        no1TextView.setTextColor(colorPrimary);
        no2TextView.setBackground(selectedDrawable);
        no2TextView.setTextColor(coloeWhite);
        if (transferDetails != null) {
            if (createTransfer.getStatus() == 5 || createTransfer.getEquipment().size() > 0) {
                no4TextView.setBackground(completedDrawable);
                no4TextView.setTextColor(colorPrimary);
                no3TextView.setBackground(completedDrawable);
                no3TextView.setTextColor(colorPrimary);
            } else {
                no4TextView.setBackground(inCompletedDrawable);
                no4TextView.setTextColor(colorGray);
                no3TextView.setBackground(inCompletedDrawable);
                no3TextView.setTextColor(colorGray);
            }
          /*
            no4TextView.setBackground(inCompletedDrawable);
            no4TextView.setTextColor(colorGray);
*/
        } else {

        /*    no3TextView.setBackground(disableDrawable);
            no3TextView.setTextColor(colorDisableGray);
            no4TextView.setBackground(disableDrawable);
            no4TextView.setTextColor(colorDisableGray);
*/
            if (canMoveAheadDropOff()) {
                no3TextView.setBackground(inCompletedDrawable);
                no3TextView.setTextColor(colorGray);
            } else {
                no3TextView.setBackground(disableDrawable);
                no3TextView.setTextColor(colorDisableGray);
                equipmentTextView.setTextColor(colorDisableGray);
            }
            if (canAheadEquipment && createTransfer.getEquipment() != null && createTransfer.getEquipment().size() > 0) {
                no4TextView.setBackground(completedDrawable);
                no4TextView.setTextColor(colorPrimary);

                no3TextView.setBackground(completedDrawable);
                no3TextView.setTextColor(colorPrimary);
            } else {
                no4TextView.setBackground(disableDrawable);
                no4TextView.setTextColor(colorDisableGray);
                infoTextView.setTextColor(colorDisableGray);
            }
        }
    }

    public void selectEquipment() {

        no1TextView.setBackground(completedDrawable);
        no1TextView.setTextColor(colorPrimary);
        no2TextView.setBackground(completedDrawable);
        no2TextView.setTextColor(colorPrimary);
        no3TextView.setBackground(selectedDrawable);
        no3TextView.setTextColor(coloeWhite);
//           if (!transferOption.equals("New Transfer") || (canMoveAheadPickup() && canMoveAheadDropOff() && createTransfer.getEquipment() != null && canAheadEquipment && createTransfer.getEquipment() != null && createTransfer.getEquipment().size() > 0)) {

        if (transferDetails != null) {
            if (createTransfer.getStatus() == 5 || createTransfer.getEquipment().size() > 0) {
                no4TextView.setBackground(completedDrawable);
                no4TextView.setTextColor(colorPrimary);
            } else {
                no4TextView.setBackground(inCompletedDrawable);
                no4TextView.setTextColor(colorGray);
            }
        } else if (canAheadEquipment && createTransfer.getEquipment() != null && createTransfer.getEquipment().size() > 0) {
            no4TextView.setBackground(completedDrawable);
            no4TextView.setTextColor(colorPrimary);
        } else {
            no4TextView.setBackground(disableDrawable);
            no4TextView.setTextColor(colorDisableGray);
            infoTextView.setTextColor(colorDisableGray);
        }
    }

    public void selectInfo() {
        no1TextView.setBackground(completedDrawable);
        no1TextView.setTextColor(colorPrimary);
        no2TextView.setBackground(completedDrawable);
        no2TextView.setTextColor(colorPrimary);
        no3TextView.setBackground(completedDrawable);
        no3TextView.setTextColor(colorPrimary);
        no4TextView.setBackground(selectedDrawable);
        no4TextView.setTextColor(coloeWhite);
    }

    public void deleteEquipment(int adapterPosition) {
        if (createTransfer.getEquipment() != null && createTransfer.getEquipment().size() > adapterPosition && createTransferEquipmentFragment != null) {
            Equipment equipment = createTransfer.getEquipment().get(adapterPosition);

            if (equipment.getTransferEquipmentId() != 0 && createTransfer.getTransferId() != 0) {
                DeleteEquipmentRequest deleteEquipmentRequest = new DeleteEquipmentRequest();
                deleteEquipmentRequest.setTransferEquipmentId(equipment.getTransferEquipmentId());
                deleteEquipmentRequest.setTransferId(createTransfer.getTransferId());
                callDeleteEquipment(deleteEquipmentRequest, adapterPosition);
            } else {
                createTransfer.getEquipment().remove(adapterPosition);
                createTransferEquipmentFragment.updateData();
            }


            if (transferDetails == null && (createTransfer.getEquipment() == null || createTransfer.getEquipment().size() == 0)) {
                no4TextView.setBackground(disableDrawable);
                no4TextView.setTextColor(colorDisableGray);
                infoTextView.setTextColor(colorDisableGray);
                eqInfoView.setBackgroundColor(colorDisableGray);
            }
        }
    }

    private void callDeleteEquipment(DeleteEquipmentRequest deleteEquipmentRequest, int adapterPosition) {
        CustomProgressBar.showDialog(this);
        transferOverviewProvider.callDeleteEquipment(deleteEquipmentRequest, new ProviderResult<DeleteEquipmentResponse>() {
            @Override
            public void success(DeleteEquipmentResponse transferRequestResponse) {
                CustomProgressBar.dissMissDialog(CreateTransfersActivity.this);
                createTransfer.getEquipment().remove(adapterPosition);
                createTransferEquipmentFragment.updateData();
            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(CreateTransfersActivity.this);
                startActivity(new Intent(CreateTransfersActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(CreateTransfersActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(CreateTransfersActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                showMessageAlert(CreateTransfersActivity.this, message, getString(R.string.ok), false);
                CustomProgressBar.dissMissDialog(CreateTransfersActivity.this);

            }
        }, loginResponse);
    }

    public void addEquipment(int adapterPosition) {
        ((PronovosApplication) getApplication()).setCreateTransferInBaseActivity(createTransfer);

        boolean showTrackId = false;
        if (!transferOption.equals("New Request") || (transferDetails != null && transferDetails.getStatus() > 1)) {
            showTrackId = true;
        }

        if (createTransfer.getEquipment() != null && createTransfer.getEquipment().size() > adapterPosition && createTransferEquipmentFragment != null) {
            Equipment eq = createTransfer.getEquipment().get(adapterPosition);
            startActivityForResult(new Intent(this, CreateTransferEquipmentActivity.class)
                    .putExtra("project_id", projectId)
                    .putExtra("show_track_id", showTrackId)
                    .putExtra("transfer_id", createTransfer.getTransferId())
//                    .putExtra("transfer_request", (Parcelable) createTransfer)
                    .putExtra("equipment", (Parcelable) eq)
                    .putExtra("position", adapterPosition)
                    .putExtra("transfer_option1", titleTextView.getText().toString()), GET_EQUIPMENT);

        }
    }

    public Details getTransferDetails() {
        return transferDetails;
    }

    public void setTransferDetails(Details transferDetails) {
        this.transferDetails = transferDetails;
    }

    @OnClick(R.id.no1TextView)
    public void openPickupNo1() {
        openPickupView();
    }

    @OnClick(R.id.pickupTextView)
    public void openPickup() {
        openPickupView();
    }

    @OnClick(R.id.no2TextView)
    public void openDropOff2() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.listContainer);
        if (!transferOption.equals("New Transfer") || canMoveAheadPickup()) {
            openTransferDropOff();
        }
    }

    @OnClick(R.id.dropoffTextView)
    public void openDropoff() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.listContainer);
        if (!transferOption.equals("New Transfer") || canMoveAheadPickup()) {
            openTransferDropOff();
        }
    }

    @OnClick(R.id.no3TextView)
    public void openEquipment3() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.listContainer);
        if (!transferOption.equals("New Transfer") || (canMoveAheadPickup() && canMoveAheadDropOff())) {
            openEquipment();
        }
    }

    @OnClick(R.id.equipmentTextView)
    public void openEquipmentTV() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.listContainer);
        if (!transferOption.equals("New Transfer") || (canMoveAheadPickup() && canMoveAheadDropOff())) {
            openEquipment();
        }
    }

    private boolean canMoveAheadDropOff() {
       /* if (TextUtils.isEmpty(createTransfer.getDeliveryDate()) || TextUtils.isEmpty(createTransfer.getDropoffTime()) || TextUtils.isEmpty(createTransfer.getDropOffName()) || TextUtils.isEmpty(createTransfer.getDropOffPhone()) || createTransfer.getDropOffLocation() == 0) {
            return false;
        } else {*/
        return isDropOffDone;
//        }
    }

    private boolean canMoveAheadPickup() {
       /* if (TextUtils.isEmpty(createTransfer.getPickupDate()) || TextUtils.isEmpty(createTransfer.getPickupTime()) || TextUtils.isEmpty(createTransfer.getPickupName()) || TextUtils.isEmpty(createTransfer.getPickupPhone()) || createTransfer.getPickupLocation() == 0) {
            return false;
        } else {*/

        return isPickUpDone;
//        }
    }

    @OnClick(R.id.no4TextView)
    public void openInfo4() {
        if (!transferOption.equals("New Transfer") || (canMoveAheadPickup() && canMoveAheadDropOff() && createTransfer.getEquipment() != null && canAheadEquipment && createTransfer.getEquipment() != null && createTransfer.getEquipment().size() > 0)) {
            openInfo();
        }
    }

    @OnClick(R.id.infoTextView)
    public void openInfoTV() {
        if (!transferOption.equals("New Transfer") || (canMoveAheadPickup() && canMoveAheadDropOff() && createTransfer.getEquipment() != null && (canAheadEquipment && createTransfer.getEquipment() != null && createTransfer.getEquipment().size() > 0))) {
            openInfo();
        }
    }


    public void callCreateTransferAPI(CreateTransferRequest mCreateTransfer) {
        CustomProgressBar.showDialog(this);
        mCreateTransfer.setProjectId(String.valueOf(projectId));
        transferOverviewProvider.callCreateTransfer(mCreateTransfer, new ProviderResult<CreateTransferResponse>() {
            @Override
            public void success(CreateTransferResponse transferRequestResponse) {
                CustomProgressBar.dissMissDialog(CreateTransfersActivity.this);

                startActivity(new Intent(CreateTransfersActivity.this, TransferOverviewDetailsActivity.class).putExtra("project_id", projectId).putExtra("selected_tab", mCreateTransfer.getStatus() != 5 ? mCreateTransfer.getStatus() : getIntent().getIntExtra("selected_tab", 0)));
                CreateTransfersActivity.this.finish();

            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(CreateTransfersActivity.this);
                startActivity(new Intent(CreateTransfersActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(CreateTransfersActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(CreateTransfersActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                showMessageAlert(CreateTransfersActivity.this, message, getString(R.string.ok), false);
                CustomProgressBar.dissMissDialog(CreateTransfersActivity.this);

            }
        }, loginResponse);
    }

    public void updateTotalWeight(String valueOf) {
        createTransfer.setTotalWeight(valueOf);
    }

    @OnClick(R.id.addImageView)
    public void addEq() {

        addEquipment();

    }

    public TransferLocationResponse getTransferOverviewResponse() {
        return transferOverviewResponse;
    }

    public void setTransferOverviewResponse(TransferLocationResponse transferOverviewResponse) {
        this.transferOverviewResponse = transferOverviewResponse;
    }

    public TransferLocationVendorResponse getTransferOverviewVendorResponse() {
        return transferOverviewVendorResponse;
    }

    public void setTransferOverviewVendorResponse(TransferLocationVendorResponse transferOverviewVendorResponse) {
        this.transferOverviewVendorResponse = transferOverviewVendorResponse;
    }
/*

    public TransferContactsResponse getJobSiteTransferContactsResponse() {
        return jobSiteTransferContactsResponse;
    }
*/

    public void setJobSiteTransferContactsResponse(TransferContactsResponse jobSiteTransferContactsResponse) {
        this.jobSiteTransferContactsResponse = jobSiteTransferContactsResponse;
    }

    public void canAheadEquipment(boolean b) {
        canAheadEquipment = b;
    }

    public void setPickUpDone() {
        isPickUpDone = true;
    }

    public void setDropOffDone() {
        isDropOffDone = true;
    }


    public String addMasking(String str) {
        int i = str.length();
        if (i > 4) {
            String ss = str;
            String first = ss.substring(0, 3);
            String last = ss.substring(3);
            str = first + "-" + last;
        }
        if (i > 7) {
            String ss = str;
            String first = ss.substring(0, 7);
            String last = ss.substring(7);
            str = first + "-" + last;
        }
        return str;
    }
}
