package com.pronovoscm.adapter;

import android.app.Activity;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pronovoscm.R;
import com.pronovoscm.persistence.domain.PhotosMobile;
import com.pronovoscm.utils.ui.LoadImage;
import com.pronovoscm.utils.ui.LoadImageInBackground;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int cornerRadius;
    PhotoAdapterClick photoAdapterClick;
    private List<Object> mPhotosList;
    private Activity mActivity;
    private int dateAdapterPosition;
    private boolean isThumbImage = true;
    private LoadImage mLoadImage;
    private LoadImageInBackground loadImageInBackground;
    private long mLastClickTime=0;

    public PhotoAdapter(Activity mActivity, List<Object> photosList, int dateAdapterPosition, PhotoAdapterClick photoAdapterClick) {
        this.mPhotosList = photosList;
        this.mActivity = mActivity;
        this.dateAdapterPosition = dateAdapterPosition;
        this.photoAdapterClick = photoAdapterClick;
        cornerRadius = (int) (mActivity.getResources().getDimension(R.dimen.album_photo_radius) / mActivity.getResources().getDisplayMetrics().density);
        setHasStableIds(true);
        mLoadImage = new LoadImage(mActivity);

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == -1) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.photo_recycler_header, parent, false);

            return new PhotoHeaderViewHolder(view);
        } else {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.photo_item_list, parent, false);

            return new PhotoViewHolder(view);

        }
    }

    @Override
    public long getItemId(int position) {
//        PhotosMobile product = mPhotosList.get(position);
//        return (long)product.getPjPhotosIdMobile();
        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mPhotosList.get(position) instanceof PhotosMobile) {
            ((PhotoViewHolder) holder).bind((PhotosMobile) mPhotosList.get(position));
        } else {
            ((PhotoHeaderViewHolder) holder).bind(position);

        }
    }

    @Override
    public int getItemCount() {
        if (mPhotosList != null) {
            return mPhotosList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mPhotosList.get(position) instanceof PhotosMobile) {
            return position;
        } else {
            return -1;
        }
    }

    public boolean isHeader(int position) {
        return mPhotosList.get(position) instanceof PhotosMobile ? false : true;
    }

    public interface PhotoAdapterClick {
        void onPhotoClick(int albumId, int projectId, long photoFolderId, int dateAdapterPosition, int
                photoAdapterPosition, Long pjPhotosIdMobile);
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.photoImageView)
        ImageView photoImageView;
        @BindView(R.id.cloudImageView)
        ImageView cloudImageView;
        @BindView(R.id.photoCardView)
        RelativeLayout photoCardView;
        @BindView(R.id.backImageView)
        ImageView backgroundImageView;
        @BindView(R.id.photoImageProgressBar)
        ProgressBar photoImageProgressBar;

        public PhotoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }


        private void bind(PhotosMobile photos1) {
            PhotosMobile photos = (PhotosMobile) mPhotosList.get(getAdapterPosition());
            if (photos != null) {

                if (photos.getIsSync()) {
                    cloudImageView.setVisibility(View.GONE);
                } else {
                    cloudImageView.setVisibility(View.VISIBLE);
                }/*
                URI uri = null;
                try {
                    uri = new URI(photos.getPhotoThumb() != null ? photos.getPhotoThumb() : photos.getPhotoLocation());
                    String[] segments = uri.getPath().split("/");
                    String imageName = segments[segments.length - 1];
                    String filePath = photoImageView.getContext().getFilesDir().getAbsolutePath() + "/Pronovos/ThumbImage/";
                    File imgFile = new File(filePath + "/" + imageName);
                    if (!imgFile.exists()) {
                        final Downloader downloader = Downloader.getInstance(mActivity)
                                .setUrl(photos.getPhotoThumb() != null ? photos.getPhotoThumb() : photos.getPhotoLocation())
                                .setListener(new DownloadListener() {
                                    @Override
                                    public void onComplete(int totalBytes) {
//                                    Bitmap bitmap = BitmapFactory.decodeByteArray(totalBytes, 0, totalBytes.length);
                                        if (mActivity != null) {
                                            String str = Downloader.getInstance(mActivity).getDownloadedFilePath(photos.getPhotoThumb() != null ? photos.getPhotoThumb() : photos.getPhotoLocation());
                                            File imgFile = new File(str);
                                            if (imgFile.exists()) {
                                                try {
                                                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(photoImageView.getContext().getResources(), myBitmap);
                                                    roundedBitmapDrawable.setCornerRadius(cornerRadius);
                                                    photoImageView.setImageDrawable(roundedBitmapDrawable);
                                                    photoImageProgressBar.setVisibility(View.GONE);
                                                } catch (OutOfMemoryError e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                })
                                .setToken(photos.getPhotoThumb() != null ? photos.getPhotoThumb() : photos.getPhotoLocation())
//                        .setAllowedOverRoaming(roamingAllowed)
//                        .setAllowedOverMetered(meteredAllowed) //Api 16 and higher
                                .setVisibleInDownloadsUi(true)
                                .setDestinationDir(filePath, imageName)
//                            .setNotificationTitle(notificationTitle)
//                            .setDescription(description)
//                            .setNotificationVisibility(visibility)
//                            .setAllowedNetworkTypes(networkTypes)
//                            .setKeptAllDownload(allDownloadKept)
                                ;
                        downloader.start();
                    } else {
                        try {
                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(photoImageView.getContext().getResources(), myBitmap);
                            roundedBitmapDrawable.setCornerRadius(cornerRadius);
                            photoImageView.setImageDrawable(roundedBitmapDrawable);
                            photoImageProgressBar.setVisibility(View.GONE);
                        } catch (OutOfMemoryError e) {
                            e.printStackTrace();
                        }
                    }
                } catch (URISyntaxException e) {

                }*/

//             with glide

                URI uri = null;
                try {
                    uri = new URI(photos.getPhotoThumb() != null ? photos.getPhotoThumb() : photos.getPhotoLocation());
                    String[] segments = uri.getPath().split("/");
                    String imageName = segments[segments.length - 1];
                    photoImageView.setImageResource(R.drawable.ic_blank);

//                    uri = new URI(photos.getPhotoThumb() != null ? photos.getPhotoThumb() : photos.getPhotoLocation());
//                    String[] segments = uri.getPath().split("/");
//                    String imageName = segments[segments.length - 1];
//                        uri = new URI(projects.getShowcasePhoto());
//                        String[] segments = uri.getPath().split("/");
//                        String imageName = segments[segments.length - 1];
                    String filePath = photoImageView.getContext().getFilesDir().getAbsolutePath() + "/Pronovos/ThumbImage/";
                    String[] params = new String[]{photos.getPhotoThumb() != null ? photos.getPhotoThumb() : photos.getPhotoLocation(), filePath};
                    File imgFile = new File(filePath + "/" + imageName);
                    if (!imgFile.exists()) {
                        filePath = photoImageView.getContext().getFilesDir().getAbsolutePath() + "/Pronovos/";
                        imgFile = new File(filePath + "/" + imageName);
                    }
                    if (!imgFile.exists()) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    int position = getAdapterPosition();
                                    if (position!=RecyclerView.NO_POSITION){
                                        if (mPhotosList.size() > getAdapterPosition()) {
                                            PhotosMobile photos = (PhotosMobile) mPhotosList.get(getAdapterPosition());
                                            int dp = (int) (mActivity.getResources().getDimension(R.dimen.album_photo_radius) / mActivity.getResources().getDisplayMetrics().density);
                                   /* Glide.with(mActivity)
                                            .load(photos.getPhotoThumb())
                                            .apply(new RequestOptions().centerCrop().transform(new RoundedCorners(dp)))
                                            .into(photoImageView);*/
                                            mLoadImage.getRoundedImagePath(photos.getPhotoLocation(), photos.getPhotoThumb(),
                                                    imageName, photoImageView, photoImageProgressBar,
                                                    isThumbImage, backgroundImageView);
                                        }//                            photoImageView.setImageResource(R.drawable.ic_blank);
                                    }else {
                                        //Do nothing for now
                                    }
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 0);
                    } else {
                        try {
                          /*  Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(photoImageView.getContext().getResources(), myBitmap);
                            roundedBitmapDrawable.setCornerRadius(cornerRadius);
                            photoImageView.setImageDrawable(roundedBitmapDrawable);
                            photoImageProgressBar.setVisibility(View.GONE);
                        */
                            mLoadImage.getRoundedImagePath(photos.getPhotoLocation(), photos.getPhotoThumb(), imageName, photoImageView, photoImageProgressBar, isThumbImage, backgroundImageView);


                        } catch (OutOfMemoryError e) {
                            e.printStackTrace();
                        }
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
//                Picasso.get().load(photos.getPhotoThumb() != null ? photos.getPhotoThumb() : photos.getPhotoLocation()).into(photoImageView);
 /*
                    uri = new URI(photos.getPhotoThumb() != null ? photos.getPhotoThumb() : photos.getPhotoLocation());
                    String[] segments = uri.getPath().split("/");
                    String imageName = segments[segments.length - 1];
//                        uri = new URI(projects.getShowcasePhoto());
//                        String[] segments = uri.getPath().split("/");
//                        String imageName = segments[segments.length - 1];
                    String filePath = photoImageView.getContext().getFilesDir().getAbsolutePath() + "/Pronovos/ThumbImage/";
                    String[] params = new String[]{photos.getPhotoThumb() != null ? photos.getPhotoThumb() : photos.getPhotoLocation(), filePath};
                    File imgFile = new File(filePath + "/" + imageName);
                    if (!imgFile.exists()) {
                        filePath = photoImageView.getContext().getFilesDir().getAbsolutePath() + "/Pronovos/";
                        imgFile = new File(filePath + "/" + imageName);
                    }
                    if (!imgFile.exists()) {
                        try {
                            if (NetworkService.isNetworkAvailable(mActivity)) {

                                imageLoader.displayImage(photos.getPhotoThumb() != null ? photos.getPhotoThumb() : photos.getPhotoLocation(), photoImageView, options, new ImageLoadingListener() {

                                }, new ImageLoadingProgressListener() {
                                    @Override
                                    public void onProgressUpdate(String imageUri, View view, int current, int total) {

                                    }
                                });

                                loadImageInBackground = (LoadImageInBackground) new LoadImageInBackground(new LoadImageInBackground.Listener() {
                                    @Override
                                    public void onImageDownloaded(Bitmap bitmap) {
                                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(photoImageView.getContext().getResources(), bitmap);
                                        roundedBitmapDrawable.setCornerRadius(cornerRadius);
                                        photoImageView.setImageDrawable(roundedBitmapDrawable);
                                        backgroundImageView.setVisibility(View.GONE);
                                        photoImageProgressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onImageDownloadError() {
                                        photoImageProgressBar.setVisibility(View.GONE);
                                    }
                                }).executeOnExecutor(SERIAL_EXECUTOR, params);
                            }
                        } catch (RejectedExecutionException e) {
                            e.printStackTrace();
                        }

                    } else {
                        try {
                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(photoImageView.getContext().getResources(), myBitmap);
                            roundedBitmapDrawable.setCornerRadius(cornerRadius);
                            photoImageView.setImageDrawable(roundedBitmapDrawable);
                            photoImageProgressBar.setVisibility(View.GONE);
                        } catch (OutOfMemoryError e) {
                            e.printStackTrace();
                        }
                    }
*/


                photoCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Preventing multiple clicks, using threshold of 1 second
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        try {
                            if (mPhotosList.size() > getAdapterPosition()) {
                                PhotosMobile photos = (PhotosMobile) mPhotosList.get(getAdapterPosition());
                                photoAdapterClick.onPhotoClick(photos.getPjPhotosFolderId(), photos.getPjProjectsId(), photos.getPjPhotosFolderMobileId(), dateAdapterPosition, getAdapterPosition(), photos.getPjPhotosIdMobile());
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

    }

    public class PhotoHeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.header_list_text)
        TextView headerListText;
        @BindView(R.id.progressBar)
        ProgressBar progressBar;

        private PhotoAdapter mPhotoAdapter;


        public PhotoHeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind(int position) {

            if (mPhotosList.get(position) == null || mPhotosList.get(position).equals("nodate")) {
                progressBar.setVisibility(View.VISIBLE);
                headerListText.setVisibility(View.GONE);

            } else {
                progressBar.setVisibility(View.GONE);
                headerListText.setVisibility(View.VISIBLE);
                headerListText.setText(mPhotosList.get(position).toString());

            }
        }

    }
}
