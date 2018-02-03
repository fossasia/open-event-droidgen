package org.fossasia.openevent.core.speaker;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ui.Views;
import org.fossasia.openevent.common.ui.image.CircleTransform;
import org.fossasia.openevent.common.utils.Utils;
import org.fossasia.openevent.config.StrategyRegistry;
import org.fossasia.openevent.data.Speaker;

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

    @Nullable
    @BindView(R.id.linear_layout_speaker_list_info)
    protected LinearLayout speakerTextualInfo;

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
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation((Activity) this.context,
                            speakerImage, speakerImage.getTransitionName()).toBundle();
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

        final Palette.PaletteAsyncListener paletteAsyncListener = palette -> {
            Palette.Swatch swatch = palette.getVibrantSwatch();
            if (swatch != null && speakerTextualInfo != null) {
                speakerTextualInfo.setBackgroundColor(swatch.getRgb());
            }
        };

        RequestCreator requestCreator = StrategyRegistry.getInstance()
                .getHttpStrategy()
                .getPicassoWithCache()
                .load(thumbnail);

        TextDrawable drawable;
        if (isImageCircle) {
            requestCreator.transform(new CircleTransform());
            drawable = Views.getTextDrawableBuilder().round().build(Utils.getNameLetters(name),
                    ColorGenerator.MATERIAL.getColor(name));
        } else {
            drawable = Views.getTextDrawableBuilder().buildRect(Utils.getNameLetters(name),
                    ColorGenerator.MATERIAL.getColor(name));
        }

        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                speakerImage.setImageBitmap(bitmap);
                if (speakerTextualInfo != null) {
                    Palette.from(bitmap).generate(paletteAsyncListener);
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                speakerImage.setImageDrawable(drawable);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                speakerImage.setImageDrawable(drawable);
            }
        };

        requestCreator.into(target);

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
