package me.tombailey.store;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import me.tombailey.store.http.Cache;
import me.tombailey.store.http.Proxy;
import me.tombailey.store.http.Request;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.ReplaySubject;

/**
 * Created by tomba on 04/02/2017.
 */

public class StoreApp extends Application {

    private static final String LOG_TAG = StoreApp.class.getName();


    private RealmConfiguration mRealmConfiguration;

    private BroadcastReceiver mProxyBroadReceiver;

    private Proxy mLatestProxy;
    private ReplaySubject<Proxy> mProxyReplaySubject;
    private BehaviorSubject<Proxy> mProxyBehaviorSubject;

    @Override
    public void onCreate() {
        super.onCreate();

        setupRealm();

        setupTempCacheDirectory();
        setupRequestCacheDirectory();

        subscribeForProxyUpdates();
        startProxy();
    }

    private void setupRealm() {
        Realm.init(this);
        mRealmConfiguration = new RealmConfiguration.Builder()
                .modules(Realm.getDefaultModule(), new RealmAppModule())
                .name("me.tombailey.store")
                .build();
    }

    public Realm getRealm() {
        return Realm.getInstance(mRealmConfiguration);
    }

    public RealmConfiguration getRealmConfiguration() {
        return mRealmConfiguration;
    }

    /**
     *
     * @return
     */
    public File getTempCacheDirectory() {
        return new File(getCacheDir(), "temp");
    }

    protected void setupTempCacheDirectory() {
        File tempCacheDir = getTempCacheDirectory();
        if (tempCacheDir.exists()) {
            tempCacheDir.delete();
        }
        tempCacheDir.mkdirs();
    }

    /**
     * Get the cache directory for HTTP request data
     * @return the cache directory for HTTP request data
     */
    public File getRequestCacheDirectory() {
        return new File(getCacheDir(), "requests");
    }

    protected void setupRequestCacheDirectory() {
        File requestCacheDir = getRequestCacheDirectory();
        if (!requestCacheDir.exists()) {
            requestCacheDir.mkdirs();
        }

        Request.setCache(new Cache.Builder()
                .cacheDirectory(requestCacheDir)
                .context(this)
                //20mb
                .maxSize(20 * 1024 * 1024)
                .build());
    }

    /**
     * Get the latest instance of the proxy. Can be null if the proxy has stopped
     * @return the latest instance of the proxy
     */
    public Proxy getProxy() {
        return mLatestProxy;
    }

    /**
     * Get a subscription for the latest instance of the proxy. The latest proxy value may be null
     * if the proxy has stopped
     * @return a subscription for the latest instance of the proxy
     */
    public Observable<Proxy> subscribeForProxy() {
        return mProxyBehaviorSubject.take(1);
    }

    public Observable<Proxy> subscribeForContinuousProxyUpdates() {
        return mProxyBehaviorSubject;
    }

    protected void subscribeForProxyUpdates() {
        IntentFilter proxyIntentFilter = new IntentFilter();
        proxyIntentFilter.addAction("me.tombailey.store.PROXY_STATUS_UPDATE");

        if (mProxyBroadReceiver == null) {
            mProxyReplaySubject = ReplaySubject.create(1);
            mProxyBehaviorSubject = BehaviorSubject.create();
            mProxyReplaySubject.subscribe(mProxyBehaviorSubject);

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
                            mLatestProxy = new Proxy(host, port);
                        } else {
                            mLatestProxy = null;
                        }
                        mProxyReplaySubject.onNext(mLatestProxy);
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
