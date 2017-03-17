package org.fossasia.openevent.fragments;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.fossasia.openevent.R;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.SocialLink;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.ISO8601Date;
import java.util.List;

import butterknife.BindView;

/**
 * Created by harshita30 on 9/3/17.
 */

public class AboutFragment extends BaseFragment {

    @BindView(R.id.welcomeMessage)
    TextView welcomeMessage;
    @BindView(R.id.event_descrption)
    TextView event_descrption;
    @BindView(R.id.event_timing_details)
    TextView event_timing;
    @BindView(R.id.organiser_description)
    TextView organiser_description;
    @BindView(R.id.carddemo)
    CardView cardView;
    @BindView(R.id.item_description_img)
    ImageView mDescriptionImg;
    @BindView(R.id.readmore)
    TextView readMore;
    @BindView(R.id.readless)
    TextView readLess;
    @BindView(R.id.img_twitter)
    ImageView img_twitter;
    @BindView(R.id.img_facebook)
    ImageView img_facebook;
    @BindView(R.id.img_github)
    ImageView img_github;
    @BindView(R.id.img_linkedin)
    ImageView img_linkedin;
    @BindView(R.id.event_venue_details)
    TextView venue_details;

    private String url;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_about;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Event event = DbSingleton.getInstance().getEventDetails();
       if (event != null) {
           String date = ISO8601Date.getTimeZoneDateString(
                   ISO8601Date.getDateObject(event.getStart())).split(",")[0] + ","
                   + ISO8601Date.getTimeZoneDateString(ISO8601Date.getDateObject(event.getStart())).split(",")[1]
                   + " - "
                   + ISO8601Date.getTimeZoneDateString(ISO8601Date.getDateObject(event.getEnd())).split(",")[0] + ","
                   + ISO8601Date.getTimeZoneDateString(ISO8601Date.getDateObject(event.getEnd())).split(",")[1];

           welcomeMessage.setText(getText(R.string.welcome_message));
           organiser_description.setText(Html.fromHtml(event.getOrganizerDescription()));
           event_descrption.setText(Html.fromHtml(event.getDescription()));
           venue_details.setText(event.getLocationName());
           event_timing.setText(date);
           mDescriptionImg.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   collapseExpandTextView();
               }
           });
           readMore.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   organiser_description.setMaxLines(Integer.MAX_VALUE);
                   readMore.setVisibility(View.GONE);
                   readLess.setVisibility(View.VISIBLE);
               }
           });
           readLess.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   organiser_description.setMaxLines(4);
                   readLess.setVisibility(View.GONE);
                   readMore.setVisibility(View.VISIBLE);
               }
           });

           final List<SocialLink> socialLinks = DbSingleton.getInstance().getSocialLink();
           img_twitter.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                    setUpCustomTab(socialLinks.get(2).getLink());
               }
           });

           img_facebook.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   setUpCustomTab(socialLinks.get(3).getLink());
               }
           });

           img_github.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   setUpCustomTab(socialLinks.get(7).getLink());
               }
           });

           img_linkedin.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   setUpCustomTab(socialLinks.get(8).getLink());
               }
           });
       }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

    }

    @TargetApi(16)
    void collapseExpandTextView() {
        if (event_descrption.getVisibility() == View.GONE) {
            // it's collapsed - expand it
            event_descrption.setVisibility(View.VISIBLE);
            mDescriptionImg.setImageResource(R.drawable.ic_expand_less_black_24dp);
        } else {
            // it's expanded - collapse it
            event_descrption.setVisibility(View.GONE);
            mDescriptionImg.setImageResource(R.drawable.ic_expand_more_black_24dp);
        }

        ObjectAnimator animation = ObjectAnimator.ofInt(event_descrption, "maxLines", event_descrption.getMaxLines());
        animation.setDuration(200).start();
    }

    private void setUpCustomTab(String url) {

        Uri uri = Uri.parse(url);

        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

        intentBuilder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.color_primary));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(getContext(), R.color.color_primary_dark));

        intentBuilder.setStartAnimations(getContext(), R.anim.slide_in_right, R.anim.slide_out_left);
        intentBuilder.setExitAnimations(getContext(), android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);

        CustomTabsIntent customTabsIntent = intentBuilder.build();

        customTabsIntent.launchUrl(getActivity(), uri);
    }
}

