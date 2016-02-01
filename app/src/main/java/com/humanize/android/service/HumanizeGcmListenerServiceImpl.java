package com.humanize.android.service;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.humanize.android.data.ClientMessage;

/**
 * Created by kamal on 1/31/16.
 */
public class HumanizeGcmListenerServiceImpl extends GcmListenerService {

    private static final String TAG = HumanizeGcmListenerServiceImpl.class.getName();

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String msg = data.getString("message");
        String id = data.getString("id");
        String entityId = data.getString("entityId");
        String entityType = data.getString("entityType");
        String localId = data.getString("localId");

        String tripName = data.getString("tripName");
        String tripId = data.getString("tripId");
        String tripIdNew = data.getString("tripIdNew");



        sendNotification(null, entityType, entityId, 0, 0);
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(ClientMessage message, String entityType, String entityId, int localId, long serverId) {
        if (message == null) {
            Log.e(TAG, "Message is null");
            return;
        }

        PendingIntent pendingIntent = null;
        PendingIntent cancelPendingIntent = null;
        Intent intent, cancelIntent;


    }
}

