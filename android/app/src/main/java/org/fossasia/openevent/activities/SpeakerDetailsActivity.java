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
import android.text.TextUtils;
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
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.ConnectionCheckEvent;
import org.fossasia.openevent.utils.SpeakerIntent;
import org.fossasia.openevent.utils.StringUtils;
import org.fossasia.openevent.utils.Utils;
import org.fossasia.openevent.utils.Views;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.realm.RealmChangeListener;

/**
 * Created by MananWason on 30-06-2015.
 */
public class SpeakerDetailsActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {

    private SessionsListAdapter sessionsListAdapter;

    private GridLayoutManager gridLayoutManager;

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
    @BindView(R.id.speaker_details_title) TextView speakerNameText;
    @BindView(R.id.speaker_image) ImageView speakerImage;
    @BindView(R.id.speaker_bio) TextView biography;
    @BindView(R.id.speaker_details_header) LinearLayout toolbarHeaderView;
    @BindView(R.id.recyclerView_speakers) RecyclerView sessionRecyclerView;
    @BindView(R.id.speaker_details_designation) TextView speakerDesignation;
    @BindView(R.id.progress_bar)
    protected ProgressBar progressBar;

    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();
    private Speaker speaker;
    private String speakerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        speakerName = getIntent().getStringExtra(Speaker.SPEAKER);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout.setTitle(" ");

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
        String photo = Utils.parseImageUri(selectedSpeaker.getPhoto());

        if (photo == null) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        final Context context = this;

        final Palette.PaletteAsyncListener paletteAsyncListener = palette -> {
            Palette.Swatch swatch = palette.getDarkVibrantSwatch();

            int backgroundColor = ContextCompat.getColor(context, R.color.color_primary);

            if(swatch != null) {
                backgroundColor = swatch.getRgb();
            }

            collapsingToolbarLayout.setBackgroundColor(backgroundColor);
            collapsingToolbarLayout.setStatusBarScrimColor(getDarkColor(backgroundColor));
            collapsingToolbarLayout.setContentScrimColor(backgroundColor);

            sessionsListAdapter.setColor(backgroundColor);
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
                .load(Uri.parse(photo))
                .into(imageTarget);
    }

    private void loadSpeakerDetails() {
        mSessions.clear();
        mSessions.addAll(selectedSpeaker.getSessions());

        sessionsListAdapter.notifyDataSetChanged();
        handleVisibility();

        speakerNameText.setText(selectedSpeaker.getName());
        if (!TextUtils.isEmpty(selectedSpeaker.getPosition()) && !TextUtils.isEmpty(selectedSpeaker.getOrganisation()))
            speakerDesignation.setText(String.format("%s, %s", selectedSpeaker.getPosition(), selectedSpeaker.getOrganisation()));
        else if (!TextUtils.isEmpty(selectedSpeaker.getPosition()))
            speakerDesignation.setText(selectedSpeaker.getPosition());
        else if (!TextUtils.isEmpty(selectedSpeaker.getOrganisation()))
            speakerDesignation.setText(selectedSpeaker.getOrganisation());
        else
            speakerDesignation.setVisibility(View.GONE);

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

        Views.setHtml(biography, selectedSpeaker.getShortBiography(), true);

        loadSpeakerImage();
    }

    @Override
    protected void onResume() {
        super.onResume();

        OpenEventApp.getEventBus().register(this);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        int spanCount = (int) (width / 250.00);

        gridLayoutManager.setSpanCount(spanCount);

        speaker = realmRepo.getSpeaker(speakerName);
        speaker.addChangeListener((RealmChangeListener<Speaker>) speaker -> {
            selectedSpeaker = speaker;
            loadSpeakerDetails();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(speaker != null)
            speaker.removeAllChangeListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        OpenEventApp.getEventBus().unregister(this);
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
                String message = String.format("%s %s %s %s\n\n",
                        selectedSpeaker.getName(),
                        getResources().getString(R.string.message_1),
                        getResources().getString(R.string.app_name),
                        getResources().getString(R.string.message_2)) +
                        StringUtils.join(mSessions, ", ") +
                        String.format("\n\n%s (%s)\n",
                                getResources().getString(R.string.message_3),
                                Urls.getAppLink()
                                  );

                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getString(R.string.share)));
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
        if(customTabsServiceConnection != null)
            unbindService(customTabsServiceConnection);
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
            if (TextUtils.isEmpty(selectedSpeaker.getOrganisation())) {
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
