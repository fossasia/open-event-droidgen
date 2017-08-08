package org.fossasia.openevent.utils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.StringRes;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Patterns;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.R;
import org.fossasia.openevent.api.Urls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static boolean isTwoPane = false;

    public static boolean isEmpty(String string) {
        return string == null || string.trim().length() == 0;
    }

    public static String checkStringEmpty(String string) {
        String finalString = "";
        if(!isEmpty(string)) {
            finalString = string;
        }
        return finalString;
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
            OpenEventApp.getEventBus().register(object);
            swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        }
    }

    public static void unregisterIfUrlValid(Object object){
        if (!isBaseUrlEmpty()) {
            OpenEventApp.getEventBus().unregister(object);
        }
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

        Uri uri = Uri.parse(url);

        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setToolbarColor(ContextCompat.getColor(context, R.color.color_primary));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.color_primary_dark));
        intentBuilder.setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left);
        intentBuilder.setExitAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        CustomTabsIntent customTabsIntent = intentBuilder.build();
        customTabsIntent.launchUrl(context, uri);
    }

    public static String getString(@StringRes int id){
        return OpenEventApp.getAppContext().getResources().getString(id);
    }

    /**
     * @return Drawable id for given SocialLink name if found else returns 1
     */
    public static int getSocialLinkDrawableId(String name){
        int id = 1;
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
}
