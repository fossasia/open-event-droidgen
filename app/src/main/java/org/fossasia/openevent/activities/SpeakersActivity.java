package org.fossasia.openevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.SessionsListAdapter;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.SpeakerIntent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MananWason on 30-06-2015.
 */
public class SpeakersActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    SessionsListAdapter sessionsListAdapter;
    private Speaker selectedSpeaker;
    private List<Session> mSessions;
    private RecyclerView sessionRecyclerView;
    private String speaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speakers);
        DbSingleton dbSingleton = DbSingleton.getInstance();
        speaker = getIntent().getStringExtra(Speaker.SPEAKER);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_speakers);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selectedSpeaker = dbSingleton.getSpeakerbySpeakersname(speaker);


        TextView biography = (TextView) findViewById(R.id.speaker_bio);
        ImageView linkedin = (ImageView) findViewById(R.id.imageView_linkedin);
        ImageView twitter = (ImageView) findViewById(R.id.imageView_twitter);
        ImageView github = (ImageView) findViewById(R.id.imageView_github);
        ImageView fb = (ImageView) findViewById(R.id.imageView_fb);

        biography.setText(selectedSpeaker.getBio());
        final SpeakerIntent speakerIntent = new SpeakerIntent(selectedSpeaker);

        speakerIntent.clickedImage(github);
        speakerIntent.clickedImage(linkedin);
        speakerIntent.clickedImage(fb);
        speakerIntent.clickedImage(twitter);

        sessionRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_speakers);
        mSessions = dbSingleton.getSessionbySpeakersName(speaker);
        sessionsListAdapter = new SessionsListAdapter(mSessions);
        sessionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sessionRecyclerView.setAdapter(sessionsListAdapter);
        sessionRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void shareSpeakerInfo () {
        String speakerInfo = getString(R.string.share_speaker_name) + speaker + "\n" + getString(R.string.share_speaker_email) + selectedSpeaker.getEmail();
        Intent sendSpeakerInfoIntent = new Intent();
        sendSpeakerInfoIntent.setAction(Intent.ACTION_SEND);
        sendSpeakerInfoIntent.putExtra(Intent.EXTRA_TEXT, speakerInfo);
        sendSpeakerInfoIntent.setType("text/plain");
        startActivity(sendSpeakerInfoIntent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.share_speaker_info:
                shareSpeakerInfo();
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

        mSessions = dbSingleton.getSessionbySpeakersName(speaker);
        final List<Session> filteredModelList = filter(mSessions, query);

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
