package org.fossasia.openevent.core.feedback;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.data.Feedback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedbackViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.feedback_rating)
    protected RatingBar feedbackRating;

    @BindView(R.id.feedback_comment)
    protected TextView feedbackComment;

    public FeedbackViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bingFeedback(Feedback feedback) {
        String rating = Utils.checkStringEmpty(feedback.getRating());
        String comment = Utils.checkStringEmpty(feedback.getComment());

        feedbackRating.setRating(Float.parseFloat(rating));
        setStringFieldWithPrefix(feedbackComment, comment, null);
    }


    private void setStringFieldWithPrefix(TextView textView, String field, String prefix) {
        if (textView == null)
            return;

        if (!TextUtils.isEmpty(field.trim())) {
            textView.setVisibility(View.VISIBLE);
            if(prefix == null)
                textView.setText(field);
            else
                textView.setText(prefix + field);
        } else {
            textView.setVisibility(View.GONE);
        }
    }
}