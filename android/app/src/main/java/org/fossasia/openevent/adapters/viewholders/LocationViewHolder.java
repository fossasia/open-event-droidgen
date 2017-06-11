package org.fossasia.openevent.adapters.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.LocationActivity;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.Utils;

import java.text.MessageFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.location_name)
    protected TextView locationName;

    @BindView(R.id.location_floor)
    protected TextView locationFloor;

    private Microlocation location;

    public LocationViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        Context applicationContext = context.getApplicationContext();

        //Attach onClickListener for ViewHolder
        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(applicationContext, LocationActivity.class);
            intent.putExtra(ConstantStrings.MICROLOCATIONS, location.getName());
            getAdapterPosition();
            applicationContext.startActivity(intent);
        });
    }

    public void bindLocation(Microlocation location) {
        this.location = location;

        String locationNameString = Utils.checkStringEmpty(location.getName());
        locationName.setText(locationNameString);
        locationFloor.setText(MessageFormat.format("{0}{1}",
                itemView.getResources().getString(R.string.fmt_floor),
                location.getFloor()));
    }
}
