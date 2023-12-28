package com.pronovoscm.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
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
import com.pronovoscm.adapter.WorkImpactListAdapter;
import com.pronovoscm.data.FieldPaperWorkProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.data.WorkImpactProvider;
import com.pronovoscm.fragments.WorkImpactFragment;
import com.pronovoscm.model.TransactionLogUpdate;
import com.pronovoscm.model.TransactionModuleEnum;
import com.pronovoscm.model.request.validateemail.ValidateEmailRequest;
import com.pronovoscm.model.request.workimpact.WorkImpactRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.validateemail.ValidateEmailResponse;
import com.pronovoscm.persistence.domain.WorkImpact;
import com.pronovoscm.persistence.repository.WorkImpactRepository;
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

import static com.pronovoscm.activity.ProjectAlbumActivity.FILESTORAGE_REQUEST_CODE;

public class DailyWorkImpactActivity extends BaseActivity {
    @Inject
    FieldPaperWorkProvider mFieldPaperWorkProvider;
    @Inject
    WorkImpactProvider mWorkImpactProvider;
    @Inject
    WorkImpactRepository mmWorkImpactRepository;

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
    RecyclerView workImpactsRecyclerView;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
    private List<WorkImpact> mWorkImpacts;
    private int projectId;
    private MessageDialog messageDialog;
    private Date date;
    private WorkImpactListAdapter mWorkImpactListAdapter;
    private WorkImpactFragment workImpactFragment;
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
        mWorkImpacts = new ArrayList<>();
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
        titleTextView.setText(getString(R.string.work_impact));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{getExternalPermission(), Manifest.permission.CAMERA}, FILESTORAGE_REQUEST_CODE);
        } else {

            /*List<WorkImpact> nonSyncWorkImpactList = mmWorkImpactRepository.getNonSyncWorkImpactList(projectId, date);
            if (nonSyncWorkImpactList.size() > 0) {
                mWorkImpactProvider.syncWorkImpactsToServer(projectId, date);
            }*/
            callWorkImpactReport(date);
        }
    }

    private void callWorkImpactReport(Date date) {
        WorkImpactRequest workImpactRequest = new WorkImpactRequest();
        workImpactRequest.setProjectId(projectId);
        workImpactRequest.setReportDate(DateFormatter.formatDateTimeHHForService(date));
        workImpactRequest.setWorkImpactsReport(mmWorkImpactRepository.getNonSyncWorkImpactyncAttachmentList(projectId, date));
        mWorkImpactProvider.getWorkImpacts(workImpactRequest, date, new ProviderResult<List<WorkImpact>>() {
            @Override
            public void success(List<WorkImpact> workImpacts) {
                mWorkImpacts.clear();
                mWorkImpacts.addAll(workImpacts);
                mWorkImpactListAdapter = new WorkImpactListAdapter(DailyWorkImpactActivity.this, mWorkImpacts, workImpactsRecyclerView);
                workImpactsRecyclerView.setLayoutManager(new LinearLayoutManager(DailyWorkImpactActivity.this));
                workImpactsRecyclerView.setAdapter(mWorkImpactListAdapter);
                if (mWorkImpacts == null || mWorkImpacts.size() <= 0) {
                    noRecordTextView.setText(R.string.work_impacts_no_reord_message);
                    noRecordTextView.setVisibility(View.VISIBLE);
                } else {
                    noRecordTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(DailyWorkImpactActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(DailyWorkImpactActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(DailyWorkImpactActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(DailyWorkImpactActivity.this, message, getString(R.string.ok));

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
                startActivity(new Intent(DailyWorkImpactActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(DailyWorkImpactActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(DailyWorkImpactActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(DailyWorkImpactActivity.this, message, getString(R.string.ok));
//                messageDialog.showMessageAlert(DailyWeatherReportActivity.this, getString(R.string.failureMessage), getString(R.string.ok));

            }

        });
    }

    @OnClick(R.id.leftImageView)
    public void onBackClick() {
        Utils.hideSoftKeyboard(this, backImageView);
        if (WorkImpactFragment.backpressedlistener != null  && WorkImpactFragment.isUpdated) {
            WorkImpactFragment.backpressedlistener.onBackPressed();
        } else {
            if (workImpactFragment != null) {
                workImpactFragment.refreshData();
            }
            setAddIcon();
            WorkImpactFragment.isUpdated = true;
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        onBackClick();
    }


    @OnClick(R.id.addImageView)
    public void onAddWorkImpactClick() {
//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        WorkImpactDialog workImpactDialog = new WorkImpactDialog();
//        Bundle bundle = new Bundle();
//        bundle.putInt("projectId", projectId);
//        bundle.putSerializable("workImpactDate", date);
//        workImpactDialog.setCancelable(false);
//        workImpactDialog.setArguments(bundle);
//        workImpactDialog.show(ft, "");
        workImpactFragment = new WorkImpactFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt("projectId", projectId);
        bundle.putSerializable("workImpactDate", date);
        workImpactFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.workContainer, workImpactFragment, workImpactFragment.getClass().getSimpleName()).addToBackStack(WorkImpactFragment.class.getName());
        fragmentTransaction.commit();


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
    public void onAddWorkImpact(WorkImpact workImpact) {
//        if (workImpact.getDeletedAt() != null) {
//            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//            alertDialog.setTitle(getString(R.string.message));
//            alertDialog.setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_entry));
//            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
//                alertDialog.dismiss();
//                updateWorkImpact(workImpact);
//
//            });
//            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> {
//                workImpact.setDeletedAt(null);
//                workImpact.setIsSync(true);
//                updateWorkImpact(workImpact);
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
        updateWorkImpact(workImpact);

//        }
    }

    public void updateWorkImpact(WorkImpact workImpact) {
        if (workImpact.getWorkImpactReportId() != 0 || workImpact.getDeletedAt() != null) {
            mmWorkImpactRepository.updateWorkImpact(workImpact);
        }
        updateWorkImpact();
//        mWorkImpactProvider.syncWorkImpactsToServer(projectId, date);
    }

    private void updateWorkImpact() {
        mWorkImpacts.clear();
        mWorkImpacts.addAll(mmWorkImpactRepository.getWorkImpact(projectId, date));
//        List<WorkImpact> workImpacts = mmWorkImpactRepository.getWorkImpact(projectId, date);
        // TODO: 19/11/18 need to be optimised
//        mWorkImpactListAdapter = new WorkImpactListAdapter(DailyWorkImpactActivity.this, workImpacts, workImpactsRecyclerView);

        if (mWorkImpacts == null || mWorkImpacts.size() <= 0) {
            noRecordTextView.setText(R.string.work_impacts_no_reord_message);
            noRecordTextView.setVisibility(View.VISIBLE);
        } else {
            noRecordTextView.setVisibility(View.GONE);
        }

        if (mWorkImpactListAdapter != null) {
            mWorkImpactListAdapter.notifyDataSetChanged();
        }
//        mWorkImpactProvider.syncWorkImpactsToServer(projectId, date);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == FILESTORAGE_REQUEST_CODE) {
            //resume tasks needing this permission
            List<WorkImpact> nonSyncWorkImpactList = mmWorkImpactRepository.getNonSyncWorkImpactList(projectId, date);
            if (nonSyncWorkImpactList.size() > 0) {
                mWorkImpactProvider.syncWorkImpactsToServer(projectId, date);
            }
            if (workImpactFragment != null && workImpactFragment.isAdded()) {
                workImpactFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            } else {
                callWorkImpactReport(date);
            }
        }
    }


    @OnClick(R.id.emailSubmitImageView)
    public void onEmailClick() {
        startActivity(new Intent(this, DailyEmailActivity.class).putExtra("date", date).putExtra("project_id", projectId));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mWorkImpactListAdapter != null) {
            mWorkImpactListAdapter.hidePopUp();
            mWorkImpactListAdapter.notifyDataSetChanged();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TransactionLogUpdate transactionLogUpdate) {
        if (transactionLogUpdate.getTransactionModuleEnum() != null && transactionLogUpdate.getTransactionModuleEnum().equals(TransactionModuleEnum.WORK_IMPACT)) {
            updateWorkImpact();
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
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
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
}
