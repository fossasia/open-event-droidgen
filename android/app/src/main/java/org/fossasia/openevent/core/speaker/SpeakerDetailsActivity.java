package org.fossasia.openevent.core.speaker;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
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

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.api.Urls;
import org.fossasia.openevent.common.events.ConnectionCheckEvent;
import org.fossasia.openevent.common.ui.SnackbarUtil;
import org.fossasia.openevent.common.ui.Views;
import org.fossasia.openevent.common.ui.base.BaseActivity;
import org.fossasia.openevent.common.ui.image.ZoomableImageUtil;
import org.fossasia.openevent.common.utils.StringUtils;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.config.StrategyRegistry;
import org.fossasia.openevent.core.bookmark.BookmarkStatus;
import org.fossasia.openevent.core.bookmark.OnBookmarkSelectedListener;
import org.fossasia.openevent.core.track.session.SessionDetailActivity;
import org.fossasia.openevent.core.track.session.SessionsListAdapter;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SpeakerDetailsActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener, OnBookmarkSelectedListener, SessionsListAdapter.OnItemClickListener {

    private SessionsListAdapter sessionsListAdapter;

    private GridLayoutManager gridLayoutManager;

    private Speaker selectedSpeaker;

    private List<Session> sessions = new ArrayList<>();

    private boolean isHideToolbarView = false;

    private static final int spearkerWiseSessionList = 2;

    private SpeakerDetailsViewModel speakerDetailsViewModel;

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
    @BindView(R.id.tv_speaker_seminar_title) TextView speakerSessionsTitle;
    @BindView(R.id.tv_speaker_bio_title) TextView speakerBioTitle;
    @BindView(R.id.tv_speaker_social_media_title) TextView speakerSocialMediaTitle;
    @BindView(R.id.progress_bar)
    protected ProgressBar progressBar;

    @OnClick({R.id.imageView_linkedin, R.id.imageView_fb, R.id.imageView_github, R.id.imageView_twitter, R.id.imageView_web})
    public void openUrl(View v) {
        int id = v.getId();
        String url;
        switch (id) {
            case R.id.imageView_linkedin:
                url = selectedSpeaker.getLinkedin();
                break;
            case R.id.imageView_fb:
                url = selectedSpeaker.getFacebook();
                break;
            case R.id.imageView_github:
                url = selectedSpeaker.getGithub();
                break;
            case R.id.imageView_twitter:
                url = selectedSpeaker.getTwitter();
                break;
            case R.id.imageView_web:
                url = selectedSpeaker.getWebsite();
                break;
            default:
                return;
        }

        if (!TextUtils.isEmpty(url)) {
            Utils.setUpCustomTab(this, url);
        }
    }

    private String speakerName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        speakerName = getIntent().getStringExtra(Speaker.SPEAKER);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout.setTitle(" ");

        appBarLayout.addOnOffsetChangedListener(this);

        speakerDetailsViewModel = ViewModelProviders.of(this).get(SpeakerDetailsViewModel.class);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        int spanCount = (int) (width / 250.00);

        sessionRecyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, spanCount);
        sessionRecyclerView.setLayoutManager(gridLayoutManager);

        sessionsListAdapter = new SessionsListAdapter(this, sessions, spearkerWiseSessionList);
        sessionsListAdapter.setOnBookmarkSelectedListener(this);
        sessionsListAdapter.setHandleItemClickListener(this);
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
        if (!sessions.isEmpty()) {
            noSessionsView.setVisibility(View.GONE);
            speakerSessionsTitle.setVisibility(View.VISIBLE);
            sessionRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noSessionsView.setVisibility(View.VISIBLE);
            speakerSessionsTitle.setVisibility(View.GONE);
            sessionRecyclerView.setVisibility(View.GONE);
        }
    }

    private void loadSpeakerImage() {
        String photo = Utils.parseImageUri(selectedSpeaker.getPhotoUrl());

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
        sessions.clear();
        sessions.addAll(selectedSpeaker.getSessions());

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

        Views.setHtml(biography, selectedSpeaker.getShortBiography(), true);
        if (biography.getVisibility() == View.GONE) {
            speakerBioTitle.setVisibility(View.GONE);
        }

        loadSpeakerImage();
        hideEmptyURLButtons();

    }

    private void hideEmptyURLButtons() {

        boolean showSocialMediaTitle = false;

        if (!TextUtils.isEmpty(selectedSpeaker.getLinkedin())) {
            linkedin.setVisibility(View.VISIBLE);
            showSocialMediaTitle = true;
        }
        if (!TextUtils.isEmpty(selectedSpeaker.getTwitter())) {
            twitter.setVisibility(View.VISIBLE);
            showSocialMediaTitle = true;
        }
        if (!TextUtils.isEmpty(selectedSpeaker.getGithub())) {
            github.setVisibility(View.VISIBLE);
            showSocialMediaTitle = true;
        }
        if (!TextUtils.isEmpty(selectedSpeaker.getFacebook())) {
            fb.setVisibility(View.VISIBLE);
            showSocialMediaTitle = true;
        }
        if (!TextUtils.isEmpty(selectedSpeaker.getWebsite())) {
            website.setVisibility(View.VISIBLE);
            showSocialMediaTitle = true;
        }

        //show the title
        if (showSocialMediaTitle) {
            speakerSocialMediaTitle.setVisibility(View.VISIBLE);
        }

    }
    @Override
    protected void onResume() {
        super.onResume();

        StrategyRegistry.getInstance().getEventBusStrategy().getEventBus().register(this);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        int spanCount = (int) (width / 250.00);

        gridLayoutManager.setSpanCount(spanCount);

        speakerDetailsViewModel.getSpeaker(speakerName).observe(this, speakerData -> {
            selectedSpeaker = speakerData;
            loadSpeakerDetails();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        StrategyRegistry.getInstance().getEventBusStrategy().getEventBus().unregister(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_speakers;
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
                        StringUtils.buildSession(sessions) +
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sessionsListAdapter.clearOnBookmarkSelectedListener();
        sessionsListAdapter.clearHandleItemClickListener();
    }

    @OnClick(R.id.speaker_image)
    public void onZoom() {
        String imageUri = Utils.parseImageUri(selectedSpeaker.getPhotoUrl());
        ZoomableImageUtil.showZoomableImageDialogFragment(getSupportFragmentManager(), imageUri);
    }

    @Override
    public void showSnackbar(BookmarkStatus bookmarkStatus) {
        Snackbar snackbar = Snackbar.make(sessionRecyclerView, SnackbarUtil.getMessageResource(bookmarkStatus), Snackbar.LENGTH_LONG);
        SnackbarUtil.setSnackbarAction(this, snackbar, bookmarkStatus)
                .show();
    }

    @Override
    public void itemOnClick(Session session, int layoutPosition) {
        Intent intent = new Intent(this, SessionDetailActivity.class);
        startActivity(Views.openSessionDetails(session, intent));
    }

}
