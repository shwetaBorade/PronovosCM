package com.pronovoscm.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pronovoscm.R;
import com.pronovoscm.data.FieldPaperWorkProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.datepicker.date.DatePickerFragmentDialog;
import com.pronovoscm.model.request.validateemail.ValidateEmailRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.validateemail.ValidateEmailResponse;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.dialogs.MessageDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class DailyReportActivity extends BaseActivity {

    @Inject
    FieldPaperWorkProvider mFieldPaperWorkProvider;

    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.dayTextView)
    TextView dayTextView;
    //    @BindView(R.id.dateTextView)
//    TextView dateTextView;
    @BindView(R.id.emailSubmitImageView)
    ImageView emailSubmitImageView;
    @BindView(R.id.decreaseDateImageView)
    ImageView decreaseDateImageView;

    @BindView(R.id.increaseDateImageView)
    ImageView increaseDateImageView;

    private int projectId;
    private Date date;
    private MessageDialog messageDialog;
    private LoginResponse loginResponse;
    private int canEditWorkDetail;
    private long mLastClickTime=0;


    @Override
    protected int doGetContentView() {
        return R.layout.daily_report_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        projectId = getIntent().getIntExtra("project_id", 0);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.INVISIBLE);
        titleTextView.setText(getString(R.string.daily_reports));
        messageDialog = new MessageDialog();
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        canEditWorkDetail = loginResponse.getUserDetails().getPermissions().get(0).getEditProjectDailyReport();


        Calendar calendar = Calendar.getInstance();

        Calendar c = new GregorianCalendar(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
//        c.set(Calendar.HOUR_OF_DAY, 0);
        date = c.getTime();
        dayTextView.setText(DateFormatter.formatDateForDailyReport(date));
//        dateTextView.setText(DateFormatter.formatDateForDailyReport(date));
        if (canEditWorkDetail == 1) {
            callValidateEmail(date);
        }

    }


    @OnClick(R.id.leftImageView)
    public void onBackClick() {
        super.onBackPressed();
    }

    @OnClick(R.id.weatherView)
    public void onWeatherViewClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        startActivity(new Intent(this, DailyWeatherReportActivity.class).putExtra("project_id", projectId).putExtra("date", date));

    }

    @OnClick(R.id.crewView)
    public void onCrewViewClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        startActivity(new Intent(this, DailyCrewReportActivity.class).putExtra("project_id", projectId).putExtra("date", date));
    }

    @OnClick(R.id.workDetails)
    public void onWorkDetailsViewClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        startActivity(new Intent(this, DailyWorkDetailsActivity.class).putExtra("project_id", projectId).putExtra("date", date));
    }

    @OnClick(R.id.workImpact)
    public void onWorkImpactViewClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        startActivity(new Intent(this, DailyWorkImpactActivity.class).putExtra("project_id", projectId).putExtra("date", date));
    }

    @OnClick(R.id.dayDateView)
    public void onDayDateViewClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Calendar calendar1 = new GregorianCalendar();
        calendar1.setTime(date);
        DatePickerFragmentDialog datePickerFragmentDialog =
                DatePickerFragmentDialog.newInstance(new DatePickerFragmentDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerFragmentDialog view, int year, int monthOfYear, int dayOfMonth) {
                        calendar1.set(year, monthOfYear, dayOfMonth);

                        if (date != calendar1.getTime()) {
                            emailSubmitImageView.setVisibility(View.GONE);
                            if (canEditWorkDetail == 1) {
                                callValidateEmail(calendar1.getTime());
                            }
                        }
                        date = calendar1.getTime();
                        dayTextView.setText(DateFormatter.formatDateForDailyReport(date));
                        decreaseDateImageView.setVisibility(View.VISIBLE);
                        increaseDateImageView.setVisibility(View.VISIBLE);
                        Calendar calendarMax = new GregorianCalendar();

                        calendarMax.set(Calendar.MONTH, Calendar.DECEMBER);
                        calendarMax.set(Calendar.YEAR, 2100);
                        calendarMax.set(Calendar.DATE, 31);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        if (sdf.format(calendar1.getTime()).equals(sdf.format(calendarMax.getTime()))) {
                            increaseDateImageView.setVisibility(View.GONE);
                        }
                        Calendar calendarMin = new GregorianCalendar();

                        calendarMin.set(Calendar.MONTH, Calendar.JANUARY);
                        calendarMin.set(Calendar.YEAR, 1900);
                        calendarMin.set(Calendar.DATE, 1);
                        if (sdf.format(calendar1.getTime()).equals(sdf.format(calendarMin.getTime()))) {
                            decreaseDateImageView.setVisibility(View.GONE);
                        }


                    }
                }, calendar1);
        datePickerFragmentDialog.setCancelable(false);
        datePickerFragmentDialog.show(getSupportFragmentManager(), DatePickerFragmentDialog.class.getName());
      /*  int mYear = calendar1.get(Calendar.YEAR);
        int mMonth = calendar1.get(Calendar.MONTH);
        int mDay = calendar1.get(Calendar.DATE);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    Calendar calendar = new GregorianCalendar(year,
                            monthOfYear,
                            dayOfMonth);
                    if (date != calendar.getTime()) {
                        emailSubmitImageView.setVisibility(View.GONE);
                        callValidateEmail(calendar.getTime());
                    }
                    date = calendar.getTime();
                    dayTextView.setText(DateFormatter.formatDateForDailyReport(date));
//                    dateTextView.setText(DateFormatter.formatDateForDailyReport(date));
                }, mYear, mMonth, mDay);
        datePickerDialog.show();*/
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
            emailSubmitImageView.setVisibility(View.GONE);
        } else {
            offlineTextView.setVisibility(View.GONE);
            canEditWorkDetail = loginResponse.getUserDetails().getPermissions().get(0).getEditProjectDailyReport();
            if (canEditWorkDetail == 1) {
            emailSubmitImageView.setVisibility(View.VISIBLE);
                callValidateEmail(date);
            }
        }
    }

    @OnClick(R.id.increaseDateImageView)
    public void onIncreaseDate() {
        Calendar calendarMax = new GregorianCalendar();

        calendarMax.set(Calendar.MONTH, Calendar.DECEMBER);
        calendarMax.set(Calendar.YEAR, 2100);
        calendarMax.set(Calendar.DATE, 31);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        calendar.add(Calendar.DATE, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        if (sdf.format(calendar.getTime()).equals(sdf.format(calendarMax.getTime()))) {
            increaseDateImageView.setVisibility(View.GONE);
        }

        if (date != calendar.getTime()) {
            emailSubmitImageView.setVisibility(View.GONE);
            if (canEditWorkDetail == 1) {
                callValidateEmail(calendar.getTime());
            }
        }

        date = calendar.getTime();
        dayTextView.setText(DateFormatter.formatDateForDailyReport(date));
        decreaseDateImageView.setVisibility(View.VISIBLE);

//        dateTextView.setText(DateFormatter.formatDateForDailyReport(date));
    }

    @OnClick(R.id.decreaseDateImageView)
    public void onDecreaseDate() {
        Calendar calendarMin = new GregorianCalendar();

        calendarMin.set(Calendar.MONTH, Calendar.JANUARY);
        calendarMin.set(Calendar.YEAR, 1900);
        calendarMin.set(Calendar.DATE, 1);

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        if (sdf.format(calendar.getTime()).equals(sdf.format(calendarMin.getTime()))) {
            decreaseDateImageView.setVisibility(View.GONE);
        }
        if (date != calendar.getTime()) {
            emailSubmitImageView.setVisibility(View.GONE);
            if (canEditWorkDetail == 1) {
                callValidateEmail(calendar.getTime());
            }
        }
        date = calendar.getTime();
        dayTextView.setText(DateFormatter.formatDateForDailyReport(date));
//        dateTextView.setText(DateFormatter.formatDateForDailyReport(date));
        increaseDateImageView.setVisibility(View.VISIBLE);

    }

    @OnClick(R.id.emailSubmitImageView)
    public void onEmailClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        startActivity(new Intent(this, DailyEmailActivity.class).putExtra("date", date).putExtra("project_id", projectId));
    }

    private void callValidateEmail(Date date) {
        ValidateEmailRequest validateEmailRequest = new ValidateEmailRequest();
        validateEmailRequest.setProjectId(String.valueOf(projectId));
        validateEmailRequest.setReportDate(DateFormatter.formatDateTimeHHForService(date));
        mFieldPaperWorkProvider.getValidateEmail(validateEmailRequest, new ProviderResult<ValidateEmailResponse>() {
            @Override
            public void success(ValidateEmailResponse validateEmailResponse) {
                if (validateEmailResponse.getValidateEmailData().getSent() && canEditWorkDetail == 1) {
                    emailSubmitImageView.setVisibility(View.VISIBLE);
                } else {
                    emailSubmitImageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(DailyReportActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(DailyReportActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(DailyReportActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(DailyReportActivity.this, message, getString(R.string.ok));
//                messageDialog.showMessageAlert(DailyWeatherReportActivity.this, getString(R.string.failureMessage), getString(R.string.ok));

            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (date != null) {
            if (canEditWorkDetail == 1) {
                callValidateEmail(date);
            }
        }
    }
}
