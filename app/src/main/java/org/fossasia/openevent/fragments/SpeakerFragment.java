package org.fossasia.openevent.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.adapters.SpeakersListAdapter;
import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SpeakersActivity;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.DataDownload;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.SpeakerDownloadEvent;
import org.fossasia.openevent.events.TracksDownloadEvent;
import org.fossasia.openevent.utils.RecyclerItemClickListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the factory method to
 * create an instance of this fragment.
 */
public class SpeakerFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView speakersRecyclerView;
    private SpeakersListAdapter speakersListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bus bus = OpenEventApp.getEventBus();
        bus.register(this);

        View view = inflater.inflate(R.layout.list_speakers, container, false);
        speakersRecyclerView = (RecyclerView) view.findViewById(R.id.rv_speakers);
        final DbSingleton dbSingleton = DbSingleton.getInstance();
        speakersListAdapter = new SpeakersListAdapter(dbSingleton.getSpeakerList());
        speakersRecyclerView.setAdapter(speakersListAdapter);
        speakersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.speaker_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DataDownload download = new DataDownload();
                dbSingleton.clearDatabase(DbContract.Speakers.TABLE_NAME);
                download.downloadSpeakers();

            }
        });
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


    @Subscribe
    public void speakerDownloadDone(SpeakerDownloadEvent event) {

        swipeRefreshLayout.setRefreshing(false);
        if (event.isState()) {
            speakersListAdapter.refresh();
            Log.d("countersp", "Refresh done");

        } else {
            Snackbar.make(getView(), getActivity().getString(R.string.refresh_failed), Snackbar.LENGTH_LONG).show();
            Log.d("countersp", "Refresh not done");

        }
    }
}
