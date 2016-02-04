package org.fossasia.openevent.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.dbutils.DataDownload;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.*;
import org.fossasia.openevent.fragments.*;
import org.fossasia.openevent.utils.SmoothActionBarDrawerToggle;

import retrofit.RetrofitError;

public class MainActivity extends BaseActivity {

    public static final String TYPE = "RetrofitError Type";

    public static final String ERROR_CODE = "Error Code";

    private static final String COUNTER_TAG = "Donecounter";

    private final static String STATE_FRAGMENT = "stateFragment";

    private static final String NAV_ITEM = "navItem";

    private static final String BOOKMARK = "bookmarks";

    private final String FRAGMENT_TAG = "FTAG";

    public String errorType;

    public String errorDesc;

    private DrawerLayout mDrawerLayout;

    private Toolbar mToolbar;

    private NavigationView navigationView;

    private ProgressBar downloadProgress;

    private CoordinatorLayout mainFrame;

    private int counter;

    private int eventsDone;

    private int currentMenuItemId;

    private SmoothActionBarDrawerToggle smoothActionBarToggle;

    public static Intent createLaunchFragmentIntent(Context context) {
        return new Intent(context, MainActivity.class)
                .putExtra(NAV_ITEM, BOOKMARK);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        counter = 0;
        setContentView(R.layout.activity_main);
        eventsDone = 0;
        setUpToolbar();
        setUpNavDrawer();
        mainFrame = (CoordinatorLayout) findViewById(R.id.layout_main);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        downloadProgress = (ProgressBar) findViewById(R.id.progress);
        downloadProgress.setVisibility(View.VISIBLE);
        downloadProgress.setIndeterminate(true);
        this.findViewById(android.R.id.content).setBackgroundColor(Color.LTGRAY);
        OpenEventApp.postEventOnUIThread(new DataDownloadEvent());
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

        if (getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG) == null) {
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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
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
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(navigationView)) {
            mDrawerLayout.closeDrawer(navigationView);
        } else {
            super.onBackPressed();
        }
    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    private void setUpNavDrawer() {
        if (mToolbar != null) {
            final ActionBar ab = getSupportActionBar();
            assert ab != null;
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
            smoothActionBarToggle = new SmoothActionBarDrawerToggle(this,
                    mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

            mDrawerLayout.setDrawerListener(smoothActionBarToggle);
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            smoothActionBarToggle.syncState();
        }
    }

    private void syncComplete() {
        downloadProgress.setVisibility(View.GONE);
        Bus bus = OpenEventApp.getEventBus();
        bus.post(new RefreshUiEvent());
        ImageView header_drawer = (ImageView) findViewById(R.id.headerDrawer);
        DbSingleton dbSingleton = DbSingleton.getInstance();
        if (!(dbSingleton.getEventDetails().getLogo().isEmpty())) {
            Picasso.with(getApplicationContext()).load(dbSingleton.getEventDetails().getLogo()).into(header_drawer);
        }

        Snackbar.make(mainFrame, getString(R.string.download_complete), Snackbar.LENGTH_SHORT).show();
        Log.d("DownNotif", "Download done");
    }

    private void downloadFailed() {
        downloadProgress.setVisibility(View.GONE);
        Snackbar.make(mainFrame, getString(R.string.download_failed), Snackbar.LENGTH_LONG).show();

    }

    private void setupDrawerContent(NavigationView navigationView, final Menu menu) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        menuItem.setChecked(true);
                        menu.clear();
                        doMenuAction(id);
                        return true;
                    }
                });
    }

    private void doMenuAction(int menuItemId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (menuItemId) {
            case R.id.nav_tracks:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new TracksFragment(), FRAGMENT_TAG).commit();
                getSupportActionBar().setTitle(R.string.menu_tracks);
                break;
            case R.id.nav_bookmarks:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new BookmarksFragment(), FRAGMENT_TAG).commit();
                getSupportActionBar().setTitle(R.string.menu_bookmarks);
                break;
            case R.id.nav_speakers:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new SpeakerFragment(), FRAGMENT_TAG).commit();
                getSupportActionBar().setTitle(R.string.menu_speakers);
                break;
            case R.id.nav_sponsors:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new SponsorsFragment(), FRAGMENT_TAG).commit();
                getSupportActionBar().setTitle(R.string.menu_sponsor);
                break;
            case R.id.nav_locations:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new LocationsFragment(), FRAGMENT_TAG).commit();
                getSupportActionBar().setTitle(R.string.menu_locations);
                break;
            case R.id.nav_map:
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.content_frame,
                        ((OpenEventApp) getApplication())
                                .getMapModuleFactory()
                                .provideMapModule()
                                .provideMapFragment(), FRAGMENT_TAG).commit();
                getSupportActionBar().setTitle(R.string.menu_map);
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
            case R.id.nav_about:
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(this);
                builder.setTitle(String.format("%1$s", getString(R.string.app_name)));
                builder.setMessage(getResources().getText(R.string.about_text));
                builder.setPositiveButton("OK", null);
                builder.setIcon(R.mipmap.ic_launcher);

                AlertDialog welcomeAlert = builder.create();
                welcomeAlert.show();
                // Make the textview clickable. Must be called after show()
                ((TextView) welcomeAlert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                break;

        }
        currentMenuItemId = menuItemId;
        mDrawerLayout.closeDrawers();
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

    //Subscribe Events
    @Subscribe
    public void onCounterReceiver(CounterEvent event) {
        counter = event.getRequestsCount();
        Log.d(COUNTER_TAG, counter + "");
        if (counter == 0) {
            syncComplete();
        }
    }

    @Subscribe
    public void onTracksDownloadDone(TracksDownloadEvent event) {
        if (event.isState()) {
            eventsDone++;
            Log.d(COUNTER_TAG, eventsDone + " " + counter);
            if (counter == eventsDone) {
                syncComplete();
            }
        } else {
            downloadFailed();
        }
    }

    @Subscribe
    public void onSponsorsDownloadDone(SponsorDownloadEvent event) {
        if (event.isState()) {
            eventsDone++;
            Log.d(COUNTER_TAG, eventsDone + " " + counter);
            if (counter == eventsDone) {
                syncComplete();
            }
        } else {

            downloadFailed();
        }
    }

    @Subscribe
    public void onSpeakersDownloadDone(SpeakerDownloadEvent event) {
        if (event.isState()) {
            eventsDone++;
            Log.d(COUNTER_TAG, eventsDone + " " + counter);
            if (counter == eventsDone) {
                syncComplete();
            }
        } else {

            downloadFailed();
        }
    }

    @Subscribe
    public void onSessionDownloadDone(SessionDownloadEvent event) {
        if (event.isState()) {
            eventsDone++;
            Log.d(COUNTER_TAG, eventsDone + " " + counter);
            if (counter == eventsDone) {
                syncComplete();
            }
        } else {

            downloadFailed();
        }
    }

    @Subscribe
    public void noInternet(NoInternetEvent event) {
        downloadFailed();
    }

    @Subscribe
    public void onEventsDownloadDone(EventDownloadEvent event) {
        if (event.isState()) {
            eventsDone++;
            Log.d(COUNTER_TAG, eventsDone + " " + counter);
            if (counter == eventsDone) {
                syncComplete();
            }
        } else {

            downloadFailed();
        }
    }

    @Subscribe
    public void onMicrolocationsDownloadDone(MicrolocationDownloadEvent event) {
        if (event.isState()) {
            eventsDone++;
            Log.d(COUNTER_TAG, eventsDone + " " + counter);
            if (counter == eventsDone) {
                syncComplete();
            }
        } else {

            downloadFailed();
        }

    }

    @Subscribe
    public void showNetworkDialog(ShowNetworkDialogEvent event) {
        downloadProgress.setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.net_unavailable))
                .setMessage(getString(R.string.turn_on))
                .setPositiveButton(R.string.action_settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent setNetworkIntent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(setNetworkIntent);
                    }
                })
                .setNeutralButton(R.string.cancel, null)
                .create();
        builder.show();
    }

    @Subscribe
    public void downloadData(DataDownloadEvent event) {
        DataDownload download = new DataDownload();
        download.downloadVersions();
        downloadProgress.setVisibility(View.VISIBLE);
        Log.d("DataNotif", "Download has started");
    }

    @Subscribe
    public void ErrorHandlerEvent(RetrofitError cause) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = connMgr.getActiveNetworkInfo();
        if (!(netinfo != null && netinfo.isConnected())) {
            OpenEventApp.postEventOnUIThread(new ShowNetworkDialogEvent());
        } else {
            switch (cause.getKind()) {
                case CONVERSION: {
                    Log.d(TYPE, "ConversionError");
                    errorType = "Conversion Error";
                    errorDesc = String.valueOf(cause.getCause());
                    break;
                }
                case HTTP: {
                    Log.d(TYPE, "HTTPError");
                    errorType = "HTTP Error";
                    errorDesc = String.valueOf(cause.getResponse().getStatus());
                    Log.d(ERROR_CODE, String.valueOf(cause.getResponse().getStatus()));
                    break;
                }
                case UNEXPECTED: {
                    Log.d(TYPE, "UnexpectedError");
                    errorType = "Unexpected Error";
                    errorDesc = String.valueOf(cause.getCause());
                    break;
                }
                case NETWORK: {
                    Log.d(TYPE, "NetworkError");
                    errorType = "Network Error";
                    errorDesc = String.valueOf(cause.getCause());
                    break;
                }
                default: {
                    Log.d(TYPE, "Other Error");
                    errorType = "Other Error";
                    errorDesc = String.valueOf(cause.getCause());
                }
            }
            showErrorDialog(errorType, errorDesc);
        }
    }

    @Subscribe

    public void onConnectionChangeReact(ConnectionCheckEvent event) {
        if (event.connState()) {
            OpenEventApp.postEventOnUIThread(new DataDownloadEvent());
            Log.d("NetNotif", "Connected to Internet");
        } else {
            Log.d("NetNotif", "Not connected to Internet");
            OpenEventApp.postEventOnUIThread(new ShowNetworkDialogEvent());
        }
    }

}
