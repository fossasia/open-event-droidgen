package org.fossasia.openevent.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.viewholders.DayScheduleViewHolder;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.fragments.DayScheduleFragment;
import org.fossasia.openevent.utils.DateConverter;
import org.fossasia.openevent.utils.SortOrder;
import org.fossasia.openevent.utils.Utils;
import org.fossasia.openevent.views.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.realm.RealmResults;
import timber.log.Timber;

/**
 * Created by Manan Wason on 17/06/16.
 */
public class DayScheduleAdapter extends BaseRVAdapter<Session, DayScheduleViewHolder> implements StickyRecyclerHeadersAdapter {

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
        return new DayScheduleViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(DayScheduleViewHolder holder, int position) {
        Session currentSession = getItem(position);
        holder.setSession(currentSession);
        holder.bindSession(realmRepo);
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

        ((RealmResults<Session>) copySessions).sort(SortOrder.sortOrderSchedule());

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
        if (SortOrder.sortOrderSchedule().equals(Session.TITLE)) {
            return getItem(position).getTitle().charAt(0);
        } else if (SortOrder.sortOrderSchedule().equals(Session.TRACK)){
            if (tracks != null && !tracks.contains(getItem(position).getTrack().getName())) {
                tracks.add(getItem(position).getTrack().getName());
            }
            return tracks.indexOf(getItem(position).getTrack().getName());
        } else if (SortOrder.sortOrderSchedule().equals(Session.START_TIME)) {
            id = DateConverter.formatDateWithDefault(DateConverter.FORMAT_24H, getItem(position).getStartsAt(), "")
                    .replace(":", "")
                    .replace(" ", "");
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

        if (SortOrder.sortOrderSchedule().equals(Session.TITLE) && (!Utils.isEmpty(sortTitle))) {
            textView.setText(String.valueOf(sortTitle.charAt(0)));
        } else if (SortOrder.sortOrderSchedule().equals(Session.TRACK)){
            textView.setText(String.valueOf(sortName));
        } else if (SortOrder.sortOrderSchedule().equals(Session.START_TIME)) {
            textView.setText(DateConverter.formatDateWithDefault(DateConverter.FORMAT_24H, getItem(position).getStartsAt()));
        }
    }

}