package me.tombailey.store;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import me.tombailey.store.http.Proxy;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusAppCompatActivity;

/**
 * Created by tomba on 08/02/2017.
 */

@RequiresPresenter(ProxyStatusPresenter.class)
public class ProxyStatusActivity extends NucleusAppCompatActivity<ProxyStatusPresenter> {

    private View mContent;
    private View mProgress;
    private View mError;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy_status);

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
                new AlertDialog.Builder(ProxyStatusActivity.this)
                        .setTitle(R.string.proxy_status_activity_dialog_title)
                        .setMessage(R.string.proxy_status_activity_dialog_description)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StoreApp.getInstance().stopProxy();

                                mProgress.setVisibility(View.VISIBLE);
                                mContent.setVisibility(View.GONE);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        init();
    }

    public void init() {
        mProgress.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.GONE);
    }

    public void showStatus(Proxy proxy) {
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

    public void showError(Throwable throwable) {
        mError.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.GONE);
        mProgress.setVisibility(View.GONE);

        TextView tvError = (TextView) mError.findViewById(R.id.activity_proxy_status_text_view_error_description);
        tvError.setText(R.string.generic_error);
        mError.findViewById(R.id.activity_proxy_status_button_error_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPresenter().start(ProxyStatusPresenter.PROXY_UPDATES);
            }
        });
    }
}
