package org.fossasia.openevent.common.api.processor;

import org.fossasia.openevent.common.api.DataDownloadManager;
import org.fossasia.openevent.common.events.CounterEvent;
import org.fossasia.openevent.common.events.DownloadEvent;
import org.fossasia.openevent.common.events.EventDownloadEvent;
import org.fossasia.openevent.common.events.RetrofitResponseEvent;
import org.fossasia.openevent.config.StrategyRegistry;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.repository.RealmDataRepository;

import timber.log.Timber;

public class EventListResponseProcessor extends ResponseProcessor<Event> {

    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();

    private void save(Event event) {
        realmRepo.saveEvent(event).subscribe(() ->
                        StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(new EventDownloadEvent(true)),
                        Timber::e);
    }

    @Override
    protected void onSuccess(Event event) {
        int counterRequests = 7;

        final DataDownloadManager download = DataDownloadManager.getInstance();
        StrategyRegistry.getInstance().getEventBusStrategy().postEventOnUIThread(new CounterEvent(counterRequests));

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