package org.fossasia.openevent.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.viewholders.SponsorViewHolder;
import org.fossasia.openevent.data.Sponsor;

import java.util.List;

/**
 * User: MananWason
 * Date: 09-06-2015
 */
public class SponsorsListAdapter extends BaseRVAdapter<Sponsor, SponsorViewHolder> {

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
}
