package org.fossasia.openevent.core.feed.facebook;

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
import org.fossasia.openevent.core.feed.OpenCommentsDialogListener;
import org.fossasia.openevent.core.feed.facebook.api.CommentItem;
import org.fossasia.openevent.core.feed.facebook.api.FeedItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

import static org.fossasia.openevent.core.auth.AuthUtil.INVALID;
import static org.fossasia.openevent.core.auth.AuthUtil.VALID;

public class FacebookFeedFragment extends BaseFeedFragment implements OpenCommentsDialogListener, OnImageZoomListener {

    private FacebookFeedAdapter facebookFeedAdapter;
    private FacebookFeedFragmentViewModel facebookFeedFragmentViewModel;
    private List<FeedItem> feedItems;

    @BindView(R.id.feed_swipe_refresh)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.feed_recycler_view)
    protected RecyclerView feedRecyclerView;
    @BindView(R.id.txt_no_posts)
    protected TextView noFeedView;

    public static FacebookFeedFragment getInstance() {
        return new FacebookFeedFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = super.onCreateView(inflater, container, savedInstanceState);

        feedItems = new ArrayList<>();
        facebookFeedFragmentViewModel = ViewModelProviders.of(this).get(FacebookFeedFragmentViewModel.class);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        feedRecyclerView.setLayoutManager(mLayoutManager);
        facebookFeedAdapter = new FacebookFeedAdapter(getContext(), feedItems);
        facebookFeedAdapter.setOpenCommentsDialogListener(this);
        facebookFeedAdapter.setOnImageZoomListener(this);
        feedRecyclerView.setAdapter(facebookFeedAdapter);

        setupProgressBar();

        if (NetworkUtils.haveNetworkConnection(getContext()))
            showProgressBar(true);

        downloadFeed();

        swipeRefreshLayout.setOnRefreshListener(this::refresh);

        return view;
    }

    private void downloadFeed() {
        if (SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_ID, null) == null) {
            if (downloadProgressDialog.isShowing())
                showProgressBar(false);
            return;
        }

        facebookFeedFragmentViewModel.getPosts(getContext().getResources().getString(R.string.fields),
                getContext().getResources().getString(R.string.facebook_access_token), SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_ID, null))
                .observe(this, feedResponse -> {
                    if (feedResponse.getResponse() == VALID) {
                        feedItems.clear();
                        feedItems.addAll(feedResponse.getFeed().getData());
                        facebookFeedAdapter.notifyDataSetChanged();
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
                facebookFeedFragmentViewModel.updateFBPageID(getResources().getString(R.string.facebook_access_token), SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_ID, null))
                        .observe(FacebookFeedFragment.this, facebookPageId -> {
                            String id = facebookPageId.getId();
                            SharedPreferencesUtil.putString(ConstantStrings.FACEBOOK_PAGE_ID, id);
                        });
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

    @Override
    protected int getLayoutResource() {
        return R.layout.list_feed;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        facebookFeedAdapter.removeOnImageZoomListener();
        facebookFeedAdapter.removeOpenCommentsDialogListener();
    }

    @Override
    protected List getFeedItems() {
        return feedItems;
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return feedRecyclerView;
    }

    @Override
    protected View getNoFeedView() {
        return noFeedView;
    }

    @Override
    public void openCommentsDialog(List<CommentItem> commentItems) {
        CommentsDialogFragment newFragment = new CommentsDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ConstantStrings.FACEBOOK_COMMENTS, new ArrayList<>(commentItems));
        newFragment.setArguments(bundle);
        newFragment.show(getChildFragmentManager(), "Comments");
    }

    @Override
    public void onZoom(String imageUri) {
        ZoomableImageUtil.showZoomableImageDialogFragment(getChildFragmentManager(), imageUri);
    }
}
