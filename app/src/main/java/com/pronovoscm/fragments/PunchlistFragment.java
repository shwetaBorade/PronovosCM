package com.pronovoscm.fragments;

import static android.app.Activity.RESULT_OK;
import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pronovos.pdf.utils.AnnotAction;
import com.pronovos.pdf.utils.AnnotUpdateAction;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.activity.BaseActivity;
import com.pronovoscm.activity.DrawingPDFActivity;
import com.pronovoscm.activity.LoginActivity;
import com.pronovoscm.activity.PronovosCameraActivity;
import com.pronovoscm.activity.PunchListActivity;
import com.pronovoscm.adapter.CompanyAdapter;
import com.pronovoscm.adapter.PunchListAttachmentAdapter;
import com.pronovoscm.data.DrawingAnnotationProvider;
import com.pronovoscm.data.FieldPaperWorkProvider;
import com.pronovoscm.data.NetworkStateProvider;
import com.pronovoscm.data.PDFFileDownloadProvider;
import com.pronovoscm.data.ProjectDrawingFolderProvider;
import com.pronovoscm.data.ProjectDrawingListProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.data.PunchListProvider;
import com.pronovoscm.materialchips.ChipsInput;
import com.pronovoscm.materialchips.model.ChipInterface;
import com.pronovoscm.model.AnnotDeleteAction;
import com.pronovoscm.model.DrawingAction;
import com.pronovoscm.model.ObjectEnum;
import com.pronovoscm.model.PDFSynEnum;
import com.pronovoscm.model.PunchListStatus;
import com.pronovoscm.model.request.assignee.AssigneeRequest;
import com.pronovoscm.model.request.drawingfolder.DrawingFolderRequest;
import com.pronovoscm.model.request.drawinglist.DrawingListRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.login.UserPermissions;
import com.pronovoscm.persistence.domain.DrawingFolders;
import com.pronovoscm.persistence.domain.DrawingList;
import com.pronovoscm.persistence.domain.PunchListAttachments;
import com.pronovoscm.persistence.domain.PunchlistAssignee;
import com.pronovoscm.persistence.domain.PunchlistDb;
import com.pronovoscm.persistence.domain.PunchlistDrawing;
import com.pronovoscm.persistence.domain.punchlist.PunchListHistoryDb;
import com.pronovoscm.persistence.domain.punchlist.PunchListRejectReasonAttachments;
import com.pronovoscm.persistence.repository.DrawingListRepository;
import com.pronovoscm.persistence.repository.FieldPaperWorkRepository;
import com.pronovoscm.persistence.repository.PunchListRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.ui.punchlist.PunchListHistoryDialog;
import com.pronovoscm.ui.punchlist.RejectPunchListDialog;
import com.pronovoscm.ui.punchlist.RejectReasonOnFragmentCallback;
import com.pronovoscm.ui.punchlist.adapter.PunchlistAssigneeList;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.FileUtils;
import com.pronovoscm.utils.IntentExtra;
import com.pronovoscm.utils.ObjectEvent;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.customcamera.CameraUtils;
import com.pronovoscm.utils.dialogs.AttachmentDeleteInterface;
import com.pronovoscm.utils.dialogs.AttachmentDialog;
import com.pronovoscm.utils.dialogs.MessageDialog;
import com.pronovoscm.utils.ui.CustomProgressBar;
import com.pronovoscm.utils.ui.LoadImageInBackground;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PunchlistFragment extends BaseFragment implements View.OnClickListener, PunchListAttachmentAdapter.OnAddItemClick,
        AttachmentDeleteInterface, RejectReasonOnFragmentCallback {
    public static final int LINK_EXIST = 444;
    public static final int FILECAMERA_REQUEST_CODE = 331;
    public static final int CAPTURE_IMAGE = 2222;
    private static final int PERMISSION_READ_REQUEST_CODE = 113;
    private static final int TAKE_PICTURE = 3115;
    private static final int SELECT_PICTURE = 5645;
    @BindView(R.id.locationEditText)
    public EditText locationEditText;
    @BindView(R.id.punchListItemDescriptionEditText)
    public EditText punchListItemDescriptionEditText;
    @Inject
    FieldPaperWorkProvider mFieldPaperWorkProvider;
    @Inject
    FieldPaperWorkRepository mFieldPaperWorkRepository;
    @Inject
    PunchListRepository mPunchListRepository;
    @Inject
    PunchListProvider mPunchListProvider;
    @Inject
    ProjectDrawingListProvider mProjectDrawingListProvider;

    @Inject
    ProjectDrawingFolderProvider mProjectDrawingProvider;

    @Inject
    DrawingAnnotationProvider mAnnotationProvider;
    @Inject
    NetworkStateProvider mNetworkStateProvider;
    @Inject
    PDFFileDownloadProvider mPDFFileDownloadProvider;
    @Inject
    DrawingListRepository mDrawingListRepository;

    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.attachmentRecycleView)
    RecyclerView recyclerView;
    @BindView(R.id.deleteTextView)
    TextView deleteTextView;
    @BindView(R.id.saveTextView)
    TextView saveTextView;
    @BindView(R.id.saveSendTextView)
    TextView saveSendTextView;
    @BindView(R.id.cancelTextView)
    TextView cancelTextView;
    //    @BindView(R.id.assignedToSpinnewView)
//    RelativeLayout assignedToSpinnewView;
    @BindView(R.id.statusSpinnewView)
    RelativeLayout statusSpinnewView;
    //    @BindView(R.id.cardView)
//    CardView cardView;
//    @BindView(R.id.assignedToSpinner)
//    AppCompatSpinner assignedToSpinner;
    @BindView(R.id.statusSpinner)
    AppCompatSpinner statusSpinner;
    @BindView(R.id.assigneeErrorTextView)
    TextView assigneeErrorTextView;
    //    @BindView(R.id.assigneeListNameTextView)
//    TextView assigneeListNameTextView;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.deleteImageView)
    ImageView deleteImageView;
    //    @BindView(R.id.addTextView)
//    TextView addTextView;
    @BindView(R.id.itemTextView)
    EditText itemTextView;
    @BindView(R.id.emailSubmitImageView)
    ImageView emailSubmitImageView;
    @BindView(R.id.dateCreatedTextView)
    TextView dateCreatedTextView;
    @BindView(R.id.dateTextView)
    TextView dateTextView;
    @BindView(R.id.createdNameTextView)
    TextView createdNameTextView;
    @BindView(R.id.punchListLinkedDrawingList)
    TextView punchListLinkedDrawingList;
    @BindView(R.id.punchListLinkedDrawing)
    TextView punchListLinkedDrawing;
    @BindView(R.id.linkExistingTextView)
    TextView linkExistingTextView;
    @BindView(R.id.descriptionErrorTextView)
    TextView descriptionErrorTextView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.dateDueShowView)
    RelativeLayout dateDueShowView;
    @BindView(R.id.tradespinnewView)
    RelativeLayout tradespinnewView;
    @BindView(R.id.punchListItemDescriptionView)
    RelativeLayout punchListItemDescriptionView;

    @BindView(R.id.assignee_chips_input)
    ChipsInput assigneeChipsInput;
    @BindView(R.id.assigneeRelativeLayout)
    RelativeLayout assigneeLayout;

    @BindView(R.id.assignee_cc_chips_input)
    ChipsInput assigneeCCChipsInput;
    @BindView(R.id.assigneeCCRelativeLayout)
    RelativeLayout assigneeCCLayout;

    @BindView(R.id.commentsEditText)
    EditText commentsEdit;

    @BindView(R.id.historyView)
    CardView historyCardView;
    @BindView(R.id.statusIconId)
    ImageView statusIcone;
    @BindView(R.id.historyStatusTitleId)
    TextView historyStatusTitle;
    @BindView(R.id.historySubTitleId)
    TextView historyStatusSubTitle;
    @BindView(R.id.statusHistoryTxtId)
    TextView statusHistoryText;
    @BindView(R.id.statusHistoryIconId)
    ImageView statusHistoryIcon;

    @BindView(R.id.approvedBtnId)
    TextView approvedBtn;
    @BindView(R.id.rejectedBtnId)
    TextView rejectedBtn;
    @BindView(R.id.recompleteBtnId)
    TextView recompleteBtn;
    @BindView(R.id.completeBtnId)
    TextView completeBtn;
    @BindView(R.id.historyTextView)
    TextView showHistoryBtn;

    List<PunchlistAssigneeList> filterCCList = new ArrayList<>();
    List<PunchlistAssigneeList> filterToList = new ArrayList<>();

    List<PunchlistAssigneeList> mRemovedCclists;
    private List<PunchlistAssigneeList> punchlistAssigneeLists = new ArrayList<>();
    private List<PunchlistAssigneeList> mFilteredSelectedAssignedLists;
    private List<PunchlistAssigneeList> mSelectedAssigneeLists = new ArrayList<>();
    private List<PunchlistAssigneeList> mSelectedAssigneeCcLists = new ArrayList<>();

    private List<PunchlistAssigneeList> punchListAssigneeCCLists = new ArrayList<>();
    private List<PunchlistAssigneeList> mFilteredSelectedAssignedCCLists;

    private Uri fileUri;
    private MessageDialog messageDialog;
    private PunchlistAssignee mPunchlistAssignee;
    private int projectId;
    private List<PunchlistAssignee> mPunchlistAssignees;
    private PunchlistDb mPunchlistDb;
    private long punchListMobileId;
    private CompanyAdapter companyAdapter;
    //    private TradeAdapter tradeAdapter;
    private List<PunchListAttachments> attachmentList;
    private List<PunchListAttachments> newAddAttachmentList;
    private List<PunchListStatus> statusList;
    private LoginResponse loginResponse = null;
    private PunchListStatus mPunchListStatus;
    private PunchListAttachmentAdapter adapterAttachment;
    private Date dueDate;
    private UserPermissions userPermissions;
    private Calendar calendar, mCalendar, calendar1;
    private boolean linkExisting;
    private boolean drawingView;
    private boolean canRemove;
    private ArrayList<PunchListAttachments> removedPunchlistAttachments;
    private Activity activity;
    private boolean saveButtonClicked = false;

    private boolean isSaveAndSendClicked = false;
    private int canDeletePunchList;

    private List<PunchListHistoryDb> punchListHistoryDbs = new ArrayList<>();

    public static List<PunchListAttachments> cloneList(List<PunchListAttachments> imageTags) {
        List<PunchListAttachments> clonedList = new ArrayList<>(imageTags.size());
        for (PunchListAttachments imageTag : imageTags) {
            if (imageTag != null && imageTag.getDeletedAt() == null) {
                clonedList.add(imageTag);
            }
        }
        return clonedList;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.punch_list_detail_view, container, false);
        ButterKnife.bind(this, rootview);
        return rootview;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int myInteger = getResources().getInteger(R.integer.quantity_length);
        Log.i("PunchlistFragment", "onConfigurationChanged: " + myInteger);
        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), myInteger));
        recyclerView.setAdapter(adapterAttachment);
