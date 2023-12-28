
//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2019 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.pdf.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.annotation.ColorInt;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import android.view.MotionEvent;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.ColorPt;
import com.pdftron.pdf.Field;
import com.pdftron.pdf.Font;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.Rect;
import com.pdftron.pdf.annots.TextWidget;
import com.pdftron.pdf.annots.Widget;
import com.pdftron.pdf.config.ToolStyleConfig;
import com.pdftron.pdf.tools.ToolManager.ToolMode;
import com.pdftron.pdf.utils.Utils;

import java.util.UUID;

/**
 * This class is for creating multiline text field
 */
@Keep
public class TextFieldCreate extends RectCreate {

    private boolean mIsMultiline;
    private int mJustification;
    @ColorInt
    protected int mTextColor;
    protected float mTextSize;
    protected String mPDFTronFontName;

    /**
     * Class constructor
     */
    public TextFieldCreate(PDFViewCtrl ctrl) {
        this(ctrl, true, Field.e_left_justified);
    }

    /**
     * Class constructor
     */
    public TextFieldCreate(PDFViewCtrl ctrl, boolean isMultiline, int justification) {
        super(ctrl);
        mIsMultiline = isMultiline;
        mJustification = justification;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        initTextField();
        return super.onDown(e);
    }

    /**
     * The overload implementation of {@link Tool#getToolMode()}.
     */
    @Override
    public ToolManager.ToolModeBase getToolMode() {
        return ToolMode.FORM_TEXT_FIELD_CREATE;
    }

    @Override
    public int getCreateAnnotType() {
        return Annot.e_Widget;
    }

    @Override
    protected Annot createMarkup(@NonNull PDFDoc doc, Rect bbox) throws PDFNetException {
        TextWidget widget = TextWidget.create(doc, bbox, UUID.randomUUID().toString());
        ColorPt colorPt = Utils.color2ColorPt(Color.WHITE);
        widget.setBackgroundColor(colorPt, 3);
        widget.getSDFObj().putString(PDFTRON_ID, "");

        Field field = widget.getField();
        field.setFlag(Field.e_multiline, mIsMultiline);
        field.setJustification(mJustification);

        setWidgetStyle(doc, widget, "");

        return widget;
    }

    protected void setWidgetStyle(@NonNull PDFDoc doc, @NonNull Widget widget, @NonNull String contents) throws PDFNetException {
        ColorPt color = Utils.color2ColorPt(mTextColor);
        widget.setFontSize(mTextSize);
        widget.setTextColor(color, 3);
        // change font
        ToolManager toolManager = (ToolManager) mPdfViewCtrl.getToolManager();
        if (!Utils.isNullOrEmpty(mPDFTronFontName) && toolManager.isFontLoaded()) {
            Font font = Font.create(doc, mPDFTronFontName, contents);
            String fontName = font.getName();
            widget.setFont(font);

            // save font name with font if not saved already
            updateFontMap(mPdfViewCtrl.getContext(), widget.getType(), mPDFTronFontName, fontName);
        }
    }

    public void initTextField() {
        Context context = mPdfViewCtrl.getContext();
        SharedPreferences settings = Tool.getToolPreferences(context);
        mTextColor = settings.getInt(getTextColorKey(getCreateAnnotType()), ToolStyleConfig.getInstance().getDefaultTextColor(context, Annot.e_Widget));
        mTextSize = settings.getFloat(getTextSizeKey(getCreateAnnotType()), ToolStyleConfig.getInstance().getDefaultTextSize(context, Annot.e_Widget));
        mStrokeColor = settings.getInt(getColorKey(getCreateAnnotType()), ToolStyleConfig.getInstance().getDefaultColor(context, Annot.e_Widget));
        mThickness = settings.getFloat(getThicknessKey(getCreateAnnotType()), ToolStyleConfig.getInstance().getDefaultThickness(context, Annot.e_Widget));
        mFillColor = settings.getInt(getColorFillKey(getCreateAnnotType()), ToolStyleConfig.getInstance().getDefaultFillColor(context, Annot.e_Widget));
        mOpacity = settings.getFloat(getOpacityKey(getCreateAnnotType()), ToolStyleConfig.getInstance().getDefaultOpacity(context, Annot.e_Widget));
        mPDFTronFontName = settings.getString(getFontKey(getCreateAnnotType()), ToolStyleConfig.getInstance().getDefaultFont(mPdfViewCtrl.getContext(), Annot.e_Widget));
    }
}
