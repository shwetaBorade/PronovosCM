package com.pronovoscm.utils;

import com.pronovoscm.persistence.domain.PjDocumentsFiles;
import com.pronovoscm.persistence.domain.PjDocumentsFolders;

public class DocumentsFolderFileAdapterItem {

    private PjDocumentsFiles pjDocumentsFiles;
    private PjDocumentsFolders pjDocumentsFolders;
    private int adapterItemType;

    public PjDocumentsFiles getPjDocumentsFiles() {
        return pjDocumentsFiles;
    }

    public void setPjDocumentsFiles(PjDocumentsFiles pjDocumentsFiles) {
        this.pjDocumentsFiles = pjDocumentsFiles;
    }

    public PjDocumentsFolders getPjDocumentsFolders() {
        return pjDocumentsFolders;
    }

    public void setPjDocumentsFolders(PjDocumentsFolders pjDocumentsFolders) {
        this.pjDocumentsFolders = pjDocumentsFolders;
    }

    public int getAdapterItemType() {
        return adapterItemType;
    }

    public void setAdapterItemType(int adapterItemType) {
        this.adapterItemType = adapterItemType;
    }
}
