package org.fossasia.openevent.core.speaker;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.common.arch.LiveRealmData;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.repository.RealmDataRepository;

public class SpeakerDetailsViewModel extends ViewModel {
    private final RealmDataRepository realmRepo;

    private LiveData<Speaker> speaker;

    public SpeakerDetailsViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
    }

    public LiveData<Speaker> getSpeaker(String speakerName) {
        if (speaker == null) {
            LiveRealmData<Speaker> liveRealmDataObject = RealmDataRepository.asLiveData(realmRepo.getSpeakersForName(speakerName));
            speaker = Transformations.map(liveRealmDataObject, input -> input.first());
        }
        return speaker;
    }

}
