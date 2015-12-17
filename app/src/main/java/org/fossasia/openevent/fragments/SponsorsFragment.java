package org.fossasia.openevent.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.SponsorsListAdapter;
import org.fossasia.openevent.dbutils.DataDownload;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.SponsorDownloadEvent;

/**
 * Created by MananWason on 05-06-2015.
 */
public class SponsorsFragment extends Fragment {
    private RecyclerView sponsorsRecyclerView;
    private SponsorsListAdapter sponsorsListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_sponsors, container, false);

        Bus bus = OpenEventApp.getEventBus();
        bus.register(this);
        sponsorsRecyclerView = (RecyclerView) view.findViewById(R.id.list_sponsors);
        final DbSingleton dbSingleton = DbSingleton.getInstance();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sponsor_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (haveNetworkConnection()) {
                    DataDownload download = new DataDownload();
                    dbSingleton.clearDatabase(DbContract.Sponsors.TABLE_NAME);
                    download.downloadSponsors();
                } else {
                    OpenEventApp.getEventBus().post(new SponsorDownloadEvent(true));
                }
            }
        });
        sponsorsListAdapter = new SponsorsListAdapter(dbSingleton.getSponsorList());
        sponsorsRecyclerView.setAdapter(sponsorsListAdapter);
        //2 grids
        mLayoutManager = new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false);
        sponsorsRecyclerView.setLayoutManager(mLayoutManager);
        return view;
    }


    @Subscribe
    public void sponsorDownloadDone(SponsorDownloadEvent event) {

        swipeRefreshLayout.setRefreshing(false);
        if (event.isState()) {
            sponsorsListAdapter.refresh();
            Log.d("countersp", "Refresh done");

        } else {
            Snackbar.make(getView(), getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).show();
            Log.d("countersp", "Refresh not done");

        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
