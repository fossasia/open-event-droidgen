package org.fossasia.openevent.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.ScheduleListAdapter;
import org.fossasia.openevent.data.Event;

import java.util.ArrayList;

/**
 * Created by md91829 on 3/3/16.
 */
public class ScheduleFragment extends Fragment {

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Sample list
        ArrayList<Event> schedule=new ArrayList<>();
        Event event=new Event(0,"FOSSASIA","dev@fossasia.org","#ff0000","http://mysecureshell.readthedocs.org/en/latest/_images/logo_redhat.png","13:00","17:00",(float)37.783839,(float)-122.400546,"Moscone centre","www.google.com","Fossasia");
        schedule.add(event);
        Event event1=new Event(1,"FOSSASIA1","dev@fossasia.org1","#00ddff","http://mysecureshell.readthedocs.org/en/latest/_images/logo_redhat1.png","12:00","18:00",(float)37.783839,(float)-122.400546,"Moscone centre1","www.google1.com","Fossasia1");
        schedule.add(event1);
        Event event2=new Event(2,"FOSSASIA2","dev@fossasia.org2","#00aa00","http://mysecureshell.readthedocs.org/en/latest/_images/logo_redhat2.png","10:00","14:00",(float)37.783839,(float)-122.400546,"Moscone centre2","www.google2.com","Fossasia2");
        schedule.add(event2);
        Event event3=new Event(3,"FOSSASIA3","dev@fossasia.org3","#ff0000","http://mysecureshell.readthedocs.org/en/latest/_images/logo_redhat3.png","11:00","16:00",(float)37.783839,(float)-122.400546,"Moscone centre3","www.google3.com","Fossasia3");
        schedule.add(event3);

        recyclerView=(RecyclerView)view.findViewById(R.id.rv_schedule);
        ScheduleListAdapter scheduleListAdapter=new ScheduleListAdapter(schedule);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(scheduleListAdapter);

    }
}
