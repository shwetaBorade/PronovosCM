package com.pronovoscm.fragments;

import android.Manifest;
import android.app.Activity;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pdftron.pdf.utils.Utils;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.activity.BaseActivity;
import com.pronovoscm.activity.DailyWorkDetailsActivity;
import com.pronovoscm.activity.LoginActivity;
import com.pronovoscm.adapter.CompanyAdapter;
import com.pronovoscm.adapter.WorkDetailsAttachmentAdapter;
import com.pronovoscm.data.FieldPaperWorkProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.model.ObjectEnum;
import com.pronovoscm.model.response.companylist.CompanyListRequest;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.login.UserPermissions;
import com.pronovoscm.persistence.domain.CompanyList;
import com.pronovoscm.persistence.domain.WorkDetails;
import com.pronovoscm.persistence.domain.WorkDetailsAttachments;
import com.pronovoscm.persistence.repository.FieldPaperWorkRepository;
import com.pronovoscm.persistence.repository.WorkDetailsRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.FileUtils;
import com.pronovoscm.utils.ObjectEvent;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.customcamera.CameraUtils;
import com.pronovoscm.utils.dialogs.AttachmentDeleteInterface;
import com.pronovoscm.utils.dialogs.AttachmentDialog;
import com.pronovoscm.utils.dialogs.ObjectDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class WorkDetailFragment extends BaseFragment implements View.OnClickListener, WorkDetailsAttachmentAdapter.OnAddItemClick, AttachmentDeleteInterface, BackPressedListener {


    public static final int FILECAMERA_REQUEST_CODE = 331;
    private static final int TAKE_PICTURE = 3115;
    private static final int SELECT_PICTURE = 5645;
    public static BackPressedListener backpressedlistener;
    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;
    private static final String workDetailMobileIdConstant = "workDetailMobileId";

    @Inject
    FieldPaperWorkRepository mFieldPaperWorkRepository;

    @Inject
    FieldPaperWorkProvider mFieldPaperWorkProvider;
    @Inject
    WorkDetailsRepository mWorkDetailsRepository;
    @BindView(R.id.deleteTextView)
    TextView deleteTextView;
    @BindView(R.id.saveTextView)
    TextView saveTextView;
    @BindView(R.id.cancelTextView)
    TextView cancelTextView;
    @BindView(R.id.companyListNameTextView)
    TextView companyListNameTextView;
    @BindView(R.id.locationEditText)
    EditText locationEditText;
    @BindView(R.id.detailSummaryEditText)
    EditText detailSummaryEditText;
    @BindView(R.id.addTextView)
    TextView addTextView;
    @BindView(R.id.attachmentTextView)
    TextView attachmentTextView;
    @BindView(R.id.attachmentRecycleView)
    RecyclerView recyclerView;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.allKeyword)
    RelativeLayout allKeyword;
    @BindView(R.id.tradespinnewView)
    RelativeLayout tradespinnewView;
    @BindView(R.id.detailsSummaryView)
    RelativeLayout detailsSummaryView;
    TextView titleTextView;
    ImageView addImageView;

    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    ImageView deleteImageView;
    UserPermissions userPermissions;
    private Uri fileUri;
    private List<CompanyList> mCompanyLists;
    private int projectId;
    private WorkDetails mWorkDetails;
    private long workDetailMobileId;
    private CompanyAdapter companyAdapter;
    private List<WorkDetailsAttachments> attachmentList;
    private WorkDetailsAttachmentAdapter adapterAttachment;
    private CompanyList mSelectedCompany;
    private LoginResponse loginResponse = null;
    public static Boolean isUpdated = true;
    ActivityResultLauncher<Intent> galaryResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    Bitmap bitmap = null;
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            Intent data = result.getData();
                            Log.d("onActivityResult", "Select Pic");
                            try {
                                Uri selectedImage = data.getData();
                                String[] filePath = {MediaStore.Images.Media.DATA};
                                Cursor c = getContext().getContentResolver().query(
                                        selectedImage, filePath, null, null, null);
                                c.moveToFirst();
                                int columnIndex = c.getColumnIndex(filePath[0]);
                                String picturePath = c.getString(columnIndex);
                                c.close();

                                File output = FileUtils.getOutputMediaFile(1);

                                Bitmap thumbnail1 =
                                        (BitmapFactory.decodeFile(picturePath));
                                Bitmap thumbnail = getRotateBitmap(picturePath, thumbnail1);
                                try {
                                    FileOutputStream out = new FileOutputStream(output);
                                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                    out.flush();
                                    out.close();
                                    WorkDetailsAttachments workDetailsAttachments = new WorkDetailsAttachments();

                                    workDetailsAttachments.setAttachmentPath(output.getPath());
                                    workDetailsAttachments.setIsAwsSync(false);
                                    workDetailsAttachments.setWorkDetailsReportIdMobile(workDetailMobileId);
                                    workDetailsAttachments.setUsersId(loginResponse.getUserDetails().getUsers_id());
                                    workDetailsAttachments.setWorkDetailsReportId(0);
                                    workDetailsAttachments.setType("jpeg");
                                    attachmentList.add(workDetailsAttachments);
                                    if (attachmentList.size() == 11 && attachmentList.get(0) == null) {
                                        attachmentList.remove(0);
                                    }
                                    adapterAttachment.setAttechmentList(attachmentList);
                                    adapterAttachment.notifyDataSetChanged();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
    ActivityResultLauncher<Intent> cameraResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
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
//                                rotatedBitmap = bitmap;
                                    try {
                                        saveImage(bitmap, outputFile);

                                        WorkDetailsAttachments workDetailsAttachments = new WorkDetailsAttachments();
                                        workDetailsAttachments.setAttachmentPath(outputFile.getPath());
                                        workDetailsAttachments.setAttachmentId(0);
                                        workDetailsAttachments.setIsAwsSync(false);
                                        workDetailsAttachments.setWorkDetailsReportIdMobile(workDetailMobileId);
                                        workDetailsAttachments.setUsersId(loginResponse.getUserDetails().getUsers_id());
                                        workDetailsAttachments.setType("jpeg");
                                        attachmentList.add(workDetailsAttachments);
                                        if (attachmentList.size() == 11 && attachmentList.get(0) == null) {
                                            attachmentList.remove(0);
                                        }
                                        adapterAttachment.setAttechmentList(attachmentList);

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
                    }
                }
            });
    private Date dateWorkDetail = null;
    private Activity activity;
    private ArrayList<WorkDetailsAttachments> removedWorkDetailsAttachments;
    private int canDeleteWorkDetail;
    private int canEditWorkDetail;
    private int attachmentTemp;

    public static List<WorkDetailsAttachments> cloneList(List<WorkDetailsAttachments> imageTags) {
        List<WorkDetailsAttachments> clonedList = new ArrayList<>(imageTags.size());
        for (WorkDetailsAttachments imageTag : imageTags) {
            clonedList.add(new WorkDetailsAttachments(imageTag));
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
        View rootview = inflater.inflate(R.layout.work_detail_view, container, false);
        ButterKnife.bind(this, rootview);
        return rootview;
    }

    @OnClick(R.id.allKeyword)
    public void onClickCompanyListView() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ObjectDialog tagsDialog = new ObjectDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable("object", mSelectedCompany);
        bundle.putInt("pjProjectId", projectId);
        bundle.putInt("obj_type", ObjectEnum.COMPANY_LIST.ordinal());
        tagsDialog.setCancelable(false);
        tagsDialog.setArguments(bundle);
        tagsDialog.show(ft, "");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int myInteger = getResources().getInteger(R.integer.quantity_length);
        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), myInteger));
        recyclerView.setAdapter(adapterAttachment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((PronovosApplication) getActivity().getApplication()).getDaggerComponent().inject(this);
        activity = getActivity();
        titleTextView = getActivity().findViewById(R.id.titleTextView);
        if (!NetworkService.isNetworkAvailable(getContext())) {
            offlineTextView.setVisibility(View.VISIBLE);
        }
        addImageView = getActivity().findViewById(R.id.addImageView);
        deleteImageView = getActivity().findViewById(R.id.deleteImageView);
        addImageView.setVisibility(View.GONE);
        cancelTextView.setOnClickListener(this);
        saveTextView.setOnClickListener(this);
        deleteTextView.setOnClickListener(this);
        attachmentList = new ArrayList<>();
        removedWorkDetailsAttachments = new ArrayList<>();
        attachmentList.add(null);
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(getContext()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        userPermissions = loginResponse.getUserDetails().getPermissions().get(0);
        canEditWorkDetail = loginResponse.getUserDetails().getPermissions().get(0).getEditProjectDailyReport();
        canDeleteWorkDetail = loginResponse.getUserDetails().getPermissions().get(0).getDeleteProjectDailyReport();
        projectId = getArguments().getInt("projectId");
        dateWorkDetail = (Date) getArguments().getSerializable("workDetailsDate");
        workDetailMobileId = getArguments().getLong(workDetailMobileIdConstant);
        if (workDetailMobileId != 0) {
            mWorkDetails = mWorkDetailsRepository.getWorkDetailsItem(workDetailMobileId);
            if (mWorkDetails != null) {
                detailSummaryEditText.setText(mWorkDetails.getWorkSummary());
                locationEditText.setText(mWorkDetails.getWorkDetLocation());
                attachmentList.remove(0);
                attachmentList.addAll(cloneList(mWorkDetailsRepository.getAttachments(workDetailMobileId)));
                if (attachmentList.size() <= 0) {
                    attachmentTextView.setVisibility(View.GONE);
                }
                attachmentTemp = attachmentList.size();
                if (userPermissions.getEditProjectDailyReport() == 1) {
                    titleTextView.setText(R.string.edit_work_details);
                    attachmentTextView.setVisibility(View.VISIBLE);
                    if (attachmentList.size() < 10) {
                        attachmentList.add(0, null);
                    }
                } else
                    titleTextView.setText(R.string.work_details);

                if (canDeleteWorkDetail == 1) {
                    deleteImageView.setVisibility(View.VISIBLE);
                }
                if (canEditWorkDetail != 1) {
                    allKeyword.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                    tradespinnewView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                    detailsSummaryView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.disable_rounded_gray_border));
                    companyListNameTextView.setLongClickable(false);
                    allKeyword.setClickable(false);
                    locationEditText.setLongClickable(false);
                    locationEditText.setFocusableInTouchMode(false);
                    detailSummaryEditText.setLongClickable(false);
                    detailSummaryEditText.setFocusableInTouchMode(false);
                    saveTextView.setVisibility(View.GONE);
                    cancelTextView.setVisibility(View.GONE);
                    titleTextView.setText(R.string.work_details);
                }


            }
        } else {
//            addTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(getString(R.string.add_work_details));
            detailSummaryEditText.setText("");
            locationEditText.setText("");

//            deleteTextView.setVisibility(View.GONE);
        }

        mCompanyLists = mFieldPaperWorkRepository.getCompanyList(projectId);
      /*  companyAdapter = new CompanyAdapter(getActivity(), R.layout.simple_spinner_item, mCompanyLists);
        companyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        companySpinner.setAdapter(companyAdapter);
        companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedCompany = mCompanyLists.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        int myInteger = getResources().getInteger(R.integer.quantity_length);
        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
//                        alertDialog.setTitle(getString(R.string.message));
                alertDialog.setMessage(getActivity().getString(R.string.are_you_sure_you_want_to_delete_this_entry));
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getActivity().getString(R.string.cancel), (dialog, which) -> {
                    dialog.dismiss();
                });
                alertDialog.setCancelable(false);
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getActivity().getString(R.string.ok), (dialog, which) -> {
                    alertDialog.dismiss();
                    mWorkDetails.setDeletedAt(new Date());
                    mWorkDetails.setIsSync(false);
                    ((DailyWorkDetailsActivity) getActivity()).updateWorkDetail(mWorkDetails);
                    isUpdated=false;
                    getActivity().onBackPressed();
//                                        dailyCrewReportActivity.onBackPressed();
                });
                alertDialog.show();
                Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(ContextCompat.getColor(getActivity(), R.color.gray_948d8d));
                Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), myInteger));
//        List<WorkDetailsAttachments> workDetailsAttachments = mWorkDetailsRepository.getAttachments(workDetail.getWorkDetailsReportIdMobile());
        adapterAttachment = new WorkDetailsAttachmentAdapter(getActivity(), attachmentList, position -> {
            FragmentManager fm = (getActivity()).getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            AttachmentDialog attachmentDialog = new AttachmentDialog();
            Bundle bundle = new Bundle();
            bundle.putString("attachment_path", attachmentList.get(position).getAttachmentPath());
            bundle.putString("title_text", getString(R.string.work_details));
            if (workDetailMobileId != 0) {
                bundle.putString("title_text", getString(R.string.work_details));
            } else {
                bundle.putString("title_text", getString(R.string.work_details));
            }
            attachmentDialog.setArguments(bundle);
            attachmentDialog.show(ft, "");
        }, this, mWorkDetailsRepository);
        recyclerView.setAdapter(adapterAttachment);

        spinnerSelection();


        detailSummaryEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == detailSummaryEditText.getId()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
        callCompanyListAPI();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
        } else {
            offlineTextView.setVisibility(View.GONE);
        }
    }

    private void callCompanyListAPI() {
        CompanyListRequest companyListRequest = new CompanyListRequest();
        companyListRequest.setProjectId(projectId);
        mFieldPaperWorkProvider.getCompanyList(companyListRequest, new ProviderResult<String>() {
            @Override
            public void success(String result) {
                mCompanyLists = mFieldPaperWorkRepository.getCompanyList(projectId);

                CompanyAdapter companyAdapter = new CompanyAdapter(activity, R.layout.simple_spinner_item, mCompanyLists);
                companyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mCompanyList = mCompanyLists.get(0);

               /* companySpinner.setAdapter(companyAdapter);
                companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mSelectedCompany = mCompanyLists.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });*/
                spinnerSelection();

            }

            @Override
            public void AccessTokenFailure(String message) {
                startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(getActivity()).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(getActivity()).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                getActivity().finish();
            }

            @Override
            public void failure(String message) {
//                messageDialog.showMessageAlert(getActivity(), message, getString(R.string.ok));

            }
        });
    }

    private void spinnerSelection() {
        for (int i = 0; i < mCompanyLists.size(); i++) {
            CompanyList companyList = mCompanyLists.get(i);
            if (mWorkDetails != null) {

                int workCmpId = mWorkDetails.getCompanyId();
                int cmpId = companyList.getCompanyId();
                if (workCmpId == cmpId) {
                    mSelectedCompany = companyList;
                }
            } else if (mWorkDetails == null && companyList.getSelected()) {
                mSelectedCompany = companyList;
            }
        }
        if (mSelectedCompany != null)
            companyListNameTextView.setText(mSelectedCompany.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ObjectEvent event) {
        if (event.getObject() != null) {
            if (event.getObjectType() == ObjectEnum.COMPANY_LIST.ordinal()) {
                mSelectedCompany = (CompanyList) event.getObject();
                companyListNameTextView.setText(mSelectedCompany.getName());

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
                if (attachmentList.size() > 0 && attachmentList.get(0) == null) {
                    attachmentList.remove(0);
                }
                ArrayList<WorkDetailsAttachments> workDetailsAttachments = new ArrayList<>();
                for (WorkDetailsAttachments wa : attachmentList) {
                    if (wa != null && wa.getDeletedAt() == null) {
                        workDetailsAttachments.add(wa);
                    }
                }
                workDetailsAttachments.addAll(removedWorkDetailsAttachments);
                if (mWorkDetails != null) {
                    WorkDetails details = mWorkDetailsRepository.updateWorkDetail(projectId, mSelectedCompany, workDetailsAttachments, dateWorkDetail,
                            locationEditText.getText().toString().trim(), detailSummaryEditText.getText().toString().trim(), mWorkDetails);
                    EventBus.getDefault().post(details);
                    isUpdated=false;
                    getActivity().onBackPressed();
                } else {
                    WorkDetails details = mWorkDetailsRepository.addWorkDetail(projectId, mSelectedCompany, workDetailsAttachments, dateWorkDetail,
                            locationEditText.getText().toString().trim(), detailSummaryEditText.getText().toString().trim());
                    // Event is registered in {@link DailyWorkDetailsActivity}
//                    details.setDeletedAt(null);
                    EventBus.getDefault().post(details);
                    isUpdated=false;
                    getActivity().onBackPressed();
                }
                break;
            case R.id.cancelTextView:
                Utils.hideSoftKeyboard(getContext(), cancelTextView);
                showCancelDialog(getContext());
                break;
            case R.id.deleteTextView:
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
//                alertDialog.setTitle(getString(R.string.message));
                alertDialog.setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_entry));
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
                    dialog.dismiss();
                    attachmentList.addAll(removedWorkDetailsAttachments);
                    if (attachmentList.size() > 0 && attachmentList.get(0) == null) {
                        attachmentList.remove(0);

                    }
                    for (WorkDetailsAttachments attachment : attachmentList) {
                        if (attachment != null) {
                            attachment.setDeletedAt(null);
                        }

                    }
                    mWorkDetails.setDeletedAt(new Date());
                    mWorkDetails.setIsSync(false);
                    EventBus.getDefault().post(mWorkDetails);
                    isUpdated=false;
                    getActivity().onBackPressed();
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> {
//                    workDetails.setDeletedAt(null);
//                    workDetails.setIsSync(true);
//                    updateWorkDetail(workDetails);
                    dialog.dismiss();
                });
                alertDialog.setCancelable(false);
                alertDialog.show();
                Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_948d8d));
                Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));


                break;
            default:
                break;
        }
    }

    private void showCancelDialog(Context context) {

        if (getArguments().getLong(workDetailMobileIdConstant) != 0) {
            if (locationEditText.getText().toString().trim().equals(mWorkDetails.getWorkDetLocation())
                    && detailSummaryEditText.getText().toString().trim().equals(mWorkDetails.getWorkSummary())
                    && mWorkDetails.getCompanyId().toString().equals(mSelectedCompany.getCompanyId().toString())
                    && validateImages()) {
                isUpdated = false;
                getActivity().onBackPressed();
            } else {
                showDialog(context);
            }
        } else {
            if (attachmentList.size() > 0 && attachmentList.get(0) == null) {
                attachmentList.remove(0);
            }
            if (locationEditText.getText().toString().trim().isEmpty() &&
                    detailSummaryEditText.getText().toString().trim().isEmpty() &&
                    attachmentList != null && attachmentList.isEmpty() &&
                    mSelectedCompany.getCompanyId().equals(loginResponse.getUserDetails().getTenantId())) {
                isUpdated = false;
                getActivity().onBackPressed();
            } else {
                showDialog(context);
            }
        }
    }

    //    @OnClick(R.id.addTextView)
    public void onClickAddButton() {

        int numberOfItems = 0;
        for (WorkDetailsAttachments attachments : attachmentList) {
            if (attachments != null && attachments.getDeletedAt() == null)
                numberOfItems++;
        }
        if (numberOfItems < 11) {
            selectImage();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setMessage(R.string.max_attachement_message)
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.dismiss());
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void selectImage() {
//        Constants.iscamera = true;
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.CAMERA,
                            getExternalPermission()}, FILECAMERA_REQUEST_CODE);

                } else {
                    openCamera();
                }

            } else if (items[item].equals(getString(R.string.choose_from_library))) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                intent.setType("image/*");
        /*        startActivityForResult(
                        Intent.createChooser(intent, getString(R.string.select_picture)),
                        SELECT_PICTURE);*/
                galaryResultLauncher.launch(intent);
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == FILECAMERA_REQUEST_CODE) {
            //resume tasks needing this permission
            openCamera();
        }
    }

    public void openCamera() {
        Intent intents = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = FileUtils.getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intents.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        //startActivityForResult(intents, TAKE_PICTURE);
        cameraResultLauncher.launch(intents);

    }

    private void saveImage(Bitmap bitmap, File output) throws IOException {
        FileOutputStream out = new FileOutputStream(output);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        titleTextView.setText(R.string.work_details);
        addImageView.setVisibility(View.VISIBLE);
        deleteImageView.setVisibility(View.GONE);
    }

    @Override
    public void onAddItemClick() {
        onClickAddButton();
    }

    @Override
    public void onDelete(int position) {
        WorkDetailsAttachments attachments = attachmentList.get(position);
//        if (attachments.getIsAwsSync()) {
        attachments.setDeletedAt(new Date());
        attachments.setIsAwsSync(true);
        removedWorkDetailsAttachments.add(attachments);
//        }
        attachmentList.remove(position);
        if (workDetailMobileId == 0 && attachmentList.size() < 11 && attachmentList.get(0) != null) {
            attachmentList.add(0, null);
        }
        if (attachmentList.size() < 10 && attachmentList.get(0) != null || attachmentList.size() == 0) {
            attachmentList.add(0, null);
        }
        adapterAttachment.setAttechmentList(attachmentList);
        adapterAttachment.notifyDataSetChanged();

//        }
//        attachments.setIsAwsSync(true);
//        notifyItemRemoved(getAdapterPosition());
//        notifyDataSetChanged();
    }

    public void refreshData() {
        if (locationEditText != null) {
            locationEditText.setText("");
        }
        if (detailSummaryEditText != null) {
            detailSummaryEditText.setText("");
        }
    }

    public Bitmap getRotateBitmap(String photoPath, Bitmap bitmap) {
        Bitmap rotatedBitmap = null;
        try {
            ExifInterface ei = null;
            ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap;
        }
        return rotatedBitmap != null ? rotatedBitmap : bitmap;
    }

    void showDialog(Context context) {
        isUpdated = false;
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setMessage(getString(R.string.are_you_sure_you_want_to_exit_without_saving_your_changes));
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {

            if (attachmentList.size() > 0 && attachmentList.get(0) == null) {
                attachmentList.remove(0);
            }
            attachmentList.clear();
            attachmentList.addAll(cloneList(mWorkDetailsRepository.getAttachments(workDetailMobileId)));
            for (WorkDetailsAttachments attachment : attachmentList) {
                if (attachment != null) {
                    attachment.setDeletedAt(null);
                }
            }
            isUpdated = false;
            dialog.dismiss();
            getActivity().onBackPressed();
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> {
            isUpdated = true;
            dialog.dismiss();
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(ContextCompat.getColor(context, R.color.gray_948d8d));
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
    }

    boolean validateImages() {
        if (attachmentList.size() > 0 && attachmentList.get(0) == null) {
            attachmentList.remove(0);
        }
        if (attachmentList.size() != attachmentTemp || removedWorkDetailsAttachments.size()>0)
            return false;
        for (WorkDetailsAttachments attachment : attachmentList) {
            if (attachment != null)
                if (attachment.getAttachmentIdMobile() == null)
                    return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        showCancelDialog(getContext());
    }

    @Override
    public void onPause() {
        backpressedlistener = null;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        backpressedlistener = this;
    }
}
