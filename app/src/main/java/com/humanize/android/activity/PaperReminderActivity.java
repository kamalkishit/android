
package com.humanize.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.humanize.android.R;
import com.humanize.android.common.Constants;
import com.humanize.android.util.AlarmHelper;
import com.humanize.android.util.ApplicationState;

import java.sql.Time;

import butterknife.Bind;
import butterknife.ButterKnife;

// Referenced classes of package com.humanize.android.activity:
//            AppLauncherActivity

public class PaperReminderActivity extends AppCompatActivity {

    @Bind(R.id.doneButton) Button doneButton;
    @Bind(R.id.timePicker) TimePicker timePicker;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_reminder);

        ButterKnife.bind(this);

        timePicker.setCurrentHour(Constants.DEFAULT_PAPER_TIME_HOUR);
        timePicker.setCurrentMinute(Constants.DEFAULT_PAPER_TIME_MINUTE);
        configureListeners();
    }

    private void configureListeners() {
        doneButton.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                Time time = new Time(timePicker.getCurrentHour().intValue(), timePicker.getCurrentMinute().intValue(), 0);
                (new AlarmHelper()).createAlarm(time);
                //ApplicationState.getUser().setPaperTime(new Time(time.getHours(), time.getMinutes(), time.getSeconds()));
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
