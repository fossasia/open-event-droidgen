package org.fossasia.openevent.core.feed.facebook;

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
import org.fossasia.openevent.core.feed.OpenCommentsDialogListener;
import org.fossasia.openevent.core.feed.Resource;
import org.fossasia.openevent.core.feed.facebook.api.CommentItem;
import org.fossasia.openevent.core.feed.facebook.api.Feed;
import org.fossasia.openevent.core.feed.facebook.api.FeedItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

public class FacebookFeedFragment extends BaseFeedFragment implements OpenCommentsDialogListener, OnImageZoomListener {

    private FacebookFeedAdapter facebookFeedAdapter;
    private FacebookFeedFragmentViewModel facebookFeedFragmentViewModel;
    private List<FeedItem> feedItems;

    @BindView(R.id.feed_swipe_refresh)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.feed_recycler_view)
    protected RecyclerView feedRecyclerView;
    @BindView(R.id.ll_no_posts)
    protected LinearLayout noFeedView;

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

        swipeRefreshLayout.setOnRefreshListener(this::refresh);
        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(true);
            refresh();
        });

        return view;
    }

    private void downloadFeed() {
        facebookFeedFragmentViewModel.getPosts(getContext().getResources().getString(R.string.fields),
                getContext().getResources().getString(R.string.facebook_access_token), SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_ID, null))
                .observe(this, feedResponseResource -> {
                    if (feedResponseResource == null) return;
                    if (feedResponseResource.getStatus() == Resource.Status.SUCCESS) {
                        Feed feed = feedResponseResource.getData();
                        feedItems.clear();
                        if (feed != null && feed.getData() != null)
                            feedItems.addAll(feed.getData());
                        facebookFeedAdapter.notifyDataSetChanged();
                        handleVisibility();
                        Timber.d("Refresh done");
                    } else if (feedResponseResource.getStatus() == Resource.Status.ERROR) {
                        Timber.d(feedResponseResource.getMessage());
                        showRetrySnackbar(R.string.refresh_failed);
                    }
                    swipeRefreshLayout.postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 1000);
                });
    }

    private void refresh() {
        if (NetworkUtils.haveNetworkConnection(getContext())) {
            if (SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_ID, null) == null) {
                facebookFeedFragmentViewModel.getFBPageID(getResources().getString(R.string.facebook_access_token))
                        .observe(FacebookFeedFragment.this, facebookPageIdResource -> {
                            if (facebookPageIdResource == null) return;
                            if (facebookPageIdResource.getStatus() == Resource.Status.SUCCESS) {
                                SharedPreferencesUtil.putString(ConstantStrings.FACEBOOK_PAGE_ID, facebookPageIdResource.getData().getId());
                                downloadFeed();
                            } else {
                                Timber.e(facebookPageIdResource.getMessage());
                                showRetrySnackbar(R.string.refresh_failed);
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
            } else {
                downloadFeed();
            }
        } else {
            swipeRefreshLayout.setRefreshing(false);
            showRetrySnackbar(R.string.no_internet_connection);
        }
    }

    private void showRetrySnackbar(@StringRes int resId) {
        Snackbar.make(swipeRefreshLayout, getActivity()
                .getString(resId), Snackbar.LENGTH_LONG)
                .setAction(R.string.retry_download, view -> refresh()).show();
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
