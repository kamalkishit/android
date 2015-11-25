package com.humanize.android.authentication.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humanize.android.R;
import com.humanize.android.activity.AppLauncherActivity;
import com.humanize.android.data.User;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.humanize.android.util.SharedPreferencesStorage;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText emailId;
    private EditText password;
    private Button loginButton;
    private TextView signupLink;
    private Toolbar toolbar;
    SharedPreferencesStorage sharedPreferencesStorage;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize();
        configureListeners();
    }

    private void initialize() {
        emailId = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.login_button);
        //signupLink = (TextView) findViewById(R.id.signup_link);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setCollapsible(true);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferencesStorage = SharedPreferencesStorage.getInstance();
    }

    private void configureListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        /*signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });*/
    }

    private void login() {
        if (validate()) {
            loginButton.setEnabled(false);

            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();

            User user = new User();
            user.setEmailId(emailId.getText().toString());
            user.setPassword(password.getText().toString());

            String json = new Gson().toJson(user);

            HttpUtil httpUtil = HttpUtil.getInstance();
            httpUtil.login(Config.USER_LOGIN_URL, json, new LoginCallback());
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void loginSuccess(String response) {
        loginButton.setEnabled(true);
        System.out.println("login success");

        User user = new Gson().fromJson(response, User.class);
        ArrayList<String> categories = new ArrayList<String>();
        categories.add("Education");
        categories.add("Health");
        categories.add("Environment");
        categories.add("Humanity");
        categories.add("Empowerment");
        categories.add("Real Heroes");
        categories.add("Achievers");
        categories.add("Sports");
        categories.add("Governance");
        categories.add("Beautiful");
        user.setCategories(categories);
        if (user != null) {
            ApplicationState.setUser(user);
            sharedPreferencesStorage.putBoolean(Config.IS_LOGGED_IN, true);
            sharedPreferencesStorage.putString(Config.USER_DATA_JSON, response);
            navigatetoMainActivity();
        } else {
            // need to show error message
        }
    }

    public void loginFailure() {
        loginButton.setEnabled(true);
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
    }

    public boolean validate() {
        String emailStr = emailId.getText().toString();
        String passwordStr = password.getText().toString();

        if (emailStr.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            emailId.setError("enter a valid email address");
            return false;
        }

        if (passwordStr.isEmpty() || passwordStr.length() < Config.PASSWORD_MIN_LENGTH || password.length() > Config.PASSWORD_MAX_LENGTH) {
            password.setError("between 4 and 10 characters");
            return false;
        }

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

    public void navigatetoMainActivity() {
        Intent intent = new Intent(getApplicationContext(), AppLauncherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private class LoginCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Network connection error", Toast.LENGTH_LONG).show();
                    loginButton.setEnabled(true);
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
                        loginButton.setEnabled(true);
                        progressDialog.dismiss();
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                        loginSuccess(responseStr);
                    }
                });
            }
        }
    }
}

