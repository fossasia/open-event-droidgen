package org.fossasia.openevent.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Session;

import java.util.List;

/**
 * Created by MananWason on 26-06-2015.
 */
public class SessionsAdapter extends RecyclerView.Adapter<SessionsAdapter.Viewholder> {
    List<Session> sessions;

    public SessionsAdapter(List<Session> sessions) {
        this.sessions = sessions;
    }

    @Override
    public SessionsAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.tracksactvity_item, parent,false);
        Viewholder viewholder = new Viewholder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(Viewholder holder, int position) {
        Session current = sessions.get(position);
        String title = current.getTitle();
        holder.sessionName.setText(title);
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView sessionName;

        public Viewholder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            sessionName = (TextView) itemView.findViewById(R.id.session_title);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
