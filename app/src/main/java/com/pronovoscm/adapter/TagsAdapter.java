package com.pronovoscm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.pronovoscm.R;
import com.pronovoscm.persistence.domain.ImageTag;
import com.pronovoscm.utils.dialogs.TagsDialog;

import java.util.ArrayList;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TagsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ImageTag> mImageTags;
    private TagsDialog mTagsDialog;
    private ArrayList<ImageTag> mselectedImageTag;
    private boolean unableToEditPhoto;

    public TagsAdapter(TagsDialog tagsDialog, List<ImageTag> imageTags, ArrayList<ImageTag> selectedImageTag, boolean unableToEditPhoto) {
        this.mImageTags = imageTags;
        this.mselectedImageTag = selectedImageTag;
        this.mTagsDialog = tagsDialog;
        this.unableToEditPhoto = unableToEditPhoto;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tags_item_list, parent, false);

        return new ImageTagViewHolder(view);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((ImageTagViewHolder) holder).bind(mImageTags.get(position));
    }

    @Override
    public int getItemCount() {
        if (mImageTags != null) {
            return mImageTags.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface updateTags {
        void onUpdateSelectedTags(ArrayList<ImageTag> selectedTag);
    }

    public class ImageTagViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tagTextView)
        TextView tagTextView;
        @BindView(R.id.tagCheckBox)
        CheckBox tagCheckBox;
        @BindView(R.id.tagsView)
        ConstraintLayout tagsView;

        public ImageTagViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind(final ImageTag imageTag) {
tagCheckBox.setClickable(false);
            if (imageTag != null) {
                boolean isSelected = false;
                tagTextView.setText(imageTag.getName());
                for (ImageTag imageTag1 :
                        mselectedImageTag) {
                    if (imageTag1.getId() == imageTag.getId()) {
                        isSelected = true;
                    }
                }
                if (isSelected) {
                    tagCheckBox.setChecked(true);
                } else {
                    tagCheckBox.setChecked(false);
                }
                tagsView.setOnClickListener(v -> {
                    tagCheckBox.setChecked(!tagCheckBox.isChecked());
                    if (tagCheckBox.isChecked()) {
                        mselectedImageTag.add(imageTag);
                    } else {
                        mselectedImageTag.remove(imageTag);
                    }
                    (mTagsDialog).onUpdateSelectedTags(mselectedImageTag);

                });
                if (unableToEditPhoto) {

                    tagsView.setClickable(false);
                    tagCheckBox.setClickable(false);
                    tagCheckBox.setEnabled(false);
                }
            }
        }

    }
}