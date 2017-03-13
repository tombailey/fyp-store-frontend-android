package me.tombailey.store.rx.service;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import me.tombailey.store.http.Proxy;
import me.tombailey.store.http.Request;
import me.tombailey.store.http.Response;
import me.tombailey.store.http.form.body.FormBody;
import me.tombailey.store.http.form.body.UrlEncodedForm;
import me.tombailey.store.model.App;
import me.tombailey.store.model.InstalledApp;
import me.tombailey.store.model.Review;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by tomba on 04/02/2017.
 */

public class AppService {

    private static final String HOST_NAME = "q6yl3es3js7j3gm3.onion";


    private static App getAppById(final Proxy proxy, final String id) throws IOException, JSONException {
        Request request = new Request.Builder()
                .proxy(proxy)
                .get()
                .url("http://" + HOST_NAME + "/api/applications/" + id)
                .build();
        Response response = request.execute();

        JSONObject jsonResponse = new JSONObject(new String(response.getMessageBody()));
        return App.fromJson(jsonResponse.getJSONObject("data"));
    }

    public static Observable<App> getApp(final Proxy proxy, final String id) {
        return Observable.create(new Observable.OnSubscribe<App>() {
            @Override
            public void call(Subscriber<? super App> subscriber) {
                try {
                    subscriber.onNext(getAppById(proxy, id));
                    subscriber.onCompleted();
                } catch (Throwable t) {
                    subscriber.onError(t);
                }
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<App[]> getAppsUsingSearch(final Proxy proxy, final String keywords, final int page) {
        return Observable.create(new Observable.OnSubscribe<App[]>() {
            @Override
            public void call(Subscriber<? super App[]> subscriber) {
                try {
                    Request request = new Request.Builder()
                            .proxy(proxy)
                            .get()
                            .url("http://" + HOST_NAME + "/api/applications?keywords=" +
                                    URLEncoder.encode(keywords, "UTF-8") + "&page=" + page)
                            .build();
                    Response response = request.execute();

                    JSONObject jsonResponse = new JSONObject(new String(response.getMessageBody()));
                    JSONArray appsJson = jsonResponse.getJSONArray("data");

                    App[] apps = new App[appsJson.length()];
                    for (int index = 0; index < appsJson.length(); index++) {
                        apps[index] = App.fromJson(appsJson.getJSONObject(index));
                    }

                    subscriber.onNext(apps);
                    subscriber.onCompleted();
                } catch (Throwable t) {
                    subscriber.onError(t);
                }
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<App[]> getAppsUsingCategory(final Proxy proxy, final String category, final int page) {
        return Observable.create(new Observable.OnSubscribe<App[]>() {
            @Override
            public void call(Subscriber<? super App[]> subscriber) {
                try {
                    Request request = new Request.Builder()
                            .proxy(proxy)
                            .get()
                            .url("http://" + HOST_NAME + "/api/applications?category=" +
                                    URLEncoder.encode(category, "UTF-8") + "&page=" + page +
                                    "&sortBy=currentVersionDate&sortDirection=desc")
                            .build();
                    Response response = request.execute();

                    JSONObject jsonResponse = new JSONObject(new String(response.getMessageBody()));
                    JSONArray appsJson = jsonResponse.getJSONArray("data");

                    App[] apps = new App[appsJson.length()];
                    for (int index = 0; index < appsJson.length(); index++) {
                        apps[index] = App.fromJson(appsJson.getJSONObject(index));
                    }

                    subscriber.onNext(apps);
                    subscriber.onCompleted();
                } catch (Throwable t) {
                    subscriber.onError(t);
                }
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Review[]> getReviewsForApp(final Proxy proxy, final App app) {
        return getReviewsForApp(proxy, app.getId());
    }

    public static Observable<Review[]> getReviewsForApp(final Proxy proxy, final String appId) {
        return Observable.create(new Observable.OnSubscribe<Review[]>() {
            @Override
            public void call(Subscriber<? super Review[]> subscriber) {
                try {
                    Request request = new Request.Builder()
                            .proxy(proxy)
                            .get()
                            .url("http://" + HOST_NAME + "/api/applications/" + appId + "/reviews")
                            .build();
                    Response response = request.execute();

                    JSONObject jsonResponse = new JSONObject(new String(response.getMessageBody()));
                    JSONArray reviewsJson = jsonResponse.getJSONArray("data");

                    Review[] reviews = new Review[reviewsJson.length()];
                    for (int index = 0; index < reviewsJson.length(); index++) {
                        reviews[index] = Review.fromJson(reviewsJson.getJSONObject(index));
                    }

                    subscriber.onNext(reviews);
                    subscriber.onCompleted();
                } catch (Throwable t) {
                    subscriber.onError(t);
                }
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<File> downloadApp(final Proxy proxy, final App app, final File saveTo) {
        return HttpService.download(proxy, app.getDownloadLink(), saveTo);
    }

    public static Observable<Boolean> addReviewForApp(final Proxy proxy, final String appId,
                                                      final String description, final int stars) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    FormBody formBody = new UrlEncodedForm.Builder()
                            .add("description", description)
                            .add("stars", stars)
                            .build();

                    Request request = new Request.Builder()
                            .proxy(proxy)
                            .post(formBody)
                            .url("http://" + HOST_NAME + "/api/applications/" + appId + "/reviews")
                            .build();
                    Response response = request.execute();

                    if (response.getStatusCode() != HttpURLConnection.HTTP_CREATED) {
                        //TODO: use custom exception
                        throw new IOException("review not accepted");
                    }

                    subscriber.onNext(true);
                    subscriber.onCompleted();
                } catch (Throwable t) {
                    subscriber.onError(t);
                }
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * TODO:
     * @param realm TODO:
     * @param packageManager TODO:
     * @return TODO:
     */
    public static List<InstalledApp> getInstalledApps(final Realm realm,
                                                      final PackageManager packageManager) {
            List<InstalledApp> appsStoreInstalled = getAppsInstalledByStore(realm);
            List<ApplicationInfo> appsCurrentlyInstalled =
                    packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

            for (InstalledApp appStoreInstalled : appsStoreInstalled) {
                boolean stillInstalled = false;
                for (ApplicationInfo appInfo : appsCurrentlyInstalled) {
                    if (appStoreInstalled.getAppId().equals(appInfo.packageName)) {
                        stillInstalled = true;
                    }
                }

                if (!stillInstalled) {
                    removeInstalledAppFromRealm(realm, appStoreInstalled);
                    appsStoreInstalled.remove(appStoreInstalled);
                }
            }
            return appsStoreInstalled;
    }

    /**
     * TODO:
     * @param realm a realm instance with which InstalledApps will be retrieved. The realm instance
     *              will not be closed
     * @param installedApp the InstalledApp to remove from realm
     */
    private static void removeInstalledAppFromRealm(Realm realm,
                                                    InstalledApp installedApp) {
        realm.beginTransaction();
        realm.where(InstalledApp.class)
                .equalTo("mAppId", installedApp.getAppId())
                .findFirst()
                .deleteFromRealm();
        realm.commitTransaction();
    }

    /**
     * TODO:
     * @param realm a realm instance with which InstalledApps will be retrieved. The realm instance
     *              will not be closed
     * @return the apps that have been installed by the store. Note that some apps may have been
     * installed since their installation was recorded; use PackageManager to verify they are still
     * installed
     */
    private static List<InstalledApp> getAppsInstalledByStore(Realm realm) {
        RealmResults<InstalledApp> installedApps = realm.where(InstalledApp.class).findAll();
        List<InstalledApp> appsStoreInstalled = new ArrayList<InstalledApp>(installedApps);
        return appsStoreInstalled;
    }

    /**
     * TODO:
     * @param proxy TODO:
     * @param realmConfiguration TODO:
     * @param packageManager TODO:
     * @return the apps that have been installed by the store. Note that some apps may have been
     * installed since their installation was recorded; use PackageManager to verify they are still
     * installed
     */
    public static Observable<List<App>> checkForUpdates(final Proxy proxy,
                                                        final RealmConfiguration realmConfiguration,
                                                        final PackageManager packageManager) {
        return Observable.create(new Observable.OnSubscribe<List<App>>() {
            @Override
            public void call(Subscriber<? super List<App>> subscriber) {
                try {
                    Realm realm = Realm.getInstance(realmConfiguration);
                    List<InstalledApp> installedApps = getInstalledApps(realm, packageManager);
                    List<App> appsToUpdate = new ArrayList<App>(4);

                    for (InstalledApp installedApp : installedApps) {
                        App app = getAppById(proxy, installedApp.getAppId());
                        if (app.getCurrentVersionNumber() > installedApp.getVersionNumber()) {
                            appsToUpdate.add(app);
                        }
                    }

                    realm.close();

                    subscriber.onNext(appsToUpdate);
                    subscriber.onCompleted();
                } catch (Throwable t) {
                    subscriber.onError(t);
                }
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * TODO:
     * @param realmConfiguration TODO:
     * @param app TODO:
     * @return the apps that have been installed by the store. Note that some apps may have been
     * installed since their installation was recorded; use PackageManager to verify they are still
     * installed
     */
    public static Observable<Boolean> registerAppInstall(final RealmConfiguration realmConfiguration,
                                                         final App app) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    Realm realm = Realm.getInstance(realmConfiguration);
                    InstalledApp installedApp = realm.where(InstalledApp.class)
                            .equalTo("mAppId", app.getId())
                            .findFirst();

                    realm.beginTransaction();
                    if (installedApp == null) {
                        realm.insert(InstalledApp.newInstance(app.getId(),
                                app.getCurrentVersionNumber()));
                    } else {
                        installedApp.setVersionNumber(app.getCurrentVersionNumber());
                    }
                    realm.commitTransaction();
                    realm.close();

                    subscriber.onNext(true);
                    subscriber.onCompleted();
                } catch (Throwable t) {
                    subscriber.onError(t);
                }
            }
        });
    }

}
