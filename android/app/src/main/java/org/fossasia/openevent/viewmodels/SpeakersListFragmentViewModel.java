package org.fossasia.openevent.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.RealmDataRepository;

import java.util.List;

import io.realm.RealmResults;

import static org.fossasia.openevent.utils.SortOrder.sortOrderSpeaker;

public class SpeakersListFragmentViewModel extends ViewModel{

    private MutableLiveData<List<Speaker>> speakersList;
    private RealmDataRepository realmRepo;
    private RealmResults<Speaker> realmResults;
    private String searchText = "";

    public SpeakersListFragmentViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
    }

    public LiveData<List<Speaker>> getSpeakers() {
        speakersList = new MutableLiveData<>();
        realmResults = realmRepo.getSpeakers(sortOrderSpeaker());
        realmResults.addChangeListener((speakers, orderedCollectionChangeSet) -> {
            speakersList.setValue(speakers);
        });
        return speakersList;
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
