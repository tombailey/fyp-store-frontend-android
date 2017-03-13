package me.tombailey.store;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import me.tombailey.store.adapter.AppReviewListAdapter;
import me.tombailey.store.model.App;

/**
 * Created by tomba on 09/03/2017.
 */

public class AppReviewActivity extends AppCompatActivity {

    public static final String APP = "app";


    private App mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_reviews);

        mApp = getIntent().getParcelableExtra(APP);

        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(getTitle());

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        showReviews();
    }

    private void showReviews() {
        RecyclerView rvReviews = (RecyclerView) findViewById(R.id.app_review_activity_recycler_view_reviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        AppReviewListAdapter appReviewListAdapter = new AppReviewListAdapter(mApp.getReviews());
        rvReviews.setAdapter(appReviewListAdapter);
    }


}
