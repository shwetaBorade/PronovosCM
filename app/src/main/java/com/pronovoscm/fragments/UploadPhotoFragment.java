package com.pronovoscm.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.activity.PhotoEditActivity;
import com.pronovoscm.adapter.UploadPhotoAdapter;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.persistence.domain.ImageTag;
import com.pronovoscm.persistence.domain.PhotoFolder;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.MessageEvent;
import com.pronovoscm.utils.PhotoFolderEvent;
import com.pronovoscm.utils.dialogs.AlbumsDialog;
import com.pronovoscm.utils.dialogs.MessageDialog;
import com.pronovoscm.utils.dialogs.TagsDialog;
import com.pronovoscm.utils.library.AutoLabelUI;
import com.pronovoscm.utils.library.AutoLabelUISettings;
import com.pronovoscm.utils.library.Label;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.pronovoscm.utils.photoeditor.MyDrawingView.TAG;

public class UploadPhotoFragment extends Fragment implements View.OnClickListener, UploadPhotoAdapter.OnClickListener {
    private static final int CAMERA_REQUEST = 52;
    @Inject
    ProjectsProvider projectsProvider;
    @BindView(R.id.keywordAutoLabelUI)
    AutoLabelUI keywordAutoLabelUI;
    @BindView(R.id.allKeyword)
    ConstraintLayout allKeyword;
    //    @BindView(R.id.dateTakenTextView)
//    TextView dateTakenTextView;
    @BindView(R.id.photoRecyclerView)
    RecyclerView photoRecyclerView;
    @BindView(R.id.keywordSelectTextView)
    TextView keywordSelectTextView;
    @BindView(R.id.albumTextView)
    TextView albumTextView;
    @BindView(R.id.albumViewShow)
    RelativeLayout albumViewShow;
    @BindView(R.id.albumView)
    ConstraintLayout albumView;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.uploadPhotoTextView)
    TextView uploadPhotoTextView;
    @BindView(R.id.cancelTextView)
    TextView cancelTextView;
    int pos = 0;
    private ArrayList<String> capturedImages;
    private Intent mRecievedIntent;
    private UploadPhotoAdapter mUploadPhotoAdapter;
    private ArrayList<ImageTag> mSelectedImageTags = new ArrayList<>();
    private Date mDate;
    private long albumMobileId;
    private int pjProjectId;
    private PhotoFolder mPhotoFolder;
    private MessageDialog messageDialog;
    private String dateString;
    private ExifInterface exif;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.upload_photo_view, container, false);
        ButterKnife.bind(this, rootView);
        messageDialog = new MessageDialog();
        if (mSelectedImageTags.size() <= 0) {
            setAutoLabelUISettings();
        }
        setListeners();
        updateView();
        setRetainInstance(true);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        uploadPhotoTextView.setClickable(true);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            try {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

//        if (savedInstanceState == null) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        try {
            EventBus.getDefault().register(this);
        } catch (EventBusException e) {
            e.printStackTrace();
        }
