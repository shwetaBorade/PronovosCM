package com.pronovoscm.activity;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;
import static com.pronovoscm.activity.ProjectAlbumActivity.FILECAMERA_REQUEST_CODE;
import static com.pronovoscm.activity.ProjectAlbumActivity.PERMISSION_READ_REQUEST_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.pronovoscm.BuildConfig;
import com.pronovoscm.R;
import com.pronovoscm.data.ProjectFormProvider;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.model.TransactionLogUpdate;
import com.pronovoscm.model.TransactionModuleEnum;
import com.pronovoscm.model.request.submitform.Submission;
import com.pronovoscm.model.request.submitform.SubmitFormRequest;
import com.pronovoscm.model.response.formDelete.DeleteUserFormResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.uploadformfile.UploadFile;
import com.pronovoscm.persistence.domain.FormAssets;
import com.pronovoscm.persistence.domain.FormImage;
import com.pronovoscm.persistence.domain.Forms;
import com.pronovoscm.persistence.domain.FormsName;
import com.pronovoscm.persistence.domain.FormsSchedule;
import com.pronovoscm.persistence.domain.PjProjects;
import com.pronovoscm.persistence.domain.ProjectFormArea;
import com.pronovoscm.persistence.domain.UserForms;
import com.pronovoscm.persistence.repository.ProjectFormRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.dialogs.FormAttachmentDialog;
import com.pronovoscm.utils.dialogs.FormAttachmentWebviewDialog;
import com.pronovoscm.utils.formwebview.MyWebChromeClient;
import com.pronovoscm.utils.formwebview.WebClient;
import com.pronovoscm.utils.ui.CustomProgressBar;
import com.pronovoscm.utils.ui.LoadImageInBackground;

