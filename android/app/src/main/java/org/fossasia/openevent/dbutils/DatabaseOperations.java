package org.fossasia.openevent.dbutils;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Version;
import org.fossasia.openevent.data.parsingExtra.Microlocation;
import org.fossasia.openevent.data.parsingExtra.Track;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by MananWason on 24-06-2015.
 */
public class DatabaseOperations {
    public static final String TAG=DatabaseOperations.class.getSimpleName();
    private static final String ASCENDING = " ASC";

//    private static final String DESCENDING = " DESC";
//
//    private static final String SELECT_ALL = "SELECT * FROM ";
//
//    private static final String WHERE = " WHERE ";

    private static final String EQUAL = " == ";

//    private static final String LIKE = " LIKE ";

    private static final String ORDERBY = " ORDER BY ";

    private static final String IN= " IN ";

    private Event event;

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
                int microlocationId = cur.getInt(cur.getColumnIndex(DbContract.Sessions.MICROLOCATION));
                // TODO: Look for more elegent way to solve microlocation being null problem
                Microlocation microlocation;
                if (microlocationId != 0) {
                    microlocation = new Microlocation(microlocationId, getMicroLocationById(microlocationId, mDb).getName());
                } else {
                    microlocation = new Microlocation(0, "Not decided yet");
                }
                int trackId = cur.getInt(cur.getColumnIndex(DbContract.Sessions.TRACK));
                Track track = new Track(trackId, getTracksbyTracksId(trackId, mDb).getName());

