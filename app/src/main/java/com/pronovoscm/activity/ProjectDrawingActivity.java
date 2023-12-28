package com.pronovoscm.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.adapter.DrawingFolderAdapter;
import com.pronovoscm.data.ProjectDrawingFolderProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.model.request.drawingfolder.DrawingFolderRequest;
import com.pronovoscm.persistence.domain.DrawingFolders;
import com.pronovoscm.utils.FileUtils;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.dialogs.MessageDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class ProjectDrawingActivity extends BaseActivity implements View.OnClickListener {
    @Inject
    ProjectDrawingFolderProvider mProjectDrawingProvider;


    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.cameraImageView)
    ImageView cameraImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.drawingRecyclerView)
    RecyclerView drawingRecyclerView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
    @BindView(R.id.searchView)
    RelativeLayout mSearchView;
    @BindView(R.id.searchDrawingEditText)
    EditText mSearchDrawingEditText;

    private DrawingFolderAdapter mDrawingFolderAdapter;
    private List<DrawingFolders> drawingFolderList;
    private int projectId;
    private String projectName;
    private MessageDialog messageDialog;


    @Override
    protected int doGetContentView() {
        return R.layout.project_drawing_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        titleTextView.setText(getString(R.string.drawings));
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        backImageView.setOnClickListener(this);
        messageDialog = new MessageDialog();
        projectName = getIntent().getStringExtra("project_name");
        projectId = getIntent().getIntExtra("project_id", 0);

        drawingRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        /*Configuration newConfig = getResources().getConfiguration();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // set background for landscape
            drawingRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // set background for portrait
            drawingRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        }*/
        drawingRecyclerView.setAdapter(mDrawingFolderAdapter);
        rightImageView.setVisibility(View.INVISIBLE);
        cameraImageView.setVisibility(View.GONE);
        mSearchDrawingEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<DrawingFolders> drawingFolders = mProjectDrawingProvider.getDrawingFolders(projectId, mSearchDrawingEditText.getText().toString());
                if (drawingFolderList == null) {
                    drawingFolderList = new ArrayList();
                    mDrawingFolderAdapter = new DrawingFolderAdapter(ProjectDrawingActivity.this, drawingFolderList, projectId, projectName);
                }
                drawingFolderList.clear();
                drawingFolderList.addAll(drawingFolders);
                mDrawingFolderAdapter.notifyDataSetChanged();
                if (drawingFolderList == null || drawingFolderList.size() <= 0) {
                    noRecordTextView.setText(R.string.drawing_folder_no_reord_message);
                    noRecordTextView.setVisibility(View.VISIBLE);
//                    mSearchView.setVisibility(View.GONE);
                } else {
//                    mSearchView.setVisibility(View.VISIBLE);
                    noRecordTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
     }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
        } else {
            offlineTextView.setVisibility(View.GONE);
        }
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
    public void onBackPressed() {
        FileUtils.deleteFile();
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSearchDrawingEditText.setFocusable(true);
        mSearchDrawingEditText.setFocusableInTouchMode(true);
        callProjectDrawingFolderRequest();
    }

    private void callProjectDrawingFolderRequest() {
        DrawingFolderRequest drawingFolderRequest = new DrawingFolderRequest();
        drawingFolderRequest.setProject_id(projectId);
        mProjectDrawingProvider.getDrawingFolderList(drawingFolderRequest, new ProviderResult<List<DrawingFolders>>() {
            @Override
            public void success(List<DrawingFolders> result) {
                drawingFolderList = result;
                List<DrawingFolders> drawingFolders = mProjectDrawingProvider.getDrawingFolders(projectId, mSearchDrawingEditText.getText().toString());
                drawingFolderList.clear();
                drawingFolderList.addAll(drawingFolders);
                mDrawingFolderAdapter = new DrawingFolderAdapter(ProjectDrawingActivity.this, drawingFolderList, projectId, projectName);
                drawingRecyclerView.setAdapter(mDrawingFolderAdapter);
                if (drawingFolderList == null || drawingFolderList.size() <= 0) {
                    noRecordTextView.setText(R.string.drawing_folder_no_reord_message);
                    noRecordTextView.setVisibility(View.VISIBLE);
                } else {
                    noRecordTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(ProjectDrawingActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(ProjectDrawingActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(ProjectDrawingActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(ProjectDrawingActivity.this, message, getString(R.string.ok));
//                messageDialog.showMessageAlert(ProjectDrawingActivity.this, getString(R.string.failureMessage), getString(R.string.ok));
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mSearchDrawingEditText.setFocusable(false);
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

}
