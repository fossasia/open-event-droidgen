package org.fossasia.openevent;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.test.AndroidTestCase;

import com.google.gson.Gson;

import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.helper.IOUtils;
import org.junit.Test;

/**
 * User: mohit
 * Date: 25/1/16
 */
public class EventInsertTest extends AndroidTestCase {
    private static final String TAG = SpeakerInsertTest.class.getSimpleName();

    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @Test
    public void testEventInsertion() {
        Gson gson = new Gson();
        // For reading resources we need this
        String jsonStr = IOUtils.readRaw(org.fossasia.openevent.test.R.raw.event_v1, InstrumentationRegistry.getContext());
        Event event = gson.fromJson(jsonStr, Event.class);
        String query = event.generateSql();

        DbSingleton instance = new DbSingleton(context);
        instance.clearDatabase(DbContract.Event.TABLE_NAME);
        instance.insertQuery(query);

        Event eventDetails = instance.getEventDetails();
        assertNotNull(eventDetails);
        assertEquals(event.getEmail(), eventDetails.getEmail());
        assertEquals(event.getColor(), eventDetails.getColor());
        assertEquals(event.getLogo(), eventDetails.getLogo());
    }
}
