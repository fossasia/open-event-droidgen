package org.fossasia.openevent.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import org.fossasia.openevent.adapters.DayScheduleAdapter;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.DataDownloadManager;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.RefreshUiEvent;
import org.fossasia.openevent.events.SessionDownloadEvent;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.NetworkUtils;
import org.fossasia.openevent.utils.ShowNotificationSnackBar;
import org.fossasia.openevent.utils.SortOrder;
import org.fossasia.openevent.views.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Manan Wason on 17/06/16.
 */
public class DayScheduleFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    final private String SEARCH = "searchText";

    private String searchText = "";

    private SearchView searchView;

    @BindView(R.id.schedule_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.list_schedule) RecyclerView dayRecyclerView;
    @BindView(R.id.txt_no_schedule) TextView noSchedule;

    private List<Session> mSessions = new ArrayList<>();
    private List<Session> mSessionsFiltered = new ArrayList<>();
    private DayScheduleAdapter dayScheduleAdapter;

    private String date;
    private CompositeDisposable compositeDisposable;
    private String[] mTracksNames;
    private boolean[] mSelectedTracks;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        date = getArguments().getString(ConstantStrings.EVENT_DAY, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = super.onCreateView(inflater, container, savedInstanceState);

        compositeDisposable = new CompositeDisposable();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            refresh();
            if(mSelectedTracks != null && mTracksNames != null) {
                filter(mTracksNames,mSelectedTracks);
            }
        });

        dayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        dayScheduleAdapter = new DayScheduleAdapter(mSessionsFiltered, getContext());
        dayRecyclerView.setAdapter(dayScheduleAdapter);
        dayScheduleAdapter.setEventDate(date);

        final StickyRecyclerHeadersDecoration headersDecoration = new StickyRecyclerHeadersDecoration(dayScheduleAdapter);
        dayRecyclerView.addItemDecoration(headersDecoration);
        dayScheduleAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override public void onChanged() {
                headersDecoration.invalidateHeaders();
            }
        });

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }

        compositeDisposable.add(DbSingleton.getInstance().getSessionsByDateObservable(date, SortOrder.sortOrderSchedule(getActivity()))
                .subscribe(sortedSessions -> {
                    mSessions.clear();
                    mSessions.addAll(sortedSessions);
                    mSessionsFiltered.clear();
                    mSessionsFiltered.addAll(sortedSessions);
                    handleVisibility();
                }));

        handleVisibility();

        return view;
    }

    public void filter(String[] tracksNames, boolean[] isSelectedTrack) {
        mSessionsFiltered.clear();

        for(int i=0 ; i<mSessions.size() ; i++) {
            String trackName = mSessions.get(i).getTrack().getName();
            for(int j=0;j<isSelectedTrack.length; j++){

                String trackName1 = tracksNames[j];
                if(trackName1.equals(trackName) && isSelectedTrack[j]){
                    mSessionsFiltered.add(mSessions.get(i));
                    break;
                }
            }
        }
        if(mSessionsFiltered.size()==0){
            mSessionsFiltered.addAll(mSessions);
        }

        if(searchText!=null)
            dayScheduleAdapter.getFilter().filter(searchText);

        dayScheduleAdapter.notifyDataSetChanged();
        handleVisibility();
    }

    private void handleVisibility() {
        if (dayRecyclerView != null && noSchedule != null) {
            if (!mSessionsFiltered.isEmpty()) {
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
        if(compositeDisposable != null && !compositeDisposable.isDisposed())
            compositeDisposable.dispose();

        // Remove listeners to fix memory leak
        if(swipeRefreshLayout != null) swipeRefreshLayout.setOnRefreshListener(null);
        if(searchView != null) searchView.setOnQueryTextListener(null);
    }

    @Override
    public void onStart() {
        OpenEventApp.getEventBus().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        OpenEventApp.getEventBus().unregister(this);
        super.onStop();
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

        searchText = "";
        inflater.inflate(R.menu.menu_schedule, menu);
        MenuItem item = menu.findItem(R.id.action_search_schedule);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        if (searchText != null) {
            searchView.setQuery(searchText, false);
        }
    }

    @Override
    public boolean onQueryTextChange(String query) {
        searchText = query;

        if (!TextUtils.isEmpty(query)) {
            if(mSelectedTracks != null && mTracksNames != null) {
                refreshSchedule();
            } else {
                dayScheduleAdapter.getFilter().filter(searchText);
            }
        } else {
            if(dayScheduleAdapter!=null) {
                if(mSelectedTracks != null && mTracksNames != null) {
                    filter(mTracksNames,mSelectedTracks);
                } else {
                    refreshSchedule();
                }
            }
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        return false;
    }

    @Subscribe
    public void refreshData(RefreshUiEvent event) {
        // if adapter has not initialised, no point in refreshing it/
        if (dayScheduleAdapter != null) {
            refreshSchedule();
        }
    }

    @Subscribe
    public void onScheduleDownloadDone(SessionDownloadEvent event) {
        if(swipeRefreshLayout == null)
            return;

        swipeRefreshLayout.setRefreshing(false);
        if (event.isState()) {
            if (dayScheduleAdapter != null && searchView != null) {
                if (!searchView.getQuery().toString().isEmpty() && !searchView.isIconified()) {
                    dayScheduleAdapter.getFilter().filter(searchView.getQuery());
                } else {
                    refreshSchedule();
                }
            }
        } else {
            Snackbar.make(swipeRefreshLayout, getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_download, view -> refresh()).show();
        }
    }

    public void refreshSchedule() {
        compositeDisposable.add(DbSingleton.getInstance().getSessionsByDateObservable(date, SortOrder.sortOrderSchedule(getActivity()))
                .subscribe(sortedSessions -> {
                    mSessions.clear();
                    mSessions.addAll(sortedSessions);
                    mSessionsFiltered.clear();
                    mSessionsFiltered.addAll(sortedSessions);
                    if(mSelectedTracks != null && mTracksNames != null) {
                        filter(mTracksNames,mSelectedTracks);
                    }
                    dayScheduleAdapter.notifyDataSetChanged();
                    handleVisibility();
                }));
    }

    private void refresh() {
        NetworkUtils.checkConnection(new WeakReference<>(getContext()), new NetworkUtils.NetworkStateReceiverListener() {
            @Override
            public void activeConnection() {
                //Internet is working
                DataDownloadManager.getInstance().downloadSession();
            }

            @Override
            public void inactiveConnection() {
                //Device is connected to WI-FI or Mobile Data but Internet is not working
                //set is refreshing false as let user to login
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
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
                OpenEventApp.getEventBus().post(new SessionDownloadEvent(false));
            }
        });
    }
}
