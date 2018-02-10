package org.fossasia.openevent.core.location;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
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

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ConstantStrings;
import org.fossasia.openevent.common.date.DateConverter;
import org.fossasia.openevent.common.ui.SnackbarUtil;
import org.fossasia.openevent.common.ui.Views;
import org.fossasia.openevent.common.ui.base.BaseActivity;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.config.StrategyRegistry;
import org.fossasia.openevent.core.bookmark.BookmarkStatus;
import org.fossasia.openevent.core.bookmark.OnBookmarkSelectedListener;
import org.fossasia.openevent.core.track.session.SessionDetailActivity;
import org.fossasia.openevent.core.track.session.SessionsListAdapter;
import org.fossasia.openevent.data.Session;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class LocationActivity extends BaseActivity implements SearchView.OnQueryTextListener, OnBookmarkSelectedListener, SessionsListAdapter.OnItemClickListener {
    final private String SEARCH = "searchText";

    private SessionsListAdapter sessionsListAdapter;

    private final String FRAGMENT_TAG_LOCATION = "FTAGR";

    private GridLayoutManager gridLayoutManager;
    private Dialog upcomingDialogBox;

    private List<Session> sessions = new ArrayList<>();
    private LocationActivityViewModel locationActivityViewModel;

    private static final int locationWiseSessionList = 1;

    @BindView(R.id.recyclerView_locations)
    RecyclerView sessionRecyclerView;
    @BindView(R.id.txt_no_sessions)
    TextView noSessionsView;
    @BindView(R.id.txt_no_result_loc_sessions)
    protected TextView noResultSessionsView;
    @BindView(R.id.toolbar_locations)
    Toolbar toolbar;

    private ImageView trackImageIcon;

    private String location;
    private int listPosition;

    private String searchText = "";

    private SearchView searchView;
    private Menu menu;


    private TextView upcomingSessionText;
    private TextView upcomingSessionTitle;
    private View upcomingSessionDetails;
    private TextDrawable.IBuilder drawableBuilder = TextDrawable.builder().round();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setUpcomingSessionsDialog();

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }
        locationActivityViewModel = ViewModelProviders.of(this).get(LocationActivityViewModel.class);
        searchText = locationActivityViewModel.getSearchText();
    }

    @Override
    protected void onResume() {
        super.onResume();

        location = getIntent().getStringExtra(ConstantStrings.LOCATION_NAME);
        toolbar.setTitle(location);

        //setting the grid layout to cut-off white space in tablet view
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        int spanCount = (int) (width / 250.00);

        sessionRecyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, spanCount);
        sessionRecyclerView.setLayoutManager(gridLayoutManager);
        sessionsListAdapter = new SessionsListAdapter(this, sessions, locationWiseSessionList);
        sessionsListAdapter.setHandleItemClickListener(this);
        sessionsListAdapter.setOnBookmarkSelectedListener(this);
        sessionRecyclerView.setAdapter(sessionsListAdapter);
        sessionRecyclerView.scrollToPosition(listPosition);
        sessionRecyclerView.setItemAnimator(new DefaultItemAnimator());

        loadData();

        handleVisibility();
    }

    private void loadData() {
        locationActivityViewModel.getSessionByLocation(location, searchText).observe(this, sessionsList -> {
            sessions.clear();
            sessions.addAll(sessionsList);
            sessionsListAdapter.notifyDataSetChanged();
            setUpcomingSession();
            handleVisibility();
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
            ZonedDateTime start = DateConverter.getDate(session.getStartsAt());
            if (start.isAfter(current)) {
                upcomingTitle = session.getTitle();
                track = session.getTrack().getName();
                color = session.getTrack().getColor();
                break;
            }
        }

        if (!TextUtils.isEmpty(upcomingTitle)) {
            int trackColor = Color.parseColor(color);
            upcomingSessionTitle.setText(getResources().getString(R.string.upcoming_sess));
            TextDrawable drawable = drawableBuilder.build(String.valueOf(track.charAt(0)), trackColor);
            trackImageIcon.setImageDrawable(drawable);
            trackImageIcon.setBackgroundColor(Color.TRANSPARENT);
            upcomingSessionText.setText(upcomingTitle);
        } else {
            upcomingSessionTitle.setText(getResources().getString(R.string.no_upcoming_Sess));
            upcomingSessionDetails.setVisibility(View.GONE);
        }
    }
    private void handleVisibility() {
        if (!sessions.isEmpty()) {
            noSessionsView.setVisibility(View.GONE);
            sessionRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noSessionsView.setVisibility(View.VISIBLE);
            sessionRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_locations;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        if (searchView != null && locationActivityViewModel != null) {
            locationActivityViewModel.setSearchText(searchText);
        }
        super.onSaveInstanceState(bundle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_location_activity, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search_tracks_location).getActionView();
        DrawableCompat.setTint(menu.findItem(R.id.action_search_tracks_location).getIcon(), Color.WHITE);
        DrawableCompat.setTint(menu.findItem(R.id.action_map_location).getIcon(), Color.WHITE);
        searchView.setOnQueryTextListener(this);
        searchView.setQuery(searchText, false);
        return true;
    }

    @Override
    public void onBackPressed() {
        if ((sessions.isEmpty())) {
            noSessionsView.setVisibility(View.VISIBLE);
        } else {
            sessionRecyclerView.setVisibility(View.VISIBLE);
        }
        menu.setGroupVisible(R.id.menu_group_location_activity, true);
        super.onBackPressed();

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map_location:
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putBoolean(ConstantStrings.IS_MAP_FRAGMENT_FROM_MAIN_ACTIVITY, false);
                bundle.putString(ConstantStrings.LOCATION_NAME, location);

                Fragment mapFragment = StrategyRegistry.getInstance().getMapModuleStrategy()
                        .getMapModuleFactory()
                        .provideMapModule()
                        .provideMapFragment();
                mapFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.content_frame_location, mapFragment, FRAGMENT_TAG_LOCATION).addToBackStack(null).commit();

                sessionRecyclerView.setVisibility(View.GONE);
                noSessionsView.setVisibility(View.GONE);
                menu.setGroupVisible(R.id.menu_group_location_activity, false);
                return true;
            case android.R.id.home:
                onBackPressed();
                getSupportFragmentManager().popBackStack();
                sessionRecyclerView.setVisibility(View.VISIBLE);
                return true;
            case R.id.upcoming_sessions:
                upcomingDialogBox.show();
                return true;
            default:
                return true;
        }
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
    protected void onDestroy() {
        super.onDestroy();
        sessionsListAdapter.clearOnBookmarkSelectedListener();
        sessionsListAdapter.clearHandleItemClickListener();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        return false;
    }

    @Override
    public boolean onQueryTextChange(final String query) {
        searchText = query;
        loadData();
        sessionsListAdapter.animateTo(sessions);
        Utils.displayNoResults(noResultSessionsView, sessionRecyclerView, noSessionsView,
                sessionsListAdapter.getItemCount());

        return false;
    }

    @Override
    public void showSnackbar(BookmarkStatus bookmarkStatus) {
        Snackbar snackbar = Snackbar.make(sessionRecyclerView, SnackbarUtil.getMessageResource(bookmarkStatus), Snackbar.LENGTH_LONG);
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
