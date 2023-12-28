package com.pronovoscm.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.DocumentActivity;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.fragments.WorkDetailFragment;
import com.pronovoscm.model.PDFSynEnum;
import com.pronovoscm.persistence.domain.WorkDetailsAttachments;
import com.pronovoscm.persistence.repository.WorkDetailsRepository;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.WrokDetailFileDownloadProvider;
import com.pronovoscm.utils.dialogs.AttachmentDialog;
import com.pronovoscm.utils.dialogs.RejectReasonAttachmentDialog;
import com.pronovoscm.utils.ui.LoadImageInBackground;
import com.pronovoscm.utils.ui.LoadImageRejectReasonInBackground;

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

public class WorkDetailsAttachmentAdapter extends RecyclerView.Adapter {

    private final int cornerRadius;
    Activity mActivity;
    List<WorkDetailsAttachments> mWorkDetailsAttachments;
    @Inject
    WorkDetailsRepository mmWorkDetailsRepository;
    //    private LoadImage mLoadImage;
    private OnItemClickListener onItemClickListener;
    private WorkDetailFragment mOnAddItemClick;
    WorkDetailsRepository mWorkDetailsRepository;

    public WorkDetailsAttachmentAdapter(Activity activity, List<WorkDetailsAttachments> workDetailsAttachments, OnItemClickListener onItemClickListener, WorkDetailsRepository mWorkDetailsRepository) {
        ((PronovosApplication) activity.getApplication()).getDaggerComponent().inject(this);
        mActivity = activity;
        mWorkDetailsAttachments = workDetailsAttachments;
//        mLoadImage = new LoadImage(mActivity);
        cornerRadius = (int) (mActivity.getResources().getDimension(R.dimen.album_photo_radius) / mActivity.getResources().getDisplayMetrics().density);
        this.onItemClickListener = onItemClickListener;
        setHasStableIds(true);
        this.mWorkDetailsRepository = mWorkDetailsRepository;
    }

