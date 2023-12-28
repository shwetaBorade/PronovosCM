package com.pronovoscm.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pronovoscm.R;
import com.pronovoscm.adapter.WeatherWidgetAdapter;
import com.pronovoscm.data.FieldPaperWorkProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.data.WeatherReportProvider;
import com.pronovoscm.model.TransactionLogUpdate;
import com.pronovoscm.model.TransactionModuleEnum;
import com.pronovoscm.model.request.forecast.ForecastRequest;
import com.pronovoscm.model.request.weatherreport.WeatherReportRequest;
import com.pronovoscm.model.request.weatherreport.WeatherReports;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.WeatherReport;
import com.pronovoscm.persistence.domain.WeatherWidget;
import com.pronovoscm.persistence.repository.WeatherReportRepository;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.WeatherConditionEvent;
import com.pronovoscm.utils.dialogs.MessageDialog;
import com.pronovoscm.utils.dialogs.WeatherConditionDialog;
import com.pronovoscm.utils.library.AutoLabelUI;
import com.pronovoscm.utils.library.AutoLabelUISettings;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;


public class DailyWeatherReportActivity extends BaseActivity {
    @Inject
    WeatherReportProvider mWeatherReportProvider;
    @Inject
    WeatherReportRepository mWeatherReportRepository;
    @Inject
    FieldPaperWorkProvider mFieldPaperWorkProvider;

    @BindView(R.id.leftImageView)
    ImageView backImageView;
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
    @BindView(R.id.weatherHintTextView)
    TextView weatherHintTextView;
    @BindView(R.id.cancelTextView)
    TextView cancelTextView;
    @BindView(R.id.saveTextView)
    TextView saveTextView;
    @BindView(R.id.noteEditText)
    EditText noteEditText;
    @BindView(R.id.yesImpactRadioButton)
    RadioButton yesImpactRadioButton;
    @BindView(R.id.noImpactRadioButton)
    RadioButton noImpactRadioButton;
    @BindView(R.id.keywordAutoLabelUI)
    AutoLabelUI keywordAutoLabelUI;
    @BindView(R.id.widgetView)
    LinearLayout widgetView;

    @BindView(R.id.recyclerViewHourlyForecast)
    RecyclerView hourlyForecastRecyclerView;
    private ArrayList<WeatherWidget> mWidgetArrayList = null;
    private WeatherWidgetAdapter mHourlyForecastAdapter;


    private String weatherImpact = "";
    private int projectId;
    private MessageDialog messageDialog;
    private Date date;
    private ArrayList<String> mWeatherConditions;
    private int canAddWorkDetail;

