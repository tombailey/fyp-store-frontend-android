package me.tombailey.store;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.io.File;

import me.tombailey.store.adapter.AdapterItemSelectedListener;
import me.tombailey.store.adapter.AppReviewListAdapter;
import me.tombailey.store.adapter.AppScreenshotListAdapter;
import me.tombailey.store.model.App;
import me.tombailey.store.model.Review;
import me.tombailey.store.util.NavigationUtil;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusAppCompatActivity;

/**
 * Created by tomba on 30/11/2016.
 */

@RequiresPresenter(AppPresenter.class)
public class AppActivity extends NucleusAppCompatActivity<AppPresenter> {

    public static final String APP = "app";


    private App mApp;

    private View mReviewsRecyclerView;
    private View mReviewsShowAllView;
    private View mReviewsProgressView;
    private View mReviewsErrorView;

    private Button mInstallOrUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        Intent intent = getIntent();
        mApp = intent.getParcelableExtra(APP);


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
                NavigationUtil.goBackToHome(AppActivity.this);
            }
        });


        mReviewsRecyclerView = findViewById(R.id.app_activity_recycler_view_reviews);
        mReviewsShowAllView = findViewById(R.id.app_activity_text_view_show_all);
        mReviewsProgressView = findViewById(R.id.app_activity_progress_bar_reviews);
        mReviewsErrorView = findViewById(R.id.app_activity_linear_layout_reviews_error);


        getPresenter().showApp(mApp);
        getPresenter().loadIcon(mApp);
        getPresenter().loadReviews(mApp);
        showScreenshots();
    }

    public void showApp(String name, String description, double rating, final boolean isInstalled,
                        final boolean needsUpdate) {
        TextView tvName = (TextView) findViewById(R.id.app_activity_text_view_name);
        tvName.setText(name);

        TextView tvDescription = (TextView) findViewById(R.id.app_activity_text_view_description);
        tvDescription.setText(description);

        SimpleRatingBar srb = (SimpleRatingBar) findViewById(R.id.app_activity_simple_rating_bar);
        srb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View rvReviews = findViewById(R.id.app_activity_recycler_view_reviews);
                findViewById(R.id.app_activity_scroll_view)
                        .scrollTo(0, (int) rvReviews.getX() + rvReviews.getHeight());
            }
        });
        srb.setRating((float) rating);


        mInstallOrUpdate = (Button) findViewById(R.id.app_activity_button_install);
        if (isInstalled && !needsUpdate) {
            mInstallOrUpdate.setText(R.string.app_activity_open);
            mInstallOrUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getPresenter().openApp(mApp);
                }
            });
        } else {
            mInstallOrUpdate.setText(needsUpdate ? R.string.app_activity_update :
                    R.string.app_activity_install);
            mInstallOrUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downloadApp();
                }
            });
        }

        findViewById(R.id.app_activity_button_create_review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateReviewDialog();
            }
        });

        findViewById(R.id.app_activity_text_view_show_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reviewActivityIntent = new Intent(AppActivity.this, AppReviewActivity.class);
                reviewActivityIntent.putExtra(AppReviewActivity.APP, mApp);
                startActivity(reviewActivityIntent);
            }
        });
    }

    private void downloadApp() {
        Toast.makeText(AppActivity.this, R.string.app_activity_app_downloading, Toast.LENGTH_LONG).show();
        getPresenter().downloadApp(mApp);
    }

    public void showAppNotInstalled() {
        Toast.makeText(AppActivity.this, getString(R.string.app_activity_app_no_longer_installed),
                Toast.LENGTH_SHORT).show();

        mInstallOrUpdate.setText(R.string.app_activity_install);
        mInstallOrUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadApp();
            }
        });
    }

    private void showCreateReviewDialog() {
        final View createReviewView = getLayoutInflater().inflate(R.layout.create_review_dialog_view, null);

        new AlertDialog.Builder(this)
                .setTitle(R.string.create_review_dialog_title)
                .setView(createReviewView)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.app_activity_create_review, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String reviewDescription = ((EditText) createReviewView.findViewById(R.id.create_review_dialog_edit_text_description))
                                .getText().toString();
                        int reviewStars = (int) ((SimpleRatingBar) createReviewView.findViewById(R.id.create_review_dialog_rating_bar_stars))
                                .getRating();
                        getPresenter().createReview(mApp, reviewDescription, reviewStars);
                    }
                })
                .setCancelable(false)
                .show();
    }

    public void showIcon(File iconFile) {
        ImageView ivIcon = (ImageView) findViewById(R.id.app_activity_image_view_icon);
        Glide.with(AppActivity.this).load(iconFile).into(ivIcon);
    }

    private void showScreenshots() {
        RecyclerView rvScreenshots = (RecyclerView) findViewById(R.id.app_activity_recycler_view_screenshots);
        rvScreenshots.setHasFixedSize(true);
        rvScreenshots.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        StoreApp storeApp = StoreApp.getInstance();
        AppScreenshotListAdapter appScreenshotListAdapter = new AppScreenshotListAdapter(mApp,
                Glide.with(this), storeApp.getProxy(), storeApp.getTempCacheDirectory(),
                new AdapterItemSelectedListener<File>() {
                    @Override
                    public void onSelected(File screenshotFile) {
                        Intent appScreenshotIntent = new Intent(AppActivity.this, AppScreenshotActivity.class);
                        appScreenshotIntent.putExtra(AppScreenshotActivity.SCREENSHOT_PATH,
                                screenshotFile.getAbsolutePath());
                        startActivity(appScreenshotIntent);
                    }
                });
        rvScreenshots.setAdapter(appScreenshotListAdapter);
    }

    public void showReviewsError() {
        TextView tvErrorDescription =
                (TextView) mReviewsErrorView.findViewById(R.id.app_activity_text_view_reviews_error_description);
        tvErrorDescription.setText(R.string.generic_error);

        mReviewsRecyclerView.setVisibility(View.GONE);
        mReviewsShowAllView.setVisibility(View.GONE);
        mReviewsProgressView.setVisibility(View.GONE);
        mReviewsErrorView.setVisibility(View.VISIBLE);
    }

    public void showNoReviews() {
        TextView tvErrorDescription =
                (TextView) mReviewsErrorView.findViewById(R.id.app_activity_text_view_reviews_error_description);
        tvErrorDescription.setText(R.string.app_activity_reviews_no_reviews);

        mReviewsRecyclerView.setVisibility(View.GONE);
        mReviewsShowAllView.setVisibility(View.GONE);
        mReviewsProgressView.setVisibility(View.GONE);
        mReviewsErrorView.setVisibility(View.VISIBLE);
    }

    public void showReviews(Review[] reviews) {
        mApp.setReviews(reviews);

        RecyclerView rvReviews = (RecyclerView) findViewById(R.id.app_activity_recycler_view_reviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        AppReviewListAdapter appReviewListAdapter = new AppReviewListAdapter(reviews,
                new AdapterItemSelectedListener<Review>() {
                    @Override
                    public void onSelected(Review review) {
                        Intent reviewActivityIntent = new Intent(AppActivity.this, AppReviewActivity.class);
                        reviewActivityIntent.putExtra(AppReviewActivity.APP, mApp);
                        startActivity(reviewActivityIntent);
                    }
                });
        rvReviews.setAdapter(appReviewListAdapter);

        mReviewsProgressView.setVisibility(View.GONE);
        mReviewsErrorView.setVisibility(View.GONE);
        mReviewsRecyclerView.setVisibility(View.VISIBLE);
        mReviewsShowAllView.setVisibility(View.VISIBLE);
    }

    public void showReviewBeingCreated() {
        Toast.makeText(AppActivity.this, R.string.create_review_dialog_creating_review, Toast.LENGTH_LONG).show();
    }
}
