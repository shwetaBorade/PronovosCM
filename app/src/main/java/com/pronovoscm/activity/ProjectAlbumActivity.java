package com.pronovoscm.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pronovoscm.R;
import com.pronovoscm.adapter.AlbumAdapter;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.galleryimagepicker.GalleryPickerActivity;
import com.pronovoscm.model.TransactionLogUpdate;
import com.pronovoscm.model.TransactionModuleEnum;
import com.pronovoscm.model.request.albums.Album;
import com.pronovoscm.model.request.albums.AlbumRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.PhotoFolder;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.dialogs.AddFolderDialog;
import com.pronovoscm.utils.dialogs.MessageDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProjectAlbumActivity extends BaseActivity implements View.OnClickListener, AlbumAdapter.addNewFolder {
    public static final int PERMISSION_READ_REQUEST_CODE = 113;
    public static final int FILESTORAGE_REQUEST_CODE = 221;
    public static final int SELECT_PICTURE = 5645;
    public static final int FILECAMERA_REQUEST_CODE = 331;
    private static final int TAKE_PICTURE = 3115;
    @Inject
    ProjectsProvider projectsProvider;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.cameraImageView)
    ImageView cameraImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.albumRecyclerView)
    RecyclerView albumRecyclerView;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.searchAlbumEditText)
    EditText searchAlbumEditText;
    private AlbumAdapter mAlbumAdapter;
    private List<PhotoFolder> mAlbumList;
    private String projectName;
    private int projectId;
    private LoginResponse loginResponse;
    private int canCreateAlbum;
    private MessageDialog messageDialog;
    private ArrayList<String> captureImageList = new ArrayList<>();
    private String suffix;
    private Bitmap rotatedBitmap;
    private long mLastClickTime = 0;
    private long mMenuLastClickTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        doGetApplication().getDaggerComponent().inject(this);
        messageDialog = new MessageDialog();
        projectName = getIntent().getStringExtra("project_name");
        projectId = getIntent().getIntExtra("project_id", 0);
        projectsProvider.setCaptureImageList(new ArrayList<>());

        titleTextView.setText(getString(R.string.albums));
        mAlbumList = new ArrayList<>();
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        canCreateAlbum = loginResponse.getUserDetails().getPermissions().get(0).getCreateAlbum();
        if (canCreateAlbum == 1) {
            mAlbumList.add(null);
        }
        mAlbumAdapter = new AlbumAdapter(ProjectAlbumActivity.this, mAlbumList);
        Configuration newConfig = getResources().getConfiguration();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            albumRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            // set background for landscape
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            albumRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

            // set background for portrait
        }
        albumRecyclerView.setAdapter(mAlbumAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{getExternalPermission()}, FILESTORAGE_REQUEST_CODE);
        } else {
//            callAlbumService();
        }
        backImageView.setOnClickListener(this);
        cameraImageView.setOnClickListener(this);
