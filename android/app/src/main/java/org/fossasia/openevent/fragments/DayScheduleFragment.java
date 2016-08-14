package org.fossasia.openevent.fragments;

import android.content.Intent;
import android.os.Bundle;
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
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DataDownloadManager;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.RefreshUiEvent;
import org.fossasia.openevent.events.SessionDownloadEvent;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.NetworkUtils;

import java.util.List;

import timber.log.Timber;

/**
 * Created by Manan Wason on 17/06/16.
 */
public class DayScheduleFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    private DayScheduleAdapter dayScheduleAdapter;

    private String date;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        date = getArguments().getString(ConstantStrings.EVENT_DAY, "");
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.list_schedule, container, false);
        RecyclerView dayRecyclerView = (RecyclerView) view.findViewById(R.id.list_schedule);
        final DbSingleton dbSingleton = DbSingleton.getInstance();
        TextView noSchedule = (TextView) view.findViewById(R.id.txt_no_schedule);
        List<Session> sortedSessions = dbSingleton.getSessionbyDate(date);
        if (!sortedSessions.isEmpty()) {
            noSchedule.setVisibility(View.GONE);
        } else {
            noSchedule.setVisibility(View.VISIBLE);
        }
        dayScheduleAdapter = new DayScheduleAdapter(sortedSessions);
        dayRecyclerView.setAdapter(dayScheduleAdapter);
        dayScheduleAdapter.setOnClickListener(new DayScheduleAdapter.SetOnClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Session model = dayScheduleAdapter.getItem(position);
                String sessionName = model.getTitle();
                Timber.d(sessionName);
                Track track = dbSingleton.getTrackbyId(model.getTrack().getId());
                String trackName = track.getName();
                Intent intent = new Intent(getContext(), SessionDetailActivity.class);
                intent.putExtra(ConstantStrings.SESSION, sessionName);
                intent.putExtra(ConstantStrings.TRACK, trackName);
                startActivity(intent);
            }
        });
        dayScheduleAdapter.setEventDate(date);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.schedule_swipe_refresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtils.haveNetworkConnection(getContext())) {
                    DataDownloadManager.getInstance().downloadSession();
                } else {
                    OpenEventApp.getEventBus().post(new SessionDownloadEvent(false));
                }
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
            case R.id.share_tracks_url:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, Urls.WEB_APP_URL_BASIC + Urls.TRACKS);
                intent.putExtra(Intent.EXTRA_SUBJECT, R.string.share_links);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_links)));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_schedule, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Subscribe
    public void refreshData(RefreshUiEvent event) {

        dayScheduleAdapter.refresh();

    }

    @Subscribe
    public void onScheduleDownloadDone(SessionDownloadEvent event) {

        swipeRefreshLayout.setRefreshing(false);
        if (event.isState()) {
            dayScheduleAdapter.refresh();

        } else {
            if (getActivity() != null) {
                Snackbar.make(getView(), getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).show();
            }
        }
    }

}