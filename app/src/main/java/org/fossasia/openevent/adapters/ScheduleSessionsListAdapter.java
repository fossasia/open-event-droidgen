package org.fossasia.openevent.adapters;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.ISO8601Date;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * User: MananWason
 * Date: 26-06-2015
 */
public class ScheduleSessionsListAdapter extends BaseRVAdapter<Session, ScheduleSessionsListAdapter.Viewholder> {
    private List<Session> originalList, displayedList;

    @SuppressWarnings("all")
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            DbSingleton instance = DbSingleton.getInstance();
            // TODO: Use a query to do this, iterating over an entire set is pretty bad
            final ArrayList<Session> filteredSessionList = new ArrayList<>();
            String query = constraint.toString().toLowerCase();
            for (Session session : displayedList) {
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

    public ScheduleSessionsListAdapter(List<Session> sessions) {
        super(sessions);
        this.originalList = new ArrayList<>(sessions);
        this.displayedList = sessions;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_session_with_bookmark, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(Viewholder holder, int position) {
        Session current = getItem(position);
        SimpleDateFormat formatSDF = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        String startTime = formatSDF.format(ISO8601Date.getDateObject(current.getStartTime())),
                endTime = formatSDF.format(ISO8601Date.getDateObject(current.getEndTime()));

        TypedArray ta = holder.itemView.getContext().getResources().obtainTypedArray(R.array.tracks_colors);
        holder.trackIndicator.setBackgroundColor(ta.getColor(current.getTrack() - 1, Color.GRAY));
        ta.recycle();

        holder.sessionTitle.setText(current.getTitle());
        holder.sessionTime.setText(String.format("%s - %s", startTime, endTime));
        holder.sessionLocation.setText(DbSingleton.getInstance().getMicrolocationById(current.getMicrolocations()).getName());
    }

    class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View trackIndicator;
        TextView sessionTitle, sessionTime, sessionLocation;

        public Viewholder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            trackIndicator = itemView.findViewById(R.id.itemSessionTrackIndicator);
            sessionTitle = (TextView) itemView.findViewById(R.id.session_title);
            sessionTime = (TextView) itemView.findViewById(R.id.session_time);
            sessionLocation = (TextView) itemView.findViewById(R.id.session_location);
        }

        @Override
        public void onClick(View view) {

        }
    }

    public void refresh() {
        Timber.d("Refreshing tracks from db");
        clear();
        displayedList = new ArrayList<>(originalList);
        animateTo(displayedList);
    }
}
