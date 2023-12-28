package com.pronovoscm.utils.photoeditor.model;

import android.graphics.Paint;

/**
 * Modal class to store the Rectangle dimensions entered by the User.
 * This dimensions are used to draw the rectangle in the DrawingCanvas class.
 */
public class RectangleFilled extends Shape {

    private float top;
    private float bottom;
    private float left;
    private float right;

    private Paint paint;
    private int color;
    private Paint.Style style;


    public RectangleFilled(float left, float top, float right, float bottom, Paint paint, int color) {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        this.paint = paint;
        this.color = color;
        this.style = paint.getStyle();
    }


    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getBottom() {
        return bottom;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public int getColor() {
        return color;
    }

    public Paint.Style getStyle() {
        return style;
    }
}
