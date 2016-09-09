package org.fossasia.openevent.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SessionDetailActivity;
import org.fossasia.openevent.adapters.SessionsListAdapter;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.widget.DialogFactory;

import java.text.ParseException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * User: manan
 * Date: 22-05-2015
 */
public class BookmarksFragment extends Fragment {

    private final String FRAGMENT_TAG = "FTAG";
    SessionsListAdapter sessionsListAdapter;

    @BindView(R.id.list_bookmarks) RecyclerView bookmarkedTracks;

    private Unbinder unbinder;

    View view;
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
        if (!bookmarkedIds.isEmpty()) {
            bookmarkedTracks.setVisibility(View.VISIBLE);
        } else {
            DialogFactory.createSimpleActionDialog(getActivity(), R.string.bookmarks, R.string.empty_list, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, new TracksFragment(), FRAGMENT_TAG).commit();
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.menu_tracks);
                }
            }).show();
            bookmarkedTracks.setVisibility(View.GONE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.i("Bookmarks Fragment create view");
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_bookmarks, container, false);

        unbinder = ButterKnife.bind(this,view);

        final DbSingleton dbSingleton = DbSingleton.getInstance();

        try {
            bookmarkedIds = dbSingleton.getBookmarkIds();

        } catch (ParseException e) {
            Timber.e("Parsing Error Occurred at BookmarksFragment::onCreateView.");
        }
        bookmarkedTracks.setVisibility(View.VISIBLE);
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
                Session model = sessionsListAdapter.getItem(position);
                String sessionName = model.getTitle();
                Timber.d(model.getTitle());
                Track track = dbSingleton.getTrackbyId(model.getTrack().getId());
                String trackName = track.getName();
                Intent intent = new Intent(getContext(), SessionDetailActivity.class);
                intent.putExtra(ConstantStrings.SESSION, sessionName);
                intent.putExtra(ConstantStrings.TRACK, trackName);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}