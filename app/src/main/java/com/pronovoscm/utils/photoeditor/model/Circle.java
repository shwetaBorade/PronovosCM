package com.pronovoscm.utils.photoeditor.model;

import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

/**
 * Modal class to store the Circle dimensions entered by the User.
 * This dimensions are used to draw the circle in the DrawingCanvas class.
 */
public class Circle extends Shape {

    private RectF rectF;
    private Paint paint;
    private int color;
    private Style style;


    public Circle(RectF rectF, Paint paint, int color) {
        this.rectF = rectF;
        this.paint = paint;
        this.color = color;
        this.style = paint.getStyle();
    }


    public RectF getRectF() {
        return rectF;
    }

    public void setRectF(RectF rectF) {
        this.rectF = rectF;
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

    public void setColor(int color) {
        this.color = color;
    }

    public Style getStyle() {
        return style;
    }
}
