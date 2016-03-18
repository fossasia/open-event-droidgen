package org.fossasia.openevent.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.Receivers.NotificationAlarmReceiver;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.ISO8601Date;
import org.fossasia.openevent.utils.IntentStrings;
import org.fossasia.openevent.widget.BookmarkWidgetProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * User: MananWason
 * Date: 08-07-2015
 */
public class ScheduleSessionDetailActivity extends BaseActivity {
    private static final String TAG = "Session Detail";

    private Session session;

    private String timings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_sessions_detail);
        DbSingleton dbSingleton = DbSingleton.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final String title = getIntent().getStringExtra(IntentStrings.SESSION);
        String trackName = getIntent().getStringExtra(IntentStrings.TRACK);
        Timber.d(title);
        TextView titleTextView = (TextView) findViewById(R.id.session_title);
        TextView dateTimeTextView = (TextView) findViewById(R.id.session_datetime);
        TextView organisationTextView = (TextView) findViewById(R.id.session_organisation);
        TextView locationTextView = (TextView) findViewById(R.id.session_location);
        TextView speakerTextView = (TextView) findViewById(R.id.session_speaker);
        TextView descriptionTextView = (TextView) findViewById(R.id.session_description);
        TextView addToCalendarTextView = (TextView)findViewById(R.id.session_addtocalendar);

        session = dbSingleton.getSessionbySessionname(title);

        locationTextView.setText((dbSingleton.getMicrolocationById(session.getMicrolocations())).getName());

        titleTextView.setText(title);
        SimpleDateFormat timeSDF = new SimpleDateFormat("hh:mm aa", Locale.getDefault()),
                humanSDF = new SimpleDateFormat("EEEE, dd MMM, hh:mm aa", Locale.getDefault());

        Date startDate = ISO8601Date.getDateObject(session.getStartTime());
        Date endDate = ISO8601Date.getDateObject(session.getEndTime());

        try {
            dateTimeTextView.setText(String.format("%s - %s", humanSDF.format(startDate), timeSDF.format(endDate)));
        } catch (Exception e) {
            Timber.e(e.getMessage());
            //TODO: Print something else
            dateTimeTextView.setText(e.getMessage());
        }

        String start = ISO8601Date.getTimeZoneDateString(ISO8601Date.getDateObject(session.getStartTime()));
        String end = ISO8601Date.getTimeZoneDateString(ISO8601Date.getDateObject(session.getEndTime()));


        if (TextUtils.isEmpty(start) && TextUtils.isEmpty(end)) {
            addToCalendarTextView.setText(R.string.time_not_specified);
        } else {
            timings = start + " - " + end;
            addToCalendarTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_INSERT);
                    intent.setType("vnd.android.cursor.item/event");
                    intent.putExtra(CalendarContract.Events.TITLE, title);
                    intent.putExtra(CalendarContract.Events.DESCRIPTION, session.getDescription());
                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, ISO8601Date.getDateObject(session.getStartTime()).getTime());
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                            ISO8601Date.getDateObject(session.getEndTime()).getTime());
                    startActivity(intent);

                }
            });
        }

        List<Speaker> speakerList = dbSingleton.getSpeakersbySessionName(session.getTitle());
        //TODO: Change this from using just the first speaker's organisation
        organisationTextView.setText(speakerList.get(0).getOrganisation());
        for (Speaker speaker : speakerList) {
            speakerTextView.append(String.format("%s\n", speaker.getName()));
        }

        descriptionTextView.setText(session.getDescription());
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
                    Timber.d("Bookmark Removed");
                    dbSingleton.deleteBookmarks(session.getId());
                    item.setIcon(R.drawable.ic_bookmark_outline_white_24dp);
                } else {
                    Timber.d("Bookmarked");
                    dbSingleton.addBookmarks(session.getId());
                    item.setIcon(R.drawable.ic_bookmark_white_24dp);
                    createNotification();
                }
                sendBroadcast(new Intent(BookmarkWidgetProvider.ACTION_UPDATE));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_session_detail, menu);
        DbSingleton dbSingleton = DbSingleton.getInstance();
        MenuItem item = menu.findItem(R.id.bookmark_status);
        if (dbSingleton.isBookmarked(session.getId())) {
            Timber.d("Bookmarked");
            item.setIcon(R.drawable.ic_bookmark_white_24dp);
        } else {
            Timber.d("Bookmark Removed");
            item.setIcon(R.drawable.ic_bookmark_outline_white_24dp);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void createNotification() {
//        TODO: This notification still opens the session detail from the old design
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ISO8601Date.getTimeZoneDate(ISO8601Date.getDateObject(session.getStartTime())));

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Integer pref_result = Integer.parseInt(sharedPrefs.getString("notification", "10"));
        if (pref_result.equals(1)) {
            calendar.add(Calendar.HOUR, -1);
        } else if (pref_result.equals(12)) {
            calendar.add(Calendar.HOUR, -12);
        } else {
            calendar.add(Calendar.MINUTE, -10);
        }
        Intent myIntent = new Intent(this, NotificationAlarmReceiver.class);
        myIntent.putExtra(IntentStrings.SESSION, session.getId());
        myIntent.putExtra(IntentStrings.SESSION_TIMING, timings);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }

}
