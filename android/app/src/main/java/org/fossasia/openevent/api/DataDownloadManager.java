package org.fossasia.openevent.api;

import org.fossasia.openevent.api.processor.EventListResponseProcessor;
import org.fossasia.openevent.api.processor.MicrolocationListResponseProcessor;
import org.fossasia.openevent.api.processor.SessionListResponseProcessor;
import org.fossasia.openevent.api.processor.SessionTypeListResponseProcessor;
import org.fossasia.openevent.api.processor.SpeakerListResponseProcessor;
import org.fossasia.openevent.api.processor.SponsorListResponseProcessor;
import org.fossasia.openevent.api.processor.TrackListResponseProcessor;

/**
 * User: MananWason
 * Date: 31-05-2015
 * <p/>
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
