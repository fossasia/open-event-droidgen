package org.fossasia.openevent.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikepenz.fastadapter.AbstractAdapter;
import com.mikepenz.fastadapter.IItem;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.items.LocationItem;
import org.fossasia.openevent.adapters.items.TrackItem;
import org.fossasia.openevent.adapters.viewholders.HeaderViewHolder;
import org.fossasia.openevent.utils.Utils;
import org.fossasia.openevent.views.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

public class StickyHeaderAdapter extends AbstractAdapter implements StickyRecyclerHeadersAdapter<HeaderViewHolder> {

    @Override
    public long getHeaderId(int position) {
        IItem item = getItem(position);

        if (item instanceof TrackItem && !Utils.isEmpty(((TrackItem) item).getModel().getName())) {
            return ((TrackItem) item).getModel().getName().toUpperCase().charAt(0);
        } else if (item instanceof LocationItem && !Utils.isEmpty(((LocationItem) item).getModel().getName())) {
            return ((LocationItem) item).getModel().getName().toUpperCase().charAt(0);
        }
        return 0;
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_header, parent, false);
        return new HeaderViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder holder, int position) {
        TextView textView = holder.textView;
        IItem item = getItem(position);

        if (item instanceof TrackItem && !Utils.isEmpty(((TrackItem) item).getModel().getName())) {
            textView.setText(String.valueOf(((TrackItem) item).getModel().getName().toUpperCase().charAt(0)));
        } else if (item instanceof LocationItem && !Utils.isEmpty(((LocationItem) item).getModel().getName())) {
            textView.setText(String.valueOf(((LocationItem) item).getModel().getName().toUpperCase().charAt(0)));
        }
    }

    //Required for FastAdapter

    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    public int getAdapterItemCount() {
        return 0;
    }

    @Override
    public List getAdapterItems() {
        return null;
    }

    @Override
    public IItem getAdapterItem(int position) {
        return null;
    }

    @Override
    public int getAdapterPosition(IItem item) {
        return -1;
    }

    @Override
    public int getAdapterPosition(long identifier) {
        return -1;
    }

    @Override
    public int getGlobalPosition(int position) {
        return -1;
    }
}
