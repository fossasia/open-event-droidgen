package org.fossasia.openevent.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.EventDates;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.SessionSpeakersMapping;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DataDownloadManager;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.CounterEvent;
import org.fossasia.openevent.events.DataDownloadEvent;
import org.fossasia.openevent.events.DownloadEvent;
import org.fossasia.openevent.events.EventDownloadEvent;
import org.fossasia.openevent.events.JsonReadEvent;
import org.fossasia.openevent.events.MicrolocationDownloadEvent;
import org.fossasia.openevent.events.NoInternetEvent;
import org.fossasia.openevent.events.RefreshUiEvent;
import org.fossasia.openevent.events.RetrofitError;
import org.fossasia.openevent.events.RetrofitResponseEvent;
import org.fossasia.openevent.events.SessionDownloadEvent;
import org.fossasia.openevent.events.ShowNetworkDialogEvent;
import org.fossasia.openevent.events.SpeakerDownloadEvent;
import org.fossasia.openevent.events.SponsorDownloadEvent;
import org.fossasia.openevent.events.TracksDownloadEvent;
import org.fossasia.openevent.fragments.BookmarksFragment;
import org.fossasia.openevent.fragments.LocationsFragment;
import org.fossasia.openevent.fragments.ScheduleFragment;
import org.fossasia.openevent.fragments.SpeakerFragment;
import org.fossasia.openevent.fragments.SponsorsFragment;
import org.fossasia.openevent.fragments.TracksFragment;
import org.fossasia.openevent.utils.CommonTaskLoop;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.ISO8601Date;
import org.fossasia.openevent.utils.NetworkUtils;
import org.fossasia.openevent.utils.SmoothActionBarDrawerToggle;
import org.fossasia.openevent.widget.DialogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends BaseActivity {


    private static final String COUNTER_TAG = "Donecounter";

    private final static String STATE_FRAGMENT = "stateFragment";

    private static final String NAV_ITEM = "navItem";

    private static final String BOOKMARK = "bookmarks";

    private final String FRAGMENT_TAG_TRACKS = "FTAGT";

    private final String FRAGMENT_TAG_REST = "FTAGR";
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.progress)
    ProgressBar downloadProgress;
    @Bind(R.id.layout_main)
    CoordinatorLayout mainFrame;
    @Bind(R.id.drawer)
    DrawerLayout drawerLayout;
    private String errorType;
    private String errorDesc;
    private SharedPreferences sharedPreferences;
    private int counter;

    private int eventsDone;

    private int currentMenuItemId;

    private SmoothActionBarDrawerToggle smoothActionBarToggle;
    private AppBarLayout appBarLayout;

    public static Intent createLaunchFragmentIntent(Context context) {
        return new Intent(context, MainActivity.class)
                .putExtra(NAV_ITEM, BOOKMARK);
    }

    public static void getDaysBetweenDates(Date startdate, Date enddate) {
        ArrayList<String> dates = new ArrayList<String>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startdate);

        while (calendar.getTime().before(enddate)) {
            Date result = calendar.getTime();
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(result);
            dates.add(new EventDates(ISO8601Date.dateFromCalendar(calendar1)).generateSql());
            calendar.add(Calendar.DATE, 1);
        }

        DbSingleton.getInstance().insertQueries(dates);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        counter = 0;
        eventsDone = 0;
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setUpToolbar();
        setUpNavDrawer();

        downloadProgress.setVisibility(View.VISIBLE);
        downloadProgress.setIndeterminate(true);
        this.findViewById(android.R.id.content).setBackgroundColor(Color.WHITE);
        if (NetworkUtils.haveNetworkConnection(this)) {
            if (!sharedPreferences.getBoolean(ConstantStrings.IS_DOWNLOAD_DONE, false)) {
                AlertDialog.Builder downloadDialog = new AlertDialog.Builder(this);
                downloadDialog.setTitle(R.string.download_assets).setMessage(R.string.charges_warning);
                downloadDialog.setIcon(R.drawable.ic_file_download_black_24dp);
                downloadDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DbSingleton.getInstance().clearDatabase();
                                Boolean preference = sharedPreferences.getBoolean(getResources().getString(R.string.download_mode_key), true);
                                if (preference) {
                                    if (NetworkUtils.haveWifiConnection(MainActivity.this)) {
                                        OpenEventApp.postEventOnUIThread(new DataDownloadEvent());
                                        sharedPreferences.edit().putBoolean(ConstantStrings.IS_DOWNLOAD_DONE, true).apply();

                                    } else {
                                        final Snackbar snackbar = Snackbar.make(mainFrame, R.string.internet_preference_warning, Snackbar.LENGTH_INDEFINITE);
                                        snackbar.setAction(R.string.yes, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                downloadFromAssets();
                                            }
                                        });
                                        snackbar.show();
                                    }
                                } else {
                                    OpenEventApp.postEventOnUIThread(new DataDownloadEvent());
                                }

                            }
                        }

                );
                downloadDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener()

                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                downloadFromAssets();

                            }
                        }

                );
                downloadDialog.show();
            } else {
                downloadFromAssets();
            }
        } else {
            final Snackbar snackbar = Snackbar.make(mainFrame, R.string.display_offline_schedule, Snackbar.LENGTH_LONG);
            snackbar.show();
            downloadFromAssets();
        }
        if (savedInstanceState == null) {
            currentMenuItemId = R.id.nav_tracks;
        } else {
            currentMenuItemId = savedInstanceState.getInt(STATE_FRAGMENT);
        }

        if (getIntent().hasExtra(NAV_ITEM)) {
            if (getIntent().getStringExtra(NAV_ITEM).equalsIgnoreCase(BOOKMARK)) {
                currentMenuItemId = R.id.nav_bookmarks;
            }
        }

        if (getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_TRACKS) == null && getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_REST) == null) {
            doMenuAction(currentMenuItemId);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        OpenEventApp.getEventBus().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        OpenEventApp.getEventBus().register(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_FRAGMENT, currentMenuItemId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        setupDrawerContent(navigationView, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tracks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Will close the drawer if the home button is pressed
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    private void setUpNavDrawer() {
        if (toolbar != null) {
            final ActionBar ab = getSupportActionBar();
            assert ab != null;
            smoothActionBarToggle = new SmoothActionBarDrawerToggle(this,
                    drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

            drawerLayout.addDrawerListener(smoothActionBarToggle);
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            smoothActionBarToggle.syncState();
        }
    }

    private void syncComplete() {
        downloadProgress.setVisibility(View.GONE);
        Event currentEvent = DbSingleton.getInstance().getEventDetails();
        getDaysBetweenDates(ISO8601Date.getDateObject(currentEvent.getStart()), ISO8601Date.getDateObject(currentEvent.getEnd()));

        Bus bus = OpenEventApp.getEventBus();
        bus.post(new RefreshUiEvent());
        DbSingleton dbSingleton = DbSingleton.getInstance();
        try {
            if (!(dbSingleton.getEventDetails().getLogo().isEmpty())) {
                ImageView headerDrawer = (ImageView) findViewById(R.id.headerDrawer);
                Picasso.with(getApplicationContext()).load(dbSingleton.getEventDetails().getLogo()).into(headerDrawer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Snackbar.make(mainFrame, getString(R.string.download_complete), Snackbar.LENGTH_SHORT).show();
        Timber.d("Download done");
    }

    private void downloadFailed(final DownloadEvent event) {
        downloadProgress.setVisibility(View.GONE);
        Snackbar.make(mainFrame, getString(R.string.download_failed), Snackbar.LENGTH_LONG).setAction(R.string.retry_download, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (event == null) {
                    Timber.d("no internet.");
                    OpenEventApp.postEventOnUIThread(new DataDownloadEvent());
                } else {
                    Timber.tag(COUNTER_TAG).d(event.getClass().getSimpleName());
                    OpenEventApp.postEventOnUIThread(event);
                }
            }
        }).show();

    }

    private void setupDrawerContent(NavigationView navigationView, final Menu menu) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        menu.clear();
                        doMenuAction(id);
                        return true;
                    }
                });
    }

    private void doMenuAction(int menuItemId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        addShadowToAppBar(true);
        switch (menuItemId) {
            case R.id.nav_tracks:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new TracksFragment(), FRAGMENT_TAG_TRACKS).commit();
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(R.string.menu_tracks);
                }
                break;
            case R.id.nav_schedule:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new ScheduleFragment(), FRAGMENT_TAG_REST).commit();
                addShadowToAppBar(false);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(R.string.menu_schedule);
                }
                break;
            case R.id.nav_bookmarks:
                DbSingleton dbSingleton = DbSingleton.getInstance();
                if (!dbSingleton.isBookmarksTableEmpty()) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, new BookmarksFragment(), FRAGMENT_TAG_REST).commit();
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(R.string.menu_bookmarks);
                    }
                } else {
                    DialogFactory.createSimpleActionDialog(this, R.string.bookmarks, R.string.empty_list, null).show();
                    if (currentMenuItemId == R.id.nav_schedule) addShadowToAppBar(false);
                }
                break;
            case R.id.nav_speakers:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new SpeakerFragment(), FRAGMENT_TAG_REST).commit();
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(R.string.menu_speakers);
                }
                break;
            case R.id.nav_sponsors:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new SponsorsFragment(), FRAGMENT_TAG_REST).commit();
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(R.string.menu_sponsor);
                }
                break;
            case R.id.nav_locations:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new LocationsFragment(), FRAGMENT_TAG_REST).commit();
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(R.string.menu_locations);
                }
                break;
            case R.id.nav_map:
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.content_frame,
                        ((OpenEventApp) getApplication())
                                .getMapModuleFactory()
                                .provideMapModule()
                                .provideMapFragment(), FRAGMENT_TAG_REST).commit();
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(R.string.menu_map);
                }
                break;
            case R.id.nav_settings:
                final Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                smoothActionBarToggle.runWhenIdle(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                    }
                });
                break;
            case R.id.nav_about:
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(this);
                builder.setTitle(String.format("%1$s", getString(R.string.app_name)));
                builder.setMessage(getResources().getText(R.string.about_text));
                builder.setPositiveButton("OK", null);
                builder.setIcon(R.mipmap.ic_launcher);
                AlertDialog welcomeAlert = builder.create();
                welcomeAlert.show();
                ((TextView) welcomeAlert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                break;
        }
        currentMenuItemId = menuItemId;
        drawerLayout.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG_TRACKS);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (fragment != null && fragment.isVisible()) {
            super.onBackPressed();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new TracksFragment(), FRAGMENT_TAG_TRACKS).commit();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.menu_tracks);
            }
        }
    }

    public void addShadowToAppBar(boolean addShadow) {
        if (addShadow) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                appBarLayout.setElevation(12);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                appBarLayout.setElevation(0);
            }
        }
    }

    public void showErrorDialog(String errorType, String errorDesc) {
        downloadProgress.setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.error)
                .setMessage(errorType + ": " + errorDesc)
                .setNeutralButton(R.string.ok, null)
                .create();
        builder.show();
    }

    //Subscribe EVENT
    @Subscribe
    public void onCounterReceiver(CounterEvent event) {
        counter = event.getRequestsCount();
        Timber.tag(COUNTER_TAG).d(counter + " counter");
        if (counter == 0) {
            syncComplete();
        }
    }

    @Subscribe
    public void onTracksDownloadDone(TracksDownloadEvent event) {
        if (event.isState()) {
            eventsDone++;
            Timber.tag(COUNTER_TAG).d(eventsDone + " tracks " + counter);
            if (counter == eventsDone) {
                syncComplete();
            }
        } else {
            downloadFailed(event);
        }
    }

    @Subscribe
    public void onSponsorsDownloadDone(SponsorDownloadEvent event) {
        if (event.isState()) {
            eventsDone++;
            Timber.tag(COUNTER_TAG).d(eventsDone + " sponsors " + counter);
            if (counter == eventsDone) {
                syncComplete();
            }
        } else {

            downloadFailed(event);
        }
    }

    @Subscribe
    public void onSpeakersDownloadDone(SpeakerDownloadEvent event) {
        if (event.isState()) {
            eventsDone++;
            Timber.tag(COUNTER_TAG).d(eventsDone + " speakers " + counter);
            if (counter == eventsDone) {
                syncComplete();
            }
        } else {

            downloadFailed(event);
        }
    }

    @Subscribe
    public void onSessionDownloadDone(SessionDownloadEvent event) {
        if (event.isState()) {
            eventsDone++;
            Timber.tag(COUNTER_TAG).d(eventsDone + " session " + counter);
            if (counter == eventsDone) {
                syncComplete();
            }
        } else {

            downloadFailed(event);
        }
    }

    @Subscribe
    public void noInternet(NoInternetEvent event) {
        downloadFailed(null);
    }

    @Subscribe
    public void onEventsDownloadDone(EventDownloadEvent event) {
        if (event.isState()) {
            eventsDone++;
            Timber.tag(COUNTER_TAG).d(eventsDone + " events " + counter);
            if (counter == eventsDone) {
                syncComplete();
            }
        } else {

            downloadFailed(event);
        }
    }

    @Subscribe
    public void onMicrolocationsDownloadDone(MicrolocationDownloadEvent event) {
        if (event.isState()) {
            eventsDone++;
            Timber.tag(COUNTER_TAG).d(eventsDone + " microlocation " + counter);
            if (counter == eventsDone) {
                syncComplete();
            }
        } else {

            downloadFailed(event);
        }

    }

    @Subscribe
    public void showNetworkDialog(ShowNetworkDialogEvent event) {
        downloadProgress.setVisibility(View.GONE);
        DialogFactory.createSimpleActionDialog(this,
                R.string.net_unavailable,
                R.string.turn_on,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent setNetworkIntent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(setNetworkIntent);
                    }
                }).show();
    }

    @Subscribe

    public void downloadData(DataDownloadEvent event) {
        if (Urls.getBaseUrl().equals(Urls.INVALID_LINK)) {
            showErrorDialog("Invalid Api", "Api link doesn't seem to be valid");
        } else {
            DataDownloadManager.getInstance().downloadEvents();
        }
        downloadProgress.setVisibility(View.VISIBLE);
        Timber.d("Download has started");
    }

    @Subscribe
    public void handleResponseEvent(RetrofitResponseEvent responseEvent) {
        Integer statusCode = responseEvent.getStatusCode();
        if (statusCode.equals(404)) {
            showErrorDialog("HTTP Error", statusCode + "Api Not Found");
        }
    }

    @Subscribe
    public void handleJsonEvent(final JsonReadEvent jsonReadEvent) {
        final String name = jsonReadEvent.getName();
        final String json = jsonReadEvent.getJson();
        CommonTaskLoop.getInstance().post(new Runnable() {
            @Override
            public void run() {
                final Gson gson = new Gson();
                switch (name) {
                    case ConstantStrings.EVENT:
                        CommonTaskLoop.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                Event event = gson.fromJson(json, Event.class);
                                DbSingleton.getInstance().insertQuery(event.generateSql());
                                OpenEventApp.postEventOnUIThread(new EventDownloadEvent(true));
                            }
                        });
                        break;
                    case ConstantStrings.TRACKS:
                        CommonTaskLoop.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                Type listType = new TypeToken<List<Track>>() {
                                }.getType();
                                List<Track> tracks = gson.fromJson(json, listType);
                                ArrayList<String> queries = new ArrayList<String>();
                                for (Track current : tracks) {
                                    queries.add(current.generateSql());
                                }
                                DbSingleton.getInstance().insertQueries(queries);
                                OpenEventApp.postEventOnUIThread(new TracksDownloadEvent(true));
                            }
                        });
                        break;
                    case ConstantStrings.SESSIONS: {
                        Type listType = new TypeToken<List<Session>>() {
                        }.getType();
                        List<Session> sessions = gson.fromJson(json, listType);
                        ArrayList<String> queries = new ArrayList<>();
                        for (Session current : sessions) {
                            current.setStartDate(current.getStartTime().split("T")[0]);
                            queries.add(current.generateSql());
                        }
                        DbSingleton.getInstance().insertQueries(queries);
                        OpenEventApp.postEventOnUIThread(new SessionDownloadEvent(true));
                        break;
                    }
                    case ConstantStrings.SPEAKERS: {
                        Type listType = new TypeToken<List<Speaker>>() {
                        }.getType();
                        List<Speaker> speakers = gson.fromJson(json, listType);

                        ArrayList<String> queries = new ArrayList<String>();
                        for (Speaker current : speakers) {
                            for (int i = 0; i < current.getSession().size(); i++) {
                                SessionSpeakersMapping sessionSpeakersMapping = new SessionSpeakersMapping(current.getSession().get(i).getId(), current.getId());
                                String query_ss = sessionSpeakersMapping.generateSql();
                                queries.add(query_ss);
                            }

                            queries.add(current.generateSql());
                        }
                        DbSingleton.getInstance().insertQueries(queries);
                        OpenEventApp.postEventOnUIThread(new SpeakerDownloadEvent(true));

                        break;
                    }
                    case ConstantStrings.SPONSORS:
                        CommonTaskLoop.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                Type listType = new TypeToken<List<Sponsor>>() {
                                }.getType();
                                List<Sponsor> sponsors = gson.fromJson(json, listType);
                                ArrayList<String> queries = new ArrayList<String>();
                                for (Sponsor current : sponsors) {
                                    queries.add(current.generateSql());
                                }
                                DbSingleton.getInstance().insertQueries(queries);
                                OpenEventApp.postEventOnUIThread(new SponsorDownloadEvent(true));
                            }
                        });
                        break;
                    case ConstantStrings.MICROLOCATIONS:
                        CommonTaskLoop.getInstance().post(new Runnable() {
                            @Override
                            public void run() {

                                Type listType = new TypeToken<List<Microlocation>>() {
                                }.getType();
                                List<Microlocation> microlocations = gson.fromJson(json, listType);
                                ArrayList<String> queries = new ArrayList<String>();
                                for (Microlocation current : microlocations) {
                                    queries.add(current.generateSql());
                                }
                                DbSingleton.getInstance().insertQueries(queries);
                                OpenEventApp.postEventOnUIThread(new MicrolocationDownloadEvent(true));
                            }
                        });
                        break;
                }
            }
        });

    }

    @Subscribe
    public void errorHandlerEvent(RetrofitError error) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = connMgr.getActiveNetworkInfo();
        if (!(netinfo != null && netinfo.isConnected())) {
            OpenEventApp.postEventOnUIThread(new ShowNetworkDialogEvent());
        } else {
            if (error.getThrowable() instanceof IOException) {
                errorType = "Timeout";
                errorDesc = String.valueOf(error.getThrowable().getCause());
            } else if (error.getThrowable() instanceof IllegalStateException) {
                errorType = "ConversionError";
                errorDesc = String.valueOf(error.getThrowable().getCause());
            } else {
                errorType = "Other Error";
                errorDesc = String.valueOf(error.getThrowable().getLocalizedMessage());
            }
            Timber.tag(errorType).e(errorDesc);
            showErrorDialog(errorType, errorDesc);
        }
    }

    public void downloadFromAssets() {

        if (!sharedPreferences.getBoolean(ConstantStrings.DATABASE_RECORDS_EXIST, false)) {
            //TODO: Add and Take counter value from to config.json
            sharedPreferences.edit().putBoolean(ConstantStrings.DATABASE_RECORDS_EXIST, true).apply();
            counter = 6;
            readJsonAsset(Urls.EVENT);
            readJsonAsset(Urls.TRACKS);
            readJsonAsset(Urls.SPEAKERS);
            readJsonAsset(Urls.SESSIONS);
            readJsonAsset(Urls.SPONSORS);
            readJsonAsset(Urls.MICROLOCATIONS);
        } else {
            downloadProgress.setVisibility(View.GONE);
        }
    }

    public void readJsonAsset(final String name) {
        CommonTaskLoop.getInstance().post(new Runnable() {
            String json = null;

            @Override
            public void run() {
                try {
                    InputStream inputStream = getAssets().open(name);
                    int size = inputStream.available();
                    byte[] buffer = new byte[size];
                    inputStream.read(buffer);
                    inputStream.close();
                    json = new String(buffer, "UTF-8");


                } catch (IOException e) {
                    e.printStackTrace();

                }
                OpenEventApp.postEventOnUIThread(new JsonReadEvent(name, json));

            }
        });
    }

}
