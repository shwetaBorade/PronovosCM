package com.pronovoscm.utils.formwebview;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebClient extends WebViewClient {

    private static String TAG = "nitin";

    public WebClient() {

    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        Log.i(TAG, "onPageStarted: "+url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
       // Log.i(TAG, "onPageFinished: "+ url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d(TAG, "shouldOverrideUrlLoading: "+url);
       return false;
    }

    @Override
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        // Log.i(TAG, "onUnhandledKeyEvent: "+event);
        super.onUnhandledKeyEvent(view, event);
    }
}
