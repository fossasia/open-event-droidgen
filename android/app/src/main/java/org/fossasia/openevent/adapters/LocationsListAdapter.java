package org.fossasia.openevent.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.LocationActivity;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.ConstantStrings;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 8/18/2015
 */
public class LocationsListAdapter extends BaseRVAdapter<Microlocation, LocationsListAdapter.LocationViewHolder> {

    private Context context;
    public static int listPosition;

    public LocationsListAdapter(Context context, List<Microlocation> microLocations) {
        super(microLocations);
        this.context = context;
    }

    @SuppressWarnings("all")
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            DbSingleton instance = DbSingleton.getInstance();
            List<Microlocation> microlocations = instance.getMicrolocationsList();
            final ArrayList<Microlocation> filteredLocationList = new ArrayList<>();
            String query = constraint.toString().toLowerCase(Locale.getDefault());
            for (Microlocation microlocation : microlocations) {
                final String text = microlocation.getName().toLowerCase(Locale.getDefault());
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

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LocationViewHolder holder, int position) {
        final Microlocation location = getItem(position);
        holder.locationName.setText(location.getName());
        holder.locationFloor.setText(MessageFormat.format("{0}{1}",
                holder.itemView.getResources().getString(R.string.fmt_floor),
                location.getFloor()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LocationActivity.class);
                intent.putExtra(ConstantStrings.MICROLOCATIONS, location.getName());
                listPosition = holder.getAdapterPosition();
                context.startActivity(intent);
            }
        });
    }

    public void refresh() {
        clear();
        animateTo(DbSingleton.getInstance().getMicrolocationsList());
    }

    protected class LocationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.location_name)
        TextView locationName;

        @BindView(R.id.location_floor)
        TextView locationFloor;

        public LocationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
