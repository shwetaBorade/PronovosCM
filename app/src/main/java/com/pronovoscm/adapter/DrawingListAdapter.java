package com.pronovoscm.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.pronovoscm.R;
import com.pronovoscm.activity.DrawingPDFActivity;
import com.pronovoscm.model.PDFSynEnum;
import com.pronovoscm.pdftron.PDFActivity;
import com.pronovoscm.persistence.domain.DrawingList;
import com.pronovoscm.utils.DateFormatter;
import com.pronovoscm.utils.ui.LoadImageInBackground;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class DrawingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int cornerRadius;
    boolean isOffline;
    int projectId;
    int drawingsId;
    private List<DrawingList> mDrawingList;
    private Context context;
//    private LoadImage mLoadImage;

    public DrawingListAdapter(Context context, List<DrawingList> mDrawingLists, boolean isOffline, int projectId, Integer drawingsId) {
        this.mDrawingList = mDrawingLists;
        this.isOffline = isOffline;
        this.drawingsId = drawingsId;
        this.context = context;
        this.projectId = projectId;
//        mLoadImage = new LoadImage(context);
        setHasStableIds(true);
        cornerRadius = (int) (context.getResources().getDimension(R.dimen.album_photo_radius) / context.getResources().getDisplayMetrics().density);

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.drawing_list_item, parent, false);
        return new DrawingListHolder(view);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((DrawingListHolder) holder).bind();
    }

    @Override
    public int getItemCount() {
        if (mDrawingList != null) {
            return mDrawingList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {

        return position;

    }

    public void deviceOffline(boolean b) {
        isOffline = b;
        notifyDataSetChanged();
    }

    public class DrawingListHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.listImageView)
        ImageView listImageView;
        @BindView(R.id.backImageView)
        ImageView backImageView;
        @BindView(R.id.listNameTextView)
        TextView listNameTextView;
        @BindView(R.id.createdDateTextView)
        TextView createdDateTextView;
        @BindView(R.id.revTextView)
        TextView revTextView;
        @BindView(R.id.offlineView)
        RelativeLayout offlineView;
        @BindView(R.id.listImageProgressBar)
        ProgressBar listImageProgressBar;
        @BindView(R.id.listCardView)
        CardView listCardView;
        @BindView(R.id.syncTextView)
        TextView syncTextView;
        @BindView(R.id.syncImageView)
        ImageView syncImageView;
        @BindView(R.id.space)
        Space space;
        @BindView(R.id.progressView)
        ProgressBar progressView;

        public DrawingListHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind() {
            DrawingList drawingList = mDrawingList.get(getAdapterPosition());
            listNameTextView.setText(drawingList.getDrawingName() + " - " + drawingList.getDescriptions());
            revTextView.setText(String.valueOf(drawingList.getRevisitedNum()));
            if ((getAdapterPosition() == 0
                    || !(mDrawingList.get(getAdapterPosition())
                    .getDrawingDiscipline().equals(mDrawingList.get(getAdapterPosition() - 1)
                            .getDrawingDiscipline()))) && drawingsId == -1) {
                space.setVisibility(View.VISIBLE);
            } else {
                space.setVisibility(View.GONE);
            }

            if (drawingList.getDrawingDate() != null) {
                createdDateTextView.setText(DateFormatter.formatDateForImage(drawingList.getDrawingDate()));
            }
            if (isOffline) {
                syncImageView.setImageResource(R.drawable.ic_sync_offline_image);
                if (drawingList.getPdfStatus() == PDFSynEnum.NOTSYNC.ordinal()) {
                    offlineView.setVisibility(View.VISIBLE);
                    syncImageView.setVisibility(View.VISIBLE);
                    progressView.setVisibility(View.GONE);
                    syncTextView.setVisibility(View.INVISIBLE);

                } else if (drawingList.getPdfStatus() == PDFSynEnum.SYNC.ordinal()) {
                    offlineView.setVisibility(View.GONE);
                    syncImageView.setVisibility(View.INVISIBLE);
                    syncTextView.setVisibility(View.VISIBLE);
                    progressView.setVisibility(View.GONE);
                } else {
                    offlineView.setVisibility(View.VISIBLE);
                    syncImageView.setVisibility(View.VISIBLE);
                    progressView.setVisibility(View.GONE);
                    syncTextView.setVisibility(View.INVISIBLE);

                }

            } else {
                offlineView.setVisibility(View.GONE);
                syncImageView.setImageResource(R.drawable.ic_sync_image);
                if (drawingList.getPdfStatus() == PDFSynEnum.SYNC.ordinal()) {
                    syncImageView.setVisibility(View.INVISIBLE);
                    progressView.setVisibility(View.GONE);
                    syncTextView.setVisibility(View.VISIBLE);
                    listCardView.setClickable(true);
                } else if (drawingList.getPdfStatus() == PDFSynEnum.PROCESSING.ordinal()) {
                    syncImageView.setVisibility(View.INVISIBLE);
                    syncTextView.setVisibility(View.INVISIBLE);
                    listCardView.setClickable(false);
                    progressView.setVisibility(View.VISIBLE);
                } else {
                    syncTextView.setVisibility(View.INVISIBLE);
                    syncImageView.setVisibility(View.VISIBLE);
                    listCardView.setClickable(true);
                    progressView.setVisibility(View.GONE);
                }


            }
            listCardView.setOnClickListener(v -> {
                try {
                    if (mDrawingList.size() > getAdapterPosition() && getAdapterPosition() != -1) {
                        if ((drawingList.getPdfStatus() == PDFSynEnum.NOTSYNC.ordinal() ||
                                drawingList.getPdfStatus() == PDFSynEnum.SYNC_FAILED.ordinal()) && !isOffline) {
                            mDrawingList.get(getAdapterPosition()).setPdfStatus(PDFSynEnum.PROCESSING.ordinal());
                            syncImageView.setVisibility(View.INVISIBLE);
                            progressView.setVisibility(View.VISIBLE);
                            EventBus.getDefault().post(drawingList);
                        } else if (drawingList.getPdfStatus() == PDFSynEnum.SYNC.ordinal()) {
//                            context.startActivity(new Intent(context, SampleActivity.class).
                            context.startActivity(new Intent(context, DrawingPDFActivity.class).
                                    putExtra("drawing_name", drawingList.getDrawingName()).
                                    putExtra("drawing_folder_id", drawingList.getDrwFoldersId()).
                                    putExtra("drawing_id", drawingList.getId()).
                                    putExtra("projectId", projectId).
                                    putExtra("drawing_rev_no", drawingList.getRevisitedNum()));
//                        context.startActivity(new Intent(context, PDFActivity.class));
                        }
                    }

                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            });

            URI uri = null;
            try {
                uri = new URI(drawingList.getImageThumb() != null ? drawingList.getImageThumb() : drawingList.getImageOrg());
                String[] segments = uri.getPath().split("/");
                String imageName = segments[segments.length - 1];
                listImageView.setImageResource(R.drawable.ic_blank);
                String filePath = listImageView.getContext().getFilesDir().getAbsolutePath() + "/Pronovos";
                File imgFile = new File(filePath + "/" + imageName);
                if (!imgFile.exists()) {
                    try {
                        String[] params = new String[]{drawingList.getImageThumb() != null ? drawingList.getImageThumb() :
                                drawingList.getImageOrg(), filePath};

                        /*Object[] params = new Object[]{drawingList.getImageThumb() != null ? drawingList.getImageThumb() :
                                drawingList.getImageOrg(), filePath,listImageView.getContext(),listImageView};*/
                        new LoadImageInBackground(new LoadImageInBackground.Listener() {
                            @Override
                            public void onImageDownloaded(Bitmap bitmap) {
                                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(listImageView.getContext().getResources(), bitmap);
//                            roundedBitmapDrawable.setCircular(true);
                                roundedBitmapDrawable.setCornerRadius(cornerRadius);
                                listImageView.setImageDrawable(roundedBitmapDrawable);
                                backImageView.setVisibility(View.GONE);
                                listImageProgressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onImageDownloadError() {
                                listImageProgressBar.setVisibility(View.GONE);
                            }
                        }).executeOnExecutor(THREAD_POOL_EXECUTOR, params);
                    } catch (RejectedExecutionException e) {
                        e.printStackTrace();
                    }

                } else {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(listImageView.getContext().getResources(), myBitmap);
                    roundedBitmapDrawable.setCornerRadius(cornerRadius);
                    listImageView.setImageDrawable(roundedBitmapDrawable);
                    backImageView.setVisibility(View.GONE);
                    listImageProgressBar.setVisibility(View.GONE);

                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }


//            }, 0);
    }
}


