package com.pronovoscm.model;

import com.pdftron.pdf.PDFViewCtrl;

public class AnnotDeleteAction {

    String action;
    PDFViewCtrl mPDFViewCtrl;

    public PDFViewCtrl getPDFViewCtrl() {
        return mPDFViewCtrl;
    }

    public void setPDFViewCtrl(PDFViewCtrl PDFViewCtrl) {
        mPDFViewCtrl = PDFViewCtrl;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
