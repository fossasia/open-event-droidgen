package org.fossasia.openevent.adapters.viewholders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.TrackSessionsActivity;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrackViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.imageView)
    protected ImageView trackImageIcon;

    @BindView(R.id.track_title)
    protected TextView trackTitle;

    private Track track;
    private TextDrawable.IBuilder drawableBuilder = TextDrawable.builder().round();

    public TrackViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        Context applicationContext = context.getApplicationContext();

        //Attach onClickListener for ViewHolder
        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, TrackSessionsActivity.class);
            intent.putExtra(ConstantStrings.TRACK, track.getName());
            intent.putExtra(ConstantStrings.TRACK_ID, track.getId());
            applicationContext.startActivity(intent);
        });
    }

    public void bindTrack(Track track) {
        this.track = track;

        int trackColor = Color.parseColor(track.getColor());
        String trackName = Utils.checkStringEmpty(track.getName());

        trackTitle.setText(trackName);
        if(!Utils.isEmpty(trackName)) {
            TextDrawable drawable = drawableBuilder.build(String.valueOf(trackName.charAt(0)), trackColor);
            trackImageIcon.setImageDrawable(drawable);
            trackImageIcon.setBackgroundColor(Color.TRANSPARENT);
        }
    }
}
