package com.humanize.android.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.humanize.android.R;
import com.humanize.android.helper.ActivityLauncher;

/**
 * Created by kamal on 1/8/16.
 */
public class LoginFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LinearLayout linearLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.login_dialog, null);
        Button submitButton = (Button) linearLayout.findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        builder.setView(linearLayout);
        return builder.create();
    }

    private void login() {
        new ActivityLauncher().startLoginActivity();
        this.dismiss();
    }
}