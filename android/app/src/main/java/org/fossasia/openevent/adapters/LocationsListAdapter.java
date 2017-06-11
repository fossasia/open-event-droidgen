package org.fossasia.openevent.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.utils.Utils;
import org.fossasia.openevent.views.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;

import org.fossasia.openevent.adapters.viewholders.LocationViewHolder;

import io.reactivex.functions.Predicate;
import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 8/18/2015
 */
public class LocationsListAdapter extends BaseRVAdapter<Microlocation, LocationViewHolder> implements StickyRecyclerHeadersAdapter {

    private Context context;

    public LocationsListAdapter(Context context, List<Microlocation> microLocations) {
        super(microLocations);
        this.context = context;
    }

    @SuppressWarnings("all")
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final String query = constraint.toString().toLowerCase(Locale.getDefault());

            Realm realm = Realm.getDefaultInstance();

            RealmResults<Microlocation> locations = RealmDataRepository
                    .getInstance(realm)
                    .getLocationsSync();

            List<Microlocation> filteredMicrolocationList = Observable.fromIterable(locations)
                    .filter(new Predicate<Microlocation>() {
                        @Override
                        public boolean test(@NonNull Microlocation microlocation) throws Exception {
                            return microlocation.getName()
                                    .toLowerCase(Locale.getDefault())
                                    .contains(query);
                        }
                    }).toList().blockingGet();

            FilterResults filterResults = new FilterResults();
            filterResults.values = realm.copyFromRealm(filteredMicrolocationList);
            filterResults.count = filteredMicrolocationList.size();
            Timber.d("Filtering done total results %d", filterResults.count);

            realm.close();
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
        return new LocationViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(final LocationViewHolder holder, int position) {
        holder.bindLocation(getItem(position));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public long getHeaderId(int position) {
        return getItem(position).getName().charAt(0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView.findViewById(R.id.recyclerview_view_header);
        String locationName = Utils.checkStringEmpty(getItem(position).getName());

        if(!Utils.isEmpty(locationName)) {
            textView.setText(String.valueOf(locationName.charAt(0)));
        }
    }

}