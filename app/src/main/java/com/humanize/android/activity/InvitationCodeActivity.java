package com.humanize.android.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.humanize.android.R;
import com.humanize.android.utils.StringConstants;
import com.humanize.android.utils.HttpUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InvitationCodeActivity extends AppCompatActivity {

    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.emailId) EditText emailId;
    @Bind(R.id.submitButton) Button submitButton;

    private static final String TAG = InvitationCodeActivity.class.getSimpleName();

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
        emailId.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (emailId.getError() != null) {
                    emailId.setError(null);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendInvitationCode(emailId.getText().toString());
            }
        });
    }

    private void sendInvitationCode(String emailId) {
        if (validate()) {
            HttpUtil.getInstance().inviteUser(emailId, new InviteUserCallback());
        }
    }

    public boolean validate() {
        String emailStr = emailId.getText().toString();

        if (emailStr.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            emailId.setError(StringConstants.EMAIL_VALIDATION_ERROR_STR);
            Snackbar snackbar = Snackbar.make(coordinatorLayout, StringConstants.EMAIL_VALIDATION_ERROR_STR, Snackbar.LENGTH_SHORT);
            snackbar.show();
            return false;
        }

        return true;
    }

    private class InviteUserCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException exception) {
            Log.e(TAG, exception.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    submitButton.setEnabled(true);
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, StringConstants.NETWORK_CONNECTION_ERROR_STR, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            });
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
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
                        submitButton.setEnabled(true);
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, StringConstants.SUCCESS_STR, Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });
            }
        }
    }
}
