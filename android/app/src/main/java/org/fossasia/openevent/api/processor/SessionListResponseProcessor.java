package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.DownloadEvent;
import org.fossasia.openevent.events.SessionDownloadEvent;

import java.util.List;

import io.reactivex.Observable;

public class SessionListResponseProcessor extends ResponseProcessor<List<Session>> {

    @Override
    protected void onSuccess(List<Session> sessions) {
        complete(Observable.fromIterable(sessions)
                .map(session -> {
                    if (session.getMicrolocation() == null)
                        session.setMicrolocation(new Microlocation(0, ""));
                    session.setStartDate(session.getStartTime().split("T")[0]);

                    return session;
                })
                .toList()
                .flatMapCompletable(sessionList ->
                        RealmDataRepository
                        .getDefaultInstance()
                        .saveSessions(sessionList)));
    }

    @Override
    protected DownloadEvent getDownloadEvent(boolean success) {
        return new SessionDownloadEvent(success);
    }

    @Override
    protected Object getErrorResponseEvent(int errorCode) {
        return getDownloadEvent(false);
    }
}