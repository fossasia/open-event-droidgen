package org.fossasia.openevent.core.schedule;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ui.base.BaseRVAdapter;
import org.fossasia.openevent.common.ui.recyclerview.HeaderViewHolder;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.repository.RealmDataRepository;
import org.fossasia.openevent.core.bookmark.OnBookmarkSelectedListener;
import org.fossasia.openevent.common.date.DateConverter;
import org.fossasia.openevent.common.utils.SortOrder;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.common.ui.recyclerview.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.List;

public class DayScheduleAdapter extends BaseRVAdapter<Session, DayScheduleViewHolder> implements StickyRecyclerHeadersAdapter<HeaderViewHolder> {

    private Context context;
    private OnBookmarkSelectedListener onBookmarkSelectedListener;
    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();
    private ArrayList<String> tracks = new ArrayList<>();

    public DayScheduleAdapter(List<Session> sessions, Context context) {
        super(sessions);
        this.context = context;
    }

    @Override
    public DayScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_schedule, parent, false);
        return new DayScheduleViewHolder(view,context, onBookmarkSelectedListener);
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
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public long getHeaderId(int position) {
        String id = "";
        if (SortOrder.sortTypeSchedule().equals(Session.TITLE)) {
            return getItem(position).getTitle().toUpperCase().charAt(0);
        } else if (SortOrder.sortTypeSchedule().equals(Session.TRACK)){
            if (tracks != null && !tracks.contains(getItem(position).getTrack().getName())) {
                tracks.add(getItem(position).getTrack().getName());
            }
            return tracks.indexOf(getItem(position).getTrack().getName());
        } else if (SortOrder.sortTypeSchedule().equals(Session.START_TIME)) {
            id = DateConverter.formatDateWithDefault(DateConverter.FORMAT_24H, getItem(position).getStartsAt(), "")
                    .replace(":", "")
                    .replace(" ", "");
        }
        return Long.valueOf(id);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_header, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder holder, int position) {
        String sortTitle = Utils.checkStringEmpty(getItem(position).getTitle());
        String sortName = Utils.checkStringEmpty(getItem(position).getTrack().getName());

        if (SortOrder.sortTypeSchedule().equals(Session.TITLE) && (!Utils.isEmpty(sortTitle))) {
            holder.header.setText(String.valueOf(sortTitle.toUpperCase().charAt(0)));
        } else if (SortOrder.sortTypeSchedule().equals(Session.TRACK)){
            holder.header.setText(String.valueOf(sortName));
        } else if (SortOrder.sortTypeSchedule().equals(Session.START_TIME)) {
            holder.header.setText(DateConverter.formatDateWithDefault(DateConverter.FORMAT_24H, getItem(position).getStartsAt()));
        }
    }

    public void setOnBookmarkSelectedListener(OnBookmarkSelectedListener onBookmarkSelectedListener) {
        this.onBookmarkSelectedListener = onBookmarkSelectedListener;
    }
}