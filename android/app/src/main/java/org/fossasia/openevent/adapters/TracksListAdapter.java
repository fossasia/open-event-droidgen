package org.fossasia.openevent.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.TrackSessionsActivity;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.ConstantStrings;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 07-06-2015
 */
public class TracksListAdapter extends BaseRVAdapter<Track, TracksListAdapter.RecyclerViewHolder> {

    private Context context;
    private ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder drawableBuilder = TextDrawable.builder().round();
    @SuppressWarnings("all")
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            DbSingleton instance = DbSingleton.getInstance();
            List<Track> trackList = instance.getTrackList();
            final ArrayList<Track> filteredTracksList = new ArrayList<>();
            String query = constraint.toString().toLowerCase(Locale.getDefault());
            for (Track track : trackList) {
                final String text = track.getName().toLowerCase(Locale.getDefault());
                if (text.contains(query)) {
                    filteredTracksList.add(track);
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredTracksList;
            filterResults.count = filteredTracksList.size();
            Timber.d("Filtering done total results %d", filterResults.count);
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            animateTo((List<Track>) results.values);
        }
    };

    public TracksListAdapter(Context context, List<Track> tracks) {
        super(tracks);
        this.context = context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_track, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        final Track currentTrack = getItem(position);

        DbSingleton.getInstance().getSessionbyTracksnameObservable(currentTrack.getName())
                .subscribe(new Consumer<ArrayList<Session>>() {
                    @Override
                    public void accept(@NonNull ArrayList<Session> sessions) throws Exception {
                        if(!sessions.isEmpty()) {
                            holder.trackTitle.setText(currentTrack.getName());
                            holder.trackDescription.setText(currentTrack.getDescription());

                            TextDrawable drawable = drawableBuilder.build(String.valueOf(currentTrack.getName().charAt(0)), colorGenerator.getColor(currentTrack.getName()));
                            holder.trackImageIcon.setImageDrawable(drawable);
                            holder.trackImageIcon.setBackgroundColor(Color.TRANSPARENT);

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String trackTitle = currentTrack.getName();
                                    Intent intent = new Intent(context, TrackSessionsActivity.class);
                                    intent.putExtra(ConstantStrings.TRACK, trackTitle);
                                    context.startActivity(intent);
                                }
                            });
                        }
                    }
                });


    }

    public void refresh() {
        Timber.d("Refreshing tracks from db");
        clear();
        DbSingleton.getInstance().getTrackListObservable()
                .subscribe(new Consumer<List<Track>>() {
                    @Override
                    public void accept(@NonNull List<Track> tracks) throws Exception {
                        animateTo(tracks);
                    }
                });
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    protected class RecyclerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageView)
        ImageView trackImageIcon;

        @BindView(R.id.track_title)
        TextView trackTitle;

        @BindView(R.id.track_description)
        TextView trackDescription;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
