package org.fossasia.openevent.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.facebook.CommentItem;
import org.fossasia.openevent.utils.DateConverter;

import java.text.ParseException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by rohanagarwal94 on 07/6/17.
 */
public class CommentsListAdapter extends BaseRVAdapter<CommentItem, CommentsListAdapter.RecyclerViewHolder> {

    private List<CommentItem> commentItems;

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.comment_time)
        TextView commentTime;
        @BindView(R.id.commenter)
        TextView commenter;
        @BindView(R.id.comment)
        TextView comment;

        public RecyclerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.bringToFront();
        }
    }

    public CommentsListAdapter(List<CommentItem> commentItems) {
        super(commentItems);
        this.commentItems=commentItems;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);

        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        final CommentItem commentItem = commentItems.get(position);

        if (commentItem.getCreatedTime() != null) {
            String createdTime = commentItem.getCreatedTime();
            try {
                holder.commentTime.setText(DateConverter.getRelativeTimeFromTimestamp(createdTime));
            } catch (ParseException e) {
                Timber.e(e);
            }
        }

        if (!TextUtils.isEmpty(commentItem.getMessage())) {
            holder.comment.setText(commentItem.getMessage());
            holder.comment.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            holder.comment.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(commentItem.getFrom().getName())) {
            holder.commenter.setText(commentItem.getFrom().getName());
            holder.commenter.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            holder.commenter.setVisibility(View.GONE);
        }

    }
}
