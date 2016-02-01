package org.fossasia.openevent.adapters;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.CircleTransform;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * User: MananWason
 * Date: 11-06-2015
 */
public class SpeakersListAdapter extends BaseRVAdapter<Speaker, SpeakersListAdapter.ViewHolder> {

    @SuppressWarnings("all")
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            DbSingleton instance = DbSingleton.getInstance();
            List<Speaker> trackList = instance.getSpeakerList();
            final ArrayList<Speaker> filteredSpeakerList = new ArrayList<>();
            String query = constraint.toString().toLowerCase();
            for (Speaker speaker : trackList) {
                final String text = speaker.getName().toLowerCase();
                if (text.contains(query)) {
                    filteredSpeakerList.add(speaker);
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredSpeakerList;
            filterResults.count = filteredSpeakerList.size();
            Timber.d("Speaker filtering done total results %d", filterResults.count);
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            animateTo((List<Speaker>) results.values);
        }
    };

    public SpeakersListAdapter(List<Speaker> speakers) {
        super(speakers);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public SpeakersListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_speaker, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SpeakersListAdapter.ViewHolder holder, int position) {
        Speaker current = getItem(position);

        Uri uri = Uri.parse(current.getPhoto());
        Picasso.with(holder.speakerImage.getContext())
                .load(uri)
                .placeholder(R.drawable.ic_account_circle_grey_24dp)
                .transform(new CircleTransform())
                .into(holder.speakerImage);

        holder.designation.setText(current.getPosition());
        holder.name.setText(TextUtils.isEmpty(current.getName()) ? "" : current.getName());
    }

    public void refresh() {
        DbSingleton dbSingleton = DbSingleton.getInstance();
        clear();
        animateTo(dbSingleton.getSpeakerList());
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView speakerImage;

        TextView name;

        TextView designation;


        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            speakerImage = (ImageView) itemView.findViewById(R.id.speaker_image);
            name = (TextView) itemView.findViewById(R.id.speaker_name);
            designation = (TextView) itemView.findViewById(R.id.speaker_designation);
        }
    }
}

