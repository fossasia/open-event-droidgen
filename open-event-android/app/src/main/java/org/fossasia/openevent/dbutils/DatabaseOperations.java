package org.fossasia.openevent.dbutils;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.data.Version;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MananWason on 24-06-2015.
 */
public class DatabaseOperations {
    private static final String ASCENDING = " ASC";

    private static final String DESCENDING = " DESC";

    private static final String SELECT_ALL = "SELECT * FROM ";

    private static final String WHERE = " WHERE ";

    private static final String EQUAL = " == ";

    Event event;

    public ArrayList<Session> getSessionList(SQLiteDatabase mDb) {

        String sortOrder = DbContract.Sessions.ID + ASCENDING;
        Cursor cur = mDb.query(
                DbContract.Sessions.TABLE_NAME,
                DbContract.Sessions.FULL_PROJECTION,
                null,
                null,
                null,
                null,
                sortOrder
        );

        ArrayList<Session> sessions = new ArrayList<>();
        Session s;

        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            try {
                s = new Session(
                        cur.getInt(cur.getColumnIndex(DbContract.Sessions.ID)),
                        cur.getString(cur.getColumnIndex(DbContract.Sessions.TITLE)),
                        cur.getString(cur.getColumnIndex(DbContract.Sessions.SUBTITLE)),
                        cur.getString(cur.getColumnIndex(DbContract.Sessions.SUMMARY)),
                        cur.getString(cur.getColumnIndex(DbContract.Sessions.DESCRIPTION)),
                        cur.getString(cur.getColumnIndex(DbContract.Sessions.START_TIME)),
                        cur.getString(cur.getColumnIndex(DbContract.Sessions.END_TIME)),
                        cur.getString(cur.getColumnIndex(DbContract.Sessions.TYPE)),
                        cur.getInt(cur.getColumnIndex(DbContract.Sessions.TRACK)),
                        cur.getString(cur.getColumnIndex(DbContract.Sessions.LEVEL)),
                        cur.getInt(cur.getColumnIndex(DbContract.Sessions.MICROLOCATION))
                );
                sessions.add(s);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            cur.moveToNext();
        }
        cur.close();
        return sessions;
    }

