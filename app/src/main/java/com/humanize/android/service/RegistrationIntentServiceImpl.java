package com.humanize.android.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.humanize.android.R;
import com.humanize.android.config.Config;
import com.humanize.android.config.Constants;
import com.humanize.android.config.StringConstants;
import com.humanize.android.data.UserDevice;
import com.humanize.android.fragment.ContactUsSuccessFragment;
import com.humanize.android.utils.AppUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by kamal on 1/31/16.
 */
public class RegistrationIntentServiceImpl extends IntentService {

    private static final String TAG = RegistrationIntentServiceImpl.class.getName();
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentServiceImpl() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "GCM Registration Token: Enter ");

        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG) {
                // [START get_token]
                // Initially this call goes out to the network to retrieve the token, subsequent calls
                // are local.
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.i(TAG, "GCM Registration Token: " + token);

                // send to server if the key is not stored in local preference
                String storedToken = SharedPreferencesService.getInstance().getString(Config.GCM_DEVICE_TOKEN);
                if ( !token.equals(storedToken) || !SharedPreferencesService.getInstance().getBoolean(Config.SENT_TOKEN_TO_SERVER)) {
                    sendRegistrationToServer(token);
                }

                // Subscribe to topic channels
                subscribeTopics(token);

                // You should store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
                SharedPreferencesService.getInstance().putBoolean(Config.SENT_TOKEN_TO_SERVER, true);
                SharedPreferencesService.getInstance().putString(Config.GCM_DEVICE_TOKEN, token);
                // [END get_token]
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            SharedPreferencesService.getInstance().putBoolean(Config.SENT_TOKEN_TO_SERVER, false);
        }

        // TBD
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(String token) {
        UserDevice userDevice = AppUtils.getDeviceInformation();
        userDevice.setGcmRegistrationToken(token);
        new ApiServiceImpl().registerDevice(userDevice, new UserDeviceInfoUpdateCallback());
    }

    private void subscribeTopics(String token) throws IOException {
        for (String topic : TOPICS) {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }

    private class UserDeviceInfoUpdateCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException exception) {
            SharedPreferencesService.getInstance().putBoolean(Config.SENT_TOKEN_TO_SERVER, false);
        }

        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            if (!response.isSuccessful()) {
                SharedPreferencesService.getInstance().putBoolean(Config.SENT_TOKEN_TO_SERVER, false);
            } else {
                SharedPreferencesService.getInstance().putBoolean(Config.SENT_TOKEN_TO_SERVER, true);
            }
        }
    }

}
