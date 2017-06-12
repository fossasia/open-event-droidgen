package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.api.DataDownloadManager;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.extras.Version;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.CounterEvent;
import org.fossasia.openevent.events.DownloadEvent;
import org.fossasia.openevent.events.EventDownloadEvent;
import org.fossasia.openevent.events.RetrofitResponseEvent;

import timber.log.Timber;

public class EventListResponseProcessor extends ResponseProcessor<Event> {

    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();

    private void save(Event event) {
        realmRepo.saveEvent(event).subscribe(() ->
                OpenEventApp.postEventOnUIThread(new EventDownloadEvent(true)),
                Timber::e);
    }

    @Override
    protected void onSuccess(Event event) {
        int counterRequests = 0;

        final Version version = event.getVersion();

        final DataDownloadManager download = DataDownloadManager.getInstance();

        Version storedVersion = realmRepo.getVersionIdsSync();

        if (storedVersion == null) {
            Timber.d("Version info not present. Downloading complete data again...");

            counterRequests = 6;
            OpenEventApp.postEventOnUIThread(new CounterEvent(counterRequests));

            save(event);

            download.downloadSession();
            download.downloadSpeakers();
            download.downloadTracks();
            download.downloadMicrolocations();
            download.downloadSponsors();
        } else {
            if (storedVersion.getEventVer() != version.getEventVer()) {
                Timber.d("Downloading EVENT");
                save(event);

                counterRequests++;
            }

            if (storedVersion.getSponsorsVer() != version.getSponsorsVer()) {
                download.downloadSponsors();

                Timber.d("Downloading Sponsor");
                counterRequests++;
            }

            if (storedVersion.getSpeakersVer() != version.getSpeakersVer()) {
                download.downloadSpeakers();

                Timber.d("Downloading SPEAKERS");
                counterRequests++;
            }

            if (storedVersion.getSessionsVer() != version.getSessionsVer()) {
                download.downloadSession();

                Timber.d("Downloading SESSIONS");
                counterRequests++;
            }

            if (storedVersion.getTracksVer() != version.getTracksVer()) {
                download.downloadTracks();

                Timber.d("Downloading TRACKS");
                counterRequests++;
            }

            if (storedVersion.getMicrolocationsVer() != version.getMicrolocationsVer()) {
                download.downloadMicrolocations();
                Timber.d("Downloading microlocations");

                counterRequests++;
            }

            if (counterRequests == 0) {
                Timber.d("Data fresh");
            } else {
                OpenEventApp.postEventOnUIThread(new CounterEvent(counterRequests));
            }
        }
    }

    @Override
    protected DownloadEvent getDownloadEvent(boolean success) {
        return null;
    }

    @Override
    protected Object getErrorResponseEvent(int errorCode) {
        return new RetrofitResponseEvent(errorCode);
    }
}