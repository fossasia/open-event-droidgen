package org.fossasia.openevent.common.utils;

import org.fossasia.openevent.common.ConstantStrings;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;

public final class SortOrder {

    private static final int SORT_TYPE_FIRST = 0;
    private static final int SORT_TYPE_SECOND = 1;
    private static final int SORT_TYPE_THIRD = 2;

    public static final int SORT_ORDER_ASCENDING = 0;
    public static final int SORT_ORDER_DESCENDING = 1;

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

    public static String sortTypeSchedule() {
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

    public static int sortOrderSchedule() {
        switch (SharedPreferencesUtil.getInt(ConstantStrings.PREF_SORT_ORDER, 0)) {
            case SORT_ORDER_ASCENDING:
                return SORT_ORDER_ASCENDING;

            case SORT_ORDER_DESCENDING:
                return SORT_ORDER_DESCENDING;

            default:
                return SORT_ORDER_ASCENDING;
        }
    }

}
