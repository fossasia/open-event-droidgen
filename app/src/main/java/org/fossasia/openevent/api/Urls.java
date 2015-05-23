package org.fossasia.openevent.api;

/**
 * Created by championswimmer on 23/5/15.
 */
public abstract class Urls {

    public static final String API_VERSION = "v1";

    //TODO: Make it configurable. Shouldn't be hardcoded.
    public static final String BASE_URL = "http://championswimmer.in:8080";

    public static abstract class Get {
        public static final String BASE_GET_URL = BASE_URL + "/get/api/" + API_VERSION;

        public static final String SESSIONS = BASE_GET_URL + "/sessions";
        public static final String SPONSORS = BASE_GET_URL + "/sponsors";
        public static final String SPEAKERS = BASE_GET_URL + "/speakers";
        public static final String TRACKS = BASE_GET_URL + "/tracks";
        public static final String MICROLOCATIONS = BASE_GET_URL + "/microlocations";

    }
}
