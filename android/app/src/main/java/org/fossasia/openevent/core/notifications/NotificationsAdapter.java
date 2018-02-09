package org.fossasia.openevent.core.notifications;

import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.date.AndroidDateConverter;
import org.fossasia.openevent.common.ui.Views;
import org.fossasia.openevent.common.ui.base.BaseRVAdapter;
import org.fossasia.openevent.data.Notification;
import org.threeten.bp.format.DateTimeParseException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class NotificationsAdapter extends BaseRVAdapter<Notification, NotificationsAdapter.RecyclerViewHolder> {

    private List<Notification> notificationsList;

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        protected TextView title;
        @BindView(R.id.message)
        protected TextView message;
        @BindView(R.id.received_at)
        protected TextView receivedAt;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.bringToFront();
        }
    }

    public NotificationsAdapter(List<Notification> notificationList) {
        super(notificationList);
        this.notificationsList = notificationList;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationsAdapter.RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        final Notification notificationItem = notificationsList.get(position);
        holder.title.setText(notificationItem.getTitle());
        holder.message.setText(Views.fromHtml(notificationItem.getMessage()));
        //making links in the message clickable.
        holder.message.setMovementMethod(LinkMovementMethod.getInstance());
        String receivedAt = notificationItem.getReceivedAt();
        try {
            holder.receivedAt.setText(AndroidDateConverter.getRelativeTimeFromOffsetDateTime(receivedAt));
        } catch (DateTimeParseException e) {
            Timber.e(e);
        }
    }
}
