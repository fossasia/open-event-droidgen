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
import org.fossasia.openevent.activities.LocationActivtiy;
import org.fossasia.openevent.adapters.LocationsListAdapter;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.dbutils.DataDownload;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.MicrolocationDownloadEvent;
import org.fossasia.openevent.events.RefreshUiEvent;
import org.fossasia.openevent.utils.IntentStrings;
import org.fossasia.openevent.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MananWason on 8/18/2015.
 */
public class LocationsFragment extends Fragment implements SearchView.OnQueryTextListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView locationsRecyclerView;
    private LocationsListAdapter locationsListAdapter;
    private List<Microlocation> mLocations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.list_locations, container, false);
        OpenEventApp.getEventBus().register(this);
        locationsRecyclerView = (RecyclerView) view.findViewById(R.id.list_locations);
        final DbSingleton dbSingleton = DbSingleton.getInstance();
        mLocations = dbSingleton.getMicrolocationsList();
        locationsListAdapter = new LocationsListAdapter(mLocations);
        locationsRecyclerView.setAdapter(locationsListAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.locations_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DataDownload download = new DataDownload();
                download.downloadMicrolocations();
            }
        });

        locationsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        locationsRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(view.getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String title = ((TextView) view.findViewById(R.id.location_name)).getText().toString();
                        Intent intent = new Intent(getActivity(), LocationActivtiy.class);
                        intent.putExtra(IntentStrings.MICROLOCATIONS, title);
                        startActivity(intent);
                    }
                })
        );
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_locations_fragment, menu);
        final MenuItem item = menu.findItem(R.id.action_search_locations);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        DbSingleton dbSingleton = DbSingleton.getInstance();

        mLocations = dbSingleton.getMicrolocationsList();
        final List<Microlocation> filteredModelList = filter(mLocations, query);

        locationsListAdapter.animateTo(filteredModelList);
        locationsRecyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<Microlocation> filter(List<Microlocation> locations, String query) {
        query = query.toLowerCase();

        final List<Microlocation> filteredLocationsList = new ArrayList<>();
        for (Microlocation microlocation : locations) {
            final String text = microlocation.getName().toLowerCase();
            if (text.contains(query)) {
                filteredLocationsList.add(microlocation);
            }
        }
        return filteredLocationsList;
    }

    @Subscribe
    public void RefreshData(RefreshUiEvent event) {
        locationsListAdapter.refresh();
    }

    @Subscribe
    public void LocationsDownloadDone(MicrolocationDownloadEvent event) {

        swipeRefreshLayout.setRefreshing(false);
        if (event.isState()) {
            locationsListAdapter.refresh();

        } else {
            if(getActivity()!=null) {
                Snackbar.make(getView(), getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
