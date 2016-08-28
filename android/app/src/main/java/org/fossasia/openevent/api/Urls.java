package org.fossasia.openevent.api;

import android.webkit.URLUtil;

/**
 * User: championswimmer
 * Date: 23/5/15
 */
public abstract class Urls {

    public static final String API_VERSION = "v1";

    /**
     * Change EVENT Id Here *
     */
    public static final int EVENT_ID = 1;

    public static final String WEB_APP_URL_BASIC = "http://fossasia.github.io/open-event-webapp/#/";

    public static final String EVENT = "event";

    public static final String SPEAKERS = "speakers";

    public static final String TRACKS = "tracks";

    public static final String SESSIONS = "sessions";

    public static final String SPONSORS = "sponsors";

    public static final String BOOKMARKS = "bookmarks";

    public static final String MICROLOCATIONS = "microlocations";

    public static final String MAP = "map";

    public static String BASE_URL = "";

    public static final String BASE_GET_URL = BASE_URL + "/api/" + API_VERSION;

    public static final String BASE_GET_URL_ALT = "https://raw.githubusercontent.com/fossasia/open-event/master/testapi/";

    public static final String APP_LINK = "https://app_link_goes_here.com";

    public static final String INVALID_LINK = "http://abc//";


    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static void setBaseUrl(String baseUrl) {
        if (URLUtil.isValidUrl(baseUrl)) {
            if (!baseUrl.endsWith("/")) {
                BASE_URL = baseUrl + "/";
            } else {
                BASE_URL = baseUrl;
            }
        } else {
            BASE_URL = "http://abc//";

        }
    }
}
