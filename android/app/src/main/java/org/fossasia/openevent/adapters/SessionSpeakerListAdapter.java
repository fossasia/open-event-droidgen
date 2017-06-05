package org.fossasia.openevent.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SpeakerDetailsActivity;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.utils.CircleTransform;
import org.fossasia.openevent.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SessionSpeakerListAdapter extends BaseRVAdapter<Speaker, SessionSpeakerListAdapter.RecyclerViewHolder> {

    private Activity activity;

    public SessionSpeakerListAdapter(List<Speaker> speakers, Activity activity) {
        super(speakers);
        this.activity = activity;
    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session_speaker, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        final Speaker current = getItem(position);

        String thumbnail = Utils.parseImageUri(current.getThumbnail());
        if (thumbnail == null)
            thumbnail = Utils.parseImageUri(current.getPhoto());
        Drawable placeholder = VectorDrawableCompat.create(activity.getResources(), R.drawable.ic_account_circle_grey_24dp, null);

        if(thumbnail != null) {
            Picasso.with(holder.speakerImage.getContext())
                    .load(Uri.parse(thumbnail))
                    .placeholder(placeholder)
                    .transform(new CircleTransform())
                    .into(holder.speakerImage);
        } else {
            holder.speakerImage.setImageDrawable(placeholder);
        }

        String name = current.getName();
        name = TextUtils.isEmpty(name) ? "" : name;

        String positionString = current.getPosition();
        positionString = TextUtils.isEmpty(positionString) ? "" : positionString;

        holder.speakerName.setText(name);
        holder.speakerDesignation.setText(String.format("%s %s", positionString, current.getOrganisation()));

        holder.itemView.setOnClickListener(v -> {
            String speakerName = current.getName();
            Intent intent = new Intent(activity, SpeakerDetailsActivity.class);
            intent.putExtra(Speaker.SPEAKER, speakerName);
            activity.startActivity(intent);
        });
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.speakers_list_image)
        ImageView speakerImage;

        @BindView(R.id.speakers_list_name)
        TextView speakerName;

        @BindView(R.id.speakers_list_designation)
        TextView speakerDesignation;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}