package org.fossasia.openevent.core.track.session;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
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

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ConstantStrings;
import org.fossasia.openevent.common.date.DateConverter;
import org.fossasia.openevent.common.notification.NotificationUtil;
import org.fossasia.openevent.common.ui.SnackbarUtil;
import org.fossasia.openevent.common.ui.Views;
import org.fossasia.openevent.common.ui.WidgetUpdater;
import org.fossasia.openevent.common.ui.base.BaseActivity;
import org.fossasia.openevent.common.utils.SharedPreferencesUtil;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.config.StrategyRegistry;
import org.fossasia.openevent.core.bookmark.BookmarkStatus;
import org.fossasia.openevent.core.bookmark.OnBookmarkSelectedListener;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Track;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class SessionDetailActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener, OnBookmarkSelectedListener {
    private static final String TAG = "Session Detail";

    private SessionSpeakerListAdapter adapter;

    private Session session;
    private Menu menu;

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
    @BindView(R.id.list_speakers)
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

    private String trackName, title, location;
    private int trackColor, darkColor, fontColor;
    private int id;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final List<Speaker> speakers = new ArrayList<>();

    private Session sessionById;
    private Session sessionByName;

    private boolean isHideToolbarView = false;
    private boolean hasTrack = true;
    private boolean showMap = false;

    private String loadedFlag;

    private SessionDetailActivityViewModel sessionDetailActivityViewModel;

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

        sessionDetailActivityViewModel = ViewModelProviders.of(this).get(SessionDetailActivityViewModel.class);

        fabSessionBookmark.setOnClickListener(view -> {
            if(session == null)
                return;

            if(session.getIsBookmarked()) {
                Timber.tag(TAG).d("Bookmark Removed");

                sessionDetailActivityViewModel.setBookmark(session, false);
                fabSessionBookmark.setImageResource(R.drawable.ic_bookmark_border_white_24dp);

                showSnackbar(new BookmarkStatus(Color.parseColor(session.getTrack().getColor()), session.getId(), BookmarkStatus.Status.CODE_UNDO_REMOVED));
            } else {
                Timber.tag(TAG).d("Bookmark Added");

                sessionDetailActivityViewModel.setBookmark(session, true);
                fabSessionBookmark.setImageResource(R.drawable.ic_bookmark_white_24dp);

                compositeDisposable.add(NotificationUtil.createNotification(session, getApplicationContext()).subscribe(
                        () -> showSnackbar(new BookmarkStatus(Color.parseColor(session.getTrack().getColor()),
                                session.getId(),
                                BookmarkStatus.Status.CODE_UNDO_ADDED)),
                        throwable -> showSnackbar(new BookmarkStatus(Color.parseColor(session.getTrack().getColor()),
                                session.getId(),
                                BookmarkStatus.Status.CODE_ERROR))));
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
            setUiColor();

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

        String video_link = session.getVideoUrl();

        if(!Utils.isEmpty(video_link)) {
            playButton.setVisibility(View.VISIBLE);

            if(video_link.contains(ConstantStrings.YOUTUBE)) {
                youtubeThumbnail.setVisibility(View.VISIBLE);

                Picasso.with(this)
                        .load(ConstantStrings.YOUTUBE_URI_1 + video_link.substring(video_link.length()-11) + ConstantStrings.YOUTUBE_URI_2)
                        .into(youtubeThumbnail);
            }

            playButton.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(video_link))));
        }

        String date = DateConverter.formatDateWithDefault(DateConverter.FORMAT_DATE_COMPLETE, session.getStartsAt());
        String startTime = DateConverter.formatDateWithDefault(DateConverter.FORMAT_12H, session.getStartsAt());
        String endTime = DateConverter.formatDateWithDefault(DateConverter.FORMAT_12H, session.getEndsAt());

        text_start_time.setText(startTime);
        text_end_time.setText(endTime);
        text_date.setText(date);
        Timber.d("Date: %s\nStart: %s\nEnd: %s", date, startTime, endTime);

        Views.setHtml(summary, session.getShortAbstract(), true);
        Views.setHtml(descrip, session.getLongAbstract(), true);
    }

    private void updateFloatingIcon() {
        fabSessionBookmark.setColorFilter(fontColor, PorterDuff.Mode.SRC_ATOP);
        if(session.getIsBookmarked()) {
            Timber.tag(TAG).d("Bookmarked");
            fabSessionBookmark.setImageResource(R.drawable.ic_bookmark_white_24dp);
        } else {
            Timber.tag(TAG).d("Bookmark Removed");
            fabSessionBookmark.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
        }
    }

    private void setUiColor() {
        toolbar.setBackgroundColor(trackColor);

        //setting title colour
        text_title.setTextColor(fontColor);
        collapsingToolbarLayout.setCollapsedTitleTextColor(fontColor);
        collapsingToolbarLayout.setExpandedTitleColor(fontColor);
        collapsingToolbarLayout.setBackgroundColor(trackColor);
        collapsingToolbarLayout.setContentScrimColor(trackColor);

        //coloring status bar icons for marshmallow+ devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (speakersRecyclerView != null) && (fontColor != Color.WHITE)) {
            collapsingToolbarLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //setting of back button according to track font color
        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(fontColor, PorterDuff.Mode.SRC_ATOP);
        }
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        if (Views.isCompatible(Build.VERSION_CODES.LOLLIPOP)) {
            getWindow().setStatusBarColor(darkColor);
            Views.setEdgeGlowColorScrollView(trackColor, scrollView);
            speakersRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Views.setEdgeGlowColorRecyclerView(speakersRecyclerView, trackColor);
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
            showMap = false;
            text_title.setText(title);
            menu.setGroupVisible(R.id.menu_group_session_detail, true);
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                showMap = true;

                // Hide all the views except the frame layout and appbar layout
                scrollView.setVisibility(View.GONE);
                fabSessionBookmark.setVisibility(View.GONE);

                menu.setGroupVisible(R.id.menu_group_session_detail, false);
                text_title.setText(" ");
                appBarLayout.setExpanded(false);
                collapsingToolbarLayout.setTitle(location);
                mapFragment.setVisibility(View.VISIBLE);

                Bundle bundle = new Bundle();
                bundle.putBoolean(ConstantStrings.IS_MAP_FRAGMENT_FROM_MAIN_ACTIVITY, false);
                bundle.putString(ConstantStrings.LOCATION_NAME, location);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment mapFragment = StrategyRegistry.getInstance().getMapModuleStrategy()
                        .getMapModuleFactory()
                        .provideMapModule()
                        .provideMapFragment();
                mapFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.content_frame_session, mapFragment, FRAGMENT_TAG_REST).addToBackStack(null).commit();
                return true;

            case R.id.action_share:
                StringBuilder speakersNameBuilder  = new StringBuilder();
                boolean firstSpeaker = true;
                for (Speaker speaker : session.getSpeakers()) {
                    if (!firstSpeaker) {
                        speakersNameBuilder.append(", ");
                    }
                    speakersNameBuilder.append(speaker.getName());
                    firstSpeaker = false;
                }

                String startTime = DateConverter.formatDateWithDefault(DateConverter.FORMAT_DATE_COMPLETE, session.getStartsAt());
                String endTime = DateConverter.formatDateWithDefault(DateConverter.FORMAT_DATE_COMPLETE, session.getEndsAt());
                String shareText = String.format(getResources().getString(R.string.session_track_details) + " %s \n" +
                                getResources().getString(R.string.title_details) + " %s \n" +
                                getResources().getString(R.string.start_time_details) + " %s \n" +
                                getResources().getString(R.string.end_time_details) + " %s \n" +
                                getResources().getString(R.string.speakers_details) + " %s \n" +
                                getResources().getString(R.string.location_details) + " %s ",
                        trackName, title, startTime, endTime, speakersNameBuilder.toString(), location) +
                        "\n"+getResources().getString(R.string.description_details) + " " + Views.fromHtml(session.getShortAbstract());

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
                intent.putExtra(CalendarContract.Events.DESCRIPTION, Html.fromHtml(session.getShortAbstract()).toString());
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, DateConverter.formatDateWithDefault(DateConverter.FORMAT_24H, session.getStartsAt()));
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, DateConverter.formatDateWithDefault(DateConverter.FORMAT_24H, session.getEndsAt()));
                startActivity(intent);

            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_session_detail, menu);
        DrawableCompat.setTint(menu.findItem(R.id.action_add_to_calendar).getIcon(), fontColor);
        DrawableCompat.setTint(menu.findItem(R.id.action_map).getIcon(), fontColor);
        DrawableCompat.setTint(menu.findItem(R.id.action_share).getIcon(), fontColor);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            // Collapsed

            linearLayout.setVisibility(View.GONE);
            if (showMap){
                collapsingToolbarLayout.setTitle(location);
            } else {
                collapsingToolbarLayout.setTitle(title);
            }
            isHideToolbarView = !isHideToolbarView;
        } else if (percentage < 1f && !isHideToolbarView) {
            // Not Collapsed

            collapsingToolbarLayout.setTitle(" ");
            if (showMap){
                text_title.setText(location);
            } else {
                text_title.setText(title);
            }
            text_title.setMaxLines(2);
            linearLayout.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        id = getIntent().getIntExtra(ConstantStrings.ID, 0);
        title = getIntent().getStringExtra(ConstantStrings.SESSION);
        trackName = getIntent().getStringExtra(ConstantStrings.TRACK);

        sessionDetailActivityViewModel.getSessionById(id, loadedFlag).observe(this, sessionData -> {
            sessionById = sessionData;
            loadedFlag = SessionDetailActivityViewModel.BY_ID;
            SharedPreferencesUtil.putInt(ConstantStrings.SESSION_MAP_ID, id);
            loadSession(sessionById);
        });

        sessionDetailActivityViewModel.getSessionByName(title, loadedFlag).observe(this, sessionData -> {
            sessionByName = sessionData;
            loadedFlag = SessionDetailActivityViewModel.BY_NAME;
            SharedPreferencesUtil.putInt(ConstantStrings.SESSION_MAP_ID, -1);
            loadSession(sessionByName);
        });

    }

    private void loadSession(Session session) {
        this.session = session;
        Track track = session.getTrack();

        if (track != null && !Utils.isEmpty(track.getName())) {
            this.trackName = track.getName();
            this.trackColor = Color.parseColor(track.getColor());
            this.fontColor = Color.parseColor(track.getFontColor());
            this.darkColor = Views.getDarkColor(Color.parseColor(track.getColor()));
            hasTrack = true;
        }

        updateSession();
    }

    @Override
    public void showSnackbar(BookmarkStatus bookmarkStatus) {
        Snackbar snackbar = Snackbar.make(speakersRecyclerView, SnackbarUtil.getMessageResource(bookmarkStatus), Snackbar.LENGTH_LONG);
        SnackbarUtil.setSnackbarAction(this, snackbar, bookmarkStatus)
                .show();
    }
}