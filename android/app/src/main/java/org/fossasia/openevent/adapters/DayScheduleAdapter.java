package org.fossasia.openevent.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SessionDetailActivity;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.ISO8601Date;
import org.fossasia.openevent.utils.SortOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Manan Wason on 17/06/16.
 */
public class DayScheduleAdapter extends BaseRVAdapter<Session, DayScheduleAdapter.DayScheduleViewHolder> {

    private Context context;
    private String eventDate;

    @SuppressWarnings("all")
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            DbSingleton instance = DbSingleton.getInstance();
            List<Session> sessionList = instance.getSessionbyDate(eventDate, SortOrder.sortOrderSchedule(context));
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
        String startTime = ISO8601Date.get24HourTime(ISO8601Date.getDateObject(currentSession.getStartTime()));
        String endTime = ISO8601Date.get24HourTime(ISO8601Date.getDateObject(currentSession.getEndTime()));

        holder.startTime.setText(startTime);
        holder.endTime.setText(endTime);
        holder.slotTitle.setText(currentSession.getTitle());
        if(currentSession.getDescription().isEmpty()){
            holder.slotDescription.setVisibility(View.GONE);
        }else {
            holder.slotDescription.setText(currentSession.getDescription());
        }
        holder.slotLocation.setText(currentSession.getMicrolocation().getName().toString());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sessionName = currentSession.getTitle();
                Track track = DbSingleton.getInstance().getTrackbyId(currentSession.getTrack().getId());
                String trackName = track.getName();
                Intent intent = new Intent(context, SessionDetailActivity.class);
                intent.putExtra(ConstantStrings.SESSION, sessionName);
                intent.putExtra(ConstantStrings.TRACK, trackName);
                intent.putExtra(ConstantStrings.ID, currentSession.getId());
                context.startActivity(intent);
            }
        });
    }

    public void refresh() {
        clear();
        animateTo(DbSingleton.getInstance().getSessionbyDate(eventDate, SortOrder.sortOrderSchedule(context)));
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    protected class DayScheduleViewHolder extends RecyclerView.ViewHolder {

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


        public DayScheduleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}