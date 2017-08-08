package org.fossasia.openevent.fragments;

import android.content.Intent;
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

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.GenericItemAdapter;
import com.squareup.otto.Subscribe;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.TrackSessionsActivity;
import org.fossasia.openevent.adapters.StickyHeaderAdapter;
import org.fossasia.openevent.adapters.items.TrackItem;
import org.fossasia.openevent.api.DataDownloadManager;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.RefreshUiEvent;
import org.fossasia.openevent.events.TracksDownloadEvent;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.NetworkUtils;
import org.fossasia.openevent.utils.ShowNotificationSnackBar;
import org.fossasia.openevent.utils.Utils;
import org.fossasia.openevent.views.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmResults;

/**
 * User: MananWason
 * Date: 05-06-2015
 */
public class TracksFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    final private String SEARCH = "searchText";

    private List<Track> mTracks = new ArrayList<>();
    private GenericItemAdapter<Track, TrackItem> trackItemsAdapter;

    @BindView(R.id.tracks_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.txt_no_tracks) TextView noTracksView;
    @BindView(R.id.list_tracks) RecyclerView tracksRecyclerView;
    @BindView(R.id.tracks_frame) View windowFrame;

    private String searchText = "";

    private SearchView searchView;

    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();
    private RealmResults<Track> realmResults;
    private Disposable disposable;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = super.onCreateView(inflater, container, savedInstanceState);

        handleVisibility();
        setUpRecyclerView();

        Utils.registerIfUrlValid(swipeRefreshLayout, this, this::refresh);

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }

        disposable = realmRepo.getTracksObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(realmResults -> {
                    this.realmResults = realmResults;
                    this.realmResults.addChangeListener((tracks, orderedCollectionChangeSet) -> {
                        mTracks.clear();
                        mTracks.addAll(realmRepo.getRealmInstance().copyFromRealm(tracks));
                        trackItemsAdapter.clearModel();
                        trackItemsAdapter.addModel(mTracks);
                        trackItemsAdapter.notifyDataSetChanged();
                        handleVisibility();
                    });
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

    private void setUpRecyclerView() {
        FastAdapter fastAdapter = new FastAdapter();
        fastAdapter.setHasStableIds(true);

        StickyHeaderAdapter stickyHeaderAdapter = new StickyHeaderAdapter();
        trackItemsAdapter = new GenericItemAdapter<>(TrackItem::new);
        trackItemsAdapter.getItemFilter().withFilterPredicate((item, constraint) -> !item.getModel().getName().toLowerCase().contains(constraint.toString().toLowerCase()));

        fastAdapter.withSelectable(true);
        fastAdapter.withOnClickListener((v, adapter, item, position) -> {
            Intent intent = new Intent(getActivity(), TrackSessionsActivity.class);
            intent.putExtra(ConstantStrings.TRACK, mTracks.get(position).getName());
            intent.putExtra(ConstantStrings.TRACK_ID, mTracks.get(position).getId());
            getActivity().startActivity(intent);
            return true;
        });

        tracksRecyclerView.setHasFixedSize(true);
        tracksRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tracksRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        tracksRecyclerView.setItemAnimator(null);
        tracksRecyclerView.setAdapter(trackItemsAdapter.wrap(stickyHeaderAdapter.wrap(fastAdapter)));

        final StickyRecyclerHeadersDecoration headersDecoration = new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
        tracksRecyclerView.addItemDecoration(headersDecoration);

        stickyHeaderAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecoration.invalidateHeaders();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.unregisterIfUrlValid(this);

        // Remove listeners to fix memory leak
        realmResults.removeAllChangeListeners();
        if(swipeRefreshLayout != null) swipeRefreshLayout.setOnRefreshListener(null);
        if(searchView != null) searchView.setOnQueryTextListener(null);

        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();
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
        trackItemsAdapter.filter(searchText);
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
                trackItemsAdapter.filter(searchView.getQuery());
            }
        } else {
            if (getActivity() != null) {
                Snackbar.make(windowFrame, getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).setAction(R.string.retry_download, view -> refresh()).show();
            }
        }
    }

    private void refresh() {
        NetworkUtils.checkConnection(new WeakReference<>(getContext()), new NetworkUtils.NetworkStateReceiverListener() {
            @Override
            public void activeConnection() {
                //Internet is working
                DataDownloadManager.getInstance().downloadTracks();
            }

            @Override
            public void inactiveConnection() {
                //set is refreshing false as let user to login
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                //Device is connected to WI-FI or Mobile Data but Internet is not working
                ShowNotificationSnackBar showNotificationSnackBar = new ShowNotificationSnackBar(getContext(),getView(),swipeRefreshLayout) {
                    @Override
                    public void refreshClicked() {
                        refresh();
                    }
                };
                //show snackbar will be useful if user have blocked notification for this app
                showNotificationSnackBar.showSnackBar();
                //show notification (Only when connected to WiFi)
                showNotificationSnackBar.buildNotification();
            }

            @Override
            public void networkAvailable() {
                // Network is available but we need to wait for activity
            }

            @Override
            public void networkUnavailable() {
                OpenEventApp.getEventBus().post(new TracksDownloadEvent(false));
            }
        });

    }

}
