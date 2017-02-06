package me.tombailey.store;

import android.app.Application;

import me.tombailey.store.http.Proxy;
import rx.subjects.ReplaySubject;

/**
 * Created by tomba on 04/02/2017.
 */

public class StoreApp extends Application {

    private ReplaySubject<Proxy> mProxyReplaySubject;

    @Override
    public void onCreate() {
        super.onCreate();

        mProxyReplaySubject = ReplaySubject.create();
    }

    public ReplaySubject<Proxy> getProxyReplaySubject() {
        return mProxyReplaySubject;
    }
}
