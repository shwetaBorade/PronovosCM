package com.pronovoscm.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.adapter.EditingToolsAdapter;
import com.pronovoscm.fragments.PropertiesBSFragment;
import com.pronovoscm.fragments.TextEditorDialogFragment;
import com.pronovoscm.model.ToolModel;
import com.pronovoscm.utils.photoeditor.MyPhotoEditor;
import com.pronovoscm.utils.photoeditor.MyPhotoEditorView;
import com.pronovoscm.utils.photoeditor.OnPhotoEditorListener;
import com.pronovoscm.utils.photoeditor.SaveSettings;
import com.pronovoscm.utils.photoeditor.ToolType;
import com.pronovoscm.utils.photoeditor.ViewType;
import com.pronovoscm.utils.ui.LoadImage;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoEditActivity extends BaseActivity implements OnPhotoEditorListener,
        View.OnClickListener,
        PropertiesBSFragment.Properties,
        EditingToolsAdapter.OnItemSelected {

    private static final String TAG = PhotoEditActivity.class.getSimpleName();
    @BindView(R.id.photoImageProgressBar)
    ProgressBar photoImageProgressBar;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.backImageView)
    ImageView backgroundImageView;
    @BindView(R.id.rightImageView)
    ImageView cameraImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.saveButton)
    TextView saveButton;
    //    @BindView(R.id.dateTimeTextView)
//    TextView dateTimeTextView;
    private MyPhotoEditor mPhotoEditor;
    private MyPhotoEditorView mPhotoEditorView1;
    private PropertiesBSFragment mPropertiesBSFragment;
    private RecyclerView mRvTools;
    private EditingToolsAdapter mEditingToolsAdapter;//= new EditingToolsAdapter(this, R.color.black);

    private String fileLocation;
    private String photoDate;
    private ArrayList<ToolModel> mToolList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_photo_editor);

        ButterKnife.bind(this);
        doGetApplication().getDaggerComponent().inject(this);

        initViews();
        mPropertiesBSFragment = new PropertiesBSFragment();
        mPropertiesBSFragment.setPropertiesChangeListener(this);

//        LinearLayoutManager llmTools = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        mRvTools.setLayoutManager(llmTools);


        mPhotoEditor = new MyPhotoEditor.Builder(this, mPhotoEditorView1)
                .setPinchTextScalable(true) // set flag to make text scalable when pinch
                //.setDefaultTextTypeface(mTextRobotoTf)
                //.setDefaultEmojiTypeface(mEmojiTypeFace)
                .build(); // build photo editor sdk

        mPhotoEditor.setOnPhotoEditorListener(this);

        fileLocation = getIntent().getStringExtra("file_location");
        photoDate = getIntent().getStringExtra("photo_date");
//        dateTimeTextView.setText(photoDate);
        Configuration newConfig = getResources().getConfiguration();
//        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        mRvTools.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        } else {
//            mRvTools.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//
//        }
        mToolList = new ArrayList<>();
        mToolList.add(new ToolModel("Color", R.drawable.white_black_rounded_view, ToolType.COLOR_CHOOSER));
        mToolList.add(new ToolModel("Smoothline", R.drawable.ic_pen, ToolType.SMOOTHLINE));
        mToolList.add(new ToolModel("Line", R.drawable.ic_line, ToolType.LINE));
        mToolList.add(new ToolModel("Arrow", R.drawable.ic_arrow, ToolType.ARROW));
        mToolList.add(new ToolModel("Rectangle", R.drawable.ic_rounded_rectangle_539, ToolType.RECTANGLE));
        mToolList.add(new ToolModel("Rectangle Filled", R.drawable.ic_rounded_fill_rectangle, ToolType.RECTANGLE_FILLED));
        mToolList.add(new ToolModel("Circle", R.drawable.ic_circle, ToolType.CIRCLE));
        mToolList.add(new ToolModel("Circle Filled", R.drawable.ic_circle_filled, ToolType.CIRCLE_FILLED));
        mToolList.add(new ToolModel("Eraser", R.drawable.ic_eraser, ToolType.ERASER));
        mToolList.add(new ToolModel("Text", R.drawable.ic_text, ToolType.TEXT));
        mEditingToolsAdapter = new EditingToolsAdapter(this, R.color.black, mToolList, mToolList.get(1).getmToolType());
        mPhotoEditor.setBrushColor(ContextCompat.getColor(this, R.color.black));
        mEditingToolsAdapter.setColorCode(ContextCompat.getColor(this, R.color.black));
        mRvTools.setAdapter(mEditingToolsAdapter);

        new LoadImage(this).LoadImagePath("", "", fileLocation, mPhotoEditorView1.getSource(), photoImageProgressBar, false, backgroundImageView);
        mPhotoEditor.setBrushDrawingMode(true, ToolType.SMOOTHLINE);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
