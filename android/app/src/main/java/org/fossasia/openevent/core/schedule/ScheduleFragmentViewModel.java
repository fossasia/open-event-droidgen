package org.fossasia.openevent.core.schedule;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.data.extras.EventDates;
import org.fossasia.openevent.common.arch.FilterableRealmLiveData;
import org.fossasia.openevent.data.repository.RealmDataRepository;
import org.fossasia.openevent.common.date.DateConverter;
import org.threeten.bp.format.DateTimeParseException;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import timber.log.Timber;

public class ScheduleFragmentViewModel extends ViewModel {

    private LiveData<List<EventDateStrings>> eventDateStringLivePair;
    private FilterableRealmLiveData<Track> trackFilterableRealmLiveData;
    private FilterableRealmLiveData<EventDates> eventDatesFilterableRealmLiveData;

    public ScheduleFragmentViewModel() {
        RealmDataRepository realmRepo = RealmDataRepository.getDefaultInstance();
        trackFilterableRealmLiveData = RealmDataRepository.asFilterableLiveData(realmRepo.getTracks());
        eventDatesFilterableRealmLiveData = RealmDataRepository.asFilterableLiveData(realmRepo.getEventDates());
    }

    public LiveData<List<EventDateStrings>> getEventDateString() {
        if (eventDateStringLivePair == null) {
            eventDateStringLivePair = Transformations.map(eventDatesFilterableRealmLiveData, eventDates -> {
                int eventDays = eventDates.size();
                List<EventDateStrings> eventDateStringData = new ArrayList<>();
                for (int i = 0; i < eventDays; i++) {
                    String date = eventDates.get(i).getDate();
                    try {
                        String formattedDate = DateConverter.formatDay(date);
                        EventDateStrings data = new EventDateStrings(formattedDate, date);
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

    @Data
    public static class EventDateStrings {
        private final String formattedDate;
        private final String date;
    }
}
