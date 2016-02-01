package org.fossasia.openevent.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.dbutils.DbSingleton;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * User: MananWason
 * Date: 8/18/2015
 */
public class LocationsListAdapter extends BaseRVAdapter<Microlocation, LocationsListAdapter.Viewholder> {
    @SuppressWarnings("all")
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            DbSingleton instance = DbSingleton.getInstance();
            List<Microlocation> microlocations = instance.getMicrolocationsList();
            final ArrayList<Microlocation> filteredLocationList = new ArrayList<>();
            String query = constraint.toString().toLowerCase();
            for (Microlocation microlocation : microlocations) {
                final String text = microlocation.getName().toLowerCase();
                if (text.contains(query)) {
                    filteredLocationList.add(microlocation);
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredLocationList;
            filterResults.count = filteredLocationList.size();
            Timber.d("Filtering done total results %d", filterResults.count);
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            animateTo((List<Microlocation>) results.values);
        }
    };

    public LocationsListAdapter(List<Microlocation> microlocations) {
        super(microlocations);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public LocationsListAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_location, parent, false);
        return new Viewholder(view);
    }


    @Override
    public void onBindViewHolder(LocationsListAdapter.Viewholder holder, int position) {
        Microlocation current = getItem(position);
        holder.name.setText(current.getName());
        holder.floor.setText(MessageFormat.format("{0}{1}",
                holder.itemView.getResources().getString(R.string.fmt_floor),
                current.getFloor()));
    }

    public void refresh() {
        DbSingleton dbSingleton = DbSingleton.getInstance();
        clear();
        animateTo(dbSingleton.getMicrolocationsList());
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
