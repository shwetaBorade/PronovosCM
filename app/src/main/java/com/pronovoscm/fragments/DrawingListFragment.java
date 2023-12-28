package com.pronovoscm.fragments;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.activity.BaseActivity;
import com.pronovoscm.activity.DrawingListTabActivity;
import com.pronovoscm.adapter.DrawingListAdapter;
import com.pronovoscm.chipslayoutmanager.util.log.Log;
import com.pronovoscm.data.DrawingAnnotationProvider;
import com.pronovoscm.data.NetworkStateProvider;
import com.pronovoscm.data.PDFFileDownloadProvider;
import com.pronovoscm.data.ProjectDrawingListProvider;
import com.pronovoscm.data.PunchListProvider;
import com.pronovoscm.model.DrawingAction;
import com.pronovoscm.model.response.syncupdate.Drawings;
import com.pronovoscm.persistence.domain.DrawingFolders;
import com.pronovoscm.persistence.domain.DrawingList;
import com.pronovoscm.persistence.domain.DrawingXmls;
import com.pronovoscm.persistence.repository.DrawingListRepository;
import com.pronovoscm.persistence.repository.PunchListRepository;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.dialogs.MessageDialog;
import com.pronovoscm.utils.ui.RecyclerSectionItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("ValidFragment")
public class DrawingListFragment extends BaseFragment {
    public static final int FILESTORAGE_REQUEST_CODE = 221;
    static DrawingListFragment drawingListFragment = new DrawingListFragment();
    @Inject
    DrawingAnnotationProvider mDrawingAnnotationProvider;
    @Inject
    PunchListProvider mPunchListProvider;
    @Inject
    PunchListRepository mPunchListRepository;
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
    //    @BindView(R.id.leftImageView)
//    ImageView backImageView;
//    @BindView(R.id.rightImageView)
//    ImageView rightImageView;
//    @BindView(R.id.cameraImageView)
//    ImageView cameraImageView;
//    @BindView(R.id.image_auto_sync)
//    ImageView imageViewAutoSync;
//    @BindView(R.id.titleTextView)
//    TextView titleTextView;
    @BindView(R.id.drawingsRecyclerView)
    RecyclerView drawingsRecyclerView;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
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
    private DrawingList headerDrawingList;
    private List<DrawingList> mDrawingArrayList;
    private RecyclerSectionItemDecoration sectionItemDecoration;
    private DrawingListAdapter mDrawingListAdapter;
    private List<Drawings> mDrawingAnnotations;
    private int drwFolderId;
    private int projectId;
    private DrawingFolders drawingFolder;
    private boolean isLoading = false;
    private boolean isOffline = false;
    private boolean isFirstLoading = true;
    private MessageDialog messageDialog;
    private DrawingAction mAction = DrawingAction.NON;
    private Date updatedAt;

    public DrawingListFragment() {
    }


    @SuppressLint("ValidFragment")
    public DrawingListFragment(DrawingList drawingList) {
        headerDrawingList = drawingList;
    }

