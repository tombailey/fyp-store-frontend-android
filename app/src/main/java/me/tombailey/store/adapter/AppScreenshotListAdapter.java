package me.tombailey.store.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;

import java.io.File;

import me.tombailey.store.R;
import me.tombailey.store.http.Proxy;
import me.tombailey.store.model.App;
import me.tombailey.store.rx.service.HttpService;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Tom on 28/11/2016.
 */

public class AppScreenshotListAdapter extends RecyclerView.Adapter<AppScreenshotListAdapter.AppScreenshotViewHolder> {

    private App mApp;

    private Proxy mProxy;
    private File mTempCacheDir;

    private RequestManager mRequestManager;
    private AdapterItemSelectedListener<File> mScreenshotSelectedListener;

    public AppScreenshotListAdapter(App app, RequestManager requestManager, Proxy proxy,
                                    File tempCacheDir,
                                    AdapterItemSelectedListener<File> screenshotSelectedListener) {
        mApp = app;

        mProxy = proxy;
        mTempCacheDir = tempCacheDir;

        mRequestManager = requestManager;

        mScreenshotSelectedListener = screenshotSelectedListener;
    }

    @Override
    public AppScreenshotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = ((Activity) parent.getContext()).getLayoutInflater();
        return new AppScreenshotViewHolder(layoutInflater.inflate(R.layout.list_screenshot_view, parent, false));
    }

    @Override
    public void onBindViewHolder(AppScreenshotViewHolder holder, int position) {
        holder.setScreenshotNumber(position);
    }

    @Override
    public int getItemCount() {
        return mApp.getScreenshotCount();
    }

    public class AppScreenshotViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivScreenshot;

        private int mScreenshotNumber;

        private Subscription mDownloadScreenshot;

        public AppScreenshotViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mScreenshotSelectedListener.onSelected(new File(mTempCacheDir,
                            mApp.getId() + "-screenshot-" + mScreenshotNumber + ".png"));
                }
            });
            ivScreenshot = (ImageView) itemView.findViewById(R.id.list_screenshot_view_image_view_screenshot);
        }

        public void setScreenshotNumber(int screenshotNumber) {
            mScreenshotNumber = screenshotNumber;

            if (mDownloadScreenshot != null && !mDownloadScreenshot.isUnsubscribed()) {
                mDownloadScreenshot.unsubscribe();
            }

            if (mProxy != null) {
                mDownloadScreenshot = HttpService.download(mProxy,
                        mApp.getScreenshotLink(screenshotNumber),
                        new File(mTempCacheDir, mApp.getId() + "-screenshot-" + screenshotNumber +
                                ".png"))
                    .subscribe(new Action1<File>() {
                        @Override
                        public void call(File featureGraphicFile) {
                            mRequestManager.load(featureGraphicFile).into(ivScreenshot);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });
            }
        }
    }
}
