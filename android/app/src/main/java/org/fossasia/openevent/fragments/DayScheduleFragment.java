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
import org.fossasia.openevent.dbutils.RealmDataRepository;
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

    public static String searchText = "";

    private SearchView searchView;

    @BindView(R.id.schedule_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.list_schedule) RecyclerView dayRecyclerView;
    @BindView(R.id.txt_no_schedule) TextView noSchedule;

    private List<Session> mSessions = new ArrayList<>();
    private List<Session> mSessionsFiltered = new ArrayList<>();
    private DayScheduleAdapter dayScheduleAdapter;

    private String date;
    private CompositeDisposable compositeDisposable;
    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();

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

        swipeRefreshLayout.setOnRefreshListener(this::refresh);

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

        realmRepo.getSessionsByDate(date, SortOrder.sortOrderSchedule(getContext()))
                .addChangeListener((sortedSessions, orderedCollectionChangeSet) -> {
                    mSessions.clear();
                    mSessions.addAll(sortedSessions);

                    mSessionsFiltered.clear();
                    mSessionsFiltered.addAll(sortedSessions);

                    dayScheduleAdapter.setCopy(sortedSessions);
                    dayScheduleAdapter.notifyDataSetChanged();

                    handleVisibility();
                });

        handleVisibility();

        return view;
    }

    public void filter(List<String> selectedTracks) {
        if(dayScheduleAdapter == null)
            return;

        realmRepo.getSessionsByDate(date, SortOrder.sortOrderSchedule(OpenEventApp.getAppContext()))
                .addChangeListener((sortedSessions, orderedCollectionChangeSet) -> {
                    mSessions.clear();
                    mSessions.addAll(sortedSessions);

                    mSessionsFiltered.clear();
                    if (selectedTracks.isEmpty()) {
                        mSessionsFiltered.addAll(mSessions);
                    } else {
                        for(int i = 0 ; i < mSessions.size() ; i++) {
                            String trackName = mSessions.get(i).getTrack().getName();
                            if (selectedTracks.contains(trackName)) {
                                mSessionsFiltered.add(mSessions.get(i));
                            }
                        }

                        if(searchText!=null)
                            dayScheduleAdapter.filter(searchText);
                    }

                    dayScheduleAdapter.notifyDataSetChanged();

                    handleVisibility();
                });
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

    @Subscribe
    public void onSessionsDownloadDone(SessionDownloadEvent event) {
        if(swipeRefreshLayout!=null)
            swipeRefreshLayout.setRefreshing(false);
        if (event.isState()) {
            if (searchView != null && !searchView.getQuery().toString().isEmpty() && !searchView.isIconified()) {
                dayScheduleAdapter.filter(searchView.getQuery().toString());
            }
        } else {
            if (swipeRefreshLayout != null) {
                Snackbar.make(swipeRefreshLayout, getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).setAction(R.string.retry_download, view -> refresh()).show();
            }
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        searchText = "";
        inflater.inflate(R.menu.menu_schedule, menu);

        MenuItem item = menu.findItem(R.id.action_search_schedule);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        if (searchText != null && !TextUtils.isEmpty(searchText))
            searchView.setQuery(searchText, false);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        searchText = query;

        dayScheduleAdapter.filter(searchText);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        return false;
    }

    private void refresh() {
        NetworkUtils.checkConnectionSchedule(new WeakReference<>(getContext()), new NetworkUtils.NetworkStateReceiverListener() {
            @Override
            public void activeConnection() {
                //Internet is working
            }

            @Override
            public void inactiveConnection() {
                //internet is not working
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
                //network unavailable
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
        showNotificationSnackBar.showSnackBar();
        showNotificationSnackBar.buildNotification();
    }
}
