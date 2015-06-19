package org.fossasia.openevent;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbHelper;

/**
 * Created by MananWason on 18-06-2015.
 */
public class TracksInsertTest extends AndroidTestCase {
    private static int id;
    private static String name;
    private static String description;
    private static long tracksAssignId;

    private void insertValuesTest() {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        id = 1;
        name = "Android";
        description = "abcd";

        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.Tracks.NAME, name);
        contentValues.put(DbContract.Tracks.DESCRIPTION, description);

        tracksAssignId = db.insert(DbContract.Tracks.TABLE_NAME, null, contentValues);
        assertTrue(tracksAssignId != -1);
    }

    public void testDataCorrectness() {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(DbContract.Tracks.TABLE_NAME, null, null, null, null, null, null);
        assertTrue(cursor.moveToFirst());

        int idColumnIndex = cursor.getColumnIndex(DbContract.Tracks.ID);
        int dbId = cursor.getInt(idColumnIndex);

        int nameColumnIndex = cursor.getColumnIndex(DbContract.Tracks.NAME);
        String dbName = cursor.getString(nameColumnIndex);

        int descriptionColumnIndex = cursor.getColumnIndex(DbContract.Tracks.DESCRIPTION);
        String dbDescription = cursor.getString(descriptionColumnIndex);

        assertEquals(id, dbId);
        assertEquals(name, dbName);
        assertEquals(description, dbDescription);
    }
}
