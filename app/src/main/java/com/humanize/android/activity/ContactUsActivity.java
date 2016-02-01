package com.humanize.android.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.humanize.android.service.ApiServiceImpl;
import com.humanize.android.R;
import com.humanize.android.config.Constants;
import com.humanize.android.config.StringConstants;
import com.humanize.android.data.ContactUs;
import com.humanize.android.fragment.ContactUsSuccessFragment;
import com.humanize.android.service.ApiService;
import com.humanize.android.service.LogService;
import com.humanize.android.service.LogServiceImpl;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ContactUsActivity extends AppCompatActivity {

    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarText) TextView toolbarText;
    @Bind(R.id.subject) EditText subject;
    @Bind(R.id.body) EditText body;
    @Bind(R.id.submitButton) Button submitButton;

    private ProgressDialog progressDialog;
    private ApiService apiService;

    private static final String TAG = ContactUsActivity.class.getSimpleName();
    private static final LogService logService = new LogServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        overridePendingTransition(R.anim.slide_right_to_left, 0);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    private void initialize() {
        progressDialog = new ProgressDialog(this);
        apiService = new ApiServiceImpl();
        toolbarText.setText(StringConstants.CONTACT_US);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void configureListeners() {
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

    private void submit() {
        if (validate()) {
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(StringConstants.SUBMITTING);
            progressDialog.show();
            ContactUs contactUs = new ContactUs();
            contactUs.setSubject(subject.getText().toString());
            contactUs.setBody(body.getText().toString());

            apiService.contactUs(contactUs, new ContactUsCallback());
        }
    }

    private boolean validate() {
        if (subject == null || subject.getText().toString().isEmpty()) {
            subject.setError(StringConstants.SUBJECT_VALIDATION_ERROR_STR);
            Snackbar.make(coordinatorLayout, StringConstants.SUBJECT_VALIDATION_ERROR_STR, Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (body == null || body.getText().toString().isEmpty()) {
            body.setError(StringConstants.BODY_VALIDATION_ERROR_STR);
            Snackbar.make(coordinatorLayout, StringConstants.BODY_VALIDATION_ERROR_STR, Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void returnToMainActivity() {
        super.onBackPressed();
    }

    private class ContactUsCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException exception) {
            logService.e(TAG, exception.getMessage());
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
                        ContactUsSuccessFragment contactUsSuccessFragment = new ContactUsSuccessFragment();
                        contactUsSuccessFragment.show(ContactUsActivity.this.getFragmentManager(), "");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                returnToMainActivity();
                            }
                        }, Constants.ACTIVITY_START_DELAY_TIME);
                    }
                });
            }
        }
    }
}
