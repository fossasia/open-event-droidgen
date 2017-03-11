package org.fossasia.openevent.fragments;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.SessionsListAdapter;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.BookmarksListChangeListener;
import org.fossasia.openevent.widget.DialogFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import timber.log.Timber;

/**
 * User: manan
 * Date: 22-05-2015
 */
public class BookmarksFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    private final String FRAGMENT_TAG = "FTAG";
    final private String SEARCH = "org.fossasia.openevent.searchText";
    SessionsListAdapter sessionsListAdapter;

    private GridLayoutManager gridLayoutManager;

    private static final int bookmarkedSessionList =3;

    private String searchText = "";

    private SearchView searchView;

    @BindView(R.id.list_bookmarks) RecyclerView bookmarkedTracks;

    View view;
    ArrayList<Integer> bookmarkedIds;
    private LinearLayoutManager linearLayoutManager;
    private Toolbar toolbar;
    private AppBarLayout.LayoutParams layoutParams;
    private int SCROLL_OFF = 0;


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

        View view = super.onCreateView(inflater, container, savedInstanceState);

        final DbSingleton dbSingleton = DbSingleton.getInstance();

        try {
            bookmarkedIds = dbSingleton.getBookmarkIds();

        } catch (ParseException e) {
            Timber.e("Parsing Error Occurred at BookmarksFragment::onCreateView.");
        }

        //setting the grid layout to cut-off white space in tablet view
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        int spanCount = (int) (width/250.00);

        bookmarkedTracks.setVisibility(View.VISIBLE);
        sessionsListAdapter = new SessionsListAdapter(getContext(), new ArrayList<Session>(),bookmarkedSessionList);
        for (int i = 0; i < bookmarkedIds.size(); i++) {
            Integer id = bookmarkedIds.get(i);
            Session session = dbSingleton.getSessionById(id);
            sessionsListAdapter.addItem(i, session);
        }
        sessionsListAdapter.setBookmarksListChangeListener(new BookmarksListChangeListener() {
            @Override
            public void onChange() {
                onResume();
            }
        });
        bookmarkedTracks.setAdapter(sessionsListAdapter);
        gridLayoutManager = new GridLayoutManager(getActivity(), spanCount);
        bookmarkedTracks.setLayoutManager(gridLayoutManager);
        bookmarkedTracks.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
                layoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
                if (gridLayoutManager.findLastCompletelyVisibleItemPosition() == gridLayoutManager.getChildCount() - 1) {
                    layoutParams.setScrollFlags(SCROLL_OFF);
                    toolbar.setLayoutParams(layoutParams);
                }
                bookmarkedTracks.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }
        //scrollup shows actionbar
        bookmarkedTracks.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
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
        ArrayList<Session> Sessions = new ArrayList<Session>();
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
        layoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
        toolbar.setLayoutParams(layoutParams);
    }
}