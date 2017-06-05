package org.fossasia.openevent.utils;

public class Utils {

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

    public static String parseImageUri(String uri) {
        if(isEmpty(uri))
            return null;

        if(uri.startsWith("http") || uri.startsWith("https"))
            return uri;

        if(uri.startsWith("/"))
            return "file:///android_asset" + uri;

        return null;
    }

}
