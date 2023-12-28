package com.pronovoscm;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pronovoscm.activity.PunchlistPhotoEditFragment;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.fragments.UploadPhotoFragment;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.customcamera.CameraUtils;
import com.pronovoscm.utils.customcamera.CameraView;
import com.pronovoscm.utils.customcamera.Facing;
import com.pronovoscm.utils.customcamera.Flash;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;


//import com.pronovoscm.camerakit.CameraKit;
//import com.pronovoscm.camerakit.CameraView;
//import com.pronovoscm.camerakit.OnCameraKitEvent;
//import static com.pronovoscm.camerakit.CameraKit.Constants.FLASH_AUTO;
//import static com.pronovoscm.camerakit.CameraKit.Constants.FLASH_OFF;
//import static com.pronovoscm.camerakit.CameraKit.Constants.FLASH_ON;

public class CameraControls extends LinearLayout {
    private final AlertDialog alertDialog;
    @Inject
    ProjectsProvider mProjectsProvider;
    @BindView(R.id.facingButton)
    ImageView facingButton;
    //
//    @BindView(R.id.flashButton)
//    ImageView flashButton;
    @BindView(R.id.nextTextView)
    TextView nextTextView;
    @BindView(R.id.flash)
    ImageView flashButton;
    @BindView(R.id.flashOn)
    ImageView flashOnCameraButton;
    @BindView(R.id.flashOff)
    ImageView flashOffCameraButton;
    @BindView(R.id.flashAuto)
    ImageView flashAutoCameraButton;
    @BindView(R.id.captureButton)
    ImageView captureButton;
    @BindView(R.id.flashBack)
    ConstraintLayout flashBack;
    //@OnCameraKitEvent(CameraKitImage.class)
    Bitmap rotatedBitmap = null;
    private LoginResponse loginResponse;
    private int cameraViewId = -1;
    private CameraView cameraView;
    private int coverViewId = -1;
    private View coverView;
    private long captureStartTime;
    private boolean capturingVideo;
    private ArrayList<String> captureImageList = new ArrayList<>();
    private String suffix;
    private Context mContext;
    private boolean isAlertVisible = false;
    private int totalImageCount = 0;

    public CameraControls(Context context) {
        this(context, null);
    }

    public CameraControls(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraControls(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.camera_controls, this);
        alertDialog = new AlertDialog.Builder(getContext()).create();
        ButterKnife.bind(this);
        mContext = context;
        ((PronovosApplication) ((AppCompatActivity) mContext).getApplication()).getDaggerComponent().inject(this);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(context).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        totalImageCount = ((Activity) mContext).getIntent().getIntExtra("totalImageCount", 0);
        if (totalImageCount == 1) {
            facingButton.setVisibility(GONE);
            nextTextView.setVisibility(GONE);
        }
        suffix = loginResponse.getUserDetails().getAuthtoken().substring(loginResponse.getUserDetails().getAuthtoken().length() - 3);
        captureImageList = mProjectsProvider.getCaptureImageList();
        if (captureImageList.size() > 0) {
            nextTextView.setText("Next (" + captureImageList.size() + ")");
            nextTextView.setBackground(ContextCompat.getDrawable(nextTextView.getContext(), R.drawable.rounded_blue_button));
        } else {
            nextTextView.setText("Next");
            nextTextView.setBackground(ContextCompat.getDrawable(nextTextView.getContext(), R.drawable.rounded_gray_button));
        }
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.CameraControls,
                    0, 0);

