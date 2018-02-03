package org.fossasia.openevent.common.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.fossasia.openevent.common.api.APIClient;
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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class JSONDeserializationTest {

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = APIClient.getObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    }

    @Test
    public void testEventModelDeserialization() throws IOException {
        Event event = objectMapper.readValue(readFile("event"), Event.class);
        assertNotNull(event);
        assertEquals(6, event.getId());
        assertTrue(event.getIsSessionsSpeakersEnabled());
        assertEquals(9, event.getSocialLinks().size());
    }

    @Test
    public void testTrackModelDeserialization() throws IOException {
        List<Track> tracks = objectMapper.readValue(readFile("tracks"), objectMapper.getTypeFactory().constructCollectionType(List.class, Track.class));
        assertNotNull(tracks);
        assertEquals(23, tracks.size());
        assertEquals(3, tracks.get(0).getSessions().size());
    }

    @Test
    public void testMicrolocationModelDeserialization() throws IOException {
        List<Microlocation> microlocations = objectMapper.readValue(readFile("microlocations"), objectMapper.getTypeFactory().constructCollectionType(List.class, Microlocation.class));
        assertNotNull(microlocations);
        assertEquals(15, microlocations.size());
    }

    @Test
    public void testSponsorModelDeserialization() throws IOException {
        List<Track> sponsors = objectMapper.readValue(readFile("sponsors"), objectMapper.getTypeFactory().constructCollectionType(List.class, Sponsor.class));
        assertNotNull(sponsors);
        assertEquals(17, sponsors.size());
    }

    @Test
    public void testSessionModelDeserialization() throws IOException {
        List<Session> sessions = objectMapper.readValue(readFile("sessions"), objectMapper.getTypeFactory().constructCollectionType(List.class, Session.class));
        assertNotNull(sessions);
        assertEquals(228, sessions.size());
        assertEquals(1, sessions.get(0).getSpeakers().size());
        assertFalse(sessions.get(0).getIsMailSent());
    }

    @Test
    public void testSessionTypeModelDeserialization() throws IOException {
        List<SessionType> sessionTypes = objectMapper.readValue(readFile("session_types"), objectMapper.getTypeFactory().constructCollectionType(List.class, SessionType.class));
        assertNotNull(sessionTypes);
        assertEquals(32, sessionTypes.size());
    }

    @Test
    public void testSpeakerModelDeserialization() throws IOException {
        List<Speaker> speakers = objectMapper.readValue(readFile("speakers"), objectMapper.getTypeFactory().constructCollectionType(List.class, Speaker.class));
        assertNotNull(speakers);
        assertEquals(207, speakers.size());
        assertFalse(speakers.get(0).getIsFeatured());
        assertEquals(1, speakers.get(0).getSessions().size());
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