import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class ProjectFormDetailActivity extends BaseActivity {

    public static final int FILESTORAGE_REQUEST_CODE = 221;
    private static final int REQUEST_CODE_ALBUM = 1;
    private static final int REQUEST_CODE_CAMERA = 2;
    private static final int FORM_SEND = 2772;
    //Flage for xls file URL
    public boolean isXLSFile = false;
    public WebView webView;
    @Inject
    ProjectsProvider projectsProvider;
    @Inject
    ProjectFormRepository mprojectFormRepository;
    @BindView(R.id.saveTextView)
    TextView saveTextView;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.emailSubmitImageView)
    ImageView emailSubmitImageView;
    @BindView(R.id.deleteImageView)
    ImageView deleteImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.cancelTextView)
    TextView cancelTextView;
    /*@BindView(R.id.saveExitTextView)
     TextView saveExitTextView;*/
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.mailOfflineProgress)
    ProgressBar mailOfflineProgress;
    @BindView(R.id.saveTextViewRL)
    RelativeLayout saveTextViewRL;

    @BindView(R.id.bottomButtons)
    ConstraintLayout bottomButtons;
    @BindView(R.id.formMainLayout)
    ConstraintLayout formMainLayout;
    @Inject
    ProjectFormProvider mprojectFormProvider;
    boolean edit_permission;
    ViewTreeObserver observer;
    ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;
    String formComponentJson = "";
    AlertDialog alertDialog;
    private int formId;
    private long userFormId;
    private Dialog dialog;
    private boolean resetCallback = true;
    private Uri mCameraPhotoPath;
    private int projectId;
    private LoginResponse loginResponse;
    private boolean callSaveAndSend = false;
    private boolean callImageSelect = false;
    private File newCreatedFile;
    private String dueDate;
    private int schedule_form_id;
    private long mLastClickTime = 0;
    private String submittedData;
    private String fileKey;
    private AlertDialog.Builder builder;
    private String currentDeleteImages = "";
    private ArrayList<String> allDeletedImages = new ArrayList<>();
    private String deleteImageString;
    private int uploadCount;
    private int imageCount;
    private JSONObject initialFormJson;
    private String initialTempFormJson;
    private boolean isFormUpdated = false;
    private String formType;
    private boolean isAdded;
    private boolean manageDateChange;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    private String formSections;
    private String formDate;
    private String createdFormDate;
    private boolean formDateUpdated = false;
    private String formCreater;
    private UserForms firstUserForms;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            getExternalPermission()
    };
    private String formAreas;
    private int originalFormId;
    private int activeRevisionNumber;
    private String saveValidationBypassHtml = "  function remove_validation(components){\n" +
            "//console.log('zero',components);\n" +
            "$.each(components, function( index, value ) {\n" +
            "if(value.component.input == true){\n" +
            "//console.log('one',value.component);\n" +
            "//console.log('Before one validate',value.validators);\n" +
            "var validationcheck = value.validators;\n" +
            "var removeindex = validationcheck.indexOf('required');\n" +
            "if(validationcheck[removeindex]){\n" +
            "value.validators = validationcheck.splice(0,removeindex);\n" +
            "//console.log('After one validate',value.validators);\n" +
            "}\n" +
            "\n" +
            "}else if(value.components){\n" +
            "\n" +
            "$.each(value.components, function( index, value ) {\n" +
            "//console.log('three',value.component);\n" +
            "if(value.component.input == true){\n" +
            "//console.log('four',value.components);\n" +
            "if(value.components && value.components != 'undefined'){\n" +
            "remove_validation(value.components);\n" +
            "}else{\n" +
            "var validationcheck = value.validators;\n" +
            "var removeindex = validationcheck.indexOf('required');\n" +
            "if(validationcheck[removeindex]){\n" +
            "value.validators = validationcheck.splice(0,removeindex);\n" +
            "}\n" +
            "}\n" +
            "}else{\n" +
            "//console.log('five',value.components);\n" +
            "remove_validation(value.components);\n" +
            "}\n" +
            "});\n" +
            "}else{\n" +
            "remove_validation(value.components);\n" +
            "}\n" +
            "});\n" +
            "\n" +
            "return true;\n" +
            "};";
    private String jsValidatePublishFun = " function startAlertInJSForPublish(value){\n  " +
            "$(document).data('publish_btn_clicked', true);\n" +
            "        $('#pj_form_render button#submit').click();         }";
    private String handleDateSelect = "\n var handleDateSelect = function(dateText, object){\n" +
            "    console.log(dateText, object);\n" +
            "              android.onHtmlDateChange(dateText)\n" +
            "}\n";
    String formNameTitle;

    private UserForms tempUserForm;
    private UserForms userFormItem;
    Forms actualForm;
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            // navigation bar height
            int navigationBarHeight = 0;
            int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                navigationBarHeight = getResources().getDimensionPixelSize(resourceId);
            }

            // status bar height
            int statusBarHeight = 0;
            resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }

            // display window size for the app layout
            Rect rect = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

            // screen height - (user app height + status + nav) ..... if non-zero, then there is a soft keyboard
            int keyboardHeight = formMainLayout.getRootView().getHeight() - (statusBarHeight + navigationBarHeight + rect.height());

            if (keyboardHeight <= 0) {
                if (loginResponse.getUserDetails().getPermissions().get(0).getEditForm() == 1)
                    bottomButtons.setVisibility(View.VISIBLE);
                //Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show();            }
                bottomButtons.setVisibility(View.GONE);
            }
        }
    };
    private long formSubmitMobileId = -1;

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj,
                null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onBackPressed() {
//        webView.evaluateJavascript("runFocusOutContainer()", null);
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && imm.isActive()) {
                imm.hideSoftInputFromWindow(backImageView.getWindowToken(), 0);
            }
        } catch (Exception ignored) {
        }
        showChangesAlert();
    }

    @OnClick(R.id.leftImageView)
    public void onBackClick() {
//        webView.evaluateJavascript("runFocusOutContainer()", null);
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && imm.isActive()) {
                imm.hideSoftInputFromWindow(backImageView.getWindowToken(), 0);
            }
        } catch (Exception ignored) {
        }
        showChangesAlert();
    }

    private void showChangesAlert() {
        if (saveTextView.getText().toString().equalsIgnoreCase("EMAIL FORM")) {
            super.onBackPressed();
        } else if (isFormUpdated) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setMessage(getString(R.string.save_form_changes));

            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.save), (dialog, which) -> {

                callSaveAndSend = false;
                webView.evaluateJavascript("startAlertInJS('Pronovos')", null);

            });

            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Discard", (dialog, which) -> {
                dialog.dismiss();
                Log.e("DISCARD", "showChangesAlert: initialFormJson " + initialFormJson + "   *** firstUserForms " + firstUserForms);
                Log.e("OPEN_FORM", "showChangesAlert: initialFormJson " + createdFormDate + "   *** firstUserForms " + userFormId);
                if (initialFormJson != null && firstUserForms != null) {
                    updateTempData(initialFormJson);
                    mprojectFormRepository.saveUserFormData(userFormId, loginResponse.getUserDetails().getUsers_id(), initialTempFormJson, createdFormDate);
                } else {
                    UserForms userForms = mprojectFormRepository.getUserFormSubmitted(userFormId, loginResponse.getUserDetails().getUsers_id());
                    if (userForms != null && userForms.getFormSubmitId() == 0) {
                        mprojectFormRepository.removeUserForm(userForms, loginResponse.getUserDetails().getUsers_id());
                    }
                }
                if (isAdded && tempUserForm != null) {
                    // handle delete temp saved userform due to html changes
                    mprojectFormRepository.deleteTempUserForm(loginResponse.getUserDetails().getUsers_id(), tempUserForm.getId());
                    Log.d("OPEN_FORM", "showChangesAlert: DISCARD  delete " + tempUserForm);
                }
                super.onBackPressed();
            });
            alertDialog.setCancelable(false);
            alertDialog.show();
            Button cancelButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            cancelButton.setTextColor(ContextCompat.getColor(this, R.color.gray_948d8d));
            Button okButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            okButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected int doGetContentView() {
        return R.layout.form_detail_main;
    }

    /*@OnClick(R.id.saveExitTextView)
    public void onSaveEixtClick(){
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        webView.evaluateJavascript("startAlertInJS('Pronovos')", null);
    }*/
    @OnClick(R.id.cancelTextView)
    public void onCancelClick() {

        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        callSaveAndSend = false;
        if (cancelTextView.getText().toString().equalsIgnoreCase("Return")) {
            ProjectFormDetailActivity.this.finish();
        } else
            webView.evaluateJavascript("startAlertInJS('Pronovos')", null);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
            deleteImageView.setVisibility(View.GONE);
            if (saveTextView.getText().toString().equalsIgnoreCase("EMAIL FORM")) {
                saveTextView.setEnabled(false);
                saveTextViewRL.setEnabled(false);
            }


        } else {
            saveTextView.setEnabled(true);
            saveTextViewRL.setEnabled(true);

            if (saveTextView.getText().toString().equalsIgnoreCase("EMAIL FORM") && firstUserForms == null) {
                saveTextView.setEnabled(false);
                saveTextViewRL.setEnabled(false);
                mailOfflineProgress.setVisibility(View.VISIBLE);

            } else if ((saveTextView.getText().toString().equalsIgnoreCase("EMAIL FORM") && firstUserForms != null)) {
                mailOfflineProgress.setVisibility(View.GONE);
                saveTextView.setEnabled(true);
                saveTextViewRL.setEnabled(true);
            }

            offlineTextView.setVisibility(View.GONE);
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            if (loginResponse.getUserDetails().getPermissions().get(0).getEditForm() == 1 && userFormId != -1) {
                UserForms userForms = mprojectFormRepository.getUserFormSubmitted(userFormId, loginResponse.getUserDetails().getUsers_id());
                if (userForms != null && userForms.getTempSubmittedData() != null) {
                    deleteImageView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void loadViewTreeObserver() {
        if (formMainLayout != null) {
            observer = formMainLayout.getViewTreeObserver();

            if (observer.isAlive()) {
                observer.dispatchOnGlobalLayout(); // In case a previous call is waiting when this call is made
                formMainLayout.getViewTreeObserver().addOnGlobalLayoutListener(getGlpobleLayoutListner());
            }
        }
    }

    private ArrayList<String> getUploadFileKeysList(String formComponent) {
        ArrayList<String> keyList = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(formComponent);
            Iterator iterator = jsonObj.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                Log.d("OPEN_FORM", "ONCREATE getUploadFileKeysList: key " + key);
                if (key.equalsIgnoreCase("components")) {
                    JSONArray componentsJsonArray = jsonObj.getJSONArray(key);
                    for (int i = 0; i < componentsJsonArray.length(); i++) {
                        JSONObject componentsObject = componentsJsonArray.getJSONObject(i);
                        if (componentsObject.has("type")) {
                            if (componentsObject.get("type").toString().equals("file")) {
                                if (componentsObject.has("key") && componentsObject.get("key") != null) {
                                    keyList.add(componentsObject.get("key").toString());
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("OPEN_FORM", "ONCREATE getUploadFileKeysList: keyList = " + keyList);
        return keyList;
    }

    private String formAreaDataJson;
    private String defaultValues;
    private String formAreaOptionSelected;

    private void downLoadAllAttachments(String key, JSONObject jsonObj, File myDir) throws Exception {
        JSONArray fileJsonArray = jsonObj.getJSONArray(key);
        Log.e("OPEN_FORM", key + "  downLoadAllAttachments  ******* FORM_COMPONENT_KEY  ===     " + fileJsonArray);
        for (int i = 0; i < fileJsonArray.length(); i++) {
            JSONObject fileObject = fileJsonArray.getJSONObject(i);
            URI uri = null;
            try {
                Log.e("OPEN_FORM", key + "  downLoadAllAttachments  *#########* FORM_COMPONENT_KEY  ===     " + fileObject);
                uri = new URI(fileObject.get("url").toString());
                String[] segments = uri.getPath().split("/");
                String imageName = segments[segments.length - 1];
                String filePath = myDir.getAbsolutePath();
                String[] params = new String[]{fileObject.get("url").toString(), filePath};
//                Object[] params = new Object[]{fileObject.get("url").toString(), filePath,getContext(),null};
                File imgFile = new File(filePath + "/" + imageName);
                if (!imgFile.exists()) {
                    try {
                        new LoadImageInBackground(new LoadImageInBackground.Listener() {
                            @Override
                            public void onImageDownloaded(Bitmap bitmap) {
                                Log.d("ONCREATE", "onImageDownloaded: " + params);
                            }

                            @Override
                            public void onImageDownloadError() {
                            }
                        }).executeOnExecutor(THREAD_POOL_EXECUTOR, params);
                    } catch (RejectedExecutionException e) {
                        e.printStackTrace();
                    }
                } else {
                    submittedData = submittedData.replaceAll(fileObject.get("url").toString(), myDir.getAbsolutePath() + "/" + imageName);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //   formMainLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);
    }

    /*private ViewTreeObserver.OnGlobalLayoutListener getGlpobleLayoutListner() {
        globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("NewApi")
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                // navigation bar height
                int navigationBarHeight = 0;
                int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    navigationBarHeight = getResources().getDimensionPixelSize(resourceId);
                }

                // status bar height
                int statusBarHeight = 0;
                resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    statusBarHeight = getResources().getDimensionPixelSize(resourceId);
                }

                // display window size for the app layout
                Rect rect = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                // screen height - (user app height + status + nav) ..... if non-zero, then there is a soft keyboard
                int keyboardHeight = formMainLayout.getRootView().getHeight() - (statusBarHeight + navigationBarHeight + rect.height());
                if (keyboardHeight <= 0) {
                    bottomButtons.setVisibility(View.VISIBLE);
                    //Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show();            }
                    bottomButtons.setVisibility(View.GONE);
                }
                // Once data has been obtained, this listener is no longer needed, so remove it...
                       *//* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            Log.d("FORMACTIVITY", "onGlobalLayout: removing listner ");
                            formMainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            Log.d("FORMACTIVITY", "onGlobalLayout: removing listner 11111111111111111");
                            formMainLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }*//*
            }

        };
        return globalLayoutListener;
    }*/


    private ViewTreeObserver.OnGlobalLayoutListener getGlpobleLayoutListner() {
        globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("NewApi")
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
// navigation bar height
                int navigationBarHeight = 0;
                int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    navigationBarHeight = getResources().getDimensionPixelSize(resourceId);
                }

// status bar height
                int statusBarHeight = 0;
                resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    statusBarHeight = getResources().getDimensionPixelSize(resourceId);
                }

// display window size for the app layout
                Rect rect = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
// screen height - (user app height + status + nav) ..... if non-zero, then there is a soft keyboard
                int keyboardHeight = formMainLayout.getRootView().getHeight() - (statusBarHeight + navigationBarHeight + rect.height() + 1);
                Log.d("Manya", "onGlobalLayout Keyboard height: " + keyboardHeight + " Ppermission: " + loginResponse.getUserDetails().getPermissions().get(0).getEditForm());
                if (keyboardHeight <= 0 && loginResponse.getUserDetails().getPermissions().get(0).getEditForm() == 1) {

                    bottomButtons.setVisibility(View.VISIBLE);
                    Log.d("Manya", "onGlobalLayout button visible: ");
//                    openCloseKeyboardMessageDialog("Keyboard closed keyboardHeight = "+keyboardHeight);
//Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show();
                } else {
//Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show(); }
                    bottomButtons.setVisibility(View.GONE);
                    Log.d("Manya", "onGlobalLayout button Gone: ");
//                    openCloseKeyboardMessageDialog("Keyboard open keyboardHeight = "+keyboardHeight);
                }
// Once data has been obtained, this listener is no longer needed, so remove it...
/* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
Log.d("FORMACTIVITY", "onGlobalLayout: removing listner ");
formMainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
} else {
Log.d("FORMACTIVITY", "onGlobalLayout: removing listner 11111111111111111");
formMainLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
}*/
            }

        };
        return globalLayoutListener;
    }

    public void openCloseKeyboardMessageDialog(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
// Toast.makeText(ProjectFormDetailActivity.this,"You clicked yes button", Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();
                    }
                });
        if (alertDialog == null)
            alertDialog = alertDialogBuilder.create();
        alertDialog.setMessage(message);
        alertDialog.show();

    }

    @Override
    public void onResume() {
        super.onResume();
        callSaveAndSend = false;
        //formMainLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);
//        loadViewTreeObserver();

    }

    @Override
    public void onPause() {
        super.onPause();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Log.d("FORMACTIVITY", "onGlobalLayout: removing listner ");
            if (globalLayoutListener != null)
                formMainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
        } else {
            Log.d("FORMACTIVITY", "onGlobalLayout: removing listner 11111111111111111");
            if (globalLayoutListener != null)
                formMainLayout.getViewTreeObserver().removeGlobalOnLayoutListener(globalLayoutListener);
        }
    }

    private String handleProjectFormAreaChange = "\n  var handleAreaChange = function(e){ \n" +
            "    var changed = $(this).val();\n" +
            "    android.onHtmlFormAreaChange(changed)\n" +
            "  }\n  ";
    private Integer projectFormAreasID;

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = checkSelfPermission(activity, getExternalPermission());

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        originalFormId = getIntent().getIntExtra(Constants.INTENT_KEY_ORIGINAL_FORM_ID, 0);
        activeRevisionNumber = getIntent().getIntExtra(Constants.INTENT_KEY_FORM_ACTIVE_REVISION_NUMBER, 0);
        projectId = getIntent().getIntExtra("project_id", 0);
        formId = getIntent().getIntExtra(Constants.INTENT_KEY_FORM_ID, 0);
        formType = getIntent().getStringExtra("form_type");
        formSections = getIntent().getStringExtra(Constants.INTENT_KEY_FORM_SECTIONS);
        userFormId = getIntent().getLongExtra("user_form_id", -1);

        Log.e("OPENFORM", "DetailForm  onCreate: userFormId  " + userFormId + " *** formId  " + formId + "  originalFormId  " + originalFormId + " activeRevisionNumber  " + activeRevisionNumber);
        edit_permission = loginResponse.getUserDetails().getPermissions().get(0).getEditForm() == 1 ? false : true;

        firstUserForms = mprojectFormRepository.getUserFormDetails(userFormId, loginResponse.getUserDetails().getUsers_id());
        ProjectFormArea projectFormArea = mprojectFormRepository.getProjectFormArea(Long.valueOf(projectId));
        if (projectFormArea != null)
            formAreas = projectFormArea.getProjectFormAreas();
        else
            formAreas = "";

        if (!TextUtils.isEmpty(formAreas)) {
            formAreaDataJson = "dataJson:" + formAreas + " , ";
        } else {
            formAreaDataJson = "";
        }
        if (firstUserForms != null && firstUserForms.getPjAreasId() != null && firstUserForms.getPjAreasId() != 0) {
            formAreaOptionSelected = "\n optionSelected: " + firstUserForms.getPjAreasId() + " , \n ";
            projectFormAreasID = firstUserForms.getPjAreasId();
        } else {
            formAreaOptionSelected = "";
        }
        // Log.d("FORMDETAIL", "onCreate: formAreaDataJson " + formAreaDataJson + " \n\n formAreaOptionSelected " + formAreaOptionSelected);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{getExternalPermission()}, FILESTORAGE_REQUEST_CODE);
        }
        //Log.e("*Activity ", "onCreate: userFormId " + userFormId+" formSections "+formSections);

        isAdded = getIntent().getBooleanExtra("isAdded", false);
        formComponentJson = mprojectFormRepository.getFormComponent(formId);
        actualForm = mprojectFormRepository.getActualForm(originalFormId, activeRevisionNumber);
        Log.d("OPENFORM", "ONCREATE   actual form : " + actualForm);
        ArrayList<String> keyList = getUploadFileKeysList(formComponentJson);
        manageDateChange = getIntent().getBooleanExtra("manage_date_change", false);
        schedule_form_id = getIntent().getIntExtra("schedule_form_id", 0);
        emailSubmitImageView.setVisibility(View.GONE);
        if (isAdded) {
            deleteImageView.setVisibility(View.GONE);
            createdFormDate = sdf.format(new Date());
            formDate = sdf.format(new Date());
            formCreater = loginResponse.getUserDetails().getFirstname() + " " + loginResponse.getUserDetails().getLastname();
            defaultValues = actualForm.getDefaultValues();
        } else {
            deleteImageView.setVisibility(View.VISIBLE);
            formDate = getIntent().getStringExtra(Constants.INTENT_KEY_FORM_CREATED_DATE);
            createdFormDate = getIntent().getStringExtra(Constants.INTENT_KEY_FORM_CREATED_DATE);
            formCreater = getIntent().getStringExtra(Constants.INTENT_KEY_FORM_CREATED_BY);
        }
        if (TextUtils.isEmpty(formDate)) {
            formDate = sdf.format(new Date());
            createdFormDate = sdf.format(new Date());
        }
        if (TextUtils.isEmpty(formCreater)) {
            formCreater = loginResponse.getUserDetails().getFirstname() + " " + loginResponse.getUserDetails().getLastname();
        }
        dueDate = getIntent().getStringExtra("due_date");
        cancelTextView.setText("Save & Exit");
        saveTextView.setText("Publish");
        if (firstUserForms != null && firstUserForms.getPublish() == 1) {
            edit_permission = true;
            cancelTextView.setText("Return");
            saveTextView.setText("EMAIL FORM");
            if (!NetworkService.isNetworkAvailable(getApplicationContext())) {
                if (saveTextView.getText().toString().equalsIgnoreCase("EMAIL FORM")) {
                    saveTextView.setEnabled(false);
                    saveTextViewRL.setEnabled(false);

                }
            }
        } else {
            // saveExitTextView.setVisibility(View.VISIBLE);
            //  cancelTextView.setVisibility(View.GONE);
            cancelTextView.setText("Save & Exit");
            saveTextView.setText("COMPLETE");
        }

        FormsName formsName = mprojectFormRepository.getUserFormsName(originalFormId, activeRevisionNumber, projectId);
        if (formsName != null) {
            formNameTitle = formsName.formName;
        } else {
            formNameTitle = mprojectFormRepository.getFormName(formId);
        }
        titleTextView.setText(formNameTitle);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.GONE);

        File myDir = new File(ProjectFormDetailActivity.this.getFilesDir().getAbsolutePath() + "/Pronovos/Form/");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        submittedData = null;
        Log.d("OPEN_FORM", "******************* onCreate: TAG  userFormId " + userFormId);
        currentDeleteImages = userFormId == -1 ? "" : mprojectFormRepository.getUserFormDeleteImages(userFormId, loginResponse.getUserDetails().getUsers_id());
        if (!TextUtils.isEmpty(currentDeleteImages)) {
            String newCurrentDeleteImages = currentDeleteImages.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "");
            if (!TextUtils.isEmpty(newCurrentDeleteImages)) {
                allDeletedImages = new ArrayList<String>(Arrays.asList(newCurrentDeleteImages.split(","))); //"/PronovosPronovos"
            }
        }
        try {
            JSONObject jsonObj = new JSONObject(userFormId == -1 ? "" : mprojectFormRepository.getUserFormSubmittedData(userFormId, loginResponse.getUserDetails().getUsers_id()));
            initialFormJson = jsonObj;
            initialTempFormJson = firstUserForms.getTempSubmittedData();
            if (userFormId != -1) {
                submittedData = mprojectFormRepository.getUserFormSubmittedData(userFormId, loginResponse.getUserDetails().getUsers_id());
            }
            // Log.d("ONCREATE", "FORM_COMPONENT_KEY  initialFormJson  ===     " + initialFormJson);
            Iterator iterator = jsonObj.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
               /* if (key.contains("upload"))
                    Log.d("ONCREATE", "   " + jsonObj.getJSONArray(key));*/
                if (key.contains("file-")) {
                    downLoadAllAttachments(key, jsonObj, myDir);
                }
            }
            for (String uploadKey : keyList) {
                downLoadAllAttachments(uploadKey, jsonObj, myDir);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isAdded && !TextUtils.isEmpty(defaultValues)) {
            callLoadFormAPI(formComponentJson, defaultValues);
        } else {
            callLoadFormAPI(formComponentJson, submittedData);
        }
        if (loginResponse.getUserDetails().getPermissions().get(0).getEditForm() == 1) {
            deleteImageView.setVisibility(View.VISIBLE);
            deleteImageView.setClickable(true);
            bottomButtons.setVisibility(View.VISIBLE);
        } else {
            bottomButtons.setVisibility(View.GONE);
            deleteImageView.setClickable(false);
            deleteImageView.setVisibility(View.INVISIBLE);
        }
        verifyStoragePermissions(ProjectFormDetailActivity.this);
        EventBus.getDefault().register(this);

        showRibbonLayout(userFormId, projectId, loginResponse.getUserDetails().getUsers_id());
    }

    private void callLoadFormAPI(String formComponent, String data) {
        if (projectFormAreasID != null && projectFormAreasID != 0) {
            formAreaOptionSelected = "\n optionSelected: " + projectFormAreasID + " , \n ";
        }
//        data = data == null ? "{}" : data;
        formSections = mprojectFormRepository.getFormSection(formId);
        if (TextUtils.isEmpty(formSections)) {
            formSections = "{}";
        }
        PjProjects projects = projectsProvider.getProjectDetail(projectId, loginResponse.getUserDetails().getUsers_id());
        String projectName = projects.getName().replaceAll("'", "\\\\'");

        String formHeader = "var form_header = FormioUtils.FormHeaderFields;\n" +

                "var project_data = " + formSections + ";\n" +
                "\n" +
                "if(project_data && project_data.pj_name)\n" +
                "{\n" +
                "project_data.pj_name =  '" + projectName + "';\n" +
                "}\n" +
                "\n" +
                "if(project_data && project_data.pj_number)\n" +
                "{\n" +
                "project_data.pj_number = '" + projects.getProjectNumber() + "';\n" +
                "}\n" +
                "\n" +
                "    form_header.initialize(JSON.stringify(project_data) ,  \n" +
                "                           {" + formAreaDataJson +
                formAreaOptionSelected +
                "  date:\"" + formDate +
                "\", inspector_name:\"" +
                formCreater +
                "\"},\n" +
                "   \"#pj_form_header_field_cont\");";
        //Log.d("*Activity", "callLoadFormAPI: formSections "+formSections);
        File myDir = new File(ProjectFormDetailActivity.this.getFilesDir().getAbsolutePath() + "/Pronovos/");//"/PronovosPronovos"
        List<FormAssets> formAssets = mprojectFormRepository.getFormAssets();
        String styleFiles = "";
        String jsFiles = "";
        for (int i = 0; i < formAssets.size(); i++) {
            if (formAssets.get(i).getFileType().equalsIgnoreCase("css")) {
                styleFiles = styleFiles + "<link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\"" + myDir.getAbsolutePath() + "/" + formAssets.get(i).getFileName() + "\">\n";
            }
            if (formAssets.get(i).getFileType().equalsIgnoreCase("js")) {
                jsFiles = jsFiles + "<script src=\"" + myDir.getAbsolutePath() + "/" + formAssets.get(i).getFileName() + "\"></script>\n";

            }
        }
        //  jsFiles += "<script src=\"https://ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js\"></script>";
        Log.d("FORMDETAIL", "callLoadFormAPI: styleFiles = \n " + styleFiles + " \n jsFiles " + jsFiles);

        String baseUrl = "http://poc.pronovos.com";
        if (BuildConfig.FLAVOR.equals("production")) {
            baseUrl = "https://app.pronovos.com";
        }
        String string = "<!DOCTYPE html>\n" + "<html>\n" +

                "<body>\n" + "\n" + "<head>\n" +
                //                "<link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\"http://dev.smartsubz.com/assets/newtheme/css/bootstrap.min.css\">\n" +
                styleFiles +
//                "        <link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\"https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css\">\n"
                "\n" + "<style>"
                + "#pj_form_render .alert-success{\n" + "display:none;\n" + "}" + "#pj_form_render button#submit, #pj_form_render input[type=submit]{\n" + "        display:none;\n" + "    }" + "" + "</style>" +


                "</head>\n" +
                "<div id=\"pj_form_header_field_cont\"></div>"
                + "\n" + " <div id=\"pj_form_render\"></div>\n" + "\n" + jsFiles + "\n" + "\n"

                + "<script>\n" + "var render_form = document.getElementById('pj_form_render');\n"
                + "var base_url = '" + baseUrl + "';\n"
                + "var user_data = " + data + ";\n"
                + " var $enableFormArea = true;\n";


        if (data != null) {
            string = string + "user_data.fillform = true;\n";
        }
        string = string + "var webform_instance = null;\n" + "var raw_form = " + formComponent + "\n" +
                "var handleRemoveInspectionImage = function(component, event, fileInfo){\n" +
                "\tconsole.log(component, event, fileInfo.url);\n" +
                "android.fileRemoved(JSON.stringify(fileInfo),JSON.stringify(component.currentForm.data));" +
                "};" +
                "\nfunction triggerFocusOut()\n" +
                "{\n" +
                "" +
                "    var element = $(document.activeElement);\n" +
                "    ($(element).is('input[type=\"text\"]')) ? $(element).trigger('blur') : null;\n" +
                "}" +

                "\n" +

                saveValidationBypassHtml
                +

                "        $(document).ready(function(event){\n"

                + formHeader
                + "if( " + edit_permission + ") { \n "
                + " $( '.datepicker' ).datepicker( 'option', 'disabled', true );  "
                + " $('#form_area_data_field').attr('disabled', true); "
                + " }" +

                "\n" +
                "            var handleFileInputEvents = function(event){\n" +
                "                var form = (event && event.data) ? event.data.form : null;\n" +
                "\n" +
                "                if(!form)\n" +
                "                    return true;\n" +
                "\n" +
                "                var target = event.target;\n" +
                "                var file_comp_cont = $(target).closest('.formio-component');\n" +
                "                var inspection_comp_cont = (file_comp_cont) ? $(file_comp_cont).parent('.formio-component') : null;\n" +
                "                var inspection_comp_id = (inspection_comp_cont) ? $(inspection_comp_cont).attr('id') : null;\n" +
                "                var file_comp_id = (file_comp_cont) ? $(file_comp_cont).attr('id') : null;\n" +
                "\t\n" +
                "\t/*\n" +
                "\tvar file_comp_cl_pat = (file_comp_classes) ? file_comp_classes.match(/formio-component-file-([^\\s]+)/) : null;\n" +
                "\tvar file_comp_parent = (file_comp_cl_pat && file_comp_cl_pat.length) ? file_comp_cl_pat[1] : null;\n" +
                "\t*/\n" +
                "\n" +
                "                var file_component = (file_comp_id) ? form.getComponentById(file_comp_id) : null;\n" +
                "                var inspection_comp = (inspection_comp_id) ? form.getComponentById(inspection_comp_id) : null;\n" +
                "\n" +
                "                var file_key = (file_component) ? file_component.key : null;\n" +
                "                var ins_key = (inspection_comp) ? inspection_comp.key : null;\n" +
                "android.handleFileInputEvent(file_key,JSON.stringify(form.submission))\n" + "\n" +
                "                console.log(file_key, ins_key);\n" +
                "\n" +
                "                console.log(form.submission);\n" +
                "\n" +
                "                return true;\n" +
                "            };\n" +
                "\n" +
                "            var handleTextFieldFocusOut = function(event){\n" +
                "                var form  = event.data.form;\n" +
                "                var submission = event.data.submission;\n" +
                "                console.log(event, 'Text field change', form, submission.data);\n" +
//                " console.log('Text field change  delete', $(document).data('deleted_images'))"+
                "android.onChange(JSON.stringify(form.submission))" +

                "            };\n" +
                "\n" +
                "            var handleComponentFieldsChange = function(form, submission){\n" +
                "                console.log(form, submission, 'Comp change');\n" +
                "android.onChange(JSON.stringify(form.submission))" +

                "            };\n" +
                "\n" +
                "            var checkForTextFields = function(submission){\n" +
                "                return (\n" +
                "                        (\n" +
                "                                (\n" +
                "                                        (submission.changed.instance.inputs.length == 1 ) &&\n" +
                "                                                ($(submission.changed.instance.inputs).attr('type') == 'text')\n" +
                "                                )\n" +
                "                                        ||\n" +
                "                                        (\n" +
                "                                                (submission.changed.instance.inputs.length > 1 ) &&\n" +
                "                                                        ($(submission.changed.instance.inputs).attr('type') == 'text') &&\n" +
                "                                                        (submission.changed.flags.changed_input == 'inspection_comments')\n" +
                "                                        )\n" +
                "                        )\n" +
                "                );\n" +
                "            }\n" +
                "\n" +
                "            var androidHandleFormChange = function(form, submission){\n" +
                "            console.log('aaaa');\n" +

                "                if(submission && typeof submission.changed === 'undefined')\n" +
                "                return true;\n" +
                "\n" +
                "            console.log('aaaabbbb');\n" +

                "                var inputs = submission.changed.instance.inputs;\n" +
                "            console.log(inputs);\n" +
                " var isSelect = $(submission.changed.instance.element).hasClass('formio-component-select');\n" +
                "\n" + "            \n" +

                "        if(isSelect)\n" +
                "        {\n" +
                "            return handleComponentFieldsChange(form, submission);\n" +
                "        }" +

                "                var isText = (checkForTextFields(submission)) ? true : false;\n" +
                "            console.log(isText);\n" +
                "                return (isText) ? $(inputs).off('focusout').on('focusout', {form, submission}, handleTextFieldFocusOut) : handleComponentFieldsChange(form, submission);\n" +
                "            };\n" +
                "\n" +
                "            console.log('aaa111');\n" +

                "            var registerFileInputEvents  = function(form){\n" +
                "                $('input[type=\"file\"]').click({form: form}, handleFileInputEvents);\n" +
                "            console.log('aaa11122');\n" +
                "            };\n" +

                "var handleFileInspectionClick = function(event){\n" +
                "            event.preventDefault();\n" +
                "            var url = $(this).attr('href');\n" +
                "            console.log(url);\n" +
                "android.clickImageUrl(url)" +
                "}\n" +
                "\n" +
                "var registerFileInspectionClick = function(){\n" +
                "    $(document).on('click', '.file-inspection-image', handleFileInspectionClick);\n" +
                "}" +
                "\n" + "$(document).data('deleted_images', []);\n" +
                "\n" +


                "            Formio.icons = 'fontawesome';\n" +
                "            Formio.createForm(render_form, raw_form, {readOnly: " + edit_permission + "})\n" +
                "\t\t\t\t.then(function(form) {\n" +
                "                form.submission={data:user_data}\n" +
                "                form.on('submit', function(submission) {\n" +
                "                    console.log(submission);\n" +
                "            console.log(JSON.stringify(submission.data));\n" +
                "            console.log(JSON.stringify(submission));\n" +

                "                    android.onData(JSON.stringify(submission.data))\n" +
                "                });\n" +
                "                form.on('change', function(submission) {\n" +
                "            console.log('aaaachange');\n" +
                "            console.log(submission.changed);\n" +
                "            console.log(JSON.stringify(submission.data));\n" +
                "            console.log(JSON.stringify(submission.data.fillform));\n" +

                "                  if(!submission.changed)\n" +
                "    {\n" +
                "        return true;\n" +
                "    }\n" +
                "    else if(submission.changed && submission.data.fillform)\n" +
                "    {\n" +
                "        delete(submission.data.fillform);\n" +
                "        return true;\n" +
                "    }  androidHandleFormChange(form, submission);\n" +
                "                });\n" +
                "                webform_instance = form;\n" +
                "                /* Register Events :start */\n" +
                "                registerFileInputEvents(form);\n" +
                "                /* Register Events :end*/\n" +
                " /* Register file inspection click event. */\n" +
                "                    registerFileInspectionClick();\n" +
                "                    /* Register file inspection click event. */\n" +
                "                return form;\n" +
                "            });\n" +
                "        });\n" +

                "function runFocusOutContainer()\n" +
                "{\n" +
                "    $('input[type=\"text\"]').trigger('focusout');\n" +
                "}" +
                "        function startAlertInJS(value){\n  " +
                "     remove_validation(webform_instance.components); \n " +
                "                \n" +
                "        $('#pj_form_render button#submit').click();         }    " +
                jsValidatePublishFun + handleDateSelect +
                handleProjectFormAreaChange +
                "   </script>\n" +
                "\n" +
                "\n" +
                "\n" +
                "</body></html>";

        webView = findViewById(R.id.opencontent);
        Log.d("FORMDETAIL", "callLoadFormAPI: ");
       /* File dir = new File (Environment.getExternalStorageDirectory().getAbsolutePath()+"/PronovosCM");
        dir.mkdirs();
        writeStringAsFile(string, Environment.getExternalStorageDirectory().getAbsolutePath()+"/PronovosCM/pronovos_form1111_.html");
*/
        webView.loadData(string, "text/html", "utf-8");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        webView.setWebChromeClient(new MyWebChromeClient() {
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                return true;
            }
        });
        webView.setWebViewClient(new WebClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadViewTreeObserver();
            }

            @Override
            public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
                super.onUnhandledKeyEvent(view, event);
                Log.i("Text input ", "onUnhandledKeyEvent: test " + event);
                if (event.getAction() == KeyEvent.ACTION_UP)
                    switch (event.getKeyCode()) {
                        case KeyEvent.KEYCODE_ENTER:
                            Log.i("Text input ", "onUnhandledKeyEvent: test enter " + event);
                            // e.g. get space and enter events here
                            webView.evaluateJavascript("triggerFocusOut()", null);

                            break;
                    }

            }
        });
        webView.addJavascriptInterface(this, "android");
        webView.loadDataWithBaseURL("file:///android_asset/", string, "text/html", "utf-8", null);

    }

    public void writeFileOnInternalStorage(Context mcoContext, String sFileName, String sBody) {
        File dir = new File(mcoContext.getFilesDir(), "");
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            File gpxfile = new File(dir, sFileName);
            Log.e("TAG", "writeFileOnInternalStorage: " + gpxfile.getAbsolutePath());
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.deleteImageView)
    public void onDeleteClick() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && imm.isActive()) {
                imm.hideSoftInputFromWindow(cancelTextView.getWindowToken(), 0);
            }
        } catch (Exception ignored) {
        }
        UserForms userForms = mprojectFormRepository.getUserFormSubmitted(userFormId, loginResponse.getUserDetails().getUsers_id());
        if (userForms != null /*&& !TextUtils.isEmpty(userForms.getTempSubmittedData())*/) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setMessage(getString(R.string.delete_form));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.yes), (dialog, which) -> {

                if (userForms.getFormSubmitId() == 0) {
                    mprojectFormRepository.removeUserForm(userForms, loginResponse.getUserDetails().getUsers_id());
                    super.onBackPressed();
                } else {

                    callDeleteFormAPI(userForms.getFormSubmitId(), userForms.getFormSubmitMobileId(), loginResponse.getUserDetails().getUsers_id());
                }
                //  super.onBackPressed();
            });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.no), (dialog, which) -> {
                dialog.dismiss();
            });
            alertDialog.setCancelable(false);
            alertDialog.show();
            Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(ContextCompat.getColor(this, R.color.gray_948d8d));
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

        } else {
            super.onBackPressed();
        }
    }

    /**
     * Call the Delete Form API.
     *
     * @param formSubmittedId The submitted form id.
     * @param users_id        The user id.
     */
    private void callDeleteFormAPI(long formSubmittedId, long formSubmitMobilID, int users_id) {
        Log.d("OPEN_FORM", "callDeleteFormAPI: formSubmittedId " + formSubmittedId + " userFormId " + userFormId);

        mprojectFormProvider.deleteForm(formSubmittedId, new ProviderResult<DeleteUserFormResponse>() {
            @Override
            public void success(DeleteUserFormResponse deleteUserFormResponse) {
                //   Log.d("Activity", "success: call mprojectFormRepository.deleteForm( formSubmittedId = "+formSubmittedId+"  formSubmitMobilID  "+formSubmitMobilID);
                mprojectFormRepository.deleteForm1(formSubmittedId, users_id);
                //onBackPressed();
                finish();
            }


            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(ProjectFormDetailActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(ProjectFormDetailActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(ProjectFormDetailActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {

            }
        });
    }

    @OnClick(R.id.saveTextView)
    public void onSaveClick() {
        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        callSaveAndSend = true;
       /* webView.evaluateJavascript(
                "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String html) {
                        Log.i("Temp", "onReceiveValue: "+html); }
                });*/
        //   webView.evaluateJavascript("startNoAlertInJS('Pronovos')", null);
        if (saveTextView.getText().toString().equalsIgnoreCase("COMPLETE")) {
            webView.evaluateJavascript("startAlertInJSForPublish('Pronovos')", null);
        } else if (saveTextView.getText().toString().equalsIgnoreCase("EMAIL FORM")) {
            Log.d("OPEN_FORM", "onSaveClick: ");
            String submitValue = null;
            if (firstUserForms != null) {
                submitValue = firstUserForms.getSubmittedData();
            } else {
             /*   UserForms userForms = mprojectFormRepository.getUserForm(projectId, submission.getUserFormMobileId(),
                        loginResponse.getUserDetails().getUsers_id());*/

            }
            navigateToEmailScreen(submitValue);
        }
    }

    public void writeStringAsFile(final String fileContents, String fileName) {
        Log.d("FORMDETAIL", "writeStringAsFile: fileName " + fileName);
        try {

            FileWriter out = new FileWriter(new File(fileName));
            out.write(fileContents);
            out.close();
            Log.e("USER_FORM_ACTIVITY", "writeStringAsFile: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("USER_FORM_ACTIVITY", "  " + e.getLocalizedMessage());
        }
    }

    @JavascriptInterface
    public void handleFileInputEvent(String key, String value) {
        submittedData = value;
        fileKey = key;
        Log.i("Android", key + " Android call onHandleImage: " + value);


        boolean edit_permission = loginResponse.getUserDetails().getPermissions().get(0).getEditForm() == 1;
        if (edit_permission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    checkSelfPermission(getExternalPermission()) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, getExternalPermission()},
                        FILESTORAGE_REQUEST_CODE);
            } else
                showChooserDialog();
        }
    }

    @JavascriptInterface
    public void clickImageUrl(String url) {
        Log.i("OPEN_FORM", "clickImageUrl: " + url);
        try {
            FormsName formsName = mprojectFormRepository.getUserFormsName(originalFormId, activeRevisionNumber, projectId);

            if (formsName != null) {
                formNameTitle = formsName.formName;
            } else {
                formNameTitle = mprojectFormRepository.getFormName(formId);
            }
            URI uri = new URI(url.toString());
            String[] segments = uri.getPath().split("/");
            String imageName = segments[segments.length - 1];
            String[] exts = imageName.split("[.]");
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            String fileExt = exts[exts.length - 1];
            if (fileExt.equals("pdf") || fileExt.equals("xls") || fileExt.equals("xlsm") ||
                    fileExt.equals("doc") || fileExt.equals("docx") || fileExt.equals("xlsx")) {
                if (fileExt.equals("xls") || fileExt.equals("xlsm") || fileExt.equals("xlsx")) {
                    isXLSFile = true;
                } else {
                    isXLSFile = false;
                }
                FormAttachmentWebviewDialog webviewDialog = new FormAttachmentWebviewDialog();
                Bundle bundle = new Bundle();
                bundle.putString("attachment_path", url);
                bundle.putBoolean("isXLS", isXLSFile);
                bundle.putString("title_text", formNameTitle);
                webviewDialog.setArguments(bundle);
                webviewDialog.show(ft, "");
            } else {
                FormAttachmentDialog attachmentDialog = new FormAttachmentDialog();
                Bundle bundle = new Bundle();
                bundle.putString("attachment_path", url);
                bundle.putString("title_text", formNameTitle);
                attachmentDialog.setArguments(bundle);
                attachmentDialog.show(ft, "");
            }

       /* String filePath = url.toString().getFilesDir().getAbsolutePath() + "/Pronovos/Form/";
        String[] params = new String[]{url.toString(), filePath};*/
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }

    private void navigateToEmailScreen(String submitValue) {
        Log.d("OPEN_FORM", schedule_form_id + "  schedule_form_id  navigateToEmailScreen: submitValue " + submitValue + "   userFormId " + userFormId + " firstUserForms.getScheduleFormId()  " + firstUserForms.getScheduleFormId());
        Long user_form_id = null;
        if (userFormItem != null) {
            /*userFormItem = mprojectFormRepository.saveUserFormSubmittedValue(projectId, formId, submitValue, DateFormatter.getDateFromString(formDate), userFormId, loginResponse);
            formSubmitMobileId = userFormItem.getFormSubmitMobileId();*/
            user_form_id = userFormItem.getId();
            Log.d("OPEN_FORM", "userFormItem != null " + user_form_id);
        } else if (firstUserForms != null) {
            user_form_id = firstUserForms.getId();
            Log.d("OPEN_FORM", "firstUserForms != null " + user_form_id);
        }
        startActivityForResult(new Intent(ProjectFormDetailActivity.this, FormEmailActivity.class)
                        .putExtra("project_id", projectId)
                        // .putExtra("formRevisionNumber",)
                        .putExtra("form_id", firstUserForms.getFormId())
                        .putExtra(Constants.INTENT_KEY_ORIGINAL_FORM_ID, originalFormId)
                        .putExtra(Constants.INTENT_KEY_FORM_ACTIVE_REVISION_NUMBER, activeRevisionNumber)
                        .putExtra("delete_images", deleteImageString)
                        .putExtra(Constants.INTENT_KEY_PROJECT_FORM_AREAS_ID, projectFormAreasID)
                        .putExtra(Constants.INTENT_KEY_FORM_CREATED_DATE, formDate)
                        .putExtra(Constants.INTENT_KEY_FORM_SAVE_DATE, DateFormatter.formatDateTimeHHForService(firstUserForms.getFormSaveDate()))
                        .putExtra("user_form_id", user_form_id).putExtra("due_date", dueDate)
                        .putExtra("schedule_form_id", schedule_form_id).putExtra("submit_value", submitValue)
                , FORM_SEND);

    }

    @JavascriptInterface
    public void onData(String value) {
        Log.i("OPEN_FORM", "onData: user  form id " + userFormId);

        //.. do something with the data
        File myDir = new File(ProjectFormDetailActivity.this.getFilesDir().getAbsolutePath() + "/Pronovos/Form/");
        if (BuildConfig.FLAVOR.equals("production")) {
            value = value.replaceAll(myDir.getAbsolutePath(), "https://app.pronovos.com/form/file");
        } else {
            value = value.replaceAll(myDir.getAbsolutePath(), "http://poc.pronovos.com/form/file");
        }

        callSubmitForm(value);
    }

    @JavascriptInterface
    public void onHtmlDateChange(String value) {
        formDate = value;
        isFormUpdated = true;
        formDateUpdated = true;
        Log.e("Android", "onHtmlDateChange: ");
    }

    @JavascriptInterface
    public void fileRemoved(String fileInfo, String submissionData) {
        Log.i("OPEN_FORM", "fileRemoved: " + fileInfo);
        try {
            JSONObject jsonObj = new JSONObject(fileInfo);
            URI uri = new URI(jsonObj.get("url").toString());
            String[] segments = uri.getPath().split("/");
            String imageName = segments[segments.length - 1];
            FormImage formImage = mprojectFormRepository.isFileExist(imageName);
            if (formImage != null) {
                mprojectFormRepository.deleteFormImage(formImage.getId());
            }
            File myDir = new File(ProjectFormDetailActivity.this.getFilesDir().getAbsolutePath() + "/Pronovos/Form/");
            String filePath = myDir.getAbsolutePath();
            File imgFile = new File(filePath + "/" + imageName);
            if (!allDeletedImages.contains(imageName)) {
                allDeletedImages.add(imageName);
            }


            if (imgFile.exists()) {
                imgFile.delete();
            }

            String deleteImageString = "[";
            for (String deleteImage : allDeletedImages) {
                deleteImageString = deleteImageString + deleteImage + ",";
            }
            if (deleteImageString != null && deleteImageString.length() > 0 && deleteImageString.charAt(deleteImageString.length() - 1) == ',') {
                deleteImageString = deleteImageString.substring(0, deleteImageString.length() - 1);
            }
            deleteImageString = deleteImageString + "]";
            try {
                JSONObject jObj = new JSONObject(submissionData);
                if (jObj.has("data")) {

                    String formData;
//                    if (BuildConfig.BASE_URL.equals(Constants.PRODUCTION)) {
                    if (BuildConfig.FLAVOR.equals("production")) {
                        formData = jObj.getJSONObject("data").toString().replaceAll(myDir.getAbsolutePath(), "https://" + Constants.PRODUCTION_URL + "/form/file");
                    } else {
                        formData = jObj.getJSONObject("data").toString().replaceAll(myDir.getAbsolutePath(), "http://" + Constants.DEVELOPMENT_URL + "/form/file");
                    }

                    UserForms userForms = mprojectFormRepository.saveUserFormTempData(projectId, originalFormId, formData, userFormId,
                            loginResponse, dueDate, schedule_form_id, deleteImageString, DateFormatter.getDateFromString(formDate), activeRevisionNumber, projectFormAreasID);
                    if (userForms != null) {
                        userFormId = userForms.getId();
                        Log.d("OPEN_FORM", "**************** fileRemoved: userFormId " + userFormId);

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void onHtmlFormAreaChange(String value) {
        if (!TextUtils.isEmpty(value)) {
            projectFormAreasID = Integer.parseInt(value);
            isFormUpdated = true;
            if (userFormItem != null) {
                userFormItem.setPjAreasId(projectFormAreasID);
            }
            Log.e("FORMDETAIL", "onHtmlFormAreaChange: " + projectFormAreasID);
        }
    }

    @JavascriptInterface
    public void onChange(String submissionData) {
        Log.e("OPEN_FORM", "onChange: submissionData userFormId " + userFormId);

        String deleteImageString = "[";
        for (String deleteImage : allDeletedImages) {
            deleteImageString = deleteImageString + deleteImage + ",";
        }
        if (deleteImageString != null && deleteImageString.length() > 0 && deleteImageString.charAt(deleteImageString.length() - 1) == ',') {
            deleteImageString = deleteImageString.substring(0, deleteImageString.length() - 1);
        }
        deleteImageString = deleteImageString + "]";
        try {
            JSONObject jsonObj = new JSONObject(submissionData);
            isFormUpdated = true;
            if (jsonObj.has("data")) {
                File myDir = new File(ProjectFormDetailActivity.this.getFilesDir().getAbsolutePath() + "/Pronovos/Form/");

                String value = jsonObj.getJSONObject("data").toString();
//                if (BuildConfig.FBASE_URL.equals(Constants.PRODUCTION)) {
                if (BuildConfig.FLAVOR.equals("production")) {
                    value = value.replaceAll(myDir.getAbsolutePath(), "https://" + Constants.PRODUCTION_URL + "/form/file");
                } else {
                    value = value.replaceAll(myDir.getAbsolutePath(), "http://" + Constants.DEVELOPMENT_URL + "/form/file");
                }
                Log.e("OPEN_FORM", "onChange: save temp data userFormId = " + userFormId + "  schedule_form_id " + schedule_form_id);
                tempUserForm = mprojectFormRepository.saveUserFormTempData(projectId, originalFormId, value,
                        userFormId, loginResponse, dueDate, schedule_form_id
                        , deleteImageString, DateFormatter.getDateFromString(formDate), activeRevisionNumber, projectFormAreasID);
                if (tempUserForm != null) {
                    // TODO ASK SHWETA NITIN
                    if (userFormId == -1)
                        userFormId = tempUserForm.getId();
                    Log.d("OPEN_FORM", " *************  onChange:tempUserForm  userFormId  " + userFormId);
                    loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
                    Log.d("OPEN_FORM", " onChange: save temp data " + userFormId + "  schedule_form_id " + schedule_form_id);

                    if (NetworkService.isNetworkAvailable(this) && loginResponse.getUserDetails().getPermissions().get(0).getEditForm() == 1) {
//                        deleteImageView.setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @JavascriptInterface
    public void onImageLoad() {

    }

    private void updateTempData(JSONObject formJson) {
        try {
            if (formJson != null && formJson.has("data")) {
                File myDir = new File(ProjectFormDetailActivity.this.getFilesDir().getAbsolutePath() + "/Pronovos/Form/");

                String value = null;

                value = formJson.getJSONObject("data").toString();

                if (BuildConfig.FLAVOR.equals("production")) {
                    value = value.replaceAll(myDir.getAbsolutePath(), "https://" + Constants.PRODUCTION_URL + "/form/file");
                } else {
                    value = value.replaceAll(myDir.getAbsolutePath(), "http://" + Constants.DEVELOPMENT_URL + "/form/file");
                }
                mprojectFormRepository.saveUserFormTempData(projectId, originalFormId, value, userFormId, loginResponse, dueDate, schedule_form_id,
                        deleteImageString, DateFormatter.getDateFromString(formDate), activeRevisionNumber, projectFormAreasID);
            } else if (formJson != null && formType.equals("Un-sync")) {
                mprojectFormRepository.saveUserFormTempData(projectId, originalFormId, formJson.toString(), userFormId, loginResponse, dueDate,
                        schedule_form_id, deleteImageString, DateFormatter.getDateFromString(formDate), activeRevisionNumber, projectFormAreasID);
            } else {
                UserForms userForms = mprojectFormRepository.getUserFormSubmitted(userFormId, loginResponse.getUserDetails().getUsers_id());
                if (userForms.getFormSubmitId() == 0 && initialTempFormJson == null) {
                    mprojectFormRepository.removeUserForm(userForms, loginResponse.getUserDetails().getUsers_id());
                } else if (initialTempFormJson == null) {
                    mprojectFormRepository.removeUserFormTempData(userFormId, loginResponse.getUserDetails().getUsers_id());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handaleOfflineFormComplete(String value) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        UserForms userFormItem = mprojectFormRepository.saveUserFormSubmittedData(projectId, originalFormId, value, userFormId, loginResponse, dueDate, schedule_form_id, deleteImageString, 1,
                projectFormAreasID, DateFormatter.getDateFromString(formDate), activeRevisionNumber);
        formSubmitMobileId = userFormItem.getFormSubmitMobileId();
        Log.e("OPEN_FORM", "   handaleOfflineFormComplete:  formSubmitMobileId *   " + formSubmitMobileId);
        //  ProjectFormDetailActivity.this.finish();
        if (saveTextView.getText().toString().equalsIgnoreCase("COMPLETE")) {
            edit_permission = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    saveTextView.setEnabled(false);
                    saveTextViewRL.setEnabled(false);
                    saveTextView.setText("EMAIL FORM");
                    cancelTextView.setText("Return");
                    callLoadFormAPI(mprojectFormRepository.getFormComponent(formId), value);
                }
            });

        }
    }

    private void callSubmitForm(String value) {
        try {
            Log.d("OPEN_FORM", "  callSubmitForm: ");
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && imm.isActive()) {
                imm.hideSoftInputFromWindow(saveTextView.getWindowToken(), 0);
            }
        } catch (Exception ignored) {
        }
        if (/*manageDateChange &&*/ (dueDate == null || !dueDate.equals(formDate)) && formDateUpdated) {
            dueDate = null;
            schedule_form_id = 0;
            UserForms userForms = mprojectFormRepository.getUserFormSubmitted(userFormId, loginResponse.getUserDetails().getUsers_id());
            if (userForms != null && userForms.getFormSubmitId() == 0) {
                mprojectFormRepository.removeUserForm(userForms, loginResponse.getUserDetails().getUsers_id());
            }
        }
        deleteImageString = "[";
        for (String deleteImage : allDeletedImages) {
            deleteImageString = deleteImageString + deleteImage + ",";
        }
        if (deleteImageString != null && deleteImageString.length() > 0 && deleteImageString.charAt(deleteImageString.length() - 1) == ',') {
            deleteImageString = deleteImageString.substring(0, deleteImageString.length() - 1);
        }
        deleteImageString = deleteImageString + "]";
        if (!callSaveAndSend) {
            // save and exit click here
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            mprojectFormRepository.saveUserFormSubmittedData(projectId, originalFormId, value, userFormId, loginResponse, dueDate,
                    schedule_form_id, deleteImageString, 0, projectFormAreasID, DateFormatter.getDateFromString(formDate), activeRevisionNumber);
            this.finish();
        } else {
            if (saveTextView.getText().toString().equalsIgnoreCase("Complete"))
                handleCompleteForm(value);

            else if (saveTextView.getText().toString().equalsIgnoreCase("Email Form")) {
                if (NetworkService.isNetworkAvailable(this)) {
                    CustomProgressBar.showDialog(ProjectFormDetailActivity.this);
                    callSubmitDataForm(value, deleteImageString);
                }
            }
        }


    }

    private void handleCompleteForm(String value) {
        {
            Boolean isOnline = NetworkService.isNetworkAvailable(this);
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            if (isOnline) {
                alertDialog.setMessage(getString(R.string.ask_message_form_complete_confirmation));
            } else {
                alertDialog.setMessage(getString(R.string.ask_message_form_complete_confirmation)/*+" This will occur once back online."*/);
            }
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.yes), (dialog, which) -> {
                Log.d("OPEN_FORM", " callSubmitDataForm   handleCompleteForm: value userFormId " + userFormId + "   deleteImageString   " + deleteImageString);
                dialog.dismiss();
                if (isOnline) {
                    CustomProgressBar.showDialog(ProjectFormDetailActivity.this);

                    callSubmitDataForm(value, deleteImageString);
                } else {
                    handaleOfflineFormComplete(value);
                }

            });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.no), (dialog, which) -> {
                dialog.dismiss();

            });
            alertDialog.setCancelable(false);
            alertDialog.show();
            Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(ContextCompat.getColor(this, R.color.gray_948d8d));
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TransactionLogUpdate transactionLogUpdate) {
        Log.i("OPEN_FORM", "onEvent TransactionLogUpdate  bind: 1 ");
        if (transactionLogUpdate.getTransactionModuleEnum() != null && (transactionLogUpdate.getTransactionModuleEnum()
                .equals(TransactionModuleEnum.PROJECT_FORM_SUBMIT))) {
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            firstUserForms = mprojectFormRepository.getUserFormDetails(userFormId, loginResponse.getUserDetails().getUsers_id());
            if (formSubmitMobileId != -1 && firstUserForms == null) {
                firstUserForms = mprojectFormRepository.getOfflineSubmittedUserForm(formSubmitMobileId, loginResponse.getUserDetails().getUsers_id());
            }
            if (saveTextView.getText().toString().equalsIgnoreCase("EMAIL FORM") && firstUserForms != null) {
                saveTextView.setEnabled(true);
                saveTextViewRL.setEnabled(true);
                mailOfflineProgress.setVisibility(View.GONE);
            }
        }
    }

    private void callSubmitDataForm(String submitValue, String deleteImageString) {
        Log.d("OPEN_FORM", "callSubmitDataForm: user form id " + userFormId);
        ArrayList<FormImage> formImageArrayList = new ArrayList<>();
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        try {
            JSONObject jsonObject = new JSONObject(submitValue);

            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                // Log.d("OPEN_FORM", " FORM_COMPONENT_KEY  " + key);
                if (key.contains("file-")) {

                    JSONArray fileJsonArray = jsonObject.getJSONArray(key);
                    for (int i = 0; i < fileJsonArray.length(); i++) {
                        JSONObject fileObject = fileJsonArray.getJSONObject(i);
                        URI uri = null;
                        try {
                            uri = new URI(fileObject.get("url").toString());
                            String[] segments = uri.getPath().split("/");
                            String imageName = segments[segments.length - 1];
                            FormImage formImage = mprojectFormRepository.isFileExist(imageName);
                            if (formImage != null) {
                                formImageArrayList.add(formImage);
                            }

                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (formImageArrayList.size() <= 0) {
                saveFormData(submitValue, deleteImageString);
            } else {
                imageCount = formImageArrayList.size();
                uploadCount = 0;
                for (FormImage formImage : formImageArrayList) {

                    if (formImage != null) {
                        mprojectFormProvider.uploadFormImage(formImage.getImageName(), formImage.getId(), null, null, new ProviderResult<UploadFile>() {
                            @Override
                            public void success(UploadFile result) {
                                uploadCount++;
                                if (imageCount == uploadCount) {
                                    Log.d("OPEN_FORM", "uploadFormImage  success: ");
                                    saveFormData(submitValue, deleteImageString);
                                }
                            }

                            @Override
                            public void AccessTokenFailure(String message) {
                                Log.d("OPEN_FORM", "AccessTokenFailure: message " + message);

                                mprojectFormRepository.saveUserFormSubmittedData(projectId, originalFormId, submitValue, userFormId,
                                        loginResponse, dueDate, schedule_form_id, deleteImageString, 1, projectFormAreasID, DateFormatter.getDateFromString(formDate), activeRevisionNumber);
                                CustomProgressBar.dissMissDialog(ProjectFormDetailActivity.this);
                                ProjectFormDetailActivity.this.finish();
                            }

                            @Override
                            public void failure(String message) {
                                Log.d("OPEN_FORM", "USERFORMDETAIL  failure: message  " + message);
                                mprojectFormRepository.saveUserFormSubmittedData(projectId, originalFormId, submitValue, userFormId, loginResponse, dueDate,
                                        schedule_form_id, deleteImageString, 1, projectFormAreasID, DateFormatter.getDateFromString(formDate), activeRevisionNumber);
                                CustomProgressBar.dissMissDialog(ProjectFormDetailActivity.this);
                                ProjectFormDetailActivity.this.finish();
                            }
                        });
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveFormData(String submitValue, String deleteImages) {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        if (TextUtils.isEmpty(dueDate) && schedule_form_id == 0) {
            List<FormsSchedule> formsScheduleList = mprojectFormRepository.getProjectFormSchedule(projectId, formId);
            for (FormsSchedule formsSchedule : formsScheduleList) {

                Forms forms = mprojectFormRepository.getFormDetails(projectId, formsSchedule.getFormsId(), loginResponse.getUserDetails().getUsers_id());
                if (!TextUtils.isEmpty(formsSchedule.getRecurrence())) {
                    if (formsSchedule.getStartDate() != null && forms != null) {
                        Date d1 = formsSchedule.getStartDate();
                        Calendar currentPageDate = Calendar.getInstance();
                        currentPageDate.setTime(DateFormatter.getDateFromString(formDate));
                        Calendar calendar = Calendar.getInstance();
                        Calendar endCalendar = Calendar.getInstance();
                        calendar.set(currentPageDate.DAY_OF_MONTH, 1);
                        calendar.set(Calendar.MONTH, currentPageDate.get(currentPageDate.MONTH) - 1);
                        calendar.set(Calendar.YEAR, currentPageDate.get(Calendar.YEAR));
                        endCalendar.setTime(d1);
                        endCalendar.set(Calendar.YEAR, currentPageDate.get(Calendar.YEAR));
                        String day = (String) DateFormat.format("dd", formsSchedule.getStartDate()); // 20
                        String monthNumber = (String) DateFormat.format("MM", formsSchedule.getStartDate()); // 06
                        String year = (String) DateFormat.format("yyyy", formsSchedule.getStartDate()); // 2013
                        if (currentPageDate.get(Calendar.YEAR) - 1 > Integer.parseInt(year)) {
                            year = currentPageDate.get(Calendar.YEAR) - 1 + "";
                        }
                        FormsSchedule formsSchedule1 = showScheduleOnCalendar(Integer.parseInt(day), Integer.parseInt(monthNumber) - 1, Integer.parseInt(year), formsSchedule, currentPageDate, endCalendar, DateFormatter.getDateFromString(formDate), loginResponse);
                        if (formsSchedule1 != null) {
                            schedule_form_id = formsSchedule1.getScheduledFormId();
                            SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            dueDate = sdformat.format(DateFormatter.getDateFromString(formDate));
                            break;
                        }
                    }
                } else if (forms != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(formsSchedule.getStartDate());
                    SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
                    List<UserForms> userForm = mprojectFormRepository.getUserFormDueDate(cal, formsSchedule.getFormsId(), formsSchedule.getPjProjectId(), formsSchedule.getScheduledFormId(),
                            loginResponse.getUserDetails().getUsers_id());
                    if (userForm.size() <= 0 && sdformat.format(cal.getTime()).equals(sdformat.format(DateFormatter.getDateFromString(formDate)))) {
                        schedule_form_id = formsSchedule.getScheduledFormId();
                        SimpleDateFormat sdformat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        dueDate = sdformat1.format(DateFormatter.getDateFromString(formDate));
                        break;
                    }
                }
            }
        }


        SubmitFormRequest submitFormRequest = new SubmitFormRequest();
        List<Submission> submissions = new ArrayList<>();
        Submission submission = new Submission();
        submission.setForm(originalFormId);
        submission.setProject(projectId);
        submission.setSendEmail(0);
        submission.setScheduleFormId(schedule_form_id);
        submission.setPublish(1);
        submission.setDueDate(dueDate);
        submission.setSubmittedData(submitValue);
        submission.setCreatedDate(formDate);
        if (projectFormAreasID != null && projectFormAreasID != 0)
            submission.setPjAreasId(projectFormAreasID);
        userFormItem = null;
        if (userFormItem == null) {
            userFormItem = mprojectFormRepository.saveUserFormSubmittedValue(projectId, originalFormId, activeRevisionNumber,
                    submitValue, DateFormatter.getDateFromString(formDate), userFormId, projectFormAreasID, loginResponse);
            formSubmitMobileId = userFormItem.getFormSubmitMobileId();
        }
        if (userFormItem.getPjAreasId() != null && userFormItem.getPjAreasId() != 0)
            submission.setPjAreasId(userFormItem.getPjAreasId());
        submission.setUserFormsId(userFormItem.getFormSubmitId());
        submission.setRevisionNumber(userFormItem.getRevisionNumber());
        submission.setUserFormMobileId(userFormItem.getFormSubmitMobileId());
        submission.setSaveDate(DateFormatter.formatDateTimeHHForService(userFormItem.getFormSaveDate()));
        formSubmitMobileId = userFormItem.getFormSubmitMobileId();
        String str = deleteImages;
        if (str != null) {
            str = str.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "");
            if (!TextUtils.isEmpty(str)) {
                ArrayList<String> deletedImages = new ArrayList<String>(Arrays.asList(str.split(",")));
                submission.setDeletedImages(deletedImages);
            }
        }
        submissions.add(submission);
        submitFormRequest.setSubmission(submissions);

        Log.d("OPEN_FORM", submission.getUserFormsId() + " submission.setUserFormsId   saveFormData: userFormItem.getFormId() "
                + userFormItem.getFormId() + "  formSubmitMobileId  " + formSubmitMobileId
                + "  userFormItem.getRevisionNumber() " + userFormItem.getRevisionNumber());
        mprojectFormProvider.submitProjectFormComponents(userFormItem.getFormId(),
                userFormItem.getFormId(), userFormItem.getRevisionNumber(),
                userFormItem.getPjProjectsId(),
                submitFormRequest, loginResponse, new ProviderResult<List<UserForms>>() {
                    @Override
                    public void success(List<UserForms> result) {
                        Log.d("OPEN_FORM", "saveFormData    success: result  "
                                + result + "    formSubmitMobileId   " + formSubmitMobileId +
                                "   userFormItem.getRevisionNumber() " + userFormItem.getRevisionNumber());
                        // after updaate to server update local object for email to next screen
                        firstUserForms = mprojectFormRepository.getUserForm(projectId,
                                submission.getUserFormMobileId(), loginResponse.getUserDetails().getUsers_id());
                        Log.d("OPEN_FORM", "saveFormData    success: firstUserForms  " + firstUserForms);
               /* startActivityForResult(new Intent(ProjectFormDetailActivity.this, FormEmailActivity.class)
                                .putExtra("project_id", projectId)
                                .putExtra("form_id", formId)
                                .putExtra("delete_images", deleteImageString)
                                .putExtra(Constants.INTENT_KEY_FORM_CREATED_DATE, formDate)
                                .putExtra("user_form_id", userFormItem.getId()).putExtra("due_date", dueDate)
                                .putExtra("schedule_form_id", schedule_form_id).putExtra("submit_value", submitValue)
                        , FORM_SEND);*/
                        if (saveTextView.getText().toString().equalsIgnoreCase("COMPLETE")) {
                            saveTextView.setText("EMAIL FORM");
                            cancelTextView.setText("Return");
                            edit_permission = true;

                            Log.e("OPEN_FORM", formId + " = formId COmpleteFrormSuccess success: submittedData  userFormId  " + userFormId + "   mprojectFormRepository.getFormComponent(formId) " + mprojectFormRepository.getFormComponent(formId));
                            callLoadFormAPI(mprojectFormRepository.getFormComponent(formId), submitValue);
                        }
                        CustomProgressBar.dissMissDialog(ProjectFormDetailActivity.this);
                    }

                    @Override
                    public void AccessTokenFailure(String message) {
        /*      progressBar.setVisibility(View.GONE);
                sendTextView.setVisibility(View.VISIBLE);
                cancleTextView.setVisibility(View.VISIBLE);
*/
                        mprojectFormRepository.saveUserFormSubmittedData(projectId, originalFormId, submitValue, userFormId, loginResponse,
                                dueDate, schedule_form_id, deleteImageString, 1, projectFormAreasID, DateFormatter.getDateFromString(formDate), activeRevisionNumber);
                        CustomProgressBar.dissMissDialog(ProjectFormDetailActivity.this);
                        ProjectFormDetailActivity.this.finish();

                    }

                    @Override
                    public void failure(String message) {
  /*              messageDialog.showMessageAlert(FormEmailActivity.this, message, getString(R.string.ok));
                progressBar.setVisibility(View.GONE);
                sendTextView.setVisibility(View.VISIBLE);
                cancleTextView.setVisibility(View.VISIBLE);
  */
                        mprojectFormRepository.saveUserFormSubmittedData(projectId, originalFormId, submitValue, userFormId, loginResponse, dueDate,
                                schedule_form_id, deleteImageString, 1, projectFormAreasID, DateFormatter.getDateFromString(formDate), activeRevisionNumber);
                        CustomProgressBar.dissMissDialog(ProjectFormDetailActivity.this);
                        ProjectFormDetailActivity.this.finish();
                    }
                });

    }

    private FormsSchedule showScheduleOnCalendar(int day, int month, int year, FormsSchedule formsSchedule, Calendar currentPageDate, Calendar cal1, Date createdDate, LoginResponse loginResponse) {
        // com.pronovoscm.chipslayoutmanager.util.log.Log.i("Calendar rr ", "showScheduleOnCalendar: " + day + "/" + month + "/" + year + "  rule  " + formsSchedule.getRecurrence());
        RecurrenceRule rule = null;
        ArrayList<String> exDateList = new ArrayList<>();
        String[] exDates = null;
        RecurrenceRuleIterator it = null;
        DateTime start = new DateTime(year, month, day, 0, 0, 0);
        try {
            String rrule = formsSchedule.getRecurrence();
            int maxInstances = 0;
            //            if (!rrule.contains("EXDATE") ) {
            rule = new RecurrenceRule(formsSchedule.getRecurrence());
            //   com.pronovoscm.chipslayoutmanager.util.log.Log.e("Test", "showScheduleOnCalendar: " + rule);
            it = rule.iterator(start);
            maxInstances = 366;
            if (rrule.contains("EXDATE")) {
                String[] parts = formsSchedule.getRecurrence().toUpperCase().split(";");
                for (String keyvalue : parts) {
                    if (keyvalue.startsWith("EXDATE")) {
                        int equals = keyvalue.indexOf("=");
                        if (equals > 0) {
                            String key = keyvalue.substring(0, equals);
                            if (key.equals("EXDATE")) {
                                String value = keyvalue.substring(equals + 1);
                                exDates = value.toUpperCase().split(",");
                                //                                    exDateList = Arrays.asList(value.toUpperCase().split(","));
                            }
                        }
                        break;
                    }
                }

            }

            if (exDates != null) {
                exDateList.addAll(Arrays.asList(exDates));
            }

            while (it.hasNext() && (!rule.isInfinite() || maxInstances-- > 0)) {

                DateTime nextInstance = it.nextDateTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                Date date = null;
                Date date2 = null;
                try {
                    date = sdf.parse(nextInstance.toString());
                    date2 = sdf.parse(nextInstance.toString());
                    currentPageDate.set(Calendar.DAY_OF_MONTH, currentPageDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                    currentPageDate.set(Calendar.MONTH, currentPageDate.MONTH + 2);
                    SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    if (year < cal.get(Calendar.YEAR)) {
                        //       Log.i("Calendar", year + " end date showScheduleOnCalendar: == " + cal.get(Calendar.YEAR));
                        break;
                    }
                    if (year > cal.get(Calendar.YEAR)) {
                        maxInstances = maxInstances + 1;
                        continue;
                    }

                    SimpleDateFormat exsdformat = new SimpleDateFormat("yyyyMMdd");
                    List<UserForms> userForm = mprojectFormRepository.getUserFormDueDate(cal, formsSchedule.getFormsId(), formsSchedule.getPjProjectId(), formsSchedule.getScheduledFormId(), loginResponse.getUserDetails().getUsers_id());
                    /*com.pronovoscm.chipslayoutmanager.util.log.Log.i("formSchedule", start + " " + sdformat.format(cal.getTime())
                            + " new  == showScheduleOnCalendar: formSchedule " + formsSchedule.getRecurrence() + " id "
                            + formsSchedule.getFormsId() + " schedule id " + formsSchedule.getScheduledFormId());*/

                    boolean excontain = exDateList.contains(exsdformat.format(cal.getTime()));
                    int userFormSize = userForm.size();
                    if (sdformat.format(cal.getTime()).equals("2020-01-16")) {
                        com.pronovoscm.chipslayoutmanager.util.log.Log.e("test", formsSchedule.getStartDate() + "userFormSize  " + userFormSize + " excontain = " + excontain + " rrule " + formsSchedule.getRecurrence() + " form schedule test " + cal.getTime());
                    }


                    if (!exDateList.contains(exsdformat.format(cal.getTime())) && userForm.size() <= 0 && sdformat.format(cal.getTime()).equals(sdformat.format(createdDate))) {
                        return formsSchedule;
                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (InvalidRecurrenceRuleException e) {
            com.pronovoscm.chipslayoutmanager.util.log.Log.i("RRule", "showScheduleOnCalendar: " + formsSchedule.getRecurrence());
            e.printStackTrace();
        }
        return null;
    }

    public void dispatchTakePictureIntent(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 121);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFiles();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 121);
            }
        }
    }

    String currentPhotoPath;

    private File createImageFiles() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void showChooserDialog() {

        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.choose_from_library)};
        TextView title = new TextView(this);
        title.setText(R.string.add_photo);
        title.setBackgroundColor(Color.BLACK);
        title.setPadding(10, 15, 15, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(22);
        builder = new AlertDialog.Builder(this);

        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals(getString(R.string.take_photo))) {
                if (ContextCompat.checkSelfPermission(ProjectFormDetailActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, FILECAMERA_REQUEST_CODE);
                        } else {
                            requestPermissions(new String[]{Manifest.permission.CAMERA, getExternalPermission()}, FILECAMERA_REQUEST_CODE);
                        }
                    } else {

                        resetCallback = false;
                        dialog.dismiss();

                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }

                            if (photoFile != null) {
                                //mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                                Uri imageUri = FileProvider.getUriForFile(ProjectFormDetailActivity.this, "com.pronovoscm.provider", //(use your app signature + ".provider" )
                                        photoFile);
                                mCameraPhotoPath = Uri.fromFile(photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
                            }
                        }
                    }
                } else {

                    resetCallback = false;
                    dialog.dismiss();

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    if (photoFile != null) {
                        //mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        Uri imageUri = FileProvider.getUriForFile(ProjectFormDetailActivity.this, "com.pronovoscm.provider", //(use your app signature + ".provider" )
                                photoFile);
                        mCameraPhotoPath = Uri.fromFile(photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
                    }
//                    }
                }
            } else if (items[item].equals(getString(R.string.choose_from_library))) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getExternalPermission()) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{getExternalPermission()}, PERMISSION_READ_REQUEST_CODE);
                } else {
                   /* Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
//                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), SELECT_PICTURE);*/
                    resetCallback = false;
                    dialog.dismiss();
                    Intent albumIntent = new Intent(Intent.ACTION_PICK);
                    albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(albumIntent, REQUEST_CODE_ALBUM);

                }
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == FILECAMERA_REQUEST_CODE) {
            resetCallback = false;
            //            dialog.dismiss();
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                if (photoFile != null) {
                    //mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    Uri imageUri = FileProvider.getUriForFile(ProjectFormDetailActivity.this, "com.pronovoscm.provider", //(use your app signature + ".provider" )
                            photoFile);
                    mCameraPhotoPath = Uri.fromFile(photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
                }
            }

        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == PERMISSION_READ_REQUEST_CODE) {

            resetCallback = false;
            //            dialog.dismiss();
            Intent albumIntent = new Intent(Intent.ACTION_PICK);
            albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(albumIntent, REQUEST_CODE_ALBUM);


        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ALBUM:
                    if (data != null) {

                        String dataString = data.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                            File currentFile = null;
                            currentFile = new File(getRealPathFromURI(ProjectFormDetailActivity.this, results[0]));
                            File myDir = new File(ProjectFormDetailActivity.this.getFilesDir().getAbsolutePath() + "/Pronovos/Form/");
                            String filePath = myDir.getAbsolutePath();
                            File imgFile = new File(filePath);
                            try {
                                exportFile(currentFile, imgFile);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }
                    break;
                case REQUEST_CODE_CAMERA:
                    Log.d("CustomChooserActivity", mCameraPhotoPath.getPath());
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{mCameraPhotoPath};
//                        mFilePathCallback.onReceiveValue(results);
                        loadFile(results[0].toString());

                        File fdelete = new File(mCameraPhotoPath.getPath());
                        if (fdelete.exists()) {
                            if (fdelete.delete()) {
                                // System.out.println("file Deleted :" + mCameraPhotoPath.getPath());
                            } else {
                                //System.out.println("file not Deleted :" + mCameraPhotoPath.getPath());
                            }
                        }
                    }

                    break;
                case FORM_SEND:
                    if (data != null) {
                        finish();
                    }
                    break;
            }
        }
    }

    private void loadFile(String result) {
        File currentFile = null;
        try {
            currentFile = new File(new URI(result));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        File myDir = new File(ProjectFormDetailActivity.this.getFilesDir().getAbsolutePath() + "/Pronovos/Form/");

        String filePath = myDir.getAbsolutePath();
        File imgFile = new File(filePath);
        try {
            exportFile(currentFile, imgFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeHTMLFILE(String content) {
        try {
            Log.d("OPEN_FORM", "writeHTMLFILE: " + ProjectFormDetailActivity.this.getExternalFilesDir(null));
            File htmlFile = new File("/mnt/sdcard" + "/Download/Pronovos.html");
            FileOutputStream fos = new FileOutputStream(htmlFile);
            fos.write(content.getBytes());
            fos.close();
            Log.e("OPEN_FORM", "writeHTMLFILE: " + htmlFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File exportFile(File src, File dst) throws IOException {
///storage/emulated/0/DCIM/Camera/PXL_20230201_085503483.jpg: open failed: EACCES (Permission denied)
        //if folder does not exist
        if (!dst.exists()) {
            if (!dst.mkdir()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        String fileName = "IMG_ANDROID_" + timeStamp + loginResponse.getUserDetails().getAuthtoken().substring(loginResponse.getUserDetails().getAuthtoken().length() - 3) + ".jpg";

        File expFile = new File(dst.getPath() + File.separator + fileName);
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            outChannel = new FileOutputStream(expFile).getChannel();
            inChannel = new FileInputStream(src).getChannel();
            try {
                InputStream iStream = null;
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(src.getPath());
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

                    Matrix matrix = new Matrix();
                    if (orientation == 6) {
                        matrix.postRotate(90);
                    } else if (orientation == 3) {
                        matrix.postRotate(180);
                    } else if (orientation == 8) {
                        matrix.postRotate(270);
                    }
                    Bitmap mb = BitmapFactory.decodeFile(src.getPath());
                    Bitmap myBitmap = Bitmap.createBitmap(mb, 0, 0, mb.getWidth(), mb.getHeight(), matrix, true); // rotating bitmap


                    inChannel.transferTo(0, inChannel.size(), outChannel);
//                    File expFile = new File("path");
                    OutputStream os = new BufferedOutputStream(new FileOutputStream(expFile));
                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.close();
                    newCreatedFile = expFile;


                    mprojectFormRepository.saveNewFile(newCreatedFile.getName(), loginResponse);
                    if (newCreatedFile != null) {
                        JSONObject jsonObj = null;
                        JSONArray fileJsonArray;
                        try {
                            String string = "{\"name\":\"" + fileName + "\",\"storage\":\"url\",\"data\":{\"form\":\"\",\"baseUrl\":\"https:\\/\\/api.form.io\",\"project\":\"\",\"url\":\"" + newCreatedFile.getAbsolutePath() + "\"},\"size\":5129941,\"type\":\"image\\/jpeg\",\"originalName\":\"" + fileName + "\",\"url\":\"" + newCreatedFile.getAbsolutePath() + "\"}";
                            JSONObject fileJsonObject = new JSONObject(string);
                            jsonObj = new JSONObject(submittedData);
                            if (jsonObj.has("data")) {
                                //                        Iterator iterator = jsonObj.keys();
                                //                        while (iterator.hasNext()) {
                                //                            String key = (String) iterator.next();
                                //                            if (key.equals(fileKey)) {
                                JSONObject datajsonObject = jsonObj.getJSONObject("data");

                                if (datajsonObject.has(fileKey)) {
                                    fileJsonArray = datajsonObject.getJSONArray(fileKey);
                                    if (fileJsonArray != null) {

                                        fileJsonArray.put(fileJsonObject);
                                        // datajsonObject.put(fileKey, fileJsonArray);

                                    } else {
                                        JSONArray jsonArray = new JSONArray();
                                        jsonArray.put(fileJsonObject);
                                        datajsonObject.put(fileKey, jsonArray);
                                    }

                                }
                                //                        jsonObj.remove("data");
                                //                        jsonObj.put("data",datajsonObject);
                            }
                            callLoadFormAPI(mprojectFormRepository.getFormComponent(formId), jsonObj.get("data").toString());
                            onChange(jsonObj.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();


        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }

        return expFile;
    }

    private void showRibbonLayout(long userFormId, int projectId, int users_id) {
        UserForms userForms = mprojectFormRepository.getUserFormDetails(userFormId, users_id, projectId);
        ConstraintLayout ribbonLayout = findViewById(R.id.formRibbonId);
        Log.d("Manya", "showRibbonLayoutForm Id : " + userFormId);
        ImageView icon = findViewById(R.id.emailNotSentIconId);
        TextView ribbonTxt = findViewById(R.id.ribbonTxtId);
        String date = "";
        if (userForms != null) {
            if (userForms.getUpdatedAt() != null)
                date = DateFormatter.currentDateIntoMMDDYYY(userForms.getUpdatedAt());

            if (userForms.getEmailStatus() == 0 && userForms.getDateSent() == null) {
                ribbonLayout.setVisibility(View.VISIBLE);
            } else if (userForms.getEmailStatus() == 2 && userForms.getDateSent() != null) {
                ribbonLayout.setVisibility(View.VISIBLE);
                icon.setImageResource(R.drawable.ic_digital_signature_valid);
                ribbonTxt.setText(getString(R.string.email_sent, date));
            } else if (userForms.getEmailStatus() == 3) {
                ribbonLayout.setVisibility(View.VISIBLE);
                icon.setImageResource(R.drawable.ic_digital_signature_warning);
                ribbonTxt.setText(getString(R.string.email_failed, date));
            } else if (userForms.getEmailStatus() == 1) {
                ribbonLayout.setVisibility(View.VISIBLE);
                icon.setImageResource(R.drawable.ic_form_waiting);
                ribbonTxt.setText(R.string.email_in_process);
            }
        } else {
            ribbonLayout.setVisibility(View.GONE);
        }

    }

}
