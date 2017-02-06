package me.tombailey.store.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import me.tombailey.store.AppActivity;
import me.tombailey.store.R;
import me.tombailey.store.adapter.AdapterItemSelectedListener;
import me.tombailey.store.adapter.AppListAdapter;
import me.tombailey.store.model.App;

public class AppListFragment extends Fragment {

    public static final String TAG = AppListFragment.class.getName();


    private static final String APPS = "mApps";


    private FragmentActivity mActivity;
    private App[] mApps;

    public static AppListFragment newInstance(App[] apps) {
        AppListFragment appListFragment = new AppListFragment();
        Bundle args = new Bundle();
        args.putParcelableArray(APPS, apps);
        appListFragment.setArguments(args);
        return appListFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();

        Bundle args = getArguments();
        Parcelable[] parcelables = args.getParcelableArray(APPS);
        mApps = new App[parcelables.length];
        System.arraycopy(parcelables, 0, mApps, 0, parcelables.length);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.fragment_app_list, container, false);
        init(content, mApps);
        return content;
    }

    private void init(View content, App[] apps) {
        RecyclerView rv = (RecyclerView) content.findViewById(R.id.app_list_fragment_recycler_view);
        rv.setHasFixedSize(true);

        int columns = getResources().getConfiguration().screenWidthDp / 190;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, columns);
        rv.setLayoutManager(gridLayoutManager);

        AppListAdapter appListAdapter = new AppListAdapter(apps, Glide.with(this), new AdapterItemSelectedListener<App>() {
            @Override
            public void onSelected(App item) {
                Intent appActivity = new Intent(mActivity, AppActivity.class);
                appActivity.putExtra(AppActivity.APP, item);
                startActivity(appActivity);
            }
        });
        rv.setAdapter(appListAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);

        final MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setIconified(true);

                //TODO:

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }
}
