package org.fossasia.openevent.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;

/**
 * User: anupam (Opticod)
 * Date: 1/3/16
 */

public final class SortOrder {

    private static final int SORT_TYPE_FIRST = 0;
    private static final int SORT_TYPE_SECOND = 1;
    private static final int SORT_TYPE_THIRD = 2;
    private static final String PREF_SORT = "sortType";
    private static SharedPreferences prefsSort;

    private SortOrder() {
    }

    public static String sortOrderSpeaker(Context context) {
        prefsSort = PreferenceManager.getDefaultSharedPreferences(context);
        switch (prefsSort.getInt(PREF_SORT, 0)) {
            case SORT_TYPE_FIRST:
                //By NAME
                return Speaker.NAME;
            case SORT_TYPE_SECOND:
                //By ORGANISATION
                return Speaker.ORGANISATION;
            case SORT_TYPE_THIRD:
                //By COUNTRY
                return Speaker.COUNTRY;
            default:
                return Speaker.NAME;
        }
    }

    public static String sortOrderSchedule(Context context) {
        prefsSort = PreferenceManager.getDefaultSharedPreferences(context);
        switch (prefsSort.getInt(PREF_SORT, 2)) {
            case SORT_TYPE_FIRST:
                //By TITLE
                return Session.TITLE;
            case SORT_TYPE_SECOND:
                //By TRACKS
                return Session.TRACK;
            case SORT_TYPE_THIRD:
                //By START_TIME
                return Session.START_TIME;
            default:
                return Session.START_TIME;
        }
    }

}
