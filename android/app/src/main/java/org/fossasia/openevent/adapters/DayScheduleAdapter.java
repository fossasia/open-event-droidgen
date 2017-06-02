package org.fossasia.openevent.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SessionDetailActivity;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.fragments.DayScheduleFragment;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.ISO8601Date;
import org.fossasia.openevent.utils.SortOrder;
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
        final Session currentSession = getItem(position);
        String startTime = ISO8601Date.get12HourTimeFromString(currentSession.getStartTime());
        String endTime = ISO8601Date.get12HourTimeFromString(currentSession.getEndTime());

        holder.startTime.setText(startTime);
        holder.endTime.setText(endTime);
        holder.slotTitle.setText(currentSession.getTitle());
        if (currentSession.getShortAbstract().isEmpty()) {
            holder.slotDescription.setVisibility(View.GONE);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                holder.slotDescription.setText(Html.fromHtml(currentSession.getShortAbstract(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                holder.slotDescription.setText(Html.fromHtml(currentSession.getShortAbstract()));
            }
        }

        if(currentSession.getMicrolocation() != null)
            holder.slotLocation.setText(currentSession.getMicrolocation().getName());

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
        
        if (SortOrder.sortOrderSchedule(context).equals(Session.TITLE)) {
            textView.setText(String.valueOf(getItem(position).getTitle().charAt(0)));
        } else if (SortOrder.sortOrderSchedule(context).equals(Session.TRACK)){
            textView.setText(String.valueOf(getItem(position).getTrack().getName()));
        }
        else if (SortOrder.sortOrderSchedule(context).equals(Session.START_TIME)) {
            textView.setText(ISO8601Date.get12HourTimeFromString(getItem(position).getStartTime()));
        }
    }

    class DayScheduleViewHolder extends RecyclerView.ViewHolder {

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


        DayScheduleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}