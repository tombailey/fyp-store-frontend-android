package me.tombailey.store;

import android.os.Bundle;

import me.tombailey.store.http.Proxy;
import me.tombailey.store.model.App;
import me.tombailey.store.rx.service.AppService;
import nucleus.presenter.RxPresenter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by tomba on 29/03/2017.
 */

public class UpdatesPresenter extends RxPresenter<UpdatesActivity> {

    public static final int CHECK_FOR_UPDATE = 1;


    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        final StoreApp storeApp = StoreApp.getInstance();
        restartableLatestCache(CHECK_FOR_UPDATE, new Func0<Observable<App[]>>() {
            @Override
            public Observable<App[]> call() {
                return storeApp.subscribeForProxy()
                        .flatMap(new Func1<Proxy, Observable<App[]>>() {
                            @Override
                            public Observable<App[]> call(Proxy proxy) {
                                return AppService.checkForUpdates(proxy, storeApp.getRealmConfiguration(),
                                        storeApp.getPackageManager());
                            }
                        }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        }, new Action2<UpdatesActivity, App[]>() {
            @Override
            public void call(UpdatesActivity updatesActivity, App[] apps) {
                if (apps.length == 0) {
                    updatesActivity.showNoUpdates();
                } else {
                    updatesActivity.showApps(apps);
                }
            }
        }, new Action2<UpdatesActivity, Throwable>() {
            @Override
            public void call(UpdatesActivity updatesActivity, Throwable throwable) {
                throwable.printStackTrace();
                updatesActivity.showError(throwable);
            }
        });

        if (savedState == null) {
            start(CHECK_FOR_UPDATE);
        }
    }

    public void checkForUpdates() {
        start(CHECK_FOR_UPDATE);
    }

}
