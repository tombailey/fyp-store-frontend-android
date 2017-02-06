package me.tombailey.store;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by tomba on 30/11/2016.
 */

public class SearchActivity extends AppCompatActivity {

    public static final String KEYWORDS = "keywords";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        String keywords = getIntent().getStringExtra(KEYWORDS);
        init(keywords);
    }

    private void init(String keywords) {
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





        //TODO: impl
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        AppListFragment appListFragment = AppListFragment.newInstance(apps);
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.search_activity_frame_layout_fragment, appListFragment, AppListFragment.TAG);
//        fragmentTransaction.commitAllowingStateLoss();
    }

}
