package com.pronovoscm.activity;


import static android.os.AsyncTask.SERIAL_EXECUTOR;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pronovoscm.BuildConfig;
import com.pronovoscm.R;
import com.pronovoscm.adapter.ProjectAdapter;
import com.pronovoscm.adapter.RegionAdapter;
import com.pronovoscm.data.FieldPaperWorkProvider;
import com.pronovoscm.data.ProjectFormProvider;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.data.WeatherReportProvider;
import com.pronovoscm.model.request.projects.ProjectVersionsCheck;
import com.pronovoscm.model.request.projects.ProjectsRequest;
import com.pronovoscm.model.response.cssjs.CSSJSResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.projects.UsersProject;
import com.pronovoscm.persistence.domain.FormAssets;
import com.pronovoscm.persistence.domain.ImageTag;
import com.pronovoscm.persistence.domain.PjProjects;
import com.pronovoscm.persistence.domain.RegionsTable;
import com.pronovoscm.persistence.repository.BackupSyncRepository;
import com.pronovoscm.persistence.repository.ProjectFormRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.backupsync.BackupSyncProgressStartUpdate;
import com.pronovoscm.utils.backupsync.BackupSyncProgressUpdate;
import com.pronovoscm.utils.dialogs.MessageDialog;
import com.pronovoscm.utils.ui.LoadTextFilesInBackground;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ProjectsActivity extends BaseActivity implements View.OnClickListener, RegionAdapter.selectRegion {
    private static final String TAG = "ProjectsActivity";
    @Inject
    ProjectsProvider projectsProvider;
    @Inject
    WeatherReportProvider mWeatherReportProvider;
    @Inject
    FieldPaperWorkProvider mFieldPaperWorkProvider;
    @Inject
    ProjectFormRepository mprojectFormRepository;

    @Inject
    ProjectFormProvider mprojectFormProvider;
    @Inject
    BackupSyncRepository backupSyncRepository;

    @BindView(R.id.projectsRecyclerView)
    RecyclerView projectsRecyclerView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.leftImageView)
    ImageView logoutImageView;

    @BindView(R.id.syncImageView)
    ImageView syncImageView;

    @BindView(R.id.syncProgressView)
    ProgressBar syncProgressView;

    @BindView(R.id.rightImageView)
    ImageView filterImageView;
    @BindView(R.id.searchProjectEditText)
    EditText searchProjectEditText;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    private ProjectAdapter mProjectAdapter;
    private RegionAdapter mRegionAdapter;
    private List<PjProjects> projectList;
    private LoginResponse loginResponse;
    private PopupWindow mPopupWindow;
    private int regionId = -1;
    private int currentRegionId = -1;
    private MessageDialog messageDialog;

    @Override
    protected int doGetContentView() {
        return R.layout.projects_view;
    }

    TextWatcher searchProjectEditTextTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            projectList.clear();

            projectList.addAll(projectsProvider.getSearchProject(currentRegionId, s.toString(), loginResponse));
            mProjectAdapter.notifyDataSetChanged();
