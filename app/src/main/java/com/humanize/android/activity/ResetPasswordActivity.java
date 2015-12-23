package com.humanize.android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.humanize.android.JsonParser;
import com.humanize.android.R;
import com.humanize.android.common.StringConstants;
import com.humanize.android.data.User;
import com.humanize.android.helper.ActivityLauncher;
import com.humanize.android.service.SharedPreferencesService;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ResetPasswordActivity extends AppCompatActivity {

    private static final String TAG = ResetPasswordActivity.class.getSimpleName();
    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.emailId) EditText emailId;
    @Bind(R.id.tempPassword) EditText tempPassword;
    @Bind(R.id.newPassword) EditText newPassword;
    @Bind(R.id.submitButton) Button submitButton;

    ActivityLauncher activityLauncher;
    String emailIdStr;
    String tempPasswordStr;
    String newPasswordStr;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    private void initialize() {
        tempPassword.setTypeface(Typeface.DEFAULT);
        tempPassword.setTransformationMethod(new PasswordTransformationMethod());
        newPassword.setTypeface(Typeface.DEFAULT);
        newPassword.setTransformationMethod(new PasswordTransformationMethod());
        activityLauncher = new ActivityLauncher();
    }

    private void configureListeners() {
        emailId.addTextChangedListener(new TextWatcher() {
            // after every change has been made to this editText, we would like to check validity
            public void afterTextChanged(Editable s) {
                if (emailId.getError() != null) {
                    emailId.setError(null);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        tempPassword.addTextChangedListener(new TextWatcher() {
            // after every change has been made to this editText, we would like to check validity
            public void afterTextChanged(Editable s) {
                if (tempPassword.getError() != null) {
                    tempPassword.setError(null);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        newPassword.addTextChangedListener(new TextWatcher() {
            // after every change has been made to this editText, we would like to check validity
            public void afterTextChanged(Editable s) {
                if (newPassword.getError() != null) {
                    newPassword.setError(null);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
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

    private void resetPassword() {
        if (validate()) {
            submitButton.setEnabled(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(StringConstants.RESETTING_PASSWORD);
            progressDialog.show();

            try {
                HttpUtil.getInstance().resetPassword(Config.USER_RESET_PASSWORD_URL, emailIdStr, tempPasswordStr, newPasswordStr, new ResetPasswordCallback());
            } catch (Exception exception) {
                Log.e(TAG, exception.toString());
            }
        }
    }

    private boolean validate() {
        emailIdStr = emailId.getText().toString();
        tempPasswordStr = tempPassword.getText().toString();
        newPasswordStr = newPassword.getText().toString();

        if (emailIdStr.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailIdStr).matches()) {
            emailId.setError(StringConstants.EMAIL_VALIDATION_ERROR_STR);
            Snackbar snackbar = Snackbar.make(coordinatorLayout, StringConstants.EMAIL_VALIDATION_ERROR_STR, Snackbar.LENGTH_SHORT);
            snackbar.show();
            return false;
        }

        if (tempPasswordStr.isEmpty() || tempPasswordStr.length() < Config.PASSWORD_MIN_LENGTH || tempPassword.length() > Config.PASSWORD_MAX_LENGTH) {
            tempPassword.setError(StringConstants.PASSWORD_VALIDATION_ERROR_STR);
            Snackbar snackbar = Snackbar.make(coordinatorLayout, StringConstants.PASSWORD_VALIDATION_ERROR_STR, Snackbar.LENGTH_SHORT);
            snackbar.show();
            return false;
        }

        if (newPasswordStr.isEmpty() || newPasswordStr.length() < Config.PASSWORD_MIN_LENGTH || newPassword.length() > Config.PASSWORD_MAX_LENGTH) {
            newPassword.setError(StringConstants.PASSWORD_VALIDATION_ERROR_STR);
            Snackbar snackbar = Snackbar.make(coordinatorLayout, StringConstants.PASSWORD_VALIDATION_ERROR_STR, Snackbar.LENGTH_SHORT);
            snackbar.show();
            return false;
        }

        return true;
    }

    private void resetPasswordSuccess(String response) {
        try {
            User user = new JsonParser().fromJson(response, User.class);

            if (user != null) {
                ApplicationState.setUser(user);
                SharedPreferencesService.getInstance().putBoolean(Config.IS_LOGGED_IN, true);
                SharedPreferencesService.getInstance().putString(Config.USER_DATA_JSON, response);
                if (!user.getIsConfigured()) {
                    activityLauncher.startCardActivity(coordinatorLayout);
                } else {
                    Intent intent = new Intent(getApplicationContext(), SelectCategoriesActivity.class);
                    startActivity(intent);
                }
            } else {
                Log.e(TAG, "user is null");
            }
        } catch (Exception exception) {
            Log.e(TAG, exception.toString());
        }
    }

    private class ResetPasswordCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException exception) {
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
        public void onResponse(final Response response) throws IOException {
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
                        resetPasswordSuccess(responseStr);
                    }
                });
            }
        }
    }
}
