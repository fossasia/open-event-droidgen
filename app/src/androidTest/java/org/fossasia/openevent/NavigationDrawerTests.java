package org.fossasia.openevent;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import org.fossasia.openevent.activities.MainActivity;
import org.junit.Before;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


/**
 * Created by MananWason on 24-07-2015.
 */
public class NavigationDrawerTests extends ActivityInstrumentationTestCase2<MainActivity> {
    private static String TAG = NavigationDrawerTests.class.getSimpleName();

    public NavigationDrawerTests() {
        super(MainActivity.class);
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
        Log.d("DCA", "SETUP");
    }

    /**
     * Test that clicking on a Navigation Drawer Item will open the correct fragment.
     * Espresso: openDrawer, onView, withText, perform, click, matches, check, isDisplayed
     */
    @SmallTest
    public void testNavigationDrawerItemClick() {

        openDrawer(R.id.drawer);
        Log.d("DCA", "testitemclick");
        String text = getActivity().getResources().getString(R.string.menu_tracks);
        Log.d("DCA", text);
        onView(withText(text)).perform(click());
    }

    /**
     * Test opening the Navigation Drawer and pressing the back button.
     * Espresso: openDrawer, pressBack
     */
    @SmallTest
    public void testNavigationDrawerBackButton() {
        openDrawer(R.id.drawer);
        pressBack();
    }

}