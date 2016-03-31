package org.fossasia.openevent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.test.AndroidTestCase;

import com.google.gson.Gson;

import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbHelper;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.helper.IOUtils;
import org.junit.Test;

/**
 * User: mohit
 * Date: 25/1/16
 */
public class EventInsertTest extends AndroidTestCase {
    private static final String TAG = SpeakerInsertTest.class.getSimpleName();

    private static long eventAssignId;

    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    int id;

    String name;

    String email;

    String color;

    String logo;

    String start;

    String end;

    float latitude;

    float longitude;

    String locationName;

    String url;

    String slogan;

    private DbHelper db;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        db = new DbHelper(mContext);

        SQLiteDatabase database = db.getWritableDatabase();

        id = 1;

        name = "FOSSASIA";

        email = "dev@fossasia.org";

        color = "#fdfdfd";

        logo = "2015-05-05T16:15:00";

        start = "2015-05-28T13:00:00";

        end = "2015-07-14T00:00:00";

        latitude = (float) 37.783839;

        longitude = (float) -122.400546;

        locationName = "Moscone centre";

        url = "www.google.com";

        slogan = "Fossasia";


        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.Event.NAME, name);
        contentValues.put(DbContract.Event.EMAIL, email);
        contentValues.put(DbContract.Event.COLOR, color);
        contentValues.put(DbContract.Event.LOGO_URL, logo);
        contentValues.put(DbContract.Event.START, start);
        contentValues.put(DbContract.Event.END, end);
        contentValues.put(DbContract.Event.LATITUDE, latitude);
        contentValues.put(DbContract.Event.LONGITUDE, longitude);
        contentValues.put(DbContract.Event.LOCATION_NAME, locationName);
        contentValues.put(DbContract.Event.EVENT_URL, url);
        contentValues.put(DbContract.Event.EVENT_SLOGAN, slogan);
        eventAssignId = database.insert(DbContract.Event.TABLE_NAME, null, contentValues);
        assertTrue(eventAssignId != -1);
    }

    @Test
    public void testDataCorrectness() {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(DbContract.Event.TABLE_NAME, null, null, null, null, null, null);
        assertTrue(cursor.moveToFirst());

        int idColumnIndex = cursor.getColumnIndex(DbContract.Event.ID);
        int dbId = cursor.getInt(idColumnIndex);

        int nameColumnIndex = cursor.getColumnIndex(DbContract.Event.NAME);
        String dbName = cursor.getString(nameColumnIndex);

        int emailColumnIndex = cursor.getColumnIndex(DbContract.Event.EMAIL);
        String dbEmail = cursor.getString(emailColumnIndex);

        int colorColumnIndex = cursor.getColumnIndex(DbContract.Event.COLOR);
        String dbColor = cursor.getString(colorColumnIndex);

        int logoColumnIndex = cursor.getColumnIndex(DbContract.Event.LOGO_URL);
        String dbLogo = cursor.getString(logoColumnIndex);

        int startColumnIndex = cursor.getColumnIndex(DbContract.Event.START);
        String dbStart = cursor.getString(startColumnIndex);

        int endColumnIndex = cursor.getColumnIndex(DbContract.Event.END);
        String dbEnd = cursor.getString(endColumnIndex);

        int latitudeColumnIndex = cursor.getColumnIndex(DbContract.Event.LATITUDE);
        float dbLatitude = cursor.getFloat(latitudeColumnIndex);

        int longitudeColumnIndex = cursor.getColumnIndex(DbContract.Event.LONGITUDE);
        float dbLongitude = cursor.getFloat(longitudeColumnIndex);

        int locationColumnIndex = cursor.getColumnIndex(DbContract.Event.LOCATION_NAME);
        String dbLocation = cursor.getString(locationColumnIndex);

        int urlColumnIndex = cursor.getColumnIndex(DbContract.Event.EVENT_URL);
        String dbURL = cursor.getString(urlColumnIndex);

        int sloganColumnIndex = cursor.getColumnIndex(DbContract.Event.EVENT_SLOGAN);
        String dbSlogan = cursor.getString(sloganColumnIndex);

        cursor.close();
        assertEquals(id, dbId);
        assertEquals(name, dbName);
        assertEquals(email, dbEmail);
        assertEquals(color, dbColor);
        assertEquals(logo, dbLogo);
        assertEquals(start, dbStart);
        assertEquals(end, dbEnd);
        assertEquals(latitude, dbLatitude);
        assertEquals(longitude, dbLongitude);
        assertEquals(locationName, dbLocation);
        assertEquals(url, dbURL);
        assertEquals(slogan, dbSlogan);

    }

    @Test
    public void testEventInsertion() {
        Gson gson = new Gson();
        // For reading resources we need this
        String jsonStr = IOUtils.readRaw(org.fossasia.openevent.test.R.raw.event_v1, InstrumentationRegistry.getContext());
        Event event = gson.fromJson(jsonStr, Event.class);
        String query = event.generateSql();

        DbSingleton instance = new DbSingleton(context);
        instance.clearDatabase(DbContract.Event.TABLE_NAME);
        instance.insertQuery(query);

        Event eventDetails = instance.getEventDetails();
        assertNotNull(eventDetails);
        assertEquals(event.getEmail(), eventDetails.getEmail());
        assertEquals(event.getColor(), eventDetails.getColor());
        assertEquals(event.getLogo(), eventDetails.getLogo());
    }

    @Override
    protected void tearDown() throws Exception {
        getContext().deleteDatabase(DbContract.DATABASE_NAME);
        db.close();
        super.tearDown();
    }
}
