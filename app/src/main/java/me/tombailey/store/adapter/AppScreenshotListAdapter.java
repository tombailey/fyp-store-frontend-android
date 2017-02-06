package me.tombailey.store.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;

import me.tombailey.store.R;

/**
 * Created by Tom on 28/11/2016.
 */

public class AppScreenshotListAdapter extends RecyclerView.Adapter<AppScreenshotListAdapter.AppScreenshotViewHolder> {

    private String[] mScreenshots;
    private RequestManager mRequestManager;
    private AdapterItemSelectedListener<String> mScreenshotSelectedListener;

    public AppScreenshotListAdapter(String[] screenshots, RequestManager requestManager, AdapterItemSelectedListener<String> screenshotSelectedListener) {
        mScreenshots = screenshots;
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
        holder.setScreenshot(mScreenshots[position]);
    }

    @Override
    public int getItemCount() {
        return mScreenshots.length;
    }

    public void setScreenshots(String[] screenshots) {
        mScreenshots = screenshots;
        notifyDataSetChanged();
    }

    public class AppScreenshotViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivScreenshot;

        private String mScreenshot;

        public AppScreenshotViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mScreenshotSelectedListener.onSelected(mScreenshot);
                }
            });

            ivScreenshot = (ImageView) itemView.findViewById(R.id.list_screenshot_view_image_view_screenshot);
        }

        public void setScreenshot(String screenshot) {
            mScreenshot = screenshot;
            mRequestManager.load(screenshot).into(ivScreenshot);
        }
    }
}
