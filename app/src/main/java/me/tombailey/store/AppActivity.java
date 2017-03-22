package me.tombailey.store;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import io.realm.Realm;
import me.tombailey.store.adapter.AdapterItemSelectedListener;
import me.tombailey.store.adapter.AppReviewListAdapter;
import me.tombailey.store.adapter.AppScreenshotListAdapter;
import me.tombailey.store.exception.ProxyNotRunningException;
import me.tombailey.store.http.Proxy;
import me.tombailey.store.model.App;
import me.tombailey.store.model.InstalledApp;
import me.tombailey.store.model.Review;
import me.tombailey.store.rx.service.AppService;
import me.tombailey.store.rx.service.HttpService;
import me.tombailey.store.service.AppDownloadService;
import me.tombailey.store.service.AppReviewService;
import me.tombailey.store.util.NavigationUtil;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by tomba on 30/11/2016.
 */

public class AppActivity extends AppCompatActivity {

    public static final String APP = "app";


    private StoreApp mStoreApp;

    private App mApp;

    private View mReviewsRecyclerView;
    private View mReviewsShowAllView;
    private View mReviewsProgressView;
    private View mReviewsErrorView;

    private Button mInstallOrUpdate;

    private Subscription mDownloadAppSubscription;
    private Subscription mDownloadIconSubscription;
    private Subscription mGetReviewsSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        mStoreApp = (StoreApp) getApplication();

        Intent intent = getIntent();
        mApp = intent.getParcelableExtra(APP);

        mReviewsRecyclerView = findViewById(R.id.app_activity_recycler_view_reviews);
        mReviewsShowAllView = findViewById(R.id.app_activity_text_view_show_all);
        mReviewsProgressView = findViewById(R.id.app_activity_progress_bar_reviews);
        mReviewsErrorView = findViewById(R.id.app_activity_linear_layout_reviews_error);

