package org.fossasia.openevent.dbutils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import timber.log.Timber;

/**
 * User: championswimmer
 * Date: 17/5/15
 */
public class DbHelper extends SQLiteOpenHelper {
    public static final String TAG = DbHelper.class.getSimpleName();

    public DbHelper(Context context) {
        super(context, DbContract.DATABASE_NAME, null, DbContract.DATABASE_VERSION);
    }

    public DbHelper(Context context, String dbName) {
        super(context, dbName, null, DbContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Timber.tag(TAG).d("==== OnCreate DB ====");
        db.execSQL(DbContract.Speakers.CREATE_TABLE);
        db.execSQL(DbContract.Sponsors.CREATE_TABLE);
        db.execSQL(DbContract.Sessions.CREATE_TABLE);
        db.execSQL(DbContract.Tracks.CREATE_TABLE);
        db.execSQL(DbContract.Sessionsspeakers.CREATE_TABLE);
        db.execSQL(DbContract.Event.CREATE_TABLE);
        db.execSQL(DbContract.Microlocation.CREATE_TABLE);
        db.execSQL(DbContract.Versions.CREATE_TABLE);
        db.execSQL(DbContract.SocialLink.CREATE_TABLE);
        db.execSQL(DbContract.Bookmarks.CREATE_TABLE);
        db.execSQL(DbContract.EventDates.CREATE_TABLE);
        Timber.tag(TAG).d("==== onCreate DB Completed ====");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.tag(TAG).d("==== onUpgrade DB ====");
        db.execSQL(DbContract.Sponsors.DELETE_TABLE);
        db.execSQL(DbContract.Sessions.DELETE_TABLE);
        db.execSQL(DbContract.Tracks.DELETE_TABLE);
        db.execSQL(DbContract.Speakers.DELETE_TABLE);
        db.execSQL(DbContract.Sessionsspeakers.DELETE_TABLE);
        db.execSQL(DbContract.Event.DELETE_TABLE);
        db.execSQL(DbContract.Microlocation.DELETE_TABLE);
        db.execSQL(DbContract.Bookmarks.DELETE_TABLE);
        db.execSQL(DbContract.EventDates.DELETE_TABLE);
        db.execSQL(DbContract.Versions.DELETE_TABLE);
        db.execSQL(DbContract.SocialLink.DELETE_TABLE);
        onCreate(db);
        Timber.tag(TAG).d("==== OnUpgrade DB Completed ====");
    }
}
