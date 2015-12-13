package org.fossasia.openevent.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.dbutils.DbSingleton;

import java.util.List;

/**
 * Created by MananWason on 8/18/2015.
 */
public class LocationsListAdapter extends RecyclerView.Adapter<LocationsListAdapter.Viewholder> {

    List<Microlocation> microlocations;

    public LocationsListAdapter(List<Microlocation> microlocations) {
        this.microlocations = microlocations;
    }

    @Override
    public LocationsListAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_location, parent, false);
        Viewholder viewholder = new Viewholder(view);
        return viewholder;
    }


    @Override
    public void onBindViewHolder(LocationsListAdapter.Viewholder holder, int position) {
        Microlocation current = microlocations.get(position);
        holder.name.setText(current.getName());
        holder.floor.setText("Floor : " + current.getFloor());

    }

    @Override
    public int getItemCount() {
        return microlocations.size();
    }

    public void refresh() {

        DbSingleton dbSingleton = DbSingleton.getInstance();
        microlocations.clear();
        microlocations = dbSingleton.getMicrolocationsList();
        notifyDataSetChanged();

    }

    public void animateTo(List<Microlocation> microlocations) {
        applyAndAnimateRemovals(microlocations);
        applyAndAnimateAdditions(microlocations);
        applyAndAnimateMovedItems(microlocations);
    }

    private void applyAndAnimateRemovals(List<Microlocation> newMicrolocations) {
        for (int i = microlocations.size() - 1; i >= 0; i--) {
            final Microlocation microlocation = microlocations.get(i);
            if (!newMicrolocations.contains(microlocation)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Microlocation> newMicrolocations) {
        for (int i = 0, count = newMicrolocations.size(); i < count; i++) {
            final Microlocation microlocation = newMicrolocations.get(i);
            if (!microlocations.contains(microlocation)) {
                addItem(i, microlocation);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Microlocation> newMicrolocations) {
        for (int toPosition = newMicrolocations.size() - 1; toPosition >= 0; toPosition--) {
            final Microlocation microlocation = newMicrolocations.get(toPosition);
            final int fromPosition = microlocations.indexOf(microlocation);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public Microlocation removeItem(int position) {
        final Microlocation microlocation = microlocations.remove(position);
        notifyItemRemoved(position);
        return microlocation;
    }

    public void addItem(int position, Microlocation microlocation) {
        microlocations.add(position, microlocation);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Microlocation location = microlocations.remove(fromPosition);
        microlocations.add(toPosition, location);
        notifyItemMoved(fromPosition, toPosition);
    }

    class Viewholder extends RecyclerView.ViewHolder {
        TextView name;
        TextView floor;

        public Viewholder(View itemView) {
            super(itemView);
            itemView.setClickable(true);

            name = (TextView) itemView.findViewById(R.id.location_name);
            floor = (TextView) itemView.findViewById(R.id.location_floor);

        }

    }
}
