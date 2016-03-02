package org.fossasia.openevent.adapters;

/**
 * Created by user on 2/29/2016.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Information;
import org.fossasia.openevent.utils.SaveFilters;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
/**
 * Created by user on 02-06-2015.
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    Context context;
    List<Information> data = Collections.emptyList();

    Boolean[] checkedFilters;


    public FilterAdapter(Context context, List<Information> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
        this.checkedFilters = new Boolean[data.size()];
        this.checkedFilters = new SaveFilters().loadArray("Filters", context);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.filter_tiles, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view, context);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        Information current = data.get(i);
        myViewHolder.item.setText(current.Title);
        myViewHolder.itemCheck.setChecked(checkedFilters[i]);


       // myViewHolder.icon.setImageResource(current.itemId);


    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView item;
        CheckBox itemCheck;
        Context c;
      //  ImageView icon;

        public MyViewHolder(View itemView, Context c) {
            super(itemView);

            item = (TextView) itemView.findViewById(R.id.itemTitle);
            itemCheck = (CheckBox)itemView.findViewById(R.id.checkItem);
            this.c = c;
         //   icon = (ImageView) itemView.findViewById(R.id.lis_icon);
          //  Typeface tf = Typeface.createFromAsset(c.getAssets(), "MyriadPro-Light.ttf");
          //  item.setTypeface(tf);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int itemPos = getPosition();
            CheckBox cb = (CheckBox)v.findViewById(R.id.checkItem);
            TextView f = (TextView)v.findViewById(R.id.itemTitle);

            if(cb.isChecked()) {
                // remove the filter
                cb.setChecked(false);
                checkedFilters[itemPos] = false;

          }
            else {
                // add the filter
                cb.setChecked(true);
                checkedFilters[itemPos] = true;

            }

            // add to sharedpreference
            new SaveFilters().saveArray(checkedFilters,"Filters", c);

        }
    }
}