package org.fossasia.openevent.adapters;

import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
    public static final int SPONSOR = 0;

    public static final int CATEGORY = 1;

    public SponsorsListAdapter(List<Sponsor> sponsors) {
        super(sponsors);
    }

    public void setOnClickListener(ViewHolder.SetOnClickListener clickListener) {
        ViewHolder.SetOnClickListener listener = clickListener;
    }

    @Override
    public ViewHolder.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewHolder.Viewholder viewHolder;
        switch (viewType) {
            case SPONSOR:
                View viewSponsors = layoutInflater.inflate(R.layout.item_sponsor, parent, false);
                viewHolder = new ViewHolder.Viewholder(viewSponsors);
                viewHolder.setImgView1((ImageView) viewSponsors.findViewById(R.id.sponsor_image));
                viewHolder.setTxtView1((TextView) viewSponsors.findViewById(R.id.sponsor_type));
                break;
            case CATEGORY:
                View viewCategory = layoutInflater.inflate(R.layout.item_sponsor_type, parent, false);
                viewHolder = new ViewHolder.Viewholder(viewCategory);
                viewHolder.setTxtView1((TextView) viewCategory.findViewById(R.id.sponsor_category));
                break;
            default:
                View defaultView = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                viewHolder = new ViewHolder.Viewholder(defaultView);
                break;

        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder.Viewholder holder, int position) {
        ViewHolder.Viewholder viewHolder = (ViewHolder.Viewholder) holder;
        switch (holder.getItemViewType()) {

            case SPONSOR:
                DisplayMetrics displayMetrics = holder.getImgView1().getContext().getResources().getDisplayMetrics();
                int width = displayMetrics.widthPixels;
                int height = displayMetrics.heightPixels;
                Uri uri;
                Sponsor currentSponsor = getItem(position);
                if (!currentSponsor.getLogo().startsWith("https://")) {
                    uri = Uri.parse(Urls.getBaseUrl() + currentSponsor.getLogo());
                } else {
                    uri = Uri.parse(currentSponsor.getLogo());
                }
                viewHolder.getTxtView1().setText(currentSponsor.getType());
                Picasso.with(holder.getImgView1().getContext()).load(uri).resize(width, (height / 6)).centerInside().into(holder.getImgView1());
                break;
            case CATEGORY:
                viewHolder.getTxtView1().setText(getItem(position).toString());
                break;
            default:
                viewHolder.getTxtView1().setText("NO VIEW");
                break;

        }

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

    @Override
    public int getItemViewType(int position) {
        return SPONSOR;

    }

}
