package org.fossasia.openevent.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.fossasia.openevent.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrackViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.imageView)
    public ImageView trackImageIcon;
    @BindView(R.id.track_title)
    public TextView trackTitle;

    public TrackViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
