package org.fossasia.openevent.core.feedback;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ui.base.BaseRVAdapter;
import org.fossasia.openevent.data.Feedback;

import java.util.List;

public class FeedbackListAdapter extends BaseRVAdapter<Feedback, FeedbackViewHolder> {

    public FeedbackListAdapter(List<Feedback> dataList) {
        super(dataList);
    }

    @Override
    public FeedbackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FeedbackViewHolder holder, int position) {
        holder.bindFeedbacks(getDataList().get(position));
    }
}
