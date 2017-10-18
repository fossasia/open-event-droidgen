package org.fossasia.openevent.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SessionDetailActivity;
import org.fossasia.openevent.adapters.viewholders.SessionViewHolder;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.DateConverter;
import org.fossasia.openevent.utils.DateService;
import org.fossasia.openevent.utils.NotificationUtil;
import org.fossasia.openevent.utils.Utils;
import org.fossasia.openevent.utils.WidgetUpdater;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 26-06-2015
 */
public class SessionsListAdapter extends BaseRVAdapter<Session, SessionViewHolder> {

    private Context context;
    private int trackId;
    public static int listPosition;
    private int type;
    private static final int locationWiseSessionList = 1;
    private static final int trackWiseSessionList = 4;
    private static final int speakerWiseSessionList = 2;

    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();
    private List<Session> copyOfSessions = new ArrayList<>();

    private int color;

    public SessionsListAdapter(Context context, List<Session> sessions, int type) {
        super(sessions);
        this.copyOfSessions = sessions;
        this.context = context;
        this.color = ContextCompat.getColor(context, R.color.color_primary);
        this.type = type;
    }

    public void setCopyOfSessions(List<Session> sessions) {
        this.copyOfSessions = sessions;
    }

    public void filter(String constraint) {
        final String query = constraint.toLowerCase(Locale.getDefault());

        List<Session> filteredSessionsList = Observable.fromIterable(copyOfSessions)
                .filter(session -> session.getTitle()
                        .toLowerCase(Locale.getDefault())
                        .contains(query))
                .toList().blockingGet();

        Timber.d("Filtering done total results %d", filteredSessionsList.size());

        if (filteredSessionsList.isEmpty()) {
            Timber.e("No results published. There is an error in query. Check " + getClass().getName() + " filter!");
        }

        animateTo(filteredSessionsList);
    }

    public void setColor(int color) {
        this.color = color;
        notifyDataSetChanged();
    }

