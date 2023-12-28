package com.pronovoscm.utils.dialogs;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.adapter.CompanyAdapter;
import com.pronovoscm.adapter.TradeAdapter;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.CompanyList;
import com.pronovoscm.persistence.domain.CrewList;
import com.pronovoscm.persistence.domain.Trades;
import com.pronovoscm.persistence.repository.CrewReportRepository;
import com.pronovoscm.persistence.repository.FieldPaperWorkRepository;
import com.pronovoscm.utils.CustomDialogFragment;
import com.pronovoscm.utils.SharedPref;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddCrewDialog extends CustomDialogFragment implements View.OnClickListener {
    @Inject
    FieldPaperWorkRepository mFieldPaperWorkRepository;
    @Inject
    CrewReportRepository mCrewReportRepository;

    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.tradeErrorTextView)
    TextView tradeErrorTextView;
    @BindView(R.id.deleteTextView)
    TextView deleteTextView;
    @BindView(R.id.saveTextView)
    TextView saveTextView;
    @BindView(R.id.selectTextView)
    TextView selectTextView;
    @BindView(R.id.cancelTextView)
    TextView cancelTextView;
    @BindView(R.id.tradeSpinner)
    AppCompatSpinner tradeSpinner;
    @BindView(R.id.companySpinner)
    AppCompatSpinner companySpinner;
    @BindView(R.id.journeymanEditText)
    EditText journeymanEditText;
    @BindView(R.id.foremanEditText)
    EditText foremanEditText;
    @BindView(R.id.suptEditText)
    EditText suptEditText;
    @BindView(R.id.apprenticeEditText)
    EditText apprenticeEditText;
    @BindView(R.id.cardView)
    CardView cardView;
    @BindView(R.id.cancelView)
    RelativeLayout cancelView;

    private List<Trades> mTradesList;
    private List<CompanyList> mCompanyLists;
    private Trades mTrades;
    private CompanyList mCompanyList;
    private int projectId;
    private CrewList crewList;
    private long crewListMobileId;
    private CompanyAdapter companyAdapter;
    private TradeAdapter tradeAdapter;
    private LoginResponse loginResponse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Translucent_Dialog);
//        getActivity().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams attrs = dialog.getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setGravity(Gravity.TOP | Gravity.START);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.add_crew_dialog_view, container, false);
        ButterKnife.bind(this, rootview);
        return rootview;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        hideKeyboardFrom(getActivity(), getView());
