package com.pronovoscm.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.DocumentActivity;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.fragments.PunchlistFragment;
import com.pronovoscm.model.PDFSynEnum;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.login.UserPermissions;
import com.pronovoscm.persistence.domain.PunchListAttachments;
import com.pronovoscm.persistence.repository.PunchListRepository;
import com.pronovoscm.persistence.repository.WorkDetailsRepository;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.PunchListFileDownloadProvider;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.ui.LoadImageInBackground;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class PunchListAttachmentAdapter extends RecyclerView.Adapter {
    private final LoginResponse loginResponse;
    private final UserPermissions userPermissions;
    private final int cornerRadius;
    Activity mActivity;
    List<PunchListAttachments> mPunchListAttachments;
    @Inject
    WorkDetailsRepository mmWorkDetailsRepository;
    //    private LoadImage mLoadImage;
    private OnItemClickListener onItemClickListener;
    private PunchlistFragment mOnAddItemClick;

    @Inject
    PunchListRepository mPunchListRepository;

    public PunchListAttachmentAdapter(Activity activity, List<PunchListAttachments> punchListAttachments,
                                      OnItemClickListener onItemClickListener, PunchlistFragment mOnAddItemClick) {
        ((PronovosApplication) activity.getApplication()).getDaggerComponent().inject(this);
        mActivity = activity;

        loginResponse = (new Gson().fromJson(SharedPref.getInstance(mActivity).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        userPermissions = loginResponse.getUserDetails().getPermissions().get(0);
        mPunchListAttachments = punchListAttachments;
//        mLoadImage = new LoadImage(mActivity);
        this.onItemClickListener = onItemClickListener;
        this.mOnAddItemClick = mOnAddItemClick;
        setHasStableIds(true);
        cornerRadius = (int) (mActivity.getResources().getDimension(R.dimen.album_photo_radius) / mActivity.getResources().getDisplayMetrics().density);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (mPunchListAttachments.get(position) == null) {
            return 0;
        }
        if (mPunchListAttachments.get(position).getDeletedAt() != null) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == 1) {
            View view = inflater.inflate(R.layout.work_details_attachment_item_list, parent, false);
            return new WorkDetailsAttachmentHolder(view);
        } else {
            View view = inflater.inflate(R.layout.attachment_header_item, parent, false);
            return new AttachmentHeaderHolder(view);

        }

//        View view = inflater.inflate(R.layout.work_details_attachment_item_list, parent, false);
//
//        return new WorkDetailsAttachmentHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WorkDetailsAttachmentHolder) {
            ((WorkDetailsAttachmentHolder) holder).bind(mPunchListAttachments.get(position), onItemClickListener);
        } else {

        }
    }

    @Override
    public int getItemCount() {
        return mPunchListAttachments.size();
    }

    public interface OnAddItemClick {
        void onAddItemClick();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class AttachmentHeaderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.backgroundImageView)
        ImageView backgroundImageView;

        public AttachmentHeaderHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bind() {
        }

        @OnClick(R.id.backgroundImageView)
        public void onClickAddImage() {
            if (mOnAddItemClick != null) {
                mOnAddItemClick.onAddItemClick();
            }
        }
    }

    public class WorkDetailsAttachmentHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.attachmentImageView)
        ImageView attachmentImageView;
        @BindView(R.id.backgroundImageView)
        ImageView backgroundImageView;
        @BindView(R.id.imageViewRemove)
        ImageView imageViewRemove;
        @BindView(R.id.attachmentProgressBar)
        ProgressBar attachmentProgressBar;

        public WorkDetailsAttachmentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        AlertDialog alertDialog = null;


        @OnClick({R.id.imageViewRemove})
        public void onClickRemoveImage() {
            AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
//            alertDialog.setTitle(mActivity.getString(R.string.message));
            alertDialog.setMessage(mActivity.getString(R.string.are_you_sure_you_want_to_permanently_remove_this_attachment));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, mActivity.getString(R.string.ok), (dialog, which) -> {
                alertDialog.dismiss();
                PunchListAttachments attachments = mPunchListAttachments.get(getAdapterPosition());
                attachments.setDeletedAt(new Date());
                attachments.setIsAwsSync(true);
                notifyItemRemoved(getAdapterPosition());
                notifyDataSetChanged();
            });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, mActivity.getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
            alertDialog.setCancelable(false);
            alertDialog.show();
            Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(ContextCompat.getColor(mActivity, R.color.gray_948d8d));
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
        }

        private void bind(PunchListAttachments punchListAttachments, OnItemClickListener listener) {
            if (punchListAttachments.getDeletedAt() == null) {


                imageViewRemove.setVisibility(View.GONE);
//                if (listener == null || userPermissions.getEditPunchList() != 1) {
//                } else {
//                    imageViewRemove.setVisibility(View.VISIBLE);
//                }
                backgroundImageView.setVisibility(View.VISIBLE);
                attachmentProgressBar.setVisibility(View.VISIBLE);
                attachmentImageView.setVisibility(View.VISIBLE);
                URI uri = null;
                if (punchListAttachments.getType().equalsIgnoreCase("png")
                        || punchListAttachments.getType().equalsIgnoreCase("jpg")
                        || punchListAttachments.getType().equalsIgnoreCase("jpeg")) {
                    try {
                        uri = new URI(punchListAttachments.getAttachmentPath());
                        String[] segments = uri.getPath().split("/");
                        String imageName = segments[segments.length - 1];

//                    new Handler().postDelayed(() -> mLoadImage.getRoundedImagePath(workDetailsAttachments.getAttachmentPath(), workDetailsAttachments.getAttachmentPath(), imageName, attachmentImageView, attachmentProgressBar, false, backgroundImageView), 0);
                        String filePath = attachmentImageView.getContext().getFilesDir().getAbsolutePath() + "/Pronovos";
                        String[] params = new String[]{punchListAttachments.getAttachmentPath(), filePath};
//                        Object[] params = new Object[]{punchListAttachments.getAttachmentPath(), filePath,attachmentImageView.getContext(),attachmentImageView};
                        File imgFile = new File(filePath + "/" + imageName);
                        if (!imgFile.exists()) {
                            try {
                                new LoadImageInBackground(new LoadImageInBackground.Listener() {
                                    @Override
                                    public void onImageDownloaded(Bitmap bitmap) {
                                /*    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(attachmentImageView.getContext().getResources(), bitmap);
//                                    roundedBitmapDrawable.setCircular(true);
                                    roundedBitmapDrawable.setCornerRadius(cornerRadius);
                                    attachmentImageView.setImageDrawable(roundedBitmapDrawable);*/
                                        backgroundImageView.setVisibility(View.GONE);
                                        attachmentProgressBar.setVisibility(View.GONE);
                                        attachmentImageView.setImageResource(getFileImage(punchListAttachments.getType()));
                                        punchListAttachments.setFileStatus(PDFSynEnum.SYNC.ordinal());
                                        mPunchListRepository.updateAttachment(punchListAttachments);
                                    }

                                    @Override
                                    public void onImageDownloadError() {
                                        attachmentProgressBar.setVisibility(View.GONE);
                                    }
                                }).executeOnExecutor(THREAD_POOL_EXECUTOR, params);
                            } catch (RejectedExecutionException e) {
                                e.printStackTrace();
                            }

                        } else {
                            /*Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(attachmentImageView.getContext().getResources(), myBitmap);
                            roundedBitmapDrawable.setCornerRadius(cornerRadius);
                            attachmentImageView.setImageDrawable(roundedBitmapDrawable);*/
                            attachmentProgressBar.setVisibility(View.GONE);
                            attachmentImageView.setImageResource(getFileImage(punchListAttachments.getType()));
                        }

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    attachmentProgressBar.setVisibility(View.GONE);
                    if (punchListAttachments.getFileStatus() != null && punchListAttachments.getFileStatus() == (PDFSynEnum.SYNC.ordinal()))
                        attachmentImageView.setImageResource(getFileImage(punchListAttachments.getType()));
                    else {
                        // download file
                        attachmentProgressBar.setVisibility(View.VISIBLE);
                        attachmentImageView.setVisibility(View.GONE);
                        downLoadPjDocumentFile(punchListAttachments, attachmentImageView, false);
                    }


                }

                attachmentImageView.setOnClickListener(v -> {
                    if (punchListAttachments.getType().equalsIgnoreCase("png")
                            || punchListAttachments.getType().equalsIgnoreCase("jpg")
                            || punchListAttachments.getType().equalsIgnoreCase("jpeg")) {
                        if (listener != null) {
                            listener.onItemClick(getAdapterPosition());
                        }
                    } else {
                        if (punchListAttachments.getFileStatus() != null && punchListAttachments.getFileStatus() == PDFSynEnum.SYNC.ordinal()) {
                            String fileName = "";
                            String fileExt = "";
                            String attachPath = punchListAttachments.getAttachmentPath();
                            try {
                                URI uri1 = new URI(attachPath);
                                String[] segments = uri1.getPath().split("/");
                                fileName = segments[segments.length - 1];
                                String imageName = segments[segments.length - 1];
                                String[] exts = imageName.split("[.]");
                                fileExt = exts[exts.length - 1];
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String localUrl = mActivity.getFilesDir().getAbsolutePath() + Constants.WORK_DETAILS_FILES_PATH + fileName;
                            if (punchListAttachments.getType().equalsIgnoreCase("rvt") ||
                                    (punchListAttachments.getType().equalsIgnoreCase("msg")) ||
                                    punchListAttachments.getType().equalsIgnoreCase("dwg")) {

                                if (alertDialog == null || !alertDialog.isShowing()) {
                                    alertDialog = new AlertDialog.Builder(mActivity).create();
                                }
                                alertDialog.setMessage("Unable to Preview file.");
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, mActivity.getString(R.string.ok), (dialog, which) -> {
                                    alertDialog.dismiss();

                                });
                                alertDialog.show();

                   /*  new  MaterialAlertDialogBuilder(mActivity, R.style.AlertDialogTheme)
                               .setMessage("No apps can perform this action.")
                               .setPositiveButton("Ok", null)
                               .show();*/
                            } else
                                openDocFiles(punchListAttachments, localUrl, fileExt);
                            //openFileUri(localUrl, workImpactAttachments.getPjRfiOrigName(), workImpactAttachments);
                        } else {
                            attachmentImageView.setVisibility(View.GONE);
                            attachmentProgressBar.setVisibility(View.VISIBLE);
                            downLoadPjDocumentFile(punchListAttachments, attachmentImageView, true);
                        }
                    }
                });
            } else {
                attachmentImageView.setVisibility(View.GONE);
                imageViewRemove.setVisibility(View.GONE);
                backgroundImageView.setVisibility(View.GONE);
                attachmentProgressBar.setVisibility(View.GONE);
            }
        }

        private void downLoadPjDocumentFile(PunchListAttachments docFile, ImageView attachmentImageView, boolean fromclick) {
            // open file here
            PunchListFileDownloadProvider fileDownloadProvider = new PunchListFileDownloadProvider(PronovosApplication.getContext(), mPunchListRepository);
            fileDownloadProvider.getFileFromServer(docFile.getAttachmentPath(), docFile, new ProviderResult<Boolean>() {
                @Override
                public void success(Boolean result) {
                    Log.d("FileDoewnloadSuccess", "success: ");
                    docFile.setFileStatus(PDFSynEnum.SYNC.ordinal());
                    attachmentProgressBar.setVisibility(View.GONE);

                    //docFile.setIsSync(true);
                    docFile.setFileStatus(PDFSynEnum.SYNC.ordinal());
                    mPunchListRepository.updateAttachment(docFile);

                    attachmentProgressBar.setVisibility(View.GONE);
                    attachmentImageView.setVisibility(View.VISIBLE);
                    attachmentImageView.setImageResource(getFileImage(docFile.getType()));

                }

                @Override
                public void AccessTokenFailure(String message) {
                    attachmentProgressBar.setVisibility(View.GONE);
                    docFile.setFileStatus(PDFSynEnum.SYNC_FAILED.ordinal());
                    attachmentImageView.setImageResource(getFileImage(docFile.getType()));
                    mPunchListRepository.updateAttachment(docFile);
                    attachmentImageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void failure(String message) {
                    attachmentProgressBar.setVisibility(View.GONE);
                    docFile.setFileStatus(PDFSynEnum.SYNC_FAILED.ordinal());
                    mPunchListRepository.updateAttachment(docFile);
                    attachmentImageView.setImageResource(getFileImage(docFile.getType()));
                    attachmentImageView.setVisibility(View.VISIBLE);
                    if (alertDialog == null || !alertDialog.isShowing()) {
                        alertDialog = new AlertDialog.Builder(mActivity).create();
                    }
                    alertDialog.setMessage(mActivity.getString(R.string.message_file_not_found));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, mActivity.getString(R.string.ok), (dialog, which) -> {
                        alertDialog.dismiss();

                    });
                    if (fromclick)
                        alertDialog.show();
                }
            });

        }

        private void openDocFiles(PunchListAttachments docFile, String url, String fileExt) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
            Log.e("ADAPTER", "openDocFiles: url " + url);
            Uri docUri = FileProvider.getUriForFile(PronovosApplication.getContext(),
                    "com.pronovoscm.provider",

                    new File(url)); // same as defined in Manifest file in android:authorities="com.sample.example.provider"
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (fileExt.equals("xls")
                    || fileExt.equals("xlsm")
                    || fileExt.equals("xlsx")
                    || fileExt.equals("docx")
                    || fileExt.equals("docm")
                    || fileExt.equals("doc")
                    || fileExt.equals("ppt")
                    || fileExt.equals("pptx")
            ) {
                intent.setDataAndType(docUri, "application/msword");
                openActivity(intent);
            } else if (fileExt.equals("pdf")) {
                ViewerConfig.Builder pdfbuilder = new ViewerConfig.Builder();
                ViewerConfig config = pdfbuilder
                        .fullscreenModeEnabled(true)
                        .multiTabEnabled(false)
                        .documentEditingEnabled(false)
                        .longPressQuickMenuEnabled(false)
                        .toolbarTitle("Attachment")
                        .showSearchView(false)
                        .autoHideToolbarEnabled(true)
                        .showOpenFileOption(false)
                        .showThumbnailView(false)
                        .showAnnotationsList(false)
                        .showBottomNavBar(false)
                        .showEditPagesOption(false)
                        .showTopToolbar(false)
                        .build();

               /* Intent ii = DocumentActivity.IntentBuilder.fromActivityClass(mActivity, DocumentActivity.class)
                        .withUri(docUri)
                        .usingConfig(config)
                        .usingTheme(R.style.PDFTronAppTheme)
                        .build();*/
                DocumentActivity.openDocument(mActivity, docUri, config);
            } else if (fileExt.equals("zip")) {
                intent.setDataAndType(docUri, "text/plain");
                openActivity(intent);
            } else if (fileExt.equals("mp4")) {
                intent.setDataAndType(docUri, "video/mp4");
                openActivity(intent);
            } else if (fileExt.equals("txt")) {
                intent.setDataAndType(docUri, "text/plain");
                openActivity(intent);
            } else if (fileExt.equals("msg")) {
                docUri = Uri.parse(url);
                Log.d("ADAPTER", "openDocFiles: " + docUri.getPath());
                intent.setDataAndType(docUri, "application/vnd.ms-outlook");
                openActivity(intent);
            } else if (fileExt.equals("rvt")) {
                intent.setDataAndType(docUri, "application/vnd.ms-project, application/msproj, application/msproject, application/x-msproject, " +
                        " application/x-ms-project, application/x-dos_ms_project, application/mpp, zz-application/zz-winassoc-mpp"
                );
                openActivity(intent);
            } else if (fileExt.equals("dwg")) {
                intent.setDataAndType(docUri, "image/vnd.dwg");
                openActivity(intent);
            } else if (fileExt.equals("mpp")) {
                intent.setDataAndType(docUri, "application/vnd.ms-project, application/msproj, application/msproject, application/x-msproject, " +
                        " application/x-ms-project, application/x-dos_ms_project, application/mpp, zz-application/zz-winassoc-mpp");

                openActivity(intent);
            }
        }

        private void openActivity(Intent intent) {
            try {
                intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);
                Intent chooser = Intent.createChooser(intent, "Open With..");
                mActivity.startActivity(chooser);
            } catch (ActivityNotFoundException e) {
                //user does not have a pdf viewer installed
                Log.d("NOT_FOUND", "shouldOverrideUrlLoading: " + e.getLocalizedMessage());
                Toast.makeText(mActivity, "No application to open file", Toast.LENGTH_SHORT).show();
            }
        }

        private int getFileImage(String type) {
            if (type.equalsIgnoreCase("zip")) {
                return R.drawable.ic_zip;
            } else if (type.equalsIgnoreCase("pdf")) {
                return R.drawable.ic_file_pdf;
            } else if (type.equalsIgnoreCase("xls")) {
                return R.drawable.ic_file_xls;
            } else if (type.equalsIgnoreCase("xlsm")) {
                return R.drawable.ic_file_xlsm;
            } else if (type.equalsIgnoreCase("xlsx")) {
                return R.drawable.ic_file_xlsx;
            } else if (type.equalsIgnoreCase("doc")) {
                return R.drawable.ic_doc;
            } else if (type.equalsIgnoreCase("docx")) {
                return R.drawable.ic_file_docx;
            } else if (type.equalsIgnoreCase("docm")) {
                return R.drawable.ic_docm;
            } else if (type.equalsIgnoreCase("ppt")) {
                return R.drawable.ic_file_ppt;
            } else if (type.equalsIgnoreCase("pptx")) {
                return R.drawable.ic_pptx;
            } else if (type.equalsIgnoreCase("mp4")) {
                return R.drawable.ic_file_audio;
            } else if (type.equalsIgnoreCase("jpeg")) {
                return R.drawable.ic_file_jpeg;
            } else if (type.equalsIgnoreCase("jpg")) {
                return R.drawable.ic_file_jpeg;
            } else if (type.equalsIgnoreCase("png")) {
                return R.drawable.ic_png;
            } else if (type.equalsIgnoreCase("txt")) {
                return R.drawable.ic_file_txt;
            } else if (type.equalsIgnoreCase("mpp")) {
                return R.drawable.ic_mpp;
            } else if (type.equalsIgnoreCase("rvt")) {
                return R.drawable.ic_file_type_rvt;
            } else if (type.equalsIgnoreCase("msg")) {
                return R.drawable.ic_file_type_msg;
            } else if (type.equalsIgnoreCase("dwg")) {
                return R.drawable.ic_file_type_dwg;
            } else {
                return R.drawable.ic_file_txt;
            }
        }


    }
}
