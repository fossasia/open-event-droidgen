package org.fossasia.openevent.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.fossasia.openevent.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Shreyas on 6/29/2017.
 */

public class DividerViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.search_result_type_view)
    protected TextView resultTypeHeader;

    public DividerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindHeader(String headerItem) {
        resultTypeHeader.setText(headerItem);
    }
}