package me.tombailey.store.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

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

public class FeaturedAppListAdapter extends RecyclerView.Adapter<FeaturedAppListAdapter.FeaturedAppViewHolder> {

    private App[] mApps;
    private RequestManager mRequestManager;

    private Proxy mProxy;
    private File mTempCacheDir;

    private AdapterItemSelectedListener<App> mAppSelectedListener;

    public FeaturedAppListAdapter(App[] apps, RequestManager requestManager, Proxy proxy,
                                  File tempCacheDir, AdapterItemSelectedListener<App> appSelectedListener) {
        mApps = apps;
        mRequestManager = requestManager;

        mProxy = proxy;
        mTempCacheDir = tempCacheDir;

        mAppSelectedListener = appSelectedListener;
    }

    @Override
    public FeaturedAppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FeaturedAppViewHolder(((Activity) parent.getContext()).getLayoutInflater().inflate(R.layout.list_featured_app_view, parent, false));
    }

    @Override
    public void onBindViewHolder(FeaturedAppViewHolder holder, int position) {
        holder.setApp(mApps[position]);
    }

    @Override
    public int getItemCount() {
        return mApps.length;
    }

    public class FeaturedAppViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivGraphic;
        private TextView tvName;

        private App mApp;

        private Subscription mDownloadFeatureGraphic;

        public FeaturedAppViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAppSelectedListener.onSelected(mApp);
                }
            });

            ivGraphic = (ImageView) itemView.findViewById(R.id.list_app_view_image_view_icon);
            tvName = (TextView) itemView.findViewById(R.id.list_app_view_text_view_name);
        }

        public void setApp(App app) {
            mApp = app;
            tvName.setText(app.getName());

            if (mDownloadFeatureGraphic != null && !mDownloadFeatureGraphic.isUnsubscribed()) {
                mDownloadFeatureGraphic.unsubscribe();
            }

            if (mProxy != null) {
                mDownloadFeatureGraphic = HttpService.download(mProxy, app.getFeatureGraphicLink(),
                        new File(mTempCacheDir, app.getId() + "-featureGraphic.png"))
                        .subscribeOn(Schedulers.immediate())
                        .subscribe(new Action1<File>() {
                            @Override
                            public void call(File featureGraphicFile) {
                                mRequestManager.load(featureGraphicFile).into(ivGraphic);
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
