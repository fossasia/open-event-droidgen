package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.extras.Version;
import org.fossasia.openevent.api.DataDownloadManager;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.CounterEvent;
import org.fossasia.openevent.events.RetrofitError;
import org.fossasia.openevent.events.RetrofitResponseEvent;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 27-05-2015
 */
public class EventListResponseProcessor implements Callback<Event> {
    private int counterRequests;

    private RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();

    @Override
    public void onResponse(Call<Event> call, final Response<Event> response) {

        if (!response.isSuccessful()) {
            OpenEventApp.postEventOnUIThread(new RetrofitResponseEvent(response.code()));
        } else {
            final Event event = response.body();

            counterRequests = 0;

            final Version version = event.getVersion();

            final DataDownloadManager download = DataDownloadManager.getInstance();

            Version storedVersion = realmRepo.getVersionIdsSync();

            if (storedVersion == null) {
                Timber.d("Version info not present. Downloading complete data again...");

                realmRepo.saveEvent(event).subscribe();

                download.downloadSession();
                download.downloadSpeakers();
                download.downloadTracks();
                download.downloadMicrolocations();
                download.downloadSponsors();

                counterRequests += 5;
            } else {
                if (storedVersion.getEventVer() != version.getEventVer()) {
                    Timber.d("Downloading EVENT");
                    realmRepo.saveEvent(event).subscribe();
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
                }
            }

        }
        CounterEvent counterEvent = new CounterEvent(counterRequests);
        OpenEventApp.postEventOnUIThread(counterEvent);
    }


    @Override
    public void onFailure(Call<Event> call, Throwable t) {
        OpenEventApp.postEventOnUIThread(new RetrofitError(t));
    }
}