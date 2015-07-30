package org.fossasia.openevent.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fossasia.openevent.adapters.SpeakersListAdapter;
import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SpeakersActivity;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.RecyclerItemClickListener;

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
}
