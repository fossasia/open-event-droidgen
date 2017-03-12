package org.fossasia.openevent.dbutils;

import android.provider.BaseColumns;

/**
 * Created by championswimmer on 17/5/15.
 */
public class DbContract {
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "OpenEvent.db";

    private static final String TEXT_TYPE = " TEXT";

    private static final String INT_TYPE = " INTEGER";

    private static final String REAL_TYPE = " REAL";

    private static final String COMMA_SEP = ",";

    private static final String PRIMARY_KEY = " PRIMARY KEY";

    public DbContract() {
        //Empty constructor to prevent object creation.
    }

    public static abstract class Versions implements BaseColumns {
        public static final String TABLE_NAME = "versions";

        public static final String VER_EVENT = "verevent";


        public static final String VER_MICROLOCATIONS = "vermicrolocations";

        public static final String VER_SESSIONS = "versessions";

        public static final String VER_SPEAKERS = "verspeakers";

        public static final String VER_SPONSORS = "versponsors";

        public static final String VER_TRACKS = "vertracks";

        public static final String[] FULL_PROJECTION = {
                VER_EVENT,
                VER_MICROLOCATIONS,
                VER_SESSIONS,
                VER_SPEAKERS,
                VER_SPONSORS,
                VER_TRACKS
        };

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME
                        + " ("
                        + VER_EVENT + INT_TYPE + COMMA_SEP
                        + VER_TRACKS + INT_TYPE + COMMA_SEP
                        + VER_SESSIONS + INT_TYPE + COMMA_SEP
                        + VER_SPONSORS + INT_TYPE + COMMA_SEP
                        + VER_SPEAKERS + INT_TYPE + COMMA_SEP
                        + VER_MICROLOCATIONS + INT_TYPE
                        + " );";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;


    }
    public static abstract class SocialLink implements BaseColumns {
        public static final String TABLE_NAME = "sociallinks";

        public static final String NAME = "name";

        public static final String LINK = "link";

        public static final String ID = "id";

        public static final String[] FULL_PROJECTION = {
                LINK,
                ID,
                NAME
        };

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME
                        + " ("
                        + LINK + TEXT_TYPE + COMMA_SEP
                        + ID + TEXT_TYPE + COMMA_SEP
                        + NAME + TEXT_TYPE
                        + " );";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;


    }

    public static abstract class Speakers implements BaseColumns {
        public static final String TABLE_NAME = "speakers";

        public static final String ID = "id";

        public static final String NAME = "name";

        public static final String PHOTO = "photo";

        public static final String BIO = "shortBiography";

        public static final String EMAIL = "email";

        public static final String WEB = "web";

        public static final String FACEBOOK = "facebook";

        public static final String TWITTER = "twitter";

        public static final String GITHUB = "github";

        public static final String LINKEDIN = "linkedin";

        public static final String ORGANISATION = "organisation";

        public static final String POSITION = "position";

        public static final String COUNTRY = "country";

        public static final String[] FULL_PROJECTION = {
                ID,
                NAME,
                PHOTO,
                BIO,
                EMAIL,
                WEB,
                FACEBOOK,
                TWITTER,
                GITHUB,
                LINKEDIN,
                ORGANISATION,
                POSITION,
                COUNTRY
        };

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME
                        + " ("
                        + ID + INT_TYPE + PRIMARY_KEY + COMMA_SEP
                        + NAME + TEXT_TYPE + COMMA_SEP
                        + PHOTO + TEXT_TYPE + COMMA_SEP
                        + BIO + TEXT_TYPE + COMMA_SEP
                        + EMAIL + TEXT_TYPE + COMMA_SEP
                        + WEB + TEXT_TYPE + COMMA_SEP
                        + FACEBOOK + TEXT_TYPE + COMMA_SEP
                        + TWITTER + TEXT_TYPE + COMMA_SEP
                        + GITHUB + TEXT_TYPE + COMMA_SEP
                        + LINKEDIN + TEXT_TYPE + COMMA_SEP
                        + ORGANISATION + TEXT_TYPE + COMMA_SEP
                        + POSITION + TEXT_TYPE + COMMA_SEP
                        + COUNTRY + TEXT_TYPE
                        + " );";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    public static abstract class Sessionsspeakers implements BaseColumns {
        public static final String TABLE_NAME = "sessionsspeakers";

        public static final String _ID = "_id";

        public static final String SPEAKER_ID = "speakerid";

        public static final String SESSION_ID = "sessionid";


        public static final String[] FULL_PROJECTION = {
                SESSION_ID,
                SPEAKER_ID
        };

        public static final String CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
                        + " ("
                        + _ID + INT_TYPE + PRIMARY_KEY + COMMA_SEP
                        + SESSION_ID + INT_TYPE + COMMA_SEP
                        + SPEAKER_ID + INT_TYPE
                        + " );";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }


