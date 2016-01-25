package com.humanize.app.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.humanize.app.R;
import com.humanize.app.config.StringConstants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AboutUsActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarText) TextView toolbarText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        overridePendingTransition(R.anim.slide_right_to_left, 0);

        ButterKnife.bind(this);

        initialize();
    }

    private void initialize() {
        toolbarText.setText(StringConstants.ABOUT_US);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();
            overridePendingTransition(0, R.anim.slide_left_to_right);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_left_to_right);
    }
}
