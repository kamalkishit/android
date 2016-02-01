package com.humanize.android.service;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;
import com.humanize.android.config.Config;

/**
 * Created by kamal on 1/31/16.
 */
public class HumanizeInstanceIDListenerServiceImpl extends InstanceIDListenerService {

    private static final String TAG = HumanizeInstanceIDListenerServiceImpl.class.getName();

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        SharedPreferencesService sharedPreferencesService = SharedPreferencesService.getInstance();
        sharedPreferencesService.putBoolean(Config.SENT_TOKEN_TO_SERVER, false);

        Intent intent = new Intent(this, RegistrationIntentServiceImpl.class);
        startService(intent);
    }
// [END refresh_token]
}
