package org.fossasia.openevent.core.feedback;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ui.base.BaseRVAdapter;
import org.fossasia.openevent.data.Feedback;

import java.util.List;

public class FeedbacksListAdapter extends BaseRVAdapter<Feedback, FeedbackViewHolder> {

    public FeedbacksListAdapter(List<Feedback> feedbacks) {
        super(feedbacks);
    }

    @Override
    public FeedbackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    public void onBindViewHolder(FeedbackViewHolder holder, int position) {
        holder.bindFeedback(getItem(position));
    }
}