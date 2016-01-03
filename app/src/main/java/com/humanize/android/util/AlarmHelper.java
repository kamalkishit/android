package com.humanize.android.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.humanize.android.AlarmReceiver;
import java.sql.Time;
import java.util.Calendar;

public class AlarmHelper {

    public void createAlarm(Time time) {
        AlarmManager alarmmanager = (AlarmManager)ApplicationState.getAppContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ApplicationState.getAppContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ApplicationState.getAppContext(), 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
        calendar.set(Calendar.MINUTE, time.getMinutes());
        alarmmanager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000*60*24, pendingIntent);
    }
}
