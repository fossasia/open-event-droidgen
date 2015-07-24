package org.fossasia.openevent;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import org.fossasia.openevent.activities.TracksActivity;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DatabaseOperations;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbHelper;

import java.util.ArrayList;

/**
 * Created by MananWason on 22-07-2015.
 */

public class EspressoSampleTest extends ActivityInstrumentationTestCase2<TracksActivity> {

    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    private DbHelper db;

    public EspressoSampleTest() {
        super(TracksActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context.deleteDatabase(DbContract.DATABASE_NAME);
        ArrayList<String> queries;
        queries = new ArrayList<>();
        db = new DbHelper(context, DbContract.DATABASE_NAME);
        Event event = new Event(4, "foss", "a@b.com", "#000000", "img.png", "2015-06-05T12:00:00",
                "2015-06-06T12:00:00", 23.7f, 45.60f, "moscone centre", "www.event2.com", "swagger event");
        String eventQuery = event.generateSql();
        Log.d("Event", eventQuery);
        queries.add(eventQuery);

        Sponsor sponsor = new Sponsor(5, "Google", "www.google.com", "google.png");
        String sponsorQuery = sponsor.generateSql();
        Log.d("Sponsor", sponsorQuery);
        queries.add(sponsorQuery);

        Speaker speaker = new Speaker(5, "manan", "manan.png", "manan wason", "IIITD",
                "mananwason.me", "twitter.com/mananwason", "facebook.com/mananwason",
                "github.com/mananwason", "linkedin.com/mananwason", "fossasia", "gsoc student", null, "india");
        String speakerQuery = speaker.generateSql();
        Log.d("speaekr", speakerQuery);
        queries.add(speakerQuery);

        Microlocation microlocation = new Microlocation(4, "moscone centre", 35.6f, 112.5f, 2);
        String microlocationQuery = microlocation.generateSql();
        Log.d("micro", microlocationQuery);
        int[] speakers_array = {1};
        queries.add(microlocationQuery);

        Session session = new Session(5, "abcd", "abc", "abcdefgh", "sdfjs dsjfnjs",
                "2015-06-05T00:00:00", "2015-06-06T00:00:00", "abcde", 1,
                "3", speakers_array, 1);

        String sessionQuery = session.generateSql();
        Log.d("session", sessionQuery);
        queries.add(sessionQuery);

//        Version version = new Version(1, 2, 3, 4, 5, 6, 7);
//        String versionQuery = version.generateSql();
//        Log.d("VErsion", versionQuery);
//        queries.add(versionQuery);
        Track track = new Track(6, "android", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");
        Track track1 = new Track(7, "android1", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");
        String trackQuery = track.generateSql();
        String trackQuery1 = track1.generateSql();
        Log.d("track", trackQuery);
        Log.d("track", trackQuery1);
        queries.add(trackQuery);
        queries.add(trackQuery1);
        DatabaseOperations databaseOperations = new DatabaseOperations();
        databaseOperations.insertQueries(queries, db);

        // getActivity();
    }

    public void testClickAtPosition() {
        // onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Log.d("session", "test");

//        onView(withId(R.id.)).check(matches(withText(BOOK_TITLE)));
//        onView(withId(R.id.book_author)).check(matches(withText(BOOK_AUTHOR)));

    }
}

