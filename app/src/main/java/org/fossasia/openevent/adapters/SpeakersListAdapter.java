package org.fossasia.openevent.adapters;

import android.net.Uri;
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
import org.fossasia.openevent.utils.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * User: MananWason
 * Date: 11-06-2015
 */
public class SpeakersListAdapter extends BaseRVAdapter<Speaker, ViewHolder.Viewholder> {

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
    private ViewHolder.SetOnClickListener listener;

    public SpeakersListAdapter(List<Speaker> speakers) {
        super(speakers);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public void setOnClickListener(ViewHolder.SetOnClickListener clickListener) {
        this.listener = clickListener;
    }

    @Override
    public ViewHolder.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_speaker, parent, false);
        ViewHolder.Viewholder viewholder = new ViewHolder.Viewholder(view);

        viewholder.setImgView1((ImageView) view.findViewById(R.id.speaker_image));
        viewholder.setTxtView1((TextView) view.findViewById(R.id.speaker_name));
        viewholder.setTxtView2((TextView) view.findViewById(R.id.speaker_designation));

        return viewholder;
    }

    @Override
    public void onBindViewHolder(ViewHolder.Viewholder holder, int position) {
        Speaker current = getItem(position);

        Uri uri = Uri.parse(current.getPhoto());
        Picasso.with(holder.getImgView1().getContext()).load(uri)
                .placeholder(R.drawable.ic_account_circle_grey_24dp).transform(new CircleTransform()).into(holder.getImgView1());

        holder.getTxtView2().setText(current.getPosition());
        holder.getTxtView1().setText(TextUtils.isEmpty(current.getName()) ? "" : current.getName());

        holder.setItemClickListener(listener);
    }

    public void refresh() {
        DbSingleton dbSingleton = DbSingleton.getInstance();
        clear();
        animateTo(dbSingleton.getSpeakerList());
    }

    /**
     * to handle click listener
     */
    public interface SetOnClickListener extends ViewHolder.SetOnClickListener {
        void onItemClick(int position, View itemView);
    }
}
