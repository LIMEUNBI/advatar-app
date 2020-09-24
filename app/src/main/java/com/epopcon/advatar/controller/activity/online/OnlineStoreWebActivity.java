package com.epopcon.advatar.controller.activity.online;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.epopcon.advatar.R;
import com.epopcon.advatar.controller.activity.BaseActivity;

public class OnlineStoreWebActivity extends BaseActivity {

    private final String TAG = OnlineStoreWebActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_store_web);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarMessage = (TextView) findViewById(R.id.toolbar_message);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String title = intent.getStringExtra("title");

        toolbarMessage.setText(title);

        WebView webView = (WebView) findViewById(R.id.store_website);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE); // 웹뷰 로딩 향상
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onJsAlert(WebView view, String url,
                                     String message, final android.webkit.JsResult result) {

                new AlertDialog.Builder(OnlineStoreWebActivity.this)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        result.confirm();
                                    }
                                }).setCancelable(false).create().show();

                return true;
            }

            public boolean onJsConfirm(WebView view, String url,
                                       String message, final android.webkit.JsResult result) {
                new AlertDialog.Builder(OnlineStoreWebActivity.this)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        result.confirm();
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel,
                                new AlertDialog.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        result.cancel();
                                    }
                                }).setCancelable(false).create().show();
                return true;
            }
        });

        Log.d(TAG, "url -> " + url);

        webView.loadUrl(url);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
