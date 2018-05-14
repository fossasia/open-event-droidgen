package org.fossasia.openevent.core.faqs;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.network.NetworkUtils;
import org.fossasia.openevent.common.ui.base.BaseFragment;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.core.auth.AuthUtil;
import org.fossasia.openevent.core.auth.LoginActivity;
import org.fossasia.openevent.data.FAQ;

import java.util.ArrayList;

import butterknife.BindView;
import timber.log.Timber;

public class FAQFragment extends BaseFragment {

    @BindView(R.id.rv_faqs)
    protected RecyclerView rvFaq;
    @BindView(R.id.tv_emptyFaqs)
    protected TextView tvEmptyFaqs;
    @BindView(R.id.faq_swiperefreshlayout)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.btn_login)
    protected TextView loginBtn;

    private ArrayList<FAQ> faqArrayList;
    private FAQListAdapter faqListAdapter;
    private FAQViewModel faqViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        setHasOptionsMenu(true); TODO : ADD SEARCH OPTION
        View view = super.onCreateView(inflater, container, savedInstanceState);
        faqViewModel = ViewModelProviders.of(this).get(FAQViewModel.class);

        if (AuthUtil.isUserLoggedIn()) {
            if (NetworkUtils.haveNetworkConnection(getContext())) {
                downloadFAQS();
            }
            showFaqLayout(true);
            Utils.registerIfUrlValid(swipeRefreshLayout, this, this::refresh);
            faqArrayList = new ArrayList<>();
            setUpRecyclerView();
            loadFAQs();
            handleVisibility();
        } else {
            showFaqLayout(false);
            tvEmptyFaqs.setText(R.string.login_to_continue);
            loginBtn.setOnClickListener(v -> redirectToLogin());
        }

        return view;
    }

    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    private void loadFAQs() {
        faqViewModel.getFaqData().observe(this, faqList -> {
            faqArrayList.clear();
            faqArrayList.addAll(faqList);
            faqListAdapter.notifyDataSetChanged();
            handleVisibility();
        });
    }

    private void downloadFAQS() {
        faqViewModel.downloadFAQ().observe(this, this::onDownloadResponse);
    }

    private void onDownloadResponse(boolean faqDownloadResult) {
        if (!faqDownloadResult) {
            Timber.d("FAQs download failed");
            if (getActivity() != null && swipeRefreshLayout != null)
                Snackbar.make(swipeRefreshLayout, getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).setAction(R.string.retry_download, view -> refresh()).show();
        }

        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
    }

    private void refresh() {
        if (NetworkUtils.haveNetworkConnection(getContext())) {
            downloadFAQS();
            swipeRefreshLayout.setRefreshing(false);
        } else {
            onDownloadResponse(false);
        }
    }

    private void handleVisibility() {
        if (faqArrayList.isEmpty()) {
            tvEmptyFaqs.setVisibility(View.VISIBLE);
            rvFaq.setVisibility(View.GONE);
        } else {
            tvEmptyFaqs.setVisibility(View.GONE);
            rvFaq.setVisibility(View.VISIBLE);
        }
    }

    private void showFaqLayout(boolean showLayout) {
        if (showLayout) {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        } else {
            swipeRefreshLayout.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }

    private void setUpRecyclerView() {
        rvFaq.setLayoutManager(new LinearLayoutManager(getContext()));
        faqListAdapter = new FAQListAdapter(faqArrayList);
        rvFaq.setAdapter(faqListAdapter);
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
        return R.layout.fragment_faqs;
    }
}
