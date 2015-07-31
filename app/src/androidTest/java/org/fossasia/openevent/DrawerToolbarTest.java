package org.fossasia.openevent;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.Toolbar;
import android.test.ActivityInstrumentationTestCase2;

import org.fossasia.openevent.activities.MainActivity;
import org.fossasia.openevent.dbutils.DbHelper;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.Is.is;

/**
 * Created by MananWason on 7/31/2015.
 */
public class DrawerToolbarTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    private DbHelper db;

    public DrawerToolbarTest() {
        super(MainActivity.class);
    }

    private static Matcher<Object> withToolbarTitle(final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override
            public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }

    private static ViewInteraction matchToolbarTitle(CharSequence title) {
        return onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(is(title))));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        WifiManager wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
        getActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        WifiManager wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    public void testToolbarTitleTracks() {
        openDrawer(R.id.drawer);
        String text = getActivity().getResources().getString(R.string.menu_tracks);
        onView(withText(text)).perform(click());
        CharSequence title = InstrumentationRegistry.getTargetContext().getString(R.string.menu_tracks);
        matchToolbarTitle(title);
    }

    public void testToolbarTitleBookmarks() {
        openDrawer(R.id.drawer);
        String text = getActivity().getResources().getString(R.string.menu_bookmarks);
        onView(withText(text)).perform(click());
        CharSequence title = InstrumentationRegistry.getTargetContext().getString(R.string.menu_bookmarks);
        matchToolbarTitle(title);
    }

    public void testToolbarTitleSpeakers() {
        openDrawer(R.id.drawer);
        String text = getActivity().getResources().getString(R.string.menu_speakers);
        onView(withText(text)).perform(click());
        CharSequence title = InstrumentationRegistry.getTargetContext().getString(R.string.menu_speakers);
        matchToolbarTitle(title);
    }

    public void testToolbarTitleSponsors() {
        openDrawer(R.id.drawer);
        String text = getActivity().getResources().getString(R.string.menu_sponsor);
        onView(withText(text)).perform(click());
        CharSequence title = InstrumentationRegistry.getTargetContext().getString(R.string.menu_sponsor);
        matchToolbarTitle(title);
    }

    public void testToolbarTitleMap() {
        openDrawer(R.id.drawer);
        String text = getActivity().getResources().getString(R.string.menu_map);
        onView(withText(text)).perform(click());
        CharSequence title = InstrumentationRegistry.getTargetContext().getString(R.string.menu_map);
        matchToolbarTitle(title);
    }


}