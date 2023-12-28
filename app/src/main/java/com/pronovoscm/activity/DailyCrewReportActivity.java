package com.pronovoscm.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pronovoscm.R;
import com.pronovoscm.adapter.CrewListAdapter;
import com.pronovoscm.data.CrewReportProvider;
import com.pronovoscm.data.FieldPaperWorkProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.fragments.CrewFragment;
import com.pronovoscm.model.TransactionLogUpdate;
import com.pronovoscm.model.TransactionModuleEnum;
import com.pronovoscm.model.request.crewreport.CrewReportRequest;
import com.pronovoscm.model.request.validateemail.ValidateEmailRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.validateemail.ValidateEmailResponse;
import com.pronovoscm.persistence.domain.CrewList;
import com.pronovoscm.persistence.repository.CrewReportRepository;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.dialogs.MessageDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * class for all Crew of Daily Reports.
 *
 * @author GWL
 */
public class DailyCrewReportActivity extends BaseActivity {

    public int position = 0;
    @Inject
    FieldPaperWorkProvider mFieldPaperWorkProvider;
    @Inject
    CrewReportProvider mCrewReportProvider;
    @Inject
    CrewReportRepository mCrewReportRepository;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.addImageView)
    ImageView addImageView;
    @BindView(R.id.emailSubmitImageView)
    ImageView emailSubmitImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.dataOfflineTextView)
    TextView dataOfflineTextView;
    @BindView(R.id.dayTextView)
    TextView dayTextView;
    @BindView(R.id.crewRecyclerView)
    RecyclerView crewRecyclerView;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
    private int projectId;
    private MessageDialog messageDialog;
    private Date date;
    private CrewListAdapter mCrewListAdapter;
    private List<CrewList> mCrewLists;
    private LoginResponse loginResponse;
    private int canAddWorkDetail;

    @Override
    protected int doGetContentView() {
        return R.layout.crew_report_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        messageDialog = new MessageDialog();
        projectId = getIntent().getIntExtra("project_id", 0);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.GONE);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        canAddWorkDetail = loginResponse.getUserDetails().getPermissions().get(0).getCreateProjectDailyReport();

        titleTextView.setText(getString(R.string.crew));
        date = (Date) getIntent().getSerializableExtra("date");
        mCrewLists = new ArrayList<>();
        dayTextView.setText(DateFormatter.formatDayDateForDailyReport(date));
        callValidateEmail(date);
        callCrewReport(date);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (canAddWorkDetail == 1) {
            addImageView.setVisibility(View.VISIBLE);
        }else {
            addImageView.setVisibility(View.GONE);
        }
    }
    /**
     * Get the list of Crew from server and pass not sync crew to the server.
     *
     * @param date
     */
    private void callCrewReport(Date date) {
        CrewReportRequest crewReportRequest = new CrewReportRequest();
        crewReportRequest.setProjectId(projectId);
        crewReportRequest.setReportDate(DateFormatter.formatDateTimeHHForService(date));
        crewReportRequest.setCrewReport(mCrewReportRepository.getNonSyncCrewReports(projectId));
        mCrewReportProvider.getCrewReports(crewReportRequest, date, new ProviderResult<List<CrewList>>() {
            @Override
            public void success(List<CrewList> crewLists) {
                mCrewLists.clear();
                mCrewLists.addAll(crewLists);
                mCrewListAdapter = new CrewListAdapter(mCrewLists, DailyCrewReportActivity.this, getApplicationContext(), crewRecyclerView);
                if (mCrewLists == null || mCrewLists.size() <= 0) {
                    noRecordTextView.setText(getString(R.string.crew_no_reord_message));
                    noRecordTextView.setVisibility(View.VISIBLE);
                } else {
                    noRecordTextView.setVisibility(View.GONE);
                }
                crewRecyclerView.setLayoutManager(new LinearLayoutManager(DailyCrewReportActivity.this));
                crewRecyclerView.setAdapter(mCrewListAdapter);
            }

            @Override
            public void AccessTokenFailure(String message) {

                startActivity(new Intent(DailyCrewReportActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(DailyCrewReportActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(DailyCrewReportActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");

                finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(DailyCrewReportActivity.this, message, getString(R.string.ok));

            }
        });
    }

    /**
     * Get the detail to show email icon or not.
     *
     * @param date
     */
    private void callValidateEmail(Date date) {
        ValidateEmailRequest validateEmailRequest = new ValidateEmailRequest();
        validateEmailRequest.setProjectId(String.valueOf(projectId));
        validateEmailRequest.setReportDate(DateFormatter.formatDateTimeHHForService(date));
        mFieldPaperWorkProvider.getValidateEmail(validateEmailRequest, new ProviderResult<ValidateEmailResponse>() {
            @Override
            public void success(ValidateEmailResponse validateEmailResponse) {
                if (validateEmailResponse.getValidateEmailData().getSent()) {
                    emailSubmitImageView.setVisibility(View.GONE);
                } else {
                    emailSubmitImageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(DailyCrewReportActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(DailyCrewReportActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(DailyCrewReportActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(DailyCrewReportActivity.this, message, getString(R.string.ok));
//                messageDialog.showMessageAlert(DailyWeatherReportActivity.this, getString(R.string.failureMessage), getString(R.string.ok));

            }

        });
    }


    @OnClick(R.id.leftImageView)
    public void onBackClick() {
        if (mCrewListAdapter != null) {
            mCrewListAdapter.hideKeyBoard();
        }
        if (CrewFragment.backpressedlistener != null && CrewFragment.isUpdated) {
            CrewFragment.backpressedlistener.onBackPressed();
        } else {
            CrewFragment.isUpdated = true;
            super.onBackPressed();
        }
    }

    @OnClick(R.id.emailSubmitImageView)
    public void onEmailClick() {
        startActivity(new Intent(this, DailyEmailActivity.class).putExtra("date", date).putExtra("project_id", projectId));
    }

    @OnClick(R.id.addImageView)
    public void onAddCrewClick() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CrewFragment fragment = new CrewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("projectId", projectId);
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.crewContainer, fragment, fragment.getClass().getSimpleName()).addToBackStack(CrewFragment.class.getName());
        fragmentTransaction.commit();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CrewList crewList) {


        updateCrew(crewList);

//            }

//        }
    }

    public void updateCrew(CrewList crewList) {
        crewList.setCreatedAt(date);
        mCrewReportRepository.addUpdateCrewList(crewList);
        updateCrewList();
    }

    private void updateCrewList() {
        mCrewLists.clear();
        mCrewLists.addAll(mCrewReportRepository.getCrewList(projectId, date));

//        mCrewListAdapter = new CrewListAdapter(mCrewLists, DailyCrewReportActivity.this, getApplicationContext(), crewRecyclerView);
        if (mCrewLists == null || mCrewLists.size() <= 0) {
            noRecordTextView.setText(getString(R.string.crew_no_reord_message));
            noRecordTextView.setVisibility(View.VISIBLE);
        } else {
            noRecordTextView.setVisibility(View.GONE);
        }
        mCrewListAdapter.notifyDataSetChanged();
//        crewRecyclerView.setLayoutManager(new LinearLayoutManager(DailyCrewReportActivity.this));
//        crewRecyclerView.setAdapter(mCrewListAdapter);
//        callCrewReport(date);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
            dataOfflineTextView.setVisibility(View.VISIBLE);
            emailSubmitImageView.setVisibility(View.GONE);
        } else {
            dataOfflineTextView.setVisibility(View.GONE);
            offlineTextView.setVisibility(View.GONE);
            callValidateEmail(date);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TransactionLogUpdate transactionLogUpdate) {
        if (transactionLogUpdate.getTransactionModuleEnum() != null && transactionLogUpdate.getTransactionModuleEnum().equals(TransactionModuleEnum.CREW)) {
            updateCrewList();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
//        mCrewLists.get(position).setPopOverOpen(false);
//        mCrewListAdapter.hidePopOver();
        if (mCrewListAdapter != null) {
            mCrewListAdapter.hidePopup();
            mCrewListAdapter.notifyDataSetChanged();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        onBackClick();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }
    public void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

}