//        }
    }

    private void updateView() {

        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
        getIntendData();
        setData();
        if (getArguments() != null && getArguments().getString("dateString") != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");//2018:12:24 15:04:37

            try {
                mDate = simpleDateFormat.parse(getArguments().getString("dateString"));
//                dateTakenTextView.setText(DateFormatter.formatAMDateForUploadImage(mDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            mDate = null;
        }

        allKeyword.setOnClickListener(this);
//        dateTakenTextView.setOnClickListener(this);
        uploadPhotoTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);
        albumViewShow.setOnClickListener(this);
        titleTextView.setText(getString(R.string.upload_photos));
        if (mSelectedImageTags.size() > 0) {
            keywordSelectTextView.setVisibility(View.GONE);
        } else {
            keywordSelectTextView.setVisibility(View.VISIBLE);
        }
        if (mPhotoFolder != null) {
            albumMobileId = mPhotoFolder.getPjPhotosFolderMobileId();
            albumTextView.setText(mPhotoFolder.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PhotoFolderEvent event) {
        if (event.getPhotoFolder() != null) {
            mPhotoFolder = event.getPhotoFolder();
            albumMobileId = mPhotoFolder.getPjPhotosFolderMobileId();
            albumTextView.setText(mPhotoFolder.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {

        if (event.getImageTags() != null) {
            addLabels(event.getImageTags());
        }
    }

    private void addLabels(ArrayList<ImageTag> imageTags) {
        keywordAutoLabelUI.clear();
        mSelectedImageTags = imageTags;
        if (mSelectedImageTags.size() > 0) {
            keywordSelectTextView.setVisibility(View.GONE);
        } else {
            keywordSelectTextView.setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < mSelectedImageTags.size(); i++) {
            keywordAutoLabelUI.addLabel(mSelectedImageTags.get(i).getName(), i);
        }
        if (mSelectedImageTags.size() >= 6) {
            keywordAutoLabelUI.getLabel(5).setText("View More");
            keywordAutoLabelUI.getLabel(5).setIcon(R.drawable.ic_add_white, false);
        }
    }

    private void setData() {
        mDate = new Date();
//        dateTakenTextView.setText(DateFormatter.formatAMDateForUploadImage(mDate));

        if (exif != null) {
//            dateTakenTextView.setText(exif.getAttribute(ExifInterface.TAG_DATETIME));
        } else {
//            dateTakenTextView.setText("");
        }
//        dateTakenTextView.setText(DateFormatter.formatAMDateForUploadImage(mDate));
        mUploadPhotoAdapter = new UploadPhotoAdapter(getActivity(), capturedImages, this);
        photoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        photoRecyclerView.setAdapter(mUploadPhotoAdapter);
//        backImageView.setImageResource(R.drawable.ic_arrow_back);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.INVISIBLE);
        backImageView.setOnClickListener(this);
        albumView.setVisibility(View.VISIBLE);
        if (albumMobileId == -1) {
        } else {
            mPhotoFolder = projectsProvider.getPhotoFolder(albumMobileId);
            albumMobileId = mPhotoFolder.getPjPhotosFolderMobileId();
            albumTextView.setText(mPhotoFolder.getName());
        }
    }

    private void setAutoLabelUISettings() {
        AutoLabelUISettings autoLabelUISettings =
                new AutoLabelUISettings.Builder()
                        .withIconCross(R.drawable.ic_close_white)
                        .withMaxLabels(6)
                        .withShowCross(true)
                        .withLabelsClickables(true)
                        .withTextColor(android.R.color.black)
                        .withLabelPadding(30)
                        .build();

        keywordAutoLabelUI.setSettings(autoLabelUISettings);
    }

    /*private void callPhotoTagService() {
        projectsProvider.getPhotoTags(new ProviderResult<List<ImageTag>>() {
            @Override
            public void success(List<ImageTag> result) {


            }

            @Override
            public void AccessTokenFailure(String message) {
            }

            @Override
            public void failure(String message) {
//                projectsProvider.showMessageAlert(getActivity(), message, getString(R.string.ok));
                messageDialog.showMessageAlert(getActivity(), message, getString(R.string.ok));
//                messageDialog.showMessageAlert(getActivity(), getString(R.string.failureMessage), getString(R.string.ok));

            }
        }, loginResponse);
    }*/

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

        keywordAutoLabelUI.setOnLabelClickListener(new AutoLabelUI.OnLabelClickListener() {
            @Override
            public void onClickLabel(Label labelClicked, int position) {
                if (position == 5) {
                    openTagDialog();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.allKeyword:
                openTagDialog();

                break;
//            case R.id.dateTakenTextView:
//
//                Calendar calendar1 = new GregorianCalendar();
//                calendar1.setTime(mDate);
//                int mYear = calendar1.get(Calendar.YEAR);
//                int mMonth = calendar1.get(Calendar.MONTH);
//                int mDay = calendar1.get(Calendar.DATE);
//                int hr = calendar1.get(Calendar.HOUR_OF_DAY);
//                int min = calendar1.get(Calendar.MINUTE);
//                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
//                        (view, year, monthOfYear, dayOfMonth) -> {
//
//                            Calendar calendar = new GregorianCalendar(year,
//                                    monthOfYear,
//                                    dayOfMonth,
//                                    hr,
//                                    min);
//

//
//
//                        }, mYear, mMonth, mDay);
//                datePickerDialog.show();
//                break;
            case R.id.leftImageView:
                try {
                    InputMethodManager imm = (InputMethodManager)getActivity(). getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null && imm.isActive()) {
                        imm.hideSoftInputFromWindow(backImageView.getWindowToken(), 0);
                    }
                } catch (Exception ignored) {
                }
                getActivity().onBackPressed();
                break;
            case R.id.cancelTextView:
                try {
                    InputMethodManager imm = (InputMethodManager)getActivity(). getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null && imm.isActive()) {
                        imm.hideSoftInputFromWindow(backImageView.getWindowToken(), 0);
                    }
                } catch (Exception ignored) {
                }
                getActivity().onBackPressed();
                break;
            case R.id.uploadPhotoTextView:
                if (albumMobileId == -1) {
                    projectsProvider.showMessageAlert(getActivity(), "Please select an album to continue.", getString(R.string.ok), false);
                    uploadPhotoTextView.setClickable(true);
                } else {
                    uploadPhotoTextView.setClickable(false);
                    projectsProvider.addImages(capturedImages, pjProjectId, mPhotoFolder.getPjPhotosFolderId(), albumMobileId, mDate, "", mSelectedImageTags);
                    getActivity().finish();
                }
                break;
            case R.id.albumViewShow:
                openAlbumDialog();
                break;
        }
    }


    private void openTagDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        TagsDialog tagsDialog = new TagsDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("selected_image_tags", mSelectedImageTags);
        tagsDialog.setCancelable(false);
        tagsDialog.setArguments(bundle);
        tagsDialog.show(ft, "");
    }

    private void openAlbumDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        AlbumsDialog tagsDialog = new AlbumsDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable("photoFolder", mPhotoFolder);
        bundle.putInt("pjProjectId", pjProjectId);
        tagsDialog.setCancelable(false);
        tagsDialog.setArguments(bundle);
        tagsDialog.show(ft, "");
    }

    public void getIntendData() {
        mRecievedIntent = getActivity().getIntent();
        albumMobileId = mRecievedIntent.getLongExtra("albumMobileId", -1);
        pjProjectId = mRecievedIntent.getIntExtra("pjProjectId", 0);
        capturedImages = getArguments().getStringArrayList("captured_images");

        String completePath = getContext().getFilesDir().getAbsolutePath() + "/Pronovos/" + capturedImages.get(0);
        exif = null;
        try {
            exif = new ExifInterface(completePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST:

                    Log.d(TAG, "onActivityResult: " + data.getStringExtra("path"));
                    List<String> tempImages = getArguments().getStringArrayList("captured_images"); //capturedImages;
//                    capturedImages.set(pos,)
//                    mUploadPhotoAdapter = new UploadPhotoAdapter(getActivity(), capturedImages, this);
//                    photoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//                    photoRecyclerView.setAdapter(mUploadPhotoAdapter);
//                    mUploadPhotoAdapter.notifyItemChanged(pos);
                    mUploadPhotoAdapter.notifyDataSetChanged();
//                    mPhotoEditor.clearAllViews();
//                    Bitmap photo = (Bitmap) data.getExtras().get("data");
//                    mPhotoEditorView1.getSource().setImageBitmap(photo);
                    break;
            }
        }
    }

    @Override
    public void onPhotoClick(int position, String photo) {
        Log.d(TAG, "onPhotoClick: " + photo);
        pos = position;
        startActivityForResult(new Intent(getActivity(),
                PhotoEditActivity.class).putExtra("file_location", photo).
                putExtra("photo_date", DateFormatter.formatAMDateForUploadImage(mDate)).putExtra("position", position), CAMERA_REQUEST);

    }
}