//        adapterAttachment.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        cancelTextView.setOnClickListener(this);
        saveTextView.setOnClickListener(this);
        deleteTextView.setOnClickListener(this);
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        deleteImageView.setVisibility(View.INVISIBLE);
        rightImageView.setVisibility(View.INVISIBLE);
        if (!NetworkService.isNetworkAvailable(getContext())) {
            offlineTextView.setVisibility(View.VISIBLE);
        }

        attachmentList = new ArrayList<>();
        removedPunchlistAttachments = new ArrayList<>();
        newAddAttachmentList = new ArrayList<>();
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(getContext()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        canDeletePunchList = loginResponse.getUserDetails().getPermissions().get(0).getDeletePunchList();
        userPermissions = loginResponse.getUserDetails().getPermissions().get(0);
        messageDialog = new MessageDialog();
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
        projectId = getArguments().getInt("projectId");
        linkExisting = getArguments().getBoolean("linkExisting");
        drawingView = getArguments().getBoolean("drawingView");
        canRemove = getArguments().getBoolean("canRemove");
        punchListMobileId = getArguments().getLong("punchListMobileId");
        emailSubmitImageView.setClickable(true);
        if (punchListMobileId != 0) {
            mPunchlistDb = mPunchListRepository.getPunchListDetail(punchListMobileId);
            if (mPunchlistDb != null) {

                mPunchlistAssignee = new PunchlistAssignee();
                mPunchlistAssignee.setName(mPunchlistDb.getAssigneeName().get(0)); // TODO: Nitin
                if (mPunchlistDb.getAssignedTo().get(0).equals(""))
                    mPunchlistAssignee.setUsersId(0);
                else
                    mPunchlistAssignee.setUsersId(Integer.parseInt(mPunchlistDb.getAssignedTo().get(0))); //TODO: Nitin

                if (userPermissions.getEditPunchList() == 1)
                    titleTextView.setText(getString(R.string.edit_punch_list));
                else
                    titleTextView.setText(getString(R.string.punch_list));

                manageHistoryStatusView(true, mPunchlistDb);
            }
        } else {
            manageHistoryStatusView(false, mPunchlistDb);
            titleTextView.setText(getString(R.string.add_new_punch_list));
            itemTextView.setText(getString(R.string.new_title));
        }
        callAssigneeAPI();

        setPunchListInfo();

        punchListItemDescriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    descriptionErrorTextView.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        adapterAttachment = new PunchListAttachmentAdapter(getActivity(), attachmentList, position -> {
            FragmentManager fm = (getActivity()).getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            AttachmentDialog attachmentDialog = new AttachmentDialog();
            Bundle bundle = new Bundle();
            bundle.putString("attachment_path", attachmentList.get(position).getAttachmentPath());
//            bundle.putString("attachment_path", "https://s3.amazonaws.com/dev.smartsubz.com/punchlist_files/16650642275M87191vFE.jpg");
            if (mPunchlistDb != null) {
                bundle.putString("title_text", getString(R.string.punch_list));
            } else {
                bundle.putString("title_text", getString(R.string.punch_list));
            }
            if (mPunchlistDb == null || userPermissions.getEditPunchList() == 1) {
                bundle.putInt("image_position", position);
                attachmentDialog.onAttachToParentFragment(this);
            }
            attachmentDialog.setArguments(bundle);
            attachmentDialog.show(ft, "");
        }, this);
        recyclerView.setAdapter(adapterAttachment);

        int myInteger = getResources().getInteger(R.integer.quantity_length);

        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), myInteger));
        if (linkExisting) {
            linkExistingTextView.setVisibility(View.VISIBLE);
        }
       /* if (drawingView) {
//            if (canRemove && loginResponse.getUserDetails().getPermissions().get(0).getDeletePunchList() == 1) {
            if (loginResponse.getUserDetails().getPermissions().get(0).getDeletePunchList() == 1) {
                deleteTextView.setVisibility(View.VISIBLE);
            } else {
                deleteTextView.setVisibility(View.GONE);
            }
            //REMOVE
            deleteTextView.setText(R.string.delete);
        } else {
            deleteTextView.setText(R.string.delete);
        }*/

        if (mPunchlistDb != null) {
            assigneeSpinnerSelection();
        }
        setData();
