package org.fossasia.openevent.common.utils;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import org.fossasia.openevent.data.Session;

import java.util.List;

public final class StringUtils {

    private StringUtils() { }

    /**
     * Return an empty string if the input is null
     *
     * @param string the string which can be null
     * @return an empty string if input is null
     */
    public static String nullToEmpty(String string) {
        return TextUtils.isEmpty(string) ? "" : string;
    }

    /**
     * Joins the items of provided list separated by a delimiter
     * and returns a string
     *
     * @param list List to be joined
     * @param delimiter Delimiter separating the list
     * @param <T> Generic type of the string
     * @return String with joined list separated by delimiter
     */
    public static <T> String join(List<T> list, String delimiter) {
        String delim = "";

        // Assume amount of char in string for pre-allocation
        int avg = 20;

        StringBuilder stringBuilder = new StringBuilder(avg*list.size());
        for (T item: list) {
            stringBuilder.append(delim);
            stringBuilder.append(item.toString());

            delim = delimiter;
        }

        return stringBuilder.toString();
    }

    public static Spanned buildSession(List<Session> sessions) {
        StringBuilder sessionsBuilder = new StringBuilder();
        boolean firstSession = true;

        for (Session session : sessions) {
            if (!firstSession) {
                sessionsBuilder.append(", ");
            }
            sessionsBuilder.append(session.getTitle()).append(session.getShortAbstract());
            firstSession = false;
        }

        return Html.fromHtml(sessionsBuilder.toString());
    }
}
