package org.fossasia.openevent.common.utils

import android.text.Html
import android.text.Spanned
import android.text.TextUtils

import org.fossasia.openevent.data.Session

object StringUtils {

    /**
     * Return an empty string if the input is null
     *
     * @param string the string which can be null
     * @return an empty string if input is null
     */
    @JvmStatic
    fun nullToEmpty(string: String): String {
        return if (TextUtils.isEmpty(string)) "" else string
    }

    /**
     * Joins the items of provided list separated by a delimiter
     * and returns a string
     *
     * @param list List to be joined
     * @param delimiter Delimiter separating the list
     * @param <T> Generic type of the string
     * @return String with joined list separated by delimiter
    </T> */
    @JvmStatic
    fun <T> join(list: List<T>, delimiter: String): String {
        var delim = ""

        // Assume amount of char in string for pre-allocation
        val avg = 20

        val stringBuilder = StringBuilder(avg * list.size)
        for (item in list) {
            stringBuilder.append(delim)
            stringBuilder.append(item.toString())

            delim = delimiter
        }

        return stringBuilder.toString()
    }

    @JvmStatic
    fun buildSession(sessions: List<Session>): Spanned {
        val sessionsBuilder = StringBuilder()
        var firstSession = true

        for (session in sessions) {
            if (!firstSession) {
                sessionsBuilder.append(", ")
            }
            sessionsBuilder.append(session.title).append(session.shortAbstract)
            firstSession = false
        }

        return Html.fromHtml(sessionsBuilder.toString())
    }
}
