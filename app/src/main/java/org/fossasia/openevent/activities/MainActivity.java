package org.fossasia.openevent.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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

import org.fossasia.openevent.BuildConfig;
import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.dbutils.DataDownload;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.ConnectionCheckEvent;
import org.fossasia.openevent.events.CounterEvent;
import org.fossasia.openevent.events.DataDownloadEvent;
import org.fossasia.openevent.events.ErrorLogProcessEvent;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit.RetrofitError;
import timber.log.Timber;

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
    private static final String format = "yyyy-MM-dd HH:mm:ss.SSS";
    private final int request_code = 123;
    public File logfile;
    public int permission;
    public String errorLog;
    public boolean isCleared;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        counter = 0;
        errorLog = "";
        setContentView(R.layout.activity_main);
        eventsDone = 0;
        isCleared = false;
        setUpToolbar();
        setUpNavDrawer();
        mainFrame = (FrameLayout) findViewById(R.id.layout_main);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        downloadProgress = (ProgressBar) findViewById(R.id.progress);
        downloadProgress.setVisibility(View.VISIBLE);
        downloadProgress.setIndeterminate(true);
        this.findViewById(android.R.id.content).setBackgroundColor(Color.LTGRAY);
        if (savedInstanceState == null) {
            currentMenuItemId = R.id.nav_tracks;
        } else {
            currentMenuItemId = savedInstanceState.getInt(STATE_FRAGMENT);
        }
        doMenuAction(currentMenuItemId);
        permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        OpenEventApp.postEventOnUIThread(new DataDownloadEvent());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case request_code: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    writeLogsToFile(errorLog);
                } else {
                    Timber.d("Not Granted");
                }
            }
        }
    }

    public boolean handleErrorLogs(int priority) {
        return (!(priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO));
    }

    public void writeLogsToFile(String text) {
        permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            logfile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/OpenEventAppErrors.txt");
            Timber.d("Value of isCleared is: ".concat(String.valueOf(isCleared)));
            if (logfile.isFile() && logfile.exists() && !isCleared) {
                try {
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logfile, false));
                    bufferedWriter.close();
                    Timber.d("Log file cleared");
                    isCleared = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Timber.d("File is not empty");
            }

            if (!logfile.exists()) {
                try {
                    logfile.createNewFile();
                    Timber.d("Created new file");
                } catch (IOException e) {
                    Timber.d("Error. Cannot create file: " + String.valueOf(e.getCause()));
                }
            }
            if (logfile.length() == 0 && logfile.exists()) {
                try {
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logfile, true));
                    bufferedWriter.append("App version: ".concat(BuildConfig.VERSION_NAME));
                    bufferedWriter.newLine();
                    bufferedWriter.append("Android Version: ".concat(Build.VERSION.RELEASE));
                    bufferedWriter.newLine();
                    bufferedWriter.append("SDK level: ".concat(String.valueOf(Build.VERSION.SDK_INT)));
                    bufferedWriter.newLine();
                    bufferedWriter.newLine();
                    bufferedWriter.close();
                    Timber.d("Added version details");
                } catch (IOException e) {
                    Timber.d("Cannot add version: ".concat(String.valueOf(e.getCause())));
                }
            }
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logfile, true));
                bufferedWriter.append(getTimeStampString().concat("--"));
                bufferedWriter.append(text);
                bufferedWriter.newLine();
                bufferedWriter.close();
                Timber.d("Written logs to file");
            } catch (IOException e) {
                Timber.d("Cannot append logs: " + String.valueOf(e.getCause()));
            }
        } else {
            Timber.d("writeLogsToFile permission not granted");
        }
    }

    private static String calendarProcess(Calendar date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        return dateFormat.format(date.getTime());
    }

    private static String getTimeStampString() {
        Calendar calendar = Calendar.getInstance();
        return calendarProcess(calendar, format);
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

    private void doMenuAction(int menuItemId) {
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

    public void showErrorDialog(String errorType, String errorDesc) {
        Timber.d("ShowErrorDialog called");
        downloadProgress.setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.error)
                .setMessage(errorType + ": " + errorDesc + "\n" + "Send error report?")
                .setNeutralButton(R.string.cancel, null)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Timber.d("Email intent sent");
                        sendEmail();
                    }
                })
                .create();
        builder.show();
    }

    public void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setType("jpeg/image");
        emailIntent.setData(Uri.parse("mailto:shiven.exun97@gmail.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "OpenEventApp Error Report");
        if (permission == PackageManager.PERMISSION_GRANTED) {
            logfile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/OpenEventAppErrors.txt");
            if (logfile.exists() && logfile.canRead()) {
                Timber.d("File existent");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Error Report attached");
                Uri uri = Uri.fromFile(logfile);
                emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                Timber.d("File sending...");
                this.startActivity(Intent.createChooser(emailIntent, null));
            } else {
                Timber.d("No error logs to mail");
            }
        } else {
            Timber.d("sendEmail won't work");
        }

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
    public void errorHandlerEvent(RetrofitError cause) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = connMgr.getActiveNetworkInfo();
        if (!(netinfo != null && netinfo.isConnected())) {
            OpenEventApp.postEventOnUIThread(new ShowNetworkDialogEvent());
        } else {
            switch (cause.getKind()) {
                case CONVERSION: {
                    errorType = "Conversion Error";
                    errorDesc = String.valueOf(cause.getCause());
                    break;
                }
                case HTTP: {
                    errorType = "HTTP Error";
                    errorDesc = String.valueOf(cause.getResponse().getStatus());
                    break;
                }
                case UNEXPECTED: {
                    errorType = "Unexpected Error";
                    errorDesc = String.valueOf(cause.getCause());
                    break;
                }
                case NETWORK: {
                    errorType = "Network Error";
                    errorDesc = String.valueOf(cause.getCause());
                    break;
                }
                default: {
                    errorType = "Other Error";
                    errorDesc = String.valueOf(cause.getCause());
                }
            }
            Timber.e(errorType);
            Timber.e(errorDesc);
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

    @Subscribe
    public void processErrorLog(ErrorLogProcessEvent event) {
        if (handleErrorLogs(event.priority)) {
            errorLog = event.message;
            Timber.d("Sending message to log file");
            Timber.d(errorLog);
            permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Error")
                            .setMessage("You need to allow access to External Storage")
                            .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, request_code);
                                }
                            })
                            .setNeutralButton("Cancel", null)
                            .create();
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, request_code);
                }
            } else {
                writeLogsToFile(errorLog);
            }
        }
    }
}