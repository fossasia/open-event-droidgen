package org.fossasia.openevent.fragments;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpeakerlistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpeakerlistFragment extends Fragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SpeakerlistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SpeakerlistFragment newInstance() {
        SpeakerlistFragment fragment = new SpeakerlistFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SpeakerlistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_speakerlist, container, false);
    }


}
