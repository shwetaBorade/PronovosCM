//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2019 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.pdf.adapter;

import android.graphics.Bitmap;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdftron.pdf.tools.R;

public class StandardRubberStampAdapter extends RecyclerView.Adapter<StandardRubberStampAdapter.ViewHolder> {

    private Bitmap[] mBitmaps;

    public StandardRubberStampAdapter(Bitmap[] bitmaps) {
        mBitmaps = bitmaps;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_rubber_stamp, parent, false);
        return new StandardRubberStampAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mStampView.setImageBitmap(mBitmaps[position]);
    }

    @Override
    public int getItemCount() {
        return mBitmaps == null ? 0 : mBitmaps.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView mStampView;

        public ViewHolder(View itemView) {
            super(itemView);
            mStampView = itemView.findViewById(R.id.stamp_image_view);
        }

    }

}
