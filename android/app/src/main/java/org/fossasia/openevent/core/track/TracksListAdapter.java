package org.fossasia.openevent.core.track;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ui.base.BaseRVAdapter;
import org.fossasia.openevent.common.ui.recyclerview.HeaderViewHolder;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.common.ui.recyclerview.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

public class TracksListAdapter extends BaseRVAdapter<Track, TrackViewHolder> implements StickyRecyclerHeadersAdapter<HeaderViewHolder> {

    private Context context;

    public TracksListAdapter(Context context, List<Track> tracks) {
        super(tracks);
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(TrackViewHolder holder, int position) {
        holder.bindTrack(getItem(position));
    }
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_track, parent, false);
        return new TrackViewHolder(view,context);
    }

    @Override
    public long getHeaderId(int position) {
        String trackName = Utils.checkStringEmpty(getItem(position).getName());
        if(!Utils.isEmpty(trackName))
            return trackName.toUpperCase().charAt(0);
        else
            return 0;
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_header, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder holder, int position) {
        String trackName = getItem(position).getName();
        if (!TextUtils.isEmpty(trackName))
            holder.header.setText(String.valueOf(trackName.toUpperCase().charAt(0)));
    }
}