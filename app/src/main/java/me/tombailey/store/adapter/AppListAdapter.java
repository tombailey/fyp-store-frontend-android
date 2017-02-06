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

import me.tombailey.store.R;
import me.tombailey.store.model.App;

/**
 * Created by Tom on 28/11/2016.
 */

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppViewHolder> {

    private App[] mApps;
    private RequestManager mRequestManager;
    private AdapterItemSelectedListener<App> mAppSelectedListener;

    public AppListAdapter(App[] apps, RequestManager requestManager, AdapterItemSelectedListener<App> appSelectedListener) {
        mApps = apps;
        mRequestManager = requestManager;
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
            mRequestManager.load(app.getIconLink()).into(ivIcon);
            tvName.setText(app.getName());

            //TODO: add rating to server side elements?
//            srbRating.setRating((float) app.getRating());
        }
    }
}
