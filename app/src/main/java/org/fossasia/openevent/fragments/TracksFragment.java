package org.fossasia.openevent.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.Adapters.TracksListAdapter;
import org.fossasia.openevent.R;
import org.fossasia.openevent.dbutils.DbSingleton;

/**
 * Created by MananWason on 05-06-2015.
 */
public class TracksFragment extends Fragment {

    RecyclerView tracksRecyclerView;
    TracksListAdapter tracksListAdapter;
    DbSingleton dbSingleton = DbSingleton.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_tracks, container, false);
        tracksRecyclerView = (RecyclerView) view.findViewById(R.id.list_tracks);
        tracksListAdapter = new TracksListAdapter(getActivity(), dbSingleton.getTrackList());
        tracksRecyclerView.setAdapter(tracksListAdapter);
        tracksRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

}
