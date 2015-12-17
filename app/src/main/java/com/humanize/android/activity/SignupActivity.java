package com.humanize.android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.humanize.android.JsonParser;
import com.humanize.android.R;
import com.humanize.android.common.StringConstants;
import com.humanize.android.data.User;
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

public class SignupActivity extends AppCompatActivity {

    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.emailId) EditText emailId;
    @Bind(R.id.password) EditText password;
    @Bind(R.id.invitationCode) EditText invitationCode;
    @Bind(R.id.submitButton) Button submitButton;
    @Bind(R.id.invitationCodeLink) TextView invitationCodeLink;

    private ProgressDialog progressDialog;
    private static final String TAG = SignupActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    private void initialize() {
        progressDialog = new ProgressDialog(this);
        invitationCodeLink.setText(Html.fromHtml(StringConstants.INVITATION_CODE_STR));
    }

    private void configureListeners() {
        emailId.addTextChangedListener(new TextWatcher() {
            // after every change has been made to this editText, we would like to check validity
            public void afterTextChanged(Editable s) {
                if (emailId.getError() != null) {
                    emailId.setError(null);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        password.addTextChangedListener(new TextWatcher() {
            // after every change has been made to this editText, we would like to check validity
            public void afterTextChanged(Editable s) {
                if (password.getError() != null) {
                    password.setError(null);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        invitationCode.addTextChangedListener(new TextWatcher() {
            // after every change has been made to this editText, we would like to check validity
            public void afterTextChanged(Editable s) {
                if (invitationCode.getError() != null) {
                    invitationCode.setError(null);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });

        invitationCodeLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InvitationCodeActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validate() {
        String emailStr = emailId.getText().toString();
        String passwordStr = password.getText().toString();
        String invitationCodeStr = invitationCode.getText().toString();

        if (emailStr.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            emailId.setError(StringConstants.EMAIL_VALIDATION_ERROR_STR);
            Snackbar snackbar = Snackbar.make(coordinatorLayout, StringConstants.EMAIL_VALIDATION_ERROR_STR, Snackbar.LENGTH_SHORT);
            snackbar.show();
            return false;
        }

        if (passwordStr.isEmpty() || passwordStr.length() < Config.PASSWORD_MIN_LENGTH || password.length() > Config.PASSWORD_MAX_LENGTH) {
            password.setError(StringConstants.PASSWORD_VALIDATION_ERROR_STR);
            Snackbar snackbar = Snackbar.make(coordinatorLayout, StringConstants.PASSWORD_VALIDATION_ERROR_STR, Snackbar.LENGTH_SHORT);
            snackbar.show();
            return false;
        }

        if (invitationCodeStr.isEmpty()) {
            invitationCode.setError(StringConstants.INVITATION_CODE_VALIDATION_ERROR_STR);
            Snackbar snackbar = Snackbar.make(coordinatorLayout, StringConstants.INVITATION_CODE_VALIDATION_ERROR_STR, Snackbar.LENGTH_SHORT);
            snackbar.show();
            return false;
        }

        return true;
    }

    private void signup() {
        if (validate()) {
            submitButton.setEnabled(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(StringConstants.REGISTERING);
            progressDialog.show();

            User user = new User();
            user.setEmailId(emailId.getText().toString());
            user.setPassword(password.getText().toString());
            user.setInvitationCode(invitationCode.getText().toString());

            try {
                HttpUtil.getInstance().signup(Config.USER_SIGNUP_URL, new JsonParser().toJson(user), new SignupCallback());
            } catch (Exception exception) {
                Log.e(TAG, exception.toString());
            }
        }
    }

    private void signupSuccess(String response) {
        try {
            User user = new JsonParser().fromJson(response, User.class);

            if (user != null) {
                ApplicationState.setUser(user);
                SharedPreferencesService.getInstance().putBoolean(Config.IS_LOGGED_IN, true);
                SharedPreferencesService.getInstance().putString(Config.USER_DATA_JSON, response);

                Intent intent = new Intent(getApplicationContext(), SelectCategoriesActivity.class);
                startActivity(intent);
            } else {
                Log.e(TAG, "user is null");
            }
        } catch (Exception exception) {
            Log.e(TAG, exception.toString());
        }
    }

    private class SignupCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException exception) {
            Log.e(TAG, exception.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    submitButton.setEnabled(true);
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, StringConstants.NETWORK_CONNECTION_ERROR_STR, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    //Toast.makeText(getApplicationContext(), StringConstants.NETWORK_CONNECTION_ERROR_STR, Toast.LENGTH_LONG).show();
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
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, StringConstants.FAILURE_STR, Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        submitButton.setEnabled(true);
                        signupSuccess(responseStr);
                    }
                });
            }
        }
    }
}
