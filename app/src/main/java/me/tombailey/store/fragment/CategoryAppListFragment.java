package me.tombailey.store.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import me.tombailey.store.AppActivity;
import me.tombailey.store.R;
import me.tombailey.store.StoreApp;
import me.tombailey.store.adapter.AdapterItemSelectedListener;
import me.tombailey.store.adapter.FeaturedAppListAdapter;
import me.tombailey.store.exception.NoAppsException;
import me.tombailey.store.http.Proxy;
import me.tombailey.store.model.App;
import me.tombailey.store.model.Category;
import nucleus.factory.RequiresPresenter;
import nucleus.view.NucleusSupportFragment;

/**
 * Created by Tom on 29/11/2016.
 */

@RequiresPresenter(CategoryAppListPresenter.class)
public class CategoryAppListFragment extends NucleusSupportFragment<CategoryAppListPresenter> {

    public static final String CATEGORY = "category";


    private View mProgressBar;
    private View mErrorView;
    private RecyclerView mRecyclerView;

    private Category mCategory;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mCategory = Category.valueOf(getArguments().getString(CATEGORY));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.fragment_featured_app_list, container, false);

        mProgressBar = content.findViewById(R.id.featured_app_list_fragment_progress_bar);
        mErrorView = content.findViewById(R.id.featured_app_list_fragment_linear_layout_error);
        mRecyclerView = (RecyclerView) content.findViewById(R.id.featured_app_list_fragment_recycler_view);

        init();

        getPresenter().loadApps(mCategory);

        return content;
    }

    public void init() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
    }

    public void showError(Throwable throwable) {
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);

        TextView tvErrorDescription = (TextView) mErrorView.findViewById(R.id.featured_app_list_fragment_text_view_error_description);
        if (throwable instanceof NoAppsException) {
            tvErrorDescription.setText(R.string.no_apps);
        } else {
            tvErrorDescription.setText(R.string.generic_error);
        }

        mErrorView.findViewById(R.id.featured_app_list_fragment_button_error_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init();
                getPresenter().start(CategoryAppListPresenter.LOAD_APPS);
            }
        });
    }

    public void showApps(App[] apps, Proxy proxy) {
        mRecyclerView.setHasFixedSize(true);

        int columns = getResources().getConfiguration().screenWidthDp / 490;
        if (columns < 1) {
            columns = 1;
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), columns);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        FeaturedAppListAdapter featuredAppListAdapter = new FeaturedAppListAdapter(apps,
                Glide.with(getActivity()), proxy, StoreApp.getInstance().getTempCacheDirectory(),
                new AdapterItemSelectedListener<App>() {
                    @Override
                    public void onSelected(App item) {
                        Intent appActivity = new Intent(getActivity(), AppActivity.class);
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


    public static CategoryAppListFragment newInstance(Category category) {
        CategoryAppListFragment categoryAppListFragment = new CategoryAppListFragment();
        Bundle args = new Bundle();
        args.putString(CATEGORY, category.toString());
        categoryAppListFragment.setArguments(args);
        return categoryAppListFragment;
    }

}
