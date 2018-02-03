package org.fossasia.openevent.core.track;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.api.DataDownloadManager;
import org.fossasia.openevent.common.events.RefreshUiEvent;
import org.fossasia.openevent.common.events.TracksDownloadEvent;
import org.fossasia.openevent.common.network.NetworkUtils;
import org.fossasia.openevent.common.ui.Views;
import org.fossasia.openevent.common.ui.base.BaseFragment;
import org.fossasia.openevent.common.ui.recyclerview.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.config.StrategyRegistry;
import org.fossasia.openevent.data.Track;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

public class TracksFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    private Context context;
    private final List<Track> tracks = new ArrayList<>();
    private TracksListAdapter tracksListAdapter;
    private RecyclerView.AdapterDataObserver adapterDataObserver;

    @BindView(R.id.tracks_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.txt_no_tracks) TextView noTracksView;
    @BindView(R.id.txt_no_result_tracks) protected TextView noResultsTracksView;
    @BindView(R.id.list_tracks) RecyclerView tracksRecyclerView;
    @BindView(R.id.tracks_frame) View windowFrame;

    private String searchText = "";
    private SearchView searchView;

    private TracksFragmentViewModel tracksFragmentViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        context = getContext();
        View view = super.onCreateView(inflater, container, savedInstanceState);

        Utils.registerIfUrlValid(swipeRefreshLayout, this, this::refresh);
        setUpRecyclerView();

        tracksFragmentViewModel = ViewModelProviders.of(this).get(TracksFragmentViewModel.class);
        searchText = tracksFragmentViewModel.getSearchText();
        loadTracks();
        handleVisibility();

        return view;
    }

    private void loadTracks() {
        tracksFragmentViewModel.getTracks(searchText).observe(this, tracksList -> {
            tracks.clear();
            tracks.addAll(tracksList);
            tracksListAdapter.notifyDataSetChanged();
            handleVisibility();
        });
    }

    private void setUpRecyclerView() {
        tracksListAdapter = new TracksListAdapter(context, tracks);

        tracksRecyclerView.setHasFixedSize(true);
        tracksRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        tracksRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        tracksRecyclerView.setAdapter(tracksListAdapter);

        final StickyRecyclerHeadersDecoration headersDecoration = new StickyRecyclerHeadersDecoration(tracksListAdapter);
        tracksRecyclerView.addItemDecoration(headersDecoration);
        adapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecoration.invalidateHeaders();
            }
        };
        tracksListAdapter.registerAdapterDataObserver(adapterDataObserver);
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
        tracksListAdapter.unregisterAdapterDataObserver(adapterDataObserver);

        if(swipeRefreshLayout != null) swipeRefreshLayout.setOnRefreshListener(null);
        if(searchView != null) searchView.setOnQueryTextListener(null);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        if (isAdded() && searchView != null && tracksFragmentViewModel != null) {
            tracksFragmentViewModel.setSearchText(searchText);
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
        searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(this);
        if (searchText != null && !TextUtils.isEmpty(searchText)) {
            searchView.setQuery(searchText, false);
        }
    }

    @Override
    public boolean onQueryTextChange(String query) {
        searchText = query;
        loadTracks();
        tracksListAdapter.animateTo(tracks);
        Utils.displayNoResults(noResultsTracksView, tracksRecyclerView, noTracksView, tracksListAdapter.getItemCount());

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
                loadTracks();
                tracksListAdapter.animateTo(tracks);
            }
        } else {
            Timber.i("Tracks download failed");
            if (getActivity() != null && windowFrame != null) {
                Snackbar.make(windowFrame, getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).setAction(R.string.retry_download, view -> refresh()).show();
            }
        }
    }

    private void refresh() {
        NetworkUtils.checkConnection(new WeakReference<>(context), new NetworkUtils.NetworkStateReceiverListener() {

            @Override
            public void networkAvailable() {
                // Network is available
                DataDownloadManager.getInstance().downloadTracks();
            }

            @Override
            public void networkUnavailable() {
                StrategyRegistry.getInstance().getEventBusStrategy().getEventBus().post(new TracksDownloadEvent(false));
            }
        });
    }

}
