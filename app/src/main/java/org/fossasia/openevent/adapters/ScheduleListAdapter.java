package org.fossasia.openevent.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.utils.ViewHolder;

import java.util.List;

/**
 * Created by md91829 on 3/3/16.
 */
public class ScheduleListAdapter extends BaseRVAdapter<Event,ViewHolder.Viewholder> {

    public ScheduleListAdapter(List<Event> eventsList) {
        super(eventsList);
    }

    private ViewHolder.SetOnClickListener listener;

    @Override
    public ViewHolder.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_schedule, parent, false);
        ViewHolder.Viewholder viewholder = new ViewHolder.Viewholder(view);
        viewholder.setTxtView1((TextView) view.findViewById(R.id.tv_title_event_schedule));
        viewholder.setTxtView2((TextView) view.findViewById(R.id.tv_details_event_schedule));
        viewholder.setImgView1((ImageView) view.findViewById(R.id.imageView_color_track_schedule));
        return viewholder;
    }

    @Override
    public void onBindViewHolder(ViewHolder.Viewholder holder, int position) {
        Event current = getItem(position);
        String title = current.getName();
        String start = current.getStart();
        String end = current.getEnd();
        String timings = start+" - "+end;
        String location = current.getLocationName();
        String details = timings + " -- " + location;
        holder.getTxtView1().setText(title);
        holder.getTxtView2().setText(details);
        holder.getImgView1().setBackgroundColor(Color.parseColor(current.getColor()));
        holder.setItemClickListener(listener);
    }

    public void setOnClickListener(ViewHolder.SetOnClickListener clickListener) {
        this.listener = clickListener;
    }

    /**
     * to handle click listener
     */
    public interface SetOnClickListener extends ViewHolder.SetOnClickListener {
        void onItemClick(int position, View itemView);
    }
}
