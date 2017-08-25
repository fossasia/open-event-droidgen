package org.fossasia.openevent.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.viewholders.SpeakerViewHolder;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 11-06-2015
 */
public class SpeakersListAdapter extends BaseRVAdapter<Speaker, SpeakerViewHolder> {

    private List<String> distinctOrgs = new ArrayList<>();
    private List<String> distinctCountry = new ArrayList<>();

    private Context context;
    private List<Speaker> copyOfSpeakers = new ArrayList<>();

    public SpeakersListAdapter(List<Speaker> speakers, Context context) {
        super(speakers);
        this.context = context;
    }

    public void setCopyOfSpeakers(List<Speaker> speakers) {
        this.copyOfSpeakers = speakers;
    }

    public void filter(String constraint) {
        final String query = constraint.toLowerCase(Locale.getDefault());

        List<Speaker> filteredSpeakersList = Observable.fromIterable(copyOfSpeakers)
                .filter(speaker -> speaker.getName()
                        .toLowerCase(Locale.getDefault())
                        .contains(query))
                .toList().blockingGet();

        Timber.d("Filtering done total results %d", filteredSpeakersList.size());

        if (filteredSpeakersList.isEmpty()) {
            Timber.e("No results published. There is an error in query. Check " + getClass().getName() + " filter!");
        }

        animateTo(filteredSpeakersList);
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
