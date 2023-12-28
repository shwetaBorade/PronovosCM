package com.pronovoscm.utils.photoeditor.model;

import android.graphics.Paint;
import android.graphics.Paint.Style;

public class ArrowPath extends Shape {
    private Paint mPaint;
    private float mStartX;
    private float mStartY;
    private float mEndX;
    private float mEndY;
    private int color;
    private Style style;

    public ArrowPath(float mStartX, float mStartY, float mEndX, float mEndY, Paint mPaint, int color) {
        this.mStartX = mStartX;
        this.mStartY = mStartY;
        this.mEndX = mEndX;
        this.mEndY = mEndY;
        this.mPaint = mPaint;
        this.color = color;
        this.style = mPaint.getStyle();
    }

    public Paint getPaint() {
        return mPaint;
    }

    public float getStartX() {
        return mStartX;
    }

    public float getStartY() {
        return mStartY;
    }

    public float getEndX() {
        return mEndX;
    }

    public float getEndY() {
        return mEndY;
    }

    public int getColor() {
        return color;
    }

    public Style getStyle() {
        return style;
    }
}