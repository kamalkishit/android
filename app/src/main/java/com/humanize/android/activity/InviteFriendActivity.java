package com.humanize.android.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.humanize.android.ApiImpl;
import com.humanize.android.R;
import com.humanize.android.common.StringConstants;
import com.humanize.android.data.InviteFriend;
import com.humanize.android.fragment.InviteSuccessFragment;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class InviteFriendActivity extends AppCompatActivity {

    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarText) TextView toolbarText;
    @Bind(R.id.emailId) EditText emailId;
    @Bind(R.id.submitButton) Button submitButton;

    private ApiImpl apiImpl;
    private ProgressDialog progressDialog;
    private static String TAG = InviteFriendActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initialize() {
        apiImpl = new ApiImpl();
        progressDialog = new ProgressDialog(this);
        toolbar.setCollapsible(true);
        toolbarText.setText(StringConstants.INVITE_FRIEND);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void configureListeners() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                submit();
            }
        });
    }

    private void submit() {
        if (validate()) {
            InviteFriend inviteFriend = new InviteFriend();
            inviteFriend.setEmailId(emailId.getText().toString());
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(StringConstants.INVITING);
            progressDialog.show();

            apiImpl.inviteFriend(inviteFriend, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Snackbar.make(coordinatorLayout, StringConstants.NETWORK_CONNECTION_ERROR_STR, Snackbar.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Snackbar.make(coordinatorLayout, StringConstants.FAILURE_STR, Snackbar.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                InviteSuccessFragment inviteSuccessFragment = new InviteSuccessFragment();
                                inviteSuccessFragment.show(InviteFriendActivity.this.getFragmentManager(), "");
                            }
                        });
                    }
                }
            });
        }
    }

    public boolean validate() {
        String emailStr = emailId.getText().toString();

        if (emailStr.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            emailId.setError(StringConstants.EMAIL_VALIDATION_ERROR_STR);
            Snackbar.make(coordinatorLayout, StringConstants.EMAIL_VALIDATION_ERROR_STR, Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void returnToMainActivity() {
        super.onBackPressed();
    }

    private class InviteCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException exception) {
            Log.e(TAG, exception.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(coordinatorLayout, StringConstants.NETWORK_CONNECTION_ERROR_STR, Snackbar.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(coordinatorLayout, StringConstants.FAILURE_STR, Snackbar.LENGTH_LONG).show();
                    }
                });
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(coordinatorLayout, StringConstants.SUCCESS_STR, Snackbar.LENGTH_LONG).show();
                        InviteSuccessFragment inviteSuccessFragment = new InviteSuccessFragment();
                        inviteSuccessFragment.show(InviteFriendActivity.this.getFragmentManager(), "");
                    }
                });
            }
        }
    }
}
