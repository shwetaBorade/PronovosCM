package com.pronovoscm.adapter;

import android.app.Activity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.pronovoscm.R;
import com.pronovoscm.utils.ui.LoadImage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UploadPhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LoadImage mLoadImage;
    private List<String> mPhotosList;
    private Activity mActivity;
    private OnClickListener mListener;


    public UploadPhotoAdapter(Activity mActivity, List<String> photosList, OnClickListener listener) {
        this.mPhotosList = photosList;
        this.mActivity = mActivity;
        mLoadImage = new LoadImage(mActivity);
        this.mListener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.upload_photo_item_list, parent, false);

        return new UploadPhotoViewHolder(view);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((UploadPhotoAdapter.UploadPhotoViewHolder) holder).bind(position, mPhotosList.get(position));
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
        return position;
    }

    public interface OnClickListener {
        void onPhotoClick(int position, String photo);
    }

    public class UploadPhotoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.photoImageView)
        ImageView photoImageView;
        @BindView(R.id.backImageView)
        ImageView backgroundImageView;
        @BindView(R.id.photoImageProgressBar)
        ProgressBar photoImageProgressBar;
        @BindView(R.id.cardViewPhotoImageView)
        CardView photoCardView;

        public UploadPhotoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind(int position, final String photos) {
            if (photos != null) {
                photoImageView.setImageResource(R.drawable.ic_blank);
                mLoadImage.LoadImagePathRounded("", "", photos, photoImageView, photoImageProgressBar, false, backgroundImageView);
//                String completePath = Environment.getExternalStorageDirectory() + "/Pronovos/" + photos;
//
//                File file = new File(completePath);
//                Picasso.get()
//                        .load(file)
//                        //.placeholder(placeHolder).error(placeHolder)
////                        .resize(200, 200)
////                        .centerCrop()
//
//                        .into(photoImageView);
                photoImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onPhotoClick(position, photos);
                        }
                    }
                });

            }
        }

    }

}