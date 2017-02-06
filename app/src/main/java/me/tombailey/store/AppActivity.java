package me.tombailey.store;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import me.tombailey.store.adapter.AdapterItemSelectedListener;
import me.tombailey.store.adapter.AppReviewListAdapter;
import me.tombailey.store.adapter.AppScreenshotListAdapter;
import me.tombailey.store.model.App;
import me.tombailey.store.model.Review;

/**
 * Created by tomba on 30/11/2016.
 */

public class AppActivity extends AppCompatActivity {

    public static final String APP = "app";


    private App mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

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



        ImageView ivIcon = (ImageView) findViewById(R.id.app_activity_image_view_icon);
        TextView tvName = (TextView) findViewById(R.id.app_activity_text_view_name);
        SimpleRatingBar srb = (SimpleRatingBar) findViewById(R.id.app_activity_simple_rating_bar);


        TextView tvDescription = (TextView) findViewById(R.id.app_activity_text_view_description);
        tvDescription.setText(mApp.getDescription());


        Glide.with(this).load(mApp.getIconLink()).into(ivIcon);
        tvName.setText(mApp.getName());
        //TODO: add rating to server side elements?
//        srb.setRating(mApp.getRating());


        RecyclerView rvScreenshots = (RecyclerView) findViewById(R.id.app_activity_recycler_view_screenshots);
        rvScreenshots.setHasFixedSize(true);
        rvScreenshots.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        AppScreenshotListAdapter appScreenshotListAdapter = new AppScreenshotListAdapter(mApp.getScreenshotLinks(), Glide.with(this), new AdapterItemSelectedListener<String>() {
            @Override
            public void onSelected(String screenshotLink) {
                //TODO: allow fullscreen screenshots
            }
        });
        rvScreenshots.setAdapter(appScreenshotListAdapter);


        RecyclerView rvReviews = (RecyclerView) findViewById(R.id.app_activity_recycler_view_reviews);
        rvReviews.setHasFixedSize(true);
        rvReviews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        AppReviewListAdapter appReviewListAdapter = new AppReviewListAdapter(mApp.getReviews(), Glide.with(this), new AdapterItemSelectedListener<Review>() {
            @Override
            public void onSelected(Review review) {
                //TODO: show full review (full screen here?)
            }
        });
        rvReviews.setAdapter(appReviewListAdapter);


        //TODO: actual impl
        findViewById(R.id.app_activity_floating_action_button_install).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(AppActivity.this);
                progressDialog.setMessage("Installing " + mApp.getName() + "...");
                progressDialog.show();

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                            }
                        });
                    }
                }.start();
            }
        });
    }

}
