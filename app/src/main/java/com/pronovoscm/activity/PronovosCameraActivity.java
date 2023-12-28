package com.pronovoscm.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.pronovoscm.CameraControls;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.fragments.UploadPhotoFragment;
import com.pronovoscm.utils.customcamera.CameraView;
import com.pronovoscm.utils.customcamera.Facing;
import com.pronovoscm.utils.customcamera.Flash;
import com.pronovoscm.utils.customcamera.OrientationHelper;
import com.pronovoscm.utils.ui.RotateLayout;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

/**
 * Custom camera having pinch to zoom and auto focus features
 *
 * @author gwl
 */
public class PronovosCameraActivity extends AppCompatActivity implements OrientationHelper.Callback {

    private static final String TAG = PronovosCameraActivity.class.getSimpleName();
    @Inject
    ProjectsProvider mProjectsProvider;

    @BindView(R.id.contentFrame)
    ViewGroup parent;
    @BindView(R.id.backImageView)
    ImageView backImageView;
    @BindView(R.id.backImageView1)
    ImageView backImageViewLandscape;
    @BindView(R.id.camera)
    CameraView camera;
    @BindView(R.id.cameraControls)
    CameraControls mCameraControls;
    String dateString;
    int lastOrientation = -1;
    private RotateLayout mRotateLayout = null;

    private ArrayList<String> captureImageList = new ArrayList<>();
    private int totalImageCount;
    private String fileLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);
        ButterKnife.bind(this);
        ((PronovosApplication) (this).getApplication()).getDaggerComponent().inject(this);
        captureImageList = getIntent().getStringArrayListExtra("captureImageList");
        dateString = getIntent().getStringExtra("dateString");
        fileLocation = getIntent().getStringExtra("file_location");
        totalImageCount = getIntent().getIntExtra("totalImageCount", 0);
        if (captureImageList != null && captureImageList.size() > 0) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            UploadPhotoFragment fragment = new UploadPhotoFragment();
            Bundle bundle = new Bundle();

            bundle.putStringArrayList("captured_images", captureImageList);
            bundle.putString("dateString", dateString);
            fragment.setArguments(bundle);
            fragmentTransaction.add(R.id.photoContainer, fragment, fragment.getClass().getSimpleName());
            fragmentTransaction.commit();
            mProjectsProvider.setCaptureImageList(new ArrayList<>());
        } else if (totalImageCount == 1 && fileLocation != null && !TextUtils.isEmpty(fileLocation)) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            PunchlistPhotoEditFragment fragment = new PunchlistPhotoEditFragment();
            Bundle bundle = new Bundle();

            bundle.putString("file_location", fileLocation);
            bundle.putBoolean("isGallery", true);
            fragment.setArguments(bundle);
            fragmentTransaction.add(R.id.photoContainer, fragment, fragment.getClass().getSimpleName());
            fragmentTransaction.commit();
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        viewSize.setText(camera.getCamera().getHeight()+"  "+camera.getCamera().getWidth());
//        camera.setFlash(mProjectsProvider.getCameraFlash());
            camera.setFlash(Flash.AUTO);
        /*if (mProjectsProvider.getCameraFlash() == FLASH_ON || mProjectsProvider.getCameraFlash() == CameraKit.Constants.FLASH_AUTO) {
            camera.setMethod(CameraKit.Constants.METHOD_STANDARD);
        } else {
            camera.setMethod(CameraKit.Constants.METHOD_STILL);
        }*/
            camera.setCropOutput(false);
//        camera.setFacing(mProjectsProvider.getCAMERA_CURRNT_FACE());
            camera.setFacing(Facing.BACK);
        }


    }

   /* @Override
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
    }*/
    @Override
    protected void onResume() {
        captureImageList = getIntent().getStringArrayListExtra("captureImageList");
        if (captureImageList == null || captureImageList.size() == 0) {
            if (totalImageCount == 1 && fileLocation != null && !TextUtils.isEmpty(fileLocation)) {

            } else {
                camera.start();
            }
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (captureImageList == null || captureImageList.size() == 0) {
            camera.stop();
        }
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        this.recreate();
        Log.i(TAG, "onConfigurationChanged: " + "cameraActivity");
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.photoContainer);
        if ((fragment != null && fragment instanceof PunchlistPhotoEditFragment)) {
            showConfirmationDialog();
        } else if ((fragment != null && fragment instanceof UploadPhotoFragment) || mCameraControls.isHavingImages()) {

            showConfirmationDialog();
        } else {
            super.onBackPressed();
        }
        mProjectsProvider.setCaptureImageList(new ArrayList<>());
    }


    @OnTouch({R.id.backImageView, R.id.backImageView1})
    boolean onTouchBack(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_UP: {
                onBackPressed();
                break;
            }
        }
        return true;
    }

    private void showConfirmationDialog() {
        // Create custom dialog object
        final Dialog dialog = new Dialog(PronovosCameraActivity.this);
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_confirmation);

        mRotateLayout = dialog.findViewById(R.id.rotate_layout);
        /*boolean isTablet= getResources().getBoolean(R.bool.isTablet);
        if (isTablet){
            if (lastOrientation==90){
                lastOrientation=0;
            }else {
                lastOrientation=90;
            }
        }
        */
        Log.i(TAG, "showConfirmationDialog: " + lastOrientation);
        rotateConfirmationDialog(lastOrientation);
        dialog.show();

        Button declineButton = dialog.findViewById(R.id.button_cancel);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRotateLayout = null;
                dialog.dismiss();
            }
        });

        Button acceptButton = dialog.findViewById(R.id.button_ok);
        // if decline button is clicked, close the custom dialog
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PronovosCameraActivity.super.onBackPressed();
                mCameraControls.setNewImageList();
                mRotateLayout = null;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                dialog.dismiss();
            }
        });
        rotateConfirmationDialog(getResources().getConfiguration().getLayoutDirection());
    }

    /**
     * Call back of device rotation
     *
     * @param deviceOrientation rotation angle
     */
    @Override
    public void onDeviceOrientationChanged(int deviceOrientation) {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
     /*   if (isTablet){
            if (deviceOrientation==90){
                deviceOrientation=0;
            }else {
                deviceOrientation=90;
            }
        }
*/
        if (isTablet) {
            deviceOrientation = 0;
        }
        this.lastOrientation = deviceOrientation;
        mCameraControls.updateOrientation(deviceOrientation);
        if (deviceOrientation == 0 || deviceOrientation == 180) {
            backImageView.setVisibility(View.VISIBLE);
            backImageViewLandscape.setVisibility(View.GONE);
        } else {
            backImageView.setVisibility(View.GONE);
            backImageViewLandscape.setVisibility(View.VISIBLE);
        }


        rotateConfirmationDialog(deviceOrientation);
    }

    private void rotateConfirmationDialog(int deviceOrientation) {
        if (mRotateLayout != null) {

            int orientation = deviceOrientation;
            if (orientation == 180 ) {
                orientation = 0;
            }

            mRotateLayout.setAngle(orientation);
        }
    }
}