    @Override
    public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.tracksactvity_item, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SessionViewHolder holder, final int position) {
        Session session = getItem(position);
        //removing draft sessions
        if ((!Utils.isEmpty(session.getState())) && session.getState().equals("draft")) {
            getDataList().remove(position);
            notifyItemRemoved(position);
        }

        String sessionTitle = Utils.checkStringEmpty(session.getTitle());
        String sessionSubTitle = Utils.checkStringEmpty(session.getSubtitle());

        holder.sessionTitle.setText(sessionTitle);

        if (Utils.isEmpty(sessionSubTitle)) {
            holder.sessionSubtitle.setVisibility(View.GONE);
        } else {
            holder.sessionSubtitle.setVisibility(View.VISIBLE);
            holder.sessionSubtitle.setText(sessionSubTitle);
        }
        holder.sessionStatus.setVisibility(View.GONE);

        ZonedDateTime start = DateConverter.getDate(session.getStartsAt());
        ZonedDateTime end = DateConverter.getDate((session.getEndsAt()));
        ZonedDateTime current = ZonedDateTime.now();
        if (DateService.isUpcomingSession(start, end, current)) {
            holder.sessionStatus.setVisibility(View.VISIBLE);
            holder.sessionStatus.setText("UPCOMING");
        } else if (DateService.isOngoingSession(start, end, current)) {
            holder.sessionStatus.setVisibility(View.VISIBLE);
            holder.sessionStatus.setText("ONGOING");
        }

        Track track = session.getTrack();

        if (!RealmDataRepository.isNull(track)) {
            int storedColor = Color.parseColor(track.getColor());

            if (type != trackWiseSessionList) {
                color = storedColor;
            }

            TextDrawable drawable = OpenEventApp.getTextDrawableBuilder().round().build(String.valueOf(track.getName().charAt(0)), storedColor);
            holder.trackImageIcon.setImageDrawable(drawable);
            holder.trackImageIcon.setBackgroundColor(Color.TRANSPARENT);
            holder.sessionTrack.setText(track.getName());

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
        } else {
            holder.trackImageIcon.setVisibility(View.GONE);
            holder.sessionTrack.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(v -> {
                final String sessionName = session.getTitle();

                Intent intent = new Intent(context, SessionDetailActivity.class);
                intent.putExtra(ConstantStrings.SESSION, sessionName);
                intent.putExtra(ConstantStrings.ID, session.getId());
                listPosition = holder.getLayoutPosition();
                context.startActivity(intent);
            });

            Timber.d("This session has a null or incomplete track somehow : " + session.getTitle() + " " + track);
        }

        String date = DateConverter.formatDateWithDefault(DateConverter.FORMAT_DATE_COMPLETE, session.getStartsAt());
        holder.sessionDate.setText(date);
        holder.sessionTime.setText(String.format("%s - %s",
                DateConverter.formatDateWithDefault(DateConverter.FORMAT_12H, session.getStartsAt()),
                DateConverter.formatDateWithDefault(DateConverter.FORMAT_12H, session.getEndsAt())));
        if(session.getMicrolocation() != null) {
            String locationName = Utils.checkStringEmpty(session.getMicrolocation().getName());
            holder.sessionLocation.setText(locationName);
        }

        Observable.just(session.getSpeakers())
                .map(speakers -> {
                    ArrayList<String> speakerName = new ArrayList<>();

                    for (Speaker speaker : speakers) {
                        String name = Utils.checkStringEmpty(speaker.getName());
                        speakerName.add(name);
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

        final int sessionId = session.getId();

        holder.sessionBookmarkIcon.setOnClickListener(v -> {
            if (session.getIsBookmarked()) {

                realmRepo.setBookmark(sessionId, false).subscribe();
                setBookmarkIcon(holder.sessionBookmarkIcon, false, track.getFontColor());

                if ("MainActivity".equals(context.getClass().getSimpleName())) {
                    Snackbar.make(holder.sessionCard, R.string.removed_bookmark, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, view -> {

                                realmRepo.setBookmark(sessionId, true).subscribe();
                                setBookmarkIcon(holder.sessionBookmarkIcon, true, track.getFontColor());
                                WidgetUpdater.updateWidget(context);
                            }).show();
                } else {
                    Snackbar.make(holder.sessionCard, R.string.removed_bookmark, Snackbar.LENGTH_SHORT).show();
                }
            } else {
                NotificationUtil.createNotification(session, context).subscribe(
                        () -> Snackbar.make(holder.sessionCard,
                                R.string.added_bookmark,
                                Snackbar.LENGTH_SHORT)
                                .show(),
                        throwable -> Snackbar.make(holder.sessionCard,
                                R.string.error_create_notification,
                                Snackbar.LENGTH_LONG).show());

                realmRepo.setBookmark(sessionId, true).subscribe();
                setBookmarkIcon(holder.sessionBookmarkIcon, true, track.getFontColor());

                Snackbar.make(holder.sessionCard, R.string.added_bookmark, Snackbar.LENGTH_SHORT).show();
            }
            WidgetUpdater.updateWidget(context);
        });

        // Set color generated by palette on views
        holder.sessionHeader.setBackgroundColor(color);
        if(track!=null && track.isValid()) {
            holder.sessionTitle.setTextColor(Color.parseColor(track.getFontColor()));
            setBookmarkIcon(holder.sessionBookmarkIcon, session.getIsBookmarked(), track.getFontColor());
        }
    }

    private void setBookmarkIcon(ImageView sessionBookmarkIcon, boolean bookmarked, String color) {
        if (bookmarked) {
            sessionBookmarkIcon.setImageResource(R.drawable.ic_bookmark_white_24dp);
        } else {
            sessionBookmarkIcon.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
        }
        if (!Utils.isEmpty(color))
            sessionBookmarkIcon.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_ATOP);
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }
}
