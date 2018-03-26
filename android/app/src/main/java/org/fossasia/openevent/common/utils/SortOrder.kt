package org.fossasia.openevent.common.utils

import org.fossasia.openevent.common.ConstantStrings
import org.fossasia.openevent.data.Session
import org.fossasia.openevent.data.Speaker

object SortOrder {

        private const val SORT_TYPE_FIRST = 0
        private const val SORT_TYPE_SECOND = 1
        private const val SORT_TYPE_THIRD = 2

        const val SORT_ORDER_ASCENDING = 0
        const val SORT_ORDER_DESCENDING = 1

        @JvmStatic
        fun sortOrderSpeaker(): String {
            return when (SharedPreferencesUtil.getInt(ConstantStrings.PREF_SORT_SPEAKER, 0)) {
                SORT_TYPE_FIRST ->
                    //By NAME
                    Speaker.NAME
                SORT_TYPE_SECOND ->
                    //By ORGANISATION
                    Speaker.ORGANISATION
                SORT_TYPE_THIRD ->
                    //By COUNTRY
                    Speaker.COUNTRY
                else -> Speaker.NAME
            }
        }

        @JvmStatic
        fun sortTypeSchedule(): String {
            return when (SharedPreferencesUtil.getInt(ConstantStrings.PREF_SORT_SCHEDULE, 2)) {
                SORT_TYPE_FIRST ->
                    //By TITLE
                    Session.TITLE
                SORT_TYPE_SECOND ->
                    //By TRACKS
                    Session.TRACK
                SORT_TYPE_THIRD ->
                    //By START_TIME
                    Session.START_TIME
                else -> Session.START_TIME
            }
        }

        @JvmStatic
        fun sortOrderSchedule(): Int {
            return when (SharedPreferencesUtil.getInt(ConstantStrings.PREF_SORT_ORDER, 0)) {
                SORT_ORDER_ASCENDING -> SORT_ORDER_ASCENDING

                SORT_ORDER_DESCENDING -> SORT_ORDER_DESCENDING

                else -> SORT_ORDER_ASCENDING
            }
        }
    }

