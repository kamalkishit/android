// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.humanize.android.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.humanize.android.R;
import com.humanize.android.data.User;
import com.humanize.android.util.ApplicationState;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;

// Referenced classes of package com.humanize.android.activity:
//            PaperReminderActivity

public class SelectCategoriesActivity extends AppCompatActivity {

    private Set<String> categories;
    private Button achievers;
    private boolean achieversFlag;
    private Button beautiful;
    private boolean beautifulFlag;
    private Button education;
    private boolean educationFlag;
    private Button empowerment;
    private boolean empowermentFlag;
    private Button environment;
    private boolean environmentFlag;
    private Button governance;
    private boolean governanceFlag;
    private Button health;
    private boolean healthFlag;
    private Button humanity;
    private boolean humanityFlag;
    private Button law;
    private boolean lawFlag;
    private Button nextButton;
    private Button realHeroes;
    private boolean realHeroesFlag;
    private Button science;
    private boolean scienceFlag;
    private int selectedCategoriesCount;
    private Button sports;
    private boolean sportsFlag;

    private void configureListeners() {
        nextButton.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view)
            {
                //User user = ApplicationState.getUser().setCategories(Arrays.asList(String [])(categories.toArray()));
                startPaperTimeSelectorActivity();
            }
        });

        achievers.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view)
            {
                System.out.println(achieversFlag);
                achieversFlag = handleButtonSelection(achievers, achieversFlag);
            }
        });

        beautiful.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view)
            {
                beautifulFlag = handleButtonSelection(beautiful, beautifulFlag);
            }
        });

        education.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view)
            {
                educationFlag = handleButtonSelection(education, educationFlag);
            }
        });

        empowerment.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view)
            {
                empowermentFlag = handleButtonSelection(empowerment, empowermentFlag);
            }
        });

        environment.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view)
            {
                environmentFlag = handleButtonSelection(environment, environmentFlag);
            }
        });

        governance.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view)
            {
                governanceFlag = handleButtonSelection(governance, governanceFlag);
            }
        });

        health.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view)
            {
                healthFlag = handleButtonSelection(health, healthFlag);
            }
        });

        humanity.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view)
            {
                humanityFlag = handleButtonSelection(humanity, humanityFlag);
            }
        });

        law.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view)
            {
                lawFlag = handleButtonSelection(law, lawFlag);
            }
        });

        realHeroes.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view)
            {
                realHeroesFlag = handleButtonSelection(realHeroes, realHeroesFlag);
            }
        });

        science.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view)
            {
                scienceFlag = handleButtonSelection(science, scienceFlag);
            }
        });

        sports.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view)
            {
                sportsFlag = handleButtonSelection(sports, sportsFlag);
            }
        });
    }

    private boolean handleButtonSelection(Button button, boolean flag) {
        System.out.println(flag);
        if (!flag) {
            selectedCategoriesCount = selectedCategoriesCount + 1;
            if (selectedCategoriesCount > 3) {
                nextButton.setEnabled(true);
            }

            button.setTextColor(getResources().getColor(R.color.colorAccent));
            categories.add(button.getText().toString());
            return true;
        }

        selectedCategoriesCount = selectedCategoriesCount - 1;
        if (selectedCategoriesCount < 3) {
            nextButton.setEnabled(false);
        }

        button.setTextColor(getResources().getColor(R.color.colorBlack));
        categories.remove(button.getText().toString());
        return false;
    }

    private void initialize() {
        selectedCategoriesCount = 0;
        nextButton.setEnabled(false);
        categories = new HashSet();
        achievers = (Button)findViewById(R.id.achievers);
        beautiful = (Button)findViewById(R.id.beautiful);
        education = (Button)findViewById(R.id.education);
        empowerment = (Button)findViewById(R.id.empowerment);
        environment = (Button)findViewById(R.id.environment);
        governance = (Button)findViewById(R.id.governance);
        health = (Button)findViewById(R.id.health);
        humanity = (Button)findViewById(R.id.humanity);
        law = (Button)findViewById(R.id.law);
        realHeroes = (Button)findViewById(R.id.realHeroes);
        science = (Button)findViewById(R.id.science);
        sports = (Button)findViewById(R.id.sports);
        achieversFlag = false;
        beautifulFlag = false;
        educationFlag = false;
        empowermentFlag = false;
        environmentFlag = false;
        governanceFlag = false;
        healthFlag = false;
        humanityFlag = false;
        lawFlag = false;
        realHeroesFlag = false;
        scienceFlag = false;
        sportsFlag = false;
    }

    private void startPaperTimeSelectorActivity() {
        categories.toArray();
        //ApplicationState.getUser().setCategories((ArrayList<String>)(categories.toArray()));
        Intent intent = new Intent(getApplicationContext(), PaperReminderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_categories);

        categories = new HashSet<String>();

        nextButton = (Button)findViewById(R.id.nextButton);
        initialize();
        configureListeners();
    }
}
