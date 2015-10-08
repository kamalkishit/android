package com.humanize.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.humanize.android.R;
import com.humanize.android.util.Config;

public class WebBrowserActivity extends AppCompatActivity {
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_browser);

        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setProgress(0);

        WebView browser = (WebView) findViewById(R.id.webview);
        Intent intent = getIntent();
        browser.setWebChromeClient(new MyWebChromeClient());
        browser.setWebViewClient(new MyWebViewClient());
        browser.getSettings().setJavaScriptEnabled(true);
        browser.loadUrl(intent.getStringExtra(Config.CONTENT_URL));
        //browser.loadUrl(CardActivity.RecyclerViewAdapter.contents.getContents().get(CardActivity.RecyclerViewAdapter.currentItem).getContentURL());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_submit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
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
        this.progress.setProgress(progress);
    }
}
