package org.fossasia.openevent.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.utils.CircleTransform;

import java.util.List;

/**
 * Created by MananWason on 11-06-2015.
 */
public class SpeakersListAdapter extends RecyclerView.Adapter<SpeakersListAdapter.ViewHolder> {
    List<Speaker> speakers;

    public SpeakersListAdapter(List<Speaker> speakers) {
        this.speakers = speakers;
    }

    @Override
    public SpeakersListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.speakers_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SpeakersListAdapter.ViewHolder holder, int position) {
        Speaker current = speakers.get(position);

        Uri uri = Uri.parse(current.getPhoto());
        Picasso.with(holder.speaker_image.getContext()).load(uri).transform(new CircleTransform()).into(holder.speaker_image);

        holder.designation.setText(current.getPosition());
        holder.name.setText(current.getName());

    }

    @Override
    public int getItemCount() {
        return speakers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView speaker_image;
        TextView name;
        TextView designation;


        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            speaker_image = (ImageView) itemView.findViewById(R.id.speaker_image);
            name = (TextView) itemView.findViewById(R.id.speaker_name);
            designation = (TextView) itemView.findViewById(R.id.speaker_designation);
        }
    }
}

