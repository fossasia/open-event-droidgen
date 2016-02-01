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

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SessionDetailActivity;
import org.fossasia.openevent.adapters.SessionsListAdapter;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.utils.IntentStrings;
import org.fossasia.openevent.utils.RecyclerItemClickListener;

import java.util.List;

/**
 * Created by duncanleo on 1/2/16.
 */
public class SessionFragment extends Fragment {
    private List<Session> data;
    private RecyclerView sessionsRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = getArguments().getParcelableArrayList("data");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_session, container, false);
        sessionsRecyclerView = (RecyclerView)v.findViewById(R.id.sessionRecyclerView);
        sessionsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sessionsRecyclerView.setAdapter(new SessionsListAdapter(data));
        sessionsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), SessionDetailActivity.class);
                intent.putExtra(IntentStrings.SESSION, data.get(position).getTitle());
                startActivity(intent);
            }
        }));
        return v;
    }
}
