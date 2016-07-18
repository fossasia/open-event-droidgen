package org.fossasia.openevent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.EventDates;
import org.fossasia.openevent.data.Microlocation;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by championswimmer on 18/7/16.
 */
public class ModelsTest {

    Event.EventList events;
    EventDates.EventDatesList eventDatesList;
    Microlocation microlocation;

    public static final String  ASSET_BASE_PATH = "../app/src/main/assets/";

    @Before
    public void createModels () throws IOException, JSONException {
        Gson gson = new GsonBuilder().create();
        events = gson.fromJson(readJsonFile("events.json"),
                Event.EventList.class);
        eventDatesList = gson.fromJson(readJsonFile("eventDates.json"), EventDates.EventDatesList.class);
    }

    @Test
    public void eventModelTest () {
        assertEquals(4, events.getEvents().get(0).getId());
        assertEquals("2015-05-05", eventDatesList.getDates().get(0).getDate());
    }

    public String readJsonFile (String filename) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ASSET_BASE_PATH + filename)));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        while (line != null) {
            sb.append(line);
            line = br.readLine();
        }

        return sb.toString();
    }
}
