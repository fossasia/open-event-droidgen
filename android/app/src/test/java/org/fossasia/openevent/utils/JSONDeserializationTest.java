package org.fossasia.openevent.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.SessionType;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Track;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static junit.framework.Assert.assertTrue;

public class JSONDeserializationTest {

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testLocalJsonDeserialization() throws IOException {
        //Event Deserialization
        assertTrue(doModelDeserialization(Event.class, "event", false));

        //Microlocations Deserialization
        assertTrue(doModelDeserialization(Microlocation.class, "microlocations", true));

        //Sponsor Deserialization
        assertTrue(doModelDeserialization(Sponsor.class, "sponsors", true));

        //Track Deserialization
        assertTrue(doModelDeserialization(Track.class, "tracks", true));

        //SessionType Deserialization
        assertTrue(doModelDeserialization(SessionType.class, "session_types", true));

        //Session Deserialization
        assertTrue(doModelDeserialization(Session.class, "sessions", true));

        //Speakers Deserialization
        assertTrue(doModelDeserialization(Speaker.class, "speakers", true));
    }

    private <T> boolean doModelDeserialization(Class<T> type, String name, boolean isList) throws IOException {
        if (isList) {
            List<T> items = objectMapper.readValue(readFile(name), objectMapper.getTypeFactory().constructCollectionType(List.class, type));
            if (items == null)
                return false;
        } else {
            T item = objectMapper.readValue(readFile(name), type);
            if (item == null)
                return false;
        }
        return true;
    }

    private String readFile(String name) throws IOException {
        String json = "";
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(name);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }
}