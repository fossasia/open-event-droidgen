package org.fossasia.openevent.fragments;

import android.app.ProgressDialog;
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
import org.fossasia.openevent.data.feed.FacebookFeed;
import org.fossasia.openevent.data.feed.FeedItem;
import org.fossasia.openevent.data.feed.LoklakFeed;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.DateConverter;
import org.fossasia.openevent.utils.NetworkUtils;
import org.fossasia.openevent.utils.SharedPreferencesUtil;
import org.fossasia.openevent.utils.ShowNotificationSnackBar;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by rohanagarwal94 on 10/6/17.
 */
public class FeedFragment extends BaseFragment {

    private FeedAdapter feedAdapter;
    private List<FeedItem> feedItems;
    private ProgressDialog downloadProgressDialog;

    @BindView(R.id.feed_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.feed_recycler_view)
    RecyclerView feedRecyclerView;
    @BindView(R.id.txt_no_posts)
    TextView noFeedView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = super.onCreateView(inflater, container, savedInstanceState);

        feedItems = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        feedRecyclerView.setLayoutManager(mLayoutManager);
        feedAdapter = new FeedAdapter(getContext(), (FeedAdapter.AdapterCallback) getActivity(), feedItems);
        feedRecyclerView.setAdapter(feedAdapter);

        setupProgressBar();

        if (NetworkUtils.haveNetworkConnection(getContext()))
            showProgressBar(true);

        downloadFeed();

        swipeRefreshLayout.setOnRefreshListener(this::refresh);

        return view;
    }

    private void downloadFeed() {
        if (SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_ID, null) == null
                && SharedPreferencesUtil.getString(ConstantStrings.TWITTER_PAGE_NAME, null) == null) {
            if (downloadProgressDialog.isShowing())
                showProgressBar(false);
            return;
        }

        Observable<FacebookFeed> facebookFeedObservable = null;
        Observable<LoklakFeed> loklakFeedObservable = null;

        if (SharedPreferencesUtil.getString(ConstantStrings.TWITTER_PAGE_NAME, null) != null) {
            loklakFeedObservable = APIClient.getLoklakAPI()
                    .getTwitterFeed(SharedPreferencesUtil.getString(ConstantStrings.TWITTER_PAGE_NAME, null), 20, "twitter")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

        if (SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_ID, null) != null) {
            facebookFeedObservable = APIClient.getFacebookGraphAPI()
                    .getPosts(SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_ID, null),
                            getContext().getResources().getString(R.string.fields),
                            getContext().getResources().getString(R.string.facebook_access_token))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

        if (facebookFeedObservable != null && loklakFeedObservable != null) {
            Observable.zip(loklakFeedObservable, facebookFeedObservable, (loklakFeed, facebookFeed) -> {
                feedItems.clear();
                feedItems.addAll(loklakFeed.getStatuses());
                feedItems.addAll(facebookFeed.getData());
                Collections.sort(feedItems, new CustomDateComparator());
                swipeRefreshLayout.setRefreshing(false);
                Timber.d("Refresh done");
                showProgressBar(false);
                return feedItems;
            })
                    .subscribe(feed -> {
                        feedAdapter.notifyDataSetChanged();
                        handleVisibility();
                    }, throwable -> {
                        Snackbar.make(swipeRefreshLayout, getActivity()
                                .getString(R.string.refresh_failed), Snackbar.LENGTH_LONG)
                                .setAction(R.string.retry_download, view -> refresh()).show();
                        Timber.d("Refresh not done");
                        showProgressBar(false);
                        feedAdapter.notifyDataSetChanged();
                        handleVisibility();
                    });
        } else if (facebookFeedObservable != null && loklakFeedObservable == null) {
            downloadFacebookFeed(facebookFeedObservable);
        } else if (facebookFeedObservable == null && loklakFeedObservable != null) {
            downloadLoklakFeed(loklakFeedObservable);
        }
    }

    private void downloadFacebookFeed(Observable<FacebookFeed> facebookFeedObservable) {
        if (SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_ID, null) == null) {
            if (downloadProgressDialog.isShowing())
                showProgressBar(false);
            return;
        }

        facebookFeedObservable.subscribe(feed -> {
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

    private void downloadLoklakFeed(Observable<LoklakFeed> loklakFeedObservable) {
        if (SharedPreferencesUtil.getString(ConstantStrings.TWITTER_PAGE_NAME, null) == null) {
            if (downloadProgressDialog.isShowing())
                showProgressBar(false);
            return;
        }

        loklakFeedObservable
                .subscribe(feed -> {
                    feedItems.clear();
                    feedItems.addAll(feed.getStatuses());
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

        if (swipeRefreshLayout != null) swipeRefreshLayout.setOnRefreshListener(null);
    }

    private void refresh() {
        NetworkUtils.checkConnection(new WeakReference<>(getContext()), new NetworkUtils.NetworkStateReceiverListener() {
            @Override
            public void activeConnection() {
                //Internet is working
                if (SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_ID, null) == null
                        && SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_NAME, null) != null) {
                    APIClient.getFacebookGraphAPI().getPageId(SharedPreferencesUtil.getString(ConstantStrings.FACEBOOK_PAGE_NAME, null),
                            getResources().getString(R.string.facebook_access_token))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(facebookPageId -> {
                                String id = facebookPageId.getId();
                                SharedPreferencesUtil.putString(ConstantStrings.FACEBOOK_PAGE_ID, id);
                            });
                }

                downloadFeed();
            }

            @Override
            public void inactiveConnection() {
                //set is refreshing false as let user to login
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                //Device is connected to WI-FI or Mobile Data but Internet is not working
                ShowNotificationSnackBar showNotificationSnackBar = new ShowNotificationSnackBar(getContext(), getView(), swipeRefreshLayout) {
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
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.list_feed;
    }

    private class CustomDateComparator implements Comparator<FeedItem> {
        @Override
        public int compare(FeedItem o1, FeedItem o2) {
            try {
                if (o1.getCreatedTime() == null || o2.getCreatedTime() == null)
                    return 0;
                return DateConverter.getDate(o2.getCreatedTime()).compareTo(DateConverter.getDate(o1.getCreatedTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}
