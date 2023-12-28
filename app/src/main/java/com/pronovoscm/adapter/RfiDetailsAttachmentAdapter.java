package com.pronovoscm.adapter;

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
import com.pronovoscm.persistence.domain.PjRfiAttachments;
import com.pronovoscm.persistence.repository.ProjectRfiRepository;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.RfiFileDownloadProvider;
import com.pronovoscm.utils.dialogs.RfiImageFileDialog;
import com.pronovoscm.utils.ui.LoadImageInBackground;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class RfiDetailsAttachmentAdapter extends RecyclerView.Adapter {
    Activity mActivity;
    List<PjRfiAttachments> mRfiListAttachments;
    ProjectRfiRepository projectRfiRepository;
    private LoginResponse loginResponse;
    private int cornerRadius;
    private boolean isOffline;

    public RfiDetailsAttachmentAdapter(Activity activity, List<PjRfiAttachments> rfiListAttachments, boolean isOffline, ProjectRfiRepository projectRfiRepository) {
        ((PronovosApplication) activity.getApplication()).getDaggerComponent().inject(this);
        this.mRfiListAttachments = rfiListAttachments;
        this.mActivity = activity;
        this.isOffline = isOffline;
        this.projectRfiRepository = projectRfiRepository;
        cornerRadius = (int) (mActivity.getResources().getDimension(R.dimen.album_photo_radius) / mActivity.getResources().getDisplayMetrics().density);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.rfi_detail_attachment_item_layout, parent, false);
        return new RfiDetailsAttachmentHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RfiDetailsAttachmentHolder) holder).bind(mRfiListAttachments.get(position), new OnItemClickListener() {
            @Override
            public void onItemClick(int position, PjRfiAttachments attachments) {
                Log.d("AttachmentAdapter", "onItemClick: ");
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mRfiListAttachments != null && mRfiListAttachments.size() > 0) {
            return mRfiListAttachments.size();
        }
        return 0;
    }


    public interface OnItemClickListener {
        void onItemClick(int position, PjRfiAttachments pjRfiAttachments);
    }

    public class RfiDetailsAttachmentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.attachmentImageView)
        ImageView attachmentImageView;
        @BindView(R.id.backgroundImageView)
        ImageView backgroundImageView;
        @BindView(R.id.imageViewRemove)
        ImageView imageViewRemove;
        @BindView(R.id.attachmentProgressBar)
        ProgressBar attachmentProgressBar;
        AlertDialog alertDialog = null;

        public RfiDetailsAttachmentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(PjRfiAttachments pjRfiAttachments, OnItemClickListener listener) {
            backgroundImageView.setVisibility(View.VISIBLE);
            if (pjRfiAttachments.getType().equalsIgnoreCase("png")
                    || pjRfiAttachments.getType().equalsIgnoreCase("jpg")
                    || pjRfiAttachments.getType().equalsIgnoreCase("jpeg")) {
                backgroundImageView.setVisibility(View.VISIBLE);
                attachmentProgressBar.setVisibility(View.VISIBLE);
                attachmentImageView.setVisibility(View.VISIBLE);
                URI uri = null;
                try {
                    attachmentImageView.setImageResource(getFileImage(pjRfiAttachments.getType()));
                    uri = new URI(pjRfiAttachments.getAttachPath());
                    String[] segments = uri.getPath().split("/");
                    String imageName = segments[segments.length - 1];

//                    new Handler().postDelayed(() -> mLoadImage.getRoundedImagePath(workDetailsAttachments.getAttachmentPath(), workDetailsAttachments.getAttachmentPath(), imageName, attachmentImageView, attachmentProgressBar, false, backgroundImageView), 0);
                    String filePath = attachmentImageView.getContext().getFilesDir().getAbsolutePath() + Constants.RFI_ATTACHMENTS_PATH;
                    String[] params = new String[]{pjRfiAttachments.getAttachPath(), filePath};
//                    Object[] params = new Object[]{pjRfiAttachments.getAttachPath(), filePath,attachmentImageView.getContext(),attachmentImageView};
                    File dir = new File(filePath);
                    if (!dir.exists())
                        dir.mkdirs();
                    File imgFile = new File(filePath + "/" + imageName);
                    if (!imgFile.exists()) {
                        try {
                            new LoadImageInBackground(new LoadImageInBackground.Listener() {
                                @Override
                                public void onImageDownloaded(Bitmap bitmap) {
                                    attachmentImageView.setImageResource(getFileImage(pjRfiAttachments.getType()));
                                    backgroundImageView.setVisibility(View.GONE);
                                    attachmentProgressBar.setVisibility(View.GONE);
                                    pjRfiAttachments.setFileStatus(PDFSynEnum.SYNC.ordinal());
                                    pjRfiAttachments.setIsSync(true);
                                    projectRfiRepository.updatePjRfiAttachments(pjRfiAttachments);
                                }

                                @Override
                                public void onImageDownloadError() {
                                    Log.e("ADAPTER", getFileImage(pjRfiAttachments.getType()) + "   drawable  1212121212121 onImageDownloadError: " + pjRfiAttachments.getType());
                                    attachmentProgressBar.setVisibility(View.GONE);
                                    attachmentImageView.setImageResource(getFileImage(pjRfiAttachments.getType()));

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
                        attachmentImageView.setImageResource(getFileImage(pjRfiAttachments.getType()));
                    }

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else {
                attachmentProgressBar.setVisibility(View.GONE);
                if (pjRfiAttachments.getFileStatus() != null && pjRfiAttachments.getFileStatus() == (PDFSynEnum.SYNC.ordinal()))
                    attachmentImageView.setImageResource(getFileImage(pjRfiAttachments.getType()));
                else {
                    // download file
                    attachmentProgressBar.setVisibility(View.VISIBLE);
                    attachmentImageView.setVisibility(View.GONE);
                    downLoadPjDocumentFile(pjRfiAttachments, attachmentImageView, false);
                }
            }
            attachmentImageView.setOnClickListener(v -> {
                /*if (listener != null) {
                    listener.onItemClick(getAbsoluteAdapterPosition(), pjRfiAttachments);
                }*/

                if (pjRfiAttachments.getIsSync() != null && pjRfiAttachments.getIsSync()) {
                    String fileName = "";
                    String attachPath = pjRfiAttachments.getAttachPath();
                    try {
                        URI uri = new URI(attachPath);
                        String[] segments = uri.getPath().split("/");
                        fileName = segments[segments.length - 1];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String localUrl = mActivity.getFilesDir().getAbsolutePath() + Constants.RFI_ATTACHMENTS_PATH + fileName;
                    if (pjRfiAttachments.getType().equalsIgnoreCase("rvt") ||
                            (pjRfiAttachments.getType().equalsIgnoreCase("msg")) ||
                            pjRfiAttachments.getType().equalsIgnoreCase("dwg")) {

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
                        openFileUri(localUrl, pjRfiAttachments.getPjRfiOrigName(), pjRfiAttachments);
                } else {
                    attachmentImageView.setVisibility(View.GONE);
                    attachmentProgressBar.setVisibility(View.VISIBLE);
                    downLoadPjDocumentFile(pjRfiAttachments, attachmentImageView, true);
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

        public void openFileUri(String url, String fileNameTitle, PjRfiAttachments docFile) {
            Log.i("RfiAttachmentAdapter", fileNameTitle + "      openFileUri clickImageUrl: " + url);
            try {

                URI uri = new URI(url.toString());
                String[] segments = uri.getPath().split("/");
                String imageName = segments[segments.length - 1];
                String[] exts = imageName.split("[.]");
                String fileExt = exts[exts.length - 1];

                FragmentManager fm = ((AppCompatActivity) mActivity).getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                if (fileExt.equals("png") || fileExt.equals("jpg") || fileExt.equals("jpeg")) {
                    RfiImageFileDialog attachmentDialog = new RfiImageFileDialog();
                    attachmentDialog.setContext(mActivity);
                    Bundle bundle = new Bundle();
                    bundle.putString("attachment_path", url);
                    bundle.putString("title_text", fileNameTitle);
                    attachmentDialog.setArguments(bundle);
                    attachmentDialog.show(ft, "");

                } else {
                    openDocFiles(docFile, url, fileExt);

                }

       /* String filePath = url.toString().getFilesDir().getAbsolutePath() + "/Pronovos/Form/";
        String[] params = new String[]{url.toString(), filePath};*/
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }


        private void openDocFiles(PjRfiAttachments docFile, String url, String fileExt) {
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
                        .toolbarTitle(docFile.getPjRfiOrigName())
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


        private void downLoadPjDocumentFile(PjRfiAttachments docFile, ImageView attachmentImageView, boolean fromclick) {
            // open file here
            RfiFileDownloadProvider fileDownloadProvider = new RfiFileDownloadProvider(PronovosApplication.getContext(), projectRfiRepository);
            fileDownloadProvider.getFileFromServer(docFile.getAttachPath(), docFile, new ProviderResult<Boolean>() {
                @Override
                public void success(Boolean result) {
                    Log.d("FileDoewnloadSuccess", "success: ");
                    docFile.setFileStatus(PDFSynEnum.SYNC.ordinal());
                    attachmentProgressBar.setVisibility(View.GONE);

                    docFile.setIsSync(true);
                    docFile.setFileStatus(PDFSynEnum.SYNC.ordinal());
                    projectRfiRepository.updatePjRfiAttachments(docFile);

                    attachmentProgressBar.setVisibility(View.GONE);
                    attachmentImageView.setVisibility(View.VISIBLE);
                    attachmentImageView.setImageResource(getFileImage(docFile.getType()));

                }

                @Override
                public void AccessTokenFailure(String message) {
                    attachmentProgressBar.setVisibility(View.GONE);
                    docFile.setFileStatus(PDFSynEnum.SYNC_FAILED.ordinal());
                    attachmentImageView.setImageResource(getFileImage(docFile.getType()));
                    projectRfiRepository.updatePjRfiAttachments(docFile);
                    attachmentImageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void failure(String message) {
                    attachmentProgressBar.setVisibility(View.GONE);
                    docFile.setFileStatus(PDFSynEnum.SYNC_FAILED.ordinal());
                    projectRfiRepository.updatePjRfiAttachments(docFile);
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
