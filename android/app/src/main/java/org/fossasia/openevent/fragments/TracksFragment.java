package org.fossasia.openevent.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
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
import org.fossasia.openevent.adapters.TracksListAdapter;
import org.fossasia.openevent.api.DataDownloadManager;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.RefreshUiEvent;
import org.fossasia.openevent.events.TracksDownloadEvent;
import org.fossasia.openevent.utils.NetworkUtils;
import org.fossasia.openevent.utils.Utils;
import org.fossasia.openevent.utils.Views;
import org.fossasia.openevent.views.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.realm.RealmResults;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 05-06-2015
 */
public class TracksFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    final private String SEARCH = "searchText";

    private List<Track> tracks = new ArrayList<>();
    private TracksListAdapter tracksListAdapter;

    @BindView(R.id.tracks_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.txt_no_tracks) TextView noTracksView;
    @BindView(R.id.list_tracks) RecyclerView tracksRecyclerView;
    @BindView(R.id.tracks_frame) View windowFrame;

    private String searchText = "";
    private SearchView searchView;

    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();
    private RealmResults<Track> realmResults;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = super.onCreateView(inflater, container, savedInstanceState);

        Utils.registerIfUrlValid(swipeRefreshLayout, this, this::refresh);
        setUpRecyclerView();

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }

        realmResults = realmRepo.getTracks();
        realmResults.addChangeListener((tracks, orderedCollectionChangeSet) -> {
            this.tracks.clear();
            this.tracks.addAll(tracks);

            tracksListAdapter.setCopyOfTracks(tracks);
            tracksListAdapter.notifyDataSetChanged();
            if (!Utils.isEmpty(searchText))
                tracksListAdapter.filter(searchText);
            handleVisibility();
        });

        handleVisibility();

        return view;
    }

    private void setUpRecyclerView() {
        tracksListAdapter = new TracksListAdapter(getContext(), tracks);

        tracksRecyclerView.setHasFixedSize(true);
        tracksRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tracksRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        tracksRecyclerView.setAdapter(tracksListAdapter);

        final StickyRecyclerHeadersDecoration headersDecoration = new StickyRecyclerHeadersDecoration(tracksListAdapter);
        tracksRecyclerView.addItemDecoration(headersDecoration);
        tracksListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecoration.invalidateHeaders();
            }
        });
    }

    public void handleVisibility() {
        if (!tracks.isEmpty()) {
            noTracksView.setVisibility(View.GONE);
            tracksRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noTracksView.setVisibility(View.VISIBLE);
            tracksRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.list_tracks;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.unregisterIfUrlValid(this);

        // Remove listeners to fix memory leak
        realmResults.removeAllChangeListeners();
        if(swipeRefreshLayout != null) swipeRefreshLayout.setOnRefreshListener(null);
        if(searchView != null) searchView.setOnQueryTextListener(null);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        if (isAdded() && searchView != null) {
            bundle.putString(SEARCH, searchText);
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

        inflater.inflate(R.menu.menu_tracks, menu);
        MenuItem item = menu.findItem(R.id.action_search_tracks);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        if (searchText != null && !TextUtils.isEmpty(searchText)) {
            searchView.setQuery(searchText, false);
        }
    }

    @Override
    public boolean onQueryTextChange(String query) {
        searchText = query;
        tracksListAdapter.filter(searchText);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        return false;
    }

    @Subscribe
    public void  refreshData(RefreshUiEvent event) {
        handleVisibility();
    }

    @Subscribe
    public void onTrackDownloadDone(TracksDownloadEvent event) {
        Views.setSwipeRefreshLayout(swipeRefreshLayout, false);

        if (event.isState()) {
            Timber.i("Tracks download completed");
            if (!searchView.getQuery().toString().isEmpty() && !searchView.isIconified()) {
                tracksListAdapter.filter(searchText);
            }
        } else {
            Timber.i("Tracks download failed");
            if (getActivity() != null && windowFrame != null) {
                Snackbar.make(windowFrame, getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).setAction(R.string.retry_download, view -> refresh()).show();
            }
        }
    }

    private void refresh() {
        NetworkUtils.checkConnection(new WeakReference<>(getContext()), new NetworkUtils.NetworkStateReceiverListener() {

            @Override
            public void networkAvailable() {
                // Network is available
                DataDownloadManager.getInstance().downloadTracks();
            }

            @Override
            public void networkUnavailable() {
                OpenEventApp.getEventBus().post(new TracksDownloadEvent(false));
            }
        });
    }

}
