package org.fossasia.openevent.espresso;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.Toolbar;
import android.test.suitebuilder.annotation.LargeTest;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.MainActivity;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DataDownload;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.closeDrawer;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.Is.is;

/**
 * User: opticod(anupam)
 * Date: 30/1/16
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    private final Context context = getInstrumentation().getTargetContext();

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void testDrawer() {

        //initially drawer will be in closed state
        onView(withId(R.id.drawer)).check(matches(isClosed()));

        openDrawer(R.id.drawer);
        closeDrawer(R.id.drawer);

        openDrawer(R.id.drawer);
        onView(withId(R.id.drawer)).check(matches(isOpen()));

        //repeatedly opening drawer
        openDrawer(R.id.drawer);
        openDrawer(R.id.drawer);
        openDrawer(R.id.drawer);
        openDrawer(R.id.drawer);

        //it should be in opened state
        onView(withId(R.id.drawer)).check(matches(isOpen()));

        //repeatedly closing drawer
        closeDrawer(R.id.drawer);
        closeDrawer(R.id.drawer);
        closeDrawer(R.id.drawer);
        closeDrawer(R.id.drawer);

        //it should be in closed state
        onView(withId(R.id.drawer)).check(matches(isClosed()));

        openDrawer(R.id.drawer);

        //changing orientation of app
        activityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        onView(withId(R.id.drawer)).check(matches(isOpen()));
        activityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        onView(withId(R.id.drawer)).check(matches(isOpen()));

        openDrawer(R.id.drawer);

        //track is selected in drawer
        String toolbarTitle = context.getString(R.string.menu_tracks);
        onView(withText(toolbarTitle)).check(matches(isChecked()));

    }

    @Test
    public void toolbar() {
        String toolbarTitle = context.getString(R.string.menu_tracks);
        onView(allOf(withText(toolbarTitle), isAssignableFrom(AppCompatCheckedTextView.class))).check(matches(withText(toolbarTitle.toString())));
        onView(withId(R.id.action_search_tracks)).check(matches(isDisplayed()));
        onView(withId(R.id.share_tracks_url)).check(matches(isDisplayed()));

    }

    @Test
    public void testMainFunctionality() {

        DataDownload download = new DataDownload();
        download.downloadTracks();
        download.downloadSession();

        final DbSingleton dbSingleton = DbSingleton.getInstance();
        List<Track> tracks = dbSingleton.getTrackList();
        if (tracks.size() > 0) {

            //check scroll is fine and last element is displayed
            onView(withId(R.id.list_tracks)).perform(RecyclerViewActions.scrollToPosition(tracks.size() - 1)).check(matches(isDisplayed()));

            //taking any random element from the list of tracks
            int rand = (int) (Math.random() * (tracks.size() - 1));
            Track model = tracks.get(rand);

            onView(withId(R.id.list_tracks)).perform(RecyclerViewActions.scrollToPosition(rand)).check(matches(isDisplayed()));
            onView(withId(R.id.list_tracks)).perform(RecyclerViewActions.actionOnItemAtPosition(rand, click()));

            //now check for tracks detail activity
            onView(withId(R.id.action_search_sessions)).check(matches(isDisplayed()));
            onView(withId(R.id.share_fab)).check(matches(isDisplayed()));
            List<Session> sessions = dbSingleton.getSessionbyTracksname(model.getName());
            if (sessions.size() > 0) {

                //check scroll is fine and last element is displayed
                onView(withId(R.id.list_speakerss)).perform(RecyclerViewActions.scrollToPosition(sessions.size() - 1)).check(matches(isDisplayed()));

                //taking any random element from the list of sessions
                rand = (int) (Math.random() * (sessions.size() - 1));

                //check random element is present and we click it
                onView(allOf(withId(R.id.list_speakerss))).perform(RecyclerViewActions.scrollToPosition(rand)).check(matches(isDisplayed()));
                onView(allOf(withId(R.id.list_speakerss))).perform(RecyclerViewActions.actionOnItemAtPosition(rand, click()));

                //now check for session detail activity
                onView(withId(R.id.bookmark_status)).check(matches(isDisplayed()));

            }
        }

    }

    @Test
    public void testFragmentSwitch() {

        //this will check fragment transaction is occuring fine or not.
        openDrawer(R.id.drawer);

        String toolbarTitle = context.getString(R.string.menu_tracks);
        onView(allOf(withText(toolbarTitle), isChecked())).perform(click());
        onView(allOf(withText(toolbarTitle), isAssignableFrom(AppCompatCheckedTextView.class))).check(matches(withText(toolbarTitle.toString())));

        openDrawer(R.id.drawer);
        toolbarTitle = context.getString(R.string.menu_bookmarks);
        onView(allOf(withText(toolbarTitle), isNotChecked())).perform(click());
        onView(allOf(withText(toolbarTitle), isAssignableFrom(AppCompatCheckedTextView.class))).check(matches(withText(toolbarTitle.toString())));

        openDrawer(R.id.drawer);
        toolbarTitle = context.getString(R.string.menu_speakers);
        onView(allOf(withText(toolbarTitle), isNotChecked())).perform(click());
        onView(allOf(withText(toolbarTitle), isAssignableFrom(AppCompatCheckedTextView.class))).check(matches(withText(toolbarTitle.toString())));

        openDrawer(R.id.drawer);
        toolbarTitle = context.getString(R.string.menu_sponsor);
        onView(allOf(withText(toolbarTitle), isNotChecked())).perform(click());
        onView(allOf(withText(toolbarTitle), isAssignableFrom(AppCompatCheckedTextView.class))).check(matches(withText(toolbarTitle.toString())));

        openDrawer(R.id.drawer);
        toolbarTitle = context.getString(R.string.menu_map);
        onView(allOf(withText(toolbarTitle), isNotChecked())).perform(click());
        onView(allOf(withText(toolbarTitle), isAssignableFrom(AppCompatCheckedTextView.class))).check(matches(withText(toolbarTitle.toString())));

        openDrawer(R.id.drawer);
        toolbarTitle = context.getString(R.string.menu_locations);
        onView(allOf(withText(toolbarTitle), isNotChecked())).perform(click());
        onView(allOf(withText(toolbarTitle), isAssignableFrom(AppCompatCheckedTextView.class))).check(matches(withText(toolbarTitle.toString())));

    }
}
