package org.fossasia.openevent.common.api.processor;

import org.fossasia.openevent.data.SessionType;
import org.fossasia.openevent.data.repository.RealmDataRepository;
import org.fossasia.openevent.common.events.DownloadEvent;
import org.fossasia.openevent.common.events.SessionTypesDownloadEvent;

import java.util.List;

public class SessionTypeListResponseProcessor extends ResponseProcessor<List<SessionType>> {

    @Override
    protected void onSuccess(List<SessionType> sessionTypes) {
        complete(RealmDataRepository.getDefaultInstance()
                .saveSessionTypes(sessionTypes));
    }

    @Override
    protected DownloadEvent getDownloadEvent(boolean suceess) {
        return new SessionTypesDownloadEvent(suceess);
    }

    @Override
    protected Object getErrorResponseEvent(int errorCode) {
        return getDownloadEvent(false);
    }
}
