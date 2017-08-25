package org.fossasia.openevent.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.viewholders.HeaderViewHolder;
import org.fossasia.openevent.adapters.viewholders.SponsorViewHolder;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.utils.Utils;
import org.fossasia.openevent.views.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

/**
 * User: MananWason
 * Date: 09-06-2015
 */
public class SponsorsListAdapter extends BaseRVAdapter<Sponsor, SponsorViewHolder> implements StickyRecyclerHeadersAdapter<HeaderViewHolder> {

    private Context context;

    public SponsorsListAdapter(Context context, List<Sponsor> sponsors) {
        super(sponsors);
        this.context = context;
    }

    @Override
    public SponsorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_sponsor, parent, false);
        return new SponsorViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(final SponsorViewHolder holder, int position) {
        holder.bindSponsor(getItem(position));
    }

    @Override
    public long getHeaderId(int position) {
        String level = getItem(position).getLevel();
        return Long.valueOf(level);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_header, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder holder, int position) {
        holder.header.setGravity(Gravity.CENTER_HORIZONTAL);

        String sponsorType = getItem(position).getType();
        if (!Utils.isEmpty(sponsorType))
            holder.header.setText(sponsorType.toUpperCase());
    }
}
