package org.fossasia.openevent.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.FilterableRealmLiveData;
import org.fossasia.openevent.dbutils.RealmDataRepository;

import java.util.List;
import java.util.Locale;

import io.reactivex.functions.Predicate;

import static org.fossasia.openevent.utils.SortOrder.sortOrderSpeaker;

public class SpeakersListFragmentViewModel extends ViewModel {

    private LiveData<List<Speaker>> speakersList;
    private FilterableRealmLiveData<Speaker> filterableRealmLiveData;
    private RealmDataRepository realmRepo;
    private String searchText = "";
    private int speakersListSortType = 0;

    public SpeakersListFragmentViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
    }

    public LiveData<List<Speaker>> getSpeakers(int sortType, String searchText) {
        setSearchText(searchText);
        if (speakersList == null || sortType != speakersListSortType) {
            filterableRealmLiveData = RealmDataRepository.asFilterableLiveData(realmRepo.getSpeakers(sortOrderSpeaker()));
            speakersListSortType = sortType;
            loadFilteredSpeakers();
            speakersList = Transformations.map(filterableRealmLiveData, input -> input);
        } else {
            loadFilteredSpeakers();
        }
        return speakersList;
    }

    private void loadFilteredSpeakers() {
        final String query = searchText.toLowerCase(Locale.getDefault());
        Predicate<Speaker> predicate = speaker -> speaker.getName()
                .toLowerCase(Locale.getDefault()).contains(query);
        filterableRealmLiveData.filter(predicate);
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

}