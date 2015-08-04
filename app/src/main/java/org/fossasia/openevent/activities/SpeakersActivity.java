package org.fossasia.openevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.fossasia.openevent.adapters.SessionsListAdapter;
import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.SpeakerIntent;

import java.text.ParseException;
import java.util.List;

/**
 * Created by MananWason on 30-06-2015.
 */
public class SpeakersActivity extends AppCompatActivity {
    SessionsListAdapter sessionsListAdapter;
    private String speaker;
    private Speaker selectedSpeaker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speakers);
        DbSingleton dbSingleton = DbSingleton.getInstance();
        speaker = getIntent().getStringExtra(Speaker.SPEAKER);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_speakers);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            selectedSpeaker = dbSingleton.getSpeakerbySpeakersname(speaker);
        } catch (ParseException e) {
            e.printStackTrace();
        }

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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_speakers);
        try {
            List<Session> sessionList = dbSingleton.getSessionbySpeakersName(speaker);
            sessionsListAdapter = new SessionsListAdapter(sessionList);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(sessionsListAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.share_speakers:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, selectedSpeaker.getGithub());
                intent.setType("text/html");
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_speakers_activity, menu);
        return true;
    }
}
