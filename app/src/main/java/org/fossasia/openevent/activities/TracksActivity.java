package org.fossasia.openevent.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.fossasia.openevent.Adapters.SessionsAdapter;
import org.fossasia.openevent.R;
import org.fossasia.openevent.dbutils.DbSingleton;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by MananWason on 14-06-2015.
 */
public class TracksActivity extends AppCompatActivity {
    RecyclerView sessionRecyclerView;
    SessionsAdapter sessionsAdapter;
    DbSingleton dbSingleton = DbSingleton.getInstance();
    CollapsingToolbarLayout collapsingToolbar;
    private String track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);
        track = getIntent().getStringExtra("TRACK");
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadImage();
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(track);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        try {
            sessionsAdapter = new SessionsAdapter(dbSingleton.getSessionbyTracksname(track));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(sessionsAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
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

    private void loadImage() {
        //TODO: Add method to parse image and add it to the view
    }
}
