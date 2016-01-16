package com.humanize.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.humanize.android.R;
import com.humanize.android.common.StringConstants;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WebBrowserActivity extends AppCompatActivity {

    @Bind(R.id.progressBar) ProgressBar progressBar;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarText) TextView toolbarText;
    @Bind(R.id.webView) WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_browser);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initialize() {
        progressBar.setProgress(0);

        toolbar.setCollapsible(true);
        toolbarText.setText(getIntent().getStringExtra(Config.SOURCE));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(intent.getStringExtra(Config.URL));
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
        System.out.println(progress);
        this.progressBar.setProgress(progress);
        if (progress == 100) {
            this.progressBar.setVisibility(View.GONE);
        }
    }
}
