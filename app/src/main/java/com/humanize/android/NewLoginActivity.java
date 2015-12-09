package com.humanize.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.humanize.android.common.Constants;
import com.humanize.android.common.StringConstants;
import com.humanize.android.data.User;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewLoginActivity extends AppCompatActivity {

    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.emailId) EditText emailId;
    @Bind(R.id.password) EditText password;
    @Bind(R.id.submitButton) Button submitButton;
    @Bind(R.id.forgotPasswordLink) TextView forgotPasswordLink;
    @Bind(R.id.registerLink) TextView registerLink;

    private boolean doubleBackToExitPressedOnce;

    private static final String TAG = "NewLoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_login);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Snackbar snackbar = Snackbar.make(coordinatorLayout, StringConstants.DOUBLE_BACK_EXIT_STR, Snackbar.LENGTH_SHORT);
        snackbar.show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, Constants.DOUBLE_EXIT_DELAY_TIME);
    }

    private void initialize() {
        doubleBackToExitPressedOnce = false;
        forgotPasswordLink.setText(Html.fromHtml(StringConstants.FORGOT_PASSWORD_STR));
        registerLink.setText(Html.fromHtml(StringConstants.REGISTER_STR));
    }

    private void configureListeners() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(intent);
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewRegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login() {
        if (validate()) {
            submitButton.setEnabled(false);

            /*progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();*/

            User user = new User();
            user.setEmailId(emailId.getText().toString());
            user.setPassword(password.getText().toString());

            try {
                HttpUtil.getInstance().login(Config.USER_LOGIN_URL, new JsonParser().toJson(user), new LoginCallback());
            } catch (Exception exception) {
                Log.e(TAG, exception.toString());
            }
        }
    }

    public boolean validate() {
        String emailStr = emailId.getText().toString();
        String passwordStr = password.getText().toString();

        if (emailStr.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            emailId.setError(StringConstants.EMAIL_VALIDATION_ERROR_STR);
            Snackbar snackbar = Snackbar.make(coordinatorLayout, StringConstants.EMAIL_VALIDATION_ERROR_STR, Snackbar.LENGTH_SHORT);
            return false;
        }

        if (passwordStr.isEmpty() || passwordStr.length() < Config.PASSWORD_MIN_LENGTH || password.length() > Config.PASSWORD_MAX_LENGTH) {
            password.setError(StringConstants.PASSWORD_VALIDATION_ERROR_STR);
            Snackbar snackbar = Snackbar.make(coordinatorLayout, StringConstants.PASSWORD_VALIDATION_ERROR_STR, Snackbar.LENGTH_SHORT);
            return false;
        }

        return true;
    }

    private class LoginCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException exception) {
            Log.e(TAG, exception.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), StringConstants.NETWORK_CONNECTION_ERROR_STR, Toast.LENGTH_SHORT).show();
                    submitButton.setEnabled(true);
                }
            });
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), StringConstants.FAILURE_STR, Toast.LENGTH_SHORT).show();
                        submitButton.setEnabled(true);
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), StringConstants.SUCCESS_STR, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
