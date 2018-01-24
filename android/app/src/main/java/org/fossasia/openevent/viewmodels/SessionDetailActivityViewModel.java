package org.fossasia.openevent.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.RealmDataRepository;

public class SessionDetailActivityViewModel extends ViewModel {

    public static final String BY_ID = "id";
    public static final String BY_NAME = "name";

    private RealmDataRepository realmRepo;
    private MutableLiveData<Session> sessionByIdMutableLiveData;
    private MutableLiveData<Session> sessionByNameMutableLiveData;
    private LiveData<Session> sessionByNameLiveData;
    private LiveData<Session> sessionByIdLiveData;
    private Observer<Session> sessionByIdObserver;
    private Observer<Session> sessionByNameObserver;


    public SessionDetailActivityViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
        sessionByNameMutableLiveData = new MutableLiveData<>();
        sessionByIdMutableLiveData = new MutableLiveData<>();
    }

    public LiveData<Session> getSessionById(int id, String loadedFlag) {
        if(sessionByIdLiveData == null) {
            sessionByIdLiveData = RealmDataRepository.asLiveDataForObject(realmRepo.getSession(id));
            Observer<Session> sessionObserver = session -> {
                if(!session.isValid()) {
                    return;
                }
                if(loadedFlag == null || loadedFlag.equals(BY_NAME)){
                    sessionByIdMutableLiveData.setValue(session);
                }
            };
            sessionByIdLiveData.observeForever(sessionObserver);
            sessionByIdObserver = sessionObserver;

            }
        return sessionByIdMutableLiveData;
    }

    public LiveData<Session> getSessionByName(String title, String loadedFlag) {
        if(sessionByNameLiveData == null) {
            sessionByNameLiveData = RealmDataRepository.asLiveDataForObject(realmRepo.getSession(title));
            Observer<Session> sessionObserver = session -> {
                if(!session.isValid()) {
                    return;
                }
                if(loadedFlag == null || loadedFlag.equals(BY_NAME)){
                    sessionByNameMutableLiveData.setValue(session);
                }
            };
            sessionByNameLiveData.observeForever(sessionObserver);
            sessionByNameObserver = sessionObserver;
        }
        return sessionByNameMutableLiveData;
    }

    public void setBookmark(Session session, boolean bookmark) {
        realmRepo.setBookmark(session.getId(), bookmark).subscribe();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(sessionByIdLiveData != null){
            sessionByIdLiveData.removeObserver(sessionByIdObserver);

        }
        if(sessionByNameLiveData != null){
            sessionByNameLiveData.removeObserver(sessionByNameObserver);

        }
    }
}
