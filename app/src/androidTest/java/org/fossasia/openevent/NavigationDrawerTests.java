package org.fossasia.openevent;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import org.fossasia.openevent.activities.MainActivity;
import org.junit.Before;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.closeDrawer;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


/**
 * Created by MananWason on 24-07-2015.
 */
public class NavigationDrawerTests extends ActivityInstrumentationTestCase2<MainActivity> {

    public NavigationDrawerTests() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    public void testDrawerViewPresence() {
        closeDrawer(R.id.drawer);
        onView(withId(R.id.drawer)).check(matches(isDisplayed()));
    }


//    public void testNavigationDrawerItemClick() {
//        openDrawer(R.id.drawer);
//        String text = getActivity().getResources().getString(R.string.menu_tracks);
//        onView(withText(text)).perform(click());
//    }


    public void testNavigationDrawerBackButton() {
        openDrawer(R.id.drawer);
        pressBack();
    }

    public void testOpenAndCloseDrawer() {
        onView(withId(R.id.drawer)).check(matches(isClosed()));

        openDrawer(R.id.drawer);
        onView(withId(R.id.drawer)).check(matches(isOpen()));

        closeDrawer(R.id.drawer);
        onView(withId(R.id.drawer)).check(matches(isClosed()));
    }


}