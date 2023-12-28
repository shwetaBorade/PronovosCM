package com.pronovoscm.utils.customcamera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.pronovoscm.R;

/**
 * Created on 15/11/18.
 *
 * @author GWL
 */
public class ShutterView extends View {

    public ShutterView(Context context) {
        super(context);
        setColorLevel();
    }

    public ShutterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setColorLevel();
    }


    public void setColorLevel() {
        setBackgroundColor(getResources().getColor(R.color.translucent_blue));
        setVisibility(GONE);
    }


    public void playShutterEffect() {
        post(new Runnable() {
            @Override
            public void run() {
                setVisibility(VISIBLE);
            }
        });
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisibility(GONE);
            }
        }, 100);
    }
}