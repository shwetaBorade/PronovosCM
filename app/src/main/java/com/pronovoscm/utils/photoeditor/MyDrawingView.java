package com.pronovoscm.utils.photoeditor;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.pronovoscm.utils.photoeditor.model.ArrowPath;
import com.pronovoscm.utils.photoeditor.model.Circle;
import com.pronovoscm.utils.photoeditor.model.CircleFilled;
import com.pronovoscm.utils.photoeditor.model.LinePath;
import com.pronovoscm.utils.photoeditor.model.PathStore;
import com.pronovoscm.utils.photoeditor.model.Rectangle;
import com.pronovoscm.utils.photoeditor.model.RectangleFilled;

import java.util.Stack;

/**
 * <p>
 * This is custom drawing view used to do painting on user touch events it it will paint on canvas
 * as per attributes provided to the paint
 * </p>
 */
public class MyDrawingView extends View {

    public static final String TAG = MyDrawingView.class.getSimpleName();
    public static final float TOUCH_TOLERANCE = 4;
    public static final float TOUCH_STROKE_WIDTH = 5;
    protected Path mPath;
    protected Paint mPaint;
    protected Paint mPaintErase;
    //    protected Paint mPaint;
    protected Bitmap mBitmap;
    protected Canvas mCanvas;
    /**
     * Indicates if you are drawing
     */
    protected boolean isDrawing = false;
    /**
     * Indicates if the drawing is ended
     */
    protected boolean isDrawingEnded = false;
    protected float mStartX;
    protected float mStartY;
    protected float mx;
    protected float my;
    int countTouch = 0;
    float basexTriangle = 0;
    float baseyTriangle = 0;
    private ToolType toolType = ToolType.BRUSH;
    private float mBrushSize = 8;
    private int mOpacity = 255;
    private float mBrushEraserSize = 50;
    private Stack mDrawnPaths = new Stack<>();
    private boolean mBrushDrawMode;
    private MyBrushViewChangeListener mMyBrushViewChangeListener;
    private int mSelectedColor = Color.BLACK;
    private ToolType tempToolType;

    public MyDrawingView(Context context) {
        this(context, null);
    }

