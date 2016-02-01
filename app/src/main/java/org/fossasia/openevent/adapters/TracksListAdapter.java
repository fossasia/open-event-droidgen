package org.fossasia.openevent.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DbSingleton;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * User: MananWason
 * Date: 07-06-2015
 */
public class TracksListAdapter extends BaseRVAdapter<Track, TracksListAdapter.Viewholder> {

    @SuppressWarnings("all")
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            DbSingleton instance = DbSingleton.getInstance();
            List<Track> trackList = instance.getTrackList();
            final ArrayList<Track> filteredTracksList = new ArrayList<>();
            String query = constraint.toString().toLowerCase();
            for (Track track : trackList) {
                final String text = track.getName().toLowerCase();
                if (text.contains(query)) {
                    filteredTracksList.add(track);
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredTracksList;
            filterResults.count = filteredTracksList.size();
            Timber.d("Filtering done total results %d", filterResults.count);
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            animateTo((List<Track>) results.values);
        }
    };

    public TracksListAdapter(List<Track> tracks) {
        super(tracks);
    }

    @Override
    public TracksListAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_track, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(TracksListAdapter.Viewholder holder, int position) {
        Track current = getItem(position);
        holder.title.setText(current.getName());
        holder.desc.setText(current.getDescription());

        if (position == 0) {
            holder.topLine.setVisibility(View.INVISIBLE);
        } else {
            holder.topLine.setVisibility(View.VISIBLE);
        }

        if (position == getItemCount() - 1) {
            holder.bottomLine.setVisibility(View.INVISIBLE);
        } else {
            holder.bottomLine.setVisibility(View.VISIBLE);
        }
    }

    public void refresh() {
        Timber.d("Refreshing tracks from db");
        DbSingleton dbSingleton = DbSingleton.getInstance();
        clear();
        animateTo(dbSingleton.getTrackList());
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    class Viewholder extends RecyclerView.ViewHolder {
        TextView title;

        TextView desc;

        View topLine;

        View bottomLine;

        public Viewholder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            title = (TextView) itemView.findViewById(R.id.track_title);
            desc = (TextView) itemView.findViewById(R.id.track_description);
            topLine = itemView.findViewById(R.id.track_top);
            bottomLine = itemView.findViewById(R.id.track_bottom);
        }
    }
}
