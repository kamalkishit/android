package com.humanize.android.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.humanize.android.R;
import com.humanize.android.config.Config;
import com.humanize.android.data.UserDevice;
import com.humanize.android.utils.AppUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by kamal on 1/31/16.
 */
public class RegistrationIntentServiceImpl extends IntentService {

    String token;

    private static final String[] TOPICS = {"global"};
    private static final LogService logService = new LogServiceImpl();
    private static final String TAG = RegistrationIntentServiceImpl.class.getName();

    public RegistrationIntentServiceImpl() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG) {
                // [START get_token]
                // Initially this call goes out to the network to retrieve the token, subsequent calls
                // are local.
                InstanceID instanceID = InstanceID.getInstance(this);
                token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                sendRegistrationToServer(token);
            }
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
        }

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

    private void success() {
        SharedPreferencesService.getInstance().putBoolean(Config.TOKEN_SENT_TO_SERVER, true);
        try {
            subscribeTopics(token);
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
        }
    }

    private class UserDeviceInfoUpdateCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException exception) {
            logService.e(TAG, exception.getMessage());
        }

        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            if (!response.isSuccessful()) {
            } else {
                success();
            }
        }
    }
}
