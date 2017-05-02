package org.fossasia.openevent.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.fossasia.openevent.dbutils.DbContract;

/**
 * User: anupam (Opticod)
 * Date: 1/3/16
 */

public final class SortOrder {

    public static final int SORT_TYPE_FIRST = 0;
    public static final int SORT_TYPE_SECOND = 1;
    public static final int SORT_TYPE_THIRD = 2;
    private static final String PREF_SORT = "sortType";
    private static SharedPreferences prefsSort;

    private SortOrder() {
    }

    public static String sortOrderSpeaker(Activity activity) {
        prefsSort = PreferenceManager.getDefaultSharedPreferences(activity);
        switch (prefsSort.getInt(PREF_SORT, 0)) {
            case SORT_TYPE_FIRST:
                //By NAME
                return DbContract.Speakers.NAME;
            case SORT_TYPE_SECOND:
                //By ORGANISATION
                return DbContract.Speakers.ORGANISATION;
            case SORT_TYPE_THIRD:
                //By COUNTRY
                return DbContract.Speakers.COUNTRY;
            default:
                return DbContract.Speakers.NAME;
        }
    }

    public static String sortOrderSchedule(Context context) {
        prefsSort = PreferenceManager.getDefaultSharedPreferences(context);
        switch (prefsSort.getInt(PREF_SORT, 2)) {
            case SORT_TYPE_FIRST:
                //By TITLE
                return DbContract.Sessions.TITLE;
            case SORT_TYPE_SECOND:
                //By TRACKS
                return DbContract.Sessions.TRACK;
            case SORT_TYPE_THIRD:
                //By START_TIME
                return DbContract.Sessions.START_TIME;
            default:
                return DbContract.Sessions.START_TIME;
        }
    }

}
