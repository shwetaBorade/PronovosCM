package com.pdftron.pdf.controls;


import android.util.SparseIntArray;
import android.view.View;

import com.pdftron.pdf.Annot;
import com.pdftron.pdf.model.AnnotStyle;
import com.pdftron.pdf.tools.R;

/**
 * Id that references a specific button on the {@link AnnotationToolbar}
 */
public enum AnnotationToolbarButtonId {
    PAN(R.id.controls_annotation_toolbar_tool_pan),
    CLOSE(R.id.controls_annotation_toolbar_btn_close),
    OVERFLOW(R.id.controls_annotation_toolbar_btn_more),
    UNDERLINE(R.id.controls_annotation_toolbar_tool_text_underline);

//    EXT_HIGHLIGHT(R.id.controls_annotation_toolbar_tool_text_highlight),            // 1  , Annot.e_Highlight
//    TEXT_STRIKEOUT(R.id.controls_annotation_toolbar_tool_text_strikeout),           // 2  , Annot.e_StrikeOut
//    TEXT_SQUIGGLY(R.id.controls_annotation_toolbar_tool_text_squiggly),             // 3  , Annot.e_Squiggly
//    SIGNATURE(R.id.controls_annotation_toolbar_tool_stamp);                         // 4  , AnnotStyle.CUSTOM_ANNOT_TYPE_SIGNATURE

    public final int id;

    AnnotationToolbarButtonId(int id) {
        this.id = id;
    }

    /**
     * Returns a sparse array of toolbar button ids and the associated
     * button visibility. Default all buttons are visible.
     */
    static SparseIntArray getButtonVisibilityArray() {
        SparseIntArray array = new SparseIntArray();
        array.put(R.id.controls_annotation_toolbar_tool_pan, View.VISIBLE);
        array.put(R.id.controls_annotation_toolbar_btn_close, View.VISIBLE);
        array.put(R.id.controls_annotation_toolbar_btn_more, View.VISIBLE);
        array.put(R.id.controls_annotation_toolbar_tool_text_underline, View.VISIBLE);
        return array;
    }
}
