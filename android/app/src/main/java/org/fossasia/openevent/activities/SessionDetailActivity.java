package org.fossasia.openevent.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.SpeakersListAdapter;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.receivers.NotificationAlarmReceiver;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.ISO8601Date;
import org.fossasia.openevent.utils.WidgetUpdater;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

/**
 * User: MananWason
 * Date: 08-07-2015
 */
public class SessionDetailActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {
    private static final String TAG = "Session Detail";

    private SpeakersListAdapter adapter;

    private Session session;

    private String timings;
    private String FRAGMENT_TAG_REST = "fgtr";

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.title_session)
    protected TextView text_title;
    @BindView(R.id.subtitle_session)
    protected TextView text_subtitle;
    @BindView(R.id.date_session)
    protected TextView text_date;
    @BindView(R.id.start_time_session)
    protected TextView text_start_time;
    @BindView(R.id.end_time_session)
    protected TextView text_end_time;
    @BindView(R.id.track)
    protected TextView text_track;
    @BindView(R.id.tv_location)
    protected TextView text_room1;
    @BindView(R.id.tv_abstract_text)
    protected TextView summary;
    @BindView(R.id.tv_description)
    protected TextView descrip;
    @BindView(R.id.list_speakerss)
    protected RecyclerView speakersRecyclerView;
    @BindView(R.id.fab_session_bookmark)
    protected FloatingActionButton fabSessionBookmark;
    @BindView(R.id.app_bar_session_detail)
    protected AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_layout)
    protected CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.header_title_session)
    protected LinearLayout linearLayout;
    @BindView(R.id.content_frame_session)
    protected FrameLayout mapFragment;
    @BindView(R.id.nested_scrollview_session_detail)
    protected NestedScrollView scrollView;

    private String trackName, title;

    private Spanned result;

    private boolean isHideToolbarView = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        DbSingleton dbSingleton = DbSingleton.getInstance();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int id;

        title = getIntent().getStringExtra(ConstantStrings.SESSION);
        trackName = getIntent().getStringExtra(ConstantStrings.TRACK);
        id = getIntent().getIntExtra(ConstantStrings.ID, 0);
        Timber.tag(TAG).d(title);

        collapsingToolbarLayout.setTitle(" ");
        appBarLayout.addOnOffsetChangedListener(this);

        final List<Speaker> speakers = dbSingleton.getSpeakersbySessionName(title);
        try {
            session = dbSingleton.getSessionById(id);
            sharedPreferences.edit().putInt(ConstantStrings.SESSION_MAP_ID, id).apply();
        } catch (Exception e) {
            session = dbSingleton.getSessionbySessionname(title);
            sharedPreferences.edit().putInt(ConstantStrings.SESSION_MAP_ID, -1).apply();
        }

        String microlocationName = "Not decided yet";
        if (dbSingleton.getMicrolocationById(session.getMicrolocation().getId()) != null) {
            // This function returns id=0 when microlocation is null in session JSON
            microlocationName = dbSingleton.getMicrolocationById(session.getMicrolocation().getId()).getName();
        }
        text_room1.setText(microlocationName);

        text_title.setText(title);
        if (session.getSubtitle().equals("")) {
            text_subtitle.setVisibility(View.GONE);
        }
        text_subtitle.setText(session.getSubtitle());
        text_track.setText(trackName);

        updateFloatingIcon(fabSessionBookmark);

        fabSessionBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DbSingleton dbSingleton = DbSingleton.getInstance();
                if (dbSingleton.isBookmarked(session.getId())) {
                    Timber.tag(TAG).d("Bookmark Removed");
                    dbSingleton.deleteBookmarks(session.getId());

                    fabSessionBookmark.setImageResource(R.drawable.ic_bookmark_outline_white_24dp);
                    Snackbar.make(v, R.string.removed_bookmark, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dbSingleton.addBookmarks(session.getId());
                                    fabSessionBookmark.setImageResource(R.drawable.ic_bookmark_white_24dp);
                                    WidgetUpdater.updateWidget(getApplicationContext());
                                }
                            });
                } else {
                    Timber.tag(TAG).d("Bookmarked");
                    dbSingleton.addBookmarks(session.getId());
                    fabSessionBookmark.setImageResource(R.drawable.ic_bookmark_white_24dp);
                    createNotification();
                    Toast.makeText(SessionDetailActivity.this, R.string.added_bookmark, Toast.LENGTH_SHORT).show();
                }
                WidgetUpdater.updateWidget(getApplicationContext());
            }
        });

        String date = ISO8601Date.getTimeZoneDateString(
                ISO8601Date.getDateObject(session.getStartTime())).split(",")[0] + ","
                + ISO8601Date.getTimeZoneDateString(ISO8601Date.getDateObject(session.getStartTime())).split(",")[1];
        String startTime = ISO8601Date.getTimeZoneDateString(ISO8601Date.getDateObject(session.getStartTime())).split(",")[2] + ","
                + ISO8601Date.getTimeZoneDateString(ISO8601Date.getDateObject(session.getStartTime())).split(",")[3];
        String endTime = ISO8601Date.getTimeZoneDateString(ISO8601Date.getDateObject(session.getEndTime())).split(",")[2] + ","
                + ISO8601Date.getTimeZoneDateString(ISO8601Date.getDateObject(session.getEndTime())).split(",")[3];

        if (TextUtils.isEmpty(startTime) && TextUtils.isEmpty(endTime)) {
            text_start_time.setText(R.string.time_not_specified);
            text_end_time.setVisibility(View.GONE);

        } else {
            text_start_time.setText(startTime.trim());
            text_end_time.setText(endTime.trim());
            text_date.setText(date.trim());
            Timber.d(date + "\n" + endTime + "\n" + startTime);

        }

        summary.setMovementMethod(LinkMovementMethod.getInstance());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(session.getDescription(), FROM_HTML_MODE_LEGACY);
            summary.setText(Html.fromHtml(session.getSummary(), FROM_HTML_MODE_LEGACY));
        } else {
            result = Html.fromHtml(session.getDescription());
            summary.setText(Html.fromHtml(session.getSummary()));

        }
        descrip.setText(result);

        adapter = new SpeakersListAdapter(speakers, this);

        speakersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        speakersRecyclerView.setAdapter(adapter);
        speakersRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void updateFloatingIcon(FloatingActionButton fabSessionBookmark) {
        DbSingleton dbSingleton = DbSingleton.getInstance();
        if (dbSingleton.isBookmarked(session.getId())) {
            Timber.tag(TAG).d("Bookmarked");
            fabSessionBookmark.setImageResource(R.drawable.ic_bookmark_white_24dp);
        } else {
            Timber.tag(TAG).d("Bookmark Removed");
            fabSessionBookmark.setImageResource(R.drawable.ic_bookmark_outline_white_24dp);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_sessions_detail;
    }

    @Override
    public void onBackPressed() {
        if (mapFragment.getVisibility() == View.VISIBLE) {
            /** hide fragment again on back pressed and show session views **/
            mapFragment.setVisibility(View.GONE);
            fabSessionBookmark.setVisibility(View.VISIBLE);
            if (scrollView.getVisibility() == View.GONE) {
                scrollView.setVisibility(View.VISIBLE);
            }
            if (appBarLayout.getVisibility() == View.GONE) {
                appBarLayout.setVisibility(View.VISIBLE);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                /** Hide all the views except the frame layout **/
                scrollView.setVisibility(View.GONE);
                appBarLayout.setVisibility(View.GONE);
                fabSessionBookmark.setVisibility(View.GONE);

                mapFragment.setVisibility(View.VISIBLE);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame_session,
                        ((OpenEventApp) getApplication())
                                .getMapModuleFactory()
                                .provideMapModule()
                                .provideMapFragment(), FRAGMENT_TAG_REST).commit();
                return true;

            case R.id.action_share:
                String startTime = ISO8601Date.getTimeZoneDateString(ISO8601Date.getDateObject(session.getStartTime()));
                String endTime = ISO8601Date.getTimeZoneDateString(ISO8601Date.getDateObject(session.getEndTime()));
                StringBuilder shareText = new StringBuilder();
                shareText.append(String.format("Session Track: %s \nTitle: %s \nStart Time: %s \nEnd Time: %s\n",
                        trackName, title, startTime, endTime));
                if (!result.toString().isEmpty()) {
                    shareText.append("\nDescription: ").append(result.toString());
                } else {
                    shareText.append(getString(R.string.descriptionEmpty));
                }
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getString(R.string.share_links)));
                return true;

            case R.id.action_add_to_calendar:
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(CalendarContract.Events.TITLE, title);
                intent.putExtra(CalendarContract.Events.DESCRIPTION, session.getDescription());
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, ISO8601Date.getDateObject(session.getStartTime()).getTime());
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                        ISO8601Date.getDateObject(session.getEndTime()).getTime());
                startActivity(intent);

            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_session_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public void createNotification() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ISO8601Date.getTimeZoneDate(ISO8601Date.getDateObject(session.getStartTime())));

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Integer pref_result = Integer.parseInt(sharedPrefs.getString("notification", "10 mins").substring(0, 2).trim());
        if (pref_result.equals(1)) {
            calendar.add(Calendar.HOUR, -1);
        } else if (pref_result.equals(12)) {
            calendar.add(Calendar.HOUR, -12);
        } else {
            calendar.add(Calendar.MINUTE, -10);
        }
        Intent myIntent = new Intent(this, NotificationAlarmReceiver.class);
        myIntent.putExtra(ConstantStrings.SESSION, session.getId());
        myIntent.putExtra(ConstantStrings.SESSION_TIMING, timings);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            // Collapsed

            linearLayout.setVisibility(View.GONE);
            collapsingToolbarLayout.setTitle(title);
            isHideToolbarView = !isHideToolbarView;
        } else if (percentage < 1f && !isHideToolbarView) {
            // Not Collapsed

            collapsingToolbarLayout.setTitle(" ");
            text_title.setMaxLines(2);
            linearLayout.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;
        }
    }
}