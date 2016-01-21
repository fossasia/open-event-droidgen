package org.fossasia.openevent.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SessionDetailActivity;
import org.fossasia.openevent.adapters.SessionsListAdapter;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.IntentStrings;

import java.text.ParseException;
import java.util.ArrayList;

import timber.log.Timber;

/**
 * User: manan
 * Date: 22-05-2015
 */
public class BookmarksFragment extends Fragment {
    SessionsListAdapter sessionsListAdapter;

    RecyclerView bookmarkedTracks;

    ArrayList<Integer> bookmarkedIds;

    @Override
    public void onResume() {
        super.onResume();
        if (sessionsListAdapter != null) {
            try {
                DbSingleton dbSingleton = DbSingleton.getInstance();
                bookmarkedIds = dbSingleton.getBookmarkIds();
                sessionsListAdapter.clear();
                for (int i = 0; i < bookmarkedIds.size(); i++) {
                    Integer id = bookmarkedIds.get(i);
                    Session session = dbSingleton.getSessionById(id);
                    sessionsListAdapter.addItem(i, session);
                }
                sessionsListAdapter.notifyDataSetChanged();

            } catch (ParseException e) {
                Timber.e("Parsing Error Occurred at BookmarksFragment::onResume.");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.i("Bookmarks Fragment create view");
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        TextView noBookmarkView = (TextView) view.findViewById(R.id.txt_no_bookmarks);
        bookmarkedTracks = (RecyclerView) view.findViewById(R.id.list_bookmarks);
        DbSingleton dbSingleton = DbSingleton.getInstance();

        try {
            bookmarkedIds = dbSingleton.getBookmarkIds();

        } catch (ParseException e) {
            Timber.e("Parsing Error Occurred at BookmarksFragment::onCreateView.");
        }
        if (!bookmarkedIds.isEmpty()) {
            noBookmarkView.setVisibility(View.GONE);
        }
        sessionsListAdapter = new SessionsListAdapter(new ArrayList<Session>());
        for (int i = 0; i < bookmarkedIds.size(); i++) {
            Integer id = bookmarkedIds.get(i);
            Session session = dbSingleton.getSessionById(id);
            sessionsListAdapter.addItem(i, session);
        }

        bookmarkedTracks.setAdapter(sessionsListAdapter);
        sessionsListAdapter.setOnClickListener(new SessionsListAdapter.SetOnClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Session model = (Session) sessionsListAdapter.getItem(position);
                String sessionName = model.getTitle();
                Intent intent = new Intent(getContext(), SessionDetailActivity.class);
                intent.putExtra(IntentStrings.SESSION, sessionName);
                startActivity(intent);
            }
        });


        bookmarkedTracks.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_bookmarks_url:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, Urls.WEB_APP_URL_BASIC + Urls.BOOKMARKS);
                intent.putExtra(Intent.EXTRA_SUBJECT, R.string.share_links);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_links)));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_bookmarks, menu);
    }
}