    public MyDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupBrushDrawing();
    }

    public MyDrawingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setupBrushDrawing();
    }

    private void setupBrushDrawing() {
        //Caution: This line is to disable hardware acceleration to make eraser feature work properly
        setLayerType(LAYER_TYPE_HARDWARE, null);
        mPaint = new Paint();
        mPath = new Path();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mBrushSize);
        mPaint.setAlpha(mOpacity);
        //Resolve Brush color changes after saving image  #52
        //Resolve Brush bug using PorterDuff.Mode.SRC_OVER #80 and PR #83
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

        mPaintErase = new Paint();
        mPaintErase.setAntiAlias(true);
        mPaintErase.setDither(true);
        mPaintErase.setColor(Color.BLACK);
        mPaintErase.setStyle(Paint.Style.STROKE);
        mPaintErase.setStrokeJoin(Paint.Join.ROUND);
        mPaintErase.setStrokeCap(Paint.Cap.ROUND);
        mPaintErase.setStrokeWidth(25);
        mPaintErase.setAlpha(mOpacity);
        //Resolve Brush color changes after saving image  #52
        //Resolve Brush bug using PorterDuff.Mode.SRC_OVER #80 and PR #83
        mPaintErase.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        this.setVisibility(View.GONE);
    }

    private void refreshBrushDrawing() {
        mBrushDrawMode = true;
        mPath = new Path();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mBrushSize);
        mPaint.setAlpha(mOpacity);
        //Resolve Brush color changes after saving image  #52
        //Resolve Brush bug using PorterDuff.Mode.SRC_OVER #80 and PR #83
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
    }

    void brushEraser() {
//        setBrushDrawingMode(true, ToolType.ERASER);
//        setBrushDrawingMode(false, ToolType.BRUSH);
        toolType = ToolType.ERASER;
        mBrushDrawMode = true;
//        mPaint.setStrokeWidth(mBrushEraserSize);
//        mPaint.setColor(Color.TRANSPARENT);
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    void setBrushDrawingMode(boolean brushDrawMode, ToolType viewType) {
        this.mBrushDrawMode = brushDrawMode;
        this.toolType = viewType;
        if (brushDrawMode) {
            this.setVisibility(View.VISIBLE);
            refreshBrushDrawing();
            if (toolType == ToolType.RECTANGLE_FILLED || toolType == ToolType.CIRCLE_FILLED) {
                mPaint.setStyle(Paint.Style.FILL);
            } else if (toolType == ToolType.ERASER) {
                mPaint.setStyle(Paint.Style.FILL);
            }
        }
    }

    void setOpacity(@IntRange(from = 0, to = 255) int opacity) {
        this.mOpacity = opacity;
        setBrushDrawingMode(true, toolType);
    }

    boolean getBrushDrawingMode() {
        return mBrushDrawMode;
    }

    void setBrushEraserSize(float brushEraserSize) {
        this.mBrushEraserSize = brushEraserSize;
        setBrushDrawingMode(true, toolType);
    }

    void setBrushEraserColor(@ColorInt int color) {
        mPaint.setColor(color);
        setBrushDrawingMode(true, toolType);
    }

    float getEraserSize() {
        return mBrushEraserSize;
    }

    float getBrushSize() {
        return mBrushSize;
    }

    void setBrushSize(float size) {
        mBrushSize = size;
        setBrushDrawingMode(true, toolType);
    }

    int getBrushColor() {
        return mPaint.getColor();
    }

    void setBrushColor(@ColorInt int color) {
        this.mSelectedColor = color;
        mPaint.setColor(color);
        setBrushDrawingMode(true, toolType);
    }

    void clearAll() {
//        mDrawnPaths.clear();
//        mRedoPaths.clear();
//        if (mCanvas != null) {
//            mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
//        }
//        invalidate();
    }

    void setBrushViewChangeListener(MyBrushViewChangeListener brushViewChangeListener) {
        mMyBrushViewChangeListener = brushViewChangeListener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        try {
            if (w > 0 && h > 0) {
                mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                mCanvas = new Canvas(mBitmap);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawBitmap(mBitmap, 0, 0, null);
        if (mBitmap != null) {
            mBitmap = Bitmap.createScaledBitmap(mBitmap, canvas.getWidth(), canvas.getHeight(), true);
            canvas.drawBitmap(mBitmap, 0f, 0f, null);
        }
        if (isDrawing) {

//            if (toolType != ToolType.ERASER) {
//                if (mBitmap != null) {
//                    mBitmap = Bitmap.createScaledBitmap(mBitmap, canvas.getWidth(), canvas.getHeight(), true);
//                    canvas.drawBitmap(mBitmap, 0f, 0f, null);
//                }
//            } else {
//
//                if (mBitmap != null) {
//                    mBitmap = Bitmap.createScaledBitmap(mBitmap, canvas.getWidth(), canvas.getHeight(), true);
//                    canvas.drawBitmap(mBitmap, 0f, 0f, null);
//                }
//                canvas.drawBitmap(mBitmap, 0, 0, mPaintErase);
//
//            }

            onDrawFromStack(canvas);

            switch (toolType) {
                case LINE:
                case ARROW:
                    onDrawLine(canvas);
                    break;
                case SMOOTHLINE:
                    onDrawSmoothLine(canvas);
                    break;
                case RECTANGLE:
                case RECTANGLE_FILLED:
                    onDrawRectangle(canvas, false);
                    break;
                case SQUARE:
                    onDrawSquare(canvas, false);
                    break;
                case CIRCLE:
                case CIRCLE_FILLED:
                    onDrawCircle(canvas, false);
                    break;
                case TRIANGLE:
                    onDrawTriangle(canvas);
                    break;
                case ERASER:
                    onDrawEraser(canvas);
                    break;
            }

        } else if (toolType == ToolType.UNDO) {
            Log.d(TAG, "onDraw: Undo");

            if (mBitmap != null) {
                mBitmap = Bitmap.createScaledBitmap(mBitmap, canvas.getWidth(), canvas.getHeight(), true);
                canvas.drawBitmap(mBitmap, 0f, 0f, null);
            }

            if (!mDrawnPaths.empty()) {

                if (mBitmap != null) {
                    mBitmap = Bitmap.createScaledBitmap(mBitmap, canvas.getWidth(), canvas.getHeight(), true);
                    canvas.drawBitmap(mBitmap, 0f, 0f, null);
                }
                mDrawnPaths.pop();

                onDrawFromStack(canvas);
            }
            // Reset the previous drawing tool
            toolType = tempToolType;
        } else {
            if (mBitmap != null) {
                mBitmap = Bitmap.createScaledBitmap(mBitmap, canvas.getWidth(), canvas.getHeight(), true);
                canvas.drawBitmap(mBitmap, 0f, 0f, null);
            }
            if (!mDrawnPaths.empty()) {

                if (mBitmap != null) {
                    mBitmap = Bitmap.createScaledBitmap(mBitmap, canvas.getWidth(), canvas.getHeight(), true);
                    canvas.drawBitmap(mBitmap, 0f, 0f, null);
                }
                onDrawFromStack(canvas);
            }
        }
    }

    private void onDrawFromStack(Canvas canvas) {
        for (Object obj : mDrawnPaths) {

            Log.d(TAG, "onDraw: Circle");
            if (obj instanceof Circle) {
                mPaint.setColor(((Circle) obj).getColor());
                mPaint.setStyle(((Circle) obj).getStyle());
                canvas.drawOval(((Circle) obj).getRectF(), ((Circle) obj).getPaint());

            } else if (obj instanceof CircleFilled) {
                mPaint.setColor(((CircleFilled) obj).getColor());
                mPaint.setStyle(((CircleFilled) obj).getStyle());
                canvas.drawOval(((CircleFilled) obj).getRectF(), ((CircleFilled) obj).getPaint());

            } else if (obj instanceof Rectangle) {
                mPaint.setColor(((Rectangle) obj).getColor());
                mPaint.setStyle(((Rectangle) obj).getStyle());
                canvas.drawRect(((Rectangle) obj).getLeft(), ((Rectangle) obj).getTop(), ((Rectangle) obj).getRight(), ((Rectangle) obj).getBottom(), ((Rectangle) obj).getPaint());

            } else if (obj instanceof RectangleFilled) {
                mPaint.setColor(((RectangleFilled) obj).getColor());
                mPaint.setStyle(((RectangleFilled) obj).getStyle());
                canvas.drawRect(((RectangleFilled) obj).getLeft(), ((RectangleFilled) obj).getTop(), ((RectangleFilled) obj).getRight(), ((RectangleFilled) obj).getBottom(), ((RectangleFilled) obj).getPaint());

            } else if (obj instanceof PathStore) {
                mPaint.setColor(((PathStore) obj).getColor());
                mPaint.setStyle(((PathStore) obj).getStyle());
                canvas.drawPath(((PathStore) obj).getDrawPath(), ((PathStore) obj).getDrawPaint());

            } else if (obj instanceof LinePath) {
                mPaint.setColor(((LinePath) obj).getColor());
                mPaint.setStyle(((LinePath) obj).getStyle());
                canvas.drawLine(((LinePath) obj).getStartX(), ((LinePath) obj).getStartY(), ((LinePath) obj).getEndX(), ((LinePath) obj).getEndY(), ((LinePath) obj).getPaint());

            } else if (obj instanceof ArrowPath) {
                mPaint.setColor(((ArrowPath) obj).getColor());
                mPaint.setStyle(((ArrowPath) obj).getStyle());
                canvas.drawLine(((ArrowPath) obj).getStartX(), ((ArrowPath) obj).getStartY(), ((ArrowPath) obj).getEndX(), ((ArrowPath) obj).getEndY(), ((ArrowPath) obj).getPaint());
                drawArrow(canvas, ((ArrowPath) obj).getStartX(), ((ArrowPath) obj).getStartY(), ((ArrowPath) obj).getEndX(), ((ArrowPath) obj).getEndY(), ((ArrowPath) obj).getPaint());
            }
        }
    }


    //------------------------------------------------------------------
    // Line
    //------------------------------------------------------------------

    /**
     * Handle touch event to draw paint on canvas i.e brush drawing
     *
     * @param event points having touch info
     * @return true if handling touch events
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {

        if (mBrushDrawMode) {
            mx = event.getX();
            my = event.getY();
            switch (toolType) {
                case LINE:
                    onTouchEventLine(event);
                    break;
                case SMOOTHLINE:
                    onTouchEventSmoothLine(event);
                    break;
                case RECTANGLE:
                case RECTANGLE_FILLED:
                    onTouchEventRectangle(event);
                    break;
                case SQUARE:
                    onTouchEventSquare(event);
                    break;
                case CIRCLE:
                case CIRCLE_FILLED:
                    onTouchEventCircle(event);
                    break;
                case TRIANGLE:
                    onTouchEventTriangle(event);
                    break;
                case ARROW:
                    onTouchEventArrow(event);
                    break;
                case ERASER:
                    onTouchEventEraser(event);
                    break;
            }
            return true;
        } else {
            return false;
        }
    }

    boolean undo() {
        tempToolType = toolType;
        toolType = ToolType.UNDO;
        if (!mDrawnPaths.empty()) {
//            mRedoPaths.push(mDrawnPaths.pop());
            invalidate();
        }
        if (mMyBrushViewChangeListener != null) {
            mMyBrushViewChangeListener.onViewRemoved(this);
        }
//        return !mDrawnPaths.empty();
        return false;
    }

    boolean redo() {
//        if (!mRedoPaths.empty()) {
//            mDrawnPaths.push(mRedoPaths.pop());
//            invalidate();
//        }
//
        if (mMyBrushViewChangeListener != null) {
            mMyBrushViewChangeListener.onViewAdd(this);
        }
//        return !mRedoPaths.empty();
        return false;
    }

    private void onDrawLine(Canvas canvas) {

        float dx = Math.abs(mx - mStartX);
        float dy = Math.abs(my - mStartY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPaint.setColor(mSelectedColor);
            canvas.drawLine(mStartX, mStartY, mx, my, mPaint);
        }
    }

    //------------------------------------------------------------------
    // Triangle
    //------------------------------------------------------------------

    private void onTouchEventLine(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mStartX = mx;
                mStartY = my;
                invalidate();
                if (mMyBrushViewChangeListener != null) {
                    mMyBrushViewChangeListener.onStartDrawing(toolType);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                mCanvas.drawLine(mStartX, mStartY, mx, my, mPaint);
                mPaint.setColor(mSelectedColor);
                mDrawnPaths.push(new LinePath(mStartX, mStartY, mx, my, mPaint, mSelectedColor));
                invalidate();
                if (mMyBrushViewChangeListener != null) {
                    mMyBrushViewChangeListener.onStopDrawing(toolType);
                    mMyBrushViewChangeListener.onViewAdd(this);
                }
                break;
        }
    }

    //------------------------------------------------------------------
    // Smooth Line
    //------------------------------ ------------------------------------
    private void onDrawSmoothLine(Canvas canvas) {
        mPaint.setColor(mSelectedColor);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mPath, mPaint);
    }

    private void onTouchEventSmoothLine(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mStartX = mx;
                mStartY = my;

                mPath.reset();
                mPath.moveTo(mx, my);

                invalidate();
                if (mMyBrushViewChangeListener != null) {
                    mMyBrushViewChangeListener.onStartDrawing(toolType);
                }
                break;
            case MotionEvent.ACTION_MOVE:

                float dx = Math.abs(mx - mStartX);
                float dy = Math.abs(my - mStartY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    mPath.quadTo(mStartX, mStartY, (mx + mStartX) / 2, (my + mStartY) / 2);
                    mStartX = mx;
                    mStartY = my;
                }
                mCanvas.drawPath(mPath, mPaint);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                mPaint.setColor(mSelectedColor);
                mPath.lineTo(mStartX, mStartY);
                mCanvas.drawPath(mPath, mPaint);
                mDrawnPaths.push(new PathStore(mPath, mPaint, mSelectedColor));
                mPath.reset();
//                mDrawnPaths.push(new PathStore(mPath, mPaint.getColor(), (int) mPaint.getStrokeWidth()));
                invalidate();
                if (mMyBrushViewChangeListener != null) {
                    mMyBrushViewChangeListener.onStopDrawing(toolType);
                    mMyBrushViewChangeListener.onViewAdd(this);
                }
                break;
        }
    }

    private void onDrawTriangle(Canvas canvas) {
        mPaint.setColor(mSelectedColor);
        if (countTouch < 3) {
            canvas.drawLine(mStartX, mStartY, mx, my, mPaint);
        } else if (countTouch == 3) {
            canvas.drawLine(mx, my, mStartX, mStartY, mPaint);
            canvas.drawLine(mx, my, basexTriangle, baseyTriangle, mPaint);
        }
    }

    private void onTouchEventTriangle(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                countTouch++;
                if (countTouch == 1) {
                    isDrawing = true;
                    mStartX = mx;
                    mStartY = my;
                } else if (countTouch == 3) {
                    isDrawing = true;
                }
                invalidate();
                if (mMyBrushViewChangeListener != null) {
                    mMyBrushViewChangeListener.onStartDrawing(toolType);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                countTouch++;
                isDrawing = false;
                if (countTouch < 3) {
                    basexTriangle = mx;
                    baseyTriangle = my;
                    mCanvas.drawLine(mStartX, mStartY, mx, my, mPaint);
                } else if (countTouch >= 3) {
                    mCanvas.drawLine(mx, my, mStartX, mStartY, mPaint);
                    mCanvas.drawLine(mx, my, basexTriangle, baseyTriangle, mPaint);
                    countTouch = 0;
                }
                invalidate();
                if (mMyBrushViewChangeListener != null) {
                    mMyBrushViewChangeListener.onStopDrawing(toolType);
                    mMyBrushViewChangeListener.onViewAdd(this);
                }
                break;
        }
    }

    //------------------------------------------------------------------
    // Circle
    //------------------------------------------------------------------

    private void onDrawCircle(Canvas canvas, boolean isActionUp) {
        float right = mStartX > mx ? mStartX : mx;
        float left = mStartX > mx ? mx : mStartX;
        float bottom = mStartY > my ? mStartY : my;
        float top = mStartY > my ? my : mStartY;
        if (toolType == ToolType.CIRCLE) {
            mPaint.setStyle(Paint.Style.STROKE);
        } else {
            mPaint.setStyle(Paint.Style.FILL);
        }
        mPaint.setColor(mSelectedColor);
        canvas.drawOval(new RectF(left, top, right, bottom), mPaint);

        if (isActionUp) {
            if (toolType == ToolType.CIRCLE) {
                mDrawnPaths.push(new Circle(new RectF(left, top, right, bottom), mPaint, mSelectedColor));
            } else {
                mDrawnPaths.push(new CircleFilled(new RectF(left, top, right, bottom), mPaint, mSelectedColor));
            }
        }
    }

    private void onTouchEventCircle(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mStartX = mx;
                mStartY = my;
                invalidate();
                if (mMyBrushViewChangeListener != null) {
                    mMyBrushViewChangeListener.onStartDrawing(toolType);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;

                onDrawCircle(mCanvas, true);
                invalidate();
                if (mMyBrushViewChangeListener != null) {
                    mMyBrushViewChangeListener.onStopDrawing(toolType);
                    mMyBrushViewChangeListener.onViewAdd(this);
                }
                break;
        }
    }

    /**
     * @return
     */
    protected float calculateRadius(float x1, float y1, float x2, float y2) {

        return (float) Math.sqrt(
                Math.pow(x1 - x2, 2) +
                        Math.pow(y1 - y2, 2)
        );
    }

    //------------------------------------------------------------------
    // Rectangle
    //------------------------------------------------------------------

    private void onDrawRectangle(Canvas canvas, boolean isActionUp) {
        drawRectangle(canvas, mPaint, isActionUp);
    }

    private void onTouchEventRectangle(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mStartX = mx;
                mStartY = my;
                invalidate();
                if (mMyBrushViewChangeListener != null) {
                    mMyBrushViewChangeListener.onStartDrawing(toolType);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                drawRectangle(mCanvas, mPaint, true);
                invalidate();
                if (mMyBrushViewChangeListener != null) {
                    mMyBrushViewChangeListener.onStopDrawing(toolType);
                    mMyBrushViewChangeListener.onViewAdd(this);
                }
                break;
        }
    }

    private void drawRectangle(Canvas canvas, Paint paint, boolean isActionUp) {
        float right = mStartX > mx ? mStartX : mx;
        float left = mStartX > mx ? mx : mStartX;
        float bottom = mStartY > my ? mStartY : my;
        float top = mStartY > my ? my : mStartY;

        if (toolType == ToolType.RECTANGLE) {
            paint.setStyle(Paint.Style.STROKE);
        } else {
            paint.setStyle(Paint.Style.FILL);
        }
        paint.setColor(mSelectedColor);
        canvas.drawRect(left, top, right, bottom, paint);

        if (isActionUp) {
            if (toolType == ToolType.RECTANGLE) {
                mDrawnPaths.push(new Rectangle(left, top, right, bottom, paint, mSelectedColor));
            } else {
                mDrawnPaths.push(new RectangleFilled(left, top, right, bottom, paint, mSelectedColor));
            }
        }
    }

    //------------------------------------------------------------------
    // Square
    //------------------------------------------------------------------

    private void onDrawSquare(Canvas canvas, boolean isActionUp) {
        onDrawRectangle(canvas, isActionUp);
    }

    private void onTouchEventSquare(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mStartX = mx;
                mStartY = my;
                invalidate();
                if (mMyBrushViewChangeListener != null) {
                    mMyBrushViewChangeListener.onStartDrawing(toolType);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                adjustSquare(mx, my);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                adjustSquare(mx, my);
                drawRectangle(mCanvas, mPaint, true);
                invalidate();
                if (mMyBrushViewChangeListener != null) {
                    mMyBrushViewChangeListener.onStopDrawing(toolType);
                    mMyBrushViewChangeListener.onViewAdd(this);
                }
                break;
        }
    }


    //------------------------------------------------------------------
    // Eraser
    //------------------------------------------------------------------


    private void onDrawEraser(Canvas canvas) {
        canvas.drawPath(mPath, mPaintErase);
    }

    private void onTouchEventEraser(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mStartX = mx;
                mStartY = my;

                mPath.reset();
                mPath.moveTo(mx, my);

                invalidate();
                if (mMyBrushViewChangeListener != null) {
                    mMyBrushViewChangeListener.onStartDrawing(toolType);
                }
                break;
            case MotionEvent.ACTION_MOVE:

                float dx = Math.abs(mx - mStartX);
                float dy = Math.abs(my - mStartY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    mPath.quadTo(mStartX, mStartY, (mx + mStartX) / 2, (my + mStartY) / 2);
                    mStartX = mx;
                    mStartY = my;
                }

                mCanvas.drawPath(mPath, mPaintErase);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                mPaint.setColor(mSelectedColor);
                mPath.lineTo(mStartX, mStartY);
                mCanvas.drawPath(mPath, mPaintErase);
                mDrawnPaths.push(new PathStore(mPath, mPaintErase, mSelectedColor));
                mPath.reset();
                if (mMyBrushViewChangeListener != null) {
                    mMyBrushViewChangeListener.onStopDrawing(toolType);
                    mMyBrushViewChangeListener.onViewAdd(this);
                }
                invalidate();
                break;
        }
    }


    //------------------------------------------------------------------
    // Arrow
    //------------------------------------------------------------------


    public void drawArrow(Canvas canvas, float startx, float starty, float endX, float endY, Paint paint) {
        int startX = midpoint(startx, starty, endX, endY).x;
        int startY = midpoint(startx, starty, endX, endY).y;

        float deltaX = endX - startX;
        float deltaY = endY - startY;
        float frac = (float) 0.08;
        float point_x_1 = startX + (float) ((1 - frac) * deltaX + frac * deltaY);
        float point_y_1 = startY + (float) ((1 - frac) * deltaY - frac * deltaX);
        float point_x_2 = endX;
        float point_y_2 = endY;
        float point_x_3 = startX + (float) ((1 - frac) * deltaX - frac * deltaY);
        float point_y_3 = startY + (float) ((1 - frac) * deltaY + frac * deltaX);
        Path mPath = new Path();
        mPath.moveTo(point_x_1, point_y_1);
        mPath.lineTo(point_x_2, point_y_2);
        mPath.lineTo(point_x_3, point_y_3);
        canvas.drawPath(mPath, paint);
    }


    private void onTouchEventArrow(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mStartX = mx;
                mStartY = my;

                mPath.reset();
                mPath.moveTo(mx, my);
                invalidate();
                if (mMyBrushViewChangeListener != null) {
                    mMyBrushViewChangeListener.onStartDrawing(toolType);
                }
                break;
            case MotionEvent.ACTION_MOVE:

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(mSelectedColor);
                drawArrow(mCanvas, mStartX, mStartY, mx, my, mPaint);
                mCanvas.drawLine(mStartX, mStartY, mx, my, mPaint);
                mDrawnPaths.push(new ArrowPath(mStartX, mStartY, mx, my, mPaint, mSelectedColor));
                invalidate();
                mPaint.setStyle(Paint.Style.STROKE);
                mPath.reset();
                if (mMyBrushViewChangeListener != null) {
                    mMyBrushViewChangeListener.onStopDrawing(toolType);
                    mMyBrushViewChangeListener.onViewAdd(this);
                }
                break;
        }
    }


    /**
     * Adjusts current coordinates to build a square
     *
     * @param x
     * @param y
     */
    protected void adjustSquare(float x, float y) {
        float deltaX = Math.abs(mStartX - x);
        float deltaY = Math.abs(mStartY - y);

        float max = Math.max(deltaX, deltaY);

        mx = mStartX - x < 0 ? mStartX + max : mStartX - max;
        my = mStartY - y < 0 ? mStartY + max : mStartY - max;
    }


    private Point midpoint(float x1, float y1, float x2, float y2) {
        Point point = new Point();
        point.set((int) (x1 + x2) / 2, (int) (y1 + y2) / 2);
        return point;
    }
}