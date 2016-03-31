package org.fossasia.openevent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.test.AndroidTestCase;

import com.google.gson.Gson;

import org.fossasia.openevent.data.Session;
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

public class SessionInsertTest extends AndroidTestCase {
    private static long sessionAssignId;

    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    int id;

    String summary;

    String description;

    String startTime;

    String endTime;

    String level;

    int microlocations;

    String title;

    String subtitle;

    String type;

    String track;

    private DbHelper db;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        db = new DbHelper(mContext);
        SQLiteDatabase database = db.getWritableDatabase();

        id = 1;

        summary = "Die Indizien h\\u00e4ufen sich, dass die globale Massen\\u00fcberwachung durch" +
                " ihre Geheimdiensten die au\\u00dfenpolitischen Ziele westlicher Demokratien" +
                " unterl\\u00e4uft. Und dies nicht nur, wenn das Verborgene \\u00f6ffentlich" +
                " wird und sich Regierungen erkl\\u00e4ren m\\u00fcssen, sondern auch, wenn " +
                "Staaten aufh\\u00f6ren, entschlossen f\\u00fcr Freiheit und Menschenrechte " +
                "im Netz einzutreten. Anhand aktueller Bespiele wird das Ph\\u00e4nomen " +
                "erl\\u00e4utert und L\\u00f6sungsvorschl\\u00e4ge pr\\u00e4sentiert.";

        description = "Als im Herbst 2013 bekannt wird, dass die NSA das private " +
                "Handy von Kanzlerin Merkel und viele andere Anschl\\u00fcsse deutscher" +
                " Politiker auf einer streng geheimen \\u00dcberwachungsliste f\\u00fchrt,";

        startTime = "2015-05-05T15:45:00";

        endTime = "2015-05-05T16:15:00";

        level = null;

        microlocations = 1;

        title = "Strategische Aufkl\u00e4rung? \u2013 Wie Geheimdienste Au\u00dfenpolitik sabotieren";

        subtitle = null;

        type = null;

        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.Sessions.TITLE, title);
        contentValues.put(DbContract.Sessions.SUBTITLE, subtitle);
        contentValues.put(DbContract.Sessions.SUMMARY, summary);
        contentValues.put(DbContract.Sessions.DESCRIPTION, description);
        contentValues.put(DbContract.Sessions.START_TIME, startTime);
        contentValues.put(DbContract.Sessions.END_TIME, endTime);
        contentValues.put(DbContract.Sessions.TYPE, type);
        contentValues.put(DbContract.Sessions.LEVEL, level);
        contentValues.put(DbContract.Sessions.MICROLOCATION, microlocations);
        sessionAssignId = database.insert(DbContract.Sessions.TABLE_NAME, null, contentValues);
        assertTrue(sessionAssignId != -1);
    }

    @Test
    public void testDataCorrectness() {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(DbContract.Sessions.TABLE_NAME, null, null, null, null, null, null);
        assertTrue(cursor.moveToFirst());

        int idColumnIndex = cursor.getColumnIndex(DbContract.Sessions.ID);
        int dbId = cursor.getInt(idColumnIndex);

        int summaryColumnIndex = cursor.getColumnIndex(DbContract.Sessions.SUMMARY);
        String dbSummary = cursor.getString(summaryColumnIndex);

        int descriptionColumnIndex = cursor.getColumnIndex(DbContract.Sessions.DESCRIPTION);
        String dbDescription = cursor.getString(descriptionColumnIndex);

        int startColumnIndex = cursor.getColumnIndex(DbContract.Sessions.START_TIME);
        String dbStart = cursor.getString(startColumnIndex);

        int endColumnIndex = cursor.getColumnIndex(DbContract.Sessions.END_TIME);
        String dbEnd = cursor.getString(endColumnIndex);

        int levelColumnIndex = cursor.getColumnIndex(DbContract.Sessions.LEVEL);
        String dbLevel = cursor.getString(levelColumnIndex);

        int microlocationColumnIndex = cursor.getColumnIndex(DbContract.Sessions.MICROLOCATION);
        int dbMicrolocation = cursor.getInt(microlocationColumnIndex);

        int titleColumnIndex = cursor.getColumnIndex(DbContract.Sessions.TITLE);
        String dbTitle = cursor.getString(titleColumnIndex);

        int subTitleColumnIndex = cursor.getColumnIndex(DbContract.Sessions.SUBTITLE);
        String dbSubTitle = cursor.getString(subTitleColumnIndex);

        int typeColumnIndex = cursor.getColumnIndex(DbContract.Sessions.TYPE);
        String dbType = cursor.getString(typeColumnIndex);

        int trackColumnIndex = cursor.getColumnIndex(DbContract.Sessions.TRACK);
        String dbTrack = cursor.getString(trackColumnIndex);

        cursor.close();
        assertEquals(id, dbId);
        assertEquals(summary, dbSummary);
        assertEquals(description, dbDescription);
        assertEquals(startTime, dbStart);
        assertEquals(endTime, dbEnd);
        assertEquals(level, dbLevel);
        assertEquals(microlocations, dbMicrolocation);
        assertEquals(title, dbTitle);
        assertEquals(subtitle, dbSubTitle);
        assertEquals(type, dbType);
        assertEquals(track, dbTrack);
    }


    /**
     * Checks that null values are correctly coerced into empty strings
     */
    @Test
    public void testDataInsertionIsCorrect() {
        Gson gson = new Gson();
        // For reading resource we need this
        String jsonStr = IOUtils.readRaw(org.fossasia.openevent.test.R.raw.event_v1, InstrumentationRegistry.getContext());
        Session session = gson.fromJson(jsonStr, Session.class);
        String query = session.generateSql();
        DbSingleton instance = new DbSingleton(context);
        instance.clearDatabase(DbContract.Sessions.TABLE_NAME);
        instance.insertQuery(query);

        List<Session> sessionList = instance.getSessionList();
        assertTrue(sessionList != null);
        assertTrue(sessionList.size() == 1);
        Session session2 = sessionList.get(0);
        // NULL String must be transformed into an empty string upon insertion
        assertEquals(null, session.getTitle());

        // Must be empty string
        assertEquals("", session2.getDescription());

        // NULL must be converted to empty string
        assertEquals("", session2.getEndTime());

        // NULL must be converted to empty string
        assertEquals("", session2.getStartTime());

        // NULL must be converted to empty string
        assertEquals("", session2.getSummary());
    }

    @Override
    protected void tearDown() throws Exception {
        getContext().deleteDatabase(DbContract.DATABASE_NAME);
        db.close();
        super.tearDown();
    }
}
