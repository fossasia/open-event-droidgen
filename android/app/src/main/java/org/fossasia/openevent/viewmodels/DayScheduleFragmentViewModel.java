package org.fossasia.openevent.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.utils.SortOrder;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

/**
 * Created by samagra on 23/1/18.
 */

public class DayScheduleFragmentViewModel extends ViewModel {
    private RealmResults<Session> realmResults;
    private String searchText = "";
    private MutableLiveData<List<Session>> sessions ;
    private MutableLiveData<List<Session>> filteredSessions ;
    private List<Session> sessionsList = new ArrayList<>();
    private List<Session> filteredSessionsList = new ArrayList<>();
    private RealmResults<Session> sortedSessions;

    private RealmDataRepository realmRepo;

    public DayScheduleFragmentViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();

    }

    public void init(String date){
        realmResults = realmRepo.getSessionsByDate(date, SortOrder.sortTypeSchedule());
        realmResults.addChangeListener((sortedSessions, orderedCollectionChangeSet) -> {
            sessionsList.clear();
            sessionsList.addAll(sortedSessions);
            sessions.setValue(sessionsList);

            filteredSessionsList.clear();
            filteredSessionsList.addAll(sortedSessions);
            filteredSessions.setValue(filteredSessionsList);

            this.sortedSessions = sortedSessions;

        });
    }

    public LiveData<List<Session>> getSessions() {
        if (sessions == null) {
            sessions = new MutableLiveData<>();
        }
        return sessions;
    }




    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }





    public LiveData<List<Session>> getFilteredSessions() {
        if (filteredSessions == null) {
            filteredSessions = new MutableLiveData<>();
        }
        return filteredSessions;
    }

    public RealmResults<Session> getSortedSessions(){

        return this.sortedSessions;
    }

    public void filteredList(List<String> selectedTracks, String date){
        realmResults = realmRepo.getSessionsByDate(date, SortOrder.sortTypeSchedule());
        realmResults.addChangeListener((sortedSessions, orderedCollectionChangeSet) -> {
            sessionsList.clear();
            sessionsList.addAll(sortedSessions);
            sessions.setValue(sessionsList);

            filteredSessionsList.clear();
            if (selectedTracks.isEmpty()) {
                filteredSessionsList.addAll(sessionsList);
            } else {
                for (int i = 0; i < sessionsList.size(); i++) {
                    String trackName = sessionsList.get(i).getTrack().getName();
                    if (selectedTracks.contains(trackName)) {
                        filteredSessionsList.add(sessionsList.get(i));
                    }
                }
            }
            filteredSessions.setValue(filteredSessionsList);

            this.sortedSessions = sortedSessions;

        });


    }

    @Override
    protected void onCleared() {
        super.onCleared();
        realmResults.removeAllChangeListeners();
    }
}
