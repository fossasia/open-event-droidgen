package org.fossasia.openevent.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fossasia.openevent.Adapters.SessionsListAdapter;
import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SessionDetailActivity;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.RecyclerItemClickListener;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by manan on 22-05-2015.
 */
public class BookmarksFragment extends Fragment {
    SessionsListAdapter sessionsListAdapter;
    RecyclerView bookmarkedTracks;
    ArrayList<Session> bookmarkedSessions = new ArrayList<>();
    ArrayList<Integer> bookmarkedIds;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        bookmarkedTracks = (RecyclerView) view.findViewById(R.id.list_bookmarks);
        DbSingleton dbSingleton = DbSingleton.getInstance();

        try {
            bookmarkedIds = dbSingleton.getBookmarkIds();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (Integer id : bookmarkedIds) {
            try {
                Session session = dbSingleton.getSessionById(id);
                bookmarkedSessions.add(session);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        sessionsListAdapter = new SessionsListAdapter(bookmarkedSessions);
        bookmarkedTracks.setAdapter(sessionsListAdapter);
        bookmarkedTracks.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        bookmarkedTracks.addOnItemTouchListener(
                new RecyclerItemClickListener(view.getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String session_name = ((TextView) view.findViewById(R.id.session_title)).getText().toString();
                        Intent intent = new Intent(view.getContext(), SessionDetailActivity.class);
                        intent.putExtra("SESSION", session_name);
                        startActivity(intent);
                    }
                })
        );        return view;
    }
}
