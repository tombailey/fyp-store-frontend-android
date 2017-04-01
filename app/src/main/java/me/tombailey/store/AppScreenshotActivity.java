package me.tombailey.store;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

import me.tombailey.store.util.NavigationUtil;

/**
 * Created by tomba on 09/03/2017.
 */

public class AppScreenshotActivity extends AppCompatActivity {

    public static final String SCREENSHOT_PATH = "screenshot path";


    private File mScreenshotFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_screenshot);

        mScreenshotFile = new File(getIntent().getStringExtra(SCREENSHOT_PATH));

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
                NavigationUtil.goBackToHome(AppScreenshotActivity.this);
            }
        });

        showScreenshot();
    }

    private void showScreenshot() {
        ImageView ivScreenshot = (ImageView) findViewById(R.id.app_screenshot_activity_image_view_screenshot);
        Glide.with(this).load(mScreenshotFile).into(ivScreenshot);
    }


}
