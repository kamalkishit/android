package com.humanize.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.humanize.android.activity.PaperActivity;
import com.humanize.android.data.Content;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.HttpUtil;
import com.humanize.android.HttpResponseCallback;

import org.json.JSONObject;

import java.util.ArrayList;

public class PaperNotifier extends Activity {

    private final String FILENAME = "MainActivity";

    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = (TextView) findViewById(R.id.title);

        System.out.println("inside notifier");

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load(view);
            }
        });
    }

    public void load(View view) {
        System.out.println("load");
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getContents(new HttpResponseCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    ArrayList<Content> contents = new ArrayList<Content>();
                    PaperActivity.PaperAdapter.contents = contents;
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
}

