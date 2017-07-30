package org.fossasia.openevent.adapters.viewholders;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.extras.SocialLink;
import org.fossasia.openevent.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SocialLinkViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.img_social_link)
    protected ImageView imageView;

    @BindView(R.id.layout_social_link_parent)
    protected FrameLayout layout;

    private SocialLink socialLink;
    private Context context;

    public SocialLinkViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        this.context = context;
        imageView.setOnClickListener(view -> {
            if (socialLink != null && !Utils.isEmpty(socialLink.getLink())) {
                Utils.setUpCustomTab(context, socialLink.getLink());
            }
        });
    }

    public void bindSocialLink(@NonNull SocialLink socialLink) {
        this.socialLink = socialLink;
        setImageDrawable(socialLink.getName(), socialLink.getLink());
    }

    private void setImageDrawable(@NonNull String name, @NonNull String link) {

        if (Utils.isEmpty(name) || Utils.isEmpty(link) || Utils.getSocialLinkDrawableId(name) == 1) {
            showView(false);
            return;
        }

        imageView.setImageDrawable(getDrawable(Utils.getSocialLinkDrawableId(name)));
    }

    private Drawable getDrawable(@DrawableRes int id) {
        return context.getResources().getDrawable(id);
    }

    private void showView(boolean show) {
        if (show) {
            imageView.setVisibility(View.VISIBLE);
            layout.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
            layout.setVisibility(View.GONE);
        }
    }
}
