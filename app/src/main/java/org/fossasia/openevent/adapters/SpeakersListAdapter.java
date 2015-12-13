package org.fossasia.openevent.adapters;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.DbSingleton;
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
        View view = layoutInflater.inflate(R.layout.item_speaker, parent, false);
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

    public void refresh() {
        DbSingleton dbSingleton = DbSingleton.getInstance();
        speakers.clear();
        speakers = dbSingleton.getSpeakerList();
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return speakers.size();
    }

    public void animateTo(List<Speaker> speakers) {
        applyAndAnimateRemovals(speakers);
        applyAndAnimateAdditions(speakers);
        applyAndAnimateMovedItems(speakers);
    }

    private void applyAndAnimateRemovals(List<Speaker> newSpeakers) {
        for (int i = speakers.size() - 1; i >= 0; i--) {
            final Speaker speaker = speakers.get(i);
            if (!newSpeakers.contains(speaker)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Speaker> newSpeakers) {
        for (int i = 0, count = newSpeakers.size(); i < count; i++) {
            final Speaker speaker = newSpeakers.get(i);
            if (!speakers.contains(speaker)) {
                addItem(i, speaker);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Speaker> newSpeakers) {
        for (int toPosition = newSpeakers.size() - 1; toPosition >= 0; toPosition--) {
            final Speaker speaker = newSpeakers.get(toPosition);
            final int fromPosition = speakers.indexOf(speaker);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public Speaker removeItem(int position) {
        final Speaker speaker = speakers.remove(position);
        notifyItemRemoved(position);
        return speaker;
    }

    public void addItem(int position, Speaker speaker) {
        speakers.add(position, speaker);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Speaker speaker = speakers.remove(fromPosition);
        speakers.add(toPosition, speaker);
        notifyItemMoved(fromPosition, toPosition);
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

