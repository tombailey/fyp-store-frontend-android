package me.tombailey.store.rx.service;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import me.tombailey.store.http.Proxy;
import me.tombailey.store.http.Request;
import me.tombailey.store.http.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by tomba on 19/02/2017.
 */

public class HttpService {

    public static Observable<File> download(final Proxy proxy, final String url, final File saveTo) {
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                try {
                    save(download(proxy, url), saveTo);

                    subscriber.onNext(saveTo);
                    subscriber.onCompleted();
                } catch (Throwable t) {
                    subscriber.onError(t);
                }
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
    }

    private static byte[] download(final Proxy proxy, final String url) throws IOException {
        Request request = new Request.Builder()
                .proxy(proxy)
                .get()
                .url(url)
                .build();

        Response response = request.execute();
        if (response.getStatusCode() > 300 || response.getStatusCode() < 200) {
            Log.d(HttpService.class.getName(), "url=" + url);
            throw new IOException("status code, " + response.getStatusCode() + " is not in 2xx range");
        } else {
            return response.getMessageBody();
        }
    }

    private static void save(final byte[] toSave, final File saveTo) throws IOException {
        if (!saveTo.exists()) {
            saveTo.createNewFile();
        }

        FileOutputStream fileOutputStream = new FileOutputStream(saveTo);
        fileOutputStream.write(toSave);
        fileOutputStream.close();
    }

}
