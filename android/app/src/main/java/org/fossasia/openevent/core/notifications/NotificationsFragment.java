package org.fossasia.openevent.core.notifications;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.network.NetworkUtils;
import org.fossasia.openevent.common.ui.base.BaseFragment;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.core.auth.AuthUtil;
import org.fossasia.openevent.core.auth.LoginActivity;
import org.fossasia.openevent.data.Notification;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

public class NotificationsFragment extends BaseFragment {

    private NotificationsFragmentViewModel notificationsFragmentViewModel;
    private NotificationsAdapter notificationsAdapter;
    private List<Notification> notificationsList;

    @BindView(R.id.notification_refresh)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.notifications_recycler_view)
    protected RecyclerView notificationRecyclerView;
    @BindView(R.id.txt_no_notification)
    protected TextView noNotificationView;

    public static NotificationsFragment getInstance() {
        return new NotificationsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = super.onCreateView(inflater, container, savedInstanceState);

        Utils.registerIfUrlValid(swipeRefreshLayout, this, this::refresh);

        notificationsList = new ArrayList<>();
        notificationsFragmentViewModel = ViewModelProviders.of(this).get(NotificationsFragmentViewModel.class);
        setUpRecyclerView();

        if(AuthUtil.isUserLoggedIn()) {
            if (NetworkUtils.haveNetworkConnection(getContext())) {
                swipeRefreshLayout.setRefreshing(true);
                downloadNotifications();
            }
            loadNotifications();
            handleVisibility();
        } else {
            redirectToLogin();
        }

        return view;
    }

    private void setUpRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        notificationRecyclerView.setLayoutManager(layoutManager);
        notificationsAdapter = new NotificationsAdapter(notificationsList);
        notificationRecyclerView.setAdapter(notificationsAdapter);
    }

    private void loadNotifications() {
        notificationsFragmentViewModel.getNotificationsData().observe(this, notifications -> {
            notificationsList.clear();
            notificationsList.addAll(notifications);
            notificationsAdapter.notifyDataSetChanged();
            handleVisibility();
        });
    }

    private void handleVisibility() {
        if (notificationsList.isEmpty()) {
            noNotificationView.setVisibility(View.VISIBLE);
            notificationRecyclerView.setVisibility(View.GONE);
        } else {
            noNotificationView.setVisibility(View.GONE);
            notificationRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void onNotificationsDownloadDone(boolean status) {
        if (!status) {
            Timber.d("Notifications download failed");
            if (getActivity() != null && swipeRefreshLayout != null) {
                Snackbar.make(swipeRefreshLayout, getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).setAction(R.string.retry_download, view -> refresh()).show();
            }
        }
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
    }

    private void refresh() {
        NetworkUtils.checkConnection(new WeakReference<>(getContext()), new NetworkUtils.NetworkStateReceiverListener() {

            @Override
            public void networkAvailable() {
                // Network is available
                if (AuthUtil.isUserLoggedIn()) {
                    downloadNotifications();
                } else {
                    redirectToLogin();
                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void networkUnavailable() {
                onNotificationsDownloadDone(false);
            }
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.list_notification;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.unregisterIfUrlValid(this);
        if (swipeRefreshLayout != null) swipeRefreshLayout.setOnRefreshListener(null);
    }

    private void redirectToLogin() {
        Toast.makeText(getContext(), "Please login to see notifications!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    private void downloadNotifications() {
        notificationsFragmentViewModel.downloadNotifications().observe(NotificationsFragment.this, notificationsDownloadResponse -> {
            onNotificationsDownloadDone(notificationsDownloadResponse);
        });
    }
}
