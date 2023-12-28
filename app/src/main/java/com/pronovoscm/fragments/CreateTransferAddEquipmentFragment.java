package com.pronovoscm.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.activity.CreateTransferEquipmentActivity;
import com.pronovoscm.adapter.AutocompleteSelectEquipmentAdapter;
import com.pronovoscm.adapter.CompanyAdapter;
import com.pronovoscm.adapter.TradeAdapter;
import com.pronovoscm.model.EquipmentStatusEnum;
import com.pronovoscm.model.request.transferrequest.Equipment;
import com.pronovoscm.model.request.transferrequest.TransferRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.EquipmentInventory;
import com.pronovoscm.persistence.domain.EquipmentRegion;
import com.pronovoscm.persistence.repository.EquipementInventoryRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.SharedPref;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class CreateTransferAddEquipmentFragment extends Fragment {
    @BindView(R.id.enterDescriptionViewET)
    AutoCompleteTextView enterDescriptionViewET;
    @BindView(R.id.inputSwitch)
    Switch inputSwitch;
    @BindView(R.id.unitsNumberET)
    EditText unitsNumberET;
    @BindView(R.id.inputTextView)
    TextView inputTextView;
    @BindView(R.id.quantityNumberET)
    EditText quantityNumberET;
    @BindView(R.id.enterWeightViewET)
    EditText enterWeightViewET;
    @BindView(R.id.statusSpinner)
    AppCompatSpinner statusSpinner;
    @BindView(R.id.rentedCheckBox)
    CheckBox rentedCheckBox;
    @BindView(R.id.nameErrorTextView)
    TextView nameErrorTextView;
    @BindView(R.id.weightErrorTextView)
    TextView weightErrorTextView;
    @BindView(R.id.quantityErrorTextView)
    TextView quantityErrorTextView;
    @BindView(R.id.unitsView)
    RelativeLayout unitsView;
    @BindView(R.id.unTextView)
    TextView unTextView;
    @BindView(R.id.quantityView)
    RelativeLayout quantityView;
    @BindView(R.id.statusSpinnewView)
    RelativeLayout statusSpinnewView;
    @BindView(R.id.enterWeightView)
    ConstraintLayout enterWeightView;
    @BindView(R.id.enterDescriptionView)
    ConstraintLayout enterDescriptionView;
    @BindView(R.id.enterTrackingView)
    ConstraintLayout enterTrackingView;
    @BindView(R.id.trackingIDConditionTextView)
    TextView trackingIDConditionTextView;
    @BindView(R.id.lookUpEquipmentTextView)
    TextView lookUpEquipmentTextView;
    @BindView(R.id.trackingIdTextView)
    TextView trackingIdTextView;
    @BindView(R.id.trackingErrorTextView)
    TextView trackingErrorTextView;
    @BindView(R.id.addTextView)
    TextView addTextView;
    @BindView(R.id.trackingIdSpinner)
    AppCompatSpinner trackingIdSpinner;
    @Inject
    EquipementInventoryRepository mEquipementInventoryRepository;
    boolean showTrackId = false;
    String trackingNo = "";
    //    private Drawable enableDrawable;
//    private Drawable disableDrawable;
    private KeyListener listener;
    private AutocompleteSelectEquipmentAdapter autocompleteSelectEquipmentAdapter;
    private TransferRequest createTransfer;
    private AlertDialog alertDialog;
    private int projectId;
    private List<EquipmentRegion> equipmentCategoriesDetails = new ArrayList<>();
    private EquipmentRegion equipmentCategoriesDetail;
    private LoginResponse loginResponse;
    private ArrayList<String> statusArrayList;
    private CompanyAdapter companyAdapter;
    private List<EquipmentInventory> inventoryList;
    private TradeAdapter tradeAdapter;
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            trackingNo = "";
            ((CreateTransferEquipmentActivity) getActivity()).hideKeyboard(getActivity());
            equipmentCategoriesDetail = (EquipmentRegion) adapterView.getItemAtPosition(i);
            enterDescriptionViewET.setText(equipmentCategoriesDetail.getName());
            if (equipmentCategoriesDetail != null && !TextUtils.isEmpty(equipmentCategoriesDetail.getWeight())) {

                BigDecimal weight1 = BigDecimal.valueOf(Double.parseDouble(equipmentCategoriesDetail.getWeight().startsWith(".") ? "0" + equipmentCategoriesDetail.getWeight() : equipmentCategoriesDetail.getWeight()));
                Float w1 = weight1.floatValue();
                String s2 = String.format("%.2f", w1);
                enterWeightViewET.setText(s2);

            }
            if (!TextUtils.isEmpty(equipmentCategoriesDetail.getItemsPerUnit())) {
                enableInputMethodSwitch();
            }
            if (!TextUtils.isEmpty(equipmentCategoriesDetail.getWeight())) {
                enterWeightView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rounded_gray_border));
                if (!TextUtils.isEmpty(enterWeightViewET.getText().toString())) {
                    enterWeightViewET.setFocusableInTouchMode(false);
                    enterWeightView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                }
            }
            enableStatus();
            checkUniqueCase(false);
        }
    };

    private void checkUniqueCase(boolean close) {
        if (equipmentCategoriesDetail != null && equipmentCategoriesDetail.getType().equals("Unique")) {
            TransferRequest transferRequest = ((CreateTransferEquipmentActivity) getActivity()).getTransferRequest();
            quantityNumberET.setText("1");
            quantityNumberET.setFocusableInTouchMode(false);
            unitsNumberET.setText("");
            inputSwitch.setEnabled(false);
            inputSwitch.setClickable(false);
            inputSwitch.setVisibility(View.INVISIBLE);
            inputTextView.setVisibility(View.INVISIBLE);
            unitsView.setVisibility(View.INVISIBLE);
            unTextView.setVisibility(View.INVISIBLE);
            quantityView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
            disableStatus();
           /* enterWeightViewET.setText("");
            enterWeightViewET.setFocusableInTouchMode(true);
            enterWeightView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rounded_gray_border));
*/
            if (TextUtils.isEmpty(enterWeightViewET.getText())) {
                enterWeightViewET.setText("");
                enterWeightViewET.setFocusableInTouchMode(true);
                enterWeightView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rounded_gray_border));
            } else {
                BigDecimal weight1 = BigDecimal.valueOf(Double.parseDouble(enterWeightViewET.getText().toString().startsWith(".") ? "0" + enterWeightViewET.getText().toString() : enterWeightViewET.getText().toString()));
                Float w1 = weight1.floatValue();
                String s2 = String.format("%.2f", w1);
                enterWeightViewET.setText(String.valueOf(s2));
                enterWeightViewET.setFocusableInTouchMode(false);
                enterWeightView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
            }
            String message = "This equipment is not currently in the pick-up location's inventory.";
            if (transferRequest.getPickupVendorStatus() == 1) {
                equipmentCategoriesDetail = null;
                enableQuantity();
                enableStatus();
                enterDescriptionViewET.setText("");
                showMessageAlert(getActivity(), message, getString(R.string.ok), close);
            } else {
                List<EquipmentInventory> equipmentCategories = mEquipementInventoryRepository.getEquipmentInventory(equipmentCategoriesDetail.getEqRegionEquipentId(), transferRequest.getPickupLocation());
                if (equipmentCategories == null || equipmentCategories.size() == 0) {
                    equipmentCategoriesDetail = null;
                    enterDescriptionViewET.setText("");
                    enableQuantity();
                    enableStatus();
                    enterDescriptionViewET.setText("");
                    showMessageAlert(getActivity(), message, getString(R.string.ok), close);
                } else if (showTrackId) {
                    if (TextUtils.isEmpty(enterWeightViewET.getText())) {
                        enterWeightViewET.setText("");
                        enterWeightViewET.setFocusableInTouchMode(true);
                        enterWeightView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rounded_gray_border));
                    } else {
                        enterWeightViewET.setFocusableInTouchMode(false);
                        enterWeightView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                    }
                    showTracking();
                }
            }

        }
    }

    private void enableQuantity() {
        quantityView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rounded_gray_border));
        quantityNumberET.setFocusableInTouchMode(true);
        enterWeightViewET.setFocusableInTouchMode(true);
        quantityNumberET.setText("");
        enterWeightViewET.setText("");
        unitsNumberET.setText("");
        enterWeightView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rounded_gray_border));
    }

    private void showTracking() {
        trackingErrorTextView.setText("");
        enterTrackingView.setVisibility(View.VISIBLE);
        trackingIdTextView.setVisibility(View.VISIBLE);
        quantityNumberET.setText("1");
        quantityNumberET.setFocusableInTouchMode(false);
        quantityView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));

        inputSwitch.setClickable(false);
        TransferRequest transferRequest = ((CreateTransferEquipmentActivity) getActivity()).getTransferRequest();

        inventoryList = mEquipementInventoryRepository.getEquipmentInventory(equipmentCategoriesDetail.getEqRegionEquipentId(), transferRequest.getPickupLocation());
        inventoryList.add(0, null);
        disableStatus();
        int trackingPosition = -1;
        if (transferRequest != null && transferRequest.getEquipment() != null) {
            for (Equipment equipment : transferRequest.getEquipment()) {
                for (int i = 0; i < inventoryList.size(); i++) {

                    if (((CreateTransferEquipmentActivity) getActivity()).getEquipment() != null && ((CreateTransferEquipmentActivity) getActivity()).getEquipment().getTrackingNumber() != null && inventoryList.get(i) != null && ((CreateTransferEquipmentActivity) getActivity()).getEquipment().getTrackingNumber().equals(inventoryList.get(i).getCompanyIdNumber())) {
                        trackingPosition = i;
                    } else if (equipment != null && equipment.getTrackingNumber() != null && inventoryList != null && inventoryList.get(i) != null && equipment.getTrackingNumber().equals(inventoryList.get(i).getCompanyIdNumber())) {
                        inventoryList.remove(i);
                        trackingErrorTextView.setText("");
                        break;
                    }
                }
            }
        }
        Log.i("Equipment", "showTracking: " + trackingPosition);
        tradeAdapter = new TradeAdapter(getActivity(), R.layout.simple_spinner_item, inventoryList);
        tradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        trackingIdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos != 0) {
                    trackingNo = inventoryList.get(pos).getCompanyIdNumber();
                    trackingErrorTextView.setText("");
                } else {
                    trackingNo = "";
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        trackingIdSpinner.setAdapter(tradeAdapter);
        if (trackingPosition != -1) {
            trackingIdSpinner.setSelection(trackingPosition);
        }
        unitsNumberET.setText("");
    }

    private void disableStatus() {
        statusSpinnewView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
        statusSpinner.setSelection(0);
        statusSpinner.setClickable(false);
        statusSpinner.setEnabled(false);
    }

    private void enableStatus() {
        statusSpinnewView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rounded_gray_border));
        statusSpinner.setClickable(true);
        statusSpinner.setEnabled(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create_transfer_add_equiment_fragment, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (((CreateTransferEquipmentActivity) getActivity()).getEquipmentRegion() != null && enterDescriptionViewET != null) {
            equipmentCategoriesDetail = ((CreateTransferEquipmentActivity) getActivity()).getEquipmentRegion();
            if (equipmentCategoriesDetail != null) {
                enterDescriptionViewET.setText(equipmentCategoriesDetail.getName());
                if (equipmentCategoriesDetail != null && !TextUtils.isEmpty(equipmentCategoriesDetail.getWeight())) {

                    BigDecimal weight1 = BigDecimal.valueOf(Double.parseDouble(equipmentCategoriesDetail.getWeight().startsWith(".") ? "0" + equipmentCategoriesDetail.getWeight() : equipmentCategoriesDetail.getWeight()));
                    Float w1 = weight1.floatValue();
                    String s2 = String.format("%.2f", w1);
                    enterWeightViewET.setText(s2);
                }
                enableInputMethodSwitch();
                if (!TextUtils.isEmpty(equipmentCategoriesDetail.getWeight())) {
                    enterWeightView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rounded_gray_border));
                    if (!TextUtils.isEmpty(enterWeightViewET.getText().toString())) {
                        enterWeightViewET.setFocusableInTouchMode(false);
                        enterWeightView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                    }
                }
                String message = "This equipment is not currently in the pick-up location's inventory.";
                checkUniqueCase(false);

            }
        }
        if (enterDescriptionViewET != null) {
            if (equipmentCategoriesDetails != null) {
                equipmentCategoriesDetails.clear();
            }
            equipmentCategoriesDetails.addAll(mEquipementInventoryRepository.getEquipmentRegionAccordingTenant(loginResponse.getUserDetails().getTenantId(), loginResponse.getUserDetails().getUsers_id()));
            autocompleteSelectEquipmentAdapter = new AutocompleteSelectEquipmentAdapter(getContext(), R.layout.searchable_adapter_item, equipmentCategoriesDetails);
            enterDescriptionViewET.setOnItemClickListener(onItemClickListener);
            enterDescriptionViewET.setAdapter(autocompleteSelectEquipmentAdapter);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
        projectId = getActivity().getIntent().getIntExtra("project_id", 0);
        showTrackId = getActivity().getIntent().getBooleanExtra("show_track_id", false);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
//        enableDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.rounded_gray_border);
        disableInputMethodSwitch();
        if (equipmentCategoriesDetails != null) {
            equipmentCategoriesDetails.clear();
        }
      /*  inputSwitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!inputSwitch.isClickable() && !inputSwitch.isEnabled()) {
                    showMessageAlert(getActivity(), "Qunatity per unit has not been added for this Equipment.", getString(R.string.ok), false);
                }

                return false;
            }
        });*/
        equipmentCategoriesDetails.addAll(mEquipementInventoryRepository.getEquipmentRegionAccordingTenant(loginResponse.getUserDetails().getTenantId(), loginResponse.getUserDetails().getUsers_id()));
        autocompleteSelectEquipmentAdapter = new AutocompleteSelectEquipmentAdapter(getContext(), R.layout.searchable_adapter_item, equipmentCategoriesDetails);
        enterDescriptionViewET.setOnItemClickListener(onItemClickListener);
        enterDescriptionViewET.setAdapter(autocompleteSelectEquipmentAdapter);
        if (((CreateTransferEquipmentActivity) getActivity()).getEquipment() != null) {
            lookUpEquipmentTextView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rounded_opacity_blue_view));
