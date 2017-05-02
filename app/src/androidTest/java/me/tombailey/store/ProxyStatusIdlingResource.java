package me.tombailey.store;

import android.support.test.espresso.IdlingResource;

import me.tombailey.store.http.Proxy;

/**
 * Created by tomba on 02/05/2017.
 */

public class ProxyStatusIdlingResource implements IdlingResource {

    private ResourceCallback mResourceCallback;

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        Proxy proxy = StoreApp.getInstance().getProxy();
        if (proxy != null) {
            mResourceCallback.onTransitionToIdle();
        }
        return proxy != null;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        mResourceCallback = callback;
    }
}
