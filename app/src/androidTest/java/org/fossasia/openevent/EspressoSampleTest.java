package org.fossasia.openevent;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import org.fossasia.openevent.activities.MainActivity;
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

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by MananWason on 22-07-2015.
 */

public class EspressoSampleTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private static final String TRACK_TITLE = "android";
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    private DbHelper db;

    public EspressoSampleTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        WifiManager wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);

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
        Sponsor sponsor1 = new Sponsor(1, "Google", "www.google.com", "google.png");
        Sponsor sponsor2 = new Sponsor(2, "Google", "www.google.com", "google.png");
        Sponsor sponsor3 = new Sponsor(3, "Google", "www.google.com", "google.png");
        String sponsorQuery = sponsor.generateSql();
        String sponsorQuery1 = sponsor1.generateSql();
        String sponsorQuery2 = sponsor2.generateSql();
        String sponsorQuery3 = sponsor3.generateSql();
        Log.d("Sponsor", sponsorQuery);
        queries.add(sponsorQuery);
        queries.add(sponsorQuery1);
        queries.add(sponsorQuery2);
        queries.add(sponsorQuery3);

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


        Session session = new Session(1, "abcd", "abc", "abcdefgh", "sdfjs dsjfnjs",
                "2015-06-05T00:00:00", "2015-06-06T00:00:00", "abcde", 1,
                "3", speakers_array, 1);
        Session session1 = new Session(6, "abcd", "abc", "abcdefgh", "sdfjs dsjfnjs",
                "2015-06-05T00:00:00", "2015-06-06T00:00:00", "abcde", 2,
                "3", speakers_array, 1);
        Session session2 = new Session(5, "abcd", "abc", "abcdefgh", "sdfjs dsjfnjs",
                "2015-06-05T00:00:00", "2015-06-06T00:00:00", "abcde", 3,
                "3", speakers_array, 1);
        Session session3 = new Session(7, "abcd", "abc", "abcdefgh", "sdfjs dsjfnjs",
                "2015-06-05T00:00:00", "2015-06-06T00:00:00", "abcde", 6,
                "3", speakers_array, 1);
        String sessionQuery = session.generateSql();
        String sessionQuery1 = session1.generateSql();
        String sessionQuery2 = session2.generateSql();
        String sessionQuery3 = session3.generateSql();

        queries.add(sessionQuery);
        queries.add(sessionQuery1);
        queries.add(sessionQuery2);
        queries.add(sessionQuery3);

        Version version = new Version(1, 2, 3, 4, 5, 6, 7);
        String versionQuery = version.generateSql();
        Log.d("VErsion", versionQuery);
        queries.add(versionQuery);
        Track track = new Track(6, "android", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");
        Track track1 = new Track(7, "android1", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");
        Track track2 = new Track(8, "android", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");
        Track track3 = new Track(9, "android1", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");
        Track track4 = new Track(10, "android", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");
        Track track5 = new Track(11, "android1", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");
        Track track6 = new Track(12, "android", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");
        Track track7 = new Track(13, "android1", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");
        Track track8 = new Track(14, "android", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");
        Track track9 = new Track(15, "android1", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");


        String trackQuery = track.generateSql();
        String trackQuery1 = track1.generateSql();
        String trackQuery2 = track2.generateSql();
        String trackQuery3 = track3.generateSql();
        String trackQuery4 = track4.generateSql();
        String trackQuery5 = track5.generateSql();
        String trackQuery6 = track6.generateSql();
        String trackQuery7 = track7.generateSql();
        String trackQuery8 = track8.generateSql();
        String trackQuery9 = track9.generateSql();

        queries.add(trackQuery);
        queries.add(trackQuery1);
        queries.add(trackQuery2);
        queries.add(trackQuery3);
        queries.add(trackQuery4);
        queries.add(trackQuery5);
        queries.add(trackQuery6);
        queries.add(trackQuery7);
        queries.add(trackQuery8);
        queries.add(trackQuery9);
        DatabaseOperations databaseOperations = new DatabaseOperations();
        databaseOperations.insertQueries(queries, db);

        getActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        WifiManager wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

    }

//    public void testClickAtPositionTracks() {
//        onView(withId(R.id.list_tracks)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
//    }
//
//    public void testScrollToTracks() {
//        onView(withId(R.id.list_tracks)).perform(RecyclerViewActions.scrollToPosition(3));
//    }
//
//
    public void testClickAtPositionSpeakers() {
        openDrawer(R.id.drawer);
        String text = getActivity().getResources().getString(R.string.menu_speakers);
        onView(withText(text)).perform(click());
        onView(withId(R.id.rv_speakers)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    public void testClickAtPositionBookmarks() {

        onView(withId(R.id.list_tracks)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.add_bookmark)).perform(click());
        onView(withContentDescription("Navigate up")).perform(click());
        onView(withContentDescription("Navigate up")).perform(click());
        openDrawer(R.id.drawer);
        onView(withId(R.id.nav_bookmarks)).perform(click());
        onView(withId(R.id.list_bookmarks)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }


}