package com.pronovoscm.utils.dialogs;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.ui.LoadImageInBackground;
import com.pronovoscm.utils.ui.ZoomImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.RejectedExecutionException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class FormAttachmentDialog extends DialogFragment {

    @BindView(R.id.attachmentImageView)
    ZoomImageView attachmentImageView;
    @BindView(R.id.backgroundImageView)
    ImageView backgroundImageView;
    @BindView(R.id.attachmentProgressBar)
    ProgressBar attachmentProgressBar;
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
    private String attachmentPath;
    //    private LoadImage mLoadImage;
    private AttachmentDeleteInterface callBackInterface;

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
        View rootview = inflater.inflate(R.layout.attachment_item_view, container, false);
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
            String filePath = attachmentImageView.getContext().getFilesDir().getAbsolutePath() + "/Pronovos/Form/";
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
                            backgroundImageView.setVisibility(View.GONE);
                            attachmentProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onImageDownloadError() {
                            attachmentProgressBar.setVisibility(View.GONE);
                        }
                    }).executeOnExecutor(THREAD_POOL_EXECUTOR, params);
                } catch (RejectedExecutionException
                        e) {
                    e.printStackTrace();
                }

            } else {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                backgroundImageView.setVisibility(View.GONE);
                attachmentProgressBar.setVisibility(View.GONE);
                attachmentImageView.setImageBitmap(myBitmap);
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

