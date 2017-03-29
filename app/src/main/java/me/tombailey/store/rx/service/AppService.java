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


    /**
     * Retrieves the App associated with the id given
     * @param proxy a Proxy instance with which network connections can be established
     * @param id the id of the application to retrieve
     * @return the App associated with the id given
     * @throws IOException if a network error occurs
     * @throws JSONException if the network response can not be mapped
     */
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

    /**
     * Retrieves the App associated with the id given
     * @param proxy a Proxy instance with which network connections can be established
     * @param id the id of the application to retrieve
     * @return an Observable that will emit the App associated with the id given or an error
     */
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

    /**
     * Retrieves an array of Apps related to the keywords on the specified page
     * @param proxy a Proxy instance with which network connections can be established
     * @param keywords the keywords to use when finding Apps
     * @param page the page of results to be retrieved
     * @return an Observable that will emit an array of Apps related to the keywords on the
     * specified page or an error
     */
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

    /**
     * Retrieves an array of Apps according to the category and page specified
     * @param proxy a Proxy instance with which network connections can be established
     * @param category the category to retrieve Apps from
     * @param page the page of results to be retrieved
     * @return an Observable that will emit an array of Apps according to the category and page
     * specified or an error
     */
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

    /**
     * Retrieves an array of Reviews for the given App
     * @param proxy a Proxy instance with which network connections can be established
     * @param app the App to retrieve Reviews for
     * @return an Observable that will emit an array of Reviews for the given app or error
     */
    public static Observable<Review[]> getReviewsForApp(final Proxy proxy, final App app) {
        return getReviewsForApp(proxy, app.getId());
    }

    /**
     * Retrieves an array of Reviews for the given app
     * @param proxy a Proxy instance with which network connections can be established
     * @param appId the appId for the App to retrieve Reviews for
     * @return an Observable that will emit an array of Reviews for the given app or an error
     */
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

    /**
     * Downloads the APK for the App specified to the File location specified
     * @param proxy a Proxy instance with which network connections can be established
     * @param app the App to download
     * @param saveTo the File location to save the APK to
     * @return an Observable that will emit the File location that the APK was saved to or an error
     */
    public static Observable<File> downloadApp(final Proxy proxy, final App app, final File saveTo) {
        return HttpService.download(proxy, app.getDownloadLink(), saveTo);
    }

    /**
     * Creates a new Review for the App specified
     * @param proxy a Proxy instance with which network connections can be established
     * @param app the App to leave a Review for
     * @param description the description for the Review to create
     * @param stars the stars for the Review to create
     * @return an Observable that will emit true if the review is created or an error otherwise
     */
    public static Observable<Boolean> addReviewForApp(final Proxy proxy, final App app,
                                                      final String description, final int stars) {
        return addReviewForApp(proxy, app.getId(), description, stars);
    }

    /**
     * Creates a new Review for the App specified
     * @param proxy a Proxy instance with which network connections can be established
     * @param appId the appId for the App to leave a Review for
     * @param description the description for the Review to create
     * @param stars the stars for the Review to create
     * @return an Observable that will emit true if the review is created or an error otherwise
     */
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
     * Retrieves a list of InstalledApps that are actually installed on the device
     * @param realm a realm instance with which InstalledApps will be retrieved. The realm instance
     *              will not be closed
     * @param packageManager a PackageManager to verify installed applications with
     * @return a list of InstalledApps that are actually installed on the device
     */
    private static List<InstalledApp> getInstalledApps(final Realm realm,
                                                      final PackageManager packageManager) {
        removeUninstalledInstalledAppsFromRealm(realm, packageManager);
        return getAppsInstalledByStore(realm);
    }

    /**
     * Removes InstalledApps from Realm which are no longer installed on the device
     * @param realm a realm instance with which the InstalledApps will be removed. The realm instance
     *              will not be closed
     * @param packageManager a PackageManager to verify installed applications with
     */
    private static void removeUninstalledInstalledAppsFromRealm(Realm realm,
                                                                PackageManager packageManager) {
        List<InstalledApp> installedApps = getAppsInstalledByStore(realm);
        List<ApplicationInfo> appsCurrentlyInstalled =
                packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        for (InstalledApp installedApp : installedApps) {
            boolean stillInstalled = false;
            for (ApplicationInfo appInfo : appsCurrentlyInstalled) {
                if (installedApp.getAppId().equals(appInfo.packageName)) {
                    stillInstalled = true;
                }
            }

            if (!stillInstalled) {
                removeInstalledAppFromRealm(realm, installedApp);
            }
        }
    }

    /**
     * Removes an InstalledApp from Realm
     * @param realm a realm instance with which the InstalledApp will be removed. The realm instance
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
     * Retrieves a List of InstalledApps that have been installed by the store. Note that some apps
     * may have been installed since their installation was recorded; use PackageManager to verify
     * they are still installed
     * @param realm a realm instance with which InstalledApps will be retrieved. The realm instance
     *              will not be closed
     * @return a List of InstalledApps that have been installed by the store. Note that some apps
     * may have been installed since their installation was recorded; use PackageManager to verify
     * they are still installed
     */
    private static List<InstalledApp> getAppsInstalledByStore(Realm realm) {
        RealmResults<InstalledApp> installedApps = realm.where(InstalledApp.class).findAll();
        List<InstalledApp> appsStoreInstalled = new ArrayList<InstalledApp>(installedApps);
        return appsStoreInstalled;
    }

    /**
     * Retrieves an array of Apps that should be updated
     * @param proxy a Proxy instance with which network connections can be established
     * @param realmConfiguration a RealmConfiguration to create a Realm instance with
     * @param packageManager a PackageManager to verify installed applications with
     * @return an Observable that will emit a List of Apps that need updates or an error
     */
    public static Observable<App[]> checkForUpdates(final Proxy proxy,
                                                        final RealmConfiguration realmConfiguration,
                                                        final PackageManager packageManager) {
        return Observable.create(new Observable.OnSubscribe<App[]>() {
            @Override
            public void call(Subscriber<? super App[]> subscriber) {
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

                    subscriber.onNext(appsToUpdate.toArray(new App[appsToUpdate.size()]));
                    subscriber.onCompleted();
                } catch (Throwable t) {
                    subscriber.onError(t);
                }
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Registers an InstalledApp for the app specified
     * @param realmConfiguration a RealmConfiguration to create a Realm instance with
     * @param app the App to register an InstalledApp for
     * @return an Observable that will emit true if the review is created or an error otherwise
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
