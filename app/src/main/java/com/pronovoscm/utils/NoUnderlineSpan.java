package com.pronovoscm.utils;

import android.text.TextPaint;
import android.text.style.UnderlineSpan;

public class NoUnderlineSpan  extends UnderlineSpan {
    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(false);
    }
}
