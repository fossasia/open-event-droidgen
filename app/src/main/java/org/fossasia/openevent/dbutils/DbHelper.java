package org.fossasia.openevent.dbutils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by championswimmer on 17/5/15.
 */
public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(Context context) {
        super(context, DbContract.DATABASE_NAME, null, DbContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbContract.Speakers.CREATE_TABLE);
        db.execSQL(DbContract.Sponsors.CREATE_TABLE);
        db.execSQL(DbContract.Sessions.CREATE_TABLE);
        db.execSQL(DbContract.Tracks.CREATE_TABLE);
        db.execSQL(DbContract.Sessionsspeakers.CREATE_TABLE);
        db.execSQL(DbContract.Event.CREATE_TABLE);
        db.execSQL(DbContract.Microlocation.CREATE_TABLE);
     //   db.execSQL(DbContract.Versions.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DbContract.Sponsors.DELETE_TABLE);
        db.execSQL(DbContract.Sessions.DELETE_TABLE);
        db.execSQL(DbContract.Tracks.DELETE_TABLE);
        db.execSQL(DbContract.Speakers.DELETE_TABLE);
        db.execSQL(DbContract.Sessionsspeakers.DELETE_TABLE);
        db.execSQL(DbContract.Event.DELETE_TABLE);
        db.execSQL(DbContract.Microlocation.DELETE_TABLE);
        onCreate(db);
    }
}
