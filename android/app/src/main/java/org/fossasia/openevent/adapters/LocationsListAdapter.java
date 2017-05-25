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
import org.fossasia.openevent.views.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 8/18/2015
 */
public class LocationsListAdapter extends BaseRVAdapter<Microlocation, LocationsListAdapter.LocationViewHolder> implements StickyRecyclerHeadersAdapter {

    private Context context;
    private CompositeDisposable disposable;

    public LocationsListAdapter(Context context, List<Microlocation> microLocations) {
        super(microLocations);
        this.context = context;
    }

    @SuppressWarnings("all")
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            DbSingleton instance = DbSingleton.getInstance();
            List<Microlocation> microlocations = instance.getMicrolocationList();
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

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, LocationActivity.class);
            intent.putExtra(ConstantStrings.MICROLOCATIONS, location.getName());
            holder.getAdapterPosition();
            context.startActivity(intent);
        });
    }

    public void refresh() {
        clear();
        disposable.add(DbSingleton.getInstance().getMicrolocationListObservable()
                .subscribe(microlocations -> animateTo(microlocations)));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        disposable = new CompositeDisposable();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if(disposable != null && !disposable.isDisposed())
            disposable.dispose();
    }

    @Override
    public long getHeaderId(int position) {
        return getItem(position).getName().charAt(0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_header, parent, false);
        return new RecyclerView.ViewHolder(view) {};
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView.findViewById(R.id.recyclerview_view_header);
        textView.setText(String.valueOf(getItem(position).getName().charAt(0)));
    }

    class LocationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.location_name)
        TextView locationName;

        @BindView(R.id.location_floor)
        TextView locationFloor;

        LocationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
