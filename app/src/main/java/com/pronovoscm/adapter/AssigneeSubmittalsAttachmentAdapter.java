package com.pronovoscm.adapter;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.DocumentActivity;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.model.PDFSynEnum;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.PjAssigneeAttachments;
import com.pronovoscm.persistence.repository.ProjectSubmittalsRepository;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.SubmittalsAssigneeFileDownloadProvider;
import com.pronovoscm.utils.dialogs.SubmittalsImageFileDialog;
import com.pronovoscm.utils.ui.LoadImageInBackground;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AssigneeSubmittalsAttachmentAdapter extends RecyclerView.Adapter {
    Activity mActivity;
    List<PjAssigneeAttachments> pjSubmittalAttachments;
    ProjectSubmittalsRepository projectSubmittalsRepository;
    private LoginResponse loginResponse;
    private int cornerRadius;
    private boolean isOffline;

    public AssigneeSubmittalsAttachmentAdapter(Activity activity, List<PjAssigneeAttachments> submittalAttachmentsList, boolean isOffline, ProjectSubmittalsRepository projectSubmittalsRepository) {
        ((PronovosApplication) activity.getApplication()).getDaggerComponent().inject(this);
        this.pjSubmittalAttachments = submittalAttachmentsList;
        this.mActivity = activity;
        this.isOffline = isOffline;
        this.projectSubmittalsRepository = projectSubmittalsRepository;
        cornerRadius = (int) (mActivity.getResources().getDimension(R.dimen.album_photo_radius) / mActivity.getResources().getDisplayMetrics().density);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.submittal_detail_att_item, parent, false);
        return new AssigneeSubmittalsAttachmentHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((AssigneeSubmittalsAttachmentHolder) holder).bind(pjSubmittalAttachments.get(position), new OnItemClickListener() {
            @Override
            public void onItemClick(int position, PjAssigneeAttachments pjSubmittalAttachments) {
                Log.d("AttachmentAdapter", "onItemClick: ");
            }
        });
    }

    @Override
    public int getItemCount() {
        if (pjSubmittalAttachments != null && pjSubmittalAttachments.size() > 0) {
            return pjSubmittalAttachments.size();
        }
        return 0;
    }


    public interface OnItemClickListener {
        void onItemClick(int position, PjAssigneeAttachments pjSubmittalAttachments);
    }

    public class AssigneeSubmittalsAttachmentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.attachmentImageView)
        ImageView attachmentImageView;
        @BindView(R.id.backgroundImageView)
        ImageView backgroundImageView;
        @BindView(R.id.imageViewRemove)
        ImageView imageViewRemove;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.attachmentProgressBar)
        ProgressBar attachmentProgressBar;
        AlertDialog alertDialog = null;

        public AssigneeSubmittalsAttachmentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(PjAssigneeAttachments pjSubmittalAttachments, OnItemClickListener listener) {
            backgroundImageView.setVisibility(View.VISIBLE);
            if (pjSubmittalAttachments != null && pjSubmittalAttachments.getType() != null) {
                if (pjSubmittalAttachments.getType().equalsIgnoreCase("png")
                        || pjSubmittalAttachments.getType().equalsIgnoreCase("jpg")
                        || pjSubmittalAttachments.getType().equalsIgnoreCase("jpeg")) {
                    backgroundImageView.setVisibility(View.VISIBLE);
                    attachmentProgressBar.setVisibility(View.VISIBLE);
                    attachmentImageView.setVisibility(View.VISIBLE);
                    URI uri = null;
                    try {
                        attachmentImageView.setImageResource(getFileImage(pjSubmittalAttachments.getType()));
                        uri = new URI(pjSubmittalAttachments.getAttachPath());
                        String[] segments = uri.getPath().split("/");
                        String imageName = segments[segments.length - 1];

                        String filePath = attachmentImageView.getContext().getFilesDir().getAbsolutePath() + Constants.SUBMITTALS_ATTACHMENTS_PATH;
                        String[] params = new String[]{pjSubmittalAttachments.getAttachPath(), filePath};
                        File dir = new File(filePath);
                        if (!dir.exists())
                            dir.mkdirs();
                        File imgFile = new File(filePath + "/" + imageName);
                        if (!imgFile.exists()) {
                            try {
                                new LoadImageInBackground(new LoadImageInBackground.Listener() {
                                    @Override
                                    public void onImageDownloaded(Bitmap bitmap) {
                                        attachmentImageView.setImageResource(getFileImage(pjSubmittalAttachments.getType()));
                                        backgroundImageView.setVisibility(View.GONE);
                                        attachmentProgressBar.setVisibility(View.GONE);
                                        pjSubmittalAttachments.setSyncStatus(PDFSynEnum.SYNC.ordinal());
                                        pjSubmittalAttachments.setIsAwsSync(true);
                                        projectSubmittalsRepository.updatePjSubmittalAssigneeAtt(pjSubmittalAttachments);
                                    }

                                    @Override
                                    public void onImageDownloadError() {
                                        Log.e("ADAPTER", getFileImage(pjSubmittalAttachments.getType()) + "   drawable  1212121212121 onImageDownloadError: " + pjSubmittalAttachments.getType());
                                        attachmentProgressBar.setVisibility(View.GONE);
                                        attachmentImageView.setImageResource(getFileImage(pjSubmittalAttachments.getType()));

                                    }
                                }).executeOnExecutor(THREAD_POOL_EXECUTOR, params);
                            } catch (RejectedExecutionException e) {
                                e.printStackTrace();
                                Log.e("ADAPTER", "@@@@@@@@@@@@@@@@@ onImageDownloadError: ");
                            }

                        } else {
                            //  Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            //  RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(attachmentImageView.getContext().getResources(), myBitmap);
                            //  roundedBitmapDrawable.setCornerRadius(cornerRadius);
                            //  attachmentImageView.setImageDrawable(roundedBitmapDrawable);
                            attachmentProgressBar.setVisibility(View.GONE);
                            attachmentImageView.setImageResource(getFileImage(pjSubmittalAttachments.getType()));
                        }

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    attachmentProgressBar.setVisibility(View.GONE);
                    if (pjSubmittalAttachments.getSyncStatus() != null && pjSubmittalAttachments.getSyncStatus() == (PDFSynEnum.SYNC.ordinal()))
                        attachmentImageView.setImageResource(getFileImage(pjSubmittalAttachments.getType()));
                    else {
                        // download file
                        attachmentProgressBar.setVisibility(View.VISIBLE);
                        attachmentImageView.setVisibility(View.GONE);
                        downLoadPjDocumentFile(pjSubmittalAttachments, attachmentImageView, false);
                    }
                }
            }
            if (pjSubmittalAttachments != null && pjSubmittalAttachments.getOriginalName() != null) {
                tvName.setText(pjSubmittalAttachments.getOriginalName());
            }
            attachmentImageView.setOnClickListener(v -> {
                /*if (listener != null) {
                    listener.onItemClick(getAbsoluteAdapterPosition(), pjSubmittalAttachments);
                }*/

                if (pjSubmittalAttachments.getIsAwsSync() != null && pjSubmittalAttachments.getIsAwsSync()) {
                    String fileName = "";
                    String attachPath = pjSubmittalAttachments.getAttachPath();
                    try {
                        URI uri = new URI(attachPath);
                        String[] segments = uri.getPath().split("/");
                        fileName = segments[segments.length - 1];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String localUrl = mActivity.getFilesDir().getAbsolutePath() + Constants.SUBMITTALS_ATTACHMENTS_PATH + fileName;
                    if (pjSubmittalAttachments.getType().equalsIgnoreCase("rvt") ||
                            (pjSubmittalAttachments.getType().equalsIgnoreCase("msg")) ||
                            pjSubmittalAttachments.getType().equalsIgnoreCase("dwg")) {

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
                    } else if (pjSubmittalAttachments.getType().equalsIgnoreCase("pdf")) {
                        String localUrlPdf = mActivity.getFilesDir().getAbsolutePath() + "/Pronovos/PDF/" + fileName;
                        openFileUri(localUrlPdf, pjSubmittalAttachments.getOriginalName(), pjSubmittalAttachments);
                    } else {
                        openFileUri(localUrl, pjSubmittalAttachments.getOriginalName(), pjSubmittalAttachments);
                    }

                } else {
                    attachmentImageView.setVisibility(View.GONE);
                    attachmentProgressBar.setVisibility(View.VISIBLE);
                    downLoadPjDocumentFile(pjSubmittalAttachments, attachmentImageView, true);
                }
            });
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

        public void openFileUri(String url, String fileNameTitle, PjAssigneeAttachments docFile) {
            Log.i("SubmittalsAttachmentAdapter", fileNameTitle + "      openFileUri clickImageUrl: " + url);
            try {

                URI uri = new URI(url.toString());
                String[] segments = uri.getPath().split("/");
                String imageName = segments[segments.length - 1];
                String[] exts = imageName.split("[.]");
                String fileExt = exts[exts.length - 1];

                FragmentManager fm = ((AppCompatActivity) mActivity).getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                if (fileExt.equals("png") || fileExt.equals("jpg") || fileExt.equals("jpeg")) {
                    SubmittalsImageFileDialog attachmentDialog = new SubmittalsImageFileDialog();
                    attachmentDialog.setContext(mActivity);
                    Bundle bundle = new Bundle();
                    bundle.putString("attachment_path", url);
                    bundle.putString("title_text", fileNameTitle);
                    attachmentDialog.setArguments(bundle);
                    attachmentDialog.show(ft, "");

                } else {
                    openDocFiles(docFile, url, fileExt);

                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }


        private void openDocFiles(PjAssigneeAttachments docFile, String url, String fileExt) {
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
                        .toolbarTitle(docFile.getOriginalName())
                        .showSearchView(false)
                        .autoHideToolbarEnabled(true)
                        .showOpenFileOption(false)
                        .showThumbnailView(false)
                        .showAnnotationsList(false)
                        .showBottomNavBar(false)
                        .showEditPagesOption(false)
                        .showTopToolbar(false)
                        .useStandardLibrary(true)
                        .build();

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


        private void downLoadPjDocumentFile(PjAssigneeAttachments docFile, ImageView attachmentImageView, boolean fromclick) {
            // open file here
            SubmittalsAssigneeFileDownloadProvider fileDownloadProvider = new SubmittalsAssigneeFileDownloadProvider(PronovosApplication.getContext(), projectSubmittalsRepository);
            fileDownloadProvider.getFileFromServer(docFile.getAttachPath(), docFile, new ProviderResult<Boolean>() {
                @Override
                public void success(Boolean result) {
                    Log.d("FileDoewnloadSuccess", "success: ");
                    docFile.setSyncStatus(PDFSynEnum.SYNC.ordinal());
                    attachmentProgressBar.setVisibility(View.GONE);

                    docFile.setIsAwsSync(true);
                    docFile.setSyncStatus(PDFSynEnum.SYNC.ordinal());
                    projectSubmittalsRepository.updatePjSubmittalAssigneeAtt(docFile);
                    attachmentProgressBar.setVisibility(View.GONE);
                    attachmentImageView.setVisibility(View.VISIBLE);
                    attachmentImageView.setImageResource(getFileImage(docFile.getType()));

                }

                @Override
                public void AccessTokenFailure(String message) {
                    attachmentProgressBar.setVisibility(View.GONE);
                    docFile.setSyncStatus(PDFSynEnum.SYNC_FAILED.ordinal());
                    attachmentImageView.setImageResource(getFileImage(docFile.getType()));
                    projectSubmittalsRepository.updatePjSubmittalAssigneeAtt(docFile);
                    attachmentImageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void failure(String message) {
                    attachmentProgressBar.setVisibility(View.GONE);
                    docFile.setSyncStatus(PDFSynEnum.SYNC_FAILED.ordinal());
                    projectSubmittalsRepository.updatePjSubmittalAssigneeAtt(docFile);
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
    }


}
