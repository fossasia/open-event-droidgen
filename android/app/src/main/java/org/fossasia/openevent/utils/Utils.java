package org.fossasia.openevent.utils;

import android.support.v4.widget.SwipeRefreshLayout;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.api.Urls;

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
}
