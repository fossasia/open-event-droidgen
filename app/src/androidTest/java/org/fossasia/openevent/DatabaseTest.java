package org.fossasia.openevent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.test.AndroidTestCase;
import android.util.Log;

import org.fossasia.openevent.data.*;
import org.fossasia.openevent.dbutils.DbHelper;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.randomStringGenerator;

/**
 * Created by MananWason on 17-06-2015.
 */
public class DatabaseTest extends AndroidTestCase {
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    private DbHelper db;

    private String DB_NAME;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        randomStringGenerator randomStringGenerator = new randomStringGenerator();
        DB_NAME = randomStringGenerator.generateRandomString() + ".db";
        db = new DbHelper(context, DB_NAME);
        Event event = new Event(4, "foss", "a@b.com", "#000000", "img.png", "2015-06-05T12:00:00",
                "2015-06-06T12:00:00", 23.7f, 45.60f, "moscone centre", "www.event2.com", "swagger event");
        String eventQuery = event.generateSql();
        Log.d("Event", eventQuery);

        Sponsor sponsor = new Sponsor(5, "Google", "www.google.com", "google.png");
        String sponsorQuery = sponsor.generateSql();
        Log.d("Sponsor", sponsorQuery);

        Speaker speaker = new Speaker(5, "manan", "manan.png", "manan wason", "IIITD",
                "mananwason.me", "twitter.com/mananwason", "facebook.com/mananwason",
                "github.com/mananwason", "linkedin.com/mananwason", "fossasia", "gsoc student", null, "india");
        String speakerQuery = speaker.generateSql();
        Log.d("speaekr", speakerQuery);

        Microlocation microlocation = new Microlocation(4, "moscone centre", 35.6f, 112.5f, 2);
        String microlocationQuery = microlocation.generateSql();
        Log.d("micro", microlocationQuery);
        int[] speakers_array = {1};

        Session session = new Session(5, "abcd", "abc", "abcdefgh", "sdfjs dsjfnjs",
                "2015-06-05T00:00:00", "2015-06-06T00:00:00", "abcde", 1,
                "3", speakers_array, 2);

        String sessionQuery = session.generateSql();
        Log.d("session", sessionQuery);

        Version version = new Version(1, 2, 3, 4, 5, 6, 7);
        String versionQuery = version.generateSql();
        Log.d("VErsion", versionQuery);

        Track track = new Track(6, "android", "open source mobile os by google");
        String trackQuery = track.generateSql();
        Log.d("track", trackQuery);

        SQLiteDatabase database = db.getWritableDatabase();
        database.beginTransaction();
        database.execSQL(versionQuery);
        database.execSQL(eventQuery);
        database.execSQL(speakerQuery);
        database.execSQL(sponsorQuery);
        database.execSQL(microlocationQuery);
        database.execSQL(sessionQuery);
        database.execSQL(trackQuery);

        DbSingleton.setInstance(new DbSingleton(database, context, db));
        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
        db.close();
    }

    public void testDropDB() {
        /** Check that the DB path is correct and db exists */
        assertTrue(context.getDatabasePath(DB_NAME).exists());
        /** delete dB */
        assertTrue(context.deleteDatabase(DB_NAME));
    }

    public void testCreateDB() {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        assertTrue(db.isOpen());
        db.close();
    }

    public void testSpeakersList() throws Exception {
        DbSingleton dbSingleton = DbSingleton.getInstance();

        assertNotNull(dbSingleton.getSpeakerList());
        assertTrue(dbSingleton.getSpeakerList().size() > 0);
    }

    public void testSessionsList() throws Exception {
        DbSingleton dbSingleton = DbSingleton.getInstance();

        assertNotNull(dbSingleton.getSessionList());
        assertTrue(dbSingleton.getSessionList().size() > 0);
    }

    public void testTracksList() throws Exception {
        DbSingleton dbSingleton = DbSingleton.getInstance();

        assertNotNull(dbSingleton.getTrackList());
        Log.d("TEST TRACKS", dbSingleton.getTrackList().size() + " " + DB_NAME);
        assertTrue(dbSingleton.getTrackList().size() > 0);
    }

    public void testVersionList() throws Exception {
        DbSingleton dbSingleton = DbSingleton.getInstance();
        assertNotNull(dbSingleton.getVersionIds());
    }

    public void testSponsorsList() throws Exception {
        /**
         * This call is ambiguous because the Singleton caches the DB and the helper
         * each of which change every time {@link #setUp) is run
         */
        DbSingleton dbSingleton = DbSingleton.getInstance();
        assertNotNull(dbSingleton.getSponsorList());
        assertTrue(dbSingleton.getSponsorList().size() >= 0);
    }


    @Override
    protected void tearDown() throws Exception {
        db.close();
        context.deleteDatabase(DB_NAME);
        super.tearDown();
    }
}
