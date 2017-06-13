package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.DownloadEvent;
import org.fossasia.openevent.events.TracksDownloadEvent;

import java.util.List;
public class TrackListResponseProcessor extends ResponseProcessor<List<Track>> {

    @Override
    protected void onSuccess(List<Track> tracks) {
        complete(RealmDataRepository.getDefaultInstance()
                .saveTracks(tracks));
    }

    @Override
    protected DownloadEvent getDownloadEvent(boolean suceess) {
        return new TracksDownloadEvent(suceess);
    }

    @Override
    protected Object getErrorResponseEvent(int errorCode) {
        return getDownloadEvent(false);
    }
}