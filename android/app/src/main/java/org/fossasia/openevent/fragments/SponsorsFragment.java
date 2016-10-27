package org.fossasia.openevent.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.SponsorsListAdapter;
import org.fossasia.openevent.dbutils.DataDownloadManager;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.SponsorDownloadEvent;
import org.fossasia.openevent.utils.NetworkUtils;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Created by MananWason on 05-06-2015.
 */
public class SponsorsFragment extends BaseFragment {

    private SponsorsListAdapter sponsorsListAdapter;

    @BindView(R.id.txt_no_sponsors)
    TextView noSponsorsView;
    @BindView(R.id.sponsor_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.list_sponsors)
    RecyclerView sponsorsRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        final View view = super.onCreateView(inflater, container, savedInstanceState);

        OpenEventApp.getEventBus().register(this);
        final DbSingleton dbSingleton = DbSingleton.getInstance();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        sponsorsListAdapter = new SponsorsListAdapter(getContext(), dbSingleton.getSponsorList());
        sponsorsRecyclerView.setAdapter(sponsorsListAdapter);
        sponsorsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (sponsorsListAdapter.getItemCount() != 0) {
            noSponsorsView.setVisibility(View.GONE);
            sponsorsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noSponsorsView.setVisibility(View.VISIBLE);
            sponsorsRecyclerView.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.list_sponsors;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OpenEventApp.getEventBus().unregister(this);
    }

    @Subscribe
    public void sponsorDownloadDone(SponsorDownloadEvent event) {

        swipeRefreshLayout.setRefreshing(false);
        if (event.isState()) {
            sponsorsListAdapter.refresh();
            Timber.d("Refresh done");

        } else {
            if (getActivity() != null) {
                Snackbar.make(getView(), getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).setAction(R.string.retry_download, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refresh();
                    }
                }).show();
            }
            Timber.d("Refresh not done");

        }
    }

    private void refresh() {
        if (NetworkUtils.haveNetworkConnection(getActivity())) {
            DataDownloadManager.getInstance().downloadSponsors();
        } else {
            OpenEventApp.getEventBus().post(new SponsorDownloadEvent(true));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }
}
