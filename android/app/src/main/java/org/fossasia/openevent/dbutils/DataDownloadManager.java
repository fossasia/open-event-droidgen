package org.fossasia.openevent.dbutils;

import org.fossasia.openevent.api.APIClient;
import org.fossasia.openevent.api.processor.EventListResponseProcessor;
import org.fossasia.openevent.api.processor.MicrolocationListResponseProcessor;
import org.fossasia.openevent.api.processor.SessionListResponseProcessor;
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
    static DataDownloadManager instance;

    APIClient client = new APIClient();

    private DataDownloadManager() {
    }

    public static DataDownloadManager getInstance() {
        if (instance == null) {
            instance = new DataDownloadManager();
        }
        return instance;
    }

    public void downloadEvents() {
        client.getOpenEventAPI().getEvents().enqueue(new EventListResponseProcessor());
    }

    public void downloadSpeakers() {
        client.getOpenEventAPI().getSpeakers().enqueue(new SpeakerListResponseProcessor());
    }

    public void downloadSponsors() {
        client.getOpenEventAPI().getSponsors().enqueue(new SponsorListResponseProcessor());
    }

    public void downloadSession() {
        client.getOpenEventAPI().getSessions("start_time.asc").enqueue(new SessionListResponseProcessor());
    }

    public void downloadTracks() {
        client.getOpenEventAPI().getTracks().enqueue(new TrackListResponseProcessor());
    }

    public void downloadMicrolocations() {
        client.getOpenEventAPI().getMicrolocations().enqueue(new MicrolocationListResponseProcessor());
    }


}
