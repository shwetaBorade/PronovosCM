package com.pronovoscm.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.adapter.ImageViewPagerAdapter;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.data.UpdatePhotoDetailsProvider;
import com.pronovoscm.model.request.updatephoto.UpdatePhotoDetail2;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.PhotoFolder;
import com.pronovoscm.persistence.domain.PhotosMobile;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.FileUtils;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.dialogs.MessageDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pronovoscm.activity.ProjectAlbumActivity.FILESTORAGE_REQUEST_CODE;

/**
 * Activity offers user to show full size photo
 * It also has the swipe functionality to change the photo so that user no need to go back and change photo
 *
 * @author GWL
 */
public class FullPhotoActivity extends BaseActivity implements View.OnClickListener {
    @Inject
    ProjectsProvider mProjectsProvider;
    @Inject
    UpdatePhotoDetailsProvider mUpdatePhotoDetailsProvider;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    //    @BindView(R.id.cameraImageView)
//    ImageView cameraImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;

    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.photoViewPager)
    ViewPager photoViewPager;
    @BindView(R.id.dateTextView)
    TextView dateTextView;
    @BindView(R.id.detailFloattingButton)
    FloatingActionButton detailFloatingButton;
    @BindView(R.id.editImageView)
    ImageView editImageView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    private List<PhotosMobile> photoList;
    private int position;
    private ImageViewPagerAdapter mImageViewPagerAdapter;
    private int albumId, projectId;
    private long pjPhotosFolderMobileId;
    private PhotosMobile mPhotosMobile;
    private LoginResponse loginResponse;
    private MessageDialog messageDialog;
    private ArrayList<String> captureImageList = new ArrayList<>();
    private PhotoFolder photoFolder;
    private static final int REQUEST_CODE_PHOTO_EDIT_DELETE = 10001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.full_photo_view);
        ButterKnife.bind(this);
        messageDialog = new MessageDialog();
        ((PronovosApplication) getApplication()).getDaggerComponent().inject(this);
        mProjectsProvider.setCaptureImageList(new ArrayList<>());
        albumId = getIntent().getIntExtra("album_id", 0);
        projectId = getIntent().getIntExtra("project_id", 0);
        pjPhotosFolderMobileId = getIntent().getLongExtra("pjPhotosFolderMobileId", 0);
        photoList = mProjectsProvider.getAlbumPhotos(albumId, projectId, pjPhotosFolderMobileId);
        backImageView.setOnClickListener(this);

        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.INVISIBLE);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        photoFolder = mProjectsProvider.getAlbumDetail(loginResponse.getUserDetails().getUsers_id(), pjPhotosFolderMobileId, albumId);
        int isStatic = 0;
        if (photoFolder.getIsStatic() != null) {
            isStatic = photoFolder.getIsStatic();
        }
        if (isStatic != 1) {
            editImageView.setVisibility(View.VISIBLE);
        }
        position = getIntent().getIntExtra("position", 0);
        mImageViewPagerAdapter = new ImageViewPagerAdapter(photoList, this);

        photoViewPager.setAdapter(mImageViewPagerAdapter);
        photoViewPager.setCurrentItem(position);
     /*   if (photoList.get(position).getDateTaken() != null) {
            titleTextView.setText(DateFormatter.formatDateForImage(photoList.get(position).getDateTaken()));
        }*/
        if (photoList.get(position).getDateTaken() != null) {
            titleTextView.setText(DateFormatter.formatDateForImage(photoList.get(position).getDateTaken()));
        }/* else if (photoList.get(position).getCreatedAt() != null) {
//            titleTextView.setText(DateFormatter.formatDateForImage(photoList.get(position).getCreatedAt()));
        }*/ else {
            titleTextView.setText("-");
        }
        mPhotosMobile = photoList.get(position);

        if (mPhotosMobile.getPjPhotosId() != 0 && mPhotosMobile.getPjPhotosFolderId() != 0) {
//            photoList.set(position, null);
//            callUpdateProfile();
        }

        editImageView.setOnClickListener(v ->
                startActivityForResult(new Intent(FullPhotoActivity.this, PhotoDetailActivity.class).putExtra("pjProjectId", projectId)
                        .putExtra("albumMobileId", pjPhotosFolderMobileId).putExtra("photoMobileId", mPhotosMobile.getPjPhotosIdMobile()), REQUEST_CODE_PHOTO_EDIT_DELETE));
        photoViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mPhotosMobile = photoList.get(position);
                FullPhotoActivity.this.position = position;
              /*  if (photoList.get(position).getDateTaken() != null) {
                    titleTextView.setText(DateFormatter.formatDateForImage(photoList.get(position).getDateTaken()));
                }  */
                if (photoList.get(position).getDateTaken() != null) {
                    titleTextView.setText(DateFormatter.formatDateForImage(photoList.get(position).getDateTaken()));
                } /*else if (photoList.get(position).getCreatedAt() != null) {
//                    titleTextView.setText(DateFormatter.formatDateForImage(photoList.get(position).getCreatedAt()));
                } */ else {
                    titleTextView.setText("-");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (loginResponse.getUserDetails().getPermissions().get(0).getUploadPhoto() == 1) {
//            cameraImageView.setVisibility(View.VISIBLE);
        } else {
//            cameraImageView.setVisibility(View.GONE);
        }
        (PronovosApplication.getContext()).setupAndStartWorkManager();
    }


    /**
     * Call update profile
     */
    private void callUpdateProfile() {
        UpdatePhotoDetail2 updatePhotoDetail2 = new UpdatePhotoDetail2();
        updatePhotoDetail2.setAlbum_id(albumId);
        updatePhotoDetail2.setPhoto_id(mPhotosMobile.getPjPhotosId());

        Log.i("getResult", "success: call load pager");
        mUpdatePhotoDetailsProvider.updatePhotoDetails2(updatePhotoDetail2, mPhotosMobile, new ProviderResult<PhotosMobile>() {
            @Override
            public void success(PhotosMobile result) {
                Log.i("getResult", "success: load pager");

                photoList.set(position, result);
                mImageViewPagerAdapter.notifyDataSetChanged();
                mImageViewPagerAdapter = new ImageViewPagerAdapter(photoList, FullPhotoActivity.this);
                photoViewPager.setAdapter(mImageViewPagerAdapter);
                photoViewPager.setCurrentItem(position);
                if (photoList.get(position).getDateTaken() != null) {
                    dateTextView.setText(DateFormatter.formatDateForImage(photoList.get(position).getDateTaken()));
                } else if (photoList.get(position).getCreatedAt() != null) {
                    dateTextView.setText(DateFormatter.formatDateForImage(photoList.get(position).getCreatedAt()));
                } else {
                    dateTextView.setText("January 01,1970");
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(FullPhotoActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(FullPhotoActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(FullPhotoActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(FullPhotoActivity.this, message, getString(R.string.ok));
//                messageDialog.showMessageAlert(FullPhotoActivity.this, getString(R.string.failureMessage), getString(R.string.ok));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == FILESTORAGE_REQUEST_CODE) {
            //resume tasks needing this permission
            startActivity(new Intent(this, PronovosCameraActivity.class).putExtra("totalImageCount", 25).putExtra("pjProjectId", projectId).putExtra("albumMobileId", pjPhotosFolderMobileId));
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == ProjectAlbumActivity.FILECAMERA_REQUEST_CODE) {
            //resume tasks needing this permission
            startActivity(new Intent(this, PronovosCameraActivity.class).putExtra("totalImageCount", 25).putExtra("pjProjectId", projectId).putExtra("albumMobileId", pjPhotosFolderMobileId));

        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == ProjectAlbumActivity.PERMISSION_READ_REQUEST_CODE) {
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(
                    Intent.createChooser(intent, getString(R.string.select_picture)),
                    ProjectAlbumActivity.SELECT_PICTURE);
        }
    }

    @Override
    protected int doGetContentView() {
        return R.layout.full_photo_view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftImageView:
                onBackPressed();
                break;
            case R.id.cameraImageView:
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
//                    ActivityCompat.requestPermissions(this, new String[]{getExternalPermission()}, FILESTORAGE_REQUEST_CODE);
//                } else {
//               selectImage(); }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.choose_from_library)};
        TextView title = new TextView(this);
        title.setText(R.string.add_photo);
        title.setBackgroundColor(Color.BLACK);
        title.setPadding(10, 15, 15, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(22);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals(getString(R.string.take_photo))) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{
                                Manifest.permission.CAMERA,
                                getExternalPermission()}, ProjectAlbumActivity.FILECAMERA_REQUEST_CODE);
                    } else {
                        startActivity(new Intent(this, PronovosCameraActivity.class).putExtra("totalImageCount", 25).putExtra("pjProjectId", projectId).putExtra("albumMobileId", pjPhotosFolderMobileId));

                    }
                } else {
                    startActivity(new Intent(this, PronovosCameraActivity.class).putExtra("totalImageCount", 25).putExtra("pjProjectId", projectId).putExtra("albumMobileId", pjPhotosFolderMobileId));

                }
            } else if (items[item].equals(getString(R.string.choose_from_library))) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        checkSelfPermission(getExternalPermission()) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{getExternalPermission()}, ProjectAlbumActivity.PERMISSION_READ_REQUEST_CODE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
//                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), ProjectAlbumActivity.SELECT_PICTURE);
                }
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_PHOTO_EDIT_DELETE: {
                if (resultCode == RESULT_OK) {
                    FullPhotoActivity.this.finish();
                }
            }
            case ProjectAlbumActivity.SELECT_PICTURE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        captureImageList = new ArrayList<>();
                        String
                                dateString = null;
                        Log.d("onActivityResult", "Select Pic");
                        try {
                            String[] filePath = {MediaStore.Images.Media.DATA};
//                            ClipData mClipData = data.getClipData();
//                            if (mClipData==null){
                            Uri selectedImage = data.getData();
                            Cursor c = getContentResolver().query(
                                    selectedImage, filePath, null, null, null);
                            c.moveToFirst();
                            int columnIndex = c.getColumnIndex(filePath[0]);
                            String picturePath = c.getString(columnIndex);
                            c.close();

                            File output = FileUtils.getOutputGalleryMediaFile(1, this);
                            Bitmap thumbnail =
                                    (BitmapFactory.decodeFile(picturePath));

                            try {
                                FileOutputStream out = new FileOutputStream(output);
                                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                out.flush();
                                out.close();
                                URI uri = null;
                                if (output.exists()) //Extra check, Just to validate the given path
                                {
                                    ExifInterface intf = null;
                                    try {
                                        intf = new ExifInterface(picturePath);
                                        if (intf != null) {
                                            dateString = intf.getAttribute(ExifInterface.TAG_DATETIME);
                                            Log.i("Dated : ", "DATE " + dateString.toString()); //Dispaly dateString. You can do/use it your own way
                                        }
                                    } catch (Exception e) {
                                    }
                                    if (intf == null) {
                                        Date lastModDate = new Date(output.lastModified());
                                        Log.i("Dated : ", "DATE " + lastModDate.toString());//Dispaly lastModDate. You can do/use it your own way
                                    }
                                }
                                try {
                                    uri = new URI(output.getPath());
                                    String[] segments = uri.getPath().split("/");
                                    String imageName = segments[segments.length - 1];

                                    captureImageList.add(imageName);


                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (captureImageList.size() > 0) {
                                startActivity(new Intent(this, PronovosCameraActivity.class)
                                        .putExtra("totalImageCount", 25)
                                        .putExtra("pjProjectId", projectId)
                                        .putExtra("albumMobileId", pjPhotosFolderMobileId)
                                        .putExtra("dateString", dateString)
                                        .putStringArrayListExtra("captureImageList", captureImageList));

                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

                break;
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

}
