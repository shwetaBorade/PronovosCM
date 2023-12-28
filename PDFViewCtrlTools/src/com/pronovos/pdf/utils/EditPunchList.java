package com.pronovos.pdf.utils;

import com.pdftron.pdf.Annot;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.widget.AnnotView;


public class EditPunchList {
    String content;
    PDFViewCtrl mPDFViewCtrl;
    Annot mAnnot;
    AnnotView mAnnotView;
    boolean flag;

    public AnnotView getAnnotView() {
        return mAnnotView;
    }

    public void setAnnotView(AnnotView annotView) {
        mAnnotView = annotView;
    }

    public Annot getAnnot() {
        return mAnnot;
    }

    public void setAnnot(Annot annot) {
        mAnnot = annot;
    }

    public PDFViewCtrl getPDFViewCtrl() {
        return mPDFViewCtrl;
    }

    public void setPDFViewCtrl(PDFViewCtrl PDFViewCtrl) {
        mPDFViewCtrl = PDFViewCtrl;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
