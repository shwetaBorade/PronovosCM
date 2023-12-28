package com.pronovoscm.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.pronovoscm.R;
import com.pronovoscm.adapter.TransferLogPagerAdapter;
import com.pronovoscm.data.ProjectFormProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.fragments.ProjectFormCalendarFragment;
import com.pronovoscm.fragments.ProjectFormListFragment;
import com.pronovoscm.fragments.ProjectUnSyncFormFragment;
import com.pronovoscm.model.TransactionLogUpdate;
import com.pronovoscm.model.TransactionModuleEnum;
import com.pronovoscm.model.request.formuser.ProjectFormUserRequest;
import com.pronovoscm.model.request.projectoverview.ProjectOverviewRequest;
import com.pronovoscm.model.response.cssjs.CSSJSResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.FormAssets;
import com.pronovoscm.persistence.domain.FormCategory;
import com.pronovoscm.persistence.domain.Forms;
import com.pronovoscm.persistence.domain.FormsComponent;
import com.pronovoscm.persistence.domain.FormsSchedule;
import com.pronovoscm.persistence.domain.ProjectFormArea;
import com.pronovoscm.persistence.domain.UserForms;
import com.pronovoscm.persistence.repository.ProjectFormRepository;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.ui.CustomProgressBar;
import com.pronovoscm.utils.ui.LoadTextFilesInBackground;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

import static android.os.AsyncTask.SERIAL_EXECUTOR;

public class ProjectFormActivity extends BaseActivity {

