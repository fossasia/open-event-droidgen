package org.fossasia.openevent.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.viewholders.SpeakerViewHolder;
import org.fossasia.openevent.data.Speaker;

import java.util.List;

public class SessionSpeakerListAdapter extends BaseRVAdapter<Speaker, SpeakerViewHolder> {

    public SessionSpeakerListAdapter(List<Speaker> speakers) {
        super(speakers);
    }

    @Override
    public SpeakerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session_speaker, parent, false);
        return new SpeakerViewHolder(view, parent.getContext());
    }

    public void onBindViewHolder(SpeakerViewHolder holder, int position) {
        holder.bindSpeaker(getItem(position));
    }
}