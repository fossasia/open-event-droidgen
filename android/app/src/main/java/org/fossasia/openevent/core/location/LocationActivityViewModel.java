package org.fossasia.openevent.core.location;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.common.arch.FilterableRealmLiveData;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.repository.RealmDataRepository;

import java.util.List;
import java.util.Locale;

import io.reactivex.functions.Predicate;

public class LocationActivityViewModel extends ViewModel {

    private FilterableRealmLiveData<Session> filterableRealmLiveData;
    private RealmDataRepository realmRepo;
    private String searchText = "";

    public LocationActivityViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
    }

    public LiveData<List<Session>> getSessionByLocation(String location, String searchText) {
        if(filterableRealmLiveData == null)
            filterableRealmLiveData = RealmDataRepository.asFilterableLiveData(realmRepo.getSessionsByLocation(location));
        if (!this.searchText.equals(searchText)) {
            setSearchText(searchText);
            final String query = searchText.toLowerCase(Locale.getDefault());
            Predicate<Session> predicate = session -> session.getTitle()
                    .toLowerCase(Locale.getDefault())
                    .contains(query);
            filterableRealmLiveData.filter(predicate);
        }
        return filterableRealmLiveData;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

}
