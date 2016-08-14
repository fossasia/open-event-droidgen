package org.fossasia.openevent.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.ISO8601Date;
import org.fossasia.openevent.utils.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * User: MananWason
 * Date: 26-06-2015
 */
public class SessionsListAdapter extends BaseRVAdapter<Session, ViewHolder.Viewholder> {

    private String trackName;
    @SuppressWarnings("all")
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            DbSingleton instance = DbSingleton.getInstance();
            // TODO: Use a query to do this, iterating over an entire set is pretty bad
            List<Session> sessionList = instance.getSessionbyTracksname(trackName);
            final ArrayList<Session> filteredSessionList = new ArrayList<>();
            String query = constraint.toString().toLowerCase(Locale.getDefault());
            for (Session session : sessionList) {
                final String text = session.getTitle().toLowerCase(Locale.getDefault());
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
    private ViewHolder.SetOnClickListener listener;

    public SessionsListAdapter(List<Session> sessions) {
        super(sessions);
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public void setOnClickListener(ViewHolder.SetOnClickListener clickListener) {
        this.listener = clickListener;
    }

    @Override
    public ViewHolder.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.tracksactvity_item, parent, false);
        ViewHolder.Viewholder viewholder = new ViewHolder.Viewholder(view);
        viewholder.setTxtView1((TextView) view.findViewById(R.id.session_title));
        viewholder.setTxtView2((TextView) view.findViewById(R.id.session_abstract));
        viewholder.setTxtView3 ((TextView) view.findViewById(R.id.session_track));
        viewholder.setTxtView4 ((TextView) view.findViewById(R.id.session_date));
        viewholder.setTxtView5 ((TextView) view.findViewById(R.id.session_start_time));
        viewholder.setTxtView6((TextView) view.findViewById(R.id.session_location));
        return viewholder;
    }

    @Override
    public void onBindViewHolder(ViewHolder.Viewholder holder, int position) {
        Session current = getItem(position);
        String title = current.getTitle();
        String summary = current.getSummary();
        String date = ISO8601Date.getTimeZoneDateString(ISO8601Date.getDateObject(current.getStartTime())).split(",")[0] + ", " + ISO8601Date.getTimeZoneDateString(ISO8601Date.getDateObject(current.getStartTime())).split(",")[1];
        holder.getTxtView1().setText(title);
        holder.getTxtView2().setText(summary);
        holder.getTxtView3().setText(current.getTrack().getName());
        holder.getTxtView4().setText(date);
        holder.getTxtView5().setText(ISO8601Date.get24HourTime(ISO8601Date.getDateObject(current.getStartTime())));
        holder.getTxtView6().setText(current.getMicrolocation().getName());
        holder.setItemClickListener(listener);
    }

    public void refresh() {
        Timber.d("Refreshing session List from db");
        DbSingleton dbSingleton = DbSingleton.getInstance();
        clear();
        animateTo(dbSingleton.getSessionbyTracksname(trackName));
    }

    /**
     * to handle click listener
     */
    public interface SetOnClickListener extends ViewHolder.SetOnClickListener {
        void onItemClick(int position, View itemView);
    }
}
