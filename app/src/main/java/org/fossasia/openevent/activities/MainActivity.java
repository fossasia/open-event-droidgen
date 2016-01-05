package org.fossasia.openevent.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.dbutils.DataDownload;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.ConnectionCheckEvent;
import org.fossasia.openevent.events.CounterEvent;
import org.fossasia.openevent.events.DataDownloadEvent;
import org.fossasia.openevent.events.EventDownloadEvent;
import org.fossasia.openevent.events.MicrolocationDownloadEvent;
import org.fossasia.openevent.events.NoInternetEvent;
import org.fossasia.openevent.events.RefreshUiEvent;
import org.fossasia.openevent.events.SessionDownloadEvent;
import org.fossasia.openevent.events.ShowNetworkDialogEvent;
import org.fossasia.openevent.events.SpeakerDownloadEvent;
import org.fossasia.openevent.events.SponsorDownloadEvent;
import org.fossasia.openevent.events.TracksDownloadEvent;
import org.fossasia.openevent.fragments.BookmarksFragment;
import org.fossasia.openevent.fragments.LocationsFragment;
import org.fossasia.openevent.fragments.SpeakerFragment;
import org.fossasia.openevent.fragments.SponsorsFragment;
import org.fossasia.openevent.fragments.TracksFragment;

import retrofit.RetrofitError;

public class MainActivity extends AppCompatActivity {

    private static final String COUNTER_TAG = "Donecounter";
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavigationView navigationView;
    private ProgressBar downloadProgress;
    private FrameLayout mainFrame;
    private int counter;
    private int eventsDone;
    private int currentMenuItemId;
    private final static String STATE_FRAGMENT = "stateFragment";
    public String errorType;
    public String errorDesc;
    public static final String TYPE = "RetrofitError Type";
    public static final String ERROR_CODE = "Error Code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        counter = 0;
        setContentView(R.layout.activity_main);
        eventsDone = 0;
        setUpToolbar();
        setUpNavDrawer();
        mainFrame = (FrameLayout) findViewById(R.id.layout_main);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        downloadProgress = (ProgressBar) findViewById(R.id.progress);
        downloadProgress.setVisibility(View.VISIBLE);
        downloadProgress.setIndeterminate(true);
        this.findViewById(android.R.id.content).setBackgroundColor(Color.LTGRAY);
        OpenEventApp.postEventOnUIThread(new DataDownloadEvent());
        if(savedInstanceState == null){
            currentMenuItemId = R.id.nav_tracks;
        } else {
            currentMenuItemId = savedInstanceState.getInt(STATE_FRAGMENT);
        }

        doMenuAction(currentMenuItemId);

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
            final android.support.v7.app.ActionBar ab = getSupportActionBar();
            assert ab != null;
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
            ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(this,
                    mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

            mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            mActionBarDrawerToggle.syncState();
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

    private void doMenuAction(int menuItemId){
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (menuItemId) {
            case R.id.nav_tracks:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new TracksFragment()).commit();
                getSupportActionBar().setTitle(R.string.menu_tracks);
                break;
            case R.id.nav_bookmarks:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new BookmarksFragment()).commit();
                getSupportActionBar().setTitle(R.string.menu_bookmarks);
                break;
            case R.id.nav_speakers:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new SpeakerFragment()).commit();
                getSupportActionBar().setTitle(R.string.menu_speakers);
                break;
            case R.id.nav_sponsors:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new SponsorsFragment()).commit();
                getSupportActionBar().setTitle(R.string.menu_sponsor);
                break;
            case R.id.nav_locations:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new LocationsFragment()).commit();
                getSupportActionBar().setTitle(R.string.menu_locations);
                break;
            case R.id.nav_map:
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.content_frame,
                        ((OpenEventApp) getApplication())
                                .getMapModuleFactory()
                                .provideMapModule()
                                .provideMapFragment()).commit();
                getSupportActionBar().setTitle(R.string.menu_map);
                break;
            case R.id.nav_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                mDrawerLayout.closeDrawers();
                startActivity(intent);

                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

        }
        currentMenuItemId = menuItemId;
        mDrawerLayout.closeDrawers();
    }

    public void showErrorDialog(String errorType, String errorDesc){
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
    public void showNetworkDialog(ShowNetworkDialogEvent event){
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
    public void downloadData(DataDownloadEvent event){
        DataDownload download = new DataDownload();
        download.downloadVersions();
        downloadProgress.setVisibility(View.VISIBLE);
        Log.d("DataNotif", "Download has started");
    }

    @Subscribe
    public void ErrorHandlerEvent(RetrofitError cause) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = connMgr.getActiveNetworkInfo();
        if(!(netinfo!=null && netinfo.isConnected())){
            OpenEventApp.postEventOnUIThread(new ShowNetworkDialogEvent());
        }
        else
        {
            switch(cause.getKind()){
                case CONVERSION: {
                    Log.d(TYPE, "ConversionError");
                    errorType="Conversion Error";
                    errorDesc=String.valueOf(cause.getCause());
                    break;
                }
                case HTTP: {
                    Log.d(TYPE, "HTTPError");
                    errorType="HTTP Error";
                    errorDesc=String.valueOf(cause.getResponse().getStatus());
                    Log.d(ERROR_CODE, String.valueOf(cause.getResponse().getStatus()));
                    break;
                }
                case UNEXPECTED: {
                    Log.d(TYPE, "UnexpectedError");
                    errorType="Unexpected Error";
                    errorDesc=String.valueOf(cause.getCause());
                    break;
                }
                case NETWORK: {
                    Log.d(TYPE, "NetworkError");
                    errorType="Network Error";
                    errorDesc=String.valueOf(cause.getCause());
                    break;
                }
                default: {
                    Log.d(TYPE, "Other Error");
                    errorType="Other Error";
                    errorDesc=String.valueOf(cause.getCause());
                }
            }
            showErrorDialog(errorType, errorDesc);
        }
    }

    @Subscribe

    public void onConnectionChangeReact(ConnectionCheckEvent event)
    {
        if(event.connState())
        {
            OpenEventApp.postEventOnUIThread(new DataDownloadEvent());
            Log.d("NetNotif", "Connected to Internet");
        }
        else
        {
            Log.d("NetNotif", "Not connected to Internet");
            OpenEventApp.postEventOnUIThread(new ShowNetworkDialogEvent());
        }
    }
}