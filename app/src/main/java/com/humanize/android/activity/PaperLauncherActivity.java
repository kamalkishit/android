package com.humanize.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.humanize.android.HttpResponseCallback;
import com.humanize.android.R;
import com.humanize.android.content.data.Contents;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.humanize.android.util.SharedPreferencesStorage;

public class PaperLauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_launcher);

        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getPaper(new HttpResponseCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    Contents contents = new Gson().fromJson(response, Contents.class);
                    PaperActivity.PaperAdapter.contents = contents.getContents();
                    SharedPreferencesStorage.getInstance().putString(Config.JSON_PAPER, response);
                    Intent intent = new Intent(ApplicationState.getAppContext(), PaperActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ApplicationState.getAppContext().startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(String errorMsg) {
                System.out.println("failure");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_paper_launcher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
