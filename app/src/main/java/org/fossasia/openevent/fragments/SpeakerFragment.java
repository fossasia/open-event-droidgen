package org.fossasia.openevent.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.Adapters.SpeakersListAdapter;
import org.fossasia.openevent.R;
import org.fossasia.openevent.dbutils.DbSingleton;

/**
 * A simple {@link Fragment} subclass.
 * Use the factory method to
 * create an instance of this fragment.
 */
public class SpeakerFragment extends Fragment {

    RecyclerView speakersRecyclerView;
    SpeakersListAdapter speakersListAdapter;
    DbSingleton dbSingleton = DbSingleton.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_speakers, container, false);
        speakersRecyclerView = (RecyclerView) view.findViewById(R.id.rv_speakers);
        speakersListAdapter = new SpeakersListAdapter(dbSingleton.getSpeakerList());
        speakersRecyclerView.setAdapter(speakersListAdapter);
        speakersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }
}
