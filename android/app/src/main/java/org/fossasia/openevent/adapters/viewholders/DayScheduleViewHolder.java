package org.fossasia.openevent.adapters.viewholders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SessionDetailActivity;
import org.fossasia.openevent.activities.TrackSessionsActivity;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.DateUtils;
import org.fossasia.openevent.utils.NotificationUtil;
import org.fossasia.openevent.utils.Utils;
import org.fossasia.openevent.utils.WidgetUpdater;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmChangeListener;
import timber.log.Timber;

/**
 * Created by Shreyas on 6/29/2017.
 */

public class DayScheduleViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.content_frame)
    RelativeLayout slot_content;

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
    ImageButton slot_bookmark;

    private Session session;
    private Context context;

    public DayScheduleViewHolder(View itemView,Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = context;
    }

    public void bindSession(RealmDataRepository realmRepo){

        String startTimeText = DateUtils.formatDateWithDefault(DateUtils.FORMAT_24H, session.getStartsAt());
        String endTimeText = DateUtils.formatDateWithDefault(DateUtils.FORMAT_24H, session.getEndsAt());
        String title = Utils.checkStringEmpty(session.getTitle());

        startTime.setText(startTimeText);
        endTime.setText(endTimeText);
        slotTitle.setText(title);

        Track sessionTrack = session.getTrack();

        if (!RealmDataRepository.isNull(sessionTrack)) {
            int storedColor = Color.parseColor(sessionTrack.getColor());
            slotTrack.setVisibility(View.VISIBLE);
            slotTrack.getBackground().setColorFilter(storedColor, PorterDuff.Mode.SRC_ATOP);
            slotTrack.setText(sessionTrack.getName());

            if(session.getIsBookmarked()) {
                slot_bookmark.setImageResource(R.drawable.ic_bookmark_white_24dp);
            } else {
                slot_bookmark.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
            }
            slot_bookmark.setColorFilter(storedColor,PorterDuff.Mode.SRC_ATOP);

            final int sessionId = session.getId();

            slot_bookmark.setOnClickListener(v -> {
                if(session.getIsBookmarked()) {

                    realmRepo.setBookmark(sessionId, false).subscribe();
                    slot_bookmark.setImageResource(R.drawable.ic_bookmark_border_white_24dp);

                    if ("MainActivity".equals(context.getClass().getSimpleName())) {
                        Snackbar.make(slot_content, R.string.removed_bookmark, Snackbar.LENGTH_LONG)
                                .setAction(R.string.undo, view -> {

                                    realmRepo.setBookmark(sessionId, true).subscribe();
                                    slot_bookmark.setImageResource(R.drawable.ic_bookmark_white_24dp);

                                    WidgetUpdater.updateWidget(context);
                                }).show();
                    } else {
                        Snackbar.make(slot_content, R.string.removed_bookmark, Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    NotificationUtil.createNotification(session, context).subscribe(
                            () -> Snackbar.make(slot_content,
                                    R.string.added_bookmark,
                                    Snackbar.LENGTH_SHORT)
                                    .show(),
                            throwable -> Snackbar.make(slot_content,
                                    R.string.error_create_notification,
                                    Snackbar.LENGTH_LONG).show());

                    realmRepo.setBookmark(sessionId, true).subscribe();
                    slot_bookmark.setImageResource(R.drawable.ic_bookmark_white_24dp);
                    slot_bookmark.setColorFilter(storedColor,PorterDuff.Mode.SRC_ATOP);

                    Snackbar.make(slot_content, R.string.added_bookmark, Snackbar.LENGTH_SHORT).show();
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

        if(session.getMicrolocation() != null) {
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
