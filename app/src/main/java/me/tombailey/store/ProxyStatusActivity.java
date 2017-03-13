package me.tombailey.store;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import me.tombailey.store.http.Proxy;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by tomba on 08/02/2017.
 */

public class ProxyStatusActivity extends AppCompatActivity {

    private StoreApp mStoreApp;

    private View mContent;
    private View mProgress;
    private View mError;


    private Subscription mProxySubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy_status);

        init();
    }

    @Override
    protected void onDestroy() {
        if (mProxySubscription != null && !mProxySubscription.isUnsubscribed()) {
            mProxySubscription.unsubscribe();
        }

        super.onDestroy();
    }

    private void init() {
        mStoreApp = (StoreApp) getApplication();

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


        mContent = findViewById(R.id.activity_proxy_status_linear_layout_content);
        mProgress = findViewById(R.id.activity_proxy_status_progress_bar);
        mError = findViewById(R.id.activity_proxy_status_linear_layout_error);

        findViewById(R.id.activity_proxy_status_button_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStoreApp.stopProxy();

                mProgress.setVisibility(View.VISIBLE);
                mContent.setVisibility(View.GONE);
            }
        });


        displayProxyStatus();
    }

    private void displayProxyStatus() {
        mProgress.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.GONE);


        if (mProxySubscription == null) {
            mProxySubscription = mStoreApp.subscribeForContinuousProxyUpdates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Proxy>() {
                    @Override
                    public void call(Proxy proxy) {
                        showStatus(proxy);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        showError();
                    }
                });
        }

        mStoreApp.queryProxyStatus();
    }

    private void showStatus(Proxy proxy) {
        TextView tvStatus = (TextView) mContent.findViewById(R.id.activity_proxy_status_text_view_status);
        View btnStop = mContent.findViewById(R.id.activity_proxy_status_button_stop);
        if (proxy == null) {
            tvStatus.setText(R.string.proxy_status_activity_proxy_is_not_running);
            btnStop.setVisibility(View.GONE);
        } else {
            tvStatus.setText(R.string.proxy_status_activity_proxy_is_running);
            btnStop.setVisibility(View.VISIBLE);
        }

        mProgress.setVisibility(View.GONE);
        mContent.setVisibility(View.VISIBLE);
    }

    private void showError() {
        mError.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.GONE);
        mProgress.setVisibility(View.GONE);

        TextView tvError = (TextView) mError.findViewById(R.id.activity_proxy_status_text_view_error_description);
        tvError.setText(R.string.generic_error);
        mError.findViewById(R.id.activity_proxy_status_button_error_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayProxyStatus();
            }
        });
    }
}
