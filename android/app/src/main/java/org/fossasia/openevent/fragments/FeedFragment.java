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
import org.fossasia.openevent.adapters.FeedAdapter;
import org.fossasia.openevent.data.facebook.FeedItem;
import org.fossasia.openevent.modules.OnImageZoomListener;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.NetworkUtils;
import org.fossasia.openevent.utils.SharedPreferencesUtil;
import org.fossasia.openevent.utils.Views;
import org.fossasia.openevent.viewmodels.FeedFragmentViewModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

import static org.fossasia.openevent.utils.AuthUtil.INVALID;
import static org.fossasia.openevent.utils.AuthUtil.VALID;

/**
 * Created by rohanagarwal94 on 10/6/17.
 */
public class FeedFragment extends BaseFragment {

    private FeedAdapter feedAdapter;
    private ProgressDialog downloadProgressDialog;
    private FeedFragmentViewModel feedFragmentViewModel;
    private List<FeedItem> feedItems;
    private FeedAdapter.OpenCommentsDialogListener openCommentsDialogListener;
    private OnImageZoomListener onImageZoomListener;

    @BindView(R.id.feed_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.feed_recycler_view) RecyclerView feedRecyclerView;
    @BindView(R.id.txt_no_posts) TextView noFeedView;

    public static FeedFragment getInstance(FeedAdapter.OpenCommentsDialogListener openCommentsDialogListener,
                                           OnImageZoomListener onImageZoomListener) {
        FeedFragment feedFragment = new FeedFragment();
        feedFragment.openCommentsDialogListener = openCommentsDialogListener;
        feedFragment.onImageZoomListener = onImageZoomListener;

        return feedFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = super.onCreateView(inflater, container, savedInstanceState);

        feedItems = new ArrayList<>();
        feedFragmentViewModel = ViewModelProviders.of(this).get(FeedFragmentViewModel.class);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        feedRecyclerView.setLayoutManager(mLayoutManager);
        feedAdapter = new FeedAdapter(getContext(), openCommentsDialogListener, feedItems);
        feedAdapter.setOnImageZoomListener(onImageZoomListener);
        feedRecyclerView.setAdapter(feedAdapter);

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

        feedFragmentViewModel.getPosts(getContext().getResources().getString(R.string.fields),
                getContext().getResources().getString(R.string.facebook_access_token), SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_ID, null))
                .observe(this, feedResponse -> {
                    if (feedResponse.getResponse() == VALID) {
                        feedItems.clear();
                        feedItems.addAll(feedResponse.getFeed().getData());
                        feedAdapter.notifyDataSetChanged();
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

    public void handleVisibility() {
        if (!feedItems.isEmpty()) {
            noFeedView.setVisibility(View.GONE);
            feedRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noFeedView.setVisibility(View.VISIBLE);
            feedRecyclerView.setVisibility(View.GONE);
        }
    }

    private void refresh() {
        NetworkUtils.checkConnection(new WeakReference<>(getContext()), new NetworkUtils.NetworkStateReceiverListener() {

            @Override
            public void networkAvailable() {
                // Network is available
                swipeRefreshLayout.setRefreshing(true);
                feedFragmentViewModel.updateFBPageID(getResources().getString(R.string.facebook_access_token), SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_ID, null))
                        .observe(FeedFragment.this, facebookPageId -> {
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
        return R.layout.list_feed;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (feedAdapter != null) {
            feedAdapter.removeOnImageZoomListener();
            feedAdapter.removeOpenCommentsDialogListener();
        }
    }
}
