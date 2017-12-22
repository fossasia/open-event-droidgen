package org.fossasia.openevent.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.dbutils.RealmDataRepository;

import java.util.List;

import io.realm.RealmResults;

public class LocationsFragmentViewModel extends ViewModel{

    private MutableLiveData<List<Microlocation>> locations;
    private RealmDataRepository realmRepo;
    private RealmResults<Microlocation> realmResults;

    private String searchText = "";

    public LocationsFragmentViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
    }

    public LiveData<List<Microlocation>> getLocations() {
        if (locations == null) {
            locations = new MutableLiveData<>();
            realmResults = realmRepo.getLocations();
            realmResults.addChangeListener((microlocations, orderedCollectionChangeSet) -> {
                locations.setValue(microlocations);
            });
        }
        return locations;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    @Override
    protected void onCleared() {
        realmResults.removeAllChangeListeners();
        super.onCleared();
    }
}