    public Session getSessionById(int id, SQLiteDatabase mDb) {
        String selection = DbContract.Sessions.ID + EQUAL + id;
        Cursor cursor = mDb.query(
                DbContract.Sessions.TABLE_NAME,
                DbContract.Sessions.FULL_PROJECTION,
                selection,
                null,
                null,
                null,
                null
        );
        Session session = null;
        cursor.moveToFirst();
        //Should return only one due to UNIQUE constraint
        try {
            session = new Session(
                    cursor.getInt(cursor.getColumnIndex(DbContract.Sessions.ID)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.TITLE)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.SUBTITLE)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.SUMMARY)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.START_TIME)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.END_TIME)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.TYPE)),
                    cursor.getInt(cursor.getColumnIndex(DbContract.Sessions.TRACK)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.LEVEL)),
                    cursor.getInt(cursor.getColumnIndex(DbContract.Sessions.MICROLOCATION))
            );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cursor.close();

        return session;
    }

    public Microlocation getMicroLocationById(int id, SQLiteDatabase mDb) {
        String selection = DbContract.Microlocation.ID + EQUAL + id;
        Cursor cursor = mDb.query(
                DbContract.Microlocation.TABLE_NAME,
                DbContract.Microlocation.FULL_PROJECTION,
                selection,
                null,
                null,
                null,
                null
        );
        Microlocation location;
        cursor.moveToFirst();
        //Should return only one due to UNIQUE constraint
        location = new Microlocation(
                cursor.getInt(cursor.getColumnIndex(DbContract.Microlocation.ID)),
                cursor.getString(cursor.getColumnIndex(DbContract.Microlocation.NAME)),
                cursor.getFloat(cursor.getColumnIndex(DbContract.Microlocation.LATITUDE)),
                cursor.getFloat(cursor.getColumnIndex(DbContract.Microlocation.LONGITUDE)),
                cursor.getInt(cursor.getColumnIndex(DbContract.Microlocation.FLOOR))
        );
        cursor.close();

        return location;
    }

    public List<Speaker> getSpeakerList(SQLiteDatabase mDb) {
        //getReadOnlyDatabase();

        String sortOrder = DbContract.Speakers.ID + ASCENDING;
        Cursor cur = mDb.query(
                DbContract.Speakers.TABLE_NAME,
                DbContract.Speakers.FULL_PROJECTION,
                null,
                null,
                null,
                null,
                sortOrder
        );


        List<Speaker> speakers = new ArrayList<>();
        Speaker s;

        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            s = new Speaker(
                    cur.getInt(cur.getColumnIndex(DbContract.Speakers.ID)),
                    cur.getString(cur.getColumnIndex(DbContract.Speakers.NAME)),
                    cur.getString(cur.getColumnIndex(DbContract.Speakers.PHOTO)),
                    cur.getString(cur.getColumnIndex(DbContract.Speakers.BIO)),
                    cur.getString(cur.getColumnIndex(DbContract.Speakers.EMAIL)),
                    cur.getString(cur.getColumnIndex(DbContract.Speakers.WEB)),
                    cur.getString(cur.getColumnIndex(DbContract.Speakers.TWITTER)),
                    cur.getString(cur.getColumnIndex(DbContract.Speakers.FACEBOOK)),
                    cur.getString(cur.getColumnIndex(DbContract.Speakers.GITHUB)),
                    cur.getString(cur.getColumnIndex(DbContract.Speakers.LINKEDIN)),
                    cur.getString(cur.getColumnIndex(DbContract.Speakers.ORGANISATION)),
                    cur.getString(cur.getColumnIndex(DbContract.Speakers.POSITION)),
                    null,
                    cur.getString(cur.getColumnIndex(DbContract.Speakers.COUNTRY))

            );
            speakers.add(s);
            cur.moveToNext();
        }
        cur.close();
        return speakers;
    }

    public Version getVersionIds(SQLiteDatabase mDb) {
        //getReadOnlyDatabase();

        Cursor cursor = mDb.query(
                DbContract.Versions.TABLE_NAME,
                DbContract.Versions.FULL_PROJECTION,
                null,
                null,
                null,
                null,
                null
        );

        Version currentVersion;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            currentVersion = new Version(
                    cursor.getInt(cursor.getColumnIndex(DbContract.Versions.VER_ID)),
                    cursor.getInt(cursor.getColumnIndex(DbContract.Versions.VER_EVENT)),
                    cursor.getInt(cursor.getColumnIndex(DbContract.Versions.VER_TRACKS)),
                    cursor.getInt(cursor.getColumnIndex(DbContract.Versions.VER_SESSIONS)),
                    cursor.getInt(cursor.getColumnIndex(DbContract.Versions.VER_SPONSORS)),
                    cursor.getInt(cursor.getColumnIndex(DbContract.Versions.VER_SPEAKERS)),
                    cursor.getInt(cursor.getColumnIndex(DbContract.Versions.VER_MICROLOCATIONS))
            );
            cursor.close();
            return currentVersion;

        } else {
            return null;
        }
    }

    public Speaker getSpeakerById(int id, SQLiteDatabase mDb) {
        //getReadOnlyDatabase();
        String selection = DbContract.Speakers.ID + EQUAL + id;
        Cursor cursor = mDb.query(
                DbContract.Sessions.TABLE_NAME,
                DbContract.Sessions.FULL_PROJECTION,
                selection,
                null,
                null,
                null,
                null
        );
        Speaker speaker;
        cursor.moveToFirst();

        //Should return only one due to UNIQUE constraint
        speaker = new Speaker(
                cursor.getInt(cursor.getColumnIndex(DbContract.Speakers.ID)),
                cursor.getString(cursor.getColumnIndex(DbContract.Speakers.NAME)),
                cursor.getString(cursor.getColumnIndex(DbContract.Speakers.PHOTO)),
                cursor.getString(cursor.getColumnIndex(DbContract.Speakers.BIO)),
                cursor.getString(cursor.getColumnIndex(DbContract.Speakers.EMAIL)),
                cursor.getString(cursor.getColumnIndex(DbContract.Speakers.WEB)),
                cursor.getString(cursor.getColumnIndex(DbContract.Speakers.TWITTER)),
                cursor.getString(cursor.getColumnIndex(DbContract.Speakers.FACEBOOK)),
                cursor.getString(cursor.getColumnIndex(DbContract.Speakers.GITHUB)),
                cursor.getString(cursor.getColumnIndex(DbContract.Speakers.LINKEDIN)),
                cursor.getString(cursor.getColumnIndex(DbContract.Speakers.ORGANISATION)),
                cursor.getString(cursor.getColumnIndex(DbContract.Speakers.POSITION)),
                null,
                cursor.getString(cursor.getColumnIndex(DbContract.Speakers.COUNTRY))

        );
        cursor.close();
        return speaker;
    }

    public List<Track> getTrackList(SQLiteDatabase mDb) {
        String sortOrder = DbContract.Tracks.ID + ASCENDING;
        Cursor cursor = mDb.query(
                DbContract.Tracks.TABLE_NAME,
                DbContract.Tracks.FULL_PROJECTION,
                null,
                null,
                null,
                null,
                sortOrder
        );
        List<Track> tracks = new ArrayList<>();
        Track track;

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            track = new Track(
                    cursor.getInt(cursor.getColumnIndex(DbContract.Tracks.ID)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Tracks.NAME)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Tracks.DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Tracks.IMAGE))
            );
            tracks.add(track);
            cursor.moveToNext();
        }
        cursor.close();
        return tracks;
    }


    public ArrayList<Sponsor> getSponsorList(SQLiteDatabase mDb) {
        //getReadOnlyDatabase();
        String sortOrder = DbContract.Sponsors.NAME + ASCENDING;
        Cursor cursor = mDb.query(
                DbContract.Sponsors.TABLE_NAME,
                DbContract.Sponsors.FULL_PROJECTION,
                null,
                null,
                null,
                null,
                sortOrder
        );

        ArrayList<Sponsor> sponsors = new ArrayList<>();
        Sponsor sponsor;

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            sponsor = new Sponsor(
                    cursor.getInt(cursor.getColumnIndex(DbContract.Sponsors.ID)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sponsors.NAME)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sponsors.URL)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sponsors.LOGO_URL))
            );
            sponsors.add(sponsor);
            cursor.moveToNext();
        }
        cursor.close();
        return sponsors;
    }


    public ArrayList<Microlocation> getMicrolocationsList(SQLiteDatabase mDb) {
        String sortOrder = DbContract.Microlocation.NAME + ASCENDING;
        Cursor cursor = mDb.query(
                DbContract.Microlocation.TABLE_NAME,
                DbContract.Microlocation.FULL_PROJECTION,
                null,
                null,
                null,
                null,
                sortOrder
        );

        ArrayList<Microlocation> microlocations = new ArrayList<>();
        Microlocation microlocation;

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            microlocation = new Microlocation(
                    cursor.getInt(cursor.getColumnIndex(DbContract.Microlocation.ID)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Microlocation.NAME)),
                    cursor.getFloat(cursor.getColumnIndex(DbContract.Microlocation.LATITUDE)),
                    cursor.getFloat(cursor.getColumnIndex(DbContract.Microlocation.LONGITUDE)),
                    cursor.getInt(cursor.getColumnIndex(DbContract.Microlocation.FLOOR))
            );
            microlocations.add(microlocation);
            cursor.moveToNext();
        }
        cursor.close();
        return microlocations;
    }

    public ArrayList<Session> getSessionbyTracksname(String trackName, SQLiteDatabase mDb) {
        String tracksColumnSelection = DbContract.Tracks.NAME + EQUAL + DatabaseUtils.sqlEscapeString(trackName);
        String[] columns = {DbContract.Tracks.ID, DbContract.Tracks.NAME};
        Cursor tracksCursor = mDb.query(
                DbContract.Tracks.TABLE_NAME,
                columns,
                tracksColumnSelection,
                null,
                null,
                null,
                null
        );
        int trackSelected;
        tracksCursor.moveToFirst();
        trackSelected = tracksCursor.getInt(tracksCursor.getColumnIndex(DbContract.Speakers.ID));

        //Select columns having track id same as that obtained previously
        String sessionColumnSelection = DbContract.Sessions.TRACK + EQUAL + trackSelected;

        //Order
        String sortOrder = DbContract.Sessions.ID + ASCENDING;

        Cursor sessionCursor = mDb.query(
                DbContract.Sessions.TABLE_NAME,
                DbContract.Sessions.FULL_PROJECTION,
                sessionColumnSelection,
                null,
                null,
                null,
                sortOrder
        );

        ArrayList<Session> sessions = new ArrayList<>();
        Session session;
        sessionCursor.moveToFirst();
        //Should return only one due to UNIQUE constraint
        while (!sessionCursor.isAfterLast()) {
            try {
                session = new Session(
                        sessionCursor.getInt(sessionCursor.getColumnIndex(DbContract.Sessions.ID)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.TITLE)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.SUBTITLE)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.SUMMARY)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.DESCRIPTION)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.START_TIME)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.END_TIME)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.TYPE)),
                        sessionCursor.getInt(sessionCursor.getColumnIndex(DbContract.Sessions.TRACK)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.LEVEL)),
                        sessionCursor.getInt(sessionCursor.getColumnIndex(DbContract.Sessions.MICROLOCATION))
                );
                sessions.add(session);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            sessionCursor.moveToNext();
        }

        sessionCursor.close();
        return sessions;
    }

    public Track getTracksbyTracksname(String trackName, SQLiteDatabase mDb) {
        String tracksColumnSelection = DbContract.Tracks.NAME + EQUAL + DatabaseUtils.sqlEscapeString(trackName);

        Cursor tracksCursor = mDb.query(
                DbContract.Tracks.TABLE_NAME,
                DbContract.Tracks.FULL_PROJECTION,
                tracksColumnSelection,
                null,
                null,
                null,
                null
        );

        tracksCursor.moveToFirst();

        Track selected = new Track(
                tracksCursor.getInt(tracksCursor.getColumnIndex(DbContract.Tracks.ID)),
                tracksCursor.getString(tracksCursor.getColumnIndex(DbContract.Tracks.NAME)),
                tracksCursor.getString(tracksCursor.getColumnIndex(DbContract.Tracks.DESCRIPTION)),
                tracksCursor.getString(tracksCursor.getColumnIndex(DbContract.Tracks.IMAGE))
        );
        tracksCursor.close();
        return selected;

    }

    public void insertQueries(ArrayList<String> queries, DbHelper mDbHelper) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        for (String query : queries) {
            db.execSQL(query);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }


    public void clearDatabaseTable(String table, DbHelper mDbHelper) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {

            db.delete(table, null, null);

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();


        }
    }

    public ArrayList<Session> getSessionbySpeakersname(String speakerName, SQLiteDatabase mDb) {
        String speakerColumnSelection = DbContract.Speakers.NAME + EQUAL + DatabaseUtils.sqlEscapeString(speakerName);
        String[] columns = {DbContract.Speakers.ID, DbContract.Speakers.NAME};
        Cursor speakersCursor = mDb.query(
                DbContract.Speakers.TABLE_NAME,
                columns,
                speakerColumnSelection,
                null,
                null,
                null,
                null
        );
        int speakerSelected;
        speakersCursor.moveToFirst();
        speakerSelected = speakersCursor.getInt(speakersCursor.getColumnIndex(DbContract.Speakers.ID));

        speakersCursor.close();

        //Select columns having speaker id same as that obtained previously
        String sessionColumnSelection = DbContract.Sessionsspeakers.SPEAKER_ID + EQUAL + speakerSelected;

        //Order
        String[] columns1 = {DbContract.Sessionsspeakers.SESSION_ID};

        Cursor sessionCursor = mDb.query(
                DbContract.Sessionsspeakers.TABLE_NAME,
                columns1,
                sessionColumnSelection,
                null,
                null,
                null,
                null
        );

        ArrayList<Integer> sessionIds = new ArrayList<>();
        sessionCursor.moveToFirst();
        //Should return only one due to UNIQUE constraint
        while (!sessionCursor.isAfterLast()) {
            sessionIds.add(sessionCursor.getInt(sessionCursor.getColumnIndex(DbContract.Sessionsspeakers.SESSION_ID)));
            sessionCursor.moveToNext();
        }

        sessionCursor.close();

        ArrayList<Session> sessions = new ArrayList<>();

        for (int i = 0; i < sessionIds.size(); i++) {
            String sessionTableColumnSelection = DbContract.Sessions.ID + EQUAL + sessionIds.get(i);
            Cursor sessionTableCursor = mDb.query(
                    DbContract.Sessions.TABLE_NAME,
                    DbContract.Sessions.FULL_PROJECTION,
                    sessionTableColumnSelection,
                    null,
                    null,
                    null,
                    null
            );

            Session session;
            if (sessionTableCursor != null && sessionTableCursor.moveToFirst()) {
                try {
                    session = new Session(
                            sessionTableCursor.getInt(sessionTableCursor.getColumnIndex(DbContract.Sessions.ID)),
                            sessionTableCursor.getString(sessionTableCursor.getColumnIndex(DbContract.Sessions.TITLE)),
                            sessionTableCursor.getString(sessionTableCursor.getColumnIndex(DbContract.Sessions.SUBTITLE)),
                            sessionTableCursor.getString(sessionTableCursor.getColumnIndex(DbContract.Sessions.SUMMARY)),
                            sessionTableCursor.getString(sessionTableCursor.getColumnIndex(DbContract.Sessions.DESCRIPTION)),
                            sessionTableCursor.getString(sessionTableCursor.getColumnIndex(DbContract.Sessions.START_TIME)),
                            sessionTableCursor.getString(sessionTableCursor.getColumnIndex(DbContract.Sessions.END_TIME)),
                            sessionTableCursor.getString(sessionTableCursor.getColumnIndex(DbContract.Sessions.TYPE)),
                            sessionTableCursor.getInt(sessionTableCursor.getColumnIndex(DbContract.Sessions.TRACK)),
                            sessionTableCursor.getString(sessionTableCursor.getColumnIndex(DbContract.Sessions.LEVEL)),
                            sessionTableCursor.getInt(sessionTableCursor.getColumnIndex(DbContract.Sessions.MICROLOCATION))
                    );
                    sessions.add(session);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                sessionTableCursor.moveToNext();
                sessionTableCursor.close();
            }
        }

        return sessions;
    }

    public Speaker getSpeakerbySpeakersname(String speakerName, SQLiteDatabase mDb) {
        String speakerColumnSelection = DbContract.Speakers.NAME + EQUAL + DatabaseUtils.sqlEscapeString(speakerName);
        Cursor speakersCursor = mDb.query(
                DbContract.Speakers.TABLE_NAME,
                DbContract.Speakers.FULL_PROJECTION,
                speakerColumnSelection,
                null,
                null,
                null,
                null
        );
        Speaker speaker;
        speakersCursor.moveToFirst();
        speaker = new Speaker(
                speakersCursor.getInt(speakersCursor.getColumnIndex(DbContract.Speakers.ID)),
                speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.NAME)),
                speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.PHOTO)),
                speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.BIO)),
                speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.EMAIL)),
                speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.WEB)),
                speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.TWITTER)),
                speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.FACEBOOK)),
                speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.GITHUB)),
                speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.LINKEDIN)),
                speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.ORGANISATION)),
                speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.POSITION)),
                null,
                speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.COUNTRY))

        );

        speakersCursor.close();
        return speaker;
    }

    public Event getEventDetails(SQLiteDatabase mDb) {
        Cursor cursor = mDb.query(
                DbContract.Event.TABLE_NAME,
                DbContract.Event.FULL_PROJECTION,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            event = new Event(
                    cursor.getInt(cursor.getColumnIndex(DbContract.Event.ID)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Event.NAME)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Event.EMAIL)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Event.COLOR)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Event.LOGO_URL)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Event.START)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Event.END)),
                    cursor.getFloat(cursor.getColumnIndex(DbContract.Event.LATITUDE)),
                    cursor.getFloat(cursor.getColumnIndex(DbContract.Event.LONGITUDE)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Event.LOCATION_NAME)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Event.EVENT_URL)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Event.EVENT_SLOGAN))
            );
            cursor.close();
        }
        return event;

    }


    public Microlocation getLocationByName(String speakerName, SQLiteDatabase mDb) {
        String locationColumnSelection = DbContract.Microlocation.NAME + EQUAL + DatabaseUtils.sqlEscapeString(speakerName);
        Cursor locationCursor = mDb.query(
                DbContract.Microlocation.TABLE_NAME,
                DbContract.Microlocation.FULL_PROJECTION,
                locationColumnSelection,
                null,
                null,
                null,
                null
        );
        Microlocation location;
        locationCursor.moveToFirst();
        location = new Microlocation(
                locationCursor.getInt(locationCursor.getColumnIndex(DbContract.Microlocation.ID)),
                locationCursor.getString(locationCursor.getColumnIndex(DbContract.Microlocation.NAME)),
                locationCursor.getFloat(locationCursor.getColumnIndex(DbContract.Microlocation.LATITUDE)),
                locationCursor.getFloat(locationCursor.getColumnIndex(DbContract.Microlocation.LONGITUDE)),
                locationCursor.getInt(locationCursor.getColumnIndex(DbContract.Microlocation.FLOOR))
        );

        locationCursor.close();
        return location;
    }

    public ArrayList<Session> getSessionbyLocationname(String locationName, SQLiteDatabase mDb) {
        String locationColumnSelection = DbContract.Microlocation.NAME + EQUAL + DatabaseUtils.sqlEscapeString(locationName);
        String[] columns = {DbContract.Microlocation.ID, DbContract.Speakers.NAME};
        Cursor cursor = mDb.query(
                DbContract.Microlocation.TABLE_NAME,
                columns,
                locationColumnSelection,
                null,
                null,
                null,
                null
        );
        int locationSelected;
        cursor.moveToFirst();
        locationSelected = cursor.getInt(cursor.getColumnIndex(DbContract.Microlocation.ID));

        cursor.close();

        //Select rows having location id same as that obtained previously
        String sessionColumnSelection = DbContract.Sessions.MICROLOCATION + EQUAL + locationSelected;


        Cursor sessionCursor = mDb.query(
                DbContract.Sessions.TABLE_NAME,
                DbContract.Sessions.FULL_PROJECTION,
                sessionColumnSelection,
                null,
                null,
                null,
                null
        );

        ArrayList<Session> sessions = new ArrayList<>();
        Session s;
        sessionCursor.moveToFirst();
        if (cursor.getCount() > 0) {
            while (!sessionCursor.isAfterLast()) {
                try {
                    s = new Session(
                            sessionCursor.getInt(sessionCursor.getColumnIndex(DbContract.Sessions.ID)),
                            sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.TITLE)),
                            sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.SUBTITLE)),
                            sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.SUMMARY)),
                            sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.DESCRIPTION)),
                            sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.START_TIME)),
                            sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.END_TIME)),
                            sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.TYPE)),
                            sessionCursor.getInt(sessionCursor.getColumnIndex(DbContract.Sessions.TRACK)),
                            sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.LEVEL)),
                            sessionCursor.getInt(sessionCursor.getColumnIndex(DbContract.Sessions.MICROLOCATION))
                    );
                    sessions.add(s);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                sessionCursor.moveToNext();
            }
        } else {
            return sessions;
        }
        sessionCursor.close();
        return sessions;
    }

    public ArrayList<Speaker> getSpeakersbySessionName(String sessionName, SQLiteDatabase mDb) {
        String sessionColumnSelection = DbContract.Sessions.TITLE + EQUAL + DatabaseUtils.sqlEscapeString(sessionName);
        String[] columns = {DbContract.Sessions.ID, DbContract.Sessions.TITLE};
        Cursor sessionsCursor = mDb.query(
                DbContract.Sessions.TABLE_NAME,
                columns,
                sessionColumnSelection,
                null,
                null,
                null,
                null
        );
        int sessionSelected;
        sessionsCursor.moveToFirst();
        sessionSelected = sessionsCursor.getInt(sessionsCursor.getColumnIndex(DbContract.Sessions.ID));

        sessionsCursor.close();

        //Select columns having speaker id same as that obtained previously
        String speakersColumnSelection = DbContract.Sessionsspeakers.SESSION_ID + EQUAL + sessionSelected;

        //Order
        String[] columns1 = {DbContract.Sessionsspeakers.SPEAKER_ID};

        Cursor speakerCursor = mDb.query(
                DbContract.Sessionsspeakers.TABLE_NAME,
                columns1,
                speakersColumnSelection,
                null,
                null,
                null,
                null
        );

        ArrayList<Integer> speakersIds = new ArrayList<>();
        speakerCursor.moveToFirst();
        //Should return only one due to UNIQUE constraint
        while (!speakerCursor.isAfterLast()) {
            speakersIds.add(speakerCursor.getInt(speakerCursor.getColumnIndex(DbContract.Sessionsspeakers.SPEAKER_ID)));
            speakerCursor.moveToNext();
        }

        speakerCursor.close();

        ArrayList<Speaker> speakers = new ArrayList<>();

        for (int i = 0; i < speakersIds.size(); i++) {
            String speakerTableColumnSelection = DbContract.Speakers.ID + EQUAL + speakersIds.get(i);
            Cursor speakersCursor = mDb.query(
                    DbContract.Speakers.TABLE_NAME,
                    DbContract.Speakers.FULL_PROJECTION,
                    speakerTableColumnSelection,
                    null,
                    null,
                    null,
                    null
            );

            Speaker speaker;
            speakersCursor.moveToFirst();
            speaker = new Speaker(
                    speakersCursor.getInt(speakersCursor.getColumnIndex(DbContract.Speakers.ID)),
                    speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.NAME)),
                    speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.PHOTO)),
                    speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.BIO)),
                    speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.EMAIL)),
                    speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.WEB)),
                    speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.TWITTER)),
                    speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.FACEBOOK)),
                    speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.GITHUB)),
                    speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.LINKEDIN)),
                    speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.ORGANISATION)),
                    speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.POSITION)),
                    null,
                    speakersCursor.getString(speakersCursor.getColumnIndex(DbContract.Speakers.COUNTRY))
            );
            speakers.add(speaker);
            speakersCursor.moveToNext();
            speakersCursor.close();
        }
        return speakers;
    }

    public boolean isBookmarked(int sessionId, SQLiteDatabase db) {
        boolean number = false;
        Cursor c = null;
        try {
            c = db.rawQuery("select " + DbContract.Bookmarks.SESSION_ID + " from " + DbContract.Bookmarks.TABLE_NAME
                    + " where session_id = ?", new String[]{String.valueOf(sessionId)});

            if (c.getCount() == 1) {
                number = true;

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) c.close();
        }
        return number;
    }

    public Session getSessionbySessionname(String sessionName, SQLiteDatabase mDb) {
        String sessionColumnSelection = DbContract.Sessions.TITLE + EQUAL + DatabaseUtils.sqlEscapeString(sessionName);
        Cursor cursor = mDb.query(
                DbContract.Sessions.TABLE_NAME,
                DbContract.Sessions.FULL_PROJECTION,
                sessionColumnSelection,
                null,
                null,
                null,
                null
        );
        Session session = null;
        cursor.moveToFirst();
        try {
            session = new Session(
                    cursor.getInt(cursor.getColumnIndex(DbContract.Sessions.ID)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.TITLE)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.SUBTITLE)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.SUMMARY)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.START_TIME)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.END_TIME)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.TYPE)),
                    cursor.getInt(cursor.getColumnIndex(DbContract.Sessions.TRACK)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.LEVEL)),
                    cursor.getInt(cursor.getColumnIndex(DbContract.Sessions.MICROLOCATION))

            );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cursor.close();
        return session;
    }


    public ArrayList<Integer> getBookmarkIds(SQLiteDatabase mDb) {
        String sortOrder = DbContract.Bookmarks.SESSION_ID + ASCENDING;

        Cursor cursor = mDb.query(
                DbContract.Bookmarks.TABLE_NAME,
                DbContract.Bookmarks.FULL_PROJECTION,
                null,
                null,
                null,
                null,
                sortOrder
        );

        ArrayList<Integer> ids = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ids.add(cursor.getInt(cursor.getColumnIndex(DbContract.Bookmarks.SESSION_ID)));
            cursor.moveToNext();
        }

        cursor.close();
        return ids;
    }

    public void insertQuery(String query, DbHelper mDbHelper) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        db.execSQL(query);

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void addBookmarksToDb(int id) {
        String query_normal = "INSERT INTO %s VALUES ('%d');";
        String query = String.format(
                query_normal,
                DbContract.Bookmarks.TABLE_NAME,
                id
        );
        DbSingleton dbSingleton = DbSingleton.getInstance();
        dbSingleton.insertQuery(query);
    }

    public void deleteBookmarks(int id, SQLiteDatabase db) {
        db.delete(DbContract.Bookmarks.TABLE_NAME, DbContract.Bookmarks.SESSION_ID + "=" + id, null);

    }

    public void deleteAllRecords(String tableName, SQLiteDatabase db) {

        db.execSQL("delete from " + DatabaseUtils.sqlEscapeString(tableName));
    }

}