                s = new Session(
                        cur.getInt(cur.getColumnIndex(DbContract.Sessions.ID)),
                        cur.getString(cur.getColumnIndex(DbContract.Sessions.TITLE)),
                        cur.getString(cur.getColumnIndex(DbContract.Sessions.SUBTITLE)),
                        cur.getString(cur.getColumnIndex(DbContract.Sessions.SUMMARY)),
                        cur.getString(cur.getColumnIndex(DbContract.Sessions.DESCRIPTION)),
                        cur.getString(cur.getColumnIndex(DbContract.Sessions.START_TIME)),
                        cur.getString(cur.getColumnIndex(DbContract.Sessions.END_TIME)),
                        cur.getString(cur.getColumnIndex(DbContract.Sessions.START_DATE)),
                        cur.getString(cur.getColumnIndex(DbContract.Sessions.TYPE)),
                        track,
                        cur.getString(cur.getColumnIndex(DbContract.Sessions.LEVEL)),
                        microlocation

                );
                sessions.add(s);
            } catch (ParseException e) {
                Timber.e("Parsing Error Occurred at DatabaseOperations::getSessionList.");
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
            int microlocationId = cursor.getInt(cursor.getColumnIndex(DbContract.Sessions.MICROLOCATION));
            Microlocation microlocation;
            if (microlocationId != 0) {
                microlocation = new Microlocation(microlocationId, getMicroLocationById(microlocationId, mDb).getName());
            } else {
                microlocation = new Microlocation(0, "Not decided yet");
            }
            int trackId = cursor.getInt(cursor.getColumnIndex(DbContract.Sessions.TRACK));
            Track track = new Track(trackId, getTracksbyTracksId(trackId, mDb).getName());

            session = new Session(
                    cursor.getInt(cursor.getColumnIndex(DbContract.Sessions.ID)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.TITLE)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.SUBTITLE)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.SUMMARY)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.START_TIME)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.END_TIME)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.START_DATE)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.TYPE)),
                    track,
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.LEVEL)),
                    microlocation
            );
        } catch (ParseException e) {
            Timber.e("Parsing Error Occurred at DatabaseOperations::getSessionById.");
        }
        cursor.close();

        return session;
    }

    public org.fossasia.openevent.data.Microlocation getMicroLocationById(int id, SQLiteDatabase mDb) {
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

        org.fossasia.openevent.data.Microlocation location = null;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            //Should return only one due to UNIQUE constraint
            location = new org.fossasia.openevent.data.Microlocation(
                    cursor.getInt(cursor.getColumnIndex(DbContract.Microlocation.ID)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Microlocation.NAME)),
                    cursor.getFloat(cursor.getColumnIndex(DbContract.Microlocation.LATITUDE)),
                    cursor.getFloat(cursor.getColumnIndex(DbContract.Microlocation.LONGITUDE)),
                    cursor.getInt(cursor.getColumnIndex(DbContract.Microlocation.FLOOR))
            );
            cursor.moveToNext();
        }
        cursor.close();

        return location;
    }

    public List<Speaker> getSpeakerList(SQLiteDatabase mDb, String sortBy) {

        String sortOrder = sortBy + ASCENDING;
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

    public List<org.fossasia.openevent.data.Track> getTrackList(SQLiteDatabase mDb) {
        String sortOrder = DbContract.Tracks.NAME + ASCENDING;
        Cursor cursor = mDb.query(
                DbContract.Tracks.TABLE_NAME,
                DbContract.Tracks.FULL_PROJECTION,
                null,
                null,
                null,
                null,
                sortOrder
        );
        List<org.fossasia.openevent.data.Track> tracks = new ArrayList<>();
        org.fossasia.openevent.data.Track track;

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            track = new org.fossasia.openevent.data.Track(
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
        String sortOrder = DbContract.Sponsors.LEVEL + ASCENDING + ", " + DbContract.Sponsors.NAME + ASCENDING;
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
                    cursor.getString(cursor.getColumnIndex(DbContract.Sponsors.LOGO_URL)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sponsors.TYPE)),
                    cursor.getInt(cursor.getColumnIndex(DbContract.Sponsors.LEVEL))

            );
            sponsor.changeSponsorTypeToString(cursor.getString(cursor.getColumnIndex(DbContract.Sponsors.TYPE)));

            sponsors.add(sponsor);
            cursor.moveToNext();
        }
        cursor.close();
        return sponsors;
    }


    public ArrayList<org.fossasia.openevent.data.Microlocation> getMicrolocationsList(SQLiteDatabase mDb) {
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

        ArrayList<org.fossasia.openevent.data.Microlocation> microlocations = new ArrayList<>();
        org.fossasia.openevent.data.Microlocation microlocation;

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            microlocation = new org.fossasia.openevent.data.Microlocation(
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
        String sessionColumnSelection = DbContract.Sessions.TRACK + EQUAL +
                DatabaseUtils.sqlEscapeString(String.valueOf(trackSelected));

        //Order
        String sortOrder = DbContract.Sessions.START_TIME + ASCENDING;

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
                int microlocationId = sessionCursor.getInt(sessionCursor.getColumnIndex(DbContract.Sessions.MICROLOCATION));
                Microlocation microlocation;
                if (microlocationId != 0) {
                    microlocation = new Microlocation(microlocationId, getMicroLocationById(microlocationId, mDb).getName());
                } else {
                    microlocation = new Microlocation(0, "Not decided yet");
                }
                int trackId = sessionCursor.getInt(sessionCursor.getColumnIndex(DbContract.Sessions.TRACK));
                Track track = new Track(trackId, getTracksbyTracksId(trackId, mDb).getName());

                session = new Session(
                        sessionCursor.getInt(sessionCursor.getColumnIndex(DbContract.Sessions.ID)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.TITLE)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.SUBTITLE)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.SUMMARY)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.DESCRIPTION)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.START_TIME)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.END_TIME)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.START_DATE)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.TYPE)),
                        track,
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.LEVEL)),
                        microlocation
                );
                sessions.add(session);
            } catch (ParseException e) {
                Timber.e("Parsing Error Occurred at DatabaseOperations::getSessionbyTracksname.");
            }
            sessionCursor.moveToNext();
        }
        tracksCursor.close();
        sessionCursor.close();
        return sessions;
    }

    public org.fossasia.openevent.data.Track getTracksbyTracksname(String trackName, SQLiteDatabase mDb) {
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

        org.fossasia.openevent.data.Track selected = new org.fossasia.openevent.data.Track(
                tracksCursor.getInt(tracksCursor.getColumnIndex(DbContract.Tracks.ID)),
                tracksCursor.getString(tracksCursor.getColumnIndex(DbContract.Tracks.NAME)),
                tracksCursor.getString(tracksCursor.getColumnIndex(DbContract.Tracks.DESCRIPTION)),
                tracksCursor.getString(tracksCursor.getColumnIndex(DbContract.Tracks.IMAGE))
        );
        tracksCursor.close();
        return selected;

    }

    public org.fossasia.openevent.data.Track getTracksbyTracksId(int id, SQLiteDatabase mDb) {
        String tracksColumnSelection = DbContract.Tracks.ID + EQUAL + DatabaseUtils.sqlEscapeString(String.valueOf(id));

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

        org.fossasia.openevent.data.Track selected = new org.fossasia.openevent.data.Track(
                tracksCursor.getInt(tracksCursor.getColumnIndex(DbContract.Tracks.ID)),
                tracksCursor.getString(tracksCursor.getColumnIndex(DbContract.Tracks.NAME)),
                tracksCursor.getString(tracksCursor.getColumnIndex(DbContract.Tracks.DESCRIPTION)),
                tracksCursor.getString(tracksCursor.getColumnIndex(DbContract.Tracks.IMAGE))
        );
        tracksCursor.close();
        return selected;

    }

    public void insertQueries(ArrayList<String> queries, DbHelper mDbHelper) {

        try {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.beginTransaction();
            for (String query : queries) {
                db.execSQL(query);
                Timber.d(query);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            Timber.d(e.getMessage());
            Timber.e("Parsing Error Occurred at DatabaseOperations::insertQueries.");
        }
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

    public void clearDatabase(DbHelper mDbHelper) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {

            mDbHelper.onUpgrade(db, 0, 0);
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

        ArrayList<Integer> sortedSessionIds = new ArrayList<>();
        sessionCursor.moveToFirst();
        //Should return only one due to UNIQUE constraint
        while (!sessionCursor.isAfterLast()) {
            sortedSessionIds.add(sessionCursor.getInt(sessionCursor.getColumnIndex(DbContract.Sessionsspeakers.SESSION_ID)));
            sessionCursor.moveToNext();
        }
        sessionCursor.close();

        ArrayList<Session> sessions = new ArrayList<>();
        StringBuilder builder =new StringBuilder(DbContract.Sessions.ID + IN +"( ");
        for (Integer sessionId:sortedSessionIds){
            builder.append(sessionId).append(',');
        }
        builder.deleteCharAt(builder.length()-1);
        builder.append(')');
        String sortOrder = DbContract.Sessions.START_TIME + ASCENDING;
        String sessionTableColumnSelection = builder.toString();
        Cursor sessionTableCursor = mDb.query(
                DbContract.Sessions.TABLE_NAME,
                DbContract.Sessions.FULL_PROJECTION,
                sessionTableColumnSelection,
                null,
                null,
                null,
                sortOrder
        );

        Session session;
        if (sessionTableCursor != null && sessionTableCursor.moveToFirst()) {
            do {
                try {
                    int microlocationId = sessionTableCursor.getInt(sessionTableCursor.getColumnIndex(DbContract.Sessions.MICROLOCATION));
                    Microlocation microlocation;
                    if (microlocationId != 0) {
                        microlocation = new Microlocation(microlocationId, getMicroLocationById(microlocationId, mDb).getName());
                    } else {
                        microlocation = new Microlocation(0, "Not decided yet");
                    }
                    int trackId = sessionTableCursor.getInt(sessionTableCursor.getColumnIndex(DbContract.Sessions.TRACK));
                    Track track = new Track(trackId, getTracksbyTracksId(trackId, mDb).getName());

                    session = new Session(
                            sessionTableCursor.getInt(sessionTableCursor.getColumnIndex(DbContract.Sessions.ID)),
                            sessionTableCursor.getString(sessionTableCursor.getColumnIndex(DbContract.Sessions.TITLE)),
                            sessionTableCursor.getString(sessionTableCursor.getColumnIndex(DbContract.Sessions.SUBTITLE)),
                            sessionTableCursor.getString(sessionTableCursor.getColumnIndex(DbContract.Sessions.SUMMARY)),
                            sessionTableCursor.getString(sessionTableCursor.getColumnIndex(DbContract.Sessions.DESCRIPTION)),
                            sessionTableCursor.getString(sessionTableCursor.getColumnIndex(DbContract.Sessions.START_TIME)),
                            sessionTableCursor.getString(sessionTableCursor.getColumnIndex(DbContract.Sessions.END_TIME)),
                            sessionTableCursor.getString(sessionTableCursor.getColumnIndex(DbContract.Sessions.START_DATE)),
                            sessionTableCursor.getString(sessionTableCursor.getColumnIndex(DbContract.Sessions.TYPE)),
                            track,
                            sessionTableCursor.getString(sessionTableCursor.getColumnIndex(DbContract.Sessions.LEVEL)),
                            microlocation
                    );
                    sessions.add(session);
                } catch (ParseException e) {
                    Timber.e("Parsing Error Occurred at DatabaseOperations::getSessionbySpeakersname.");
                }
            }while (sessionTableCursor.moveToNext());
            sessionTableCursor.close();

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
                    cursor.getString(cursor.getColumnIndex(DbContract.Event.LOGO_URL)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Event.START)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Event.END)),
                    cursor.getFloat(cursor.getColumnIndex(DbContract.Event.LATITUDE)),
                    cursor.getFloat(cursor.getColumnIndex(DbContract.Event.LONGITUDE)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Event.LOCATION_NAME)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Event.EVENT_URL)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Event.TIMEZONE)));
            cursor.close();
        }
        return event;

    }


    public org.fossasia.openevent.data.Microlocation getLocationByName(String speakerName, SQLiteDatabase mDb) {
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
        org.fossasia.openevent.data.Microlocation location;
        locationCursor.moveToFirst();
        location = new org.fossasia.openevent.data.Microlocation(
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
        String sort = DbContract.Sessions.START_TIME + ASCENDING;


        Cursor sessionCursor = mDb.query(
                DbContract.Sessions.TABLE_NAME,
                DbContract.Sessions.FULL_PROJECTION,
                sessionColumnSelection,
                null,
                null,
                null,
                sort
        );

        ArrayList<Session> sessions = new ArrayList<>();
        Session s;
        sessionCursor.moveToFirst();
        if (cursor.getCount() > 0) {
            while (!sessionCursor.isAfterLast()) {
                try {
                    int microlocationId = sessionCursor.getInt(sessionCursor.getColumnIndex(DbContract.Sessions.MICROLOCATION));
                    Microlocation microlocation;
                    if (microlocationId != 0) {
                        microlocation = new Microlocation(microlocationId, getMicroLocationById(microlocationId, mDb).getName());
                    } else {
                        microlocation = new Microlocation(0, "Not decided yet");
                    }                    int trackId = sessionCursor.getInt(sessionCursor.getColumnIndex(DbContract.Sessions.TRACK));
                    Track track = new Track(trackId, getTracksbyTracksId(trackId, mDb).getName());

                    s = new Session(
                            sessionCursor.getInt(sessionCursor.getColumnIndex(DbContract.Sessions.ID)),
                            sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.TITLE)),
                            sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.SUBTITLE)),
                            sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.SUMMARY)),
                            sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.DESCRIPTION)),
                            sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.START_TIME)),
                            sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.END_TIME)),
                            sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.START_DATE)),
                            sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.TYPE)),
                            track,
                            sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.LEVEL)),
                            microlocation
                    );
                    sessions.add(s);

                } catch (ParseException e) {
                    Timber.e("Parsing Error Occurred at DatabaseOperations::getSessionbyLocationname.");
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

        String speakersColumnSelection = DbContract.Sessionsspeakers.SESSION_ID + EQUAL + sessionSelected;

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
            Timber.e("Parsing Error Occurred at DatabaseOperations::isBookmarked.");
        } finally {
            if (c != null) c.close();
        }
        return number;
    }

    public boolean isBookmarksTableEmpty(SQLiteDatabase db) {
        boolean check = false;
        Cursor c = null;
        try {
            c = db.rawQuery("select * from " + DbContract.Bookmarks.TABLE_NAME, null);
            if (c.getCount() == 0) {
                check = true;
            }
        } catch (Exception e) {
            Timber.e("Parsing Error Occurred at DatabaseOperations::isBookmarksTableEmpty.");
        } finally {
            if (c != null) c.close();
        }
        return check;
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
            int microlocationId = cursor.getInt(cursor.getColumnIndex(DbContract.Sessions.MICROLOCATION));
            Microlocation microlocation;
            if (microlocationId != 0) {
                microlocation = new Microlocation(microlocationId, getMicroLocationById(microlocationId, mDb).getName());
            } else {
                microlocation = new Microlocation(0, "Not decided yet");
            }
            int trackId = cursor.getInt(cursor.getColumnIndex(DbContract.Sessions.TRACK));
            Track track = new Track(trackId, getTracksbyTracksId(trackId, mDb).getName());

            session = new Session(
                    cursor.getInt(cursor.getColumnIndex(DbContract.Sessions.ID)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.TITLE)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.SUBTITLE)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.SUMMARY)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.START_TIME)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.END_TIME)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.START_DATE)),
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.TYPE)),
                    track,
                    cursor.getString(cursor.getColumnIndex(DbContract.Sessions.LEVEL)),
                    microlocation

            );
        } catch (ParseException e) {
            Timber.e("Parsing Error Occurred at DatabaseOperations::getSessionbySessionname.");
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
        Timber.d(query);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void addBookmarksToDb(int id) {
        String insertQuery = "INSERT INTO %s VALUES ('%d');";
        String query = String.format(Locale.ENGLISH,
                insertQuery,
                DbContract.Bookmarks.TABLE_NAME,
                id
        );
        DbSingleton dbSingleton = DbSingleton.getInstance();
        dbSingleton.insertQuery(query);
    }

    public void deleteBookmarks(int id, SQLiteDatabase db) {
        db.delete(DbContract.Bookmarks.TABLE_NAME, DbContract.Bookmarks.SESSION_ID + "=" + id, null);

    }

    public ArrayList<Session> getSessionbyDate(String date, String sortOrder, SQLiteDatabase mDb) {

        String sessionColumnSelection = DbContract.Sessions.START_DATE + EQUAL + DatabaseUtils.sqlEscapeString(date);

        String order = sortOrder + ASCENDING;

        Cursor sessionCursor = mDb.query(
                DbContract.Sessions.TABLE_NAME,
                DbContract.Sessions.FULL_PROJECTION,
                sessionColumnSelection,
                null,
                null,
                null,
                order
        );


        ArrayList<Session> sessions = new ArrayList<>();
        Session session;
        sessionCursor.moveToFirst();
        while (!sessionCursor.isAfterLast()) {
            try {
                int microlocationId = sessionCursor.getInt(sessionCursor.getColumnIndex(DbContract.Sessions.MICROLOCATION));
                Microlocation microlocation;
                if (microlocationId != 0) {
                    microlocation = new Microlocation(microlocationId, getMicroLocationById(microlocationId, mDb).getName());
                } else {
                    microlocation = new Microlocation(0, "Not decided yet");
                }
                int trackId = sessionCursor.getInt(sessionCursor.getColumnIndex(DbContract.Sessions.TRACK));
                Track track = new Track(trackId, getTracksbyTracksId(trackId, mDb).getName());

                session = new Session(
                        sessionCursor.getInt(sessionCursor.getColumnIndex(DbContract.Sessions.ID)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.TITLE)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.SUBTITLE)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.SUMMARY)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.DESCRIPTION)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.START_TIME)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.END_TIME)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.START_DATE)),
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.TYPE)),
                        track,
                        sessionCursor.getString(sessionCursor.getColumnIndex(DbContract.Sessions.LEVEL)),
                        microlocation
                );
                sessions.add(session);
            } catch (ParseException e) {
                Timber.e("Parsing Error Occurred at DatabaseOperations::getSessionbyDatename.");
            }
            sessionCursor.moveToNext();
        }
        sessionCursor.close();
        return sessions;

    }

    public List<String> getDateList(SQLiteDatabase mDb) {
        Cursor cursor = mDb.rawQuery("SELECT * FROM " + DbContract.EventDates.TABLE_NAME + ORDERBY + DbContract.EventDates.DATE + ASCENDING + ";", null);
        List<String> dates = new ArrayList<>();
        String date;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            date = cursor.getString(cursor.getColumnIndex(DbContract.EventDates.DATE));
            dates.add(date);
            cursor.moveToNext();
        }
        cursor.close();
        return dates;
    }
}