package org.fossasia.openevent;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.test.ActivityInstrumentationTestCase2;

import org.fossasia.openevent.activities.MainActivity;
import org.fossasia.openevent.dbutils.DbContract;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by MananWason on 7/31/2015.
 */
public class SpeakerActivityTests extends ActivityInstrumentationTestCase2<MainActivity> {
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    public SpeakerActivityTests() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        context.deleteDatabase(DbContract.DATABASE_NAME);

    }

    public void testToolbarTitleTracks() {
        openDrawer(R.id.drawer);
        String text = getActivity().getResources().getString(R.string.menu_speakers);
        onView(withText(text)).perform(click());

        //click on first track;
        onView(withId(R.id.rv_speakers)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        //click on first session;
        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.title_session)).check(matches(withText("super awecome")));

        onView(withId(R.id.subtitle_session)).check(matches(withText("super")));

        onView(withId(R.id.tv_time)).check(matches(withText("2015-08-01T16:00:00")));

        onView(withId(R.id.tv_location)).check(matches(withText("1")));

        onView(withId(R.id.track)).check(matches(withText("")));

        onView(withId(R.id.tv_abstract_text)).check(matches(withText("super awesome")));

//        onView(withId(R.id.tv_description))
//                .check(matches(withText("Super awesome means that it is very awesome and it is definitely worth attending. Don't miss it at cost. See you there")));

        //Scroll RV
        onView(withId(R.id.list_speakerss)).perform(RecyclerViewActions.scrollToPosition(2));


    }
}
