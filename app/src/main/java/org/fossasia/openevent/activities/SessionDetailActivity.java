package org.fossasia.openevent.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.Receivers.NotificationReceiver;
import org.fossasia.openevent.adapters.SpeakersListAdapter;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.ISO8601Date;
import org.fossasia.openevent.utils.IntentStrings;

import java.util.Calendar;
import java.util.List;

/**
 * Created by MananWason on 08-07-2015.
 */
public class SessionDetailActivity extends AppCompatActivity {
    private static final String TAG = "Session Detail";
    private RecyclerView speakersRecyclerView;
    private SpeakersListAdapter adapter;
    private Session session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sessions_detail);
        DbSingleton dbSingleton = DbSingleton.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_details);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = getIntent().getStringExtra(IntentStrings.SESSION);
        String trackName = getIntent().getStringExtra(IntentStrings.TRACK);
        TextView tv_title = (TextView) findViewById(R.id.title_session);
        TextView tv_subtitle = (TextView) findViewById(R.id.subtitle_session);
        TextView tv_time = (TextView) findViewById(R.id.tv_time);
        TextView track = (TextView) findViewById(R.id.track);
        TextView tv_room1 = (TextView) findViewById(R.id.tv_location);
        TextView summary = (TextView) findViewById(R.id.tv_abstract_text);
        TextView descrip = (TextView) findViewById(R.id.tv_description);

        speakersRecyclerView = (RecyclerView) findViewById(R.id.list_speakerss);
        List<Speaker> speakers = null;

        speakers = dbSingleton.getSpeakersbySessionName(title);
        session = dbSingleton.getSessionbySessionname(title);

        tv_room1.setText((dbSingleton.getMicrolocationById(session.getMicrolocations())).getName());

        tv_title.setText(title);
        tv_subtitle.setText(session.getSubtitle());
        track.setText(trackName);

        String start = ISO8601Date.getTimeZoneDate(ISO8601Date.getDateObject(session.getStartTime())).toString();
        String end = ISO8601Date.getTimeZoneDate(ISO8601Date.getDateObject(session.getEndTime())).toString();


        if (TextUtils.isEmpty(start) && TextUtils.isEmpty(end)) {
            tv_time.setText("Timings not specified");
        } else {
            String timings = start + " - " + end;
            tv_time.setText(timings);
        }
        summary.setText(session.getSummary());
        descrip.setText(session.getDescription());
        adapter = new SpeakersListAdapter(speakers);
        speakersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        speakersRecyclerView.setAdapter(adapter);
        speakersRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.bookmark_status:
                DbSingleton dbSingleton = DbSingleton.getInstance();
                if (dbSingleton.isBookmarked(session.getId())) {
                    Log.d(TAG, "Bookmark Removed");
                    dbSingleton.deleteBookmarks(session.getId());
                    item.setIcon(R.drawable.ic_star_border_bookmark);
                } else {
                    Log.d(TAG, "Bookmarked");
                    dbSingleton.addBookmarks(session.getId());
                    item.setIcon(R.drawable.ic_star_bookmark);
                    createNotification();

                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_session_detail, menu);
        DbSingleton dbSingleton = DbSingleton.getInstance();
        MenuItem item = menu.findItem(R.id.bookmark_status);
        if (dbSingleton.isBookmarked(session.getId())) {
            Log.d(TAG, "Bookmarked");
            item.setIcon(R.drawable.ic_star_bookmark);
        } else {
            Log.d(TAG, "Bookmark Removed");
            item.setIcon(R.drawable.ic_star_border_bookmark);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void createNotification() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ISO8601Date.getTimeZoneDate(ISO8601Date.getDateObject(session.getStartTime())));
        calendar.add(Calendar.MINUTE, -10);
        Intent myIntent = new Intent(this, NotificationReceiver.class);
        myIntent.putExtra(IntentStrings.SESSION, session.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }

}
