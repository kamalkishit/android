package com.humanize.android.authentication.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;

import com.google.gson.Gson;
import com.humanize.android.JsonParser;
import com.humanize.android.R;
import com.humanize.android.UserTempService;
import com.humanize.android.common.StringConstants;
import com.humanize.android.data.User;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {

    @Bind(R.id.emailId) EditText emailId;
    @Bind(R.id.password) EditText password;
    @Bind(R.id.invitationCode) EditText invitationCode;
    @Bind(R.id.signupButton) Button signupButton;
    @Bind(R.id.toolbar) Toolbar toolbar;

    private ProgressDialog progressDialog;

    private static final String TAG = "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    public void navigateToLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void initialize() {
        //loginLink = (TextView) findViewById(R.id.login_link);

        toolbar.setCollapsible(true);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setIndeterminate(true);
    }

    private void configureListeners() {
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserTempService userTempService = new UserTempService();
                userTempService.signup(SignupActivity.this, view, new User());

                //signup();
            }
        });

        /*loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });*/
    }

    private void signup() {
        if (validate()) {
            signupButton.setEnabled(false);
            progressDialog.setMessage("Authenticating...");
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

    public void signupSuccess(String response) {
        progressDialog.dismiss();
        signupButton.setEnabled(true);
        navigateToLoginActivity();
    }

    private boolean validate() {
        String emailStr = emailId.getText().toString();
        String passwordStr = password.getText().toString();
        String invitationCodeStr = invitationCode.getText().toString();

        if (emailStr.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            emailId.setError("enter a valid email address");
            return false;
        }

        if (passwordStr.isEmpty() || passwordStr.length() < Config.PASSWORD_MIN_LENGTH || passwordStr.length() > Config.PASSWORD_MAX_LENGTH) {
            password.setError("between 4 and 10 alphanumeric characters");
            return false;
        }

        if (invitationCodeStr.isEmpty()) {
            invitationCode.setError("invitation code is null");
            return false;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            supportFinishAfterTransition();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class SignupCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException exception) {
            Log.e(TAG, exception.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
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
                    }
                });
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String responseStr = response.body().string().toString();
                            signupSuccess(responseStr);
                        } catch (IOException exception) {
                            Log.e(TAG, exception.toString());
                        } finally {
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        }
    }
}