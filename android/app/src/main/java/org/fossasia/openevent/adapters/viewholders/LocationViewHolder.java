package org.fossasia.openevent.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.fossasia.openevent.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.location_name)
    public TextView locationName;

    @BindView(R.id.location_floor)
    public TextView locationFloor;

    public LocationViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
