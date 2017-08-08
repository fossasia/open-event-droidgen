package org.fossasia.openevent.adapters.items;

import android.graphics.Color;
import android.view.View;

import com.amulyakhare.textdrawable.TextDrawable;
import com.mikepenz.fastadapter.items.GenericAbstractItem;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.viewholders.TrackViewHolder;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.utils.Utils;

import java.util.List;

public class TrackItem extends GenericAbstractItem<Track, TrackItem, TrackViewHolder> {

    public TrackItem(Track track) {
        super(track);
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_track;
    }

    @Override
    public void bindView(TrackViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        int trackColor = Color.parseColor(getModel().getColor());
        String trackName = Utils.checkStringEmpty(getModel().getName());

        holder.trackTitle.setText(trackName);
        if (!Utils.isEmpty(trackName)) {
            TextDrawable drawable = OpenEventApp.getTextDrawableBuilder().build(String.valueOf(trackName.charAt(0)), trackColor);
            holder.trackImageIcon.setImageDrawable(drawable);
            holder.trackImageIcon.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public TrackViewHolder getViewHolder(View v) {
        return new TrackViewHolder(v);
    }
}
