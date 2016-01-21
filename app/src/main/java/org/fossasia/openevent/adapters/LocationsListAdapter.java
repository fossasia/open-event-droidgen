package org.fossasia.openevent.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.ViewHolder;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * User: MananWason
 * Date: 8/18/2015
 */
public class LocationsListAdapter extends BaseRVAdapter<Microlocation, ViewHolder.Viewholder> {
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
    private ViewHolder.SetOnClickListener listener;

    public LocationsListAdapter(List<Microlocation> microlocations) {
        super(microlocations);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public void setOnClickListener(ViewHolder.SetOnClickListener clickListener) {
        this.listener = clickListener;
    }

    @Override
    public ViewHolder.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_location, parent, false);
        ViewHolder.Viewholder viewholder = new ViewHolder.Viewholder(view);

        viewholder.setTxtView1((TextView) view.findViewById(R.id.location_name));
        viewholder.setTxtView2((TextView) view.findViewById(R.id.location_floor));

        return viewholder;
    }

    @Override
    public void onBindViewHolder(ViewHolder.Viewholder holder, int position) {
        Microlocation current = getItem(position);
        holder.getTxtView1().setText(current.getName());
        holder.getTxtView2().setText(MessageFormat.format("{0}{1}",
                holder.itemView.getResources().getString(R.string.fmt_floor),
                current.getFloor()));
        holder.setItemClickListener(listener);
    }

    public void refresh() {
        DbSingleton dbSingleton = DbSingleton.getInstance();
        clear();
        animateTo(dbSingleton.getMicrolocationsList());
    }

    /**
     * to handle click listener
     */
    public interface SetOnClickListener extends ViewHolder.SetOnClickListener {
        void onItemClick(int position, View itemView);
    }
}
