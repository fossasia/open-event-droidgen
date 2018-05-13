package org.fossasia.openevent.core.discount;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
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
import org.fossasia.openevent.data.DiscountCode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

public class DiscountCodeFragment extends BaseFragment {

    @BindView(R.id.discount_refresh_layout)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.txt_no_discount_codes)
    protected TextView noDiscountCodeView;
    @BindView(R.id.discount_code_header)
    protected TextView discountCodeHeader;
    @BindView(R.id.list_discount_codes)
    protected RecyclerView discountCodesRecyclerView;

    private List<DiscountCode> discountCodes = new ArrayList<>();
    private DiscountCodesListAdapter discountCodesListAdapter;
    private DiscountFragmentViewModel discountFragmentViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        setUpRecyclerView();
        Utils.registerIfUrlValid(swipeRefreshLayout, this, this::refresh);
        discountFragmentViewModel = ViewModelProviders.of(this).get(DiscountFragmentViewModel.class);
        if (AuthUtil.isUserLoggedIn()) {
            if (NetworkUtils.haveNetworkConnection(getContext())) {
                swipeRefreshLayout.setRefreshing(true);
                downloadDiscountCodes();
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

    private void downloadDiscountCodes() {
        discountFragmentViewModel.downloadDiscountCodes().observe(this, this::onDiscountCodeDownloadDone);
    }

    private void setUpRecyclerView() {
        discountCodesRecyclerView.setVisibility(View.VISIBLE);
        discountCodesListAdapter = new DiscountCodesListAdapter(discountCodes);
        discountCodesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        discountCodesRecyclerView.setNestedScrollingEnabled(false);
        discountCodesRecyclerView.setAdapter(discountCodesListAdapter);
        discountCodesRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void handleVisibility() {
        if (!discountCodes.isEmpty()) {
            discountCodeHeader.setVisibility(View.VISIBLE);
            discountCodesRecyclerView.setVisibility(View.VISIBLE);
            noDiscountCodeView.setVisibility(View.GONE);
        } else {
            discountCodeHeader.setVisibility(View.GONE);
            discountCodesRecyclerView.setVisibility(View.GONE);
        }
    }

    private void loadData() {
        discountFragmentViewModel.getDiscountCodes().observe(this, discountCodes -> {
            this.discountCodes.clear();
            this.discountCodes.addAll(discountCodes);
            discountCodesListAdapter.notifyDataSetChanged();
            handleVisibility();
        });
    }

    public void onDiscountCodeDownloadDone(boolean status) {
        if (!status) {
            Timber.d("Discount Codes Download failed");
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
                downloadDiscountCodes();
            } else {
                redirectToLogin();
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);
            }
        } else {
            onDiscountCodeDownloadDone(false);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.list_discount_codes;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.unregisterIfUrlValid(this);
    }

}
