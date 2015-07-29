package org.fossasia.openevent;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.assertion.ViewAssertions;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import org.fossasia.openevent.activities.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.DrawerActions.closeDrawer;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static java.util.regex.Pattern.matches;

/**
 * Created by MananWason on 24-07-2015.
 */
public class FragmentReplaceTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public FragmentReplaceTest(){
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        getActivity();
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        Log.d("TEARDOWN", "TEARDOWN");
        super.tearDown();
    }

    public void testClickShowFragmentButtonTestFragmentShown()
    {
        //get the text which the fragment shows
        ViewInteraction fragmentText = onView(withId(R.id.content_frame));

        //check the fragment text does not exist on fresh activity start
        fragmentText.check(ViewAssertions.doesNotExist());

        //click the button to show the fragment
        onView((withId(R.id.nav_tracks))).perform(click());

        //check the fragments text is now visible in the activity
        fragmentText.check(ViewAssertions.matches(isDisplayed()));
    }

//    public void testClickShowFragmentButtonTestFragmentShown()
//    {
//        openDrawer(R.id.drawer);
//        closeDrawer(R.id.drawer);
//
//        openDrawer(R.id.drawer);
//        onView(withId(R.id.drawer)).check(matches(isOpen()));
//
//    }
}
