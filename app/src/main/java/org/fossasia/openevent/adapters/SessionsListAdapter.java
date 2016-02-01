package org.fossasia.openevent.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.DbSingleton;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * User: MananWason
 * Date: 26-06-2015
 */
public class SessionsListAdapter extends BaseRVAdapter<Session, SessionsListAdapter.Viewholder> {
    @SuppressWarnings("all")
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            DbSingleton instance = DbSingleton.getInstance();
            // TODO: Use a query to do this, iterating over an entire set is pretty bad
            List<Session> sessionList = instance.getSessionList();
            final ArrayList<Session> filteredSessionList = new ArrayList<>();
            String query = constraint.toString().toLowerCase();
            for (Session session : sessionList) {
                final String text = session.getTitle().toLowerCase();
                if (text.contains(query)) {
                    filteredSessionList.add(session);
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredSessionList;
            filterResults.count = filteredSessionList.size();
            Timber.d("Filtering done total results %d", filterResults.count);
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            animateTo((List<Session>) results.values);
        }
    };

    public SessionsListAdapter(List<Session> sessions) {
        super(sessions);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public SessionsListAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.tracksactvity_item, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(Viewholder holder, int position) {
        Session current = getItem(position);
        String title = current.getTitle();
        String summary = current.getSummary();
        holder.sessionName.setText(title);
        holder.sessionSummary.setText(summary);
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
