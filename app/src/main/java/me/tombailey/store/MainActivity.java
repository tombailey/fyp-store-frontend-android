package me.tombailey.store;

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

import me.tombailey.store.fragment.FeaturedAppListFragment;
import me.tombailey.store.model.Category;

public class MainActivity extends AppCompatActivity {

    private StoreApp mStoreApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        mStoreApp = (StoreApp) getApplication();

        setupActionBar();
        setupTabLayout();
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
}
