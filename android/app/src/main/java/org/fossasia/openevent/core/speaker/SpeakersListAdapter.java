package org.fossasia.openevent.core.speaker;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ui.base.BaseRVAdapter;
import org.fossasia.openevent.common.ui.recyclerview.HeaderViewHolder;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.common.ConstantStrings;
import org.fossasia.openevent.common.utils.SharedPreferencesUtil;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.common.ui.recyclerview.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.List;

public class SpeakersListAdapter extends BaseRVAdapter<Speaker, SpeakerViewHolder> implements StickyRecyclerHeadersAdapter<HeaderViewHolder> {

    private final List<String> distinctOrganizations = new ArrayList<>();
    private final List<String> distinctCountry = new ArrayList<>();
    private final List<String> organizations = new ArrayList<>();
    private final List<String> countries = new ArrayList<>();

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
            if (distinctOrganizations.isEmpty()) {
                distinctOrganizations.add(organisation);
            } else if (distinctOrganizations.size() == 1 && (!organisation.equals(distinctOrganizations.get(0)))) {
                distinctOrganizations.add(organisation);
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

    public int getDistinctOrganizationsSize(){
        return distinctOrganizations.size();
    }

    public int getDistinctCountriesSize(){
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
            if (!organizations.contains(organisation)) {
                organizations.add(organisation);
            }
            return organizations.indexOf(organisation);
        } else {
            if (!countries.contains(country)) {
                countries.add(country);
            }
            return countries.indexOf(country);
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
