package com.humanize.android.authentication.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.google.gson.Gson;
import com.humanize.android.R;
import com.humanize.android.data.User;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText emailId;
    private EditText password;
    private EditText invitationCode;
    private Button signupButton;
    private TextView loginLink;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;

    private static final String TAG = "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initialize();
        configureListeners();
    }

    public void navigateToLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void initialize() {
        emailId = (EditText) findViewById(R.id.emailId);
        password = (EditText) findViewById(R.id.password);
        invitationCode = (EditText) findViewById(R.id.invitationCode);
        signupButton = (Button) findViewById(R.id.signup_button);
        signupButton.setEnabled(true);
        //loginLink = (TextView) findViewById(R.id.login_link);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
                signup();
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

            HttpUtil asyncHttpWrapper = HttpUtil.getInstance();
            asyncHttpWrapper.signup(Config.USER_SIGNUP_URL, new Gson().toJson(user), new SignupCallback());
        }
    }

    public void signupSuccess(String response) {
        progressDialog.dismiss();
        signupButton.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Please check your mail for verification code", Toast.LENGTH_LONG).show();
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
        public void onFailure(Request request, IOException e) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Network connection error", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
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
                        }
                    }
                });
            }
        }
    }
}