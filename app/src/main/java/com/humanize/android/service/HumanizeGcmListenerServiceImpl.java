package com.humanize.android.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.humanize.android.R;
import com.humanize.android.activity.SingleContentActivity;
import com.humanize.android.config.Config;
import com.humanize.android.config.StringConstants;
import com.humanize.android.helper.ApplicationState;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by kamal on 1/31/16.
 */
public class HumanizeGcmListenerServiceImpl extends GcmListenerService {

    private static int notificationId = 0;

    private static final LogService logService = new LogServiceImpl();
    private static final String TAG = HumanizeGcmListenerServiceImpl.class.getName();


    @Override
    public void onMessageReceived(String from, Bundle data) {
        String title = data.getString(StringConstants.TITLE);
        String imageUrl = data.getString(StringConstants.IMAGE_URL);
        String urlId = data.getString(StringConstants.URL_ID);

        if (ApplicationState.getGuestUser().getNotification()) {
            sendNotification(title, imageUrl, urlId);
        }
    }

    private void sendNotification(String title, String imageUrl, String urlId) {
        NotificationManager notificationManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);

        Intent intent = new Intent(ApplicationState.getAppContext(), SingleContentActivity.class);
        intent.putExtra(Config.URL, urlId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        notificationId++;

        PendingIntent pendingIntent = PendingIntent.getActivity(ApplicationState.getAppContext(), notificationId, intent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap remotePicture = null;
        Bitmap largeIcon = null;

        try {
            remotePicture = BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent());
            largeIcon = BitmapFactory.decodeResource(ApplicationState.getAppContext().getResources(), R.drawable.ic_launcher);
        } catch (Exception exception) {
            logService.e(TAG, exception.getMessage());
        }

        Notification notification = new Notification.Builder(ApplicationState.getAppContext())
                .setContentTitle(StringConstants.HUMANIZE)
                .setContentText(title)
                .setSmallIcon(R.drawable.ic_humanize)
                //.setLargeIcon(largeIcon)
                .setAutoCancel(true)
                .setStyle(new Notification.BigPictureStyle()
                        .bigPicture(remotePicture).setBigContentTitle(title))
                .build();

        notification.contentIntent = pendingIntent;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(notificationId, notification);
    }
}

