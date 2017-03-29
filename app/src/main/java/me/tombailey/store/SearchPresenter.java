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

public class SearchPresenter extends RxPresenter<SearchActivity> {

    public static final int SEARCH = 1;


    private static final String KEYWORDS = "keywords";


    private String mKeywords;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        if (savedState != null) {
            mKeywords = savedState.getString(KEYWORDS);
        }

        final StoreApp storeApp = StoreApp.getInstance();
        restartableLatestCache(SEARCH, new Func0<Observable<App[]>>() {
            @Override
            public Observable<App[]> call() {
                return storeApp.subscribeForProxy().flatMap(new Func1<Proxy, Observable<App[]>>() {
                    @Override
                    public Observable<App[]> call(Proxy proxy) {
                        return AppService.getAppsUsingSearch(proxy, mKeywords, 1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                });
            }
        }, new Action2<SearchActivity, App[]>() {
            @Override
            public void call(SearchActivity searchActivity, App[] apps) {
                if (apps.length == 0) {
                    searchActivity.showNoApps();
                } else {
                    searchActivity.showApps(apps);
                }
            }
        }, new Action2<SearchActivity, Throwable>() {
            @Override
            public void call(SearchActivity searchActivity, Throwable throwable) {
                throwable.printStackTrace();
                searchActivity.showError(throwable);
            }
        });
    }

    @Override
    protected void onSave(Bundle state) {
        super.onSave(state);
        state.putString(KEYWORDS, mKeywords);
    }

    public void loadApps(String keywords) {
        mKeywords = keywords;
        start(SEARCH);
    }

}