    public static abstract class Sessions implements BaseColumns {
        public static final String TABLE_NAME = "sessions";

        public static final String ID = "id";

        public static final String TITLE = "title";

        public static final String SUBTITLE = "subtitle";

        public static final String SUMMARY = "summary";

        public static final String DESCRIPTION = "description";

        public static final String START_TIME = "starttime";

        public static final String START_DATE = "startdate";

        public static final String END_TIME = "end_time";

        public static final String TYPE = "type";

        public static final String TRACK = "track";

        public static final String LEVEL = "level";

        public static final String MICROLOCATION = "microlocation";

        public static final String[] FULL_PROJECTION = {
                ID,
                TITLE,
                SUBTITLE,
                SUMMARY,
                DESCRIPTION,
                START_TIME,
                END_TIME,
                START_DATE,
                TYPE,
                TRACK,
                LEVEL,
                MICROLOCATION
        };

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME
                        + " ("
                        + ID + INT_TYPE + PRIMARY_KEY + COMMA_SEP
                        + TITLE + TEXT_TYPE + COMMA_SEP
                        + SUBTITLE + TEXT_TYPE + COMMA_SEP
                        + SUMMARY + TEXT_TYPE + COMMA_SEP
                        + DESCRIPTION + TEXT_TYPE + COMMA_SEP
                        + START_TIME + TEXT_TYPE + COMMA_SEP
                        + END_TIME + TEXT_TYPE + COMMA_SEP
                        + START_DATE + TEXT_TYPE + COMMA_SEP
                        + TYPE + TEXT_TYPE + COMMA_SEP
                        + TRACK + INT_TYPE + COMMA_SEP
                        + LEVEL + TEXT_TYPE + COMMA_SEP
                        + MICROLOCATION + INT_TYPE
                        + " );";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;


    }

    public static abstract class Tracks implements BaseColumns {
        public static final String TABLE_NAME = "tracks";

        public static final String _ID = "_id";

        public static final String ID = "id";

        public static final String NAME = "name";

        public static final String DESCRIPTION = "description";

        public static final String IMAGE = "image";

        public static final String[] FULL_PROJECTION = {
                ID,
                NAME,
                DESCRIPTION,
                IMAGE
        };

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME
                        + " ("
                        + ID + INT_TYPE + PRIMARY_KEY + COMMA_SEP
                        + NAME + TEXT_TYPE + COMMA_SEP
                        + DESCRIPTION + TEXT_TYPE + COMMA_SEP
                        + IMAGE + TEXT_TYPE
                        + " );";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    public static abstract class Sponsors implements BaseColumns {
        public static final String TABLE_NAME = "sponsors";

        public static final String ID = "id";

        public static final String NAME = "name";

        public static final String URL = "url";

        public static final String LOGO_URL = "logo_url";

        public static final String TYPE = "type";

        public static final String LEVEL = "level";

        public static final String[] FULL_PROJECTION = {
                ID,
                NAME,
                URL,
                LOGO_URL,
                TYPE,
                LEVEL
        };

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME
                        + " ("
                        + ID + INT_TYPE + PRIMARY_KEY + COMMA_SEP
                        + NAME + TEXT_TYPE + COMMA_SEP
                        + URL + TEXT_TYPE + COMMA_SEP
                        + LOGO_URL + TEXT_TYPE + COMMA_SEP
                        + TYPE + TEXT_TYPE + COMMA_SEP
                        + LEVEL + INT_TYPE
                        + " );";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }


