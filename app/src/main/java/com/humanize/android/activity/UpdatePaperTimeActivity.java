package com.humanize.android.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import com.humanize.android.R;
import com.humanize.android.config.StringConstants;
import com.humanize.android.fragment.PaperTimeUpdateFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UpdatePaperTimeActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarText) TextView toolbarText;

    private PaperTimeUpdateFragment paperTimeUpdateFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_paper_time);
        overridePendingTransition(R.anim.slide_right_to_left, 0);

        ButterKnife.bind(this);

        initialize();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();
            paperTimeUpdateFragment.dismiss();
            overridePendingTransition(0, R.anim.slide_left_to_right);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(0, R.anim.slide_left_to_right);
        getFragmentManager().popBackStack();
        super.onBackPressed();
    }

    private void initialize() {
        toolbarText.setText(StringConstants.UPDATE_PAPER_TIME);
        toolbarText.setGravity(Gravity.CENTER);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        paperTimeUpdateFragment = new PaperTimeUpdateFragment();
        paperTimeUpdateFragment.show(getFragmentManager(), "");
    }
}
