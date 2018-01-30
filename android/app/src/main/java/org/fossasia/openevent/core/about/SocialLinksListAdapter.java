package org.fossasia.openevent.core.about;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ui.base.BaseRVAdapter;
import org.fossasia.openevent.data.extras.SocialLink;

import java.util.List;

public class SocialLinksListAdapter extends BaseRVAdapter<SocialLink, SocialLinkViewHolder> {

    public SocialLinksListAdapter(List<SocialLink> socialLinks) {
        super(socialLinks);
    }

    @Override
    public SocialLinkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_social_link, parent, false);
        return new SocialLinkViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(SocialLinkViewHolder holder, int position) {
        holder.bindSocialLink(getItem(position));
    }
}