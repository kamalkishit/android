package com.humanize.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.humanize.android.authentication.activity.SignupActivity;
import com.humanize.android.data.User;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by kamal on 11/29/15.
 */
public class UserTempService {

    ProgressDialog progressDialog;
    View view;

    private static final String TAG = "UserTempService";

    public void signup(Context context, View view, User user) {
        this.view = view;
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);

        progressDialog.setMessage("Registering...");
        progressDialog.show();

        try {
            HttpUtil.getInstance().signup(Config.USER_SIGNUP_URL, new JsonParser().toJson(user), new SignupCallback());
        } catch (Exception exception) {

        }

    }

    private class SignupCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException exception) {
            progressDialog.dismiss();
            Log.e(TAG, exception.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(view, "Network connection error", Snackbar.LENGTH_SHORT).show();

                }
            });
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            progressDialog.dismiss();
            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(view, "Error", Snackbar.LENGTH_SHORT).show();
                    }
                });
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String responseStr = response.body().string().toString();
                        } catch (IOException exception) {
                            Log.e(TAG, exception.toString());
                        } finally {
                        }
                    }
                });
            }
        }
    }
}
