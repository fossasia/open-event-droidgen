package org.fossasia.openevent.core.location;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ui.base.BaseRVAdapter;
import org.fossasia.openevent.common.ui.recyclerview.HeaderViewHolder;
import org.fossasia.openevent.common.ui.recyclerview.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.data.Microlocation;

import java.util.List;

public class LocationsListAdapter extends BaseRVAdapter<Microlocation, LocationViewHolder> implements StickyRecyclerHeadersAdapter<HeaderViewHolder> {

    private final Context context;

    public LocationsListAdapter(Context context, List<Microlocation> microLocations) {
        super(microLocations);
        this.context = context;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_location, parent, false);
        return new LocationViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(final LocationViewHolder holder, int position) {
        holder.bindLocation(getItem(position));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public long getHeaderId(int position) {
        return getItem(position).getName().toUpperCase().charAt(0);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_header, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder holder, int position) {
        String locationName = Utils.checkStringEmpty(getItem(position).getName());
        if (!Utils.isEmpty(locationName)) {
            holder.header.setText(String.valueOf(locationName.toUpperCase().charAt(0)));
        }
    }
}