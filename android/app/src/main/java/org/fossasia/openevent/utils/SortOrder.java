package org.fossasia.openevent.utils;

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

    private SortOrder() {
    }

    public static String sortOrderSpeaker() {
        switch (SharedPreferencesUtil.getInt(ConstantStrings.PREF_SORT_SPEAKER, 0)) {
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

    public static String sortOrderSchedule() {
        switch (SharedPreferencesUtil.getInt(ConstantStrings.PREF_SORT_SCHEDULE, 2)) {
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
