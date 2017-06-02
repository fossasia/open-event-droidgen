package org.fossasia.openevent.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SessionDetailActivity;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.receivers.NotificationAlarmReceiver;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.ISO8601Date;
import org.fossasia.openevent.utils.WidgetUpdater;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.realm.Realm;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 26-06-2015
 */
public class SessionsListAdapter extends BaseRVAdapter<Session, SessionsListAdapter.SessionViewHolder> {

    private Context context;
    private int trackId;
    public static int listPosition;
    private int type;
    private static final int locationWiseSessionList = 1;
    private static final int trackWiseSessionList = 4;
    private static final int speakerWiseSessionList = 2;

    private boolean isBookmarkView;

    private TextDrawable.IBuilder drawableBuilder = TextDrawable.builder().round();

    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();

    private int color;

    @SuppressWarnings("all")
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            Realm realm = Realm.getDefaultInstance();

            List<Session> filteredSessions = realm.copyFromRealm(RealmDataRepository.getInstance(realm)
            .getSessionsFiltered(trackId, constraint.toString()));

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredSessions;
            filterResults.count = filteredSessions.size();
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

            animateTo((List<Session>) results.values);
        }
    };

    public SessionsListAdapter(Context context, List<Session> sessions, int type) {
        super(sessions);
        this.context = context;
        this.color = ContextCompat.getColor(context, R.color.color_primary);
        this.type = type;
    }


    public void setBookmarkView(boolean bookmarkView) {
        isBookmarkView = bookmarkView;
    }

    public void setColor(int color) {
        this.color = color;
        notifyDataSetChanged();
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
    public void onBindViewHolder(final SessionViewHolder holder, final int position) {
        final Session session = getItem(position);
        String date = ISO8601Date.getDateFromStartDateString(session.getStartTime());

        holder.sessionTitle.setText(session.getTitle());


        if(session.getSubtitle().isEmpty()) {
            holder.sessionSubtitle.setVisibility(View.GONE);
        } else {
            holder.sessionSubtitle.setVisibility(View.VISIBLE);
            holder.sessionSubtitle.setText(session.getSubtitle());
        }


        final Track track = session.getTrack();

        int storedColor = Color.parseColor(track.getColor());

        if(type != trackWiseSessionList) {
            color = storedColor;
        }

        TextDrawable drawable = drawableBuilder.build(String.valueOf(session.getTrack().getName().charAt(0)), storedColor);
        holder.trackImageIcon.setImageDrawable(drawable);
        holder.trackImageIcon.setBackgroundColor(Color.TRANSPARENT);
        holder.sessionTrack.setText(session.getTrack().getName());
        holder.sessionDate.setText(date);
        holder.sessionTime.setText(ISO8601Date.get12HourTimeFromCombinedDateString(session.getStartTime(), session.getEndTime()));
        if(session.getMicrolocation() != null)
            holder.sessionLocation.setText(session.getMicrolocation().getName());

        Observable.just(session.getSpeakers())
                .map(speakers -> {
                    ArrayList<String> speakerName = new ArrayList<>();

                    for(Speaker speaker: speakers){
                        speakerName.add(speaker.getName());
                    }

                    if (speakers.isEmpty()) {
                        holder.sessionSpeaker.setVisibility(View.GONE);
                        holder.speakerIcon.setVisibility(View.GONE);
                    }

                    return TextUtils.join(", ", speakerName);
                }).subscribe(speakerList -> holder.sessionSpeaker.setText(speakerList));

        switch (type) {
            case trackWiseSessionList:
                holder.trackImageIcon.setVisibility(View.GONE);
                holder.sessionTrack.setVisibility(View.GONE);
                break;
            case locationWiseSessionList:
                holder.sessionLocation.setVisibility(View.GONE);
                holder.locationIcon.setVisibility(View.GONE);
                break;
            case speakerWiseSessionList:
                holder.sessionSpeaker.setVisibility(View.GONE);
                holder.speakerIcon.setVisibility(View.GONE);
                break;
            default:
        }

        if(session.isBookmarked()) {
            holder.sessionBookmarkIcon.setImageResource(R.drawable.ic_bookmark_white_24dp);
        } else {
            holder.sessionBookmarkIcon.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
        }

        final int sessionId = session.getId();
        final int finalPosition = holder.getAdapterPosition();

        holder.sessionBookmarkIcon.setOnClickListener(v -> {
            if(session.isBookmarked()) {

                realmRepo.setBookmark(sessionId, false).subscribe();
                holder.sessionBookmarkIcon.setImageResource(R.drawable.ic_bookmark_border_white_24dp);

                if(isBookmarkView)
                    removeItem(session);

                if ("MainActivity".equals(context.getClass().getSimpleName())) {
                    Snackbar.make(holder.sessionCard, R.string.removed_bookmark, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, view -> {

                                realmRepo.setBookmark(sessionId, true).subscribe();
                                holder.sessionBookmarkIcon.setImageResource(R.drawable.ic_bookmark_white_24dp);

                                if(isBookmarkView)
                                    addItem(finalPosition, session);

                                WidgetUpdater.updateWidget(context);
                            }).show();
                } else {
                    Snackbar.make(holder.sessionCard, R.string.removed_bookmark, Snackbar.LENGTH_SHORT).show();
                }
            } else {
                createNotification(session);

                realmRepo.setBookmark(sessionId, true).subscribe();
                holder.sessionBookmarkIcon.setImageResource(R.drawable.ic_bookmark_white_24dp);

                if(isBookmarkView)
                    addItem(holder.getAdapterPosition(), session);

                Snackbar.make(holder.sessionCard, R.string.added_bookmark, Snackbar.LENGTH_SHORT).show();
            }
            WidgetUpdater.updateWidget(context);
        });

        holder.itemView.setOnClickListener(v -> {
            final String sessionName = session.getTitle();

            String trackName = track.getName();
            Intent intent = new Intent(context, SessionDetailActivity.class);
            intent.putExtra(ConstantStrings.SESSION, sessionName);
            intent.putExtra(ConstantStrings.TRACK, trackName);
            intent.putExtra(ConstantStrings.ID, session.getId());
            intent.putExtra(ConstantStrings.TRACK_ID, track.getId());
            listPosition = holder.getLayoutPosition();
            context.startActivity(intent);
        });

        // Set color generated by palette on views
        holder.sessionHeader.setBackgroundColor(color);
    }

    private void createNotification(Session session) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ISO8601Date.getTimeZoneDateFromString(session.getStartTime()));

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

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    class SessionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.session_title)
        TextView sessionTitle;

        @BindView(R.id.session_subtitle)
        TextView sessionSubtitle;

        @BindView(R.id.trackImageDrawable)
        ImageView trackImageIcon;

        @BindView(R.id.session_track)
        TextView sessionTrack;

        @BindView(R.id.session_date)
        TextView sessionDate;

        @BindView(R.id.session_speaker)
        TextView sessionSpeaker;

        @BindView(R.id.icon_speaker)
        ImageView speakerIcon;

        @BindView(R.id.icon_location)
        ImageView locationIcon;

        @BindView(R.id.session_time)
        TextView sessionTime;

        @BindView(R.id.session_location)
        TextView sessionLocation;

        @BindView(R.id.session_bookmark_status)
        ImageView sessionBookmarkIcon;

        @BindView(R.id.session_details)
        LinearLayout sessionDetailsHolder;

        @BindView(R.id.session_card)
        CardView sessionCard;

        @BindView(R.id.titleLinearLayout)
        LinearLayout sessionHeader;

        SessionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
