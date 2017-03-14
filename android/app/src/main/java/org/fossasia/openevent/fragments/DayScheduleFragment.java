package org.fossasia.openevent.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

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
    private DayScheduleAdapter dayScheduleAdapter;

    private String date;

    private int sortType;

    private SharedPreferences sharedPreferences;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        date = getArguments().getString(ConstantStrings.EVENT_DAY, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortType = sharedPreferences.getInt(ConstantStrings.PREF_SORT, 0);
        View view = super.onCreateView(inflater, container, savedInstanceState);

        /**
         * Loading data in background to improve performance.
         * */
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Session> sortedSessions = DbSingleton.getInstance().getSessionbyDate(date, SortOrder.sortOrderSchedule(getActivity()));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dayRecyclerView != null && noSchedule != null) {
                            if (!sortedSessions.isEmpty()) {
                                noSchedule.setVisibility(View.GONE);
                            } else {
                                noSchedule.setVisibility(View.VISIBLE);
                            }
                            dayScheduleAdapter = new DayScheduleAdapter(sortedSessions, getContext());
                            dayRecyclerView.setAdapter(dayScheduleAdapter);
                            dayScheduleAdapter.setEventDate(date);

                            final StickyRecyclerHeadersDecoration headersDecoration = new StickyRecyclerHeadersDecoration(dayScheduleAdapter);
                            dayRecyclerView.addItemDecoration(headersDecoration);
                            dayScheduleAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                                @Override public void onChanged() {
                                    headersDecoration.invalidateHeaders();
                                }
                            });
                        }
                    }
                });
            }
        }).start();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.schedule_swipe_refresh);
      
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        dayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }
        //scrollup shows actionbar
        dayRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy < 0){
                    AppBarLayout appBarLayout;
                    appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbar);
                    appBarLayout.setExpanded(true);
                }
            }
        });

        DbSingleton.getInstance().getSessionByDateObservable(date, SortOrder.sortOrderSchedule(getActivity()))
                .subscribe(new Consumer<ArrayList<Session>>() {
                    @Override
                    public void accept(@NonNull ArrayList<Session> sortedSessions) throws Exception {
                        mSessions.clear();
                        mSessions.addAll(sortedSessions);

                        dayScheduleAdapter.notifyDataSetChanged();

                        handleVisibility();
                    }
                });

        handleVisibility();

        return view;
    }

    private void handleVisibility() {
        if (dayRecyclerView != null && noSchedule != null) {
            if (!mSessions.isEmpty()) {
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
        switch (item.getItemId()) {
            case R.id.action_sort_schedule:
                final AlertDialog.Builder dialogSort = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.dialog_sort_title)
                        .setSingleChoiceItems(R.array.session_sort, sortType, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sortType = which;
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt(ConstantStrings.PREF_SORT, which);
                                editor.apply();
                                dayScheduleAdapter.refresh();
                                dialog.dismiss();
                            }
                        });

                dialogSort.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        searchText = "";
        inflater.inflate(R.menu.menu_schedule, menu);
        MenuItem item = menu.findItem(R.id.action_search_schedule);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        if (searchText != null) {
            searchView.setQuery(searchText, false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (!TextUtils.isEmpty(query)) {
            searchText = query;
            dayScheduleAdapter.getFilter().filter(searchText);
        } else {
            if(dayScheduleAdapter!=null)
                dayScheduleAdapter.refresh();
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
        /**
         * if adapter has not initialised, no point in refreshing it.
         * */
        if (dayScheduleAdapter != null)
            dayScheduleAdapter.refresh();

    }

    @Subscribe
    public void onScheduleDownloadDone(SessionDownloadEvent event) {

        swipeRefreshLayout.setRefreshing(false);
        if (event.isState()) {
            if (dayScheduleAdapter != null && searchView != null) {
                if (!searchView.getQuery().toString().isEmpty() && !searchView.isIconified()) {
                    dayScheduleAdapter.getFilter().filter(searchView.getQuery());
                } else {
                    dayScheduleAdapter.refresh();
                }
            }
        } else {
            if (getActivity() != null) {
                Snackbar.make(getView(), getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).setAction(R.string.retry_download, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refresh();
                    }
                }).show();
            }
        }
    }

    private void refresh() {
        if (NetworkUtils.haveNetworkConnection(getContext())) {
            if (NetworkUtils.isActiveInternetPresent()) {
                //Internet is working
                DataDownloadManager.getInstance().downloadSession();
            } else {
                //Device is connected to WI-FI or Mobile Data but Internet is not working
                //set is refreshing false as let user to login
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                ShowNotificationSnackBar showNotificationSnackBar = new ShowNotificationSnackBar(getContext(),getView(),swipeRefreshLayout) {
                    @Override
                    public void refreshClicked() {
                        refresh();
                    }
                };
                //show snackbar will be useful if user have blocked notification for this app
                Snackbar snackbar = showNotificationSnackBar.showSnackBar();
                //show notification (Only when connected to WiFi)
                showNotificationSnackBar.buildNotification();
            }
        } else {
            OpenEventApp.getEventBus().post(new SessionDownloadEvent(false));
        }
    }


}