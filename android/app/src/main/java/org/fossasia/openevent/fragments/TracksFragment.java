package org.fossasia.openevent.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import org.fossasia.openevent.adapters.TracksListAdapter;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.RefreshUiEvent;
import org.fossasia.openevent.events.TracksDownloadEvent;
import org.fossasia.openevent.utils.NetworkUtils;
import org.fossasia.openevent.utils.ShowNotificationSnackBar;
import org.fossasia.openevent.views.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.realm.RealmResults;

/**
 * User: MananWason
 * Date: 05-06-2015
 */
public class TracksFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    final private String SEARCH = "searchText";

    private List<Track> mTracks = new ArrayList<>();
    private TracksListAdapter tracksListAdapter;

    @BindView(R.id.tracks_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.txt_no_tracks) TextView noTracksView;
    @BindView(R.id.list_tracks) RecyclerView tracksRecyclerView;
    @BindView(R.id.tracks_frame) View windowFrame;

    private String searchText = "";

    private SearchView searchView;

    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();
    private RealmResults<Track> realmResults;

    private Snackbar snackbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = super.onCreateView(inflater, container, savedInstanceState);

        OpenEventApp.getEventBus().register(this);

        handleVisibility();
        swipeRefreshLayout.setOnRefreshListener(this::refresh);

        tracksRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        tracksRecyclerView.setLayoutManager(linearLayoutManager);
        tracksListAdapter = new TracksListAdapter(getContext(), mTracks);
        tracksRecyclerView.setAdapter(tracksListAdapter);

        final StickyRecyclerHeadersDecoration headersDecoration = new StickyRecyclerHeadersDecoration(tracksListAdapter);
        tracksRecyclerView.addItemDecoration(headersDecoration);
        tracksListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override public void onChanged() {
                headersDecoration.invalidateHeaders();
            }
        });

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }

        realmResults = realmRepo.getTracks();
        realmResults.addChangeListener((tracks, orderedCollectionChangeSet) -> {
            mTracks.clear();
            mTracks.addAll(tracks);

            tracksListAdapter.notifyDataSetChanged();
            handleVisibility();
        });

        return view;
    }

    public void handleVisibility() {
        if (!mTracks.isEmpty()) {
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
        OpenEventApp.getEventBus().unregister(this);

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
        tracksListAdapter.getFilter().filter(searchText);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        return false;
    }

    @Subscribe
    public void refreshData(RefreshUiEvent event) {
        handleVisibility();
    }

    @Subscribe
    public void onTrackDownloadDone(TracksDownloadEvent event) {
        if(swipeRefreshLayout!=null)
            swipeRefreshLayout.setRefreshing(false);
        if (event.isState()) {
            if (!searchView.getQuery().toString().isEmpty() && !searchView.isIconified()) {
                tracksListAdapter.getFilter().filter(searchView.getQuery());
            }
        } else {
            if (getActivity() != null) {
                Snackbar.make(windowFrame, getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).setAction(R.string.retry_download, view -> refresh()).show();
            }
        }
    }

    private void refresh() {
        NetworkUtils.checkConnectionTracks(new WeakReference<>(getContext()), new NetworkUtils.NetworkStateReceiverListener() {
            @Override
            public void activeConnection() {
                //internet is working
            }

            @Override
            public void inactiveConnection() {
                //internet not working
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                showNotification();
            }

            @Override
            public void networkAvailable() {
                //network is available
            }

            @Override
            public void networkUnavailable() {
                //network is unavailable
                if (snackbar!=null && snackbar.isShown()) {
                    snackbar.dismiss();
                }
            }
        });
    }

    public void showNotification() {
        ShowNotificationSnackBar showNotificationSnackBar = new ShowNotificationSnackBar(getContext(),getView(),swipeRefreshLayout) {
            @Override
            public void refreshClicked() {
                refresh();
            }
        };
        //show snackbar will be useful if user have blocked notification for this app
        snackbar = showNotificationSnackBar.showSnackBar();
        showNotificationSnackBar.buildNotification();
    }
}
