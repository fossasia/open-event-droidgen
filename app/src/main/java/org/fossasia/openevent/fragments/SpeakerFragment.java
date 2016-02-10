package org.fossasia.openevent.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SpeakersActivity;
import org.fossasia.openevent.adapters.SpeakersListAdapter;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.DataDownload;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.SpeakerDownloadEvent;

import java.util.List;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the factory method to
 * create an instance of this fragment.
 */
public class SpeakerFragment extends Fragment implements SearchView.OnQueryTextListener {

    final private String SEARCH = "searchText";

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView speakersRecyclerView;

    private SpeakersListAdapter speakersListAdapter;

    private List<Speaker> mSpeakers;

    private String searchText = "";

    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.list_speakers, container, false);
        OpenEventApp.getEventBus().register(this);
        speakersRecyclerView = (RecyclerView) view.findViewById(R.id.rv_speakers);
        final DbSingleton dbSingleton = DbSingleton.getInstance();
        mSpeakers = dbSingleton.getSpeakerList();
        speakersListAdapter = new SpeakersListAdapter(mSpeakers);
        speakersRecyclerView.setAdapter(speakersListAdapter);

        speakersListAdapter.setOnClickListener(new SpeakersListAdapter.SetOnClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Speaker model = (Speaker) speakersListAdapter.getItem(position);
                String speakerName = model.getName();
                Intent intent = new Intent(getContext(), SpeakersActivity.class);
                intent.putExtra(Speaker.SPEAKER, speakerName);
                startActivity(intent);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.speaker_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (haveNetworkConnection()) {
                    DataDownload download = new DataDownload();
                    download.downloadSpeakers();
                } else {
                    OpenEventApp.getEventBus().post(new SpeakerDownloadEvent(false));
                }
            }
        });

        speakersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null && savedInstanceState.getString(SEARCH) != null) {
            searchText = savedInstanceState.getString(SEARCH);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        if (isAdded()) {
            if (searchView != null) {
                bundle.putString(SEARCH, searchText);
            }
        }
        super.onSaveInstanceState(bundle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_speakers_url:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, Urls.WEB_APP_URL_BASIC + Urls.SPEAKERS);
                intent.putExtra(Intent.EXTRA_SUBJECT, R.string.share_links);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_links)));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_speakers, menu);
        MenuItem item = menu.findItem(R.id.action_search_speakers);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        searchView.setQuery(searchText, false);
    }

    @Subscribe
    public void speakerDownloadDone(SpeakerDownloadEvent event) {
        swipeRefreshLayout.setRefreshing(false);
        if (event.isState()) {
            speakersListAdapter.refresh();
            Timber.i("Speaker download completed");

        } else {
            if (getActivity() != null) {
                Snackbar.make(getView(), getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).show();
            }
            Timber.i("Speaker download failed.");
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        searchText = query;
        if (!TextUtils.isEmpty(query)) {
            speakersListAdapter.getFilter().filter(query);
        }
        return true;
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
