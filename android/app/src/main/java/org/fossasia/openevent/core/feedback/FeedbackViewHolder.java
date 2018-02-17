package org.fossasia.openevent.core.feedback;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Feedback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedbackViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.feedback_rating)
    protected TextView tvRating;
    @BindView(R.id.feedback_comment)
    protected TextView tvComment;
    @BindView(R.id.parent_card_feedback)
    protected CardView parentCardView;

    public FeedbackViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindFeedbacks(Feedback feedback) {
        tvRating.setText(feedback.getRating());
        tvComment.setText(feedback.getComment());
        tvComment.setVisibility(View.GONE);

        tvRating.setOnClickListener(view -> toggleAnswerVisibility());
    }

    private void toggleAnswerVisibility() {
        if (tvComment.getVisibility() == View.GONE)
            tvComment.setVisibility(View.VISIBLE);
        else
            tvComment.setVisibility(View.GONE);
    }

}
