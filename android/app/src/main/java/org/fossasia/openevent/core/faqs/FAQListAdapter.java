package org.fossasia.openevent.core.faqs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ui.base.BaseRVAdapter;
import org.fossasia.openevent.data.FAQ;

import java.util.List;

public class FAQListAdapter extends BaseRVAdapter<FAQ, FAQViewHolder> {

    public FAQListAdapter(List<FAQ> dataList) {
        super(dataList);
    }

    @Override
    public FAQViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_faq, parent, false);
        return new FAQViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FAQViewHolder holder, int position) {
        holder.bindFAQs(getDataList().get(position));
    }
}
