package org.fossasia.openevent.core.track;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ui.Views;
import org.fossasia.openevent.core.track.session.TrackSessionsActivity;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.common.ConstantStrings;
import org.fossasia.openevent.common.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrackViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.imageView)
    ImageView trackImageIcon;

    @BindView(R.id.track_title)
    TextView trackTitle;

    @BindView(R.id.no_of_sessions)
    TextView noOfSessions;

    private Track track;

    public TrackViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        //Attach onClickListener for ViewHolder
        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, TrackSessionsActivity.class);
            intent.putExtra(ConstantStrings.TRACK, track.getName());
            intent.putExtra(ConstantStrings.TRACK_ID, track.getId());
            context.startActivity(intent);
        });
    }

    public void bindTrack(Track track) {
        this.track = track;

        int trackColor = Color.parseColor(track.getColor());
        int sessions = track.getSessions().size();
        String trackName = Utils.checkStringEmpty(track.getName());

        trackTitle.setText(trackName);
        noOfSessions.getBackground().setColorFilter(trackColor, PorterDuff.Mode.SRC_ATOP);
        noOfSessions.setText(OpenEventApp.getAppContext().getResources().getQuantityString(R.plurals.sessions,
                sessions, sessions));

        if(!Utils.isEmpty(trackName)) {
            trackImageIcon.setImageDrawable(Views.getTextDrawableBuilder().round()
                    .build(String.valueOf(trackName.charAt(0)), trackColor));
            trackImageIcon.setBackgroundColor(Color.TRANSPARENT);
        }
    }
}
