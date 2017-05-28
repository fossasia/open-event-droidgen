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

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.SessionsListAdapter;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.widget.DialogFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;
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

    private static final int bookmarkedSessionList =3;

    private String searchText = "";

    private SearchView searchView;

    @BindView(R.id.list_bookmarks) RecyclerView bookmarkedTracks;

    private ArrayList<Integer> bookmarkedIds;
    private List<Session> mSessions = new ArrayList<>();

    private CompositeDisposable compositeDisposable;

    @Override
    public void onResume() {
        super.onResume();
        if (sessionsListAdapter != null) {
            try {
                final DbSingleton dbSingleton = DbSingleton.getInstance();
                if (searchText != null) {
                    ArrayList<Session> Sessions = new ArrayList<>();
                    try {
                        ArrayList<Integer> bookmarkedIds = dbSingleton.getBookmarkIds();
                        for (int i = 0; i < bookmarkedIds.size(); i++) {
                            Integer id = bookmarkedIds.get(i);
                            Session session = dbSingleton.getSessionById(id);
                            Sessions.add(session);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    final List<Session> filteredModelList = filter(Sessions, searchText.toLowerCase(Locale.getDefault()));
                    sessionsListAdapter.animateTo(filteredModelList);
                } else {
                    compositeDisposable.add(dbSingleton.getBookmarkIdsObservable()
                            .subscribe(ids -> {
                                bookmarkedIds = ids;
                                sessionsListAdapter.clear();
                                for (int i = 0; i < bookmarkedIds.size(); i++) {
                                    Integer id = bookmarkedIds.get(i);
                                    final int index = i;
                                    dbSingleton.getSessionByIdObservable(id)
                                            .subscribe(session -> {
                                                mSessions.add(session);
                                                sessionsListAdapter.notifyItemInserted(index);
                                                if (index == bookmarkedIds.size())
                                                    handleVisibility();
                                            });
                                }
                            }));
                }
            } catch (ParseException e) {
                Timber.e("Parsing Error Occurred at BookmarksFragment::onResume.");
            }
        }
    }

    private void handleVisibility() {
        if (!bookmarkedIds.isEmpty()) {
            bookmarkedTracks.setVisibility(View.VISIBLE);
        } else {
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

        compositeDisposable = new CompositeDisposable();

        //setting the grid layout to cut-off white space in tablet view
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        int spanCount = (int) (width/250.00);

        bookmarkedTracks.setVisibility(View.VISIBLE);
        sessionsListAdapter = new SessionsListAdapter(getContext(), mSessions, bookmarkedSessionList);
        sessionsListAdapter.setBookmarksListChangeListener(this::onResume);
        bookmarkedTracks.setAdapter(sessionsListAdapter);
        gridLayoutManager = new GridLayoutManager(getActivity(), spanCount);
        bookmarkedTracks.setLayoutManager(gridLayoutManager);

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }

        return view;
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
    public void onDestroyView() {
        super.onDestroyView();
        if(compositeDisposable != null && !compositeDisposable.isDisposed())
            compositeDisposable.dispose();

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
        ArrayList<Session> Sessions = new ArrayList<>();
        try {
            ArrayList<Integer> bookmarkedIds = DbSingleton.getInstance().getBookmarkIds();
            for (int i = 0; i < bookmarkedIds.size(); i++) {
                Integer id = bookmarkedIds.get(i);
                Session session = DbSingleton.getInstance().getSessionById(id);
                Sessions.add(session);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final List<Session> filteredModelList = filter(Sessions, query.toLowerCase(Locale.getDefault()));

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