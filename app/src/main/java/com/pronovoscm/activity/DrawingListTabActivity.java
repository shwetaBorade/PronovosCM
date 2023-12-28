package com.pronovoscm.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.pronovoscm.BuildConfig;
import com.pronovoscm.R;
import com.pronovoscm.adapter.DrawingPagerAdapter;
import com.pronovoscm.adapter.DrawingSectionsPagerAdapter;
import com.pronovoscm.data.DrawingAnnotationProvider;
import com.pronovoscm.data.NetworkStateProvider;
import com.pronovoscm.data.PDFFileDownloadProvider;
import com.pronovoscm.data.ProjectDrawingListProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.fragments.DrawingListFragment;
import com.pronovoscm.model.DrawingAction;
import com.pronovoscm.model.PDFSynEnum;
import com.pronovoscm.model.request.drawinglist.DrawingListRequest;
import com.pronovoscm.model.request.drawingpunchlist.DrawingPunchlist;
import com.pronovoscm.model.response.drawingpunchlist.DrawingPunchlistResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.logresponse.LogRequest;
import com.pronovoscm.model.response.syncupdate.Drawings;
import com.pronovoscm.model.response.syncupdate.SyncUpdateResponse;
import com.pronovoscm.persistence.domain.DrawingFolders;
import com.pronovoscm.persistence.domain.DrawingList;
import com.pronovoscm.persistence.domain.DrawingXmls;
import com.pronovoscm.persistence.repository.DrawingListRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.FileUtils;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.dialogs.AutoSycFolderDialog;
import com.pronovoscm.utils.dialogs.BugReportDialog;
import com.pronovoscm.utils.dialogs.MessageDialog;
import com.pronovoscm.utils.ui.RecyclerSectionItemDecoration;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


/**
 * Activity to show drawing (PDF) list
 *
 * @author GWL
 */
public class DrawingListTabActivity extends BaseActivity implements View.OnClickListener {
    public static final int FILESTORAGE_REQUEST_CODE = 221;


