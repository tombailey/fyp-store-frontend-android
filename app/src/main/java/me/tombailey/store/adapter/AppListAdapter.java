package me.tombailey.store.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.io.File;

import me.tombailey.store.R;
import me.tombailey.store.http.Proxy;
import me.tombailey.store.model.App;
import me.tombailey.store.rx.service.HttpService;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Tom on 28/11/2016.
 */

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppViewHolder> {

    private App[] mApps;
    private RequestManager mRequestManager;

    private Proxy mProxy;
    private File mTempCacheDir;

    private AdapterItemSelectedListener<App> mAppSelectedListener;

    public AppListAdapter(App[] apps, RequestManager requestManager, Proxy proxy, File tempCacheDir,
                          AdapterItemSelectedListener<App> appSelectedListener) {
        mApps = apps;
        mRequestManager = requestManager;

        mProxy = proxy;
        mTempCacheDir = tempCacheDir;

        mAppSelectedListener = appSelectedListener;
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = ((Activity) parent.getContext()).getLayoutInflater();
        return new AppViewHolder(layoutInflater.inflate(R.layout.list_app_view, parent, false));
    }

    @Override
    public void onBindViewHolder(AppViewHolder holder, int position) {
        holder.setApp(mApps[position]);
    }

    @Override
    public int getItemCount() {
        return mApps.length;
    }

    public void setApps(App[] apps) {
        mApps = apps;
        notifyDataSetChanged();
    }

    public class AppViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivIcon;
        private TextView tvName;
        private SimpleRatingBar srbRating;

        private App mApp;

        private Subscription mDownloadFeatureGraphic;

        public AppViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAppSelectedListener.onSelected(mApp);
                }
            });

            ivIcon = (ImageView) itemView.findViewById(R.id.list_app_view_image_view_icon);
            tvName = (TextView) itemView.findViewById(R.id.list_app_view_text_view_name);
            srbRating = (SimpleRatingBar) itemView.findViewById(R.id.list_app_view_simple_rating_bar_rating);
        }

        public void setApp(App app) {
            mApp = app;
            tvName.setText(app.getName());
            srbRating.setRating((float) app.getRating());

            if (mDownloadFeatureGraphic != null && !mDownloadFeatureGraphic.isUnsubscribed()) {
                mDownloadFeatureGraphic.unsubscribe();
            }

            if (mProxy != null) {
                mDownloadFeatureGraphic = HttpService.download(mProxy, app.getIconLink(),
                        new File(mTempCacheDir, app.getId() + "-icon.png"))
                        .subscribeOn(Schedulers.immediate())
                        .subscribe(new Action1<File>() {
                            @Override
                            public void call(File featureGraphicFile) {
                                mRequestManager.load(featureGraphicFile).into(ivIcon);
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
