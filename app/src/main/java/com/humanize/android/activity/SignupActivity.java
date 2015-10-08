package com.humanize.android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

import com.google.gson.Gson;
import com.humanize.android.HttpResponseCallback;
import com.humanize.android.R;
import com.humanize.android.data.User;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private EditText email;
    private EditText password;
    private Button signupButton;
    private TextView loginLink;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        signupButton = (Button) findViewById(R.id.signup_button);
        //loginLink = (TextView) findViewById(R.id.login_link);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setCollapsible(true);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        if (!validate()) {
            signupFailure();
            return;
        }

        signupButton.setEnabled(false);

        progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        User user = new User();
        user.setEmailId(email.getText().toString());
        user.setPassword(password.getText().toString());

        HttpUtil asyncHttpWrapper = HttpUtil.getInstance();
        asyncHttpWrapper.signup(Config.SIGNUP_URL, new Gson().toJson(user), new HttpResponseCallback() {
            @Override
            public void onSuccess(String response) {
                signupSuccess();
            }

            @Override
            public void onFailure(String errorMsg) {
                signupFailure();
            }
        });
    }

    private Map createParams() {
        Map params = new HashMap<String, String>();
        User user = new User();
        user.setEmailId(email.getText().toString());
        user.setPassword(password.getText().toString());
        params.put("user", new Gson().toJson(user));

        return params;
    }

    public void signupSuccess() {
        progressDialog.dismiss();
        signupButton.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Signup success", Toast.LENGTH_LONG).show();
        navigateToLoginActivity();
    }

    public void signupFailure() {
        //progressDialog.dismiss();
        signupButton.setEnabled(true);
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();
    }

    public boolean validate() {
        boolean valid = true;

        String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();

        if (emailStr.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            email.setError("enter a valid email address");
            valid = false;
        } else {
            email.setError(null);
        }

        if (passwordStr.isEmpty() || passwordStr.length() < Config.PASSWORD_MIN_LENGTH || passwordStr.length() > Config.PASSWORD_MAX_LENGTH) {
            password.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
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

    public void navigateToLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}