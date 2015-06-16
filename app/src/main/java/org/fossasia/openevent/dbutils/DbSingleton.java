package org.fossasia.openevent.dbutils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.data.Version;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by championswimmer on 17/5/15.
 */
public class DbSingleton {
    private static final String ASCENDING = " ASC";
    private static final String DESCENDING = " DESC";
    private static final String SELECT_ALL = "SELECT * FROM ";
    private static final String WHERE = " WHERE ";
    private static final String EQUAL = " == ";
    private static DbSingleton mInstance;
    private static Context mContext;
    private static DbHelper mDbHelper;
    private SQLiteDatabase mDb;

    private DbSingleton(Context context) {
        mContext = context;
        mDbHelper = new DbHelper(mContext);

    }

    public static void init(Context context) {
        if (mInstance == null) {
            mInstance = new DbSingleton(context);
        }
    }

    public static DbSingleton getInstance() {
        return mInstance;
    }

    private void getReadOnlyDatabase() {
        if ((mDb == null) || (!mDb.isReadOnly())) {
            mDb = mDbHelper.getReadableDatabase();
        }
    }

    public ArrayList<Session> getSessionList() throws ParseException {
        getReadOnlyDatabase();

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
                    null,
                    cur.getInt(cur.getColumnIndex(DbContract.Sessions.MICROLOCATION))
            );
            sessions.add(s);
            cur.moveToNext();
        }
        cur.close();
        return sessions;
    }

    public Session getSessionById(int id) throws ParseException {
        getReadOnlyDatabase();
        String selection = SELECT_ALL + DbContract.Sessions.TABLE_NAME +
                WHERE + DbContract.Sessions.ID + EQUAL + id;
        Cursor cursor = mDb.query(
                DbContract.Sessions.TABLE_NAME,
                DbContract.Sessions.FULL_PROJECTION,
                selection,
                null,
                null,
                null,
                null
        );
        Session session;
        cursor.moveToFirst();
        //Should return only one due to UNIQUE constraint
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
                null,
                cursor.getInt(cursor.getColumnIndex(DbContract.Sessions.MICROLOCATION))
        );
        cursor.close();

        return session;
    }

    public List<Speaker> getSpeakerList() {
        getReadOnlyDatabase();

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
            Log.d("singl", s.getName());
        }
        cur.close();
        return speakers;
    }

    public Version getVersionIds() {
        getReadOnlyDatabase();

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

    public Speaker getSpeakerById(int id) {
        getReadOnlyDatabase();
        String selection = SELECT_ALL + DbContract.Sessions.TABLE_NAME +
                WHERE + DbContract.Speakers.ID + EQUAL + id;
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

    public List<Track> getTrackList() {
        getReadOnlyDatabase();
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
                    cursor.getString(cursor.getColumnIndex(DbContract.Tracks.DESCRIPTION))
            );
            tracks.add(track);
            cursor.moveToNext();
        }
        cursor.close();
        return tracks;
    }


    public ArrayList<Sponsor> getSponsorList() {
        getReadOnlyDatabase();
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

    public void insertQueries(ArrayList<String> queries) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        for (String query : queries) {
            db.execSQL(query);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }


    public void clearDatabase(String table) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {

            db.delete(table, null, null);

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();


        }
    }

}
