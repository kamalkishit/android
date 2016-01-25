package com.humanize.app.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.humanize.app.R;

/**
 * Created by kamal on 1/14/16.
 */
public class InviteSuccessFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LinearLayout linearLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_invite_friend_success, null);
        Button submitButton = (Button) linearLayout.findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissFragment();
            }
        });

        builder.setView(linearLayout);
        return builder.create();
    }

    private void dismissFragment() {
        this.dismiss();
    }
}
