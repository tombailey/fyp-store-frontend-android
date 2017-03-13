package me.tombailey.store;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import me.tombailey.store.fragment.FeaturedAppListFragment;
import me.tombailey.store.model.Category;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            AlertDialog searchDialog = new SearchDialog.Builder(this)
                .searchClicked(new SearchDialog.SearchListener() {
                    @Override
                    public void onSearch(String keywords) {
                        Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                        searchIntent.putExtra(SearchActivity.KEYWORDS, keywords);
                        startActivity(searchIntent);
                    }
                })
                .create();
            searchDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void init() {
        setupActionBar();
        setupTabLayout();
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(getTitle());

        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        //TODO: show proxy status menu item

        final DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.activity_main_navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    dl.closeDrawers();
                } else if (item.getItemId() == R.id.nav_updates) {
                    startActivity(new Intent(MainActivity.this, UpdatesActivity.class));
                } else if (item.getItemId() == R.id.nav_settings) {
                    startActivity(new Intent(MainActivity.this, AboutActivity.class));
                } else {
                    return false;
                }

                return true;
            }
        });


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
