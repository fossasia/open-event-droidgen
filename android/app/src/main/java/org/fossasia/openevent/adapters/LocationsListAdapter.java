package org.fossasia.openevent.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.viewholders.HeaderViewHolder;
import org.fossasia.openevent.adapters.viewholders.LocationViewHolder;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.utils.Utils;
import org.fossasia.openevent.views.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 8/18/2015
 */
public class LocationsListAdapter extends BaseRVAdapter<Microlocation, LocationViewHolder> implements StickyRecyclerHeadersAdapter<HeaderViewHolder> {

    private Context context;
    private List<Microlocation> copyOfLocations = new ArrayList<>();

    public LocationsListAdapter(Context context, List<Microlocation> microLocations) {
        super(microLocations);
        this.context = context;
    }

    public void setCopyOfTracks(List<Microlocation> locations) {
        this.copyOfLocations = locations;
    }

    public void filter(String constraint) {
        final String query = constraint.toLowerCase(Locale.getDefault());

        List<Microlocation> filteredLocationsList = Observable.fromIterable(copyOfLocations)
                .filter(microlocation -> microlocation.getName()
                        .toLowerCase(Locale.getDefault())
                        .contains(query))
                .toList().blockingGet();

        Timber.d("Filtering done total results %d", filteredLocationsList.size());

        if (filteredLocationsList.isEmpty()) {
            Timber.e("No results published. There is an error in query. Check " + getClass().getName() + " filter!");
        }

        animateTo(filteredLocationsList);
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
        return getItem(position).getName().toUpperCase().charAt(0);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_header, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder holder, int position) {
        String locationName = Utils.checkStringEmpty(getItem(position).getName());
        if (!Utils.isEmpty(locationName)) {
            holder.header.setText(String.valueOf(locationName.toUpperCase().charAt(0)));
        }
    }
}