    @Inject
    ProjectFormProvider mprojectFormProvider;
    @Inject
    ProjectFormRepository mprojectFormRepository;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.lineView)
    View lineView;
    private int projectId;
    private LoginResponse loginResponse;
    private TransferLogPagerAdapter mSectionsPagerAdapter;
    private ProjectFormListFragment projectFormListFragment;
    private ProjectFormCalendarFragment projectFormCalendarFragment;
    private ProjectOverviewRequest projectOverviewRequest;
   // private ArrayList<Forms> formsArrayList;
    private ProjectUnSyncFormFragment projectUnSyncFormFragment;

    @Override
    protected int doGetContentView() {
        return R.layout.project_form_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        projectId = getIntent().getIntExtra("project_id", 0);
        // formsArrayList = new ArrayList<>();
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        // formsArrayList.addAll(mprojectFormRepository.getProjectForm(projectId, "", loginResponse.getUserDetails().getUsers_id()));
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.INVISIBLE);
        titleTextView.setText(getString(R.string.forms));
        callCSSJSAPI();
        setupViewPager();
        projectOverviewRequest = new ProjectOverviewRequest(projectId);
        callProjectFormScheduleAPI();
        callCategoryAPI();


    }

    public void setupViewPager() {
        viewPager.removeAllViews();
        tabLayout.removeAllTabs();
        mSectionsPagerAdapter = new TransferLogPagerAdapter(getSupportFragmentManager());
        lineView.setVisibility(View.VISIBLE);
        TabLayout.Tab pickUpTab = tabLayout.newTab();
        pickUpTab.setText("   List View   ");
        tabLayout.addTab(pickUpTab);
        projectFormListFragment = new ProjectFormListFragment();
        mSectionsPagerAdapter.addFragment(projectFormListFragment, "List View");


        TabLayout.Tab calendarTab = tabLayout.newTab();
        calendarTab.setText("   Calendar View   ");
        tabLayout.addTab(calendarTab);
        projectFormCalendarFragment = new ProjectFormCalendarFragment();
        mSectionsPagerAdapter.addFragment(projectFormCalendarFragment, "Calendar View");

        TabLayout.Tab unSyncTab = tabLayout.newTab();
        unSyncTab.setText("   Unsynced Forms   ");
        tabLayout.addTab(unSyncTab);
        projectUnSyncFormFragment = new ProjectUnSyncFormFragment();
        mSectionsPagerAdapter.addFragment(projectUnSyncFormFragment, "Unsynced Forms");

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setAdapter(mSectionsPagerAdapter);
        //viewPager.setOffscreenPageLimit(equipmentSubCategoriesMasterList.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                tabLayout.getTabAt(i).select();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mSectionsPagerAdapter.notifyDataSetChanged();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TransactionLogUpdate transactionLogUpdate) {
        Log.i("TEMP", "  bind: 1 ");
        if (transactionLogUpdate.getTransactionModuleEnum() != null && (transactionLogUpdate.getTransactionModuleEnum().equals(TransactionModuleEnum.PROJECT_FORM_SUBMIT)) && projectUnSyncFormFragment != null) {
            projectUnSyncFormFragment.loadData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
        } else {
            offlineTextView.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.leftImageView)
    public void onBackClick() {
        super.onBackPressed();
    }

    private void callCategoryAPI() {
        mprojectFormProvider.getCategories(loginResponse, new ProviderResult<List<FormCategory>>() {
            @Override
            public void success(List<FormCategory> result) {
                //                projectFormListFragment.refreshData(result);
                Log.i("Test", "success: A");
                callProjectFormAPI();
            }

            @Override
            public void AccessTokenFailure(String message) {

                CustomProgressBar.dissMissDialog(ProjectFormActivity.this);
            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(ProjectFormActivity.this);
                projectFormListFragment.refreshAdapter(true, false);

            }
        });
    }

    private void callProjectFromAreaApi() {
        mprojectFormProvider.getProjectAreas(projectOverviewRequest, loginResponse, new ProviderResult<ProjectFormArea>() {
            @Override
            public void success(ProjectFormArea result) {
                CustomProgressBar.dissMissDialog(ProjectFormActivity.this);
            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(ProjectFormActivity.this);
            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(ProjectFormActivity.this);
            }
        });
    }

    private void callProjectFormAPI() {

        mprojectFormProvider.getProjectForms(projectOverviewRequest, loginResponse, new ProviderResult<List<Forms>>() {
            @Override
            public void success(List<Forms> result) {
                // formsArrayList.clear();
                //  formsArrayList.addAll(mprojectFormRepository.getProjectForm(projectId, ""));
                projectFormListFragment.refreshAdapter(true, false);
                callProjectFromAreaApi();
            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(ProjectFormActivity.this);
            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(ProjectFormActivity.this);
                projectFormListFragment.refreshAdapter(true, false);

            }
        });
    }

    private void callProjectFormScheduleAPI() {
        Log.i("schedule", " callProjectFormScheduleAPI");
        mprojectFormProvider.getProjectFormsSchedules(projectOverviewRequest, loginResponse, new ProviderResult<List<FormsSchedule>>() {
            @Override
            public void success(List<FormsSchedule> result) {

                Log.i("schedule", "callProjectFormScheduleAPI schedule success: rojectFormSchedule size " + mprojectFormRepository.getProjectFormSchedule(projectId).size());
                callFormUserAPI();
            }

            @Override
            public void AccessTokenFailure(String message) {

                CustomProgressBar.dissMissDialog(ProjectFormActivity.this);
            }

            @Override
            public void failure(String message) {
                Log.e("schedule", "failure: callProjectFormScheduleAPI ");
                CustomProgressBar.dissMissDialog(ProjectFormActivity.this);
                projectFormListFragment.refreshAdapter(true, false);

            }
        });
    }

    private void callProjectFormComponentAPI() {
        Log.i("Test", "success: B");
        mprojectFormProvider.getProjectFormComponents(projectOverviewRequest, loginResponse, new ProviderResult<List<FormsComponent>>() {
            @Override
            public void success(List<FormsComponent> result) {
              /*  formsArrayList.clear();
                formsArrayList.addAll(mprojectFormRepository.getProjectForm(projectId));
                projectFormListFragment.refreshAdapter(    formsArrayList);*/
                if (viewPager != null && viewPager.getAdapter() != null)
                    viewPager.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void AccessTokenFailure(String message) {

                CustomProgressBar.dissMissDialog(ProjectFormActivity.this);
            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(ProjectFormActivity.this);

            }
        });
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

                CustomProgressBar.dissMissDialog(ProjectFormActivity.this);
            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(ProjectFormActivity.this);

            }
        });
    }

    private void callFormUserAPI() {
        ProjectFormUserRequest projectFormUserRequest = new ProjectFormUserRequest(projectId);

        mprojectFormProvider.getProjectFormUsers(projectFormUserRequest, loginResponse, new ProviderResult<List<UserForms>>() {
            @Override
            public void success(List<UserForms> result) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {

                      //  projectFormCalendarFragment.updateData(Calendar.getInstance());
                    }
                });
            }

            @Override
            public void AccessTokenFailure(String message) {

                CustomProgressBar.dissMissDialog(ProjectFormActivity.this);
            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(ProjectFormActivity.this);

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        callProjectFormComponentAPI();
        downloadAssets();
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

    private void downloadAssets() {
        File myDir = new File(ProjectFormActivity.this.getFilesDir().getAbsolutePath() + "/Pronovos/");//"/PronovosPronovos"
        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        List<FormAssets> formAssets = mprojectFormRepository.getFormAssets();
        for (FormAssets formAsset : formAssets) {
            if (TextUtils.isEmpty(formAsset.getFilePath())){
                return;
            }
//            String fname = formAsset.getFileName();
            String fname = "new_" + formAsset.getFileName();
            File file = new File(myDir, fname);
            if (file.exists() && file.length() != 0) {
                continue;
            }
            Object[] params = new Object[]{formAsset.getFilePath(), file, formAsset.getFileName(), ProjectFormActivity.this};
//            Object[] params = new Object[]{formAsset.getFilePath(), file};

            LoadTextFilesInBackground loadTextFilesInBackground = new LoadTextFilesInBackground(new LoadTextFilesInBackground.Listener() {
                @Override
                public void onImageDownloaded(Boolean aBoolean) {

                }

                @Override
                public void onImageDownloadError() {

                }
            });
            loadTextFilesInBackground.executeOnExecutor(SERIAL_EXECUTOR, params);


        }
    }


}
