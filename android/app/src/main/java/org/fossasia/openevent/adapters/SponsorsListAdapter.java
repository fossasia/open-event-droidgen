package org.fossasia.openevent.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.viewholders.SponsorViewHolder;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.utils.Utils;
import org.fossasia.openevent.views.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

/**
 * User: MananWason
 * Date: 09-06-2015
 */
public class SponsorsListAdapter extends BaseRVAdapter<Sponsor, SponsorViewHolder> implements StickyRecyclerHeadersAdapter {

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
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView.findViewById(R.id.recyclerview_view_header);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

        String sponsorType = getItem(position).getType();
        if (!Utils.isEmpty(sponsorType))
            textView.setText(sponsorType.toUpperCase());
    }
}
