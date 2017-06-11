package org.fossasia.openevent.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.SessionSpeakerListAdapter;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.receivers.NotificationAlarmReceiver;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.ISO8601Date;
import org.fossasia.openevent.utils.StringUtils;
import org.fossasia.openevent.utils.Utils;
import org.fossasia.openevent.utils.Views;
import org.fossasia.openevent.utils.WidgetUpdater;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import io.realm.RealmChangeListener;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 08-07-2015
 */
public class SessionDetailActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {
    private static final String TAG = "Session Detail";

    private SessionSpeakerListAdapter adapter;

    private Session session;

    private String timings;
    private static final String FRAGMENT_TAG_REST = "fgtr";

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
    @BindView(R.id.trak)
    protected TextView trackLabel;
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
    @BindView(R.id.iv_youtube_view)
    protected ImageView youtubeThumbnail;
    @BindView(R.id.watch)
    protected ImageButton playButton;

    private static final String BY_ID = "id";
    private static final String BY_NAME = "name";

    private String trackName, title, location;
    private int id;
    private List<Speaker> speakers = new ArrayList<>();

    private boolean isHideToolbarView = false;
    private boolean hasTrack = true;

    private String loadedFlag;

    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();
    private Session sessionById;
    private Session sessionByName;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = getIntent().getStringExtra(ConstantStrings.SESSION);
        trackName = getIntent().getStringExtra(ConstantStrings.TRACK);
        if (TextUtils.isEmpty(trackName))
            hasTrack = false;
        id = getIntent().getIntExtra(ConstantStrings.ID, 0);
        Timber.tag(TAG).d(title);

        appBarLayout.addOnOffsetChangedListener(this);

        adapter = new SessionSpeakerListAdapter(speakers);

        fabSessionBookmark.setOnClickListener(view -> {
            if(session == null)
                return;

            if(session.isBookmarked()) {
                Timber.tag(TAG).d("Bookmark Removed");

                realmRepo.setBookmark(session.getId(), false).subscribe();
                fabSessionBookmark.setImageResource(R.drawable.ic_bookmark_outline_white_24dp);

                Snackbar.make(speakersRecyclerView, R.string.removed_bookmark, Snackbar.LENGTH_SHORT).show();
            } else {
                Timber.tag(TAG).d("Bookmark Added");

                realmRepo.setBookmark(session.getId(), true).subscribe();
                fabSessionBookmark.setImageResource(R.drawable.ic_bookmark_white_24dp);

                createNotification();
                Snackbar.make(speakersRecyclerView, R.string.added_bookmark, Snackbar.LENGTH_SHORT).show();
            }

            WidgetUpdater.updateWidget(getApplicationContext());
        });

        speakersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        speakersRecyclerView.setNestedScrollingEnabled(false);
        speakersRecyclerView.setAdapter(adapter);
        speakersRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void updateSession() {
        if(hasTrack)
            setUiColor(Color.parseColor(session.getTrack().getColor()));

        Timber.d("Updated");

        speakers.clear();
        speakers.addAll(session.getSpeakers());
        adapter.notifyDataSetChanged();

        updateFloatingIcon();

        Microlocation microlocation = session.getMicrolocation();

        if(microlocation != null) {
            location = microlocation.getName();
            text_room1.setText(microlocation.getName());
        } else {
            location = getString(R.string.location_not_decided);
            text_room1.setText(location);
        }

        text_title.setText(title);
        if (TextUtils.isEmpty(session.getSubtitle())) {
            text_subtitle.setVisibility(View.GONE);
        }
        text_subtitle.setText(session.getSubtitle());

        if (hasTrack) {
            trackLabel.setVisibility(View.VISIBLE);
            text_track.setVisibility(View.VISIBLE);
            text_track.setText(trackName);
        } else {
            trackLabel.setVisibility(View.GONE);
            text_track.setVisibility(View.GONE);
        }

        String video_link = session.getVideo();

        if(!Utils.isEmpty(video_link)) {
            playButton.setVisibility(View.VISIBLE);

            if(video_link.contains(ConstantStrings.YOUTUBE)) {
                youtubeThumbnail.setVisibility(View.VISIBLE);

                Picasso.with(this)
                        .load(ConstantStrings.YOUTUBE_URI_1 + video_link.substring(video_link.length()-11) + ConstantStrings.YOUTUBE_URI_2)
                        .into(youtubeThumbnail);
            }

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(video_link)));
                }
            });
        }


        String date = ISO8601Date.getDateFromStartDateString(session.getStartTime());
        String startTime = ISO8601Date.getTimeFromStartDateString(session.getStartTime());
        String endTime = ISO8601Date.getTimeFromEndDateString(session.getEndTime());

        if (TextUtils.isEmpty(startTime) && TextUtils.isEmpty(endTime)) {
            text_start_time.setText(R.string.time_not_specified);
            text_end_time.setVisibility(View.GONE);
        } else {
            text_start_time.setText(startTime.trim());
            text_end_time.setText(endTime.trim());
            text_date.setText(date.trim());
            Timber.d("%s\n%s\n%s", date, endTime, startTime);
        }

        Views.setHtml(summary, session.getShortAbstract(), true);
        Views.setHtml(descrip, session.getLongAbstract(), true);
    }

    private void updateFloatingIcon() {
        if(session.isBookmarked()) {
            Timber.tag(TAG).d("Bookmarked");
            fabSessionBookmark.setImageResource(R.drawable.ic_bookmark_white_24dp);
        } else {
            Timber.tag(TAG).d("Bookmark Removed");
            fabSessionBookmark.setImageResource(R.drawable.ic_bookmark_outline_white_24dp);
        }
    }

    private void setUiColor(int color) {
        int darkColor = Views.getDarkColor(color);

        toolbar.setBackgroundColor(color);
        collapsingToolbarLayout.setBackgroundColor(color);
        collapsingToolbarLayout.setContentScrimColor(darkColor);

        if (Views.isCompatible(Build.VERSION_CODES.LOLLIPOP)) {
            getWindow().setStatusBarColor(darkColor);
            Views.setEdgeGlowColorScrollView(color, scrollView);
            speakersRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Views.setEdgeGlowColorRecyclerView(speakersRecyclerView, color);
                }
            });
        }

        fabSessionBookmark.setBackgroundTintList(ColorStateList.valueOf(darkColor));
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_sessions_detail;
    }

    @Override
    public void onBackPressed() {
        if (fabSessionBookmark.getVisibility() == View.GONE) {
            // Hide fragment again on back pressed and show session views
            appBarLayout.setExpanded(true);
            mapFragment.setVisibility(View.GONE);
            fabSessionBookmark.setVisibility(View.VISIBLE);
            if (scrollView.getVisibility() == View.GONE) {
                scrollView.setVisibility(View.VISIBLE);
            }
            if (appBarLayout.getVisibility() == View.GONE) {
                appBarLayout.setVisibility(View.VISIBLE);
            }
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                // Hide all the views except the frame layout and appbar layout
                scrollView.setVisibility(View.GONE);
                fabSessionBookmark.setVisibility(View.GONE);

                appBarLayout.setExpanded(false);
                mapFragment.setVisibility(View.VISIBLE);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame_session,
                        ((OpenEventApp) getApplication())
                                .getMapModuleFactory()
                                .provideMapModule()
                                .provideMapFragment(), FRAGMENT_TAG_REST).addToBackStack(null).commit();
                return true;

            case R.id.action_share:
                String startTime = ISO8601Date.getTimeZoneDateStringFromString(session.getStartTime());
                String endTime = ISO8601Date.getTimeZoneDateStringFromString(session.getEndTime());
                String shareText = String.format("Session Track: %s \n" +
                                "Title: %s \n" +
                                "Start Time: %s \n" +
                                "End Time: %s\n" +
                                "Speakers: %s\n" +
                                "Location: %s",
                        trackName, title, startTime, endTime, StringUtils.join(speakers, ", "), location) +
                        "\nDescription: " + Views.fromHtml(session.getLongAbstract());

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getString(R.string.share_links)));
                return true;

            case R.id.action_add_to_calendar:
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(CalendarContract.Events.TITLE, title);
                intent.putExtra(CalendarContract.Events.DESCRIPTION, session.getShortAbstract());
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, ISO8601Date.get24HourTimeFromString(session.getStartTime()));
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, ISO8601Date.get24HourTimeFromString(session.getEndTime()));
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
        calendar.setTime(ISO8601Date.getTimeZoneDateFromString(session.getStartTime()));

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

    @Override
    protected void onResume() {
        super.onResume();

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        sessionById = realmRepo.getSession(id);
        sessionById.addChangeListener((RealmChangeListener<Session>) loadedSession -> {
            if(!loadedSession.isValid())
                return;

            if(loadedFlag == null || loadedFlag.equals(BY_ID)) {

                loadedFlag = BY_ID;

                session = loadedSession;

                sharedPreferences.edit().putInt(ConstantStrings.SESSION_MAP_ID, id).apply();
                updateSession();
            }
        });

        sessionByName = realmRepo.getSession(title);
        sessionByName.addChangeListener((RealmChangeListener<Session>) loadedSession -> {
            if(!loadedSession.isValid())
                return;

            if(loadedFlag == null || loadedFlag.equals(BY_NAME)) {

                loadedFlag = BY_NAME;

                session = loadedSession;

                sharedPreferences.edit().putInt(ConstantStrings.SESSION_MAP_ID, -1).apply();
                updateSession();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(sessionById != null) sessionById.removeAllChangeListeners();
        if(sessionByName != null) sessionByName.removeAllChangeListeners();
    }
}