//        setListeners();
//        setAutoLabelUISettings();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
        } else {
            offlineTextView.setVisibility(View.GONE);
        }
    }

    private void setPunchListInfo() {
        statusList = new ArrayList<>();
        attachmentList = new ArrayList<>();
        attachmentList.add(null);
        itemTextView.setKeyListener(null);
        statusList.add(PunchListStatus.Open);
        statusList.add(PunchListStatus.Complete);
        companyAdapter = new CompanyAdapter(getActivity(), R.layout.simple_spinner_item, statusList);
        companyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(companyAdapter);
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mPunchListStatus = statusList.get(pos);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (mPunchlistDb != null) {
            /*if (PunchListStatus.Open.getValue() == mPunchlistDb.getStatus()) {
                mPunchListStatus = PunchListStatus.Open;
            } else if (PunchListStatus.Complete.getValue() == mPunchlistDb.getStatus()) {
                mPunchListStatus = PunchListStatus.Complete;
            }*/
            mPunchListStatus = PunchListStatus.getStatus(mPunchlistDb.getStatus());
        } else {
            mPunchListStatus = PunchListStatus.Open;
        }


        mPunchlistAssignees = new ArrayList<>();
        mPunchlistAssignees.addAll(mFieldPaperWorkRepository.getAssignee(projectId));
//        tradeAdapter = new TradeAdapter(getActivity(), R.layout.simple_spinner_item, mPunchlistAssignees);
//        tradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        addAssignee(mPunchlistAssignees);
//        setChip();

        if (mPunchlistDb == null) {
            itemTextView.setText(getString(R.string.new_title));
            emailSubmitImageView.setVisibility(View.INVISIBLE);
            emailSubmitImageView.setClickable(false);
            mPunchlistAssignees.add(0, null);
            calendar = Calendar.getInstance();


            mCalendar = new GregorianCalendar(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            mCalendar.set(Calendar.HOUR, 0);
            mCalendar.set(Calendar.MINUTE, 0);
            mCalendar.set(Calendar.SECOND, 0);
            dueDate = mCalendar.getTime();
            dateCreatedTextView.setText(DateFormatter.formatDateForPunchList(dueDate));

            calendar1 = new GregorianCalendar();
            calendar1.setTime(dueDate);
            calendar1.add(Calendar.DATE, 1);
            dueDate = calendar1.getTime();
            dateTextView.setText(DateFormatter.formatDateForPunchList(dueDate));
            createdNameTextView.setText(loginResponse.getUserDetails().getFirstname() + " " + loginResponse.getUserDetails().getLastname());
            mPunchListStatus = statusList.get(0);
        } else {
//            selectTextView.setVisibility(View.GONE);
//            assigneeListNameTextView.setHint("");
            if (mPunchlistDb.getPunchlistId() != 0) {
                itemTextView.setText(String.valueOf(mPunchlistDb.getItemNumber()));
            } else {
                itemTextView.setText(getString(R.string.new_title));
            }
            dueDate = mPunchlistDb.getDateDue();
            dateCreatedTextView.setText(DateFormatter.formatDateForPunchList(mPunchlistDb.getDateCreated()));
            dateTextView.setText(DateFormatter.formatDateForPunchList(dueDate));
            createdNameTextView.setText(mPunchlistDb.getCreatedBy());
            punchListItemDescriptionEditText.setText(mPunchlistDb.getDescriptions());
            locationEditText.setText(mPunchlistDb.getLocation());

            commentsEdit.setText(mPunchlistDb.getComments());

            statusSpinnerSelection();
            attachmentList.addAll(cloneList(mPunchListRepository.getPunchListAttachments(mPunchlistDb.getPunchlistIdMobile())));
            if (attachmentList.size() >= 11 && attachmentList.get(0) == null) {
                attachmentList.remove(0);
            }
            adapterAttachment = new PunchListAttachmentAdapter(getActivity(), attachmentList, position -> {
                FragmentManager fm = (getActivity()).getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                AttachmentDialog attachmentDialog = new AttachmentDialog();
                Bundle bundle = new Bundle();
                bundle.putString("attachment_path", attachmentList.get(position).getAttachmentPath());
                if (mPunchlistDb != null) {
                    bundle.putString("title_text", getString(R.string.edit_punch_list));
                } else {
                    bundle.putString("title_text", getString(R.string.punch_list));
                }
                if (mPunchlistDb == null || userPermissions.getEditPunchList() == 1) {
                    bundle.putInt("image_position", position);
                    attachmentDialog.onAttachToParentFragment(this);
                }
                attachmentDialog.setArguments(bundle);
                attachmentDialog.show(ft, "");
            }, this);
            if (mPunchlistDb.getPunchlistId() != null && mPunchlistDb.getPunchlistId() != 0) {
                emailSubmitImageView.setVisibility(View.INVISIBLE);
                emailSubmitImageView.setClickable(true);

            } else {
                emailSubmitImageView.setVisibility(View.INVISIBLE);
                emailSubmitImageView.setClickable(false);

            }
            isOffline(!NetworkService.isNetworkAvailable(getActivity()));
            recyclerView.setAdapter(adapterAttachment);
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), RecyclerView.HORIZONTAL, false));
            if (userPermissions.getEditPunchList() != 1) {
                saveTextView.setVisibility(View.GONE);
                saveSendTextView.setVisibility(View.GONE);
                cancelTextView.setVisibility(View.GONE);
                statusSpinnewView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
//                assignedToSpinnewView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                dateDueShowView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                tradespinnewView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                punchListItemDescriptionView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                locationEditText.setKeyListener(null);
                locationEditText.setFocusableInTouchMode(false);

                commentsEdit.setKeyListener(null);
                commentsEdit.setFocusableInTouchMode(false);
                punchListItemDescriptionEditText.setFocusableInTouchMode(false);
                punchListItemDescriptionEditText.setKeyListener(null);
                statusSpinner.setClickable(false);
//                assignedToSpinnewView.setClickable(false);
                statusSpinner.setEnabled(false);
//                assignedToSpinnewView.setEnabled(false);
                assigneeLayout.setClickable(false);
                assigneeCCLayout.setClickable(false);
                assigneeChipsInput.setEnabled(false);
                assigneeChipsInput.setClickable(false);
//                assigneeLayout.setBackground();
                dateDueShowView.setEnabled(false);
                if (attachmentList.size() > 0 && attachmentList.get(0) == null) {
                    attachmentList.remove(0);
                }
                punchListItemDescriptionEditText.setClickable(false);
            }
            punchListLinkedDrawingList.setText("");
            List<PunchlistDrawing> punchlistDrawings = mPunchListRepository.getPunchListDrawings(mPunchlistDb.getPunchlistIdMobile());
            SpannableStringBuilder result = new SpannableStringBuilder();
            for (int i = 0; i <
                    punchlistDrawings.size(); i++) {
                PunchlistDrawing linkedDrawing = punchlistDrawings.get(i);
                if (i != punchlistDrawings.size() - 1) {
                    result.append(linkedDrawing.getDrawingName() + ", ");

                } else {

                    result.append(linkedDrawing.getDrawingName() + "  ");
                }

                result.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View view) {
                        Log.i("Testclick", linkedDrawing.getOriginalDrwId() + "  onClick: " + linkedDrawing.getDrawingName());
                        if (loginResponse.getUserDetails().getPermissions().get(0).getViewDrawings() == 1) {
                            DrawingList headerDrawingList = new DrawingList(-1, "All");
                            List<DrawingList> drawingLists = mProjectDrawingListProvider.getSearchDrawing(linkedDrawing.getDrwFoldersId(), "", headerDrawingList);
                            if (drawingLists.size() > 0) {
                                DrawingList originalDrawing = mDrawingListRepository.getDrawing(linkedDrawing.getDrwFoldersId(), linkedDrawing.getOriginalDrwId(), linkedDrawing.getRevisitedNum());
                                if (originalDrawing != null && originalDrawing.getPdfStatus() == PDFSynEnum.SYNC.ordinal()) {
                                    startActivity(new Intent(getActivity(), DrawingPDFActivity.class).
                                            putExtra("drawing_name", originalDrawing.getDrawingName()).
                                            putExtra("drawing_folder_id", originalDrawing.getDrwFoldersId()).
                                            putExtra("drawing_id", originalDrawing.getId()).
                                            putExtra("projectId", projectId).
                                            putExtra("drawing_rev_no", originalDrawing.getRevisitedNum()));

                                } else if (originalDrawing != null) {
                                    getPDFFile(originalDrawing, linkedDrawing.getDrwFoldersId(), linkedDrawing);
                                }

                            } else {
                                getPDFFile(null, linkedDrawing.getDrwFoldersId(), linkedDrawing);

                            }
                        }
                    }
                }, (result.length() - 2) - linkedDrawing.getDrawingName().length(), (result.length() - 2), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            }
            punchListLinkedDrawingList.setMovementMethod(LinkMovementMethod.getInstance());
            punchListLinkedDrawingList.setText(result, TextView.BufferType.SPANNABLE);
            if (punchlistDrawings.size() == 0) {
                punchListLinkedDrawingList.setText("-");
            } else if (loginResponse.getUserDetails().getPermissions().get(0).getViewDrawings() == 1) {
                punchListLinkedDrawingList.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            }

            if (canDeletePunchList == 1) {
                deleteImageView.setVisibility(View.VISIBLE);
            }

            if (loginResponse.getUserDetails().getPermissions().get(0).getViewDrawings() != 1) {
                /*punchListLinkedDrawingList.setVisibility(View.GONE);
                punchListLinkedDrawing.setVisibility(View.GONE);*/
                String temp = punchListLinkedDrawingList.getText().toString();
                punchListLinkedDrawingList.setText(temp);
            }
        }

    }

    @OnClick(R.id.deleteImageView)
    public void onDeletePunchlist() {

        if (attachmentList.size() > 0 && attachmentList.get(0) == null) {
            attachmentList.remove(0);
        }
        attachmentList.addAll(removedPunchlistAttachments);
        for (PunchListAttachments attachment : attachmentList) {
            attachment.setDeletedAt(null);
        }


        if (drawingView) {

            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
//                    alertDialog.setTitle(getString(R.string.message));
            alertDialog.setMessage(getString(R.string.are_you_sure_you_want_to_remove_this_punch_list_markup_from_the_drawing));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {

                /**
                 * Call deleteStickyAnnot() in EditAnnot class From here.
                 */
                mPunchListRepository.deleteDrawingPunchlist(projectId, mPunchlistDb.getPunchlistId(), mPunchlistDb.getPunchlistIdMobile(), loginResponse.getUserDetails().getUsers_id());
                DrawingList drawingList = getArguments().getParcelable("drawing_details");
                mPunchListRepository.deletePunchlistDrawing(mPunchlistDb, drawingList);

                AnnotDeleteAction deleteAnnot = new AnnotDeleteAction();
                deleteAnnot.setAction("AnnotAction");
                EventBus.getDefault().post(deleteAnnot);
                dialog.dismiss();
                getActivity().onBackPressed();
            });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> {
                dialog.dismiss();
            });
            alertDialog.setCancelable(false);
            alertDialog.show();
            Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(ContextCompat.getColor(getActivity(), R.color.gray_948d8d));
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

        } else {

            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
//                    alertDialog.setTitle(getString(R.string.message));
            alertDialog.setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_entry));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
                mPunchlistDb.setDeletedAt(new Date());
                mPunchlistDb.setIsSync(false);
                EventBus.getDefault().post(mPunchlistDb);
                dialog.dismiss();
                getActivity().onBackPressed();

            });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> {
                dialog.dismiss();
            });
            alertDialog.setCancelable(false);
            alertDialog.show();
            Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(ContextCompat.getColor(getActivity(), R.color.gray_948d8d));
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));


        }
    }

    private void getPDFFile(DrawingList originalDrawing, Integer drwFoldersId, PunchlistDrawing linkedDrawing) {
        if (!mNetworkStateProvider.isOffline()) {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setMessage("Would you like to sync and navigate to drawing " + linkedDrawing.getDrawingName() + "?");
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.no), (dialog, which) -> {
                dialog.dismiss();
            });
            alertDialog.setCancelable(false);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.yes), (dialog, which) -> {

                CustomProgressBar.showDialog(getActivity());
                if (originalDrawing != null && originalDrawing.getPdfStatus() != PDFSynEnum.PROCESSING.ordinal()) {
                    if (!mNetworkStateProvider.isOffline()) {
                        onDownloadPDF(originalDrawing);
                    }
                } else {

                    if (mProjectDrawingProvider.getDrawingFolder(projectId, drwFoldersId) == null) {
                        callProjectDrawingFolderRequest(drwFoldersId, linkedDrawing);
                    } else {
                        callDrawingListAPI(drwFoldersId, linkedDrawing);
                    }
                }

            });
            alertDialog.show();
            Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(ContextCompat.getColor(getActivity(), R.color.gray_948d8d));
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        } else if (originalDrawing.getCurrentRevision() == 0 && originalDrawing.getIsSync() == true) {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
            alertDialog.setMessage("This drawing has recently been modified. Please restore network connection to download latest version to view.");
            alertDialog.setCancelable(false);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
                dialog.dismiss();
            });
            alertDialog.show();
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
            alertDialog.setMessage("Drawing " + linkedDrawing.getDrawingName() + " is not synced to your device. You must be online to sync drawings.");
            alertDialog.setCancelable(false);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
                dialog.dismiss();
            });
            alertDialog.show();
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

        }
    }

    //TODO: Nitin
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ObjectEvent event) {
        if (event.getObject() != null) {
            if (event.getObjectType() == ObjectEnum.ASSIGNEE.ordinal()) {
                mPunchlistAssignee = (PunchlistAssignee) event.getObject();
//                assigneeListNameTextView.setText(mPunchlistAssignee.getName());


            }
        }
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveTextView:
                saveButtonClicked = true;
                if (assigneeChipsInput.getSelectedChipList().size() == 0 && TextUtils.isEmpty(punchListItemDescriptionEditText.getText().toString().trim())) {
                    descriptionErrorTextView.setText(getString(R.string.enter_description));
                    assigneeErrorTextView.setText(getString(R.string.select_assignee));
                } else if (assigneeChipsInput.getSelectedChipList().size() == 0) {
                    assigneeErrorTextView.setText(getString(R.string.select_assignee));
                } else if (TextUtils.isEmpty(punchListItemDescriptionEditText.getText().toString().trim())) {
                    descriptionErrorTextView.setText(getString(R.string.enter_description));
                } else {
                    if (attachmentList.size() > 0 && attachmentList.get(0) == null) {
                        attachmentList.remove(0);
                    }
                    attachmentList.addAll(removedPunchlistAttachments);

                    if (mPunchlistDb == null) {
                        PunchlistDb punchlistDb = mPunchListRepository.addPunchListDb(projectId, mPunchListStatus, mPunchlistAssignee,
                                getSelectedToAssignee()/*mFilteredSelectedAssignedLists*/, getSelectedCCAssignee() /*mFilteredSelectedAssignedCCLists*/, attachmentList, dueDate,
                                locationEditText.getText().toString().trim(), punchListItemDescriptionEditText.getText().toString().trim(), linkExisting, 0, commentsEdit.getText().toString().trim());
                        if (punchlistDb != null) {
                            EventBus.getDefault().post(punchlistDb);
                        }
                        getActivity().onBackPressed();
                    } else {
                        if (attachmentList.size() > 0 && attachmentList.get(0) == null) {
                            attachmentList.remove(0);
                        }
//                        attachmentList.addAll(removedPunchlistAttachments);

                        boolean attachmentSync = true;
                        for (PunchListAttachments attachment : attachmentList) {
                            if (!attachment.getIsAwsSync() || attachment.getDeletedAt() != null) {
                                attachmentSync = false;
                            }
                        }
                        PunchlistDb punchListDetail = mPunchListRepository.getPunchListDetail(punchListMobileId);
                        if (punchListDetail.getStatus() != mPunchListStatus.getValue() ||
//                                !punchListDetail.getAssignedTo().equals(String.valueOf(mPunchlistAssignee.getUsersId())) ||  TODO: Nitin
                                !punchListDetail.getAssigneeName().equals(String.valueOf(mPunchlistAssignee.getName())) ||
                                !checkDueDate(punchListDetail) ||
                                !punchListDetail.getLocation().equals(locationEditText.getText().toString().trim()) ||
                                !punchListDetail.getDescriptions().equals(punchListItemDescriptionEditText.getText().toString().trim()) ||
                                !attachmentSync) {
                            mPunchlistDb = mPunchListRepository.getPunchListDetail(punchListMobileId);

                            /*PunchlistDb punchlistDb = mPunchListRepository.updatePunchListDb(projectId, mPunchListStatus, mPunchlistAssignee, attachmentList, dueDate,
                                    locationEditText.getText().toString().trim(), punchListItemDescriptionEditText.getText().toString().trim(),
                                    mPunchlistDb.getPunchlistId(), punchListDetail.getPunchlistIdMobile(), punchListDetail.getItemNumber(), attachmentSync,
                                    punchListDetail.getCreatedAt(), punchListDetail.getDateCreated(), punchListDetail.getCreatedByUserId(), punchListDetail.getCreatedBy(), 0,
                                    getSelectedToAssignee(), getSelectedCCAssignee(), commentsEdit.getText().toString().trim());*/
                            PunchlistDb punchlistDb = mPunchListRepository.updatePunchListInDBForSaveAndSend(projectId, mPunchListStatus, mPunchlistAssignee, attachmentList, dueDate,
                                    locationEditText.getText().toString().trim(), punchListItemDescriptionEditText.getText().toString().trim(),
                                    mPunchlistDb.getPunchlistId(), punchListDetail.getPunchlistIdMobile(), punchListDetail.getItemNumber(), attachmentSync,
                                    punchListDetail.getCreatedAt(), punchListDetail.getDateCreated(), punchListDetail.getCreatedByUserId(), punchListDetail.getCreatedBy(), 0,
                                    getSelectedToAssignee(), getSelectedCCAssignee(), commentsEdit.getText().toString().trim());
                            if (punchlistDb != null) {
                                EventBus.getDefault().post(punchlistDb);
                            }
/*

                            String str = "punch_id = " + punchlistDb.getPunchlistId() +
                                    ", punch_id_mobile = " + punchlistDb.getPunchlistIdMobile() +
                                    ", punch_status = " + mPunchListStatus.getValue() +
                                    ", punch_number = " + punchlistDb.getItemNumber() +
                                    ", title = " + loginResponse.getUserDetails().getFirstname() + " " + loginResponse.getUserDetails().getLastname();
*/

                            AnnotAction annotAction = new AnnotAction();
                            annotAction.setContent(mPunchListStatus.getValue() + "");
                            annotAction.setAction("UpdateAnnot");
                            EventBus.getDefault().post(annotAction);
                            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                            getActivity().onBackPressed();
                            //                            dismiss();
                        } else {


                            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                            getActivity().onBackPressed();//                            dismiss();

                        }
                    }
                }
                break;
            case R.id.cancelTextView:
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                if (attachmentList.size() > 0 && attachmentList.get(0) == null) {
                    attachmentList.remove(0);
                }
                attachmentList.addAll(removedPunchlistAttachments);
                if (mPunchlistDb == null) {

                    for (PunchListAttachments attachment : attachmentList) {
                        attachment.setDeletedAt(null);
                    }
                    AnnotAction annotAction = new AnnotAction();
                    annotAction.setAction("DismissAnnot");
                    EventBus.getDefault().post(annotAction);
                    getActivity().onBackPressed();
                } else {
                    onBackPress();
                }
                break;
            case R.id.deleteTextView:

                if (attachmentList.size() > 0 && attachmentList.get(0) == null) {
                    attachmentList.remove(0);
                }
                attachmentList.addAll(removedPunchlistAttachments);
                for (PunchListAttachments attachment : attachmentList) {
                    attachment.setDeletedAt(null);
                }


                if (drawingView) {

                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
//                    alertDialog.setTitle(getString(R.string.message));
                    alertDialog.setMessage(getString(R.string.are_you_sure_you_want_to_remove_this_punch_list_markup_from_the_drawing));
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {

                        /**
                         * Call deleteStickyAnnot() in EditAnnot class From here.
                         */
                        mPunchListRepository.deleteDrawingPunchlist(projectId, mPunchlistDb.getPunchlistId(), mPunchlistDb.getPunchlistIdMobile(), loginResponse.getUserDetails().getUsers_id());
                        AnnotDeleteAction deleteAnnot = new AnnotDeleteAction();
                        deleteAnnot.setAction("AnnotAction");
                        EventBus.getDefault().post(deleteAnnot);
                        dialog.dismiss();
                        getActivity().onBackPressed();
                    });
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> {
                        dialog.dismiss();
                    });
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    nbutton.setTextColor(ContextCompat.getColor(getActivity(), R.color.gray_948d8d));
                    Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

                } else {

                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
//                    alertDialog.setTitle(getString(R.string.message));
                    alertDialog.setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_entry));
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
                        mPunchlistDb.setDeletedAt(new Date());
                        mPunchlistDb.setIsSync(false);
                        EventBus.getDefault().post(mPunchlistDb);
                        dialog.dismiss();
                        getActivity().onBackPressed();

                    });
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> {
                        dialog.dismiss();
                    });
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    nbutton.setTextColor(ContextCompat.getColor(getActivity(), R.color.gray_948d8d));
                    Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));


                }
                break;
            default:
                break;
        }
    }

    private boolean checkDueDate(PunchlistDb punchListDetail) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = sdf.format(dueDate);
        String dueDate = sdf.format(punchListDetail.getDateDue());
        return strDate.equals(dueDate);
    }

    private void callAssigneeAPI() {
        AssigneeRequest assigneeRequest = new AssigneeRequest();
        assigneeRequest.setProjectId(projectId);

        mFieldPaperWorkProvider.getAssignee(assigneeRequest, new ProviderResult<List<PunchlistAssignee>>() {
            @Override
            public void success(List<PunchlistAssignee> result) {
                mPunchlistAssignees = new ArrayList<>();
                if (mPunchlistDb == null) {
                    mPunchlistAssignees.add(null);
                }
                mPunchlistAssignees.addAll(result);
                Log.d("Nitin", "success: "+ mPunchlistAssignees.size());
                addAssignee(mPunchlistAssignees);
                if(mPunchlistDb != null)
                    setAssigneeInEditMode();

                try {
//                    tradeAdapter = new TradeAdapter(activity, R.layout.simple_spinner_item, mPunchlistAssignees);
//                    tradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
               /*     assignedToSpinner.setAdapter(tradeAdapter);

                    assignedToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                            mPunchlistAssignee = mPunchlistAssignees.get(pos);
                            if (pos > 0) {
                                assigneeErrorTextView.setText("");
                                selectTextView.setVisibility(View.GONE);
                            }
                        }

                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });*/
                    if (mPunchlistDb != null) {
                        assigneeSpinnerSelection();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(getActivity()).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(getActivity()).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
//                dismiss();
                getActivity().finish();
            }

            @Override
            public void failure(String message) {
                try {
                    messageDialog.showMessageAlert(getContext(), message, getString(R.string.ok));
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick(R.id.saveSendTextView)
    public void onEmailClick() {
        isSaveAndSendClicked = true;
        fieldValidation();
    }

    public void fieldValidation() {
        saveButtonClicked = true;
        if (assigneeChipsInput.getSelectedChipList().size() == 0 && TextUtils.isEmpty(punchListItemDescriptionEditText.getText().toString().trim())) {
            descriptionErrorTextView.setText(getString(R.string.enter_description));
            assigneeErrorTextView.setText(getString(R.string.select_assignee));
        } else if (assigneeChipsInput.getSelectedChipList().size() == 0) {
            assigneeErrorTextView.setText(getString(R.string.select_assignee));
        } else if (TextUtils.isEmpty(punchListItemDescriptionEditText.getText().toString().trim())) {
            descriptionErrorTextView.setText(getString(R.string.enter_description));
        } else {
            if (attachmentList.size() > 0 && attachmentList.get(0) == null) {
                attachmentList.remove(0);
            }
            attachmentList.addAll(removedPunchlistAttachments);

            if (mPunchlistDb == null) {
                PunchlistDb punchlistDb = mPunchListRepository.addPunchListDb(projectId, mPunchListStatus, mPunchlistAssignee,
                        getSelectedToAssignee()/*mFilteredSelectedAssignedLists*/, getSelectedCCAssignee()/*mFilteredSelectedAssignedCCLists*/, attachmentList, dueDate,
                        locationEditText.getText().toString().trim(), punchListItemDescriptionEditText.getText().toString().trim(),
                        linkExisting, 1, commentsEdit.getText().toString().trim());
                if (punchlistDb != null) {
                    EventBus.getDefault().post(punchlistDb);
                }
                getActivity().onBackPressed();
            } else {
                if (attachmentList.size() > 0 && attachmentList.get(0) == null) {
                    attachmentList.remove(0);
                }
//                        attachmentList.addAll(removedPunchlistAttachments);

                boolean attachmentSync = true;
                for (PunchListAttachments attachment : attachmentList) {
                    if (!attachment.getIsAwsSync() || attachment.getDeletedAt() != null) {
                        attachmentSync = false;
                    }
                }
                PunchlistDb punchListDetail = mPunchListRepository.getPunchListDetail(punchListMobileId);
              /*  if (punchListDetail.getStatus() != mPunchListStatus.getValue() ||
                        !punchListDetail.getAssignedTo().equals(String.valueOf(mPunchlistAssignee.getUsersId())) ||
                        !punchListDetail.getAssigneeName().equals(String.valueOf(mPunchlistAssignee.getName())) ||
                        !checkDueDate(punchListDetail) ||
                        !punchListDetail.getLocation().equals(locationEditText.getText().toString().trim()) ||
                        !punchListDetail.getDescriptions().equals(punchListItemDescriptionEditText.getText().toString().trim()) ||
                        !attachmentSync) {*/
                mPunchlistDb = mPunchListRepository.getPunchListDetail(punchListMobileId);

                PunchlistDb punchlistDb = new PunchlistDb();

                if(!isSaveAndSendClicked){
                    punchlistDb = mPunchListRepository.updatePunchListDb(projectId, mPunchListStatus, mPunchlistAssignee, attachmentList, dueDate,
                            locationEditText.getText().toString().trim(), punchListItemDescriptionEditText.getText().toString().trim(),
                            mPunchlistDb.getPunchlistId(), punchListDetail.getPunchlistIdMobile(), punchListDetail.getItemNumber(), attachmentSync,
                            punchListDetail.getCreatedAt(), punchListDetail.getDateCreated(), punchListDetail.getCreatedByUserId(), punchListDetail.getCreatedBy(), 1,
                            getSelectedToAssignee(), getSelectedCCAssignee(), commentsEdit.getText().toString().trim());
                } else {
                    punchlistDb = mPunchListRepository.updatePunchListInDBForSaveAndSend(projectId, mPunchListStatus, mPunchlistAssignee, attachmentList, dueDate,
                            locationEditText.getText().toString().trim(), punchListItemDescriptionEditText.getText().toString().trim(),
                            mPunchlistDb.getPunchlistId(), punchListDetail.getPunchlistIdMobile(), punchListDetail.getItemNumber(), attachmentSync,
                            punchListDetail.getCreatedAt(), punchListDetail.getDateCreated(), punchListDetail.getCreatedByUserId(), punchListDetail.getCreatedBy(), 1,
                            getSelectedToAssignee(), getSelectedCCAssignee(), commentsEdit.getText().toString().trim());
                    isSaveAndSendClicked = false;
                }

                if (punchlistDb != null) {
                    EventBus.getDefault().post(punchlistDb);
                }
/*

                            String str = "punch_id = " + punchlistDb.getPunchlistId() +
                                    ", punch_id_mobile = " + punchlistDb.getPunchlistIdMobile() +
                                    ", punch_status = " + mPunchListStatus.getValue() +
                                    ", punch_number = " + punchlistDb.getItemNumber() +
                                    ", title = " + loginResponse.getUserDetails().getFirstname() + " " + loginResponse.getUserDetails().getLastname();
*/

                AnnotAction annotAction = new AnnotAction();
                annotAction.setContent(mPunchListStatus.getValue() + "");
                annotAction.setAction("UpdateAnnot");
                EventBus.getDefault().post(annotAction);
                AnnotUpdateAction annotUpdateAction = new AnnotUpdateAction();
                annotUpdateAction.setStatus(mPunchListStatus.getValue() + "");
                annotUpdateAction.setAction("UpdateAnnot");
                EventBus.getDefault().post(annotUpdateAction);

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                getActivity().onBackPressed();
                //                            dismiss();
             /*   } else {


                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    getActivity().onBackPressed();//                            dismiss();

                }*/
            }
        }
    }

    @OnClick(R.id.dateDueShowView)
    public void onDateClick() {
        Calendar calendar1 = new GregorianCalendar();
        calendar1.setTime(dueDate);
        int mYear = calendar1.get(Calendar.YEAR);
        int mMonth = calendar1.get(Calendar.MONTH);
        int mDay = calendar1.get(Calendar.DATE);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                (view, year, monthOfYear, dayOfMonth) -> {

                    Calendar calendar = new GregorianCalendar(year,
                            monthOfYear,
                            dayOfMonth);
                    dueDate = calendar.getTime();
                    dateTextView.setText(DateFormatter.formatDateForPunchList(dueDate));
                }, mYear, mMonth, mDay);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis() - 1000);
        datePickerDialog.show();
    }

    //    @OnClick(R.id.addTextView)
    public void onClickAddButton() {
        int numberOfItems = 0;
        for (PunchListAttachments attachments : attachmentList) {
            if (attachments != null && attachments.getDeletedAt() == null)
                numberOfItems++;
        }
        if (numberOfItems < 11) {
            selectImage();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setMessage(R.string.max_attachement_message_punch_list)
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.dismiss());
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @OnClick(R.id.linkExistingTextView)
    public void onClicklinkExistingTextView() {
        saveButtonClicked = true;
        DrawingList drawingList = getArguments().getParcelable("drawing_details");

        getActivity().startActivityForResult(new Intent(getActivity(), PunchListActivity.class).putExtra(IntentExtra.PROJECT_ID.name(), projectId).putExtra(IntentExtra.PROJECT_NAME.name(), "").putExtra("linkExisting", linkExisting).putExtra("drawing_details", drawingList), LINK_EXIST);
        getActivity().onBackPressed();
    }

    @OnClick(R.id.leftImageView)
    public void onClickCancelView() {
        if (mPunchlistDb == null) {
            for (PunchListAttachments attachment : attachmentList) {
                if (attachment != null) {
                    attachment.setDeletedAt(null);
                }
            }
            getActivity().onBackPressed();
        } else {
            onBackPress();
        }
    }

    public void activityOnBackPress() {
        if (!saveButtonClicked) {
            AnnotAction annotAction = new AnnotAction();
            annotAction.setAction("DismissAnnot");
            EventBus.getDefault().post(annotAction);
        }
    }

    private void onBackPress() {
        {
            boolean attachmentSync = true;
            if (attachmentList.size() > 0 && attachmentList.get(0) == null) {
                attachmentList.remove(0);
            }
            if (removedPunchlistAttachments.size() > 0) {
                attachmentSync = false;
            }
            if (newAddAttachmentList.size() > 0) {
                attachmentSync = false;
            }
            PunchlistDb punchListDetail = mPunchListRepository.getPunchListDetail(punchListMobileId);
            if (punchListDetail != null && (punchListDetail.getStatus() != mPunchListStatus.getValue() ||
                    punchListDetail.getAssignedTo().size() < assigneeChipsInput.getSelectedChipList().size() || //TODO: Nitin
//                    punchListDetail.getAssignedToList().size() > 0 ||
                    punchListDetail.getAssignedCcList().size() < assigneeCCChipsInput.getSelectedChipList().size() ||
                    /*!punchListDetail.getAssigneeName().get(0).equals(String.valueOf(mPunchlistAssignee.getName())) ||*/
                    !checkDueDate(punchListDetail) ||
                    !punchListDetail.getLocation().equals(locationEditText.getText().toString().trim()) ||
                    !punchListDetail.getDescriptions().equals(punchListItemDescriptionEditText.getText().toString().trim()) ||
                    !attachmentSync)) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setMessage("Are you sure you want to exit without saving?");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getActivity().getString(R.string.ok), (dialog, which) -> {
                    attachmentList.addAll(removedPunchlistAttachments);
                    attachmentList.removeAll(newAddAttachmentList);
                    for (PunchListAttachments attachment : attachmentList) {
                        attachment.setDeletedAt(null);
                    }
                    dialog.dismiss();
                    getActivity().onBackPressed();
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getActivity().getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
                alertDialog.setCancelable(false);
                alertDialog.show();
                Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(ContextCompat.getColor(getActivity(), R.color.gray_948d8d));
                Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            } else {
                getActivity().onBackPressed();
            }
        }
    }

    private void selectImage() {
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.choose_from_library)};
        TextView title = new TextView(getContext());
        title.setText(R.string.add_photo);
        title.setBackgroundColor(Color.BLACK);
        title.setPadding(10, 15, 15, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(22);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals(getString(R.string.take_photo))) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    getActivity().requestPermissions(new String[]{
                            Manifest.permission.CAMERA,
                            getExternalPermission()}, FILECAMERA_REQUEST_CODE);
                } else {
                    openCamera();
                }
            } else if (items[item].equals(getString(R.string.choose_from_library))) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        checkSelfPermission(getActivity(), getExternalPermission()) != PackageManager.PERMISSION_GRANTED) {
                    getActivity().requestPermissions(new String[]{getExternalPermission()}, PERMISSION_READ_REQUEST_CODE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), SELECT_PICTURE);
                }


            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == FILECAMERA_REQUEST_CODE) {
            openCamera();
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == PERMISSION_READ_REQUEST_CODE) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), SELECT_PICTURE);
        }
    }

    public void openCamera() {
        Intent captureIntent = new Intent(getActivity(), PronovosCameraActivity.class).putExtra("totalImageCount", 1);
        startActivityForResult(captureIntent, CAPTURE_IMAGE);
    }

    ActivityResultLauncher<Intent> cameraResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result != null) {
                        PunchListAttachments punchListAttachment = new PunchListAttachments();
                        punchListAttachment.setAttachmentPath(result.getData().getStringExtra("image_path"));
                        punchListAttachment.setIsAwsSync(false);
                        punchListAttachment.setPunchListIdMobile(punchListMobileId);
                        punchListAttachment.setUsersId(loginResponse.getUserDetails().getUsers_id());
                        punchListAttachment.setPunchListId(0);
//                    punchListAttachment.setAttachmentIdMobile(0L);
                        newAddAttachmentList.add(punchListAttachment);
                        attachmentList.add(punchListAttachment);
                        Log.i("Punchlist", "onActivityResult: " + attachmentList.size());
                        if (attachmentList.size() >= 11 && attachmentList.get(0) == null) {
                            attachmentList.remove(0);
                        }
                        adapterAttachment.notifyDataSetChanged();
                    }
                }
            });

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAPTURE_IMAGE:
                if (data != null) {
                    PunchListAttachments punchListAttachment = new PunchListAttachments();
                    punchListAttachment.setAttachmentPath(data.getStringExtra("image_path"));
                    punchListAttachment.setIsAwsSync(false);
                    punchListAttachment.setPunchListIdMobile(punchListMobileId);
                    punchListAttachment.setUsersId(loginResponse.getUserDetails().getUsers_id());
                    punchListAttachment.setPunchListId(0);
//                    punchListAttachment.setAttachmentIdMobile(0L);
                    newAddAttachmentList.add(punchListAttachment);
                    attachmentList.add(punchListAttachment);
                    Log.i("Punchlist", "onActivityResult: " + attachmentList.size());
                    if (attachmentList.size() >= 11 && attachmentList.get(0) == null) {
                        attachmentList.remove(0);
                    }
                    adapterAttachment.notifyDataSetChanged();
                }
                break;
            case SELECT_PICTURE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        String dateString = null;
                        Log.d("onActivityResult", "Select Pic");
                        try {
                            String[] filePath = {MediaStore.Images.Media.DATA};
                            Uri selectedImage = data.getData();
                            Cursor c = getActivity().getContentResolver().query(
                                    selectedImage, filePath, null, null, null);
                            c.moveToFirst();
                            int columnIndex = c.getColumnIndex(filePath[0]);
                            String picturePath = c.getString(columnIndex);
                            c.close();

                            File output = FileUtils.getOutputGalleryMediaFile(1, getContext());
                            Bitmap thumbnail1 =
                                    (BitmapFactory.decodeFile(picturePath));
                            Bitmap thumbnail = getRotateBitmap(picturePath, thumbnail1);
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

                                    Intent captureIntent = new Intent(getActivity(), PronovosCameraActivity.class).putExtra("totalImageCount", 1).putExtra("file_location", output.getPath());
                                    startActivityForResult(captureIntent, CAPTURE_IMAGE);

                                } catch (URISyntaxException e) {
                                    e.printStackTrace();
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }


                break;
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    Log.d("onActivityResult", "Take Pic");
//                    previewCapturedImage();
                    InputStream iStream = null;
                    try {
                        iStream = getContext().getContentResolver().openInputStream(fileUri);
                        byte[] jpeg = FileUtils.getBytes(iStream);

                        File outputFile = FileUtils.getOutputMediaFile(1);


                        CameraUtils.decodeBitmap(jpeg, 1000, 1000, new CameraUtils.BitmapCallback() {
                            @Override
                            public void onBitmapReady(Bitmap bitmap) {
                                try {
                                    saveImage(bitmap, outputFile);

                                    PunchListAttachments punchListAttachments = new PunchListAttachments();
                                    punchListAttachments.setAttachmentPath(outputFile.getPath());
                                    punchListAttachments.setAttachmentId(0);
                                    punchListAttachments.setIsAwsSync(false);
                                    punchListAttachments.setPunchListIdMobile(punchListMobileId);
                                    punchListAttachments.setUsersId(loginResponse.getUserDetails().getUsers_id());
//                                    punchListAttachments.setAttachmentIdMobile(0L);
                                    newAddAttachmentList.add(punchListAttachments);
                                    attachmentList.add(punchListAttachments);
                                    Log.i("Punchlist", "onActivityResult: " + attachmentList.size());
                                    if (attachmentList.size() >= 11 && attachmentList.get(0) == null) {
                                        attachmentList.remove(0);
                                    }
                                    adapterAttachment.notifyDataSetChanged();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                }
        }

    }

    private void assigneeSpinnerSelection() {
        for (int i = 0; i < mPunchlistAssignees.size(); i++) {
            PunchlistAssignee punchlistAssignee = mPunchlistAssignees.get(i);
            if (punchlistAssignee != null) {
                String assignedTo = mPunchlistDb.getAssignedTo().get(0);  //TODO:Nitin
                int assigneeId = punchlistAssignee.getUsersId();
                if (assignedTo.equals(String.valueOf(assigneeId))) {
                    mPunchlistAssignee = punchlistAssignee;
                }
            }
        }
//        assigneeListNameTextView.setText(mPunchlistAssignee.getName());
    }

   /* @OnClick(R.id.assignedToSpinnewView)
    public void clickAssignee() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ObjectDialog tagsDialog = new ObjectDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable("object", mPunchlistAssignee);
        bundle.putInt("pjProjectId", projectId);
        bundle.putInt("obj_type", ObjectEnum.ASSIGNEE.ordinal());
        tagsDialog.setCancelable(false);
        tagsDialog.setArguments(bundle);
        tagsDialog.show(ft, "");
    }*/

    private void statusSpinnerSelection() {
        for (int i = 0; i < statusList.size(); i++) {
            PunchListStatus listStatus = statusList.get(i);
            if (listStatus != null && mPunchlistDb != null && mPunchlistDb.getStatus() == listStatus.getValue()) {
                statusSpinner.setSelection(i);
            }
        }
    }

    private void saveImage(Bitmap bitmap, File output) throws IOException {
        FileOutputStream out = new FileOutputStream(output);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();
    }

    public void isOffline(Boolean event) {
        if (mPunchlistDb != null && mPunchlistDb.getPunchlistId() != null && mPunchlistDb.getPunchlistId() != 0) {

            if (event) {
                emailSubmitImageView.setVisibility(View.INVISIBLE);
                emailSubmitImageView.setClickable(false);
            } else {
                emailSubmitImageView.setClickable(true);
                emailSubmitImageView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onAddItemClick() {
        onClickAddButton();
    }

    @Override
    public void onDelete(int position) {
        PunchListAttachments attachments = attachmentList.get(position);
//        if (attachments.getIsAwsSync()) {
        if (newAddAttachmentList.contains(attachments)) {
            newAddAttachmentList.remove(attachments);
        } else {
            attachments.setIsAwsSync(true);
            attachments.setDeletedAt(new Date());
            removedPunchlistAttachments.add(attachments);
        }
//        }
        attachmentList.remove(position);
        if (attachmentList.size() < 11 && attachmentList.get(0) != null) {
            if (mPunchlistDb == null || userPermissions.getEditPunchList() == 1) {
                attachmentList.add(0, null);
            }
        }
        adapterAttachment.notifyDataSetChanged();
    }

    /**
     * download the pdf if there is any event from event bus
     *
     * @param drawingList
     */
    public void onDownloadPDF(DrawingList drawingList) {
        mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.PROCESSING.ordinal());
        LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        mPDFFileDownloadProvider.getDrawingPDF(drawingList, loginResponse.getUserDetails().getUsers_id(), new ProviderResult<Boolean>() {
            @Override
            public void success(Boolean result) {
                if (result) {
                    getDrawingAnnotation(drawingList);
                } else {
                    messageDialog.showMessageAlert(getContext(), "Drawing not available.", getString(R.string.ok));
                    mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.SYNC_FAILED.ordinal());
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(getActivity());
            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(getActivity());
            }
        });
    }

    /**
     * Get the annotation of the drawing from the server
     *
     * @param drawingList
     */
    private void getDrawingAnnotation(DrawingList drawingList) {
        LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(getActivity()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        mAnnotationProvider.getDrawingAnnotations(drawingList.getRevisitedNum(), drawingList.getDrwFoldersId(), drawingList.getDrawingsId(), new ProviderResult<String>() {
            @Override
            public void success(String result) {

                try {
                    URL url = null;

                    if (drawingList.getPdfOrg() != null && !TextUtils.isEmpty(drawingList.getPdfOrg())) {
                        url = new URL(drawingList.getPdfOrg());
                    } else {
                        url = new URL(drawingList.getImageOrg());
                    }

                    String[] segments = url.getPath().split("/");
                    String fileName = segments[segments.length - 1];

                    if (isFileExist(loginResponse.getUserDetails().getUsers_id() + fileName) && result != null) {
                        mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.SYNC.ordinal());
                        startActivity(new Intent(getActivity(), DrawingPDFActivity.class).
                                putExtra("drawing_name", drawingList.getDrawingName()).
                                putExtra("drawing_folder_id", drawingList.getDrwFoldersId()).
                                putExtra("drawing_id", drawingList.getId()).
                                putExtra("projectId", projectId).
                                putExtra("drawing_rev_no", drawingList.getRevisitedNum()));

                    }
                    CustomProgressBar.dissMissDialog(getActivity());
                } catch (MalformedURLException m) {
                    CustomProgressBar.dissMissDialog(getActivity());
                    m.printStackTrace();
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(getActivity());
            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(getActivity());
            }
        });
    }

    /**
     * Check that File is exist in storage or not.
     *
     * @param fileName
     * @return
     */
    private boolean isFileExist(String fileName) {
        String root = getActivity().getFilesDir().getAbsolutePath();
        File myDir = new File(root + "/Pronovos/PDF");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String fname = fileName;
        File file = new File(myDir, fname);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    private void callProjectDrawingFolderRequest(int drwFolderId, PunchlistDrawing linkedDrawing) {
        DrawingFolderRequest drawingFolderRequest = new DrawingFolderRequest();
        drawingFolderRequest.setProject_id(projectId);
        mProjectDrawingProvider.getDrawingFolderList(drawingFolderRequest, new ProviderResult<List<DrawingFolders>>() {
            @Override
            public void success(List<DrawingFolders> result) {
                callDrawingListAPI(drwFolderId, linkedDrawing);
            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(getActivity());
            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(getActivity());
            }
        });
    }

    /**
     * Get all the drawing from the server with respect to the folder id
     *
     * @param drwFolderId   id of the folder
     * @param linkedDrawing
     */
    public void callDrawingListAPI(int drwFolderId, PunchlistDrawing linkedDrawing) {

        DrawingListRequest drawingListRequest = new DrawingListRequest();
        drawingListRequest.setFolder_id(drwFolderId);
        String lastUpdateStr = "1970-01-01 01:01:01";
       /* if (lastUpdate != null) {
            lastUpdateStr = DateFormatter.formatDateTimeForService(lastUpdate);

        }*/
        mProjectDrawingListProvider.getDrawingList(drawingListRequest, lastUpdateStr, projectId, DrawingAction.NON, new ProviderResult<List<DrawingList>>() {
            @Override
            public void success(List<DrawingList> result) {
//                DrawingList originalDrawing = mDrawingListRepository.getDrawing(linkedDrawing.getDrwFoldersId(), linkedDrawing.getOriginalDrwId());
                DrawingList originalDrawing = mDrawingListRepository.getDrawing(linkedDrawing.getDrwFoldersId(), linkedDrawing.getOriginalDrwId(), linkedDrawing.getRevisitedNum());
                if (originalDrawing != null) {
                    onDownloadPDF(originalDrawing);
                } else {
                    CustomProgressBar.dissMissDialog(getActivity());
                    messageDialog.showMessageAlert(getContext(), "Drawing not available.", getString(R.string.ok));
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                CustomProgressBar.dissMissDialog(getActivity());

            }

            @Override
            public void failure(String message) {
                CustomProgressBar.dissMissDialog(getActivity());
            }
        });
    }

    public Bitmap getRotateBitmap(String photoPath, Bitmap bitmap) {
        Bitmap rotatedBitmap = null;
        try {
            androidx.exifinterface.media.ExifInterface ei = null;
            ei = new androidx.exifinterface.media.ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
                    androidx.exifinterface.media.ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {

                case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap;
        }
        return rotatedBitmap != null ? rotatedBitmap : bitmap;
    }

    public static class ClickableString extends ClickableSpan {
        private final View.OnClickListener mListener;

        public ClickableString(View.OnClickListener listener) {
            mListener = listener;
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setData() {
        assigneeChipsInput.setChipDeletable(true);
        assigneeChipsInput.setChipHasAvatarIcon(false);
        assigneeChipsInput.setShowChipDetailed(false);


        assigneeCCChipsInput.setChipDeletable(true);
        assigneeCCChipsInput.setChipHasAvatarIcon(false);
        assigneeCCChipsInput.setShowChipDetailed(false);
    }

    public void setChip() {
        Log.d("King", "setChip: " + filterToList.size());
        assigneeChipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                Log.e("onChipAdded", "chip added, " + newSize);
                Log.e("onChipAdded", "chip added, " + chip.getId());

//                punchlistAssigneeLists.add((PunchlistAssigneeList) chip.getId());
                if (mFilteredSelectedAssignedLists == null) {
                    mFilteredSelectedAssignedLists = new ArrayList<>();
                }
                if (!mFilteredSelectedAssignedLists.contains((PunchlistAssigneeList) chip.getId())) {
                    mFilteredSelectedAssignedLists.add((PunchlistAssigneeList) chip.getId());
                }
                mSelectedAssigneeLists.add((PunchlistAssigneeList) chip.getId());
                assigneeErrorTextView.setText("");
                if (assigneeChipsInput.getSelectedChipList().size() > 1) {
                    assigneeChipsInput.requestEditFocus();
                }
//                assigneeChipsInput.requestEditFocus();

                PunchlistAssigneeList assigneeList = (PunchlistAssigneeList) chip.getId();
//                filterCCList.removeAll(mFilteredSelectedAssignedLists);
//                filterCCList.removeIf(item -> item.getUsersId() == assigneeList.getUsersId());
//                addAssigneeCC(filterCCList);
            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                Log.e("onChipRemoved", "chip removed, " + newSize);
                punchlistAssigneeLists.remove((PunchlistAssigneeList) chip.getId());
                assigneeChipsInput.requestEditFocus();
                mFilteredSelectedAssignedLists.remove((PunchlistAssigneeList) chip.getId());
//                filterCCList.add((PunchlistAssigneeList) chip.getId());

                PunchlistAssigneeList assigneeList = (PunchlistAssigneeList) chip.getId();
                if(!assigneeList.getActive())
                    filterToList.removeIf(item -> item.getUsersId() == assigneeList.getUsersId());

//                addAssigneeCC(filterCCList);
            }

            @Override
            public void onTextChanged(CharSequence text) {
                Log.e("onTextChanged", "text changed: " + text.toString());
            }
        });

        Log.d("nitin", "setChip: "+ punchlistAssigneeLists.size());
        assigneeChipsInput.setFilterableList(punchlistAssigneeLists);
    }

    private void addAssignee(List<PunchlistAssignee> mSelectedCclists) {

        Log.d("Nitin", "addAssignee Enter: " + mSelectedCclists.size() + " fdfa " + mPunchlistAssignees.size());
        List<String> defaultAssignee = new ArrayList<>();
        List<String> defaultCC = new ArrayList<>();
        for (int i = 0; i < mSelectedCclists.size(); i++) {
            Log.d("nitin", "addAssignee: " + mSelectedCclists.get(i));
            if (mSelectedCclists.get(i) != null) {
                PunchlistAssigneeList assigneeList = new PunchlistAssigneeList();
                assigneeList.setUserId(mSelectedCclists.get(i).getUserId());
                assigneeList.setName(mSelectedCclists.get(i).getName());
                assigneeList.setUsersId(mSelectedCclists.get(i).getUsersId());
                assigneeList.setPjProjectsId(mSelectedCclists.get(i).getPjProjectsId());
                assigneeList.setActive(mSelectedCclists.get(i).getActive());
                assigneeList.setDefaultAssignee(mSelectedCclists.get(i).getDefaultAssignee());
                assigneeList.setDefaultCC(mSelectedCclists.get(i).getDefaultCC());

                if(mPunchlistDb == null) {
                    if(mSelectedCclists.get(i).getDefaultAssignee()){
//                        defaultAssignee.add(String.valueOf(mSelectedCclists.get(i).getUsersId()));
                        mSelectedAssigneeLists.add(assigneeList);
                    }
                    if(mSelectedCclists.get(i).getDefaultCC()){
//                        defaultCC.add(String.valueOf(mSelectedCclists.get(i).getUsersId()));
                        mSelectedAssigneeCcLists.add(assigneeList);
                    }

                }
                punchlistAssigneeLists.add(assigneeList);
                Log.d("Nitin", "addAssignee Count: " + punchlistAssigneeLists.size());
            }
        }

        if(mPunchlistDb == null){
            setDefaultAssigneeAndCC();
        }

       /* filterCCList.clear();
        filterToList.clear();
        filterCCList.addAll(punchlistAssigneeLists) ;
        filterToList.addAll(punchlistAssigneeLists) ;
        addAssigneeTo(filterToList);
        addAssigneeCC(filterCCList);*/

        filterCCList = punchlistAssigneeLists ;
        filterToList = punchlistAssigneeLists ;
        addAssigneeTo(punchlistAssigneeLists);
        addAssigneeCC(punchlistAssigneeLists);
        Log.d("Nitin", "addAssignee End: ");
    }

    private void setAssigneeCCChip() {
        assigneeCCChipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                Log.e("onChipAdded", "chip added CC, " + newSize);
                Log.e("onChipAdded", "chip added CC, " + chip.getId());


                punchListAssigneeCCLists.add((PunchlistAssigneeList) chip.getId());
                if (mFilteredSelectedAssignedCCLists == null) {
                    mFilteredSelectedAssignedCCLists = new ArrayList<>();
                }
                if (mFilteredSelectedAssignedCCLists.contains((PunchlistAssigneeList) chip.getId())) {
                }
                mFilteredSelectedAssignedCCLists.add((PunchlistAssigneeList) chip.getId());

                punchlistAssigneeLists.remove((PunchlistAssigneeList) chip.getId());
                if (assigneeCCChipsInput.getSelectedChipList().size() > 1) {
                    assigneeCCChipsInput.requestEditFocus();
                }


//                filterToList.removeAll(mFilteredSelectedAssignedCCLists);
                PunchlistAssigneeList assigneeList = (PunchlistAssigneeList) chip.getId();
//                filterToList.removeIf(item -> item.getUsersId() == assigneeList.getUsersId());
                if(!assigneeList.getActive())
                    filterCCList.removeIf(item -> item.getUsersId() == assigneeList.getUsersId());

//                addAssigneeTo(filterToList);
            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                Log.e("onChipRemoved", "chip removed CC, " + newSize);
                punchListAssigneeCCLists.remove((PunchlistAssigneeList) chip.getId());
                assigneeCCChipsInput.requestEditFocus();
                mFilteredSelectedAssignedCCLists.remove((PunchlistAssigneeList) chip.getId());


                PunchlistAssigneeList removeAssignee = (PunchlistAssigneeList) chip.getId();

                /*if(removeAssignee.getActive()){
                    filterToList.add((PunchlistAssigneeList) chip.getId());
                }*/
//                addAssigneeTo(filterToList);
            }

            @Override
            public void onTextChanged(CharSequence text) {
                Log.e("onTextChanged", "text changed CC: " + text.toString());
            }
        });

        Log.d("nitin", "setChip CC: "+ punchListAssigneeCCLists.size());
        assigneeCCChipsInput.setFilterableList(punchListAssigneeCCLists);
    }

    private  void addAssigneeTo(List<PunchlistAssigneeList> addAssignedToList) {
        Log.d("Nitin", "addAssigneeTo Enter: "+ addAssignedToList.size() + " fdfa "+ mPunchlistAssignees.size());

        punchlistAssigneeLists = addAssignedToList;
        setChip();
        Log.d("Nitin", "addAssigneeTo End: ");
    }
    private void addAssigneeCC(List<PunchlistAssigneeList> addAssignedCCList){

        Log.d("Nitin", "addAssigneeCC Enter: "+ addAssignedCCList.size() + " fdfa "+ mPunchlistAssignees.size());

        punchListAssigneeCCLists = addAssignedCCList;
        setAssigneeCCChip();
        Log.d("Nitin", "addAssigneeCC End: ");
    }

    private void setAssigneeInEditMode(){
        mSelectedAssigneeLists = new ArrayList<>();
        /*for (String assigneeId : mPunchlistDb.getAssignedTo()) {
            if (!assigneeId.isEmpty()) {
                try {
                    Thread.sleep(2000);
                    PunchlistAssignee assignee = mFieldPaperWorkProvider.getAssignee(projectId, Integer.parseInt(assigneeId));
                    mSelectedAssigneeLists.add(getPunchlistAssigneeList(assignee));
                    if(!assignee.getActive()) {
                        punchlistAssigneeLists.add(getPunchlistAssigneeList(assignee));
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }*/
        for(String assigneeId : mPunchlistDb.getAssignedTo()){
            if(!assigneeId.isEmpty()){
                PunchlistAssignee assignee = mFieldPaperWorkProvider.getAssignee(projectId, Integer.parseInt(assigneeId));
                if(!assignee.getActive()){
                    punchlistAssigneeLists.add(getPunchlistAssigneeList(assignee));
                }
            }
        }

        int i = 0;
        for(PunchlistAssigneeList assigneeList : punchlistAssigneeLists){
            int j = 0;
            for(String assigneeId : mPunchlistDb.getAssignedTo()){
                if(assigneeList.getUsersId().toString().equals(assigneeId)){
                    mSelectedAssigneeLists.add(punchlistAssigneeLists.get(i));
                    j++;
                    break;
                }
            }
            if(j == mPunchlistDb.getAssignedTo().size()) break;
            i++;
        }

        int ci = 0;
        for(PunchlistAssigneeList assigneeList : punchlistAssigneeLists){
            int j = 0;
            for(String assigneeId : mPunchlistDb.getAssignedCcList()){
                if(assigneeList.getUsersId().toString().equals(assigneeId)){
                    mSelectedAssigneeCcLists.add(punchlistAssigneeLists.get(ci));
                    j++;
                    break;
                }
            }

            if(j == mPunchlistDb.getAssignedCcList().size()) break;
            ci++;
        }
        /*for (String assigneeId : mPunchlistDb.getAssignedCcList()) {
            if (!assigneeId.isEmpty()) {
                PunchlistAssignee assignee = mFieldPaperWorkProvider.getAssignee(projectId, Integer.parseInt(assigneeId));
                mSelectedAssigneeCcLists.add(getPunchlistAssigneeList(assignee));
            }
        }*/
        new CountDownTimer(2000, 10) {
            int index = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                if (index < mSelectedAssigneeLists.size()) {
                    assigneeChipsInput.addChip(mSelectedAssigneeLists.get(index));
                    index++;
                } else {
                    cancel();
                }
            }

            @Override
            public void onFinish() {

            }
        }.start();

        new CountDownTimer(2000, 10) {
            int index = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                if (index < mSelectedAssigneeCcLists.size()) {
                    assigneeCCChipsInput.addChip(mSelectedAssigneeCcLists.get(index));
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
    private PunchlistAssigneeList getPunchlistAssigneeList(PunchlistAssignee assignee) {
        PunchlistAssigneeList punAssignee = new PunchlistAssigneeList();
        punAssignee.setName(assignee.getName());
        punAssignee.setActive(assignee.getActive());
        punAssignee.setUsersId(assignee.getUsersId());
        punAssignee.setPjProjectsId(assignee.getPjProjectsId());
        punAssignee.setUserId(assignee.getUserId());
        return punAssignee;
    }
    private List<PunchlistAssigneeList> getSelectedCCAssignee() {
        List<PunchlistAssigneeList> assignees = new ArrayList<>();
        for (ChipInterface assigneeList : assigneeCCChipsInput.getSelectedChipList()) {
            assignees.add((PunchlistAssigneeList) assigneeList.getId());
        }
        return assignees;
    }
    private List<PunchlistAssigneeList> getSelectedToAssignee() {
        List<PunchlistAssigneeList> assignees = new ArrayList<>();
        for (ChipInterface assigneeList : assigneeChipsInput.getSelectedChipList()) {
            assignees.add((PunchlistAssigneeList) assigneeList.getId());
        }
        return assignees;
    }

    /**
     * Manage history status view
     *
     * @param isShowHistory
     * @param punchlistDb
     */
    private void manageHistoryStatusView(boolean isShowHistory, PunchlistDb punchlistDb) {
        boolean isPermission = (loginResponse.getUserDetails().getPermissions().get(0).getCreatePunchList() == 1 &&
                loginResponse.getUserDetails().getPermissions().get(0).getEditPunchList() == 1)? true : false;
        if(mPunchlistDb != null)
            punchListHistoryDbs = mPunchListRepository.getPunchListHistories(mPunchlistDb.getUserId(),
                    Long.valueOf(mPunchlistDb.getPunchlistId()), mPunchlistDb.getPunchlistIdMobile());
        if(loginResponse.getUserDetails().getPermissions().get(0).getViewPunchList() == 1 && !isPermission) {
            isShowHistory = false;
        }

        if (!isShowHistory) {
            historyCardView.setVisibility(View.GONE);
        } else {
            if (isPermission && punchlistDb.getStatus() == PunchListStatus.Open.getValue()) {
                historyCardView.setCardBackgroundColor(getResources().getColor(R.color.white));
                statusIcone.setImageResource(R.drawable.ic_punchlist_recomplete);
                statusIcone.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.gray_948d8d));
                historyStatusTitle.setText("Created");
                historyStatusTitle.setTextColor(getResources().getColor(R.color.gray_948d8d));
                historyStatusSubTitle.setVisibility(View.INVISIBLE);
                statusHistoryText.setVisibility(View.VISIBLE);
                statusHistoryIcon.setVisibility(View.VISIBLE);

                approvedBtn.setVisibility(View.GONE);
                rejectedBtn.setVisibility(View.GONE);
                recompleteBtn.setVisibility(View.GONE);
                completeBtn.setVisibility(View.VISIBLE);
                showHistoryBtn.setVisibility(View.GONE);
            } else if (isPermission && punchlistDb.getStatus() == PunchListStatus.Complete.getValue()) {
                historyCardView.setCardBackgroundColor(getResources().getColor(R.color.blue_complete));
                statusIcone.setImageResource(R.drawable.ic_punchlist_recomplete);
                statusIcone.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue));
                historyStatusTitle.setText(R.string.punchlist_status_completed);
                historyStatusTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                historyStatusSubTitle.setVisibility(View.INVISIBLE);
                statusHistoryText.setVisibility(View.VISIBLE);
                statusHistoryIcon.setVisibility(View.VISIBLE);

                approvedBtn.setVisibility(View.VISIBLE);
                rejectedBtn.setVisibility(View.VISIBLE);
                recompleteBtn.setVisibility(View.GONE);
                completeBtn.setVisibility(View.GONE);
                showHistoryBtn.setVisibility(View.GONE);
            } else if (isPermission && punchlistDb.getStatus() == PunchListStatus.Rejected.getValue()) {
                historyCardView.setCardBackgroundColor(getResources().getColor(R.color.recomplete_color));
                statusIcone.setImageResource(R.drawable.ic_punchlist_recomplete);
                historyStatusTitle.setText(PunchListStatus.Rejected.name());
                historyStatusTitle.setTextColor(ContextCompat.getColor(getContext(),R.color.red));
//                historyStatusSubTitle.setVisibility(View.GONE);

                approvedBtn.setVisibility(View.GONE);
                rejectedBtn.setVisibility(View.GONE);
                recompleteBtn.setVisibility(View.VISIBLE);
                completeBtn.setVisibility(View.GONE);
                showHistoryBtn.setVisibility(View.GONE);
            } else if (isPermission && punchlistDb.getStatus() == PunchListStatus.Approved.getValue()) {
                historyCardView.setCardBackgroundColor(getResources().getColor(R.color.white));
                statusIcone.setImageResource(R.drawable.ic_punchlist_recomplete);
                statusIcone.setColorFilter(getResources().getColor(R.color.green));
                historyStatusTitle.setText(PunchListStatus.Approved.name());
                historyStatusSubTitle.setVisibility(View.INVISIBLE);

                approvedBtn.setVisibility(View.GONE);
                rejectedBtn.setVisibility(View.GONE);
                recompleteBtn.setVisibility(View.GONE);
                completeBtn.setVisibility(View.GONE);
                showHistoryBtn.setVisibility(View.GONE);
            }
        }

        if(punchListHistoryDbs.size() <= 0) {
            statusHistoryText.setVisibility(View.GONE);
            statusHistoryIcon.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.recompleteBtnId)
    public void onReCompleteClick() {
        mPunchListStatus = PunchListStatus.Complete;
        fieldValidation();
    }
    @OnClick(R.id.completeBtnId)
    public void onCompleteClick() {
        Log.d("Nitin", "markCompleteCallback: ");
        /*PunchlistDb punchlistDb = mPunchlistDb;
        punchlistDb.setStatus(PunchListStatus.Complete.getValue());
        punchlistDb.setIsSync(false);
        if (punchlistDb.getComments() == null) {
            punchlistDb.setComments("");
        }
        mPunchListProvider.mPunchListRepository.updatePunchListDb(punchlistDb, "", new ArrayList<>());*/
        mPunchListStatus = PunchListStatus.Complete;
        fieldValidation();
    }

    @OnClick(R.id.approvedBtnId)
    public void onApprovedClick() {
        /*Log.d("Nitin", "approvedCallback: ");
        PunchlistDb punchlist = mPunchlistDb;
        punchlist.setStatus(PunchListStatus.Approved.getValue());
        punchlist.setIsSync(false);
        if (punchlist.getComments() == null) {
            punchlist.setComments("");
        }
        mPunchListProvider.mPunchListRepository.updatePunchListDb(punchlist, "", new ArrayList<>());*/
        mPunchListStatus = PunchListStatus.Approved;
        fieldValidation();
    }

    @OnClick(R.id.rejectedBtnId)
    public void onRejectClick() {
        FragmentActivity activity = (FragmentActivity) (this.getActivity());
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        RejectPunchListDialog rejectPunchListDialog = new RejectPunchListDialog( true,this, mPunchlistDb, mPunchListRepository);
        rejectPunchListDialog.show(ft, "");
        mPunchListStatus = PunchListStatus.Rejected;
    }

    /*@Override
    public void onDismiss(DialogInterface dialogInterface) {
        getActivity().onBackPressed();

    }*/

    @Override
    public void rejectReasonCallback() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.historyTextView)
    public void onHistoryClick() {
        openHistoryDialog();
    }

    /**
     * Open punch list history dialog
     */
    private void openHistoryDialog() {
        FragmentActivity activity = (FragmentActivity)(getContext());
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        PunchListHistoryDialog historyDialog = new PunchListHistoryDialog(this,punchListHistoryDbs, mPunchlistAssignees, mPunchListRepository);
        historyDialog.show(ft, "");
    }

    @OnClick(R.id.statusHistoryTxtId)
    public void showHistory() {
        openHistoryDialog();
    }

    private void setDefaultAssigneeAndCC(){
//        mSelectedAssigneeLists = new ArrayList<>();
        new CountDownTimer(2000, 10) {
            int index = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                if (index < mSelectedAssigneeLists.size()) {
                    assigneeChipsInput.addChip(mSelectedAssigneeLists.get(index));
                    index++;
                } else {
                    cancel();
                }
            }

            @Override
            public void onFinish() {

            }
        }.start();

        new CountDownTimer(2000, 10) {
            int index = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                if (index < mSelectedAssigneeCcLists.size()) {
                    assigneeCCChipsInput.addChip(mSelectedAssigneeCcLists.get(index));
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

}



