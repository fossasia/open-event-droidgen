package org.fossasia.openevent.core.feed.twitter;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ConstantStrings;
import org.fossasia.openevent.common.network.NetworkUtils;
import org.fossasia.openevent.common.ui.Views;
import org.fossasia.openevent.common.ui.image.OnImageZoomListener;
import org.fossasia.openevent.common.ui.image.ZoomableImageUtil;
import org.fossasia.openevent.common.utils.SharedPreferencesUtil;
import org.fossasia.openevent.core.feed.BaseFeedFragment;
import org.fossasia.openevent.core.feed.twitter.api.TwitterFeedItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

import static org.fossasia.openevent.core.auth.AuthUtil.INVALID;
import static org.fossasia.openevent.core.auth.AuthUtil.VALID;

public class TwitterFeedFragment extends BaseFeedFragment implements OnImageZoomListener {

    private TwitterFeedAdapter twitterFeedAdapter;
    private List<TwitterFeedItem> twitterFeedItems;
    private TwitterFeedFragmentViewModel twitterFeedFragmentViewModel;

    @BindView(R.id.twitter_feed_swipe_refresh)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.twitter_feed_recycler_view)
    protected RecyclerView twitterFeedRecyclerView;
    @BindView(R.id.twitter_txt_no_posts)
    protected TextView noFeedView;

    public static TwitterFeedFragment getInstance() {
        return new TwitterFeedFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = super.onCreateView(inflater, container, savedInstanceState);

        twitterFeedItems = new ArrayList<>();
        twitterFeedFragmentViewModel = ViewModelProviders.of(this).get(TwitterFeedFragmentViewModel.class);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        twitterFeedRecyclerView.setLayoutManager(layoutManager);
        twitterFeedAdapter = new TwitterFeedAdapter(getContext(), twitterFeedItems);
        twitterFeedAdapter.setOnImageZoomListener(this);
        twitterFeedRecyclerView.setAdapter(twitterFeedAdapter);

        setupProgressBar();

        if (NetworkUtils.haveNetworkConnection(getContext()))
            showProgressBar(true);

        downloadFeed();

        swipeRefreshLayout.setOnRefreshListener(this::refresh);
        return view;
    }

    private void downloadFeed() {
        if (SharedPreferencesUtil.getString(ConstantStrings.TWITTER_PAGE_NAME, null) == null) {
            if (downloadProgressDialog.isShowing())
                showProgressBar(false);
            return;
        }

        twitterFeedFragmentViewModel.getPosts(SharedPreferencesUtil.getString(ConstantStrings.TWITTER_PAGE_NAME, null), 20, "twitter")
                .observe(this, feedResponse -> {
                    if (feedResponse.getResponse() == VALID) {
                        twitterFeedItems.clear();
                        twitterFeedItems.addAll(feedResponse.getTwitterFeed().getStatuses());
                        twitterFeedAdapter.notifyDataSetChanged();
                        handleVisibility();
                        Views.setSwipeRefreshLayout(swipeRefreshLayout, false);
                        Timber.d("Refresh done");
                        showProgressBar(false);
                    } else if (feedResponse.getResponse() == INVALID) {
                        Snackbar.make(swipeRefreshLayout, getActivity()
                                .getString(R.string.refresh_failed), Snackbar.LENGTH_LONG)
                                .setAction(R.string.retry_download, view -> refresh()).show();
                        Timber.d("Refresh not done");
                        showProgressBar(false);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void refresh() {
        NetworkUtils.checkConnection(new WeakReference<>(getContext()), new NetworkUtils.NetworkStateReceiverListener() {

            @Override
            public void networkAvailable() {
                // Network is available
                swipeRefreshLayout.setRefreshing(true);
                downloadFeed();
            }

            @Override
            public void networkUnavailable() {
                Views.setSwipeRefreshLayout(swipeRefreshLayout, false);

                Snackbar.make(swipeRefreshLayout, getActivity()
                        .getString(R.string.refresh_failed), Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry_download, view -> refresh()).show();
            }
        });
    }

    public void handleVisibility() {
        if (!twitterFeedItems.isEmpty()) {
            noFeedView.setVisibility(View.GONE);
            twitterFeedRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noFeedView.setVisibility(View.VISIBLE);
            twitterFeedRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.list_twitter_feed;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        twitterFeedAdapter.removeOnImageZoomListener();
    }

    @Override
    protected List getFeedItems() {
        return twitterFeedItems;
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return twitterFeedRecyclerView;
    }

    @Override
    protected View getNoFeedView() {
        return noFeedView;
    }

    @Override
    public void onZoom(String imageUri) {
        ZoomableImageUtil.showZoomableImageDialogFragment(getChildFragmentManager(), imageUri);
    }
}
