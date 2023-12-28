package com.pronovoscm.adapter;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.DocumentActivity;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.fragments.WorkImpactFragment;
import com.pronovoscm.model.PDFSynEnum;
import com.pronovoscm.persistence.domain.WorkImpactAttachments;
import com.pronovoscm.persistence.repository.WorkImpactRepository;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.WrokImpactFileDownloadProvider;
import com.pronovoscm.utils.dialogs.RejectReasonAttachmentDialog;
import com.pronovoscm.utils.ui.LoadImageRejectReasonInBackground;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WorkImpactAttachmentAdapter extends RecyclerView.Adapter {
    private final int cornerRadius;
    Activity mActivity;
    List<WorkImpactAttachments> mWorkImpactAttachments;
    //    private LoadImage mLoadImage;
    private WorkDetailsAttachmentAdapter.OnItemClickListener onItemClickListener;
    private WorkImpactFragment mOnAddItemClick;
    WorkImpactRepository mWorkImpactRepositor;
    public void setAttachmentList(List<WorkImpactAttachments> list) {
        mWorkImpactAttachments.clear();
        mWorkImpactAttachments.addAll(cloneList(list));
        this.notifyDataSetChanged();
    }
    public static List<WorkImpactAttachments> cloneList(List<WorkImpactAttachments> workImpactAttachments) {
        List<WorkImpactAttachments> clonedList = new ArrayList<>(workImpactAttachments.size());
        for (WorkImpactAttachments imageTag : workImpactAttachments) {
            if (imageTag!=null){
                clonedList.add(new WorkImpactAttachments(imageTag));
            }else {
                clonedList.add(null);
            }
        }
        return clonedList;
    }
    public WorkImpactAttachmentAdapter(Activity activity, List<WorkImpactAttachments> workImpactAttachments, WorkDetailsAttachmentAdapter.OnItemClickListener onItemClickListener, WorkImpactRepository mmWorkImpactRepositor) {
        mActivity = activity;
        mWorkImpactAttachments = cloneList(workImpactAttachments);
//        mLoadImage = new LoadImage(mActivity);
        cornerRadius = (int) (mActivity.getResources().getDimension(R.dimen.album_photo_radius) / mActivity.getResources().getDisplayMetrics().density);
        this.onItemClickListener = onItemClickListener;
        setHasStableIds(true);
        this.mWorkImpactRepositor = mmWorkImpactRepositor;
    }

    public WorkImpactAttachmentAdapter(Activity activity, List<WorkImpactAttachments> workImpactAttachments, WorkDetailsAttachmentAdapter.OnItemClickListener onItemClickListener,
                                       WorkImpactFragment onAddItemClick, WorkImpactRepository mmWorkImpactRepositor) {
        mActivity = activity;
        mWorkImpactAttachments = cloneList(workImpactAttachments);
        cornerRadius = (int) (mActivity.getResources().getDimension(R.dimen.album_photo_radius) / mActivity.getResources().getDisplayMetrics().density);
//        mLoadImage = new LoadImage(mActivity);
        this.onItemClickListener = onItemClickListener;
        setHasStableIds(true);
        mOnAddItemClick = onAddItemClick;
        this.mWorkImpactRepositor = mmWorkImpactRepositor;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == 2) {
            View view = inflater.inflate(R.layout.empty_attachment, parent, false);
            return new EmptyAttachmentHolder(view);
        } else if (viewType == 1) {
            View view = inflater.inflate(R.layout.work_details_attachment_item_list, parent, false);
            return new WorkDetailsAttachmentHolder(view);
        } else {
            View view = inflater.inflate(R.layout.attachment_header_item, parent, false);
            return new WorkDetailsHeaderHolder(view);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mWorkImpactAttachments.get(position) == null) {
            return 0;
        }
        if (mWorkImpactAttachments.get(position).getDeletedAt() != null) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WorkDetailsAttachmentHolder) {
            ((WorkDetailsAttachmentHolder) holder).bind(mWorkImpactAttachments.get(position), onItemClickListener,position);
        }
        //holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return mWorkImpactAttachments.size();
    }

    public interface OnAddItemClick {
        void onAddItemClick();
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

    public class EmptyAttachmentHolder extends RecyclerView.ViewHolder {


        public EmptyAttachmentHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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
        @BindView(R.id.attachmentProgressBar)
        ProgressBar attachmentProgressBar;
        @BindView(R.id.imageViewRemove)
        ImageView imageViewRemove;
        @BindView(R.id.cardPhotoImage)
        CardView attachmentCardView;

        public WorkDetailsAttachmentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        AlertDialog alertDialog = null;

        private void bind(WorkImpactAttachments workImpactAttachments, WorkDetailsAttachmentAdapter.OnItemClickListener listener, int position) {

            if (workImpactAttachments.getDeletedAt() == null) {
                imageViewRemove.setVisibility(View.GONE);
//                if (listener == null) {
//                } else {
//                    imageViewRemove.setVisibility(View.VISIBLE);
//                }

                backgroundImageView.setVisibility(View.VISIBLE);
                attachmentProgressBar.setVisibility(View.VISIBLE);
                attachmentImageView.setVisibility(View.VISIBLE);
                attachmentCardView.setVisibility(View.VISIBLE);
                if (workImpactAttachments.getType().equalsIgnoreCase("png")
                        || workImpactAttachments.getType().equalsIgnoreCase("jpg")
                        || workImpactAttachments.getType().equalsIgnoreCase("jpeg")) {
                    URI uri = null;
                    try {
//                        attachmentImageView.setImageResource(getFileImage(workImpactAttachments.getType()));
                        uri = new URI(workImpactAttachments.getAttachmentPath());
                        String[] segments = uri.getPath().split("/");
                        String imageName = segments[segments.length - 1];

                        String filePath = attachmentImageView.getContext().getFilesDir().getAbsolutePath() + "/Pronovos";
//                        String[] params = new String[]{workImpactAttachments.getAttachmentPath(), filePath};
                        Object[] params = new Object[]{workImpactAttachments.getAttachmentPath(), filePath,attachmentImageView.getContext(),null};
                        File imgFile = new File(filePath + "/" + imageName);
                        if (!imgFile.exists()) {
                            new LoadImageRejectReasonInBackground(new LoadImageRejectReasonInBackground.Listener() {
                                @Override
                                public void onImageDownloaded(Bitmap bitmap) {
                                    /*RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(attachmentImageView.getContext().getResources(), bitmap);
                                    roundedBitmapDrawable.setCornerRadius(cornerRadius);
                                    attachmentImageView.setImageDrawable(roundedBitmapDrawable);*/
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("UI thread", "I am the UI thread");
                                            backgroundImageView.setVisibility(View.GONE);
                                            attachmentProgressBar.setVisibility(View.GONE);
                                            attachmentImageView.setImageResource(getFileImage(workImpactAttachments.getType()));
                                            workImpactAttachments.setFileStatus(PDFSynEnum.SYNC.ordinal());
                                            mWorkImpactRepositor.updateAttachment(workImpactAttachments);
                                        }
                                    });
                                }

                                @Override
                                public void onImageDownloadError() {
                                    attachmentProgressBar.setVisibility(View.GONE);
                                }
                            }).executeOnExecutor(THREAD_POOL_EXECUTOR, params);
                        } else {
                            /*Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                        Glide.with(attachmentImageView.getContext()).load(new LoadImage().bitmapToByte(myBitmap)).into(attachmentImageView);
                            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(attachmentImageView.getContext().getResources(), myBitmap);
                            roundedBitmapDrawable.setCornerRadius(cornerRadius);
                            attachmentImageView.setImageDrawable(roundedBitmapDrawable);*/

                            attachmentProgressBar.setVisibility(View.GONE);
                            attachmentImageView.setImageResource(getFileImage(workImpactAttachments.getType()));
                            attachmentProgressBar.setVisibility(View.GONE);
                        }
//                    new Handler().postDelayed(() -> mLoadImage.getRoundedImagePath(workImpactAttachments.getAttachmentPath(), workImpactAttachments.getAttachmentPath(), imageName, attachmentImageView, attachmentProgressBar, false, backgroundImageView), 0);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {

                    attachmentProgressBar.setVisibility(View.GONE);
                    if (workImpactAttachments.getFileStatus() != null && workImpactAttachments.getFileStatus() == (PDFSynEnum.SYNC.ordinal()))
                        attachmentImageView.setImageResource(getFileImage(workImpactAttachments.getType()));
                    else {
                        // download file
                        attachmentProgressBar.setVisibility(View.VISIBLE);
                        attachmentImageView.setVisibility(View.GONE);
                        downLoadPjDocumentFile(workImpactAttachments, attachmentImageView, false);
                    }
                }
                attachmentImageView.setOnClickListener(v -> {
//                    if (listener!=null){
//                        listener.onItemClick(getAdapterPosition());
//                    }
                    if (workImpactAttachments.getType().equalsIgnoreCase("png")
                            || workImpactAttachments.getType().equalsIgnoreCase("jpg")
                            || workImpactAttachments.getType().equalsIgnoreCase("jpeg")) {
                        FragmentManager fm = ((AppCompatActivity) mActivity).getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        RejectReasonAttachmentDialog attachmentDialog = new RejectReasonAttachmentDialog();
                        Bundle bundle = new Bundle();
                        bundle.putString("attachment_path", workImpactAttachments.getAttachmentPath());
                        bundle.putString("title_text", mActivity.getString(R.string.work_impact));
                        Log.e("Nitin", "Image path: " + workImpactAttachments.getAttachmentPath(), null);
                        if (mOnAddItemClick != null) {
                            bundle.putInt("image_position", position);
                            attachmentDialog.onAttachToParentFragment(mOnAddItemClick);
                        }
                        attachmentDialog.setArguments(bundle);
                        attachmentDialog.show(ft, "");

                    } else {
                        if (workImpactAttachments.getFileStatus() != null && workImpactAttachments.getFileStatus() == PDFSynEnum.SYNC.ordinal()) {
                            String fileName = "";
                            String fileExt = "";
                            String attachPath = workImpactAttachments.getAttachmentPath();
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
                            if (workImpactAttachments.getType().equalsIgnoreCase("rvt") ||
                                    (workImpactAttachments.getType().equalsIgnoreCase("msg")) ||
                                    workImpactAttachments.getType().equalsIgnoreCase("dwg")) {

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
                                openDocFiles(workImpactAttachments, localUrl, fileExt);
                            //openFileUri(localUrl, workImpactAttachments.getPjRfiOrigName(), workImpactAttachments);
                        } else {
                            attachmentImageView.setVisibility(View.GONE);
                            attachmentProgressBar.setVisibility(View.VISIBLE);
                            downLoadPjDocumentFile(workImpactAttachments, attachmentImageView, true);
                        }
                    }


                });

            } else {
                attachmentImageView.setVisibility(View.GONE);
                attachmentCardView.setVisibility(View.GONE);
                imageViewRemove.setVisibility(View.GONE);
                backgroundImageView.setVisibility(View.GONE);
                attachmentProgressBar.setVisibility(View.GONE);
            }


        }

        private void downLoadPjDocumentFile(WorkImpactAttachments docFile, ImageView attachmentImageView, boolean fromclick) {
            // open file here
            WrokImpactFileDownloadProvider fileDownloadProvider = new WrokImpactFileDownloadProvider(PronovosApplication.getContext(), mWorkImpactRepositor);
            fileDownloadProvider.getFileFromServer(docFile.getAttachmentPath(), docFile, new ProviderResult<Boolean>() {
                @Override
                public void success(Boolean result) {
                    Log.d("FileDoewnloadSuccess", "success: ");
                    docFile.setFileStatus(PDFSynEnum.SYNC.ordinal());
                    attachmentProgressBar.setVisibility(View.GONE);

                    //docFile.setIsSync(true);
                    docFile.setFileStatus(PDFSynEnum.SYNC.ordinal());
                    mWorkImpactRepositor.updateAttachment(docFile);

                    attachmentProgressBar.setVisibility(View.GONE);
                    attachmentImageView.setVisibility(View.VISIBLE);
                    attachmentImageView.setImageResource(getFileImage(docFile.getType()));

                }

                @Override
                public void AccessTokenFailure(String message) {
                    attachmentProgressBar.setVisibility(View.GONE);
                    docFile.setFileStatus(PDFSynEnum.SYNC_FAILED.ordinal());
                    attachmentImageView.setImageResource(getFileImage(docFile.getType()));
                    mWorkImpactRepositor.updateAttachment(docFile);
                    attachmentImageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void failure(String message) {
                    attachmentProgressBar.setVisibility(View.GONE);
                    docFile.setFileStatus(PDFSynEnum.SYNC_FAILED.ordinal());
                    mWorkImpactRepositor.updateAttachment(docFile);
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

        private void openDocFiles(WorkImpactAttachments docFile, String url, String fileExt) {
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
//            alertDialog.setTitle(mActivity.getString(R.string.message));
            alertDialog.setMessage(mActivity.getString(R.string.are_you_sure_you_want_to_permanently_remove_this_attachment));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, mActivity.getString(R.string.ok), (dialog, which) -> {
                alertDialog.dismiss();
                WorkImpactAttachments attachments = mWorkImpactAttachments.get(getAdapterPosition());
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
