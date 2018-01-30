package org.fossasia.openevent.core.track.session;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.openevent.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SessionViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.session_title)
    public TextView sessionTitle;

    @BindView(R.id.session_subtitle)
    public TextView sessionSubtitle;

    @BindView(R.id.trackImageDrawable)
    public ImageView trackImageIcon;

    @BindView(R.id.session_track)
    public TextView sessionTrack;

    @BindView(R.id.session_date)
    public TextView sessionDate;

    @BindView(R.id.session_speaker)
    public TextView sessionSpeaker;

    @BindView(R.id.icon_speaker)
    public ImageView speakerIcon;

    @BindView(R.id.icon_location)
    public ImageView locationIcon;

    @BindView(R.id.session_time)
    public TextView sessionTime;

    @BindView(R.id.session_location)
    public TextView sessionLocation;

    @BindView(R.id.session_bookmark_status)
    public ImageView sessionBookmarkIcon;

    @BindView(R.id.session_details)
    public LinearLayout sessionDetailsHolder;

    @BindView(R.id.session_card)
    public CardView sessionCard;

    @BindView(R.id.titleLinearLayout)
    public LinearLayout sessionHeader;

    @BindView(R.id.session_status)
    public TextView sessionStatus;

    public SessionViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
