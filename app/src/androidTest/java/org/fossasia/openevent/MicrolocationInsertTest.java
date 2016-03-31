package org.fossasia.openevent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.test.AndroidTestCase;

import com.google.gson.Gson;

import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbHelper;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.helper.IOUtils;
import org.junit.Test;

import java.util.List;

/**
 * User: opticod(Anupam Das)
 * Date: 13/3/16
 */

public class MicrolocationInsertTest extends AndroidTestCase {
    private static long microlocationAssignId;

    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    int id;

    String name;

    float latitude;

    float longitude;

    int floor;

    private DbHelper db;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        db = new DbHelper(mContext);
        SQLiteDatabase database = db.getWritableDatabase();

        id = 1;

        name = "Stage 7";

        latitude = (float) 75.12312;

        longitude = (float) 123.11212;

        floor = 2;

        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.Microlocation.NAME, name);
        contentValues.put(DbContract.Microlocation.LATITUDE, latitude);
        contentValues.put(DbContract.Microlocation.LONGITUDE, longitude);
        contentValues.put(DbContract.Microlocation.FLOOR, floor);
        microlocationAssignId = database.insert(DbContract.Microlocation.TABLE_NAME, null, contentValues);
        assertTrue(microlocationAssignId != -1);
    }

    @Test
    public void testDataCorrectness() {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(DbContract.Microlocation.TABLE_NAME, null, null, null, null, null, null);
        assertTrue(cursor.moveToFirst());

        int idColumnIndex = cursor.getColumnIndex(DbContract.Microlocation.ID);
        int dbId = cursor.getInt(idColumnIndex);

        int nameColumnIndex = cursor.getColumnIndex(DbContract.Microlocation.NAME);
        String dbName = cursor.getString(nameColumnIndex);

        int latitudeColumnIndex = cursor.getColumnIndex(DbContract.Microlocation.LATITUDE);
        float dbLatitude = cursor.getFloat(latitudeColumnIndex);

        int longitudeColumnIndex = cursor.getColumnIndex(DbContract.Microlocation.LONGITUDE);
        float dbLongitude = cursor.getFloat(longitudeColumnIndex);

        int floorColumnIndex = cursor.getColumnIndex(DbContract.Microlocation.FLOOR);
        int dbFloor = cursor.getInt(floorColumnIndex);

        cursor.close();
        assertEquals(id, dbId);
        assertEquals(name, dbName);
        assertEquals(latitude, dbLatitude);
        assertEquals(longitude, dbLongitude);
        assertEquals(floor, dbFloor);
    }


    /**
     * Checks that null values are correctly coerced into empty strings
     */
    @Test
    public void testDataInsertionIsCorrect() {
        Gson gson = new Gson();
        // For reading resource we need this
        String jsonStr = IOUtils.readRaw(org.fossasia.openevent.test.R.raw.event_v1, InstrumentationRegistry.getContext());
        Microlocation microlocation = gson.fromJson(jsonStr, Microlocation.class);
        String query = microlocation.generateSql();
        DbSingleton instance = new DbSingleton(context);
        instance.clearDatabase(DbContract.Microlocation.TABLE_NAME);
        instance.insertQuery(query);

        List<Microlocation> microlocationsList = instance.getMicrolocationsList();
        assertTrue(microlocationsList != null);
        assertTrue(microlocationsList.size() == 1);

    }

    @Override
    protected void tearDown() throws Exception {
        getContext().deleteDatabase(DbContract.DATABASE_NAME);
        db.close();
        super.tearDown();
    }
}
