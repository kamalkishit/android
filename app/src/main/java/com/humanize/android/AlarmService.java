package com.humanize.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

import com.humanize.android.activity.PaperActivity;
import com.humanize.android.activity.PaperLauncherActivity;
import com.humanize.android.util.ApplicationState;

public class AlarmService extends Service {

    private NotificationManager notificationManager;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        notificationManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(this.getApplicationContext(), PaperActivity.class);
        //intent1.putExtra(Config.IS_CONTENT_NAVIGATOR, false);

        Notification notification = new Notification(R.mipmap.ic_launcher, "This is a test message!", System.currentTimeMillis());
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        //notification.setLatestEventInfo(this.getApplicationContext(), "PAPER", "Your paper is ready!", pendingNotificationIntent);

        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(ApplicationState.getAppContext())
                .setContentIntent(pendingNotificationIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("PAPER")
                .setContentText("Paper is ready");

        notificationManager.notify(0, builder.build());
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
 