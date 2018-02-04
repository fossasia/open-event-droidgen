package org.fossasia.openevent.core.sponsor;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.api.DataDownloadManager;
import org.fossasia.openevent.common.events.SponsorDownloadEvent;
import org.fossasia.openevent.common.network.NetworkUtils;
import org.fossasia.openevent.common.ui.Views;
import org.fossasia.openevent.common.ui.base.BaseFragment;
import org.fossasia.openevent.common.ui.recyclerview.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.config.StrategyRegistry;
import org.fossasia.openevent.data.Sponsor;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

public class SponsorsFragment extends BaseFragment {

    private Context context;
    private List<Sponsor> sponsors = new ArrayList<>();
    private SponsorsListAdapter sponsorsListAdapter;
    private RecyclerView.AdapterDataObserver adapterDataObserver;

    @BindView(R.id.txt_no_sponsors)
    TextView noSponsorsView;
    @BindView(R.id.sponsor_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.list_sponsors)
    RecyclerView sponsorsRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        context = getContext();
        final View view = super.onCreateView(inflater, container, savedInstanceState);

        Utils.registerIfUrlValid(swipeRefreshLayout, this, this::refresh);
        setUpRecyclerView();

        //set up view model
        SponsorsFragmentViewModel sponsorsFragmentViewModel = ViewModelProviders.of(this).get(SponsorsFragmentViewModel.class);
        sponsorsFragmentViewModel.getSponsors().observe(this, sponsorsList -> {
            sponsors.clear();
            sponsors.addAll(sponsorsList);

            sponsorsListAdapter.notifyDataSetChanged();
            handleVisibility();
        });

        return view;
    }

    private void setUpRecyclerView() {
        sponsorsListAdapter = new SponsorsListAdapter(getContext(), sponsors);

        sponsorsRecyclerView.setHasFixedSize(true);
        sponsorsRecyclerView.setAdapter(sponsorsListAdapter);
        sponsorsRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        sponsorsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        final StickyRecyclerHeadersDecoration headersDecoration = new StickyRecyclerHeadersDecoration(sponsorsListAdapter);
        sponsorsRecyclerView.addItemDecoration(headersDecoration);
        adapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecoration.invalidateHeaders();
            }
        };
        sponsorsListAdapter.registerAdapterDataObserver(adapterDataObserver);
    }

    private void handleVisibility() {
        if (sponsorsListAdapter.getItemCount() != 0) {
            noSponsorsView.setVisibility(View.GONE);
            sponsorsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noSponsorsView.setVisibility(View.VISIBLE);
            sponsorsRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.list_sponsors;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.unregisterIfUrlValid(this);
        sponsorsListAdapter.unregisterAdapterDataObserver(adapterDataObserver);

        if(swipeRefreshLayout != null) swipeRefreshLayout.setOnRefreshListener(null);
    }

    @Subscribe
    public void sponsorDownloadDone(SponsorDownloadEvent event) {
        Views.setSwipeRefreshLayout(swipeRefreshLayout, false);

        if (event.isState()) {
            Timber.i("Sponsors download completed");
        } else {
            Timber.i("Sponsors download failed");
            if (getActivity() != null && swipeRefreshLayout != null) {
                Snackbar.make(swipeRefreshLayout, getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).setAction(R.string.retry_download, view -> refresh()).show();
            }
        }
    }

    private void refresh() {
        NetworkUtils.checkConnection(new WeakReference<>(getContext()), new NetworkUtils.NetworkStateReceiverListener() {

            @Override
            public void networkAvailable() {
                // Network is available
                DataDownloadManager.getInstance().downloadSponsors();
            }

            @Override
            public void networkUnavailable() {
                StrategyRegistry.getInstance()
                        .getEventBusStrategy()
                        .getEventBus()
                        .post(new SponsorDownloadEvent(true));
            }
        });
    }

}
