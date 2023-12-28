package com.pronovoscm.activity;

import static com.pronovoscm.activity.ProjectAlbumActivity.FILESTORAGE_REQUEST_CODE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pdftron.pdf.utils.Utils;
import com.pronovoscm.R;
import com.pronovoscm.adapter.WorkDetailsListAdapter;
import com.pronovoscm.data.FieldPaperWorkProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.data.WorkDetailsProvider;
import com.pronovoscm.fragments.WorkDetailFragment;
import com.pronovoscm.model.TransactionLogUpdate;
import com.pronovoscm.model.TransactionModuleEnum;
import com.pronovoscm.model.request.validateemail.ValidateEmailRequest;
import com.pronovoscm.model.request.workdetails.WorkDetailsRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.validateemail.ValidateEmailResponse;
import com.pronovoscm.persistence.domain.WorkDetails;
import com.pronovoscm.persistence.repository.WorkDetailsRepository;
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

public class DailyWorkDetailsActivity extends BaseActivity {
    @Inject
    FieldPaperWorkProvider mFieldPaperWorkProvider;
    @Inject
    WorkDetailsProvider mWorkDetailsProvider;
    @Inject
    WorkDetailsRepository mWorkDetailsRepository;

    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.addImageView)
    ImageView addImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
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
    @BindView(R.id.workDetailsRecyclerView)
    RecyclerView workDetailsRecyclerView;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;

    private List<WorkDetails> mWorkDetails;
    private int projectId;
    private MessageDialog messageDialog;
    private Date date;
    private WorkDetailsListAdapter mWorkDetailsListAdapter;
    private WorkDetailFragment workDetailFragment;
    private LoginResponse loginResponse;
    private int canAddWorkDetail;

    @Override
    protected int doGetContentView() {
        return R.layout.work_details_report_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        mWorkDetails = new ArrayList<>();
        messageDialog = new MessageDialog();
        projectId = getIntent().getIntExtra("project_id", 0);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.GONE);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        canAddWorkDetail = loginResponse.getUserDetails().getPermissions().get(0).getCreateProjectDailyReport();
        setAddIcon();
