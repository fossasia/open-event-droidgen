package org.fossasia.openevent.activities;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.otto.Subscribe;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.auth.UserProfileActivity;
import org.fossasia.openevent.adapters.FeedAdapter;
import org.fossasia.openevent.api.DataDownloadManager;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.facebook.CommentItem;
import org.fossasia.openevent.events.CounterEvent;
import org.fossasia.openevent.events.DownloadEvent;
import org.fossasia.openevent.events.EventLoadedEvent;
import org.fossasia.openevent.events.JsonReadEvent;
import org.fossasia.openevent.events.NoInternetEvent;
import org.fossasia.openevent.events.RetrofitError;
import org.fossasia.openevent.events.RetrofitResponseEvent;
import org.fossasia.openevent.events.ShowNetworkDialogEvent;
import org.fossasia.openevent.fragments.AboutFragment;
import org.fossasia.openevent.fragments.CommentsDialogFragment;
import org.fossasia.openevent.fragments.FeedFragment;
import org.fossasia.openevent.fragments.LocationsFragment;
import org.fossasia.openevent.fragments.ScheduleFragment;
import org.fossasia.openevent.fragments.SpeakersListFragment;
import org.fossasia.openevent.fragments.SponsorsFragment;
import org.fossasia.openevent.fragments.TracksFragment;
import org.fossasia.openevent.modules.OnImageZoomListener;
import org.fossasia.openevent.utils.AuthUtil;
import org.fossasia.openevent.utils.CommonTaskLoop;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.DownloadCompleteHandler;
import org.fossasia.openevent.utils.NetworkUtils;
import org.fossasia.openevent.utils.SharedPreferencesUtil;
import org.fossasia.openevent.utils.SmoothActionBarDrawerToggle;
import org.fossasia.openevent.utils.Utils;
import org.fossasia.openevent.utils.ZoomableImageUtil;
import org.fossasia.openevent.viewmodels.MainActivityViewModel;
import org.fossasia.openevent.widget.DialogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements FeedAdapter.OpenCommentsDialogListener, OnImageZoomListener, AboutFragment.OnMapSelectedListener {

    private static final String STATE_FRAGMENT = "stateFragment";
    private static final String NAV_ITEM = "navItem";
    private static final String BOOKMARK = "bookmarks";
    private static final String FRAGMENT_TAG_HOME = "HOME_FRAGMENT";
    private static final String FRAGMENT_TAG_REST = "REST_FRAGMENTS";

    private boolean fromServer = true;
    private boolean atHome = true;
    private boolean backPressedOnce;
    private boolean isTwoPane;
    private boolean isAuthEnabled = SharedPreferencesUtil.getBoolean(ConstantStrings.IS_AUTH_ENABLED, false);
    private boolean customTabsSupported;
    private int currentMenuItemId;
    private boolean isMapFragment;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.layout_main) CoordinatorLayout mainFrame;
    @BindView(R.id.appbar) AppBarLayout appBarLayout;
    @Nullable @BindView(R.id.drawer) DrawerLayout drawerLayout;
    private ImageView headerView;

    private Context context;
    private Dialog dialogNetworkNotification;
    private FragmentManager fragmentManager;
    private CustomTabsServiceConnection customTabsServiceConnection;
    private CustomTabsClient customTabsClient;
    private DownloadCompleteHandler completeHandler;
    private CompositeDisposable disposable;
    private MainActivityViewModel mainActivityViewModel;
    private Event event; // Future Event, stored to remove listeners
    private AboutFragment.OnMapSelectedListener onMapSelectedListener = value -> isMapFragment = value;

    public static Intent createLaunchFragmentIntent(Context context) {
        return new Intent(context, MainActivity.class)
                .putExtra(NAV_ITEM, BOOKMARK);
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
        context = this;
        ((OpenEventApp) getApplicationContext()).attachMainActivity(this);
        ButterKnife.setDebug(true);
        fragmentManager = getSupportFragmentManager();
        setTheme(R.style.AppTheme_NoActionBar_MainTheme);
        super.onCreate(savedInstanceState);

        mainActivityViewModel= ViewModelProviders.of(this).get(MainActivityViewModel.class);

        SharedPreferencesUtil.putInt(ConstantStrings.SESSION_MAP_ID, -1);
        isTwoPane = drawerLayout == null;
        Utils.setTwoPane(isTwoPane);

        disposable = new CompositeDisposable();

        setUpToolbar();
        setUpNavDrawer();
        setUpCustomTab();
        setupEvent();

        completeHandler = DownloadCompleteHandler.with(context);

        if (Utils.isBaseUrlEmpty()) {
            if (!SharedPreferencesUtil.getBoolean(ConstantStrings.IS_DOWNLOAD_DONE, false)) {
                downloadFromAssets();
                SharedPreferencesUtil.putBoolean(ConstantStrings.IS_DOWNLOAD_DONE, true);
            }
        } else {
            showDownloadDialog();
        }

        if (savedInstanceState == null) {
            currentMenuItemId = R.id.nav_home;
        } else {
            currentMenuItemId = savedInstanceState.getInt(STATE_FRAGMENT);
        }

        if (getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_HOME) == null &&
                getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_REST) == null) {
            doMenuAction(currentMenuItemId);
        }
    }

    private void setUpCustomTab() {
        Intent customTabIntent = new Intent("android.support.customtabs.action.CustomTabsService");
        customTabIntent.setPackage("com.android.chrome");
        customTabsServiceConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
                customTabsClient = client;
                customTabsClient.warmup(0L);
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                //initially left empty
            }
        };
        customTabsSupported = bindService(customTabIntent, customTabsServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setUserProfileMenuItem(){
        MenuItem userProfileMenuItem = navigationView.getMenu().findItem(R.id.nav_user_profile);
        if (AuthUtil.isUserLoggedIn()) {
            String email = SharedPreferencesUtil.getString(ConstantStrings.USER_EMAIL, null);
            String firstName = SharedPreferencesUtil.getString(ConstantStrings.USER_FIRST_NAME, null);
            String lastName = SharedPreferencesUtil.getString(ConstantStrings.USER_LAST_NAME, null);

            if (!TextUtils.isEmpty(firstName))
                userProfileMenuItem.setTitle(firstName + " " + lastName);
            else if (!TextUtils.isEmpty(email))
                userProfileMenuItem.setTitle(email);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
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
        setUserProfileMenuItem();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_FRAGMENT, currentMenuItemId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        currentMenuItemId = savedInstanceState.getInt(STATE_FRAGMENT);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        setupDrawerContent(navigationView);
        return super.onPrepareOptionsMenu(menu);
    }

    private void setUpToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    private void setupEvent() {
        mainActivityViewModel.getEvent().observe(this, eventData -> {
            event = eventData;
            setNavHeader(event);
        });
    }

    private void setUpNavDrawer() {
        setUpUserProfileMenu();
        headerView = navigationView.getHeaderView(0).findViewById(R.id.headerDrawer);
        if (toolbar != null && !isTwoPane) {
            final ActionBar ab = getSupportActionBar();
            if(ab == null) return;
            SmoothActionBarDrawerToggle smoothActionBarToggle = new SmoothActionBarDrawerToggle(this,
                    drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

                @Override
                public void onDrawerStateChanged(int newState) {
                    super.onDrawerStateChanged(newState);

                    if(toolbar.getTitle().equals(getString(R.string.menu_about))) {
                        navigationView.setCheckedItem(R.id.nav_home);
                    }
                }
            };

            drawerLayout.addDrawerListener(smoothActionBarToggle);
            ab.setDisplayHomeAsUpEnabled(true);
            smoothActionBarToggle.syncState();
        } else if (toolbar!=null && toolbar.getTitle().equals(getString(R.string.menu_about))) {
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private void setUpUserProfileMenu() {
        if (!isAuthEnabled) {
            navigationView.getMenu().setGroupVisible(R.id.menu_user_profile, false);
            return;
        }

        setUserProfileMenuItem();
    }

    private void setNavHeader(Event event) {
        String logo = event.getLogoUrl();
        if (!Utils.isEmpty(logo)) {
            OpenEventApp.picassoWithCache.load(logo).into(headerView);
        } else {
            OpenEventApp.picassoWithCache.load(R.mipmap.ic_launcher).into(headerView);
        }
    }


    private void syncComplete() {
        String successMessage = "Data loaded from JSON";
        String pageId = SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_ID, null);
        String pageName = SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_NAME, null);

        String token = getResources().getString(R.string.facebook_app_id);


        if (fromServer) {
            // Event successfully loaded, set data downloaded to true
            SharedPreferencesUtil.putBoolean(ConstantStrings.IS_DOWNLOAD_DONE, true);

            successMessage = "Download done";
        }

        Snackbar.make(mainFrame, getString(R.string.download_complete), Snackbar.LENGTH_SHORT).show();
        Timber.d(successMessage);

        setupEvent();
        OpenEventApp.postEventOnUIThread(new EventLoadedEvent(event));
        mainActivityViewModel.saveEventDates(event);

        mainActivityViewModel.downloadPageId(pageName).observe(this, fbPageName -> SharedPreferencesUtil.putString(ConstantStrings.FACEBOOK_PAGE_NAME, fbPageName));
        mainActivityViewModel.downloadPageName(pageId, pageName, token).observe(this, fbPageId -> SharedPreferencesUtil.putString(ConstantStrings.FACEBOOK_PAGE_ID, fbPageId));

    }

    private void startDownloadFromNetwork() {
        fromServer = true;
        boolean preference = SharedPreferencesUtil.getBoolean(getResources().getString(R.string.download_mode_key), true);
        if (preference) {
            disposable.add(NetworkUtils.haveNetworkConnectionObservable(MainActivity.this)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(isConnected -> {
                        if (isConnected) {
                            startDownloading();
                        } else {
                            final Snackbar snackbar = Snackbar.make(mainFrame, R.string.internet_preference_warning, Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction(R.string.yes, view -> downloadFromAssets());
                            snackbar.show();
                        }
                    }));
        } else {
            startDownloading();
        }
    }

    public void showDownloadDialog() {
        NetworkUtils.checkConnection(new WeakReference<>(this), new NetworkUtils.NetworkStateReceiverListener() {

            @Override
            public void networkAvailable() {
                // Network is available
                if (!SharedPreferencesUtil.getBoolean(ConstantStrings.IS_DOWNLOAD_DONE, false)) {
                    DialogFactory.createDownloadDialog(context, R.string.download_assets, R.string.charges_warning,
                            (dialogInterface, button) -> {
                                switch (button) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        startDownloadFromNetwork();
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        downloadFromAssets();
                                        break;
                                    default:
                                        // No action to be taken
                                }
                            }).show();
                } else {
                    completeHandler.hide();
                }
            }

            @Override
            public void networkUnavailable() {
                downloadFromAssets();
                Snackbar.make(mainFrame, R.string.display_offline_schedule, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void downloadFailed(final DownloadEvent event) {
        Snackbar.make(mainFrame, getString(R.string.download_failed), Snackbar.LENGTH_LONG).setAction(R.string.retry_download, view -> {
            if (event == null)
                showDownloadDialog();
            else
                OpenEventApp.postEventOnUIThread(event);
        }).show();

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            final int id = menuItem.getItemId();
            if(!isTwoPane) {
                drawerLayout.closeDrawers();
                drawerLayout.postDelayed(() -> doMenuAction(id), 300);
            } else {
                doMenuAction(id);
            }
            return true;
        });
    }

    private void shareApplication() {
        try {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.whatsapp_promo_msg_template),
                    String.format(getString(R.string.app_share_url),getPackageName())));
            startActivity(shareIntent);
        }
        catch (Exception e) {
            Snackbar.make(mainFrame, getString(R.string.error_msg_retry), Snackbar.LENGTH_SHORT).show();
        }
    }


    private void replaceFragment(Fragment fragment, int title) {
        boolean isAtHome = false;
        String TAG = FRAGMENT_TAG_REST;

        if(fragment instanceof AboutFragment) {
            isAtHome = true;
            TAG = FRAGMENT_TAG_HOME;
        }

        addShadowToAppBar(currentMenuItemId != R.id.nav_schedule);
        atHome = isAtHome;

        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.content_frame, fragment, TAG).commit();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
        appBarLayout.setExpanded(true, true);
    }

    private void doMenuAction(int menuItemId) {
        Intent intent;
        currentMenuItemId = menuItemId;

        switch (menuItemId) {
            case R.id.nav_home:
                replaceFragment(AboutFragment.newInstance(onMapSelectedListener), R.string.menu_home);
                break;
            case R.id.nav_tracks:
                replaceFragment(new TracksFragment(), R.string.menu_tracks);
                break;
            case R.id.nav_feed:
                replaceFragment(FeedFragment.getInstance(this, this), R.string.menu_feed);
                break;
            case R.id.nav_schedule:
                replaceFragment(new ScheduleFragment(), R.string.menu_schedule);
                break;
            case R.id.nav_speakers:
                replaceFragment(new SpeakersListFragment(), R.string.menu_speakers);
                break;
            case R.id.nav_sponsors:
                replaceFragment(new SponsorsFragment(), R.string.menu_sponsor);
                break;
            case R.id.nav_locations:
                replaceFragment(new LocationsFragment(), R.string.menu_locations);
                break;
            case R.id.nav_map:
                Bundle bundle = new Bundle();
                bundle.putBoolean(ConstantStrings.IS_MAP_FRAGMENT_FROM_MAIN_ACTIVITY, true);
                Fragment mapFragment = ((OpenEventApp)getApplication())
                        .getMapModuleFactory()
                        .provideMapModule()
                        .provideMapFragment();
                mapFragment.setArguments(bundle);
                replaceFragment(mapFragment, R.string.menu_map);
                break;
            case R.id.nav_user_profile:
                intent = new Intent(MainActivity.this, UserProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_settings:
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_share:
                shareApplication();
                break;
            default:
                //Do nothing
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (!isTwoPane && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (atHome) {
            if (backPressedOnce) {
                super.onBackPressed();
            } else if (fragment instanceof AboutFragment) {
                backPressedOnce = true;
                Snackbar snackbar = Snackbar.make(mainFrame, R.string.press_back_again, 2000);
                snackbar.show();
                new Handler().postDelayed(() -> backPressedOnce = false, 2000);
            } else if (isMapFragment) {
                replaceFragment(AboutFragment.newInstance(onMapSelectedListener), R.string.menu_home);
                addShadowToAppBar(true);
            }
        } else {
            replaceFragment(AboutFragment.newInstance(onMapSelectedListener), R.string.menu_home);
            navigationView.setCheckedItem(R.id.nav_home);
            addShadowToAppBar(true);
        }
    }

    public void addShadowToAppBar(boolean addShadow) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return;

        if (addShadow) {
            appBarLayout.setElevation(Utils.dpToPx(4));
        } else {
            appBarLayout.setElevation(0);
        }
    }

    private void startDownloadListener() {
        Disposable disposable = completeHandler.startListening()
                .show()
                .withCompletionListener()
                .subscribe(this::syncComplete, throwable -> {
                    throwable.printStackTrace();
                    Timber.e(throwable);
                    if (throwable instanceof DownloadCompleteHandler.DataEventError) {
                        downloadFailed(((DownloadCompleteHandler.DataEventError) throwable)
                                .getDataDownloadEvent());
                    } else {
                        OpenEventApp.postEventOnUIThread(new RetrofitError(throwable));
                    }
                });
        // Currently a hack, refactor when possible
        mainActivityViewModel.addDisposable(disposable);
    }

    private void startDownloadFromApi() {
        int eventId = SharedPreferencesUtil.getInt(ConstantStrings.EVENT_ID, 0);
        if (eventId == 0)
            return;
        DataDownloadManager.getInstance().downloadEvent(eventId);
        startDownloadListener();
        Timber.d("Download has started");
    }

    public void showErrorDialog(String errorType, String errorDesc) {
        completeHandler.hide();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.error)
                .setMessage(errorType + ": " + errorDesc)
                .setNeutralButton(android.R.string.ok, null)
                .create();
        builder.show();
    }

    public void showErrorSnackbar(String errorType, String errorDesc) {
        completeHandler.hide();
        Snackbar.make(mainFrame, errorType + ": " + errorDesc, Snackbar.LENGTH_LONG).show();
    }

    public void dismissDialogNetworkNotification() {
        if (dialogNetworkNotification != null)
            dialogNetworkNotification.dismiss();
    }

    @Subscribe
    public void noInternet(NoInternetEvent event) {
        downloadFailed(null);
    }

    @Subscribe
    public void showNetworkDialog(ShowNetworkDialogEvent event) {
        completeHandler.hide();
        if (dialogNetworkNotification == null) {
            dialogNetworkNotification = DialogFactory.createCancellableSimpleActionDialog(this,
                    R.string.net_unavailable,
                    R.string.turn_on,
                    R.string.ok,
                    R.string.cancel,
                    (dialogInterface, button) -> {
                        switch (button) {
                            case DialogInterface.BUTTON_POSITIVE:
                                Intent setNetworkIntent = new Intent(Settings.ACTION_SETTINGS);
                                startActivity(setNetworkIntent);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                dialogNetworkNotification.dismiss();
                                return;
                            default:
                                // No action to be taken
                        }
                    });
        }

        dialogNetworkNotification.show();
    }

    public void startDownloading() {
        switch (Urls.getBaseUrl()) {
            case Urls.INVALID_LINK:
                showErrorDialog("Invalid Api", "Api link doesn't seem to be valid");
                downloadFromAssets();
                break;
            case Urls.EMPTY_LINK:
                downloadFromAssets();
                break;
            default:
                startDownloadFromApi();
                break;
        }
    }

    @Subscribe
    public void handleResponseEvent(RetrofitResponseEvent responseEvent) {
        Integer statusCode = responseEvent.getStatusCode();
        if (statusCode.equals(404)) {
            showErrorDialog(getResources().getString(R.string.http_error), statusCode + "\n" + getResources().getString(R.string.api_not_found));
            downloadFromAssets();
        }
    }

    @Subscribe
    public void handleJsonEvent(final JsonReadEvent jsonReadEvent) {
        mainActivityViewModel.handleEvent(jsonReadEvent);
    }

    @Subscribe
    public void errorHandlerEvent(RetrofitError error) {
        String errorType;
        String errorDesc;
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
            showErrorSnackbar(errorType, errorDesc);
        }
    }

    public void downloadFromAssets() {
        fromServer = false;
        if (!SharedPreferencesUtil.getBoolean(ConstantStrings.DATABASE_RECORDS_EXIST, false)) {
            //TODO: Add and Take counter value from to config.json
            SharedPreferencesUtil.putBoolean(ConstantStrings.DATABASE_RECORDS_EXIST, true);

            startDownloadListener();
            Timber.d("JSON parsing started");

            OpenEventApp.postEventOnUIThread(new CounterEvent(7)); // Bump if increased

            readJsonAsset(Urls.EVENT);
            readJsonAsset(Urls.SESSIONS);
            readJsonAsset(Urls.SPEAKERS);
            readJsonAsset(Urls.TRACKS);
            readJsonAsset(Urls.SPONSORS);
            readJsonAsset(Urls.MICROLOCATIONS);
            readJsonAsset(Urls.SESSION_TYPES);
        } else {
            completeHandler.hide();
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
                    if (inputStream.read(buffer) == -1)
                        Timber.d("Empty Stream");
                    inputStream.close();
                    json = new String(buffer, "UTF-8");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                OpenEventApp.postEventOnUIThread(new JsonReadEvent(name, json));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(customTabsServiceConnection);
        if(disposable != null && !disposable.isDisposed())
            disposable.dispose();
        if(completeHandler != null)
            completeHandler.stopListening();
        ((OpenEventApp) getApplicationContext()).detachMainActivity();
    }


    @Override
    public void openCommentsDialog(List<CommentItem> commentItems) {
        CommentsDialogFragment newFragment = new CommentsDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ConstantStrings.FACEBOOK_COMMENTS, new ArrayList<>(commentItems));
        newFragment.setArguments(bundle);
        newFragment.show(fragmentManager, "Comments");
    }

    @Override
    public void onMapSelected(boolean value) {
        //it is used to check if the maps fragment is selected
    }

    public void onZoom(String imageUri) {
        ZoomableImageUtil.showZoomableImageDialogFragment(fragmentManager, imageUri);
    }
}
