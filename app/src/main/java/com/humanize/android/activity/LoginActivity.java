package com.humanize.android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.humanize.android.HttpResponseCallback;
import com.humanize.android.R;
import com.humanize.android.data.User;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.humanize.android.util.SharedPreferencesStorage;



public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText email;
    private EditText password;
    private Button loginButton;
    private TextView signupLink;
    private Toolbar toolbar;
    SharedPreferencesStorage sharedPreferencesStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize();
        configureListeners();
    }

    private void initialize() {
        email = (EditText) findViewById(R.id.email);
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
        if (!validate()) {
            loginFailure();
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        //progressDialog.show();

        User user = new User();
        user.setEmailId(email.getText().toString());
        user.setPassword(password.getText().toString());

        String json = new Gson().toJson(user);

        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.login(Config.LOGIN_URL, json, new HttpResponseCallback() {
            @Override
            public void onSuccess(String response) {
                System.out.println("i m here");
                //progressDialog.dismiss();
                loginSuccess(response);
            }

            @Override
            public void onFailure(String errorMsg) {
                System.out.println("or here");
                //progressDialog.dismiss();
                loginFailure();
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void loginSuccess(String response) {
        loginButton.setEnabled(true);
        System.out.println("login success");

        User user = new Gson().fromJson(response, User.class);
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
        boolean valid = true;

        String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();

        if (emailStr.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            email.setError("enter a valid email address");
            valid = false;
        } else {
            email.setError(null);
        }

        if (passwordStr.isEmpty() || passwordStr.length() < Config.PASSWORD_MIN_LENGTH || password.length() > Config.PASSWORD_MAX_LENGTH) {
            password.setError("between 4 and 10 characters");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
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
}

