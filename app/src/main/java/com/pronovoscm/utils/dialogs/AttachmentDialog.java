package com.pronovoscm.utils.dialogs;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.DialogFragment;

import com.google.gson.Gson;
import com.pronovoscm.R;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.model.response.login.UserPermissions;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.ui.LoadImageInBackground;
import com.pronovoscm.utils.ui.ZoomImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.RejectedExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class AttachmentDialog extends DialogFragment {

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
    private int imagePosition;
    //    private LoadImage mLoadImage;
    private AttachmentDeleteInterface callBackInterface;
    private LoginResponse loginResponse = null;
    private UserPermissions userPermissions;
    private int canAddWorkDetail;

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
        imagePosition = getArguments().getInt("image_position");
        backImageView.setOnClickListener(v -> dismiss());
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.INVISIBLE);
        deleteImageView.setVisibility(View.VISIBLE);
        getUserPermissions();
        URI uri = null;
        try {
            uri = new URI(attachmentPath);
            String[] segments = uri.getPath().split("/");
            String imageName = segments[segments.length - 1];
            String filePath = attachmentImageView.getContext().getFilesDir().getAbsolutePath() + "/Pronovos";
            String[] params = new String[]{attachmentPath, filePath};
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
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(attachmentImageView.getContext().getResources(), myBitmap);
                roundedBitmapDrawable.setCornerRadius(cornerRadius);
                backgroundImageView.setVisibility(View.GONE);
                attachmentImageView.setImageDrawable(roundedBitmapDrawable);
                attachmentProgressBar.setVisibility(View.GONE);
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

    private void getUserPermissions() {
        loginResponse = (new Gson().fromJson(SharedPref.getInstance(getContext()).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        canAddWorkDetail = loginResponse.getUserDetails().getPermissions().get(0).getCreateProjectDailyReport();
        if (canAddWorkDetail != 1) {
            deleteImageView.setVisibility(View.GONE);
        }
    }


    //    @OnClick(R.id.attachmentImageView)
//    public void onAttachmentClick() {
//        appbarToolbar.setVisibility(View.VISIBLE);
//    }
    @OnClick(R.id.deleteImageView)
    public void onDeleteClick() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
//        alertDialog.setTitle(getActivity().getString(R.string.message));
        alertDialog.setMessage(getActivity().getString(R.string.are_you_sure_you_want_to_permanently_remove_this_attachment));
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getActivity().getString(R.string.ok), (dialog, which) -> {
            callBackInterface.onDelete(imagePosition);
            alertDialog.dismiss();
            AttachmentDialog.this.dismiss();
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getActivity().getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        alertDialog.setCancelable(false);
        alertDialog.show();
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(ContextCompat.getColor(getActivity(), R.color.gray_948d8d));
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
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

