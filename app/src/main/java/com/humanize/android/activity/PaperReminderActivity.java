package com.humanize.android.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.humanize.android.AlarmReceiver;
import com.humanize.android.R;
import com.humanize.android.common.Constants;
import com.humanize.android.common.StringConstants;
import com.humanize.android.data.PaperTime;
import com.humanize.android.util.AlarmHelper;
import com.humanize.android.util.ApplicationState;

import java.sql.Time;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kamal on 1/3/16.
 */
public class PaperReminderActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarText) TextView toolbarText;
    @Bind(R.id.doneButton) Button doneButton;
    @Bind(R.id.timePicker) TimePicker timePicker;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_reminder);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    private void initialize() {
        setSupportActionBar(toolbar);
        toolbarText.setText(StringConstants.PAPER_TIME_SELECTOR);
        toolbarText.setGravity(1);
        timePicker.setCurrentHour(Constants.DEFAULT_PAPER_TIME_HOUR);
        timePicker.setCurrentMinute(Constants.DEFAULT_PAPER_TIME_MINUTE);
    }

    private void configureListeners() {
        doneButton.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                Time time = new Time(timePicker.getCurrentHour().intValue(), timePicker.getCurrentMinute().intValue(), 0);
                //(new AlarmHelper()).createAlarm(time);
                ApplicationState.getUser().setPaperTime(new PaperTime(timePicker.getCurrentHour().intValue(), timePicker.getCurrentMinute().intValue()));

                AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(PaperReminderActivity.this, AlarmReceiver.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(PaperReminderActivity.this, 0, intent, 0);
                alarmManager.cancel(alarmIntent);

                navigatetoMainActivity();
            }
        });
    }

    private void navigatetoMainActivity() {
        Intent intent = new Intent(getApplicationContext(), AppLauncherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
