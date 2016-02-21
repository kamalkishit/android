package com.humanize.android.activity;

import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.humanize.android.R;
import com.humanize.android.config.Config;
import com.humanize.android.config.Constants;
import com.humanize.android.data.Article;
import com.humanize.android.fragment.HomeFragment;
import com.humanize.android.helper.SpinnerItem;
import com.humanize.android.config.StringConstants;
import com.humanize.android.data.Content;
import com.humanize.android.data.Contents;
import com.humanize.android.helper.ActivityLauncher;
import com.humanize.android.service.ApiService;
import com.humanize.android.service.ApiServiceImpl;
import com.humanize.android.service.GsonParserServiceImpl;
import com.humanize.android.service.JsonParserService;
import com.humanize.android.service.SharedPreferencesService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CreateArticleActivity extends AppCompatActivity {

    @Bind(R.id.relativeLayout) RelativeLayout relativeLayout;
    @Bind(R.id.contentUrl)  EditText contentURL;
    @Bind(R.id.submitButton) Button submitButton;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.toolbarText) TextView toolbarText;
    @Bind(R.id.categoriesSpinner) Spinner categoriesSpinner;
    @Bind(R.id.isVerified) CheckBox isVerified;

    private JsonParserService jsonParserService;
    private ApiService apiService;
    private Article article;
    private SpinnerAdapter spinnerAdapter;

    private static String TAG = CreateArticleActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_article);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    private void initialize() {
        jsonParserService = new GsonParserServiceImpl();
        apiService = new ApiServiceImpl();
        article = new Article();

        toolbarText.setText(StringConstants.SUBMIT_ARTICLE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayList<SpinnerItem> list = new ArrayList<>();
        list.add(new SpinnerItem(StringConstants.ACHIEVERS));
        list.add(new SpinnerItem(StringConstants.BEAUTIFUL));
        list.add(new SpinnerItem(StringConstants.EDUCATION));
        list.add(new SpinnerItem(StringConstants.EMPOWERMENT));
        list.add(new SpinnerItem(StringConstants.ENVIRONMENT));
        list.add(new SpinnerItem(StringConstants.GOVERNANCE));
        list.add(new SpinnerItem(StringConstants.HEALTH));
        list.add(new SpinnerItem(StringConstants.HUMANITY));
        list.add(new SpinnerItem(StringConstants.LAW_AND_JUSTICE));
        list.add(new SpinnerItem(StringConstants.REAL_HEROES));
        list.add(new SpinnerItem(StringConstants.SCIENCE_AND_TECH));
        list.add(new SpinnerItem(StringConstants.SPORTS));
        list.add(new SpinnerItem(StringConstants.SELECT_CATEGORY));

        spinnerAdapter = new SpinnerAdapter(this, R.layout.spinner_item, R.id.textView, list);
        categoriesSpinner.setAdapter(spinnerAdapter);
        categoriesSpinner.setSelection(spinnerAdapter.getCount());

        categoriesSpinner.setOnItemSelectedListener(new SpinnerCategoriesHandler());
    }

    private void configureListeners() {
        contentURL.addTextChangedListener(new TextWatcher() {
            // after every change has been made to this editText, we would like to check validity
            public void afterTextChanged(Editable s) {

                if (contentURL.getError() != null) {
                    contentURL.setError(null);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        contentURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        categoriesSpinner.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (contentURL == null || !isValidUrl(contentURL.getText().toString())) {
                    Snackbar.make(relativeLayout, StringConstants.URL_VALIDATION_ERROR_STR, Snackbar.LENGTH_LONG).show();
                }

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
                submit(view);
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

            try {
                apiService.createArticle(article, new CreateContentCallback());
            } catch (Exception exception) {
                Log.e(TAG, exception.toString());
                Snackbar.make(view, StringConstants.FAILURE_STR, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validate() {
        if (article.getUrl() == null || !isValidUrl(article.getUrl())) {
            contentURL.setError(StringConstants.URL_VALIDATION_ERROR_STR);
            Snackbar.make(relativeLayout, StringConstants.URL_VALIDATION_ERROR_STR, Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (article.getCategory() == null || article.getCategory().isEmpty() || article.getCategory().equals(StringConstants.SELECT_CATEGORY)) {
            Snackbar.make(relativeLayout, StringConstants.CATEGORY_NOT_SELECTED, Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (article.isVerified() == false) {
            Snackbar.make(relativeLayout, StringConstants.URL_NOT_VERIFIED, Snackbar.LENGTH_SHORT).show();
        }

        return true;
    }

    private void success(String response) {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }

    private void failure(View view, String errorMsg) {
        Snackbar.make(view, errorMsg, Snackbar.LENGTH_SHORT).show();
    }

    public void onCheckboxClicked(View view) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_submit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public class SpinnerCategoriesHandler implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            if (contentURL != null && isValidUrl(contentURL.getText().toString())) {
                categoriesSpinner.setSelection(position);
                article.setUrl(contentURL.getText().toString());
                SpinnerItem spinnerItem= (SpinnerItem)parent.getItemAtPosition(position);
                System.out.println(spinnerItem.getText());
                article.setCategory(spinnerItem.getText());
            } else {
                categoriesSpinner.setSelection(spinnerAdapter.getCount());
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    }

    private class CreateContentCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException exception) {
            Log.e(TAG, exception.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(relativeLayout, StringConstants.NETWORK_CONNECTION_ERROR_STR, Snackbar.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onResponse(Call call, final Response response) throws IOException {
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
                            Snackbar.make(relativeLayout, "SUCCESS", Snackbar.LENGTH_LONG).show();
                            Contents contents = jsonParserService.fromJson(responseStr, Contents.class);

                            if (contents != null && contents.getContents().size() >= Constants.DEFAULT_CONTENTS_SIZE) {
                                HomeFragment.contents = contents;
                            } else if (contents != null) {
                                String json = SharedPreferencesService.getInstance().getString(Config.JSON_HOME_CONTENTS);
                                Contents origContents = jsonParserService.fromJson(json, Contents.class);
                                origContents.getContents().addAll(0, contents.getContents());
                                HomeFragment.contents = origContents;
                            }

                            new ActivityLauncher().startHomeActivity();
                        } catch (Exception exception) {
                            Log.e(TAG, exception.toString());
                        }
                    }
                });
            }
        }
    }

    public class SpinnerAdapter extends ArrayAdapter<SpinnerItem> {

        int groupid;
        Activity context;
        ArrayList<SpinnerItem> list;
        LayoutInflater inflater;

        public SpinnerAdapter(Activity context, int groupid, int id, ArrayList<SpinnerItem> list) {
            super(context, id, list);
            this.list = list;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.groupid = groupid;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = inflater.inflate(groupid, parent, false);
            TextView textView = (TextView) itemView.findViewById(R.id.textView);
            textView.setText(list.get(position).getText());
            return itemView;
        }

        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getView(position, convertView, parent);
        }

        @Override
        public int getCount() {
            // don't display last item. It is used as hint.
            int count = super.getCount();
            return count > 0 ? count - 1 : count;
        }
    }
}
