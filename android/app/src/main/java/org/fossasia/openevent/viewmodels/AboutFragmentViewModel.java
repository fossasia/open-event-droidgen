package org.fossasia.openevent.viewmodels;

import android.arch.lifecycle.LiveData;
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
    private ArrayList<String> dateList;
    private LiveData<Event> eventLiveData;

    public AboutFragmentViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
    }

    public LiveData<Event> getEvent() {
        if (eventLiveData == null) {
            LiveRealmDataObject<Event> liveRealmDataObject = RealmDataRepository.asLiveDataForObject(realmRepo.getEvent());
            eventLiveData = Transformations.map(liveRealmDataObject, input -> input);
        }
        return eventLiveData;
    }

    public ArrayList<String> getDateList() {
        if (dateList == null) {
            dateList = new ArrayList<>();
            RealmResults<EventDates> eventDates = realmRepo.getEventDatesSync();
            for (EventDates eventDate : eventDates) {
                dateList.add(eventDate.getDate());
            }
        }
        return dateList;
    }

    public LiveData<List<Object>> getBookmarkedSessions() {
        if (sessions == null) {
            LiveRealmData<Session> sessionLiveRealmData = RealmDataRepository.asLiveData(realmRepo.getBookMarkedSessions());
            sessions = Transformations.map(sessionLiveRealmData, input -> getSessionsList(dateList, input));
        }
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

}
