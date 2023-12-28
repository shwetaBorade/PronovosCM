package com.pronovoscm.adapter;

import android.app.Activity;
import android.content.res.Configuration;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.persistence.domain.PhotosMobile;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DatePhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    //private String[] dates;
    PhotoAdapter.PhotoAdapterClick photoAdapterClick;
    private HashMap<String, ArrayList<PhotosMobile>> mPhotosHashMap;
    private Activity mActivity;
    private boolean isThumbImage = true;

    public DatePhotoAdapter(Activity mActivity, HashMap<String, ArrayList<PhotosMobile>> mPhotosHashMap, PhotoAdapter.PhotoAdapterClick photoAdapterClick) {
        this.mPhotosHashMap = mPhotosHashMap;
        this.mActivity = mActivity;
        this.photoAdapterClick = photoAdapterClick;
        setHasStableIds(true);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_ITEM) {
            View view = inflater.inflate(R.layout.photo_recycler_header, parent, false);

            return new PhotoViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public long getItemId(int position) {
//        PhotosMobile product = mPhotosHashMap.get(position);
//        return (long)product.getPjPhotosIdMobile();
        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //this.dates = this.mPhotosHashMap.keySet().toArray(new String[10]);
        if (holder instanceof LoadingViewHolder) {
        } else {
            ((PhotoViewHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemCount() {
        if (mPhotosHashMap != null) {
            return mPhotosHashMap.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
      /*  if (mPhotosHashMap.get("nodate") == null) {
            return VIEW_TYPE_LOADING;
        } else {*/
        return VIEW_TYPE_ITEM;
//        }
//        return mPhotosHashMap.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;

    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.header_list_text)
        TextView headerListText;
        @BindView(R.id.progressBar)
        ProgressBar progressBar;


        private PhotoAdapter mPhotoAdapter;


        public PhotoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind(int position) {
            if (mPhotosHashMap.keySet().toArray(new String[0])[position].equals("nodate")) {
                headerListText.setVisibility(View.GONE);
//                photoRecyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
                headerListText.setVisibility(View.VISIBLE);
                headerListText.setText(mPhotosHashMap.keySet().toArray(new String[0])[position]);
                Configuration newConfig = mActivity.getResources().getConfiguration();
                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    // set background for landscape
                } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    // set background for portrait
                }
//                mPhotoAdapter = new PhotoAdapter(mActivity, mPhotosHashMap.get(mPhotosHashMap.keySet().toArray(new String[0])[position]), position, photoAdapterClick);

            }
        }

    }

}