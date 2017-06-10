package org.fossasia.openevent.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import org.fossasia.openevent.fragments.DayScheduleFragment;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.ISO8601Date;
import org.fossasia.openevent.utils.SortOrder;
import org.fossasia.openevent.utils.Utils;
import org.fossasia.openevent.utils.Views;
import org.fossasia.openevent.utils.WidgetUpdater;
import org.fossasia.openevent.views.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import timber.log.Timber;

import static org.fossasia.openevent.utils.BookmarkUtil.createNotification;

/**
 * Created by Manan Wason on 17/06/16.
 */
public class DayScheduleAdapter extends BaseRVAdapter<Session, DayScheduleAdapter.DayScheduleViewHolder> implements StickyRecyclerHeadersAdapter {

    private Context context;
    private String eventDate;
    private CompositeDisposable disposable;

    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();

    private ArrayList<String> tracks = new ArrayList<>();
    private List<Session> copySessions = new ArrayList<>();

    public DayScheduleAdapter(List<Session> sessions, Context context) {
        super(sessions);
        copySessions = new ArrayList<>(sessions);
        this.context = context;
    }

    public void setCopy(List<Session> sessions) {
        copySessions = sessions;
    }

    public String getEventDate() {
        return eventDate;
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
        Session currentSession = getItem(position);
        String startTime = ISO8601Date.get12HourTimeFromString(currentSession.getStartTime());
        String endTime = ISO8601Date.get12HourTimeFromString(currentSession.getEndTime());
        String title = Utils.checkStringEmpty(currentSession.getTitle());
        String shortAbstract = Utils.checkStringEmpty(currentSession.getShortAbstract());

        holder.startTime.setText(startTime);
        holder.endTime.setText(endTime);
        holder.slotTitle.setText(title);

        Views.setHtml(holder.slotDescription, shortAbstract, true);

        Track sessionTrack = currentSession.getTrack();

        if (!RealmDataRepository.isNull(sessionTrack)) {
            int storedColor = Color.parseColor(sessionTrack.getColor());
            holder.slotTrack.setVisibility(View.VISIBLE);
            holder.slotTrack.getBackground().setColorFilter(storedColor, PorterDuff.Mode.SRC_ATOP);
            holder.slotTrack.setText(sessionTrack.getName());

            if(currentSession.isBookmarked()) {
                holder.slot_bookmark.setImageResource(R.drawable.ic_bookmark_white_24dp);
            } else {
                holder.slot_bookmark.setImageResource(R.drawable.ic_bookmark_border_white_24dp);
            }
            holder.slot_bookmark.setColorFilter(storedColor,PorterDuff.Mode.SRC_ATOP);

            final int sessionId = currentSession.getId();

            holder.slot_bookmark.setOnClickListener(v -> {
                if(currentSession.isBookmarked()) {

                    realmRepo.setBookmark(sessionId, false).subscribe();
                    holder.slot_bookmark.setImageResource(R.drawable.ic_bookmark_border_white_24dp);

                    if ("MainActivity".equals(context.getClass().getSimpleName())) {
                        Snackbar.make(holder.slot_content, R.string.removed_bookmark, Snackbar.LENGTH_LONG)
                                .setAction(R.string.undo, view -> {

                                    realmRepo.setBookmark(sessionId, true).subscribe();
                                    holder.slot_bookmark.setImageResource(R.drawable.ic_bookmark_white_24dp);

                                    WidgetUpdater.updateWidget(context);
                                }).show();
                    } else {
                        Snackbar.make(holder.slot_content, R.string.removed_bookmark, Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    createNotification(currentSession,context);

                    realmRepo.setBookmark(sessionId, true).subscribe();
                    holder.slot_bookmark.setImageResource(R.drawable.ic_bookmark_white_24dp);
                    holder.slot_bookmark.setColorFilter(storedColor,PorterDuff.Mode.SRC_ATOP);

                    Snackbar.make(holder.slot_content, R.string.added_bookmark, Snackbar.LENGTH_SHORT).show();
                }
                WidgetUpdater.updateWidget(context);
            });

            holder.slotTrack.setOnClickListener(v -> {
                Intent intent = new Intent(context, TrackSessionsActivity.class);
                intent.putExtra(ConstantStrings.TRACK, sessionTrack.getName());
                intent.putExtra(ConstantStrings.TRACK_ID, sessionTrack.getId());
                context.startActivity(intent);
            });

            holder.itemView.setOnClickListener(v -> {
                final String sessionName = currentSession.getTitle();

                realmRepo.getTrack(currentSession.getTrack().getId())
                        .addChangeListener((RealmChangeListener<Track>) track -> {
                            String trackName = track.getName();
                            Intent intent = new Intent(context, SessionDetailActivity.class);
                            intent.putExtra(ConstantStrings.SESSION, sessionName);
                            intent.putExtra(ConstantStrings.TRACK, trackName);
                            intent.putExtra(ConstantStrings.ID, currentSession.getId());
                            context.startActivity(intent);
                        });
            });
        } else {
            holder.slotTrack.setOnClickListener(null);
            holder.slotTrack.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(v -> {
                final String sessionName = currentSession.getTitle();

                Intent intent = new Intent(context, SessionDetailActivity.class);
                intent.putExtra(ConstantStrings.SESSION, sessionName);
                intent.putExtra(ConstantStrings.ID, currentSession.getId());
                context.startActivity(intent);
            });

            Timber.d("This session has no track somehow : " + currentSession + " " + sessionTrack);
        }

        if(currentSession.getMicrolocation() != null) {
            String locationName = Utils.checkStringEmpty(currentSession.getMicrolocation().getName());
            holder.slotLocation.setText(locationName);
        }
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

    public void filter(String constraint) {
        final String query = constraint.toLowerCase(Locale.getDefault());

        ((RealmResults<Session>) copySessions).sort(SortOrder.sortOrderSchedule(context));

        List<Session> filteredSessions = Observable.fromIterable(copySessions)
                .filter(session -> {
                    boolean co = session.getTitle().toLowerCase().contains(query);

                    Log.d("TAG", session.getTitle() + " " + co + " " + query);

                    return co;
                }).toList().blockingGet();

        Timber.d("Filtering done total results %d", filteredSessions.size());

        if (DayScheduleFragment.searchText.equals("")) {
            return;
        }

        if(filteredSessions.isEmpty()) {
            Timber.e("No results published. There is an error in query. Check " + getClass().getName() + " filter!");
            return;
        }

        animateTo(filteredSessions);
    }

    @Override
    public long getHeaderId(int position) {
        String id = "";
        if (SortOrder.sortOrderSchedule(context).equals(Session.TITLE)) {
            return getItem(position).getTitle().charAt(0);
        } else if (SortOrder.sortOrderSchedule(context).equals(Session.TRACK)){
            if (tracks != null && !tracks.contains(getItem(position).getTrack().getName())) {
                tracks.add(getItem(position).getTrack().getName());
            }
            return tracks.indexOf(getItem(position).getTrack().getName());
        }
        else if (SortOrder.sortOrderSchedule(context).equals(Session.START_TIME)) {
            id = ISO8601Date.get24HourTimeFromString(getItem(position).getStartTime());
            id = id.replace(":", "");
            id = id.replace(" ", "");
        }
        return Long.valueOf(id);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_header, parent, false);
        return new RecyclerView.ViewHolder(view) {};
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView.findViewById(R.id.recyclerview_view_header);
        String sortTitle = Utils.checkStringEmpty(getItem(position).getTitle());
        String sortName = Utils.checkStringEmpty(getItem(position).getTrack().getName());

        if (SortOrder.sortOrderSchedule(context).equals(Session.TITLE) && (!Utils.isEmpty(sortTitle))) {
            textView.setText(String.valueOf(sortTitle.charAt(0)));
        } else if (SortOrder.sortOrderSchedule(context).equals(Session.TRACK)){
            textView.setText(String.valueOf(sortName));
        } else if (SortOrder.sortOrderSchedule(context).equals(Session.START_TIME)) {
            textView.setText(ISO8601Date.get12HourTimeFromString(getItem(position).getStartTime()));
        }
    }

    class DayScheduleViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.content_frame)
        RelativeLayout slot_content;

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

        @BindView(R.id.slot_track)
        Button slotTrack;

        @BindView(R.id.slot_bookmark)
        ImageButton slot_bookmark;


        DayScheduleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}