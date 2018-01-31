package org.fossasia.openevent.core.track;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.common.arch.FilterableRealmLiveData;
import org.fossasia.openevent.data.repository.RealmDataRepository;

import java.util.List;
import java.util.Locale;

import io.reactivex.functions.Predicate;

public class TracksFragmentViewModel extends ViewModel {

    private FilterableRealmLiveData<Track> filterableRealmLiveData;

    private String searchText = "";

    public TracksFragmentViewModel() {
        filterableRealmLiveData = RealmDataRepository.asFilterableLiveData(RealmDataRepository.getDefaultInstance().getTracks());
    }

    public LiveData<List<Track>> getTracks(String searchText) {
        if (!this.searchText.equals(searchText)) {
            setSearchText(searchText);
            final String query = searchText.toLowerCase(Locale.getDefault());
            Predicate<Track> predicate = track -> track.getName()
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
