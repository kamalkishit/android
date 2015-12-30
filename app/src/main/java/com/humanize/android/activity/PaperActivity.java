package com.humanize.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.humanize.android.utils.StringConstants;
import com.humanize.android.data.Content;
import com.humanize.android.utils.ApplicationState;
import com.humanize.android.utils.Config;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PaperActivity extends AppCompatActivity {

    @Bind(R.id.toolbarText) TextView toolbarText;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    private void initialize() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        PaperAdapter paperAdapter = new PaperAdapter();
        recyclerView.setAdapter(paperAdapter);

        toolbar.setCollapsible(true);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarText.setText(StringConstants.PAPER);
    }

    private void configureListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_paper, menu);
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

    private void refresh() {
    }

    public static class PaperAdapter extends RecyclerView.Adapter<PaperAdapter.ViewHolder> {
        public static List<Content> contents = new ArrayList<>();
        public static int currentItem = 0;

        public PaperAdapter() {

        }

        @Override
        public int getItemCount() {
            return contents.size();
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int index) {
            Content content = contents.get(index);
            viewHolder.title.setText(content.getTitle());
            viewHolder.description.setText(content.getDescription());
            viewHolder.source.setText(content.getSource());
            viewHolder.imageView.getLayoutParams().width = Config.IMAGE_WIDTH;
            viewHolder.imageView.getLayoutParams().height = Config.IMAGE_HEIGHT;
            Picasso.with(ApplicationState.getAppContext()).load(Config.IMAGES_URL + content.getImageURL())
                    .placeholder(R.drawable.background)
                    .resize(Config.IMAGE_WIDTH, Config.IMAGE_HEIGHT).into(viewHolder.imageView);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int index) {
            View cardView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.content_card, viewGroup, false);

            return new PaperAdapter.ViewHolder(cardView);
        }

        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            protected TextView title;
            protected TextView description;
            protected ImageView imageView;
            protected TextView source;

            public ViewHolder(View view) {
                super(view);
                title =  (TextView) view.findViewById(R.id.contentTitle);
                description = (TextView)  view.findViewById(R.id.contentDescription);
                imageView = (ImageView) view.findViewById(R.id.contentImage);
                //source = (TextView) view.findViewById(R.id.contentSource);
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