            try {
                cameraViewId = a.getResourceId(R.styleable.CameraControls_camera, -1);
                coverViewId = a.getResourceId(R.styleable.CameraControls_cover, -1);
            } finally {
                a.recycle();
            }
        }

        if (mProjectsProvider.getCameraFlash() == Flash.ON.value()) {
            flashButton.setImageResource(R.drawable.ic_flash_on_yellow);
        } else if (mProjectsProvider.getCameraFlash() == Flash.AUTO.value()) {
            flashButton.setImageResource(R.drawable.ic_flash_auto_yellow);
        } else {
            flashButton.setImageResource(R.drawable.ic_flash_off_yellow);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (cameraViewId != -1) {
            View view = getRootView().findViewById(cameraViewId);
            if (view instanceof CameraView) {
                cameraView = (CameraView) view;
                setFacingImageBasedOnCamera();
            }
        }
        if (coverViewId != -1) {
            View view = getRootView().findViewById(coverViewId);
            if (view != null) {
                coverView = view;
                coverView.setVisibility(GONE);
            }
        }
        if (mProjectsProvider.getCameraFlash() == Flash.ON.value()) {
            flashButton.setImageResource(R.drawable.ic_flash_on_yellow);
            cameraView.setFlash(Flash.ON);
        } else if (mProjectsProvider.getCameraFlash() == Flash.AUTO.value()) {
            flashButton.setImageResource(R.drawable.ic_flash_auto_yellow);
            cameraView.setFlash(Flash.AUTO);
        } else {
            cameraView.setFlash(Flash.OFF);
        }
    }

    private void setFacingImageBasedOnCamera() {
        facingButton.setImageResource(R.drawable.ic_camera_flip);
        cameraView.setFlash(Flash.OFF);
    }

    public void onImageCaptured() {

    }

    public void imageCaptured(byte[] jpeg) {
        if (captureImageList.size() < totalImageCount) {


            long callbackTime = System.currentTimeMillis();
            File imageFile;

            try {
                File folder = new File(mContext.getFilesDir().getAbsolutePath() + "/Pronovos/");
                boolean success = true;
                String fileName = String.valueOf(DateFormatter.currentTimeMillisLocal());
                if (!folder.exists()) {
                    success = folder.mkdirs();
                }
                if (success) {
                    imageFile = new File(folder.getAbsolutePath()
                            + File.separator
                            + fileName + suffix + ".jpg");

                    imageFile.createNewFile();

                } else {
                    if (totalImageCount != 1) {
                        captureButton.setEnabled(true);
                    }
                    return;
                }

                CameraUtils.decodeBitmap(jpeg, new CameraUtils.BitmapCallback() {
                    @Override
                    public void onBitmapReady(Bitmap bitmap) {
                        rotatedBitmap = bitmap;
                        try {
                            saveImage(rotatedBitmap, imageFile, fileName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                if (totalImageCount != 1) {
                    captureButton.setEnabled(true);
                }

            } catch (Exception e) {
                e.printStackTrace();
                captureButton.setEnabled(true);

            }
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
        if (captureImageList.size() < totalImageCount) {
            captureImageList.add(fileName + suffix + ".jpg");
        }

        ((Activity) getContext()).runOnUiThread(() -> {
            nextTextView.setText("Next (" + captureImageList.size() + ")");
            nextTextView.setBackground(ContextCompat.getDrawable(nextTextView.getContext(), R.drawable.rounded_blue_button));
            mProjectsProvider.setCaptureImageList(captureImageList);
            captureButton.setEnabled(true);
            if (mProjectsProvider.getCaptureImageList().size() > 0 && totalImageCount == 1) {
                captureButton.setEnabled(false);
                FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                PunchlistPhotoEditFragment fragment = new PunchlistPhotoEditFragment();
                Bundle bundle = new Bundle();

                bundle.putString("file_location", mProjectsProvider.getCaptureImageList().get(0));
                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.photoContainer, fragment, fragment.getClass().getSimpleName()).addToBackStack(UploadPhotoFragment.class.getName());
                fragmentTransaction.commit();
                mProjectsProvider.setCaptureImageList(new ArrayList<>());
            }

        });
    }

    @OnTouch(R.id.nextTextView)
    boolean onTouchNextButton(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                break;
            }

            case MotionEvent.ACTION_UP: {
                String dateString = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    dateString = Calendar.getInstance().toString();
                } else {
                    dateString = new Date().toString();
                }
                if (mProjectsProvider.getCaptureImageList().size() > 0 && totalImageCount != 1) {
                    FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    UploadPhotoFragment fragment = new UploadPhotoFragment();
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("captured_images", mProjectsProvider.getCaptureImageList());
                    bundle.putString("dateString", dateString);
                    fragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.photoContainer, fragment, fragment.getClass().getSimpleName()).addToBackStack(UploadPhotoFragment.class.getName());
                    fragmentTransaction.commit();
                    mProjectsProvider.setCaptureImageList(new ArrayList<>());
                } else if (mProjectsProvider.getCaptureImageList().size() > 0) {
                    FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    PunchlistPhotoEditFragment fragment = new PunchlistPhotoEditFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("file_location", mProjectsProvider.getCaptureImageList().get(0));
                    fragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.photoContainer, fragment, fragment.getClass().getSimpleName()).addToBackStack(UploadPhotoFragment.class.getName());
                    fragmentTransaction.commit();
                    mProjectsProvider.setCaptureImageList(new ArrayList<>());
                }
                break;
            }
        }
        return true;
    }

    @OnClick(R.id.captureButton)
    public void onCaptureClick() {
        if (capturingVideo) {
            capturingVideo = false;
        } else {

            if (captureImageList.size() < totalImageCount) {
                captureButton.setEnabled(false);
                captureStartTime = System.currentTimeMillis();
                cameraView.capturePicture(this::imageCaptured);
            } else if (totalImageCount != 1) {
                // TODO: 22/10/18 show alert for the max limit has reached
                isAlertVisible = true;
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
//                alertDialog.setTitle(getContext().getString(R.string.message));
                alertDialog.setMessage(getContext().getString(R.string.more_than_10_image_message));
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getContext().getString(R.string.ok), (dialog, which) -> {
                            dialog.dismiss();
                        }
                );
                alertDialog.setCancelable(false);
                alertDialog.show();
                alertDialog.setOnDismissListener(dialog -> isAlertVisible = false);
                Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            } else {

                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
                isAlertVisible = true;
//                alertDialog.setTitle(getContext().getString(R.string.message));
                alertDialog.setMessage(getContext().getString(R.string.more_than_1_image_message));
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getContext().getString(R.string.ok), (dialog, which) -> {
                            dialog.dismiss();
                        }
                );
                alertDialog.setCancelable(false);
                alertDialog.show();
                alertDialog.setOnDismissListener(dialog -> isAlertVisible = false);
                Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            }
        }
    }

    @OnTouch(R.id.facingButton)
    boolean onTouchFacing(final View view, MotionEvent motionEvent) {
        handleViewTouchFeedback(view, motionEvent);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_UP: {
                if (cameraView.getFacing() == Facing.FRONT) {
                    cameraView.setFacing(Facing.BACK);
                    flashBack.setVisibility(VISIBLE);
                    mProjectsProvider.setCAMERA_CURRNT_FACE(Facing.BACK.value());
                } else {
                    cameraView.setFacing(Facing.FRONT);
                    flashBack.setVisibility(GONE);

                    mProjectsProvider.setCAMERA_CURRNT_FACE(Facing.FRONT.value());
                }
                if (mProjectsProvider.getCameraFlash() == Flash.ON.value()) {
                    flashButton.setImageResource(R.drawable.ic_flash_on_yellow);
                } else if (mProjectsProvider.getCameraFlash() == Flash.AUTO.value()) {
                    flashButton.setImageResource(R.drawable.ic_flash_auto_yellow);
                } else {
                    flashButton.setImageResource(R.drawable.ic_flash_off_yellow);
                }
                cameraView.setFlash(Flash.fromValue(mProjectsProvider.getCameraFlash()));
                break;
            }
        }
        return true;
    }

    @OnTouch(R.id.flash)
    boolean onTouchFlash(View view, MotionEvent motionEvent) {
        handleViewTouchFeedback(view, motionEvent);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_UP: {
                flashButton.setVisibility(GONE);
                flashOnCameraButton.setVisibility(VISIBLE);
                flashOffCameraButton.setVisibility(VISIBLE);
                flashAutoCameraButton.setVisibility(VISIBLE);


                flashOnCameraButton.setImageResource(R.drawable.ic_flash_on);
                flashAutoCameraButton.setImageResource(R.drawable.ic_flash_auto);
                flashOffCameraButton.setImageResource(R.drawable.ic_flash_off);
                if (mProjectsProvider.getCameraFlash() == Flash.ON.value()) {
                    flashOnCameraButton.setImageResource(R.drawable.ic_flash_on_yellow);
                } else if (mProjectsProvider.getCameraFlash() == Flash.AUTO.value()) {
                    flashAutoCameraButton.setImageResource(R.drawable.ic_flash_auto_yellow);
                } else {
                    flashOffCameraButton.setImageResource(R.drawable.ic_flash_off_yellow);
                }

                break;
            }
        }
        return true;
    }

    @OnTouch(R.id.flashOn)
    boolean onTouchFlashOn(View view, MotionEvent motionEvent) {
        handleViewTouchFeedback(view, motionEvent);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_UP: {
                if (cameraView.getFlash() == Flash.OFF || cameraView.getFlash() == Flash.AUTO) {
                    cameraView.setFlash(Flash.ON);
//                    cameraView.setMethod(CameraKit.Constants.METHOD_STANDARD);
                    mProjectsProvider.setCameraFlash(Flash.ON.value());
                }
                flashButton.setImageResource(R.drawable.ic_flash_on_yellow);
                hideAllFlashButtons();
                break;
            }
        }
        return true;
    }

    @OnTouch(R.id.flashOff)
    boolean onTouchFlashOff(View view, MotionEvent motionEvent) {
        handleViewTouchFeedback(view, motionEvent);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_UP: {
                if (cameraView.getFlash() == Flash.ON || cameraView.getFlash() == Flash.AUTO) {
                    cameraView.setFlash(Flash.OFF);
                    mProjectsProvider.setCameraFlash(Flash.OFF.value());
                }
                flashButton.setImageResource(R.drawable.ic_flash_off_yellow);
                hideAllFlashButtons();
                break;
            }
        }
        return true;
    }

    @OnTouch(R.id.flashAuto)
    boolean onTouchFlashAuto(View view, MotionEvent motionEvent) {
        handleViewTouchFeedback(view, motionEvent);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_UP: {
                if (cameraView.getFlash() == Flash.ON || cameraView.getFlash() == Flash.OFF) {
                    cameraView.setFlash(Flash.AUTO);
                    mProjectsProvider.setCameraFlash(Flash.AUTO.value());
                }
                flashButton.setImageResource(R.drawable.ic_flash_auto_yellow);
                hideAllFlashButtons();
                break;
            }
        }
        return true;
    }

    private void capturePhoto() {
//        if (mCapturingPicture) return;
//        mCapturingPicture = true;
//        mCaptureTime = System.currentTimeMillis();
//        mCaptureNativeSize = camera.getPictureSize();
//        message("Capturing picture...", false);
//        camera.capturePicture();
    }

    private void hideAllFlashButtons() {
        flashOnCameraButton.setVisibility(View.GONE);
        flashOffCameraButton.setVisibility(View.GONE);
        flashAutoCameraButton.setVisibility(View.GONE);
        flashButton.setVisibility(View.VISIBLE);
    }

    boolean handleViewTouchFeedback(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                touchDownAnimation(view);
                return true;
            }

            case MotionEvent.ACTION_UP: {
                touchUpAnimation(view);
                return true;
            }

            default: {
                return true;
            }
        }
    }

    void touchDownAnimation(View view) {
        view.animate()
                .scaleX(0.88f)
                .scaleY(0.88f)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    void touchUpAnimation(View view) {
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    void changeViewImageResource(final ImageView imageView, @DrawableRes final int resId) {
        imageView.setRotation(0);
        imageView.animate()
                .rotationBy(360)
                .setDuration(400)
                .setInterpolator(new OvershootInterpolator())
                .start();

        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.setImageResource(resId);
            }
        }, 120);
    }

    public void setNewImageList() {
        captureImageList = mProjectsProvider.getCaptureImageList();
        if (captureImageList.size() > 0) {
            nextTextView.setText("Next (" + captureImageList.size() + ")");
            nextTextView.setBackground(ContextCompat.getDrawable(nextTextView.getContext(), R.drawable.rounded_blue_button));
        } else {
            nextTextView.setText("Next");
            nextTextView.setBackground(ContextCompat.getDrawable(nextTextView.getContext(), R.drawable.rounded_gray_button));
        }
    }

    public void updateOrientation(int deviceOrientation) {
        if (deviceOrientation != 0)
            deviceOrientation = deviceOrientation + 180;
        nextTextView.setRotation(deviceOrientation);
        flashButton.setRotation(deviceOrientation);
        flashOnCameraButton.setRotation(deviceOrientation);
        flashOffCameraButton.setRotation(deviceOrientation);
        flashAutoCameraButton.setRotation(deviceOrientation);
        facingButton.setRotation(deviceOrientation);
    }

    public boolean isHavingImages() {
        return !captureImageList.isEmpty();
    }

    public interface OnImageCapturedCallback {
        void onImageCapture(byte[] jpeg);
    }
}
