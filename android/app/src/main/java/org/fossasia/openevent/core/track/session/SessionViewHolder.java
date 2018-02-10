package org.fossasia.openevent.core.track.session;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.date.DateConverter;
import org.fossasia.openevent.common.date.DateService;
import org.fossasia.openevent.common.notification.NotificationUtil;
import org.fossasia.openevent.common.ui.Views;
import org.fossasia.openevent.common.ui.WidgetUpdater;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.core.bookmark.BookmarkStatus;
import org.fossasia.openevent.core.bookmark.OnBookmarkSelectedListener;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.data.repository.RealmDataRepository;
import org.threeten.bp.ZonedDateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import timber.log.Timber;

import static org.fossasia.openevent.core.bookmark.BookmarkStatus.Status.CODE_ERROR;
import static org.fossasia.openevent.core.bookmark.BookmarkStatus.Status.CODE_UNDO_ADDED;


public class SessionViewHolder extends RecyclerView.ViewHolder {

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

    @BindView(R.id.session_status)
    TextView sessionStatus;

    private Session session;
    private Context context;
    private OnBookmarkSelectedListener onBookmarkSelectedListener;
    private SessionsListAdapter.OnItemClickListener onItemClickListener;

    private static final int locationWiseSessionList = 1;
    private static final int trackWiseSessionList = 4;
    private static final int speakerWiseSessionList = 2;

    // TODO : Move all unrelated logic out of view holder
    SessionViewHolder(View itemView, Context context, OnBookmarkSelectedListener onBookmarkSelectedListener, SessionsListAdapter.OnItemClickListener onItemClickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = context;
        this.onBookmarkSelectedListener = onBookmarkSelectedListener;
        this.onItemClickListener = onItemClickListener;
    }

    public void bindSession(int type, int colorInTracks, RealmDataRepository realmRepo) {

        String sessionTitleString = Utils.checkStringEmpty(session.getTitle());
        String sessionSubTitle = Utils.checkStringEmpty(session.getSubtitle());
        int color = ContextCompat.getColor(context, R.color.color_primary);

        sessionTitle.setText(sessionTitleString);

        if (Utils.isEmpty(sessionSubTitle)) {
            sessionSubtitle.setVisibility(View.GONE);
        } else {
            sessionSubtitle.setVisibility(View.VISIBLE);
            sessionSubtitle.setText(sessionSubTitle);
        }

        sessionStatus.setVisibility(View.GONE);

        setSessionStatus();

        Track track = session.getTrack();

        if (!RealmDataRepository.isNull(track)) {
            int storedColor = Color.parseColor(track.getColor());

            if (type != trackWiseSessionList) {
                color = storedColor;
            } else {
                color = colorInTracks;
            }

            TextDrawable drawable = Views.getTextDrawableBuilder().round()
                    .build(String.valueOf(track.getName().charAt(0)), storedColor);

            trackImageIcon.setImageDrawable(drawable);
            trackImageIcon.setBackgroundColor(Color.TRANSPARENT);
            sessionTrack.setText(track.getName());

        } else {
            trackImageIcon.setVisibility(View.GONE);
            sessionTrack.setVisibility(View.GONE);
            Timber.d("This session has a null or incomplete track somehow : " + session.getTitle() + " " + track);
        }

        itemView.setOnClickListener(v -> onItemClickListener.itemOnClick(session, getLayoutPosition()));

        String date = DateConverter.formatDateWithDefault(DateConverter.FORMAT_DATE_COMPLETE, session.getStartsAt());
        sessionDate.setText(date);
        sessionTime.setText(String.format("%s - %s",
                DateConverter.formatDateWithDefault(DateConverter.FORMAT_12H, session.getStartsAt()),
                DateConverter.formatDateWithDefault(DateConverter.FORMAT_12H, session.getEndsAt())));

        if (session.getMicrolocation() != null) {
            String locationName = Utils.checkStringEmpty(session.getMicrolocation().getName());
            sessionLocation.setText(locationName);
        } else {
            sessionLocation.setText(context.getString(R.string.location_not_decided));
        }

        setSpeakerView();

        handleVisibilityByType(type);

        setBookmarkClickListener(realmRepo, track);

        // Set color generated by palette on views
        sessionHeader.setBackgroundColor(color);
        if (track != null && track.isValid()) {
            sessionTitle.setTextColor(Color.parseColor(track.getFontColor()));
            setBookmarkIcon(sessionBookmarkIcon, session.getIsBookmarked(), track.getFontColor());
        }
    }

