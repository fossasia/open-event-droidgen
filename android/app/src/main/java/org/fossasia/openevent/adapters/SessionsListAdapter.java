package org.fossasia.openevent.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SessionDetailActivity;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.receivers.NotificationAlarmReceiver;
import org.fossasia.openevent.utils.BookmarksListChangeListener;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.ISO8601Date;
import org.fossasia.openevent.utils.WidgetUpdater;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 26-06-2015
 */
public class SessionsListAdapter extends BaseRVAdapter<Session, SessionsListAdapter.SessionViewHolder> {

    private String trackName;
    private Context context;
    public static int listPosition;
    private int type;
    private static final int locationWiseSessionList = 1;
    private static final int trackWiseSessionList = 4;
    private static final int speakerWiseSessionList = 2;

    private ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder drawableBuilder = TextDrawable.builder().round();
    private BookmarksListChangeListener bookmarksListChangeListener;

    private CompositeDisposable disposable;

    public void setBookmarksListChangeListener(BookmarksListChangeListener listener){
        this.bookmarksListChangeListener = listener;
    }

    private int color;

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

    public SessionsListAdapter(Context context, List<Session> sessions,int type) {
        super(sessions);
        this.context = context;
        this.color = ContextCompat.getColor(context, R.color.color_primary);
        this.type = type;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public void setColor(int color) {
        this.color = color;
        notifyDataSetChanged();
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
    public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.tracksactvity_item, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SessionViewHolder holder, final int position) {
        final Session session = getItem(position);
        String date = ISO8601Date.getTimeZoneDateString(
                ISO8601Date.getDateObject(session.getStartTime())).split(",")[0] + ","
                + ISO8601Date.getTimeZoneDateString(ISO8601Date.getDateObject(session.getStartTime())).split(",")[1];


        holder.sessionTitle.setText(session.getTitle());
        holder.sessionSubtitle.setText(session.getSubtitle());
        TextDrawable drawable = drawableBuilder.build(String.valueOf(session.getTrack().getName().charAt(0)), colorGenerator.getColor(session.getTrack().getName()));
        holder.trackImageIcon.setImageDrawable(drawable);
        holder.trackImageIcon.setBackgroundColor(Color.TRANSPARENT);
        holder.sessionTrack.setText(session.getTrack().getName());
        holder.sessionDate.setText(date);
        holder.sessionTime.setText(ISO8601Date.get12HourTime(ISO8601Date.getDateObject(session.getStartTime())) + " - " + ISO8601Date.get12HourTime(ISO8601Date.getDateObject(session.getEndTime())));
        holder.sessionLocation.setText(session.getMicrolocation().getName());

        final DbSingleton dbSingleton;
        dbSingleton = DbSingleton.getInstance();

        disposable.add(dbSingleton.getSpeakersbySessionNameObservable(session.getTitle())
                .map(new Function<ArrayList<Speaker>, String>() {
                    @Override
                    public String apply(@NonNull ArrayList<Speaker> speakers) throws Exception {
                        ArrayList<String> speakerName = new ArrayList<>();

                        for(Speaker speaker: speakers){
                            speakerName.add(speaker.getName());
                        }

                        if(speakers.isEmpty()){
                            holder.sessionSpeaker.setVisibility(View.GONE);
                            holder.speakerIcon.setVisibility(View.GONE);
                        }

                        return TextUtils.join(",", speakerName);
                    }
                }).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String speakerList) throws Exception {
                        holder.sessionSpeaker.setText(speakerList);
                    }
                }));

        switch (type){
            case trackWiseSessionList :
                holder.trackImageIcon.setVisibility(View.GONE);
                holder.sessionTrack.setVisibility(View.GONE);
                break;
            case locationWiseSessionList :
                holder.sessionLocation.setVisibility(View.GONE);
                holder.locationIcon.setVisibility(View.GONE);
                break;
            case speakerWiseSessionList:
                holder.sessionSpeaker.setVisibility(View.GONE);
                holder.speakerIcon.setVisibility(View.GONE);
                break;
            default:
        }

        disposable.add(dbSingleton.isBookmarkedObservable(session.getId())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean bookmarked) {
                        if(bookmarked) {
                            holder.sessionBookmarkIcon.setImageResource(R.drawable.ic_bookmark_white_24dp);
                        } else {
                            holder.sessionBookmarkIcon.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
                        }
                    }
                }));

        final Consumer<Boolean> bookmarkConsumer = new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean bookmarked) throws Exception {
                if(bookmarked) {
                    disposable.add(dbSingleton.deleteBookmarksObservable(session.getId()).subscribe());

                    holder.sessionBookmarkIcon.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
                    if (bookmarksListChangeListener != null) {
                        bookmarksListChangeListener.onChange();
                    }
                    if ("MainActivity".equals(context.getClass().getSimpleName())) {
                        Snackbar.make(holder.sessionCard, R.string.removed_bookmark, Snackbar.LENGTH_LONG)
                                .setAction(R.string.undo, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        disposable.add(dbSingleton.addBookmarksObservable(session.getId()).subscribe());
                                        holder.sessionBookmarkIcon.setImageResource(R.drawable.ic_bookmark_white_24dp);
                                        WidgetUpdater.updateWidget(context);
                                        if(bookmarksListChangeListener != null){
                                            bookmarksListChangeListener.onChange();
                                        }
                                    }
                                }).show();
                    } else {
                        Snackbar.make(holder.sessionCard, R.string.removed_bookmark, Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    createNotification(session);
                    disposable.add(dbSingleton.addBookmarksObservable(session.getId()).subscribe());
                    Snackbar.make(holder.sessionCard, R.string.added_bookmark, Snackbar.LENGTH_SHORT).show();
                    holder.sessionBookmarkIcon.setImageResource(R.drawable.ic_bookmark_white_24dp);
                }
                WidgetUpdater.updateWidget(context);
            }
        };

        holder.sessionBookmarkIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disposable.add(dbSingleton.isBookmarkedObservable(session.getId())
                        .subscribe(bookmarkConsumer));
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sessionName = session.getTitle();
                Timber.d(session.getTitle());
                disposable.add(dbSingleton.getTrackbyIdObservable(session.getTrack().getId())
                        .subscribe(new Consumer<Track>() {
                            @Override
                            public void accept(@NonNull Track track) throws Exception {
                                String trackName = track.getName();
                                Intent intent = new Intent(context, SessionDetailActivity.class);
                                intent.putExtra(ConstantStrings.SESSION, sessionName);
                                intent.putExtra(ConstantStrings.TRACK, trackName);
                                intent.putExtra(ConstantStrings.ID, session.getId());
                                listPosition = holder.getLayoutPosition();
                                context.startActivity(intent);
                            }
                        }));
            }
        });

        holder.shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String startTime = ISO8601Date.getTimeZoneDateString(ISO8601Date.getDateObject(session.getStartTime()));
                String endTime = ISO8601Date.getTimeZoneDateString(ISO8601Date.getDateObject(session.getEndTime()));
                StringBuilder shareText = new StringBuilder();
                shareText.append(String.format("Session Track: %s \nTitle: %s \nStart Time: %s \nEnd Time: %s\n",
                        session.getTrack().getName(), session.getTitle(), startTime, endTime));
                if (!session.getSummary().isEmpty()){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        shareText.append("\nSummary: ").append(Html.fromHtml(session.getSummary(), Html.FROM_HTML_MODE_LEGACY));
                    else
                        shareText.append("\nSummary: ").append(Html.fromHtml(session.getSummary()));
                }
                else{
                    shareText.append(context.getString(R.string.descriptionEmpty));
                }
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
                sendIntent.setType("text/plain");
                context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.share_links)));
            }
        });

        // Set color generated by palette on views
        holder.sessionHeader.setBackgroundColor(color);
    }

    public void refresh() {
        Timber.d("Refreshing session List from db");
        clear();
        disposable.add(DbSingleton.getInstance().getSessionbyTracksnameObservable(trackName)
                .subscribe(new Consumer<ArrayList<Session>>() {
                    @Override
                    public void accept(@NonNull ArrayList<Session> sessions) throws Exception {
                        animateTo(sessions);
                    }
                }));
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

        @BindView(R.id.shareImageIcon)
        ImageView shareIcon;

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

    private void createNotification(Session session) {

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

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }

}
