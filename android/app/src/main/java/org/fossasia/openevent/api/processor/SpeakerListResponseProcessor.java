package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.DownloadEvent;
import org.fossasia.openevent.events.SpeakerDownloadEvent;

import java.util.List;

public class SpeakerListResponseProcessor extends ResponseProcessor<List<Speaker>> {

    @Override
    protected void onSuccess(List<Speaker> speakers) {
        complete(RealmDataRepository.getDefaultInstance()
                .saveSpeakers(speakers));
    }

    @Override
    protected DownloadEvent getDownloadEvent(boolean success) {
        return new SpeakerDownloadEvent(success);
    }

    @Override
    protected Object getErrorResponseEvent(int errorCode) {
        return getDownloadEvent(false);
    }
}