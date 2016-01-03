package org.fossasia.openevent.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.SessionsListAdapter;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.IntentStrings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MananWason on 8/18/2015.
 */
public class LocationActivtiy extends AppCompatActivity implements SearchView.OnQueryTextListener {
    SessionsListAdapter sessionsListAdapter;
    private Microlocation selectedLocation;
    private List<Session> mSessions;
    private RecyclerView sessionRecyclerView;
    private String location;

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
        sessionRecyclerView.setItemAnimator(new DefaultItemAnimator());
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
        getMenuInflater().inflate(R.menu.menu_speakers_activity, menu);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search_sessions).getActionView();
        searchView.setOnQueryTextListener(this);
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