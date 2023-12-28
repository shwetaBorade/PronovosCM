package com.pronovoscm.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.adapter.PhotoAdapter;
import com.pronovoscm.data.FileUploadProvider;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.data.UpdatePhotoDetailsProvider;
import com.pronovoscm.galleryimagepicker.GalleryPickerActivity;
import com.pronovoscm.model.TransactionLogUpdate;
import com.pronovoscm.model.TransactionModuleEnum;
import com.pronovoscm.model.request.photo.PhotoRequest;
import com.pronovoscm.model.request.updatephoto.Photo_tags;
import com.pronovoscm.model.request.updatephoto.UpdatePhotoDetail;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.photo.PhotoResponse;
import com.pronovoscm.model.response.updatephoto.UpdatePhotoDetailResponse;
import com.pronovoscm.persistence.domain.PhotoFolder;
import com.pronovoscm.persistence.domain.PhotosMobile;
import com.pronovoscm.persistence.domain.Taggables;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.dialogs.MessageDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

import static com.pronovoscm.activity.ProjectAlbumActivity.FILESTORAGE_REQUEST_CODE;
import static com.pronovoscm.activity.ProjectAlbumActivity.SELECT_PICTURE;
import static com.pronovoscm.utils.Constants.INTENT_KEY_TOTAL_IMAGE_COUNT;


/**
 * Activity to show photos inside any album
 *
 * @author GWL
 */
public class AlbumsPhotoActivity extends BaseActivity implements View.OnClickListener {
    @Inject
    ProjectsProvider projectsProvider;
    @Inject
    FileUploadProvider mFileUploadProvider;
    @Inject
    UpdatePhotoDetailsProvider mUpdatePhotoDetailsProvider;

    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.cameraImageView)
    ImageView cameraImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;
    @BindView(R.id.photoRecyclerView)
    RecyclerView photoRecyclerView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    boolean isLoading = false;
    Bitmap rotatedBitmap = null;
    //private PhotoAdapter mPhotoAdapter;
//    private DatePhotoAdapter mDatePhotoAdapter;
    private PhotoAdapter mPhotoAdapter;
    private String albumName;
    private int albumId;
    private long albumMobileId;
    private int pjProjectId;
    private boolean albumSync;
    private LoginResponse loginResponse;
    private Call<PhotoResponse> projectResponseCall;
    private List<Object> mPhotoMobileList = new ArrayList<>();
    private HashMap<String, ArrayList<PhotosMobile>> mPhotoMobileDateList = new LinkedHashMap<>();
    private MessageDialog messageDialog;
    private ArrayList<String> captureImageList = new ArrayList<>();
    private Date startWeekDate = null;
    private Date endWeekDate = null;
    private PhotoFolder photoFolder;
    private GridLayoutManager gridLayoutManager;
    private String suffix;
    private long mLastClickTime = 0;
    private long mMenuLastClickTime = 0;
    private RecyclerView.OnScrollListener photoRecylerViewScrollListner = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            GridLayoutManager mLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
            if (!isLoading) {
                if (mLayoutManager != null && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    isLoading = true;
                    callPhotoRequest();

                }
            }
        }
    };

    private void init() {
        messageDialog = new MessageDialog();
        projectsProvider.setCaptureImageList(new ArrayList<>());
        albumName = getIntent().getStringExtra("album_name");
        albumId = getIntent().getIntExtra("album_id", 0);
        albumMobileId = getIntent().getLongExtra("album_mobile_id", 0);
        pjProjectId = getIntent().getIntExtra("pj_project_id", 0);
        albumSync = getIntent().getBooleanExtra("is_sync", false);
        titleTextView.setText(albumName);
        backImageView.setOnClickListener(this);
        cameraImageView.setOnClickListener(this);
        rightImageView.setVisibility(View.INVISIBLE);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        cameraImageView.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_add_photo));
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        photoFolder = projectsProvider.getAlbumDetail(loginResponse.getUserDetails().getUsers_id(), albumMobileId, albumId);
        cameraImageView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        doGetApplication().getDaggerComponent().inject(this);
        init();
        int isStatic = 0;
        if (photoFolder.getIsStatic() != null) {
            isStatic = photoFolder.getIsStatic();
        }

        if (loginResponse.getUserDetails().getPermissions().get(0).getUploadPhoto() == 1 && isStatic == 0) {
            cameraImageView.setVisibility(View.VISIBLE);
        } else {
            cameraImageView.setVisibility(View.GONE);
        }

        gridLayoutManager = new GridLayoutManager(this, 2);
        Configuration newConfig = getResources().getConfiguration();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // set background for landscape
            gridLayoutManager = new GridLayoutManager(this, 3);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(this, 2);
            // set background for portrait
        }
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mPhotoAdapter.isHeader(position) ? gridLayoutManager.getSpanCount() : 1;
            }
        });
        photoRecyclerView.setLayoutManager(gridLayoutManager);
        mPhotoAdapter = new PhotoAdapter(this, mPhotoMobileList, 0, (albumId, projectId, photoFolderId, dateAdapterPosition, photoAdapterPosition, pjPhotosIdMobile) -> {
            int count = 0;
            List<PhotosMobile> result = projectsProvider.getAlbumPhotos(albumId, pjProjectId, albumMobileId);
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).getPjPhotosIdMobile().toString().equals(pjPhotosIdMobile.toString())) {
                    count = i;
                }
            }

            startActivity(new Intent(AlbumsPhotoActivity.this, FullPhotoActivity.class)
                    .putExtra(Constants.INTENT_KEY_ALBUM_ID, albumId)
                    .putExtra(Constants.INTENT_KEY_PROJECT_ID, projectId)
                    .putExtra(Constants.INTENT_KEY_PJ_PHOTOS_FOLDER_MOBILE_ID, photoFolderId)
                    .putExtra(Constants.INTENT_KEY_POSITION, count));
        });

        photoRecyclerView.setAdapter(mPhotoAdapter);
