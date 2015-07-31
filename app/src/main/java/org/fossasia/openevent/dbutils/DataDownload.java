package org.fossasia.openevent.dbutils;

import android.util.Log;

import org.fossasia.openevent.api.APIClient;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.api.processor.EventListResponseProcessor;
import org.fossasia.openevent.api.processor.MicrolocationListResponseProcessor;
import org.fossasia.openevent.api.processor.SessionListResponseProcessor;
import org.fossasia.openevent.api.processor.SpeakerListResponseProcessor;
import org.fossasia.openevent.api.processor.SponsorListResponseProcessor;
import org.fossasia.openevent.api.processor.TrackListResponseProcessor;
import org.fossasia.openevent.api.processor.VersionApiProcessor;

/**
 * Created by MananWason on 31-05-2015.
 */
public class DataDownload {
    APIClient client = new APIClient();


    public void downloadEvents() {
        client.getOpenEventAPI().getEvents(new EventListResponseProcessor());
    }

    public void downloadSpeakers() {
        client.getOpenEventAPI().getSpeakers(Urls.EVENT_ID, new SpeakerListResponseProcessor());
    }

    public void downloadSponsors() {
        client.getOpenEventAPI().getSponsors(Urls.EVENT_ID, new SponsorListResponseProcessor());
    }

    public void downloadSession() {
        client.getOpenEventAPI().getSessions(Urls.EVENT_ID, new SessionListResponseProcessor());
    }

    public void downloadTracks() {
        client.getOpenEventAPI().getTracks(Urls.EVENT_ID, new TrackListResponseProcessor());
        Log.d("TRACKS", "download");
    }

    public void downloadMicrolocations() {
        client.getOpenEventAPI().getMicrolocations(Urls.EVENT_ID, new MicrolocationListResponseProcessor());
    }

    public void downloadVersions() {
        client.getOpenEventAPI().getVersion(Urls.EVENT_ID, new VersionApiProcessor());
        Log.d("!", "2");
    }


}
