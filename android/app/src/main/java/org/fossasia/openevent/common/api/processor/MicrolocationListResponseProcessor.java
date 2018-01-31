package org.fossasia.openevent.common.api.processor;

import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.repository.RealmDataRepository;
import org.fossasia.openevent.common.events.DownloadEvent;
import org.fossasia.openevent.common.events.MicrolocationDownloadEvent;

import java.util.List;

public class MicrolocationListResponseProcessor extends ResponseProcessor<List<Microlocation>> {

    @Override
    protected void onSuccess(List<Microlocation> microlocations) {
        complete(RealmDataRepository.getDefaultInstance()
                .saveLocations(microlocations));
    }

    @Override
    protected DownloadEvent getDownloadEvent(boolean success) {
        return new MicrolocationDownloadEvent(success);
    }

    @Override
    protected Object getErrorResponseEvent(int errorCode) {
        return getDownloadEvent(false);
    }
}