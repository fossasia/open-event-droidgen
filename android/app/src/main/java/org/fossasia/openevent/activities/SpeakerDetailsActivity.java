package org.fossasia.openevent.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.SessionsListAdapter;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.SpeakerIntent;
import org.fossasia.openevent.utils.Views;

import java.util.List;

import butterknife.BindView;

/**
 * Created by MananWason on 30-06-2015.
 */
public class SpeakerDetailsActivity extends BaseActivity {

    private SessionsListAdapter sessionsListAdapter;

    private Speaker selectedSpeaker;

    private List<Session> mSessions;

    private String speaker;

    private CustomTabsClient customTabsClient;

    private CustomTabsServiceConnection customTabsServiceConnection;

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
    @BindView(R.id.speaker_bio) TextView biography;
    @BindView(R.id.speaker_details_header) ViewGroup header;
    @BindView(R.id.recyclerView_speakers) RecyclerView sessionRecyclerView;
    @BindView(R.id.session_details_designation) TextView speakerDesignation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final DbSingleton dbSingleton = DbSingleton.getInstance();
        speaker = getIntent().getStringExtra(Speaker.SPEAKER);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selectedSpeaker = dbSingleton.getSpeakerbySpeakersname(speaker);

        header.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = header.getHeight();
                if (height != 0) {
                    Views.removeOnGlobalLayoutListener(header.getViewTreeObserver(), this);
                    int toolbarHeight = height + Views.getActionBarSize(SpeakerDetailsActivity.this);
                    toolbar.getLayoutParams().height = toolbarHeight;
                    toolbar.requestLayout();
                    collapsingToolbarLayout.getLayoutParams().height = Math.round(2.25f * (toolbarHeight));
                    collapsingToolbarLayout.requestLayout();

                    if (!TextUtils.isEmpty(selectedSpeaker.getPhoto())) {
                        Picasso.with(SpeakerDetailsActivity.this)
                                .load(Uri.parse(selectedSpeaker.getPhoto()))
                                .into((ImageView) findViewById(R.id.speaker_image));
                    }
                }
            }
        });

        speakerName.setText(selectedSpeaker.getName());
        speakerDesignation.setText(String.format("%s%s", selectedSpeaker.getPosition(), selectedSpeaker.getOrganisation()));
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
        if (customTabsClient != null)
        {
            speakerIntent = new SpeakerIntent(selectedSpeaker, getApplicationContext(), this,
                    customTabsClient.newSession(new CustomTabsCallback()), customTabsSupported);
        }
        else
        {
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

        biography.setText(selectedSpeaker.getBio());

        mSessions = dbSingleton.getSessionbySpeakersName(speaker);
        sessionsListAdapter = new SessionsListAdapter(this, mSessions);
        sessionRecyclerView.setNestedScrollingEnabled(false);
        sessionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sessionRecyclerView.setAdapter(sessionsListAdapter);
        sessionRecyclerView.setItemAnimator(new DefaultItemAnimator());
        if (!mSessions.isEmpty()) {
            noSessionsView.setVisibility(View.GONE);
            sessionRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noSessionsView.setVisibility(View.VISIBLE);
            sessionRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_speakers;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
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

        Target imageTarget = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        int shareColor;

                        int pixel = bitmap.getPixel(((int) Math.round(bitmap.getWidth() * 0.9)),
                                ((int) Math.round(bitmap.getHeight() * 0.1)));
                        if (Color.red(pixel) + Color.blue(pixel) + Color.green(pixel) > 128 * 3) {
                            shareColor = Color.BLACK;
                        } else {
                            shareColor = Color.WHITE;
                        }

                        Drawable shareDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_share_white_24dp);
                        shareDrawable.setColorFilter(shareColor, PorterDuff.Mode.MULTIPLY);

                        menu.getItem(0).setIcon(shareDrawable);

                        Drawable backDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_arrow_back_white_24dp);
                        backDrawable.setColorFilter(shareColor, PorterDuff.Mode.MULTIPLY);

                        getSupportActionBar().setHomeAsUpIndicator(backDrawable);
                    }
                });
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Drawable shareDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_share_white_24dp);
                shareDrawable.clearColorFilter();

                Drawable backDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_arrow_back_white_24dp);
                backDrawable.clearColorFilter();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                //This method is intentionally empty, because it is required to use Target, which is abstract
            }
        };

        Picasso.with(SpeakerDetailsActivity.this)
                .load(Uri.parse(selectedSpeaker.getPhoto()))
                .into(imageTarget);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        Drawable shareDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_share_white_24dp);
        shareDrawable.clearColorFilter();

        Drawable backDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_arrow_back_white_24dp);
        backDrawable.clearColorFilter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(customTabsServiceConnection);
    }
}
