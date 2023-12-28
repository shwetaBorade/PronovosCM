package com.pronovoscm.utils.dialogs;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.DialogFragment;

import com.pronovoscm.R;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.Constants;
import com.pronovoscm.utils.ui.LoadImageInBackground;
import com.pronovoscm.utils.ui.ZoomImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.RejectedExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class DocumentFileDialog extends DialogFragment {

    @BindView(R.id.attachmentImageView)
    ZoomImageView attachmentImageView;
    /*    @BindView(R.id.backgroundImageView)
        ImageView backgroundImageView;*/
    /*@BindView(R.id.attachmentProgressBar)
    ProgressBar attachmentProgressBar;*/
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.deleteImageView)
    ImageView deleteImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.appbarToolbar)
    Toolbar appbarToolbar;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    boolean isNetworkConnected;
    Context context;
    private String attachmentPath;
    //    private LoadImage mLoadImage;
    private AttachmentDeleteInterface callBackInterface;

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Translucent_Dialog);
        EventBus.getDefault().register(this);
        isNetworkConnected = NetworkService.isNetworkAvailable(getContext());

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.document_image_item_view, container, false);
        ButterKnife.bind(this, rootview);
        return rootview;
    }

    public void onAttachToParentFragment(AttachmentDeleteInterface callBackInterface) {
        if (callBackInterface != null) {
            try {
                this.callBackInterface = callBackInterface;
            } catch (ClassCastException e) {
                deleteImageView.setVisibility(View.GONE);
                throw new ClassCastException(
                        callBackInterface.toString() + " must implement Callback");
            }
        } else {
            deleteImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        appbarToolbar.setVisibility(View.INVISIBLE);
        attachmentImageView.setImageResource(R.drawable.ic_blank);
//        mLoadImage = new LoadImage(getContext());
        int cornerRadius = (int) (getActivity().getResources().getDimension(R.dimen.album_photo_radius) / getActivity().getResources().getDisplayMetrics().density);

        attachmentPath = getArguments().getString("attachment_path");
        titleTextView.setText(getArguments().getString("title_text"));
        backImageView.setOnClickListener(v -> dismiss());
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.INVISIBLE);
        deleteImageView.setVisibility(View.GONE);
        URI uri = null;
        try {
            uri = new URI(attachmentPath);
            String[] segments = uri.getPath().split("/");
            String imageName = segments[segments.length - 1];
            String filePath = context.getFilesDir().getAbsolutePath() + Constants.DOCUMENT_FILES_PATH;
            String[] params = new String[]{attachmentPath, filePath};
//            Object[] params = new Object[]{attachmentPath, filePath,attachmentImageView.getContext(),attachmentImageView};
            File imgFile = new File(filePath + "/" + imageName);
            attachmentImageView.setImageResource(R.drawable.ic_blank);

            if (!imgFile.exists()) {
                try {
                    new LoadImageInBackground(new LoadImageInBackground.Listener() {
                        @Override
                        public void onImageDownloaded(Bitmap bitmap) {
                            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(attachmentImageView.getContext().getResources(), bitmap);
                            roundedBitmapDrawable.setCornerRadius(cornerRadius);
                            attachmentImageView.setImageDrawable(roundedBitmapDrawable);
                            // backgroundImageView.setVisibility(View.GONE);
                            //    attachmentProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onImageDownloadError() {
                            //  attachmentProgressBar.setVisibility(View.GONE);
                        }
                    }).executeOnExecutor(THREAD_POOL_EXECUTOR, params);
                } catch (RejectedExecutionException
                        e) {
                    e.printStackTrace();
                }

            } else {
                //  backgroundImageView.setVisibility(View.GONE);
                //   attachmentProgressBar.setVisibility(View.GONE);
                attachmentImageView.setVisibility(View.VISIBLE);
                Uri imageUri = Uri.fromFile(imgFile);

                //use glide dont  use local bit bap because of size issue
                try {
                    Bitmap myBitmap = getBitmap(imgFile.getAbsolutePath(), imageUri);//BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    attachmentImageView.setImageBitmap(myBitmap);
                } catch (
                        OutOfMemoryError error
                ) {
                    error.printStackTrace();
                }

            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (callBackInterface == null) {
            deleteImageView.setVisibility(View.GONE);
        }

        if (isNetworkConnected) {
            offlineTextView.setVisibility(View.GONE);
        } else {
            offlineTextView.setVisibility(View.VISIBLE);
        }
    }


    //    @OnClick(R.id.attachmentImageView)
//    public void onAttachmentClick() {
//        appbarToolbar.setVisibility(View.VISIBLE);
//    }


    private Bitmap getBitmap(String path, Uri uri) {

        InputStream in = null;
        try {
            //  Uri uri = getImageUri(path);
            ContentResolver mContentResolver = (ContentResolver) context.getContentResolver();

            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = mContentResolver.openInputStream(uri);

            // Decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();


            int scale = 1;
            while ((options.outWidth * options.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d("DIalog", "scale = " + scale + ", orig-width: " + options.outWidth + ", orig-height: " + options.outHeight);

            Bitmap resultBitmap = null;
            in = mContentResolver.openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                options = new BitmapFactory.Options();
                options.inSampleSize = scale;
                resultBitmap = BitmapFactory.decodeStream(in, null, options);

                // resize to desired dimensions
                int height = resultBitmap.getHeight();
                int width = resultBitmap.getWidth();
                Log.d("DIalog", "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(resultBitmap, (int) x,
                        (int) y, true);
                resultBitmap.recycle();
                resultBitmap = scaledBitmap;

                System.gc();
            } else {
                resultBitmap = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d("DIalog", "bitmap size - width: " + resultBitmap.getWidth() + ", height: " +
                    resultBitmap.getHeight());
            return resultBitmap;
        } catch (IOException e) {
            Log.e("DIalog", e.getMessage(), e);

            return null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
        } else {
            offlineTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        EventBus.getDefault().unregister(this);
    }
}

