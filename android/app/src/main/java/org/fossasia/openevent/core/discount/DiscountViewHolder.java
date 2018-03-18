package org.fossasia.openevent.core.discount;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.data.DiscountCode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DiscountViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.discount_code)
    protected TextView discountCodeTextView;

    @BindView(R.id.discount_code_value)
    protected TextView discountCodeValue;

    @BindView(R.id.discount_is_active)
    protected TextView discountIsActive
            ;
    @BindView(R.id.discount_max_quantity)
    protected TextView discountMaxQuantity;

    @BindView(R.id.discount_min_quantity)
    protected TextView discountMinQuantity;

    @BindView(R.id.discount_tickets)
    protected TextView discountTicket;

    @BindView(R.id.discount_url)
    protected TextView discountUrl;

    @BindView(R.id.discount_used_for)
    protected TextView discountUsedFor;


    public DiscountViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindDiscountCode(DiscountCode discountCode) {
        String code = Utils.checkStringEmpty(discountCode.getCode());
        String discountUrl = Utils.checkStringEmpty(discountCode.getDiscount_url());
        String tickets = Utils.checkStringEmpty(discountCode.getTickets());
        String usedFor = Utils.checkStringEmpty(discountCode.getUsed_for());

        int minQuantity = discountCode.getMin_quantity();
        int maxQuantity = discountCode.getMax_quantity();
        float value = discountCode.getValue();

        setStringFieldWithPrefix(discountCodeTextView, code, null);
        setStringFieldWithPrefix(discountCodeValue, "Value : " + value, null);
        setStringFieldWithPrefix(discountMinQuantity, "Minimum Quantity : " + minQuantity, null);
        setStringFieldWithPrefix(discountMaxQuantity, "Maximum Quantity :" + maxQuantity, null);
        setStringFieldWithPrefix(discountTicket, tickets, "Tickets : ");
        setStringFieldWithPrefix(discountUsedFor, usedFor, "Used For : ");
        setStringFieldWithPrefix(this.discountUrl, discountUrl, "Discount URL : ");
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
