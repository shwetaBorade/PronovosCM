package com.pronovoscm.utils.dialogs;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.pronovoscm.R;
import com.pronovoscm.services.NetworkService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DocumentWebviewDialog extends DialogFragment {


    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.deleteImageView)
    ImageView deleteImageView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.appbarToolbar)
    Toolbar appbarToolbar;
    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.attachmetView)
    WebView webView;
    @BindView(R.id.webVieProgress)
    ProgressBar progressBar;
    boolean redirect = false;
    boolean loadingFinished = true;
    boolean isNetworkConnected;
    boolean isXLSFile = false;
    //    ProgressDialog progressDialog;
    Handler handler;
    private boolean isPageLoaded = false;
    private String attachmentPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Translucent_Dialog);
        EventBus.getDefault().register(this);
        isNetworkConnected = NetworkService.isNetworkAvailable(getContext());

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.attachment_item_webview, container, false);
        ButterKnife.bind(this, rootview);
        return rootview;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        attachmentPath = getArguments().getString("attachment_path");
        titleTextView.setText(getArguments().getString("title_text"));
        isXLSFile = getArguments().getBoolean("isXLS");
        backImageView.setOnClickListener(v -> {
            if (handler != null) handler.removeCallbacksAndMessages(null);
            dismiss();
        });
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.INVISIBLE);
        if (isNetworkConnected) {
            offlineTextView.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
           /* progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loading Data...");
            progressDialog.setCancelable(false);
            progressDialog.show();*/

            progressBar.setVisibility(View.VISIBLE);

            webView.requestFocus();
            webView.clearCache(true);
            String pdfURL = null;
            try {
                pdfURL = URLEncoder.encode(attachmentPath, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            final String url;
            if (isXLSFile) {
                url = "https://view.officeapps.live.com/op/view.aspx?src=" + pdfURL;
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.getSettings().setBuiltInZoomControls(true);
            } else {
                url = "http://docs.google.com/gview?embedded=true&url=" + pdfURL;
            }
//            webView.getSettings().setAppCacheEnabled(true); //TODO: Due to deprecated method
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setJavaScriptEnabled(true);
//            webView.getSettings().setBuiltInZoomControls(true);
//            webView.getSettings().setAllowFileAccess(true);
//            webView.getSettings().setBuiltInZoomControls(false);
//            webView.getSettings().setSupportZoom(true);


            webView.loadUrl(url);


            webView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
                    if (!loadingFinished) {
                        redirect = true;
                    }
                    loadingFinished = false;
                    view.loadUrl(urlNewString);
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                    loadingFinished = false;
                    //SHOW LOADING IF IT ISNT ALREADY VISIBLE
//                    progressDialog.show();
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if (!redirect) {
                        loadingFinished = true;
                    }
                    webView.loadUrl("javascript:(function() { " +
                            "document.querySelector('[role=\"toolbar\"]').remove();})()");
                    webView.loadUrl("javascript:(function() { " +
                            "document.getElementsByClassName('drive-viewer-toolstrip')[0].style.visibility='hidden'; })()");

                    if (loadingFinished && !redirect) {
                        //HIDE LOADING IT HAS FINISHED+
//                        startTime = System.currentTimeMillis();
//                        progressDialog.dismiss();
                        progressBar.setVisibility(View.INVISIBLE);
                    } else {
                        redirect = false;
                    }
                }

                @Override
                public void onPageCommitVisible(WebView view, String url) {
//                    Log.e("WebViewNews","onPageFinished Commit: ");
                    isPageLoaded = true;
//                    startTime = System.currentTimeMillis();
//                    relativeLayoutBottom.setVisibility(View.VISIBLE);
//                    progressDialog.dismiss();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });

            handler = new Handler();
//            String finalUrl = url;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isPageLoaded) {
                        progressBar.setVisibility(View.VISIBLE);
                        webView.loadUrl(url);//loadUrl(doc);
                    }
                    handler.postDelayed(this, 3000);
                }
            }, 2000);

        } else {
            offlineTextView.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);

        }


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
        } else {
            offlineTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        EventBus.getDefault().unregister(this);
    }
}

