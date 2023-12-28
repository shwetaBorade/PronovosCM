package com.pronovoscm.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.DocumentActivity;
import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.activity.ProjectDocumentsFilesActivity;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.model.PDFSynEnum;
import com.pronovoscm.persistence.domain.PjDocumentsFiles;
import com.pronovoscm.persistence.repository.ProjectDocumentsRepository;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.DocumentsFolderFileAdapterItem;
import com.pronovoscm.utils.FileDownloadProvider;
import com.pronovoscm.utils.dialogs.DocumentFileDialog;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class DocumentFolderFilesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private boolean isOffline;
    private int projectId;
    private ArrayList<DocumentsFolderFileAdapterItem> adapterItemList;
    private Context mActivity;
    private ProjectDocumentsRepository projectDocumentsRepository;

    public DocumentFolderFilesListAdapter(int projectId, ArrayList<DocumentsFolderFileAdapterItem> adapterItemLis,
                                          Context mActivity, boolean isOffline, ProjectDocumentsRepository projectDocumentsRepository) {
        this.projectId = projectId;
        this.adapterItemList = adapterItemLis;
        this.mActivity = mActivity;
        this.isOffline = isOffline;
        this.projectDocumentsRepository = projectDocumentsRepository;

    }

    public void deviceOffline(boolean b) {
        isOffline = b;
        notifyDataSetChanged();
    }

    public void setAdapterItemList(ArrayList<DocumentsFolderFileAdapterItem> itemList) {
        adapterItemList = itemList;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == Constants.ADAPTER_ITEM_TYPE_DOCUMENT_FOLDER) {
            View folderView = inflater.inflate(R.layout.document_folder_list_item, parent, false);
            return new DocumentFolderHolder(folderView);
        } else {
            View fileView = inflater.inflate(R.layout.document_file_list_item, parent, false);
            return new DocumentFileHolder(fileView);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //  Log.d("FolderAdapter", "onBindViewHolder:position  " + position);
        if (adapterItemList.get(position).getAdapterItemType() == Constants.ADAPTER_ITEM_TYPE_DOCUMENT_FOLDER) { // put your condition, according to your requirements
            ((DocumentFolderHolder) holder).bind(position);
        } else {
            ((DocumentFileHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemCount() {
        // Log.d("FolderAdapter", "getItemCount: " + adapterItemList.size());
        if (adapterItemList != null)
            return adapterItemList.size();
        else
            return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return adapterItemList.get(position).getAdapterItemType();
    }

    public class DocumentFolderHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.folderNameTextView)
        TextView folderNameTextView;
        @BindView(R.id.folderCardView)
        CardView folderCardView;
        DocumentsFolderFileAdapterItem item;

        public DocumentFolderHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bind(int position) {
            item = adapterItemList.get(position);
            folderNameTextView.setText(item.getPjDocumentsFolders().getName());
            folderCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    mActivity.startActivity(new Intent(mActivity, DrawingListActivity.class).putExtra("drw_folder_id",drawingFolderList.get(getAdapterPosition()).getDrwFoldersId()).putExtra("project_id",projectId));
                    if (item.getAdapterItemType() == Constants.ADAPTER_ITEM_TYPE_DOCUMENT_FOLDER) {

                        Intent fileFolderIntent = new Intent(mActivity, ProjectDocumentsFilesActivity.class);
                        fileFolderIntent.putExtra(Constants.INTENT_KEY_PROJECT_ID, projectId);
                        fileFolderIntent.putExtra(Constants.INTENT_KEY_PROJECT_DOCUMENT_FOLDER_ID, item.getPjDocumentsFolders().pjDocumentsFoldersId);
                        fileFolderIntent.putExtra(Constants.INTENT_KEY_PROJECT_DOCUMENT_FOLDER, item.getPjDocumentsFolders());
                        mActivity.startActivity(fileFolderIntent);
                    }
                }
            });
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

    public class DocumentFileHolder extends RecyclerView.ViewHolder {
        public boolean isXLSFile = false;
        DocumentsFolderFileAdapterItem item;
        @BindView(R.id.listImageView)
        ImageView imageView;
        @BindView(R.id.progressView)
        ProgressBar progressView;
        @BindView(R.id.syncImageView)
        ImageView syncImageView;

        @BindView(R.id.listNameTextView)
        TextView listNameTextView;
        @BindView(R.id.syncTextView)
        TextView syncTextView;
        @BindView(R.id.listCardView)
        CardView fileCardView;

        @BindView(R.id.offlineView)
        RelativeLayout offlineView;

        public DocumentFileHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bind(int position) {
            item = adapterItemList.get(position);
            listNameTextView.setText(item.getPjDocumentsFiles().originalName);

            PjDocumentsFiles docFile = item.getPjDocumentsFiles();

            if (docFile.getIsSync() != null && docFile.getIsSync()) {
                syncImageView.setVisibility(View.GONE);
                syncTextView.setText(R.string.synced);
                syncTextView.setVisibility(View.VISIBLE);
                setClickListner(docFile);
                imageView.setImageResource(getFileImage(item.getPjDocumentsFiles()));
            } else {
                if (isOffline) {
                    offlineView.setVisibility(View.VISIBLE);
                    // offlineView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.gray_cccccc));
                    // syncImageView.setVisibility(View.GONE);
                    imageView.setVisibility(View.INVISIBLE);
                    syncTextView.setVisibility(View.GONE);
                    syncImageView.setImageResource(R.drawable.ic_sync_offline_image);
                    //syncTextView.setText(R.string.unavailable_offline);
                } else {
                    imageView.setImageResource(getFileImage(item.getPjDocumentsFiles()));
                    imageView.setVisibility(View.VISIBLE);

                    syncTextView.setVisibility(View.GONE);
                    offlineView.setVisibility(View.GONE);
                    syncImageView.setVisibility(View.VISIBLE);
                    syncImageView.setImageResource(R.drawable.ic_sync_image);
                    //syncTextView.setText(R.string.synced);

                    if (docFile.getFileStatus() == PDFSynEnum.PROCESSING.ordinal()) {
                        progressView.setVisibility(View.VISIBLE);
                        syncImageView.setVisibility(View.GONE);
                    }
                }
                if (!isOffline)
                    setClickListner(docFile);
            }


        }

        private void downLoadPjDocumentFile(PjDocumentsFiles docFile) {
            // open file here
            FileDownloadProvider fileDownloadProvider = new FileDownloadProvider(PronovosApplication.getContext(), projectDocumentsRepository);
            fileDownloadProvider.getFileFromServer(docFile.getLocation(), docFile, new ProviderResult<Boolean>() {
                @Override
                public void success(Boolean result) {
                    Log.d("FileDoewnloadSuccess", "success: ");
                    docFile.setFileStatus(PDFSynEnum.SYNC.ordinal());
                    progressView.setVisibility(View.GONE);
                    PjDocumentsFiles syncRevision = projectDocumentsRepository.getPjDocumentsFilesSyncRevision(docFile.getPjProjectsId(), docFile.originalPjDocumentsFilesId);
                    if (syncRevision != null && !(syncRevision.getPjDocumentsFilesId().equals(docFile.getPjDocumentsFilesId()))) {
                        syncRevision.setSync(false);
                        syncRevision.setFileStatus(PDFSynEnum.NOTSYNC.ordinal());
                        projectDocumentsRepository.updateDocumentFile(syncRevision);
                    }
                    docFile.setIsSync(true);
                    docFile.setFileStatus(PDFSynEnum.SYNC.ordinal());
                    projectDocumentsRepository.updateDocumentFile(docFile);

                    syncImageView.setVisibility(View.GONE);
                    syncTextView.setText(R.string.synced);
                    syncTextView.setVisibility(View.VISIBLE);

                }

                @Override
                public void AccessTokenFailure(String message) {
                    syncImageView.setVisibility(View.VISIBLE);
                    progressView.setVisibility(View.GONE);
                    docFile.setFileStatus(PDFSynEnum.SYNC_FAILED.ordinal());
                    projectDocumentsRepository.updateDocumentFile(docFile);
                }

                @Override
                public void failure(String message) {
                    syncImageView.setVisibility(View.VISIBLE);
                    progressView.setVisibility(View.GONE);
                    docFile.setFileStatus(PDFSynEnum.SYNC_FAILED.ordinal());
                    projectDocumentsRepository.updateDocumentFile(docFile);
                }
            });

        }

        private void setClickListner(PjDocumentsFiles docFile) {
            syncImageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    downLoadPjDocumentFile(docFile);
                }
            });
            fileCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("DocumentFileHolder", "onClick: " + item.getPjDocumentsFiles().toString());
                    if (isOffline) {
                        if (docFile.getFileStatus() == PDFSynEnum.SYNC.ordinal()) {
                            String localUrl = mActivity.getFilesDir().getAbsolutePath() + Constants.DOCUMENT_FILES_PATH + docFile.getName();
                            openFileUri(localUrl, docFile.getOriginalName(), docFile);
                        } else if (docFile.getIsSync() != null && docFile.getIsSync()) {
                            // open file here
                            String localUrl = mActivity.getFilesDir().getAbsolutePath() + Constants.DOCUMENT_FILES_PATH + docFile.getName();
                            openFileUri(localUrl, docFile.getOriginalName(), docFile);
                        } else {
                            PjDocumentsFiles syncRevision = projectDocumentsRepository.getPjDocumentsFilesSyncRevision(docFile.getPjProjectsId(), docFile.originalPjDocumentsFilesId);
                            if (syncRevision != null) {
                                String localUrl = mActivity.getFilesDir().getAbsolutePath() + Constants.DOCUMENT_FILES_PATH + syncRevision.getName();
                                openFileUri(localUrl, syncRevision.getOriginalName(), syncRevision);
                            }
                        }
                    } else {
                        if (docFile.getFileStatus() == PDFSynEnum.SYNC.ordinal()) {
                            String localUrl = mActivity.getFilesDir().getAbsolutePath() + Constants.DOCUMENT_FILES_PATH + docFile.getName();
                            openFileUri(localUrl, docFile.getOriginalName(), docFile);
                        } else if (docFile.getIsSync() == null || !docFile.getIsSync()) {
                            //now check for old sync revision
                            PjDocumentsFiles syncRevision = projectDocumentsRepository.getPjDocumentsFilesSyncRevision(docFile.getPjProjectsId(), docFile.originalPjDocumentsFilesId);
                            if (syncRevision != null) {
                                String localUrl = mActivity.getFilesDir().getAbsolutePath() + Constants.DOCUMENT_FILES_PATH + syncRevision.getName();
                                openFileUri(localUrl, syncRevision.getOriginalName(), syncRevision);
                            } else {
                                docFile.setFileStatus(PDFSynEnum.PROCESSING.ordinal());
                                progressView.setVisibility(View.VISIBLE);
                                syncImageView.setVisibility(View.GONE);
                                projectDocumentsRepository.updateDocumentFile(docFile);
                                downLoadPjDocumentFile(docFile);
                            }

                        } else {
                            // open file
                            String localUrl = mActivity.getFilesDir().getAbsolutePath() + Constants.DOCUMENT_FILES_PATH + docFile.getName();
                            openFileUri(localUrl, docFile.getOriginalName(), docFile);
                        }
                    }
                }
            });
        }

        private int getFileImage(PjDocumentsFiles pjDocumentsFiles) {
            if (pjDocumentsFiles.getType().equalsIgnoreCase("zip")) {
                return R.drawable.ic_zip;
            } else if (pjDocumentsFiles.getType().equalsIgnoreCase("pdf")) {
                return R.drawable.ic_file_pdf;
            } else if (pjDocumentsFiles.getType().equalsIgnoreCase("xls")) {
                return R.drawable.ic_file_xls;
            } else if (pjDocumentsFiles.getType().equalsIgnoreCase("xlsm")) {
                return R.drawable.ic_file_xlsm;
            } else if (pjDocumentsFiles.getType().equalsIgnoreCase("xlsx")) {
                return R.drawable.ic_file_xlsx;
            } else if (pjDocumentsFiles.getType().equalsIgnoreCase("doc")) {
                return R.drawable.ic_doc;
            } else if (pjDocumentsFiles.getType().equalsIgnoreCase("docx")) {
                return R.drawable.ic_file_docx;
            } else if (pjDocumentsFiles.getType().equalsIgnoreCase("docm")) {
                return R.drawable.ic_docm;
            } else if (pjDocumentsFiles.getType().equalsIgnoreCase("ppt")) {
                return R.drawable.ic_file_ppt;
            } else if (pjDocumentsFiles.getType().equalsIgnoreCase("pptx")) {
                return R.drawable.ic_pptx;
            } else if (pjDocumentsFiles.getType().equalsIgnoreCase("mp4")) {
                return R.drawable.ic_file_audio;
            } else if (pjDocumentsFiles.getType().equalsIgnoreCase("jpeg")) {
                return R.drawable.ic_file_jpeg;
            } else if (pjDocumentsFiles.getType().equalsIgnoreCase("jpg")) {
                return R.drawable.ic_file_jpeg;
            } else if (pjDocumentsFiles.getType().equalsIgnoreCase("png")) {
                return R.drawable.ic_png;
            } else if (pjDocumentsFiles.getType().equalsIgnoreCase("txt")) {
                return R.drawable.ic_file_txt;
            } else if (pjDocumentsFiles.getType().equalsIgnoreCase("mpp")) {
                return R.drawable.ic_mpp;
            } else {
                return R.drawable.ic_file_txt;
            }
        }


        public void openFileUri(String url, String fileNameTitle, PjDocumentsFiles docFile) {
            Log.i("FolderAdapter", fileNameTitle + "      openFileUri clickImageUrl: " + url);
            try {

                URI uri = new URI(url.toString());
                String[] segments = uri.getPath().split("/");
                String imageName = segments[segments.length - 1];
                String[] exts = imageName.split("[.]");
                FragmentManager fm = ((AppCompatActivity) mActivity).getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                String fileExt = exts[exts.length - 1];
                if (fileExt.equals("png") || fileExt.equals("jpg") || fileExt.equals("jpeg")) {
                    DocumentFileDialog attachmentDialog = new DocumentFileDialog();
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

        private void openDocFiles(PjDocumentsFiles docFile, String url, String fileExt) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();

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
            } else if (fileExt.equals("mpp")) {
                intent.setDataAndType(docUri, "application/vnd.ms-project, application/msproj, application/msproject, application/x-msproject, " +
                        " application/x-ms-project, application/x-dos_ms_project, application/mpp, zz-application/zz-winassoc-mpp");

                openActivity(intent);
            }
        }

    }
}
