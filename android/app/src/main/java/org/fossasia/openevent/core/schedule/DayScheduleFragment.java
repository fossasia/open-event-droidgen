package org.fossasia.openevent.core.schedule;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import org.fossasia.openevent.common.ConstantStrings;
import org.fossasia.openevent.common.api.DataDownloadManager;
import org.fossasia.openevent.common.events.SessionDownloadEvent;
import org.fossasia.openevent.common.network.NetworkUtils;
import org.fossasia.openevent.common.ui.Views;
import org.fossasia.openevent.common.ui.base.BaseFragment;
import org.fossasia.openevent.common.ui.recyclerview.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import org.fossasia.openevent.common.utils.SortOrder;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.config.StrategyRegistry;
import org.fossasia.openevent.core.bookmark.OnBookmarkSelectedListener;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.repository.RealmDataRepository;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import io.realm.RealmResults;
import timber.log.Timber;

public class DayScheduleFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    final private String SEARCH = "searchText";

    private Context context;
    private String searchText = "";
    private SearchView searchView;

    @BindView(R.id.schedule_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.list_schedule) RecyclerView dayRecyclerView;
    @BindView(R.id.txt_no_schedule) TextView noSchedule;
    @BindView(R.id.txt_no_result_schedule) protected TextView noResultsSchedule;

    private List<Session> sessions = new ArrayList<>();
    private List<Session> filteredSessions = new ArrayList<>();
    private DayScheduleAdapter dayScheduleAdapter;
    private OnBookmarkSelectedListener onBookmarkSelectedListener;

    private String date;
    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();
    private RealmResults<Session> realmResults;
    private RecyclerView.AdapterDataObserver adapterDataObserver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            date = getArguments().getString(ConstantStrings.EVENT_DAY, "");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        context = getContext();
        View view = super.onCreateView(inflater, container, savedInstanceState);

        Utils.registerIfUrlValid(swipeRefreshLayout, this, this::refresh);
        setUpRecyclerView();

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }

        realmResults = realmRepo.getSessionsByDate(date, SortOrder.sortTypeSchedule());
        realmResults.addChangeListener((sortedSessions, orderedCollectionChangeSet) -> {
            sessions.clear();
            sessions.addAll(sortedSessions);

            filteredSessions.clear();
            filteredSessions.addAll(sortedSessions);

            dayScheduleAdapter.setCopy(sortedSessions);
            dayScheduleAdapter.notifyDataSetChanged();
            if (!Utils.isEmpty(searchText))
                dayScheduleAdapter.filter(searchText);

            if(SortOrder.sortOrderSchedule() == SortOrder.SORT_ORDER_DESCENDING)
                Collections.reverse(filteredSessions);

            handleVisibility();
        });

        handleVisibility();

        return view;
    }

    private void setUpRecyclerView() {
        dayScheduleAdapter = new DayScheduleAdapter(filteredSessions, context);
        dayScheduleAdapter.setOnBookmarkSelectedListener(onBookmarkSelectedListener);

        dayRecyclerView.setHasFixedSize(true);
        dayRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        dayRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        dayRecyclerView.setAdapter(dayScheduleAdapter);
        dayScheduleAdapter.setEventDate(date);

        final StickyRecyclerHeadersDecoration headersDecoration = new StickyRecyclerHeadersDecoration(dayScheduleAdapter);
        dayRecyclerView.addItemDecoration(headersDecoration);
        adapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecoration.invalidateHeaders();
            }
        };
        dayScheduleAdapter.registerAdapterDataObserver(adapterDataObserver);
    }

    public void filter(List<String> selectedTracks) {
        if(dayScheduleAdapter == null)
            return;

        realmRepo.getSessionsByDate(date, SortOrder.sortTypeSchedule())
                .addChangeListener((sortedSessions, orderedCollectionChangeSet) -> {
                    sessions.clear();
                    sessions.addAll(sortedSessions);

                    filteredSessions.clear();
                    if (selectedTracks.isEmpty()) {
                        filteredSessions.addAll(sessions);
                    } else {
                        for(int i = 0; i < sessions.size() ; i++) {
                            String trackName = sessions.get(i).getTrack().getName();
                            if (selectedTracks.contains(trackName)) {
                                filteredSessions.add(sessions.get(i));
                            }
                        }

                        if(searchText!=null)
                            dayScheduleAdapter.filter(searchText);
                    }

                    if(SortOrder.sortOrderSchedule() == SortOrder.SORT_ORDER_DESCENDING)
                        Collections.reverse(filteredSessions);

                    dayScheduleAdapter.notifyDataSetChanged();

                    handleVisibility();
                });
    }

    private void handleVisibility() {
        if (dayRecyclerView != null && noSchedule != null) {
            if (!filteredSessions.isEmpty()) {
                noSchedule.setVisibility(View.GONE);
            } else {
                noSchedule.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.list_schedule;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.unregisterIfUrlValid(this);
        dayScheduleAdapter.unregisterAdapterDataObserver(adapterDataObserver);

        // Remove listeners to fix memory leak
        realmResults.removeAllChangeListeners();
        if(swipeRefreshLayout != null) swipeRefreshLayout.setOnRefreshListener(null);
        if(searchView != null) searchView.setOnQueryTextListener(null);
    }

    @Subscribe
    public void onSessionsDownloadDone(SessionDownloadEvent event) {
        Views.setSwipeRefreshLayout(swipeRefreshLayout, false);

        if (event.isState()) {
            Timber.i("Schedule download completed");
            if (searchView != null && !searchView.getQuery().toString().isEmpty() && !searchView.isIconified()) {
                dayScheduleAdapter.filter(searchView.getQuery().toString());
            }
        } else {
            Timber.i("Schedule download failed");
            if (getActivity() != null && swipeRefreshLayout != null) {
                Snackbar.make(swipeRefreshLayout, getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).setAction(R.string.retry_download, view -> refresh()).show();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        if (isAdded() && searchView != null) {
            bundle.putString(SEARCH, searchText);
        }
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        searchText = "";
        inflater.inflate(R.menu.menu_schedule, menu);

        MenuItem item = menu.findItem(R.id.action_search_schedule);
        searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(this);

        if (searchText != null && !TextUtils.isEmpty(searchText))
            searchView.setQuery(searchText, false);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        searchText = query;

        dayScheduleAdapter.filter(searchText);
        Utils.displayNoResults(noResultsSchedule, dayRecyclerView, noSchedule, dayScheduleAdapter.getItemCount());

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        return false;
    }

    private void refresh() {
        NetworkUtils.checkConnection(new WeakReference<>(context), new NetworkUtils.NetworkStateReceiverListener() {

            @Override
            public void networkAvailable() {
                // Network is available
                DataDownloadManager.getInstance().downloadSession();
            }

            @Override
            public void networkUnavailable() {
                StrategyRegistry.getInstance().getEventBusStrategy().getEventBus().post(new SessionDownloadEvent(false));
            }
        });
    }

    public void setOnBookmarkSelectedListener(OnBookmarkSelectedListener onBookmarkSelectedListener) {
        this.onBookmarkSelectedListener = onBookmarkSelectedListener;
    }

    public void clearOnBookmarkSelectedListener() {
        this.onBookmarkSelectedListener = null;
    }
}
