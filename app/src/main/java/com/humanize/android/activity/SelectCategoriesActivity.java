package com.humanize.android.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.humanize.android.R;
import com.humanize.android.data.User;
import com.humanize.android.helper.ActivityLauncher;
import com.humanize.android.service.SharedPreferencesService;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SelectCategoriesActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarText) TextView toolbarText;
    private int selectedCategoriesCount;
    private Set<String> categories;

    private Category achievers;
    private Category beautiful;
    private Category changemakers;
    private Category education;
    private Category empowerment;
    private Category environment;
    private Category governance;
    private Category health;
    private Category humanity;
    private Category inspiring;
    private Category kindness;
    private Category lawAndJustice;
    private Category realHeroes;
    private Category scienceAndTech;
    private Category smile;
    private Category sports;
    private Button submitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_categories2);
        overridePendingTransition(R.anim.slide_right_to_left, R.anim.slide_right_to_left);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
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

    private void initialize() {
        categories = new HashSet<>();

        toolbar.setCollapsible(true);
        setSupportActionBar(toolbar);
        toolbarText.setText("Update Categories");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        submitButton = (Button) findViewById(R.id.submitButton);

        achievers = new Category();
        achievers.textView = (TextView) findViewById(R.id.achievers);
        achievers.category = "Achievers";

        beautiful = new Category();
        beautiful.textView = (TextView) findViewById(R.id.beautiful);
        beautiful.category = "Beautiful";

        changemakers = new Category();
        changemakers.textView = (TextView) findViewById(R.id.changemakers);
        changemakers.category = "Changemakers";

        education = new Category();
        education.textView = (TextView) findViewById(R.id.education);
        education.category = "Education";

        empowerment = new Category();
        empowerment.textView = (TextView) findViewById(R.id.empowerment);
        empowerment.category = "Empowerment";

        environment = new Category();
        environment.textView = (TextView) findViewById(R.id.environment);
        environment.category = "Environment";

        governance = new Category();
        governance.textView = (TextView) findViewById(R.id.governance);
        governance.category = "Governance";

        health = new Category();
        health.textView = (TextView) findViewById(R.id.health);
        health.category = "Health";

        humanity = new Category();
        humanity.textView = (TextView) findViewById(R.id.humanity);
        humanity.category = "Humanity";

        inspiring = new Category();
        inspiring.textView = (TextView) findViewById(R.id.inspiring);
        inspiring.category = "Inspiring";

        kindness = new Category();
        kindness.textView = (TextView) findViewById(R.id.kindness);
        kindness.category = "Kindness";

        lawAndJustice = new Category();
        lawAndJustice.textView = (TextView) findViewById(R.id.lawAndJustice);
        lawAndJustice.category = "Law and Justice";

        realHeroes = new Category();
        realHeroes.textView = (TextView) findViewById(R.id.realHeroes);
        realHeroes.category = "Real Heroes";

        scienceAndTech = new Category();
        scienceAndTech.textView = (TextView) findViewById(R.id.scienceAndTech);
        scienceAndTech.category = "Science and Tech";

        smile = new Category();
        smile.textView = (TextView) findViewById(R.id.smile);
        smile.category = "Smile";

        sports = new Category();
        sports.textView = (TextView) findViewById(R.id.sports);
        sports.category = "Sports";

        updateView();
    }

    private void configureListeners() {
        submitButton.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (selectedCategoriesCount < 1) {
                    Snackbar.make(view, "Select at least 1 category", Snackbar.LENGTH_LONG).show();
                } else {
                    List<String> categoriesList = new ArrayList<>();
                    categoriesList.addAll(categories);
                    ApplicationState.getUser().setCategories(categoriesList);

                    SharedPreferencesService.getInstance().delete(Config.JSON_CONTENTS);
                    new ActivityLauncher().startCardActivity();
                }
            }
        });

        achievers.textView.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (achievers.isSelected) {
                    unselectCategory(achievers);
                } else {
                    selectCategory(achievers);
                }
            }
        });

        beautiful.textView.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (beautiful.isSelected) {
                    unselectCategory(beautiful);
                } else {
                    selectCategory(beautiful);
                }
            }
        });

        changemakers.textView.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (changemakers.isSelected) {
                    unselectCategory(changemakers);
                } else {
                    selectCategory(changemakers);
                }
            }
        });

        education.textView.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (education.isSelected) {
                    unselectCategory(education);
                } else {
                    selectCategory(education);
                }
            }
        });

        empowerment.textView.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (empowerment.isSelected) {
                    unselectCategory(empowerment);
                } else {
                    selectCategory(empowerment);
                }
            }
        });

        environment.textView.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (environment.isSelected) {
                    unselectCategory(environment);
                } else {
                    selectCategory(environment);
                }
            }
        });

        governance.textView.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (governance.isSelected) {
                    unselectCategory(governance);
                } else {
                    selectCategory(governance);
                }
            }
        });

        health.textView.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (health.isSelected) {
                    unselectCategory(health);
                } else {
                    selectCategory(health);
                }
            }
        });

        humanity.textView.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (humanity.isSelected) {
                    unselectCategory(humanity);
                } else {
                    selectCategory(humanity);
                }
            }
        });

        inspiring.textView.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (inspiring.isSelected) {
                    unselectCategory(inspiring);
                } else {
                    selectCategory(inspiring);
                }
            }
        });

        kindness.textView.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (kindness.isSelected) {
                    unselectCategory(kindness);
                } else {
                    selectCategory(kindness);
                }
            }
        });

        lawAndJustice.textView.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (lawAndJustice.isSelected) {
                    unselectCategory(lawAndJustice);
                } else {
                    selectCategory(lawAndJustice);
                }
            }
        });

        realHeroes.textView.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (realHeroes.isSelected) {
                    unselectCategory(realHeroes);
                } else {
                    selectCategory(realHeroes);
                }
            }
        });

        scienceAndTech.textView.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (scienceAndTech.isSelected) {
                    unselectCategory(scienceAndTech);
                } else {
                    selectCategory(scienceAndTech);
                }
            }
        });

        smile.textView.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (smile.isSelected) {
                    unselectCategory(smile);
                } else {
                    selectCategory(smile);
                }
            }
        });

        sports.textView.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (sports.isSelected) {
                    unselectCategory(sports);
                } else {
                    selectCategory(sports);
                }
            }
        });
    }

    private void selectCategory(Category category) {
        category.isSelected = true;
        category.textView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        category.textView.setTextColor(getResources().getColor(R.color.colorWhite));
        categories.add(category.category);
        selectedCategoriesCount++;
    }

    private void unselectCategory(Category category) {
        category.isSelected = false;
        category.textView.setBackground(getResources().getDrawable(R.drawable.border_button_dark_grey));
        category.textView.setTextColor(getResources().getColor(R.color.colorDarkGrey));
        categories.remove(category.category);
        selectedCategoriesCount--;
    }

    private void updateView() {
        if (ApplicationState.getUser() != null && ApplicationState.getUser().getCategories() != null) {
            User user = ApplicationState.getUser();

            if (user.getCategories().contains("Achievers")) {
                selectCategory(achievers);
            }

            if (user.getCategories().contains("Beautiful")) {
                selectCategory(beautiful);
            }

            if (user.getCategories().contains("Changemakers")) {
                selectCategory(changemakers);
            }

            if (user.getCategories().contains("Education")) {
                selectCategory(education);
            }

            if (user.getCategories().contains("Empowerment")) {
                selectCategory(empowerment);
            }

            if (user.getCategories().contains("Environment")) {
                selectCategory(environment);
            }

            if (user.getCategories().contains("Governance")) {
                selectCategory(governance);
            }

            if (user.getCategories().contains("Health")) {
                selectCategory(health);
            }

            if (user.getCategories().contains("Humanity")) {
                selectCategory(humanity);
            }

            if (user.getCategories().contains("Inspiring")) {
                selectCategory(inspiring);
            }

            if (user.getCategories().contains("Kindness")) {
                selectCategory(kindness);
            }

            if (user.getCategories().contains("Real Heroes")) {
                selectCategory(realHeroes);
            }

            if (user.getCategories().contains("Law and Justice")) {
                selectCategory(lawAndJustice);
            }

            if (user.getCategories().contains("Science and Tech")) {
                selectCategory(scienceAndTech);
            }

            if (user.getCategories().contains("Smile")) {
                selectCategory(smile);
            }

            if (user.getCategories().contains("Sports")) {
                selectCategory(sports);
            }
        }
    }

    class Category {

        private TextView textView;
        private String category;
        private boolean isSelected;

        protected Category() {
            this.textView = null;
            this.isSelected = false;
        }
    }

}