    public static DrawingListFragment newInstance(DrawingList drawingList) {
        drawingListFragment = new DrawingListFragment();
        return drawingListFragment;
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
        drwFolderId = getActivity().getIntent().getIntExtra("drw_folder_id", 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getActivity(),getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
        } else {
            if (mDrawingListRepository.getDrawingList(drwFolderId).size() == 0 || mDrawingListRepository.getDrawingFolderDetail(projectId, drwFolderId).getSyncFolder()) {
            } else {
                List<DrawingXmls> drawingXmls = mAnnotationProvider.getNotSyncAnnotations();
                if (drawingXmls.size() <= 0) {
//                    getDrawingUpdates();
                }
                mDrawingArrayList.clear();
                if (headerDrawingList != null) {
                    mDrawingArrayList.addAll(mProjectDrawingListProvider.getSearchDrawing(drwFolderId, searchDrawingEditText.getText().toString(), headerDrawingList));
                    mDrawingListAdapter.notifyDataSetChanged();
                }
                if (mDrawingArrayList.size() == 0) {
                    noRecordTextView.setText(R.string.drawings_have_not_yet_been);
                    noRecordTextView.setVisibility(View.VISIBLE);
                } else {
                    noRecordTextView.setVisibility(View.GONE);

                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (headerDrawingList != null && headerDrawingList.getDrawingsId() == -1) {
            drawingsRecyclerView.removeItemDecoration(sectionItemDecoration);
            sectionItemDecoration = new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen._14sdp), true, getSectionCallback(mDrawingArrayList));
            drawingsRecyclerView.addItemDecoration(sectionItemDecoration);
        } else if (headerDrawingList != null) {
            if (sectionItemDecoration != null)
                drawingsRecyclerView.removeItemDecoration(sectionItemDecoration);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getActivity(),getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
            noRecordTextView.setVisibility(View.GONE);
        } else {
            if (mDrawingArrayList.size() == 0) {
                noRecordTextView.setText("Drawings have not yet been uploaded to this folder.");
                noRecordTextView.setVisibility(View.VISIBLE);
            } else {
                noRecordTextView.setVisibility(View.GONE);

            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        swipeRefreshLayout.setRefreshing(false);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == FILESTORAGE_REQUEST_CODE) {
        }
    }
    /*

     *//**
     * Sync those pdf which are not synced yet
     *//*
    private void syncPDF() {
        for (DrawingList drawingList : mDrawingArrayList) {
            if (drawingList.getPdfStatus() == PDFSynEnum.PROCESSING.ordinal() || drawingList.getPdfStatus() == PDFSynEnum.SYNC_FAILED.ordinal()) {
                onDownloadPDF(drawingList);
            }
        }
    }*/


    /**
     * Check that File is exist in storage or not.
     *
     * @param fileName
     * @return
     */
    private boolean isFileExist(String fileName) {
        String root = getActivity().getFilesDir().getAbsolutePath();
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
            buttonUpdate.setEnabled(false);
        } else {
            buttonUpdate.setEnabled(true);
        }
       /* if (event) {
            isOffline = true;
            mDrawingListAdapter.deviceOffline(true);
            buttonUpdate.setEnabled(false);
            List<DrawingList> drawingArrayList = mProjectDrawingListProvider.getProcessingDrawings(drwFolderId);
            for (DrawingList drawingList : drawingArrayList) {
                mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.SYNC_FAILED.ordinal());
            }
            mDrawingListAdapter.notifyDataSetChanged();
        } else {
            isOffline = false;
            buttonUpdate.setEnabled(true);
            mDrawingListAdapter.deviceOffline(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getActivity(),getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
            } else {
                drawingFolder = mProjectDrawingListProvider.getDrawingFolderDetail(projectId, drwFolderId);
                if (drawingFolder.getSyncFolder()) {
                }
//                syncPDF();
            }
        }*/
    }

    /*
     */
/**
 * download the pdf if there is any event from event bus
 *
 * @param drawingList
 *//*

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadPDF(DrawingList drawingList) {
        mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.PROCESSING.ordinal());
        LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        mPDFFileDownloadProvider.getDrawingPDF(drawingList, loginResponse.getUserDetails().getUsers_id(), new ProviderResult<Boolean>() {
            @Override
            public void success(Boolean result) {
                Log.i("FileDownload", "success: " + result);
                if (result) {
                    getDrawingAnnotation(drawingList);
                } else {

                    mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.SYNC_FAILED.ordinal());
                }
                mDrawingArrayList.clear();
                mDrawingArrayList.addAll(mProjectDrawingListProvider.getSearchDrawing(drwFolderId, searchDrawingEditText.getText().toString(), headerDrawingList));
                mDrawingListAdapter.notifyDataSetChanged();
            }

            @Override
            public void AccessTokenFailure(String message) {
                mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.NOTSYNC.ordinal());

            }

            @Override
            public void failure(String message) {
                Log.e("TAG", "failure PDF: " + message);
                if (!mNetworkStateProvider.isOffline() && !isOffline) {
                    Log.e("TAG", "failure PDF: 1 " + message);
                    mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.SYNC_FAILED.ordinal());
                    mDrawingArrayList.clear();
                    mDrawingArrayList.addAll(mProjectDrawingListProvider.getSearchDrawing(drwFolderId, searchDrawingEditText.getText().toString(), headerDrawingList));
                    mDrawingListAdapter.notifyDataSetChanged();
                    messageDialog.showMessageAlert(getActivity(), message, getString(R.string.ok));
                }
            }
        });
    }

*/

    /**
     * Get the annotation of the drawing from the server
     */
   /* private void getDrawingAnnotation(DrawingList drawingList) {
        LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        mAnnotationProvider.getDrawingAnnotations(drwFolderId, drawingList.getDrawingsId(), new ProviderResult<String>() {
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
                        mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.NOTSYNC.ordinal());
                    }
                    mDrawingArrayList.clear();
                    mDrawingArrayList.addAll(mProjectDrawingListProvider.getSearchDrawing(drwFolderId, searchDrawingEditText.getText().toString(), headerDrawingList));
                    mDrawingListAdapter.notifyDataSetChanged();
                } catch (MalformedURLException m) {
                    m.printStackTrace();
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(getActivity()).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(getActivity()).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.NOTSYNC.ordinal());
                getActivity().finish();
            }

            @Override
            public void failure(String message) {
                mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.SYNC_FAILED.ordinal());
                mDrawingArrayList.clear();
                mDrawingArrayList.addAll(mProjectDrawingListProvider.getSearchDrawing(drwFolderId, searchDrawingEditText.getText().toString(), headerDrawingList));
                mDrawingListAdapter.notifyDataSetChanged();
                messageDialog.showMessageAlert(getActivity(), message, getString(R.string.ok));
            }
        });
    }
*/
    @OnClick({R.id.seachClearImageView})
    public void clickSearchClear() {
        searchDrawingEditText.setText("");
    }

    @OnClick({R.id.updateTextView})
    public void onClickUpdateAnnotation() {
        if (((DrawingListTabActivity) (getActivity())).updateDrawingAnnotations(((DrawingListTabActivity) (getActivity())).getSyncNewDrawing(), true)) {
            mAction = DrawingAction.MANUAL;
            ((DrawingListTabActivity) getActivity()).callDrawingListAPI(drwFolderId, mAction, false, true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.drawing_list_view_fragment, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
        messageDialog = new MessageDialog();
        drwFolderId = getActivity().getIntent().getIntExtra("drw_folder_id", 0);
        projectId = getActivity().getIntent().getIntExtra("project_id", 0);
        drawingFolder = mProjectDrawingListProvider.getDrawingFolderDetail(projectId, drwFolderId);

        mDrawingArrayList = new ArrayList<>();
        searchDrawingEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDrawingArrayList.clear();
                if (headerDrawingList != null) {
                    mDrawingArrayList.addAll(mProjectDrawingListProvider.getSearchDrawing(drwFolderId, s.toString(), headerDrawingList));
                    mDrawingListAdapter.notifyDataSetChanged();
                    if (TextUtils.isEmpty(s.toString())) {
                        searchClearImageView.setVisibility(View.INVISIBLE);
                    } else {
                        searchClearImageView.setVisibility(View.VISIBLE);
                    }

                }
               /* if (mDrawingArrayList.size()==0){
                    noRecordTextView.setText("Drawings have not yet been uploaded to this folder.");
                    noRecordTextView.setVisibility(View.VISIBLE);
                }else {
                    noRecordTextView.setVisibility(View.GONE);

                }
*/
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        if (headerDrawingList != null) {
            mDrawingListAdapter = new DrawingListAdapter(getActivity(), mDrawingArrayList, isOffline, projectId, headerDrawingList.getDrawingsId());
            drawingsRecyclerView.setHasFixedSize(true);
            drawingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            if (headerDrawingList.getDrawingsId() == -1) {
                sectionItemDecoration = new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen.dashboard_text_size), true, getSectionCallback(mDrawingArrayList));
                drawingsRecyclerView.addItemDecoration(sectionItemDecoration);
            } else {
                if (sectionItemDecoration != null)
                    drawingsRecyclerView.removeItemDecoration(sectionItemDecoration);

            }
            drawingsRecyclerView.setAdapter(mDrawingListAdapter);
        }
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (!isLoading) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getActivity(),getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{getExternalPermission()}, FILESTORAGE_REQUEST_CODE);
                } else {
                    if (mDrawingListRepository.getDrawingList(drwFolderId).size() == 0 || mDrawingListRepository.getDrawingFolderDetail(projectId, drwFolderId).getSyncFolder()) {
                        mAction = DrawingAction.MANUAL;
                        ((DrawingListTabActivity) getActivity()).callDrawingListAPI(drwFolderId, mAction, false, true);
                        Log.e("Drawing","Swipe");
                    } else {

//                        List<DrawingXmls> drawingXmls = mAnnotationProvider.getNotSyncAnnotations();
//                        if (drawingXmls.size() <= 0) {
                        ((DrawingListTabActivity) getActivity()).getDrawingUpdates();

//                        }
                        mDrawingArrayList.clear();
                        mDrawingArrayList.addAll(mProjectDrawingListProvider.getSearchDrawing(drwFolderId, searchDrawingEditText.getText().toString(), headerDrawingList));
                        mDrawingListAdapter.notifyDataSetChanged();


                    }
                }
            }
        });

        Date dateTime = drawingFolder.getLastupdatedate();
        if (dateTime == null) {
            lastUpdatedTextView.setText("Drawing List Updated: Just Now");
        } else {
            lastUpdatedTextView.setText("Drawing List Updated: " + DateFormatter.getTimeAgo(drawingFolder.getLastupdatedate().getTime()));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getActivity(),getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
            noRecordTextView.setVisibility(View.GONE);
        } else {    if (mDrawingArrayList.size() == 0 && !((DrawingListTabActivity) getActivity()).isLoading()) {
            noRecordTextView.setText("Drawings have not yet been uploaded to this folder.");
            noRecordTextView.setVisibility(View.VISIBLE);
        } else {
            noRecordTextView.setVisibility(View.GONE);

        }}

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getActivity(),getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{getExternalPermission()}, FILESTORAGE_REQUEST_CODE);
        } else {
        }
        updateSyncInfo(((DrawingListTabActivity) getActivity()).isShowNotificationView());

        if (mDrawingListAdapter != null) {
            isOffline = ((DrawingListTabActivity) getActivity()).getOffflineMode();
            mDrawingListAdapter.deviceOffline(isOffline);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (searchDrawingEditText != null && headerDrawingList != null && mDrawingListAdapter != null && isVisibleToUser) {
            if (mDrawingArrayList != null) {
                mDrawingArrayList.clear();
            }
            mDrawingArrayList.addAll(mProjectDrawingListProvider.getSearchDrawing(drwFolderId, searchDrawingEditText.getText().toString(), headerDrawingList));
            mDrawingListAdapter.notifyDataSetChanged();
            if(getActivity()!=null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getActivity(),getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
                noRecordTextView.setVisibility(View.GONE);
            } else {
                if (mDrawingArrayList.size() == 0) {
                    noRecordTextView.setText("Drawings have not yet been uploaded to this folder.");
                    noRecordTextView.setVisibility(View.VISIBLE);
                } else {
                    noRecordTextView.setVisibility(View.GONE);

                }
            }
        }
        if (notificationView != null && getActivity() != null) {
            updateSyncInfo(((DrawingListTabActivity) getActivity()).isShowNotificationView());
        }
        if (swipeRefreshLayout != null) {
            stopSwipeRefresh();
        }

        if (mDrawingListAdapter != null) {
            mDrawingListAdapter.deviceOffline(isOffline);
        }
    }


    public void updateSyncInfo(boolean showUpdateView) {
        if (notificationView != null) {
            if (showUpdateView) {
                notificationView.setVisibility(View.VISIBLE);
            } else {
                notificationView.setVisibility(View.GONE);
            }
        }
    }


    public void stopSwipeRefresh() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void setOfflineMode(boolean isOffline) {
        this.isOffline = isOffline;
        if (mDrawingListAdapter != null) {
            mDrawingListAdapter.deviceOffline(isOffline);
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
}
