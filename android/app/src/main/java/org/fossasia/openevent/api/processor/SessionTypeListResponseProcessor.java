package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.data.SessionType;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.DownloadEvent;
import org.fossasia.openevent.events.SessionTypesDownloadEvent;

import java.util.List;

/**
 * Created by arpitdec5 on 30-06-2017.
 */

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
