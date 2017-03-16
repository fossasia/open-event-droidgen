package org.fossasia.openevent.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.SessionsListAdapter;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.ConnectionCheckEvent;
import org.fossasia.openevent.utils.SpeakerIntent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

/**
 * Created by MananWason on 30-06-2015.
 */
public class SpeakerDetailsActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {

    private SessionsListAdapter sessionsListAdapter;

    private GridLayoutManager gridLayoutManager;

    private String speaker;

    private Speaker selectedSpeaker;

    private List<Session> mSessions = new ArrayList<>();

    private CustomTabsClient customTabsClient;

    private CustomTabsServiceConnection customTabsServiceConnection;

    private boolean isHideToolbarView = false;

    private static final int spearkerWiseSessionList = 2;

    @BindView(R.id.toolbar_speakers) Toolbar toolbar;
    @BindView(R.id.txt_no_sessions) TextView noSessionsView;
    @BindView(R.id.appbar) AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.imageView_linkedin) ImageView linkedin;
    @BindView(R.id.imageView_fb) ImageView fb;
    @BindView(R.id.imageView_github) ImageView github;
    @BindView(R.id.imageView_twitter) ImageView twitter;
    @BindView(R.id.imageView_web) ImageView website;
    @BindView(R.id.speaker_details_title) TextView speakerName;
    @BindView(R.id.speaker_image) ImageView speakerImage;
    @BindView(R.id.speaker_bio) TextView biography;
    @BindView(R.id.speaker_details_header) LinearLayout toolbarHeaderView;
    @BindView(R.id.recyclerView_speakers) RecyclerView sessionRecyclerView;
    @BindView(R.id.speaker_details_designation) TextView speakerDesignation;
    @BindView(R.id.progress_bar)
    protected ProgressBar progressBar;

    private CompositeDisposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        disposable = new CompositeDisposable();

        final DbSingleton dbSingleton = DbSingleton.getInstance();
        speaker = getIntent().getStringExtra(Speaker.SPEAKER);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout.setTitle(" ");

        disposable.add(dbSingleton.getSpeakerbySpeakersnameObservable(speaker)
                .subscribe(new Consumer<Speaker>() {
                    @Override
                    public void accept(@NonNull Speaker speaker) throws Exception {
                        selectedSpeaker = speaker;
                        loadSpeakerDetails();
                    }
                }));

        appBarLayout.addOnOffsetChangedListener(this);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        int spanCount = (int) (width / 250.00);

        sessionRecyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, spanCount);
        sessionRecyclerView.setLayoutManager(gridLayoutManager);

        sessionsListAdapter = new SessionsListAdapter(this, mSessions, spearkerWiseSessionList);
        sessionRecyclerView.setNestedScrollingEnabled(false);
        sessionRecyclerView.setAdapter(sessionsListAdapter);
        sessionRecyclerView.setItemAnimator(new DefaultItemAnimator());

        disposable.add(dbSingleton.getSessionbySpeakersNameObservable(speaker)
                .subscribe(new Consumer<ArrayList<Session>>() {
                    @Override
                    public void accept(@NonNull ArrayList<Session> sessions) throws Exception {
                        mSessions.clear();
                        mSessions.addAll(sessions);

                        sessionsListAdapter.notifyDataSetChanged();
                        handleVisibility();
                    }
                }));

        handleVisibility();
    }

    @Subscribe
    public void onConnectionChange(ConnectionCheckEvent event) {
        if (!event.isConnected || selectedSpeaker == null)
            return;

        loadSpeakerImage();
    }

    private void handleVisibility() {
        if (!mSessions.isEmpty()) {
            noSessionsView.setVisibility(View.GONE);
            sessionRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noSessionsView.setVisibility(View.VISIBLE);
            sessionRecyclerView.setVisibility(View.GONE);
        }
    }

    private void loadSpeakerImage() {
        if (TextUtils.isEmpty(selectedSpeaker.getPhoto())) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        final Context context = this;

        final Palette.PaletteAsyncListener paletteAsyncListener = new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch swatch = palette.getDarkVibrantSwatch();

                int backgroundColor = ContextCompat.getColor(context, R.color.color_primary);

                if(swatch != null) {
                    backgroundColor = swatch.getRgb();
                }

                collapsingToolbarLayout.setBackgroundColor(backgroundColor);
                collapsingToolbarLayout.setStatusBarScrimColor(getDarkColor(backgroundColor));
                collapsingToolbarLayout.setContentScrimColor(backgroundColor);

                sessionsListAdapter.setColor(backgroundColor);
            }
        };

        final Target imageTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                progressBar.setVisibility(View.GONE);

                speakerImage.setImageBitmap(bitmap);

                Palette.from(bitmap).generate(paletteAsyncListener);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                // No action to be done on preparation of loading
            }
        };
        speakerImage.setTag(imageTarget);

        Picasso.with(SpeakerDetailsActivity.this)
                .load(Uri.parse(selectedSpeaker.getPhoto()))
                .into(imageTarget);
    }

    private void loadSpeakerDetails() {

        speakerName.setText(selectedSpeaker.getName());
        speakerDesignation.setText(String.format("%s %s", selectedSpeaker.getPosition(), selectedSpeaker.getOrganisation()));

        boolean customTabsSupported;
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
                //do nothing
            }
        };
        customTabsSupported = bindService(customTabIntent, customTabsServiceConnection, Context.BIND_AUTO_CREATE);

        final SpeakerIntent speakerIntent;
        if (customTabsClient != null) {
            speakerIntent = new SpeakerIntent(selectedSpeaker, getApplicationContext(), this,
                    customTabsClient.newSession(new CustomTabsCallback()), customTabsSupported);
        } else {
            speakerIntent = new SpeakerIntent(selectedSpeaker, getApplicationContext(), this, customTabsSupported);
        }

        if (!TextUtils.isEmpty(selectedSpeaker.getLinkedin())) {
            speakerIntent.clickedImage(linkedin);
        } else {
            linkedin.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(selectedSpeaker.getTwitter())) {
            speakerIntent.clickedImage(twitter);
        } else {
            twitter.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(selectedSpeaker.getGithub())) {
            speakerIntent.clickedImage(github);
        } else {
            github.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(selectedSpeaker.getFacebook())) {
            speakerIntent.clickedImage(fb);
        } else {
            fb.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(selectedSpeaker.getWebsite())) {
            speakerIntent.clickedImage(website);
        } else {
            website.setVisibility(View.GONE);
        }

        biography.setText(Html.fromHtml(selectedSpeaker.getShortBiography()));
        biography.setMovementMethod(LinkMovementMethod.getInstance());

        OpenEventApp.getEventBus().register(this);
        loadSpeakerImage();
    }

    @Override
    protected void onResume() {
        super.onResume();

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        int spanCount = (int) (width / 250.00);

        gridLayoutManager.setSpanCount(spanCount);

        final DbSingleton dbSingleton = DbSingleton.getInstance();
        disposable.add(dbSingleton.getSessionbySpeakersNameObservable(speaker)
                .subscribe(new Consumer<ArrayList<Session>>() {
                    @Override
                    public void accept(@NonNull ArrayList<Session> sessions) throws Exception {
                        mSessions.clear();
                        mSessions.addAll(sessions);

                        sessionsListAdapter.notifyDataSetChanged();
                        handleVisibility();
                    }
                }));

        handleVisibility();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_speakers;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    private static int getDarkColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_speakers_url:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.subject));
                StringBuilder message = new StringBuilder();
                message.append(String.format("%s %s %s %s\n\n",
                        selectedSpeaker.getName(),
                        getResources().getString(R.string.message_1),
                        getResources().getString(R.string.app_name),
                        getResources().getString(R.string.message_2)));
                for (Session m : mSessions) {
                    message.append(m.getTitle())
                            .append(",");
                }
                message.append(String.format("\n\n%s (%s)\n%s",
                        getResources().getString(R.string.message_3),
                        Urls.APP_LINK,
                        selectedSpeaker.getPhoto()));
                sendIntent.putExtra(Intent.EXTRA_TEXT, message.toString());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, selectedSpeaker.getEmail()));
                return true;
            default:
                //do nothing
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_speakers_activity, menu);
        
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(customTabsServiceConnection);
        OpenEventApp.getEventBus().unregister(this);
        if(disposable != null && !disposable.isDisposed())
            disposable.dispose();
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
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {

        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            //Collapsed
            if (selectedSpeaker.getOrganisation().isEmpty()) {
                toolbarHeaderView.setVisibility(View.GONE);
                collapsingToolbarLayout.setTitle(selectedSpeaker.getName());
                isHideToolbarView = !isHideToolbarView;
            } else {
                toolbarHeaderView.setVisibility(View.VISIBLE);
                collapsingToolbarLayout.setTitle(" ");
                speakerDesignation.setMaxLines(1);
                speakerDesignation.setEllipsize(TextUtils.TruncateAt.END);
                isHideToolbarView = !isHideToolbarView;
            }
        } else if (percentage < 1f && !isHideToolbarView) {
            //Not Collapsed
            toolbarHeaderView.setVisibility(View.VISIBLE);
            collapsingToolbarLayout.setTitle(" ");
            speakerDesignation.setMaxLines(3);
            isHideToolbarView = !isHideToolbarView;
        }
    }

}
