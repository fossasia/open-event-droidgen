package org.fossasia.openevent.adapters.items;

import android.view.View;

import com.mikepenz.fastadapter.items.GenericAbstractItem;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.viewholders.LocationViewHolder;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.utils.Utils;

import java.text.MessageFormat;
import java.util.List;

public class LocationItem extends GenericAbstractItem<Microlocation, LocationItem, LocationViewHolder> {

    public LocationItem(Microlocation microlocation) {
        super(microlocation);
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_location;
    }

    @Override
    public void bindView(LocationViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        String locationNameString = Utils.checkStringEmpty(getModel().getName());
        holder.locationName.setText(locationNameString);
        holder.locationFloor.setText(MessageFormat.format("{0}{1}",
                Utils.getString(R.string.fmt_floor),
                getModel().getFloor()));
    }

    @Override
    public LocationViewHolder getViewHolder(View v) {
        return new LocationViewHolder(v);
    }
}