//            enterDescriptionViewET.setFocusableInTouchMode(false);
            lookUpEquipmentTextView.setClickable(false);
            enterDescriptionViewET.setKeyListener(null);
            enterDescriptionView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
        }
        statusArrayList = new ArrayList<>();
        statusArrayList.add(EquipmentStatusEnum.ACTIVE.toString());
        statusArrayList.add(EquipmentStatusEnum.INACTIVE.toString());

        companyAdapter = new CompanyAdapter(getActivity(), R.layout.simple_spinner_item, statusArrayList);
        companyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(companyAdapter);
        statusSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ((CreateTransferEquipmentActivity) getActivity()).hideKeyboard(getActivity());
                return false;
            }
        });
        statusSpinner.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                ((CreateTransferEquipmentActivity) getActivity()).hideKeyboard(getActivity());
                return false;
            }
        });
      /*  statusSpinner.setOnFocusChangeListener(


                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {

                        ((CreateTransferEquipmentActivity) getActivity()).hideKeyboard(getActivity());
                    }
                });*/
        rentedCheckBox.setOnClickListener(view12 -> ((CreateTransferEquipmentActivity) getActivity()).hideKeyboard(getActivity()));
        enterDescriptionViewET.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                nameErrorTextView.setText("");
                if (equipmentCategoriesDetail != null && !charSequence.toString().equals(equipmentCategoriesDetail.getName()) && ((CreateTransferEquipmentActivity) getActivity()).getEquipmentRegion() == null) {
                    equipmentCategoriesDetail = null;
                    trackingNo = "";
                    quantityView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rounded_gray_border));
                    enableQuantity();
                    disableInputMethodSwitch();

                    enterTrackingView.setVisibility(View.GONE);
                    trackingIdTextView.setVisibility(View.GONE);
                    trackingErrorTextView.setText("");
                    enableStatus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        unitsNumberET.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (inputSwitch.isChecked()) {
                    if (unitsNumberET.getText().toString().length() > 0 && unitsNumberET.getText().toString().startsWith("-")) {
                        unitsNumberET.setText(unitsNumberET.getText().toString().replace("-", ""));
                        unitsNumberET.setSelection(unitsNumberET.getText().length());
                        return;
                    }
                    if (unitsNumberET.getText().toString().equals(".")) {
                        unitsNumberET.setText("0.");
                        unitsNumberET.setSelection(unitsNumberET.getText().length());
                        return;
                    } else {
                        if (unitsNumberET.getText().length() > 1 && unitsNumberET.getText().toString().contains(".") && !unitsNumberET.getText().toString().substring(unitsNumberET.getText().length() - 1).equals(".")) {
                            BigDecimal fd = new BigDecimal(unitsNumberET.getText().toString());
                            BigDecimal cutted = fd.setScale(1, RoundingMode.DOWN);
                            float f = cutted.floatValue();
                            if (!unitsNumberET.getText().toString().equals(String.valueOf(f))) {
                                unitsNumberET.setText(String.valueOf(f));
                                unitsNumberET.setSelection(unitsNumberET.getText().length());
                            }
                        }
                        if (equipmentCategoriesDetail != null && !TextUtils.isEmpty(equipmentCategoriesDetail.getItemsPerUnit())) {
                            calculateQuantity();
                        }
                    }
                } else if (unitsNumberET.getText().toString().equals("0.0")) {
                    unitsNumberET.setText("");

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        quantityNumberET.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                quantityErrorTextView.setText("");
                if (!inputSwitch.isChecked()) {
                    if (equipmentCategoriesDetail != null && !TextUtils.isEmpty(equipmentCategoriesDetail.getItemsPerUnit())) {
                        calculateUnits();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        enterWeightViewET.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                weightErrorTextView.setText("");
                if (enterWeightViewET.getText().toString().equals(".")) {
                    enterWeightViewET.setText("0.");
                    enterWeightViewET.setSelection(enterWeightViewET.getText().length());
                    return;
                } else if (enterWeightViewET.getText().length() > 1 && enterWeightViewET.getText().toString().contains(".") && !enterWeightViewET.getText().toString().substring(enterWeightViewET.getText().length() - 1).equals(".")) {
                    BigDecimal fd = new BigDecimal(enterWeightViewET.getText().toString());
                    BigDecimal cutted = fd.setScale(2, RoundingMode.DOWN);
                    float f = cutted.floatValue();
//                    String s1 = String.format("%.2f", f);
                    if (!enterWeightViewET.getText().toString().equals(f + "")) {
                        enterWeightViewET.setText(f + "");
                        enterWeightViewET.setSelection(enterWeightViewET.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

     /*   if (((CreateTransferEquipmentActivity) getActivity()).getEquipment() != null) {
            Equipment equipment = ((CreateTransferEquipmentActivity) getActivity()).getEquipment();
            addTextView.setText(getString(R.string.save));
            enterDescriptionViewET.setText(equipment.getName());
            if (equipment.getEquipmentId() != 0) {
                equipmentCategoriesDetail = mEquipementInventoryRepository.getEquipmentRegion(equipment.getEquipmentId());
            }
            if (equipment.getUnit() != 0.0) {
                unitsNumberET.setText(String.valueOf(equipment.getUnit()));
                inputSwitch.setEnabled(true);
                inputSwitch.setVisibility(View.VISIBLE);
                inputTextView.setVisibility(View.VISIBLE);
                unitsView.setVisibility(View.VISIBLE);
                unTextView.setVisibility(View.VISIBLE);
                inputSwitch.setChecked(false);
                inputSwitch.setClickable(true);
                equipmentCategoriesDetail = mEquipementInventoryRepository.getEquipmentRegion(equipment.getEquipmentId());
                if (equipmentCategoriesDetail != null && !TextUtils.isEmpty(equipmentCategoriesDetail.getWeight())) {
                    enterWeightViewET.setText(equipmentCategoriesDetail.getWeight());
                    enterWeightViewET.setFocusableInTouchMode(false);
                    enterWeightView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                }
            } else {

                unitsNumberET.setText("");
                inputSwitch.setEnabled(false);
                inputSwitch.setVisibility(View.INVISIBLE);
                inputTextView.setVisibility(View.INVISIBLE);
                unitsView.setVisibility(View.INVISIBLE);
                unTextView.setVisibility(View.INVISIBLE);
                inputSwitch.setClickable(false);
                unitsNumberET.setFocusableInTouchMode(false);

            }
            quantityNumberET.setText(equipment.getQuantity().split(".",equipment.getQuantity().length())[0]);
            enterWeightViewET.setText(equipment.getWeight());
            rentedCheckBox.setChecked(equipment.getEquipmentStatus() == 2);
            statusSpinner.setSelection(equipment.getStatus() - 1);
            checkUniqueCase(true);

        }*/
    }

    private void calculateUnits() {
        Float itemPerUnit = Float.parseFloat(equipmentCategoriesDetail.getItemsPerUnit());
        long quantity = Long.parseLong(TextUtils.isEmpty(quantityNumberET.getText().toString()) ? "0" : quantityNumberET.getText().toString());
        Float unit = quantity / itemPerUnit;
        String s = String.format("%.1f", unit);

        unitsNumberET.setText(s.equals("0.0") ? "" : s);
    }

    private void calculateQuantity() {
        Float itemPerUnit = Float.parseFloat(equipmentCategoriesDetail.getItemsPerUnit());
        String unita = unitsNumberET.getText().toString();
        if (unita.equals(".")) {
            unita = "0.";
        }
        Float unit = Float.parseFloat(TextUtils.isEmpty(unita) ? "0" : unita);
        long quantity = (long) (unit * itemPerUnit);
        quantityNumberET.setText(String.valueOf(quantity));
    }

    @OnCheckedChanged(R.id.inputSwitch)
    public void onInputCheckedChange() {
        ((CreateTransferEquipmentActivity) getActivity()).hideKeyboard(getActivity());
        unitsNumberET.setFocusable(false);
        quantityNumberET.setFocusable(false);
        if (inputSwitch.isChecked()) {
            unitsNumberET.setFocusableInTouchMode(true);
            quantityNumberET.setFocusableInTouchMode(false);
            quantityView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
            unitsView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rounded_gray_border));
        } else {
            unitsView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
            quantityView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rounded_gray_border));
            quantityNumberET.setFocusableInTouchMode(true);
            unitsNumberET.setFocusableInTouchMode(false);
        }
    }

    private void disableInputMethodSwitch() {
        inputSwitch.setEnabled(false);
        inputSwitch.setVisibility(View.INVISIBLE);
        inputTextView.setVisibility(View.INVISIBLE);
        unitsView.setVisibility(View.INVISIBLE);
        unTextView.setVisibility(View.INVISIBLE);
        inputSwitch.setClickable(false);
        unitsNumberET.setFocusableInTouchMode(false);
        quantityNumberET.setText("");
        unitsNumberET.setText("");
        enterWeightViewET.setText("");
        enterWeightViewET.setFocusableInTouchMode(true);
//        equipmentCategoriesDetail = null;
        enterWeightView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rounded_gray_border));
    }

    private void enableInputMethodSwitch() {
        inputSwitch.setEnabled(true);
        inputSwitch.setVisibility(View.VISIBLE);
        inputTextView.setVisibility(View.VISIBLE);
        unitsView.setVisibility(View.VISIBLE);
        unTextView.setVisibility(View.VISIBLE);
        inputSwitch.setChecked(false);
        inputSwitch.setClickable(true);
        unitsNumberET.setText("");
        quantityNumberET.setText("");
        enterWeightView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rounded_gray_border));
        if (!TextUtils.isEmpty(enterWeightViewET.getText().toString())) {
            enterWeightViewET.setFocusableInTouchMode(false);
            enterWeightView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
        }
    }

    public boolean checkBackValidate() {
        boolean isValid = true;


        if (((CreateTransferEquipmentActivity) getActivity()).getEquipment() != null) {
            Equipment equipment = ((CreateTransferEquipmentActivity) getActivity()).getEquipment();
            if (!enterDescriptionViewET.getText().toString().equals(equipment.getName())) {
                isValid = false;
            }
            if (!quantityNumberET.getText().toString().equals(equipment.getQuantity())) {
                isValid = false;
            }
            BigDecimal weight = BigDecimal.valueOf(Double.parseDouble(TextUtils.isEmpty(equipment.getWeight()) ? "0" : equipment.getWeight()));
            Float w = weight.floatValue();
            String s1 = String.format("%.2f", w);
            if (!enterWeightViewET.getText().toString().equals(equipment.getWeight()) && !equipment.getWeight().equals(s1)) {
                isValid = false;
            }
           /* if (!unitsNumberET.getText().toString().equals(equipment.getUnit())) {
                isValid = false;
            }*/
            if (rentedCheckBox.isChecked() != (equipment.getEquipmentStatus() == 2)) {
                isValid = false;
            }
            if (statusSpinner.getSelectedItemPosition() != equipment.getStatus() - 1) {
                isValid = false;
            }
            if (showTrackId && !trackingNo.equals(equipment.getTrackingNumber())) {
                isValid = false;
            }

        } else {
            if (!TextUtils.isEmpty(enterDescriptionViewET.getText().toString())) {
                isValid = false;
            }
            if (!TextUtils.isEmpty(quantityNumberET.getText().toString())) {
                isValid = false;
            }
            if (!TextUtils.isEmpty(enterWeightViewET.getText().toString())) {
                isValid = false;
            }
        }
        return isValid;
    }

    @OnClick(R.id.cancelTextView)
    public void cancelViewClick() {
        ((CreateTransferEquipmentActivity) getActivity()).hideKeyboard(getActivity());

        if (checkBackValidate()) {
            getActivity().finish();
        } else {

            try {
                if (alertDialog == null || !alertDialog.isShowing()) {
                    alertDialog = new AlertDialog.Builder(getActivity()).create();
                }
                alertDialog.setMessage(getString(R.string.are_you_sure_you_want_to_exit_without_saving));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
                    alertDialog.dismiss();
                    getActivity().finish();
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
        }
    }

    @OnClick(R.id.addTextView)
    public void onAddTextView() {
        if (!NetworkService.isNetworkAvailable(getActivity())) {
            showMessageAlert(getActivity(), getString(R.string.internet_connection_check_transfer_overview), getString(R.string.ok), false);
            return;
        }
        ((CreateTransferEquipmentActivity) getActivity()).hideKeyboard(getActivity());
        if (!checkValidation()) {
            return;
        }
        long qu = Long.parseLong(quantityNumberET.getText().toString());
        if (qu == 0) {
            showMessageAlert(getActivity(), getString(R.string.quantity_weight_validation_msg), getString(R.string.ok), false);
            return;
        }
        Equipment equipment = ((CreateTransferEquipmentActivity) getActivity()).getEquipment();
        if (equipment == null) {
            equipment = new Equipment();
            equipment.setTransferEquipmentId(0);
        }
        equipment.setEquipmentId(equipmentCategoriesDetail != null ? equipmentCategoriesDetail.getEqRegionEquipentId() : 0);
        equipment.setName(enterDescriptionViewET.getText().toString() != null ? enterDescriptionViewET.getText().toString().trim() : "");
        equipment.setQuantity(TextUtils.isEmpty(quantityNumberET.getText().toString()) ? "0" : quantityNumberET.getText().toString());
        equipment.setEquipmentStatus(rentedCheckBox.isChecked() ? 2 : 1);
        equipment.setStatus(statusSpinner.getSelectedItemPosition() + 1);


        Long quantity = Long.parseLong(TextUtils.isEmpty(quantityNumberET.getText().toString()) ? "0" : quantityNumberET.getText().toString());
        BigDecimal weight = BigDecimal.valueOf(Double.parseDouble(TextUtils.isEmpty(enterWeightViewET.getText().toString()) ? "0" : enterWeightViewET.getText().toString()));
        BigDecimal totalWeight = weight.multiply(BigDecimal.valueOf(quantity));
        Float f = totalWeight.floatValue();
        String s = String.format("%.2f", f);
        equipment.setTotalWeight(s);
        equipment.setUnit(Float.parseFloat(TextUtils.isEmpty(unitsNumberET.getText().toString()) || unitsNumberET.getText().toString().equals("-") ? "0.0" : unitsNumberET.getText().toString()));
        Float w = weight.floatValue();
        String s1 = String.format("%.2f", w);
        equipment.setWeight(s1);
        equipment.setTrackingNumber(trackingNo);
        ((CreateTransferEquipmentActivity) getActivity()).saveEquipment(equipment);
    }

    private boolean checkValidation() {
        boolean isValid = true;
        if (TextUtils.isEmpty(enterDescriptionViewET.getText().toString())) {
            nameErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(enterWeightViewET.getText().toString())) {
            weightErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(quantityNumberET.getText().toString())) {
            quantityErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        if (enterTrackingView.getVisibility() == View.VISIBLE && TextUtils.isEmpty(trackingNo) && showTrackId && equipmentCategoriesDetail != null && equipmentCategoriesDetail.getType().equals("Unique")) {
            trackingErrorTextView.setText(getString(R.string.this_field_is_required));
            isValid = false;
        }
        return isValid;
    }

    @OnClick(R.id.lookUpEquipmentTextView)
    public void onlookUpEquipmentTextView() {
        ((CreateTransferEquipmentActivity) getActivity()).hideKeyboard(getActivity());
        ((CreateTransferEquipmentActivity) getActivity()).setEquipmentRegion(null);
//        if (((CreateTransferEquipmentActivity) getActivity()).getEquipment() == null) {
        ((CreateTransferEquipmentActivity) getActivity()).openCategories();
//        }
    }

    public void refreshData() {
        equipmentCategoriesDetails.clear();

        if (equipmentCategoriesDetails != null) {
            equipmentCategoriesDetails.clear();
        }
        equipmentCategoriesDetails.addAll(mEquipementInventoryRepository.getEquipmentRegionAccordingTenant(loginResponse.getUserDetails().getTenantId(), loginResponse.getUserDetails().getUsers_id()));
        if (getContext() != null && equipmentCategoriesDetails != null) {
            autocompleteSelectEquipmentAdapter = new AutocompleteSelectEquipmentAdapter(getContext(), R.layout.searchable_adapter_item, equipmentCategoriesDetails);
            enterDescriptionViewET.setOnItemClickListener(onItemClickListener);
            enterDescriptionViewET.setAdapter(autocompleteSelectEquipmentAdapter);
        }

        if (getActivity() != null && ((CreateTransferEquipmentActivity) getActivity()).getEquipment() != null) {
            Equipment equipment = ((CreateTransferEquipmentActivity) getActivity()).getEquipment();
            addTextView.setText(getString(R.string.save));
            enterDescriptionViewET.setText(equipment.getName());
            if (equipment.getEquipmentId() != 0) {
                equipmentCategoriesDetail = mEquipementInventoryRepository.getEquipmentRegion(equipment.getEquipmentId());
            }
            if (equipmentCategoriesDetail != null && !TextUtils.isEmpty(equipmentCategoriesDetail.getItemsPerUnit())/*equipment.getUnit() != 0.0*/) {
                unitsNumberET.setText(String.valueOf(equipment.getUnit()));
                inputSwitch.setEnabled(true);
                inputSwitch.setVisibility(View.VISIBLE);
                inputTextView.setVisibility(View.VISIBLE);
                unitsView.setVisibility(View.VISIBLE);
                unTextView.setVisibility(View.VISIBLE);
                inputSwitch.setChecked(false);
                inputSwitch.setClickable(true);
                equipmentCategoriesDetail = mEquipementInventoryRepository.getEquipmentRegion(equipment.getEquipmentId());


            } else {

                unitsNumberET.setText("");
                inputSwitch.setEnabled(false);
                inputSwitch.setVisibility(View.INVISIBLE);
                inputTextView.setVisibility(View.INVISIBLE);
                unitsView.setVisibility(View.INVISIBLE);
                unTextView.setVisibility(View.INVISIBLE);
                inputSwitch.setClickable(false);
                unitsNumberET.setFocusableInTouchMode(false);

            }

            BigDecimal weight = BigDecimal.valueOf(Double.parseDouble(TextUtils.isEmpty(equipment.getWeight()) ? "0" : equipment.getWeight()));
            Float w = weight.floatValue();
            String s1 = String.format("%.2f", w);

            enterWeightViewET.setText(s1);
            if (equipmentCategoriesDetail != null && !TextUtils.isEmpty(equipmentCategoriesDetail.getWeight())) {

                BigDecimal weight1 = BigDecimal.valueOf(Double.parseDouble(equipmentCategoriesDetail.getWeight().startsWith(".") ? "0" + equipmentCategoriesDetail.getWeight() : equipmentCategoriesDetail.getWeight()));
                Float w1 = weight1.floatValue();
                String s2 = String.format("%.2f", w1);
                enterWeightViewET.setText(s2);
//                enterWeightViewET.setText(equipmentCategoriesDetail.getWeight());
                enterWeightViewET.setFocusableInTouchMode(false);
                enterWeightView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
            }
            quantityNumberET.setText(equipment.getQuantity());
            rentedCheckBox.setChecked(equipment.getEquipmentStatus() == 2);
            statusSpinner.setSelection(equipment.getStatus() - 1);
            checkUniqueCase(true);

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
                if (closeActivity) {
                    getActivity().finish();
                }

            });
            if (alertDialog != null && !alertDialog.isShowing()) {

                alertDialog.setCancelable(false);
                alertDialog.show();
            }


        } catch (Exception e) {
        }

    }

}


