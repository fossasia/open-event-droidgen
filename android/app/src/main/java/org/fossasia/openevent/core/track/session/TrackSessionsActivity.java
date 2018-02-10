package org.fossasia.openevent.core.track.session;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.otto.Subscribe;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ConstantStrings;
import org.fossasia.openevent.common.date.DateConverter;
import org.fossasia.openevent.common.events.BookmarkChangedEvent;
import org.fossasia.openevent.common.ui.SnackbarUtil;
import org.fossasia.openevent.common.ui.Views;
import org.fossasia.openevent.common.ui.base.BaseActivity;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.config.StrategyRegistry;
import org.fossasia.openevent.core.bookmark.BookmarkStatus;
import org.fossasia.openevent.core.bookmark.OnBookmarkSelectedListener;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Track;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

public class TrackSessionsActivity extends BaseActivity implements SearchView.OnQueryTextListener, OnBookmarkSelectedListener, SessionsListAdapter.OnItemClickListener {

    final private String SEARCH = "org.fossasia.openevent.searchText";

    private SessionsListAdapter sessionsListAdapter;

    private GridLayoutManager gridLayoutManager;

    private List<Session> sessions = new ArrayList<>();

    private String searchText = "";
    private int fontColor;

    private SearchView searchView;
    private Menu menu;

    private Dialog upcomingDialogBox;
    private ImageView trackImageIcon;
    private TextView upcomingSessionText;
    private TextView upcomingSessionTitle;
    private View upcomingSessionDetails;
    private TextDrawable.IBuilder drawableBuilder = TextDrawable.builder().round();

    private static final int trackWiseSessionList = 4;
    private int trackId;
    private int listPosition;

    private TrackSessionsActivityViewModel trackSessionsActivityViewModel;

    private Track track;

    private ActionBar actionBar;