//        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        int topBottom = (int) getResources().getDimension(R.dimen.dialog_margin);
//        int leftRight = (int) getResources().getDimension(R.dimen._15sdp);
//        layoutParams.setMargins(leftRight, topBottom, leftRight, topBottom);
//        cardView.setLayoutParams(layoutParams);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cancelTextView.setOnClickListener(this);
        saveTextView.setOnClickListener(this);
        deleteTextView.setOnClickListener(this);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
        projectId = getArguments().getInt("projectId");
        crewListMobileId = getArguments().getLong("crewListMobileId");
        if (crewListMobileId != 0) {
            crewList = mCrewReportRepository.getCrewListDetail(crewListMobileId);
            if (crewList != null) {
                deleteTextView.setVisibility(View.VISIBLE);
                setCrewListInfo();
                titleTextView.setText("Edit Crew");
            }
        } else {
            deleteTextView.setVisibility(View.GONE);
            titleTextView.setText("Add New Crew");
        }

        mCompanyLists = mFieldPaperWorkRepository.getCompanyList(projectId);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        setTradeList();
        setCompanyAdapter();

        spinnerSelection();
    }

    private void setTradeList() {
        mTradesList = new ArrayList<>();
        mTradesList.add(null);
        mTradesList.addAll(mFieldPaperWorkRepository.getTrades(loginResponse));
        tradeAdapter = new TradeAdapter(getActivity(), R.layout.simple_spinner_item, mTradesList);
        tradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tradeSpinner.setAdapter(tradeAdapter);

        tradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mTrades = mTradesList.get(pos);
                if (pos > 0) {
                    tradeErrorTextView.setText("");
                    selectTextView.setVisibility(View.GONE);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {


            }
        });
    }

    private void setCompanyAdapter(){
        companyAdapter = new CompanyAdapter(getActivity(), R.layout.simple_spinner_item, mCompanyLists);
        companyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        companySpinner.setAdapter(companyAdapter);
        companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCompanyList = mCompanyLists.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void spinnerSelection() {
        for (int i = 0; i < mCompanyLists.size(); i++) {
            CompanyList companyList = mCompanyLists.get(i);
            if (crewList != null) {
                int crewCmpId = crewList.getCompanyId();
                int cmpId = companyList.getCompanyId();
                if (crewCmpId == cmpId) {
                    companySpinner.setSelection(i);
                }
            } else if (crewList == null && companyList.getSelected()) {
                companySpinner.setSelection(i);
            }
        }
        if (crewList != null) {
            for (int i = 0; i < mTradesList.size(); i++) {
                Trades trades = mTradesList.get(i);
                if (trades != null && crewList != null && crewList.getTradesId() == trades.getTradesId()) {
                    tradeSpinner.setSelection(i);
                }
            }
        }
    }

    private void setCrewListInfo() {
        foremanEditText.setText(String.valueOf(crewList.getForeman()));
        journeymanEditText.setText(String.valueOf(crewList.getJourneyman()));
        apprenticeEditText.setText(String.valueOf(crewList.getApprentice()));
        suptEditText.setText(String.valueOf(crewList.getSupt()));
    }

    @OnClick(R.id.cancelView)
    public void clickCancelView() {
        AddCrewDialog.this.dismiss();
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveTextView:
            saveCrewDetails();
                break;
            case R.id.cancelTextView:
                hideKeyboardFrom(getContext(), getView());
                dismiss();
                break;
            case R.id.deleteTextView:
              deleteCrewDetails();
                break;
            default:
                break;
        }
    }

    private void deleteCrewDetails() {
        CrewList deleteCrewList = new CrewList();

        deleteCrewList.setCompanyId(mCompanyList.getCompanyId());
        deleteCrewList.setCompanyName(mCompanyList.getName());
        deleteCrewList.setCrewReportId(0);
        deleteCrewList.setDeletedAt(new Date());
        deleteCrewList.setIsSync(false);
        deleteCrewList.setJourneyman(TextUtils.isEmpty(apprenticeEditText.getText().toString()) ? 0 : Integer.parseInt(journeymanEditText.getText().toString()));
        deleteCrewList.setApprentice(TextUtils.isEmpty(apprenticeEditText.getText().toString()) ? 0 : Integer.parseInt(apprenticeEditText.getText().toString()));
        deleteCrewList.setForeman(TextUtils.isEmpty(apprenticeEditText.getText().toString()) ? 0 : Integer.parseInt(foremanEditText.getText().toString()));
        deleteCrewList.setSupt(TextUtils.isEmpty(suptEditText.getText().toString()) ? 0 : Integer.parseInt(suptEditText.getText().toString()));
        deleteCrewList.setProjectId(projectId);
        if (mTrades != null) {
            deleteCrewList.setTrade(mTrades.getName());
            deleteCrewList.setTradesId(mTrades.getTradesId());
        } else {
            deleteCrewList.setTradesId(0);

        }
        deleteCrewList.setType(mCompanyList.getType());
        if (crewList != null) {
            deleteCrewList.setCrewReportId(crewList.getCrewReportId());
            deleteCrewList.setCrewReportIdMobile(crewList.getCrewReportIdMobile());
        }
        hideKeyboardFrom(getContext(), getView());
        EventBus.getDefault().post(deleteCrewList);
        dismiss();
    }

    private void saveCrewDetails() {
        if (mTrades == null) {
            tradeErrorTextView.setText(R.string.please_select_trade);
        } else if (crewList != null && crewList.getTradesId() == mTrades.getTradesId() && crewList.getCompanyId() == mCompanyList.getCompanyId()
                && crewList.getApprentice() == (TextUtils.isEmpty(apprenticeEditText.getText().toString()) ? 0 : Integer.parseInt(apprenticeEditText.getText().toString()))
                && crewList.getForeman() == (TextUtils.isEmpty(foremanEditText.getText().toString()) ? 0 : Integer.parseInt(foremanEditText.getText().toString()))
                && crewList.getJourneyman() == (TextUtils.isEmpty(journeymanEditText.getText().toString()) ? 0 : Integer.parseInt(journeymanEditText.getText().toString()))
                && crewList.getSupt() == (TextUtils.isEmpty(suptEditText.getText().toString()) ? 0 : Integer.parseInt(suptEditText.getText().toString()))) {
            hideKeyboardFrom(getContext(), getView());
            dismiss();
        } else if (mTrades != null) {
            CrewList saveCrewList = new CrewList();
            saveCrewList.setCompanyId(mCompanyList.getCompanyId());
            saveCrewList.setCompanyName(mCompanyList.getName());
            saveCrewList.setCrewReportId(0);
            saveCrewList.setDeletedAt(null);
            saveCrewList.setIsSync(false);
            saveCrewList.setJourneyman(TextUtils.isEmpty(journeymanEditText.getText().toString()) ? 0 : Integer.parseInt(journeymanEditText.getText().toString()));
            saveCrewList.setApprentice(TextUtils.isEmpty(apprenticeEditText.getText().toString()) ? 0 : Integer.parseInt(apprenticeEditText.getText().toString()));
            saveCrewList.setForeman(TextUtils.isEmpty(foremanEditText.getText().toString()) ? 0 : Integer.parseInt(foremanEditText.getText().toString()));
            saveCrewList.setSupt(TextUtils.isEmpty(suptEditText.getText().toString()) ? 0 : Integer.parseInt(suptEditText.getText().toString()));
            saveCrewList.setProjectId(projectId);
            saveCrewList.setTrade(mTrades.getName());
            saveCrewList.setTradesId(mTrades.getTradesId());
            saveCrewList.setType(mCompanyList.getType());
            if (crewList != null) {
                saveCrewList.setCrewReportId(crewList.getCrewReportId());
                saveCrewList.setCrewReportIdMobile(crewList.getCrewReportIdMobile());
            }
            hideKeyboardFrom(getContext(), getView());
            EventBus.getDefault().post(saveCrewList);
            dismiss();
        }
    }


}

