package org.fossasia.openevent.adapters;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.dbutils.DbSingleton;

import java.util.List;

/**
 * User: MananWason
 * Date: 09-06-2015
 */
public class SponsorsListAdapter extends BaseRVAdapter<Sponsor, SponsorsListAdapter.Viewholder> {

    public SponsorsListAdapter(List<Sponsor> sponsors) {
        super(sponsors);
    }

    @Override
    public SponsorsListAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_sponsor, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(SponsorsListAdapter.Viewholder holder, int position) {
        Sponsor currentSponsor = getItem(position);
        DisplayMetrics displayMetrics = holder.sponsorImage.getContext().getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        Uri uri = Uri.parse(currentSponsor.getLogo());
        Picasso.with(holder.sponsorImage.getContext()).load(uri).into(holder.sponsorImage);
    }

    public void refresh() {
        DbSingleton dbSingleton = DbSingleton.getInstance();
        clear();
        animateTo(dbSingleton.getSponsorList());
    }

    public class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView sponsorImage;

        public Viewholder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            sponsorImage = (ImageView) itemView.findViewById(R.id.sponsor_image);
        }

        @Override
        public void onClick(View view) {

        }
    }
}

