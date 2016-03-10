package org.fossasia.openevent.activities;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import com.squareup.picasso.Target;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.dbutils.DataDownloadManager;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.*;
import org.fossasia.openevent.fragments.*;
import org.fossasia.openevent.utils.SmoothActionBarDrawerToggle;
import org.fossasia.openevent.widget.DialogFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RetrofitError;
import timber.log.Timber;

public class MainActivity extends BaseActivity {

    public static final String TYPE = "RetrofitError Type";

    public static final String ERROR_CODE = "Error Code";

    private static final String COUNTER_TAG = "Donecounter";

    private final static String STATE_FRAGMENT = "stateFragment";

    private static final String NAV_ITEM = "navItem";

    private static final String FILENAME_NAV_HEADER_DRAWER = "navheader";

    private static final String BOOKMARK = "bookmarks";

    private final String FRAGMENT_TAG = "FTAG";

    public String errorType;

    public String errorDesc;

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
        eventsDone = 0;
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setUpToolbar();
        setUpNavDrawer();

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
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            super.onBackPressed();
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

            drawerLayout.setDrawerListener(smoothActionBarToggle);

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
        DbSingleton dbSingleton = DbSingleton.getInstance();
        if (!(dbSingleton.getEventDetails().getLogo().isEmpty())) {
            final ImageView headerDrawer = (ImageView) findViewById(R.id.headerDrawer);
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                    headerDrawer.setImageBitmap(bitmap);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            File dir = new ContextWrapper(getApplicationContext()).getFilesDir();
                            File imageFile = new File(dir, FILENAME_NAV_HEADER_DRAWER);
                            try {
                                FileOutputStream outputStream = new FileOutputStream(imageFile);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                outputStream.flush();
                                outputStream.close();
                            } catch (FileNotFoundException e) {
                                Timber.w("Navigation Drawer Image: File Not Created");
                            } catch (IOException e) {
                                Timber.w("Navigation Drawer Image: Not Saved");
                            }
                        }
                    }).start();

                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            headerDrawer.setTag(target);
            Picasso.with(getApplicationContext()).load(dbSingleton.getEventDetails().getLogo()).into(target);
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
        final ImageView headerDrawer = (ImageView) navigationView.findViewById(R.id.headerDrawer);
        File dir = new ContextWrapper(getApplicationContext()).getFilesDir();
        File imageFile = new File(dir, FILENAME_NAV_HEADER_DRAWER);
        if (imageFile.exists()) {
            Picasso.with(getApplicationContext()).load(imageFile).into(headerDrawer);
        }

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
        DataDownloadManager.getInstance().downloadVersions();
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
}
