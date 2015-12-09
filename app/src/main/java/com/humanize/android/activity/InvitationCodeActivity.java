package com.humanize.android.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.humanize.android.R;
import com.humanize.android.common.StringConstants;
import com.humanize.android.util.Config;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InvitationCodeActivity extends AppCompatActivity {

    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.emailId) EditText emailId;
    @Bind(R.id.submitButton) Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_code);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    private void initialize() {

    }

    private void configureListeners() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendInvitationCode();
            }
        });
    }

    private void sendInvitationCode() {
        validate();
    }

    public boolean validate() {
        String emailStr = emailId.getText().toString();

        if (emailStr.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            emailId.setError(StringConstants.EMAIL_VALIDATION_ERROR_STR);
            Snackbar snackbar = Snackbar.make(coordinatorLayout, StringConstants.EMAIL_VALIDATION_ERROR_STR, Snackbar.LENGTH_SHORT);
            return false;
        }

        return true;
    }
}
