package org.fossasia.openevent.core.track.session;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ui.base.BaseRVAdapter;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.core.bookmark.OnBookmarkSelectedListener;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.repository.RealmDataRepository;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class SessionsListAdapter extends BaseRVAdapter<Session, SessionViewHolder> {

    private Context context;
    private int type;
    private OnBookmarkSelectedListener onBookmarkSelectedListener;
    private OnItemClickListener onItemClickListener;

    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private int color;

    public interface OnItemClickListener {
        void itemOnClick(Session session, int layoutPosition);
    }

    public SessionsListAdapter(Context context, List<Session> sessions, int type) {
        super(sessions);
        this.context = context;
        this.color = ContextCompat.getColor(context, R.color.color_primary);
        this.type = type;
    }

    public void setColor(int color) {
        this.color = color;
        notifyDataSetChanged();
    }

    @Override
    public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.tracksactvity_item, parent, false);
        return new SessionViewHolder(view, context, onBookmarkSelectedListener, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(final SessionViewHolder holder, final int position) {
        Session session = getItem(position);
        //removing draft sessions
        if ((!Utils.isEmpty(session.getState())) && session.getState().equals("draft")) {
            getDataList().remove(position);
            notifyItemRemoved(position);
            return;
        }
        holder.setSession(session);
        holder.bindSession(type, color, realmRepo);
    }

    public void setOnBookmarkSelectedListener(OnBookmarkSelectedListener onBookmarkSelectedListener) {
        this.onBookmarkSelectedListener = onBookmarkSelectedListener;
    }

    public void clearOnBookmarkSelectedListener() {
        this.onBookmarkSelectedListener = null;
    }

    public void setHandleItemClickListener(OnItemClickListener handleItemClickListener) {
        this.onItemClickListener = handleItemClickListener;
    }

    public void clearHandleItemClickListener() {
        this.onItemClickListener = null;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        compositeDisposable.dispose();
        super.onDetachedFromRecyclerView(recyclerView);
    }
}
