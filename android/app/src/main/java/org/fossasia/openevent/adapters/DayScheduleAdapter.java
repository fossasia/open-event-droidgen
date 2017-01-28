package org.fossasia.openevent.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SessionDetailActivity;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.receivers.NotificationAlarmReceiver;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.ISO8601Date;
import org.fossasia.openevent.utils.SortOrder;
import org.fossasia.openevent.widget.BookmarkWidgetProvider;

import java.util.ArrayList;
import java.util.Calendar;
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
    public void onBindViewHolder(DayScheduleViewHolder holder1, int position) {
        final DayScheduleViewHolder holder= holder1;
        final Session currentSession = getItem(position);
        String startTime = ISO8601Date.get12HourTime(ISO8601Date.getDateObject(currentSession.getStartTime()));
        String endTime = ISO8601Date.get12HourTime(ISO8601Date.getDateObject(currentSession.getEndTime()));

        holder.startTime.setText(startTime);
        holder.slotTitle.setText(currentSession.getTitle());
        holder.timings.setText(String.format("%s - %s", startTime, endTime));

        DbSingleton dbSingleton;
        dbSingleton = DbSingleton.getInstance();
        if (!dbSingleton.isBookmarked(currentSession.getId()))
            holder.dayImage.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
        else
            holder.dayImage.setImageResource(R.drawable.ic_bookmark_white_24dp);
        holder.dayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DbSingleton dbSingleton;
                dbSingleton = DbSingleton.getInstance();
                if (dbSingleton.isBookmarked(currentSession.getId())) {
                    new AlertDialog.Builder(context, R.style.AlertDialogCustom)
                            .setTitle("Remove Bookmark")
                            .setMessage("Are you sure you want to remove this event from your bookmarks?")
                            .setPositiveButton("YES",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dbSingleton.deleteBookmarks(currentSession.getId());
                                            holder.dayImage.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
                                            Toast.makeText(context, R.string.removed_bookmark, Toast.LENGTH_SHORT).show();
                                        }
                                    })
                            .setNegativeButton("CANCEL",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Do nothing, just close
                                        }
                                    }).create().show();

                } else {
                    createNotification(currentSession);
                    dbSingleton.addBookmarks(currentSession.getId());
                    holder.dayImage.setImageResource(R.drawable.ic_bookmark_white_24dp);
                    context.sendBroadcast(new Intent(BookmarkWidgetProvider.ACTION_UPDATE));
                }
            }
        });
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

        @BindView(R.id.start_time)
        protected TextView startTime;

        @BindView(R.id.slot_title)
        protected TextView slotTitle;

        @BindView(R.id.timings)
        protected TextView timings;

        @BindView(R.id.day_bookmark_status)
        protected ImageView dayImage;

        public DayScheduleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public void createNotification(Session session) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ISO8601Date.getTimeZoneDate(ISO8601Date.getDateObject(session.getStartTime())));

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Integer pref_result = Integer.parseInt(sharedPrefs.getString("notification", "10 mins").substring(0, 2).trim());
        if (pref_result.equals(1)) {
            calendar.add(Calendar.HOUR, -1);
        } else if (pref_result.equals(12)) {
            calendar.add(Calendar.HOUR, -12);
        } else {
            calendar.add(Calendar.MINUTE, -10);
        }
        Intent myIntent = new Intent(context, NotificationAlarmReceiver.class);
        myIntent.putExtra(ConstantStrings.SESSION, session.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }
}