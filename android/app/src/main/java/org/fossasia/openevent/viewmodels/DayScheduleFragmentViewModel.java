package org.fossasia.openevent.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.utils.SortOrder;

import io.realm.RealmResults;

/**
 * Created by samagra on 23/1/18.
 */

public class DayScheduleFragmentViewModel extends ViewModel {
    private MutableLiveData<RealmResults<Session>> realmResults;
    private String searchText = "";


    private RealmDataRepository realmRepo;
    private String date;

    public DayScheduleFragmentViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public LiveData<RealmResults<Session>> getRealmResults() {
        if (realmResults == null)
            realmResults = new MutableLiveData<>();
        realmResults.setValue(realmRepo.getSessionsByDate(date, SortOrder.sortTypeSchedule()));
        return realmResults;
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
