package org.fossasia.openevent.views;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.content.ContextCompat;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

import org.fossasia.openevent.R;

/**
 * Created by saket on 1/16/17.
 */

public class CustomTabsSpan extends ClickableSpan {

    private final String mURL;
    private Context context;
    private Activity activity;
    private CustomTabsSession cTSession;

    public CustomTabsSpan(String url, Context context, Activity activity, CustomTabsSession cTSession) {
        super();
        this.mURL = url;
        this.context = context;
        this.activity = activity;
        this.cTSession = cTSession;
    }

    public String getURL() {
        return mURL;
    }

    @Override
    public void onClick(View widget) {
        Log.d("Clicked", this.getURL());

        CustomTabsIntent.Builder customTabsBuilder = new CustomTabsIntent.Builder(cTSession);
        customTabsBuilder.setToolbarColor(ContextCompat.getColor(context, R.color.color_primary));
        customTabsBuilder.setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left);
        customTabsBuilder.setExitAnimations(context, R.anim.slide_in_left, R.anim.slide_out_right);
        CustomTabsIntent customTabsIntent = customTabsBuilder.build();
        customTabsIntent.launchUrl(activity, Uri.parse(mURL));
    }
}
