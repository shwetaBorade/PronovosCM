package com.pronovoscm.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.R;
import com.pronovoscm.activity.AlbumsPhotoActivity;
import com.pronovoscm.data.ProjectsProvider;
import com.pronovoscm.persistence.domain.AlbumCoverPhoto;
import com.pronovoscm.persistence.domain.PhotoFolder;
import com.pronovoscm.utils.ui.LoadImageInBackground;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int cornerRadius;
    @Inject
    ProjectsProvider projectsProvider;
    private List<PhotoFolder> mAlbumsList;
    private Activity mActivity;
    private long mLastClickTime = 0;

    public AlbumAdapter(Activity mActivity, List<PhotoFolder> albumsList) {
        this.mAlbumsList = albumsList;
        this.mActivity = mActivity;
        ((PronovosApplication) mActivity.getApplication()).getDaggerComponent().inject(this);
        cornerRadius = (int) (mActivity.getResources().getDimension(R.dimen.album_photo_radius) / mActivity.getResources().getDisplayMetrics().density);
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType != -1) {
            View view = inflater.inflate(R.layout.album_item_list, parent, false);
            return new AlbumHolder(view);
        } else {
            View view = inflater.inflate(R.layout.album_item_header, parent, false);
            return new AddAlbumHolder(view);

        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AlbumHolder) {
            ((AlbumHolder) holder).bind();
        } else {
            ((AddAlbumHolder) holder).bind();
        }
    }

    @Override
    public int getItemCount() {
        if (mAlbumsList != null) {
            return mAlbumsList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mAlbumsList.get(position) == null) {
            return -1;
        } else {
            return position;
        }
    }

    public interface addNewFolder {
        void onAddNewFolder();
    }

    public class AlbumHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.albumTextView)
        TextView albumNameTextView;
        @BindView(R.id.countPhotoTextView)
        TextView countPhotoTextView;
        @BindView(R.id.albumImageView)
        ImageView albumImageView;
        @BindView(R.id.backImageView)
        ImageView backgroundImageView;
        @BindView(R.id.albumCardView)
        CardView albumCardView;
        @BindView(R.id.albumImageProgressBar)
        ProgressBar albumImageProgressBar;
        @BindView(R.id.albumCardViewInside)
        CardView albumCardViewInside;

        public AlbumHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind() {
            PhotoFolder albums = mAlbumsList.get(getAdapterPosition());
            if (albums != null) {
                albumNameTextView.setText(albums.getName());
                AlbumCoverPhoto coverpic = projectsProvider.getCoverPhoto(albums.getPjProjectsId(), (int) (long) albums.getPjPhotosFolderMobileId());
                int countOfPhotos = projectsProvider.getAlbumPhotosCount(albums.getPjProjectsId(), (int) (long) albums.getPjPhotosFolderMobileId());
                albumImageView.setImageResource(R.drawable.ic_blank);
                if (coverpic != null && !TextUtils.isEmpty(coverpic.getPhotoLocation())) {
                    URI uri = null;
                    try {

                        uri = new URI(coverpic.getPhotoLocation());
                        String[] segments = uri.getPath().split("/");
                        String imageName = segments[segments.length - 1];
                        String filePath = albumImageView.getContext().getFilesDir().getAbsolutePath() + "/Pronovos/ThumbImage/";
                        String[] params = new String[]{coverpic.getPhotoLocation(), filePath};
//                        Object[] params = new Object[]{coverpic.getPhotoLocation(), filePath,albumImageView.getContext(),albumImageView};
                        File imgFile = new File(filePath + "/" + imageName);
                        if (!imgFile.exists()) {
                            filePath = albumImageView.getContext().getFilesDir().getAbsolutePath() + "/Pronovos/ThumbImage/";
                            imgFile = new File(filePath + "/" + imageName);
                        }
                        if (!imgFile.exists()) {
                            try {
                                new LoadImageInBackground(new LoadImageInBackground.Listener() {
                                    @Override
                                    public void onImageDownloaded(Bitmap bitmap) {
                                        RoundedBitmapDrawable roundedBitmapDrawable =
                                                RoundedBitmapDrawableFactory.create(albumImageView.getContext().getResources(), bitmap);
                                        roundedBitmapDrawable.setCornerRadius(cornerRadius);
                                        albumImageView.setImageDrawable(roundedBitmapDrawable);
                                        backgroundImageView.setVisibility(View.GONE);
                                        albumImageProgressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onImageDownloadError() {
                                        albumImageProgressBar.setVisibility(View.GONE);
                                    }
                                }).executeOnExecutor(THREAD_POOL_EXECUTOR, params);
                            } catch (RejectedExecutionException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(albumImageView.getContext().getResources(), myBitmap);
                            roundedBitmapDrawable.setCornerRadius(cornerRadius);
                            albumImageView.setImageDrawable(roundedBitmapDrawable);
                            albumImageProgressBar.setVisibility(View.GONE);
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    albumImageView.setImageBitmap(null);

                }
                albumCardView.setOnClickListener(v -> {
                    // Preventing multiple clicks, using threshold of 1 second
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    mActivity.startActivity(new Intent(mActivity, AlbumsPhotoActivity.class).putExtra("album_name", albums.getName()).putExtra("album_id", albums.getPjPhotosFolderId()).putExtra("album_mobile_id", albums.getPjPhotosFolderMobileId()).putExtra("pj_project_id", albums.getPjProjectsId()).putExtra("is_sync", albums.getIsSync()));
                });
                if (countOfPhotos <= 0) {
                    countPhotoTextView.setVisibility(View.GONE);
                } else {
                    countPhotoTextView.setVisibility(View.VISIBLE);
                    countPhotoTextView.setText(String.valueOf(countOfPhotos));
                }
            }
        }
    }

    public class AddAlbumHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.addFolder)
        CardView addFolder;

        public AddAlbumHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind() {
            addFolder.setOnClickListener(v -> {
// Preventing multiple clicks, using threshold of 1 second
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                ((addNewFolder) mActivity).onAddNewFolder();
            });
        }

    }
}
