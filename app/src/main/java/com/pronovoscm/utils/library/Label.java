package com.pronovoscm.utils.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pronovoscm.R;

/*
 * Copyright (C) 2015 David Pizarro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class Label extends LinearLayout {

    private TextView mTextView;
    private OnClickCrossListener listenerOnCrossClick;
    private OnLabelClickListener listenerOnLabelClick;
    private ImageView imageView;
    private int position;

    public Label(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Label(Context context, int textSize, int iconCross,
                 boolean showCross, int textColor, int backgroundResource, boolean labelsClickables, int padding, int position) {
        super(context);
        init(context, textSize, iconCross, showCross, textColor,
                backgroundResource, labelsClickables, padding, position);
    }

    private void init(final Context context, int textSize, int iconCross,
                      boolean showCross, int textColor, int backgroundResource, boolean labelsClickables, int padding, int position) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View labelView = inflater.inflate(R.layout.keyword_name_view, this, true);

        LinearLayout linearLayout = labelView.findViewById(R.id.keywordLinearLayout);

        if (labelsClickables) {
            linearLayout.setClickable(true);
            linearLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listenerOnLabelClick != null) {
                        listenerOnLabelClick.onClickLabel((Label) labelView, position);
                    }
                }
            });
        }

        mTextView = labelView.findViewById(R.id.keywordNameTextView);
//        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
//        mTextView.setTextColor(textColor);
        this.position = position;
        imageView = labelView.findViewById(R.id.crossIcon);

        if (showCross) {

            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listenerOnCrossClick != null) {
                        listenerOnCrossClick.onClickCross((Label) labelView, position);
                    }
                }
            });
        } else {
            imageView.setVisibility(GONE);
        }

    }

    public String getText() {
        return mTextView.getText().toString();
    }

    public void setText(String text) {
        mTextView.setText(text);
    }

    public void setIcon(int drawable, boolean isClickable) {

        imageView.setImageResource(drawable);
        imageView.setClickable(isClickable);
    }

    /**
     * Set a callback listener when the cross icon is clicked.
     *
     * @param listener Callback instance.
     */
    public void setOnClickCrossListener(OnClickCrossListener listener) {
        this.listenerOnCrossClick = listener;
    }

    /**
     * Set a callback listener when the {@link Label} is clicked.
     *
     * @param listener Callback instance.
     */
    public void setOnLabelClickListener(OnLabelClickListener listener) {
        this.listenerOnLabelClick = listener;
    }

    /**
     * Interface for a callback listener when the cross icon is clicked.
     */
    public interface OnClickCrossListener {

        /**
         * Call when the cross icon is clicked.
         */
        void onClickCross(Label label, int position);
    }

    /**
     * Interface for a callback listener when the {@link Label} is clicked.
     * Container Activity/Fragment must implement this interface.
     */
    public interface OnLabelClickListener {

        /**
         * Call when the {@link Label} is clicked.
         */
        void onClickLabel(Label label, int position);
    }
}
