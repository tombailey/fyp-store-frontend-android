package me.tombailey.store;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;

import java.io.File;

import io.realm.Realm;
import me.tombailey.store.exception.AppNotInstalledException;
import me.tombailey.store.exception.ProxyNotRunningException;
import me.tombailey.store.http.Proxy;
import me.tombailey.store.model.App;
import me.tombailey.store.model.InstalledApp;
import me.tombailey.store.model.Review;
import me.tombailey.store.rx.service.AppService;
import me.tombailey.store.rx.service.HttpService;
import me.tombailey.store.service.AppReviewService;
import nucleus.presenter.RxPresenter;
import rx.Observable;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;

/**
 * Created by tomba on 29/03/2017.
 */

public class AppPresenter extends RxPresenter<AppActivity> {

    public static final String APP = "app";

    public static final int LOAD_ICON = 1;
    public static final int LOAD_REVIEWS = 2;
    public static final int SHOW_APP = 3;

    public static final int OPEN_APP = 4;
    public static final int DOWNLOAD_APP = 5;
    public static final int CREATE_REVIEW = 6;


    private App mApp;

    private String mReviewMessage;
    private int mReviewStars;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        if (savedState != null) {
            mApp = savedState.getParcelable(APP);
        }

        restartableLatestCache(LOAD_ICON, new Func0<Observable<File>>() {
            @Override
            public Observable<File> call() {
                final StoreApp storeApp = StoreApp.getInstance();
                return storeApp.subscribeForProxy().flatMap(new Func1<Proxy, Observable<File>>() {
                    @Override
                    public Observable<File> call(Proxy proxy) {
                        if (proxy == null) {
                            throw new ProxyNotRunningException();
                        } else {
                            final File iconFile = new File(storeApp.getCacheDir(),
                                    "app" + File.separator + mApp.getId() + File.separator + "icon.png");
                            iconFile.getParentFile().mkdirs();
                            return HttpService.download(proxy, mApp.getIconLink(), iconFile);
                        }
                    }
                });
            }
        }, new Action2<AppActivity, File>() {
            @Override
            public void call(AppActivity appActivity, File iconFile) {
                appActivity.showIcon(iconFile);
            }
        }, new Action2<AppActivity, Throwable>() {
            @Override
            public void call(AppActivity appActivity, Throwable throwable) {
                throwable.printStackTrace();
                appActivity.showIconError();
            }
        });

        restartableLatestCache(LOAD_REVIEWS, new Func0<Observable<Review[]>>() {
            @Override
            public Observable<Review[]> call() {
                final StoreApp storeApp = StoreApp.getInstance();
                return storeApp.subscribeForProxy().flatMap(new Func1<Proxy, Observable<Review[]>>() {
                    @Override
                    public Observable<Review[]> call(Proxy proxy) {
                        if (proxy == null) {
                            throw new ProxyNotRunningException();
                        } else {
                            return AppService.getReviewsForApp(proxy, mApp);
                        }
                    }
                });
            }
        }, new Action2<AppActivity, Review[]>() {
            @Override
            public void call(AppActivity appActivity, Review[] reviews) {
                if (reviews.length == 0) {
                    appActivity.showNoReviews();
                } else {
                    if (reviews.length > 2) {
                        Review[] reviewsPreview = new Review[]{
                                reviews[0],
                                reviews[1]
                        };
                        appActivity.showReviews(reviewsPreview);
                    } else {
                        appActivity.showReviews(reviews);
                    }
                }
            }
        }, new Action2<AppActivity, Throwable>() {
            @Override
            public void call(AppActivity appActivity, Throwable throwable) {
                throwable.printStackTrace();
                appActivity.showReviewsError();
            }
        });

        restartableLatestCache(SHOW_APP, new Func0<Observable<Pair<Boolean, Boolean>>>() {
            @Override
            public Observable<Pair<Boolean, Boolean>> call() {
                Realm realm = StoreApp.getInstance().getRealm();
                InstalledApp installedApp = realm.where(InstalledApp.class)
                        .equalTo("mAppId", mApp.getId())
                        .findFirst();
                boolean isInstalled = installedApp != null;
                boolean updateNeeded = installedApp != null && installedApp.getVersionNumber() < mApp.getCurrentVersionNumber();
                realm.close();

                return Observable.just(new Pair<Boolean, Boolean>(isInstalled, updateNeeded));
            }
        }, new Action2<AppActivity, Pair<Boolean, Boolean>>() {
            @Override
            public void call(AppActivity appActivity, Pair<Boolean, Boolean> isInstalledAndNeedsUpdate) {
               appActivity.showApp(mApp.getName(), mApp.getDescription(), mApp.getRating(),
                       isInstalledAndNeedsUpdate.first, isInstalledAndNeedsUpdate.second);
            }
        }, new Action2<AppActivity, Throwable>() {
            @Override
            public void call(AppActivity appActivity, Throwable throwable) {
                throwable.printStackTrace();
                //TODO: handle
            }
        });

