package com.humanize.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.humanize.android.common.Constants;
import com.humanize.android.HttpResponseCallback;
import com.humanize.android.JsonParser;
import com.humanize.android.R;
import com.humanize.android.common.StringConstants;
import com.humanize.android.content.data.Content;
import com.humanize.android.content.data.Contents;
import com.humanize.android.util.Config;
import com.humanize.android.util.HttpUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SubmitActivity extends AppCompatActivity {

    @Bind(R.id.relativeLayout) RelativeLayout relativeLayout;
    @Bind(R.id.contentUrl)  EditText contentURL;
    @Bind(R.id.submitButton) Button submitButton;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.categoriesSpinner) Spinner categoriesSpinner;
    @Bind(R.id.isVerified) CheckBox isVerified;
    //@Bind(R.id.subCategoriesSpinner) Spinner subCategoriesSpinner;

    private Content content;

    private static String TAG = SubmitActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    private void initialize() {
        content = new Content();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        categoriesSpinner.setOnItemSelectedListener(new SpinnerCategoriesHandler());
        //subCategoriesSpinner.setOnItemSelectedListener(new SpinnnerSubCategoriesHandler());

        ArrayAdapter<CharSequence> categoriesAdapter = ArrayAdapter.createFromResource(this, R.array.categories, R.layout.spinner_item);

        ArrayAdapter<CharSequence> subcategoriesAdapter = ArrayAdapter.createFromResource(this, R.array.subCategories, R.layout.spinner_item);

        categoriesAdapter.setDropDownViewResource(R.layout.spinner_categories);
        categoriesSpinner.setAdapter(categoriesAdapter);

        subcategoriesAdapter.setDropDownViewResource(R.layout.spinner_categories);
        //subCategoriesSpinner.setAdapter(subcategoriesAdapter);
        //categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void configureListeners() {
        contentURL.addTextChangedListener(new TextWatcher() {
            // after every change has been made to this editText, we would like to check validity
            public void afterTextChanged(Editable s) {
                if (contentURL.getError() != null) {
                    contentURL.setError(null);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

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

        categoriesSpinner.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        }) ;

        isVerified.setOnTouchListener(new View.OnTouchListener() {

                                          @Override
                                          public boolean onTouch(View v, MotionEvent event) {
                                              InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                              inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                              return false;
                                          }
                                      });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                String contentURLStr = contentURL.getText().toString();
                if (contentURLStr.isEmpty()) {
                    Snackbar.make(view, StringConstants.URL_VALIDATION_ERROR_STR, Snackbar.LENGTH_LONG).show();
                } else {
                    submit(view);
                }
            }
        });
    }

    private boolean isValidUrl(String url) {
        if (url == null || url.length() == 0) {
            return false;
        }

        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url);
        return m.matches();
    }

    private void submit(final View view) {
        if (validate()) {
            content.setContentURL(contentURL.getText().toString());
            try {
                HttpUtil.getInstance().submit(Config.CONTENT_CREATE_URL, new JsonParser().toJson(content), new CreateContentCallback());
            } catch (Exception exception) {
                Log.e(TAG, exception.toString());
                Snackbar.make(view, StringConstants.FAILURE_STR, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validate() {
        if (contentURL == null || contentURL.getText().toString().isEmpty() || !isValidUrl(contentURL.getText().toString())) {
            contentURL.setError(StringConstants.URL_VALIDATION_ERROR_STR);
            Snackbar.make(relativeLayout, StringConstants.URL_VALIDATION_ERROR_STR, Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (content.getCategory() == null || content.getCategory().isEmpty() || content.getCategory().equals("Select Category")) {
            Snackbar.make(relativeLayout, StringConstants.SELECT_CATEGORY, Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void success(String response) {
        Intent intent = new Intent(getApplicationContext(), CardActivity.class);
        startActivity(intent);
    }

    private void failure(View view, String errorMsg) {
        Snackbar.make(view, errorMsg, Snackbar.LENGTH_SHORT).show();
    }

    public void onCheckboxClicked(View view) {

    }

    public class SpinnerCategoriesHandler implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<? > parent, View view, int position, long id) {

            //if (contentURL != null && contentURL.getText().length() > 0) {
              //  if (isValidUrl(contentURL.getText().toString())) {
                    categoriesSpinner.setSelection(position);
                    content.setContentURL(contentURL.getText().toString());
                    content.setCategory(parent.getItemAtPosition(position).toString());
               // } else {
               //     categoriesSpinner.setSelection(0);
               //     Snackbar.make(view, "Please insert a proper URL", Snackbar.LENGTH_SHORT).show();
            //    }
           // } else {
            //    categoriesSpinner.setSelection(0);
             //   Snackbar.make(view, "URL is empty", Snackbar.LENGTH_SHORT).show();
            //}
        }

        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_submit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionRefresh) {
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
                    //subCategoriesSpinner.setSelection(position);
                    ArrayList<String> arrayList = new ArrayList<>();
                    arrayList.add(parent.getItemAtPosition(position).toString());
                    content.setSubCategories(arrayList);
                    submitButton.setEnabled(true);
                } else {
                    //subCategoriesSpinner.setSelection(0);
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

    private class CreateContentCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException exception) {
            Log.e(TAG, exception.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(relativeLayout, StringConstants.NETWORK_CONNECTION_ERROR_STR, Snackbar.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            if (!response.isSuccessful()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(relativeLayout, StringConstants.FAILURE_STR, Snackbar.LENGTH_LONG).show();
                    }
                });
            } else {
                final String responseStr = response.body().string().toString();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Snackbar.make(relativeLayout, StringConstants.SUCCESS_STR, Snackbar.LENGTH_LONG).show();
                            Contents contents = new JsonParser().fromJson(responseStr, Contents.class);

                            if (contents != null && contents.getContents().size() >= Constants.DEFAULT_CONTENTS_SIZE) {
                                CardActivity.contents = contents;
                            } else if (contents != null) {
                                CardActivity.contents.getContents().addAll(0, contents.getContents());
                            }

                            Intent intent = new Intent(getApplicationContext(), CardActivity.class);
                            startActivity(intent);
                        } catch (Exception exception) {
                            Log.e(TAG, exception.toString());
                        }
                    }
                });
            }
        }
    }
}
