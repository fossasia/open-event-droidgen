package org.fossasia.openevent.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DbSingleton;

import java.util.List;

/**
 * Created by MananWason on 07-06-2015.
 */
public class TracksListAdapter extends RecyclerView.Adapter<TracksListAdapter.Viewholder> {

    List<Track> tracks;

    public TracksListAdapter(List<Track> tracks) {
        this.tracks = tracks;
    }

    @Override
    public TracksListAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_track, parent, false);
        Viewholder viewholder = new Viewholder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(TracksListAdapter.Viewholder holder, int position) {
        Track current = tracks.get(position);
        holder.title.setText(current.getName());
        holder.desc.setText(current.getDescription());

    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public void refresh() {

        DbSingleton dbSingleton = DbSingleton.getInstance();
        tracks.clear();
        tracks = dbSingleton.getTrackList();
        notifyDataSetChanged();

    }

    public void animateTo(List<Track> tracks) {
        applyAndAnimateRemovals(tracks);
        applyAndAnimateAdditions(tracks);
        applyAndAnimateMovedItems(tracks);
    }

    private void applyAndAnimateRemovals(List<Track> newTracks) {
        for (int i = tracks.size() - 1; i >= 0; i--) {
            final Track track = tracks.get(i);
            if (!newTracks.contains(track)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Track> newTracks) {
        for (int i = 0, count = newTracks.size(); i < count; i++) {
            final Track track = newTracks.get(i);
            if (!tracks.contains(track)) {
                addItem(i, track);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Track> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Track track = newModels.get(toPosition);
            final int fromPosition = tracks.indexOf(track);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public Track removeItem(int position) {
        final Track track = tracks.remove(position);
        notifyItemRemoved(position);
        return track;
    }

    public void addItem(int position, Track track) {
        tracks.add(position, track);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Track track = tracks.remove(fromPosition);
        tracks.add(toPosition, track);
        notifyItemMoved(fromPosition, toPosition);
    }

    class Viewholder extends RecyclerView.ViewHolder {
        TextView title;
        TextView desc;

        public Viewholder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            title = (TextView) itemView.findViewById(R.id.track_title);
            desc = (TextView) itemView.findViewById(R.id.track_description);

        }

    }
}
