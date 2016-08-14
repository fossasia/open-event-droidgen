package org.fossasia.openevent.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.ISO8601Date;
import org.fossasia.openevent.utils.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by Manan Wason on 17/06/16.
 */
public class DayScheduleAdapter extends BaseRVAdapter<Session, ViewHolder.Viewholder> {
    @SuppressWarnings("all")
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            DbSingleton instance = DbSingleton.getInstance();
            List<Session> sessionList = instance.getSessionbyDate(eventDate);
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

    private ViewHolder.SetOnClickListener listener;

    private String eventDate;

    public DayScheduleAdapter(List<Session> sessions) {
        super(sessions);
    }

    public void setOnClickListener(ViewHolder.SetOnClickListener clickListener) {
        this.listener = clickListener;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    @Override
    public ViewHolder.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_schedule, parent, false);
        ViewHolder.Viewholder viewholder = new ViewHolder.Viewholder(view);
        viewholder.setTxtView1((TextView) view.findViewById(R.id.start_time));
        viewholder.setTxtView2((TextView) view.findViewById(R.id.slot_title));
        viewholder.setTxtView3((TextView) view.findViewById(R.id.timings));
        return viewholder;
    }

    @Override
    public void onBindViewHolder(ViewHolder.Viewholder holder, int position) {
        Session currentSession = getItem(position);
        String startTime = ISO8601Date.get12HourTime(ISO8601Date.getDateObject(currentSession.getStartTime()));
        String endTime = ISO8601Date.get12HourTime(ISO8601Date.getDateObject(currentSession.getEndTime()));

        holder.getTxtView1().setText(startTime);
        holder.getTxtView2().setText(currentSession.getTitle());
        holder.getTxtView3().setText(String.format("%s - %s", startTime, endTime));
        holder.setItemClickListener(listener);

    }

    public void refresh() {
        DbSingleton dbSingleton = DbSingleton.getInstance();
        clear();
        animateTo(dbSingleton.getSessionbyDate(eventDate));
    }

    /**
     * to handle click listener
     */
    public interface SetOnClickListener extends ViewHolder.SetOnClickListener {
        void onItemClick(int position, View itemView);

    }
}