package org.fossasia.openevent.fragments;

import android.app.ProgressDialog;
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
import org.fossasia.openevent.api.APIClient;
import org.fossasia.openevent.data.facebook.FeedItem;
import org.fossasia.openevent.modules.OnImageZoomListener;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.NetworkUtils;
import org.fossasia.openevent.utils.SharedPreferencesUtil;
import org.fossasia.openevent.utils.Views;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by rohanagarwal94 on 10/6/17.
 */
public class FeedFragment extends BaseFragment {

    private FeedAdapter feedAdapter;
    private List<FeedItem> feedItems;
    private ProgressDialog downloadProgressDialog;
    private Disposable feedLoaderDisposable;

    @BindView(R.id.feed_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.feed_recycler_view) RecyclerView feedRecyclerView;
    @BindView(R.id.txt_no_posts) TextView noFeedView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = super.onCreateView(inflater, container, savedInstanceState);

        feedItems = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        feedRecyclerView.setLayoutManager(mLayoutManager);
        feedAdapter = new FeedAdapter(getContext(), (FeedAdapter.OpenCommentsDialogListener)getActivity(), feedItems);
        feedAdapter.setOnImageZoomListener((OnImageZoomListener)getActivity());
        feedRecyclerView.setAdapter(feedAdapter);

        setupProgressBar();

        if(NetworkUtils.haveNetworkConnection(getContext()))
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

        feedLoaderDisposable = APIClient.getFacebookGraphAPI()
                .getPosts(SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_ID, null),
                        getContext().getResources().getString(R.string.fields),
                        getContext().getResources().getString(R.string.facebook_access_token))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(feed -> {
                    feedItems.clear();
                    feedItems.addAll(feed.getData());
                    feedAdapter.notifyDataSetChanged();
                    handleVisibility();
                }, throwable -> {
                    Snackbar.make(swipeRefreshLayout, getActivity()
                            .getString(R.string.refresh_failed), Snackbar.LENGTH_LONG)
                            .setAction(R.string.retry_download, view -> refresh()).show();
                    Timber.d("Refresh not done");
                    showProgressBar(false);
                }, () -> {
                    Views.setSwipeRefreshLayout(swipeRefreshLayout, false);
                    Timber.d("Refresh done");
                    showProgressBar(false);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(swipeRefreshLayout != null) swipeRefreshLayout.setOnRefreshListener(null);
    }

    private void refresh() {
        NetworkUtils.checkConnection(new WeakReference<>(getContext()), new NetworkUtils.NetworkStateReceiverListener() {

            @Override
            public void networkAvailable() {
                // Network is available
                if (SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_ID, null) == null)
                    APIClient.getFacebookGraphAPI().getPageId(SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_NAME, null),
                            getResources().getString(R.string.facebook_access_token))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(facebookPageId -> {
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
        if(show)
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
            disposeRxSubscriptions();
            getActivity().onBackPressed();
        });
    }

    private void disposeRxSubscriptions() {
        if (feedLoaderDisposable != null && !feedLoaderDisposable.isDisposed()) {
            feedLoaderDisposable.dispose();
        }
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
