package org.fossasia.openevent.core.feedback;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import org.fossasia.openevent.data.Feedback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

public class FeedbackFragment extends BaseFragment {

    @BindView(R.id.rv_feedback)
    protected RecyclerView rvFeedback;
    @BindView(R.id.tv_emptyFeedbacks)
    protected TextView tvEmptyFeedback;
    @BindView(R.id.feedback_swipe_refresh_layout)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.submit_feedback_fab)
    protected FloatingActionButton submitFeedback;

    private ArrayList<Feedback> feedbackArrayList;
    private FeedbackListAdapter feedbackListAdapter;
    private FeedbackViewModel feedbackViewModel;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        Utils.registerIfUrlValid(swipeRefreshLayout, this, this::refresh);
        context = getContext();
        feedbackArrayList = new ArrayList<>();
        feedbackViewModel = ViewModelProviders.of(this).get(FeedbackViewModel.class);
        setUpRecyclerView();

        if (AuthUtil.isUserLoggedIn()) {
            if (NetworkUtils.haveNetworkConnection(getContext())) {
                downloadFeedbacks();
            }
            loadFeedbacks();
            handleVisibility();
        } else {
            redirectToLogin();
        }


        return view;
    }

    private void redirectToLogin() {
        Toast.makeText(getContext(), "Please login to see feedbacks !", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    private void loadFeedbacks() {
        feedbackViewModel.getFeedbackData().observe(this, feedbackList -> {
            feedbackArrayList.clear();
            feedbackArrayList.addAll(feedbackList);
            feedbackListAdapter.notifyDataSetChanged();
            handleVisibility();
        });
    }

    private void downloadFeedbacks() {
        feedbackViewModel.downloadFeedbacks().observe(this, this::onDownloadResponse);
    }

    private void onDownloadResponse(boolean feedbackDownloadResult) {
        if (!feedbackDownloadResult) {
            Timber.d("Feedbacks download failed");
            if (getActivity() != null && swipeRefreshLayout != null)
                Snackbar.make(swipeRefreshLayout, getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).setAction(R.string.retry_download, view -> refresh()).show();
        }

        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
    }

    private void refresh() {
        NetworkUtils.checkConnection(new WeakReference<>(getContext()), new NetworkUtils.NetworkStateReceiverListener() {

            @Override
            public void networkAvailable() {
                downloadFeedbacks();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void networkUnavailable() {
                onDownloadResponse(false);
            }

        });
    }

    private void handleVisibility() {
        if (feedbackArrayList.isEmpty()) {
            tvEmptyFeedback.setVisibility(View.VISIBLE);
            rvFeedback.setVisibility(View.GONE);
        } else {
            tvEmptyFeedback.setVisibility(View.GONE);
            rvFeedback.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!AuthUtil.isUserLoggedIn()) {
            tvEmptyFeedback.setText("Login to view Feedbacks!");
        }
    }

    private void setUpRecyclerView() {
        rvFeedback.setLayoutManager(new LinearLayoutManager(getContext()));
        feedbackListAdapter = new FeedbackListAdapter(feedbackArrayList);
        rvFeedback.setAdapter(feedbackListAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.unregisterIfUrlValid(this);
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setOnRefreshListener(null);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_feedbacks;
    }

    @OnClick(R.id.submit_feedback_fab)
    public void submitFeedback(){
        feedbackViewModel.postFeedback("1","This was bad").observe(this, postResponse ->{
            if(postResponse==true){
                Timber.d("post success");
            }else{
                Timber.d("Posting unsuccessful ");
            }
        });
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
//
//        LayoutInflater inflater = this.getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.submit_feedback_dialog, null);
//        dialogBuilder.setView(dialogView).setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
//        dialogBuilder.setView(dialogView).setPositiveButton("Submit", (dialog, which) -> {
//            Toast.makeText(context,"Submit",Toast.LENGTH_SHORT).show();
//
//        });
//        dialogBuilder.setTitle("Please share your feedback!");
//        EditText rating = dialogView.findViewById(R.id.feedback_rating);
//        EditText comments = dialogView.findViewById(R.id.feedback_comment);
//
//
//        AlertDialog alertDialog = dialogBuilder.create();
//        alertDialog.show();

    }
}