//        titleTextView.setText(getString(R.string.daily_report));
        date = (Date) getIntent().getSerializableExtra("date");
        dayTextView.setText(DateFormatter.formatDayDateForDailyReport(date));
        callValidateEmail(date);
        titleTextView.setText(getString(R.string.work_details));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{getExternalPermission()}, FILESTORAGE_REQUEST_CODE);
        } else {

            /*List<WorkDetails> nonSyncWorkDetailList = mWorkDetailsRepository.getNonSyncWorkDetailList(projectId, date);
            if (nonSyncWorkDetailList.size() > 0) {
                mWorkDetailsProvider.syncWorkDetailsToServer(projectId, date);
            }*/
            callWorkDetailsReport(date);
        }

        workDetailFragment = new WorkDetailFragment();
    }

    private void callWorkDetailsReport(Date date) {
        WorkDetailsRequest workDetailsRequest = new WorkDetailsRequest();
        workDetailsRequest.setProjectId(projectId);
        workDetailsRequest.setReportDate(DateFormatter.formatDateTimeHHForService(date));
        workDetailsRequest.setWorkDetailsReport(new ArrayList<>());
//        workDetailsRequest.setWorkDetailsReport(mWorkDetailsRepository.getNonSyncWorkDetailSyncAttachmentList(projectId, date));
        Log.i(" Activity", "callWorkDetailsReport: ");
        mWorkDetailsProvider.getWorkDetails(workDetailsRequest, date, new ProviderResult<List<WorkDetails>>() {
            @Override
            public void success(List<WorkDetails> workDetails) {
                mWorkDetails.clear();
                mWorkDetails.addAll(workDetails);
                mWorkDetailsListAdapter = new WorkDetailsListAdapter(DailyWorkDetailsActivity.this, mWorkDetails, workDetailsRecyclerView);
                workDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(DailyWorkDetailsActivity.this));
                workDetailsRecyclerView.setAdapter(mWorkDetailsListAdapter);
                if (mWorkDetails == null || mWorkDetails.size() <= 0) {
                    noRecordTextView.setText(R.string.work_details_no_reord_message);
                    noRecordTextView.setVisibility(View.VISIBLE);
                } else {
                    noRecordTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(DailyWorkDetailsActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(DailyWorkDetailsActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(DailyWorkDetailsActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(DailyWorkDetailsActivity.this, message, getString(R.string.ok));

            }
        });
    }


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
                startActivity(new Intent(DailyWorkDetailsActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(DailyWorkDetailsActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(DailyWorkDetailsActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(DailyWorkDetailsActivity.this, message, getString(R.string.ok));
//                messageDialog.showMessageAlert(DailyWeatherReportActivity.this, getString(R.string.failureMessage), getString(R.string.ok));

            }

        });
    }

    @OnClick(R.id.leftImageView)
    public void onBackClick() {
        Utils.hideSoftKeyboard(this, backImageView);
        if (WorkDetailFragment.backpressedlistener != null && WorkDetailFragment.isUpdated){
            WorkDetailFragment.backpressedlistener.onBackPressed();
        } else{
            if (workDetailFragment != null) {
                workDetailFragment.refreshData();
            }
            setAddIcon();
            WorkDetailFragment.isUpdated = true;
            super.onBackPressed();
        }
    }

    @OnClick(R.id.addImageView)
    public void onAddCrewClick() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt("projectId", projectId);
        bundle.putSerializable("workDetailsDate", date);
        workDetailFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.workContainer, workDetailFragment, workDetailFragment.getClass().getSimpleName()).addToBackStack(WorkDetailFragment.class.getName());
//        fragmentTransaction.detach(workDetailFragment);
//        fragmentTransaction.attach(workDetailFragment);
//        fragmentTransaction.setReorderingAllowed(false);
        fragmentTransaction.commit();

       /* FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        workDetailsDialog = new WorkDetailsDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("projectId", projectId);
        workDetailsDialog.setCancelable(false);
        workDetailsDialog.setArguments(bundle);
        workDetailsDialog.show(ft, "");*/


    }

    @Override
    public void onBackPressed() {
        onBackClick();
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

    //TODO: Removed if it is not in used.
    @Subscribe
    public void onAddWorkDetail(WorkDetails workDetails) {
//        if (workDetails.getDeletedAt() != null) {
//            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//            alertDialog.setTitle(getString(R.string.message));
//            alertDialog.setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_entry));
//            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
//                alertDialog.dismiss();
//                updateWorkDetail(workDetails);
//            });
//            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> {
//                workDetails.setDeletedAt(null);
//                workDetails.setIsSync(true);
//                updateWorkDetail(workDetails);
//                dialog.dismiss();
//            });
//            alertDialog.setCancelable(false);
//            alertDialog.show();
//            Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
//            nbutton.setTextColor(ContextCompat.getColor(this, R.color.gray_948d8d));
//            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
//            pbutton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
//
//        } else {
        updateWorkDetail(workDetails);

//        }
    }

    public void updateWorkDetail(WorkDetails details) {
        if (/*details.getWorkDetailsReportId() != 0 ||*/ details.getDeletedAt() != null) {
            mWorkDetailsRepository.updateWorkDetail(details);
        }
        updateWorkDetail();
//        mWorkDetailsProvider.syncWorkDetailsToServer(projectId, date);
    }

    private void updateWorkDetail() {
        mWorkDetails.clear();
//        List<WorkDetails> workDetails = mWorkDetailsRepository.getWorkDetails(projectId, date);
        mWorkDetails.addAll(mWorkDetailsRepository.getWorkDetails(projectId, date));
        // TODO: 19/11/18 need to be optimised
//        mWorkDetailsListAdapter = new WorkDetailsListAdapter(DailyWorkDetailsActivity.this, workDetails, workDetailsRecyclerView);
//        workDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(DailyWorkDetailsActivity.this));
//        workDetailsRecyclerView.setAdapter(mWorkDetailsListAdapter);
        if (mWorkDetails == null || mWorkDetails.size() <= 0) {
            noRecordTextView.setText(R.string.work_details_no_reord_message);
            noRecordTextView.setVisibility(View.VISIBLE);
        } else {
            noRecordTextView.setVisibility(View.GONE);
        }

        if (mWorkDetailsListAdapter != null) {
            mWorkDetailsListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == FILESTORAGE_REQUEST_CODE) {
            //resume tasks needing this permission
            /*List<WorkDetails> nonSyncWorkDetailList = mWorkDetailsRepository.getNonSyncWorkDetailList(projectId, date);
            if (nonSyncWorkDetailList.size() > 0) {
                mWorkDetailsProvider.syncWorkDetailsToServer(projectId, date);
            }*/
            callWorkDetailsReport(date);
        }
        if (workDetailFragment != null && !workDetailFragment.isDetached()) {
            workDetailFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }


    @OnClick(R.id.emailSubmitImageView)
    public void onEmailClick() {
        startActivity(new Intent(this, DailyEmailActivity.class).putExtra("date", date).putExtra("project_id", projectId));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mWorkDetailsListAdapter != null) {
            mWorkDetailsListAdapter.hidePopup();
            mWorkDetailsListAdapter.notifyDataSetChanged();
        }
        super.onConfigurationChanged(newConfig);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TransactionLogUpdate transactionLogUpdate) {
        if (transactionLogUpdate.getTransactionModuleEnum() != null && transactionLogUpdate.getTransactionModuleEnum().equals(TransactionModuleEnum.WORK_DETAIL)) {
            updateWorkDetail();
        }
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

    private void setAddIcon() {
        if (canAddWorkDetail == 1) {
            addImageView.setVisibility(View.VISIBLE);
        } else {
            addImageView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 3115:
                workDetailFragment.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
