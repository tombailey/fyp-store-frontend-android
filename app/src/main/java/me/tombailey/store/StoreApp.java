package me.tombailey.store;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import me.tombailey.store.http.Proxy;
import rx.subjects.ReplaySubject;

/**
 * Created by tomba on 04/02/2017.
 */

public class StoreApp extends Application {

    private static final String LOG_TAG = StoreApp.class.getName();


    private BroadcastReceiver mProxyBroadReceiver;

    private ReplaySubject<Proxy> mProxyReplaySubject;

    @Override
    public void onCreate() {
        super.onCreate();

        mProxyReplaySubject = ReplaySubject.create(1);

        subscribeForProxyUpdates();
        startProxy();
    }

    public ReplaySubject<Proxy> getProxyReplaySubject() {
        return mProxyReplaySubject;
    }

    public void subscribeForProxyUpdates() {
        IntentFilter proxyIntentFilter = new IntentFilter();
        proxyIntentFilter.addAction("me.tombailey.store.PROXY_STATUS_UPDATE");

        if (mProxyBroadReceiver != null) {
            mProxyBroadReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent statusUpdate) {
                    if ("me.tombailey.store.PROXY_STATUS_UPDATE".equalsIgnoreCase(statusUpdate.getAction())) {
                        String status = statusUpdate.getStringExtra("status");
                        Log.d(LOG_TAG, "proxy status is now '" + status + "'");

                        if ("running".equalsIgnoreCase(status)) {
                            String host = statusUpdate.getStringExtra("host");
                            int port = statusUpdate.getIntExtra("port", 0);

                            Log.d(LOG_TAG, "proxy is running on " + host + ":" + port);

                            getProxyReplaySubject().onNext(new Proxy(host, port));
                        } else {
                            getProxyReplaySubject().onNext(null);
                        }
                    }
                }
            };
            registerReceiver(mProxyBroadReceiver, proxyIntentFilter);
        }
    }

    public void startProxy() {
        Intent startTorConnectionService = new Intent();
        startTorConnectionService.setComponent(new ComponentName("me.tombailey.store", "me.tombailey.store.service.TorConnectionService"));
        startTorConnectionService.setAction("start");
        startService(startTorConnectionService);
    }

    public void stopProxy() {
        Intent stopProxy = new Intent();
        stopProxy.setComponent(new ComponentName("me.tombailey.store", "me.tombailey.store.service.TorConnectionService"));
        stopProxy.setAction("stop");
        startService(stopProxy);
    }

    public void queryProxyStatus() {
        Intent proxyQuery = new Intent();
        proxyQuery.setComponent(new ComponentName("me.tombailey.store", "me.tombailey.store.service.TorConnectionService"));
        proxyQuery.setAction("status");
        startService(proxyQuery);
    }
}
