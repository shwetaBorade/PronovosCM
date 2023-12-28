package com.pronovoscm.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.adapter.EditingToolsAdapter;
import com.pronovoscm.fragments.BaseFragment;
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

import static com.pronovoscm.activity.BaseActivity.READ_WRITE_STORAGE;

public class PunchlistPhotoEditFragment extends BaseFragment implements OnPhotoEditorListener,
        View.OnClickListener,
        PropertiesBSFragment.Properties,
        EditingToolsAdapter.OnItemSelected {

    private static final String TAG = PunchlistPhotoEditFragment.class.getSimpleName();
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
    @BindView(R.id.dateTimeTextView)
    TextView dateTimeTextView;
    private MyPhotoEditor mPhotoEditor;
    private MyPhotoEditorView mPhotoEditorView1;
    private PropertiesBSFragment mPropertiesBSFragment;
    private RecyclerView mRvTools;
    private EditingToolsAdapter mEditingToolsAdapter;//= new EditingToolsAdapter(this, R.color.black);
    private ArrayList<ToolModel> mToolList;

    private String fileLocation;
    private ProgressDialog mProgressDialog;
    private boolean isGallery;
//    private String photoDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_photo_editor, container, false);
        ButterKnife.bind(this, rootView);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);

//        getActivity().setRequestedOrientation(getActivity().getChangingConfigurations());

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        mPropertiesBSFragment = new PropertiesBSFragment();
        mPropertiesBSFragment.setPropertiesChangeListener(this);

//        LinearLayoutManager llmTools = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        mRvTools.setLayoutManager(llmTools);


        mPhotoEditor = new MyPhotoEditor.Builder(getContext(), mPhotoEditorView1)
                .setPinchTextScalable(true)
                .build(); // build photo editor sdk

        mPhotoEditor.setOnPhotoEditorListener(this);

        fileLocation = getArguments().getString("file_location");
        isGallery = getArguments().getBoolean("isGallery");
//        photoDate = getIntent().getStringExtra("photo_date");
//        dateTimeTextView.setText(photoDate);
        Configuration newConfig = getResources().getConfiguration();
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRvTools.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        } else {
            mRvTools.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        }
        mToolList = new ArrayList<>();
        mToolList.add(new ToolModel("Color", R.drawable.white_black_rounded_view, ToolType.COLOR_CHOOSER));
        mToolList.add(new ToolModel("Smoothline", R.drawable.ic_pen, ToolType.SMOOTHLINE));
        mEditingToolsAdapter = new EditingToolsAdapter(this, R.color.red_color_picker, mToolList, mToolList.get(1).getmToolType());
        mPhotoEditor.setBrushColor(ContextCompat.getColor(getActivity(), R.color.red_color_picker));
        mEditingToolsAdapter.setColorCode(ContextCompat.getColor(getActivity(), R.color.red_color_picker));
        mRvTools.setAdapter(mEditingToolsAdapter);


        new LoadImage(getContext()).LoadImagePath("", "", fileLocation, mPhotoEditorView1.getSource(), photoImageProgressBar, false, backgroundImageView);
        mPhotoEditor.setBrushDrawingMode(true, ToolType.SMOOTHLINE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void initViews(View view) {
        mPhotoEditorView1 = view.findViewById(R.id.photoEditorView);
        mRvTools = view.findViewById(R.id.rvConstraintTools);

//        backImageView.setImageResource(R.drawable.ic_arrow_back);
        backImageView.setImageResource(R.drawable.ic_arrow_back);

        backImageView.setOnClickListener(this);
        cameraImageView.setVisibility(View.INVISIBLE);
        titleTextView.setText(R.string.edit_image);

        view.findViewById(R.id.container_undo).setOnClickListener(this);
        view.findViewById(R.id.saveButton).setOnClickListener(this);

    }


    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show((AppCompatActivity) getActivity(), text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {
                mPhotoEditor.editText(rootView, inputText, colorCode);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

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
                getActivity().onBackPressed();
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
                mPropertiesBSFragment.show(getFragmentManager(), mPropertiesBSFragment.getTag());
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
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show((AppCompatActivity) getActivity());
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

//    @Override
//    public void isPermissionGranted(boolean isGranted, String permission) {
//        if (isGranted) {
//            saveImage();
//        }
//    }

//    @Override
//    protected int doGetContentView() {
//        return R.layout.activity_photo_editor;
//    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you want to exit without saving image ?");
        builder.setPositiveButton("Save", (dialog, which) -> saveImage());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setNeutralButton("Discard", (dialog, which) -> getActivity().finish());
        builder.create().show();

    }

    public boolean requestPermission(String permission) {
        boolean isGranted = checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED;
        if (!isGranted) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{permission},
                    READ_WRITE_STORAGE);
        }
        return isGranted;
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
//                        showSnackbar("Image Saved Successfully");
                        mPhotoEditorView1.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
                        Intent intent = new Intent();
                        intent.putExtra("image_path", filePath);
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();
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

    /**
     * Show the loader over any activity
     *
     * @param message message shown in the loader
     */
    protected void showLoading(@NonNull String message) {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private File getFileLocation() {
        File file;
        if (isGallery) {

            file = new File(fileLocation);
        } else {
            file = new File(getContext().getFilesDir().getAbsolutePath() + "/Pronovos/" + fileLocation);
        }
        /*if (Environment.getExternalStorageState().contains(Environment.MEDIA_MOUNTED)) {
            file = new File(Environment
                    .getExternalStorageDirectory() + "/Pronovos/" + fileLocation);
        } else {
            file = new File(Environment
                    .getExternalStorageDirectory() + "/Pronovos/" + fileLocation);
        }*/
        return file;
    }

    /**
     * Hide the loader
     */
    protected void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * Show snack bar to notify user for any event
     *
     * @param message message to be shown in snack bar
     */
    protected void showSnackbar(@NonNull String message) {
        View view = getView().findViewById(android.R.id.content);
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
//        alertDialog.setTitle(getString(R.string.message));
        alertDialog.setMessage("Are you sure you want to discard these markups?");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
//                    super.onBackPressed();
                }
        );
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        alertDialog.setCancelable(false);
        alertDialog.show();
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_948d8d));
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
    }
}
