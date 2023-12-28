package com.pronovoscm.model.request.formfileupload;

import java.io.File;

public class TypedFile {

    String s;
    File newCreatedFile;

    public TypedFile(String s, File newCreatedFile) {
        this.s = s;
        this.newCreatedFile = newCreatedFile;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public File getNewCreatedFile() {
        return newCreatedFile;
    }

    public void setNewCreatedFile(File newCreatedFile) {
        this.newCreatedFile = newCreatedFile;
    }
}
