package com.humanize.android;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.humanize.android.data.User;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by kamal on 11/26/15.
 */
public class UserCRUDService {

    private static final String TAG = "UserCRUDService";

    public void create() {

    }

    public void update() {
        try {
            HttpUtil.getInstance().updateUser(Config.USER_UPDATE_URL, new JsonParser().toJson(ApplicationState.getUser()), new UpdateCallback());
        } catch (Exception exception) {
            Log.e(TAG, exception.toString());
        }
    }

    public void findByEmailId() {

    }

    private class UpdateCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException exception) {
            Log.e(TAG, exception.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            User user = new JsonParser().fromJson(responseStr, User.class);

                            if (user != null) {
                                ApplicationState.setUser(user);
                            }
                        } catch (Exception exception) {
                            Log.e(TAG, exception.toString());
                        }
                    }
                });
            }
        }
    }
}
