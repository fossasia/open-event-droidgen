package org.fossasia.openevent.core.main;

import android.app.Dialog;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.otto.Subscribe;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ConstantStrings;
import org.fossasia.openevent.common.api.APIClient;
import org.fossasia.openevent.common.api.DataDownloadManager;
import org.fossasia.openevent.common.api.DownloadCompleteHandler;
import org.fossasia.openevent.common.api.Urls;
import org.fossasia.openevent.common.date.DateConverter;
import org.fossasia.openevent.common.events.CounterEvent;
import org.fossasia.openevent.common.events.DataDownloadEvent;
import org.fossasia.openevent.common.events.DownloadEvent;
import org.fossasia.openevent.common.events.EventDownloadEvent;
import org.fossasia.openevent.common.events.EventLoadedEvent;
import org.fossasia.openevent.common.events.JsonReadEvent;
import org.fossasia.openevent.common.events.MicrolocationDownloadEvent;
import org.fossasia.openevent.common.events.NoInternetEvent;
import org.fossasia.openevent.common.events.RetrofitError;
import org.fossasia.openevent.common.events.RetrofitResponseEvent;
import org.fossasia.openevent.common.events.SessionDownloadEvent;
import org.fossasia.openevent.common.events.SessionTypesDownloadEvent;
import org.fossasia.openevent.common.events.ShowNetworkDialogEvent;
import org.fossasia.openevent.common.events.SpeakerDownloadEvent;
import org.fossasia.openevent.common.events.SponsorDownloadEvent;
import org.fossasia.openevent.common.events.TracksDownloadEvent;
import org.fossasia.openevent.common.network.NetworkUtils;
import org.fossasia.openevent.common.ui.DialogFactory;
import org.fossasia.openevent.common.ui.SmoothActionBarDrawerToggle;
import org.fossasia.openevent.common.ui.base.BaseActivity;
import org.fossasia.openevent.common.ui.image.OnImageZoomListener;
import org.fossasia.openevent.common.ui.image.ZoomableImageUtil;
import org.fossasia.openevent.common.utils.CommonTaskLoop;
import org.fossasia.openevent.common.utils.SharedPreferencesUtil;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.config.StrategyRegistry;
import org.fossasia.openevent.core.about.AboutFragment;
import org.fossasia.openevent.core.auth.AuthUtil;
import org.fossasia.openevent.core.auth.profile.UserProfileActivity;
import org.fossasia.openevent.core.faqs.FAQFragment;
import org.fossasia.openevent.core.feed.facebook.CommentsDialogFragment;
import org.fossasia.openevent.core.feed.facebook.FeedAdapter;
import org.fossasia.openevent.core.feed.facebook.FeedFragment;
import org.fossasia.openevent.core.feed.facebook.api.CommentItem;
import org.fossasia.openevent.core.feed.facebook.api.FacebookApi;
import org.fossasia.openevent.core.feed.twitter.TwitterFeedFragment;
import org.fossasia.openevent.core.location.LocationsFragment;
import org.fossasia.openevent.core.notifications.NotificationsFragment;
import org.fossasia.openevent.core.schedule.ScheduleFragment;
import org.fossasia.openevent.core.settings.SettingsActivity;
import org.fossasia.openevent.core.speaker.SpeakersListFragment;
import org.fossasia.openevent.core.sponsor.SponsorsFragment;
import org.fossasia.openevent.core.track.TracksFragment;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.SessionType;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.data.extras.SocialLink;
import org.fossasia.openevent.data.repository.RealmDataRepository;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
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
    private final CompositeDisposable disposable = new CompositeDisposable();
    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();
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
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        ButterKnife.setDebug(true);
        fragmentManager = getSupportFragmentManager();
        setTheme(R.style.AppTheme_NoActionBar_MainTheme);
        super.onCreate(savedInstanceState);

        SharedPreferencesUtil.putInt(ConstantStrings.SESSION_MAP_ID, -1);
        isTwoPane = drawerLayout == null;
        Utils.setTwoPane(isTwoPane);

        setSupportActionBar(toolbar);
        setUpNavDrawer();
        setUpCustomTab();
        setupEvent();

        completeHandler = DownloadCompleteHandler.with(context);

        if(!SharedPreferencesUtil.getBoolean(ConstantStrings.IS_DOWNLOAD_DONE, false)) {
            if(Utils.isBaseUrlEmpty()) {
                downloadFromAssets();
            } else {
                downloadData();
            }
        } else {
            completeHandler.hide();
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
        bindService(customTabIntent, customTabsServiceConnection, Context.BIND_AUTO_CREATE);
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
        StrategyRegistry.getInstance().getEventBusStrategy().getEventBus().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StrategyRegistry.getInstance().getEventBusStrategy().getEventBus().register(this);
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

    private void setupEvent() {
        event = realmRepo.getEventSync();

        if(event == null)
            return;

        setNavHeader(event);
    }

    private void setUpNavDrawer() {
        setUpUserProfileMenu();
        headerView = navigationView.getHeaderView(0).findViewById(R.id.headerDrawer);
        if (!isTwoPane) {
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

            if (drawerLayout != null) {
                drawerLayout.addDrawerListener(smoothActionBarToggle);
            }
            ab.setDisplayHomeAsUpEnabled(true);
            smoothActionBarToggle.syncState();
        } else if (toolbar.getTitle().equals(getString(R.string.menu_about))) {
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
            StrategyRegistry.getInstance().getHttpStrategy().getPicassoWithCache().load(logo).into(headerView);
        } else {
            StrategyRegistry.getInstance().getHttpStrategy().getPicassoWithCache().load(R.mipmap.ic_launcher).into(headerView);
        }
    }

    private void saveEventDates(Event event) {
        String startTime = event.getStartsAt();
        String endTime = event.getEndsAt();

        disposable.add(Observable.fromCallable(() ->
                DateConverter.getDaysInBetween(startTime, endTime)
        ).subscribe(eventDates -> realmRepo.saveEventDates(eventDates).subscribe(), throwable -> {
            Timber.e(throwable);
            Timber.e("Error start parsing start date: %s and end date: %s in ISO format",
                    startTime, endTime);
            StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(new RetrofitError(new Throwable("Error parsing dates")));
        }));
    }

    private void syncComplete() {
        String successMessage = "Data loaded from JSON";

        if (fromServer) {
            // Event successfully loaded, set data downloaded to true
            SharedPreferencesUtil.putBoolean(ConstantStrings.IS_DOWNLOAD_DONE, true);

            successMessage = "Download done";
        }

        Snackbar.make(mainFrame, getString(R.string.download_complete), Snackbar.LENGTH_SHORT).show();
        Timber.d(successMessage);

        setupEvent();
        StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(new EventLoadedEvent(event));
        saveEventDates(event);

        storeFeedDetails();
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
                            StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(new DataDownloadEvent());
                        } else {
                            final Snackbar snackbar = Snackbar.make(mainFrame, R.string.internet_preference_warning, Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction(R.string.yes, view -> downloadFromAssets());
                            snackbar.show();
                        }
                    }));
        } else {
            StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(new DataDownloadEvent());
        }
    }

    private void storeFeedDetails() {
        //Store the facebook and twitter page name in the shared preference from the database
        storePageName(ConstantStrings.SOCIAL_LINK_FACEBOOK, ConstantStrings.FACEBOOK_PAGE_NAME);
        storePageName(ConstantStrings.SOCIAL_LINK_TWITTER, ConstantStrings.TWITTER_PAGE_NAME);

        if(SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_ID, null) == null &&
                SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_NAME, null) != null) {
            disposable.add(FacebookApi.getFacebookGraphAPI().getPageId(SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_NAME, null),
                    getResources().getString(R.string.facebook_access_token))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(facebookPageId -> {
                                String id = facebookPageId.getId();
                                SharedPreferencesUtil.putString(ConstantStrings.FACEBOOK_PAGE_ID, id);
                            },
                            throwable -> Timber.d("Facebook page id download failed: " + throwable.toString())));
        }
    }

    private void storePageName(String feedType, String key) {
        if (SharedPreferencesUtil.getString(key, null) == null) {
            RealmList<SocialLink> socialLinks = event.getSocialLinks();
            RealmResults<SocialLink> page = socialLinks.where().equalTo("name", feedType).findAll();
            if (!page.isEmpty()) {
                SocialLink socialLink = page.get(0);
                String socialLinkUrl = socialLink.getLink();
                String tempString = ".com/";
                String pageName = socialLinkUrl.substring(socialLinkUrl.indexOf(tempString) + tempString.length()).replace("/", "");
                SharedPreferencesUtil.putString(key, pageName);
            }
        }
    }

    public void downloadData() {
        NetworkUtils.checkConnection(new WeakReference<>(this), new NetworkUtils.NetworkStateReceiverListener() {

            @Override
            public void networkAvailable() {
                // Network is available
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
                StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(new DataDownloadEvent());
            else
                StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(event);
        }).show();
        SharedPreferencesUtil.putBoolean(ConstantStrings.IS_DOWNLOAD_DONE, false);

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            final int id = menuItem.getItemId();
            if(!isTwoPane) {
                if (drawerLayout != null) {
                    drawerLayout.closeDrawers();
                    drawerLayout.postDelayed(() -> doMenuAction(id), 300);
                }
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
            case R.id.nav_notification:
                replaceFragment(NotificationsFragment.getInstance(), R.string.menu_notification);
                break;
            case R.id.nav_tracks:
                replaceFragment(new TracksFragment(), R.string.menu_tracks);
                break;
            case R.id.nav_feed:
                replaceFragment(FeedFragment.getInstance(this, this), R.string.menu_feed);
                break;
            case R.id.nav_twitter_feed:
                replaceFragment(TwitterFeedFragment.getInstance(this), R.string.menu_twitter);
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
            case R.id.nav_faqs:
                replaceFragment(new FAQFragment(), R.string.menu_faqs);
                break;
            case R.id.nav_map:
                Bundle bundle = new Bundle();
                bundle.putBoolean(ConstantStrings.IS_MAP_FRAGMENT_FROM_MAIN_ACTIVITY, true);
                Fragment mapFragment = StrategyRegistry.getInstance().getMapModuleStrategy()
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

        if (!isTwoPane && (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START))) {
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
        disposable.add(completeHandler.startListening()
                .show()
                .withCompletionListener()
                .subscribe(this::syncComplete, throwable -> {
                    throwable.printStackTrace();
                    Timber.e(throwable);
                    if (throwable instanceof DownloadCompleteHandler.DataEventError) {
                        downloadFailed(((DownloadCompleteHandler.DataEventError) throwable)
                                .getDataDownloadEvent());
                    } else {
                        StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(new RetrofitError(throwable));
                    }
                }));
    }

    private void startDownload() {
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

    @Subscribe
    public void downloadData(DataDownloadEvent event) {
        switch (Urls.getBaseUrl()) {
            case Urls.INVALID_LINK:
                showErrorDialog("Invalid Api", "Api link doesn't seem to be valid");
                downloadFromAssets();
                break;
            case Urls.EMPTY_LINK:
                downloadFromAssets();
                break;
            default:
                startDownload();
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
        final String name = jsonReadEvent.getName();
        final String json = jsonReadEvent.getJson();

        disposable.add(Completable.fromAction(() -> {
            ObjectMapper objectMapper = APIClient.getObjectMapper();

            // Need separate instance for background thread
            Realm realm = Realm.getDefaultInstance();

            RealmDataRepository realmDataRepository = RealmDataRepository
                    .getInstance(realm);

            switch (name) {
                case ConstantStrings.EVENT: {
                    Event event = objectMapper.readValue(json, Event.class);

                    saveEventDates(event);
                    realmDataRepository.saveEvent(event).subscribe();

                    realmDataRepository.saveEvent(event).subscribe();

                    StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(new EventDownloadEvent(true));
                    break;
                } case ConstantStrings.TRACKS: {
                    List<Track> tracks = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Track.class));

                    realmDataRepository.saveTracks(tracks).subscribe();

                    StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(new TracksDownloadEvent(true));
                    break;
                } case ConstantStrings.SESSIONS: {
                    List<Session> sessions = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Session.class));

                    for (Session current : sessions) {
                        current.setStartDate(current.getStartsAt().split("T")[0]);
                    }

                    realmDataRepository.saveSessions(sessions).subscribe();

                    StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(new SessionDownloadEvent(true));
                    break;
                } case ConstantStrings.SPEAKERS: {
                    List<Speaker> speakers = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Speaker.class));

                    realmRepo.saveSpeakers(speakers).subscribe();

                    StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(new SpeakerDownloadEvent(true));
                    break;
                } case ConstantStrings.SPONSORS: {
                    List<Sponsor> sponsors = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Sponsor.class));

                    realmRepo.saveSponsors(sponsors).subscribe();

                    StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(new SponsorDownloadEvent(true));
                    break;
                } case ConstantStrings.MICROLOCATIONS: {
                    List<Microlocation> microlocations = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Microlocation.class));

                    realmRepo.saveLocations(microlocations).subscribe();

                    StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(new MicrolocationDownloadEvent(true));
                    break;
                } case ConstantStrings.SESSION_TYPES: {
                    List<SessionType> sessionTypes = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, SessionType.class));

                    realmRepo.saveSessionTypes(sessionTypes).subscribe();

                    StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(new SessionTypesDownloadEvent(true));
                    break;
                } default:
                    //do nothing
            }
            realm.close();
        }).observeOn(Schedulers.computation()).subscribe(() -> Timber.d("Saved event from JSON"), throwable -> {
            throwable.printStackTrace();
            Timber.e(throwable);
            StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(new RetrofitError(throwable));
        }));
    }

    @Subscribe
    public void errorHandlerEvent(RetrofitError error) {
        String errorType;
        String errorDesc;
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = connMgr != null ? connMgr.getActiveNetworkInfo() : null;
        if (!(netinfo != null && netinfo.isConnected())) {
            StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(new ShowNetworkDialogEvent());
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

            StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(new CounterEvent(7)); // Bump if increased

            readJsonAsset(Urls.EVENT);
            readJsonAsset(Urls.SESSIONS);
            readJsonAsset(Urls.SPEAKERS);
            readJsonAsset(Urls.TRACKS);
            readJsonAsset(Urls.SPONSORS);
            readJsonAsset(Urls.MICROLOCATIONS);
            readJsonAsset(Urls.SESSION_TYPES);
            //readJsonAsset(Urls.FAQS);
        } else {
            completeHandler.hide();
        }
        SharedPreferencesUtil.putBoolean(ConstantStrings.IS_DOWNLOAD_DONE, true);
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
                StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(new JsonReadEvent(name, json));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(customTabsServiceConnection);
        disposable.dispose();
        if(event != null)
            event.removeAllChangeListeners();
        if(completeHandler != null)
            completeHandler.stopListening();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
