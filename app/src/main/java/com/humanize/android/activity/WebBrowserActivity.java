package com.humanize.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.humanize.android.R;
import com.humanize.android.utils.Config;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WebBrowserActivity extends AppCompatActivity {

    @Bind(R.id.progressBar) ProgressBar progressBar;
    @Bind(R.id.webView) WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_browser);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    private void initialize() {
        progressBar.setProgress(0);

        Intent intent = getIntent();
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(intent.getStringExtra(Config.CONTENT_URL));
    }

    private void configureListeners() {

    }

    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            WebBrowserActivity.this.setValue(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }

    public class MyWebViewClient extends WebViewClient {
        public boolean shuldOverrideKeyEvent (WebView view, KeyEvent event) {
            // Do something with the event here
            return true;
        }

        public boolean shouldOverrideUrlLoading (WebView view, String url) {
            return true;
        }
    }

    public void setValue(int progress) {
        this.progressBar.setProgress(progress);
    }
}
