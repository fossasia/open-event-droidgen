package org.fossasia.openevent.dbutils;

import android.provider.BaseColumns;

/**
 * Created by championswimmer on 17/5/15.
 */
public class DbContract {
    public DbContract() {
        //Empty constructor to prevent object creation.
    }

    public static abstract class Speakers implements BaseColumns {
        public static final String TABLE_NAME = "speakers";

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String PHOTO = "photo";
        public static final String BIO = "bio";
        public static final String EMAIL = "email";
        public static final String WEB = "web";
        public static final String FACEBOOK = "facebook";
        public static final String TWITTER = "twitter";
        public static final String GITHUB = "github";
        public static final String LINKEDIN = "linkedin";
        public static final String ORGANISATION = "organisation";
        public static final String POSITION = "position";
        public static final String COUNTRY = "country";
        public static final String SESSIONS = "sessions";

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
                COUNTRY,
                SESSIONS
        };

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME
                + " ("
                + _ID + " INTEGER PRIMARY KEY,"
                + ID + " INTEGER,"
                + NAME + " TEXT,"
                + PHOTO + " TEXT,"
                + BIO + " TEXT,"
                + EMAIL + " TEXT,"
                + WEB + " TEXT,"
                + FACEBOOK + " TEXT,"
                + TWITTER + " TEXT,"
                + GITHUB + " TEXT,"
                + LINKEDIN + " TEXT,"
                + ORGANISATION + " TEXT,"
                + POSITION + " TEXT,"
                + COUNTRY + " TEXT,"
                + SESSIONS + " TEXT,"
                + " )";
    }

    public static abstract class Sessions implements BaseColumns {
        public static final String TABLE_NAME = "sessions";

        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String SUBTITLE = "subtitle";
        public static final String SUMMARY = "summary";
        public static final String DESCRIPTION = "description";
        public static final String START_TIME = "start_time";
        public static final String END_TIME = "end_time";
        public static final String TYPE = "type";
        public static final String TRACK = "track";
        public static final String SPEAKERS = "speakers";
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
                TYPE,
                TRACK,
                SPEAKERS,
                LEVEL,
                MICROLOCATION
        };

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME
                + " ("
                + _ID + " INTEGER PRIMARY KEY,"
                + ID + " INTEGER,"
                + TITLE + " TEXT,"
                + SUBTITLE + " TEXT,"
                + SUMMARY + " TEXT,"
                + DESCRIPTION + " TEXT,"
                + START_TIME + " TEXT,"
                + END_TIME + " TEXT,"
                + TYPE + " TEXT,"
                + TRACK + " INTEGER,"
                + SPEAKERS + " TEXT,"
                + LEVEL + " TEXT,"
                + MICROLOCATION + " INTEGER,"
                + " )";
    }

    public static abstract class Tracks implements BaseColumns {
        public static final String TABLE_NAME = "sessions";

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";

        public static final String[] FULL_PROJECTION = {
                ID,
                NAME,
                DESCRIPTION
        };

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME
                        + " ("
                        + _ID + " INTEGER PRIMARY KEY,"
                        + ID + " INTEGER,"
                        + NAME + " TEXT,"
                        + DESCRIPTION + " TEXT,"
                        + " )";
    }

    //TODO: Also create tables for Sponsors

}
