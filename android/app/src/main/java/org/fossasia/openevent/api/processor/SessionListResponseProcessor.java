package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.SessionDownloadEvent;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * User: MananWason
 * Date: 27-05-2015
 */
public class SessionListResponseProcessor implements Callback<List<Session>> {

    @Override
    public void onResponse(Call<List<Session>> call, final Response<List<Session>> response) {
        if (response.isSuccessful()) {

            List<Session> sessions = response.body();

            Observable.fromIterable(sessions)
                    .map(session -> {
                        if(session.getMicrolocation() == null){
                            session.setMicrolocation(new Microlocation(0, ""));
                        }
                        session.setStartDate(session.getStartTime().split("T")[0]);

                        return session;
                    })
                    .toList()
                    .flatMapCompletable(sessionList -> RealmDataRepository.getDefaultInstance().saveSessions(sessionList))
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> OpenEventApp.postEventOnUIThread(new SessionDownloadEvent(true)));
        } else {
            OpenEventApp.getEventBus().post(new SessionDownloadEvent(false));
        }
    }

    @Override
    public void onFailure(Call<List<Session>> call, Throwable t) {
        OpenEventApp.getEventBus().post(new SessionDownloadEvent(false));
    }
}