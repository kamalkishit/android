package com.humanize.android.fragment;

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

import com.humanize.android.R;
import com.humanize.android.config.Config;
import com.humanize.android.data.PaperTime;
import com.humanize.android.data.GuestUser;
import com.humanize.android.helper.ApplicationState;
import com.humanize.android.service.GsonParserServiceImpl;
import com.humanize.android.service.JsonParserService;
import com.humanize.android.service.SharedPreferencesService;

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
        jsonParserService = new GsonParserServiceImpl();
        timePicker = (TimePicker) linearLayout.findViewById(R.id.timePicker);
        cancelButton = (TextView) linearLayout.findViewById(R.id.cancelButton);
        okButton = (TextView) linearLayout.findViewById(R.id.okButton);
        timePicker.setCurrentHour(ApplicationState.getGuestUser().getPaperTime().getHour());
        timePicker.setCurrentMinute(ApplicationState.getGuestUser().getPaperTime().getMinute());
    }

    private void configureListeners() {
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GuestUser guestUser = ApplicationState.getGuestUser();
                guestUser.setPaperTime(new PaperTime(timePicker.getCurrentHour().intValue(), timePicker.getCurrentMinute().intValue()));
                try {
                    SharedPreferencesService.getInstance().putString(Config.JSON_GUEST_USER_DATA, jsonParserService.toJson(guestUser));
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
