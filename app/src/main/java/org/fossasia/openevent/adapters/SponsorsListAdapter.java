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
 * Created by MananWason on 09-06-2015.
 */
public class SponsorsListAdapter extends RecyclerView.Adapter<SponsorsListAdapter.Viewholder> {

    List<Sponsor> sponsors;

    public SponsorsListAdapter(List<Sponsor> sponsors) {
        this.sponsors = sponsors;
    }

    @Override
    public SponsorsListAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_sponsor, parent, false);
        Viewholder viewholder = new Viewholder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(SponsorsListAdapter.Viewholder holder, int position) {
        Sponsor currentSponsor = sponsors.get(position);
        DisplayMetrics displayMetrics = holder.sponsor_image.getContext().getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        Uri uri = Uri.parse(currentSponsor.getLogo());
        Picasso.with(holder.sponsor_image.getContext()).load(uri).resize(width, (height / 6)).centerCrop().into(holder.sponsor_image);
    }

    public void refresh() {
        DbSingleton dbSingleton = DbSingleton.getInstance();
        sponsors.clear();
        sponsors = dbSingleton.getSponsorList();
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return sponsors.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView sponsor_image;

        public Viewholder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            sponsor_image = (ImageView) itemView.findViewById(R.id.sponsor_image);
        }

        @Override
        public void onClick(View view) {

        }
    }
}

