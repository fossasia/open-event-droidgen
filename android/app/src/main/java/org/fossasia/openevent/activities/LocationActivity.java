package org.fossasia.openevent.activities;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.SessionsListAdapter;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.utils.ConstantStrings;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * User: MananWason
 * Date: 8/18/2015
 */
public class LocationActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    final private String SEARCH = "searchText";

    private SessionsListAdapter sessionsListAdapter;

    private final String FRAGMENT_TAG_LOCATION = "FTAGR";

    private GridLayoutManager gridLayoutManager;

    private List<Session> mSessions = new ArrayList<>();
    private static final int locationWiseSessionList = 1;

    @BindView(R.id.recyclerView_locations) RecyclerView sessionRecyclerView;
    @BindView(R.id.txt_no_sessions) TextView noSessionsView;
    @BindView(R.id.toolbar_locations) Toolbar toolbar;

    private String location;

    private String searchText;

    private SearchView searchView;
    private Menu menu;

    private CompositeDisposable disposable;
    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        disposable = new CompositeDisposable();

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        location = getIntent().getStringExtra(ConstantStrings.LOCATION_NAME);
        toolbar.setTitle(location);

        //setting the grid layout to cut-off white space in tablet view
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        int spanCount = (int) (width/250.00);

        sessionRecyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, spanCount);
        sessionRecyclerView.setLayoutManager(gridLayoutManager);
        sessionsListAdapter = new SessionsListAdapter(this, mSessions, locationWiseSessionList);
        sessionRecyclerView.setAdapter(sessionsListAdapter);
        sessionRecyclerView.scrollToPosition(SessionsListAdapter.listPosition);
        sessionRecyclerView.setItemAnimator(new DefaultItemAnimator());

        realmRepo.getSessionsByLocation(location)
                .addChangeListener((sessions, orderedCollectionChangeSet) -> {
                    mSessions.clear();
                    mSessions.addAll(sessions);
                    sessionsListAdapter.notifyDataSetChanged();

                    handleVisibility();
                });

        handleVisibility();
    }

    private void handleVisibility () {
        if (!mSessions.isEmpty()) {
            noSessionsView.setVisibility(View.GONE);
            sessionRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noSessionsView.setVisibility(View.VISIBLE);
            sessionRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_locations;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        if (searchView != null) {
            bundle.putString(SEARCH, searchText);
        }
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(disposable != null && !disposable.isDisposed())
            disposable.dispose();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_location_activity, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search_tracks_location).getActionView();
        DrawableCompat.setTint(menu.findItem(R.id.action_search_tracks_location).getIcon(), Color.WHITE);
        DrawableCompat.setTint(menu.findItem(R.id.action_map_location).getIcon(), Color.WHITE);
        searchView.setOnQueryTextListener(this);
        searchView.setQuery(searchText, false);
        return true;
    }
    @Override
    public void onBackPressed(){
        if((mSessions.isEmpty())){
            noSessionsView.setVisibility(View.VISIBLE);
        } else {
            sessionRecyclerView.setVisibility(View.VISIBLE);
        }
        menu.setGroupVisible(R.id.menu_group_location_activity, true);
        super.onBackPressed();

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_map_location:
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putBoolean(ConstantStrings.IS_MAP_FRAGMENT_FROM_MAIN_ACTIVITY, false);
                bundle.putString(ConstantStrings.LOCATION_NAME, location);

                Fragment mapFragment = ((OpenEventApp)getApplication())
                        .getMapModuleFactory()
                        .provideMapModule()
                        .provideMapFragment();
                mapFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.content_frame_location, mapFragment, FRAGMENT_TAG_LOCATION).addToBackStack(null).commit();

                sessionRecyclerView.setVisibility(View.GONE);
                noSessionsView.setVisibility(View.GONE);
                menu.setGroupVisible(R.id.menu_group_location_activity, false);
                return true;
            case android.R.id.home:
                onBackPressed();
                getSupportFragmentManager().popBackStack();
                sessionRecyclerView.setVisibility(View.VISIBLE);
                return true;
            default:
                return true;
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
        return false;
    }

    @Override
    public boolean onQueryTextChange(final String query) {
        realmRepo.getSessionsByLocation(location)
                .addChangeListener((sessions, orderedCollectionChangeSet) -> {
                    mSessions.clear();
                    mSessions.addAll(sessions);

                    final List<Session> filteredModelList = filter(mSessions,
                            query.toLowerCase(Locale.getDefault()));
                    sessionsListAdapter.notifyDataSetChanged();
                    sessionsListAdapter.animateTo(filteredModelList);
                    sessionRecyclerView.scrollToPosition(0);

                    searchText = query;
                });

        return false;
    }

    private List<Session> filter(List<Session> sessions, String query) {
        String queryLowerCase = query.toLowerCase(Locale.getDefault());

        final List<Session> filteredTracksList = new ArrayList<>();
        for (Session session : sessions) {
            final String text = session.getTitle().toLowerCase(Locale.getDefault());
            if (text.contains(queryLowerCase)) {
                filteredTracksList.add(session);
            }
        }
        return filteredTracksList;
    }
}