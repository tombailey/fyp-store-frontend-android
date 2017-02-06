package me.tombailey.store.rx.service;

import org.json.JSONArray;
import org.json.JSONObject;

import me.tombailey.store.http.Proxy;
import me.tombailey.store.http.Request;
import me.tombailey.store.http.Response;
import me.tombailey.store.model.App;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by tomba on 04/02/2017.
 */

public class AppService {

    public static Observable<App> getApp(final Proxy proxy, final String id) {
        return Observable.create(new Observable.OnSubscribe<App>() {
            @Override
            public void call(Subscriber<? super App> subscriber) {
                try {
                    Request request = new Request.Builder()
                            .proxy(proxy)
                            .get()
                            .url("http://ircudcir6p7nd3ux.onion/api/applications/" + id)
                            .build();
                    Response response = request.execute();

                    JSONObject jsonResponse = new JSONObject(new String(response.getMessageBody()));
                    App app = App.fromJson(jsonResponse.getJSONObject("data"));

                    subscriber.onNext(app);
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
                            .url("http://ircudcir6p7nd3ux.onion/api/applications?keywords=" +
                                    keywords + "&page=" + page)
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
                            .url("http://ircudcir6p7nd3ux.onion/api/applications?category=" +
                                    category + "&page=" + page +
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

}
