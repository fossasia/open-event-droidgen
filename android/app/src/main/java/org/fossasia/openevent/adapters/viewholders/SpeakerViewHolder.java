package org.fossasia.openevent.adapters.viewholders;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SpeakerDetailsActivity;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.utils.CircleTransform;
import org.fossasia.openevent.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpeakerViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.speakers_list_image)
    protected ImageView speakerImage;

    @BindView(R.id.speakers_list_name)
    protected TextView speakerName;

    @BindView(R.id.speakers_list_designation)
    protected TextView speakerDesignation;

    @BindView(R.id.speakers_list_country)
    protected TextView speakerCountry;

    private Speaker speaker;
    private Context context;

    private boolean isImageCircle = true;

    public SpeakerViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        this.context = context.getApplicationContext();

        //Attach onClickListener for ViewHolder
        itemView.setOnClickListener(view -> {
            String speakerName = speaker.getName();
            Intent intent = new Intent(this.context, SpeakerDetailsActivity.class);
            intent.putExtra(Speaker.SPEAKER, speakerName);
            this.context.startActivity(intent);
        });
    }

    public void bindSpeaker(Speaker speaker) {
        this.speaker = speaker;

        String thumbnail = Utils.parseImageUri(this.speaker.getThumbnail());

        if (thumbnail == null)
            thumbnail = Utils.parseImageUri(this.speaker.getPhoto());

        Drawable placeholder = VectorDrawableCompat.create(context.getResources(), R.drawable.ic_account_circle_grey_24dp, null);

        if(thumbnail != null) {
            RequestCreator requestCreator = Picasso.with(speakerImage.getContext())
                    .load(Uri.parse(thumbnail))
                    .placeholder(placeholder);

            if (isImageCircle) {
                requestCreator.transform(new CircleTransform());
            }

            requestCreator.into(speakerImage);
        } else {
           speakerImage.setImageDrawable(placeholder);
        }

        setStringField(speakerName, speaker.getName());
        setStringField(speakerDesignation, String.format("%s %s", speaker.getPosition(), speaker.getOrganisation()));
        setStringField(speakerCountry, speaker.getCountry());
    }

    private void setStringField(TextView textView, String field) {
        if (textView == null)
            return;

        if (!TextUtils.isEmpty(field.trim())) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(field);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    public void setIsImageCircle(boolean imageCircle) {
        isImageCircle = imageCircle;
    }
}
