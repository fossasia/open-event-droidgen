package org.fossasia.openevent.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.extras.EventDates;
import org.fossasia.openevent.dbutils.LiveRealmData;
import org.fossasia.openevent.dbutils.LiveRealmDataObject;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.utils.DateConverter;
import org.threeten.bp.format.DateTimeParseException;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

public class AboutFragmentViewModel extends ViewModel {

    private RealmDataRepository realmRepo;
    private LiveData<List<Object>> sessions;
    private MutableLiveData<ArrayList<String>> dateList;
    private LiveData<Event> eventLiveData;

    public AboutFragmentViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
        sessions = new MutableLiveData<>();
        dateList = new MutableLiveData<>();
        eventLiveData = new MutableLiveData<>();
        subscribeToEvent();
        subscribeToDateList();
    }

    private void subscribeToSessions(ArrayList<String> dates) {
        LiveRealmData<Session> sessionLiveRealmData = RealmDataRepository.asLiveData(realmRepo.getBookMarkedSessions());
        sessions = Transformations.map(sessionLiveRealmData, input -> getSessionsList(dates, input));
    }

    private void subscribeToDateList() {
        RealmResults<EventDates> eventDates = realmRepo.getEventDatesSync();
        ArrayList<String> dateListString = new ArrayList<>();
        for (EventDates eventDate : eventDates) {
            dateListString.add(eventDate.getDate());
        }
        dateList.setValue(dateListString);
        subscribeToSessions(dateListString);
    }

    private void subscribeToEvent() {
        LiveRealmDataObject<Event> liveRealmDataObject = RealmDataRepository.asLiveDataForObject(realmRepo.getEvent());
        eventLiveData = Transformations.map(liveRealmDataObject, input -> input);
    }

    public LiveData<Event> getEvent() {
        return eventLiveData;
    }

    public LiveData<ArrayList<String>> getDateList() {
        return dateList;
    }

    public LiveData<List<Object>> getSessions() {
        return sessions;
    }

    private List<Object> getSessionsList(ArrayList<String> dateList, RealmResults<Session> bookmarked) {
        List<Object> sessionsList = new ArrayList<>();
        for (String eventDate : dateList) {
            boolean headerCheck = false;
            for (Session bookmarkedSession : bookmarked) {
                if (bookmarkedSession.getStartDate() != null && bookmarkedSession.getStartDate().equals(eventDate)) {
                    if (!headerCheck) {
                        String headerDate = "Invalid";
                        try {
                            headerDate = DateConverter.formatDay(eventDate);
                        } catch (DateTimeParseException e) {
                            e.printStackTrace();
                        }
                        sessionsList.add(headerDate);
                        headerCheck = true;
                    }
                    sessionsList.add(bookmarkedSession);
                }
            }
        }
        return sessionsList;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
