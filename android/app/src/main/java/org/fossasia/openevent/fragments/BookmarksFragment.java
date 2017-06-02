package org.fossasia.openevent.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.SessionsListAdapter;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.BookmarkChangedEvent;
import org.fossasia.openevent.widget.DialogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import io.realm.RealmResults;
import timber.log.Timber;

/**
 * User: manan
 * Date: 22-05-2015
 */
public class BookmarksFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    private final String FRAGMENT_TAG = "FTAG";
    final private String SEARCH = "org.fossasia.openevent.searchText";
    private SessionsListAdapter sessionsListAdapter;

    private GridLayoutManager gridLayoutManager;

    private static final int bookmarkedSessionList = 3;

    private String searchText = "";

    private SearchView searchView;

    @BindView(R.id.list_bookmarks) RecyclerView bookmarkedTracks;

    private List<Session> mSessions = new ArrayList<>();

    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();
    private RealmResults<Session> bookmarksResult;
    private boolean dispatched = false;

    private void handleVisibility() {
        if(bookmarkedTracks == null)
            return;

        if (!mSessions.isEmpty()) {
            bookmarkedTracks.setVisibility(View.VISIBLE);
        } else if(!dispatched) {
            dispatched = true;
            DialogFactory.createSimpleActionDialog(getActivity(), R.string.bookmarks, R.string.empty_list, (dialog, which) -> {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, new TracksFragment(), FRAGMENT_TAG).commit();
                ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                if(actionBar != null) actionBar.setTitle(R.string.menu_tracks);
            }).show();
            bookmarkedTracks.setVisibility(View.GONE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.i("Bookmarks Fragment create view");
        setHasOptionsMenu(true);

        View view = super.onCreateView(inflater, container, savedInstanceState);

        //setting the grid layout to cut-off white space in tablet view
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        int spanCount = (int) (width/250.00);

        bookmarkedTracks.setVisibility(View.VISIBLE);
        sessionsListAdapter = new SessionsListAdapter(getContext(), mSessions, bookmarkedSessionList);
        sessionsListAdapter.setBookmarkView(true);

        bookmarkedTracks.setAdapter(sessionsListAdapter);
        gridLayoutManager = new GridLayoutManager(getActivity(), spanCount);
        bookmarkedTracks.setLayoutManager(gridLayoutManager);

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }

        return view;
    }

    @Subscribe
    public void onBookmarksChanged(BookmarkChangedEvent bookmarkChangedEvent) {
        Timber.d("Bookmarks changed");
        loadData();
    }

    private void loadData() {
        bookmarksResult = realmRepo.getBookMarkedSessions();
        bookmarksResult.removeAllChangeListeners();
        bookmarksResult.addChangeListener((bookmarked, orderedCollectionChangeSet) -> {
            mSessions.clear();
            mSessions.addAll(bookmarked);

            sessionsListAdapter.notifyDataSetChanged();

            handleVisibility();
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_bookmarks;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        if (searchView != null) {
            bundle.putString(SEARCH, searchText);
        }
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onStart() {
        super.onStart();
        OpenEventApp.getEventBus().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        loadData();
    }

    @Override
    public void onStop() {
        super.onStop();
        OpenEventApp.getEventBus().unregister(this);
        if(bookmarksResult != null)
            bookmarksResult.removeAllChangeListeners();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Remove listeners to fix memory leak
        if(searchView != null) searchView.setOnQueryTextListener(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_bookmarks, menu);
        searchView = (SearchView) menu.findItem(R.id.search_bookmarks).getActionView();
        searchView.setOnQueryTextListener(this);
        if (searchText != null) {
            searchView.setQuery(searchText, false);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        int spanCount = (int) (width / 250.00);
        gridLayoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        List<Session> sessions = realmRepo.getBookMarkedSessionsSync();

        final List<Session> filteredModelList = filter(sessions, query.toLowerCase(Locale.getDefault()));

        sessionsListAdapter.animateTo(filteredModelList);

        searchText = query;
        return false;
    }

    private List<Session> filter(List<Session> sessions, String query) {
        String lowerCaseQuery = query.toLowerCase(Locale.getDefault());
        final List<Session> filteredTracksList = new ArrayList<>();
        for (Session session : sessions) {
            final String text = session.getTitle().toLowerCase(Locale.getDefault());
            if (text.contains(lowerCaseQuery)) {
                filteredTracksList.add(session);
            }
        }
        return filteredTracksList;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}