// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.humanize.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.humanize.android.R;
import com.humanize.android.util.AlarmHelper;
import java.sql.Time;

// Referenced classes of package com.humanize.android.activity:
//            AppLauncherActivity

public class PaperReminderActivity extends AppCompatActivity {

    private Button doneButton;
    private TimePicker timePicker;

    public PaperReminderActivity()
    {
    }

    private void configureListeners()
    {
        doneButton.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view)
            {
                Time time = new Time(timePicker.getCurrentHour().intValue(), timePicker.getCurrentMinute().intValue(), 0);
                (new AlarmHelper()).createAlarm(time);
                navigatetoMainActivity();
            }
        });
    }

    public void navigatetoMainActivity()
    {
        Intent intent = new Intent(getApplicationContext(), AppLauncherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_reminder);

        doneButton = (Button)findViewById(R.id.doneButton);
        timePicker = (TimePicker)findViewById(R.id.timePicker);
        timePicker.setCurrentHour(Integer.valueOf(8));
        timePicker.setCurrentMinute(Integer.valueOf(0));
        configureListeners();
    }

}
