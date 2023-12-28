package com.pronovoscm.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.pronovoscm.R;
import com.pronovoscm.persistence.domain.PhotosMobile;
import com.pronovoscm.utils.ui.LoadImageInBackground;
import com.pronovoscm.utils.ui.ZoomImageView;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;
import static com.pronovoscm.activity.DrawingPDFActivity.TAG;

public class ImageViewPagerAdapter extends PagerAdapter {
    private List<PhotosMobile> mPhotosArrayList;
    private LayoutInflater layoutInflater;
    private Activity mActivity;

    public ImageViewPagerAdapter(List<PhotosMobile> photosArrayList, Activity activity) {
        mActivity = activity;
        mPhotosArrayList = photosArrayList;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (mPhotosArrayList != null) {
            return mPhotosArrayList.size();
        } else {
            return 0;

        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        Log.i("ImageViewPagerAdapter", "instantiateItem start # " + position);
        View itemView = layoutInflater.inflate(R.layout.photo_view, container, false);

        ZoomImageView imageView = itemView.findViewById(R.id.photoImageView);
        ImageView backgroundImageView = itemView.findViewById(R.id.backImageView);
        ProgressBar photoImageProgressBar = itemView.findViewById(R.id.photoImageProgressBar);
        if (mPhotosArrayList.get(position) != null) {
            URI uri = null;
            try {
                Log.i(TAG, "instantiateItem: onImageDownloaded 1");
                uri = new URI(mPhotosArrayList.get(position).getPhotoLocation());
                String[] segments = uri.getPath().split("/");
                String imageName = segments[segments.length - 1];
                imageView.setImageResource(R.drawable.ic_blank);
                String filePath = imageView.getContext().getFilesDir().getAbsolutePath() + "/Pronovos/";
                String[] params = new String[]{mPhotosArrayList.get(position).getPhotoLocation(), filePath};
//                Object[] params = new Object[]{mPhotosArrayList.get(position).getPhotoLocation(), filePath,imageView.getContext(),imageView};
                File imgFile = new File(filePath + "/" + imageName);
                Log.i(TAG, "onImageDownloaded: image download 1 " + mPhotosArrayList.get(position).getPhotoLocation());

                if (!imgFile.exists()) {
                    try {
                        new LoadImageInBackground(new LoadImageInBackground.Listener() {
                            @Override
                            public void onImageDownloaded(Bitmap bitmap) {
                                Log.i(TAG, "onImageDownloaded: image download");
                                imageView.setImageBitmap(bitmap);
                                backgroundImageView.setVisibility(View.GONE);
                                photoImageProgressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onImageDownloadError() {
                                Log.i(TAG, "onImageDownloaded: image download 2");

                                photoImageProgressBar.setVisibility(View.GONE);
                            }
                        }).executeOnExecutor(THREAD_POOL_EXECUTOR, params);
                    } catch (RejectedExecutionException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        imageView.setImageBitmap(myBitmap);
                    } catch (
                            OutOfMemoryError error
                    ) {
                        error.printStackTrace();
                    }
                    photoImageProgressBar.setVisibility(View.GONE);
                }
//                imageView.setImageResource(R.drawable.ic_blank);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout) object);
    }


}
