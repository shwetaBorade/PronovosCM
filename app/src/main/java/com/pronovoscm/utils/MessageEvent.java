package com.pronovoscm.utils;

import com.pronovoscm.persistence.domain.ImageTag;

import java.util.ArrayList;

public class MessageEvent {
    public ArrayList<ImageTag> mImageTags;

    public ArrayList<ImageTag> getImageTags() {
        return mImageTags;
    }

    public void setImageTags(ArrayList<ImageTag> imageTags) {
        mImageTags = imageTags;
    }
}
