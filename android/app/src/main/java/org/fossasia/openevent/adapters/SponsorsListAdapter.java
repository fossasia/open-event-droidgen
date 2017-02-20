package org.fossasia.openevent.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Patterns;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 09-06-2015
 */
public class SponsorsListAdapter extends BaseRVAdapter<Sponsor, RecyclerView.ViewHolder> {

    public static final int SPONSOR = 0;
    public static final int CATEGORY = 1;

    private Context context;
    private Activity activity;
    private boolean customTabsSupported;

    public SponsorsListAdapter(Context context, List<Sponsor> sponsors, Activity activity, boolean customTabsSupported) {
        super(sponsors);
        this.context = context;
        this.activity = activity;
        this.customTabsSupported = customTabsSupported;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;

        if (viewType == SPONSOR) {
            View viewSponsors = layoutInflater.inflate(R.layout.item_sponsor, parent, false);
            viewHolder = new SponsorViewHolder(viewSponsors);
        } else if (viewType == CATEGORY) {
            View viewCategory = layoutInflater.inflate(R.layout.item_sponsor_type, parent, false);
            viewHolder = new CategoryViewHolder(viewCategory);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof SponsorViewHolder) {

            final SponsorViewHolder sponsorViewHolder = (SponsorViewHolder) holder;
            DisplayMetrics displayMetrics = (sponsorViewHolder.sponsorImage.getContext().getResources().getDisplayMetrics());
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;
            Uri uri;
            Sponsor currentSponsor = getItem(position);
            if (!currentSponsor.getLogo().startsWith("https://")) {
                uri = Uri.parse(Urls.getBaseUrl() + currentSponsor.getLogo());
            } else {
                uri = Uri.parse(currentSponsor.getLogo());
            }
            sponsorViewHolder.sponsorType.setText(currentSponsor.getType());

            Picasso.with(sponsorViewHolder.sponsorImage.getContext())
                    .load(uri)
                    .resize(width, (height / 6))
                    .centerInside()
                    .into(sponsorViewHolder.sponsorImage);

            sponsorViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Sponsor> objects = DbSingleton.getInstance().getSponsorList();
                    Sponsor sponsor = objects.get(sponsorViewHolder.getAdapterPosition());
                    String sponsorUrl = sponsor.getUrl();
                    if (!sponsorUrl.startsWith("http") && !sponsorUrl.startsWith("https")) {
                        sponsorUrl = "http://" + sponsorUrl;
                    }
                    if (Patterns.WEB_URL.matcher(sponsorUrl).matches()) {
                        if (customTabsSupported) {
                            CustomTabsIntent.Builder customTabsBuilder = new CustomTabsIntent.Builder();

                            customTabsBuilder.setToolbarColor(ContextCompat.getColor(context, R.color.color_primary));
                            customTabsBuilder.setCloseButtonIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_arrow_back_white_cct_24dp));
                            customTabsBuilder.setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left);
                            customTabsBuilder.setExitAnimations(context, R.anim.slide_in_left, R.anim.slide_out_right);

                            CustomTabsIntent customTabsIntent = customTabsBuilder.build();
                            customTabsIntent.launchUrl(activity, Uri.parse(sponsorUrl));
                        } else {
                            Intent sponsorsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sponsorUrl));
                            context.startActivity(sponsorsIntent);
                        }
                    } else {
                        Snackbar.make(v, R.string.invalid_url, Snackbar.LENGTH_LONG).show();
                        Timber.d(sponsorUrl);
                    }
                }
            });
        } else if (holder instanceof CategoryViewHolder) {

            CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
            categoryViewHolder.sponsorCategory.setText(getItem(position).toString());
        }
    }

    public void refresh() {
        clear();
        animateTo(DbSingleton.getInstance().getSponsorList());
    }

    @Override
    public int getItemViewType(int position) {
        return SPONSOR;

    }

    protected class SponsorViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.sponsor_image)
        ImageView sponsorImage;

        @BindView(R.id.sponsor_type)
        TextView sponsorType;

        public SponsorViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    protected class CategoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.sponsor_category)
        TextView sponsorCategory;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
