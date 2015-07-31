package org.fossasia.openevent;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.test.ActivityInstrumentationTestCase2;

import org.fossasia.openevent.activities.MainActivity;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbHelper;
import org.mockito.Mock;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Created by MananWason on 22-07-2015.
 */
public class RecyclerViewTests extends ActivityInstrumentationTestCase2<MainActivity> {

    private static DAL mockedTrackDAL;
    private static Track track1;
    private static Track track2;
    private static Speaker speaker1;
    private static Sponsor sponsor1;
    private static Sponsor sponsor2;
    private static Sponsor sponsor3;
    private static Session session1;
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private DbHelper db;
    @Mock
    private Speaker speakerApi;

    public RecyclerViewTests() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mockedTrackDAL = mock(DAL.class);

        track1 = new Track(6, "android", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");
        track2 = new Track(7, "android1", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");//        WifiManager wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);

        ArrayList tracks = new ArrayList();
        tracks.add(track1);
        tracks.add(track2);

// wifiManager.setWifiEnabled(false);
//
//        context.deleteDatabase(DbContract.DATABASE_NAME);
//        ArrayList<String> queries;
//        queries = new ArrayList<>();
//        db = new DbHelper(context, DbContract.DATABASE_NAME);
//        Event event = new Event(4, "foss", "a@b.com", "#000000", "img.png", "2015-06-05T12:00:00",
//                "2015-06-06T12:00:00", 23.7f, 45.60f, "moscone centre", "www.event2.com", "swagger event");
//
//        queries.add(event.generateSql());
//
        sponsor1 = new Sponsor(1, "Google", "www.google.com", "http://www.theverge.com/2014/6/25/5842024/googles-android-logo-gets-a-new-look");
        sponsor2 = new Sponsor(2, "Google", "www.google.com", "google.png");
        sponsor3 = new Sponsor(3, "Google", "www.google.com", "google.png");
        ArrayList sponsors = new ArrayList();
        sponsors.add(sponsor1);
        sponsors.add(sponsor2);
        sponsors.add(sponsor3);

//        queries.add(sponsor.generateSql());
//        queries.add(sponsor1.generateSql());
//        queries.add(sponsor2.generateSql());
//        queries.add(sponsor3.generateSql());
//
        Speaker speaker1 = new Speaker(5, "Manan", "https://media.licdn.com/mpr/mpr/shrinknp_400_400/p/8/005/05f/03a/3f2a7fd.jpg", "event android app",
                "manan.wason@gmail.com", "mananwason.wordpress.com", "twitter.com/mananwason9", "facebook.com/manan",
                "github.com/mananwason", "linkedin.com/mananwason", "fossasia", "gsoc student", null, "india");
        ArrayList speakers = new ArrayList();
        speakers.add(speaker1);
//        String speakerQuery = speaker.generateSql();
//        queries.add(speakerQuery);
//
        Microlocation microlocation = new Microlocation(4, "moscone centre", 35.6f, 112.5f, 2);
        int[] speakers_array = {5};
//        queries.add(microlocation.generateSql());
//
//
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
        ArrayList sessions = new ArrayList();
        sessions.add(session);
        sessions.add(session1);
        sessions.add(session2);
        sessions.add(session3);
//        queries.add(session.generateSql());
//        queries.add(session1.generateSql());
//        queries.add(session2.generateSql());
//        queries.add(session3.generateSql());
//
//        Version version = new Version(1, 2, 3, 4, 5, 6, 7);
//        String versionQuery = version.generateSql();
//        Log.d("VErsion", versionQuery);
//        queries.add(versionQuery);
//        Track track = new Track(6, "android", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");
//        Track track1 = new Track(7, "android1", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");
//        Track track2 = new Track(8, "android2", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");
//        Track track3 = new Track(9, "android3", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");
//        Track track4 = new Track(10, "android4", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");
//        Track track5 = new Track(11, "android5", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");
//        Track track6 = new Track(12, "android6", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");
//        Track track7 = new Track(13, "android7", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");
//        Track track8 = new Track(14, "android8", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");
//        Track track9 = new Track(15, "android9", "open source mobile os by google", "https://farm8.staticflickr.com/7575/15355329014_3cb3eb0c74_b.jpg");
//
//        queries.add(track.generateSql());
//        queries.add(track1.generateSql());
//        queries.add(track2.generateSql());
//        queries.add(track3.generateSql());
//        queries.add(track4.generateSql());
//        queries.add(track5.generateSql());
//        queries.add(track6.generateSql());
//        queries.add(track7.generateSql());
//        queries.add(track8.generateSql());
//        queries.add(track9.generateSql());
//        DatabaseOperations databaseOperations = new DatabaseOperations();
//        databaseOperations.insertQueries(queries, db);
        when(mockedTrackDAL.getTrackList()).thenReturn(tracks);
        when(mockedTrackDAL.getSponsorList()).thenReturn(sponsors);
        when(mockedTrackDAL.getSpeakerList()).thenReturn(speakers);
        when(mockedTrackDAL.getSessionList()).thenReturn(sessions);

        getActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        WifiManager wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        context.deleteDatabase(DbContract.DATABASE_NAME);


    }

    public void testAllViewsDrawer() {
        onView(withId(R.id.drawer)).check(matches(isDisplayed()));
    }

    public void testClickAtPositionTracks() {
        onView(withId(R.id.list_tracks)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    public void testScrollToTracks() {
        onView(withId(R.id.list_tracks)).perform(RecyclerViewActions.scrollToPosition(3));
    }


    public void testClickAtPositionSpeakers() {
        openDrawer(R.id.drawer);
        String text = getActivity().getResources().getString(R.string.menu_speakers);
        onView(withText(text)).perform(click());
        onView(withId(R.id.rv_speakers)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }


    public void testscrollCollapsingLayout() {
        onView(withId(R.id.list_tracks)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.main_content)).perform(swipeUp());
        onView(withId(R.id.main_content)).perform(swipeDown());

    }
}