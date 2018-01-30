package org.fossasia.openevent.common.api.processor;

import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.data.repository.RealmDataRepository;
import org.fossasia.openevent.common.events.DownloadEvent;
import org.fossasia.openevent.common.events.TracksDownloadEvent;

import java.util.List;
public class TrackListResponseProcessor extends ResponseProcessor<List<Track>> {

    @Override
    protected void onSuccess(List<Track> tracks) {
        complete(RealmDataRepository.getDefaultInstance()
                .saveTracks(tracks));
    }

    @Override
    protected DownloadEvent getDownloadEvent(boolean success) {
        return new TracksDownloadEvent(success);
    }

    @Override
    protected Object getErrorResponseEvent(int errorCode) {
        return getDownloadEvent(false);
    }
}