    public static abstract class Microlocation implements BaseColumns {
        public static final String TABLE_NAME = "microlocation";

        public static final String ID = "id";

        public static final String NAME = "name";

        public static final String LATITUDE = "latitude";

        public static final String LONGITUDE = "longitude";

        public static final String FLOOR = "floor";

        public static final String[] FULL_PROJECTION = {
                ID,
                NAME,
                LATITUDE,
                LONGITUDE,
                FLOOR

        };

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME
                        + " ("
                        + ID + INT_TYPE + PRIMARY_KEY + COMMA_SEP
                        + NAME + TEXT_TYPE + COMMA_SEP
                        + LATITUDE + REAL_TYPE + COMMA_SEP
                        + LONGITUDE + REAL_TYPE + COMMA_SEP
                        + FLOOR + INT_TYPE
                        + " );";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;


    }

    public static abstract class EventDates implements BaseColumns {
        public static final String TABLE_NAME = "eventdate";


        public static final String DATE = "date";

        public static final String[] FULL_PROJECTION = {
                DATE
        };

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME
                        + " ("
                        + DATE + TEXT_TYPE + PRIMARY_KEY
                        + " );";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;


    }

    public static abstract class Event implements BaseColumns {
        public static final String TABLE_NAME = "events";

        public static final String ID = "id";

        public static final String NAME = "name";

        public static final String EMAIL = "event";

        public static final String LOGO_URL = "logo_url";

        public static final String START = "start";

        public static final String END = "end";

        public static final String LATITUDE = "latitude";

        public static final String LONGITUDE = "longitude";

        public static final String LOCATION_NAME = "location_name";

        public static final String EVENT_URL = "event_url";

        public static final String TIMEZONE = "timezone";

        public static final String DESCRIPTION = "description";

        public static final String ORG_DESCRIPTION = "organizer_description";

        public static final String[] FULL_PROJECTION = {
                ID,
                NAME,
                EMAIL,
                LOGO_URL,
                START,
                END,
                LATITUDE,
                LONGITUDE,
                LOCATION_NAME,
                EVENT_URL,
                TIMEZONE,
                DESCRIPTION,
                ORG_DESCRIPTION
        };

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME
                        + " ("
                        + ID + INT_TYPE + PRIMARY_KEY + COMMA_SEP
                        + NAME + TEXT_TYPE + COMMA_SEP
                        + EMAIL + TEXT_TYPE + COMMA_SEP
                        + LOGO_URL + TEXT_TYPE + COMMA_SEP
                        + START + TEXT_TYPE + COMMA_SEP
                        + END + TEXT_TYPE + COMMA_SEP
                        + LATITUDE + REAL_TYPE + COMMA_SEP
                        + LONGITUDE + REAL_TYPE + COMMA_SEP
                        + LOCATION_NAME + TEXT_TYPE + COMMA_SEP
                        + EVENT_URL + TEXT_TYPE + COMMA_SEP
                        + TIMEZONE + TEXT_TYPE + COMMA_SEP
                        + DESCRIPTION + TEXT_TYPE + COMMA_SEP
                        + ORG_DESCRIPTION + TEXT_TYPE
                        + " );";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class Bookmarks implements BaseColumns {
        public static final String TABLE_NAME = "bookmarks";

        public static final String SESSION_ID = "session_id";

        public static final String[] FULL_PROJECTION = {
                SESSION_ID
        };

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME
                        + " ("
                        + SESSION_ID + INT_TYPE + PRIMARY_KEY
                        + " );";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}