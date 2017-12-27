package org.fossasia.openevent.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Pair;

import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.data.extras.EventDates;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.utils.DateConverter;
import org.threeten.bp.format.DateTimeParseException;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;
import timber.log.Timber;

public class ScheduleFragmentViewModel extends ViewModel {

    private RealmDataRepository realmRepo;
    private List<Pair<String,String>> eventDateStringData;
    private MutableLiveData<Pair<String,String>> eventDateStringLivePair;
    private MutableLiveData<List<Track>> tracksLiveData;
    private RealmResults<Track> tracksRealmResults;
    private RealmResults<EventDates> eventDatesRealmResults;

    public ScheduleFragmentViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
    }

    public LiveData<Pair<String,String>> getEventDateString() {
        if (eventDateStringLivePair == null) {
            eventDateStringLivePair = new MutableLiveData<>();
        }
        if (eventDateStringData == null) {
            eventDateStringData = new ArrayList<>();
            eventDatesRealmResults = realmRepo.getEventDates();
            addEventDatesRealmResultsChangeListener();
        } else {
            for (int i = 0; i < eventDateStringData.size(); i++) {
                eventDateStringLivePair.setValue(eventDateStringData.get(i));
            }
        }
        return eventDateStringLivePair;
    }

    private void addEventDatesRealmResultsChangeListener() {
        eventDatesRealmResults.addChangeListener((eventDates, orderedCollectionChangeSet) -> {
            eventDateStringData.clear();
            int eventDays = eventDates.size();
            for (int i = 0; i < eventDays; i++) {
                String date = eventDates.get(i).getDate();
                try {
                    String formattedDate = DateConverter.formatDay(date);
                    Pair<String,String> data = new Pair<>(formattedDate, date);
                    eventDateStringData.add(data);
                    eventDateStringLivePair.setValue(data);
                } catch (DateTimeParseException pe) {
                    Timber.e(pe);
                    Timber.e("Invalid date %s in database", date);
                }
            }
        });
    }

    public LiveData<List<Track>> getTracks() {
        if (tracksLiveData == null) {
            tracksLiveData = new MutableLiveData<>();
            tracksRealmResults = realmRepo.getTracks();
            tracksRealmResults.addChangeListener((tracks, orderedCollectionChangeSet) -> {
                tracksLiveData.setValue(tracks);
            });
        }
        return tracksLiveData;
    }

    private void clearListeners() {
        if (eventDatesRealmResults != null) {
            eventDatesRealmResults.removeAllChangeListeners();
        }
        if (tracksRealmResults != null) {
            tracksRealmResults.removeAllChangeListeners();
        }
    }

    @Override
    protected void onCleared() {
        clearListeners();
        super.onCleared();
    }
}
