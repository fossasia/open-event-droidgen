package org.fossasia.openevent.core.sponsor;

import android.content.Context;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.config.StrategyRegistry;
import org.fossasia.openevent.data.Sponsor;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class SponsorViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.sponsor_image)
    public ImageView sponsorImage;
    @BindView(R.id.sponsor_name)
    public TextView sponsorName;
    private Sponsor sponsor;

    public SponsorViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        //Attach onClickListener for ViewHolder
        itemView.setOnClickListener(view -> {
            String sponsorUrl = sponsor.getUrl();

            if (TextUtils.isEmpty(sponsorUrl))
                return;

            if (!sponsorUrl.startsWith("http") && !sponsorUrl.startsWith("https")) {
                sponsorUrl = "http://" + sponsorUrl;
            }
            if (Patterns.WEB_URL.matcher(sponsorUrl).matches()) {
                Utils.setUpCustomTab(context, sponsorUrl);
            } else {
                Snackbar.make(view, R.string.invalid_url, Snackbar.LENGTH_LONG).show();
                Timber.d(sponsorUrl);
            }
        });
    }

    public void bindSponsor(Sponsor sponsor) {
        this.sponsor = sponsor;

        DisplayMetrics displayMetrics = (sponsorImage.getContext().getResources().getDisplayMetrics());
        final int width = displayMetrics.widthPixels;
        final int height = displayMetrics.heightPixels;

        String name = Utils.checkStringEmpty(sponsor.getName());
        String logo = Utils.parseImageUri(sponsor.getLogoUrl());

        sponsorName.setText(name);
        if (logo != null) {
            sponsorImage.setVisibility(View.VISIBLE);
            StrategyRegistry.getInstance()
                    .getHttpStrategy()
                    .getPicassoWithCache()
                    .load(Uri.parse(logo))
                    .resize(width, (height / 6))
                    .centerInside()
                    .error(R.drawable.ic_sponsors_grey_24dp)
                    .into(sponsorImage);
        } else {
            sponsorImage.setVisibility(View.GONE);
        }
    }
}
