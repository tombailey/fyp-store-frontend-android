package me.tombailey.store.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import me.tombailey.store.R;
import me.tombailey.store.StoreApp;
import me.tombailey.store.exception.ProxyNotRunningException;
import me.tombailey.store.http.Proxy;
import me.tombailey.store.model.App;
import me.tombailey.store.rx.service.AppService;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by tomba on 16/03/2017.
 */

public class AppReviewService extends Service {

    public static final String APP = "app";
    public static final String DESCRIPTION = "description";
    public static final String STARS = "stars";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final App app = (App) intent.getParcelableExtra(APP);
            final String description = intent.getStringExtra(DESCRIPTION);
            final int stars = intent.getIntExtra(STARS, -1);

            createReview(app, description, stars);
        }

        return START_NOT_STICKY;
    }

    protected void createReview(final App app, final String description, final int stars) {
        final StoreApp storeApp = (StoreApp) getApplication();
        storeApp.subscribeForProxy().flatMap(new Func1<Proxy, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(Proxy proxy) {
                if (proxy == null) {
                    storeApp.startProxy();
                    return storeApp.subscribeForNextProxyUpdate().flatMap(new Func1<Proxy, Observable<Boolean>>() {
                        @Override
                        public Observable<Boolean> call(Proxy proxy) {
                            if (proxy == null) {
                                throw new ProxyNotRunningException();
                            }
                            return AppService.addReviewForApp(proxy, app, description, stars);
                        }
                    });
                } else {
                    return AppService.addReviewForApp(proxy, app, description, stars);
                }
            }
        }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean reviewCreated) {
                showReviewCreatedNotification(storeApp.getUniqueNotificationId());
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                showReviewNotCreatedNotification(storeApp.getUniqueNotificationId());
            }
        });
    }

    protected void showReviewNotCreatedNotification(int notificationId) {
        Notification appReviewedNotification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_smartphone_white_24dp)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.review_service_review_not_created_notification))
                .build();

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .notify(notificationId, appReviewedNotification);
    }

    protected void showReviewCreatedNotification(int notificationId) {
        Notification appReviewedNotification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_smartphone_white_24dp)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.review_service_created_review_notification))
                .build();

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .notify(notificationId, appReviewedNotification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
