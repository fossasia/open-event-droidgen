package org.fossasia.openevent.common.api

import android.webkit.URLUtil

import org.fossasia.openevent.common.utils.Utils

object Urls {

    const val API_VERSION = "v1"

    /**
     * Change EVENT Id Here *
     */
    const val EVENT_ID = 1

    const val WEB_APP_URL_BASIC = "http://fossasia.github.io/open-event-webapp/#/"

    const val EVENT = "event"

    const val SPEAKERS = "speakers"

    const val TRACKS = "tracks"

    const val SESSIONS = "sessions"

    const val SPONSORS = "sponsors"

    const val BOOKMARKS = "bookmarks"

    const val MICROLOCATIONS = "microlocations"

    const val SESSION_TYPES = "session_types"

    const val MAP = "map"

    @JvmField
    var BASE_URL = "https://eventyay.com/api/v1/events/6"

    const val FACEBOOK_BASE_URL = "https://graph.facebook.com"

    const val LOKLAK_BASE_URL = "https://api.loklak.org"

    @JvmField
    val BASE_GET_URL = "$BASE_URL/api/$API_VERSION"

    const val BASE_GET_URL_ALT = "https://raw.githubusercontent.com/fossasia/open-event/master/testapi/"

    // Replace the template in getAppLink() if changing it
    private const val APP_LINK = "https://app_link_goes_here.com"

    const val INVALID_LINK = "http://abc//"

    const val EMPTY_LINK = "http://xyz//"

    private const val GOOGLE_PLAY_HOME = "https://play.google.com/store"

    var baseUrl: String
        @JvmStatic
        get() = BASE_URL
        @JvmStatic
        set(baseUrl) = if (URLUtil.isValidUrl(baseUrl)) {
            BASE_URL = if (!baseUrl.endsWith("/")) {
                "$baseUrl/"
            } else {
                baseUrl
            }
        } else {
            BASE_URL = if (!Utils.isEmpty(baseUrl)) {
                INVALID_LINK
            } else {
                EMPTY_LINK
            }
        }

    /**
     * Checks if the app link is replaced by the generator, if not
     * returns null which is to be checked by caller to make decision
     * accordingly.
     * @return String
     */
    val appLink: String
        @JvmStatic
        get() = if (APP_LINK == "https://app_link_goes_here.com") GOOGLE_PLAY_HOME else APP_LINK

}
