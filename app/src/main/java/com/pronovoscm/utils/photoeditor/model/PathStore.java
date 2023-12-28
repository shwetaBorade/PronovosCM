package com.pronovoscm.utils.photoeditor.model;

import android.graphics.Paint;
import android.graphics.Path;

/**
 * Modal class to store the path drawn by the User.
 * The object is later used to draw a line in the DrawingCanvas class.
 */
public class PathStore extends Shape {

    private boolean isRemove;
    private Paint mPaint;
    private Path mDrawPath;
    private int color;
    private Paint.Style style;

    public PathStore(Path drawPath, Paint drawPaints, int color) {
        mPaint = new Paint(drawPaints);
        mDrawPath = new Path(drawPath);
        this.color = color;
        this.style = drawPaints.getStyle();
    }

    public PathStore(Path drawPath, Paint drawPaints, boolean isRemove, int color) {
        mPaint = new Paint(drawPaints);
        mDrawPath = new Path(drawPath);
        this.isRemove = isRemove;
        this.color = color;
    }

    public Paint getDrawPaint() {
        return mPaint;
    }

    public Path getDrawPath() {
        return mDrawPath;
    }

    public boolean isRemove() {
        return isRemove;
    }

    public int getColor() {
        return color;
    }

    public Paint.Style getStyle() {
        return style;
    }
}
