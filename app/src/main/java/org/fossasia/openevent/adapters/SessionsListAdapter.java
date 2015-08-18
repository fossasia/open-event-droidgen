package org.fossasia.openevent.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;

import java.util.List;

/**
 * Created by MananWason on 26-06-2015.
 */
public class SessionsListAdapter extends RecyclerView.Adapter<SessionsListAdapter.Viewholder> {
    List<Session> sessions;

    public SessionsListAdapter(List<Session> sessions) {
        this.sessions = sessions;
    }

    @Override
    public SessionsListAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.tracksactvity_item, parent, false);
        Viewholder viewholder = new Viewholder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(Viewholder holder, int position) {
        Session current = sessions.get(position);
        String title = current.getTitle();
        String summary = current.getSummary();
        holder.sessionName.setText(title);
        holder.sessionSummary.setText(summary);
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }
    public void animateTo(List<Session> sessions) {
        applyAndAnimateRemovals(sessions);
        applyAndAnimateAdditions(sessions);
        applyAndAnimateMovedItems(sessions);
    }

    private void applyAndAnimateRemovals(List<Session> newSessions) {
        for (int i = sessions.size() - 1; i >= 0; i--) {
            final Session speaker = sessions.get(i);
            if (!newSessions.contains(speaker)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Session> newSessions) {
        for (int i = 0, count = newSessions.size(); i < count; i++) {
            final Session speaker = newSessions.get(i);
            if (!sessions.contains(speaker)) {
                addItem(i, speaker);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Session> newSessions) {
        for (int toPosition = newSessions.size() - 1; toPosition >= 0; toPosition--) {
            final Session speaker = newSessions.get(toPosition);
            final int fromPosition = sessions.indexOf(speaker);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public Session removeItem(int position) {
        final Session speaker = sessions.remove(position);
        notifyItemRemoved(position);
        return speaker;
    }

    public void addItem(int position, Session speaker) {
        sessions.add(position, speaker);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Session speaker = sessions.remove(fromPosition);
        sessions.add(toPosition, speaker);
        notifyItemMoved(fromPosition, toPosition);
    }
    class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView sessionName;
        TextView sessionSummary;

        public Viewholder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            sessionName = (TextView) itemView.findViewById(R.id.session_title);
            sessionSummary = (TextView) itemView.findViewById(R.id.session_abstract);

        }

        @Override
        public void onClick(View view) {

        }
    }
}
