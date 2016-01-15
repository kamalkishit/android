package com.humanize.android.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
    @Bind(R.id.doneButton) Button submitButton;
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
        toolbarText.setText(StringConstants.UPDATE_PAPER_TIME);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarText.setGravity(1);
        timePicker.setCurrentHour(ApplicationState.getUser().getPaperTime().getHour());
        timePicker.setCurrentMinute(ApplicationState.getUser().getPaperTime().getMinute());
    }

    private void configureListeners() {
        submitButton.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                Time time = new Time(timePicker.getCurrentHour().intValue(), timePicker.getCurrentMinute().intValue(), 0);
                //(new AlarmHelper()).createAlarm(time);
                ApplicationState.getUser().setPaperTime(new PaperTime(timePicker.getCurrentHour().intValue(), timePicker.getCurrentMinute().intValue()));

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(PaperReminderActivity.this, AlarmReceiver.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(PaperReminderActivity.this, 0, intent, 0);
                alarmManager.cancel(alarmIntent);

                navigatetoMainActivity();
            }
        });
    }

    private void navigatetoMainActivity() {
        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
