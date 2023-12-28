package com.pronovoscm.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.pronovoscm.R;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

/**
 * Created on 27/9/18.
 *
 * @author Sanjay Kushwah
 */
public class PhotoshopView extends View {
    private static final float STROKE_WIDTH = 7f;
    private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
    private final RectF dirtyRect = new RectF();
    private Paint paint = new Paint();
    private Path path = new Path();
    private float lastTouchX, firstTouchX, centerX;
    private float lastTouchY, firstTouchY, centerY;
    private boolean isCircle = true;

    public PhotoshopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.colorPrimary));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(STROKE_WIDTH);
    }

    public Bitmap save(View v, String StoredPath, LinearLayout content) {
        Log.v("tag", "Width: " + v.getWidth());
        Log.v("tag", "Height: " + v.getHeight());
        Bitmap mBitmap = null;
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(content.getWidth(), content.getHeight(), Bitmap.Config.RGB_565);
        }
        Canvas canvas = new Canvas(mBitmap);
        try {
            // Output the file
            FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
            v.draw(canvas);
            // Convert the output file to Image such as .png
            mBitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);


            mFileOutStream.flush();
            mFileOutStream.close();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

            return mBitmap;
//                byte[] byteArray = byteArrayOutputStream.toByteArray();
//                // to encode base64 from byte array use following method
//                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

//                uploadImage(encoded);
        } catch (Exception e) {
            Log.v("log_tag", e.toString());
            return null;
        }
    }

    public void clear() {
        path.reset();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isCircle) {
//            canvas.drawCircle(centerX, centerY,
//                    (float) Math.sqrt(Math.abs(firstTouchX - lastTouchX) * Math.sqrt(Math.abs(firstTouchX - lastTouchX)) + Math.sqrt(Math.abs(firstTouchY - lastTouchY) * Math.sqrt(Math.abs(firstTouchY - lastTouchY))))
//                    , paint);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.drawOval(firstTouchX, firstTouchY, lastTouchX, lastTouchY

                        , paint);
            }
        } else {
            canvas.drawPath(path, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        // mGetSign.setEnabled(true);

        if (isCircle) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
//                    path.moveTo(eventX, eventY);
                    if (firstTouchX == 0 && firstTouchY == 0) {
                        firstTouchX = eventX;
                        firstTouchY = eventY;
                        lastTouchX = eventX;
                        lastTouchY = eventY;
                    }
                    return true;

                case MotionEvent.ACTION_MOVE:
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_UP:
                    //resetDirtyRect(eventX, eventY);
//                    centerX = Math.abs(firstTouchX - lastTouchX);
//                    centerY = Math.abs(firstTouchY - lastTouchY);
//                    midpoint(firstTouchX, lastTouchX, firstTouchY, lastTouchY);
                    break;
                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    int historySize1 = event.getHistorySize();
                    for (int i = 0; i < historySize1; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    return true;

                case MotionEvent.ACTION_UP:
                    //resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;
                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }
        }
//        invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
//                (int) (dirtyRect.top - HALF_STROKE_WIDTH),
//                (int) (dirtyRect.right + HALF_STROKE_WIDTH),
//                (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));
        invalidate();

        lastTouchX = eventX;
        lastTouchY = eventY;

        return true;
    }

    void midpoint(float x1, float x2, float y1, float y2) {
        System.out.print((x1 + x2) / 2 +
                " , " + (y1 + y2) / 2);
        centerX = (x1 + x2) / 2;
        centerY = (x1 + x2) / 2;
    }

    float distance(float x1, float y1, float x2, float y2) {
        // Calculating distance
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) +
                Math.pow(y2 - y1, 2) * 1.0);
    }


    private void debug(String string) {
        Log.v("log_tag", string);
    }

    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < dirtyRect.left) {
            dirtyRect.left = historicalX;
        } else if (historicalX > dirtyRect.right) {
            dirtyRect.right = historicalX;
        }

        if (historicalY < dirtyRect.top) {
            dirtyRect.top = historicalY;
        } else if (historicalY > dirtyRect.bottom) {
            dirtyRect.bottom = historicalY;
        }
    }

    //left, top, right, bottom
    private void resetDirtyRect(float eventX, float eventY) {
        dirtyRect.left = Math.min(lastTouchX, eventX);
        dirtyRect.right = Math.max(lastTouchX, eventX);
        dirtyRect.top = Math.min(lastTouchY, eventY);
        dirtyRect.bottom = Math.max(lastTouchY, eventY);
    }
}
