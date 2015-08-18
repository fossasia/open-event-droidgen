package org.fossasia.openevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.SessionsListAdapter;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.IntentStrings;
import org.fossasia.openevent.utils.RecyclerItemClickListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MananWason on 14-06-2015.
 */
public class TracksActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    SessionsListAdapter sessionsListAdapter;
    private String track;
    private Track current;
    private List<Session> mSessions;
    private RecyclerView sessionsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);
        DbSingleton dbSingleton = DbSingleton.getInstance();
        track = getIntent().getStringExtra(IntentStrings.TRACK);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadImage();
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(track);
        sessionsRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mSessions = dbSingleton.getSessionbyTracksname(track);
        sessionsListAdapter = new SessionsListAdapter(mSessions);

        sessionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sessionsRecyclerView.setAdapter(sessionsListAdapter);
        sessionsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        sessionsRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String session_name = ((TextView) findViewById(R.id.session_title)).getText().toString();
                        Intent intent = new Intent(getApplicationContext(), SessionDetailActivity.class);
                        intent.putExtra(IntentStrings.SESSION, session_name);
                        intent.putExtra(IntentStrings.TRACK, track);
                        startActivity(intent);
                    }
                })
        );
        FloatingActionButton shareFab = (FloatingActionButton) findViewById(R.id.share_fab);
        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Add the real webapp links here
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, current.getName() + current.getId());
                intent.setType("text/plain");
                startActivity(intent);
            }
        });
    }

    private void loadImage() {
        try {
            DbSingleton dbSingleton = DbSingleton.getInstance();
            current = dbSingleton.getTrackbyName(track);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ImageView backdrop1 = (ImageView) findViewById(R.id.backdrop);
        if (current.getImage().length() != 0) {
            Picasso.with(getApplicationContext()).load(current.getImage()).into(backdrop1);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_search_sessions:

//            case R.id.share_tracks:
//                //TODO: Add the real webapp links here
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_SEND);
//                intent.putExtra(Intent.EXTRA_TEXT, current.getName() + current.getId());
//                intent.setType("text/plain");
//                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sessions_activity, menu);


        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search_sessions).getActionView();
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

        mSessions = dbSingleton.getSessionbyTracksname(track);
        final List<Session> filteredModelList = filter(mSessions, query);
        Log.d("xyz", mSessions.size() + " " + filteredModelList.size());

        sessionsListAdapter.animateTo(filteredModelList);
        sessionsRecyclerView.scrollToPosition(0);
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
