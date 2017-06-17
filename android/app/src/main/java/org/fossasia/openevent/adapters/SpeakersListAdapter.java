package org.fossasia.openevent.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.utils.SortOrder;
import org.fossasia.openevent.adapters.viewholders.SpeakerViewHolder;
import org.fossasia.openevent.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 11-06-2015
 */
public class SpeakersListAdapter extends BaseRVAdapter<Speaker, SpeakerViewHolder> {

    private List<String> distinctOrgs = new ArrayList<>();
    private List<String> distinctCountry = new ArrayList<>();
    private Context context;

    @SuppressWarnings("all")
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final String query = constraint.toString().toLowerCase(Locale.getDefault());

            Realm realm = Realm.getDefaultInstance();

            List<Speaker> filteredSpeakers = realm.copyFromRealm(RealmDataRepository.getInstance(realm)
                    .getSpeakersFiltered(constraint.toString(), SortOrder.sortOrderSpeaker(context)));

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredSpeakers;
            filterResults.count = filteredSpeakers.size();
            Timber.d("Filtering done total results %d", filterResults.count);

            realm.close();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(results == null || results.values == null) {
                Timber.e("No results published. There is an error in query. Check " + getClass().getName() + " filter!");

                return;
            }

            animateTo((List<Speaker>) results.values);
        }
    };

    public SpeakersListAdapter(List<Speaker> speakers, Context context) {
        super(speakers);
        this.context = context;
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
    public Filter getFilter() {
        return filter;
    }

    @Override
    public SpeakerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_speaker, parent, false);
        SpeakerViewHolder speakerViewHolder = new SpeakerViewHolder(view, parent.getContext());
        speakerViewHolder.setIsImageCircle(false);

        return speakerViewHolder;
    }

    @Override
    public void onBindViewHolder(SpeakerViewHolder holder, final int position) {
        Speaker current = getItem(position);

        String organisation = Utils.checkStringEmpty(current.getOrganisation());
        String country = Utils.checkStringEmpty(current.getCountry());

        //adding distinct org and country (note size of array will never be greater than 2)
        if (!TextUtils.isEmpty(organisation)) {
            if (distinctOrgs.isEmpty()) {
                distinctOrgs.add(organisation);
            } else if (distinctOrgs.size() == 1 && (!organisation.equals(distinctOrgs.get(0)))) {
                distinctOrgs.add(organisation);
            }
        }

        if (!Utils.isEmpty(country)) {
            if (distinctCountry.isEmpty()) {
                distinctCountry.add(country);
            } else if (distinctCountry.size() == 1 && (!country.equals(distinctCountry.get(0)))) {
                distinctCountry.add(country);
            }
        }

        holder.bindSpeaker(current);
    }
    public int getDistinctOrgs(){
        return distinctOrgs.size();
    }

    public int getDistinctCountry(){
        return distinctCountry.size();
    }

}
