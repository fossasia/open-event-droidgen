package org.fossasia.openevent.Adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Track;

import java.util.List;

/**
 * Created by MananWason on 07-06-2015.
 */
public class TracksListAdapter extends RecyclerView.Adapter<TracksListAdapter.Viewholder> {

    List<Track> tracks;
    Context context;
    private int lastPosition = -1;


    public TracksListAdapter(List<Track> tracks) {
        this.tracks = tracks;
    }

    @Override
    public TracksListAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
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
        setAnimation(holder.container, position);

    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }
    }

    class Viewholder extends RecyclerView.ViewHolder {
        TextView title;
        TextView desc;
        LinearLayout container;


        public Viewholder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            container = (LinearLayout) itemView.findViewById(R.id.ll_tracks);
            title = (TextView) itemView.findViewById(R.id.track_title);
            desc = (TextView) itemView.findViewById(R.id.track_description);

        }

    }
}
