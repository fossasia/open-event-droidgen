package org.fossasia.openevent.core.track.session;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import org.fossasia.openevent.common.arch.FilterableRealmLiveData;
import org.fossasia.openevent.common.date.DateConverter;
import org.fossasia.openevent.common.date.DateService;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.data.repository.RealmDataRepository;
import org.threeten.bp.ZonedDateTime;

import java.util.List;
import java.util.Locale;

import io.reactivex.functions.Predicate;
import io.realm.RealmResults;

public class TrackSessionsActivityViewModel extends ViewModel {

    private RealmDataRepository realmRepo;
    private Track track;
    private LiveData<Track> trackLiveData;
    private FilterableRealmLiveData<Session> filteredSessionsLiveData;
    private RealmResults<Session> sessions;
    private String searchText = "";
    private int ongoingPosition;
    private int upcomingPosition;
    private int flag;

    public TrackSessionsActivityViewModel() {
        realmRepo = RealmDataRepository.getDefaultInstance();
    }

    public LiveData<Track> getTrack(int trackId) {
        if (trackLiveData == null) {
            track = realmRepo.getTrack(trackId);
            track.addChangeListener((realmModel, changeSet) -> {
                sessions = track.getSessions().sort("startsAt");
                if (filteredSessionsLiveData == null) {
                    filteredSessionsLiveData = new FilterableRealmLiveData<>(sessions);
                }
                sessions.addChangeListener(this::calculateUpcomingOngoingPosition);
            });
            trackLiveData = RealmDataRepository.asLiveDataForObject(track);
        }
        return trackLiveData;
    }

    private void filterData(String searchText) {
        if (!this.searchText.equals(searchText)) {
            setSearchText(searchText);
            final String query = searchText.toLowerCase(Locale.getDefault());

            Predicate<Session> predicate = session -> session.getTitle()
                    .toLowerCase(Locale.getDefault())
                    .contains(query);

            filteredSessionsLiveData.filter(predicate);
        }
    }

    public LiveData<List<Session>> getSessions(String searchText) {
        filterData(searchText);
        return filteredSessionsLiveData;
    }

    private void calculateUpcomingOngoingPosition(List<Session> sessions) {
        int countUpcoming = 0;
        int countOngoing = 0;
        for (Session trackSession : sessions) {
            flag = 0;
            ZonedDateTime start = DateConverter.getDate(trackSession.getStartsAt());
            ZonedDateTime end = DateConverter.getDate((trackSession.getEndsAt()));
            ZonedDateTime current = ZonedDateTime.now();
            if (DateService.isOngoingSession(start, end, current)) {
                ongoingPosition = countOngoing;
                break;
            } else if (DateService.isUpcomingSession(start, end, current)) {
                upcomingPosition = countUpcoming;
                break;
            } else flag += 1;
            countUpcoming += 1;
            countOngoing += 1;
        }
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public int getOngoingPosition() {
        return ongoingPosition;
    }

    public int getUpcomingPosition() {
        return upcomingPosition;
    }

    public int getFlag() {
        return flag;
    }

    private void clearChangeListeners() {
        if (track != null) {
            track.removeAllChangeListeners();
        }
        if (sessions != null) {
            sessions.removeAllChangeListeners();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clearChangeListeners();
    }

}
