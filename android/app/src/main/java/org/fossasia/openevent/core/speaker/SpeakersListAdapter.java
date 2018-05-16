package org.fossasia.openevent.core.speaker;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

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

public class SpeakersListAdapter extends BaseRVAdapter<Speaker, SpeakerViewHolder> implements StickyRecyclerHeadersAdapter<HeaderViewHolder>, SectionTitleProvider {

    private final List<String> distinctOrganizations = new ArrayList<>();
    private final List<String> distinctCountry = new ArrayList<>();
    private final List<String> organizations = new ArrayList<>();
    private final List<String> countries = new ArrayList<>();

    private int sortType;

    private final int SORTED_BY_NAME = 0;
    private final int SORTED_BY_ORGANIZATION = 1;
    private final int SORTED_BY_COUNTRY = 2;

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

        String organisation = Utils.nullToEmpty(current.getOrganisation());
        String country = Utils.nullToEmpty(current.getCountry());

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
        sortType = SharedPreferencesUtil.getInt(ConstantStrings.PREF_SORT_SPEAKER, SORTED_BY_NAME);
        Speaker current = getItem(position);

        String name = current.getName();
        String organisation = current.getOrganisation();
        String country = current.getCountry();

        if (sortType == SORTED_BY_NAME)
            return name.toUpperCase().charAt(0);
        else if (sortType == SORTED_BY_ORGANIZATION) {
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
        sortType = SharedPreferencesUtil.getInt(ConstantStrings.PREF_SORT_SPEAKER, SORTED_BY_NAME);
        String speakerData;

        if (sortType == SORTED_BY_NAME)
            speakerData = Utils.nullToEmpty(getItem(position).getName());
        else if (sortType == SORTED_BY_ORGANIZATION)
            speakerData = Utils.nullToEmpty(getItem(position).getOrganisation());
        else
            speakerData = Utils.nullToEmpty(getItem(position).getCountry());

        if (!TextUtils.isEmpty(speakerData) && sortType == SORTED_BY_NAME)
            holder.header.setText(String.valueOf(speakerData.toUpperCase().charAt(0)));
        else if (!TextUtils.isEmpty(speakerData) && (sortType == SORTED_BY_ORGANIZATION || sortType == SORTED_BY_COUNTRY))
            holder.header.setText(String.valueOf(speakerData));
        else
            holder.header.setText(String.valueOf("#"));
    }

    @Override
    public String getSectionTitle(int position) {
        //this String will be shown in a bubble for specified position
        Speaker speaker = getItem(position);
        sortType = SharedPreferencesUtil.getInt(ConstantStrings.PREF_SORT_SPEAKER, SORTED_BY_NAME);
        String title;
        switch (sortType){
            case SORTED_BY_NAME:
                title = speaker.getName();
                break;
            case SORTED_BY_ORGANIZATION:
                title = speaker.getOrganisation();
                break;
            default:
                title = speaker.getCountry();
                break;
        }

        if (TextUtils.isEmpty(title)) {
            return "-";
        }
        return String.valueOf(title.charAt(0));
    }

}
