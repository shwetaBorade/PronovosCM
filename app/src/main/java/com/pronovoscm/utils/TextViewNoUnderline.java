package com.pronovoscm.utils;

import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.URLSpan;
import android.util.AttributeSet;

public class TextViewNoUnderline extends AppCompatTextView {
    public TextViewNoUnderline(Context context) {
        this(context, null);
    }

    public TextViewNoUnderline(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public TextViewNoUnderline(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSpannableFactory(Factory.getInstance());
    }

    private static class Factory extends Spannable.Factory {
        private final static Factory sInstance = new Factory();

        public static Factory getInstance() {
            return sInstance;
        }

        @Override
        public Spannable newSpannable(CharSequence source) {
            return new SpannableNoUnderline(source);
        }
    }

    private static class SpannableNoUnderline extends SpannableString {
        public SpannableNoUnderline(CharSequence source) {
            super(source);
        }

        @Override
        public void setSpan(Object what, int start, int end, int flags) {
            if (what instanceof URLSpan) {
                what = new UrlSpanNoUnderline((URLSpan) what);
            }
            super.setSpan(what, start, end, flags);
        }
    }
}
