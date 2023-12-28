package com.pronovoscm.galleryimagepicker;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.pronovoscm.R;
import com.pronovoscm.activity.BaseActivity;
import com.pronovoscm.activity.PronovosCameraActivity;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.FileUtils;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.ui.CustomProgressBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class GalleryPickerActivity extends BaseActivity implements View.OnClickListener, GalleryPickerAdapter.imageClick {
    TextView doneTextView;
    TextView photoTV;
    ImageView imageView;
    ArrayList<ImageModel> captureList;
    ConstraintLayout bottomView;
    @Inject
    ProjectsProvider projectsProvider;
    private ImagesBucketFragment fb;
    private ArrayList<String> captureImageList = new ArrayList<>();
    private String suffix;
    private LoginResponse loginResponse;
    private Bitmap rotatedBitmap;
    private int projectId;
    private long albumMobileId;

    @Override
    protected int doGetContentView() {
        return R.layout.fragment_grid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.fragment_grid);
        doGetApplication().getDaggerComponent().inject(this);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        Log.d("MyTag", "MainActivity");
        projectId = getIntent().getIntExtra("pjProjectId", 0);
        albumMobileId = getIntent().getLongExtra(Constants.INTENT_KEY_ALBUM_MOBILE_ID, -1L);
        addBucketFragment();
        captureList = new ArrayList<>();
        doneTextView = findViewById(R.id.doneTextView);
        doneTextView.setOnClickListener(this);
        bottomView = findViewById(R.id.bottomView);
        imageView = findViewById(R.id.leftImageView);
        photoTV = findViewById(R.id.photoTV);
        imageView.setOnClickListener(this);
        doneTextView.setText("Next");
        bottomView.setVisibility(View.GONE);
    }

    private void addBucketFragment() {
        FragmentManager fm = getSupportFragmentManager();
        fb = ImagesBucketFragment.newInstance();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.view_holder, fb);
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.doneTextView:

                /*Intent intent = getIntent();
                intent.putParcelableArrayListExtra("captured_images", captureList);
                setResult(Activity.RESULT_OK, intent);
                finish();*/
                new StoreImageInBackground(this).executeOnExecutor(THREAD_POOL_EXECUTOR);


                break;
            case R.id.leftImageView:
                super.onBackPressed();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.view_holder);
        if (f != null && f instanceof ImagesBucketFragment) {
            setTitleText("Photos");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onUpdateImageList(ArrayList<ImageModel> captureList) {
        this.captureList = captureList;
        doneTextView.setText("(" + captureList.size() + "/25) Next");
        if (captureList.size() == 0) {
            bottomView.setVisibility(View.GONE);
        } else {
            bottomView.setVisibility(View.VISIBLE);
        }
    }

    public void setTitleText(String folderName) {
        Log.i("GalleryPickerActivity", "setTitleText: " + folderName);
        if (photoTV != null) {
            photoTV.setText(folderName);
        }
    }

    public class StoreImageInBackground extends AsyncTask<Void, Void, ArrayList> {
        Context context;
        String dateString = null;

        public StoreImageInBackground(Context context) {
            this.context = context;
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
            ArrayList<ImageModel> capturedImages = captureList;
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
                        .putExtra("albumMobileId", albumMobileId)
                        .putExtra("dateString", dateString)
                        .putStringArrayListExtra("captureImageList", captureImageList));

            }
            CustomProgressBar.dissMissDialog(context);
            ((Activity) context).finish();
        }


    }
}
