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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.SessionsListAdapter;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.IntentStrings;
import org.fossasia.openevent.utils.SpeakerIntent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MananWason on 30-06-2015.
 */
public class SpeakersActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    final private String SEARCH = "searchText";

    SessionsListAdapter sessionsListAdapter;

    private String searchText = "";

    private SearchView searchView;

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

        final SpeakerIntent speakerIntent = new SpeakerIntent(selectedSpeaker);

        if(selectedSpeaker.getLinkedin() == null || selectedSpeaker.getLinkedin().isEmpty()) {
            linkedin.setVisibility(View.GONE);
        }
        else {
            speakerIntent.clickedImage(linkedin);
        }
        if(selectedSpeaker.getTwitter() == null || selectedSpeaker.getTwitter().isEmpty()) {
            twitter.setVisibility(View.GONE);
        }
        else {
            speakerIntent.clickedImage(twitter);
        }
        if(selectedSpeaker.getGithub() == null || selectedSpeaker.getGithub().isEmpty()) {
            github.setVisibility(View.GONE);
        }
        else {
            speakerIntent.clickedImage(github);
        }
        if(selectedSpeaker.getFacebook() == null || selectedSpeaker.getFacebook().isEmpty()) {
            fb.setVisibility(View.GONE);
        }
        else {
            speakerIntent.clickedImage(fb);
        }

        biography.setText(selectedSpeaker.getBio());

        sessionRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_speakers);
        mSessions = dbSingleton.getSessionbySpeakersName(speaker);
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
            case R.id.share_speakers_url:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.subject));
                StringBuilder message = new StringBuilder();
                message.append(String.format("%s %s %s %s\n\n",
                        selectedSpeaker.getName(),
                        getResources().getString(R.string.message_1),
                        getResources().getString(R.string.app_name),
                        getResources().getString(R.string.message_2)));
                for (Session m : mSessions) {
                    message.append(m.getTitle())
                            .append(",");
                }
                message.append(String.format("\n\n%s (%s)\n%s",
                        getResources().getString(R.string.message_3),
                        Urls.APP_LINK,
                        selectedSpeaker.getPhoto()));
                sendIntent.putExtra(Intent.EXTRA_TEXT, message.toString());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, selectedSpeaker.getEmail()));

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_speakers_activity, menu);
        searchView = (SearchView) menu.findItem(R.id.search_sessions).getActionView();
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

        mSessions = dbSingleton.getSessionbySpeakersName(speaker);
        final List<Session> filteredModelList = filter(mSessions, query);

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
