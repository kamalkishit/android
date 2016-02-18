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
import com.humanize.android.config.StringConstants;
import com.humanize.android.data.User;
import com.humanize.android.helper.ActivityLauncher;
import com.humanize.android.service.SharedPreferencesService;
import com.humanize.android.helper.ApplicationState;
import com.humanize.android.config.Config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UpdateCategoriesActivity extends AppCompatActivity {

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
    private Category lawAndJustice;
    private Category realHeroes;
    private Category scienceAndTech;
    private Category sports;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_categories);
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
        toolbarText.setText(StringConstants.UPDATE_CATEGORIES);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        submitButton = (Button) findViewById(R.id.submitButton);

        achievers = new Category();
        achievers.textView = (TextView) findViewById(R.id.achievers);
        achievers.categoryStr = StringConstants.ACHIEVERS;

        beautiful = new Category();
        beautiful.textView = (TextView) findViewById(R.id.beautiful);
        beautiful.categoryStr = StringConstants.BEAUTIFUL;

        changemakers = new Category();
        changemakers.textView = (TextView) findViewById(R.id.changemakers);
        changemakers.categoryStr = StringConstants.CHANGEMAKERS;

        education = new Category();
        education.textView = (TextView) findViewById(R.id.education);
        education.categoryStr = StringConstants.EDUCATION;

        empowerment = new Category();
        empowerment.textView = (TextView) findViewById(R.id.empowerment);
        empowerment.categoryStr = StringConstants.EMPOWERMENT;

        environment = new Category();
        environment.textView = (TextView) findViewById(R.id.environment);
        environment.categoryStr = StringConstants.ENVIRONMENT;

        governance = new Category();
        governance.textView = (TextView) findViewById(R.id.governance);
        governance.categoryStr = StringConstants.GOVERNANCE;

        health = new Category();
        health.textView = (TextView) findViewById(R.id.health);
        health.categoryStr = StringConstants.HEALTH;

        humanity = new Category();
        humanity.textView = (TextView) findViewById(R.id.humanity);
        humanity.categoryStr = StringConstants.HUMANITY;

        inspiring = new Category();
        inspiring.textView = (TextView) findViewById(R.id.inspiring);
        inspiring.categoryStr = StringConstants.INSPIRING;

        lawAndJustice = new Category();
        lawAndJustice.textView = (TextView) findViewById(R.id.lawAndJustice);
        lawAndJustice.categoryStr = StringConstants.LAW_AND_JUSTICE;

        realHeroes = new Category();
        realHeroes.textView = (TextView) findViewById(R.id.realHeroes);
        realHeroes.categoryStr = StringConstants.REAL_HEROES;

        scienceAndTech = new Category();
        scienceAndTech.textView = (TextView) findViewById(R.id.scienceAndTech);
        scienceAndTech.categoryStr = StringConstants.SCIENCE_AND_TECH;

        sports = new Category();
        sports.textView = (TextView) findViewById(R.id.sports);
        sports.categoryStr = StringConstants.SPORTS;

        updateView();
    }

    private void configureListeners() {
        submitButton.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view) {
                if (selectedCategoriesCount < Config.MINIMUM_CATEGORY_COUNT) {
                    Snackbar.make(view, StringConstants.CATETORY_SELECTION_ERROR_STR, Snackbar.LENGTH_LONG).show();
                } else {
                    List<String> categoriesList = new ArrayList<>();
                    categoriesList.addAll(categories);
                    ApplicationState.getUser().setCategories(categoriesList);

                    SharedPreferencesService.getInstance().delete(Config.JSON_HOME_CONTENTS);
                    new ActivityLauncher().startHomeActivity();
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
        categories.add(category.categoryStr);
        selectedCategoriesCount++;
    }

    private void unselectCategory(Category category) {
        category.isSelected = false;
        category.textView.setBackground(getResources().getDrawable(R.drawable.border_button_dark_grey));
        category.textView.setTextColor(getResources().getColor(R.color.colorDarkGrey));
        categories.remove(category.categoryStr);
        selectedCategoriesCount--;
    }

    private void updateView() {
        if (ApplicationState.getUser() != null && ApplicationState.getUser().getCategories() != null) {
            User user = ApplicationState.getUser();

            if (user.getCategories().contains(StringConstants.ACHIEVERS)) {
                selectCategory(achievers);
            }

            if (user.getCategories().contains(StringConstants.BEAUTIFUL)) {
                selectCategory(beautiful);
            }

            if (user.getCategories().contains(StringConstants.CHANGEMAKERS)) {
                selectCategory(changemakers);
            }

            if (user.getCategories().contains(StringConstants.EDUCATION)) {
                selectCategory(education);
            }

            if (user.getCategories().contains(StringConstants.EMPOWERMENT)) {
                selectCategory(empowerment);
            }

            if (user.getCategories().contains(StringConstants.ENVIRONMENT)) {
                selectCategory(environment);
            }

            if (user.getCategories().contains(StringConstants.GOVERNANCE)) {
                selectCategory(governance);
            }

            if (user.getCategories().contains(StringConstants.HEALTH)) {
                selectCategory(health);
            }

            if (user.getCategories().contains(StringConstants.HUMANITY)) {
                selectCategory(humanity);
            }

            if (user.getCategories().contains(StringConstants.INSPIRING)) {
                selectCategory(inspiring);
            }

            if (user.getCategories().contains(StringConstants.REAL_HEROES)) {
                selectCategory(realHeroes);
            }

            if (user.getCategories().contains(StringConstants.LAW_AND_JUSTICE)) {
                selectCategory(lawAndJustice);
            }

            if (user.getCategories().contains(StringConstants.SCIENCE_AND_TECH)) {
                selectCategory(scienceAndTech);
            }

            if (user.getCategories().contains(StringConstants.SPORTS)) {
                selectCategory(sports);
            }
        }
    }

    class Category {

        private TextView textView;
        private String categoryStr;
        private boolean isSelected;

        protected Category() {
            this.textView = null;
            this.isSelected = false;
            this.categoryStr = null;
        }
    }
}
