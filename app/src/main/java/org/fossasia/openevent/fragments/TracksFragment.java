package org.fossasia.openevent.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.adapters.TracksListAdapter;
import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.TracksActivity;
import org.fossasia.openevent.dbutils.DataDownload;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.RefreshUiEvent;
import org.fossasia.openevent.events.TracksDownloadEvent;
import org.fossasia.openevent.utils.RecyclerItemClickListener;

/**
 * Created by MananWason on 05-06-2015.
 */
public class TracksFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView tracksRecyclerView;
    private TracksListAdapter tracksListAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.list_tracks, container, false);
        Bus bus = OpenEventApp.getEventBus();
        bus.register(this);
        tracksRecyclerView = (RecyclerView) view.findViewById(R.id.list_tracks);
        final DbSingleton dbSingleton = DbSingleton.getInstance();
        tracksListAdapter = new TracksListAdapter(dbSingleton.getTrackList());
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
                        intent.putExtra("TRACK", title);
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

    @Subscribe
    public void RefreshData(RefreshUiEvent event) {
        tracksListAdapter.refresh();
        Log.d("counter", "REfresh");
    }

    @Subscribe
    public void TrackDownloadDone(TracksDownloadEvent event) {

        swipeRefreshLayout.setRefreshing(false);
        if (event.isState()) {
            tracksListAdapter.refresh();
            Log.d("counter", "REfresh done");

        } else {
            Snackbar.make(getView(), "Couldn't Refresh", Snackbar.LENGTH_LONG).show();
            Log.d("counter", "REfresh not done");

        }
    }
}
