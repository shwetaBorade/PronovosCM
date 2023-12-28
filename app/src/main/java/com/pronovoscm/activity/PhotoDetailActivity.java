package com.pronovoscm.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.data.UpdatePhotoDetailsProvider;
import com.pronovoscm.model.request.updatephoto.Photo_tags;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.ImageTag;
import com.pronovoscm.persistence.domain.PhotosMobile;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.MessageEvent;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.dialogs.MessageDialog;
import com.pronovoscm.utils.dialogs.TagsDialog;
import com.pronovoscm.utils.library.AutoLabelUI;
import com.pronovoscm.utils.library.AutoLabelUISettings;
import com.pronovoscm.utils.ui.LoadImageInBackground;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;
import static com.pronovoscm.activity.DrawingPDFActivity.TAG;

/**
 * Activity to edit photo details
 *
 * @author GWL
 */
public class PhotoDetailActivity extends BaseActivity implements View.OnClickListener {
    @Inject
    ProjectsProvider mProjectsProvider;
    @Inject
    UpdatePhotoDetailsProvider mUpdatePhotoDetailsProvider;

    @BindView(R.id.photoImageView)
    ImageView photoImageView;
    @BindView(R.id.saveTextView)
    TextView saveTextView;
    @BindView(R.id.photoImageProgressBar)
    ProgressBar photoImageProgressBar;
    @BindView(R.id.upladedByTextView)
    TextView uploadedByTextView;
    @BindView(R.id.keywordAutoLabelUI)
    AutoLabelUI keywordAutoLabelUI;
    @BindView(R.id.allKeyword)
    ConstraintLayout allKeyword;
    @BindView(R.id.dateTakenTextView)
    TextView dateTakenTextView;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.cancelTextView)
    TextView cancelTextView;
    @BindView(R.id.backImageView)
    ImageView backgroundImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.descriptionEditText)
    TextInputEditText descriptionEditText;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    long photoMobileId, pjPhotosFolderMobileId;
    private PhotosMobile mPhotosMobile;
    private ArrayList<ImageTag> mSelectedImageTags = new ArrayList<>();
    private int canEditPhoto;
    @BindView(R.id.deleteImageView)
    ImageView deleteImageView;
    View.OnClickListener deleteImageViewClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog alertDialog = new AlertDialog.Builder(PhotoDetailActivity.this).create();
            alertDialog.setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_photos));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.yes), (dialog, which) -> {
                callDeletePhoto();

                //  super.onBackPressed();
            });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.no), (dialog, which) -> {
                dialog.dismiss();
            });
            alertDialog.setCancelable(false);
            alertDialog.show();
            Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(ContextCompat.getColor(PhotoDetailActivity.this, R.color.gray_948d8d));
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(PhotoDetailActivity.this, R.color.colorPrimary));

        }
    };
    @Override
    protected int doGetContentView() {
        return R.layout.photo_detail_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        doGetApplication().getDaggerComponent().inject(this);

        LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        canEditPhoto = loginResponse.getUserDetails().getPermissions().get(0).getEditPhoto();

        MessageDialog messageDialog = new MessageDialog();
        photoMobileId = getIntent().getLongExtra("photoMobileId", 0);

        if (mSelectedImageTags.size() == 0) {
            mSelectedImageTags = (ArrayList<ImageTag>) mProjectsProvider.getPhotosTag(photoMobileId, loginResponse);
            ArrayList<ImageTag> mlastSelectedImageTags = (ArrayList<ImageTag>) mProjectsProvider.getPhotosTag(photoMobileId, loginResponse);
            setAutoLabelUISettings();
        }
        loadData();
        descriptionEditText.setText(mPhotosMobile.getDescriptions());
        if (loginResponse.getUserDetails().getPermissions().get(0).getEditPhoto() == 1
                && loginResponse.getUserDetails().getPermissions().get(0).getDeletePhoto() == 1
                && loginResponse.getUserDetails().getPermissions().get(0).getUploadPhoto() == 1)
            deleteImageView.setVisibility(View.VISIBLE);
        deleteImageView.setOnClickListener(deleteImageViewClickListner);
        (PronovosApplication.getContext()).setupAndStartWorkManager();
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

    /**
     * Get the data from the intent and populate it into the UI
     */
    private void loadData() {
        if (canEditPhoto != 1) {
            descriptionEditText.setKeyListener(null);
            saveTextView.setVisibility(View.GONE);
        }
        int projectId = getIntent().getIntExtra("pjProjectId", 0);
        pjPhotosFolderMobileId = getIntent().getLongExtra("albumMobileId", 0);
        photoMobileId = getIntent().getLongExtra("photoMobileId", 0);
        mPhotosMobile = mProjectsProvider.getAlbumPhoto(projectId, pjPhotosFolderMobileId, photoMobileId);
//        LoadImage mLoadImage = new LoadImage(this);
        URI uri;
        try {
            uri = new URI(mPhotosMobile.getPhotoLocation());
            String[] segments = uri.getPath().split("/");
            String imageName = segments[segments.length - 1];
            String filePath = getFilesDir().getAbsolutePath() + "/Pronovos/";
            String[] params = new String[]{mPhotosMobile.getPhotoLocation(),filePath};
//            Object[] params = new Object[]{mPhotosMobile.getPhotoLocation(), filePath,photoImageView.getContext(),photoImageView};
            File imgFile = new File(filePath + "/" + imageName);

            if (!imgFile.exists()) {

//
                new LoadImageInBackground(new LoadImageInBackground.Listener() {
                    @Override
                    public void onImageDownloaded(Bitmap bitmap) {
                        Log.i(TAG, "onImageDownloaded: image download");
                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);

                        photoImageView.setImageDrawable(roundedBitmapDrawable);
                        backgroundImageView.setVisibility(View.GONE);
                        photoImageProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onImageDownloadError() {
                        Log.i(TAG, "onImageDownloaded: image download 2");

                        photoImageProgressBar.setVisibility(View.GONE);
                    }
                }).executeOnExecutor(THREAD_POOL_EXECUTOR, params);
            } else {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                photoImageView.setImageBitmap(myBitmap);
                photoImageProgressBar.setVisibility(View.GONE);
            }

//            mLoadImage.getRoundedImagePath(mPhotosMobile.getPhotoLocation(), "", imageName, photoImageView, photoImageProgressBar, false, backgroundImageView);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        allKeyword.setOnClickListener(this);
        uploadedByTextView.setText(mPhotosMobile.getUploadedBy());
        if (mPhotosMobile.getDateTaken() != null) {
            dateTakenTextView.setText(DateFormatter.formatAMDateForUploadImage(mPhotosMobile.getDateTaken()));
        } else {
            dateTakenTextView.setText("January 01,1970");
        }

        rightImageView.setVisibility(View.INVISIBLE);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        backImageView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);
        saveTextView.setOnClickListener(this);
        titleTextView.setText(DateFormatter.formatAMDateForUploadImage(mPhotosMobile.getDateTaken()));

//        titleTextView.setText(R.string.progress);
        addLabels(mSelectedImageTags);
        setListeners();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        String description = Objects.requireNonNull(descriptionEditText.getText()).toString();
        setContentView(R.layout.photo_detail_view);
        ButterKnife.bind(this);
        loadData();
        descriptionEditText.setText(description);
        if (networkStateProvider.isOffline()) {
            offlineTextView.setVisibility(View.VISIBLE);
        } else {
            offlineTextView.setVisibility(View.GONE);
        }
        if (canEditPhoto != 1) {
            setAutoLabelUISettings();
            addLabels(mSelectedImageTags);
            setListeners();
            keywordAutoLabelUI.setShowCross(false);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("description", Objects.requireNonNull(descriptionEditText.getText()).toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.get("description") != null) {
            descriptionEditText.setText(savedInstanceState.getString("description"));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {
        if (event.getImageTags() != null) {
            addLabels(event.getImageTags());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setAutoLabelUISettings() {

        AutoLabelUISettings autoLabelUISettings =
                new AutoLabelUISettings.Builder()
                        .withIconCross(R.drawable.ic_close_white)
                        .withMaxLabels(6)
                        .withShowCross(canEditPhoto == 1)
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
            mSelectedImageTags.remove(position);
            addLabels(mSelectedImageTags);
        });

        keywordAutoLabelUI.setOnLabelsEmptyListener(() -> {
        });

        keywordAutoLabelUI.setOnLabelClickListener((labelClicked, position) -> {
            if (position == 5) {
                openTagDialog();
            }
        });
    }

    private void openTagDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        TagsDialog tagsDialog = new TagsDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("selected_image_tags", mSelectedImageTags);
        bundle.putBoolean("unableToEditPhoto", canEditPhoto == 0);
        tagsDialog.setCancelable(false);
        tagsDialog.setArguments(bundle);
        tagsDialog.show(ft, "");
    }

    private void addLabels(ArrayList<ImageTag> imageTags) {
        keywordAutoLabelUI.clear();

        mSelectedImageTags = imageTags;

        for (int i = 0; i < mSelectedImageTags.size(); i++) {
            keywordAutoLabelUI.addLabel(mSelectedImageTags.get(i).getName(), i);
        }
        if (mSelectedImageTags.size() >= 6) {
            keywordAutoLabelUI.getLabel(5).setText("View More");
            keywordAutoLabelUI.getLabel(5).setIcon(R.drawable.ic_add_white, false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.allKeyword:
                if (canEditPhoto == 1) {
                    openTagDialog();
                }

                break;
            case R.id.leftImageView:
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null && imm.isActive()) {
                        imm.hideSoftInputFromWindow(backImageView.getWindowToken(), 0);
                    }
                } catch (Exception ignored) {
                }   onBackPressed();
                break;
            case R.id.cancelTextView:
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null && imm.isActive()) {
                        imm.hideSoftInputFromWindow(backImageView.getWindowToken(), 0);
                    }
                } catch (Exception ignored) {
                } onBackPressed();
                break;
            case R.id.saveTextView:
                callUpdatePhotoDetail(mSelectedImageTags, Objects.requireNonNull(descriptionEditText.getText()).toString());
                break;
        }
    }

    private void callUpdatePhotoDetail(ArrayList<ImageTag> selectedImageTags, String string) {
        mPhotosMobile.setIsSync(false);
        List<Photo_tags> photo_tags = new ArrayList<>();

        for (ImageTag imagetag :
                selectedImageTags) {
            Photo_tags photoTag = new Photo_tags();
            photoTag.setKeyword(imagetag.getName());
            photo_tags.add(photoTag);
        }
        mPhotosMobile.setDescriptions(string);
        mUpdatePhotoDetailsProvider.updatePhotosData(mPhotosMobile, selectedImageTags);
        this.finish();
    }

    private void callDeletePhoto() {
        mPhotosMobile.setIsSync(false);
        mPhotosMobile.setDeletedAt(new Date());
        mProjectsProvider.updatePhotoForDelete(mPhotosMobile);
        setResult(RESULT_OK);
        PhotoDetailActivity.this.finish();

    }
}
