package org.fossasia.openevent.core.feedback;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
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
import org.fossasia.openevent.data.Feedback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

public class FeedbackFragment extends BaseFragment {

    @BindView(R.id.feedback_refresh_layout)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.txt_no_feedback)
    protected TextView noFeedbacksView;
    @BindView(R.id.feedback_header)
    protected TextView feedbackHeaderView;
    @BindView(R.id.list_feedbacks)
    protected RecyclerView feedbacksRecyclerView;

    private List<Feedback> feedbackList = new ArrayList<>();
    private FeedbacksListAdapter feedbacksListAdapter;
    private FeedbackFragmentViewModel feedbackFragmentViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        setUpRecyclerView();
        Utils.registerIfUrlValid(swipeRefreshLayout, this, this::refresh);
        feedbackFragmentViewModel = ViewModelProviders.of(this).get(FeedbackFragmentViewModel.class);
        if (AuthUtil.isUserLoggedIn()) {
            if (NetworkUtils.haveNetworkConnection(getContext())) {
                swipeRefreshLayout.setRefreshing(true);
                downloadFeedbacks();
            }
            loadData();
        } else {
            redirectToLogin();
        }
        return view;
    }

    private void redirectToLogin() {
        Toast.makeText(getContext(), "User need to be logged in!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    private void downloadFeedbacks() {
        feedbackFragmentViewModel.downloadFeedbacks().observe(this,this::onFeedbacksDownloadDone);
    }

    private void setUpRecyclerView() {
        feedbacksRecyclerView.setVisibility(View.VISIBLE);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        feedbacksRecyclerView.addItemDecoration(itemDecor);
        feedbacksListAdapter = new FeedbacksListAdapter(feedbackList);
        feedbacksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        feedbacksRecyclerView.setNestedScrollingEnabled(false);
        feedbacksRecyclerView.setAdapter(feedbacksListAdapter);
        feedbacksRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void handleVisibility() {
        if (!feedbackList.isEmpty()) {
            feedbackHeaderView.setVisibility(View.VISIBLE);
            feedbacksRecyclerView.setVisibility(View.VISIBLE);
            noFeedbacksView.setVisibility(View.GONE);
        } else {
            feedbackHeaderView.setVisibility(View.GONE);
            feedbacksRecyclerView.setVisibility(View.GONE);
        }
    }

    private void loadData() {
        feedbackFragmentViewModel.getFeedback().observe(this, feedbacks -> {
            feedbackList.clear();
            feedbackList.addAll(feedbacks);
            feedbacksListAdapter.notifyDataSetChanged();
            handleVisibility();
        });
    }

    public void onFeedbacksDownloadDone(boolean status) {
        if (!status) {
            Timber.d("Feedbacks Download failed");
            if (getActivity() != null && swipeRefreshLayout != null) {
                Snackbar.make(swipeRefreshLayout, getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry_download, view -> refresh()).show();
            }
        }
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
    }

    private void refresh() {
        if (NetworkUtils.haveNetworkConnection(getContext())) {
            if (AuthUtil.isUserLoggedIn()) {
                downloadFeedbacks();
            } else {
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(true);
                }
            }
        } else {
            onFeedbacksDownloadDone(false);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.list_feedbacks;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.unregisterIfUrlValid(this);
    }

}