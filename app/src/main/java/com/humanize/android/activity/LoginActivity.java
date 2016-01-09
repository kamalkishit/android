package com.humanize.android.activity;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.humanize.android.JsonParser;
import com.humanize.android.R;
import com.humanize.android.common.Constants;
import com.humanize.android.common.StringConstants;
import com.humanize.android.data.LoginUser;
import com.humanize.android.data.User;
import com.humanize.android.helper.ActivityLauncher;
import com.humanize.android.util.JsonParserImpl;
import com.humanize.android.service.SharedPreferencesService;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.w3c.dom.Text;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.dummyTop) TextView dummyTop;
    @Bind(R.id.title) TextView title;
    @Bind(R.id.line) View line;
    @Bind(R.id.emailId) EditText emailId;
    @Bind(R.id.password) EditText password;
    @Bind(R.id.submitButton) Button submitButton;
    @Bind(R.id.forgotPasswordLink) TextView forgotPasswordLink;
    @Bind(R.id.skipLoginLink) TextView skipLoginLink;
    //@Bind(R.id.registerLink) TextView registerLink;

    private ProgressDialog progressDialog;
    private ActivityLauncher activityLauncher;
    private JsonParser jsonParser;
    private boolean doubleBackToExitPressedOnce;


    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    /*@Override
    public void onBackPressed() {
        dummyTop.setVisibility(View.VISIBLE);
        super.onBackPressed();
    }*/

    private void initialize() {
        progressDialog = new ProgressDialog(this);
        activityLauncher = new ActivityLauncher();
        jsonParser = new JsonParserImpl();

        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());
        doubleBackToExitPressedOnce = false;
        forgotPasswordLink.setText(Html.fromHtml(StringConstants.FORGOT_PASSWORD_STR));
        //registerLink.setText(Html.fromHtml(StringConstants.REGISTER_STR));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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

        emailId.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dummyTop.setVisibility(View.GONE);

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(ApplicationState.getAppContext().INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    //inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    inputMethodManager.showSoftInput(emailId, 0);
                }

                return true;
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            // after every change has been made to this editText, we would like to check validity
            public void afterTextChanged(Editable s) {
                if (password.getError() != null) {
                    password.setError(null);
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
                login();
            }
        });

        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityLauncher.startForgotPasswordActivity();
            }
        });

        skipLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityLauncher.startCardActivity();
            }
        });

        /*registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityLauncher.startSignupActivity();
            }
        });*/
    }

    private void login() {
        if (validate()) {
            submitButton.setEnabled(false);

            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(StringConstants.AUTHENTICATING);
            progressDialog.show();

            LoginUser loginUser = new LoginUser();
            loginUser.setEmailId(emailId.getText().toString());
            loginUser.setPassword(password.getText().toString());

            try {
                HttpUtil.getInstance().login(Config.USER_LOGIN_URL, jsonParser.toJson(loginUser), new LoginCallback());
            } catch (Exception exception) {

            }
        }
    }

    public boolean validate() {
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

    private void getUserdata() {
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getUserdata(Config.USER_DATA_URL, new UserDataCallback());
    }

    private void loginSuccess(String response) {
        SharedPreferencesService.getInstance().putString(Config.TOKEN, response);
        getUserdata();
    }

    private class LoginCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException exception) {
            Log.e(TAG, exception.toString());
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
        public void onResponse(final Response response) throws IOException {
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

    private class UserDataCallback implements Callback {
        @Override
        public void onFailure(Request request, IOException e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(coordinatorLayout, StringConstants.NETWORK_CONNECTION_ERROR_STR, Snackbar.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(coordinatorLayout, StringConstants.FAILURE_STR, Snackbar.LENGTH_LONG).show();
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SharedPreferencesService.getInstance().putBoolean(Config.IS_LOGGED_IN, true);
                            SharedPreferencesService.getInstance().putString(Config.JSON_USER_DATA, responseStr);
                            ApplicationState.setUser(jsonParser.fromJson(responseStr, User.class));
                            activityLauncher.startCardActivity();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                });
            }
        }
    }
}
