package org.fossasia.openevent.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Speaker;

import java.net.URLDecoder;
import java.util.ArrayList;

/**
 * Created by MananWason on 02-07-2015.
 */
public class SpeakerIntent {
    public String url = "dummy", reurl = "dummy", error = "none";
    private Speaker speaker;
    private Context context;
    private Activity activity;
    private CustomTabsSession customTabsSession;
    private boolean customTabsSupported;

    public SpeakerIntent(Speaker speaker, Context context, Activity activity, CustomTabsSession customTabsSession, boolean customTabsSupported) {
        this.speaker = speaker;
        this.context = context;
        this.activity = activity;
        this.customTabsSession = customTabsSession;
        this.customTabsSupported = customTabsSupported;
        customTabsSession.mayLaunchUrl(Uri.parse(speaker.getWebsite()), new Bundle(), new ArrayList<Bundle>());
    }

    public SpeakerIntent(Speaker speaker, Context context, Activity activity, boolean customTabsSupported) {
        this.speaker = speaker;
        this.context = context;
        this.activity = activity;
        this.customTabsSupported = customTabsSupported;
    }

    public void clickedImage(final ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url = "";
                if (imageView.getTag().toString().equals(view.getContext().getString(R.string.linkedin))) {
                    url = speaker.getLinkedin();
                    url = URLDecoder.decode(url);
                    String[] parts = url.split("&");
                    for (String s : parts) {
                        if (s.startsWith("url=")) {
                            url = s.substring(4);
                        }
                    }

                } else if (imageView.getTag().toString().equals(view.getContext().getString(R.string.twitter))) {
                    url = speaker.getTwitter();

                } else if (imageView.getTag().toString().equals(view.getContext().getString(R.string.fb))) {
                    url = speaker.getFacebook();

                } else if (imageView.getTag().toString().equals(view.getContext().getString(R.string.github))) {
                    url = speaker.getGithub();
                } else if (imageView.getTag().toString().equals(view.getContext().getString(R.string.website))) {
                    url = speaker.getWebsite();
                }

                if ((!url.startsWith("https://") && !url.startsWith("http://"))) {
                    url = "http://" + url;
                }

                if (customTabsSupported) {
                    CustomTabsIntent.Builder customTabsBuilder = new CustomTabsIntent.Builder(customTabsSession);
                    context = view.getContext();
                    customTabsBuilder.setToolbarColor(ContextCompat.getColor(context, R.color.color_primary));
                    customTabsBuilder.setCloseButtonIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_arrow_back_white_cct_24dp));
                    customTabsBuilder.setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left);
                    customTabsBuilder.setExitAnimations(context, R.anim.slide_in_left, R.anim.slide_out_right);
                    CustomTabsIntent customTabsIntent = customTabsBuilder.build();
                    customTabsIntent.launchUrl(activity, Uri.parse(url));
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(intent);
                }

            }
        });
    }
}