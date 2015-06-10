package org.fossasia.openevent.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.Adapters.SponsorsListAdapter;
import org.fossasia.openevent.R;
import org.fossasia.openevent.dbutils.DbSingleton;

/**
 * Created by MananWason on 05-06-2015.
 */
public class SponsorsFragment extends Fragment {
    RecyclerView sponsorsRecyclerView;
    SponsorsListAdapter sponsorsListAdapter;
    DbSingleton dbSingleton = DbSingleton.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_sponsors, container, false);
        sponsorsRecyclerView = (RecyclerView) view.findViewById(R.id.list_sponsors);
        sponsorsListAdapter = new SponsorsListAdapter(getActivity(), dbSingleton.getSponsorList());
        sponsorsRecyclerView.setAdapter(sponsorsListAdapter);
        Log.d("FSPON", "");
        sponsorsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }
}
