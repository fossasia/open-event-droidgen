package org.fossasia.openevent.adapters;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SessionDetailActivity;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.receivers.NotificationAlarmReceiver;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.ISO8601Date;
import org.fossasia.openevent.widget.BookmarkWidgetProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 26-06-2015
 */
public class SessionsListAdapter extends BaseRVAdapter<Session, SessionsListAdapter.SessionViewHolder> {

    private String trackName;
    private Context context;
    public static int listPosition;

    @SuppressWarnings("all")
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            DbSingleton instance = DbSingleton.getInstance();
            // TODO: Use a query to do this, iterating over an entire set is pretty bad
            List<Session> sessionList = instance.getSessionbyTracksname(trackName);
            final ArrayList<Session> filteredSessionList = new ArrayList<>();
            String query = constraint.toString().toLowerCase(Locale.getDefault());
            for (Session session : sessionList) {

                final String text = session.getTitle().toLowerCase(Locale.getDefault());
                if (text.contains(query)) {
                    filteredSessionList.add(session);
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredSessionList;
            filterResults.count = filteredSessionList.size();
            Timber.d("Filtering done total results %d", filterResults.count);
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            animateTo((List<Session>) results.values);
        }
    };

    public SessionsListAdapter(Context context, List<Session> sessions) {
        super(sessions);
        this.context = context;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.tracksactvity_item, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SessionViewHolder holder, int position) {
        final Session session = getItem(position);
        String date = ISO8601Date.getTimeZoneDateString(
                ISO8601Date.getDateObject(session.getStartTime())).split(",")[0] + ","
                + ISO8601Date.getTimeZoneDateString(ISO8601Date.getDateObject(session.getStartTime())).split(",")[1];

        final View sessionDetailsHolder = holder.sessionDetailsHolder;
        final View sessionCard = holder.sessionCard;

        holder.sessionTitle.setText(session.getTitle());
        holder.sessionSubtitle.setText(session.getSubtitle());
        holder.sessionTrack.setText(session.getTrack().getName());
        holder.sessionDate.setText(date);
        holder.sessionStartTime.setText(ISO8601Date.get12HourTime(ISO8601Date.getDateObject(session.getStartTime())));
        holder.sessionLocation.setText(session.getMicrolocation().getName());
        DbSingleton dbSingleton;
        dbSingleton = DbSingleton.getInstance();
        if(!dbSingleton.isBookmarked(session.getId()))
            holder.sessionImage.setImageResource(R.drawable.ic_bookmark_outline_black_24dp);
        else
            holder.sessionImage.setImageResource(R.drawable.ic_bookmark_grey600_24dp);
        holder.sessionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbSingleton dbSingleton;
                dbSingleton = DbSingleton.getInstance();
                if (dbSingleton.isBookmarked(session.getId())) {
                    dbSingleton.deleteBookmarks(session.getId());
                    holder.sessionImage.setImageResource(R.drawable.ic_bookmark_outline_black_24dp);
                } else {
                    createNotification(session);
                    dbSingleton.addBookmarks(session.getId());
                    holder.sessionImage.setImageResource(R.drawable.ic_bookmark_grey600_24dp);
                }
                context.sendBroadcast(new Intent(BookmarkWidgetProvider.ACTION_UPDATE));
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sessionName = session.getTitle();
                Timber.d(session.getTitle());
                Track track = DbSingleton.getInstance().getTrackbyId(session.getTrack().getId());
                String trackName = track.getName();
                Intent intent = new Intent(context, SessionDetailActivity.class);
                intent.putExtra(ConstantStrings.SESSION, sessionName);
                intent.putExtra(ConstantStrings.TRACK, trackName);
                listPosition = holder.getLayoutPosition();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Activity activity = (Activity) context;

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                            Pair.create(sessionDetailsHolder, "session"),
                            Pair.create(sessionCard, "sessionBackground"));
//                    activity.getWindow().setEnterTransition(null);
                    context.startActivity(intent, options.toBundle());
                } else {
                    context.startActivity(intent);
                }
            }
        });
    }

    public void refresh() {
        Timber.d("Refreshing session List from db");
        clear();
        animateTo(DbSingleton.getInstance().getSessionbyTracksname(trackName));
    }

    protected class SessionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.session_title)
        protected TextView sessionTitle;

        @BindView(R.id.session_subtitle)
        protected TextView sessionSubtitle;

        @BindView(R.id.session_track)
        protected TextView sessionTrack;

        @BindView(R.id.session_date)
        protected TextView sessionDate;

        @BindView(R.id.session_start_time)
        protected TextView sessionStartTime;

        @BindView(R.id.session_location)
        protected TextView sessionLocation;

        @BindView(R.id.session_bookmark_status)
        protected ImageView sessionImage;

        @BindView(R.id.session_details)
        protected LinearLayout sessionDetailsHolder;

        @BindView(R.id.session_card)
        protected CardView sessionCard;

        public SessionViewHolder(View itemView) {
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
