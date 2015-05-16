package org.fossasia.openevent.dbutils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by championswimmer on 17/5/15.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DB_NAME = "OpenEvent.db";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbContract.Speakers.CREATE_TABLE);
        //TODO: Also create tables for Sessions, Tracks, Sponsors

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
