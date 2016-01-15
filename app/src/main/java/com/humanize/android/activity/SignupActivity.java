package com.humanize.android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.humanize.android.JsonParser;
import com.humanize.android.R;
import com.humanize.android.common.StringConstants;
import com.humanize.android.data.SignupUser;
import com.humanize.android.data.User;
import com.humanize.android.helper.ActivityLauncher;
import com.humanize.android.util.JsonParserImpl;
import com.humanize.android.service.SharedPreferencesService;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity {

    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.emailId) TextView emailId;
    @Bind(R.id.password) EditText password;
    @Bind(R.id.submitButton) Button submitButton;
    //@Bind(R.id.invitationCodeLink) TextView invitationCodeLink;

    private ActivityLauncher activityLauncher;
    private JsonParser jsonParser;
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
        activityLauncher = new ActivityLauncher();
        jsonParser = new JsonParserImpl();
        Uri uri = getIntent().getData();
        if (uri != null) {
            String path = uri.getPath();
            emailId.setText(uri.getQueryParameter("emailId"));
            password.requestFocus();
        }

        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());

        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        progressDialog = new ProgressDialog(this);
        //invitationCodeLink.setText(Html.fromHtml(StringConstants.INVITATION_CODE_STR));
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

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });

        /*invitationCodeLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InvitationCodeActivity.class);
                startActivity(intent);
            }
        });*/
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

    private void signup() {
        if (validate()) {
            submitButton.setEnabled(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(StringConstants.REGISTERING);
            progressDialog.show();

            if (getIntent().getData() != null) {
                SignupUser signupUser = new SignupUser();
                signupUser.setEmailId(emailId.getText().toString());
                signupUser.setInvitationCode(getIntent().getData().getQueryParameter("invitationCode"));
                signupUser.setPassword(password.getText().toString());

                try {
                    HttpUtil.getInstance().signup(Config.USER_SIGNUP_URL, jsonParser.toJson(signupUser), new SignupCallback());
                } catch (Exception exception) {
                    Log.e(TAG, exception.toString());
                }
            } else {
                // TBD:
            }

        }
    }

    private void signupSuccess(String response) {
        try {
            User user = jsonParser.fromJson(response, User.class);
            List<String> categories = new ArrayList<>();

            categories.add("Achievers");
            categories.add("Beautiful");
            categories.add("Education");
            categories.add("Empowerment");
            categories.add("Environment");
            categories.add("Governance");
            categories.add("Health");
            categories.add("Humanity");
            categories.add("Real Heroes");
            categories.add("Science and Tech");
            categories.add("Law and Justice");
            categories.add("Sports");

            user.setCategories(categories);

            if (user != null) {
                ApplicationState.setUser(user);
                SharedPreferencesService.getInstance().putBoolean(Config.IS_LOGGED_IN, true);
                SharedPreferencesService.getInstance().putString(Config.JSON_USER_DATA, response);
                activityLauncher.startCardActivity();
            } else {
            }
        } catch (Exception exception) {
            Log.e(TAG, exception.toString());
        }
    }

    private class SignupCallback implements Callback {

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
                        signupSuccess(responseStr);
                    }
                });
            }
        }
    }
}
