package org.fossasia.openevent.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.LocationActivity;
import org.fossasia.openevent.adapters.LocationsListAdapter;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.dbutils.DataDownloadManager;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.MicrolocationDownloadEvent;
import org.fossasia.openevent.events.RefreshUiEvent;
import org.fossasia.openevent.utils.IntentStrings;

/**
 * User: MananWason
 * Date: 8/18/2015
 */
public class LocationsFragment extends Fragment implements SearchView.OnQueryTextListener {
    final private String SEARCH = "searchText";

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView locationsRecyclerView;

    private LocationsListAdapter locationsListAdapter;

    private String searchText = "";

    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.list_locations, container, false);
        OpenEventApp.getEventBus().register(this);
        locationsRecyclerView = (RecyclerView) view.findViewById(R.id.list_locations);
        TextView noMicrolocationsView = (TextView) view.findViewById(R.id.txt_no_microlocations);
        final DbSingleton dbSingleton = DbSingleton.getInstance();
        locationsListAdapter = new LocationsListAdapter(dbSingleton.getMicrolocationsList());
        locationsRecyclerView.setAdapter(locationsListAdapter);
        locationsListAdapter.setOnClickListener(new LocationsListAdapter.SetOnClickListener() {
            @Override
            public void onItemClick(int position, View view) {

                Microlocation model = (Microlocation) locationsListAdapter.getItem(position);
                String title = model.getName();
                Intent intent = new Intent(getActivity(), LocationActivity.class);
                intent.putExtra(IntentStrings.MICROLOCATIONS, title);
                startActivity(intent);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.locations_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DataDownloadManager.getInstance().downloadMicrolocations();
            }
        });

        locationsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }
        if (locationsListAdapter.getItemCount() != 0) {
            noMicrolocationsView.setVisibility(View.GONE);
            locationsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noMicrolocationsView.setVisibility(View.VISIBLE);
            locationsRecyclerView.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        if (isAdded()) {
            if (searchView != null) {
                bundle.putString(SEARCH, searchText);
            }
        }
        super.onSaveInstanceState(bundle);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_locations_fragment, menu);
        MenuItem item = menu.findItem(R.id.action_search_locations);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        searchView.setQuery(searchText, false);

    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (!TextUtils.isEmpty(query)) {
            locationsListAdapter.getFilter().filter(query);
        } else {
            locationsListAdapter.refresh();
        }
        searchText = query;
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Subscribe
    public void onDataRefreshed(RefreshUiEvent event) {
        if (TextUtils.isEmpty(searchText)) {
            locationsListAdapter.refresh();
        }
    }

    @Subscribe
    public void LocationsDownloadDone(MicrolocationDownloadEvent event) {

        swipeRefreshLayout.setRefreshing(false);
        if (event.isState()) {
            locationsListAdapter.refresh();

        } else {
            if (getActivity() != null) {
                Snackbar.make(getView(), getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
