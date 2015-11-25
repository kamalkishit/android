package com.humanize.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.humanize.android.HttpResponseCallback;
import com.humanize.android.R;
import com.humanize.android.content.data.Content;
import com.humanize.android.content.data.Contents;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubmitActivity extends AppCompatActivity {

    private EditText contentURL;
    private Button submitButton;
    private Toolbar toolbar;
    private Spinner categoriesSpinner;
    private Spinner subCategoriesSpinner;
    private Content content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        contentURL = (EditText) findViewById(R.id.content_url);
        submitButton = (Button) findViewById(R.id.submit);
        content = new Content();

        submitButton.setEnabled(false);

        categoriesSpinner = (Spinner) findViewById(R.id.categories_spinner);
        //categoriesSpinner.setEnabled(false);
        subCategoriesSpinner = (Spinner) findViewById(R.id.subcategories_spinner);
        //subCategoriesSpinner.setEnabled(false);
        categoriesSpinner.setOnItemSelectedListener(new SpinnerCategoriesHandler());
        subCategoriesSpinner.setOnItemSelectedListener(new SpinnnerSubCategoriesHandler());

        ArrayAdapter<CharSequence> categoriesAdapter = ArrayAdapter.createFromResource(this,
                R.array.categories, R.layout.spinner_item);

        ArrayAdapter<CharSequence> subcategoriesAdapter = ArrayAdapter.createFromResource(this, R.array.sub_categories, R.layout.spinner_item);

        //categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriesAdapter.setDropDownViewResource(R.layout.spinner_categories);
        categoriesSpinner.setAdapter(categoriesAdapter);

        subcategoriesAdapter.setDropDownViewResource(R.layout.spinner_categories);
        subCategoriesSpinner.setAdapter(subcategoriesAdapter);

        submitButton.setEnabled(false);
        configureListeners();
    }

    private void configureListeners() {
        /*categoriesSpinner.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View view) {
                                                     InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                                     inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                                 }
                                             }
        );*/

        contentURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoriesSpinner.setEnabled(true);
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contentURL.getText().toString().length() == 0) {
                    Snackbar.make(view, "URL is empty", Snackbar.LENGTH_SHORT).show();
                } else {
                    submit(view);
                }
            }
        });
    }

    private boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url);
        if(m.matches())
            return true;
        else
            return false;
    }

    private void submit(final View view) {
        if (!isValidUrl(contentURL.getText().toString())) {
            Snackbar.make(view, "Invalid URL", Snackbar.LENGTH_SHORT).show();
        } else {

            HttpUtil httpUtil = HttpUtil.getInstance();
            System.out.println("inside submit");
            content.setContentURL(contentURL.getText().toString());
            System.out.println(new Gson().toJson(content));
            httpUtil.submit(Config.CONTENT_CREATE_URL, new Gson().toJson(content), new HttpResponseCallback() {
                public void onSuccess(String response) {
                    try {
                        Contents contents = new Gson().fromJson(response, Contents.class);

                        if (contents != null && contents.getContents().size() >= 20) {
                            CardActivity.contents = contents;
                        } else if (contents != null) {
                            CardActivity.contents.getContents().addAll(0, contents.getContents());
                        }

                        Intent intent = new Intent(getApplicationContext(), CardActivity.class);
                        startActivity(intent);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }

                @Override
                public void onFailure(String errorMsg) {
                    System.out.println("failure");
                }
            });
        }
    }

    private void success(String response) {
        Intent intent = new Intent(getApplicationContext(), CardActivity.class);
        startActivity(intent);
    }

    private void failure(View view, String errorMsg) {
        Snackbar.make(view, errorMsg, Snackbar.LENGTH_SHORT).show();
    }

    public class SpinnerCategoriesHandler implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<? > parent, View view, int position, long id) {

            if (contentURL != null && contentURL.getText().length() > 0) {
                if (isValidUrl(contentURL.getText().toString())) {
                    categoriesSpinner.setSelection(position);
                    content.setContentURL(contentURL.getText().toString());
                    content.setCategory(parent.getItemAtPosition(position).toString());
                } else {
                    categoriesSpinner.setSelection(0);
                    Snackbar.make(view, "Please insert a proper URL", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                categoriesSpinner.setSelection(0);
                Snackbar.make(view, "URL is empty", Snackbar.LENGTH_SHORT).show();
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            //refresh();
        } else if (id == android.R.id.home) {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public class SpinnnerSubCategoriesHandler implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<? > parent, View view, int position, long id) {

            if (contentURL != null && contentURL.getText().length() > 0) {
                if (content.getCategory() != null) {
                    subCategoriesSpinner.setSelection(position);
                    ArrayList<String> arrayList = new ArrayList<String>();
                    arrayList.add(parent.getItemAtPosition(position).toString());
                    content.setSubCategories(arrayList);
                    submitButton.setEnabled(true);
                } else {
                    subCategoriesSpinner.setSelection(0);
                    Snackbar.make(view, "Please select category first", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(view, "Please select URL and category first", Snackbar.LENGTH_SHORT).show();
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    }
}
