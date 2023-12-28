package com.pronovoscm.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.pdftron.pdf.utils.Utils;
import com.pronovoscm.R;
import com.pronovoscm.adapter.AutocompleteSelectToAdapter;
import com.pronovoscm.data.EmailProvider;
import com.pronovoscm.data.ProjectFormProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.materialchips.ChipsInput;
import com.pronovoscm.materialchips.model.ChipInterface;
import com.pronovoscm.model.request.cclist.CCListRequest;
import com.pronovoscm.model.request.emailassignee.EmailAssigneeRequest;
import com.pronovoscm.model.request.emaildefaultsettings.DefaultSettingsRequest;
import com.pronovoscm.model.request.submitform.Submission;
import com.pronovoscm.model.request.submitform.SubmitFormRequest;
import com.pronovoscm.model.response.cclist.CCListResponse;
import com.pronovoscm.model.response.cclist.Cclist;
import com.pronovoscm.model.response.emailassignee.AssigneeList;
import com.pronovoscm.model.response.emailassignee.EmailAssigneeResponse;
import com.pronovoscm.model.response.emaildefaultsettings.DefaultSettingsResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.uploadformfile.UploadFile;
import com.pronovoscm.persistence.domain.FormImage;
import com.pronovoscm.persistence.domain.FormsName;
import com.pronovoscm.persistence.domain.UserForms;
import com.pronovoscm.persistence.repository.ProjectFormRepository;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.dialogs.CCDialog;
import com.pronovoscm.utils.dialogs.MessageDialog;
import com.pronovoscm.utils.library.AutoLabelUI;
import com.pronovoscm.utils.library.AutoLabelUISettings;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class FormEmailActivity extends BaseActivity {

    @Inject
    EmailProvider mEmailProvider;
    @Inject
    ProjectFormRepository mprojectFormRepository;

    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.toTextView)
    TextView toTextView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.dateTextView)
    TextView dateTextView;
    @BindView(R.id.ccHintTextView)
    TextView ccHintTextView;
    @BindView(R.id.assigneErrorTextView)
    TextView assigneErrorTextView;
    @BindView(R.id.ccTextView)
    TextView ccTextView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.sendTextView)
    TextView sendTextView;

    @BindView(R.id.keywordAutoLabelUI)
    AutoLabelUI keywordAutoLabelUI;

    @BindView(R.id.ccRelativeLayout)
    RelativeLayout ccRelativeLayout;

    @BindView(R.id.toView)
    TextView toHeadingTextView;

    @BindView(R.id.keywordAutoLabelToUI)
    AutoLabelUI keywordAutoLabelToUI;

    @BindView(R.id.chips_input)
    ChipsInput mChipsInput;

    @BindView(R.id.chips_input_to)
    ChipsInput mChipsInputTo;

    @BindView(R.id.toAutoTextView)
    AutoCompleteTextView toAutoTextView;

    @BindView(R.id.cancelTextView)
    TextView cancleTextView;
    @BindView(R.id.formNameTextView)
    TextView formNameTextView;
    @BindView(R.id.noRecordTextView)
    TextView noRecordTextView;

    @BindView(R.id.emailScrollView)
    ScrollView scrollViewEmail;

    @BindView(R.id.ccLinearView)
    LinearLayout ccLinearView;
    @BindView(R.id.toLinearView)
    LinearLayout toLinearView;
    @Inject
    ProjectFormProvider mprojectFormProvider;
    private int projectId;
    //    private Date date;
    private List<AssigneeList> assigneeList;
    private AssigneeList mAssigneeList;
    private Cclist mCcList;
    private List<Cclist> mCclists;
    private List<Cclist> mSelectedCclists;
    private List<Cclist> mFilteredSelectedCclists;
    private List<Cclist> mRemovedCclists;
    private MessageDialog messageDialog;
    private AutocompleteSelectToAdapter adapter;
    private int formId;
    private int originalFormId;
    String formNameTitle;
    private int pjAreasId;
    private long userFormId;
    private String submitValue;
    private UserForms userFormItem;
    private ArrayList<FormImage> formImageArrayList = new ArrayList<>();

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            assigneErrorTextView.setText("");
            Utils.hideSoftKeyboard(FormEmailActivity.this, toAutoTextView);
            toAutoTextView.setText(adapter.getItem(i).getName());
            toAutoTextView.setSelection(toAutoTextView.getText().length());
            mAssigneeList = (AssigneeList) adapterView.getItemAtPosition(i);
            toAutoTextView.requestFocus();
            progressBar.setVisibility(View.GONE);
            sendTextView.setVisibility(View.VISIBLE);
            cancleTextView.setVisibility(View.VISIBLE);
            addToLabels(mAssigneeList);
        }
    };
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int heightDiff = scrollViewEmail.getRootView().getHeight() - scrollViewEmail.getHeight();
            int contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(FormEmailActivity.this);

            if (heightDiff <= contentViewTop) {
                onHideKeyboard();

                Intent intent = new Intent("KeyboardWillHide");
                broadcastManager.sendBroadcast(intent);
            } else {
                int keyboardHeight = heightDiff - contentViewTop;
                onShowKeyboard(keyboardHeight);

                Intent intent = new Intent("KeyboardWillShow");
                intent.putExtra("KeyboardHeight", keyboardHeight);
                broadcastManager.sendBroadcast(intent);
            }
        }
    };
    private boolean keyboardListenersAttached = false;
    private UserForms savedUserForm;
    private boolean isDefaultLoaded = false;
    private boolean isCCLoaded = false;
    private String dueDate;
    private int scheduleFormId;
    private int uploadCount;
    private int imageCount;
    private String deleteImages;
    private LoginResponse loginResponse;
    private String formDate;
    private String formSaveDate;
    private int revisionNumber;

    @Override
    protected int doGetContentView() {
        return R.layout.activity_form_email;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        setData();
    }

    private int formRevisionNumber;

    public void setData() {
        doGetApplication().getDaggerComponent().inject(this);
        sendTextView.setClickable(false);
        sendTextView.setTextColor(ContextCompat.getColor(this, R.color.white));
        sendTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_opacity_blue_view));
        messageDialog = new MessageDialog();
        formRevisionNumber = getIntent().getIntExtra("formRevisionNumber", 0);
        dueDate = getIntent().getStringExtra("due_date");
        deleteImages = getIntent().getStringExtra("delete_images");
        projectId = getIntent().getIntExtra("project_id", 0);
        scheduleFormId = getIntent().getIntExtra("schedule_form_id", 0);
        formId = getIntent().getIntExtra("form_id", 0);
        userFormId = getIntent().getLongExtra("user_form_id", -1);
        pjAreasId = getIntent().getIntExtra(Constants.INTENT_KEY_PROJECT_FORM_AREAS_ID, 0);
        originalFormId = getIntent().getIntExtra(Constants.INTENT_KEY_ORIGINAL_FORM_ID, 0);
        revisionNumber = getIntent().getIntExtra(Constants.INTENT_KEY_FORM_ACTIVE_REVISION_NUMBER, 0);
        formDate = getIntent().getStringExtra(Constants.INTENT_KEY_FORM_CREATED_DATE);
        formSaveDate = getIntent().getStringExtra(Constants.INTENT_KEY_FORM_SAVE_DATE);

        savedUserForm = userFormId == -1 ? null : mprojectFormRepository.getUserFormSubmitted(userFormId, loginResponse.getUserDetails().getUsers_id());
        submitValue = getIntent().getStringExtra("submit_value");
        FormsName formsName = mprojectFormRepository.getUserFormsName(originalFormId, revisionNumber, projectId);

        if (formsName != null) {
            formNameTitle = formsName.formName;
        } else {
            formNameTitle = mprojectFormRepository.getFormName(formId);
        }
        formNameTextView.setText(formNameTitle);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.INVISIBLE);
        titleTextView.setText(getString(R.string.save_and_send));
        Date date = savedUserForm != null ? (savedUserForm.getUpdatedAt() != null ? savedUserForm.getUpdatedAt() : savedUserForm.getCreatedAt()) : new Date();
        dateTextView.setText(DateFormatter.formatDateForDailyReport(date));
        mChipsInput.setChipDeletable(true);
        mChipsInput.setChipHasAvatarIcon(false);
        mChipsInput.setShowChipDetailed(false);
        mChipsInputTo.setChipDeletable(true);
        mChipsInputTo.setChipHasAvatarIcon(false);
        mChipsInputTo.setShowChipDetailed(!isTablet());

        setAutoLabelUISettings();
        setListeners();
        setToListeners();

        assigneeList = new ArrayList<>();
        mCclists = new ArrayList<>();
        mSelectedCclists = new ArrayList<>();
        //        callCCRequest();
        //        callAssigneeRequest();
        //        callCCRequest();

        cancleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null && imm.isActive()) {
                        imm.hideSoftInputFromWindow(backImageView.getWindowToken(), 0);
                    }
                } catch (Exception ignored) {
                }
                onBackClick();
            }
        });

        attachKeyboardListeners();
    }

    /**
     * Get Assignee List
     */
    private void callAssigneeRequest() {
        EmailAssigneeRequest assigneeRequest = new EmailAssigneeRequest();
        assigneeRequest.setProjectId(projectId);

        mEmailProvider.getEmailAssignee(assigneeRequest, new ProviderResult<EmailAssigneeResponse>() {
            @Override
            public void success(EmailAssigneeResponse result) {
                assigneeList = result.getEmailAssigneeData().getAssigneeList();
                toAutoTextView.setSelectAllOnFocus(true);
                toAutoTextView.setThreshold(1);
                toAutoTextView.setOnItemClickListener(onItemClickListener);
                adapter = new AutocompleteSelectToAdapter(FormEmailActivity.this, R.layout.searchable_dapter_item, assigneeList);
                toAutoTextView.setAdapter(adapter);
                callDefaultSetting();

            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(FormEmailActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(FormEmailActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(FormEmailActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(FormEmailActivity.this, message, getString(R.string.ok));
            }
        });
    }

    /**
     * Get CC list
     */
    private void callCCRequest() {
        CCListRequest ccListRequest = new CCListRequest();
        ccListRequest.setProjectId(projectId);

        mEmailProvider.getEmailCC(ccListRequest, new ProviderResult<CCListResponse>() {
            @Override
            public void success(CCListResponse result) {
                mCclists = result.getCCListData().getCclist();
                setChip();
                isCCLoaded = true;
                if (isCCLoaded && isDefaultLoaded) {
                    toLinearView.setVisibility(View.VISIBLE);
                    ccLinearView.setVisibility(View.VISIBLE);
                    noRecordTextView.setVisibility(View.GONE);
                    sendTextView.setTextColor(ContextCompat.getColor(FormEmailActivity.this, R.color.white));
                    sendTextView.setBackground(ContextCompat.getDrawable(FormEmailActivity.this, R.drawable.rounded_blue_button));
                    sendTextView.setClickable(true);
                    dateTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(FormEmailActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(FormEmailActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(FormEmailActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(FormEmailActivity.this, message, getString(R.string.ok));
            }
        });
    }

    public void setChip() {
        mChipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                Log.e("onChipAdded", "chip added, " + newSize + "   " + chip.getId());
                mCclists.add((Cclist) chip.getId());
                if (mFilteredSelectedCclists == null) {
                    mFilteredSelectedCclists = new ArrayList<>();
                }
                mFilteredSelectedCclists.add((Cclist) chip.getId());
                mChipsInput.requestEditFocus();
            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                Log.e("onChipRemoved", "chip removed, " + newSize);
                mCclists.remove((Cclist) chip.getId());
                mRemovedCclists = new ArrayList<>();
                for (Cclist cclist : mFilteredSelectedCclists) {
                    if (cclist.getLabel().equals(chip.getLabel())) {
                        mRemovedCclists.add((Cclist) chip.getId());
                    }
                }
                removeCcList(mRemovedCclists);
                mChipsInput.requestEditFocus();
            }

            @Override
            public void onTextChanged(CharSequence text) {
                Log.e("onTextChanged", "text changed: " + text.toString());
            }
        });

        mChipsInput.setFilterableList(mCclists);
    }

    private void removeCcList(List<Cclist> mRemovedCclists) {
        mFilteredSelectedCclists.removeAll(mRemovedCclists);
        mRemovedCclists.clear();
    }

    /**
     * Get default setting
     */
    private void callDefaultSetting() {
        DefaultSettingsRequest emailDefaultSetting = new DefaultSettingsRequest(projectId, originalFormId);
        //emailDefaultSetting.setProjectId(projectId);

        mprojectFormProvider.getFormEmailDefaultSetting(emailDefaultSetting, new ProviderResult<DefaultSettingsResponse>() {
            @Override
            public void success(DefaultSettingsResponse result) {
                if (result.getData().getCcList().size() > 0) {

                    if (mFilteredSelectedCclists != null) {
                        mSelectedCclists = mFilteredSelectedCclists;
                    } else {
                        mFilteredSelectedCclists = new ArrayList<>();
                        mSelectedCclists.addAll(result.getData().getCcList());
                        mFilteredSelectedCclists.addAll(result.getData().getCcList());
                    }
                    ccHintTextView.setVisibility(View.INVISIBLE);
                    addLabels(mSelectedCclists);

                    new CountDownTimer(2000, 10) {
                        int index = 0;

                        @Override
                        public void onTick(long millisUntilFinished) {
                            if (index < mSelectedCclists.size()) {
                                mChipsInput.addChip(mSelectedCclists.get(index));
                                index++;
                            } else {
                                cancel();
                            }
                        }

                        @Override
                        public void onFinish() {

                        }
                    }.start();
                } else if (mFilteredSelectedCclists != null) {
                    mSelectedCclists = mFilteredSelectedCclists;
                    ccHintTextView.setVisibility(View.INVISIBLE);
                    addLabels(mSelectedCclists);

                    new CountDownTimer(2000, 10) {
                        int index = 0;

                        @Override
                        public void onTick(long millisUntilFinished) {
                            if (index < mSelectedCclists.size()) {
                                mChipsInput.addChip(mSelectedCclists.get(index));
                                index++;
                            } else {
                                cancel();
                            }
                        }

                        @Override
                        public void onFinish() {

                        }
                    }.start();
                }
                if (result.getData().getAssignedTo() != null && !TextUtils.isEmpty(result.getData().getAssignedTo())) {

                    for (AssigneeList assigneeList : assigneeList) {
                        if (assigneeList.getEmail().equals(result.getData().getAssignedTo())) {
                            mAssigneeList = assigneeList;
                        }
                    }
                    if (mAssigneeList != null) {
                        toTextView.setText(mAssigneeList.getName());
                        toAutoTextView.setText(mAssigneeList.getName());
                        mChipsInputTo.addChip(mAssigneeList);
                        addToLabels(mAssigneeList);
                    }
                }
                isDefaultLoaded = true;

                if (isCCLoaded && isDefaultLoaded) {
                    toLinearView.setVisibility(View.VISIBLE);
                    ccLinearView.setVisibility(View.VISIBLE);
                    noRecordTextView.setVisibility(View.GONE);
                    sendTextView.setTextColor(ContextCompat.getColor(FormEmailActivity.this, R.color.white));
                    sendTextView.setBackground(ContextCompat.getDrawable(FormEmailActivity.this, R.drawable.rounded_blue_button));
                    dateTextView.setVisibility(View.VISIBLE);
                    sendTextView.setClickable(true);
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(FormEmailActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(FormEmailActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(FormEmailActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                finish();
            }

            @Override
            public void failure(String message) {
                messageDialog.showMessageAlert(FormEmailActivity.this, message, getString(R.string.ok));
            }
        });
    }

    @OnClick(R.id.leftImageView)
    public void onBackClick() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && imm.isActive()) {
                imm.hideSoftInputFromWindow(backImageView.getWindowToken(), 0);
            }
        } catch (Exception ignored) {
        }
        if (userFormItem != null && userFormItem.getFormSubmitId() == 0) {
            mprojectFormRepository.removeUserForm(userFormItem, loginResponse.getUserDetails().getUsers_id());
        } else if (userFormItem != null && savedUserForm != null) {
            mprojectFormRepository.replaceDataWithOld(savedUserForm, loginResponse.getUserDetails().getUsers_id());
        }
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && imm.isActive()) {
                imm.hideSoftInputFromWindow(backImageView.getWindowToken(), 0);
            }
        } catch (Exception ignored) {
        }
        if (progressBar.getVisibility() != View.VISIBLE) {
            Utils.hideSoftKeyboard(this, backImageView);
            super.onBackPressed();
        }
    }
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
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }


    @OnClick(R.id.sendTextView)
    public void onSendClick() {
        Utils.hideSoftKeyboard(this, sendTextView);
        if (mAssigneeList == null) {
            assigneErrorTextView.setText(R.string.please_select_assignee);
        } else {
            try {
                JSONObject jsonObject = new JSONObject(submitValue);

                Iterator iterator = jsonObject.keys();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
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

                progressBar.setVisibility(View.VISIBLE);
                sendTextView.setVisibility(View.GONE);
                cancleTextView.setVisibility(View.GONE);

                if (formImageArrayList.size() <= 0) {
                    saveFormData(formRevisionNumber);
                } else {
                    imageCount = formImageArrayList.size();
                    uploadCount = 0;
                    for (FormImage formImage : formImageArrayList
                    ) {

                        if (formImage != null) {
                            mprojectFormProvider.uploadFormImage(formImage.getImageName(), formImage.getId(), null, null, new ProviderResult<UploadFile>() {
                                @Override
                                public void success(UploadFile result) {
                                    uploadCount++;
                                    if (imageCount == uploadCount) {
                                        saveFormData(formRevisionNumber);
                                    }

                                }

                                @Override
                                public void AccessTokenFailure(String message) {
                                    progressBar.setVisibility(View.GONE);
                                    sendTextView.setVisibility(View.VISIBLE);
                                    cancleTextView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void failure(String message) {
                                    messageDialog.showMessageAlert(FormEmailActivity.this, message, getString(R.string.ok));
                                    progressBar.setVisibility(View.GONE);
                                    sendTextView.setVisibility(View.VISIBLE);
                                    cancleTextView.setVisibility(View.VISIBLE);
                                }
                            });
                        }

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void saveFormData(int revisionNumber) {
        LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(FormEmailActivity.this).readPrefs(SharedPref.SESSION_DETAILS),
                LoginResponse.class));

        SubmitFormRequest submitFormRequest = new SubmitFormRequest();
        List<Submission> submissions = new ArrayList<>();
        Submission submission = new Submission();
        submission.setForm(formId);
        submission.setProject(projectId);
        submission.setSendEmail(1);
        submission.setScheduleFormId(scheduleFormId);
        submission.setPublish(1);
        submission.setDueDate(dueDate);
        submission.setSubmittedData(submitValue);
        submission.setSaveDate(formSaveDate);
        submission.setRevisionNumber(revisionNumber);
        submission.setCreatedDate(formDate);

        if (userFormItem == null) {
            userFormItem = mprojectFormRepository.saveUserFormSubmittedValue(projectId, originalFormId, revisionNumber,
                    submitValue, DateFormatter.getDateFromString(formDate), userFormId, pjAreasId, loginResponse);
        }
        if (userFormItem.getPjAreasId() != null && userFormItem.getPjAreasId() != 0)
            submission.setPjAreasId(userFormItem.getPjAreasId());
        submission.setUserFormsId(userFormItem.getFormSubmitId());
        submission.setRevisionNumber(userFormItem.getRevisionNumber());
        submission.setUserFormMobileId(userFormItem.getFormSubmitMobileId());
        List<String> ccLists = new ArrayList<>();
        if (mFilteredSelectedCclists != null) {
            for (Cclist cclist : mFilteredSelectedCclists) {
                ccLists.add(cclist.getEmail());
            }
        }

        submission.setCc(ccLists);
        submission.setTo(mAssigneeList.getEmail());
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
        mprojectFormProvider.submitProjectFormComponents(userFormItem.getFormId(), userFormItem.getFormId(),
                userFormItem.getRevisionNumber(),
                userFormItem.getPjProjectsId(), submitFormRequest, loginResponse, new ProviderResult<List<UserForms>>() {
                    @Override
                    public void success(List<UserForms> result) {
                        Intent intent = new Intent();
                        intent.putExtra("form_send", true);
                        setResult(RESULT_OK, intent);
                        finish();
//                progressBar.setVisibility(View.GONE);
//                sendTextView.setVisibility(View.VISIBLE);
//                cancleTextView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void AccessTokenFailure(String message) {
                        progressBar.setVisibility(View.GONE);
                        sendTextView.setVisibility(View.VISIBLE);
                        cancleTextView.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void failure(String message) {
                        messageDialog.showMessageAlert(FormEmailActivity.this, message, getString(R.string.ok));
                        progressBar.setVisibility(View.GONE);
                        sendTextView.setVisibility(View.VISIBLE);
                        cancleTextView.setVisibility(View.VISIBLE);
                    }
                });

    }

    @OnClick(R.id.toLinearView)
    public void onToDialogClick() {
        /*assigneErrorTextView.setText("");
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ToDialog toDialog = new ToDialog(mAssigneeList, assigneeList);
        Bundle bundle = new Bundle();
        bundle.putInt("projectId", projectId);
        toDialog.setCancelable(false);
        toDialog.setArguments(bundle);
        toDialog.show(ft, "");*/

    }

    @OnClick(R.id.allKeyword)
    public void onCCDialogClick() {
        openCCDialog();
    }

    /**
     * Label setting
     */
    private void setAutoLabelUISettings() {
        AutoLabelUISettings autoLabelUISettings = new AutoLabelUISettings.Builder().withIconCross(R.drawable.ic_close_white).withMaxLabels(6).withShowCross(true).withLabelsClickables(true).withTextColor(android.R.color.black).withTextSize((int) getResources().getDimension(R.dimen.dashboard_text_size)).withLabelPadding(30).build();

        keywordAutoLabelUI.setSettings(autoLabelUISettings);
        keywordAutoLabelToUI.setSettings(autoLabelUISettings);
        //        keywordAutoLabelToUI.setTextSize((int) getResources().getDimension(R.dimen.dashboard_text_size));
        //        keywordAutoLabelToUI.setLabelPadding((int) getResources().getDimension(R.dimen.dashboard_text_size));
    }

    /**
     * set key listener
     */
    private void setListeners() {
        keywordAutoLabelUI.setOnLabelsCompletedListener(() -> {
        });

        keywordAutoLabelUI.setOnRemoveLabelListener((removedLabel, position) -> {
            keywordAutoLabelUI.clear();
            mSelectedCclists.remove(position);
            addLabels(mSelectedCclists);
        });

        keywordAutoLabelUI.setOnLabelsEmptyListener(() -> {
        });

        keywordAutoLabelUI.setOnLabelClickListener((labelClicked, position) -> {
            if (position == 5) {
                openCCDialog();
            }
        });

    }

    private void setToListeners() {
        keywordAutoLabelToUI.setOnLabelsCompletedListener(() -> {
        });

        keywordAutoLabelToUI.setOnRemoveLabelListener((removedLabel, position) -> {
            keywordAutoLabelToUI.clear();
            //            assigneeList.remove(position);
            toAutoTextView.setText("");
            addToLabels(null);
        });

        keywordAutoLabelToUI.setOnLabelsEmptyListener(() -> {
        });

        keywordAutoLabelToUI.setOnLabelClickListener((labelClicked, position) -> {
            if (position == 5) {
                openCCDialog();
            }
        });

    }

    /**
     * open cc dialog
     */
    private void openCCDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        CCDialog ccDialog = new CCDialog(mSelectedCclists, mCclists);
        Bundle bundle = new Bundle();
        bundle.putInt("projectId", projectId);
        ccDialog.setCancelable(false);
        ccDialog.setArguments(bundle);
        ccDialog.show(ft, "");
    }

    /**
     * add cclist in view
     *
     * @param cclistList
     */
    private void addLabels(List<Cclist> cclistList) {
        keywordAutoLabelUI.clear();
        mSelectedCclists = cclistList;
        for (int i = 0; i < mSelectedCclists.size(); i++) {
            keywordAutoLabelUI.addLabel(mSelectedCclists.get(i).getName(), i);
        }
        /*if (mSelectedCclists.size() == 0) {
            ccHintTextView.setVisibility(View.VISIBLE);
        } else {
            ccHintTextView.setVisibility(View.INVISIBLE);
        }*/
        if (mSelectedCclists.size() >= 6) {
            keywordAutoLabelUI.getLabel(5).setText("View More");
            keywordAutoLabelUI.getLabel(5).setIcon(R.drawable.ic_add_white, false);
        }
    }

    private void addToLabels(AssigneeList imageTags) {
        keywordAutoLabelToUI.clear();
        mAssigneeList = imageTags;
        if (imageTags != null) {
            toAutoTextView.setVisibility(View.GONE);
            keywordAutoLabelToUI.addLabel(imageTags.getName(), 0);
        } else {
            toAutoTextView.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AssigneeList assigneeList) {
        if (assigneeList != null) {
            mAssigneeList = assigneeList;
            toTextView.setText(mAssigneeList.getName());
            toAutoTextView.setText(mAssigneeList.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(List<Cclist> selectedCclists) {
        if (selectedCclists != null) {
            mSelectedCclists = selectedCclists;
            addLabels(mSelectedCclists);
        }

    }

    public boolean isTablet() {
        Configuration config = getResources().getConfiguration();
        return config.smallestScreenWidthDp >= 600;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //        outState.putSerializable("ccList", (Serializable) mFilteredSelectedCclists);
        //        outState.putSerializable("assignList", (Serializable) mAssigneeList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //        mFilteredSelectedCclists = (List<Cclist>) savedInstanceState.getSerializable("ccList");
        //        mAssigneeList = (AssigneeList) savedInstanceState.getSerializable("assignList");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        View view = this.getCurrentFocus();
        if (view != null) {
            Utils.hideSoftKeyboard(this, view);
        }
        if (toAutoTextView != null) {
            toAutoTextView.dismissDropDown();
        }
        if (mChipsInput != null) {
            mChipsInput.dismissDropDown();
        }
        super.onConfigurationChanged(newConfig);
    }

    protected void onShowKeyboard(int keyboardHeight) {
        int orientation = getResources().getConfiguration().orientation;
        /*if (toAutoTextView.hasFocus()) {
            scrollViewEmail.scrollTo(0, toHeadingTextView.getTop());
        } else {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                scrollViewEmail.scrollTo(0, ccLinearView.getTop());
                }

        }*/
    }

    protected void onHideKeyboard() {
    }

    protected void attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return;
        }

        scrollViewEmail.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

        keyboardListenersAttached = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
            noRecordTextView.setVisibility(View.GONE);
            sendTextView.setClickable(false);
            sendTextView.setTextColor(ContextCompat.getColor(this, R.color.white));
            sendTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_opacity_blue_view));
        } else {
            if (isCCLoaded && isDefaultLoaded) {
                sendTextView.setTextColor(ContextCompat.getColor(this, R.color.white));
                sendTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_blue_button));
                sendTextView.setClickable(true);
            } else {
                noRecordTextView.setVisibility(View.VISIBLE);
                callAssigneeRequest();
                callCCRequest();
            }
            offlineTextView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (keyboardListenersAttached) {
            scrollViewEmail.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
        }
    }
}
