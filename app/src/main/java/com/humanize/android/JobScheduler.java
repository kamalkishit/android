package com.humanize.android;

/**
 * Created by Kamal on 8/14/15.
 */
public class JobScheduler {

    public static void schedulePaper() {
       /* AlarmManager alarmManager = (AlarmManager) ApplicationState.getAppContext()
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ApplicationState.getAppContext(), PaperNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ApplicationState.getAppContext(), 0, intent, 0);

        // set the time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 54);

        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
          //      1000 * 60 * 5, pendingIntent);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                2 * 60 * 60, pendingIntent);*/
    }
}
