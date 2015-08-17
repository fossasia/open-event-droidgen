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
import android.util.Log;
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
import org.fossasia.openevent.activities.TracksActivity;
import org.fossasia.openevent.adapters.TracksListAdapter;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DataDownload;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.RefreshUiEvent;
import org.fossasia.openevent.events.TracksDownloadEvent;
import org.fossasia.openevent.utils.IntentStrings;
import org.fossasia.openevent.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MananWason on 05-06-2015.
 */
public class TracksFragment extends Fragment implements SearchView.OnQueryTextListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView tracksRecyclerView;
    private TracksListAdapter tracksListAdapter;
    private List<Track> mTracks;


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

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.tracks_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DataDownload download = new DataDownload();
                dbSingleton.clearDatabase(DbContract.Tracks.TABLE_NAME);
                download.downloadTracks();

            }
        });

        tracksRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        tracksRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(view.getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String title = ((TextView) view.findViewById(R.id.track_title)).getText().toString();
                        Intent intent = new Intent(getActivity(), TracksActivity.class);
                        intent.putExtra(IntentStrings.TRACK, title);
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
        inflater.inflate(R.menu.menu_tracks, menu);
        final MenuItem item = menu.findItem(R.id.action_search_tracks);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        DbSingleton dbSingleton = DbSingleton.getInstance();

        mTracks = dbSingleton.getTrackList();
        final List<Track> filteredModelList = filter(mTracks, query);
        Log.d("xyz", mTracks.size() + " " + filteredModelList.size());

        tracksListAdapter.animateTo(filteredModelList);
        tracksRecyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<Track> filter(List<Track> tracks, String query) {
        query = query.toLowerCase();

        final List<Track> filteredTracksList = new ArrayList<>();
        for (Track track : tracks) {
            final String text = track.getName().toLowerCase();
            if (text.contains(query)) {
                filteredTracksList.add(track);
            }
        }
        return filteredTracksList;
    }

    @Subscribe
    public void RefreshData(RefreshUiEvent event) {
        tracksListAdapter.refresh();
    }

    @Subscribe
    public void TrackDownloadDone(TracksDownloadEvent event) {

        swipeRefreshLayout.setRefreshing(false);
        if (event.isState()) {
            tracksListAdapter.refresh();

        } else {
            Snackbar.make(getView(), getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).show();

        }
    }
}
