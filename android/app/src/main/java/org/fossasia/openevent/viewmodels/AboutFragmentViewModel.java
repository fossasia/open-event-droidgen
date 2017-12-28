package org.fossasia.openevent.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.extras.EventDates;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.utils.DateConverter;
import org.threeten.bp.format.DateTimeParseException;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

public class AboutFragmentViewModel extends ViewModel {

    private RealmDataRepository realmRepo;
    private MutableLiveData<List<Object>> sessions;
    private MutableLiveData<ArrayList<String>> dateList;
    private MutableLiveData<Event> eventLiveData;
    private RealmResults<Session> bookmarksResult;
    private Event event;

    public AboutFragmentViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
    }

    public LiveData<Event> getEvent() {
        if (eventLiveData == null) {
            eventLiveData = new MutableLiveData<>();
            event = realmRepo.getEvent();
            eventLiveData.setValue(event);
            event.addChangeListener(realmModel -> {
                eventLiveData.setValue(event);
            });
        }
        return eventLiveData;
    }

    public LiveData<ArrayList<String>> getDateList() {
        if (dateList == null) {
            dateList = new MutableLiveData<>();
            RealmResults<EventDates> eventDates = realmRepo.getEventDatesSync();
            ArrayList<String> dateListString = new ArrayList<>();
            for (EventDates eventDate : eventDates) {
                dateListString.add(eventDate.getDate());
            }
            dateList.setValue(dateListString);
        }
        return dateList;
    }

    public LiveData<List<Object>> getSessions(ArrayList<String> dateList) {
        if (sessions == null) {
            sessions = new MutableLiveData<>();
            bookmarksResult = realmRepo.getBookMarkedSessions();
            bookmarksResult.addChangeListener((bookmarked, orderedCollectionInnerChangeSet) -> {
                sessions.setValue(getSessionsList(dateList,bookmarked));
            });
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

    private void clearListeners() {
        if (bookmarksResult != null) {
            bookmarksResult.removeAllChangeListeners();
        }
        if (event != null) {
            event.removeAllChangeListeners();
        }
    }

    @Override
    protected void onCleared() {
        clearListeners();
        super.onCleared();
    }
}
