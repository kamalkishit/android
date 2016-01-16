package com.humanize.android.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.humanize.android.ApiImpl;
import com.humanize.android.R;
import com.humanize.android.common.StringConstants;
import com.humanize.android.data.SuggestArticle;
import com.humanize.android.fragment.SuggestArticleSuccessFragment;
import com.humanize.android.helper.ActivityLauncher;
import com.humanize.android.util.Api;
import com.humanize.android.util.Config;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SuggestArticleActivity extends AppCompatActivity {

    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarText) TextView toolbarText;
    @Bind(R.id.contentUrl) EditText contentURL;
    @Bind(R.id.submitButton) Button submitButton;

    private ProgressDialog progressDialog;
    private Api api;

    private static String TAG = SuggestArticleActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_article);

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
        api = new ApiImpl();
        progressDialog = new ProgressDialog(this);
        toolbar.setCollapsible(true);
        toolbarText.setText(StringConstants.SUGGEST_ARTICLE);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void configureListeners() {
        contentURL.addTextChangedListener(new TextWatcher() {
            // after every change has been made to this editText, we would like to check validity
            public void afterTextChanged(Editable s) {
                if (contentURL.getError() != null) {
                    contentURL.setError(null);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        submitButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                submit();

            }
        });
    }

    private boolean validate() {
        if (contentURL == null || contentURL.getText().toString().isEmpty() || !isValidUrl(contentURL.getText().toString())) {
            contentURL.setError(StringConstants.URL_VALIDATION_ERROR_STR);
            Snackbar.make(coordinatorLayout, StringConstants.URL_VALIDATION_ERROR_STR, Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void submit() {
        if (validate()) {
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(StringConstants.SUBMITTING);
            progressDialog.show();
            SuggestArticle suggestArticle = new SuggestArticle();
            suggestArticle.setArticleUrl(contentURL.getText().toString());
            api.suggestArticle(suggestArticle, new SuggestArticleCallback());
        }
    }

    public void returnToMainActivity() {
        super.onBackPressed();
    }

    private boolean isValidUrl(String url) {
        if (url == null || url.length() == 0) {
            return false;
        }

        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url);
        return m.matches();
    }

    private class SuggestArticleCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException exception) {
            Log.e(TAG, exception.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    Snackbar.make(coordinatorLayout, StringConstants.NETWORK_CONNECTION_ERROR_STR, Snackbar.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Snackbar.make(coordinatorLayout, StringConstants.FAILURE_STR, Snackbar.LENGTH_LONG).show();
                    }
                });
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        SuggestArticleSuccessFragment suggestArticleSuccessFragment = new SuggestArticleSuccessFragment();
                        suggestArticleSuccessFragment.show(SuggestArticleActivity.this.getFragmentManager(), "");

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                ActivityLauncher activityLauncher = new ActivityLauncher();
                                returnToMainActivity();
                            }
                        }, 3000);
                    }
                });
            }
        }
    }
}