    @Inject
    DrawingAnnotationProvider mDrawingAnnotationProvider;
    //    @Inject
//    PunchListProvider mPunchListProvider;
//    @Inject
//    PunchListRepository mPunchListRepository;
    @Inject
    ProjectDrawingListProvider mProjectDrawingListProvider;
    @Inject
    PDFFileDownloadProvider mPDFFileDownloadProvider;
    @Inject
    NetworkStateProvider mNetworkStateProvider;
    @Inject
    DrawingAnnotationProvider mAnnotationProvider;
    @Inject
    DrawingListRepository mDrawingListRepository;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.cameraImageView)
    ImageView cameraImageView;
    @BindView(R.id.image_auto_sync)
    ImageView imageViewAutoSync;
    @BindView(R.id.image_bug_report)
    ImageView imageViewBugReport;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;/*@BindView(R.id.drawingsRecyclerView)
    RecyclerView drawingsRecyclerView;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.lastUpdatedTextView)
    TextView lastUpdatedTextView;
    @BindView(R.id.searchDrawingEditText)
    EditText searchDrawingEditText;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.notificationView)
    ConstraintLayout notificationView;
    @BindView(R.id.updatedNotificationTextView)
    TextView textViewUpdatedNotification;
    @BindView(R.id.updateTextView)
    TextView buttonUpdate;
    @BindView(R.id.seachClearImageView)
    ImageView searchClearImageView;
*/
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private List<DrawingList> mDrawingArrayList;
    private RecyclerSectionItemDecoration sectionItemDecoration;
    //    private DrawingListAdapter mDrawingListAdapter;
    private List<Drawings> mDrawingAnnotations;
    private int drwFolderId;
    private int projectId;
    private String lastSelectedTab;
    private boolean addingTab;
    private DrawingFolders drawingFolder;
    private boolean isLoading = false;
    private boolean isOffline = false;
    private boolean isFirstLoading = true;
    private MessageDialog messageDialog;
    //    private boolean isManualUpdate = false;
    private DrawingAction mAction = DrawingAction.NON;
    private Date updatedAt;
    private DrawingPagerAdapter drawingPagerAdapter;
    private DrawingSectionsPagerAdapter mSectionsPagerAdapter;
    private boolean mSyncUpdateResponse = false;
    private boolean showNotificationView = false;
    private ArrayList<Drawings> syncNewDrawing;
    private List<DrawingList> mDrawingDisciplineList;

    @Override
    protected int doGetContentView() {
        return R.layout.drawing_list_tab_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        ButterKnife.bind(this);
        messageDialog = new MessageDialog();
        cameraImageView.setVisibility(View.GONE);
        rightImageView.setVisibility(View.GONE);
        imageViewAutoSync.setVisibility(View.VISIBLE);
        imageViewBugReport.setVisibility(View.VISIBLE);
        drwFolderId = getIntent().getIntExtra("drw_folder_id", 0);
        projectId = getIntent().getIntExtra("project_id", 0);

        drawingFolder = mProjectDrawingListProvider.getDrawingFolderDetail(projectId, drwFolderId);
        setupViewPager();
        mDrawingArrayList = new ArrayList<>();
        titleTextView.setText(drawingFolder.getFolderName());
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        Date dateTime = drawingFolder.getLastupdatedate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(getExternalPermission()) == PackageManager.PERMISSION_GRANTED) {
                FileUtils.createFolderAndFile(null);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{getExternalPermission()}, FILESTORAGE_REQUEST_CODE);
            }
        } else {
            FileUtils.createFolderAndFile(null);
        }
    }

    public void setupViewPager() {
        tabLayout.removeAllTabs();
        mDrawingDisciplineList = mDrawingListRepository.getDrawingListdrwDiscipline(drwFolderId);
        mSectionsPagerAdapter = new DrawingSectionsPagerAdapter(getSupportFragmentManager(), mDrawingDisciplineList);
        int selectTab = 0;
        addingTab = true;
        for (int i = 0; i < mDrawingDisciplineList.size(); i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setText(mDrawingDisciplineList.get(i).getDrawingDiscipline());
            tabLayout.addTab(tab);
            mSectionsPagerAdapter.addFragment(new DrawingListFragment(mDrawingDisciplineList.get(i)), mDrawingDisciplineList.get(i).getDrawingDiscipline(), i);
            if (lastSelectedTab != null && lastSelectedTab.equals(mDrawingDisciplineList.get(i).getDrawingDiscipline())) {
                selectTab = i;
            }
        }
        addingTab = false;

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (mSectionsPagerAdapter != null && mSectionsPagerAdapter.getCurrentFragment() != null) {
                    ((DrawingListFragment) mSectionsPagerAdapter.getCurrentFragment()).setOfflineMode(isOffline);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (!addingTab) {
                    lastSelectedTab = mDrawingDisciplineList.get(tab.getPosition()).getDrawingDiscipline();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setOffscreenPageLimit(mDrawingDisciplineList.size());
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                tabLayout.getTabAt(i).select();
                if (!addingTab) {
                    lastSelectedTab = mDrawingDisciplineList.get(i).getDrawingDiscipline();
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mSectionsPagerAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(selectTab);

    }

    /**
     * download the pdf if there is any event from event bus
     *
     * @param drawingFolder
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadAllPDF(DrawingFolders drawingFolder) {

        if (drawingFolder.getSyncDrawingFolder()) {
            List<DrawingList> drawingArrayList = mProjectDrawingListProvider.getNonSyncDrawings(drwFolderId);
            for (int i = 0; i < drawingArrayList.size(); i++) {

                onDownloadPDF(drawingArrayList.get(i));
            }
            if (mSectionsPagerAdapter != null && mSectionsPagerAdapter.getCurrentFragment() != null) {
                (mSectionsPagerAdapter.getCurrentFragment()).setUserVisibleHint(true);
            }
        }
    }


    /**
     * Get the listener of the recycler view decorator to show the section in the recycler view
     *
     * @param drawingList
     * @return
     */
    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final List<DrawingList> drawingList) {
        return new RecyclerSectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection(int position) {
                if (position >= 0) {
                    return position == 0
                            || !(drawingList.get(position)
                            .getDrawingDiscipline().equals(drawingList.get(position - 1)
                                    .getDrawingDiscipline()));
                } else return false;
            }

            @Override
            public CharSequence getSectionHeader(int position) {
                if (position >= 0) {
                    return drawingList.get(position).getDrawingDiscipline();
                } else {
                    return "";
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        drwFolderId = getIntent().getIntExtra("drw_folder_id", 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
        } else {
            if (mDrawingListRepository.getDrawingList(drwFolderId).size() == 0 || mDrawingListRepository.getDrawingFolderDetail(projectId, drwFolderId).getSyncFolder()) {
                callDrawingListAPI(drwFolderId, mAction, true, true);
            } else {
                // List<DrawingXmls> drawingXmls = mAnnotationProvider.getNotSyncAnnotations();
                getDrawingUpdates();
            }
        }
        callDrawingPunchlistAPI();

    }

    private void callDrawingPunchlistAPI() {
        DrawingPunchlist drawingPunchlist = new DrawingPunchlist(projectId);
        LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        mDrawingAnnotationProvider.getDrawingPunchlist(drawingPunchlist, loginResponse, new ProviderResult<DrawingPunchlistResponse>() {
            @Override
            public void success(DrawingPunchlistResponse result) {

            }

            @Override
            public void AccessTokenFailure(String message) {

            }

            @Override
            public void failure(String message) {

            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == FILESTORAGE_REQUEST_CODE) {
            onDoneClick();
        }
    }

    /**
     * Get all the drawing from the server with respect to the folder id
     *
     * @param drwFolderId       id of the folder
     * @param callDrawingUpdate
     */
    public void callDrawingListAPI(int drwFolderId, DrawingAction action, boolean setViewPager, boolean callDrawingUpdate) {
        if (isLoading) {
            return;
        }
        isLoading = true;
        DrawingListRequest drawingListRequest = new DrawingListRequest();
        drawingListRequest.setFolder_id(drwFolderId);
        Date lastUpdate = drawingFolder.getLastUpdateXml();
        String lastUpdateStr = "1970-01-01 01:01:01";
        if (lastUpdate != null) {
            lastUpdateStr = DateFormatter.formatDateTimeForService(lastUpdate);

        }
        mProjectDrawingListProvider.getDrawingList(drawingListRequest, lastUpdateStr, projectId, action, new ProviderResult<List<DrawingList>>() {
            @Override
            public void success(List<DrawingList> result) {
                isLoading = false;
                boolean autoSync = mDrawingListRepository.getDrawingFolderDetail(projectId, drwFolderId).getSyncFolder();
                if (updatedAt != null && !autoSync) {
                    if (NetworkService.isNetworkAvailable(DrawingListTabActivity.this)) {
                        drawingFolder.setLastUpdateXml(updatedAt); // TODO: 23/10/18 last updated date of the annotation
                        mProjectDrawingListProvider.updateDrawingFolder(drawingFolder);
                    }
                }
                if (mDrawingListRepository.getDrawingList(drwFolderId).size() == 0 && !autoSync) {
                    if (NetworkService.isNetworkAvailable(DrawingListTabActivity.this)) {
                        drawingFolder.setLastUpdateXml(mProjectDrawingListProvider.getLastUpdatedDateOfFolder(drwFolderId));
                        mProjectDrawingListProvider.updateDrawingFolder(drawingFolder);
                    }
                }
                if (drawingFolder.getLastupdatedate() != null) {
//                    lastUpdatedTextView.setText("Drawing List Updated: " + DateFormatter.getTimeAgo(drawingFolder.getLastupdatedate().getTime()));
                }
                {

                    mDrawingArrayList.clear();
                    mDrawingArrayList.addAll(result);
                    if (mSectionsPagerAdapter.getmFragmentTitleList().size() <= 1) {
                        setupViewPager();
                    } else {
                        updateViewPager();
                    }
                    // If user first time comes in this screen download all the PDF which user has synced previously but somehow these PDFs are not synced
                    if (isFirstLoading) {
                        isFirstLoading = false;
                        syncPDF();
                    }
                }

                if (mDrawingArrayList == null || mDrawingArrayList.size() <= 0) {
                } else if (callDrawingUpdate && action.compareTo(DrawingAction.NEW_DRAWING) != 0) {
                    List<DrawingXmls> drawingXmls = mAnnotationProvider.getNotSyncAnnotations();
                    getDrawingUpdates();
                }

                if (action.compareTo(DrawingAction.MANUAL) == 0) {
                    if (mSectionsPagerAdapter != null && mSectionsPagerAdapter.getCurrentFragment() != null) {
                        ((DrawingListFragment) mSectionsPagerAdapter.getCurrentFragment()).updateSyncInfo(false);
                    }
                    showNotificationView = false;
                }
                if (mSectionsPagerAdapter != null && mSectionsPagerAdapter.getCurrentFragment() != null) {
                    ((DrawingListFragment) mSectionsPagerAdapter.getCurrentFragment()).stopSwipeRefresh();
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                isLoading = false;
                ((DrawingListFragment) mSectionsPagerAdapter.getCurrentFragment()).stopSwipeRefresh();

                startActivity(new Intent(DrawingListTabActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(DrawingListTabActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(DrawingListTabActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                isLoading = false;
                if (mSectionsPagerAdapter.getCurrentFragment() != null) {
                    ((DrawingListFragment) mSectionsPagerAdapter.getCurrentFragment()).stopSwipeRefresh();
                }

                messageDialog.showMessageAlert(DrawingListTabActivity.this, message, getString(R.string.ok));
            }
        });
    }

    /**
     * Sync those pdf which are not synced yet
     */
    private void syncPDF() {
        List<DrawingList> drawingArrayList = mProjectDrawingListProvider.getNonSyncDrawings(drwFolderId);
        for (DrawingList drawingList : drawingArrayList) {
            if (drawingList.getPdfStatus() == PDFSynEnum.PROCESSING.ordinal() || drawingList.getPdfStatus() == PDFSynEnum.SYNC_FAILED.ordinal()) {
                onDownloadPDF(drawingList);
            }
        }
    }

    public void getDrawingUpdates() {

        if (drawingFolder.getLastUpdateXml() != null) {

            // TODO: 19/10/18 check if there is any update of the annotations (xml) and update the date in drawing folder object
            callSyncUpdate(drwFolderId, drawingFolder.getLastUpdateXml(), true);
        } else {
            // TODO: 19/10/18 update the last updated date in the drawing folder
            // update the last updated date of the drawing folder in the database
            if (NetworkService.isNetworkAvailable(DrawingListTabActivity.this)) {
                drawingFolder.setLastUpdateXml(mProjectDrawingListProvider.getLastUpdatedDateOfFolder(drwFolderId));
                mProjectDrawingListProvider.updateDrawingFolder(drawingFolder);
            }


            callSyncUpdate(drwFolderId, drawingFolder.getLastUpdateXml(), false);
        }
    }

    /**
     * Update the drawing annotations of the synced drawing
     *
     * @param syncedDrawingsList drawing list of synced pdf
     * @param isAutoDownload     isAutoDownload
     */
    public boolean updateDrawingAnnotations(List<Drawings> syncedDrawingsList, boolean isAutoDownload) {
        updatedAt = null;
        for (int i = 0; i < syncedDrawingsList.size(); i++) {
            if (isAutoDownload) {
            }
            if (updatedAt == null) {
                updatedAt = DateFormatter.getDateFromDateTimeString(syncedDrawingsList.get(0).getUpdatedAt());
            } else if (updatedAt.compareTo(DateFormatter.getDateFromDateTimeString(syncedDrawingsList.get(i).getUpdatedAt())) < 0) {
                updatedAt = DateFormatter.getDateFromDateTimeString(syncedDrawingsList.get(i).getUpdatedAt());
            }
            mAnnotationProvider.doUpdateDrawingAnnotationTable(syncedDrawingsList.get(i).getAnnotation(), syncedDrawingsList.get(i).getDrawingId(), true, true, true, "");
        }

        if (updatedAt != null) {
            return true;
        }
        return false;
    }

    public boolean getLastSyncUpdateResponse() {
        return mSyncUpdateResponse;
    }

    private void callSyncUpdate(int drwFolderId, Date lastUpdatedDate, boolean isUpdateLastUpdatedDate) {
        isLoading = true;

        mProjectDrawingListProvider.getDrawingSyncUpdate(projectId, drwFolderId, lastUpdatedDate, new ProviderResult<SyncUpdateResponse>() {
            @Override
            public void success(SyncUpdateResponse result) {
//                mSyncUpdateResponse = result;

                updateSyncInfo(result, isUpdateLastUpdatedDate);
            }

            @Override
            public void AccessTokenFailure(String message) {
                isLoading = false;
                mAction = DrawingAction.NON;
                ((DrawingListFragment) mSectionsPagerAdapter.getCurrentFragment()).stopSwipeRefresh();

                startActivity(new Intent(DrawingListTabActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(DrawingListTabActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(DrawingListTabActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                mAction = DrawingAction.NON;
                isLoading = false;
                if (mSectionsPagerAdapter != null && mSectionsPagerAdapter.getCurrentFragment() != null) {
                    ((DrawingListFragment) mSectionsPagerAdapter.getCurrentFragment()).stopSwipeRefresh();
                }

                if (!message.equalsIgnoreCase("Offline mode")) {
                    messageDialog.showMessageAlert(DrawingListTabActivity.this, message, getString(R.string.ok));
                }
            }
        });
    }

    /**
     * Check that File is exist in storage or not.
     *
     * @param fileName
     * @return
     */
    private boolean isFileExist(String fileName) {
        String root = getFilesDir().getAbsolutePath();
        File myDir = new File(root + "/Pronovos/PDF");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String fname = fileName;
        File file = new File(myDir, fname);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            isOffline = true;
            offlineTextView.setVisibility(View.VISIBLE);
            imageViewBugReport.setVisibility(View.GONE);
            List<DrawingList> drawingArrayList = mProjectDrawingListProvider.getProcessingDrawings(drwFolderId);
            for (DrawingList drawingList : drawingArrayList) {
                if (drawingList.getPdfStatus().equals(PDFSynEnum.PROCESSING.ordinal()))
                    mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.SYNC_FAILED.ordinal());
            }

        } else {
            isOffline = false;
            offlineTextView.setVisibility(View.GONE);
            imageViewBugReport.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
            } else {
                drawingFolder = mProjectDrawingListProvider.getDrawingFolderDetail(projectId, drwFolderId);
                if (drawingFolder != null && drawingFolder.getSyncDrawingFolder() != null && drawingFolder.getSyncDrawingFolder()) {
                    syncPDF();
                }
            }
        }
        if (mSectionsPagerAdapter != null && mSectionsPagerAdapter.getCurrentFragment() != null) {
            ((DrawingListFragment) mSectionsPagerAdapter.getCurrentFragment()).setOfflineMode(isOffline);
        }
    }


    /**
     * download the pdf if there is any event from event bus
     *
     * @param drawingList
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadPDF(DrawingList drawingList) {
        mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.PROCESSING.ordinal());
        LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        mPDFFileDownloadProvider.getDrawingPDF(drawingList, loginResponse.getUserDetails().getUsers_id(), new ProviderResult<Boolean>() {
            @Override
            public void success(Boolean result) {
                Log.i("FileDownload", "success: " + result);
                if (result) {
                    if (mNetworkStateProvider.isOffline()) {
                        mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.SYNC_FAILED.ordinal());
                        if (mSectionsPagerAdapter != null && mSectionsPagerAdapter.getCurrentFragment() != null) {
                            ((DrawingListFragment) mSectionsPagerAdapter.getCurrentFragment()).setOfflineMode(isOffline);
                        }
                    }
                    getDrawingAnnotation(drawingList);
                } else {
                    mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.SYNC_FAILED.ordinal());
                }
                mDrawingArrayList.clear();
                if (mSectionsPagerAdapter != null && mSectionsPagerAdapter.getCurrentFragment() != null) {
                    (mSectionsPagerAdapter.getCurrentFragment()).setUserVisibleHint(true);
                }

            }

            @Override
            public void AccessTokenFailure(String message) {
                (mSectionsPagerAdapter.getCurrentFragment()).setUserVisibleHint(true);
                mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.SYNC_FAILED.ordinal());

//                mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.NOTSYNC.ordinal());

            }

            @Override
            public void failure(String message) {
//                if (!mNetworkStateProvider.isOffline() && !isOffline) {
                mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.SYNC_FAILED.ordinal());
                mDrawingArrayList.clear();
                messageDialog.showMessageAlert(DrawingListTabActivity.this, message, getString(R.string.ok));
                (mSectionsPagerAdapter.getCurrentFragment()).setUserVisibleHint(true);
//                }
            }
        });
    }

    /**
     * Get the annotation of the drawing from the server
     *
     * @param drawingList
     */
    private void getDrawingAnnotation(DrawingList drawingList) {
        LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        mAnnotationProvider.getDrawingAnnotations(drawingList.getRevisitedNum(), drwFolderId, drawingList.getDrawingsId(), new ProviderResult<String>() {
            @Override
            public void success(String result) {

                try {
                    URL url = null;

                    if (drawingList.getPdfOrg() != null && !TextUtils.isEmpty(drawingList.getPdfOrg())) {
                        url = new URL(drawingList.getPdfOrg());
                    } else {
                        url = new URL(drawingList.getImageOrg());
                    }

                    String[] segments = url.getPath().split("/");
                    String fileName = segments[segments.length - 1];

                    if (isFileExist(loginResponse.getUserDetails().getUsers_id() + fileName) && result != null) {
                        mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.SYNC.ordinal());
                    } else {
                        mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.SYNC_FAILED.ordinal());
                    }
                    mDrawingArrayList.clear();
                    if ((mSectionsPagerAdapter.getCurrentFragment()) != null)
                        (mSectionsPagerAdapter.getCurrentFragment()).setUserVisibleHint(true);
                } catch (MalformedURLException m) {
                    mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.SYNC_FAILED.ordinal());
                    m.printStackTrace();
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                (mSectionsPagerAdapter.getCurrentFragment()).setUserVisibleHint(true);
                startActivity(new Intent(DrawingListTabActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(DrawingListTabActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(DrawingListTabActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.SYNC_FAILED.ordinal());
//                mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.NOTSYNC.ordinal());
                finish();
            }

            @Override
            public void failure(String message) {
                (mSectionsPagerAdapter.getCurrentFragment()).setUserVisibleHint(true);
                mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.SYNC_FAILED.ordinal());
                messageDialog.showMessageAlert(DrawingListTabActivity.this, message, getString(R.string.ok));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftImageView:
                onBackPressed();
                break;
        }
    }

    public void onDoneClick() {
        FileUtils.createFolderAndFile(null);
    }

   /* private void callNotSyncAnnotations() {
        boolean callPunchlist = false;

        List<DrawingXmls> drawingXmls = mAnnotationProvider.getNotSyncAnnotations();
//        if (drawingXmls.size() <= 0) {
//            getDrawingUpdates();
//            if (mDrawingListRepository.getDrawingList(drwFolderId).size() == 0 || mDrawingListRepository.getDrawingFolderDetail(projectId, drwFolderId).getSyncFolder()) {
//                mAction = DrawingAction.MANUAL;
//                callDrawingListAPI(drwFolderId, mAction, true);
//            }
//        }

        for (DrawingXmls drawingXml : drawingXmls) {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            try {
                db = dbf.newDocumentBuilder();

                Document doc = db.parse(new InputSource(new StringReader(drawingXml.getAnnotxml())));
                doc.getDocumentElement().normalize();
                NodeList nodeList = doc.getElementsByTagName("annots");
                if (nodeList.getLength() > 0) {
                    NodeList namedNodeMap = nodeList.item(0).getChildNodes();
                    for (int i = 0; i < namedNodeMap.getLength(); i++) {
                        Node node = namedNodeMap.item(i);
                        try {
                            if (node instanceof Element) {

                                if (((Element) node).getTagName().equals("text") || ((Element) node).getTagName().equals("punchitem")) {

                                    NodeList nodeList1;

                                    String childNode = null;
                                    int childN = 0;
                                    for (int n = 0; n < (node).getChildNodes().getLength(); n++) {
                                        if ((node).getChildNodes().item(n).getNodeName().equals("contents")) {
                                            childNode = (node).getChildNodes().item(n).getTextContent();
                                            childN = n;
                                        }
                                    }
                                    if (childNode != null && childNode.contains("punch_id")) {
                                        ((Element) node).setAttribute("icon", "Comment");

                                        String[] punchlistIdArray = childNode.split(",");

                                        int punchId = 0;
                                        String punchIdMobile = "";
                                        String punchStatus = "";
                                        String punchNumber = "";
                                        String contentPunchNumber = "";
                                        boolean isSync = true;
                                        for (int p = 0; p < punchlistIdArray.length; p++) {
                                            String str1 = punchlistIdArray[p];

                                            if (str1.contains("punch_id") && !str1.contains("punch_id_mobile")) {
                                                String[] val = str1.split("=");
                                                if (val.length > 1 && val[1].trim().length() > 0) {
                                                    punchId = Integer.parseInt(val[1].trim());
                                                }
                                                if (punchId != 0) {
                                                }
                                            }
                                            if (str1.contains("punch_status")) {
                                                String[] val = str1.split("=");
                                                if (val.length > 1 && val[1].trim().length() > 0) {
                                                    punchStatus = val[1].trim();
                                                }
                                            }
                                            if (str1.contains("punch_number")) {
                                                String[] val = str1.split("=");
                                                if (val.length > 1 && val[1].trim().length() > 0 && !val[1].trim().equals("-1")) {
                                                    punchNumber = val[1].trim();
                                                    contentPunchNumber = val[1].trim();
                                                }
                                            }
                                            if (str1.contains("punch_id_mobile")) {
                                                String[] val = str1.split("=");
                                                punchIdMobile = val[1].trim();
                                                PunchlistDb punchlistDb = null;
                                                if (punchId == 0) {
                                                    if (val.length > 1 && val[1].trim().length() > 0) {
                                                        punchlistDb = mPunchListRepository.getPunchListDetail(Integer.parseInt(val[1].trim()));
                                                    }
                                                } else {
                                                    punchlistDb = mPunchListRepository.getPunchListDetail(punchId, true);

                                                }
                                                if (punchlistDb != null) {
                                                    punchId = punchlistDb.getPunchlistId();
                                                    punchNumber = String.valueOf(punchlistDb.getItemNumber());

                                                }
                                            }
                                        }
                                        if (punchId == 0 || !contentPunchNumber.equals("")) {
                                            callPunchlist = true;
                                        } else {
                                            ((Element) node).setAttribute("punch_id", String.valueOf(punchId));
                                            ((Element) node).setAttribute("punch_id_mobile", punchIdMobile);
                                            ((Element) node).setAttribute("punch_status", punchStatus);
                                            ((Element) node).setAttribute("punch_number", punchNumber);
                                            node.getChildNodes().item(childN).setTextContent("punch_id = " + String.valueOf(punchId).trim() +
                                                    ", punch_id_mobile = " + punchIdMobile +
                                                    ", punch_status = " + punchStatus +
                                                    ", punch_number = " + punchNumber);
                                        }
                                        ((Element) doc.getElementsByTagName("annots").item(0).getChildNodes().item(i)).setAttribute("publish", "true");
                                    }

                                }
                            }
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        DOMSource domSource = new DOMSource(doc);
                        StringWriter writer = new StringWriter();
                        StreamResult result = new StreamResult(writer);
                        TransformerFactory tf = TransformerFactory.newInstance();
                        Transformer transformer = tf.newTransformer();
                        transformer.transform(domSource, result);

                        String annotations = writer.toString();
                        annotations = annotations.replaceAll("</text>", "</punchitem>");
                        annotations = annotations.replaceAll("<text ", "<punchitem ");
                        mDrawingAnnotationProvider.doUpdateDrawingAnnotationTable(annotations, drawingXml.getDrwDrawingsId(), false);
                        if (!callPunchlist) {
//                            saveAnnotationServer(drawingXml, annotations);

                        }
                    } catch (TransformerException e) {
                        e.printStackTrace();
                    }
                }
                {

                }
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

    /*  private void saveAnnotationServer(DrawingXmls drawingXml, String annotations) {
          DrawingStoreRequest drawingStoreRequest = new DrawingStoreRequest();
          drawingStoreRequest.setAnnot_xml(annotations);
          drawingStoreRequest.setDrawing_id(drawingXml.getDrwDrawingsId());
          mAnnotationProvider.getDrawingStoreAnnotations(drawingStoreRequest, new ProviderResult<String>() {
              @Override
              public void success(String result) {
                  getDrawingUpdates();
              }

              @Override
              public void AccessTokenFailure(String message) {
                  startActivity(new Intent(DrawingListTabActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                  SharedPref.getInstance(DrawingListTabActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                  SharedPref.getInstance(DrawingListTabActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                  finish();
              }

              @Override
              public void failure(String message) {
                  messageDialog.showMessageAlert(DrawingListTabActivity.this, message, getString(R.string.ok));
  //                    messageDialog.showMessageAlert(DrawingListActivity.this, getString(R.string.failureMessage), getString(R.string.ok));

              }
          });
      }
  */
    /* @OnClick({R.id.seachClearImageView})
     public void clickSearchClear() {
 //        searchDrawingEditText.setText("");
     }
 */
    @OnClick({R.id.image_auto_sync})
    public void onClickAutoSync() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        AutoSycFolderDialog tagsDialog = AutoSycFolderDialog.newInstance(drwFolderId, projectId);
        tagsDialog.show(ft, "");
    }

    @OnClick({R.id.image_bug_report})
    public void onClickBugReport() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        BugReportDialog bugReportDialog = new BugReportDialog(this::callReportBugAPI);
        Bundle bundle = new Bundle();
        bugReportDialog.setCancelable(false);
        bugReportDialog.setArguments(bundle);
        bugReportDialog.show(ft, "");
    }

    /**
     * * post the log file //callReportBugAPI
     * *
     * * @param comments - user report comments
     */
    private void callReportBugAPI(String comments) {
        Date lastUpdate = drawingFolder.getLastUpdateXml();
        String lastUpdateStr = "1970-01-01 01:01:01";
        if (lastUpdate != null) {
            lastUpdateStr = DateFormatter.formatDateTimeForService(lastUpdate);

        }
        LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS),
                LoginResponse.class));
        if (isLoading) {
            return;
        }
        isLoading = true;

        File file = new File(FileUtils.getFileName());
        RequestBody reqFile = RequestBody.create(MediaType.parse("text/plain"), new File(FileUtils.getFileName()));

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        builder.addFormDataPart("user_id", String.valueOf(loginResponse.getUserDetails().getUsers_id()));
        builder.addFormDataPart("comments", comments);
        builder.addFormDataPart("folder_name", drawingFolder.getFolderName());
        builder.addFormDataPart("folder_id", String.valueOf(drwFolderId));
        builder.addFormDataPart("file", file.getName(), reqFile);
        builder.addFormDataPart("platform", "android");
        builder.addFormDataPart("version", BuildConfig.VERSION_NAME);
        RequestBody finalRequestBody = builder.build();

        mProjectDrawingListProvider.reportBugs(lastUpdateStr, finalRequestBody, new ProviderResult<String>() {
            @Override
            public void success(String result) {
                Toast.makeText(DrawingListTabActivity.this, result, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void AccessTokenFailure(String result) {
                Toast.makeText(DrawingListTabActivity.this, result, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(String result) {
                Toast.makeText(DrawingListTabActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @OnClick({R.id.leftImageView})
    public void backImageClick() {
        onBackPressed();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void updateViewPager() {
        tabLayout.removeAllTabs();
        mDrawingDisciplineList = mDrawingListRepository.getDrawingListdrwDiscipline(drwFolderId);
        mSectionsPagerAdapter = new DrawingSectionsPagerAdapter(getSupportFragmentManager(), mDrawingDisciplineList);
        int selectTab = 0;
        addingTab = true;
        for (int i = 0; i < mDrawingDisciplineList.size(); i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setText(mDrawingDisciplineList.get(i).getDrawingDiscipline());
            tabLayout.addTab(tab);
            mSectionsPagerAdapter.addFragment(new DrawingListFragment(mDrawingDisciplineList.get(i)), mDrawingDisciplineList.get(i).getDrawingDiscipline(), i);
            if (lastSelectedTab != null && mDrawingDisciplineList.get(i).getDrawingDiscipline().equals(lastSelectedTab)) {
                selectTab = i;
            }

        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (mSectionsPagerAdapter != null && mSectionsPagerAdapter.getCurrentFragment() != null) {
                    ((DrawingListFragment) mSectionsPagerAdapter.getCurrentFragment()).setOfflineMode(isOffline);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        addingTab = false;
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (!addingTab) {
                    lastSelectedTab = mDrawingDisciplineList.get(tab.getPosition()).getDrawingDiscipline();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setOffscreenPageLimit(mDrawingDisciplineList.size());
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                tabLayout.getTabAt(i).select();
                if (!addingTab) {
                    lastSelectedTab = mDrawingDisciplineList.get(i).getDrawingDiscipline();
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mSectionsPagerAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(selectTab);
        if (mSectionsPagerAdapter != null && mSectionsPagerAdapter.getCurrentFragment() != null) {
            ((DrawingListFragment) mSectionsPagerAdapter.getCurrentFragment()).updateSyncInfo(showNotificationView);
        }
    }


    public void updateSyncInfo(SyncUpdateResponse result, boolean isUpdateLastUpdatedDate) {
        isLoading = false;
        Log.e("Drawing", " ************ updateSyncInfo: " + result);
        Log.e("Drawing", " ************ updateSyncInfo: isUpdateLastUpdatedDate  " + isUpdateLastUpdatedDate);
//        swipeRefreshLayout.setRefreshing(false);
        List<Drawings> drawingAnnotations = new ArrayList<>();
        List<Drawings> newDrawing = new ArrayList<>();
        syncNewDrawing = new ArrayList<>();
        boolean isHavingUpdate = false;
//        lastUpdatedTextView.setText("Drawing List Updated: " + DateFormatter.getTimeAgo(drawingFolder.getLastupdatedate().getTime()));
        boolean autoSync = mDrawingListRepository.getDrawingFolderDetail(projectId, drwFolderId).getSyncFolder();
        if (result.getData().getCount() == 0) {
//            notificationView.setVisibility(View.GONE);
            mDrawingAnnotations = drawingAnnotations;
            if (mSectionsPagerAdapter != null && mSectionsPagerAdapter.getCurrentFragment() != null) {
                ((DrawingListFragment) mSectionsPagerAdapter.getCurrentFragment()).stopSwipeRefresh();
            }
        } else {

            if (isUpdateLastUpdatedDate) {

                for (Drawings drawingUpdate : result.getData().getDrawings()) {
                    Log.e("Drawing", " ************ updateSyncInfo: drawingUpdate  " + drawingUpdate);
//                    DrawingList originalDrawing = mDrawingListRepository.getAnySyncDrawing(drwFolderId, drawingUpdate.getOriginalDrwId());
                    DrawingList originalDrawing = mDrawingListRepository.getDrawing(drwFolderId, drawingUpdate.getOriginalDrwId(), drawingUpdate.getRevisionNumber());
                    if (originalDrawing == null) {
                        newDrawing.add(drawingUpdate);
                    } else if (originalDrawing.getPdfStatus() == PDFSynEnum.SYNC.ordinal()) {
                        //TODO: check Update at with last synced drawing update.
                        String pattern = "yyyy-MM-dd HH:mm:ss";
                        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.US);
                        try {
                            Date one = dateFormat.parse(drawingUpdate.getUpdatedAt());
                            Date two = originalDrawing.getUpdatedAt();
                            if (one.after(two) || autoSync) {
                                isHavingUpdate = true;
                                if (originalDrawing.getOriginalDrwId() == drawingUpdate.getOriginalDrwId()) {
                                    drawingAnnotations.add(drawingUpdate);
                                    isHavingUpdate = true;
                                }
                            }
                            if (originalDrawing.getCurrentRevision() != drawingUpdate.getCurrentRevision()) {
                                drawingAnnotations.add(drawingUpdate);
                                syncNewDrawing.add(drawingUpdate);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        drawingAnnotations.add(drawingUpdate);
                        newDrawing.add(drawingUpdate);
                    }
                }
            }

            mDrawingAnnotations = drawingAnnotations;
            if (syncNewDrawing.size() > 0 && !autoSync) {
//                notificationView.setVisibility(View.VISIBLE);
                if (mSectionsPagerAdapter != null && mSectionsPagerAdapter.getCurrentFragment() != null) {
                    ((DrawingListFragment) mSectionsPagerAdapter.getCurrentFragment()).updateSyncInfo(true);
                }
                showNotificationView = true;

            } else {
//                notificationView.setVisibility(View.GONE);
                if (mSectionsPagerAdapter != null && mSectionsPagerAdapter.getCurrentFragment() != null) {
                    ((DrawingListFragment) mSectionsPagerAdapter.getCurrentFragment()).updateSyncInfo(false);
                }
                showNotificationView = false;
            }
            if (isHavingUpdate) {
                if (autoSync) {
                    if (NetworkService.isNetworkAvailable(DrawingListTabActivity.this)) {
                        drawingFolder.setLastUpdateXml(updatedAt);
                        mProjectDrawingListProvider.updateDrawingFolder(drawingFolder);
                    }
                }
                // Check if auto sync is enabled auto download the annotation
                if (drawingFolder.getSyncFolder() || mAction.compareTo(DrawingAction.MANUAL) == 0
                        || !drawingAnnotations.isEmpty()) {
                    // This should be punchvisible if there is any update of downloaded revision

                    updateDrawingAnnotations(drawingAnnotations, true);
                }
            }
            if (!autoSync) {
                updateDrawingAnnotations(newDrawing, autoSync);
                mAction = DrawingAction.NEW_DRAWING;
//                if (syncNewDrawing.size() != 0) {
                updatedAt = null;
//                }
                callDrawingListAPI(drwFolderId, mAction, false, false);
            } else if (autoSync) {
                updateDrawingAnnotations(mDrawingAnnotations, autoSync);
                mAction = DrawingAction.MANUAL;
                callDrawingListAPI(drwFolderId, mAction, false, false);
//                }
            }

        }

        mAction = DrawingAction.NON;
    }

    public boolean isShowNotificationView() {
        return showNotificationView;
    }

    public ArrayList<Drawings> getSyncNewDrawing() {
        return syncNewDrawing;
    }

    public boolean getOffflineMode() {
        return isOffline;

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

    public boolean isLoading() {
        return isLoading;
    }

    public interface onReportBugListener {
        void onReportBug(String comments);
    }
}
