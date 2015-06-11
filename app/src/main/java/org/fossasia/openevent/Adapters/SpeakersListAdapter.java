package org.fossasia.openevent.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Speaker;

import java.util.List;

/**
 * Created by MananWason on 11-06-2015.
 */
public class SpeakersListAdapter extends RecyclerView.Adapter<SpeakersListAdapter.Viewholder> {

    List<Speaker> speakers;
    private Context context;

    public SpeakersListAdapter(Context context, List<Speaker> speakers) {
        this.context = context;
        this.speakers = speakers;
    }

    @Override
    public SpeakersListAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("SPEAKERADAP","");
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.speakers_item, parent, false);
        Viewholder viewholder = new Viewholder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(SpeakersListAdapter.Viewholder holder, int position) {
        Speaker current = speakers.get(position);
        //Image
        Uri uri = Uri.parse(current.getPhoto());
        //Picasso.with(context).load(uri).into(holder.speaker_image);

        //Texts
        //holder.speaker_name.setText(current.getName());

        String full_position = current.getOrganisation() + ", " + current.getPosition();
        //holder.speaker_position.setText(full_position);

        holder.speaker_bio.setText(current.getBio());
    }

    @Override
    public int getItemCount() {
        return speakers.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

       // ImageView speaker_image;
       TextView speaker_name;
//        TextView speaker_position;
        TextView speaker_bio;

        public Viewholder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);

           // speaker_image = (ImageView) itemView.findViewById(R.id.speaker_image);
          speaker_name = (TextView) itemView.findViewById(R.id.speaker_name);
//            speaker_position = (TextView) itemView.findViewById(R.id.speaker_position);
            speaker_bio = (TextView) itemView.findViewById(R.id.speaker_biog);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
