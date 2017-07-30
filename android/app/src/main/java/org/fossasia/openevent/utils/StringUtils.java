package org.fossasia.openevent.utils;

import android.text.TextUtils;

import java.util.List;

/**
 * User: mohit
 * Date: 25/1/16
 */
public final class StringUtils {

    private StringUtils() {
    }

    /**
     * Return an empty string if the input is null
     *
     * @param string the string which can be null
     * @return an empty string if input is null
     */
    public static String optionalString(String string) {
        return TextUtils.isEmpty(string) ? "" : string;
    }

    /**
     * Joins the items of provided list separated by a delimiter
     * and returns a string
     *
     * @param list      List to be joined
     * @param delimiter Delimiter separating the list
     * @param <T>       Generic type of the string
     * @return String with joined list separated by delimiter
     */
    public static <T> String join(List<T> list, String delimiter) {
        String delim = "";

        // Assume amount of char in string for pre-allocation
        int avg = 20;

        StringBuilder stringBuilder = new StringBuilder(avg * list.size());
        for (T item : list) {
            stringBuilder.append(delim);
            stringBuilder.append(item.toString());

            delim = delimiter;
        }

        return stringBuilder.toString();
    }
}
