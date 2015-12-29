package com.humanize.android.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.humanize.android.R;
import com.humanize.android.content.data.Content;
import com.humanize.android.util.ApplicationState;
import com.humanize.android.util.Config;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EditCategoriesActivity extends AppCompatActivity {

    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_categories);

        ButterKnife.bind(this);

        initialize();
        configureListeners();
    }

    private void initialize() {

    }

    private void configureListeners() {

    }

    class CategoriesRecyclerViewAdapter extends RecyclerView.Adapter<CategoriesRecyclerViewAdapter.CategoriesViewHolder> {

        @Override
        public void onBindViewHolder(CategoriesViewHolder viewHolder, int index) {

        }

        @Override
        public CategoriesViewHolder onCreateViewHolder(ViewGroup viewGroup, int index) {
            View cardView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_card, viewGroup, false);

            //return new ContentRecyclerViewAdapter.ContentViewHolder(cardView);
            return null;
        }

        @Override
        public int getItemCount() {
            return 0;
        }

        class CategoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public CategoriesViewHolder(View view) {
                super(view);
            }

            public void onClick(View view) {

            }
        }
    }

}
