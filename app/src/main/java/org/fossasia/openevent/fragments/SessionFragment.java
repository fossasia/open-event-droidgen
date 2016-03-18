package org.fossasia.openevent.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.ScheduleSessionDetailActivity;
import org.fossasia.openevent.adapters.ScheduleSessionsListAdapter;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.ISO8601Date;
import org.fossasia.openevent.utils.IntentStrings;
import org.fossasia.openevent.utils.RecyclerItemInteractListener;
import org.fossasia.openevent.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by duncanleo on 1/2/16.
 */
public class SessionFragment extends Fragment {
    //TODO: Set to the actual first day of the event
    public static final long FIRST_DAY_MILLIS = new GregorianCalendar(2015, 4, 5).getTime().getTime();

    private View emptyState;
    private RecyclerView sessionsRecyclerView;
    private ScheduleSessionsListAdapter sessionsListAdapter;
    private List<Session> data = new ArrayList<>();
    private int tabPos;
    private boolean showBookmarkedOnly;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            tabPos = getArguments().getInt(ScheduleFragment.SCHEDULE_TAB_POSITION);
            showBookmarkedOnly = getArguments().getBoolean(ScheduleFragment.SHOW_BOOKMARKED_ONLY, false);
        }
    }

    /**
     * Load data for display from database
     */
    private synchronized void loadData() {
        data.clear();
        data.addAll(DbSingleton.getInstance().getSessionList());

        //TODO: Use database to filter
        Iterator<Session> sessionIterator = data.iterator();
        while (sessionIterator.hasNext()) {
            Session s = sessionIterator.next();
            Date d = ISO8601Date.getDateObject(s.getStartTime());
            long diff = d.getTime() - FIRST_DAY_MILLIS;
            if (TimeUnit.MILLISECONDS.toDays(diff) != tabPos) {
                sessionIterator.remove();
            } else if (showBookmarkedOnly && !DbSingleton.getInstance().isBookmarked(s.getId())) {
                sessionIterator.remove();
            }
        }

        emptyState.setVisibility(data.size() == 0 ? View.VISIBLE : View.GONE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_session, container, false);
        emptyState = v.findViewById(R.id.fragment_session_empty_state);
        sessionsRecyclerView = (RecyclerView) v.findViewById(R.id.sessionRecyclerView);
        sessionsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sessionsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        sessionsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity().getResources()));
        sessionsRecyclerView.addOnItemTouchListener(new RecyclerItemInteractListener(getActivity(), new RecyclerItemInteractListener.OnItemInteractListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), ScheduleSessionDetailActivity.class);
                intent.putExtra(IntentStrings.SESSION, data.get(position).getTitle());
                startActivity(intent);
            }

            @Override
            public void onOptionsViewOpened(View optionsView, int position) {
                Session session = data.get(position);
                ImageView image = (ImageView) optionsView.findViewById(R.id.schedule_bookmark_image);
                image.setImageResource(DbSingleton.getInstance().isBookmarked(session.getId()) ? R.drawable.ic_bookmark_white_24dp : R.drawable.ic_bookmark_outline_white_24dp);

                //TODO: Logic to either bookmark/un-bookmark this session
            }
        }, R.id.item_session_bookmark_view));

        loadData();
        sessionsListAdapter = new ScheduleSessionsListAdapter(data);
        sessionsRecyclerView.setAdapter(sessionsListAdapter);
        return v;
    }

    public ScheduleSessionsListAdapter getSessionsListAdapter() {
        return sessionsListAdapter;
    }
}
