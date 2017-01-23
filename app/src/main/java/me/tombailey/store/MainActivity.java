package me.tombailey.store;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.msopentech.thali.android.toronionproxy.AndroidOnionProxyManager;
import com.msopentech.thali.toronionproxy.OnionProxyManager;

import java.io.IOException;

import me.tombailey.http.Header;
import me.tombailey.http.Request;
import me.tombailey.http.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        final OnionProxyManager onionProxyManager = new AndroidOnionProxyManager(this, "tor");

        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean started = onionProxyManager.startWithRepeat(240, 5);
                    onionProxyManager.enableNetwork(true);
                    subscriber.onNext(started && onionProxyManager.isRunning());
                    subscriber.onCompleted();
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean started) {
                if (!started) {
                    Log.e(getClass().getName(), "failed to start");
                } else {
                    Request request = new Request.Builder()
                            .onionProxyManager(onionProxyManager)
                            .get()
                            .url("https://check.torproject.org/")
                            .build();

                    try {
                        Response response = request.execute();
                        for (Header header : response.getHeaders()) {
                            Log.d(getClass().getName(), header.getName() + ": " + header.getValue());
                        }

                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
        });



    }
}
