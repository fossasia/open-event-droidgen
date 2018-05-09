package org.fossasia.openevent.core.main;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.fossasia.openevent.common.api.APIClient;
import org.fossasia.openevent.common.date.DateConverter;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.SessionType;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.data.extras.EventDates;
import org.fossasia.openevent.data.repository.RealmDataRepository;
import org.threeten.bp.format.DateTimeParseException;

import java.util.List;

import io.reactivex.Completable;
import io.realm.Realm;
import timber.log.Timber;

/**
 * Created by apple on 20/03/18.
 */

public class JsonHandler {

    public static Completable handleEvent(String json) {
        return Completable.fromAction(() -> {
            ObjectMapper objectMapper = APIClient.getObjectMapper();

            Realm realm = Realm.getDefaultInstance();

            RealmDataRepository realmDataRepository = RealmDataRepository
                    .getInstance(realm);

            Event event = objectMapper.readValue(json, Event.class);

            String startTime = event.getStartsAt();
            String endTime = event.getEndsAt();
            try {
                List<EventDates> eventDates = DateConverter.getDaysInBetween(startTime, endTime);
                realmDataRepository.saveEventDates(eventDates).subscribe();
            } catch (DateTimeParseException dateTimeParseException) {
                Timber.e(dateTimeParseException);
                Timber.e("Error start parsing start date: %s and end date: %s in ISO format", startTime, endTime);
                throw new Exception("Error parsing dates");
            }

            realmDataRepository.saveEvent(event).subscribe();

            realm.close();
        });
    }

    public static Completable handleSessions(String json) {
        return Completable.fromAction(() -> {
            ObjectMapper objectMapper = APIClient.getObjectMapper();

            Realm realm = Realm.getDefaultInstance();

            RealmDataRepository realmDataRepository = RealmDataRepository
                    .getInstance(realm);

            List<Session> sessions = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Session.class));
            for (Session current : sessions) {
                current.setStartDate(current.getStartsAt().split("T")[0]);
            }
            realmDataRepository.saveSessions(sessions).subscribe();

            realm.close();
        });
    }

    public static Completable handleTracks(String json) {
        return Completable.fromAction(() -> {
            ObjectMapper objectMapper = APIClient.getObjectMapper();

            Realm realm = Realm.getDefaultInstance();

            RealmDataRepository realmDataRepository = RealmDataRepository
                    .getInstance(realm);

            List<Track> tracks = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Track.class));
            realmDataRepository.saveTracks(tracks).subscribe();

            realm.close();
        });
    }

    public static Completable handleSpeakers(String json) {
        return Completable.fromAction(() -> {
            ObjectMapper objectMapper = APIClient.getObjectMapper();

            Realm realm = Realm.getDefaultInstance();

            RealmDataRepository realmDataRepository = RealmDataRepository
                    .getInstance(realm);

            List<Speaker> speakers = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Speaker.class));
            realmDataRepository.saveSpeakers(speakers).subscribe();

            realm.close();
        });
    }

    public static Completable handleSponsors(String json) {
        return Completable.fromAction(() -> {
            ObjectMapper objectMapper = APIClient.getObjectMapper();

            Realm realm = Realm.getDefaultInstance();

            RealmDataRepository realmDataRepository = RealmDataRepository
                    .getInstance(realm);

            List<Sponsor> sponsors = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Sponsor.class));
            realmDataRepository.saveSponsors(sponsors).subscribe();

            realm.close();
        });
    }

    public static Completable handleMicroLocations(String json) {
        return Completable.fromAction(() -> {
            ObjectMapper objectMapper = APIClient.getObjectMapper();

            Realm realm = Realm.getDefaultInstance();

            RealmDataRepository realmDataRepository = RealmDataRepository
                    .getInstance(realm);

            List<Microlocation> microLocations = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Microlocation.class));
            realmDataRepository.saveLocations(microLocations).subscribe();

            realm.close();
        });
    }

    public static Completable handleSessionTypes(String json) {
        return Completable.fromAction(() -> {
            ObjectMapper objectMapper = APIClient.getObjectMapper();

            Realm realm = Realm.getDefaultInstance();

            RealmDataRepository realmDataRepository = RealmDataRepository
                    .getInstance(realm);

            List<SessionType> sessionTypes = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, SessionType.class));
            realmDataRepository.saveSessionTypes(sessionTypes).subscribe();

            realm.close();
        });
    }
}