//        photoRecyclerView.setAdapter(mDatePhotoAdapter);
        photoRecyclerView.addOnScrollListener(photoRecylerViewScrollListner);
        ArrayList<PhotosMobile> temp = new ArrayList<>();
        if (albumSync) {
        } else {
            List<PhotosMobile> result = projectsProvider.getAlbumPhotos(albumId, pjProjectId, albumMobileId);
            mPhotoMobileList.clear();
            temp.clear();
            for (int i = 0; i < result.size(); i++) {

                Date dateTaken = result.get(i).getDateTaken();
                if (dateTaken == null) {
                    dateTaken = result.get(i).getCreatedAt();
                }

                Calendar cal = Calendar.getInstance();
                cal.setTime(dateTaken);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                startWeekDate = cal.getTime();
                cal.add(Calendar.DATE, 6);
                endWeekDate = cal.getTime();
                if ((temp = mPhotoMobileDateList.get(DateFormatter.formatWeekDays(startWeekDate, endWeekDate))) == null) {
                    temp = new ArrayList<>();
                    mPhotoMobileDateList.put(DateFormatter.formatWeekDays(startWeekDate, endWeekDate), temp);
                    mPhotoMobileList.add(DateFormatter.formatWeekDays(startWeekDate, endWeekDate));
                }
           /* if ((mPhotoMobiles = mPhotoMobileDateList.get(DateFormatter.formatDateForImage(mPhotoMobileList.get(i).getCreatedAt()))) == null) {
                mPhotoMobiles = new ArrayList<>();
                mPhotoMobileDateList.put(DateFormatter.formatDateForImage(mPhotoMobileList.get(i).getCreatedAt()), mPhotoMobiles);
            }
*/
                mPhotoMobileList.add(result.get(i));

                temp.add(result.get(i));
            }
            if (mPhotoMobileList.size() >= 500 && NetworkService.isNetworkAvailable(this)) {
                mPhotoMobileList.add("nodate");
            }

//            mDatePhotoAdapter.notifyDataSetChanged();
            mPhotoAdapter.notifyDataSetChanged();
            if (result.size() <= 0) {
                noRecordTextView.setText(R.string.no_photos_status);
            } else {
                noRecordTextView.setText("");

            }
        }
        (PronovosApplication.getContext()).setupAndStartWorkManager();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // set background for landscape
            gridLayoutManager = new GridLayoutManager(this, 3);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(this, 2);

            // set background for portrait
        }
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mPhotoAdapter.isHeader(position) ? gridLayoutManager.getSpanCount() : 1;
            }
        });
        photoRecyclerView.setLayoutManager(gridLayoutManager);/*
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            photoRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } }*/
    }

    /**
     * Get all the photos from the server
     */
    private void callPhotoRequest() {
        if (mPhotoMobileList.size() <= 0) {
            noRecordTextView.setText(R.string.loading_photos);

        }
        PhotoRequest albumRequest = new PhotoRequest(albumId);
        albumRequest.setMinPhotoId(projectsProvider.getMINPhotoID(albumId, pjProjectId));
        isLoading = true;
        projectResponseCall = projectsProvider.getAlbumPhoto(albumRequest, pjProjectId, albumMobileId, new ProviderResult<List<PhotosMobile>>() {
            @Override
            public void success(List<PhotosMobile> result) {
//                noRecordTextView.setText(R.string.loading_photos);
                isLoading = false;
                if (mPhotoMobileList.contains("nodate")) {
                    mPhotoMobileDateList.remove("nodate");
//                    mDatePhotoAdapter.notifyDataSetChanged();
                    mPhotoMobileList.remove("nodate");

                    mPhotoAdapter.notifyDataSetChanged();
                }
                mPhotoMobileList.clear();
                if (result.size() <= 0) {
                    noRecordTextView.setText(R.string.no_photos_status);
                } else {
                    noRecordTextView.setText("");
                }
                ArrayList<PhotosMobile> temp = null;
                mPhotoMobileDateList.clear();
                for (int i = 0; i < result.size(); i++) {
                    Date dateTaken = (result.get(i)).getDateTaken();
                    if (dateTaken == null) {
                        dateTaken = (result.get(i)).getCreatedAt();
                    }
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateTaken);
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
//                    cal.add(Calendar.DATE, 1);
                    startWeekDate = cal.getTime();
                    cal.add(Calendar.DATE, 6);
                    endWeekDate = cal.getTime();
                    if ((temp = mPhotoMobileDateList.get(DateFormatter.formatWeekDays(startWeekDate, endWeekDate))) == null) {
                        temp = new ArrayList<>();
                        mPhotoMobileDateList.put(DateFormatter.formatWeekDays(startWeekDate, endWeekDate), temp);
                        mPhotoMobileList.add(DateFormatter.formatWeekDays(startWeekDate, endWeekDate));
                    }
                    mPhotoMobileList.add(result.get(i));
                    temp.add(result.get(i));
                }
                mPhotoMobileDateList.put("nodate", null);
                if (mPhotoMobileList.size() >= 500 && NetworkService.isNetworkAvailable(AlbumsPhotoActivity.this)) {
                    mPhotoMobileList.add("nodate");
                }
                mPhotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void AccessTokenFailure(String message) {
                noRecordTextView.setText("");
                isLoading = false;
                startActivity(new Intent(AlbumsPhotoActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(AlbumsPhotoActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(AlbumsPhotoActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                noRecordTextView.setText("");
                isLoading = false;
                messageDialog.showMessageAlert(AlbumsPhotoActivity.this, message, getString(R.string.ok));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftImageView:
                onBackPressed();
                break;
            case R.id.cameraImageView:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this, new String[]{getExternalPermission()}, FILESTORAGE_REQUEST_CODE);
                } else {
                    selectImage();
                }
                break;
        }
    }

    private void selectImage() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
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
                if (SystemClock.elapsedRealtime() - mMenuLastClickTime < 1000) {
                    return;
                }
                mMenuLastClickTime = SystemClock.elapsedRealtime();
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{
                                Manifest.permission.CAMERA,
                                getExternalPermission()}, ProjectAlbumActivity.FILECAMERA_REQUEST_CODE);
                    } else {
                        captureImageList = new ArrayList<>();
                        projectsProvider.setCaptureImageList(new ArrayList<>());

                        startActivity(new Intent(this, PronovosCameraActivity.class)
                                .putExtra(INTENT_KEY_TOTAL_IMAGE_COUNT, 25)
                                .putExtra(Constants.INTENT_KEY_PJ_PROJECT_ID, pjProjectId)
                                .putExtra(Constants.INTENT_KEY_ALBUM_MOBILE_ID, albumMobileId)
                                .putStringArrayListExtra("captureImageList", captureImageList));

                    }
                } else {
                    captureImageList = new ArrayList<>();
                    projectsProvider.setCaptureImageList(new ArrayList<>());
                    startActivity(new Intent(this, PronovosCameraActivity.class).putExtra("totalImageCount",
                            Constants.TOTAL_IMAGE_UPLOAD_COUNT).putExtra("pjProjectId", pjProjectId)
                            .putExtra("albumMobileId", albumMobileId)
                            .putStringArrayListExtra("captureImageList", captureImageList));

                }
            } else if (items[item].equals(getString(R.string.choose_from_library))) {
                if (SystemClock.elapsedRealtime() - mMenuLastClickTime < 1000) {
                    return;
                }
                mMenuLastClickTime = SystemClock.elapsedRealtime();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        checkSelfPermission(getExternalPermission()) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{getExternalPermission()}, ProjectAlbumActivity.PERMISSION_READ_REQUEST_CODE);
                } else {
                   /* Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
//                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), SELECT_PICTURE);*/
                    startActivityForResult(new Intent(this, GalleryPickerActivity.class).putParcelableArrayListExtra("captured_images", new ArrayList<>())
                            .putExtra("pjProjectId", pjProjectId)
                            .putExtra(Constants.INTENT_KEY_ALBUM_MOBILE_ID, albumMobileId), SELECT_PICTURE);

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
            case SELECT_PICTURE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                      /*  captureImageList = new ArrayList<>();
                        String dateString = null;
                        projectsProvider.setCaptureImageList(new ArrayList<>());
                        Log.d("onActivityResult", "Select Pic");
                        ArrayList<ImageModel> capturedImages = data.getParcelableArrayListExtra("captured_images");
                        suffix = loginResponse.getUserDetails().getAuthtoken().substring(loginResponse.getUserDetails().getAuthtoken().length() - 3);

                        try {
                            if (capturedImages.size() > 0) {

                                for (int i = 0; i < capturedImages.size(); i++) {
                                    ImageModel imageModel = capturedImages.get(i);
                                    if (!capturedImages.get(i).isCameraCaptured() && TextUtils.isEmpty(capturedImages.get(i).getPath())) {
                                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        File file = new File(capturedImages.get(i).getGalleryPath());
                                        String fileName = "";

                                        File output = FileUtils.getOutputGalleryMediaFile(1, this);
                                        ExifInterface exif = new ExifInterface(file.getAbsolutePath());
                                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                                        try {
                                            if (exif != null) {
                                                dateString = exif.getAttribute(ExifInterface.TAG_DATETIME);
                                                Log.i("Dated : ", "DATE " + dateString.toString()); //Dispaly dateString. You can do/use it your own way
                                            }
                                        } catch (Exception e) {
                                        }
                                        if (exif == null) {
                                            Date lastModDate = new Date(output.lastModified());
                                            Log.i("Dated : ", "DATE " + lastModDate.toString());//Dispaly lastModDate. You can do/use it your own way
                                        }


                                        Matrix matrix = new Matrix();
                                        if (orientation == 6) {
                                            matrix.postRotate(90);
                                        } else if (orientation == 3) {
                                            matrix.postRotate(180);
                                        } else if (orientation == 8) {
                                            matrix.postRotate(270);
                                        }
                                        Bitmap mb = BitmapFactory.decodeFile(capturedImages.get(i).getGalleryPath());
                                        Bitmap myBitmap = Bitmap.createBitmap(mb, 0, 0, mb.getWidth(), mb.getHeight(), matrix, true); // rotating bitmap
                                        File imageFile = null;
                                        rotatedBitmap = myBitmap;

                                        File folder = new File(getFilesDir().getAbsolutePath() + "/Pronovos/");
                                        boolean success = true;
                                        fileName = String.valueOf(DateFormatter.currentTimeMillisLocal());
                                        if (!folder.exists()) {
                                            success = folder.mkdirs();
                                        }
                                        if (success) {
                                            imageFile = new File(folder.getAbsolutePath()
                                                    + File.separator
                                                    + fileName + suffix + ".jpg");

                                            imageFile.createNewFile();

                                        }
//                                        saveImage(rotatedBitmap, file, fileName);

                                        ByteArrayOutputStream ostream = new ByteArrayOutputStream();

                                        // save image into gallery
                                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, ostream);

                                        FileOutputStream fout = new FileOutputStream(imageFile);
                                        fout.write(ostream.toByteArray());


                                        fout.close();
                                        ContentValues values = new ContentValues();

                                        values.put(MediaStore.Images.Media.DATE_TAKEN,
                                                System.currentTimeMillis());
                                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                                        values.put(MediaStore.MediaColumns.DATA,
                                                imageFile.getAbsolutePath());

                                        captureImageList.add(fileName + suffix + ".jpg");

                                        imageModel.setPath(fileName + ".jpg");
                                        capturedImages.set(i, imageModel);


                                    }

                                }


                                if (captureImageList.size() > 0) {
                                    startActivity(new Intent(this, PronovosCameraActivity.class)
                                            .putExtra("totalImageCount", 25)
                                            .putExtra("pjProjectId", pjProjectId)
                                            .putExtra("albumMobileId", albumMobileId)
                                            .putExtra("dateString", dateString)
                                            .putStringArrayListExtra("captureImageList", captureImageList));

                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
//                        ArrayList<ImageModel> params = data.getParcelableArrayListExtra("captured_images");

                      /*  new StoreImageInBackground(this, new LoadImageInBackground.Listener() {
                            @Override
                            public void onImageDownloaded(Bitmap bitmap) {
                            }

                            @Override
                            public void onImageDownloadError() {
                            }
                        }, data).executeOnExecutor(THREAD_POOL_EXECUTOR);
*/
                    } /*catch(Exception e){
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
        }*/
                }
                break;
        }

    }

    private void saveImage(Bitmap bitmap, File imageFile, String fileName) throws IOException {

        ByteArrayOutputStream ostream = new ByteArrayOutputStream();

        // save image into gallery
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, ostream);

        FileOutputStream fout = new FileOutputStream(imageFile);
        fout.write(ostream.toByteArray());


        fout.close();
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN,
                System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA,
                imageFile.getAbsolutePath());

        captureImageList.add(fileName + suffix + ".jpg");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == FILESTORAGE_REQUEST_CODE) {
            //resume tasks needing this permission
            startActivity(new Intent(this, PronovosCameraActivity.class).putExtra("totalImageCount", 25).putExtra("pjProjectId", pjProjectId).putExtra("albumMobileId", albumMobileId));
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == ProjectAlbumActivity.FILECAMERA_REQUEST_CODE) {
            //resume tasks needing this permission
            captureImageList = new ArrayList<>();
            startActivity(new Intent(this, PronovosCameraActivity.class).putExtra("totalImageCount", 25).putExtra("pjProjectId", pjProjectId).putExtra("albumMobileId", albumMobileId).putStringArrayListExtra("captureImageList", captureImageList));

        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == ProjectAlbumActivity.PERMISSION_READ_REQUEST_CODE) {
           /* Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(
                    Intent.createChooser(intent, getString(R.string.select_picture)),
                    ProjectAlbumActivity.SELECT_PICTURE);*/
            startActivityForResult(new Intent(this, GalleryPickerActivity.class).putParcelableArrayListExtra("captured_images", new ArrayList<>())
                    .putExtra("pjProjectId", pjProjectId)
                    .putExtra("albumMobileId", albumMobileId), SELECT_PICTURE);

        }
    }

    @Override
    public void onBackPressed() {
        if (projectResponseCall != null && !projectResponseCall.isCanceled()) {
            projectResponseCall.cancel();
        }
        super.onBackPressed();
    }

    @Override
    protected int doGetContentView() {
        return R.layout.album_photo_view;
    }

    @Override
    public void onResume() {
        super.onResume();
        projectsProvider.setCaptureImageList(new ArrayList<>());

        List<PhotosMobile> result = projectsProvider.getAlbumPhotos(albumId, pjProjectId, albumMobileId);
//        mPhotoAdapter = new PhotoAdapter(AlbumsPhotoActivity.this, result);
        mPhotoMobileList.clear();
//        mPhotoMobileList.addAll(result);
        ArrayList<PhotosMobile> temp = null;
        mPhotoMobileDateList.clear();
        for (int i = 0; i < result.size(); i++) {
            Date dateTaken = (result.get(i)).getDateTaken();
            if (dateTaken == null) {
                dateTaken = (result.get(i)).getCreatedAt();
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(dateTaken);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
//                    cal.add(Calendar.DATE, 1);
            startWeekDate = cal.getTime();
            cal.add(Calendar.DATE, 6);
            endWeekDate = cal.getTime();
            if ((temp = mPhotoMobileDateList.get(DateFormatter.formatWeekDays(startWeekDate, endWeekDate))) == null) {
                temp = new ArrayList<>();
                mPhotoMobileList.add(DateFormatter.formatWeekDays(startWeekDate, endWeekDate));
                mPhotoMobileDateList.put(DateFormatter.formatWeekDays(startWeekDate, endWeekDate), temp);
            }
            /*
            if ((temp = mPhotoMobileDateList.get(DateFormatter.formatDateForImage(mPhotoMobileList.get(i).getDateTaken()))) == null) {
                temp = new ArrayList<>();
                mPhotoMobileDateList.put(DateFormatter.formatDateForImage(mPhotoMobileList.get(i).getDateTaken()), temp);
            }*/
            mPhotoMobileList.add(result.get(i));

            temp.add(result.get(i));
        }
        if (mPhotoMobileList.size() >= 500 && NetworkService.isNetworkAvailable(this)) {
            mPhotoMobileList.add("nodate");
        }
//        mDatePhotoAdapter.notifyDataSetChanged();
        mPhotoAdapter.notifyDataSetChanged();
        if (result.size() <= 0) {
//            noRecordTextView.setText(R.string.no_photos_status);
        } else {
            noRecordTextView.setText("");

        }
//        photoRecyclerView.setAdapter(mPhotoAdapter);
        callPhotoRequest();
        if (albumSync) {
//            callPhotoService();
        }
    }

    /**
     * Subscriber of the internet connection
     *
     * @param isOffline true if there is internet connection
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean isOffline) {
        if (isOffline) {
            if (mPhotoMobileList != null && mPhotoMobileList.size() > 0 && mPhotoMobileList.get(mPhotoMobileList.size() - 1).equals("nodate")) {
                mPhotoMobileList.remove("nodate");
            }
            offlineTextView.setVisibility(View.VISIBLE);
        } else {
            offlineTextView.setVisibility(View.GONE);

            if (mPhotoMobileList != null && mPhotoMobileList.size() >= 500 && !mPhotoMobileList.get(mPhotoMobileList.size() - 1).equals("nodate") && NetworkService.isNetworkAvailable(this)) {
                mPhotoMobileList.add("nodate");
            }
        }
        mPhotoAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TransactionLogUpdate transactionLogUpdate) {
        Log.i("transactionLogUpdate", "onEvent: " + " transactionLogUpdate ");
        if (transactionLogUpdate.getTransactionModuleEnum() != null && transactionLogUpdate.getTransactionModuleEnum().equals(TransactionModuleEnum.PHOTO)) {
//            updateCrewList();
            List<PhotosMobile> result = projectsProvider.getAlbumPhotos(albumId, pjProjectId, albumMobileId);
//        mPhotoAdapter = new PhotoAdapter(AlbumsPhotoActivity.this, result);
//            mPhotoMobileList.clear();
//            mPhotoMobileList.addAll(result);

            mPhotoMobileList.clear();
//            mPhotoMobileList.addAll(result);
            if (result.size() <= 0) {
                noRecordTextView.setText(R.string.no_photos_status);
            } else {
                noRecordTextView.setText("");

            }
//                photoRecyclerView.setAdapter(mPhotoAdapter);
            ArrayList<PhotosMobile> temp = null;


            mPhotoMobileDateList.clear();
            for (int i = 0; i < result.size(); i++) {
                Date dateTaken = (result.get(i)).getDateTaken();
                if (dateTaken == null) {
                    dateTaken = (result.get(i)).getCreatedAt();
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateTaken);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
//                    cal.add(Calendar.DATE, 1);
                startWeekDate = cal.getTime();
                cal.add(Calendar.DATE, 6);
                endWeekDate = cal.getTime();
                if ((temp = mPhotoMobileDateList.get(DateFormatter.formatWeekDays(startWeekDate, endWeekDate))) == null) {
                    temp = new ArrayList<>();
                    mPhotoMobileList.add(DateFormatter.formatWeekDays(startWeekDate, endWeekDate));

                    mPhotoMobileDateList.put(DateFormatter.formatWeekDays(startWeekDate, endWeekDate), temp);
                }
                mPhotoMobileList.add(result.get(i));

                temp.add(result.get(i));
            }
            if (mPhotoMobileList.size() >= 500 && NetworkService.isNetworkAvailable(this)) {
                mPhotoMobileList.add("nodate");
            }
//            mDatePhotoAdapter.notifyDataSetChanged();
            mPhotoAdapter.notifyDataSetChanged();
//            mDatePhotoAdapter

        }
    }

    /**
     * Update the photo details to the server
     *
     * @param mPhotosMobile
     * @param selectedImageTags
     * @param string
     */
    private void callUpdatePhotoDetail(PhotosMobile mPhotosMobile, List<Taggables> selectedImageTags, String string) {
        mPhotosMobile.setDescriptions(string);

        List<Photo_tags> photo_tags = new ArrayList<>();
        for (Taggables imagetag :
                selectedImageTags) {
            Photo_tags photoTag = new Photo_tags();
            photoTag.setKeyword(imagetag.getTagName());
            photo_tags.add(photoTag);
        }
        UpdatePhotoDetail updatePhotoDetail = new UpdatePhotoDetail();
        updatePhotoDetail.setPhoto_description(string);
        updatePhotoDetail.setPhoto_tags(photo_tags);
        updatePhotoDetail.setAlbum_id(mPhotosMobile.getPjPhotosFolderId());
        updatePhotoDetail.setPhoto_id(mPhotosMobile.getPjPhotosId());
        mUpdatePhotoDetailsProvider.updatePhotoDetails(updatePhotoDetail, mPhotosMobile, new ProviderResult<UpdatePhotoDetailResponse>() {
            @Override
            public void success(UpdatePhotoDetailResponse result) {

            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(AlbumsPhotoActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(AlbumsPhotoActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(AlbumsPhotoActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(AlbumsPhotoActivity.this, message, getString(R.string.ok));
            }
        });
    }
/*
    public class StoreImageInBackground extends AsyncTask<Void, Void, ArrayList> {
        Context context;
        Intent data;
        String dateString = null;
        private com.pronovoscm.utils.ui.LoadImageInBackground.Listener listener;

        public StoreImageInBackground(Context context, final com.pronovoscm.utils.ui.LoadImageInBackground.Listener listener, Intent data) {
            this.listener = listener;
            this.context = context;
            this.data = data;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CustomProgressBar.showDialog(context);

        }

        @Override
        protected ArrayList doInBackground(Void... urls) {
            captureImageList = new ArrayList<>();
            projectsProvider.setCaptureImageList(new ArrayList<>());
            Log.d("onActivityResult", "Select Pic");
            ArrayList<ImageModel> capturedImages = data.getParcelableArrayListExtra("captured_images");
            suffix = loginResponse.getUserDetails().getAuthtoken().substring(loginResponse.getUserDetails().getAuthtoken().length() - 3);

            try {
                if (capturedImages.size() > 0) {

                    for (int i = 0; i < capturedImages.size(); i++) {
                        ImageModel imageModel = capturedImages.get(i);
                        if (!capturedImages.get(i).isCameraCaptured() && TextUtils.isEmpty(capturedImages.get(i).getPath())) {
                            File file = new File(capturedImages.get(i).getGalleryPath());
                            String fileName = "";

                            File output = FileUtils.getOutputGalleryMediaFile(1, context);
                            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
                            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                            try {
                                if (exif != null) {
                                    dateString = exif.getAttribute(ExifInterface.TAG_DATETIME);
                                    Log.i("Dated : ", "DATE " + dateString.toString()); //Dispaly dateString. You can do/use it your own way
                                }
                            } catch (Exception e) {
                            }
                            if (exif == null) {
                                Date lastModDate = new Date(output.lastModified());
                                Log.i("Dated : ", "DATE " + lastModDate.toString());//Dispaly lastModDate. You can do/use it your own way
                            }


                            Matrix matrix = new Matrix();
                            if (orientation == 6) {
                                matrix.postRotate(90);
                            } else if (orientation == 3) {
                                matrix.postRotate(180);
                            } else if (orientation == 8) {
                                matrix.postRotate(270);
                            }
                            Bitmap mb = BitmapFactory.decodeFile(capturedImages.get(i).getGalleryPath());
                            if (mb != null) {
                                Bitmap myBitmap = Bitmap.createBitmap(mb, 0, 0, mb.getWidth(), mb.getHeight(), matrix, true); // rotating bitmap
                                File imageFile = null;
                                rotatedBitmap = myBitmap;

                                File folder = new File(getFilesDir().getAbsolutePath() + "/Pronovos/");
                                boolean success = true;
                                fileName = String.valueOf(DateFormatter.currentTimeMillisLocal());
                                if (!folder.exists()) {
                                    success = folder.mkdirs();
                                }
                                if (success) {
                                    imageFile = new File(folder.getAbsolutePath()
                                            + File.separator
                                            + fileName + suffix + ".jpg");

                                    imageFile.createNewFile();

                                }
//                                        saveImage(rotatedBitmap, file, fileName);

                                ByteArrayOutputStream ostream = new ByteArrayOutputStream();

                                // save image into gallery
                                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, ostream);

                                FileOutputStream fout = new FileOutputStream(imageFile);
                                fout.write(ostream.toByteArray());


                                fout.close();
                                ContentValues values = new ContentValues();

                                values.put(MediaStore.Images.Media.DATE_TAKEN,
                                        System.currentTimeMillis());
                                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                                values.put(MediaStore.MediaColumns.DATA,
                                        imageFile.getAbsolutePath());

                                captureImageList.add(fileName + suffix + ".jpg");

                                imageModel.setPath(fileName + ".jpg");
                                capturedImages.set(i, imageModel);
                            }

                        }

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return captureImageList;
        }

        @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
        @Override
        protected void onPostExecute(ArrayList result) {
            if (captureImageList.size() > 0) {
                startActivity(new Intent(context, PronovosCameraActivity.class)
                        .putExtra("totalImageCount", 25)
                        .putExtra("pjProjectId", pjProjectId)
                        .putExtra("albumMobileId", albumMobileId)
                        .putExtra("dateString", dateString)
                        .putStringArrayListExtra("captureImageList", captureImageList));

            }
            CustomProgressBar.dissMissDialog(context);

        }


    }*/
}
