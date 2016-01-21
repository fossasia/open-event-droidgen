package org.fossasia.openevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.SessionsListAdapter;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.IntentStrings;

import java.util.ArrayList;
import java.util.List;

/**
 * User: MananWason
 * Date: 8/18/2015
 */
public class LocationActivtiy extends BaseActivity implements SearchView.OnQueryTextListener {
    final private String SEARCH = "searchText";

    SessionsListAdapter sessionsListAdapter;

    private Microlocation selectedLocation;

    private List<Session> mSessions;

    private RecyclerView sessionRecyclerView;

    private String location;

    private String searchText = "";

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        DbSingleton dbSingleton = DbSingleton.getInstance();
        location = getIntent().getStringExtra(IntentStrings.MICROLOCATIONS);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_locations);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selectedLocation = dbSingleton.getLocationByLocationname(location);


        sessionRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_locations);
        mSessions = dbSingleton.getSessionbyLocationName(location);

        sessionsListAdapter = new SessionsListAdapter(mSessions);

        sessionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sessionRecyclerView.setAdapter(sessionsListAdapter);
        sessionsListAdapter.setOnClickListener(new SessionsListAdapter.SetOnClickListener() {
            @Override
            public void onItemClick(int position, View view) {

                Session model = (Session) sessionsListAdapter.getItem(position);
                String sessionName = model.getTitle();
                Intent intent = new Intent(getApplicationContext(), SessionDetailActivity.class);
                intent.putExtra(IntentStrings.SESSION, sessionName);
                startActivity(intent);
            }
        });
        sessionRecyclerView.setItemAnimator(new DefaultItemAnimator());

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }
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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_speakers_activity, menu);
        searchView = (SearchView) menu.findItem(R.id.search_sessions).getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setOnQueryTextListener(this);
        searchView.setQuery(searchText, false);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        DbSingleton dbSingleton = DbSingleton.getInstance();

        mSessions = dbSingleton.getSessionbyLocationName(location);
        final List<Session> filteredModelList = filter(mSessions, query);
        Log.d("xyz", mSessions.size() + " " + filteredModelList.size());

        sessionsListAdapter.animateTo(filteredModelList);
        sessionRecyclerView.scrollToPosition(0);

        searchText = query;
        return false;
    }

    private List<Session> filter(List<Session> sessions, String query) {
        query = query.toLowerCase();

        final List<Session> filteredTracksList = new ArrayList<>();
        for (Session session : sessions) {
            final String text = session.getTitle().toLowerCase();
            if (text.contains(query)) {
                filteredTracksList.add(session);
            }
        }
        return filteredTracksList;
    }
}
