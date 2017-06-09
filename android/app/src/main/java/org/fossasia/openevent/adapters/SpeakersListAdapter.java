package org.fossasia.openevent.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SpeakerDetailsActivity;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.utils.SortOrder;
import org.fossasia.openevent.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 11-06-2015
 */
public class SpeakersListAdapter extends BaseRVAdapter<Speaker, SpeakersListAdapter.RecyclerViewHolder> {

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
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_speaker, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        Speaker current = getItem(position);

        String organisation = Utils.checkStringEmpty(current.getOrganisation());
        String country = Utils.checkStringEmpty(current.getCountry());
        String name = Utils.checkStringEmpty(current.getName());
        String positionString = Utils.checkStringEmpty(current.getPosition());
        String thumbnail = Utils.parseImageUri(current.getThumbnail());

        //adding distinct org and country (note size of array will never be greater than 2)
        if(!TextUtils.isEmpty(organisation)) {
            if (distinctOrgs.isEmpty()) {
                distinctOrgs.add(organisation);
            } else if (distinctOrgs.size() == 1 && (!organisation.equals(distinctOrgs.get(0)))) {
                distinctOrgs.add(organisation);
            }
        }

        if(!Utils.isEmpty(country)) {
            if (distinctCountry.isEmpty()) {
                distinctCountry.add(country);
            } else if (distinctCountry.size() == 1 && (!country.equals(distinctCountry.get(0)))) {
                distinctCountry.add(country);
            }
        }

        if (thumbnail == null) {
            thumbnail = Utils.parseImageUri(current.getPhoto());
        }
        Drawable placeholder = VectorDrawableCompat.create(context.getResources(), R.drawable.ic_account_circle_grey_24dp, null);

        if(thumbnail != null) {
            Picasso.with(holder.speakerImage.getContext())
                    .load(Uri.parse(thumbnail))
                    .placeholder(placeholder)
                    .into(holder.speakerImage);
        } else {
            holder.speakerImage.setImageDrawable(placeholder);
        }

        holder.speakerName.setText(name);
        holder.speakerDesignation.setText(String.format("%s %s", positionString, organisation));
        holder.speakerCountry.setText(country);

        holder.itemView.setOnClickListener(v -> {
            String speakerName = current.getName();
            Intent intent = new Intent(context, SpeakerDetailsActivity.class);
            intent.putExtra(Speaker.SPEAKER, speakerName);
            context.startActivity(intent);
        });
    }

    public int getDistinctOrgs(){
        return distinctOrgs.size();
    }

    public int getDistinctCountry(){
        return distinctCountry.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.speakers_list_image)
        ImageView speakerImage;

        @BindView(R.id.speakers_list_name)
        TextView speakerName;

        @BindView(R.id.speakers_list_designation)
        TextView speakerDesignation;

        @BindView(R.id.speakers_list_country)
        TextView speakerCountry;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}