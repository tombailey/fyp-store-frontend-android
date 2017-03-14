package me.tombailey.store.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.msopentech.thali.android.toronionproxy.AndroidOnionProxyManager;
import com.msopentech.thali.toronionproxy.OnionProxyManager;

import java.io.IOException;

import me.tombailey.store.ProxyStatusActivity;
import me.tombailey.store.R;

/**
 * Created by tomba on 23/01/2017.
 */

public class TorConnectionService extends Service {

    public static final String START = "start";
    public static final String STOP = "stop";

    public static final String STATUS = "status";
    public static final String STATUS_RUNNING = "running";
    public static final String STATUS_NOT_RUNNING = "not running";

    public static final String PROXY_STATUS_UPDATE = "me.tombailey.store.PROXY_STATUS_UPDATE";
    public static final String HOST = "host";
    public static final String PORT = "port";



    private static final String LOG_TAG = TorConnectionService.class.getName();

    private static final int PROXY_NOTIFICATION_ID = 42;


    private OnionProxyManager mOnionProxyManager;

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if (mOnionProxyManager == null) {
            mOnionProxyManager = new AndroidOnionProxyManager(this, "tor");
        }

        if (intent != null) {
            new Thread() {
                @Override
                public void run() {
                    String action = intent.getAction();
                    Log.d(LOG_TAG, "Got intent for action '" + action + "'");

                    if (START.equals(action)) {
                        if (isOnionProxyRunning()) {
                            Log.w(LOG_TAG, "Received start command for OnionProxy but already running");
                            try {
                                broadcastProxyRunning();
                            } catch (IOException ioe) {
                                ioe.printStackTrace();
                                broadcastProxyNotRunning();
                            }
                        } else {
                            Log.d(LOG_TAG, "Starting OnionProxy");
                            if (startOnionProxy()) {
                                try {
                                    broadcastProxyRunning();
                                    showProxyNotification();
                                } catch (IOException ioe) {
                                    ioe.printStackTrace();
                                    broadcastProxyNotRunning();
                                }
                            } else {
                                broadcastProxyNotRunning();
                            }
                        }
                    } else if (STOP.equals(action)) {
                        if (isOnionProxyRunning()) {
                            Log.d(LOG_TAG, "Stopping OnionProxy");
                            stopOnionProxy();
                            broadcastProxyNotRunning();
                            hideProxyNotification();
                        } else {
                            Log.w(LOG_TAG, "Received stop command for OnionProxy but not running");
                            broadcastProxyNotRunning();
                        }
                    } else if (STATUS.equals(action)) {
                        if (isOnionProxyRunning()) {
                            try {
                                broadcastProxyRunning();
                            } catch (IOException ioe) {
                                ioe.printStackTrace();
                                broadcastProxyNotRunning();
                            }
                        } else {
                            broadcastProxyNotRunning();
                        }
                    }
                }
            }.start();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private boolean startOnionProxy() {
        try {
            if (isOnionProxyRunning()) {
                return true;
            } else {
                return mOnionProxyManager.startWithRepeat(240, 20);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            return false;
        }
    }

    private void stopOnionProxy() {
        try {
            if (isOnionProxyRunning()) {
                mOnionProxyManager.stop();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private boolean isOnionProxyRunning() {
        try {
            return mOnionProxyManager.isRunning();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }

    private void broadcastProxyRunning() throws IOException {
        Intent proxyUpdate = new Intent(PROXY_STATUS_UPDATE);
        proxyUpdate.putExtra(STATUS, STATUS_RUNNING);
        proxyUpdate.putExtra(HOST, "127.0.0.1");
        proxyUpdate.putExtra(PORT, getOnionProxyPort());
        sendBroadcast(proxyUpdate);
    }

    private void broadcastProxyNotRunning() {
        Intent proxyUpdate = new Intent(PROXY_STATUS_UPDATE);
        proxyUpdate.putExtra(STATUS, STATUS_NOT_RUNNING);
        sendBroadcast(proxyUpdate);
    }

    private int getOnionProxyPort() throws IOException {
        //TODO: catch runtime for tor is not running
        return mOnionProxyManager.getIPv4LocalHostSocksPort();
    }

    private void showProxyNotification() {
        PendingIntent proxyStatusIntent = getProxyStatusPendingIntent();
        PendingIntent stopProxyIntent = getStopProxyPendingIntent();

        Notification proxyNotification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_router_white_24dp)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.proxy_service_notification_text))
                .setContentIntent(proxyStatusIntent)
                .addAction(R.drawable.ic_stop_white_24dp, getString(R.string.stop), stopProxyIntent)
                .setOngoing(true)
                .build();

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .notify(PROXY_NOTIFICATION_ID, proxyNotification);
    }

    private PendingIntent getProxyStatusPendingIntent() {
        Intent proxyStatusIntent = new Intent(this, ProxyStatusActivity.class);
        return PendingIntent.getActivity(this, 0, proxyStatusIntent, 0);
    }

    private PendingIntent getStopProxyPendingIntent() {
        Intent stopProxy = new Intent();
        stopProxy.setComponent(new ComponentName("me.tombailey.store", "me.tombailey.store.service.TorConnectionService"));
        stopProxy.setAction(STOP);

        return PendingIntent.getService(this, 0, stopProxy, 0);
    }

    private void hideProxyNotification() {
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(PROXY_NOTIFICATION_ID);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
