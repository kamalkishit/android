package com.humanize.android.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.humanize.android.R;
import com.humanize.android.config.Config;
import com.humanize.android.config.Constants;
import com.humanize.android.config.StringConstants;
import com.humanize.android.data.LoginObj;
import com.humanize.android.fragment.ContactUsSuccessFragment;
import com.humanize.android.helper.ActivityLauncher;
import com.humanize.android.service.ApiService;
import com.humanize.android.service.ApiServiceImpl;
import com.humanize.android.service.LogService;
import com.humanize.android.service.LogServiceImpl;
import com.humanize.android.service.SharedPreferencesService;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarText) TextView toolbarText;
    @Bind(R.id.emailId) EditText emailId;
    @Bind(R.id.password) EditText password;
    @Bind(R.id.forgotPasswordLink) TextView forgotPasswordLink;
    @Bind(R.id.signupLink) TextView signupLink;
    @Bind(R.id.submitButton) Button submitButton;

    private ProgressDialog progressDialog;
    private ApiService apiService;
    private ActivityLauncher activityLauncher;

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final LogService logService = new LogServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.slide_right_to_left, 0);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
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
        progressDialog = new ProgressDialog(this);
        apiService = new ApiServiceImpl();
        activityLauncher = new ActivityLauncher();
        toolbarText.setText(StringConstants.LOGIN);
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
                login();
            }
        });

        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityLauncher.startForgotPasswordActivity();
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityLauncher.startSignupActivity();
            }
        });
    }

    private void login() {
        if (validate()) {
            submitButton.setEnabled(false);

            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(StringConstants.AUTHENTICATING);
            progressDialog.show();

            LoginObj loginObj = new LoginObj();
            loginObj.setEmailId(emailId.getText().toString());
            loginObj.setPassword(password.getText().toString());
        }
    }

    private boolean validate() {
        String emailStr = emailId.getText().toString();
        String passwordStr = password.getText().toString();

        if (emailStr.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            emailId.setError(StringConstants.EMAIL_VALIDATION_ERROR_STR);
            Snackbar.make(coordinatorLayout, StringConstants.EMAIL_VALIDATION_ERROR_STR, Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (passwordStr.isEmpty() || passwordStr.length() < Config.PASSWORD_MIN_LENGTH || password.length() > Config.PASSWORD_MAX_LENGTH) {
            password.setError(StringConstants.INVALID_PASSWORD_LENGTH);
            Snackbar.make(coordinatorLayout, StringConstants.INVALID_PASSWORD_LENGTH, Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void loginSuccess(String response) {
        //SharedPreferencesService.getInstance().putString(Config.TOKEN, response);
        //getUserdata();
    }

    private class LoginCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException exception) {
            logService.e(TAG, exception.getMessage());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    Snackbar.make(coordinatorLayout, StringConstants.NETWORK_CONNECTION_ERROR_STR, Snackbar.LENGTH_LONG).show();
                    submitButton.setEnabled(true);
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
                        Snackbar.make(coordinatorLayout, StringConstants.LOGIN_FAILURE_STR, Snackbar.LENGTH_LONG).show();
                        submitButton.setEnabled(true);
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        loginSuccess(responseStr);
                    }
                });
            }
        }
    }
}