//        backImageView.setImageResource(R.drawable.ic_arrow_back);
//        cameraImageView.setImageResource(R.drawable.ic_camera);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        cameraImageView.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_add_photo));
        rightImageView.setVisibility(View.INVISIBLE);
        if (loginResponse.getUserDetails().getPermissions().get(0).getUploadPhoto() == 1) {
            cameraImageView.setVisibility(View.VISIBLE);
        } else {
            cameraImageView.setVisibility(View.GONE);
        }
        searchAlbumEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<PhotoFolder> photoFolders = projectsProvider.getPhotoFolders(projectId, searchAlbumEditText.getText().toString());
                mAlbumList.clear();
                if (canCreateAlbum == 1) {
                    mAlbumList.add(null);
                }
                mAlbumList.addAll(photoFolders);
                mAlbumAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            albumRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            albumRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == FILESTORAGE_REQUEST_CODE) {
            //resume tasks needing this permission
            callAlbumService();
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == FILECAMERA_REQUEST_CODE) {
            //resume tasks needing this permission
            projectsProvider.setCaptureImageList(new ArrayList<>());
            captureImageList = new ArrayList<>();
            startActivity(new Intent(this, PronovosCameraActivity.class).putExtra("totalImageCount", 25).putExtra("pjProjectId", projectId).putExtra("albumMobileId", (long) -1).putStringArrayListExtra("captureImageList", captureImageList));

        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == PERMISSION_READ_REQUEST_CODE) {
           /* Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            intent.setType("image/*");
            startActivityForResult(
                    Intent.createChooser(intent, getString(R.string.select_picture)),
                    SELECT_PICTURE);*/
            startActivityForResult(new Intent(this, GalleryPickerActivity.class)
                    .putParcelableArrayListExtra("captured_images", new ArrayList<>())
                    .putExtra("pjProjectId", projectId)
                    .putExtra(Constants.INTENT_KEY_ALBUM_MOBILE_ID, -1L), SELECT_PICTURE);

        }
    }

    @Override
    protected int doGetContentView() {
        return R.layout.project_album_view;
    }


    private void callAlbumService() {
        List<Album> list = new ArrayList<>();
//        List<Album> list = projectsProvider.getAllNonSyncFolder(projectId);
        AlbumRequest albumRequest = new AlbumRequest(list, projectId);
        projectsProvider.getProjectAlbum(albumRequest, new ProviderResult<List<PhotoFolder>>() {
            @Override
            public void success(List<PhotoFolder> result) {
                List<PhotoFolder> photoFolders = projectsProvider.getPhotoFolders(projectId, searchAlbumEditText.getText().toString());
                mAlbumList.clear();
                if (canCreateAlbum == 1) {
                    mAlbumList.add(null);
                }
                mAlbumList.addAll(photoFolders);
                mAlbumAdapter.notifyDataSetChanged();

            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(ProjectAlbumActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(ProjectAlbumActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(ProjectAlbumActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
//                projectsProvider.showMessageAlert(ProjectAlbumActivity.this, message, getString(R.string.ok));


                messageDialog.showMessageAlert(ProjectAlbumActivity.this, message, getString(R.string.ok));
//                messageDialog.showMessageAlert(ProjectAlbumActivity.this, getString(R.string.failureMessage), getString(R.string.ok));
                List<PhotoFolder> photoFolders = projectsProvider.getPhotoFolders(projectId, searchAlbumEditText.getText().toString());
                mAlbumList.clear();
                if (canCreateAlbum == 1) {
                    mAlbumList.add(null);
                }
                mAlbumList.addAll(photoFolders);
                mAlbumAdapter.notifyDataSetChanged();
//                mAlbumAdapter = new AlbumAdapter(ProjectAlbumActivity.this, mAlbumList, projectId);
//                albumRecyclerView.setAdapter(mAlbumAdapter);

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
                // Preventing multiple clicks, using threshold of 1 second
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this, new String[]{getExternalPermission()}, FILESTORAGE_REQUEST_CODE);
                } else {
                    selectImage();
                }
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
                // Preventing multiple clicks, using threshold of 1 second
                if (SystemClock.elapsedRealtime() - mMenuLastClickTime < 1000) {
                    return;
                }
                mMenuLastClickTime = SystemClock.elapsedRealtime();
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{
                                Manifest.permission.CAMERA,
                                getExternalPermission()}, FILECAMERA_REQUEST_CODE);
                    } else {
                        projectsProvider.setCaptureImageList(new ArrayList<>());
                        captureImageList = new ArrayList<>();
                        startActivity(new Intent(this, PronovosCameraActivity.class).putExtra("totalImageCount", 25).putExtra("pjProjectId", projectId).putExtra("albumMobileId", (long) -1).putStringArrayListExtra("captureImageList", captureImageList));

                    }
                } else {
                    projectsProvider.setCaptureImageList(new ArrayList<>());
                    captureImageList = new ArrayList<>();
                    startActivity(new Intent(this, PronovosCameraActivity.class).putExtra("totalImageCount", 25).putExtra("pjProjectId", projectId).putExtra("albumMobileId", (long) -1).putStringArrayListExtra("captureImageList", captureImageList));

                }
            } else if (items[item].equals(getString(R.string.choose_from_library))) {
                // Preventing multiple clicks, using threshold of 1 second
                if (SystemClock.elapsedRealtime() - mMenuLastClickTime < 1000) {
                    return;
                }
                mMenuLastClickTime = SystemClock.elapsedRealtime();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        checkSelfPermission(getExternalPermission()) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{getExternalPermission()}, PERMISSION_READ_REQUEST_CODE);
                } else {
                    startActivityForResult(new Intent(this, GalleryPickerActivity.class).putParcelableArrayListExtra("captured_images", new ArrayList<>()).putExtra("pjProjectId", projectId)
                            .putExtra("albumMobileId", -1), SELECT_PICTURE);
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
                        captureImageList = new ArrayList<>();
                    }
                }
                break;
        }

    }

    private String getExifTag(ExifInterface exif, String tag) {
        String attribute = exif.getAttribute(tag);

        return (null != attribute ? attribute : "");
    }

    @Override
    public void onAddNewFolder() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{getExternalPermission()}, FILESTORAGE_REQUEST_CODE);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            AddFolderDialog addFolderDialog = new AddFolderDialog();
            Bundle bundle = new Bundle();
            bundle.putInt("projectId", projectId);
            addFolderDialog.setArguments(bundle);
            addFolderDialog.setCancelable(false);
            addFolderDialog.show(ft, "");
            fm.executePendingTransactions();
            addFolderDialog.getDialog().setOnDismissListener(dialogInterface -> {
                //do whatever you want when dialog is dismissed
//                callAlbumService();
                mAlbumList.clear();
                if (canCreateAlbum == 1) {
                    mAlbumList.add(null);
                }
                List<PhotoFolder> photoFolders = projectsProvider.getPhotoFolders(projectId, searchAlbumEditText.getText().toString());
                mAlbumList.addAll(photoFolders);
                mAlbumAdapter.notifyDataSetChanged();
            });
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String event) {
        if (event != null && event.equals("addNEWFolder")) {
            mAlbumList.clear();
            if (canCreateAlbum == 1) {
                mAlbumList.add(null);
            }
            List<PhotoFolder> photoFolders = projectsProvider.getPhotoFolders(projectId, searchAlbumEditText.getText().toString());
            mAlbumList.addAll(photoFolders);
            mAlbumAdapter.notifyDataSetChanged();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TransactionLogUpdate transactionLogUpdate) {
        Log.i("TEMP", "  bind: 1 ");
        if (transactionLogUpdate.getTransactionModuleEnum() != null && (transactionLogUpdate.getTransactionModuleEnum().equals(TransactionModuleEnum.ALBUM) || transactionLogUpdate.getTransactionModuleEnum().equals(TransactionModuleEnum.PHOTO))) {
            Log.i("TEMP", "  bind: 2 ");
            mAlbumList.clear();
            if (canCreateAlbum == 1) {
                mAlbumList.add(null);
            }
            List<PhotoFolder> photoFolders = projectsProvider.getPhotoFolders(projectId, searchAlbumEditText.getText().toString());
            mAlbumList.addAll(photoFolders);
            mAlbumAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        projectsProvider.setCaptureImageList(new ArrayList<>());
        searchAlbumEditText.setFocusable(true);
        searchAlbumEditText.setFocusableInTouchMode(true);
        projectId = getIntent().getIntExtra("project_id", 0);
        List<PhotoFolder> photoFolders = projectsProvider.getPhotoFolders(projectId, searchAlbumEditText.getText().toString());
        mAlbumList.clear();
        if (canCreateAlbum == 1) {
            mAlbumList.add(null);
        }
        mAlbumList.addAll(photoFolders);
        mAlbumAdapter.notifyDataSetChanged();
//        mAlbumAdapter = new AlbumAdapter(ProjectAlbumActivity.this, mAlbumList, projectId);
//        if (albumRecyclerView != null) {
//            albumRecyclerView.setAdapter(mAlbumAdapter);
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
//            ActivityCompat.requestPermissions(this, new String[]{getExternalPermission()}, FILESTORAGE_REQUEST_CODE);
        } else {
            callAlbumService();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        searchAlbumEditText.setFocusable(false);
    }

    /* public class StoreImageInBackground extends AsyncTask<Void, Void, ArrayList> {
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
                         .putExtra("pjProjectId", projectId)
                         .putExtra("albumMobileId", -1)
                         .putExtra("dateString", dateString)
                         .putStringArrayListExtra("captureImageList", captureImageList));

             }
             CustomProgressBar.dissMissDialog(context);

         }


     }*/
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
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}
