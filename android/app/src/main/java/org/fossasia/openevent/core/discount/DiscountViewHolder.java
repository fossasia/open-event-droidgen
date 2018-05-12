package org.fossasia.openevent.core.discount;

import android.content.Context;
import android.content.res.Resources;
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

    private Context context;

    public DiscountViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        context = itemView.getContext();
    }

    public void bindDiscountCode(DiscountCode discountCode) {
        String code = Utils.nullToEmpty(discountCode.getCode());
        String discountUrl = Utils.nullToEmpty(discountCode.getDiscountUrl());
        String tickets = Utils.nullToEmpty(discountCode.getTickets());
        String usedFor = Utils.nullToEmpty(discountCode.getUsedFor());

        Resources res = context.getResources();
        String minQuantity = String.format(res.getString(R.string.discount_code_min_quantity), discountCode.getMinQuantity());
        String maxQuantity = String.format(res.getString(R.string.discount_code_max_quantity), discountCode.getMaxQuantity());
        String value = String.format(res.getString(R.string.discount_code_value), discountCode.getValue());

        setStringFieldWithPrefix(discountCodeTextView, code, null);
        setStringFieldWithPrefix(discountCodeValue, value, null);
        setStringFieldWithPrefix(discountMinQuantity, minQuantity, null);
        setStringFieldWithPrefix(discountMaxQuantity, maxQuantity, null);
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
