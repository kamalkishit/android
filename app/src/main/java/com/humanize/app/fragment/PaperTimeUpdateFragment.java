package com.humanize.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.humanize.app.R;
import com.humanize.app.config.Config;
import com.humanize.app.data.PaperTime;
import com.humanize.app.data.User;
import com.humanize.app.helper.ApplicationState;
import com.humanize.app.service.JsonParserService;
import com.humanize.app.service.SharedPreferencesService;

/**
 * Created by kamal on 1/21/16.
 */
public class PaperTimeUpdateFragment extends DialogFragment {

    private LinearLayout linearLayout;
    private TimePicker timePicker;
    private TextView okButton;
    private TextView cancelButton;

    private JsonParserService jsonParserService;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        linearLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_update_paper_time, null);

        initialize();
        configureListeners();

        builder.setView(linearLayout);
        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Activity activity = getActivity();
        activity.overridePendingTransition(0, R.anim.slide_left_to_right);
        activity.finish();
        super.onDismiss(dialog);

    }

    private void initialize() {
        timePicker = (TimePicker) linearLayout.findViewById(R.id.timePicker);
        cancelButton = (TextView) linearLayout.findViewById(R.id.cancelButton);
        okButton = (TextView) linearLayout.findViewById(R.id.okButton);
        timePicker.setCurrentHour(ApplicationState.getUser().getPaperTime().getHour());
        timePicker.setCurrentMinute(ApplicationState.getUser().getPaperTime().getMinute());
    }

    private void configureListeners() {
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = ApplicationState.getUser();
                user.setPaperTime(new PaperTime(timePicker.getCurrentHour().intValue(), timePicker.getCurrentMinute().intValue()));
                try {
                    SharedPreferencesService.getInstance().putString(Config.JSON_USER_DATA, jsonParserService.toJson(user));
                } catch (Exception exception) {

                }

                dismissFragment();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissFragment();
            }
        });
    }

    private void dismissFragment() {
        getActivity().finish();
        this.dismiss();
    }
}
