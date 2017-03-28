package me.tombailey.store;

import android.os.Bundle;

import me.tombailey.store.http.Proxy;
import nucleus.presenter.RxPresenter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * Created by tomba on 28/03/2017.
 */

public class ProxyStatusPresenter extends RxPresenter<ProxyStatusActivity> {

    public static final int PROXY_UPDATES = 1;


    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        final StoreApp storeApp = StoreApp.getInstance();
        restartableLatestCache(PROXY_UPDATES, new Func0<Observable<Proxy>>() {
            @Override
            public Observable<Proxy> call() {
                return storeApp.subscribeForContinuousProxyUpdates()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        }, new Action2<ProxyStatusActivity, Proxy>() {
            @Override
            public void call(ProxyStatusActivity proxyStatusActivity, Proxy proxy) {
                proxyStatusActivity.showStatus(proxy);
            }
        }, new Action2<ProxyStatusActivity, Throwable>() {
            @Override
            public void call(ProxyStatusActivity proxyStatusActivity, Throwable throwable) {
                throwable.printStackTrace();
                proxyStatusActivity.showError(throwable);
            }
        });

        if (savedState == null) {
            start(PROXY_UPDATES);
        }
    }
}
