package org.fossasia.openevent.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.SponsorsListAdapter;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.SponsorDownloadEvent;
import org.fossasia.openevent.utils.NetworkUtils;
import org.fossasia.openevent.utils.ShowNotificationSnackBar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.realm.Realm;
import timber.log.Timber;

/**
 * Created by MananWason on 05-06-2015.
 */
public class SponsorsFragment extends BaseFragment {

    private List<Sponsor> mSponsors = new ArrayList<>();
    private SponsorsListAdapter sponsorsListAdapter;

    @BindView(R.id.txt_no_sponsors)
    TextView noSponsorsView;
    @BindView(R.id.sponsor_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.list_sponsors)
    RecyclerView sponsorsRecyclerView;

    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();
    private Realm realm = realmRepo.getRealmInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        final View view = super.onCreateView(inflater, container, savedInstanceState);

        OpenEventApp.getEventBus().register(this);

        swipeRefreshLayout.setOnRefreshListener(this::refresh);
        sponsorsListAdapter = new SponsorsListAdapter(getContext(), mSponsors,
                getActivity(), true);
        sponsorsRecyclerView.setAdapter(sponsorsListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        sponsorsRecyclerView.setLayoutManager(linearLayoutManager);

        RealmDataRepository.getDefaultInstance()
                .getSponsors()
                .addChangeListener((sponsors, orderedCollectionChangeSet) -> {
                    mSponsors.clear();
                    mSponsors.addAll(realm.copyFromRealm(sponsors));

                    sponsorsListAdapter.notifyDataSetChanged();
                    handleVisibility();
                });

        return view;
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
        OpenEventApp.getEventBus().unregister(this);

        // Remove listeners to fix memory leak
        if(swipeRefreshLayout != null) swipeRefreshLayout.setOnRefreshListener(null);
    }

    @Subscribe
    public void sponsorDownloadDone(SponsorDownloadEvent event) {
        if(swipeRefreshLayout == null)
            return;

        swipeRefreshLayout.setRefreshing(false);
        if (event.isState()) {
            Timber.d("Refresh done");
        } else {
            Snackbar.make(swipeRefreshLayout, getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).setAction(R.string.retry_download, view -> refresh()).show();
            Timber.d("Refresh not done");
        }
    }

    private void refresh() {
        NetworkUtils.checkConnectionSponsers(new WeakReference<>(getContext()), new NetworkUtils.NetworkStateReceiverListener() {
            @Override
            public void activeConnection() {
                //Internet is working
            }

            @Override
            public void inactiveConnection() {
                //internet is not working
                showNotification();
            }

            @Override
            public void networkAvailable() {
                //network is available
            }

            @Override
            public void networkUnavailable() {
                //network is unavailable
                Snackbar.make(swipeRefreshLayout, getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).setAction(R.string.retry_download, view -> refresh()).show();
            }
        });
    }

    public void showNotification() {
        ShowNotificationSnackBar showNotificationSnackBar = new ShowNotificationSnackBar(getContext(),getView(),swipeRefreshLayout) {
            @Override
            public void refreshClicked() {
                refresh();
            }
        };
        //show snackbar will be useful if user have blocked notification for this app
        showNotificationSnackBar.showSnackBar();
        showNotificationSnackBar.buildNotification();
    }
}
