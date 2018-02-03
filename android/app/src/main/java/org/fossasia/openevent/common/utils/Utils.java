package org.fossasia.openevent.common.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Patterns;
import android.view.View;

import org.fossasia.openevent.R;
import org.fossasia.openevent.common.ConstantStrings;
import org.fossasia.openevent.common.api.Urls;
import org.fossasia.openevent.common.date.DateConverter;
import org.fossasia.openevent.config.StrategyRegistry;
import org.fossasia.openevent.data.Event;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static boolean isTwoPane = false;

    public static boolean isEmpty(String string) {
        return string == null || string.trim().length() == 0;
    }

    public static void displayNoResults(View resultView, View recyclerView, View noView, int count) {
        if (count != 0) {
            resultView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else if (noView.getVisibility() != View.VISIBLE) {
            resultView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public static String checkStringEmpty(String string) {
        String finalString = "";
        if(!isEmpty(string)) {
            finalString = string;
        }
        return finalString;
    }

    public static float pxToDp(float px) {
        return px / Resources.getSystem().getDisplayMetrics().density;
    }

    public static float dpToPx(float dp) {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }

    public static boolean isBaseUrlEmpty(){
        return Urls.getBaseUrl().equals(Urls.EMPTY_LINK);
    }

    public static boolean isEmailValid(String email){
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    //Check password with minimum requirement
    public static boolean isPasswordValid(String password){
        return password.length() >= 6;
    }

    public static void registerIfUrlValid(SwipeRefreshLayout swipeRefreshLayout,
                                              Object object, SwipeRefreshLayout.OnRefreshListener onRefreshListener){
        if (isBaseUrlEmpty()) {
            swipeRefreshLayout.setEnabled(false);
        } else {
            StrategyRegistry.getInstance().getEventBusStrategy().getEventBus().register(object);
            swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        }
    }

    public static void unregisterIfUrlValid(Object object){
        if (!isBaseUrlEmpty()) {
            StrategyRegistry.getInstance().getEventBusStrategy().getEventBus().unregister(object);
        }
    }

    public static String getNameLetters(String name) {
        if (isEmpty(name))
            return "#";

        String[] strings = name.split(" ");
        StringBuilder nameLetters = new StringBuilder();
        for (String s : strings) {
            if (nameLetters.length() >= 2)
                return nameLetters.toString().toUpperCase();
            if (!isEmpty(s)) {
                nameLetters.append(s.trim().charAt(0));
            }
        }
        return nameLetters.toString().toUpperCase();
    }

    public static String parseImageUri(String uri) {
        if(isEmpty(uri))
            return null;

        if(uri.startsWith("http") || uri.startsWith("https"))
            return uri;

        if(uri.startsWith("/"))
            return "file:///android_asset" + uri;

        return null;
    }

    public static void setTwoPane(boolean value) {
        isTwoPane = value;
    }

    public static boolean getTwoPane() {
        return isTwoPane;
    }

    public static void setUpCustomTab(Context context, String url) {

        String URL = url;
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            URL = ("http://" + url);
        }

        CustomTabsIntent.Builder customTabsBuilder = new CustomTabsIntent.Builder();
        customTabsBuilder.setToolbarColor(ContextCompat.getColor(context, R.color.color_primary));
        customTabsBuilder.setCloseButtonIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_arrow_back_white_cct_24dp));
        customTabsBuilder.setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left);
        customTabsBuilder.setExitAnimations(context, R.anim.slide_in_left, R.anim.slide_out_right);
        CustomTabsIntent customTabsIntent = customTabsBuilder.build();
        customTabsIntent.launchUrl(context, Uri.parse(URL));
    }

    /**
     * @return Drawable id for given SocialLink if found else returns 1
     */
    public static int getSocialLinkDrawableId(String link) {
        int id = 1;
        String name = getSocialLinkName(link.toLowerCase());

        switch (name) {
            case ConstantStrings.SOCIAL_LINK_GITHUB:
                id = R.drawable.ic_github_24dp;
                break;
            case ConstantStrings.SOCIAL_LINK_TWITTER:
                id = R.drawable.ic_twitter_24dp;
                break;
            case ConstantStrings.SOCIAL_LINK_FACEBOOK:
                id = R.drawable.ic_facebook_24dp;
                break;
            case ConstantStrings.SOCIAL_LINK_LINKEDIN:
                id = R.drawable.ic_linkedin_24dp;
                break;
            case ConstantStrings.SOCIAL_LINK_YOUTUBE:
                id = R.drawable.ic_youtube_24dp;
                break;
            case ConstantStrings.SOCIAL_LINK_GOOGLE:
                id = R.drawable.ic_google_plus_24dp;
                break;
            default:
                break;
        }
        return id;
    }

    private static String getSocialLinkName(String link) {

        if (link.contains(getSocialLinkHostName(ConstantStrings.SOCIAL_LINK_GITHUB))) {
            return ConstantStrings.SOCIAL_LINK_GITHUB;
        } else if (link.contains(getSocialLinkHostName(ConstantStrings.SOCIAL_LINK_TWITTER))) {
            return ConstantStrings.SOCIAL_LINK_TWITTER;
        } else if (link.contains(getSocialLinkHostName(ConstantStrings.SOCIAL_LINK_FACEBOOK))) {
            return ConstantStrings.SOCIAL_LINK_FACEBOOK;
        } else if (link.contains(getSocialLinkHostName(ConstantStrings.SOCIAL_LINK_LINKEDIN))) {
            return ConstantStrings.SOCIAL_LINK_LINKEDIN;
        } else if (link.contains(getSocialLinkHostName(ConstantStrings.SOCIAL_LINK_YOUTUBE))) {
            return ConstantStrings.SOCIAL_LINK_YOUTUBE;
        } else if (link.contains(getSocialLinkHostName(ConstantStrings.SOCIAL_LINK_GOOGLE))) {
            return ConstantStrings.SOCIAL_LINK_GOOGLE;
        }
        return "";
    }

    public static Intent eventCalendar(Event event) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.TITLE, event.getName());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription());
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, DateConverter.formatDateWithDefault(DateConverter.FORMAT_24H, event.getStartsAt()));
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, DateConverter.formatDateWithDefault(DateConverter.FORMAT_24H, event.getEndsAt()));
        return intent;
    }

    private static String getSocialLinkHostName(String name) {
        return String.valueOf(name + ".com").toLowerCase();
    }
}
