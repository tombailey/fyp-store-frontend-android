package me.tombailey.store.fragment;

import android.os.Bundle;

import java.util.concurrent.TimeUnit;

import me.tombailey.store.StoreApp;
import me.tombailey.store.exception.NoAppsException;
import me.tombailey.store.exception.ProxyNotRunningException;
import me.tombailey.store.http.Proxy;
import me.tombailey.store.model.App;
import me.tombailey.store.model.Category;
import me.tombailey.store.rx.service.AppService;
import nucleus.presenter.RxPresenter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static me.tombailey.store.fragment.CategoryAppListFragment.CATEGORY;

/**
 * Created by tomba on 26/03/2017.
 */

public class CategoryAppListPresenter extends RxPresenter<CategoryAppListFragment> {

    public static final int LOAD_APPS = 1;


    private Category mCategory;

    @Override
    protected void onCreate(final Bundle savedState) {
        super.onCreate(savedState);

        if (savedState != null) {
            mCategory = Category.valueOf(savedState.getString(CATEGORY));
        }

        final StoreApp storeApp = StoreApp.getInstance();
        restartableLatestCache(LOAD_APPS, new Func0<Observable<App[]>>() {
            @Override
            public Observable<App[]> call() {
                return storeApp.subscribeForProxy()
                        .timeout(2, TimeUnit.MINUTES)
                        .flatMap(new Func1<Proxy, Observable<App[]>>() {
                            @Override
                            public Observable<App[]> call(final Proxy proxy) {
                                if (proxy == null) {
                                    storeApp.startProxy();
                                    throw new ProxyNotRunningException();
                                } else {
                                    return AppService.getAppsUsingCategory(proxy,
                                            mCategory.toString(), 1);
                                }
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        }, new Action2<CategoryAppListFragment, App[]>() {
            @Override
            public void call(CategoryAppListFragment categoryAppListFragment, App[] apps) {
                if (apps.length == 0) {
                    categoryAppListFragment.showError(new NoAppsException());
                } else {
                    categoryAppListFragment.showApps(apps, storeApp.getProxy());
                }
            }
        }, new Action2<CategoryAppListFragment, Throwable>() {
            @Override
            public void call(CategoryAppListFragment categoryAppListFragment, Throwable throwable) {
                throwable.printStackTrace();
                categoryAppListFragment.showError(throwable);
            }
        });
    }

    @Override
    protected void onSave(Bundle state) {
        super.onSave(state);
        state.putString(CATEGORY, mCategory.toString());
    }

    public void loadApps(Category category) {
        mCategory = category;
        start(LOAD_APPS);
    }
}
