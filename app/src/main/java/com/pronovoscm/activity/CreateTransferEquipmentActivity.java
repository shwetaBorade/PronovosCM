package com.pronovoscm.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.data.InventoryProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.data.TransferOverviewProvider;
import com.pronovoscm.fragments.CreateTransferAddEquipmentFragment;
import com.pronovoscm.fragments.LookUpEquipmentFragment;
import com.pronovoscm.model.request.deleteequipment.DeleteEquipmentRequest;
import com.pronovoscm.model.request.inventory.InventoryRequest;
import com.pronovoscm.model.request.transferrequest.Equipment;
import com.pronovoscm.model.request.transferrequest.TransferRequest;
import com.pronovoscm.model.response.deleteequipment.DeleteEquipmentResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.EquipmentCategoriesMaster;
import com.pronovoscm.persistence.domain.EquipmentInventory;
import com.pronovoscm.persistence.domain.EquipmentRegion;
import com.pronovoscm.persistence.domain.EquipmentSubCategoriesMaster;
import com.pronovoscm.persistence.repository.EquipementInventoryRepository;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.ui.CustomProgressBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateTransferEquipmentActivity extends BaseActivity {
    @Inject
    InventoryProvider mInventoryProvider;
    @Inject
    EquipementInventoryRepository mEquipementInventoryRepository;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @Inject
    TransferOverviewProvider transferOverviewProvider;

    @BindView(R.id.deleteImageView)
    ImageView deleteImageView;
    private int projectId;
    private int position;
    private LoginResponse loginResponse;
    private CreateTransferAddEquipmentFragment createTransferAddEquipmentFragment;
    private LookUpEquipmentFragment lookUpEquipmentFragment;
    private EquipmentRegion equipmentRegion = null;
    private String transferOption;
    private Equipment equipment;
    private TransferRequest transferRequest;
    private AlertDialog alertDialog;
    private int transferId;

    @Override
    protected int doGetContentView() {
        return R.layout.create_transfer_equipment_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        projectId = getIntent().getIntExtra("project_id", 0);
        position = getIntent().getIntExtra("position", -1);
        transferId = getIntent().getIntExtra("transfer_id", 0);
        transferOption = getIntent().getStringExtra("transfer_option1");
        equipment = (Equipment) getIntent().getParcelableExtra("equipment");
        transferRequest = ((PronovosApplication) getApplication()).getCreateTransferInBaseActivity();
if (equipment!=null){
    deleteImageView.setVisibility(View.VISIBLE);
}
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.INVISIBLE);
        titleTextView.setText(transferOption);

        loadAddEquipment();
        callCategoriesAPI();

    }

    public EquipmentRegion getEquipmentRegion() {
        return equipmentRegion;
    }

    public void setEquipmentRegion(EquipmentRegion equipmentRegion) {
        this.equipmentRegion = equipmentRegion;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
        } else {
            offlineTextView.setVisibility(View.GONE);
        }
    }

    public void loadAddEquipment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        createTransferAddEquipmentFragment = new CreateTransferAddEquipmentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("projectId", projectId);
        createTransferAddEquipmentFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.equipmentContainer, createTransferAddEquipmentFragment, createTransferAddEquipmentFragment.getClass().getSimpleName());
        try {
            fragmentTransaction.commit();
        } catch (IllegalStateException e) {

        }
    }

    public void callCategoriesAPI() {
        CustomProgressBar.showDialog(this);
        mInventoryProvider.getCategories(projectId, new ProviderResult<List<EquipmentCategoriesMaster>>() {
            @Override
            public void success(List<EquipmentCategoriesMaster> result) {
                mInventoryProvider.getSubCategories(new ProviderResult<List<EquipmentSubCategoriesMaster>>() {
                    @Override
                    public void success(List<EquipmentSubCategoriesMaster> result) {
                        mInventoryProvider.getEquipmentDetails(new ProviderResult<List<EquipmentRegion>>() {
                            @Override
                            public void success(List<EquipmentRegion> result) {
                                CustomProgressBar.dissMissDialog(CreateTransferEquipmentActivity.this);
                                InventoryRequest inventoryRequest = new InventoryRequest();
                                inventoryRequest.setProjectId(projectId);
                                mInventoryProvider.getInventory(inventoryRequest, new ProviderResult<List<EquipmentInventory>>() {
                                    @Override
                                    public void success(List<EquipmentInventory> result) {
                                        if (createTransferAddEquipmentFragment != null) {
                                            createTransferAddEquipmentFragment.refreshData();
                                        }
                                    }

                                    @Override
                                    public void AccessTokenFailure(String message) {
                                        CustomProgressBar.dissMissDialog(CreateTransferEquipmentActivity.this);
                                    }

                                    @Override
                                    public void failure(String message) {
                                        CustomProgressBar.dissMissDialog(CreateTransferEquipmentActivity.this);

                                    }
                                }, loginResponse);
                            }

                            @Override
                            public void AccessTokenFailure(String message) {
                                CustomProgressBar.dissMissDialog(CreateTransferEquipmentActivity.this);
                            }

                            @Override
                            public void failure(String message) {
                                CustomProgressBar.dissMissDialog(CreateTransferEquipmentActivity.this);

                            }
                        }, loginResponse);
                    }

                    @Override
                    public void AccessTokenFailure(String message) {
                        CustomProgressBar.dissMissDialog(CreateTransferEquipmentActivity.this);

                    }

                    @Override
                    public void failure(String message) {
                        CustomProgressBar.dissMissDialog(CreateTransferEquipmentActivity.this);

                    }
                }, loginResponse);
            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(CreateTransferEquipmentActivity.this);
            }

            @Override
            public void failure(String message) {

                CustomProgressBar.dissMissDialog(CreateTransferEquipmentActivity.this);
            }
        }, loginResponse);
    }

    public void openCategories() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        lookUpEquipmentFragment = new LookUpEquipmentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("projectId", projectId);
        createTransferAddEquipmentFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.equipmentContainer, lookUpEquipmentFragment, lookUpEquipmentFragment.getClass().getSimpleName()).addToBackStack(LookUpEquipmentFragment.class.getName());
        try {
            fragmentTransaction.commit();
        } catch (IllegalStateException e) {

        }

    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }


    public void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @OnClick(R.id.leftImageView)
    public void cancelViewClick() {
        hideKeyboard(this);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        hideKeyboard(this);
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.equipmentContainer);
        if (f instanceof CreateTransferAddEquipmentFragment && createTransferAddEquipmentFragment != null && !createTransferAddEquipmentFragment.checkBackValidate()) {
            try {
                if (alertDialog == null || !alertDialog.isShowing()) {
                    alertDialog = new AlertDialog.Builder(this).create();
                }
                alertDialog.setMessage(getString(R.string.are_you_sure_you_want_to_exit_without_saving));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
                    alertDialog.dismiss();
                    CreateTransferEquipmentActivity.this.finish();
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> {
                    alertDialog.dismiss();
                });
                if (alertDialog != null && !alertDialog.isShowing()) {

                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }


            } catch (Exception e) {
            }
        } else {
            super.onBackPressed();
        }
    }

    public void saveEquipment(Equipment equipment) {
        Intent intent = new Intent();
        intent.putExtra("equipment_new", (Parcelable) equipment);
        intent.putExtra("position", position);
        setResult(RESULT_OK, intent);
        CreateTransferEquipmentActivity.this.finish();
    }

    public TransferRequest getTransferRequest() {
        return transferRequest;
    }

    public void setTransferRequest(TransferRequest transferRequest) {
        this.transferRequest = transferRequest;
    }
    @OnClick(R.id.deleteImageView)
    public void deleteEquipment(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage(getString(R.string.are_you_sure_you_want_to_delete));
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> {
            dialog.dismiss();
        });
        alertDialog.setCancelable(false);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
            alertDialog.dismiss();
            deleteEquipmentDetails();
        });
        alertDialog.show();
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(ContextCompat.getColor(this, R.color.gray_948d8d));
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

    }

    private void deleteEquipmentDetails() {
        if (equipment.getTransferEquipmentId() != 0 && transferId != 0) {
            DeleteEquipmentRequest deleteEquipmentRequest = new DeleteEquipmentRequest();
            deleteEquipmentRequest.setTransferEquipmentId(equipment.getTransferEquipmentId());
            deleteEquipmentRequest.setTransferId(transferId);
            callDeleteEquipment(deleteEquipmentRequest);
        } else {
            Intent intent = new Intent();
            intent.putExtra("delete_equipment", true);
            intent.putExtra("remove_position", position);
            setResult(RESULT_OK, intent);
            CreateTransferEquipmentActivity.this.finish();
        /*
            createTransfer.getEquipment().remove(adapterPosition);
            createTransferEquipmentFragment.updateData();*/
        }


    }

    private void callDeleteEquipment(DeleteEquipmentRequest deleteEquipmentRequest) {
        CustomProgressBar.showDialog(this);
        transferOverviewProvider.callDeleteEquipment(deleteEquipmentRequest, new ProviderResult<DeleteEquipmentResponse>() {
            @Override
            public void success(DeleteEquipmentResponse transferRequestResponse) {
                CustomProgressBar.dissMissDialog(CreateTransferEquipmentActivity.this);
                Intent intent = new Intent();
                intent.putExtra("delete_equipment", true);
                intent.putExtra("remove_position", position);
                setResult(RESULT_OK, intent);
                CreateTransferEquipmentActivity.this.finish();
            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(CreateTransferEquipmentActivity.this);
                startActivity(new Intent(CreateTransferEquipmentActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(CreateTransferEquipmentActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(CreateTransferEquipmentActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                showMessageAlert(CreateTransferEquipmentActivity.this, message, getString(R.string.ok));
                CustomProgressBar.dissMissDialog(CreateTransferEquipmentActivity.this);

            }
        }, loginResponse);
    }

}
