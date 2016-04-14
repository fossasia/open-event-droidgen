package org.fossasia.openevent.adapters;

import android.widget.Filter;

import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.DbSingleton;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by suheb on 17/2/16.
 */
public class TrackSessionsListAdpater extends SessionsListAdapter {

    private int trackId;

    @SuppressWarnings("all")
    private Filter filter = new android.widget.Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            DbSingleton instance = DbSingleton.getInstance();
            String searchText = constraint.toString();
            final ArrayList<Session> filteredSessionList = instance.getSessionbySearchTextAndTrack(searchText, trackId);
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

    @Override
    public Filter getFilter() {
        return this.filter;
    }
    
    public TrackSessionsListAdpater(List<Session> sessions, int trackId) {
        super(sessions);
        this.trackId = trackId;
    }

}
