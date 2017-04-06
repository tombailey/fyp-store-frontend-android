package me.tombailey.store.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;

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

public class AppDownloadService extends Service {

    public static final String DOWNLOAD_APP = "download app";

    public static final String APP = "app";
    public static final String APK_SAVE_FILE = "apk save file";


    private static final String APP_INSTALL = "app install";
    private static final String CANCEL_APP_INSTALL = "cancel app install";


    private static final String LOG_TAG = AppDownloadService.class.getName();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            handleAction(intent.getAction(), intent);
        }

        return START_NOT_STICKY;
    }

    protected void handleAction(String action, Intent intent) {
        if (action.equalsIgnoreCase(DOWNLOAD_APP)) {
            downloadApp((App) intent.getParcelableExtra(APP));
        } else if (action.equalsIgnoreCase(APP_INSTALL)) {
            installApp((App) intent.getParcelableExtra(APP), new File(intent.getStringExtra(APK_SAVE_FILE)));
        } else if (action.equalsIgnoreCase(CANCEL_APP_INSTALL)) {
            cancelAppInstall(new File(intent.getStringExtra(APK_SAVE_FILE)));
        } else {
            Log.w(LOG_TAG, "action " + action + " is not recognised so was ignored");
        }
    }

    protected void downloadApp(final App app) {
        final StoreApp storeApp = (StoreApp) getApplication();
        storeApp.subscribeForProxy().flatMap(new Func1<Proxy, Observable<File>>() {
            @Override
            public Observable<File> call(Proxy proxy) {
                if (proxy == null) {
                    storeApp.startProxy();
                    return storeApp.subscribeForNextProxyUpdate().flatMap(new Func1<Proxy, Observable<File>>() {
                        @Override
                        public Observable<File> call(Proxy proxy) {
                            if (proxy == null) {
                                throw new ProxyNotRunningException();
                            } else {
                                return getDownloadObservable(app, proxy);
                            }
                        }
                    });
                } else {
                    return getDownloadObservable(app, proxy);
                }
            }
        }).subscribe(new Action1<File>() {
            @Override
            public void call(File savedTo) {
                showAppDownloadedNotification(app, savedTo, storeApp.getUniqueNotificationId());
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                showAppNotDownloadedNotification(storeApp.getUniqueNotificationId());
            }
        });
    }

    protected Observable<File> getDownloadObservable(App app, Proxy proxy) {
        File saveTo =
                new java.io.File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        app.getName() + ".apk");
        return AppService.downloadApp(proxy, app, saveTo);
    }

    protected void installApp(final App app, final File apkSaveFile) {
        StoreApp storeApp = (StoreApp) getApplication();
        AppService.registerAppInstall(storeApp.getRealmConfiguration(), app).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean installRegistered) {
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setDataAndType(Uri.fromFile(apkSaveFile), "application/vnd.android.package-archive");
                startActivity(install);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                //TODO: handle
                throwable.printStackTrace();
            }
        });
    }

    protected void cancelAppInstall(File apkSaveFile) {
        apkSaveFile.delete();
    }

    protected PendingIntent getInstallAppPendingIntent(App app, File apkSaveFile) {
        Intent appInstallIntent = new Intent();
        appInstallIntent.setComponent(new ComponentName("me.tombailey.store", "me.tombailey.store.service.AppDownloadService"));
        appInstallIntent.putExtra(APK_SAVE_FILE, apkSaveFile.getAbsolutePath());
        appInstallIntent.putExtra(APP, app);
        appInstallIntent.setAction(APP_INSTALL);
        //TODO: make pending intent unique to avoid update scenario

        return PendingIntent.getService(this, 0, appInstallIntent, 0);
    }

    protected PendingIntent getCancelAppInstallPendingIntent(File apkSaveFile) {
        Intent cancelAppInstall = new Intent();
        cancelAppInstall.setComponent(new ComponentName("me.tombailey.store", "me.tombailey.store.service.AppDownloadService"));
        cancelAppInstall.putExtra(APK_SAVE_FILE, apkSaveFile.getAbsolutePath());
        cancelAppInstall.setAction(CANCEL_APP_INSTALL);
        //TODO: make pending intent unique to avoid update scenario

        return PendingIntent.getService(this, 0, cancelAppInstall, 0);
    }

    protected void showAppNotDownloadedNotification(int notificationId) {
        Notification appDownloadedNotification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_smartphone_white_24dp)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.download_service_download_failed_notification))
                .build();

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .notify(notificationId, appDownloadedNotification);
    }

    protected void showAppDownloadedNotification(App app, File apkSaveFile, int notificationId) {
        Notification appDownloadedNotification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_smartphone_white_24dp)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.download_service_download_complete_notification))
                .setContentIntent(getInstallAppPendingIntent(app, apkSaveFile))
                .setDeleteIntent(getCancelAppInstallPendingIntent(apkSaveFile))
                .setAutoCancel(true)
                .build();

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .notify(notificationId, appDownloadedNotification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
