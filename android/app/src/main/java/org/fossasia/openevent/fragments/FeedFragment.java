package org.fossasia.openevent.fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.NetworkUtils;
import org.fossasia.openevent.utils.ShowNotificationSnackBar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by rohanagarwal94 on 10/6/17.
 */
public class FeedFragment extends BaseFragment {

    private FeedAdapter feedAdapter;
    private List<FeedItem> feedItems;
    private SharedPreferences sharedPreferences;
    private ProgressDialog downloadProgressDialog;

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
        feedAdapter = new FeedAdapter(getContext(), (FeedAdapter.AdapterCallback)getActivity(), feedItems);
        feedRecyclerView.setAdapter(feedAdapter);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        setupProgressBar();

        if(NetworkUtils.haveNetworkConnection(getContext()))
            showProgressBar(true);

        downloadFeed();

        swipeRefreshLayout.setOnRefreshListener(this::refresh);

        return view;
    }

    private void downloadFeed() {
        APIClient.getFacebookGraphAPI()
                .getPosts(sharedPreferences.getString(ConstantStrings.FACEBOOK_PAGE_ID, null),
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
                    swipeRefreshLayout.setRefreshing(false);
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
            public void activeConnection() {
                //Internet is working
                if(sharedPreferences.getString(ConstantStrings.FACEBOOK_PAGE_ID, null) == null)
                    APIClient.getFacebookGraphAPI().getPageId(sharedPreferences.getString(ConstantStrings.FACEBOOK_PAGE_NAME, null),
                            getResources().getString(R.string.facebook_access_token))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(facebookPageId -> {
                                String id = facebookPageId.getId();
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                                sharedPreferences.edit().putString(ConstantStrings.FACEBOOK_PAGE_ID, id).apply();
                            });

                downloadFeed();
            }

            @Override
            public void inactiveConnection() {
                //set is refreshing false as let user to login
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                //Device is connected to WI-FI or Mobile Data but Internet is not working
                ShowNotificationSnackBar showNotificationSnackBar = new ShowNotificationSnackBar(getContext(),getView(),swipeRefreshLayout) {
                    @Override
                    public void refreshClicked() {
                        refresh();
                    }
                };
                //show snackbar will be useful if user have blocked notification for this app
                showNotificationSnackBar.showSnackBar();
                //show notification (Only when connected to WiFi)
                showNotificationSnackBar.buildNotification();
            }

            @Override
            public void networkAvailable() {
                // Network is available but we need to wait for activity
            }

            @Override
            public void networkUnavailable() {
                Snackbar.make(swipeRefreshLayout, getActivity()
                        .getString(R.string.refresh_failed), Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry_download, view -> refresh()).show();
                swipeRefreshLayout.setRefreshing(false);
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
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.list_feed;
    }
}
