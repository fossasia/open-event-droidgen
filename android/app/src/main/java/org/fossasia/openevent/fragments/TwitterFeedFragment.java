package org.fossasia.openevent.fragments;


import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.TwitterFeedAdapter;
import org.fossasia.openevent.data.twitter.TwitterFeedItem;
import org.fossasia.openevent.modules.OnImageZoomListener;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.NetworkUtils;
import org.fossasia.openevent.utils.SharedPreferencesUtil;
import org.fossasia.openevent.utils.Views;
import org.fossasia.openevent.viewmodels.TwitterFeedFragmentViewModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

import static org.fossasia.openevent.utils.AuthUtil.INVALID;
import static org.fossasia.openevent.utils.AuthUtil.VALID;

public class TwitterFeedFragment extends BaseFragment {

    private TwitterFeedAdapter twitterFeedAdapter;
    private OnImageZoomListener onImageZoomListener;
    private List<TwitterFeedItem> twitterFeedItems;
    private TwitterFeedFragmentViewModel twitterFeedFragmentViewModel;
    private ProgressDialog downloadProgressDialog;

    @BindView(R.id.twitter_feed_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.twitter_feed_recycler_view)
    RecyclerView twitterFeedRecyclerView;
    @BindView(R.id.twitter_txt_no_posts)
    TextView noFeedView;

    public static TwitterFeedFragment getInstance(OnImageZoomListener onImageZoomListener) {
       TwitterFeedFragment twitterFeedFragment = new TwitterFeedFragment();
       twitterFeedFragment.onImageZoomListener = onImageZoomListener;
       return twitterFeedFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = super.onCreateView(inflater, container, savedInstanceState);

        twitterFeedItems = new ArrayList<>();
        twitterFeedFragmentViewModel = ViewModelProviders.of(this).get(TwitterFeedFragmentViewModel.class);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        twitterFeedRecyclerView.setLayoutManager(layoutManager);
        twitterFeedAdapter = new TwitterFeedAdapter(getContext(), twitterFeedItems);
        twitterFeedAdapter.setOnImageZoomListener(onImageZoomListener);
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

    private void showProgressBar(boolean show) {
        if (show)
            downloadProgressDialog.show();
        else
            downloadProgressDialog.dismiss();
    }

    private void setupProgressBar() {
        downloadProgressDialog = new ProgressDialog(getContext());
        downloadProgressDialog.setIndeterminate(true);
        downloadProgressDialog.setProgressPercentFormat(null);
        downloadProgressDialog.setProgressNumberFormat(null);
        downloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downloadProgressDialog.setCancelable(false);
        String shownMessage = String.format(getString(R.string.downloading_format), getString(R.string.menu_feed));
        downloadProgressDialog.setMessage(shownMessage);
        downloadProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialogInterface, i) -> {
            downloadProgressDialog.dismiss();
            getActivity().onBackPressed();
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.list_twitter_feed;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (twitterFeedAdapter != null) {
            twitterFeedAdapter.removeOnImageZoomListener();
        }
    }

}