//                projectsRecyclerView.setAdapter(mProjectAdapter);


        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    AlertDialog updateAlertDialog = null;

    private void checkUpdateSyncStatus() {

        boolean isSyncRequired = SharedPref.getInstance(this).readBooleanPrefs(SharedPref.SYNC_OLD_FILES_REQUIRED);
        boolean isSyncRunning = SharedPref.getInstance(this).readBooleanPrefs(SharedPref.SYNC_OLD_FILES_RUNNING);
        if (isSyncRequired) {
            if (!backupSyncRepository.isBackupSyncTableEmpty())
                syncImageView.setVisibility(View.VISIBLE);
            syncProgressView.setVisibility(View.GONE);
            syncImageView.setOnClickListener(this::onClick);
        }
        /* if(isSyncRunning){
            syncImageView.setVisibility(View.GONE);
            syncProgressView.setVisibility(View.VISIBLE);
        }*/
    }

    private String getDevelopmentAssetString() {
        return "{\n" +
                "  \"status\": 200,\n" +
                "  \"message\": \"Form asset last update.\",\n" +
                "  \"data\": {\n" +
                "    \"form_assets\": [\n" +
                "      {\n" +
                "        \"file_name\": \"bootstrap.min.css\",\n" +
                "        \"file_type\": \"css\",\n" +
                "        \"file_path\": \"http:\\/\\/poc.pronovos.com\\/assets\\/newtheme\\/css\\/bootstrap.min.css\",\n" +
                "        \"updated_at\": \"2020-12-10 11:54:45\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"file_name\": \"formio.full.min.css\",\n" +
                "        \"file_type\": \"css\",\n" +
                "        \"file_path\": \"http:\\/\\/poc.pronovos.com\\/assets\\/newtheme\\/js\\/form-builder\\/formio.full.min.css\",\n" +
                "        \"updated_at\": \"2019-11-21 11:54:45\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"file_name\": \"formio.custom.css\",\n" +
                "        \"file_type\": \"css\",\n" +
                "        \"file_path\": \"http:\\/\\/poc.pronovos.com\\/assets\\/newtheme\\/css\\/formio.custom.css\",\n" +
                "        \"updated_at\": \"2020-12-10 06:45:02\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"file_name\": \"jquery-2.1.1.min.js\",\n" +
                "        \"file_type\": \"js\",\n" +
                "        \"file_path\": \"http:\\/\\/poc.pronovos.com\\/assets\\/newtheme\\/js\\/libs\\/jquery-2.1.1.min.js\",\n" +
                "        \"updated_at\": \"2019-11-21 11:54:45\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"file_name\": \"jquery-ui.min.js\",\n" +
                "        \"file_type\": \"js\",\n" +
                "        \"file_path\": \"https:\\/\\/poc.pronovos.com\\/assets\\/newtheme\\/js\\/libs\\/jquery-ui-1.10.3.min.js\",\n" +
                "        \"updated_at\": \"2020-01-11 07:46:57\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"file_name\": \"formio.full.min.js\",\n" +
                "        \"file_type\": \"js\",\n" +
                "        \"file_path\": \"http:\\/\\/poc.pronovos.com\\/assets\\/newtheme\\/js\\/form-builder\\/formio.full.min.js\",\n" +
                "        \"updated_at\": \"2020-12-10 10:57:02\"\n" +
                "      },\n"
                + "      {\n" +
                "        \"file_name\": \"font-awesome.min.css\",\n" +
                "        \"file_type\": \"css\",\n" +
                "        \"file_path\": \"\",\n" +
                "        \"updated_at\": \"2019-09-05 15:27:27\"\n" +
                "      },\n" +
                " {\n" +
                " \"file_name\": \"nestedSelect2.css\",\n" +
                " \"file_type\": \"css\",\n" +
                "  \"file_path\": \"\",\n" +
                " \"updated_at\": \"2020-12-14 00:27:27\"\n" +
                " },\n" +
                " {\n" +
                " \"file_name\": \"nestedselect2.full.js\",\n" +
                " \"file_type\": \"js\",\n" +
                " \"file_path\": \"http:\\/\\/poc.pronovos.com\\/assets\\/newtheme\\/js\\/plugin\\/nestedSelect2\\/nestedselect2.full.js\",\n" +
                " \"updated_at\": \"2020-12-14 00:45:02\"\n" +
                " },\n" +
                "      {\n" +
                "        \"file_name\": \"custom.project.form.js\",\n" +
                "        \"file_type\": \"js\",\n" +
                "        \"file_path\": \"http:\\/\\/poc.pronovos.com\\/assets\\/newtheme\\/js\\/form-builder\\/custom.project.form.js\",\n" +
                "        \"updated_at\": \"2020-12-14 06:45:02\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"responseCode\": 101,\n" +
                "    \"responseMsg\": \"Form asset last update.\"\n" +
                "  }\n" +
                "}";
    }

    private String getProductionAssetString() {
        return "{\n" +
                "  \"status\": 200,\n" +
                "  \"message\": \"Form asset last update.\",\n" +
                "  \"data\": {\n" +
                "    \"form_assets\": [\n" +
                "      {\n" +
                "        \"file_name\": \"bootstrap.min.css\",\n" +
                "        \"file_type\": \"css\",\n" +
                "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/css\\/bootstrap.min.css\",\n" +
                "        \"updated_at\": \"2020-12-10 11:54:45\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"file_name\": \"formio.full.min.css\",\n" +
                "        \"file_type\": \"css\",\n" +
                "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/js\\/form-builder\\/formio.full.min.css\",\n" +
                "        \"updated_at\": \"2019-11-21 11:54:45\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"file_name\": \"formio.custom.css\",\n" +
                "        \"file_type\": \"css\",\n" +
                "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/css\\/formio.custom.css\",\n" +
                "        \"updated_at\": \"2020-12-10 06:45:02\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"file_name\": \"jquery-2.1.1.min.js\",\n" +
                "        \"file_type\": \"js\",\n" +
                "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/js\\/libs\\/jquery-2.1.1.min.js\",\n" +
                "        \"updated_at\": \"2019-11-21 11:54:45\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"file_name\": \"formio.full.min.js\",\n" +
                "        \"file_type\": \"js\",\n" +
                "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/js\\/form-builder\\/formio.full.min.js\",\n" +
                "        \"updated_at\": \"2020-12-10 10:16:04\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"file_name\": \"custom.project.form.js\",\n" +
                "        \"file_type\": \"js\",\n" +
                "        \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/js\\/form-builder\\/custom.project.form.js\",\n" +
                "        \"updated_at\": \"2020-12-14 10:16:04\"\n" +
                "      },\n" +
                " {\n" +
                " \"file_name\": \"nestedSelect2.css\",\n" +
                " \"file_type\": \"css\",\n" +
                " \"file_path\":\"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/css\\/nestedSelect2.css?ver=1607929196\",\n" +
                " \"updated_at\": \"2020-12-14 00:27:27\"\n" +
                " },\n" +
                " {\n" +
                " \"file_name\": \"nestedselect2.full.js\",\n" +
                " \"file_type\": \"js\",\n" +
                " \"file_path\": \"https:\\/\\/app.pronovos.com\\/assets\\/newtheme\\/js\\/plugin\\/nestedSelect2\\/nestedselect2.full.js?ver=1607929196\",\n" +
                " \"updated_at\": \"2020-12-14 00:45:02\"\n" +
                " },\n" +
                "      {\n" +
                "        \"file_name\": \"jquery-ui.min.js\",\n" +
                "        \"file_type\": \"js\",\n" +
                "        \"file_path\": \"\",\n" +
                "        \"updated_at\": \"2020-01-11 07:46:57\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"file_name\": \"font-awesome.min.css\",\n" +
                "        \"file_type\": \"css\",\n" +
                "        \"file_path\": \"\",\n" +
                "        \"updated_at\": \"2019-09-05 15:27:27\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"responseCode\": 101,\n" +
                "    \"responseMsg\": \"Form asset last update.\"\n" +
                "  }\n" +
                "}";
    }

    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                Log.d(TAG, "copyAssets 1: "+ getFilesDir().getAbsolutePath());
                File myDir = new File(getFilesDir().getAbsolutePath() + "/Pronovos/");//"/PronovosPronovos"
                Log.d(TAG, "copyAssets2 : "+myDir.getPath());
                if (!myDir.exists()) {
                    myDir.mkdirs();
                }
                File outFile = new File(myDir, filename);
                if (outFile.exists()) {
                    outFile.delete();
                }
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch (IOException e) {
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private void callPermissionAPI() {
        projectsProvider.getUserPermission(new ProviderResult<LoginResponse>() {
            @Override
            public void success(LoginResponse result) {
                SharedPref.getInstance(ProjectsActivity.this).writePrefs(SharedPref.SESSION_DETAILS, new Gson().toJson(result));
            }

            @Override
            public void AccessTokenFailure(String message) {
                onAccessTokenFail();
            }

            @Override
            public void failure(String message) {

            }
        });
    }

    /**
     * Redirect user to Login screen in case of "AccessTokenFailure".
     */
    private void onAccessTokenFail() {

        getApplicationContext().startActivity(new Intent(getApplicationContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        SharedPref.getInstance(getApplicationContext()).writePrefs(SharedPref.SESSION_DETAILS, null);
        SharedPref.getInstance(getApplicationContext()).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
        if (getApplicationContext() instanceof Activity) {
            ((Activity) getApplicationContext()).finish();
        }

    }

    private void callProjectDataAPI() {
        projectsProvider.getUserProjectData(new ProviderResult<List<RegionsTable>>() {
            @Override
            public void success(List<RegionsTable> result) {

            }

            @Override
            public void AccessTokenFailure(String message) {
                onAccessTokenFail();
            }

            @Override
            public void failure(String message) {

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
                startActivity(new Intent(ProjectsActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(ProjectsActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(ProjectsActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(ProjectsActivity.this, message, getString(R.string.ok));
//                messageDialog.showMessageAlert(DailyWeatherReportActivity.this, getString(R.string.failureMessage), getString(R.string.ok));

            }
        });
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
        } else {
            offlineTextView.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BackupSyncProgressStartUpdate event) {
        if (event.isSyncStartedRunning) {
            //  SharedPref.getInstance(ProjectsActivity.this).writeBooleanPrefs(SharedPref.SYNC_OLD_FILES_RUNNING, true);
            syncProgressView.setVisibility(View.VISIBLE);
            syncImageView.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BackupSyncProgressUpdate event) {
        Log.d(TAG, "onEvent: ");
        if (event.isSYncOldFileDone) {
            syncProgressView.setVisibility(View.GONE);
            syncImageView.setVisibility(View.GONE);
            projectsProvider.deleteBackupSyncTransactionLog(loginResponse.getUserDetails().getUsers_id());
            SharedPref.getInstance(ProjectsActivity.this).writeBooleanPrefs(SharedPref.SYNC_OLD_FILES_REQUIRED, true);
            SharedPref.getInstance(ProjectsActivity.this).writeBooleanPrefs(SharedPref.SYNC_OLD_FILES_RUNNING, false);

        } else if (event.isFailure) {
            syncProgressView.setVisibility(View.GONE);
            syncImageView.setVisibility(View.VISIBLE);
            SharedPref.getInstance(ProjectsActivity.this).writeBooleanPrefs(SharedPref.SYNC_OLD_FILES_RUNNING, false);
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.projects_view);
        messageDialog = new MessageDialog();
        ButterKnife.bind(this);
        doGetApplication().getDaggerComponent().inject(this);

        doGetApplication().setupAndStartWorkManager();

//        ((PronovosApplication) getApplication()).getDaggerComponent().inject(this);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        projectList = new ArrayList<>();
        currentRegionId = loginResponse.getUserDetails().getRegionId();
        Log.e(TAG, " ******************** oncreate: regionId " + loginResponse.getUserDetails().getRegionId());
        callProjectsService(loginResponse.getUserDetails().getRegionId());
        callRegionsService();
//        callPermissionAPI();
        if (getIntent().getBooleanExtra("after_login", false)) {
            callProjectDataAPI();
            callWeatherConditionAPI();
            callTradesAPI();
            callPhotoTagService();
        }
        projectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        projectsRecyclerView.setHasFixedSize(true);
        projectsRecyclerView.setItemViewCacheSize(20);
        projectsRecyclerView.setDrawingCacheEnabled(true);
        projectsRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        filterImageView.setOnClickListener(this);
        titleTextView.setText(getString(R.string.select_project));
        searchProjectEditText.addTextChangedListener(searchProjectEditTextTextWatcher);
        if (mprojectFormRepository.getFormAssets().size() == 0) {
            String str = "";
            if (BuildConfig.FLAVOR.equals("production")) {
                str = getProductionAssetString();
            } else {
                str = getDevelopmentAssetString();
            }
            // Log.e(TAG, "****************** onCreate: str =  " + str);
            CSSJSResponse cssjsResponse = (new Gson().fromJson(str, CSSJSResponse.class));
            mprojectFormRepository.doUpdateCSSJS(cssjsResponse.getData().getFormAssets());
            copyAssets();
        }
    }

    private void callRegionsService() {
//        CustomProgressBar.showDialog(this);
        projectsProvider.getUserRegions(new ProviderResult<List<RegionsTable>>() {
            @Override
            public void success(List<RegionsTable> result) {
//                CustomProgressBar.dissMissDialog(ProjectsActivity.this);
                LayoutInflater inflater = (LayoutInflater) ProjectsActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.regions_popup_view, null);
                mPopupWindow = new PopupWindow(
                        customView,
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                RecyclerView recyclerView = customView.findViewById(R.id.regionsRecyclerView);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                mRegionAdapter = new RegionAdapter(ProjectsActivity.this, result, regionId);
//                DividerItemDecoration itemDecor = new DividerItemDecoration(getApplicationContext(), HORIZONTAL);
//                recyclerView.addItemDecoration(itemDecor);
                recyclerView.setAdapter(mRegionAdapter);
            }

            @Override
            public void AccessTokenFailure(String message) {
//                CustomProgressBar.dissMissDialog(ProjectsActivity.this);
                onAccessTokenFail();
            }

            @Override
            public void failure(String message) {
//                CustomProgressBar.dissMissDialog(ProjectsActivity.this);
                messageDialog.showMessageAlert(ProjectsActivity.this, message, getString(R.string.ok));
//                messageDialog.showMessageAlert(ProjectsActivity.this, getString(R.string.failureMessage), getString(R.string.ok));
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
       /* projectList = projectsProvider.getSearchProject(regionId, searchProjectEditText.getText().toString());
        mProjectAdapter = new ProjectAdapter(ProjectsActivity.this, projectList);
        projectsRecyclerView.setAdapter(mProjectAdapter);
*/
        projectList.clear();
        projectList.addAll(projectsProvider.getSearchProject(currentRegionId, searchProjectEditText.getText().toString(), loginResponse));
        mProjectAdapter.notifyDataSetChanged();
    }

    private void callPhotoTagService() {
        projectsProvider.getPhotoTags(new ProviderResult<List<ImageTag>>() {
            @Override
            public void success(List<ImageTag> result) {


            }

            @Override
            public void AccessTokenFailure(String message) {
                onAccessTokenFail();
            }

            @Override
            public void failure(String message) {
//                projectsProvider.showMessageAlert(ProjectsActivity.this, message, getString(R.string.ok));
                messageDialog.showMessageAlert(ProjectsActivity.this, message, getString(R.string.ok));
//                messageDialog.showMessageAlert(ProjectsActivity.this, getString(R.string.failureMessage), getString(R.string.ok));
            }
        }, loginResponse);
    }

    @OnClick(R.id.leftImageView)
    public void logoutClick() {
        projectsProvider.showLogoutAlert(this, getString(R.string.are_you_sure_you_want_to_logout), getString(R.string.logout), getString(R.string.cancel));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.syncImageView: {
                if ((NetworkService.isNetworkAvailable(ProjectsActivity.this))) {
                    syncImageView.setVisibility(View.GONE);
                    syncProgressView.setVisibility(View.VISIBLE);
                    projectsProvider.createBackUpSyncTransactionLog(loginResponse.getUserDetails().getUsers_id());
                    doGetApplication().setupAndStartWorkManager();
                }
                break;
            }
//            case R.id.leftImageView:
//                projectsProvider.showLogoutAlert(this, getString(R.string.are_you_sure_you_want_to_logout), getString(R.string.logout), getString(R.string.cancel));
//                break;
            case R.id.rightImageView:

                int[] loc_int = new int[2];

                try {
                    filterImageView.getLocationOnScreen(loc_int);
                } catch (NullPointerException npe) {
                    //Happens when the view doesn't exist on screen anymore.

                }
                Rect location = new Rect();
                location.left = loc_int[0];
                location.top = loc_int[1];
                location.right = location.left + v.getWidth();
                location.bottom = location.top + v.getHeight();
                if (mPopupWindow != null) {
                    mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                    mPopupWindow.setOutsideTouchable(true);
                    mPopupWindow.setFocusable(true);
                    mPopupWindow.showAtLocation(filterImageView, Gravity.TOP | Gravity.RIGHT, 0, location.top + v.getHeight());
                }
                break;
        }
    }

    private void callProjectsService(int regionId) {
        Log.e(TAG, " ******************** callProjectsService: regionId " + regionId);
        projectList.clear();
        projectList.addAll(projectsProvider.getSearchProject(regionId, searchProjectEditText.getText().toString(), loginResponse));
        mProjectAdapter = new ProjectAdapter(ProjectsActivity.this, projectList);
        projectsRecyclerView.setAdapter(mProjectAdapter);

        List<PjProjects> pr = projectsProvider.getSearchProject(currentRegionId, searchProjectEditText.getText().toString(), loginResponse);
        Log.i("pr status", "callProjectsService: " + pr.size());
//        CustomProgressBar.showDialog(this);
        this.regionId = regionId;
        loginResponse.getUserDetails().setRegionId(regionId);
        SharedPref.getInstance(ProjectsActivity.this).writePrefs(SharedPref.SESSION_DETAILS, new Gson().toJson(loginResponse));
        ProjectVersionsCheck projectVersionsCheck = new ProjectVersionsCheck();
        projectVersionsCheck.setPlatform("android");
        projectVersionsCheck.setVersion(BuildConfig.VERSION_NAME);
        // projectVersionsCheck.setVersion( "2.15.0");
        ProjectsRequest projectsRequest = new ProjectsRequest(String.valueOf(regionId), projectVersionsCheck);
        projectsProvider.getUserProjects(projectsRequest, new ProviderResult<UsersProject>() {
            @Override
            public void success(UsersProject result) {
                Log.d(TAG, "success: getUserProjects " + result.toString());
//                CustomProgressBar.dissMissDialog(ProjectsActivity.this);
                projectList.clear();
                projectList.addAll(projectsProvider.getSearchProject(currentRegionId, searchProjectEditText.getText().toString(), loginResponse));
                mProjectAdapter.notifyDataSetChanged();
//                projectsRecyclerView.setAdapter(mProjectAdapter);
                if (projectList.size() == 0) {
                    noRecordTextView.setText("No projects have been assigned to your user account.");
                } else {
                    noRecordTextView.setText("");
                }
                Log.d(TAG, "success: " + result.toString());
                if (result.getNewversionAvailable()) {
                    showAppUpdateAlert();
                }

            }

            @Override
            public void AccessTokenFailure(String message) {
//                CustomProgressBar.dissMissDialog(ProjectsActivity.this);
                startActivity(new Intent(ProjectsActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(ProjectsActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(ProjectsActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(ProjectsActivity.this, message, getString(R.string.ok));
//                messageDialog.showMessageAlert(ProjectsActivity.this, getString(R.string.failureMessage), getString(R.string.ok));
            }
        });

    }

    private void callTradesAPI() {
        mFieldPaperWorkProvider.getTrades(new ProviderResult<String>() {
            @Override
            public void success(String result) {

            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(ProjectsActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(ProjectsActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(ProjectsActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
//                messageDialog.showMessageAlert(ProjectsActivity.this, message, getString(R.string.ok));
//                messageDialog.showMessageAlert(FieldPaperWorkActivity.this, getString(R.string.failureMessage), getString(R.string.ok));

            }
        });
    }

    @Override
    public void onSelectRegion(RegionsTable regions) {
        this.regionId = regions.getRegions_id();
        currentRegionId = regionId;
        Log.d(TAG, "onSelectRegion:regions  " + regions);
        callProjectsService(regions.getRegions_id());
        searchProjectEditText.setText("");
        mPopupWindow.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        callPermissionAPI();
        callCSSJSAPI();
        checkUpdateSyncStatus();
        //     callAppUpdateAPi();
    }

    private void callAppUpdateAPi() {
        showAppUpdateAlert();


    }

    private void showAppUpdateAlert() {


        try {
            if (updateAlertDialog == null || !updateAlertDialog.isShowing()) {
                updateAlertDialog = new AlertDialog.Builder(ProjectsActivity.this).create();
            }
//            alertDialog.setTitle(context.getString(R.string.message));
            updateAlertDialog.setMessage(getString(R.string.message_app_update_found));
            updateAlertDialog.setTitle(getString(R.string.alert_title_update_available));
            updateAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.button_text_update), (dialog, which) -> {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                updateAlertDialog.dismiss();

            });
            updateAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.button_text_not_now), (dialog, which) -> {

                updateAlertDialog.dismiss();
            });

            if (updateAlertDialog != null && !updateAlertDialog.isShowing()) {
                updateAlertDialog.setCancelable(false);

            }
            updateAlertDialog.show();
            Button pbutton = updateAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(ProjectsActivity.this, R.color.colorPrimary));
            Button negativebutton = updateAlertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            negativebutton.setTextColor(ContextCompat.getColor(ProjectsActivity.this, R.color.colorPrimaryDark));

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private void callCSSJSAPI() {
        Log.i("Test", "success: B");
        mprojectFormProvider.getCSSJS(loginResponse, new ProviderResult<CSSJSResponse>() {
            @Override
            public void success(CSSJSResponse result) {
                downloadAssets();

            }

            @Override
            public void AccessTokenFailure(String message) {
                onAccessTokenFail();
            }

            @Override
            public void failure(String message) {

            }
        });
    }

    private void downloadAssets() {
        File myDir = new File(ProjectsActivity.this.getFilesDir().getAbsolutePath() + "/Pronovos/");//"/PronovosPronovos"
        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        List<FormAssets> formAssets = mprojectFormRepository.getFormAssets();
        for (FormAssets formAsset : formAssets) {
            String fname = "new_" + formAsset.getFileName();
            File file = new File(myDir, fname);
            if (file.exists() && file.length() != 0) {
                continue;
            }
            Object[] params = new Object[]{formAsset.getFilePath(), file, formAsset.getFileName(), ProjectsActivity.this};

            LoadTextFilesInBackground loadTextFilesInBackground = new LoadTextFilesInBackground(new LoadTextFilesInBackground.Listener() {
                @Override
                public void onImageDownloaded(Boolean b) {

                }

                @Override
                public void onImageDownloadError() {

                }
            });
            loadTextFilesInBackground.executeOnExecutor(SERIAL_EXECUTOR, params);


        }
    }

}