        //TODO: re-launching on return to app
        restartableLatestCache(OPEN_APP, new Func0<Observable<Intent>>() {
            @Override
            public Observable<Intent> call() {
                Realm realm = StoreApp.getInstance().getRealm();

                InstalledApp installedApp = realm.where(InstalledApp.class)
                        .equalTo("mAppId", mApp.getId())
                        .findFirst();

                Intent launchIntent = null;
                if (installedApp != null) {
                    launchIntent = StoreApp.getInstance().getPackageManager()
                            .getLaunchIntentForPackage(installedApp.getAppId());
                }
                realm.close();

                if (launchIntent == null) {
                    throw new AppNotInstalledException();
                } else {
                    return Observable.just(launchIntent);
                }
            }
        }, new Action2<AppActivity, Intent>() {
            @Override
            public void call(AppActivity appActivity, Intent launchIntent) {
                StoreApp.getInstance().startActivity(launchIntent);
            }
        }, new Action2<AppActivity, Throwable>() {
            @Override
            public void call(AppActivity appActivity, Throwable throwable) {
                throwable.printStackTrace();
                appActivity.showAppNotInstalled();
            }
        });

        restartableLatestCache(DOWNLOAD_APP, new Func0<Observable<Review[]>>() {
            @Override
            public Observable<Review[]> call() {
                final StoreApp storeApp = StoreApp.getInstance();
                return storeApp.subscribeForProxy().flatMap(new Func1<Proxy, Observable<Review[]>>() {
                    @Override
                    public Observable<Review[]> call(Proxy proxy) {
                        //TODO:
                        return null;
                    }
                });
            }
        }, new Action2<AppActivity, Review[]>() {
            @Override
            public void call(AppActivity appActivity, Review[] reviews) {
                //TODO:
            }
        }, new Action2<AppActivity, Throwable>() {
            @Override
            public void call(AppActivity appActivity, Throwable throwable) {
                throwable.printStackTrace();
                //TODO:
            }
        });

        restartableLatestCache(CREATE_REVIEW, new Func0<Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call() {
                Intent createReviewInBackgroundIntent = new Intent();
                createReviewInBackgroundIntent.setComponent(
                        new ComponentName("me.tombailey.store", "me.tombailey.store.service.AppReviewService"));
                createReviewInBackgroundIntent.putExtra(AppReviewService.APP, mApp);
                createReviewInBackgroundIntent.putExtra(AppReviewService.DESCRIPTION, mReviewMessage);
                createReviewInBackgroundIntent.putExtra(AppReviewService.STARS, mReviewStars);
                StoreApp.getInstance().startService(createReviewInBackgroundIntent);
                return Observable.just(true);
            }
        }, new Action2<AppActivity, Boolean>() {
            @Override
            public void call(AppActivity appActivity, Boolean reviewBeingCreated) {
                appActivity.showReviewBeingCreated();
            }
        });
    }

    @Override
    protected void onSave(Bundle state) {
        super.onSave(state);
        state.putParcelable(APP, mApp);

        stop(OPEN_APP);
        stop(DOWNLOAD_APP);
    }

    public void showApp(App app) {
        mApp = app;
        start(SHOW_APP);
    }

    public void loadIcon(App app) {
        mApp = app;
        start(LOAD_ICON);
    }

    public void loadReviews(App app) {
        mApp = app;
        start(LOAD_REVIEWS);
    }

    public void openApp(App app) {
        mApp = app;
        start(OPEN_APP);
    }

    public void downloadApp(App app) {
        mApp = app;
        start(DOWNLOAD_APP);
    }

    public void createReview(App app, String message, int stars) {
        mApp = app;
        mReviewMessage = message;
        mReviewStars = stars;
        start(CREATE_REVIEW);
    }

}
