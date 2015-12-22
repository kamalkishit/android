package com.humanize.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.humanize.android.R;
import com.humanize.android.common.StringConstants;
import com.humanize.android.content.data.Content;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecommendationsActivity extends AppCompatActivity {

    @Bind(R.id.toolbarText) TextView toolbarText;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        ButterKnife.bind(this);

        initialize();
    }

    private void initialize() {
        toolbar.setCollapsible(true);

        toolbarText.setText(StringConstants.RECOMMENDED);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        LikesAdapter paperAdapter = new LikesAdapter();
        recyclerView.setAdapter(paperAdapter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recommendations, menu);
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

    public static class LikesAdapter extends RecyclerView.Adapter<LikesAdapter.ViewHolder> {
        public static List<Content> contents = new ArrayList<>();
        public static int currentItem = 0;

        public LikesAdapter() {

        }

        @Override
        public int getItemCount() {
            return contents.size();
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int index) {
            Content content = contents.get(index);
            viewHolder.contentTitle.setText(content.getTitle());
            viewHolder.contentDescription.setText(content.getDescription());
            viewHolder.contentSource.setText(content.getSource());
            viewHolder.contentImage.setImageResource(R.drawable.background);
            viewHolder.contentImage.getLayoutParams().width = Config.IMAGE_WIDTH;
            viewHolder.contentImage.getLayoutParams().height = Config.IMAGE_HEIGHT;
            Picasso.with(ApplicationState.getAppContext()).load(Config.IMAGES_URL + content.getImageURL())
                    .placeholder(R.drawable.background)
                    .resize(Config.IMAGE_WIDTH, Config.IMAGE_HEIGHT).into(viewHolder.contentImage);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int index) {
            View cardView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.content_card, viewGroup, false);

            return new LikesAdapter.ViewHolder(cardView);
        }

        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            protected TextView contentTitle;
            protected TextView contentDescription;
            protected ImageView contentImage;
            protected TextView contentSource;

            public ViewHolder(View view) {
                super(view);
                contentTitle =  (TextView) view.findViewById(R.id.contentTitle);
                contentDescription = (TextView)  view.findViewById(R.id.contentDescription);
                contentImage = (ImageView) view.findViewById(R.id.contentImage);
                //contentSource = (TextView) view.findViewById(R.id.contentSource);
                view.setOnClickListener(this);
            }

            public void onClick(View view) {
                currentItem = getAdapterPosition();
                Intent intent = new Intent(ApplicationState.getAppContext(), WebBrowserActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ApplicationState.getAppContext().startActivity(intent);
            }
        }
    }
}
