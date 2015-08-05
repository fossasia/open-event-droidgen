package org.fossasia.openevent.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Bus;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DataDownload;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.RefreshEvent;

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

    public void deleteItems() {
        tracks.clear();
        DataDownload download = new DataDownload();
        notifyDataSetChanged();
        download.downloadTracks();
        DbSingleton dbSingleton = DbSingleton.getInstance();
        dbSingleton.deleteAllRecords(DbContract.Tracks.TABLE_NAME);
        tracks = dbSingleton.getTrackList();
        notifyDataSetChanged();

    }

    public void refresh() {
        DbSingleton dbSingleton = DbSingleton.getInstance();
        tracks.clear();
        tracks = dbSingleton.getTrackList();
        notifyDataSetChanged();
        Bus bus = OpenEventApp.getEventBus();
        bus.post(new RefreshEvent());

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
