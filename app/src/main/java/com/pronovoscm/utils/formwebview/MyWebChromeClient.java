package com.pronovoscm.utils.formwebview;

import android.net.Uri;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class MyWebChromeClient extends WebChromeClient {
  //  private static String TAG = "MyWebChromeClient";
    //Handle javascript alerts:
    @Override
    public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
    {
        Log.d("alert", message);
        result.confirm();
        return true;
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
//        return super.onConsoleMessage(consoleMessage);
        //Log.d("MyApplication", consoleMessage.message() + " -- From line " + consoleMessage.lineNumber() + " of " + consoleMessage.sourceId());
        return true;

    }

    @Override
    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
        Log.d("MyApplication", url);
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
      //  Log.d("MyApplication", filePathCallback.toString());
        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
    }



}