    public WorkDetailsAttachmentAdapter(Activity activity, List<WorkDetailsAttachments> workDetailsAttachments, OnItemClickListener onItemClickListener, WorkDetailFragment onAddItemClick, WorkDetailsRepository mWorkDetailsRepository) {
        ((PronovosApplication) activity.getApplication()).getDaggerComponent().inject(this);
        mActivity = activity;
        mWorkDetailsAttachments = workDetailsAttachments;
        cornerRadius = (int) (mActivity.getResources().getDimension(R.dimen.album_photo_radius) / mActivity.getResources().getDisplayMetrics().density);
//        mLoadImage = new LoadImage(mActivity);
        this.onItemClickListener = onItemClickListener;
        setHasStableIds(true);
        mOnAddItemClick = onAddItemClick;
        this.mWorkDetailsRepository = mWorkDetailsRepository;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == 1) {
            View view = inflater.inflate(R.layout.work_details_attachment_item_list, parent, false);
            return new WorkDetailsAttachmentHolder(view);
        } else {
            View view = inflater.inflate(R.layout.attachment_header_item, parent, false);
            return new WorkDetailsHeaderHolder(view);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mWorkDetailsAttachments.get(position) == null) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WorkDetailsAttachmentHolder) {
            ((WorkDetailsAttachmentHolder) holder).bind(mWorkDetailsAttachments.get(position), onItemClickListener);
        } else {

        }
      //  holder.setIsRecyclable(false);
    }

    public void setAttechmentList(List<WorkDetailsAttachments> list) {
        mWorkDetailsAttachments = list;
    }

    @Override
    public int getItemCount() {
        return mWorkDetailsAttachments.size();
    }

    public interface OnAddItemClick {

        void onAddItemClick();
    }


    public interface OnItemClickListener {

        void onItemClick(int position);
    }

    public class WorkDetailsHeaderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.backgroundImageView)
        ImageView backgroundImageView;

        public WorkDetailsHeaderHolder(@NonNull View itemView) {
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

    public class WorkDetailsAttachmentHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.attachmentImageView)
        ImageView attachmentImageView;
        @BindView(R.id.backgroundImageView)
        ImageView backgroundImageView;
        @BindView(R.id.imageViewRemove)
        ImageView imageViewRemove;
        @BindView(R.id.attachmentProgressBar)
        ProgressBar attachmentProgressBar;

        AlertDialog alertDialog = null;

        public WorkDetailsAttachmentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bind(WorkDetailsAttachments workDetailsAttachments, OnItemClickListener listener) {

            if (workDetailsAttachments.getDeletedAt() == null) {
                imageViewRemove.setVisibility(View.GONE);
                if (listener == null) {
                } else {
                    //imageViewRemove.setVisibility(View.VISIBLE);
                }

                backgroundImageView.setVisibility(View.VISIBLE);
                attachmentProgressBar.setVisibility(View.VISIBLE);
                attachmentImageView.setVisibility(View.VISIBLE);

                if (workDetailsAttachments.getType() != null && (workDetailsAttachments.getType().equalsIgnoreCase("png")
                        || workDetailsAttachments.getType().equalsIgnoreCase("jpg")
                        || workDetailsAttachments.getType().equalsIgnoreCase("jpeg"))) {

                    URI uri = null;
                    try {
                        uri = new URI(workDetailsAttachments.getAttachmentPath());
                        String[] segments = uri.getPath().split("/");
                        String imageName = segments[segments.length - 1];

                        String filePath = attachmentImageView.getContext().getFilesDir().getAbsolutePath() + "/Pronovos";
//                        String[] params = new String[]{workDetailsAttachments.getAttachmentPath(), filePath};
                        Object[] params = new Object[]{workDetailsAttachments.getAttachmentPath(), filePath,attachmentImageView.getContext(),null};
                        File imgFile = new File(filePath + "/" + imageName);
                        if (!imgFile.exists()) {
                            try {
//                                attachmentImageView.setImageResource(getFileImage(workDetailsAttachments.getType()));
                                new LoadImageRejectReasonInBackground(new LoadImageRejectReasonInBackground.Listener() {
                                    @Override
                                    public void onImageDownloaded(Bitmap bitmap) {

                                       /* RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(attachmentImageView.getContext().getResources(), bitmap);
                                        //  roundedBitmapDrawable.setCircular(true);
                                        roundedBitmapDrawable.setCornerRadius(cornerRadius);*/
                                        //  attachmentImageView.setImageDrawable(roundedBitmapDrawable);
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.d("UI thread", "I am the UI thread");
                                                backgroundImageView.setVisibility(View.GONE);
                                                attachmentProgressBar.setVisibility(View.GONE);
                                                Log.d("Manya", "onImageDownloaded: "+ getFileImage(workDetailsAttachments.getType()));
                                                attachmentImageView.setImageResource(getFileImage(workDetailsAttachments.getType()));
                                                workDetailsAttachments.setFileStatus(PDFSynEnum.SYNC.ordinal());
                                                mmWorkDetailsRepository.updateAttachment(workDetailsAttachments);
                                            }
                                        });


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
                           /* Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(attachmentImageView.getContext().getResources(), myBitmap);
                            roundedBitmapDrawable.setCornerRadius(cornerRadius);
                            attachmentImageView.setImageDrawable(roundedBitmapDrawable);*/

                            attachmentProgressBar.setVisibility(View.GONE);
                            attachmentImageView.setImageResource(getFileImage(workDetailsAttachments.getType()));

                        }
//                    new Handler().postDelayed(() -> mLoadImage.getRoundedImagePath(workDetailsAttachments.getAttachmentPath(), workDetailsAttachments.getAttachmentPath(), imageName, attachmentImageView, attachmentProgressBar, false, backgroundImageView), 0);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {

                    attachmentProgressBar.setVisibility(View.GONE);
                    if (workDetailsAttachments.getFileStatus() != null && workDetailsAttachments.getFileStatus() == (PDFSynEnum.SYNC.ordinal()))
                        attachmentImageView.setImageResource(getFileImage(workDetailsAttachments.getType()));
                    else {
                        // download file
                        attachmentProgressBar.setVisibility(View.VISIBLE);
                        attachmentImageView.setVisibility(View.GONE);
                        downLoadPjDocumentFile(workDetailsAttachments, attachmentImageView, false);
                    }


                }
                attachmentImageView.setOnClickListener(v -> {


                    //                    if (listener != null) {
                    //                        listener.onItemClick(getAdapterPosition());
                    //                    }
                    if (workDetailsAttachments.getType().equalsIgnoreCase("png")
                            || workDetailsAttachments.getType().equalsIgnoreCase("jpg")
                            || workDetailsAttachments.getType().equalsIgnoreCase("jpeg")) {
                        FragmentManager fm = ((AppCompatActivity) mActivity).getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        RejectReasonAttachmentDialog attachmentDialog = new RejectReasonAttachmentDialog();
                        Bundle bundle = new Bundle();
                        bundle.putString("attachment_path", workDetailsAttachments.getAttachmentPath());
                        bundle.putString("title_text", mActivity.getString(R.string.work_details));
                        if (mOnAddItemClick != null) {
                            bundle.putInt("image_position", getAdapterPosition());
                            attachmentDialog.onAttachToParentFragment(mOnAddItemClick);

                        }
                        attachmentDialog.setArguments(bundle);
                        attachmentDialog.show(ft, "");
                    } else {
                        if (workDetailsAttachments.getFileStatus() != null && workDetailsAttachments.getFileStatus() == PDFSynEnum.SYNC.ordinal()) {
                            String fileName = "";
                            String fileExt = "";
                            String attachPath = workDetailsAttachments.getAttachmentPath();
                            try {
                                URI uri = new URI(attachPath);
                                String[] segments = uri.getPath().split("/");
                                fileName = segments[segments.length - 1];
                                String imageName = segments[segments.length - 1];
                                String[] exts = imageName.split("[.]");
                                fileExt = exts[exts.length - 1];
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String localUrl = mActivity.getFilesDir().getAbsolutePath() + Constants.WORK_DETAILS_FILES_PATH + fileName;
                            if (workDetailsAttachments.getType().equalsIgnoreCase("rvt") ||
                                    (workDetailsAttachments.getType().equalsIgnoreCase("msg")) ||
                                    workDetailsAttachments.getType().equalsIgnoreCase("dwg")) {

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
                                openDocFiles(workDetailsAttachments, localUrl, fileExt);
                            //openFileUri(localUrl, workImpactAttachments.getPjRfiOrigName(), workImpactAttachments);
                        } else {
                            attachmentImageView.setVisibility(View.GONE);
                            attachmentProgressBar.setVisibility(View.VISIBLE);
                            downLoadPjDocumentFile(workDetailsAttachments, attachmentImageView, true);
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

        private void downLoadPjDocumentFile(WorkDetailsAttachments docFile, ImageView attachmentImageView, boolean fromclick) {
            // open file here
            WrokDetailFileDownloadProvider fileDownloadProvider = new WrokDetailFileDownloadProvider(PronovosApplication.getContext(), mmWorkDetailsRepository);
            fileDownloadProvider.getFileFromServer(docFile.getAttachmentPath(), docFile, new ProviderResult<Boolean>() {
                @Override
                public void success(Boolean result) {
                    Log.d("FileDoewnloadSuccess", "success: ");
                    docFile.setFileStatus(PDFSynEnum.SYNC.ordinal());
                    attachmentProgressBar.setVisibility(View.GONE);

                    //docFile.setIsSync(true);
                    docFile.setFileStatus(PDFSynEnum.SYNC.ordinal());
                    mWorkDetailsRepository.updateAttachment(docFile);

                    attachmentProgressBar.setVisibility(View.GONE);
                    attachmentImageView.setVisibility(View.VISIBLE);
                    attachmentImageView.setImageResource(getFileImage(docFile.getType()));

                }

                @Override
                public void AccessTokenFailure(String message) {
                    attachmentProgressBar.setVisibility(View.GONE);
                    docFile.setFileStatus(PDFSynEnum.SYNC_FAILED.ordinal());
                    attachmentImageView.setImageResource(getFileImage(docFile.getType()));
                    mWorkDetailsRepository.updateAttachment(docFile);
                    attachmentImageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void failure(String message) {
                    attachmentProgressBar.setVisibility(View.GONE);
                    docFile.setFileStatus(PDFSynEnum.SYNC_FAILED.ordinal());
                    mWorkDetailsRepository.updateAttachment(docFile);
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

        private void openDocFiles(WorkDetailsAttachments docFile, String url, String fileExt) {
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

        @OnClick({R.id.imageViewRemove})
        public void onClickRemoveImage() {
            AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
            alertDialog.setTitle(mActivity.getString(R.string.message));
            alertDialog.setMessage(mActivity.getString(R.string.are_you_sure_you_want_to_permanently_remove_this_attachment));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, mActivity.getString(R.string.ok), (dialog, which) -> {
                alertDialog.dismiss();
                WorkDetailsAttachments attachments = mWorkDetailsAttachments.get(getAdapterPosition());
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
    }


}
