package org.fossasia.openevent.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import org.fossasia.openevent.activities.SessionDetailActivity;
import org.fossasia.openevent.adapters.DayScheduleAdapter;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DataDownloadManager;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.RefreshUiEvent;
import org.fossasia.openevent.events.SessionDownloadEvent;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.NetworkUtils;
import org.fossasia.openevent.utils.SortOrder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Manan Wason on 17/06/16.
 */
public class DayScheduleFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.list_schedule) RecyclerView dayRecyclerView;
    @BindView(R.id.txt_no_schedule) TextView noSchedule;

    private DayScheduleAdapter dayScheduleAdapter;

    private Unbinder unbinder;

    private String date;

    private int sortType;

    private SharedPreferences sharedPreferences;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        date = getArguments().getString(ConstantStrings.EVENT_DAY, "");

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData(new RefreshUiEvent());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortType = sharedPreferences.getInt(ConstantStrings.PREF_SORT, 0);

        View view = inflater.inflate(R.layout.list_schedule, container, false);
        unbinder = ButterKnife.bind(this,view);

        /**
         * Loading data in background to improve performance.
         * */
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Session> sortedSessions = DbSingleton.getInstance().getSessionbyDate(date, SortOrder.sortOrderSchedule(getActivity()));
                dayRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!sortedSessions.isEmpty()) {
                            noSchedule.setVisibility(View.GONE);
                        } else {
                            noSchedule.setVisibility(View.VISIBLE);
                        }
                        dayScheduleAdapter = new DayScheduleAdapter(sortedSessions, getContext());
                        dayRecyclerView.setAdapter(dayScheduleAdapter);
                        dayScheduleAdapter.setOnClickListener(new DayScheduleAdapter.SetOnClickListener() {
                            @Override
                            public void onItemClick(int position, View view) {
                                Session model = dayScheduleAdapter.getItem(position);
                                String sessionName = model.getTitle();
                                Track track = DbSingleton.getInstance().getTrackbyId(model.getTrack().getId());
                                String trackName = track.getName();
                                Intent intent = new Intent(getContext(), SessionDetailActivity.class);
                                intent.putExtra(ConstantStrings.SESSION, sessionName);
                                intent.putExtra(ConstantStrings.TRACK, trackName);
                                startActivity(intent);
                            }
                        });
                        dayScheduleAdapter.setEventDate(date);
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

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
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
        inflater.inflate(R.menu.menu_schedule, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
            if (dayScheduleAdapter != null)
                dayScheduleAdapter.refresh();

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
            DataDownloadManager.getInstance().downloadSession();
        } else {
            OpenEventApp.getEventBus().post(new SessionDownloadEvent(false));
        }
    }


}