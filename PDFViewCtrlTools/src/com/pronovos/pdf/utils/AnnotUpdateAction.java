package com.pronovos.pdf.utils;

import com.pdftron.pdf.PDFViewCtrl;

public class AnnotUpdateAction {
    String action;
    PDFViewCtrl mPDFViewCtrl;
    String status;

    public AnnotUpdateAction() {
    }

    public PDFViewCtrl getPDFViewCtrl() {
        return this.mPDFViewCtrl;
    }

    public void setPDFViewCtrl(PDFViewCtrl PDFViewCtrl) {
        this.mPDFViewCtrl = PDFViewCtrl;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
