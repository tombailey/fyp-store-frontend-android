package me.tombailey.store;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bumptech.glide.Glide;

import me.tombailey.store.adapter.AdapterItemSelectedListener;
import me.tombailey.store.adapter.AppListAdapter;
import me.tombailey.store.model.App;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusAppCompatActivity;

/**
 * Created by tomba on 23/02/2017.
 */

@RequiresPresenter(UpdatesPresenter.class)
public class UpdatesActivity extends NucleusAppCompatActivity<UpdatesPresenter> {

    private RecyclerView mRecyclerView;
    private View mNoUpdatesView;
    private View mProgressView;
    private View mErrorView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updates);


        mRecyclerView = (RecyclerView) findViewById(R.id.update_activity_recycler_view);
        mNoUpdatesView = findViewById(R.id.update_activity_linear_layout_no_updates);
        mNoUpdatesView.findViewById(R.id.update_activity_button_no_updates_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
                getPresenter().checkForUpdates();
            }
        });

        mProgressView = findViewById(R.id.update_activity_linear_layout_progress);
        mErrorView = findViewById(R.id.update_activity_linear_layout_error);
        mErrorView.findViewById(R.id.update_activity_button_error_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
                getPresenter().checkForUpdates();
            }
        });


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


        init();
    }

    public void init() {
        mErrorView.setVisibility(View.GONE);
        mNoUpdatesView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.VISIBLE);
    }

    public void showApps(App[] apps) {
        StoreApp storeApp = StoreApp.getInstance();

        AppListAdapter appListAdapter = new AppListAdapter(apps, Glide.with(UpdatesActivity.this),
                storeApp.getProxy(), storeApp.getTempCacheDirectory(),
                new AdapterItemSelectedListener<App>() {
                    @Override
                    public void onSelected(App app) {
                        Intent appIntent = new Intent(UpdatesActivity.this, AppActivity.class);
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

    public void showNoUpdates() {
        mProgressView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
        mNoUpdatesView.setVisibility(View.VISIBLE);
    }

    public void showError(Throwable throwable) {
        mProgressView.setVisibility(View.GONE);
        mNoUpdatesView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
    }

}
