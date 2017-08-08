package org.fossasia.openevent.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
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

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.GenericItemAdapter;
import com.squareup.otto.Subscribe;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.LocationActivity;
import org.fossasia.openevent.adapters.StickyHeaderAdapter;
import org.fossasia.openevent.adapters.items.LocationItem;
import org.fossasia.openevent.api.DataDownloadManager;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.MicrolocationDownloadEvent;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.Utils;
import org.fossasia.openevent.views.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 8/18/2015
 */
public class LocationsFragment extends BaseFragment implements SearchView.OnQueryTextListener {
    final private String SEARCH = "searchText";

    @BindView(R.id.locations_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.list_locations) RecyclerView locationsRecyclerView;
    @BindView(R.id.txt_no_microlocations) TextView noMicrolocationsView;

    private List<Microlocation> mLocations = new ArrayList<>();
    private GenericItemAdapter<Microlocation, LocationItem> locationItemsAdapter;

    private String searchText = "";

    private SearchView searchView;

    private CompositeDisposable compositeDisposable;
    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = super.onCreateView(inflater, container, savedInstanceState);

        Utils.registerIfUrlValid(swipeRefreshLayout, this, this::refresh);

        compositeDisposable = new CompositeDisposable();
        setUpRecyclerView();

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }

        realmRepo.getLocations()
                .addChangeListener((microlocations, orderedCollectionChangeSet) -> {
                    mLocations.clear();
                    mLocations.addAll(realmRepo.getRealmInstance().copyFromRealm(microlocations));
                    locationItemsAdapter.clearModel();
                    locationItemsAdapter.addModel(mLocations);
                    locationItemsAdapter.notifyDataSetChanged();
                    handleVisibility();
                });

        handleVisibility();

        return view;
    }

    private void handleVisibility() {
        if (!mLocations.isEmpty()) {
            noMicrolocationsView.setVisibility(View.GONE);
            locationsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noMicrolocationsView.setVisibility(View.VISIBLE);
            locationsRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.list_locations;
    }

    private void setUpRecyclerView() {
        FastAdapter fastAdapter = new FastAdapter();
        fastAdapter.setHasStableIds(true);

        StickyHeaderAdapter stickyHeaderAdapter = new StickyHeaderAdapter();
        locationItemsAdapter = new GenericItemAdapter<>(LocationItem::new);
        locationItemsAdapter.getItemFilter().withFilterPredicate((item, constraint) -> !item.getModel().getName().toLowerCase().contains(constraint.toString().toLowerCase()));

        fastAdapter.withSelectable(true);
        fastAdapter.withOnClickListener((v, adapter, item, position) -> {
            Intent intent = new Intent(getActivity(), LocationActivity.class);
            intent.putExtra(ConstantStrings.LOCATION_NAME, mLocations.get(position).getName());
            getActivity().startActivity(intent);
            return true;
        });

        locationsRecyclerView.setHasFixedSize(true);
        locationsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        locationsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        locationsRecyclerView.setItemAnimator(null);
        locationsRecyclerView.setAdapter(locationItemsAdapter.wrap(stickyHeaderAdapter.wrap(fastAdapter)));

        final StickyRecyclerHeadersDecoration headersDecoration = new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
        locationsRecyclerView.addItemDecoration(headersDecoration);

        stickyHeaderAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecoration.invalidateHeaders();
            }
        });
    }

    public void setVisibility(Boolean isDownloadDone) {
        if (isDownloadDone) {
            noMicrolocationsView.setVisibility(View.GONE);
            locationsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noMicrolocationsView.setVisibility(View.VISIBLE);
            locationsRecyclerView.setVisibility(View.GONE);
        }
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

        inflater.inflate(R.menu.menu_locations_fragment, menu);
        MenuItem item = menu.findItem(R.id.action_search_locations);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        DrawableCompat.setTint(menu.findItem(R.id.action_search_locations).getIcon(), Color.WHITE);
        searchView.setOnQueryTextListener(this);
        searchView.setQuery(searchText, false);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        locationItemsAdapter.filter(query);
        searchText = query;
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.unregisterIfUrlValid(this);

        if(compositeDisposable != null && !compositeDisposable.isDisposed())
            compositeDisposable.dispose();

        // Remove listeners to fix memory leak
        if(swipeRefreshLayout != null) swipeRefreshLayout.setOnRefreshListener(null);
        if(searchView != null) searchView.setOnQueryTextListener(null);
    }

    @Subscribe
    public void onLocationsDownloadDone(MicrolocationDownloadEvent event) {
        if(swipeRefreshLayout == null)
            return;

        swipeRefreshLayout.setRefreshing(false);
        if (event.isState()) {
            if (!searchView.getQuery().toString().isEmpty() && !searchView.isIconified()) {
                locationItemsAdapter.filter(searchView.getQuery());
            }
            Timber.d("Locations Downloaded");
        } else {
            Snackbar.make(swipeRefreshLayout, getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).setAction(R.string.retry_download, view -> refresh()).show();
        }
    }

    private void refresh() {
        DataDownloadManager.getInstance().downloadMicrolocations();
    }
}
