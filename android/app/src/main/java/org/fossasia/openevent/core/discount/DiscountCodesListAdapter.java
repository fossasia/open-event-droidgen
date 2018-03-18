package org.fossasia.openevent.core.discount;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ui.base.BaseRVAdapter;
import org.fossasia.openevent.data.DiscountCode;

import java.util.List;

public class DiscountCodesListAdapter extends BaseRVAdapter<DiscountCode, DiscountViewHolder> {

    public DiscountCodesListAdapter(List<DiscountCode> discountCodes) {
        super(discountCodes);
    }

    @Override
    public DiscountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_discount_code, parent, false);
        return new DiscountViewHolder(view);
    }

    public void onBindViewHolder(DiscountViewHolder holder, int position) {
        holder.bindDiscountCode(getItem(position));
    }
}