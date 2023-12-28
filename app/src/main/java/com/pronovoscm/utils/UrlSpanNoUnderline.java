package com.pronovoscm.utils;

import android.text.TextPaint;
import android.text.style.URLSpan;

public class UrlSpanNoUnderline extends URLSpan {
    public UrlSpanNoUnderline(URLSpan src) {
        super(src.getURL());
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
    }
}