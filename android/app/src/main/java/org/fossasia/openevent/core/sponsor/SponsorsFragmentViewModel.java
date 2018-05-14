package org.fossasia.openevent.core.sponsor;

import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.common.arch.LiveRealmData;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.repository.RealmDataRepository;

public class SponsorsFragmentViewModel extends ViewModel {

    private LiveRealmData<Sponsor> sponsorLiveRealmData;
    private RealmDataRepository realmRepo;

    public SponsorsFragmentViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
    }

    public LiveRealmData<Sponsor> getSponsors() {
        if (sponsorLiveRealmData == null) {
            sponsorLiveRealmData = RealmDataRepository.asLiveData(realmRepo.getSponsors());
        }
        return sponsorLiveRealmData;
    }

}