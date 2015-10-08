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
import com.humanize.android.data.Content;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LikesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Likes");
        toolbar.setCollapsible(true);

        TextView textView = (TextView) findViewById(R.id.toolbar_text_view);
        textView.setText("Likes");

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
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
        getMenuInflater().inflate(R.menu.menu_likes, menu);
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

    public static class LikesAdapter extends RecyclerView.Adapter<LikesAdapter.ViewHolder> {
        public static ArrayList<Content> contents = new ArrayList<Content>();
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
            viewHolder.title.setText(content.getTitle());
            viewHolder.description.setText(content.getDescription());
            viewHolder.source.setText(content.getSource());
            viewHolder.imageView.setImageResource(R.drawable.background);
            viewHolder.imageView.getLayoutParams().width = Config.IMAGE_WIDTH;
            viewHolder.imageView.getLayoutParams().height = Config.IMAGE_HEIGHT;
            Picasso.with(ApplicationState.getAppContext()).load(Config.SERVER_URL + "/images/" + content.getImageURL())
                    .placeholder(R.drawable.background)
                    .resize(Config.IMAGE_WIDTH, Config.IMAGE_HEIGHT).into(viewHolder.imageView);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int index) {
            View cardView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.content_card, viewGroup, false);

            return new LikesAdapter.ViewHolder(cardView);
        }

        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            protected TextView title;
            protected TextView description;
            protected ImageView imageView;
            protected TextView source;

            public ViewHolder(View view) {
                super(view);
                title =  (TextView) view.findViewById(R.id.content_title);
                description = (TextView)  view.findViewById(R.id.content_description);
                imageView = (ImageView) view.findViewById(R.id.content_image);
                source = (TextView) view.findViewById(R.id.content_source);
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

