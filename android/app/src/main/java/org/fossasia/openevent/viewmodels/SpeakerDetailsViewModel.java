package org.fossasia.openevent.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.LiveRealmDataObject;
import org.fossasia.openevent.dbutils.RealmDataRepository;

public class SpeakerDetailsViewModel extends ViewModel {
    private RealmDataRepository realmRepo;
    private LiveData<Speaker> speaker;

    public SpeakerDetailsViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
    }

    public LiveData<Speaker> getSpeaker(String speakerName) {
        if (speaker == null) {
            LiveRealmDataObject<Speaker> liveRealmDataObject = RealmDataRepository.asLiveDataForObject(realmRepo.getSpeaker(speakerName));
            speaker = Transformations.map(liveRealmDataObject, input -> input);
        }
        return speaker;
    }
}
