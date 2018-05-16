package org.fossasia.openevent.core.feed.twitter;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ConstantStrings;
import org.fossasia.openevent.common.network.NetworkUtils;
import org.fossasia.openevent.common.ui.image.OnImageZoomListener;
import org.fossasia.openevent.common.ui.image.ZoomableImageUtil;
import org.fossasia.openevent.common.utils.SharedPreferencesUtil;
import org.fossasia.openevent.core.feed.BaseFeedFragment;
import org.fossasia.openevent.core.feed.Resource;
import org.fossasia.openevent.core.feed.twitter.api.TwitterFeed;
import org.fossasia.openevent.core.feed.twitter.api.TwitterFeedItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

public class TwitterFeedFragment extends BaseFeedFragment implements OnImageZoomListener {

    private TwitterFeedAdapter twitterFeedAdapter;
    private List<TwitterFeedItem> twitterFeedItems;
    private TwitterFeedFragmentViewModel twitterFeedFragmentViewModel;

    @BindView(R.id.twitter_feed_swipe_refresh)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.twitter_feed_recycler_view)
    protected RecyclerView twitterFeedRecyclerView;
    @BindView(R.id.ll_no_posts)
    protected LinearLayout noFeedView;

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

        swipeRefreshLayout.setOnRefreshListener(this::refresh);
        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(true);
            refresh();
        });

        return view;
    }

    private void downloadFeed() {
        if (SharedPreferencesUtil.getString(ConstantStrings.TWITTER_PAGE_NAME, null) == null) {
            swipeRefreshLayout.setRefreshing(false);
            Timber.d("Twitter page name is null");
        }

        twitterFeedFragmentViewModel.getPosts(SharedPreferencesUtil.getString(ConstantStrings.TWITTER_PAGE_NAME, null), 20, "twitter")
                .observe(this, feedResource -> {
                    if (feedResource == null) return;
                    if (feedResource.getStatus() == Resource.Status.SUCCESS) {
                        TwitterFeed feed = feedResource.getData();
                        twitterFeedItems.clear();
                        if (feed != null && feed.getStatuses() != null)
                            twitterFeedItems.addAll(feed.getStatuses());
                        twitterFeedAdapter.notifyDataSetChanged();
                        handleVisibility();
                        Timber.d("Refresh done");
                    } else if (feedResource.getStatus() == Resource.Status.ERROR) {
                        showRetrySnackbar(R.string.refresh_failed);
                        Timber.d(feedResource.getMessage());
                    }
                    swipeRefreshLayout.postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 1000);
                });
    }

    private void refresh() {
        NetworkUtils.checkConnection(new WeakReference<>(getContext()), new NetworkUtils.NetworkStateReceiverListener() {

            @Override
            public void networkAvailable() {
                downloadFeed();
            }

            @Override
            public void networkUnavailable() {
                swipeRefreshLayout.setRefreshing(false);
                showRetrySnackbar(R.string.no_internet_connection);
            }
        });
    }

    private void showRetrySnackbar(@StringRes int resId) {
        Snackbar.make(swipeRefreshLayout, getActivity()
                .getString(resId), Snackbar.LENGTH_LONG)
                .setAction(R.string.retry_download, view -> refresh()).show();
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
