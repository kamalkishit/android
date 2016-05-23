package com.humanize.android.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.humanize.android.R;
import com.humanize.android.config.Config;
import com.humanize.android.utils.AppUtils;

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
        overridePendingTransition(R.anim.slide_right_to_left, 0);

        ButterKnife.bind(this);

        initialize();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();
            overridePendingTransition(0, R.anim.slide_left_to_right);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_left_to_right);
    }

    private void initialize() {
        progressBar.setProgress(0);

        toolbar.setCollapsible(true);
        toolbarText.setText(getIntent().getStringExtra(Config.SOURCE));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadUrl();
    }

    private void loadUrl() {
        if (AppUtils.isNetworkAvailable()) {
            Intent intent = getIntent();
            webView.setWebChromeClient(new MyWebChromeClient());
            webView.setWebViewClient(new MyWebViewClient());
            webView.getSettings().setJavaScriptEnabled(true);
            //webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
            webView.loadUrl(intent.getStringExtra(Config.URL));
        } else {
            NetworkConnectionFailureFragment networkConnectionFailureFragment = new NetworkConnectionFailureFragment();
            networkConnectionFailureFragment.show(WebBrowserActivity.this.getFragmentManager(), "");
        }
    }

    private void setValue(int progress) {
        this.progressBar.setProgress(progress);
        if (progress == 100) {
            this.progressBar.setVisibility(View.GONE);
        }
    }

    class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            WebBrowserActivity.this.setValue(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }

    class MyWebViewClient extends WebViewClient {

        public boolean shuldOverrideKeyEvent (WebView view, KeyEvent event) {
            // Do something with the event here
            return true;
        }

        public boolean shouldOverrideUrlLoading (WebView view, String url) {
            return false;
        }
    }

    public static class NetworkConnectionFailureFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LinearLayout linearLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_network_connection_failure, null);
            Button cancelButton = (Button) linearLayout.findViewById(R.id.cancelButton);
            Button retryButton = (Button) linearLayout.findViewById(R.id.retryButton);

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismissFragment();
                }
            });

            retryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    retry();
                }
            });

            builder.setView(linearLayout);
            return builder.create();
        }

        private void dismissFragment() {
            this.dismiss();
            (getActivity()).onBackPressed();
        }

        private void retry() {
            this.dismiss();
            ((WebBrowserActivity)getActivity()).loadUrl();
        }
    }
}
