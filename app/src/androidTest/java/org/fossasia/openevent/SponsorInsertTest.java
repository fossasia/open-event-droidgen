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
public class SponsorInsertTest extends AndroidTestCase {
    private static int id;
    private static String name;
    private static String url;
    private static String logo;
    private static long sponsorsAssignId;

    private void insertValuesTest() {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        id = 1;
        name = "Android";
        url = "www.android.com";
        logo = "http://www.theverge.com/2014/6/25/5842024/googles-android-logo-gets-a-new-look";

        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.Sponsors.NAME, name);
        contentValues.put(DbContract.Sponsors.URL, url);
        contentValues.put(DbContract.Sponsors.LOGO_URL, logo);

        sponsorsAssignId = db.insert(DbContract.Sponsors.TABLE_NAME, null, contentValues);
        assertTrue(sponsorsAssignId != -1);
    }

    public void testDataCorrectness() {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(DbContract.Sponsors.TABLE_NAME, null, null, null, null, null, null);
        assertTrue(cursor.moveToFirst());

        int idColumnIndex = cursor.getColumnIndex(DbContract.Sponsors.ID);
        int dbId = cursor.getInt(idColumnIndex);

        int nameColumnIndex = cursor.getColumnIndex(DbContract.Sponsors.NAME);
        String dbName = cursor.getString(nameColumnIndex);

        int urlColumnIndex = cursor.getColumnIndex(DbContract.Sponsors.URL);
        String dbUrl = cursor.getString(urlColumnIndex);

        int logoColumnIndex = cursor.getColumnIndex(DbContract.Sponsors.LOGO_URL);
        String dbLogo = cursor.getString(logoColumnIndex);

        assertEquals(id, dbId);
        assertEquals(name, dbName);
        assertEquals(logo, dbLogo);
        assertEquals(url, dbUrl);

    }
}
