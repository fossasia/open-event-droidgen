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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SpeakersActivity;
import org.fossasia.openevent.adapters.SpeakersListAdapter;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.DataDownload;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.SpeakerDownloadEvent;
import org.fossasia.openevent.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the factory method to
 * create an instance of this fragment.
 */
public class SpeakerFragment extends Fragment implements SearchView.OnQueryTextListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView speakersRecyclerView;
    private SpeakersListAdapter speakersListAdapter;
    private List<Speaker> mSpeakers;


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
        speakersRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(view.getContext(),
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                String speaker_name = ((TextView) view.findViewById(R.id.speaker_name)).getText().toString();
                                Intent intent = new Intent(view.getContext(), SpeakersActivity.class);
                                intent.putExtra(Speaker.SPEAKER, speaker_name);
                                startActivity(intent);
                            }
                        }));

        return view;
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
        menu.clear();
        inflater.inflate(R.menu.menu_speakers, menu);
        final MenuItem item = menu.findItem(R.id.action_search_speakers);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Subscribe
    public void speakerDownloadDone(SpeakerDownloadEvent event) {

        swipeRefreshLayout.setRefreshing(false);
        if (event.isState()) {
            speakersListAdapter.refresh();
            Log.d("countersp", "Refresh done");

        } else {
            if (getActivity()!=null){
                Snackbar.make(getView(), getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).show();
            }
            Log.d("countersp", "Refresh not done");

        }
    }

    private List<Speaker> filter(List<Speaker> speakers, String query) {
        query = query.toLowerCase();

        final List<Speaker> filteredSpeakersList = new ArrayList<>();
        for (Speaker speaker : speakers) {
            final String text = speaker.getName().toLowerCase();
            Log.d("XYZ speaker", text);
            if (text.contains(query)) {
                filteredSpeakersList.add(speaker);
            }
        }
        return filteredSpeakersList;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        DbSingleton dbSingleton = DbSingleton.getInstance();

        mSpeakers = dbSingleton.getSpeakerList();
        final List<Speaker> filteredSpeakersList = filter(mSpeakers, query);
        Log.d("XYZ speaker", mSpeakers.size() + " " + filteredSpeakersList.size());

        speakersListAdapter.animateTo(filteredSpeakersList);
        speakersRecyclerView.scrollToPosition(0);
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
