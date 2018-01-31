package org.fossasia.openevent.core.schedule;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.core.bookmark.BookmarkStatus;
import org.fossasia.openevent.core.track.session.SessionDetailActivity;
import org.fossasia.openevent.core.track.session.TrackSessionsActivity;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.data.repository.RealmDataRepository;
import org.fossasia.openevent.core.bookmark.OnBookmarkSelectedListener;
import org.fossasia.openevent.common.ConstantStrings;
import org.fossasia.openevent.common.date.DateConverter;
import org.fossasia.openevent.common.notification.NotificationUtil;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.common.ui.WidgetUpdater;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmChangeListener;
import timber.log.Timber;

import static org.fossasia.openevent.core.bookmark.BookmarkStatus.Status.CODE_ERROR;
import static org.fossasia.openevent.core.bookmark.BookmarkStatus.Status.CODE_UNDO_ADDED;
import static org.fossasia.openevent.core.bookmark.BookmarkStatus.Status.CODE_UNDO_REMOVED;

public class DayScheduleViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.content_frame)
    LinearLayout slotContent;

    @BindView(R.id.slot_start_time)
    TextView startTime;

    @BindView(R.id.slot_end_time)
    TextView endTime;

    @BindView(R.id.slot_title)
    TextView slotTitle;

    @BindView(R.id.slot_location)
    TextView slotLocation;

    @BindView(R.id.slot_track)
    Button slotTrack;

    @BindView(R.id.slot_bookmark)
    ImageButton slotBookmark;

    private Session session;
    private final Context context;
    private OnBookmarkSelectedListener onBookmarkSelectedListener;

    public DayScheduleViewHolder(View itemView, Context context, OnBookmarkSelectedListener onBookmarkSelectedListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = context;
        this.onBookmarkSelectedListener = onBookmarkSelectedListener;
    }

    public void bindSession(RealmDataRepository realmRepo) {
        String startTimeText = DateConverter.formatDateWithDefault(DateConverter.FORMAT_24H, session.getStartsAt());
        String endTimeText = DateConverter.formatDateWithDefault(DateConverter.FORMAT_24H, session.getEndsAt());
        String title = Utils.checkStringEmpty(session.getTitle());

        startTime.setText(startTimeText);
        endTime.setText(endTimeText);
        slotTitle.setText(title);

        Track sessionTrack = session.getTrack();

        if (!RealmDataRepository.isNull(sessionTrack)) {
            int storedColor = Color.parseColor(sessionTrack.getColor());
            slotTrack.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                slotTrack.setBackground(context.getDrawable(R.drawable.button_ripple));
            }
            slotTrack.getBackground().setColorFilter(storedColor, PorterDuff.Mode.SRC_ATOP);
            slotTrack.setText(sessionTrack.getName());

            if (session.getIsBookmarked()) {
                slotBookmark.setImageResource(R.drawable.ic_bookmark_white_24dp);
            } else {
                slotBookmark.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
            }
            slotBookmark.setColorFilter(storedColor, PorterDuff.Mode.SRC_ATOP);

            final int sessionId = session.getId();

            slotBookmark.setOnClickListener(v -> {
                if (session.getIsBookmarked()) {

                    realmRepo.setBookmark(sessionId, false).subscribe();
                    slotBookmark.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
                    if (onBookmarkSelectedListener != null)
                        onBookmarkSelectedListener.showSnackbar(new BookmarkStatus(storedColor, sessionId, CODE_UNDO_REMOVED));
                } else {
                    // TODO: Move all logic to ViewModel
                    NotificationUtil.createNotification(session, context).subscribe(
                            () -> {
                                if (onBookmarkSelectedListener != null)
                                    onBookmarkSelectedListener.showSnackbar(new BookmarkStatus(storedColor, sessionId, CODE_UNDO_ADDED));
                            },
                            throwable -> {
                                if (onBookmarkSelectedListener != null)
                                    onBookmarkSelectedListener.showSnackbar(new BookmarkStatus(-1, -1, CODE_ERROR));
                            });

                    realmRepo.setBookmark(sessionId, true).subscribe();

                    slotBookmark.setImageResource(R.drawable.ic_bookmark_white_24dp);
                    slotBookmark.setColorFilter(storedColor, PorterDuff.Mode.SRC_ATOP);
                }
                WidgetUpdater.updateWidget(context);
            });

            slotTrack.setOnClickListener(v -> {
                Intent intent = new Intent(context, TrackSessionsActivity.class);
                intent.putExtra(ConstantStrings.TRACK, sessionTrack.getName());
                intent.putExtra(ConstantStrings.TRACK_ID, sessionTrack.getId());
                context.startActivity(intent);
            });

            itemView.setOnClickListener(v -> {
                final String sessionName = session.getTitle();

                realmRepo.getTrack(session.getTrack().getId())
                        .addChangeListener((RealmChangeListener<Track>) track -> {
                            String trackName = track.getName();
                            Intent intent = new Intent(context, SessionDetailActivity.class);
                            intent.putExtra(ConstantStrings.SESSION, sessionName);
                            intent.putExtra(ConstantStrings.TRACK, trackName);
                            intent.putExtra(ConstantStrings.ID, session.getId());
                            context.startActivity(intent);
                        });
            });
        } else {
            slotTrack.setOnClickListener(null);
            slotTrack.setVisibility(View.GONE);

            itemView.setOnClickListener(v -> {
                final String sessionName = session.getTitle();

                Intent intent = new Intent(context, SessionDetailActivity.class);
                intent.putExtra(ConstantStrings.SESSION, sessionName);
                intent.putExtra(ConstantStrings.ID, session.getId());
                context.startActivity(intent);
            });

            Timber.d("This session has no track somehow : " + session + " " + sessionTrack);
        }

        if (session.getMicrolocation() != null) {
            String locationName = Utils.checkStringEmpty(session.getMicrolocation().getName());
            slotLocation.setText(locationName);
        }
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
