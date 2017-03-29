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
 * Created by tomba on 30/11/2016.
 */

@RequiresPresenter(SearchPresenter.class)
public class SearchActivity extends NucleusAppCompatActivity<SearchPresenter> {

    public static final String KEYWORDS = "keywords";


    private View mProgressView;
    private RecyclerView mAppsView;
    private View mNoAppsView;
    private View mErrorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


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


        mProgressView = findViewById(R.id.search_activity_progress_bar);
        mAppsView = (RecyclerView) findViewById(R.id.search_activity_recycler_view);
        mNoAppsView = findViewById(R.id.search_activity_text_view_no_apps);

        mErrorView = findViewById(R.id.search_activity_linear_layout_error);
        mErrorView.findViewById(R.id.search_activity_button_error_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
                getPresenter().loadApps(getIntent().getStringExtra(KEYWORDS));
            }
        });


        init();
        getPresenter().loadApps(getIntent().getStringExtra(KEYWORDS));
    }

    private void init() {
        mAppsView.setVisibility(View.GONE);
        mNoAppsView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.VISIBLE);
    }

    public void showApps(App[] apps) {
        mAppsView.setHasFixedSize(true);

        int columns = getResources().getConfiguration().screenWidthDp / 190;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchActivity.this, columns);
        mAppsView.setLayoutManager(gridLayoutManager);

        StoreApp storeApp = StoreApp.getInstance();
        AppListAdapter appListAdapter = new AppListAdapter(apps, Glide.with(SearchActivity.this),
                storeApp.getProxy(), storeApp.getTempCacheDirectory(),
                new AdapterItemSelectedListener<App>() {
                    @Override
                    public void onSelected(App item) {
                        Intent appActivity = new Intent(SearchActivity.this, AppActivity.class);
                        appActivity.putExtra(AppActivity.APP, item);
                        startActivity(appActivity);
                    }
                });
        mAppsView.setAdapter(appListAdapter);


        mProgressView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
        mNoAppsView.setVisibility(View.GONE);
        mAppsView.setVisibility(View.VISIBLE);
    }

    public void showNoApps() {
        mProgressView.setVisibility(View.GONE);
        mAppsView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
        mNoAppsView.setVisibility(View.VISIBLE);
    }

    public void showError(Throwable throwable) {
        mProgressView.setVisibility(View.GONE);
        mAppsView.setVisibility(View.GONE);
        mNoAppsView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
    }

}
