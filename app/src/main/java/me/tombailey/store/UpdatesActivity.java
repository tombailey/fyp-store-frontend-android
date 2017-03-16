package me.tombailey.store;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bumptech.glide.Glide;

import java.util.List;

import me.tombailey.store.adapter.AdapterItemSelectedListener;
import me.tombailey.store.adapter.AppListAdapter;
import me.tombailey.store.http.Proxy;
import me.tombailey.store.model.App;
import me.tombailey.store.rx.service.AppService;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by tomba on 23/02/2017.
 */

public class UpdatesActivity extends AppCompatActivity {

    private StoreApp mStoreApp;

    private RecyclerView mRecyclerView;
    private View mNoUpdatesView;
    private View mProgressView;
    private View mErrorView;

    private Subscription mCheckForUpdatesSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updates);


        mStoreApp = (StoreApp) getApplication();


        mRecyclerView = (RecyclerView) findViewById(R.id.update_activity_recycler_view);
        mNoUpdatesView = findViewById(R.id.update_activity_linear_layout_no_updates);
        mNoUpdatesView.findViewById(R.id.update_activity_button_no_updates_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForUpdates();
            }
        });

        mProgressView = findViewById(R.id.update_activity_progress_bar);
        mErrorView = findViewById(R.id.update_activity_progress_bar);


        init();
    }

    @Override
    protected void onDestroy() {
        if (mCheckForUpdatesSubscription != null && !mCheckForUpdatesSubscription.isUnsubscribed()) {
            mCheckForUpdatesSubscription.unsubscribe();
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
                onBackPressed();
            }
        });

        checkForUpdates();
    }

    private void checkForUpdates() {
        mErrorView.setVisibility(View.GONE);
        mNoUpdatesView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.VISIBLE);


        if (mCheckForUpdatesSubscription != null && !mCheckForUpdatesSubscription.isUnsubscribed()) {
            mCheckForUpdatesSubscription.unsubscribe();
        }
        mCheckForUpdatesSubscription = mStoreApp.subscribeForProxy()
            .flatMap(new Func1<Proxy, Observable<List<App>>>() {
                @Override
                public Observable<List<App>> call(Proxy proxy) {
                    return AppService.checkForUpdates(proxy, mStoreApp.getRealmConfiguration(),
                            getPackageManager());
                }
            }).subscribe(new Action1<List<App>>() {
                @Override
                public void call(List<App> apps) {
                    if (apps.size() == 0) {
                        mProgressView.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.GONE);
                        mErrorView.setVisibility(View.GONE);
                        mNoUpdatesView.setVisibility(View.VISIBLE);
                    } else {
                        AppListAdapter appListAdapter = new AppListAdapter(
                            apps.toArray(new App[apps.size()]),
                            Glide.with(UpdatesActivity.this), mStoreApp.getProxy(),
                            mStoreApp.getTempCacheDirectory(),
                            new AdapterItemSelectedListener<App>() {
                                @Override
                                public void onSelected(App app) {
                                    Intent appIntent =
                                            new Intent(UpdatesActivity.this, AppActivity.class);
                                    appIntent.putExtra(AppActivity.APP, app);
                                    startActivity(appIntent);
                                }
                            });

                        int columns = getResources().getConfiguration().screenWidthDp / 190;
                        GridLayoutManager gridLayoutManager =
                                new GridLayoutManager(UpdatesActivity.this, columns);
                        mRecyclerView.setLayoutManager(gridLayoutManager);
                        mRecyclerView.setAdapter(appListAdapter);

                        mProgressView.setVisibility(View.GONE);
                        mErrorView.setVisibility(View.GONE);
                        mNoUpdatesView.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    throwable.printStackTrace();

                    mNoUpdatesView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                    mProgressView.setVisibility(View.GONE);
                    mErrorView.setVisibility(View.VISIBLE);
                }
            });
    }

}
