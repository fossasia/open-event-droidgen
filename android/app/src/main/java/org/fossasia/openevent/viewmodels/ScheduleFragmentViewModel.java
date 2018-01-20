package org.fossasia.openevent.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.util.Pair;

import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.data.extras.EventDates;
import org.fossasia.openevent.dbutils.FilterableRealmLiveData;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.utils.DateConverter;
import org.threeten.bp.format.DateTimeParseException;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ScheduleFragmentViewModel extends ViewModel {

    private LiveData<List<Pair<String, String>>> eventDateStringLivePair;
    private FilterableRealmLiveData<Track> trackFilterableRealmLiveData;
    private FilterableRealmLiveData<EventDates> eventDatesFilterableRealmLiveData;

    public ScheduleFragmentViewModel() {
        RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();
        trackFilterableRealmLiveData = RealmDataRepository.asFilterableLiveData(realmRepo.getTracks());
        eventDatesFilterableRealmLiveData = RealmDataRepository.asFilterableLiveData(realmRepo.getEventDates());
    }

    public LiveData<List<Pair<String, String>>> getEventDateString() {
        if (eventDateStringLivePair == null) {
            eventDateStringLivePair = Transformations.map(eventDatesFilterableRealmLiveData, eventDates -> {
                int eventDays = eventDates.size();
                List<Pair<String, String>> eventDateStringData = new ArrayList<>();
                for (int i = 0; i < eventDays; i++) {
                    String date = eventDates.get(i).getDate();
                    try {
                        String formattedDate = DateConverter.formatDay(date);
                        Pair<String, String> data = new Pair<>(formattedDate, date);
                        eventDateStringData.add(data);
                    } catch (DateTimeParseException pe) {
                        Timber.e(pe);
                        Timber.e("Invalid date %s in database", date);
                    }
                }
                return eventDateStringData;
            });
        }
        return eventDateStringLivePair;
    }

    public LiveData<List<Track>> getTracks() {
        return trackFilterableRealmLiveData;
    }
}
