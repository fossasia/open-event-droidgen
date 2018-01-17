package org.fossasia.openevent.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.RealmDataRepository;

import io.realm.RealmChangeListener;

public class SessionDetailActivityViewModel extends ViewModel {

    public static final String BY_ID = "id";
    public static final String BY_NAME = "name";

    private RealmDataRepository realmRepo;
    private MutableLiveData<Session> sessionByIdLiveData;
    private MutableLiveData<Session> sessionByNameLiveData;
    private Session sessionById;
    private Session sessionByName;

    public SessionDetailActivityViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
    }

    public LiveData<Session> getSessionById(int id, String loadedFlag) {
        if (sessionByIdLiveData == null) {
            sessionByIdLiveData = new MutableLiveData<>();
            sessionById = realmRepo.getSession(id);
            sessionById.addChangeListener((RealmChangeListener<Session>) loadedSession -> {
                if (!loadedSession.isValid())
                    return;

                if (loadedFlag == null || loadedFlag.equals(BY_ID)) {
                    sessionByIdLiveData.setValue(loadedSession);
                }
            });
        }
        return sessionByIdLiveData;
    }

    public LiveData<Session> getSessionByName(String title, String loadedFlag) {
        if (sessionByNameLiveData == null) {
            sessionByNameLiveData = new MutableLiveData<>();
            sessionByName = realmRepo.getSession(title);
            sessionByName.addChangeListener((RealmChangeListener<Session>) loadedSession -> {
                if (!loadedSession.isValid())
                    return;

                if (loadedFlag == null || loadedFlag.equals(BY_NAME)) {
                    sessionByNameLiveData.setValue(loadedSession);
                }
            });
        }
        return sessionByNameLiveData;
    }

    public void setBookmark(Session session, boolean bookmark) {
        realmRepo.setBookmark(session.getId(), bookmark).subscribe();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (sessionById != null) {
            sessionById.removeAllChangeListeners();
        }
        if (sessionByName != null) {
            sessionByName.removeAllChangeListeners();
        }
    }
}
