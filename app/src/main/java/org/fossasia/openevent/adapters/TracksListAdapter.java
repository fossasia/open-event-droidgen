package org.fossasia.openevent.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
        View view = layoutInflater.inflate(R.layout.tracks_item, parent, false);
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
