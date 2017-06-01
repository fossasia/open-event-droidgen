package org.fossasia.openevent.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SessionDetailActivity;
import org.fossasia.openevent.activities.TrackSessionsActivity;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.parsingExtra.Track;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.ISO8601Date;
import org.fossasia.openevent.utils.SortOrder;
import org.fossasia.openevent.utils.TrackColors;
import org.fossasia.openevent.views.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

/**
 * Created by Manan Wason on 17/06/16.
 */
public class DayScheduleAdapter extends BaseRVAdapter<Session, DayScheduleAdapter.DayScheduleViewHolder> implements StickyRecyclerHeadersAdapter {

    private Context context;
    private String eventDate;
    private final List<Session> sessionList = new ArrayList<>();
    private CompositeDisposable disposable;

    private ArrayList<String> tracks;

    @SuppressWarnings("all")
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            sessionList.clear();
            for (int i=0 ; i<getItemCount();i++) {
                sessionList.add(getItem(i));
            }
            final ArrayList<Session> filteredSessionsList = new ArrayList<>();
            String query = constraint.toString().toLowerCase(Locale.getDefault());
            for (Session session : sessionList) {
                final String text = session.getTitle().toLowerCase(Locale.getDefault());
                if (text.contains(query)) {
                    filteredSessionsList.add(session);
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredSessionsList;
            filterResults.count = filteredSessionsList.size();
            Timber.d("Filtering done total results %d", filterResults.count);
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            animateTo((List<Session>) results.values);
        }
    };

    public DayScheduleAdapter(List<Session> sessions, Context context) {
        super(sessions);
        this.context = context;
        tracks  = new ArrayList<String>();
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    @Override
    public DayScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_schedule, parent, false);
        return new DayScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DayScheduleViewHolder holder, int position) {
        final Session currentSession = getItem(position);
        String startTime = ISO8601Date.get12HourTime(ISO8601Date.getDateObject(currentSession.getStartTime()));
        String endTime = ISO8601Date.get12HourTime(ISO8601Date.getDateObject(currentSession.getEndTime()));

        holder.startTime.setText(startTime);
        holder.endTime.setText(endTime);
        holder.slotTitle.setText(currentSession.getTitle());
        if (currentSession.getSummary().isEmpty()) {
            holder.slotDescription.setVisibility(View.GONE);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                holder.slotDescription.setText(Html.fromHtml(currentSession.getSummary(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                holder.slotDescription.setText(Html.fromHtml(currentSession.getSummary()));
            }
        }
        holder.slotLocation.setText(currentSession.getMicrolocation().getName());

        Track currentSessionTrack = currentSession.getTrack();
        int currentSessionTrackId = currentSessionTrack.getId();
        int color = TrackColors.getColor(currentSessionTrackId);

        // Color is not in cache
        if(color == -1) {
            disposable.add(DbSingleton.getInstance().getTrackByNameObservable(currentSessionTrack.getName())
                    .subscribe(track1 -> {
                        int color1 = Color.parseColor(track1.getColor());
                        holder.slotTrack.getBackground().setColorFilter(color1, PorterDuff.Mode.SRC_ATOP);
                        TrackColors.storeColor(track1.getId(), color1);
                    }));
        } else {
            holder.slotTrack.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            Timber.d("Cached color loaded for ID %d", currentSessionTrackId);
        }

        holder.slotTrack.setText(currentSessionTrack.getName());
        holder.slotTrack.setOnClickListener(v -> {
            Intent intent = new Intent(context, TrackSessionsActivity.class);
            intent.putExtra(ConstantStrings.TRACK, currentSessionTrack.getName());

            // Send Track ID to Activity to leverage color cache
            intent.putExtra(ConstantStrings.TRACK_ID, currentSession.getId());
            context.startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {
            final String sessionName = currentSession.getTitle();

            disposable.add(DbSingleton.getInstance().getTrackByIdObservable(currentSession.getTrack().getId())
                    .subscribe(track -> {
                        String trackName = track.getName();
                        Intent intent = new Intent(context, SessionDetailActivity.class);
                        intent.putExtra(ConstantStrings.SESSION, sessionName);
                        intent.putExtra(ConstantStrings.TRACK, trackName);
                        intent.putExtra(ConstantStrings.ID, currentSession.getId());
                        context.startActivity(intent);
                    }));
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        disposable = new CompositeDisposable();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if(disposable != null && !disposable.isDisposed())
            disposable.dispose();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public long getHeaderId(int position) {
        String id = "";
        if (SortOrder.sortOrderSchedule(context).equals(DbContract.Sessions.TITLE)) {
            return getItem(position).getTitle().charAt(0);
        } else if (SortOrder.sortOrderSchedule(context).equals(DbContract.Sessions.TRACK)){
            if (tracks != null && !tracks.contains(getItem(position).getTrack().getName())) {
                tracks.add(getItem(position).getTrack().getName());
            }
            return tracks.indexOf(getItem(position).getTrack().getName());
        }
        else if (SortOrder.sortOrderSchedule(context).equals(DbContract.Sessions.START_TIME)) {
            id = ISO8601Date.get24HourTime(ISO8601Date.getDateObject(getItem(position).getStartTime()));
            id = id.replace(":", "");
            id = id.replace(" ", "");
        }
        return Long.valueOf(id);
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
        
        if (SortOrder.sortOrderSchedule(context).equals(DbContract.Sessions.TITLE)) {
            textView.setText(String.valueOf(getItem(position).getTitle().charAt(0)));
        } else if (SortOrder.sortOrderSchedule(context).equals(DbContract.Sessions.TRACK)){
            textView.setText(String.valueOf(getItem(position).getTrack().getName()));
        }
        else if (SortOrder.sortOrderSchedule(context).equals(DbContract.Sessions.START_TIME)) {
            textView.setText(ISO8601Date.get12HourTime(ISO8601Date.getDateObject(getItem(position).getStartTime())));
        }
    }

    class DayScheduleViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.slot_start_time)
        TextView startTime;

        @BindView(R.id.slot_end_time)
        TextView endTime;

        @BindView(R.id.slot_title)
        TextView slotTitle;

        @BindView(R.id.slot_description)
        TextView slotDescription;

        @BindView(R.id.slot_location)
        TextView slotLocation;

        @BindView(R.id.slot_track)
        TextView slotTrack;

        DayScheduleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}