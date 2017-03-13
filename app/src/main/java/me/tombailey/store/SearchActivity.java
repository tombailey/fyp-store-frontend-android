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

import me.tombailey.store.adapter.AdapterItemSelectedListener;
import me.tombailey.store.adapter.AppListAdapter;
import me.tombailey.store.http.Proxy;
import me.tombailey.store.model.App;
import me.tombailey.store.rx.service.AppService;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by tomba on 30/11/2016.
 */

public class SearchActivity extends AppCompatActivity {

    public static final String KEYWORDS = "keywords";


    private StoreApp mStoreApp;

    private View mProgressView;
    private RecyclerView mAppsView;
    private View mNoAppsView;
    private View mErrorView;


    private String mKeywords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mStoreApp = (StoreApp) getApplication();

        mProgressView = findViewById(R.id.search_activity_progress_bar);
        mAppsView = (RecyclerView) findViewById(R.id.search_activity_recycler_view);
        mNoAppsView = findViewById(R.id.search_activity_text_view_no_apps);
        mErrorView = findViewById(R.id.search_activity_linear_layout_error);

        mKeywords = getIntent().getStringExtra(KEYWORDS);

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


        search();
    }

    private void search() {
        mStoreApp.subscribeForProxy().flatMap(new Func1<Proxy, Observable<App[]>>() {
            @Override
            public Observable<App[]> call(Proxy proxy) {
                return AppService.getAppsUsingSearch(proxy, mKeywords, 1);
            }
        }).subscribe(new Action1<App[]>() {
            @Override
            public void call(App[] apps) {
                mProgressView.setVisibility(View.GONE);

                if (apps.length == 0) {
                    mAppsView.setVisibility(View.GONE);
                    mNoAppsView.setVisibility(View.VISIBLE);
                } else {
                    mAppsView.setHasFixedSize(true);

                    int columns = getResources().getConfiguration().screenWidthDp / 190;
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchActivity.this, columns);
                    mAppsView.setLayoutManager(gridLayoutManager);

                    AppListAdapter appListAdapter = new AppListAdapter(apps, Glide.with(SearchActivity.this),
                            mStoreApp.getProxy(), mStoreApp.getTempCacheDirectory(),
                            new AdapterItemSelectedListener<App>() {
                                @Override
                                public void onSelected(App item) {
                                    Intent appActivity = new Intent(SearchActivity.this, AppActivity.class);
                                    appActivity.putExtra(AppActivity.APP, item);
                                    startActivity(appActivity);
                                }
                            });
                    mAppsView.setAdapter(appListAdapter);

                    mNoAppsView.setVisibility(View.GONE);
                    mAppsView.setVisibility(View.VISIBLE);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                mProgressView.setVisibility(View.GONE);
                mAppsView.setVisibility(View.GONE);
                mNoAppsView.setVisibility(View.GONE);
                mErrorView.setVisibility(View.VISIBLE);

                throwable.printStackTrace();
            }
        });
    }

}
