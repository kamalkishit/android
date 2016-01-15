package com.humanize.android.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.humanize.android.R;
import com.humanize.android.common.StringConstants;
import com.humanize.android.helper.ActivityLauncher;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.emailId) EditText emailId;
    @Bind(R.id.submitButton) Button submitButton;

    private ProgressDialog progressDialog;
    private ActivityLauncher activityLauncher;

    private static final String TAG = ForgotPasswordActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    private void initialize() {
        progressDialog = new ProgressDialog(this);
        activityLauncher = new ActivityLauncher();
    }

    private void configureListeners() {
        emailId.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (emailId.getError() != null) {
                    emailId.setError(null);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword();
            }
        });

        submitButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });
    }

    private void forgotPassword() {
        if (validate()) {
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Sending mail...");
            progressDialog.show();

            try {
                HttpUtil.getInstance().forgotPassword(Config.USER_FORGOT_PASSWORD_URL, emailId.getText().toString(), new ForgotPasswordCallback());
            } catch (Exception exception) {
                Log.e(TAG, exception.toString());
            }
        }
    }

    public boolean validate() {
        String emailStr = emailId.getText().toString();

        if (emailStr.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            emailId.setError(StringConstants.EMAIL_VALIDATION_ERROR_STR);
            Snackbar.make(coordinatorLayout, StringConstants.EMAIL_VALIDATION_ERROR_STR, Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private class ForgotPasswordCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException exception) {
            Log.e(TAG, exception.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    submitButton.setEnabled(true);
                    Snackbar.make(coordinatorLayout, StringConstants.NETWORK_CONNECTION_ERROR_STR, Snackbar.LENGTH_SHORT).show();
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
                        submitButton.setEnabled(true);
                        Snackbar.make(coordinatorLayout, StringConstants.FAILURE_STR, Snackbar.LENGTH_SHORT).show();
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        submitButton.setEnabled(true);
                        Snackbar.make(coordinatorLayout, "Check your mail to reset password", Snackbar.LENGTH_INDEFINITE).show();
                    }
                });
            }
        }
    }
}
