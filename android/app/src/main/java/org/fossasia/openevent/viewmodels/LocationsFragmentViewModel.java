package org.fossasia.openevent.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.dbutils.LiveRealmData;
import org.fossasia.openevent.dbutils.RealmDataRepository;

import java.util.List;

public class LocationsFragmentViewModel extends ViewModel {

    private LiveData<List<Microlocation>> locations;
    private RealmDataRepository realmRepo;

    private String searchText = "";

    public LocationsFragmentViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
    }

    public LiveData<List<Microlocation>> getLocations() {
        if (locations == null) {
            LiveRealmData<Microlocation> microlocationLiveRealmData = RealmDataRepository.asLiveData(realmRepo.getLocations());
            locations = Transformations.map(microlocationLiveRealmData, input -> input);
        }
        return locations;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

}
