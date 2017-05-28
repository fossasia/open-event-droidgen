package org.fossasia.openevent.activities;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.SessionsListAdapter;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.TrackColors;
import org.fossasia.openevent.utils.Views;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 14-06-2015
 */
public class TrackSessionsActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    final private String SEARCH = "org.fossasia.openevent.searchText";

    private SessionsListAdapter sessionsListAdapter;

    private GridLayoutManager gridLayoutManager;

    private String track;

    private List<Session> mSessions = new ArrayList<>();

    private String searchText;

    private SearchView searchView;

    private static final int trackWiseSessionList = 4;

    private CompositeDisposable disposable;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerView) RecyclerView sessionsRecyclerView;
    @BindView(R.id.txt_no_sessions) TextView noSessionsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);

        disposable = new CompositeDisposable();

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        DbSingleton dbSingleton = DbSingleton.getInstance();
        track = getIntent().getStringExtra(ConstantStrings.TRACK);
        if (!TextUtils.isEmpty(track))
            toolbar.setTitle(track);

        //setting the grid layout to cut-off white space in tablet view
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        int spanCount = (int) (width/250.00);

        sessionsRecyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, spanCount);
        sessionsRecyclerView.setLayoutManager(gridLayoutManager);
        sessionsListAdapter = new SessionsListAdapter(this, mSessions, trackWiseSessionList);
        if(searchText!=null){
            sessionsListAdapter.setTrackName(track);
            sessionsListAdapter.getFilter().filter(searchText);
        }
        sessionsRecyclerView.setAdapter(sessionsListAdapter);
        sessionsRecyclerView.scrollToPosition(SessionsListAdapter.listPosition);
        sessionsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        int trackId = getIntent().getIntExtra(ConstantStrings.TRACK_ID, -1);

        int color = TrackColors.getColor(trackId);

        // Either track ID was not sent or color is not in cache
        if(trackId == -1 || color == -1) {
            disposable.add(dbSingleton.getTrackByNameObservable(track)
                    .subscribe(track1 -> {
                        int color1 = Color.parseColor(track1.getColor());
                        setUiColor(color1);

                        TrackColors.storeColor(track1.getId(), color1);
                    }));
        } else {
            setUiColor(color);
            Timber.d("Cached color loaded for ID %d", trackId);
        }

        disposable.add(dbSingleton.getSessionsByTrackNameObservable(track)
                .subscribe(sessions -> {
                    mSessions.clear();
                    mSessions.addAll(sessions);
                    sessionsListAdapter.notifyDataSetChanged();

                    handleVisibility();
                }));

        handleVisibility();
    }

    private void handleVisibility() {
        if (!mSessions.isEmpty()) {
            noSessionsView.setVisibility(View.GONE);
            sessionsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noSessionsView.setVisibility(View.VISIBLE);
            sessionsRecyclerView.setVisibility(View.GONE);
        }
    }

    private void setUiColor(int color) {
        toolbar.setBackgroundColor(color);

        sessionsListAdapter.setColor(color);

        if(Views.isCompatible(Build.VERSION_CODES.LOLLIPOP)) {
            getWindow().setStatusBarColor(Views.getDarkColor(color));
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_tracks;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_sessions:
                return true;
            default:
                //Do nothing
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_tracks, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search_tracks).getActionView();
        searchView.setOnQueryTextListener(this);
        if (searchText != null) {
            searchView.setQuery(searchText, false);
        }
        return true;
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
        if (!TextUtils.isEmpty(query)) {
            sessionsListAdapter.setTrackName(track);
            sessionsListAdapter.getFilter().filter(query);
        } else {
            sessionsListAdapter.setTrackName(track);
            sessionsListAdapter.refresh();
        }
        searchText = query;
        return true;
    }
}