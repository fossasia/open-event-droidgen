package org.fossasia.openevent.common.api;

import org.fossasia.openevent.common.api.processor.EventListResponseProcessor;
import org.fossasia.openevent.common.api.processor.MicrolocationListResponseProcessor;
import org.fossasia.openevent.common.api.processor.SessionListResponseProcessor;
import org.fossasia.openevent.common.api.processor.SessionTypeListResponseProcessor;
import org.fossasia.openevent.common.api.processor.SpeakerListResponseProcessor;
import org.fossasia.openevent.common.api.processor.SponsorListResponseProcessor;
import org.fossasia.openevent.common.api.processor.TrackListResponseProcessor;

/**
 * A singleton to keep track of download
 */
public final class DataDownloadManager {
    private static DataDownloadManager instance;

    private DataDownloadManager() {
    }

    public static DataDownloadManager getInstance() {
        if (instance == null) {
            instance = new DataDownloadManager();
        }
        return instance;
    }

    public void downloadEvent(int eventId) {
        APIClient.getOpenEventAPI().getEvent(eventId).enqueue(new EventListResponseProcessor());
    }

    public void downloadSpeakers() {
        APIClient.getOpenEventAPI().getSpeakers().enqueue(new SpeakerListResponseProcessor());
    }

    public void downloadSponsors() {
        APIClient.getOpenEventAPI().getSponsors().enqueue(new SponsorListResponseProcessor());
    }

    public void downloadSession() {
        APIClient.getOpenEventAPI().getSessions().enqueue(new SessionListResponseProcessor());
    }

    public void downloadTracks() {
        APIClient.getOpenEventAPI().getTracks().enqueue(new TrackListResponseProcessor());
    }

    public void downloadMicrolocations() {
        APIClient.getOpenEventAPI().getMicrolocations().enqueue(new MicrolocationListResponseProcessor());
    }

    public void downloadSessionTypes() {
        APIClient.getOpenEventAPI().getSessionTypes().enqueue(new SessionTypeListResponseProcessor());
    }
}
