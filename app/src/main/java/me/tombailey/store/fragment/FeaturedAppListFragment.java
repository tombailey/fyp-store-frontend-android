package me.tombailey.store.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.concurrent.TimeUnit;

import me.tombailey.store.AppActivity;
import me.tombailey.store.R;
import me.tombailey.store.StoreApp;
import me.tombailey.store.adapter.AdapterItemSelectedListener;
import me.tombailey.store.adapter.FeaturedAppListAdapter;
import me.tombailey.store.exception.NoAppsException;
import me.tombailey.store.exception.ProxyNotRunningException;
import me.tombailey.store.http.Proxy;
import me.tombailey.store.model.App;
import me.tombailey.store.model.Category;
import me.tombailey.store.rx.service.AppService;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Tom on 29/11/2016.
 */

public class FeaturedAppListFragment extends Fragment {

    private static final String CATEGORY = "category";


    private StoreApp mStoreApp;

    private FragmentActivity mActivity;
    private Category mCategory;

    private View mContent;

    private View mProgressBar;
    private View mErrorView;
    private RecyclerView mRecyclerView;

    private int mPage;

    private Subscription mLoadAppsSubscription;

    public static FeaturedAppListFragment newInstance(Category category) {
        FeaturedAppListFragment appFragment = new FeaturedAppListFragment();
        Bundle args = new Bundle();
        args.putString(CATEGORY, category.toString());
        appFragment.setArguments(args);
        return appFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();
        mStoreApp = (StoreApp) mActivity.getApplication();

        Bundle args = getArguments();
        mCategory = Category.valueOf(args.getString(CATEGORY));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContent = inflater.inflate(R.layout.fragment_featured_app_list, container, false);

        mProgressBar = mContent.findViewById(R.id.featured_app_list_fragment_progress_bar);
        mErrorView = mContent.findViewById(R.id.featured_app_list_fragment_linear_layout_error);
        mRecyclerView = (RecyclerView) mContent.findViewById(R.id.featured_app_list_fragment_recycler_view);

        init();
        return mContent;
    }

    @Override
    public void onDestroy() {
        if (mLoadAppsSubscription != null && !mLoadAppsSubscription.isUnsubscribed()) {
            mLoadAppsSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    private void init() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);

        mPage = 1;

        mLoadAppsSubscription = mStoreApp.subscribeForProxy()
            .timeout(2, TimeUnit.MINUTES)
            .flatMap(new Func1<Proxy, Observable<App[]>>() {
                @Override
                public Observable<App[]> call(final Proxy proxy) {
                    if (proxy == null) {
                        mStoreApp.startProxy();
                        throw new ProxyNotRunningException();
                    } else {
                        return AppService.getAppsUsingCategory(proxy, mCategory.toString(), mPage++);
                    }
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<App[]>() {
                @Override
                public void call(App[] apps) {
                    if (apps.length == 0) {
                        throw new NoAppsException();
                    } else {
                        showApps(apps, mStoreApp.getProxy());
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    throwable.printStackTrace();
                    showError(throwable);
                }
            });
    }

    private void showError(Throwable throwable) {
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);

        TextView tvErrorDescription = (TextView) mErrorView.findViewById(R.id.featured_app_list_fragment_text_view_error_description);
        if (throwable instanceof NoAppsException) {
            tvErrorDescription.setText(R.string.featured_app_list_fragment_no_apps);
        } else {
            tvErrorDescription.setText(R.string.generic_error);
        }

        mErrorView.findViewById(R.id.featured_app_list_fragment_button_error_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init();
            }
        });
    }

    private void showApps(App[] apps, Proxy proxy) {
        mRecyclerView.setHasFixedSize(true);

        int columns = getResources().getConfiguration().screenWidthDp / 490;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, columns < 1 ? 1 : columns);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        FeaturedAppListAdapter featuredAppListAdapter = new FeaturedAppListAdapter(apps,
                Glide.with(mActivity), proxy,  mStoreApp.getTempCacheDirectory(),
                new AdapterItemSelectedListener<App>() {
            @Override
            public void onSelected(App item) {
                Intent appActivity = new Intent(mActivity, AppActivity.class);
                appActivity.putExtra(AppActivity.APP, item);
                startActivity(appActivity);
            }
        });
        mRecyclerView.setAdapter(featuredAppListAdapter);

        //TODO: load more when bottom reached

        mProgressBar.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
