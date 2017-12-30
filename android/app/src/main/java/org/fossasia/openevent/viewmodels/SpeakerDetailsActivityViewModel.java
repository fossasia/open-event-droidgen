package org.fossasia.openevent.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.RealmDataRepository;

import io.realm.RealmChangeListener;

public class SpeakerDetailsActivityViewModel extends ViewModel {

    private RealmDataRepository realmRepo;
    private MutableLiveData<Speaker> speaker;
    private Speaker speakerRealmResult;

    public SpeakerDetailsActivityViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
    }

    public LiveData<Speaker> getSpeaker(String speakerName) {
        if (speaker == null) {
            speaker = new MutableLiveData<>();
            speakerRealmResult = realmRepo.getSpeaker(speakerName);
            speakerRealmResult.addChangeListener((RealmChangeListener<Speaker>) speakerData -> {
                speaker.setValue(speakerData);
            });
        }
        return speaker;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (speakerRealmResult != null) {
            speakerRealmResult.removeAllChangeListeners();
        }
    }
}
