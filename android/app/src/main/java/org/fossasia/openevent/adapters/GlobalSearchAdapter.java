package org.fossasia.openevent.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.openevent.R;
import org.fossasia.openevent.adapters.viewholders.DividerViewHolder;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.adapters.viewholders.LocationViewHolder;
import org.fossasia.openevent.adapters.viewholders.SpeakerViewHolder;
import org.fossasia.openevent.adapters.viewholders.TrackViewHolder;

import java.util.ArrayList;
import java.util.List;

public class GlobalSearchAdapter extends BaseRVAdapter<Object, RecyclerView.ViewHolder> {

    private Context context;

    private List<Object> filteredResultList = new ArrayList<>();

    //ViewType Constants
    private final int TRACK = 0;
    private final int SPEAKER = 2;
    private final int LOCATION = 3;
    private final int DIVIDER = 4;

    public GlobalSearchAdapter(List<Object> dataList, Context context) {
        super(dataList);
        this.context = context.getApplicationContext();
        this.filteredResultList = dataList;
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
    public int getItemViewType(int position) {
        if (filteredResultList.get(position) instanceof Track) {
            return TRACK;
        } else if (filteredResultList.get(position) instanceof String) {
            return DIVIDER;
        } else if (filteredResultList.get(position) instanceof Speaker) {
            return SPEAKER;
        } else if (filteredResultList.get(position) instanceof Microlocation) {
            return LOCATION;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return filteredResultList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredResultList.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder resultHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TRACK:
                View track = inflater.inflate(R.layout.item_track, parent, false);
                resultHolder = new TrackViewHolder(track, context);
                break;
            case SPEAKER:
                View speaker = inflater.inflate(R.layout.search_item_speaker, parent, false);
                resultHolder = new SpeakerViewHolder(speaker, context);
                break;
            case LOCATION:
                View location = inflater.inflate(R.layout.item_location, parent, false);
                resultHolder = new LocationViewHolder(location, context);
                break;
            case DIVIDER:
                View header = inflater.inflate(R.layout.search_result_type_header_format, parent, false);
                resultHolder = new DividerViewHolder(header);
                break;
            default:
                //If viewType doesn't match any of the above objects no view is created
                break;
        }
        return resultHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TRACK:
                TrackViewHolder trackSearchHolder = (TrackViewHolder) holder;
                Track currentTrack = (Track) getItem(position);
                trackSearchHolder.bindTrack(currentTrack);
                break;
            case SPEAKER:
                SpeakerViewHolder speakerSearchHolder = (SpeakerViewHolder) holder;
                Speaker speaker = (Speaker) getItem(position);
                speakerSearchHolder.bindSpeaker(speaker);
                break;
            case LOCATION:
                LocationViewHolder locationSearchHolder = (LocationViewHolder) holder;
                Microlocation location = (Microlocation) getItem(position);
                locationSearchHolder.bindLocation(location);
                break;
            case DIVIDER:
                DividerViewHolder resultTypeViewHolder = (DividerViewHolder) holder;
                String headerItem = (String) getItem(position);
                resultTypeViewHolder.bindHeader(headerItem);
                break;
            default:
                //If viewType is none of the above then nothing is done
                break;
        }
    }

}
