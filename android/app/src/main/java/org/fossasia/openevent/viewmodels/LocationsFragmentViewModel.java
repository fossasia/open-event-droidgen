package org.fossasia.openevent.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
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
        locations = new MutableLiveData<>();
        subscribeToLocations();
    }

    private void subscribeToLocations() {
        LiveRealmData<Microlocation> microlocationLiveRealmData = RealmDataRepository.asLiveData(realmRepo.getLocations());
        locations = Transformations.map(microlocationLiveRealmData, input -> input);
    }

    public LiveData<List<Microlocation>> getLocations() {
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
        super.onCleared();
    }
}
