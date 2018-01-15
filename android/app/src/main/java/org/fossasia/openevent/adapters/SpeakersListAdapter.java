package org.fossasia.openevent.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.viewholders.HeaderViewHolder;
import org.fossasia.openevent.adapters.viewholders.SpeakerViewHolder;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.SharedPreferencesUtil;
import org.fossasia.openevent.utils.Utils;
import org.fossasia.openevent.views.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * User: MananWason
 * Date: 11-06-2015
 */
public class SpeakersListAdapter extends BaseRVAdapter<Speaker, SpeakerViewHolder> implements StickyRecyclerHeadersAdapter<HeaderViewHolder> {

    private List<String> distinctOrgs = new ArrayList<>();
    private List<String> distinctCountry = new ArrayList<>();
    private List<String> Orgs = new ArrayList<>();
    private List<String> Country = new ArrayList<>();

    private int sortType;

    public SpeakersListAdapter(List<Speaker> speakers) {
        super(speakers);
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

    @Override
    public long getHeaderId(int position) {
        sortType = SharedPreferencesUtil.getInt(ConstantStrings.PREF_SORT_SPEAKER, 0);
        Speaker current = getItem(position);

        String name = current.getName();
        String organisation = current.getOrganisation();
        String country = current.getCountry();

        if (sortType == 0)
            return name.toUpperCase().charAt(0);
        else if (sortType == 1) {
            if (!Orgs.contains(organisation)) {
                Orgs.add(organisation);
            }
            return Orgs.indexOf(organisation);
        } else {
            if (!Country.contains(country)) {
                Country.add(country);
            }
            return Country.indexOf(country);
        }
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_header, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder holder, int position) {
        sortType = SharedPreferencesUtil.getInt(ConstantStrings.PREF_SORT_SPEAKER, 0);
        String speakerData;

        if (sortType == 0)
            speakerData = Utils.checkStringEmpty(getItem(position).getName());
        else if (sortType == 1)
            speakerData = Utils.checkStringEmpty(getItem(position).getOrganisation());
        else
            speakerData = Utils.checkStringEmpty(getItem(position).getCountry());

        if (!TextUtils.isEmpty(speakerData) && sortType == 0)
            holder.header.setText(String.valueOf(speakerData.toUpperCase().charAt(0)));
        else if (!TextUtils.isEmpty(speakerData) && (sortType == 1 || sortType == 2))
            holder.header.setText(String.valueOf(speakerData));
        else
            holder.header.setText(String.valueOf("#"));
    }

}
