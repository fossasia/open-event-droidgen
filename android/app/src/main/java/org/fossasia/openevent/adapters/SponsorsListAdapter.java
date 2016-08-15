package org.fossasia.openevent.adapters;

import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.fossasia.openevent.R;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.ViewHolder;

import java.util.List;

/**
 * User: MananWason
 * Date: 09-06-2015
 */
public class SponsorsListAdapter extends BaseRVAdapter<Sponsor, ViewHolder.Viewholder> {

    public SponsorsListAdapter(List<Sponsor> sponsors) {
        super(sponsors);
    }

    public void setOnClickListener(ViewHolder.SetOnClickListener clickListener) {
        ViewHolder.SetOnClickListener listener = clickListener;
    }

    @Override
    public ViewHolder.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_sponsor, parent, false);
        ViewHolder.Viewholder viewholder = new ViewHolder.Viewholder(view);
        viewholder.setImgView1((ImageView) view.findViewById(R.id.sponsor_image));
        return viewholder;
    }

    @Override
    public void onBindViewHolder(ViewHolder.Viewholder holder, int position) {
        DisplayMetrics displayMetrics = holder.getImgView1().getContext().getResources().getDisplayMetrics();

        Sponsor currentSponsor = getItem(position);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        Uri uri;
        if (!currentSponsor.getLogo().startsWith("https://")) {
            uri = Uri.parse(Urls.getBaseUrl() + currentSponsor.getLogo());
        } else {
            uri = Uri.parse(currentSponsor.getLogo());

        }
        Picasso.with(holder.getImgView1().getContext()).load(uri).resize(width, (height / 6)).centerInside().into(holder.getImgView1());
    }

    public void refresh() {
        DbSingleton dbSingleton = DbSingleton.getInstance();
        clear();
        animateTo(dbSingleton.getSponsorList());
    }

    /**
     * to handle click listener
     */
    public interface SetOnClickListener extends ViewHolder.SetOnClickListener {
        void onItemClick(int position, View itemView);

    }
}