    // TODO: Inspect Usage
    private int ongoingPosition, upcomingPosition, flag;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView sessionsRecyclerView;
    @BindView(R.id.txt_no_sessions)
    TextView noSessionsView;
    @BindView(R.id.txt_no_result_sessions)
    protected TextView noResultSessionsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);
        setUpcomingSessionsDialog();

        setSupportActionBar(toolbar);
        String track = getIntent().getStringExtra(ConstantStrings.TRACK);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (!TextUtils.isEmpty(track))
                actionBar.setTitle(track);
        }

        trackSessionsActivityViewModel = ViewModelProviders.of(this).get(TrackSessionsActivityViewModel.class);
        searchText = trackSessionsActivityViewModel.getSearchText();

        //setting the grid layout to cut-off white space in tablet view
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        int spanCount = (int) (width / 250.00);

        sessionsRecyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, spanCount);
        sessionsRecyclerView.setLayoutManager(gridLayoutManager);
        sessionsListAdapter = new SessionsListAdapter(this, sessions, trackWiseSessionList);
        sessionsListAdapter.setOnBookmarkSelectedListener(this);
        sessionsListAdapter.setHandleItemClickListener(this);
        sessionsRecyclerView.setAdapter(sessionsListAdapter);
        sessionsRecyclerView.scrollToPosition(listPosition);
        sessionsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        trackId = getIntent().getIntExtra(ConstantStrings.TRACK_ID, -1);

        loadTracks();

        handleVisibility();
    }

    private void makeUiChanges() {
        int color = Color.parseColor(track.getColor());
        fontColor = Color.parseColor(track.getFontColor());
        setUiColor(color);

        actionBar.setTitle(track.getName());
        toolbar.setTitleTextColor(fontColor);

        //coloring status bar icons for marshmallow+ devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (sessionsRecyclerView != null) && (fontColor != Color.WHITE)) {
            sessionsRecyclerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void loadTracks() {
        trackSessionsActivityViewModel.getTrack(trackId).observe(this, track -> {
            this.track = track;
            makeUiChanges();
            loadSessions();
            setUpcomingSession();
        });
    }

    private void loadSessions() {
        trackSessionsActivityViewModel.getSessions(searchText).observe(this, filteredSessions -> {
            sessions.clear();
            sessions.addAll(filteredSessions);
            sessionsListAdapter.notifyDataSetChanged();

            handleVisibility();

            ongoingPosition = trackSessionsActivityViewModel.getOngoingPosition();
            upcomingPosition = trackSessionsActivityViewModel.getUpcomingPosition();
            flag = trackSessionsActivityViewModel.getFlag();
        });
    }

    public void setUpcomingSessionsDialog() {
        upcomingDialogBox = new Dialog(this);
        upcomingDialogBox.setContentView(R.layout.upcoming_dialogbox);
        trackImageIcon = upcomingDialogBox.findViewById(R.id.track_image_drawable);
        upcomingSessionText = upcomingDialogBox.findViewById(R.id.upcoming_session_textview);
        upcomingSessionTitle = upcomingDialogBox.findViewById(R.id.upcoming_Session_title);
        Button dialogButton = upcomingDialogBox.findViewById(R.id.upcoming_button);
        upcomingSessionDetails = upcomingDialogBox.findViewById(R.id.upcoming_session_details);
        dialogButton.setOnClickListener(view -> upcomingDialogBox.dismiss());
    }

    public void setUpcomingSession() {
        String upcomingTitle = "";
        String track = "";
        String color = null;
        ZonedDateTime current = ZonedDateTime.now();
        for (Session session : sessions) {
            if (session.getStartDate() != null) {
                ZonedDateTime start = DateConverter.getDate(session.getStartsAt());
                if (start.isAfter(current)) {
                    if (session.getTitle() != null)
                        upcomingTitle = session.getTitle();
                    if (session.getTrack().getName() != null)
                        track = session.getTrack().getName();
                    if (session.getTrack().getColor() != null)
                        color = session.getTrack().getColor();
                    break;
                }
            }
        }

        if (!TextUtils.isEmpty(upcomingTitle)) {
            int trackColor = Color.parseColor(color);
            upcomingSessionTitle.setPadding(10, 60, 10, 10);
            upcomingSessionTitle.setText(getResources().getString(R.string.upcoming_sess));
            TextDrawable drawable = drawableBuilder.build(String.valueOf(track.charAt(0)), trackColor);
            trackImageIcon.setImageDrawable(drawable);
            trackImageIcon.setBackgroundColor(Color.TRANSPARENT);
            upcomingSessionText.setText(upcomingTitle);
        } else {
            upcomingSessionTitle.setPadding(10, 60, 10, 10);
            upcomingSessionTitle.setText(getResources().getString(R.string.no_upcoming_Sess));
            upcomingSessionDetails.setVisibility(View.GONE);
        }
    }

    private void handleVisibility() {
        if (!sessions.isEmpty()) {
            noSessionsView.setVisibility(View.GONE);
            sessionsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noSessionsView.setVisibility(View.VISIBLE);
            sessionsRecyclerView.setVisibility(View.GONE);
        }
    }

    private void setUiColor(int color) {
        toolbar.setBackgroundColor(color);

        sessionsListAdapter.setColor(color);

        //setting of back button according to track font color
        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(fontColor, PorterDuff.Mode.SRC_ATOP);
        }
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        if (Views.isCompatible(Build.VERSION_CODES.LOLLIPOP)) {
            getWindow().setStatusBarColor(Views.getDarkColor(color));
            sessionsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Views.setEdgeGlowColorRecyclerView(sessionsRecyclerView, color);
                }
            });

        }
    }

    @Subscribe
    public void onBookmarksChanged(BookmarkChangedEvent bookmarkChangedEvent) {
        Timber.d("Bookmarks Changed");
        loadTracks();
        loadSessions();
    }

    @Override
    public void onStart() {
        super.onStart();
        StrategyRegistry.getInstance().getEventBusStrategy().getEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        StrategyRegistry.getInstance().getEventBusStrategy().getEventBus().unregister(this);
        if (track != null) track.removeAllChangeListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sessionsListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DrawableCompat.setTint(menu.findItem(R.id.action_search_tracks).getIcon(), Color.WHITE);
        sessionsListAdapter.clearOnBookmarkSelectedListener();
        sessionsListAdapter.clearHandleItemClickListener();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_tracks;
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
            case R.id.action_search_sessions:
                return true;
            case R.id.upcoming_sessions:
                upcomingDialogBox.show();
                return true;
            default:
                //Do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_tracksessions, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search_tracks).getActionView();
        DrawableCompat.setTint(menu.findItem(R.id.action_search_tracks).getIcon(), Color.parseColor(track.getFontColor()));
        DrawableCompat.setTint(menu.findItem(R.id.upcoming_sessions).getIcon(), Color.parseColor(track.getFontColor()));

        searchView.setOnQueryTextListener(this);
        if (searchText != null) {
            searchView.setQuery(searchText, false);
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        int spanCount = (int) (width / 250.00);
        gridLayoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        searchText = query;
        loadSessions();
        Utils.displayNoResults(noResultSessionsView, sessionsRecyclerView, noSessionsView, sessionsListAdapter.getItemCount());

        return true;
    }

    @Override
    public void showSnackbar(BookmarkStatus bookmarkStatus) {
        Snackbar snackbar = Snackbar.make(sessionsRecyclerView, SnackbarUtil.getMessageResource(bookmarkStatus), Snackbar.LENGTH_LONG);
        SnackbarUtil.setSnackbarAction(this, snackbar, bookmarkStatus)
                .show();
    }

    @Override
    public void itemOnClick(Session session, int layoutPosition) {
        listPosition = layoutPosition;
        Intent intent = new Intent(this, SessionDetailActivity.class);
        startActivity(Views.openSessionDetails(session, intent));
    }

}
