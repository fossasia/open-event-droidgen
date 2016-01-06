package org.fossasia.openevent.activites;

import android.support.design.widget.NavigationView;
import android.support.test.InstrumentationRegistry;
import android.support.v7.widget.Toolbar;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.MainActivity;
import org.junit.Before;

/**
 * user: poprosturonin
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    Toolbar toolbar;
    NavigationView navigationView;
    View decorView;

    private MainActivity mActivity;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity();

        //Assign variables
        toolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
        navigationView = (NavigationView) mActivity.findViewById(R.id.nav_view);
        decorView = mActivity.getWindow().getDecorView();
    }

    @MediumTest
    public void testToolbar_layout() {
        ViewAsserts.assertOnScreen(decorView, toolbar);

        final ViewGroup.LayoutParams layoutParams = toolbar.getLayoutParams();
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
    }

    @MediumTest
    public void testNavigationView_layout() {
        ViewAsserts.assertOnScreen(decorView, navigationView);

        final ViewGroup.LayoutParams layoutParams = navigationView.getLayoutParams();
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.MATCH_PARENT);
    }

    @MediumTest
    public void testNavigationView_menuContent() {
        final Menu menu = navigationView.getMenu();
        assertTrue(menu.hasVisibleItems());

        MenuItem navTracks = menu.findItem(R.id.nav_tracks);
        assertNotNull(navTracks);
        assertEquals(navTracks.getTitle(), mActivity.getString(R.string.menu_tracks));

        MenuItem navLocation = menu.findItem(R.id.nav_locations);
        assertNotNull(navLocation);
        assertEquals(navLocation.getTitle(), mActivity.getString(R.string.menu_locations));

        MenuItem navBookmarks = menu.findItem(R.id.nav_bookmarks);
        assertNotNull(navBookmarks);
        assertEquals(navBookmarks.getTitle(), mActivity.getString(R.string.menu_bookmarks));

        MenuItem navMap = menu.findItem(R.id.nav_map);
        assertNotNull(navMap);
        assertEquals(navMap.getTitle(), mActivity.getString(R.string.menu_map));

        MenuItem navSpeakers = menu.findItem(R.id.nav_speakers);
        assertNotNull(navSpeakers);
        assertEquals(navSpeakers.getTitle(), mActivity.getString(R.string.menu_speakers));

        MenuItem navSponsor = menu.findItem(R.id.nav_sponsors);
        assertNotNull(navSponsor);
        assertEquals(navSponsor.getTitle(), mActivity.getString(R.string.menu_sponsor));

        MenuItem navSettings = menu.findItem(R.id.nav_settings);
        assertNotNull(navSettings);
        assertEquals(navSettings.getTitle(),mActivity.getString(R.string.menu_settings));
    }
}
