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

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.TrackSessionsActivity;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.views.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 07-06-2015
 */
public class TracksListAdapter extends BaseRVAdapter<Track, TracksListAdapter.RecyclerViewHolder> implements StickyRecyclerHeadersAdapter {

    private Context context;
    private TextDrawable.IBuilder drawableBuilder = TextDrawable.builder().round();

    @SuppressWarnings("all")
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            final String query = constraint.toString().toLowerCase(Locale.getDefault());

            Realm realm = Realm.getDefaultInstance();

            List<Track> filteredTracks = realm.copyFromRealm(RealmDataRepository.getInstance(realm)
                    .getTracksFiltered(constraint.toString()));

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredTracks;
            filterResults.count = filteredTracks.size();
            Timber.d("Filtering done total results %d", filterResults.count);

            realm.close();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(results == null || results.values == null) {
                Timber.e("No results published. There is an error in query. Check " + getClass().getName() + " filter!");

                return;
            }

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
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        final Track currentTrack = getItem(position);

        holder.trackTitle.setText(currentTrack.getName());

        int trackColor = Color.parseColor(currentTrack.getColor());
        TextDrawable drawable = drawableBuilder.build(String.valueOf(currentTrack.getName().charAt(0)), trackColor);
        holder.trackImageIcon.setImageDrawable(drawable);
        holder.trackImageIcon.setBackgroundColor(Color.TRANSPARENT);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TrackSessionsActivity.class);
            intent.putExtra(ConstantStrings.TRACK, currentTrack.getName());

            intent.putExtra(ConstantStrings.TRACK_ID, currentTrack.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public long getHeaderId(int position) {
        return getItem(position).getName().charAt(0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_header, parent, false);
        return new RecyclerView.ViewHolder(view) {};
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView.findViewById(R.id.recyclerview_view_header);
        textView.setText(String.valueOf(getItem(position).getName().charAt(0)));
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageView)
        ImageView trackImageIcon;

        @BindView(R.id.track_title)
        TextView trackTitle;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
