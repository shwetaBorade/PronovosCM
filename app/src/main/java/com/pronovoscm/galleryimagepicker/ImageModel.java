package com.pronovoscm.galleryimagepicker;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class ImageModel  implements Parcelable{

    private String path;
    private boolean isCameraCaptured;
    private String galleryPath;


    protected ImageModel(Parcel in) {
        path = in.readString();
        isCameraCaptured = in.readByte() != 0;
        galleryPath = in.readString();
    }

    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (((ImageModel) o).getGalleryPath().equals(this.galleryPath)) return true;
        return false;
    }

    @Override
    public int hashCode() {

        return galleryPath == null? 0: galleryPath.hashCode();
    }

    public ImageModel(String path, boolean isCameraCaptured,String galleryPath) {

        this.path = path;
        this.galleryPath = galleryPath;
        this.isCameraCaptured = isCameraCaptured;
    }



    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isCameraCaptured() {
        return isCameraCaptured;
    }

    public void setCameraCaptured(boolean cameraCaptured) {
        isCameraCaptured = cameraCaptured;
    }

    public String getGalleryPath() {
        return galleryPath;
    }

    public void setGalleryPath(String galleryPath) {
        this.galleryPath = galleryPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(path);
        parcel.writeByte((byte) (isCameraCaptured ? 1 : 0));
        parcel.writeString(galleryPath);
    }
}