    private void setSpeakerView() {
        if (session.getSpeakers().isEmpty()) {
            sessionSpeaker.setVisibility(View.GONE);
            speakerIcon.setVisibility(View.GONE);
            return;
        }

        Observable.fromIterable(session.getSpeakers())
                .map(Speaker::getName)
                .map(Utils::checkStringEmpty) // Also, what is checkStringEmpty rename it to mean what it does
                .toList()
                .map(names -> TextUtils.join(", ", names))
                .subscribe(s -> sessionSpeaker.setText(s));
    }

    private void setSessionStatus() {
        ZonedDateTime start = DateConverter.getDate(session.getStartsAt());
        ZonedDateTime end = DateConverter.getDate((session.getEndsAt()));
        ZonedDateTime current = ZonedDateTime.now();
        if (DateService.isUpcomingSession(start, end, current)) {
            sessionStatus.setVisibility(View.VISIBLE);
            sessionStatus.setText(R.string.status_upcoming);
        } else if (DateService.isOngoingSession(start, end, current)) {
            sessionStatus.setVisibility(View.VISIBLE);
            sessionStatus.setText(R.string.status_ongoing);
        }

    }

    private void handleVisibilityByType(int type) {
        switch (type) {
            case trackWiseSessionList:
                trackImageIcon.setVisibility(View.GONE);
                sessionTrack.setVisibility(View.GONE);
                break;

            case locationWiseSessionList:
                sessionLocation.setVisibility(View.GONE);
                locationIcon.setVisibility(View.GONE);
                break;

            case speakerWiseSessionList:
                sessionSpeaker.setVisibility(View.GONE);
                speakerIcon.setVisibility(View.GONE);
                break;

            default: // Shouldn't reach here
        }
    }

    private void setBookmarkClickListener(RealmDataRepository realmRepo, Track track) {

        final int sessionId = session.getId();
        sessionBookmarkIcon.setOnClickListener(v -> {
            if (track == null) return;

            if (session.getIsBookmarked()) {

                realmRepo.setBookmark(sessionId, false).subscribe();
                setBookmarkIcon(sessionBookmarkIcon, false, track.getFontColor());

                if (onBookmarkSelectedListener != null)
                    onBookmarkSelectedListener.showSnackbar(new BookmarkStatus(Color.parseColor(track.getColor()),
                            sessionId, BookmarkStatus.Status.CODE_UNDO_REMOVED));

            } else {
                NotificationUtil.createNotification(session, context).subscribe(
                        () -> {
                            if (onBookmarkSelectedListener != null)
                                onBookmarkSelectedListener.showSnackbar(new BookmarkStatus(Color.parseColor(track.getColor()),
                                        sessionId, CODE_UNDO_ADDED));
                        },
                        throwable -> {
                            if (onBookmarkSelectedListener != null)
                                onBookmarkSelectedListener.showSnackbar(new BookmarkStatus(-1, -1, CODE_ERROR));
                        });

                realmRepo.setBookmark(sessionId, true).subscribe();
                setBookmarkIcon(
                        sessionBookmarkIcon, true, track.getFontColor());
            }
            WidgetUpdater.updateWidget(context);
        });
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

    public void setSession(Session session) {
        this.session = session;
    }
}
