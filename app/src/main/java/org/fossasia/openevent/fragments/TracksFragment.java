package org.fossasia.openevent.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.squareup.otto.Subscribe;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.TracksActivity;
import org.fossasia.openevent.adapters.TracksListAdapter;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DataDownload;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.RefreshUiEvent;
import org.fossasia.openevent.events.TracksDownloadEvent;
import org.fossasia.openevent.utils.IntentStrings;

import java.util.List;

/**
 * User: MananWason
 * Date: 05-06-2015
 */
public class TracksFragment extends Fragment implements SearchView.OnQueryTextListener {

    final private String SEARCH = "searchText";

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView tracksRecyclerView;

    private TracksListAdapter tracksListAdapter;

    private List<Track> mTracks;
    private String searchText = "";

    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.list_tracks, container, false);
        OpenEventApp.getEventBus().register(this);
        tracksRecyclerView = (RecyclerView) view.findViewById(R.id.list_tracks);
        final DbSingleton dbSingleton = DbSingleton.getInstance();
        mTracks = dbSingleton.getTrackList();

        tracksListAdapter = new TracksListAdapter(mTracks);
        tracksRecyclerView.setAdapter(tracksListAdapter);
        tracksListAdapter.setOnClickListener(new TracksListAdapter.SetOnClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Track model = (Track) tracksListAdapter.getItem(position);
                String trackTitle = model.getName();
                Intent intent = new Intent(getContext(), TracksActivity.class);
                intent.putExtra(IntentStrings.TRACK, trackTitle);
                startActivity(intent);
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.tracks_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (haveNetworkConnection()) {
                    DataDownload download = new DataDownload();
                    download.downloadTracks();
                } else {
                    OpenEventApp.getEventBus().post(new TracksDownloadEvent(false));
                }
            }
        });

        tracksRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
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
        switch (item.getItemId()) {
            case R.id.share_tracks_url:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, Urls.WEB_APP_URL_BASIC + Urls.TRACKS);
                intent.putExtra(Intent.EXTRA_SUBJECT, R.string.share_links);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_links)));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_tracks, menu);
        MenuItem item = menu.findItem(R.id.action_search_tracks);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        if (searchText != null) {
            searchView.setQuery(searchText, false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (!TextUtils.isEmpty(query)) {
            searchText = query;
            tracksListAdapter.getFilter().filter(searchText);
        } else {
            tracksListAdapter.refresh();
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Subscribe
    public void RefreshData(RefreshUiEvent event) {

        if (searchText.length() == 0) {
            tracksListAdapter.refresh();
        }
    }

    @Subscribe
    public void TrackDownloadDone(TracksDownloadEvent event) {

        swipeRefreshLayout.setRefreshing(false);
        if (event.isState()) {
            tracksListAdapter.refresh();

        } else {
            if (getActivity() != null) {
                Snackbar.make(getView(), getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