////                mPhotoEditor.setBrushDrawingMode(true, ToolType.SMOOTHLINE);
//            }
//        }, 500);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            mRvTools.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        } else {
//            mRvTools.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        }
//        setContentView(R.layout.activity_photo_editor);
    }

    private void initViews() {
        mPhotoEditorView1 = findViewById(R.id.photoEditorView);
        mRvTools = findViewById(R.id.rvConstraintTools);

//        backImageView.setImageResource(R.drawable.ic_arrow_back);
        backImageView.setImageResource(R.drawable.ic_arrow_back);

        backImageView.setOnClickListener(this);
        cameraImageView.setVisibility(View.INVISIBLE);
        titleTextView.setText(R.string.edit_image);

        findViewById(R.id.container_undo).setOnClickListener(this);
        findViewById(R.id.saveButton).setOnClickListener(this);

    }


    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this, text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {
                mPhotoEditor.editText(rootView, inputText, colorCode);
            }
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews) {
        Log.d(TAG, "onRemoveViewListener() called with: numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onRemoveViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //here
            case R.id.container_undo:
                boolean moreUndo = mPhotoEditor.undo();
                Log.i(TAG, "onClick: " + moreUndo);
                break;
            case R.id.saveButton:
                saveImage();
                break;
            case R.id.leftImageView:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setBrushColor(colorCode);
        mEditingToolsAdapter.setColorCode(colorCode);

    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setOpacity(opacity);
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        mPhotoEditor.setBrushSize(brushSize);
    }


    @Override
    public void onToolSelected(ToolType toolType) {
        switch (toolType) {
            case COLOR_CHOOSER:
                mPropertiesBSFragment.show(getSupportFragmentManager(), mPropertiesBSFragment.getTag());
                break;
            case SMOOTHLINE:
                mPhotoEditor.setBrushDrawingMode(true, ToolType.SMOOTHLINE);
                break;
            case LINE:
                mPhotoEditor.setBrushDrawingMode(true, ToolType.LINE);
                break;
            case RECTANGLE:
                mPhotoEditor.setBrushDrawingMode(true, ToolType.RECTANGLE);
                break;
            case RECTANGLE_FILLED:
                mPhotoEditor.setBrushDrawingMode(true, ToolType.RECTANGLE_FILLED);
                break;
            case SQUARE:
                mPhotoEditor.setBrushDrawingMode(true, ToolType.SQUARE);
                break;
            case CIRCLE:
                mPhotoEditor.setBrushDrawingMode(true, ToolType.CIRCLE);
                break;
            case CIRCLE_FILLED:
                mPhotoEditor.setBrushDrawingMode(true, ToolType.CIRCLE_FILLED);
                break;
            case TRIANGLE:
                mPhotoEditor.setBrushDrawingMode(true, ToolType.TRIANGLE);
                break;
            case ARROW:
                mPhotoEditor.setBrushDrawingMode(true, ToolType.ARROW);
                break;
            case TEXT:
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode) {
                        mPhotoEditor.addText(inputText, colorCode);
                    }
                });
                break;
            case ERASER:
                mPhotoEditor.setBrushDrawingMode(true, ToolType.ERASER);
//                mPhotoEditor.brushEraser();
                break;
            case UNDO:
                mPhotoEditor.undo();
                break;
            case REDO:
                mPhotoEditor.redo();
                break;
        }
    }

    @Override
    public void isPermissionGranted(boolean isGranted, String permission) {
        if (isGranted) {
            saveImage();
        }
    }

    @Override
    protected int doGetContentView() {
        return R.layout.activity_photo_editor;
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you want to exit without saving image ?");
        builder.setPositiveButton("Save", (dialog, which) -> saveImage());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setNeutralButton("Discard", (dialog, which) -> finish());
        builder.create().show();

    }

    @SuppressLint("MissingPermission")
    private void saveImage() {
        if (requestPermission(getExternalPermission())) {
            showLoading("Saving...");
//            File file = new File(Environment.getExternalStorageDirectory()
//                    + File.separator + ""
//                    + System.currentTimeMillis() + ".png");

            File file = getFileLocation();


            try {
                String filePath = file.getAbsolutePath();
                file.delete();
                file.createNewFile();

                SaveSettings saveSettings = new SaveSettings.Builder()
                        .setClearViewsEnabled(true)
                        .setTransparencyEnabled(true)
                        .build();

//                File finalFile = file;
                mPhotoEditor.saveAsFile(filePath, saveSettings, new MyPhotoEditor.OnSaveListener() {
                    @Override
                    public void onSuccess(@NonNull String imagePath) {
                        hideLoading();
                        showSnackbar("Image Saved Successfully");
                        mPhotoEditorView1.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
                        Intent intent = new Intent();
                        intent.putExtra("path", filePath);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        hideLoading();
                        showSnackbar("Failed to save Image");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                hideLoading();
                showSnackbar(e.getMessage());
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    private File getFileLocation() {
        File file = new File(getFilesDir().getAbsolutePath() + "/Pronovos/" + fileLocation);
        /*if (Environment.getExternalStorageState().contains(Environment.MEDIA_MOUNTED)) {
            file = new File(Environment
                    .getExternalStorageDirectory() + "/Pronovos/" + fileLocation);
        } else {
            file = new File(Environment
                    .getExternalStorageDirectory() + "/Pronovos/" + fileLocation);
        }*/
        return file;
    }


    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//        alertDialog.setTitle(getString(R.string.message));
        alertDialog.setMessage("Are you sure you want to discard these markups?");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
                    super.onBackPressed();
                }
        );
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        alertDialog.setCancelable(false);
        alertDialog.show();
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(ContextCompat.getColor(this, R.color.gray_948d8d));
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }
}
