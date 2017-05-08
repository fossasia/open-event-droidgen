package org.fossasia.openevent.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SpeakerDetailsActivity;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.CircleTransform;
import org.fossasia.openevent.utils.NetworkUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static org.fossasia.openevent.utils.SortOrder.sortOrderSpeaker;

public class SessionSpeakerListAdapter extends BaseRVAdapter<Speaker, SessionSpeakerListAdapter.RecyclerViewHolder> {

    private Activity activity;

    public SessionSpeakerListAdapter(List<Speaker> speakers, Activity activity) {
        super(speakers);
        this.activity = activity;
    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session_speaker, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        final Speaker current = getItem(position);

        NetworkUtils.isActiveInternetPresentObservable()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {

                    @Override
                    public void accept(@NonNull Boolean isActive) throws Exception {
                        if(!isActive) {
                            Picasso.with(holder.speakerImage.getContext())
                                    .cancelTag("ONLINE");

                            Picasso.with(holder.speakerImage.getContext())
                                    .load(Uri.parse(current.getThumbnail()))
                                    .placeholder(VectorDrawableCompat.create(activity.getResources(), R.drawable.ic_account_circle_grey_24dp, null))
                                    .transform(new CircleTransform())
                                    .networkPolicy(NetworkPolicy.OFFLINE)
                                    .into(holder.speakerImage);
                        }
                    }
                });

        Picasso.with(holder.speakerImage.getContext())
                .load(Uri.parse(current.getThumbnail()))
                .placeholder(VectorDrawableCompat.create(activity.getResources(), R.drawable.ic_account_circle_grey_24dp, null))
                .transform(new CircleTransform())
                .tag("ONLINE")
                .into(holder.speakerImage);

        holder.speakerName.setText(TextUtils.isEmpty(current.getName()) ? "" : current.getName());
        holder.speakerDesignation.setText(String.format("%s %s", current.getPosition(), current.getOrganisation()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String speakerName = current.getName();
                Intent intent = new Intent(activity, SpeakerDetailsActivity.class);
                intent.putExtra(Speaker.SPEAKER, speakerName);
                activity.startActivity(intent);
            }
        });
    }

    public void refresh() {
        clear();
        DbSingleton.getInstance().getSpeakerListObservable(sortOrderSpeaker(activity))
                .subscribe(new Consumer<List<Speaker>>() {
                    @Override
                    public void accept(@NonNull List<Speaker> speakers) throws Exception {
                        animateTo(speakers);
                    }
                });
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.speakers_list_image)
        ImageView speakerImage;

        @BindView(R.id.speakers_list_name)
        TextView speakerName;

        @BindView(R.id.speakers_list_designation)
        TextView speakerDesignation;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}