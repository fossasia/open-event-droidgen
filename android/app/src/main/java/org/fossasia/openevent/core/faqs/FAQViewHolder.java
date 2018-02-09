package org.fossasia.openevent.core.faqs;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.FAQ;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FAQViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.faq_question)
    protected TextView tvQuestion;
    @BindView(R.id.faq_answer)
    protected TextView tvAnswer;
    @BindView(R.id.parent_card_faq)
    protected CardView parentCardView;

    public FAQViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindFAQs(FAQ faq) {
        tvQuestion.setText(faq.getQuestion());
        tvAnswer.setText(faq.getAnswer());
        tvAnswer.setVisibility(View.GONE);

        tvQuestion.setOnClickListener(view -> toggleAnswerVisibility());
    }

    private void toggleAnswerVisibility() {
        if (tvAnswer.getVisibility() == View.GONE)
            tvAnswer.setVisibility(View.VISIBLE);
        else
            tvAnswer.setVisibility(View.GONE);
    }

}
