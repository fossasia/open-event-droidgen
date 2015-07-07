package org.fossasia.openevent.dbutils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.fossasia.openevent.data.Event;
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

    private static DbSingleton mInstance;

    private static Context mContext;

    private static DbHelper mDbHelper;

    private SQLiteDatabase mDb;
    private DatabaseOperations databaseOperations = new DatabaseOperations();

    private DbSingleton(Context context) {
        mContext = context;
        mDbHelper = new DbHelper(mContext);

    }

    /**
     * Only Exposed for testing purposes, either way Singletons suck.
     *
     * @param mDb     the readable/writable database
     * @param context A sane context (Application context please)
     * @param helper  A DB Helper
     */
    public DbSingleton(SQLiteDatabase mDb, Context context, DbHelper helper) {
        this.mDb = mDb;
        mContext = context;
        mDbHelper = helper;
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
        return databaseOperations.getSessionList(mDb);

    }

    public Event getEventDetails() {
        getReadOnlyDatabase();
        return databaseOperations.getEventDetails(mDb);
    }

    public Session getSessionById(int id) throws ParseException {
        getReadOnlyDatabase();
        return databaseOperations.getSessionById(id, mDb);
    }

    public List<Speaker> getSpeakerList() {
        getReadOnlyDatabase();
        return databaseOperations.getSpeakerList(mDb);

    }

    public Version getVersionIds() {
        getReadOnlyDatabase();
        return databaseOperations.getVersionIds(mDb);
    }

    public Speaker getSpeakerById(int id) {
        getReadOnlyDatabase();
        return databaseOperations.getSpeakerById(id, mDb);
    }

    public List<Track> getTrackList() {
        getReadOnlyDatabase();
        return databaseOperations.getTrackList(mDb);
    }


    public ArrayList<Sponsor> getSponsorList() {
        getReadOnlyDatabase();
        return databaseOperations.getSponsorList(mDb);
    }


    public ArrayList<Session> getSessionbyTracksname(String trackName) throws ParseException {
        return databaseOperations.getSessionbyTracksname(trackName, mDb);
    }

    public ArrayList<Session> getSessionbySpeakersName(String speakerName) throws ParseException {
        return databaseOperations.getSessionbySpeakersname(speakerName, mDb);
    }

    public ArrayList<Speaker> getSpeakersbySessionName(String sessionName) throws ParseException {
        return databaseOperations.getSpeakersbySessionname(sessionName, mDb);
    }


    public Track getTrackbyName(String trackName) throws ParseException {
        return databaseOperations.getTracksbyTracksname(trackName, mDb);
    }

    public Speaker getSpeakerbySpeakersname(String speakerName) throws ParseException {
        return databaseOperations.getSpeakerbySpeakersname(speakerName, mDb);
    }

    public Session getSessionbySessionname(String sessionName) throws ParseException {
        return databaseOperations.getSessionbySessionname(sessionName, mDb);
    }

    public void insertQueries(ArrayList<String> queries) {
        databaseOperations.insertQueries(queries, mDbHelper);
    }

    public void clearDatabase(String table) {
        databaseOperations.clearDatabase(table, mDbHelper);
    }

}
