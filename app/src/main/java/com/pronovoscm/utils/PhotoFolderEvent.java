package com.pronovoscm.utils;

import com.pronovoscm.persistence.domain.PhotoFolder;

public class PhotoFolderEvent {
    public PhotoFolder mPhotoFolder;

    public PhotoFolder getPhotoFolder() {
        return mPhotoFolder;
    }

    public void setPhotoFolder(PhotoFolder photoFolder) {
        mPhotoFolder = photoFolder;
    }
}
