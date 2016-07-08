package org.fossasia.openevent.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.fossasia.openevent.dbutils.DbContract;

/**
 * User: anupam (Opticod)
 * Date: 1/3/16
 */

public final class SortOrder {

    public static final int SORT_TYPE_NAME = 0;
    public static final int SORT_TYPE_ORGANISATION = 1;
    public static final int SORT_TYPE_COUNTRY = 2;
    private static final String PREF_SORT = "sortType";
    private static SharedPreferences prefsSort;

    private SortOrder() {
    }

    public static String sortOrderSpeaker(Activity activity) {
        prefsSort = PreferenceManager.getDefaultSharedPreferences(activity);
        switch (prefsSort.getInt(PREF_SORT, 0)) {
            case SORT_TYPE_NAME:
                return DbContract.Speakers.NAME;
            case SORT_TYPE_ORGANISATION:
                return DbContract.Speakers.ORGANISATION;
            case SORT_TYPE_COUNTRY:
                return DbContract.Speakers.COUNTRY;
            default:
                return DbContract.Speakers.NAME;
        }
    }
}
