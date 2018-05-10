package org.fossasia.openevent.core.feedback;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ui.image.CircleTransform;
import org.fossasia.openevent.config.StrategyRegistry;
import org.fossasia.openevent.core.auth.model.User;
import org.fossasia.openevent.data.Feedback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedbackViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.feedback_rating)
    protected RatingBar feedbackRating;

    @BindView(R.id.feedback_user_name)
    protected TextView userName;

    @BindView(R.id.feedback_comment)
    protected TextView feedbackComment;

    @BindView(R.id.feedback_profile)
    protected ImageView avatar;

    public FeedbackViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindFeedback(Feedback feedback) {
        String comment = feedback.getComment();
        User user = feedback.getUser();
        if (user != null) {
            String avatarUrl = user.getAvatarUrl();
            if (user.getFirstName() != null)
                setStringFieldWithPrefix(userName, user.getFirstName(), null);
            if (avatarUrl != null) {
                StrategyRegistry.getInstance()
                        .getHttpStrategy()
                        .getPicassoWithCache()
                        .load(avatarUrl)
                        .transform(new CircleTransform())
                        .into(avatar);
            }
        }

        float rating = Float.parseFloat(feedback.getRating());
        feedbackRating.setRating(rating);
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