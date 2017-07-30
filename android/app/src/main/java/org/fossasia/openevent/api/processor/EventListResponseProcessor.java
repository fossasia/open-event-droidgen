package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.api.DataDownloadManager;
import org.fossasia.openevent.data.Event;
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
        int counterRequests = 7;

        final DataDownloadManager download = DataDownloadManager.getInstance();
        OpenEventApp.postEventOnUIThread(new CounterEvent(counterRequests));

        save(event);

        download.downloadSession();
        download.downloadSpeakers();
        download.downloadTracks();
        download.downloadMicrolocations();
        download.downloadSponsors();
        download.downloadSessionTypes();
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