        init();
    }

    @Override
    protected void onDestroy() {
        if (mDownloadAppSubscription != null && !mDownloadAppSubscription.isUnsubscribed()) {
            mDownloadAppSubscription.unsubscribe();
        }
        if (mDownloadIconSubscription != null && !mDownloadIconSubscription.isUnsubscribed()) {
            mDownloadIconSubscription.unsubscribe();
        }
        if (mGetReviewsSubscription != null && !mGetReviewsSubscription.isUnsubscribed()) {
            mGetReviewsSubscription.unsubscribe();
        }
        super.onDestroy();
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
                NavigationUtil.goBackToHome(AppActivity.this);
            }
        });


        TextView tvName = (TextView) findViewById(R.id.app_activity_text_view_name);
        tvName.setText(mApp.getName());

        TextView tvDescription = (TextView) findViewById(R.id.app_activity_text_view_description);
        tvDescription.setText(mApp.getDescription());

        SimpleRatingBar srb = (SimpleRatingBar) findViewById(R.id.app_activity_simple_rating_bar);
        srb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View scrollView = findViewById(R.id.app_activity_scroll_view);
                scrollView.scrollTo(0, scrollView.getHeight());
            }
        });
        srb.setRating((float) mApp.getRating());


        mInstallOrUpdate = (Button) findViewById(R.id.app_activity_button_install);
        if (isAlreadyInstalled() && !isUpdateNeeded()) {
            mInstallOrUpdate.setText(R.string.app_activity_open);
            mInstallOrUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent launchIntent = getLaunchIntent();
                    if (launchIntent == null) {
                        Toast.makeText(AppActivity.this,
                                getString(R.string.app_activity_app_no_longer_installed),
                                Toast.LENGTH_SHORT).show();

                        mInstallOrUpdate.setText(isUpdateNeeded() ?
                                R.string.app_activity_update : R.string.app_activity_install);
                        mInstallOrUpdate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                downloadApp();
                            }
                        });
                    } else {
                        startActivity(launchIntent);
                    }
                }
            });
        } else {
            mInstallOrUpdate.setText(isUpdateNeeded() ?
                    R.string.app_activity_update : R.string.app_activity_install);
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

        showIcon();
        showScreenshots();
        getReviews();
    }

    private boolean isAlreadyInstalled() {
        //TODO: move realm off UI thread
        Realm realm = mStoreApp.getRealm();

        InstalledApp installedApp = realm.where(InstalledApp.class)
                .equalTo("mAppId", mApp.getId())
                .findFirst();

        boolean isInstalled = installedApp != null;
        realm.close();
        return isInstalled;
    }

    private Intent getLaunchIntent() {
        //TODO: move realm off UI thread
        Realm realm = mStoreApp.getRealm();

        InstalledApp installedApp = realm.where(InstalledApp.class)
                .equalTo("mAppId", mApp.getId())
                .findFirst();

        Intent launchIntent = null;
        if (installedApp != null) {
            launchIntent = getPackageManager().getLaunchIntentForPackage(installedApp.getAppId());
        }
        realm.close();
        return launchIntent;
    }

    private boolean isUpdateNeeded() {
        //TODO: move realm off UI thread
        Realm realm = mStoreApp.getRealm();

        InstalledApp installedApp = realm.where(InstalledApp.class)
                .equalTo("mAppId", mApp.getId())
                .findFirst();

        boolean updateNeeded = installedApp != null && installedApp.getVersionNumber() < mApp.getCurrentVersionNumber();
        realm.close();
        return updateNeeded;
    }

    private void downloadApp() {
        Toast.makeText(AppActivity.this, R.string.app_activity_app_downloading, Toast.LENGTH_LONG).show();

        Intent downloadAppInBackgroundIntent = new Intent();
        downloadAppInBackgroundIntent.setComponent(
                new ComponentName("me.tombailey.store", "me.tombailey.store.service.AppDownloadService"));
        downloadAppInBackgroundIntent.setAction(AppDownloadService.DOWNLOAD_APP);
        downloadAppInBackgroundIntent.putExtra(AppDownloadService.APP, mApp);
        startService(downloadAppInBackgroundIntent);
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
                        createReview(reviewDescription, reviewStars);
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void createReview(final String description, final int stars) {
        Toast.makeText(AppActivity.this, R.string.create_review_dialog_creating_review, Toast.LENGTH_LONG).show();

        Intent createReviewInBackgroundIntent = new Intent();
        createReviewInBackgroundIntent.setComponent(
                new ComponentName("me.tombailey.store", "me.tombailey.store.service.AppReviewService"));
        createReviewInBackgroundIntent.putExtra(AppReviewService.APP, mApp);
        createReviewInBackgroundIntent.putExtra(AppReviewService.DESCRIPTION, description);
        createReviewInBackgroundIntent.putExtra(AppReviewService.STARS, stars);
        startService(createReviewInBackgroundIntent);
    }

    private void showIcon() {
        if (mDownloadIconSubscription != null && !mDownloadIconSubscription.isUnsubscribed()) {
            mDownloadIconSubscription.unsubscribe();
        }

        final File iconFile = new File(getCacheDir(), "app" + File.separator + mApp.getId() +
                File.separator + "icon.png");
        iconFile.getParentFile().mkdirs();

        mDownloadIconSubscription = mStoreApp.subscribeForProxy().flatMap(new Func1<Proxy, Observable<File>>() {
            @Override
            public Observable<File> call(Proxy proxy) {
                if (proxy == null) {
                    throw new ProxyNotRunningException();
                } else {
                    return HttpService.download(proxy, mApp.getIconLink(), iconFile);
                }
            }
        }).subscribe(new Action1<File>() {
            @Override
            public void call(File file) {
                ImageView ivIcon = (ImageView) findViewById(R.id.app_activity_image_view_icon);
                Glide.with(AppActivity.this).load(file).into(ivIcon);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();

                //TODO: handle icon not loading
            }
        });
    }

    private void showScreenshots() {
        RecyclerView rvScreenshots = (RecyclerView) findViewById(R.id.app_activity_recycler_view_screenshots);
        rvScreenshots.setHasFixedSize(true);
        rvScreenshots.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        AppScreenshotListAdapter appScreenshotListAdapter = new AppScreenshotListAdapter(mApp,
            Glide.with(this), mStoreApp.getProxy(), mStoreApp.getTempCacheDirectory(),
            new AdapterItemSelectedListener<File>() {
                @Override
                public void onSelected(File screenshotFile) {
                    //TODO: allow fullscreen screenshots
                }
            });
        rvScreenshots.setAdapter(appScreenshotListAdapter);
    }

    private void getReviews() {
        if (mApp.getReviews().length == 0) {
            mReviewsRecyclerView.setVisibility(View.GONE);
            mReviewsShowAllView.setVisibility(View.GONE);
            mReviewsErrorView.setVisibility(View.GONE);
            mReviewsProgressView.setVisibility(View.VISIBLE);

            mGetReviewsSubscription = mStoreApp.subscribeForProxy()
                .flatMap(new Func1<Proxy, Observable<Review[]>>() {
                    @Override
                    public Observable<Review[]> call(Proxy proxy) {
                        return AppService.getReviewsForApp(proxy, mApp);
                    }
                }).subscribe(new Action1<Review[]>() {
                    @Override
                    public void call(Review[] reviews) {
                        mApp.setReviews(reviews);
                        showReviews();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();

                        TextView tvErrorDescription =
                                (TextView) mReviewsErrorView.findViewById(R.id.app_activity_text_view_reviews_error_description);
                        tvErrorDescription.setText(R.string.generic_error);

                        mReviewsRecyclerView.setVisibility(View.GONE);
                        mReviewsShowAllView.setVisibility(View.GONE);
                        mReviewsProgressView.setVisibility(View.GONE);
                        mReviewsErrorView.setVisibility(View.VISIBLE);
                    }
                });
        }
    }

    private void showReviews() {
        if (mApp.getReviews().length == 0) {
            TextView tvErrorDescription =
                    (TextView) mReviewsErrorView.findViewById(R.id.app_activity_text_view_reviews_error_description);
            tvErrorDescription.setText(R.string.app_activity_reviews_no_reviews);

            mReviewsRecyclerView.setVisibility(View.GONE);
            mReviewsShowAllView.setVisibility(View.GONE);
            mReviewsProgressView.setVisibility(View.GONE);
            mReviewsErrorView.setVisibility(View.VISIBLE);
        } else {
            RecyclerView rvReviews = (RecyclerView) findViewById(R.id.app_activity_recycler_view_reviews);
            rvReviews.setLayoutManager(new LinearLayoutManager(this));
            AppReviewListAdapter appReviewListAdapter = new AppReviewListAdapter(getReviewsPreview(),
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
    }

    private Review[] getReviewsPreview() {
        Review[] reviewsPreview = new Review[mApp.getReviews().length >= 2 ? 2 : mApp.getReviews().length];
        for (int index = 0; index < reviewsPreview.length; index++) {
            reviewsPreview[index] = mApp.getReviews()[index];
        }
        return reviewsPreview;
    }
}
