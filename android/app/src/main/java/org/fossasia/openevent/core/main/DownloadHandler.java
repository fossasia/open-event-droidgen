package org.fossasia.openevent.core.main;

import android.content.Context;

import org.fossasia.openevent.common.ConstantStrings;
import org.fossasia.openevent.common.api.APIClient;
import org.fossasia.openevent.common.api.Urls;
import org.fossasia.openevent.common.utils.SharedPreferencesUtil;
import org.fossasia.openevent.data.repository.RealmDataRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.realm.Realm;
import timber.log.Timber;

public class DownloadHandler {

    private final Context context;

    public DownloadHandler(Context context) {
        this.context = context;
    }

    public Observable<String> downloadFromAsset() {
        List<Observable<String>> list = new ArrayList<>();
        list.add(getEvent().toSingleDefault(ConstantStrings.EVENT).toObservable());
        list.add(getSpeakers().toSingleDefault(ConstantStrings.SPEAKERS).toObservable());
        list.add(getSessions().toSingleDefault(ConstantStrings.SESSIONS).toObservable());
        list.add(getSponsors().toSingleDefault(ConstantStrings.SPONSORS).toObservable());
        list.add(getTracks().toSingleDefault(ConstantStrings.TRACKS).toObservable());
        list.add(getMicroLocations().toSingleDefault(ConstantStrings.MICROLOCATIONS).toObservable());
        list.add(getSessionTypes().toSingleDefault(ConstantStrings.SESSION_TYPES).toObservable());
        return Observable.merge(list);
    }

    public Observable<String> downloadFromInternet() {
        return Observable.concat(getEventFromInternet().toSingleDefault(ConstantStrings.EVENT).toObservable(), downloadBatch());
    }

    public Completable getEvent() {
        return readJsonAsset(Urls.EVENT)
                .flatMapCompletable(JsonHandler::handleEvent);
    }

    public Completable getSessions() {
        return readJsonAsset(Urls.SESSIONS)
                .flatMapCompletable(JsonHandler::handleSessions);
    }

    public Completable getSpeakers() {
        return readJsonAsset(Urls.SPEAKERS)
                .flatMapCompletable(JsonHandler::handleSpeakers);
    }

    public Completable getTracks() {
        return readJsonAsset(Urls.TRACKS)
                .flatMapCompletable(JsonHandler::handleTracks);
    }

    public Completable getSponsors() {
        return readJsonAsset(Urls.SPONSORS)
                .flatMapCompletable(JsonHandler::handleSponsors);
    }

    public Completable getMicroLocations() {
        return readJsonAsset(Urls.MICROLOCATIONS)
                .flatMapCompletable(JsonHandler::handleMicroLocations);
    }

    public Completable getSessionTypes() {
        return readJsonAsset(Urls.SESSION_TYPES)
                .flatMapCompletable(JsonHandler::handleSessionTypes);
    }

    private Single<String> readJsonAsset(final String name) {
        return Single.fromCallable(() -> {
            try {
                InputStream inputStream = context.getAssets().open(name);
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                if (inputStream.read(buffer) == -1)
                    Timber.d("Empty Stream");
                inputStream.close();
                return new String(buffer, "UTF-8");
            } catch (IOException ioException) {
                Timber.e(ioException, "Error on reading event %s from Assets", name);
                throw ioException;
            }
        });
    }

    private Single<Integer> getEventId() {
        return Single.fromCallable(() -> {
            int eventId = SharedPreferencesUtil.getInt(ConstantStrings.EVENT_ID, 0);
            if (eventId == 0) throw new Exception("error");
            return eventId;
        });
    }

    public Completable getEventFromInternet() {
        RealmDataRepository realmDataRepository = RealmDataRepository.getInstance(Realm.getDefaultInstance());
        return getEventId()
                .flatMapObservable((eventId) -> APIClient.getOpenEventAPI().getEventObservable(eventId))
                .flatMapCompletable(realmDataRepository::saveEvent);
    }

    public Completable getSessionsFromInternet() {
        RealmDataRepository realmDataRepository = RealmDataRepository.getInstance(Realm.getDefaultInstance());
        return APIClient.getOpenEventAPI().getSessionsObservable()
                .flatMapCompletable(realmDataRepository::saveSessions);
    }

    public Completable getSpeakersFromInternet() {
        RealmDataRepository realmDataRepository = RealmDataRepository.getInstance(Realm.getDefaultInstance());
        return APIClient.getOpenEventAPI().getSpeakersSingle()
                .flatMapCompletable(realmDataRepository::saveSpeakers);
    }

    public Completable getTracksFromInternet() {
        RealmDataRepository realmDataRepository = RealmDataRepository.getInstance(Realm.getDefaultInstance());
        return APIClient.getOpenEventAPI().getTracksObservable()
                .flatMapCompletable(realmDataRepository::saveTracks);
    }

    public Completable getSponsorsFromInternet() {
        RealmDataRepository realmDataRepository = RealmDataRepository.getInstance(Realm.getDefaultInstance());
        return APIClient.getOpenEventAPI().getSponsorsObservable()
                .flatMapCompletable(realmDataRepository::saveSponsors);
    }

    public Completable getMicroLocationsFromInternet() {
        RealmDataRepository realmDataRepository = RealmDataRepository.getInstance(Realm.getDefaultInstance());
        return APIClient.getOpenEventAPI().getMicrolocationsObservable()
                .flatMapCompletable(realmDataRepository::saveLocations);
    }

    public Completable getSessionTypesFromInternet() {
        RealmDataRepository realmDataRepository = RealmDataRepository.getInstance(Realm.getDefaultInstance());
        return APIClient.getOpenEventAPI().getSessionTypesObservable()
                .flatMapCompletable(realmDataRepository::saveSessionTypes);
    }

    private Observable<String> downloadBatch() {
        List<Observable<String>> list = new ArrayList<>();
        list.add(getSessionsFromInternet().toSingleDefault(ConstantStrings.SESSIONS).toObservable());
        list.add(getSpeakersFromInternet().toSingleDefault(ConstantStrings.SPEAKERS).toObservable());
        list.add(getTracksFromInternet().toSingleDefault(ConstantStrings.TRACKS).toObservable());
        list.add(getMicroLocationsFromInternet().toSingleDefault(ConstantStrings.MICROLOCATIONS).toObservable());
        list.add(getSponsorsFromInternet().toSingleDefault(ConstantStrings.SPONSORS).toObservable());
        list.add(getSessionTypesFromInternet().toSingleDefault(ConstantStrings.SESSION_TYPES).toObservable());
        return Observable.merge(list);
    }

}
