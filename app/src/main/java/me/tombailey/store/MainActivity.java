package me.tombailey.store;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import me.tombailey.store.fragment.FeaturedAppListFragment;
import me.tombailey.store.http.Proxy;
import me.tombailey.store.model.Category;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver mProxyBroadReceiver;
    private PublishSubject<Intent> mProxyStatusUpdates;
    private PublishSubject<Proxy> mProxyUpdates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    protected void onDestroy() {
        unregisterForProxyUpdates();
        super.onDestroy();
    }

    private void init() {
        setupActionBar();
        setupTabLayout();
        setupProxy();
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(getTitle());

        DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        ActionBarDrawerToggle abdt = new ActionBarDrawerToggle(this, dl, toolbar, R.string.app_name, R.string.app_name);
        dl.setDrawerListener(abdt);
        abdt.syncState();
    }

    private void setupTabLayout() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.main_activity_view_pager);
        FragmentStatePagerAdapter fragmentStatePagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            private Category[] mCategories = new Category[]{
                Category.COMMUNICATION,
                Category.ENTERTAINMENT,
                Category.SHOPPING,
                Category.UTILITIES
            };

            private String[] mTitles = new String[]{
                    getString(R.string.main_activity_category_communication),
                    getString(R.string.main_activity_category_entertainment),
                    getString(R.string.main_activity_category_shopping),
                    getString(R.string.main_activity_category_utilities)
            };

            @Override
            public Fragment getItem(int position) {
                return FeaturedAppListFragment.newInstance(mCategories[position]);
            }

            @Override
            public int getCount() {
                return mCategories.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles[position];
            }
        };
        viewPager.setAdapter(fragmentStatePagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_activity_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupProxy() {
        startProxy();
        registerForProxyUpdates();
        registerForProxy();
    }

    private void startProxy() {
        Intent startTorConnectionService = new Intent();
        startTorConnectionService.setComponent(new ComponentName("me.tombailey.store", "me.tombailey.store.service.TorConnectionService"));
        startTorConnectionService.setAction("start");
        startService(startTorConnectionService);
    }

    private void registerForProxyUpdates() {
        mProxyStatusUpdates = PublishSubject.create();

        IntentFilter proxyIntentFilter = new IntentFilter();
        proxyIntentFilter.addAction("me.tombailey.store.PROXY_STATUS_UPDATE");
        mProxyBroadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("me.tombailey.store.PROXY_STATUS_UPDATE".equalsIgnoreCase(intent.getAction())) {
                    mProxyStatusUpdates.onNext(intent);
                }
            }
        };
        registerReceiver(mProxyBroadReceiver, proxyIntentFilter);
    }

    private void registerForProxy() {
        mProxyUpdates = PublishSubject.create();
        mProxyStatusUpdates.subscribe(new Action1<Intent>() {
            @Override
            public void call(Intent statusUpdate) {
                String status = statusUpdate.getStringExtra("status");

                Log.d(getClass().getName(), "proxy status is now '" + status + "'");
                if ("running".equalsIgnoreCase(status)) {
                    String host = statusUpdate.getStringExtra("host");
                    int port = statusUpdate.getIntExtra("port", 0);
                    Log.d(getClass().getName(), "proxy is running on " + host + ":" + port);

                    mProxyUpdates.onNext(new Proxy(host, port));
                }
            }
        });
    }

    private void unregisterForProxyUpdates() {
        unregisterReceiver(mProxyBroadReceiver);
    }
}
