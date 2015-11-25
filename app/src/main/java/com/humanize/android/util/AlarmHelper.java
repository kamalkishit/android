// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.humanize.android.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.humanize.android.AlarmReceiver;
import java.sql.Time;
import java.util.Calendar;

// Referenced classes of package com.humanize.android.util:
//            ApplicationState

public class AlarmHelper
{

    public AlarmHelper()
    {
    }

    public void createAlarm(Time time)
    {
        AlarmManager alarmmanager = (AlarmManager)ApplicationState.getAppContext().getSystemService("alarm");
        Object obj = new Intent(ApplicationState.getAppContext(), AlarmReceiver.class);
        obj = PendingIntent.getBroadcast(ApplicationState.getAppContext(), 0, ((Intent) (obj)), 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (time != null)
        {
            calendar.set(11, time.getHours());
            calendar.set(12, time.getMinutes());
        } else
        {
            calendar.set(11, 8);
            calendar.set(12, 0);
        }
        alarmmanager.setRepeating(0, calendar.getTimeInMillis(), 0x5265c00L, ((PendingIntent) (obj)));
    }
}
