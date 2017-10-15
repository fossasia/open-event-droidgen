package org.fossasia.openevent.adapters.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import android.app.Activity;
import android.app.ActivityOptions;
import android.os.Bundle;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.squareup.picasso.RequestCreator;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SpeakerDetailsActivity;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.utils.CircleTransform;
import org.fossasia.openevent.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

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

        this.context = context;

        //Attach onClickListener for ViewHolder
        itemView.setOnClickListener(view -> {
            String speakerName = speaker.getName();
            Intent intent = new Intent(this.context, SpeakerDetailsActivity.class);
            intent.putExtra(Speaker.SPEAKER, speakerName);

            try{
                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation((Activity) this.context, speakerImage, speakerImage.getTransitionName()).toBundle();
                    this.context.startActivity(intent, bundle);
                } else{
                    this.context.startActivity(intent);
                }
            }
            catch(Exception e){
                Timber.d("Speaker's transition doesnt occur");
            }

        });
    }

    public void bindSpeaker(Speaker speaker) {
        this.speaker = speaker;

        String thumbnail = Utils.parseImageUri(this.speaker.getThumbnailImageUrl());
        String name = Utils.checkStringEmpty(speaker.getName());

        if (thumbnail == null)
            thumbnail = Utils.parseImageUri(this.speaker.getPhotoUrl());

        RequestCreator requestCreator = OpenEventApp.picassoWithCache
                .load(thumbnail);

        TextDrawable drawable;
        if (isImageCircle) {
            requestCreator.transform(new CircleTransform());
            drawable = OpenEventApp.getTextDrawableBuilder().round().build(Utils.getNameLetters(name), ColorGenerator.MATERIAL.getColor(name));
        } else {
            drawable = OpenEventApp.getTextDrawableBuilder().buildRect(Utils.getNameLetters(name), ColorGenerator.MATERIAL.getColor(name));
        }

        requestCreator
                .placeholder(drawable)
                .error(drawable)
                .into(speakerImage);

        setStringField(speakerName, name);
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
