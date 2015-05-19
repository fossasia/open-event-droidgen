package org.fossasia.openevent.dbutils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Track;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by championswimmer on 17/5/15.
 */
public class DbSingleton {
    private static DbSingleton mInstance;
    private static Context mContext;
    private SQLiteDatabase mDb;
    private static SQLiteOpenHelper mDbHelper;

    private DbSingleton(Context context) {
        mContext = context;
        if (mDbHelper != null) {
            mDbHelper = new DbHelper(mContext);
        }
    }

    public static synchronized DbSingleton getInstance (Context context) {
        if (mInstance == null) {
            /* NOTE: Important to use getApplicationContext so as not to
             leak someone's Activity context if they pass you one */
            mInstance = new DbSingleton(context.getApplicationContext());
        }
        return mInstance;
    }

    private void getReadOnlyDatabase() {
        if ((mDb == null) || (!mDb.isReadOnly())) {
            mDb = mDbHelper.getReadableDatabase();
        }
    }

    public ArrayList<Session> getSessionList () throws ParseException {
        getReadOnlyDatabase();

        String sortOrder = DbContract.Sessions.ID + " ASC";
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
        while (cur.moveToNext()) {
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
                    new int[] {1,1,1}, //TODO: Parse the int array properly
                    cur.getString(cur.getColumnIndex(DbContract.Sessions.LEVEL)),
                    cur.getInt(cur.getColumnIndex(DbContract.Sessions.MICROLOCATION))
                    );
            sessions.add(s);
        }
        //TODO: Get data from the database
        return sessions;
    }

    public Session getSessionById (int id) {
        getReadOnlyDatabase();
        return null; //TODO: Write real code here
    }

    public ArrayList<Speaker> getSpeakerList () {
        getReadOnlyDatabase();

        String sortOrder = DbContract.Speakers.ID + " ASC";
        Cursor cur = mDb.query(
                DbContract.Speakers.TABLE_NAME,
                DbContract.Speakers.FULL_PROJECTION,
                null,
                null,
                null,
                null,
                sortOrder
        );


        ArrayList<Speaker> speakers = new ArrayList<>();
        Speaker s;

        cur.moveToFirst();
        while (cur.moveToNext()) {
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
                    cur.getString(cur.getColumnIndex(DbContract.Speakers.COUNTRY)),
                    //cur.getString(cur.getColumnIndex(DbContract.Speakers.SESSIONS))
                    //TODO: Parse the int array properly
                    new int[]{1,1,1}
                    );
            speakers.add(s);
        }

        //TODO: Get data from the database
        return speakers;
    }

    public Speaker getSpeakerById (int id) {
        getReadOnlyDatabase();
        return null; //TODO: Write real code here
    }

    public ArrayList<Track> getTrackList () {
        getReadOnlyDatabase();
        ArrayList<Track> tracks = new ArrayList<>();
        //TODO: Get data from database
        return tracks;
    }

    public ArrayList<Sponsor> getSponsorList() {
        getReadOnlyDatabase();
        ArrayList<Sponsor> sponsors = new ArrayList<>();
        //TODO: Get data from database
        return sponsors;
    }



}
