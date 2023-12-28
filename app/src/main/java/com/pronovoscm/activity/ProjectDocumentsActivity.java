package com.pronovoscm.activity;

import static android.Manifest.permission.READ_MEDIA_IMAGES;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pronovoscm.R;
import com.pronovoscm.adapter.DocumentFolderFilesListAdapter;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.model.response.documents.ProjectDocumentFilesResponse;
import com.pronovoscm.model.response.documents.ProjectDocumentFoldersResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.PjDocumentsFiles;
import com.pronovoscm.persistence.domain.PjDocumentsFolders;
import com.pronovoscm.persistence.repository.ProjectDocumentsRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.DocumentsFolderFileAdapterItem;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.ui.CustomProgressBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProjectDocumentsActivity extends BaseActivity implements View.OnClickListener {

    @Inject
    ProjectsProvider projectsProvider;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.searchDocumentEditText)
    EditText searchDocumentEditText;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.documentsRecyclerView)
    RecyclerView documentsRecyclerView;
    @BindView(R.id.lastUpdatedTextView)
    TextView lastUpdatedTextView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;


    boolean isLoading;
    @Inject
    ProjectDocumentsRepository projectDocumentsRepository;
    @BindView(R.id.seachClearImageView)
    ImageView searchClearImageView;
    private String projectName;
    private int projectId;
    private long mLastClickTime = 0;
    private long mMenuLastClickTime = 0;
    private boolean isFolderApiDone = false;
    private boolean folderListApiSuccess = false;
    private boolean isFilesApiDone = false;
    private LoginResponse loginResponse;
    private ArrayList<DocumentsFolderFileAdapterItem> adapterItemList = new ArrayList<>();
    private DocumentFolderFilesListAdapter folderFilesListAdapter;
    private boolean isOffline = false;

    @Override
    protected int doGetContentView() {
        return R.layout.activity_project_documents;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        doGetApplication().getDaggerComponent().inject(this);
        projectName = getIntent().getStringExtra(Constants.INTENT_KEY_PROJECT_NAME);
        projectId = getIntent().getIntExtra(Constants.INTENT_KEY_PROJECT_ID, 0);
        backImageView.setOnClickListener(this);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.INVISIBLE);

        titleTextView.setText(getString(R.string.documents));
        setAdapter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, Constants.FILESTORAGE_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.FILESTORAGE_REQUEST_CODE);
            }

        } else {
            callDocumentFilesList(true);
            callDocumentFoldersList(true);
        }
        setSearchTextWatcher();
        setSwipListner();

    }

    @OnClick({R.id.seachClearImageView})
    public void clickSearchClear() {
        searchDocumentEditText.setText("");

    }

    private void setSwipListner() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (!isOffline) {
                if (!isLoading) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
                        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, Constants.FILESTORAGE_REQUEST_CODE);
                        } else {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.FILESTORAGE_REQUEST_CODE);
                        }
                    } else {
                        callDocumentFoldersList(false);
                        callDocumentFilesList(false);
                    }
                }
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void setAdapter() {
        documentsRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        isOffline = NetworkService.isNetworkAvailable(ProjectDocumentsActivity.this);
        folderFilesListAdapter = new DocumentFolderFilesListAdapter(projectId, adapterItemList,
                ProjectDocumentsActivity.this, isOffline, projectDocumentsRepository);
        documentsRecyclerView.setAdapter(folderFilesListAdapter);
    }

    private void setSearchTextWatcher() {
        searchDocumentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // mAlbumList.addAll(photoFolders);
                // mAlbumAdapter.notifyDataSetChanged();
                //loadFolderFilesList();
                if (TextUtils.isEmpty(s.toString())) {
                    searchClearImageView.setVisibility(View.INVISIBLE);
                } else {
                    searchClearImageView.setVisibility(View.VISIBLE);
                }
                adapterItemList.clear();
                adapterItemList = getAdapterItemList();
                folderFilesListAdapter.setAdapterItemList(adapterItemList);
                folderFilesListAdapter.notifyDataSetChanged();
                if (adapterItemList.size() == 0) {
                    noRecordTextView.setText(getString(R.string.no_results));
                    noRecordTextView.setVisibility(View.VISIBLE);
                } else {
                    noRecordTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            isOffline = true;
            offlineTextView.setVisibility(View.VISIBLE);

        } else {
            isOffline = false;
            offlineTextView.setVisibility(View.GONE);

        }
        folderFilesListAdapter.deviceOffline(isOffline);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftImageView:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        swipeRefreshLayout.setRefreshing(false);
        if (grantResults.length > 0 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == Constants.FILESTORAGE_REQUEST_CODE) {
            callDocumentFilesList(true);
            callDocumentFoldersList(true);
        }
    }

 /*   private void callProjectFilesApi() {
        if (NetworkService.isNetworkAvailable(ProjectDocumentsActivity.this)) {
            CustomProgressBar.showDialog(ProjectDocumentsActivity.this);

        } else {
            //TODO show records form DB here
        }
    }*/

    private void handleAccessTokenFails() {
        startActivity(new Intent(ProjectDocumentsActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        SharedPref.getInstance(ProjectDocumentsActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
        SharedPref.getInstance(ProjectDocumentsActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
        finish();
    }

    private void callDocumentFilesList(boolean showProgress) {
        if (NetworkService.isNetworkAvailable(ProjectDocumentsActivity.this)) {
            if (showProgress)
                CustomProgressBar.showDialog(ProjectDocumentsActivity.this);

            projectsProvider.getProjectDocumentFiles(projectId, new ProviderResult<ProjectDocumentFilesResponse>() {
                @Override
                public void success(ProjectDocumentFilesResponse result) {

                    Log.d("ProjectDocumentsActi", "callDocumentFilesList success: ");
                    isFilesApiDone = true;
                    // callProjectFilesApi();
                    if (isFolderApiDone) {
                        CustomProgressBar.dissMissDialog(ProjectDocumentsActivity.this);
                    }
                    loadFolderFilesList();
                }


                @Override
                public void AccessTokenFailure(String message) {
                    CustomProgressBar.dissMissDialog(ProjectDocumentsActivity.this);
                    noRecordTextView.setText("");
                    isLoading = false;
                    handleAccessTokenFails();
                }

                @Override
                public void failure(String message) {
                    CustomProgressBar.dissMissDialog(ProjectDocumentsActivity.this);
                    noRecordTextView.setText("");
                    isLoading = false;
                    isFilesApiDone = true;
                    loadFolderFilesList();
                   /* mActivity.startActivity(new Intent(mActivity, ProjectDocumentsActivity.class).putExtra(Constants.INTENT_KEY_PROJECT_NAME, projectName)
                            .putExtra(Constants.INTENT_KEY_PROJECT_ID, projectId));*/
                    //TODO show records form DB here
                }
            });


        } else {
            isFilesApiDone = true;
            loadFolderFilesList();
            //TODO show records form DB here
        }
    }

    private void loadFolderFilesList() {

        List<PjDocumentsFolders> documentsFoldersArrayList = null;
        List<PjDocumentsFiles> documentsFilesArrayList = null;
        Log.d("ProjectDocumentsActi", "loadFolderFilesList: isFilesApiDone " + isFilesApiDone + "      isFolderApiDone  " + isFolderApiDone);
        if (isFilesApiDone && isFolderApiDone) {
            swipeRefreshLayout.setRefreshing(false);
            if (folderListApiSuccess) {
                lastUpdatedTextView.setText(R.string.folder_list_updated_just_now);
            }
            adapterItemList.clear();
            adapterItemList = getAdapterItemList();
            CustomProgressBar.dissMissDialog(ProjectDocumentsActivity.this);

        }

        Log.d("ProjectDocumentsActi", "loadFolderFilesList: adapterItemList " + adapterItemList.size());
        folderFilesListAdapter.setAdapterItemList(adapterItemList);
        folderFilesListAdapter.notifyDataSetChanged();
        if (adapterItemList == null || adapterItemList.size() == 0) {
            noRecordTextView.setText(getString(R.string.document_folder_no_reord_message));
            noRecordTextView.setVisibility(View.VISIBLE);
        } else {
            noRecordTextView.setVisibility(View.GONE);
        }

    }

    private ArrayList<DocumentsFolderFileAdapterItem> getAdapterItemList() {
        List<PjDocumentsFolders> documentsFoldersArrayList = null;
        List<PjDocumentsFiles> documentsFilesArrayList = null;
        documentsFoldersArrayList = projectDocumentsRepository.getPjDocumentsFoldersList(projectId, 1, searchDocumentEditText.getText().toString());
        documentsFilesArrayList = projectDocumentsRepository.getPjDocumentsFilesList(projectId, 1, searchDocumentEditText.getText().toString());

        if (documentsFoldersArrayList != null && documentsFoldersArrayList.size() > 0 && !folderListApiSuccess) {
            lastUpdatedTextView.setText(getString(R.string.folder_list_updated, DateFormatter.getTimeAgo(documentsFoldersArrayList.get(0).getLastupdatedate().getTime())));
        }
        if (documentsFoldersArrayList != null && documentsFoldersArrayList.size() > 0) {
            for (PjDocumentsFolders folder : documentsFoldersArrayList) {
                DocumentsFolderFileAdapterItem folderItem = new DocumentsFolderFileAdapterItem();
                folderItem.setAdapterItemType(Constants.ADAPTER_ITEM_TYPE_DOCUMENT_FOLDER);
                folderItem.setPjDocumentsFolders(folder);
                adapterItemList.add(folderItem);
            }
        }
        if (documentsFilesArrayList != null && documentsFilesArrayList.size() > 0) {
            for (PjDocumentsFiles files : documentsFilesArrayList) {
                DocumentsFolderFileAdapterItem fileAdapterItem = new DocumentsFolderFileAdapterItem();
                fileAdapterItem.setAdapterItemType(Constants.ADAPTER_ITEM_TYPE_DOCUMENT_FILE);
                fileAdapterItem.setPjDocumentsFiles(files);
                adapterItemList.add(fileAdapterItem);
            }
        }
        return adapterItemList;
    }

    private void callDocumentFoldersList(boolean showProgress) {
        if (NetworkService.isNetworkAvailable(ProjectDocumentsActivity.this)) {
            if (showProgress)
                CustomProgressBar.showDialog(ProjectDocumentsActivity.this);

            projectsProvider.getProjectDocumentFolders(projectId, new ProviderResult<ProjectDocumentFoldersResponse>() {
                @Override
                public void success(ProjectDocumentFoldersResponse result) {
                    Log.d("ProjectDocumentsActi", " callDocumentFoldersList success: ");
                    isFolderApiDone = true;
                    folderListApiSuccess = true;
                    if (isFilesApiDone) {
                        CustomProgressBar.dissMissDialog(ProjectDocumentsActivity.this);
                    }
                    projectDocumentsRepository.updateLastUpdatedTime(projectId);
                    loadFolderFilesList();
                }


                @Override
                public void AccessTokenFailure(String message) {
                    CustomProgressBar.dissMissDialog(ProjectDocumentsActivity.this);
                    noRecordTextView.setText("");
                    isLoading = false;
                    handleAccessTokenFails();
                }

                @Override
                public void failure(String message) {
                    CustomProgressBar.dissMissDialog(ProjectDocumentsActivity.this);
                    noRecordTextView.setText("");
                    isLoading = false;
                    isFolderApiDone = true;
                    loadFolderFilesList();
                   /* mActivity.startActivity(new Intent(mActivity, ProjectDocumentsActivity.class).putExtra(Constants.INTENT_KEY_PROJECT_NAME, projectName)
                            .putExtra(Constants.INTENT_KEY_PROJECT_ID, projectId));*/

                }
            });


        } else {
            isFolderApiDone = true;
            loadFolderFilesList();

        }
    }

}
