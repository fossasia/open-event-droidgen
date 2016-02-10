package org.fossasia.openevent.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * User: MananWason
 * Date: 07-06-2015
 */
public class TracksListAdapter extends BaseRVAdapter<Track, ViewHolder.Viewholder> {

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
    private ViewHolder.SetOnClickListener listener;

    public TracksListAdapter(List<Track> tracks) {
        super(tracks);
    }

    public void setOnClickListener(ViewHolder.SetOnClickListener clickListener) {
        this.listener = clickListener;
    }

    @Override
    public ViewHolder.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_track, parent, false);
        ViewHolder.Viewholder viewholder = new ViewHolder.Viewholder(view);
        viewholder.setTxtView1((TextView) view.findViewById(R.id.track_title));
        viewholder.setTxtView2((TextView) view.findViewById(R.id.track_description));
        viewholder.setView1(view.findViewById(R.id.track_top));
        viewholder.setView2(view.findViewById(R.id.track_bottom));

        return viewholder;
    }

    @Override
    public void onBindViewHolder(ViewHolder.Viewholder holder, int position) {
        Track current = getItem(position);
        holder.getTxtView1().setText(current.getName());
        holder.getTxtView2().setText(current.getDescription());
        holder.setItemClickListener(listener);

        if (position == 0) {
            holder.getView1().setVisibility(View.INVISIBLE);
        } else {
            holder.getView1().setVisibility(View.VISIBLE);
        }

        if (position == getItemCount() - 1) {
            holder.getView2().setVisibility(View.INVISIBLE);
        } else {
            holder.getView2().setVisibility(View.VISIBLE);
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

    /**
     * to handle click listener
     */
    public interface SetOnClickListener extends ViewHolder.SetOnClickListener {
        void onItemClick(int position, View itemView);
    }
}
