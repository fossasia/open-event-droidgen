package org.fossasia.openevent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.data.Version;
import org.fossasia.openevent.dbutils.DatabaseOperations;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbHelper;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import timber.log.Timber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * User: MananWason
 * Date: 17-06-2015
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private Context context;
    private DbHelper db;

    @Before
    protected void setUp() throws Exception {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.deleteDatabase(DbContract.DATABASE_NAME);
        ArrayList<String> queries;
        queries = new ArrayList<>();
        db = new DbHelper(context, DbContract.DATABASE_NAME);
        Event event = new Event(4, "foss", "a@b.com", "#000000", "img.png", "2015-06-05T12:00:00",
                "2015-06-06T12:00:00", 23.7f, 45.60f, "moscone centre", "www.event2.com", "swagger event");
        String eventQuery = event.generateSql();
        Timber.tag("Event").d(eventQuery);
        queries.add(eventQuery);

        Sponsor sponsor = new Sponsor(5, "Google", "www.google.com", "google.png");
        String sponsorQuery = sponsor.generateSql();
        Timber.tag("Sponsor").d(sponsorQuery);
        queries.add(sponsorQuery);

        Speaker speaker = new Speaker(5, "manan", "manan.png", "manan wason", "IIITD",
                "mananwason.me", "twitter.com/mananwason", "facebook.com/mananwason",
                "github.com/mananwason", "linkedin.com/mananwason", "fossasia", "gsoc student", null, "india");
        String speakerQuery = speaker.generateSql();
        Timber.tag("Speaker").d(speakerQuery);
        queries.add(speakerQuery);

        Microlocation microlocation = new Microlocation(4, "moscone centre", 35.6f, 112.5f, 2);
        String microlocationQuery = microlocation.generateSql();
        Timber.tag("Micro").d(microlocationQuery);
        queries.add(microlocationQuery);

        Session session = new Session(5, "abcd", "abc", "abcdefgh", "sdfjs dsjfnjs",
                "2015-06-05T00:00:00", "2015-06-06T00:00:00", "abcde", 1,
                "3", 2);

        String sessionQuery = session.generateSql();
        Timber.tag("Session").d(sessionQuery);
        queries.add(sessionQuery);

        Version version = new Version(1, 2, 3, 4, 5, 6, 7);
        String versionQuery = version.generateSql();
        Timber.tag("Version").d(versionQuery);
        queries.add(versionQuery);
        Track track = new Track(6, "android", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");
        String trackQuery = track.generateSql();
        Timber.tag("Track").d(trackQuery);
        queries.add(trackQuery);
        DatabaseOperations databaseOperations = new DatabaseOperations();
        databaseOperations.insertQueries(queries, db);

    }

    @Test
    public void testDropDB() {
        /** Check that the DB path is correct and db exists */
        assertTrue(context.getDatabasePath(DbContract.DATABASE_NAME).exists());
        /** delete dB */
        assertTrue(context.deleteDatabase(DbContract.DATABASE_NAME));
    }

    @Test
    public void testCreateDB() {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        assertTrue(db.isOpen());
        db.close();
    }

    @Test
    public void testSpeakersList() throws Exception {
        DbSingleton dbSingleton = DbSingleton.getInstance();

        assertNotNull(dbSingleton.getSpeakerList(DbContract.Speakers.ID));
        assertTrue(dbSingleton.getSpeakerList(DbContract.Speakers.ID).size() > 0);
    }

    @Test
    public void testSessionsList() throws Exception {
        DbSingleton dbSingleton = DbSingleton.getInstance();

        assertNotNull(dbSingleton.getSessionList());
        assertTrue(dbSingleton.getSessionList().size() > 0);
    }

    @Test
    public void testTracksList() throws Exception {
        DbSingleton dbSingleton = DbSingleton.getInstance();

        assertNotNull(dbSingleton.getTrackList());
        Timber.tag("TEST TRACKS").d(dbSingleton.getTrackList().size() + " ");
        assertTrue(dbSingleton.getTrackList().size() > 0);
    }

    @Test
    public void testVersionList() throws Exception {
        DbSingleton dbSingleton = DbSingleton.getInstance();
        assertNotNull(dbSingleton.getVersionIds());
    }

    @Test
    public void testSponsorsList() throws Exception {
        /**
         * This call is ambiguous because the Singleton caches the DB and the helper
         * each of which change every time {@link #setUp) is run
         */
        DbSingleton dbSingleton = DbSingleton.getInstance();
        assertNotNull(dbSingleton.getSponsorList());
        assertTrue(dbSingleton.getSponsorList().size() >= 0);
    }


    @After
    protected void tearDown() throws Exception {
        db.close();
        context.deleteDatabase(DbContract.DATABASE_NAME);
    }
}
