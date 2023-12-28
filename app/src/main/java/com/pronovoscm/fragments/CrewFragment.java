package com.pronovoscm.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.pdftron.pdf.utils.Utils;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.activity.LoginActivity;
import com.pronovoscm.adapter.CompanyAdapter;
import com.pronovoscm.data.FieldPaperWorkProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.model.ObjectEnum;
import com.pronovoscm.model.response.companylist.CompanyListRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.login.UserPermissions;
import com.pronovoscm.persistence.domain.CompanyList;
import com.pronovoscm.persistence.domain.CrewList;
import com.pronovoscm.persistence.domain.Trades;
import com.pronovoscm.persistence.repository.CrewReportRepository;
import com.pronovoscm.persistence.repository.FieldPaperWorkRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.ObjectEvent;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.dialogs.MessageDialog;
import com.pronovoscm.utils.dialogs.ObjectDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CrewFragment extends Fragment implements View.OnClickListener, BackPressedListener {
    @Inject
    FieldPaperWorkRepository mFieldPaperWorkRepository;
    @Inject
    CrewReportRepository mCrewReportRepository;

    @Inject
    FieldPaperWorkProvider mFieldPaperWorkProvider;

    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    ImageView deleteImageView;
    @BindView(R.id.tradeErrorTextView)
    TextView tradeErrorTextView;
    @BindView(R.id.deleteTextView)
    TextView deleteTextView;
    @BindView(R.id.saveTextView)
    TextView saveTextView;
    @BindView(R.id.tradeNameTextView)
    TextView tradeNameTextView;
    @BindView(R.id.cancelTextView)
    TextView cancelTextView;
    @BindView(R.id.companyListNameTextView)
    TextView companyListNameTextView;
    @BindView(R.id.journeymanEditText)
    EditText journeymanEditText;
    @BindView(R.id.foremanEditText)
    EditText foremanEditText;
    @BindView(R.id.suptEditText)
    EditText suptEditText;
    @BindView(R.id.apprenticeEditText)
    EditText apprenticeEditText;
    @BindView(R.id.tradespinnewView)
    RelativeLayout tradespinnewView;
    @BindView(R.id.companyListView)
    RelativeLayout companyListView;
    @BindView(R.id.suptView)
    RelativeLayout suptView;
    @BindView(R.id.journeymanView)
    RelativeLayout journeymanView;
    @BindView(R.id.foremanView)
    RelativeLayout foremanView;
    @BindView(R.id.apprenticeView)
    RelativeLayout apprenticeView;
    //    @BindView(R.id.addImageView)
//    ImageView addImageView;
//    @BindView(R.id.titleTextView)
    private TextView titleTextView;
    private List<Trades> mTradesList;
    private List<CompanyList> mCompanyLists;
    private Trades mTrades;
    private CompanyList mCompanyList;
    private int projectId;
    private CrewList crewList;
    private ImageView addImageView;
    private MessageDialog messageDialog;
    private Activity activity;
    private LoginResponse loginResponse;
    private UserPermissions userPermissions;
    private int canEditWorkDetail;
    private int canDeleteWorkDetail;

    public static BackPressedListener backpressedlistener;
    public static Boolean isUpdated = true;
    private static final String crewListMobileIdConstant = "crewListMobileId";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.crew_detail_view, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        userPermissions = loginResponse.getUserDetails().getPermissions().get(0);

        messageDialog = new MessageDialog();
        titleTextView = getActivity().findViewById(R.id.titleTextView);
        addImageView = getActivity().findViewById(R.id.addImageView);
        deleteImageView = getActivity().findViewById(R.id.deleteImageView);
        addImageView.setVisibility(View.GONE);
//        backImageView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_back));
        canEditWorkDetail = loginResponse.getUserDetails().getPermissions().get(0).getEditProjectDailyReport();
        canDeleteWorkDetail = loginResponse.getUserDetails().getPermissions().get(0).getDeleteProjectDailyReport();
        if (!NetworkService.isNetworkAvailable(getContext())) {
            offlineTextView.setVisibility(View.VISIBLE);
        }
        cancelTextView.setOnClickListener(this);
        saveTextView.setOnClickListener(this);
        deleteTextView.setOnClickListener(this);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
        projectId = getArguments().getInt("projectId");
        long crewListMobileId = getArguments().getLong(crewListMobileIdConstant);
        if (crewListMobileId != 0) {
            crewList = mCrewReportRepository.getCrewListDetail(crewListMobileId);
            if (crewList != null) {
//                deleteTextView.setVisibility(View.VISIBLE);
                setCrewListInfo();
                if(userPermissions.getEditProjectDailyReport() == 1){
                    titleTextView.setText(getString(R.string.edit_crew));

                }else {
                    titleTextView.setText(getString(R.string.crew));

                }
                if (canDeleteWorkDetail == 1) {
                    deleteImageView.setVisibility(View.VISIBLE);
                }
                if (canEditWorkDetail != 1) {
                    tradespinnewView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                    companyListView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                    apprenticeView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                    foremanView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                    journeymanView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                    suptView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                    tradespinnewView.setClickable(false);
                    companyListNameTextView.setLongClickable(false);
                    suptEditText.setLongClickable(false);
                    suptEditText.setFocusableInTouchMode(false);
                    journeymanEditText.setLongClickable(false);
                    journeymanEditText.setFocusableInTouchMode(false);
                    foremanEditText.setLongClickable(false);
                    foremanEditText.setFocusableInTouchMode(false);
                    apprenticeEditText.setLongClickable(false);
                    apprenticeEditText.setFocusableInTouchMode(false);
                    companyListView.setClickable(false);
                    saveTextView.setVisibility(View.GONE);
                    cancelTextView.setVisibility(View.GONE);
                }
            }
        } else {
            titleTextView.setText(getString(R.string.add_crew));
        }
        deleteImageView.setOnClickListener(view1 -> {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setMessage(getActivity().getString(R.string.are_you_sure_you_want_to_delete_this_entry));
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getActivity().getString(R.string.cancel), (dialog, which) -> {
                dialog.dismiss();
            });
            alertDialog.setCancelable(false);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getActivity().getString(R.string.ok), (dialog, which) -> {
                alertDialog.dismiss();
                crewList.setDeletedAt(new Date());
                crewList.setIsSync(false);
                mCrewReportRepository.addUpdateCrewList(crewList);
                isUpdated=false;
                getActivity().onBackPressed();
            });
            alertDialog.show();
            Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(ContextCompat.getColor(getActivity(), R.color.gray_948d8d));
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

        });
        mCompanyLists = mFieldPaperWorkRepository.getCompanyList(projectId);
        mTradesList = new ArrayList<>();
        mTradesList.add(null);
        mTradesList.addAll(mFieldPaperWorkRepository.getTrades(loginResponse));
        mCompanyList = mCompanyLists.get(0);
        companyListNameTextView.setText(mCompanyList.getName());
        spinnerSelection();
        callTradesAPI();
        callCompanyListAPI();
        Log.e("TAG", "oncreate: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        titleTextView.setText(R.string.crew);

        deleteImageView.setVisibility(View.GONE);

        if (canEditWorkDetail == 1) {
            addImageView.setVisibility(View.VISIBLE);
        }else {

            addImageView.setVisibility(View.GONE);
        }
    }

    private void spinnerSelection() {
        for (int i = 0; i < mCompanyLists.size(); i++) {
            CompanyList companyList = mCompanyLists.get(i);
            if (crewList != null) {
                int crewCmpId = crewList.getCompanyId();
                int cmpId = companyList.getCompanyId();
                Log.e("TAG", "spinnerSelection crewCmpId: "+crewCmpId);
                Log.e("TAG", "spinnerSelection cmpId tenantId: "+cmpId +" "+loginResponse.getUserDetails().getTenantId());
                if (crewCmpId == cmpId) {
//                    companySpinner.setSelection(i);
                    mCompanyList = companyList;

                }
            } else if (crewList == null && companyList.getSelected()) {
                if (companyList.getCompanyId() == loginResponse.getUserDetails().getTenantId())
                    mCompanyList = companyList;
            }
        }
        companyListNameTextView.setText(mCompanyList.getName());

        if (crewList != null) {
            for (int i = 0; i < mTradesList.size(); i++) {
                Trades trades = mTradesList.get(i);
                if (trades != null && crewList != null && crewList.getTradesId() == trades.getTradesId()) {
                    tradeNameTextView.setText(trades.getName());
//                tradeSpinner.setSelection(i);
                    mTrades = trades;
                    tradeNameTextView.setText(mTrades.getName());
                }
            }
        }
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
        } else {
            offlineTextView.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ObjectEvent event) {
        if (event.getObject() != null) {
            if (event.getObjectType() == ObjectEnum.COMPANY_LIST.ordinal()) {
                mCompanyList = (CompanyList) event.getObject();
                companyListNameTextView.setText(mCompanyList.getName());

            } else if (event.getObjectType() == ObjectEnum.TRADES.ordinal()) {
                mTrades = (Trades) event.getObject();
                tradeNameTextView.setText(mTrades.getName());

            }
        }
    }

    private void setCrewListInfo() {
        foremanEditText.setText(String.valueOf(crewList.getForeman()));
        journeymanEditText.setText(String.valueOf(crewList.getJourneyman()));
        apprenticeEditText.setText(String.valueOf(crewList.getApprentice()));
        suptEditText.setText(String.valueOf(crewList.getSupt()));
    }

    @OnClick(R.id.deleteImageView)
    public void onClickdelete() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setMessage(getActivity().getString(R.string.are_you_sure_you_want_to_delete_this_entry));
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getActivity().getString(R.string.cancel), (dialog, which) -> {
            dialog.dismiss();
        });
        alertDialog.setCancelable(false);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getActivity().getString(R.string.ok), (dialog, which) -> {
            alertDialog.dismiss();
            crewList.setDeletedAt(new Date());
            crewList.setIsSync(false);
            mCrewReportRepository.addUpdateCrewList(crewList);
            isUpdated=false;
            getActivity().onBackPressed();
        });
        alertDialog.show();
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(ContextCompat.getColor(getActivity(), R.color.gray_948d8d));
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

    }

    @OnClick(R.id.tradespinnewView)
    public void onClickTradesView() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ObjectDialog tagsDialog = new ObjectDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("obj_type", ObjectEnum.TRADES.ordinal());
        bundle.putParcelable("object", mTrades);
        bundle.putInt("pjProjectId", projectId);
        tagsDialog.setCancelable(false);
        tagsDialog.setArguments(bundle);
        tagsDialog.show(ft, "");
    }

    @OnClick(R.id.companyListView)
    public void onClickCompanyListView() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ObjectDialog tagsDialog = new ObjectDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("tenantId", loginResponse.getUserDetails().getTenantId());
        bundle.putParcelable("object", mCompanyList);
        bundle.putInt("pjProjectId", projectId);
        bundle.putInt("obj_type", ObjectEnum.COMPANY_LIST.ordinal());
        tagsDialog.setCancelable(false);
        tagsDialog.setArguments(bundle);
        tagsDialog.show(ft, "");
    }

    @OnClick(R.id.leftImageView)
    public void clickCancelView() {
        Utils.hideSoftKeyboard(getContext(), backImageView);
        getActivity().onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveTextView:
                if (mTrades == null) {
                    tradeErrorTextView.setText(R.string.please_select_trade);
                } else if (crewList != null && crewList.getTradesId() == mTrades.getTradesId() && crewList.getCompanyId() == mCompanyList.getCompanyId()
                        && crewList.getApprentice() == (TextUtils.isEmpty(apprenticeEditText.getText().toString()) ? 0 : Integer.parseInt(apprenticeEditText.getText().toString()))
                        && crewList.getForeman() == (TextUtils.isEmpty(foremanEditText.getText().toString()) ? 0 : Integer.parseInt(foremanEditText.getText().toString()))
                        && crewList.getJourneyman() == (TextUtils.isEmpty(journeymanEditText.getText().toString()) ? 0 : Integer.parseInt(journeymanEditText.getText().toString()))
                        && crewList.getSupt() == (TextUtils.isEmpty(suptEditText.getText().toString()) ? 0 : Integer.parseInt(suptEditText.getText().toString()))) {
                    isUpdated=false;
                    getActivity().onBackPressed();
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
                    EventBus.getDefault().post(saveCrewList);
                    Utils.hideSoftKeyboard(getContext(), saveTextView);
                    isUpdated=false;
                    getActivity().onBackPressed();
                }
                break;
            case R.id.cancelTextView:
                Utils.hideSoftKeyboard(getContext(), cancelTextView);
                showCancelDialog(getContext());
                break;
            case R.id.deleteTextView:

                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
//                        alertDialog.setTitle(getString(R.string.message));
                alertDialog.setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_entry));
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
                alertDialog.setCancelable(false);
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
                    alertDialog.dismiss();

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
                    EventBus.getDefault().post(deleteCrewList);
                    isUpdated=false;
                    getActivity().onBackPressed();

                });
                alertDialog.show();
                Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_948d8d));
                Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));


                break;
            default:
                break;
        }
    }

    private boolean validEditFields() {
        return journeymanEditText.getText().toString().equals(String.valueOf(crewList.getJourneyman())) &&
                foremanEditText.getText().toString().equals(String.valueOf(crewList.getForeman())) &&
                apprenticeEditText.getText().toString().equals(String.valueOf(crewList.getApprentice())) &&
                suptEditText.getText().toString().equals(String.valueOf(crewList.getSupt())) &&
                companyListNameTextView.getText().toString().equals(crewList.getCompanyName()) &&
                tradeNameTextView.getText().toString().equals(crewList.getTrade());
    }

    private boolean validNewFields() {
        return journeymanEditText.getText().toString().equals("") && foremanEditText.getText().toString().equals("") &&
                apprenticeEditText.getText().toString().equals("") && suptEditText.getText().toString().equals("") &&
                tradeNameTextView.getText().toString().equals("") &&
                mCompanyList.getCompanyId().equals(loginResponse.getUserDetails().getTenantId());
    }

    private void showCancelDialog(Context context) {
        if (getArguments().getLong(crewListMobileIdConstant) != 0) { // means edit
            if (validEditFields()) {// if the fields are same then he has not edited
                isUpdated = false;
                getActivity().onBackPressed();
            } else {
                showDialog(context);
            }
        } else { // new record
            if (validNewFields()) {
                isUpdated = false;
                getActivity().onBackPressed();
            } else {
                showDialog(context);
            }
        }
    }


    public void hideKeyBoard() {
        if (backImageView != null) {
            Utils.hideSoftKeyboard(getContext(), backImageView);

        }
    }

    /**
     * Get the list of trades
     */
    private void callTradesAPI() {
        mFieldPaperWorkProvider.getTrades(new ProviderResult<String>() {
            @Override
            public void success(String result) {

                mTradesList = new ArrayList<>();
                mTradesList.add(null);
                mTradesList.addAll(mFieldPaperWorkRepository.getTrades(loginResponse));
              /*  TradeAdapter tradeAdapter = new TradeAdapter(activity, R.layout.simple_spinner_item, mTradesList);
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
                });*/


                spinnerSelection();
            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(getActivity()).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(getActivity()).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                getActivity().finish();
            }

            @Override
            public void failure(String message) {
//                messageDialog.showMessageAlert(getActivity(), message, getString(R.string.ok));
//                messageDialog.showMessageAlert(FieldPaperWorkActivity.this, getString(R.string.failureMessage), getString(R.string.ok));

            }
        });
    }


    private void callCompanyListAPI() {
        CompanyListRequest companyListRequest = new CompanyListRequest();
        companyListRequest.setProjectId(projectId);
        mFieldPaperWorkProvider.getCompanyList(companyListRequest, new ProviderResult<String>() {
            @Override
            public void success(String result) {
                mCompanyLists = mFieldPaperWorkRepository.getCompanyList(projectId);

                CompanyAdapter companyAdapter = new CompanyAdapter(activity, R.layout.simple_spinner_item, mCompanyLists);
                companyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mCompanyList = mCompanyLists.get(0);

               /* companySpinner.setAdapter(companyAdapter);
                companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mCompanyList = mCompanyLists.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });*/
                spinnerSelection();

            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(getActivity()).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(getActivity()).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                getActivity().finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(getActivity(), message, getString(R.string.ok));
//                messageDialog.showMessageAlert(FieldPaperWorkActivity.this, getString(R.string.failureMessage), getString(R.string.ok));

            }
        });
    }

    void showDialog(Context context) {
        isUpdated = false;
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setMessage(getString(R.string.are_you_sure_you_want_to_exit_without_saving_your_changes));
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
            isUpdated = false;
            dialog.dismiss();
            getActivity().onBackPressed();
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> {
            isUpdated = true;
            dialog.dismiss();
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(ContextCompat.getColor(context, R.color.gray_948d8d));
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
    }

    @Override
    public void onBackPressed() {
        Utils.hideSoftKeyboard(getContext(), cancelTextView);
        showCancelDialog(getContext());
    }

    @Override
    public void onPause() {
        backpressedlistener = null;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        backpressedlistener = this;
    }
}

