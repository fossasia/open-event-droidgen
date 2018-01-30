package org.fossasia.openevent.core.sponsor;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ui.base.BaseRVAdapter;
import org.fossasia.openevent.common.ui.recyclerview.HeaderViewHolder;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.common.ui.recyclerview.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

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