    @Override
    protected int doGetContentView() {
        return R.layout.weather_report_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        canAddWorkDetail = loginResponse.getUserDetails().getPermissions().get(0).getCreateProjectDailyReport();

        messageDialog = new MessageDialog();
        projectId = getIntent().getIntExtra("project_id", 0);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.INVISIBLE);
        titleTextView.setText(getString(R.string.weather));
        date = (Date) getIntent().getSerializableExtra("date");
        dayTextView.setText(DateFormatter.formatDayDateForDailyReport(date));
        setAutoLabelUISettings();
        initUI();
        mWeatherConditions = new ArrayList<>();
        callWeatherConditionAPI();
        callWeatherReportAPI(date);
        if (canAddWorkDetail != 1) {
            keywordAutoLabelUI.setShowCross(false);
        }
        callWeatherForecastAPI(date);
        setListeners();
    }

    //TODO: Remove unused code.
    private void initUI() {
        mWidgetArrayList = new ArrayList<>();

        if (canAddWorkDetail != 1) {
            yesImpactRadioButton.setClickable(false);
            noImpactRadioButton.setClickable(false);
            noteEditText.setKeyListener(null);
            saveTextView.setVisibility(View.GONE);
        }
    }

    private void callWeatherReportAPI(Date date) {
        WeatherReportRequest weatherReportRequest = new WeatherReportRequest();
        weatherReportRequest.setProjectId(String.valueOf(projectId));
        weatherReportRequest.setWeatherReports(new ArrayList<>());
        List<WeatherReports> weatherReportList = mWeatherReportRepository.getNonSyncWeatherReports(projectId);
        if (weatherReportList != null) {
            weatherReportRequest.setWeatherReports(weatherReportList);
        }
        weatherReportRequest.setReportDate(DateFormatter.formatDateTimeHHForService(date));
        mWeatherReportProvider.getWeatherReport(weatherReportRequest, date, new ProviderResult<WeatherReport>() {
            @Override
            public void success(WeatherReport weatherReport) {
                noteEditText.setText("");
                if (weatherReport != null) {
                    noteEditText.setText(weatherReport.getNotes() != null ? weatherReport.getNotes() : "");
                    if (weatherReport.getImpact() != null) {
                        boolean yes = weatherReport.getImpact().equalsIgnoreCase("Yes");
                        boolean no = weatherReport.getImpact().equalsIgnoreCase("No");
                        if (!yes && !yesImpactRadioButton.isChecked()) {
                            yesImpactRadioButton.setChecked(false);

                        } else {
                            yesImpactRadioButton.setChecked(yes);
                            weatherImpact = "Yes";
                        }
                        if (!no && !noImpactRadioButton.isChecked()) {
                            noImpactRadioButton.setChecked(false);

                        } else {
                            noImpactRadioButton.setChecked(no);
                            weatherImpact = "No";
                        }

                    }
                    if (weatherReport.getConditions() != null && !TextUtils.isEmpty(weatherReport.getConditions())) {
                        mWeatherConditions = new ArrayList<>(Arrays.asList(weatherReport.getConditions().split(",")));
                        addLabels(mWeatherConditions);
                    }
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(DailyWeatherReportActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(DailyWeatherReportActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(DailyWeatherReportActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {

                noteEditText.setText("");
                yesImpactRadioButton.setChecked(false);
                noImpactRadioButton.setChecked(false);
                messageDialog.showMessageAlert(DailyWeatherReportActivity.this, message, getString(R.string.ok));
//                messageDialog.showMessageAlert(DailyWeatherReportActivity.this, getString(R.string.failureMessage), getString(R.string.ok));

            }
        });
    }

    private void callWeatherConditionAPI() {
        mWeatherReportProvider.getWeatherCondition(new ProviderResult<String>() {
            @Override
            public void success(String result) {

            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(DailyWeatherReportActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(DailyWeatherReportActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(DailyWeatherReportActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(DailyWeatherReportActivity.this, message, getString(R.string.ok));
//                messageDialog.showMessageAlert(DailyWeatherReportActivity.this, getString(R.string.failureMessage), getString(R.string.ok));

            }
        });
    }

    private void callWeatherForecastAPI(Date date) {
        ForecastRequest forecastRequest = new ForecastRequest();
        forecastRequest.setProjectId(projectId);
        forecastRequest.setReportDate(DateFormatter.formatDate(date));

        mWeatherReportProvider.getWeatherForecast(forecastRequest, date, new ProviderResult<List<WeatherWidget>>() {
            @Override
            public void success(List<WeatherWidget> result) {
                mWidgetArrayList.clear();
//        Collections.reverse(hourlyForecast);
                mWidgetArrayList.addAll(result);
                if (mWidgetArrayList.size() > 0) {
                    widgetView.setVisibility(View.VISIBLE);
                } else {
                    widgetView.setVisibility(View.GONE);
                }
                hourlyForecastRecyclerView.setHasFixedSize(true);
                hourlyForecastRecyclerView.setLayoutManager(new LinearLayoutManager(DailyWeatherReportActivity.this, LinearLayoutManager.HORIZONTAL, false));
//                DividerItemDecoration itemDecor = new DividerItemDecoration(getApplicationContext(), HORIZONTAL);
//                hourlyForecastRecyclerView.addItemDecoration(itemDecor);
                mHourlyForecastAdapter = new WeatherWidgetAdapter(DailyWeatherReportActivity.this, mWidgetArrayList, null);
                hourlyForecastRecyclerView.setAdapter(mHourlyForecastAdapter);

            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(DailyWeatherReportActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(DailyWeatherReportActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(DailyWeatherReportActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(DailyWeatherReportActivity.this, message, getString(R.string.ok));
//                messageDialog.showMessageAlert(DailyWeatherReportActivity.this, getString(R.string.failureMessage), getString(R.string.ok));

            }
        });
    }


    @OnClick(R.id.leftImageView)
    public void onBackClick() {
        onBackPressed();
    }

    @OnClick(R.id.allKeyword)
    public void onAddWeatherConditionClick() {
        if (canAddWorkDetail == 1) {
            openWeatherConditionDialog();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WeatherConditionEvent event) {
        if (event.getWeatherConditions() != null) {
            addLabels(event.getWeatherConditions());
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
            dataOfflineTextView.setVisibility(View.VISIBLE);
        } else {
            dataOfflineTextView.setVisibility(View.GONE);
            offlineTextView.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TransactionLogUpdate transactionLogUpdate) {
        if (transactionLogUpdate.getTransactionModuleEnum() != null && transactionLogUpdate.getTransactionModuleEnum().equals(TransactionModuleEnum.WEATHER)) {
            updateWeatherData(transactionLogUpdate);
        }

    }

    private void updateWeatherData(TransactionLogUpdate transactionLogUpdate) {
        noteEditText.setText("");
        if (transactionLogUpdate.getWeatherReport() != null) {
            WeatherReport weatherReport = transactionLogUpdate.getWeatherReport();
            if (weatherReport != null) {
                noteEditText.setText(weatherReport.getNotes() != null ? weatherReport.getNotes() : "");
                if (weatherReport.getImpact() != null) {
                    boolean yes = weatherReport.getImpact().equalsIgnoreCase("Yes");
                    boolean no = weatherReport.getImpact().equalsIgnoreCase("No");
                    if (!yes && !yesImpactRadioButton.isChecked()) {
                        yesImpactRadioButton.setChecked(false);

                    } else {
                        yesImpactRadioButton.setChecked(yes);
                        weatherImpact = "Yes";
                    }
                    if (!no && !noImpactRadioButton.isChecked()) {
                        noImpactRadioButton.setChecked(false);

                    } else {
                        noImpactRadioButton.setChecked(no);
                        weatherImpact = "No";
                    }

                }
                if (weatherReport.getConditions() != null && !TextUtils.isEmpty(weatherReport.getConditions())) {
                    mWeatherConditions = new ArrayList<>(Arrays.asList(weatherReport.getConditions().split(",")));
                    addLabels(mWeatherConditions);
                }
            }
        }
    }

    private void setAutoLabelUISettings() {
        boolean showCrossIcon = canAddWorkDetail == 1;
        Log.i("", "setAutoLabelUISettings: " + showCrossIcon);
        AutoLabelUISettings autoLabelUISettings =
                new AutoLabelUISettings.Builder()
                        .withIconCross(R.drawable.ic_close_white)
                        .withMaxLabels(6)
                        .withShowCross(showCrossIcon)
                        .withLabelsClickables(true)
                        .withTextColor(android.R.color.black)
                        .withLabelPadding(30)
                        .build();

        keywordAutoLabelUI.setSettings(autoLabelUISettings);
    }


    private void setListeners() {
        keywordAutoLabelUI.setOnLabelsCompletedListener(() -> {
        });

        keywordAutoLabelUI.setOnRemoveLabelListener((removedLabel, position) -> {
            keywordAutoLabelUI.clear();
            mWeatherConditions.remove(position);
            addLabels(mWeatherConditions);
        });

        keywordAutoLabelUI.setOnLabelsEmptyListener(() -> {
        });

        keywordAutoLabelUI.setOnLabelClickListener((labelClicked, position) -> {
            if (position == 5) {
                openWeatherConditionDialog();
            }
        });

    }

    private void addLabels(ArrayList<String> weatherConditions) {
        keywordAutoLabelUI.clear();

        mWeatherConditions = weatherConditions;

        for (int i = 0; i < mWeatherConditions.size(); i++) {
            keywordAutoLabelUI.addLabel(mWeatherConditions.get(i), i);
        }
        if (mWeatherConditions.size() == 0) {
            weatherHintTextView.setVisibility(View.VISIBLE);
        } else {
            weatherHintTextView.setVisibility(View.INVISIBLE);
        }
        if (mWeatherConditions.size() >= 6) {
            keywordAutoLabelUI.getLabel(5).setText("View More");
            keywordAutoLabelUI.getLabel(5).setIcon(R.drawable.ic_add_white, false);
        }
    }

    private void openWeatherConditionDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        WeatherConditionDialog tagsDialog = new WeatherConditionDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("selected_weather_conditions", mWeatherConditions);
        bundle.putInt("canAddWorkDetail", canAddWorkDetail);
        tagsDialog.setCancelable(false);
        tagsDialog.setArguments(bundle);
        tagsDialog.show(ft, "");
    }

    @OnClick(R.id.cancelTextView)
    public void onCancelClick() {
        onBackPressed();
    }

    @OnClick(R.id.yesImpactRadioButton)
    public void onYesImpactRadioButtonClick() {
        if (yesImpactRadioButton.isChecked()) {
            weatherImpact = "Yes";
        }
    }

    @OnClick(R.id.noImpactRadioButton)
    public void onNoImpactRadioButtonClick() {
        if (noImpactRadioButton.isChecked()) {
            weatherImpact = "No";
        }
    }

    @OnClick({R.id.saveTextView})
    public void onSaveClick() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && imm.isActive()) {
                imm.hideSoftInputFromWindow(saveTextView.getWindowToken(), 0);
            }
        } catch (Exception ignored) {
        }
        WeatherReport wr = mWeatherReportRepository.getWeatherReports(projectId, date);

        if (wr == null && mWeatherConditions.size() == 0 && weatherImpact.equals("") && TextUtils.isEmpty(noteEditText.getText().toString())) {
        } else if (wr != null && wr.getConditions().equals(TextUtils.join(",", mWeatherConditions)) && wr.getImpact().equalsIgnoreCase(weatherImpact) && wr.getNotes().equals(noteEditText.getText().toString())) {
        } else {
            WeatherReports weatherReports = new WeatherReports();
            weatherReports.setNotes(noteEditText.getText().toString());
            weatherReports.setConditions(android.text.TextUtils.join(",", mWeatherConditions));
            weatherReports.setImpact(weatherImpact);
            weatherReports.setProjectId(String.valueOf(projectId));
            weatherReports.setReportDate(DateFormatter.formatDateTimeHHForService(date));

            com.pronovoscm.model.response.weatherreport.WeatherReports weatherReport = new com.pronovoscm.model.response.weatherreport.WeatherReports();
            weatherReport.setConditions(weatherReports.getConditions());
            weatherReport.setImpact(weatherReports.getImpact());
            weatherReport.setNotes(weatherReports.getNotes());
            weatherReport.setCreatedAt(weatherReports.getReportDate());
            weatherReport.setPjProjectsId(projectId);
            mWeatherReportRepository.doUpdateWeatherReportTable(weatherReport, projectId, date, false);
//            callWeatherReportAPI(date);
        }
        super.onBackPressed();
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
//                if (!emailIdEt.hasWindowFocus() && !passwordEt.hasWindowFocus()){
                hideKeyboard(this);
//                }
        }
        return super.dispatchTouchEvent(ev);
    }
    public void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && imm.isActive()) {
                imm.hideSoftInputFromWindow(backImageView.getWindowToken(), 0);
            }
        } catch (Exception ignored) {
        }
        WeatherReport weatherReport = mWeatherReportRepository.getWeatherReports(projectId, date);
        if (weatherReport == null && mWeatherConditions.size() == 0 && weatherImpact.equals("") && TextUtils.isEmpty(noteEditText.getText().toString())) {
            super.onBackPressed();
        } else if (weatherReport != null && weatherReport.getConditions().equals(TextUtils.join(",", mWeatherConditions)) && weatherReport.getImpact().equalsIgnoreCase(weatherImpact) && weatherReport.getNotes().equals(noteEditText.getText().toString())) {
            super.onBackPressed();
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//            alertDialog.setTitle(getString(R.string.message));
            alertDialog.setMessage(getString(R.string.are_you_sure_you_want_to_exit_without_saving_your_changes));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> super.onBackPressed());
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
            alertDialog.setCancelable(false);
            alertDialog.show();
            Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(ContextCompat.getColor(this, R.color.gray_948d8d));
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }
}
