package com.pronovoscm.utils.photoeditor;


/**
 * Created on 1/17/2018.
 *
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * <p></p>
 */

interface MyBrushViewChangeListener {
    void onViewAdd(MyDrawingView myDrawingView);

    void onViewRemoved(MyDrawingView myDrawingView);

    void onStartDrawing(ToolType toolType);

    void onStopDrawing(ToolType toolType);
}
