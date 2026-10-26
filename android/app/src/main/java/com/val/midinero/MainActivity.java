package com.val.midinero;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private WebView webView;
    private String pendingDeepLink;

    @SuppressLint("SetJavaScriptEnabled")
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        webView = new WebView(this);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                dispatchDeepLink();
            }
        });
        pendingDeepLink = getIntent()!=null && getIntent().getData()!=null ? getIntent().getData().toString() : null;
        webView.loadUrl("file:///android_asset/index.html");
        setContentView(webView);
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if(intent!=null && intent.getData()!=null) {
            pendingDeepLink = intent.getData().toString();
            dispatchDeepLink();
        }
    }

    private void dispatchDeepLink() {
        if(pendingDeepLink==null || webView==null) return;
        String js = "window.handleDeepLink && window.handleDeepLink(" + JSONObject.quote(pendingDeepLink) + ")";
        webView.evaluateJavascript(js, null);
        pendingDeepLink = null;
    }

    @Override public void onBackPressed() {
        if(webView.canGoBack()) webView.goBack(); else super.onBackPressed();
    }
}
